import React from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom'; // useNavigate importado
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Button } from '@/components/ui/button';
import { ArrowLeft, Settings2, ListChecks, FileText, PlayCircle } from 'lucide-react';
import RoleQueueMappingTab from '@/components/simulation-setup/RoleQueueMappingTab';
import ResourceTableTab from '@/components/simulation-setup/ResourceTableTab';
import XACDMLExportTab from '@/components/simulation-setup/XACDMLExportTab';
import { useToast } from "@/components/ui/use-toast";

const SimulationSetupPage = () => {
  const { id: processId } = useParams();
  const navigate = useNavigate();
  const { toast } = useToast();

  const handleRunSimulation = () => {
    // Lógica de simulação viria aqui
    // Por enquanto, apenas navega para a página de resultados
    toast({
      title: "Simulação Iniciada (Placeholder)",
      description: "Redirecionando para a página de resultados...",
      variant: "default",
    });
    navigate(`/processes/${processId}/results`);
  };

  return (
    <div className="flex-1 flex flex-col p-6 bg-gradient-to-br from-slate-900 to-slate-800 text-white">
      <header className="mb-6 flex items-center justify-between">
        <div>
          <Link to={`/processes/${processId}/edit`} className="inline-flex items-center text-sky-400 hover:text-sky-300 transition-colors">
            <ArrowLeft className="mr-2 h-5 w-5" />
            Back to Editor
          </Link>
          <h1 className="text-4xl font-bold mt-2 bg-clip-text text-transparent bg-gradient-to-r from-sky-400 to-blue-500">
            Simulation Setup & Export
          </h1>
          <p className="text-slate-400">Configure simulation parameters and export your process model for Process ID: <code className="bg-slate-700 px-1.5 py-0.5 rounded text-sky-300">{processId}</code></p>
        </div>
        <Button 
          onClick={handleRunSimulation}
          className="bg-green-500 hover:bg-green-600 text-white font-semibold py-3 px-6 rounded-lg shadow-lg"
        >
          <PlayCircle className="mr-2 h-5 w-5" />
          Run Simulation
        </Button>
      </header>

      <Tabs defaultValue="role-queue-mapping" className="flex-1 flex flex-col">
        <TabsList className="grid w-full grid-cols-3 bg-slate-700/50 p-1 rounded-lg mb-4">
          <TabsTrigger value="role-queue-mapping" className="data-[state=active]:bg-sky-500 data-[state=active]:text-white data-[state=inactive]:text-slate-300 hover:bg-sky-600/30 transition-colors py-2.5">
            <Settings2 className="mr-2 h-5 w-5" /> Role & Queue Mapping
          </TabsTrigger>
          <TabsTrigger value="resource-table" className="data-[state=active]:bg-sky-500 data-[state=active]:text-white data-[state=inactive]:text-slate-300 hover:bg-sky-600/30 transition-colors py-2.5">
            <ListChecks className="mr-2 h-5 w-5" /> Resource Table
          </TabsTrigger>
          <TabsTrigger value="xacdml-export" className="data-[state=active]:bg-sky-500 data-[state=active]:text-white data-[state=inactive]:text-slate-300 hover:bg-sky-600/30 transition-colors py-2.5">
            <FileText className="mr-2 h-5 w-5" /> XACDML Export
          </TabsTrigger>
        </TabsList>

        <TabsContent value="role-queue-mapping" className="flex-1 bg-slate-800/70 p-6 rounded-lg shadow-inner">
          <RoleQueueMappingTab processId={processId} />
        </TabsContent>
        <TabsContent value="resource-table" className="flex-1 bg-slate-800/70 p-6 rounded-lg shadow-inner">
          <ResourceTableTab processId={processId} />
        </TabsContent>
        <TabsContent value="xacdml-export" className="flex-1 bg-slate-800/70 p-6 rounded-lg shadow-inner">
          <XACDMLExportTab processId={processId} />
        </TabsContent>
      </Tabs>
    </div>
  );
};

export default SimulationSetupPage;