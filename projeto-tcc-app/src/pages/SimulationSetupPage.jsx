import React, { useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Button } from '@/components/ui/button';
import { ArrowLeft, Settings2, ListChecks, FileText, PlayCircle } from 'lucide-react';
import RoleQueueMappingTab from '@/components/simulation-setup/RoleQueueMappingTab';
import WorkProductsTableTab from '@/components/simulation-setup/WorkProductsTableTab.jsx';
import XACDMLExportTab from '@/components/simulation-setup/XACDMLExportTab';
import { useToast } from "@/components/ui/use-toast";

const SimulationSetupPage = () => {
  const { id: processId } = useParams();
  const navigate = useNavigate();
  const { toast } = useToast();

  // Guarda o último processo válido
  useEffect(() => {
    if (processId && processId !== "new") {
      localStorage.setItem("lastProcessId", processId);
    }
  }, [processId]);

  // Recupera o último processo para o link de back
  const lastProcessId = localStorage.getItem("lastProcessId") || processId;

  const handleRunSimulation = () => {
    toast({
      title: "Simulation Started (Placeholder)",
      description: "Redirecting to results page...",
      variant: "default",
    });
    navigate(`/processes/${processId}/results`);
  };

  return (
      <div className="flex-1 flex flex-col p-6 bg-background text-foreground">

        <header className="mb-6 flex items-center justify-between">
          <div>
            <Link
                to={`/processes/${lastProcessId}/edit`}
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

        <Tabs defaultValue="role-queue-mapping" className="flex-1 flex flex-col">
          <TabsList className="grid w-full grid-cols-3 bg-muted p-1 rounded-lg mb-4">
            <TabsTrigger
                value="role-queue-mapping"
                className="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground
                       data-[state=inactive]:text-muted-foreground hover:bg-primary/10 transition-colors py-2.5"
            >
              <Settings2 className="mr-2 h-5 w-5" /> Role & Queue Mapping
            </TabsTrigger>
            <TabsTrigger
                value="resource-table"
                className="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground
                       data-[state=inactive]:text-muted-foreground hover:bg-primary/10 transition-colors py-2.5"
            >
              <ListChecks className="mr-2 h-5 w-5" /> Work Products Table
            </TabsTrigger>
            <TabsTrigger
                value="xacdml-export"
                className="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground
                       data-[state=inactive]:text-muted-foreground hover:bg-primary/10 transition-colors py-2.5"
            >
              <FileText className="mr-2 h-5 w-5" /> XACDML Export
            </TabsTrigger>
          </TabsList>

          <TabsContent value="role-queue-mapping" className="flex-1 bg-card p-6 rounded-lg shadow-inner">
            <RoleQueueMappingTab processId={processId} />
          </TabsContent>
          <TabsContent value="resource-table" className="flex-1 bg-card p-6 rounded-lg shadow-inner">
            <WorkProductsTableTab processId={processId} />
          </TabsContent>
          <TabsContent value="xacdml-export" className="flex-1 bg-card p-6 rounded-lg shadow-inner">
            <XACDMLExportTab processId={processId} />
          </TabsContent>
        </Tabs>
      </div>
  );
};

export default SimulationSetupPage;
