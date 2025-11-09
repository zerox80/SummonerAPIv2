

import { Route, Routes, Navigate } from 'react-router-dom';
import ShellLayout from './layouts/ShellLayout.jsx';
import SummonerPage from './pages/SummonerPage.jsx';
import ChampionsPage from './pages/ChampionsPage.jsx';
import ChampionDetailPage from './pages/ChampionDetailPage.jsx';


export default function App() {
  return (
    <ShellLayout>
      <Routes>
        <Route index element={<SummonerPage />} />
        <Route path="/champions" element={<ChampionsPage />} />
        <Route path="/champions/:id" element={<ChampionDetailPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </ShellLayout>
  );
}
