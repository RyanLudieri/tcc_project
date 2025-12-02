import React, { useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { ArrowLeft, Rocket } from 'lucide-react';
import { useToast } from "@/components/ui/use-toast";
import { API_BASE_URL } from "@/config/api";
import WorkProductsVariablesTable from "@/components/simulation-experimentation-setup/WorkProductsVariablesTable.jsx";

const Simulate = () => {
  const { simulationId, processId } = useParams();
  const navigate = useNavigate();
  const { toast } = useToast();

  useEffect(() => {
    if (processId && processId !== "new") {
      localStorage.setItem("lastProcessId", processId);
    }
  }, [processId]);

  const handleGenerateSimulation = async () => {
    try {
      toast({
        title: "Executing Simulation",
        description: "Please wait...",
      });

      const duration = 36000.0;
      const reps = 10;

      const response = await fetch(
          `${API_BASE_URL}/simulations/execute?simulationDuration=${duration}&replications=${reps}`,
          {
            method: "POST",
            headers: { "Content-Type": "application/json" }
          }
      );

      if (!response.ok) {
        const err = await response.text();
        throw new Error(err || "Failed to execute simulation");
      }

      navigate(`/simulations/${simulationId}/processes/${processId}/results`);

    } catch (error) {
      console.error(error);
      toast({
        title: "Error",
        description: error.message,
        variant: "destructive",
      });
    }
  };

  return (
      <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
          className="flex-1 flex flex-col p-6 bg-gray-100 dark:bg-gray-900"
      >
        <header className="mb-6 flex items-center justify-between">
          <div>
            <Link
                to={`/simulations/${simulationId}/processes/${processId}/setup`}
                className="inline-flex items-center text-primary hover:text-primary/80 transition-colors mb-2 group"
            >
              <ArrowLeft className="mr-2 h-5 w-5 group-hover:-translate-x-1 transition-transform" />
              Back to Simulation Setup
            </Link>

            <h1 className="text-4xl font-bold mt-2 bg-clip-text text-transparent bg-gradient-to-r from-primary to-sky-600">
              Simulation Experimentation Setup
            </h1>

            <p className="text-muted-foreground">
              Configure experimentation for simulation parameters for process:&nbsp;
              <code className="bg-muted px-1.5 py-0.5 rounded text-primary font-mono">
                {processId}
              </code>
            </p>
          </div>

          <Button
              onClick={handleGenerateSimulation}
              className="bg-accent text-green-950 font-semibold py-3 px-6 rounded-lg shadow-lg
                          relative overflow-hidden shimmer-btn
                          hover:bg-accent hover:text-green-950
                          hover:scale-[1.04] active:scale-[0.98]
                          transition-all duration-300
                          animated-border
                          btn-simulation"
          >
            <Rocket className="mr-2 h-5 w-5" />
            Simulate
          </Button>
        </header>

        <div className="bg-card p-6 rounded-lg shadow-inner">
          <WorkProductsVariablesTable processId={processId} />
        </div>
      </motion.div>
  );
};

export default Simulate;
