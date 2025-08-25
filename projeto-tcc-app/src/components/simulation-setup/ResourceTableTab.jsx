import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Checkbox } from '@/components/ui/checkbox';
import { PlusCircle, Trash2, Edit3, Save, XCircle } from 'lucide-react';
import { useToast } from "@/components/ui/use-toast";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';

const initialResources = [
  { id: 'res1', workProduct: 'Documento de Requisitos', inputOutput: 'Input', taskName: 'Análise de Requisitos', queueName: 'q_analysis', queueSize: 10, queueInitialQuantity: 2, policy: 'FIFO', generateActivity: true, isEditing: false },
  { id: 'res2', workProduct: 'Protótipo UI', inputOutput: 'Output', taskName: 'Design de Interface', queueName: 'q_design_review', queueSize: 5, queueInitialQuantity: 1, policy: 'FIFO', generateActivity: false, isEditing: false },
  { id: 'res3', workProduct: 'Módulo de Login', inputOutput: 'Output', taskName: 'Desenvolvimento Backend API', queueName: 'q_dev_backend', queueSize: 20, queueInitialQuantity: 5, policy: 'LIFO', generateActivity: true, isEditing: false },
  { id: 'res4', workProduct: 'Relatório de Bugs', inputOutput: 'Input', taskName: 'Teste de Aceitação', queueName: 'q_testing_bugs', queueSize: 15, queueInitialQuantity: 0, policy: 'Priority', generateActivity: true, isEditing: false },
];


