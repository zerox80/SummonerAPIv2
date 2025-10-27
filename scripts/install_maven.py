#!/usr/bin/env python3
"""Utility script to install Apache Maven and (optionally) Temurin JDK locally.

The script downloads the requested Maven version, verifies the published
SHA512 checksum and extracts it into a target directory (defaults to
``~/opt/apache-maven-<version>``). Optionally it installs a Temurin JDK build
for Linux x64 (default: 21.0.6+7), verifies the SHA256 checksum and sets up
symlinks plus shell exports so both Maven and Java are ready to use after the
next login.

Examples
--------
Install the default version to ``~/opt`` and create a symlink::

    python scripts/install_maven.py

Install version 3.9.10 under ``/opt/tools`` and overwrite existing content::

    sudo python scripts/install_maven.py --version 3.9.10 \
        --destination /opt/tools/apache-maven-3.9.10 \
        --symlink /opt/tools/apache-maven --force
"""

from __future__ import annotations

import argparse
import hashlib
import os
import platform
import shutil
import sys
import tarfile
import tempfile
import urllib.request
import zipfile
from pathlib import Path


DEFAULT_VERSION = "3.9.11"
DEFAULT_BASE_URL = "https://dlcdn.apache.org/maven/maven-3/{version}/binaries"
DEFAULT_JDK_VERSION = "21.0.6+7"


class InstallerError(RuntimeError):
    """Raised when installation cannot proceed."""


def parse_args() -> argparse.Namespace:
    system = platform.system()
    default_dest = Path.home() / "opt" / f"apache-maven-{DEFAULT_VERSION}"
    default_symlink = Path.home() / "opt" / "apache-maven"
    default_jdk_dest = Path.home() / "opt" / f"temurin-{DEFAULT_JDK_VERSION}"
    default_java_symlink = Path.home() / "opt" / "java"

    parser = argparse.ArgumentParser(description="Download and install Apache Maven.")
    parser.add_argument(
        "--version",
        default=DEFAULT_VERSION,
        help=f"Maven version to install (default: {DEFAULT_VERSION}).",
    )
    parser.add_argument(
        "--destination",
        type=Path,
        default=default_dest,
        help="Target directory for Maven (default: ~/opt/apache-maven-<version>).",
    )
    parser.add_argument(
        "--symlink",
        type=Path,
        default=default_symlink,
        help="Optional symlink that will point to the installed Maven directory.",
    )
    parser.add_argument(
        "--mirror",
        default=None,
        help="Override download base URL (should point at the binaries folder).",
    )
    parser.add_argument(
        "--skip-jdk",
        action="store_true",
        help="Skip automatic Temurin JDK installation.",
    )
    parser.add_argument(
        "--jdk-version",
        default=DEFAULT_JDK_VERSION,
        help=f"Temurin JDK version to install (default: {DEFAULT_JDK_VERSION}).",
    )
    parser.add_argument(
        "--jdk-destination",
        type=Path,
        default=default_jdk_dest,
        help="Target directory for the Temurin JDK (default: ~/opt/temurin-<version>).",
    )
    parser.add_argument(
        "--java-symlink",
        type=Path,
        default=default_java_symlink,
        help="Optional symlink that will point to the installed JDK directory.",
    )
    parser.add_argument(
        "--force",
        action="store_true",
        help="Remove existing destination directory before unpacking.",
    )
    parser.add_argument(
        "--skip-checksum",
        action="store_true",
        help="Skip SHA512 verification (not recommended).",
    )

    args = parser.parse_args()

    # Normalise paths after parsing version (destination may include <version> placeholder)
    version = args.version.strip()
    if not version:
        raise InstallerError("Version must not be empty.")

    if "<version>" in str(args.destination):
        args.destination = Path(str(args.destination).replace("<version>", version))
    elif args.destination == default_dest:
        args.destination = Path.home() / "opt" / f"apache-maven-{version}"

    args.destination = args.destination.expanduser()

    if args.symlink:
        args.symlink = args.symlink.expanduser()

    jdk_version = args.jdk_version.strip()
    if not jdk_version:
        raise InstallerError("JDK version must not be empty.")
    if "<version>" in str(args.jdk_destination):
        args.jdk_destination = Path(str(args.jdk_destination).replace("<version>", jdk_version))
    elif args.jdk_destination == default_jdk_dest:
        args.jdk_destination = Path.home() / "opt" / f"temurin-{jdk_version}"
    args.jdk_destination = args.jdk_destination.expanduser()

    if args.java_symlink:
        args.java_symlink = args.java_symlink.expanduser()

    args.system = system
    args.version = version
    args.jdk_version = jdk_version
    return args


