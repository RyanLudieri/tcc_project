import React, { useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Button } from '@/components/ui/button';
import {ArrowLeft, Settings2, ListChecks, FileText, PlayCircle, Layers} from 'lucide-react';
import RoleQueueMappingTab from '@/components/simulation-setup/RoleQueueMappingTab';
import WorkProductsTableTab from '@/components/simulation-setup/WorkProductsTableTab.jsx';
import XACDMLExportTab from '@/components/simulation-setup/XACDMLExportTab';
import WorkBreakdownElementsTab from '@/components/simulation-setup/work-breakdown-elements/WorkBreakdownElementsTab.jsx';
import { useToast } from "@/components/ui/use-toast";

const SimulationSetup = () => {
  const { simulationId, processId } = useParams();
  const navigate = useNavigate();
  const { toast } = useToast();

  useEffect(() => {
    if (processId && processId !== "new") {
      localStorage.setItem("lastProcessId", processId);
    }
  }, [processId]);

  const lastProcessId = localStorage.getItem("lastProcessId") || processId;

  const handleRunSimulation = () => {
    toast({
      title: "Simulation Started (Placeholder)",
      description: "Redirecting to results page...",
      variant: "default",
    });
    navigate(`/simulations/${simulationId}/processes/${processId}/results`);
  };

  return (
      <div className="flex-1 flex flex-col p-6 bg-gray-100 dark:bg-gray-900 text-foreground">
        <header className="mb-6 flex items-center justify-between">
          <div>
            <Link
                to={`/simulations/${simulationId}/processes/${processId}/edit`}
                className="inline-flex items-center text-primary hover:underline transition-colors"
            >
              <ArrowLeft className="mr-2 h-5 w-5" />
              Back to Editor
            </Link>
            <h1 className="text-4xl font-bold mt-2 bg-clip-text text-transparent bg-gradient-to-r from-primary to-sky-600">
              Simulation Setup & Export
            </h1>
            <p className="text-muted-foreground">
              Configure simulation parameters and export your process model for Process ID:{" "}
              <code className="bg-muted px-1.5 py-0.5 rounded text-primary font-mono">
                {processId}
              </code>
            </p>
          </div>
          <Button
              onClick={handleRunSimulation}
              className="bg-accent hover:bg-accent/90 text-accent-foreground font-semibold py-3 px-6 rounded-lg shadow-lg"
          >
            <PlayCircle className="mr-2 h-5 w-5" />
            Run Simulation
          </Button>
        </header>

        <Tabs defaultValue="roles-mapping" className="flex-1 flex flex-col">
          <TabsList className="flex w-full flex-wrap gap-4 bg-muted p-1 rounded-lg mb-4">
            <TabsTrigger
                value="roles-mapping"
                className="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground
            data-[state=inactive]:text-muted-foreground hover:bg-primary/10 transition-colors py-2.5 flex-1 min-w-[120px] text-center"
            >
              <Settings2 className="mr-2 h-5 w-5" /> Roles Mapping
            </TabsTrigger>
            <TabsTrigger
                value="resource-table"
                className="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground
            data-[state=inactive]:text-muted-foreground hover:bg-primary/10 transition-colors py-2.5 flex-1 min-w-[120px] text-center"
            >
              <ListChecks className="mr-2 h-5 w-5" /> Work Products Mapping
            </TabsTrigger>
            <TabsTrigger
                value="work-breakdown-elements-table"
                className="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground
            data-[state=inactive]:text-muted-foreground hover:bg-primary/10 transition-colors py-2.5 flex-1 min-w-[120px] text-center"
            >
              <Layers className="mr-2 h-5 w-5" /> Work Breakdown Elements Mapping
            </TabsTrigger>
            <TabsTrigger
                value="xacdml-export"
                className="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground
            data-[state=inactive]:text-muted-foreground hover:bg-primary/10 transition-colors py-2.5 flex-1 min-w-[120px] text-center"
            >
              <FileText className="mr-2 h-5 w-5" /> XACDML Export
            </TabsTrigger>
          </TabsList>


          <TabsContent value="roles-mapping" className="flex-1 bg-card p-6 rounded-lg shadow-inner">
            <RoleQueueMappingTab processId={processId} />
          </TabsContent>
          <TabsContent value="resource-table" className="flex-1 bg-card p-6 rounded-lg shadow-inner">
            <WorkProductsTableTab processId={processId} />
          </TabsContent>
          <TabsContent value="work-breakdown-elements-table" className="flex-1 bg-card p-6 rounded-lg shadow-inner">
            <WorkBreakdownElementsTab processId={processId} />
          </TabsContent>
          <TabsContent value="xacdml-export" className="flex-1 bg-card p-6 rounded-lg shadow-inner">
            <XACDMLExportTab processId={processId} />
          </TabsContent>
        </Tabs>
      </div>
  );
};

export default SimulationSetup;
