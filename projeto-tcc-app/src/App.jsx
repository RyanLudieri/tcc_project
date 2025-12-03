import React from "react";
import { Routes, Route } from "react-router-dom";
import { Toaster } from "@/components/ui/toaster.jsx";
import Navbar from "@/components/Navbar.jsx";
import HomePage from "@/pages/HomePage.jsx";
import ProcessEditor from "@/pages/ProcessEditor.jsx";
import Setup from "@/pages/Setup.jsx";
import SimulationResults from "@/pages/SimulationResults.jsx";
import LoginPage from "@/components/auth/LoginPage.jsx";
import { TooltipProvider } from "@/components/ui/tooltip.jsx";
import { AuthProvider } from "@/contexts/SupabaseAuthContext.jsx";
import LibrarySimulations from "@/pages/LibrarySimulations.jsx";
import LibraryProcesses from '@/pages/LibraryProcesses.jsx';
import Simulate from '@/pages/Simulate.jsx';

function App() {
  return (
    <AuthProvider>
      <TooltipProvider>
        <div className="min-h-screen flex flex-col bg-background">
          <Navbar />
          <main className="flex-1 flex flex-col">
            <Routes>
              {/* Login/SignUp */}
              <Route path="/" element={<HomePage />} />
              <Route path="/login" element={<LoginPage />} />

              {/* Libraries */}
              <Route path="/simulations" element={<LibrarySimulations />} />
              <Route path="/simulations/:simulationId" element={<LibraryProcesses />} />

              {/* Editor */}
              <Route path="/simulations/:simulationId/processes/:processId/edit" element={<ProcessEditor />} />

              {/* Simulation Setup */}
              <Route path="/simulations/:simulationId/processes/:processId/setup" element={<Setup />} />

              {/* Simulation Experimentation Setup */}
              <Route path="/simulations/:simulationId/processes/:processId/simulate" element={<Simulate />} />

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