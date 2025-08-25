import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Copy, Download, Save, CheckCircle, AlertTriangle } from 'lucide-react';
import { useToast } from "@/components/ui/use-toast";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';

const exampleXACDML = `<?xml version="1.0" encoding="UTF-8"?>
<MethodPlugin name="Exemplo de Processo Ágil" id="exemplo.processo.agil.plugin"
    xmlns="http://www.eclipse.org/epf/uma/ फैल/1.0.6"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.eclipse.org/epf/uma/ फैल/1.0.6 file:/C:/EPF/composer/plugins/org.eclipse.epf.uma.methodplugin/schema/methodplugin.xsd">
    <MethodPackage name="Pacote de Capacidade" id="exemplo.pacote.capacidade">
        <CapabilityPattern name="Desenvolvimento Iterativo" id="desenvolvimento.iterativo.pattern">
            <Activity name="Fase de Iniciação" id="fase.iniciacao">
                <TaskDescriptor name="Definir Escopo" id="task.definir.escopo"/>
                <RoleDescriptor name="Analista de Negócios" id="role.analista.negocios"/>
                <WorkProductDescriptor name="Documento de Visão" id="wp.documento.visao"/>
            </Activity>
            <Activity name="Fase de Construção" id="fase.construcao">
                <TaskDescriptor name="Implementar Funcionalidade" id="task.implementar.funcionalidade"/>
                <RoleDescriptor name="Desenvolvedor" id="role.desenvolvedor"/>
                <WorkProductDescriptor name="Código Fonte" id="wp.codigo.fonte"/>
            </Activity>
        </CapabilityPattern>
    </MethodPackage>
</MethodPlugin>
`;

const XACDMLExportTab = ({ processId }) => {
  const { toast } = useToast();
  const [acdId, setAcdId] = useState(() => localStorage.getItem(`xacdml_acdId_${processId}`) || 'exemplo.acd.id.001');
  const [processName, setProcessName] = useState(() => localStorage.getItem(`xacdml_processName_${processId}`) || 'Meu Processo Simulado');
  const [generatedXACDML, setGeneratedXACDML] = useState(exampleXACDML);
  const [isValidated, setIsValidated] = useState(null); // null, true, false

  useEffect(() => {
    localStorage.setItem(`xacdml_acdId_${processId}`, acdId);
  }, [acdId, processId]);

  useEffect(() => {
    localStorage.setItem(`xacdml_processName_${processId}`, processName);
  }, [processName, processId]);
  
  const handleGenerateXACDML = () => {
    toast({
      title: "Geração XACDML",
      description: "Simulando geração de XACDML com base nos campos e dados do processo (funcionalidade de geração real pendente).",
      variant: "default",
    });
    setGeneratedXACDML(exampleXACDML.replace('Exemplo de Processo Ágil', processName).replace('exemplo.processo.agil.plugin', acdId));
    setIsValidated(null); 
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(generatedXACDML);
    toast({ title: "Copiado!", description: "Conteúdo XACDML copiado para a área de transferência.", variant: "default" });
  };

  const handleDownload = () => {
    const blob = new Blob([generatedXACDML], { type: 'application/xml' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${acdId || 'process'}.xml`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
    toast({ title: "Download Iniciado", description: `Baixando ${acdId || 'process'}.xml`, variant: "default" });
  };
  
  const handleSaveXACDML = () => {
     toast({
      title: "Salvar XACDML",
      description: "🚧 Esta funcionalidade (salvar no servidor/storage) ainda não foi implementada.",
      variant: "default",
    });
  };

  const handleValidateXACDML = () => {
    toast({
      title: "Validação XACDML",
      description: "Simulando validação do XACDML (funcionalidade de validação real pendente).",
      variant: "default",
    });
    // Simula uma validação aleatória
    const isValid = Math.random() > 0.3;
    setIsValidated(isValid);
    if (isValid) {
      toast({ title: "Validação Concluída", description: "O XACDML parece estar válido!", variant: "default", className: "bg-green-500 text-white" });
    } else {
      toast({ title: "Validação Falhou", description: "Foram encontrados problemas no XACDML.", variant: "destructive" });
    }
  };


  return (
    <Card className="bg-slate-800 border-slate-700 text-slate-50">
      <CardHeader>
        <CardTitle className="text-2xl text-sky-400">Exportação XACDML</CardTitle>
        <CardDescription className="text-slate-400">
          Gere e exporte a representação XACDML do seu processo.
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-6">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <Label htmlFor="acdId" className="text-slate-300">ACD ID</Label>
            <Input 
              id="acdId" 
              value={acdId} 
              onChange={(e) => setAcdId(e.target.value)} 
              placeholder="ex: com.example.myprocess"
              className="bg-slate-700 border-slate-600 text-slate-50 placeholder:text-slate-500" 
            />
          </div>
          <div>
            <Label htmlFor="processName" className="text-slate-300">Nome do Processo</Label>
            <Input 
              id="processName" 
              value={processName} 
              onChange={(e) => setProcessName(e.target.value)} 
              placeholder="Ex: Meu Processo de Desenvolvimento"
              className="bg-slate-700 border-slate-600 text-slate-50 placeholder:text-slate-500"
            />
          </div>
        </div>

        <Button onClick={handleGenerateXACDML} className="w-full bg-sky-500 hover:bg-sky-600 text-white">
          Gerar XACDML
        </Button>

        <div>
          <Label htmlFor="xacdmlOutput" className="text-slate-300">Saída XACDML (Read-only)</Label>
          <Textarea
            id="xacdmlOutput"
            value={generatedXACDML}
            readOnly
            rows={15}
            className="bg-slate-900 border-slate-700 text-slate-300 font-mono text-sm resize-none"
          />
        </div>

        <div className="flex flex-wrap gap-3 items-center">
          <Button variant="outline" onClick={handleCopy} className="text-sky-400 border-sky-400 hover:bg-sky-400 hover:text-slate-900">
            <Copy className="mr-2 h-4 w-4" /> Copiar
          </Button>
          <Button variant="outline" onClick={handleDownload} className="text-sky-400 border-sky-400 hover:bg-sky-400 hover:text-slate-900">
            <Download className="mr-2 h-4 w-4" /> Download
          </Button>
           <Button variant="outline" onClick={handleSaveXACDML} className="text-green-400 border-green-400 hover:bg-green-400 hover:text-slate-900">
            <Save className="mr-2 h-4 w-4" /> Salvar XACDML
          </Button>
          <Button variant="outline" onClick={handleValidateXACDML} className={`
            ${isValidated === true ? 'text-green-400 border-green-400 hover:bg-green-400' : 
              isValidated === false ? 'text-red-400 border-red-400 hover:bg-red-400' : 
              'text-yellow-400 border-yellow-400 hover:bg-yellow-400'} 
            hover:text-slate-900 flex items-center
          `}>
            {isValidated === true && <CheckCircle className="mr-2 h-4 w-4" />}
            {isValidated === false && <AlertTriangle className="mr-2 h-4 w-4" />}
            {isValidated === null && <AlertTriangle className="mr-2 h-4 w-4 opacity-50" />}
            Validar XACDML
          </Button>
        </div>
         {isValidated === true && <p className="text-sm text-green-400">XACDML validado com sucesso!</p>}
         {isValidated === false && <p className="text-sm text-red-400">XACDML contém erros. Verifique a estrutura.</p>}
      </CardContent>
    </Card>
  );
};

export default XACDMLExportTab;