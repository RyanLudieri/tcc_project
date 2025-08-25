import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { PlusCircle, Trash2, Edit3, Save, XCircle } from 'lucide-react';
import { useToast } from "@/components/ui/use-toast";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';

const initialMappings = [
  { id: 'mapping1', name: 'Desenvolvedor Frontend', queueName: 'q_frontend_dev', queueType: 'FIFO', initialQuantity: 5, isEditing: false },
  { id: 'mapping2', name: 'Desenvolvedor Backend', queueName: 'q_backend_dev', queueType: 'LIFO', initialQuantity: 3, isEditing: false },
  { id: 'mapping3', name: 'Engenheiro QA', queueName: 'q_qa_test', queueType: 'Priority', initialQuantity: 2, isEditing: false },
  { id: 'mapping4', name: 'Product Owner', queueName: 'q_product_review', queueType: 'FIFO', initialQuantity: 1, isEditing: false },
];

const RoleQueueMappingTab = ({ processId }) => {
  const { toast } = useToast();
  const [mappings, setMappings] = useState(() => {
    const savedMappings = localStorage.getItem(`roleQueueMappings_${processId}`);
    return savedMappings ? JSON.parse(savedMappings) : initialMappings;
  });
  const [newMapping, setNewMapping] = useState({ name: '', queueName: '', queueType: 'FIFO', initialQuantity: 1 });
  const [isAdding, setIsAdding] = useState(false);

  useEffect(() => {
    localStorage.setItem(`roleQueueMappings_${processId}`, JSON.stringify(mappings));
  }, [mappings, processId]);

  const handleInputChange = (e, id) => {
    const { name, value } = e.target;
    if (id) {
      setMappings(mappings.map(m => m.id === id ? { ...m, [name]: name === 'initialQuantity' ? parseInt(value,10) : value } : m));
    } else {
      setNewMapping({ ...newMapping, [name]: name === 'initialQuantity' ? parseInt(value,10) : value });
    }
  };

  const handleSelectChange = (value, name, id) => {
    if (id) {
      setMappings(mappings.map(m => m.id === id ? { ...m, [name]: value } : m));
    } else {
      setNewMapping({ ...newMapping, [name]: value });
    }
  };

  const toggleEdit = (id) => {
    setMappings(mappings.map(m => m.id === id ? { ...m, isEditing: !m.isEditing } : m));
  };

  const saveMapping = (id) => {
    const mappingToSave = mappings.find(m => m.id === id);
    if (!mappingToSave.name || !mappingToSave.queueName) {
      toast({ title: "Erro", description: "Nome do Papel e Nome da Fila são obrigatórios.", variant: "destructive" });
      return;
    }
    toggleEdit(id);
    toast({ title: "Mapeamento Salvo", description: `Mapeamento para "${mappingToSave.name}" salvo.`, variant: "default" });
  };

  const addMapping = () => {
    if (!newMapping.name || !newMapping.queueName) {
      toast({ title: "Erro", description: "Nome do Papel e Nome da Fila são obrigatórios.", variant: "destructive" });
      return;
    }
    const newId = `mapping${mappings.length + 1}_${Date.now()}`;
    setMappings([...mappings, { ...newMapping, id: newId, isEditing: false }]);
    setNewMapping({ name: '', queueName: '', queueType: 'FIFO', initialQuantity: 1 });
    setIsAdding(false);
    toast({ title: "Mapeamento Adicionado", description: `Novo mapeamento "${newMapping.name}" adicionado.`, variant: "default" });
  };

  const removeMapping = (id) => {
    const mappingToRemove = mappings.find(m => m.id === id);
    setMappings(mappings.filter(m => m.id !== id));
    toast({ title: "Mapeamento Removido", description: `Mapeamento para "${mappingToRemove?.name}" removido.`, variant: "default" });
  };

  return (
    <Card className="bg-slate-800 border-slate-700 text-slate-50">
      <CardHeader>
        <CardTitle className="text-2xl text-sky-400">Mapeamento de Papéis e Filas</CardTitle>
        <CardDescription className="text-slate-400">
          Defina como os papéis do seu processo se conectam às filas de trabalho e suas configurações.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="mb-6">
          {!isAdding && (
            <Button onClick={() => setIsAdding(true)} className="bg-sky-500 hover:bg-sky-600 text-white">
              <PlusCircle className="mr-2 h-5 w-5" /> Adicionar Novo Mapeamento
            </Button>
          )}
          {isAdding && (
            <div className="p-4 border border-slate-700 rounded-lg bg-slate-700/50 space-y-4">
              <h3 className="text-lg font-semibold text-sky-300">Novo Mapeamento</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                <div>
                  <Label htmlFor="new-name" className="text-slate-300">Nome do Papel</Label>
                  <Input id="new-name" name="name" value={newMapping.name} onChange={(e) => handleInputChange(e)} placeholder="Ex: Desenvolvedor" className="bg-slate-800 border-slate-600 text-slate-50 placeholder:text-slate-500" />
                </div>
                <div>
                  <Label htmlFor="new-queueName" className="text-slate-300">Nome da Fila</Label>
                  <Input id="new-queueName" name="queueName" value={newMapping.queueName} onChange={(e) => handleInputChange(e)} placeholder="Ex: q_dev_tasks" className="bg-slate-800 border-slate-600 text-slate-50 placeholder:text-slate-500" />
                </div>
                <div>
                  <Label htmlFor="new-queueType" className="text-slate-300">Tipo da Fila</Label>
                  <Select name="queueType" value={newMapping.queueType} onValueChange={(value) => handleSelectChange(value, 'queueType')}>
                    <SelectTrigger id="new-queueType" className="bg-slate-800 border-slate-600 text-slate-50">
                      <SelectValue placeholder="Selecione o tipo" />
                    </SelectTrigger>
                    <SelectContent className="bg-slate-800 border-slate-600 text-slate-50">
                      <SelectItem value="FIFO" className="hover:bg-slate-700">FIFO</SelectItem>
                      <SelectItem value="LIFO" className="hover:bg-slate-700">LIFO</SelectItem>
                      <SelectItem value="Priority" className="hover:bg-slate-700">Priority</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                <div>
                  <Label htmlFor="new-initialQuantity" className="text-slate-300">Quantidade Inicial</Label>
                  <Input id="new-initialQuantity" type="number" name="initialQuantity" value={newMapping.initialQuantity} onChange={(e) => handleInputChange(e)} min="0" className="bg-slate-800 border-slate-600 text-slate-50" />
                </div>
              </div>
              <div className="flex justify-end gap-2 mt-2">
                <Button variant="outline" onClick={() => setIsAdding(false)} className="text-slate-300 border-slate-600 hover:bg-slate-700">
                  <XCircle className="mr-2 h-4 w-4" /> Cancelar
                </Button>
                <Button onClick={addMapping} className="bg-green-500 hover:bg-green-600 text-white">
                  <Save className="mr-2 h-4 w-4" /> Salvar Mapeamento
                </Button>
              </div>
            </div>
          )}
        </div>

        <div className="overflow-x-auto">
          <Table className="min-w-full">
            <TableHeader>
              <TableRow className="border-slate-700 hover:bg-slate-700/30">
                <TableHead className="text-sky-300">Nome do Papel</TableHead>
                <TableHead className="text-sky-300">Nome da Fila</TableHead>
                <TableHead className="text-sky-300">Tipo da Fila</TableHead>
                <TableHead className="text-sky-300 text-right">Qtde. Inicial</TableHead>
                <TableHead className="text-sky-300 text-center">Ações</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {mappings.map((mapping) => (
                <TableRow key={mapping.id} className="border-slate-700 hover:bg-slate-700/30">
                  <TableCell>
                    {mapping.isEditing ? (
                      <Input name="name" value={mapping.name} onChange={(e) => handleInputChange(e, mapping.id)} className="bg-slate-700 border-slate-600 text-slate-50" />
                    ) : (
                      mapping.name
                    )}
                  </TableCell>
                  <TableCell>
                    {mapping.isEditing ? (
                      <Input name="queueName" value={mapping.queueName} onChange={(e) => handleInputChange(e, mapping.id)} className="bg-slate-700 border-slate-600 text-slate-50" />
                    ) : (
                      mapping.queueName
                    )}
                  </TableCell>
                  <TableCell>
                    {mapping.isEditing ? (
                      <Select name="queueType" value={mapping.queueType} onValueChange={(value) => handleSelectChange(value, 'queueType', mapping.id)}>
                        <SelectTrigger className="bg-slate-700 border-slate-600 text-slate-50">
                          <SelectValue placeholder="Selecione o tipo" />
                        </SelectTrigger>
                        <SelectContent className="bg-slate-800 border-slate-600 text-slate-50">
                          <SelectItem value="FIFO" className="hover:bg-slate-700">FIFO</SelectItem>
                          <SelectItem value="LIFO" className="hover:bg-slate-700">LIFO</SelectItem>
                          <SelectItem value="Priority" className="hover:bg-slate-700">Priority</SelectItem>
                        </SelectContent>
                      </Select>
                    ) : (
                      mapping.queueType
                    )}
                  </TableCell>
                  <TableCell className="text-right">
                    {mapping.isEditing ? (
                      <Input type="number" name="initialQuantity" value={mapping.initialQuantity} onChange={(e) => handleInputChange(e, mapping.id)} min="0" className="bg-slate-700 border-slate-600 text-slate-50 w-20 text-right" />
                    ) : (
                      mapping.initialQuantity
                    )}
                  </TableCell>
                  <TableCell className="text-center">
                    <div className="flex justify-center gap-2">
                      {mapping.isEditing ? (
                        <Button size="sm" onClick={() => saveMapping(mapping.id)} className="bg-green-500 hover:bg-green-600 text-white">
                          <Save className="h-4 w-4" />
                        </Button>
                      ) : (
                        <Button size="sm" variant="outline" onClick={() => toggleEdit(mapping.id)} className="text-sky-400 border-sky-400 hover:bg-sky-400 hover:text-slate-900">
                          <Edit3 className="h-4 w-4" />
                        </Button>
                      )}
                      <Button size="sm" variant="outline" onClick={() => removeMapping(mapping.id)} className="text-red-400 border-red-400 hover:bg-red-400 hover:text-white">
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </div>
        {mappings.length === 0 && !isAdding && (
          <p className="text-center text-slate-500 mt-4">Nenhum mapeamento definido. Clique em "Adicionar Novo Mapeamento" para começar.</p>
        )}
      </CardContent>
    </Card>
  );
};

export default RoleQueueMappingTab;