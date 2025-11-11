import React from "react";
import { Routes, Route } from "react-router-dom";
import { Toaster } from "@/components/ui/toaster.jsx";
import Navbar from "@/components/Navbar.jsx";
import HomePage from "@/pages/HomePage.jsx";
import ProcessEditor from "@/pages/ProcessEditor.jsx";
import SimulationSetup from "@/pages/SimulationSetup.jsx";
import SimulationResults from "@/pages/SimulationResults.jsx";
import LoginPage from "@/components/auth/LoginPage.jsx";
import { TooltipProvider } from "@/components/ui/tooltip.jsx";
import { AuthProvider } from "@/contexts/SupabaseAuthContext.jsx";
import SimulationLibraryPage from "@/pages/SimulationLibraryPage.jsx";
import SimulationProcessesPage from '@/pages/SimulationProcessesPage.jsx';

function App() {
  return (
    <AuthProvider>
      <TooltipProvider>
        <div className="min-h-screen flex flex-col bg-background">
          <Navbar />
          <main className="flex-1 flex flex-col">
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/login" element={<LoginPage />} />

              <Route path="/simulations" element={<SimulationLibraryPage />} />
              <Route path="/simulations/:simulationId" element={<SimulationProcessesPage />} />

              {/* Editor */}
              <Route path="/simulations/:simulationId/processes/:processId/edit" element={<ProcessEditor />} />

              {/* Simulation Setup */}
              <Route path="/simulations/:simulationId/processes/:processId/simulate" element={<SimulationSetup />} />

              {/* Simulation Results */}
              <Route path="/simulations/:simulationId/processes/:processId/results" element={<SimulationResults />} />
            </Routes>

          </main>
          <Toaster />
        </div>
      </TooltipProvider>
    </AuthProvider>
  );
}

export default App;