const ResourceTableTab = ({ processId }) => {
  const { toast } = useToast();
  const [resources, setResources] = useState(() => {
    const savedResources = localStorage.getItem(`resourceTable_${processId}`);
    return savedResources ? JSON.parse(savedResources) : initialResources;
  });
  const [newResource, setNewResource] = useState({ workProduct: '', inputOutput: 'Input', taskName: '', queueName: '', queueSize: 10, queueInitialQuantity: 0, policy: 'FIFO', generateActivity: true });
  const [isAdding, setIsAdding] = useState(false);

  useEffect(() => {
    localStorage.setItem(`resourceTable_${processId}`, JSON.stringify(resources));
  }, [resources, processId]);

  const handleInputChange = (e, id) => {
    const { name, value, type, checked } = e.target;
    const val = type === 'checkbox' ? checked : (name === 'queueSize' || name === 'queueInitialQuantity' ? parseInt(value, 10) : value);
    if (id) {
      setResources(resources.map(r => r.id === id ? { ...r, [name]: val } : r));
    } else {
      setNewResource({ ...newResource, [name]: val });
    }
  };

  const handleSelectChange = (value, name, id) => {
     if (id) {
      setResources(resources.map(r => r.id === id ? { ...r, [name]: value } : r));
    } else {
      setNewResource({ ...newResource, [name]: value });
    }
  };

  const toggleEdit = (id) => {
    setResources(resources.map(r => r.id === id ? { ...r, isEditing: !r.isEditing } : r));
  };

  const saveResource = (id) => {
    const resourceToSave = resources.find(r => r.id === id);
    if (!resourceToSave.workProduct || !resourceToSave.taskName || !resourceToSave.queueName) {
      toast({ title: "Erro", description: "Produto de Trabalho, Nome da Tarefa e Nome da Fila são obrigatórios.", variant: "destructive" });
      return;
    }
    toggleEdit(id);
    toast({ title: "Recurso Salvo", description: `Recurso "${resourceToSave.workProduct}" salvo.`, variant: "default" });
  };

  const addResource = () => {
    if (!newResource.workProduct || !newResource.taskName || !newResource.queueName) {
      toast({ title: "Erro", description: "Produto de Trabalho, Nome da Tarefa e Nome da Fila são obrigatórios.", variant: "destructive" });
      return;
    }
    const newId = `res${resources.length + 1}_${Date.now()}`;
    setResources([...resources, { ...newResource, id: newId, isEditing: false }]);
    setNewResource({ workProduct: '', inputOutput: 'Input', taskName: '', queueName: '', queueSize: 10, queueInitialQuantity: 0, policy: 'FIFO', generateActivity: true });
    setIsAdding(false);
    toast({ title: "Recurso Adicionado", description: `Novo recurso "${newResource.workProduct}" adicionado.`, variant: "default" });
  };

  const removeResource = (id) => {
    const resourceToRemove = resources.find(r => r.id === id);
    setResources(resources.filter(r => r.id !== id));
    toast({ title: "Recurso Removido", description: `Recurso "${resourceToRemove?.workProduct}" removido.`, variant: "default" });
  };

  const renderInputField = (resource, fieldName, placeholder, type = "text") => (
    <Input 
      name={fieldName} 
      type={type}
      value={resource[fieldName]} 
      onChange={(e) => handleInputChange(e, resource.id)} 
      placeholder={placeholder}
      className="bg-slate-700 border-slate-600 text-slate-50 placeholder:text-slate-500"
      min={type === "number" ? "0" : undefined}
    />
  );

  const renderSelectField = (resource, fieldName, options) => (
    <Select name={fieldName} value={resource[fieldName]} onValueChange={(value) => handleSelectChange(value, fieldName, resource.id)}>
      <SelectTrigger className="bg-slate-700 border-slate-600 text-slate-50">
        <SelectValue placeholder={`Selecione ${fieldName === 'inputOutput' ? 'Entrada/Saída' : 'Política'}`} />
      </SelectTrigger>
      <SelectContent className="bg-slate-800 border-slate-600 text-slate-50">
        {options.map(opt => <SelectItem key={opt.value} value={opt.value} className="hover:bg-slate-700">{opt.label}</SelectItem>)}
      </SelectContent>
    </Select>
  );

  const renderCheckboxField = (resource, fieldName) => (
     <div className="flex items-center justify-center h-full">
        <Checkbox
            name={fieldName}
            checked={resource[fieldName]}
            onCheckedChange={(checked) => handleInputChange({ target: { name: fieldName, checked, type: 'checkbox' } }, resource.id)}
            className="data-[state=checked]:bg-sky-500 data-[state=checked]:text-white border-slate-600"
        />
    </div>
  );


  return (
    <Card className="bg-slate-800 border-slate-700 text-slate-50">
      <CardHeader>
        <CardTitle className="text-2xl text-sky-400">Tabela de Recursos e Filas</CardTitle>
        <CardDescription className="text-slate-400">
          Configure os produtos de trabalho, suas tarefas associadas, filas e políticas para a simulação.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="mb-6">
          {!isAdding && (
            <Button onClick={() => setIsAdding(true)} className="bg-sky-500 hover:bg-sky-600 text-white">
              <PlusCircle className="mr-2 h-5 w-5" /> Adicionar Novo Recurso
            </Button>
          )}
          {isAdding && (
            <div className="p-4 border border-slate-700 rounded-lg bg-slate-700/50 space-y-4">
              <h3 className="text-lg font-semibold text-sky-300">Novo Recurso</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 items-end">
                <div>
                  <Label htmlFor="new-workProduct" className="text-slate-300">Produto de Trabalho</Label>
                  {renderInputField(newResource, 'workProduct', 'Ex: Documento X', 'text')}
                </div>
                <div>
                  <Label htmlFor="new-inputOutput" className="text-slate-300">Entrada/Saída</Label>
                  {renderSelectField(newResource, 'inputOutput', [{value: 'Input', label: 'Input'}, {value: 'Output', label: 'Output'}])}
                </div>
                <div>
                  <Label htmlFor="new-taskName" className="text-slate-300">Nome da Tarefa</Label>
                  {renderInputField(newResource, 'taskName', 'Ex: Revisar Documento', 'text')}
                </div>
                <div>
                  <Label htmlFor="new-queueName" className="text-slate-300">Nome da Fila</Label>
                  {renderInputField(newResource, 'queueName', 'Ex: q_revisao', 'text')}
                </div>
                <div>
                  <Label htmlFor="new-queueSize" className="text-slate-300">Tam. Fila</Label>
                  {renderInputField(newResource, 'queueSize', '10', 'number')}
                </div>
                <div>
                  <Label htmlFor="new-queueInitialQuantity" className="text-slate-300">Qtde. Inicial Fila</Label>
                  {renderInputField(newResource, 'queueInitialQuantity', '0', 'number')}
                </div>
                <div>
                  <Label htmlFor="new-policy" className="text-slate-300">Política</Label>
                  {renderSelectField(newResource, 'policy', [{value: 'FIFO', label: 'FIFO'}, {value: 'LIFO', label: 'LIFO'}, {value: 'Priority', label: 'Priority'}])}
                </div>
                <div className="flex flex-col items-start">
                  <Label htmlFor="new-generateActivity" className="text-slate-300 mb-1.5">Gerar Atividade?</Label>
                  {renderCheckboxField(newResource, 'generateActivity')}
                </div>
              </div>
              <div className="flex justify-end gap-2 mt-2">
                <Button variant="outline" onClick={() => setIsAdding(false)} className="text-slate-300 border-slate-600 hover:bg-slate-700">
                  <XCircle className="mr-2 h-4 w-4" /> Cancelar
                </Button>
                <Button onClick={addResource} className="bg-green-500 hover:bg-green-600 text-white">
                  <Save className="mr-2 h-4 w-4" /> Salvar Recurso
                </Button>
              </div>
            </div>
          )}
        </div>

        <div className="overflow-x-auto">
          <Table className="min-w-full">
            <TableHeader>
              <TableRow className="border-slate-700 hover:bg-slate-700/30">
                <TableHead className="text-sky-300">Produto de Trabalho</TableHead>
                <TableHead className="text-sky-300">Entrada/Saída</TableHead>
                <TableHead className="text-sky-300">Nome da Tarefa</TableHead>
                <TableHead className="text-sky-300">Nome da Fila</TableHead>
                <TableHead className="text-sky-300 text-right">Tam. Fila</TableHead>
                <TableHead className="text-sky-300 text-right">Qtde. Inicial</TableHead>
                <TableHead className="text-sky-300">Política</TableHead>
                <TableHead className="text-sky-300 text-center">Gerar Atividade?</TableHead>
                <TableHead className="text-sky-300 text-center">Ações</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {resources.map((resource) => (
                <TableRow key={resource.id} className="border-slate-700 hover:bg-slate-700/30">
                  <TableCell>{resource.isEditing ? renderInputField(resource, 'workProduct', 'Produto de Trabalho') : resource.workProduct}</TableCell>
                  <TableCell>{resource.isEditing ? renderSelectField(resource, 'inputOutput', [{value: 'Input', label: 'Input'}, {value: 'Output', label: 'Output'}]) : resource.inputOutput}</TableCell>
                  <TableCell>{resource.isEditing ? renderInputField(resource, 'taskName', 'Nome da Tarefa') : resource.taskName}</TableCell>
                  <TableCell>{resource.isEditing ? renderInputField(resource, 'queueName', 'Nome da Fila') : resource.queueName}</TableCell>
                  <TableCell className="text-right">{resource.isEditing ? renderInputField(resource, 'queueSize', '10', 'number') : resource.queueSize}</TableCell>
                  <TableCell className="text-right">{resource.isEditing ? renderInputField(resource, 'queueInitialQuantity', '0', 'number') : resource.queueInitialQuantity}</TableCell>
                  <TableCell>{resource.isEditing ? renderSelectField(resource, 'policy', [{value: 'FIFO', label: 'FIFO'}, {value: 'LIFO', label: 'LIFO'}, {value: 'Priority', label: 'Priority'}]) : resource.policy}</TableCell>
                  <TableCell className="text-center">{resource.isEditing ? renderCheckboxField(resource, 'generateActivity') : (resource.generateActivity ? 'Sim' : 'Não')}</TableCell>
                  <TableCell className="text-center">
                    <div className="flex justify-center gap-2">
                      {resource.isEditing ? (
                        <Button size="sm" onClick={() => saveResource(resource.id)} className="bg-green-500 hover:bg-green-600 text-white">
                          <Save className="h-4 w-4" />
                        </Button>
                      ) : (
                        <Button size="sm" variant="outline" onClick={() => toggleEdit(resource.id)} className="text-sky-400 border-sky-400 hover:bg-sky-400 hover:text-slate-900">
                          <Edit3 className="h-4 w-4" />
                        </Button>
                      )}
                      <Button size="sm" variant="outline" onClick={() => removeResource(resource.id)} className="text-red-400 border-red-400 hover:bg-red-400 hover:text-white">
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
         {resources.length === 0 && !isAdding && (
          <p className="text-center text-slate-500 mt-4">Nenhum recurso definido. Clique em "Adicionar Novo Recurso" para começar.</p>
        )}
      </CardContent>
    </Card>
  );
};

export default ResourceTableTab;