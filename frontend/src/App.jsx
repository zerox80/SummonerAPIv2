/**
 * Main application component that defines the routing structure.
 * 
 * <p>This component serves as the root of the React application and defines
 * all the routes using React Router. It wraps the routes in a ShellLayout
 * component that provides the consistent page structure and navigation.</p>
 * 
 * <p>Routes defined:</p>
 * <ul>
 *   <li>/ - SummonerPage (default route for summoner lookup)</li>
 *   <li>/champions - ChampionsPage (browse all champions)</li>
 *   <li>/champions/:id - ChampionDetailPage (detailed champion information)</li>
 *   <li>/* - Redirect to homepage for unknown routes</li>
 * </ul>
 * 
 * <p>Features:</p>
 * <ul>
 *   <li>Client-side routing with React Router</li>
 *   <li>Wildcard route handling with redirect</li>
 *   <li>Consistent layout through ShellLayout</li>
 *   <li>Route-based code splitting support</li>
 * </ul>
 * 
 * @component App
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */

import { Route, Routes, Navigate } from 'react-router-dom';
import ShellLayout from './layouts/ShellLayout.jsx';
import SummonerPage from './pages/SummonerPage.jsx';
import ChampionsPage from './pages/ChampionsPage.jsx';
import ChampionDetailPage from './pages/ChampionDetailPage.jsx';

/**
 * Renders the main application with routing configuration.
 * 
 * <p>This function returns the JSX structure that defines all application routes.
 * It uses React Router's Routes and Route components to map URL paths to
 * page components, with a catch-all route that redirects to the homepage.</p>
 * 
 * @returns {JSX.Element} The rendered application with routing
 * 
 * @example
 * // In main.jsx or index.jsx
 * ReactDOM.createRoot(document.getElementById('root')).render(
 *   <React.StrictMode>
 *     <BrowserRouter>
 *       <App />
 *     </BrowserRouter>
 *   </React.StrictMode>
 * );
 */
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
