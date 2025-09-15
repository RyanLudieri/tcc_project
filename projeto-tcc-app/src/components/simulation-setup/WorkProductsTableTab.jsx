import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Checkbox } from '@/components/ui/checkbox';
import { PlusCircle, Trash2, Edit3, Save, XCircle, X } from 'lucide-react';
import { useToast } from "@/components/ui/use-toast";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';

const initialWorkProducts = [
  { id: 'res1', workProduct: 'Requirements Document', inputOutput: 'Input', taskName: 'Requirements Analysis', queueName: 'q_analysis', queueSize: 10, queueInitialQuantity: 2, policy: 'FIFO', generateActivity: true, isEditing: false },
  { id: 'res2', workProduct: 'UI Prototype', inputOutput: 'Output', taskName: 'Interface Design', queueName: 'q_design_review', queueSize: 5, queueInitialQuantity: 1, policy: 'FIFO', generateActivity: false, isEditing: false },
  { id: 'res3', workProduct: 'Login Module', inputOutput: 'Output', taskName: 'Backend API Development', queueName: 'q_dev_backend', queueSize: 20, queueInitialQuantity: 5, policy: 'LIFO', generateActivity: true, isEditing: false },
  { id: 'res4', workProduct: 'Bug Report', inputOutput: 'Input', taskName: 'Acceptance Testing', queueName: 'q_testing_bugs', queueSize: 15, queueInitialQuantity: 0, policy: 'Priority', generateActivity: true, isEditing: false },
];

const observerTypes = ['NONE', 'LENGTH', 'TIME'];

