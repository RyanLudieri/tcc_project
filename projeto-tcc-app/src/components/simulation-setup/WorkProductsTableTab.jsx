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

const initialWorkProducts = [
  { id: 'res1', workProduct: 'Requirements Document', inputOutput: 'Input', taskName: 'Requirements Analysis', queueName: 'q_analysis', queueSize: 10, queueInitialQuantity: 2, policy: 'FIFO', generateActivity: true, isEditing: false },
  { id: 'res2', workProduct: 'UI Prototype', inputOutput: 'Output', taskName: 'Interface Design', queueName: 'q_design_review', queueSize: 5, queueInitialQuantity: 1, policy: 'FIFO', generateActivity: false, isEditing: false },
  { id: 'res3', workProduct: 'Login Module', inputOutput: 'Output', taskName: 'Backend API Development', queueName: 'q_dev_backend', queueSize: 20, queueInitialQuantity: 5, policy: 'LIFO', generateActivity: true, isEditing: false },
  { id: 'res4', workProduct: 'Bug Report', inputOutput: 'Input', taskName: 'Acceptance Testing', queueName: 'q_testing_bugs', queueSize: 15, queueInitialQuantity: 0, policy: 'Priority', generateActivity: true, isEditing: false },
];

const WorkProductsTableTab = ({ processId }) => {
  const { toast } = useToast();
  const [workProducts, setWorkProducts] = useState(() => {
    const savedWorkProducts = localStorage.getItem(`workProductTable_${processId}`);
    return savedWorkProducts ? JSON.parse(savedWorkProducts) : initialWorkProducts;
  });
  const [newWorkProduct, setNewWorkProduct] = useState({ workProduct: '', inputOutput: 'Input', taskName: '', queueName: '', queueSize: 10, queueInitialQuantity: 0, policy: 'FIFO', generateActivity: true });
  const [isAdding, setIsAdding] = useState(false);

  useEffect(() => {
    localStorage.setItem(`workProductTable_${processId}`, JSON.stringify(workProducts));
  }, [workProducts, processId]);

  const handleInputChange = (e, id) => {
    const { name, value, type, checked } = e.target;
    const val = type === 'checkbox' ? checked : (name === 'queueSize' || name === 'queueInitialQuantity' ? parseInt(value, 10) : value);
    if (id) {
      setWorkProducts(workProducts.map(r => r.id === id ? { ...r, [name]: val } : r));
    } else {
      setNewWorkProduct({ ...newWorkProduct, [name]: val });
    }
  };

  const handleSelectChange = (value, name, id) => {
    if (id) {
      setWorkProducts(workProducts.map(r => r.id === id ? { ...r, [name]: value } : r));
    } else {
      setNewWorkProduct({ ...newWorkProduct, [name]: value });
    }
  };

  const toggleEdit = (id) => {
    setWorkProducts(workProducts.map(r => r.id === id ? { ...r, isEditing: !r.isEditing } : r));
  };

  const saveWorkProduct = (id) => {
    const workProductToSave = workProducts.find(r => r.id === id);
    if (!workProductToSave.workProduct || !workProductToSave.taskName || !workProductToSave.queueName) {
      toast({ title: "Error", description: "Work Product, Task Name, and Queue Name are required.", variant: "destructive" });
      return;
    }
    toggleEdit(id);
    toast({ title: "Work Product Saved", description: `Work Product "${workProductToSave.workProduct}" saved.`, variant: "default" });
  };

  const addWorkProduct = () => {
    if (!newWorkProduct.workProduct || !newWorkProduct.taskName || !newWorkProduct.queueName) {
      toast({ title: "Error", description: "Work Product, Task Name, and Queue Name are required.", variant: "destructive" });
      return;
    }
    const newId = `res${workProducts.length + 1}_${Date.now()}`;
    setWorkProducts([...workProducts, { ...newWorkProduct, id: newId, isEditing: false }]);
    setNewWorkProduct({ workProduct: '', inputOutput: 'Input', taskName: '', queueName: '', queueSize: 10, queueInitialQuantity: 0, policy: 'FIFO', generateActivity: true });
    setIsAdding(false);
    toast({ title: "Work Product Added", description: `New workProduct "${newWorkProduct.workProduct}" added.`, variant: "default" });
  };

  const removeWorkProduct = (id) => {
    const workProductToRemove = workProducts.find(r => r.id === id);
    setWorkProducts(workProducts.filter(r => r.id !== id));
    toast({ title: "Work Product Removed", description: `Work Product "${workProductToRemove?.workProduct}" removed.`, variant: "default" });
  };

  const renderInputField = (workProduct, fieldName, placeholder, type = "text") => (
      <Input
          name={fieldName}
          type={type}
          value={workProduct[fieldName]}
          onChange={(e) => handleInputChange(e, workProduct.id)}
          placeholder={placeholder}
          className="bg-slate-700 border-slate-600 text-slate-50 placeholder:text-slate-500"
          min={type === "number" ? "0" : undefined}
      />
  );

  const renderSelectField = (workProduct, fieldName, options) => (
      <Select name={fieldName} value={workProduct[fieldName]} onValueChange={(value) => handleSelectChange(value, fieldName, workProduct.id)}>
        <SelectTrigger className="bg-slate-700 border-slate-600 text-slate-50">
          <SelectValue placeholder={`Select ${fieldName === 'inputOutput' ? 'Input/Output' : 'Policy'}`} />
        </SelectTrigger>
        <SelectContent className="bg-slate-800 border-slate-600 text-slate-50">
          {options.map(opt => <SelectItem key={opt.value} value={opt.value} className="hover:bg-slate-700">{opt.label}</SelectItem>)}
        </SelectContent>
      </Select>
  );

  const renderCheckboxField = (workProduct, fieldName) => (
      <div className="flex items-center justify-center h-full">
        <Checkbox
            name={fieldName}
            checked={workProduct[fieldName]}
            onCheckedChange={(checked) => handleInputChange({ target: { name: fieldName, checked, type: 'checkbox' } }, workProduct.id)}
            className="data-[state=checked]:bg-sky-500 data-[state=checked]:text-white border-slate-600"
        />
      </div>
  );


  return (
      <Card className="bg-slate-800 border-slate-700 text-slate-50">
        <CardHeader>
          <CardTitle className="text-2xl text-sky-400">Work Products and Queues Table</CardTitle>
          <CardDescription className="text-slate-400">
            Configure work products, their associated tasks, queues, and policies for the simulation.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="mb-6">
            {!isAdding && (
                <Button onClick={() => setIsAdding(true)} className="bg-sky-500 hover:bg-sky-600 text-white">
                  <PlusCircle className="mr-2 h-5 w-5" /> Add New Work Product
                </Button>
            )}
            {isAdding && (
                <div className="p-4 border border-slate-700 rounded-lg bg-slate-700/50 space-y-4">
                  <h3 className="text-lg font-semibold text-sky-300">New Work Product</h3>
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 items-end">
                    <div>
                      <Label htmlFor="new-workProduct" className="text-slate-300">Work Product</Label>
                      {renderInputField(newWorkProduct, 'workProduct', 'e.g., Document X', 'text')}
                    </div>
                    <div>
                      <Label htmlFor="new-inputOutput" className="text-slate-300">Input/Output</Label>
                      {renderSelectField(newWorkProduct, 'inputOutput', [{value: 'Input', label: 'Input'}, {value: 'Output', label: 'Output'}])}
                    </div>
                    <div>
                      <Label htmlFor="new-taskName" className="text-slate-300">Task Name</Label>
                      {renderInputField(newWorkProduct, 'taskName', 'e.g., Review Document', 'text')}
                    </div>
                    <div>
                      <Label htmlFor="new-queueName" className="text-slate-300">Queue Name</Label>
                      {renderInputField(newWorkProduct, 'queueName', 'e.g., q_review', 'text')}
                    </div>
                    <div>
                      <Label htmlFor="new-queueSize" className="text-slate-300">Queue Size</Label>
                      {renderInputField(newWorkProduct, 'queueSize', '10', 'number')}
                    </div>
                    <div>
                      <Label htmlFor="new-queueInitialQuantity" className="text-slate-300">Initial Queue Quantity</Label>
                      {renderInputField(newWorkProduct, 'queueInitialQuantity', '0', 'number')}
                    </div>
                    <div>
                      <Label htmlFor="new-policy" className="text-slate-300">Policy</Label>
                      {renderSelectField(newWorkProduct, 'policy', [{value: 'FIFO', label: 'FIFO'}, {value: 'LIFO', label: 'LIFO'}, {value: 'Priority', label: 'Priority'}])}
                    </div>
                    <div className="flex flex-col items-start">
                      <Label htmlFor="new-generateActivity" className="text-slate-300 mb-1.5">Generate Activity?</Label>
                      {renderCheckboxField(newWorkProduct, 'generateActivity')}
                    </div>
                  </div>
                  <div className="flex justify-end gap-2 mt-2">
                    <Button variant="outline" onClick={() => setIsAdding(false)} className="text-slate-300 border-slate-600 hover:bg-slate-700">
                      <XCircle className="mr-2 h-4 w-4" /> Cancel
                    </Button>
                    <Button onClick={addWorkProduct} className="bg-green-500 hover:bg-green-600 text-white">
                      <Save className="mr-2 h-4 w-4" /> Save Work Product
                    </Button>
                  </div>
                </div>
            )}
          </div>

          <div className="overflow-x-auto">
            <Table className="min-w-full">
              <TableHeader>
                <TableRow className="border-slate-700 hover:bg-slate-700/30">
                  <TableHead className="text-sky-300">Work Product</TableHead>
                  <TableHead className="text-sky-300">Input/Output</TableHead>
                  <TableHead className="text-sky-300">Task Name</TableHead>
                  <TableHead className="text-sky-300">Queue Name</TableHead>
                  <TableHead className="text-sky-300 text-right">Queue Size</TableHead>
                  <TableHead className="text-sky-300 text-right">Initial Quantity</TableHead>
                  <TableHead className="text-sky-300">Policy</TableHead>
                  <TableHead className="text-sky-300 text-center">Generate Activity?</TableHead>
                  <TableHead className="text-sky-300 text-center">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {workProducts.map((workProduct) => (
                    <TableRow key={workProduct.id} className="border-slate-700 hover:bg-slate-700/30">
                      <TableCell>{workProduct.isEditing ? renderInputField(workProduct, 'workProduct', 'Work Product') : workProduct.workProduct}</TableCell>
                      <TableCell>{workProduct.isEditing ? renderSelectField(workProduct, 'inputOutput', [{value: 'Input', label: 'Input'}, {value: 'Output', label: 'Output'}]) : workProduct.inputOutput}</TableCell>
                      <TableCell>{workProduct.isEditing ? renderInputField(workProduct, 'taskName', 'Task Name') : workProduct.taskName}</TableCell>
                      <TableCell>{workProduct.isEditing ? renderInputField(workProduct, 'queueName', 'Queue Name') : workProduct.queueName}</TableCell>
                      <TableCell className="text-right">{workProduct.isEditing ? renderInputField(workProduct, 'queueSize', '10', 'number') : workProduct.queueSize}</TableCell>
                      <TableCell className="text-right">{workProduct.isEditing ? renderInputField(workProduct, 'queueInitialQuantity', '0', 'number') : workProduct.queueInitialQuantity}</TableCell>
                      <TableCell>{workProduct.isEditing ? renderSelectField(workProduct, 'policy', [{value: 'FIFO', label: 'FIFO'}, {value: 'LIFO', label: 'LIFO'}, {value: 'Priority', label: 'Priority'}]) : workProduct.policy}</TableCell>
                      <TableCell className="text-center">{workProduct.isEditing ? renderCheckboxField(workProduct, 'generateActivity') : (workProduct.generateActivity ? 'Yes' : 'No')}</TableCell>
                      <TableCell className="text-center">
                        <div className="flex justify-center gap-2">
                          {workProduct.isEditing ? (
                              <Button size="sm" onClick={() => saveWorkProduct(workProduct.id)} className="bg-green-500 hover:bg-green-600 text-white">
                                <Save className="h-4 w-4" />
                              </Button>
                          ) : (
                              <Button size="sm" variant="outline" onClick={() => toggleEdit(workProduct.id)} className="text-sky-400 border-sky-400 hover:bg-sky-400 hover:text-slate-900">
                                <Edit3 className="h-4 w-4" />
                              </Button>
                          )}
                          <Button size="sm" variant="outline" onClick={() => removeWorkProduct(workProduct.id)} className="text-red-400 border-red-400 hover:bg-red-400 hover:text-white">
                            <Trash2 className="h-4 w-4" />
                          </Button>
                        </div>
                      </TableCell>
                    </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
          {workProducts.length === 0 && !isAdding && (
              <p className="text-center text-slate-500 mt-4">No workProducts defined. Click "Add New Work Product" to get started.</p>
          )}
        </CardContent>
      </Card>
  );
};

export default WorkProductsTableTab;