def build_urls(version: str, base_url: str | None, system: str) -> tuple[str, str]:
    """Build download URLs for Maven archive and checksum.

    Constructs the appropriate URLs for downloading the Maven binary archive
    and its corresponding SHA512 checksum file based on the version, base URL,
    and operating system.

    Args:
        version: The Maven version to download (e.g., "3.9.11").
        base_url: Optional base URL override. If None, uses the default Apache CDN.
        system: Operating system identifier from platform.system().

    Returns:
        A tuple containing (archive_url, checksum_url) for the specified version.

    Raises:
        ValueError: If version is empty or invalid.
    """
    archive_ext = ".zip" if system.lower().startswith("win") else ".tar.gz"
    archive_name = f"apache-maven-{version}-bin{archive_ext}"
    base = (base_url or DEFAULT_BASE_URL).format(version=version)
    archive_url = f"{base}/{archive_name}"
    checksum_url = f"{archive_url}.sha512"
    return archive_url, checksum_url


def build_temurin_urls(jdk_version: str) -> tuple[str, str]:
    """Build download URLs for Temurin JDK archive and checksum.

    Constructs the appropriate URLs for downloading the Temurin JDK binary
    archive and its corresponding SHA256 checksum file based on the JDK version.
    The URLs are formatted for Adoptium's GitHub releases.

    Args:
        jdk_version: The Temurin JDK version (e.g., "21.0.6+7").

    Returns:
        A tuple containing (archive_url, checksum_url) for the specified JDK version.

    Raises:
        ValueError: If jdk_version is empty or malformed.
    """
    major = jdk_version.split(".")[0]
    release_tag = jdk_version.replace("+", "%2B")
    file_version = jdk_version.replace("+", "_")
    file_name = f"OpenJDK{major}U-jdk_x64_linux_hotspot_{file_version}.tar.gz"
    base = f"https://github.com/adoptium/temurin{major}-binaries/releases/download/jdk-{release_tag}"
    archive_url = f"{base}/{file_name}"
    checksum_url = f"{archive_url}.sha256.txt"
    return archive_url, checksum_url


def download(url: str, dest: Path) -> None:
    """Download a file from URL to destination path.

    Downloads the file from the specified URL and saves it to the destination
    path. Creates parent directories if they don't exist. Uses streaming download
    to handle large files efficiently.

    Args:
        url: The URL to download from.
        dest: The destination path where the file should be saved.

    Raises:
        urllib.error.URLError: If the URL is invalid or the server is unreachable.
        OSError: If there are filesystem permissions or space issues.
    """
    dest.parent.mkdir(parents=True, exist_ok=True)
    with urllib.request.urlopen(url) as response, dest.open("wb") as f:
        shutil.copyfileobj(response, f)


def read_expected_checksum(url: str, expected_len: int | None = None) -> str:
    """Read and parse expected checksum from remote server.

    Downloads the checksum file from the specified URL and extracts the first
    token, which is typically the checksum hash. Optionally validates the
    checksum length to ensure it matches the expected format.

    Args:
        url: URL of the checksum file to download.
        expected_len: Optional expected length of the checksum hash for validation.

    Returns:
        The checksum hash as a lowercase string.

    Raises:
        InstallerError: If the checksum format is unexpected or doesn't match expected_len.
        urllib.error.URLError: If the URL is invalid or the server is unreachable.
    """
    with urllib.request.urlopen(url) as response:
        body = response.read().decode("utf-8", errors="ignore")
    first_token = body.strip().split()[0]
    if expected_len and len(first_token) != expected_len:
        raise InstallerError("Unexpected checksum format from remote server.")
    return first_token.lower()