const WorkProductsTableTab = ({ processId }) => {
  const { toast } = useToast();
  const [workProducts, setWorkProducts] = useState(() => {
    const saved = localStorage.getItem(`workProductTable_${processId}`);
    return saved ? JSON.parse(saved) : initialWorkProducts;
  });
  const [newWorkProduct, setNewWorkProduct] = useState({ workProduct: '', inputOutput: 'Input', taskName: '', queueName: '', queueSize: 10, queueInitialQuantity: 0, policy: 'FIFO', generateActivity: true });
  const [isAdding, setIsAdding] = useState(false);

  const [observers, setObservers] = useState([]);
  const [isAddingObserver, setIsAddingObserver] = useState(false);
  const [selectedQueue, setSelectedQueue] = useState("");
  const [selectedType, setSelectedType] = useState("NONE");
  const [mappings, setMappings] = useState([
    { id: 1, name: 'Role A' },
    { id: 2, name: 'Role B' },
    { id: 3, name: 'Role C' },
  ]);

  useEffect(() => {
    const fetchWorkProducts = async () => {
      try {
        const response = await fetch(`http://localhost:8080/work-product-configs/process/${processId}`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();

        // map para adequar os nomes dos campos ao state
        const formatted = data.map((wp) => ({
          id: wp.id,
          workProduct: wp.workProductName,
          inputOutput: wp.input_output,
          taskName: wp.task_name,
          queueName: wp.queue_name,
          queueSize: wp.queue_size,
          queueInitialQuantity: wp.initial_quantity,
          policy: wp.policy,
          generateActivity: wp.generate_activity,
          observers: wp.observers || [],
          isEditing: false
        }));

        setWorkProducts(formatted);
      } catch (error) {
        console.error("Error ao buscar work products:", error);
        setWorkProducts([]); // fallback vazio
      }
    };

    fetchWorkProducts();
  }, [processId]);



  // --- Work Products Functions ---
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
    const wp = workProducts.find(r => r.id === id);
    if (!wp.workProduct || !wp.taskName || !wp.queueName) {
      toast({ title: "Error", description: "Work Product, Task Name, and Queue Name are required.", variant: "destructive" });
      return;
    }
    toggleEdit(id);
    toast({ title: "Work Product Saved", description: `Work Product "${wp.workProduct}" saved.`, variant: "default" });
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
    const wp = workProducts.find(r => r.id === id);
    setWorkProducts(workProducts.filter(r => r.id !== id));
    toast({ title: "Work Product Removed", description: `Work Product "${wp?.workProduct}" removed.`, variant: "default" });
  };

  const renderInputField = (wp, fieldName, placeholder, type = "text") => (
      <Input
          name={fieldName}
          type={type}
          value={wp[fieldName]}
          onChange={(e) => handleInputChange(e, wp.id)}
          placeholder={placeholder}
          className="bg-card border-border text-foreground placeholder:text-muted-foreground"
          min={type === "number" ? "0" : undefined}
      />
  );

  const renderSelectField = (wp, fieldName, options) => (
      <Select name={fieldName} value={wp[fieldName]} onValueChange={(value) => handleSelectChange(value, fieldName, wp.id)}>
        <SelectTrigger className="bg-card border-border text-foreground">
          <SelectValue placeholder={`Select ${fieldName === 'inputOutput' ? 'Input/Output' : 'Policy'}`} />
        </SelectTrigger>
        <SelectContent className="bg-card border-border text-foreground">
          {options.map(opt => <SelectItem key={opt.value} value={opt.value} className="hover:bg-slate-700">{opt.label}</SelectItem>)}
        </SelectContent>
      </Select>
  );

  const renderCheckboxField = (wp, fieldName) => (
      <div className="flex items-center justify-center h-full">
        <Checkbox
            name={fieldName}
            checked={wp[fieldName]}
            onCheckedChange={(checked) => handleInputChange({ target: { name: fieldName, checked, type: 'checkbox' } }, wp.id)}
            className="border-border data-[state=checked]:bg-primary data-[state=checked]:text-primary-foreground"
        />
      </div>
  );

  // --- Observer Functions ---
  const showAddObserverForm = () => {
    setIsAddingObserver(true);
    setSelectedQueue("");
    setSelectedType("NONE");
  };

  const cancelAddObserver = () => {
    setIsAddingObserver(false);
    setSelectedQueue("");
    setSelectedType("NONE");
  };

  const getNextObserverIndex = () => {
    if (observers.length === 0) return 1;
    const existingIndices = observers.map(obs => {
      const match = obs.name.match(/observer (\d+)$/);
      return match ? parseInt(match[1], 10) : 0;
    });
    return Math.max(...existingIndices) + 1;
  };

  const handleAddObserver = () => {
    if (!selectedQueue) {
      toast({ title: "Error", description: "Please select a role first.", variant: "destructive" });
      return;
    }
    const nextIndex = getNextObserverIndex();
    const name = `${selectedQueue} queue observer ${nextIndex}`;
    const obs = { id: `obs-${Date.now()}`, name, type: selectedType, isEditing: false };
    setObservers([...observers, obs]);
    cancelAddObserver();
    toast({ title: "Observer Added", description: `Observer "${name}" has been added.`, variant: "default" });
  };

  const toggleObserverEdit = (id) => {
    setObservers(observers.map(o => o.id === id ? { ...o, isEditing: !o.isEditing } : o));
  };

  const handleObserverTypeChange = (value, id) => {
    setObservers(observers.map(o => o.id === id ? { ...o, type: value } : o));
  };

  const saveObserver = (id) => {
    const obs = observers.find(o => o.id === id);
    toggleObserverEdit(id);
    toast({ title: "Observer Updated", description: `Observer "${obs.name}" type updated to "${obs.type}".`, variant: "default" });
  };

  const cancelObserverEdit = (id) => toggleObserverEdit(id);

  const removeObserver = (id) => {
    const obs = observers.find(o => o.id === id);
    setObservers(observers.filter(o => o.id !== id));
    toast({ title: "Observer Removed", description: `Observer "${obs?.name}" removed.`, variant: "default" });
  };

  return (
      <>
        {/* ====== CARD Work Products Table ======*/}
        <Card className="bg-card border-border text-foreground">
          <CardHeader>
            <CardTitle className="text-2xl text-primary">Work Products and Queues Table</CardTitle>
            <CardDescription className="text-muted-foreground">
              Configure work products, their associated tasks, queues, and policies for the simulation.
            </CardDescription>
          </CardHeader>
          <CardContent>
            {/* Add Work Product Form */}
            {isAdding && (
                <div className="mb-6 p-4 border border-slate-700 rounded-lg bg-slate-700/50 space-y-4">
                  <h3 className="text-lg font-semibold text-sky-300">New Work Product</h3>
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 items-end">
                    <div>
                      <Label className="text-slate-300">Work Product</Label>
                      {renderInputField(newWorkProduct, 'workProduct', 'e.g., Document X')}
                    </div>
                    <div>
                      <Label className="text-slate-300">Input/Output</Label>
                      {renderSelectField(newWorkProduct, 'inputOutput', [{ value: 'Input', label: 'Input' }, { value: 'Output', label: 'Output' }])}
                    </div>
                    <div>
                      <Label className="text-slate-300">Task Name</Label>
                      {renderInputField(newWorkProduct, 'taskName', 'Task Name')}
                    </div>
                    <div>
                      <Label className="text-slate-300">Queue Name</Label>
                      {renderInputField(newWorkProduct, 'queueName', 'Queue Name')}
                    </div>
                    <div>
                      <Label className="text-slate-300">Queue Size</Label>
                      {renderInputField(newWorkProduct, 'queueSize', '10', 'number')}
                    </div>
                    <div>
                      <Label className="text-slate-300">Initial Quantity</Label>
                      {renderInputField(newWorkProduct, 'queueInitialQuantity', '0', 'number')}
                    </div>
                    <div>
                      <Label className="text-slate-300">Policy</Label>
                      {renderSelectField(newWorkProduct, 'policy', [{ value: 'FIFO', label: 'FIFO' }, { value: 'LIFO', label: 'LIFO' }, { value: 'Priority', label: 'Priority' }])}
                    </div>
                    <div className="flex flex-col items-start">
                      <Label className="text-slate-300 mb-1.5">Generate Activity?</Label>
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

            {/* =============== Work Products Table ===================*/}
            <div className="overflow-x-auto max-h-[400px] border border-border rounded-lg">
              <Table className="min-w-[800px] w-full table-fixed">
                <TableHeader className="sticky top-0 bg-muted z-10">
                  <TableRow className="border-border h-12">
                    <TableHead className="text-primary">Work Product</TableHead>
                    <TableHead className="text-primary">Input/Output</TableHead>
                    <TableHead className="text-primary">Task Name</TableHead>
                    <TableHead className="text-primary">Queue Name</TableHead>
                    <TableHead className="text-primary text-right">Queue Size</TableHead>
                    <TableHead className="text-primary text-right">Initial Quantity</TableHead>
                    <TableHead className="text-primary">Policy</TableHead>
                    <TableHead className="text-primary text-center">Generate Activity?</TableHead>
                    <TableHead className="text-primary text-center">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {workProducts.map((wp) => (
                      <TableRow key={wp.id} className="border-border hover:bg-muted h-12">
                        <TableCell className="min-w-[120px]">{wp.isEditing ? renderInputField(wp, 'workProduct', 'Work Product') : wp.workProduct}</TableCell>
                        <TableCell className="min-w-[80px]">{wp.isEditing ? renderSelectField(wp, 'inputOutput', [{ value: 'Input', label: 'Input' }, { value: 'Output', label: 'Output' }]) : wp.inputOutput}</TableCell>
                        <TableCell className="min-w-[120px]">{wp.isEditing ? renderInputField(wp, 'taskName', 'Task Name') : wp.taskName}</TableCell>
                        <TableCell className="min-w-[120px]">{wp.isEditing ? renderInputField(wp, 'queueName', 'Queue Name') : wp.queueName}</TableCell>
                        <TableCell className="text-right min-w-[80px]">{wp.isEditing ? renderInputField(wp, 'queueSize', '10', 'number') : wp.queueSize}</TableCell>
                        <TableCell className="text-right min-w-[100px]">{wp.isEditing ? renderInputField(wp, 'queueInitialQuantity', '0', 'number') : wp.queueInitialQuantity}</TableCell>
                        <TableCell className="min-w-[80px]">{wp.isEditing ? renderSelectField(wp, 'policy', [{ value: 'FIFO', label: 'FIFO' }, { value: 'LIFO', label: 'LIFO' }, { value: 'Priority', label: 'Priority' }]) : wp.policy}</TableCell>
                        <TableCell className="text-center min-w-[120px]">{wp.isEditing ? renderCheckboxField(wp, 'generateActivity') : (wp.generateActivity ? 'Yes' : 'No')}</TableCell>
                        <TableCell className="text-center min-w-[120px]">
                          <div className="flex justify-center gap-2">
                            {wp.isEditing ? (
                                <Button size="sm" onClick={() => saveWorkProduct(wp.id)} className="bg-green-500 hover:bg-green-600 text-white">
                                  <Save className="h-4 w-4" />
                                </Button>
                            ) : (
                                <Button size="sm" variant="outline" onClick={() => toggleEdit(wp.id)} className="text-sky-400 border-sky-400 hover:bg-sky-400 hover:text-slate-900">
                                  <Edit3 className="h-4 w-4" />
                                </Button>
                            )}
                            <Button size="sm" variant="outline" onClick={() => removeWorkProduct(wp.id)} className="text-red-400 border-red-400 hover:bg-red-400 hover:text-white">
                              <Trash2 className="h-4 w-4" />
                            </Button>
                          </div>
                        </TableCell>
                      </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>



          </CardContent>
        </Card>

        {/* ======= CARD Observers ========== */}
        <Card className="bg-card border-border text-foreground">
          <CardHeader>
            <CardTitle className="text-2xl text-primary">Configure Observers</CardTitle>
            <CardDescription className="text-muted-foreground">Manage global observers for queues in this process.</CardDescription>
          </CardHeader>
          <CardContent>
            <Button onClick={showAddObserverForm} className="bg-sky-500 hover:bg-sky-600 text-white mb-4">
              <PlusCircle className="mr-2 h-5 w-5" /> Add Observer
            </Button>

            {isAddingObserver && (
                <div className="mb-4 p-4 border border-border rounded-lg bg-muted">
                  <h3 className="text-lg font-semibold text-primary mb-3">Add New Observer</h3>
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div>
                      <Label className="text-foreground mb-2">Select Role</Label>
                      <Select value={selectedQueue} onValueChange={setSelectedQueue}>
                        <SelectTrigger className="bg-card border-border text-foreground">
                          <SelectValue placeholder="Choose a role" />
                        </SelectTrigger>
                        <SelectContent className="bg-card border-border text-foreground">
                          {mappings.map((m) => (
                              <SelectItem key={m.id} value={m.name} className="hover:bg-muted">{m.name}</SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>
                    <div>
                      <Label className="text-foreground mb-2">Observer Type</Label>
                      <Select value={selectedType} onValueChange={setSelectedType}>
                        <SelectTrigger className="bg-card border-border text-foreground">
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent className="bg-card border-border text-foreground">
                          {observerTypes.map((t) => (
                              <SelectItem key={t} value={t} className="hover:bg-muted">{t}</SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>
                    <div className="flex items-end gap-2">
                      <Button onClick={handleAddObserver} className="bg-green-600 hover:bg-green-700 text-white">
                        <Save className="h-4 w-4 mr-1" /> Add
                      </Button>
                      <Button onClick={cancelAddObserver} variant="outline" className="text-foreground border-border hover:bg-muted">
                        <X className="h-4 w-4 mr-1" /> Cancel
                      </Button>
                    </div>
                  </div>
                </div>
            )}

            <div className="overflow-x-auto max-h-[400px] border border-border rounded-lg">
            <Table className="min-w-[500px] w-full table-fixed">
              <TableHeader className="sticky top-0 bg-muted z-10">
                <TableRow className="border-border h-12">
                    <TableHead className="text-primary min-w-[150px]">Name</TableHead>
                    <TableHead className="text-primary min-w-[120px]">Type</TableHead>
                    <TableHead className="text-primary text-center min-w-[120px]">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {observers.map((obs) => (
                      <TableRow key={wp.id} className="border-border hover:bg-muted h-12">
                        <TableCell className="min-w-[150px]">{obs.name}</TableCell>
                        <TableCell className="min-w-[120px]">
                          {obs.isEditing ? (
                              <Select value={obs.type} onValueChange={(val) => handleObserverTypeChange(val, obs.id)}>
                                <SelectTrigger className="bg-card border-border text-foreground">
                                  <SelectValue />
                                </SelectTrigger>
                                <SelectContent className="bg-card border-border text-foreground">
                                  {observerTypes.map((t) => (
                                      <SelectItem key={t} value={t} className="text-slate-100 hover:bg-slate-600">{t}</SelectItem>
                                  ))}
                                </SelectContent>
                              </Select>
                          ) : obs.type}
                        </TableCell>
                        <TableCell className="text-center min-w-[120px]">
                          <div className="flex justify-center gap-2">
                            {obs.isEditing ? (
                                <>
                                  <Button size="sm" variant="outline" onClick={() => saveObserver(obs.id)} className="text-green-400 border-green-400 hover:bg-green-400 hover:text-white">
                                    <Save className="h-4 w-4" />
                                  </Button>
                                  <Button size="sm" variant="outline" onClick={() => cancelObserverEdit(obs.id)} className="text-slate-400 border-slate-400 hover:bg-slate-400 hover:text-white">
                                    <X className="h-4 w-4" />
                                  </Button>
                                </>
                            ) : (
                                <>
                                  <Button size="sm" variant="outline" onClick={() => toggleObserverEdit(obs.id)} className="text-sky-400 border-sky-400 hover:bg-sky-400 hover:text-slate-900">
                                    <Edit3 className="h-4 w-4" />
                                  </Button>
                                  <Button size="sm" variant="outline" onClick={() => removeObserver(obs.id)} className="text-red-400 border-red-400 hover:bg-red-400 hover:text-white">
                                    <Trash2 className="h-4 w-4" />
                                  </Button>
                                </>
                            )}
                          </div>
                        </TableCell>
                      </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>

            {observers.length === 0 && <p className="text-center text-muted-foreground mt-4">No observers configured.</p>}
          </CardContent>
        </Card>
      </>
  );
};

export default WorkProductsTableTab;
