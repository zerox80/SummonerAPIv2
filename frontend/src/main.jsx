/**
 * Application entry point and React initialization.
 * 
 * <p>This file serves as the main entry point for the React application. It sets up
 * the React root, configures essential providers (React Query for data fetching,
 * ThemeProvider for theming, BrowserRouter for routing), and renders the App component.
 * The configuration includes optimized defaults for React Query to balance performance
 * and data freshness.</p>
 * 
 * <p>Provider stack:</p>
 * <ul>
 *   <li>React.StrictMode - Enables additional development checks and warnings</li>
 *   <li>QueryClientProvider - Provides React Query for server state management</li>
 *   <li>ThemeProvider - Provides theme context for dark/light mode switching</li>
 *   <li>BrowserRouter - Provides client-side routing capabilities</li>
 * </ul>
 * 
 * <p>React Query configuration:</p>
 * <ul>
 *   <li>Disabled refetch on window focus to reduce unnecessary API calls</li>
 *   <li>Limited retry attempts to 1 for faster error handling</li>
 *   <li>60-second stale time for balanced caching</li>
 * </ul>
 * 
 * @file main.jsx
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */

import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import App from './App.jsx';
import ThemeProvider from './providers/ThemeProvider.jsx';
import './styles/global.css';

/**
 * React Query client configuration with optimized defaults.
 * 
 * <p>This QueryClient instance is configured with sensible defaults for the
 * SummonerAPI application, balancing data freshness with performance to
 * provide a smooth user experience while minimizing unnecessary API calls.</p>
 */
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 60_000
    }
  }
});

/**
 * Renders the React application to the DOM.
 * 
 * <p>This creates a React root and renders the application with all necessary
 * providers. The provider hierarchy ensures that all components have access
 * to routing, theming, and data fetching capabilities.</p>
 */
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <ThemeProvider>
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </ThemeProvider>
    </QueryClientProvider>
  </React.StrictMode>
);