def compute_hash(path: Path, algorithm: str) -> str:
    """Compute hash checksum for a file using specified algorithm.

    Calculates the cryptographic hash of the file at the given path using
    the specified algorithm. Reads the file in 1MB chunks to handle large
    files efficiently without loading everything into memory.

    Args:
        path: Path to the file to hash.
        algorithm: Hash algorithm to use (e.g., "sha256", "sha512").

    Returns:
        The hexadecimal digest of the file hash.

    Raises:
        OSError: If the file cannot be read.
        ValueError: If the specified algorithm is not supported by hashlib.
    """
    digest = hashlib.new(algorithm)
    with path.open("rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def verify_checksum(archive: Path, checksum_url: str, algorithm: str, expected_len: int | None = None) -> None:
    """Verify downloaded archive against expected checksum.

    Downloads the expected checksum from the remote server and compares it
    with the computed checksum of the local archive file. Raises an error
    if the checksums don't match, indicating a corrupted or tampered file.

    Args:
        archive: Path to the downloaded archive file to verify.
        checksum_url: URL containing the expected checksum hash.
        algorithm: Hash algorithm used for verification (e.g., "sha256", "sha512").
        expected_len: Optional expected length of the checksum for format validation.

    Raises:
        InstallerError: If checksums don't match or format is unexpected.
        urllib.error.URLError: If the checksum URL is unreachable.
    """
    expected = read_expected_checksum(checksum_url, expected_len)
    actual = compute_hash(archive, algorithm)
    if expected != actual:
        raise InstallerError(
            "Checksum mismatch – aborting installation. "
            "Re-run with --skip-checksum only if you trust the source."
        )


def extract_archive(archive: Path, destination: Path, system: str) -> Path:
    """Extract archive to destination directory.

    Extracts the contents of a ZIP or tar.gz archive to the specified destination.
    Uses a temporary directory for extraction to handle partial failures cleanly.
    Moves the extracted root directory to the final destination.

    Args:
        archive: Path to the archive file to extract.
        destination: Final destination directory path.
        system: Operating system identifier for format detection.

    Returns:
        Path to the extracted destination directory.

    Raises:
        InstallerError: If destination already exists or extraction fails.
        zipfile.BadZipFile: If the ZIP file is corrupted.
        tarfile.TarError: If the tar file is corrupted.
    """
    destination.parent.mkdir(parents=True, exist_ok=True)
    tmp_extract = destination.parent / f".{destination.name}.tmp"
    if tmp_extract.exists():
        shutil.rmtree(tmp_extract)
    tmp_extract.mkdir(parents=True)

    if archive.suffix == ".zip" or system.lower().startswith("win"):
        with zipfile.ZipFile(archive) as zf:
            zf.extractall(tmp_extract)
    else:
        with tarfile.open(archive, "r:gz") as tf:
            tf.extractall(tmp_extract)

    [extracted_root] = list(tmp_extract.iterdir())
    if destination.exists():
        raise InstallerError(f"Destination {destination} already exists.")
    extracted_root.rename(destination)
    shutil.rmtree(tmp_extract, ignore_errors=True)
    return destination


def remove_existing(path: Path) -> None:
    """Remove existing file, directory, or symlink at path.

    Safely removes whatever exists at the specified path, whether it's a
    regular file, directory, or symbolic link. Handles both files and
    directories appropriately.

    Args:
        path: Path to the existing file/directory/symlink to remove.

    Raises:
        OSError: If removal fails due to permissions or other filesystem issues.
    """
    if path.is_symlink() or path.exists():
        if path.is_dir() and not path.is_symlink():
            shutil.rmtree(path)
        else:
            path.unlink()


def create_symlink(target: Path, link_path: Path, system: str) -> None:
    """Create a symbolic link pointing to the target directory.

    Creates a symbolic link at link_path that points to the target directory.
    Handles Windows symlink limitations and provides appropriate error messages.
    Removes any existing link at the same path before creating the new one.

    Args:
        target: Target directory that the symlink should point to.
        link_path: Path where the symbolic link should be created.
        system: Operating system identifier for platform-specific handling.

    Raises:
        InstallerError: If symlink creation fails on non-Windows systems.
        OSError: If there are filesystem permissions issues.
    """
    if not link_path:
        return
    link_path.parent.mkdir(parents=True, exist_ok=True)
    if link_path.exists() or link_path.is_symlink():
        remove_existing(link_path)
    try:
        link_path.symlink_to(target, target_is_directory=True)
    except OSError as exc:
        if system.lower().startswith("win"):
            print(
                f"[WARN] Unable to create symlink at {link_path}. Run this script in an elevated \n"
                "Command Prompt or omit --symlink on Windows.",
                file=sys.stderr,
            )
        else:
            raise InstallerError(f"Failed to create symlink: {exc}") from exc


def detect_shell_rc(system: str) -> Path | None:
    """Detect the appropriate shell configuration file for the current system.

    Identifies the most suitable shell configuration file based on the current
    shell environment and system. Checks for existing files in order of preference
    and returns the first one found, or the default candidate if none exist.

    Args:
        system: Operating system identifier from platform.system().

    Returns:
        Path to the detected shell configuration file, or None for Windows.
    """
    if system.lower().startswith("win"):
        return None
    shell = os.environ.get("SHELL", "")
    candidates: list[str]
    if shell.endswith("zsh"):
        candidates = ["~/.zshrc", "~/.zsh_profile", "~/.profile"]
    elif shell.endswith("bash"):
        candidates = ["~/.bashrc", "~/.bash_profile", "~/.profile"]
    else:
        candidates = ["~/.profile", "~/.bashrc"]
    for candidate in candidates:
        path = Path(candidate).expanduser()
        if path.exists():
            return path
    return Path(candidates[0]).expanduser()


def ensure_line(rc_file: Path, line: str) -> bool:
    """Ensure a line exists in the shell configuration file.

    Checks if the specified line already exists in the configuration file.
    If not found, appends the line to the file. Handles file creation and
    proper formatting with newlines.

    Args:
        rc_file: Path to the shell configuration file.
        line: The line to ensure exists in the file.

    Returns:
        True if the line was added, False if it already existed.

    Raises:
        OSError: If the file cannot be read or written due to permissions.
    """
    normalized = line.strip()
    if rc_file.exists():
        with rc_file.open("r", encoding="utf-8") as infile:
            existing = {entry.strip() for entry in infile.readlines()}
        if normalized in existing:
            return False
    rc_file.parent.mkdir(parents=True, exist_ok=True)
    with rc_file.open("a", encoding="utf-8") as outfile:
        if rc_file.exists() and rc_file.stat().st_size > 0:
            outfile.write("\n")
        outfile.write(normalized + "\n")
    return True


def print_success(destination: Path, symlink: Path | None, system: str) -> None:
    bin_path = destination / "bin"
    print("\nApache Maven installation completed.")
    print(f"Installed to: {destination}")
    if symlink:
        print(f"Symlink:      {symlink}")

    if not system.lower().startswith("win"):
        print("\nÖffne ein neues Terminal oder führe `source` auf deiner Shell-RC aus,\n"
              "damit der aktualisierte PATH sofort wirkt.")
    else:
        print(
            "\nRun the following command in an elevated terminal to add Maven to PATH permanently:\n"
            f"  setx PATH \"%PATH%;{bin_path}\""
        )


def print_jdk_success(destination: Path, symlink: Path | None, system: str) -> None:
    print("\nTemurin JDK installation completed.")
    print(f"JAVA_HOME:   {destination}")
    if symlink:
        print(f"Java symlink: {symlink}")

    if system.lower().startswith("win"):
        print(
            "\nRun the following commands in an elevated terminal to make the JDK available system-wide:\n"
            f"  setx JAVA_HOME \"{destination}\"\n"
            f"  setx PATH \"%PATH%;{destination / 'bin'}\""
        )


def install_jdk(args) -> Path | None:
    if args.skip_jdk:
        return None
    if args.system.lower().startswith("win"):
        print("\nSkipping automatic JDK installation on Windows (manual setup required).")
        return None

    archive_url, checksum_url = build_temurin_urls(args.jdk_version)
    destination = args.jdk_destination

    with tempfile.TemporaryDirectory() as tmp:
        tmpdir = Path(tmp)
        archive_path = tmpdir / Path(archive_url).name
        print(f"\nDownloading Temurin JDK from {archive_url} ...")
        download(archive_url, archive_path)
        if not args.skip_checksum:
            print("Verifying SHA256 checksum for JDK ...")
            verify_checksum(archive_path, checksum_url, "sha256", expected_len=64)
        else:
            print("[WARN] JDK checksum verification skipped at user request.")

        if destination.exists() and args.force:
            print(f"Removing existing {destination} ...")
            remove_existing(destination)
        elif destination.exists():
            raise InstallerError(
                f"Destination {destination} already exists. Use --force to overwrite or choose another directory."
            )

        print(f"Extracting JDK to {destination} ...")
        extract_archive(archive_path, destination, args.system)

    if args.java_symlink:
        create_symlink(destination, args.java_symlink, args.system)

    print_jdk_success(destination, args.java_symlink, args.system)
    return destination


def finalize_shell_env(system: str, maven_home: Path, jdk_home: Path | None) -> None:
    if system.lower().startswith("win"):
        # On Windows we already printed guidance.
        return

    rc_file = detect_shell_rc(system)
    if rc_file is None:
        return

    env_updates = {}
    if jdk_home is not None:
        env_updates["JAVA_HOME"] = str(jdk_home)
    env_updates["MAVEN_HOME"] = str(maven_home)

    any_updates = False
    for key, value in env_updates.items():
        any_updates |= ensure_line(rc_file, f'export {key}="{value}"')

    path_segments: list[str] = []
    if jdk_home is not None:
        path_segments.append("$JAVA_HOME/bin")
    if maven_home is not None:
        path_segments.append("$MAVEN_HOME/bin")
    if path_segments:
        path_line = 'export PATH="' + ':'.join(path_segments) + ':$PATH"'
        any_updates |= ensure_line(rc_file, path_line)

    if any_updates:
        print(f"\nUpdated shell configuration: {rc_file}. Log out/in or run `source {rc_file}` to apply.")
    else:
        print(f"\nShell configuration already contained required Maven/Java settings ({rc_file}).")


def main() -> int:
    try:
        args = parse_args()
        archive_url, checksum_url = build_urls(args.version, args.mirror, args.system)

        with tempfile.TemporaryDirectory() as tmp:
            tmpdir = Path(tmp)
            archive_path = tmpdir / Path(archive_url).name
            print(f"Downloading {archive_url} ...")
            download(archive_url, archive_path)

            if not args.skip_checksum:
                print("Verifying SHA512 checksum ...")
                verify_checksum(archive_path, checksum_url, "sha512", expected_len=128)
            else:
                print("[WARN] Checksum verification skipped at user request.")

            destination = args.destination
            if destination.exists() and args.force:
                print(f"Removing existing {destination} ...")
                remove_existing(destination)
            elif destination.exists():
                raise InstallerError(
                    f"Destination {destination} already exists. Use --force to overwrite or choose another directory."
                )

            print(f"Extracting to {destination} ...")
            extract_archive(archive_path, destination, args.system)

        if args.symlink:
            create_symlink(destination, args.symlink, args.system)

        print_success(destination, args.symlink, args.system)

        jdk_home = install_jdk(args)
        finalize_shell_env(args.system, destination, jdk_home)
        return 0

    except InstallerError as exc:
        print(f"[ERROR] {exc}", file=sys.stderr)
        return 1
    except KeyboardInterrupt:
        print("Installation aborted by user.")
        return 130


if __name__ == "__main__":
    sys.exit(main())
