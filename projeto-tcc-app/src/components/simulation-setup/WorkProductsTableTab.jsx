import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Checkbox } from '@/components/ui/checkbox';
import {PlusCircle, Trash2, Edit3, Save, XCircle, X, Activity} from 'lucide-react';
import { useToast } from "@/components/ui/use-toast";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { API_BASE_URL } from "@/config/api";
import DistributionField from "@/components/simulation-setup/work-breakdown-elements/DistributionField.jsx";

const FieldSection = ({ title, children }) => {
  return (
      <div className="space-y-4 border border-gray-300 shadow-lg rounded-lg p-4 mb-4">
        {title && <h3 className="text-sm font-semibold">{title}</h3>}
        {children}
      </div>
  );
};

const observerTypes = ['NONE', 'LENGTH', 'TIME'];
const observerTypesGenerateActivity = ['NONE', 'ACTIVE', 'DELAY', 'PROCESSOR'];

const WorkProductsTableTab = ({ processId }) => {
  const [clickedWorkProduct, setClickedWorkProduct] = useState(null);
  const { toast } = useToast();
  const [workProducts, setWorkProducts] = useState([]);
  const [newWorkProduct, setNewWorkProduct] = useState({ workProduct: '', inputOutput: 'Input', taskName: '', queueName: '', queueSize: 10, queueInitialQuantity: 0, policy: 'FIFO', generateActivity: true });
  const [isAdding, setIsAdding] = useState(false);
  const [observers, setObservers] = useState([]);
  const [selectedWorkProduct, setSelectedWorkProduct] = useState("");
  const [selectedGenerateActivity, setSelectedGenerateActivity] = useState("");
  const [isAddingObserver, setIsAddingObserver] = useState(false);
  const [isAddingObserverGenerateActivity, setIsAddingObserverGenerateActivity] = useState(false);
  const [selectedQueue, setSelectedQueue] = useState("");
  const [selectedType, setSelectedType] = useState("NONE");
  const [selectedTypeGenerateActivity, setSelectedTypeGenerateActivity] = useState("ACTIVE");
  const [distribution, setDistribution] = useState({ type: 'CONSTANT', params: {} });
  const [selectedObserverGenerateActivity, setSelectedObserverGenerateActivity] = useState("");


  const selectedWorkProductObj = workProducts.find(wp => wp.taskName === clickedWorkProduct);
  const selectedWorkProductGenerateActivity = selectedWorkProductObj ? selectedWorkProductObj.generateActivity : false;

  useEffect(() => {
    const fetchWorkProducts = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/work-product-configs/process/${processId}`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const data = await response.json();

        const workProducts = data.map((wp) => ({
          id: wp.id,
          workProduct: wp.workProductName,
          inputOutput: wp.input_output,
          taskName: wp.task_name,
          queueName: wp.queue_name,
          queueType: wp.queue_type,
          queueSize: wp.queue_size,
          queueInitialQuantity: wp.initial_quantity,
          policy: wp.policy,
          generateActivity: wp.generate_activity,
          observers: wp.observers || [],
          isEditing: false
        }));
        setWorkProducts(workProducts);

        const mappedObservers = data.flatMap((wp) =>
            (wp.observers || []).map((obs) => ({
              id: obs.id,
              name: obs.name,
              queueName: obs.queue_name,
              type: obs.type,
              position: obs.position,
              isEditing: false,
            }))
        );
        setObservers(mappedObservers);


      } catch (error) {
        console.error("Unable to load work products from process:", error);
        setWorkProducts([]);
      }
    };
    fetchWorkProducts();
  }, [processId]);

  /* ===== WORK PRODUCTS ===== */
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

  const saveWorkProduct = async (id) => {
    const wpToSave = workProducts.find(r => r.id === id);

    try {
      const response = await fetch(`${API_BASE_URL}/work-product-configs/${id}`, {
        method: "PATCH",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
          queue_name: wpToSave.queueName,
          queue_type: wpToSave.queueType || "QUEUE",
          initial_quantity: wpToSave.queueInitialQuantity,
          queue_size: wpToSave.queueSize,
          policy: wpToSave.policy,
          generate_activity: wpToSave.generateActivity,
        }),
      });

      if (!response.ok) throw new Error("Failed to update work product config");

      toggleEdit(id);
      toast({
        title: "Saved",
        description: `Work product config for "${wpToSave.queueName}" updated successfully.`,
        variant: "default",
      });
    } catch (error) {
      toast({
        title: "Error",
        description: "Unable to save work product config.",
        variant: "destructive",
      });
    }
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
          {options.map(opt => <SelectItem key={opt.value} value={opt.value} className="hover:bg-muted">{opt.label}</SelectItem>)}
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


  /* ===== OBSERVERS ===== */
  const showAddObserverForm = (isGenerateActivity) => {
    if(isGenerateActivity) {
      setIsAddingObserverGenerateActivity(true);
      setSelectedGenerateActivity(selectedWorkProduct.get().queueName);
      setSelectedTypeGenerateActivity("ACTIVE");

    } else {
      setIsAddingObserver(true);
      setSelectedWorkProduct("");
      setSelectedType("NONE");
    }
  };

  const cancelAddObserver = (isGenerateActivity) => {
    if(isGenerateActivity){
      setIsAddingObserverGenerateActivity(false);
      setSelectedGenerateActivity("");
      setSelectedTypeGenerateActivity("FALSE");
      return;
    }
    setIsAddingObserver(false);
    setSelectedWorkProduct("");
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

  const handleAddObserver = async (isGenerateActivity) => {
    if(isGenerateActivity){
      return;
    }
    if (!selectedWorkProduct) {
      toast({ title: "Error", description: "Please select a work product first.", variant: "destructive" });
      return;
    }

    // Obter o `workProduct` completo usando o `selectedWorkProduct.id`
    const selectedWorkProductData = workProducts.find(wp => wp.id === selectedWorkProduct);
    if (!selectedWorkProductData) return;

    const nextIndex = getNextObserverIndex() + 1;
    const observerName = `${selectedWorkProductData.queueName} observer ${nextIndex}`;
    const query = selectedType && selectedType !== "NONE" ? `?type=${selectedType}` : "";

    try {
      const response = await fetch(
          `${API_BASE_URL}/work-product-configs/${selectedWorkProductData.id}/observers${query}`,
          { method: "POST" }
      );
      if (!response.ok) throw new Error("Failed to add observer");

      const savedObserver = await response.json();

      setObservers([...observers, {
        id: savedObserver.id,
        workProductConfigId: selectedWorkProductData.id,  // Enviar o workProduct.id ao backend
        name: savedObserver.name,
        queueName: savedObserver.queue_name,
        type: savedObserver.type,
        position: savedObserver.position,
        isEditing: false,
      }]);

      setIsAddingObserver(false);
      toast({
        title: "Observer Added",
        description: `Observer "${observerName}" has been added.`,
        variant: "default",
      });
    } catch (error) {
      toast({ title: "Error", description: "Unable to save observer.", variant: "destructive" });
    }
  };

  const toggleObserverEdit = (id) => {
    setObservers(observers.map(o => o.id === id ? { ...o, isEditing: !o.isEditing } : o));
  };

  const handleObserverTypeChange = (value, id) => {
    setObservers(observers.map(o => o.id === id ? { ...o, type: value } : o));
  };

  const saveUpdateObserver = async (id) => {
    const observerToSave = observers.find(o => o.id === id);
    const body = { type: observerToSave.type, queueName: observerToSave.name };

    try {
      const response = await fetch(`${API_BASE_URL}/work-product-configs/observers/${id}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body),
      });
      if (!response.ok) throw new Error("Error updating observer");

      toggleObserverEdit(id);
      toast({ title: "Observer Updated", description: `Observer "${observerToSave.name}" updated.`, variant: "default" });
    } catch (error) {
      toast({ title: "Error", description: error.message, variant: "destructive" });
    }
  };

  const cancelObserverEdit = (id) => toggleObserverEdit(id);

  const handleRemoveObserver = async (id) => {
    const observerToRemove = observers.find(o => o.id === id);
    if (!observerToRemove) return;

    // setObservers(observers.filter(o => o.id !== id));

    const wp = workProducts.find(wp => wp.queueName === observerToRemove.queueName);
    if (!wp) {
      toast({ title: "Error", description: "Unable to determine work product for this observer.", variant: "destructive" });
      return;
    }

    try {
      const response = await fetch(
          `${API_BASE_URL}/work-product-configs/${wp.id}/observers/${id}`,
          {method: "DELETE"}
      );
      if (!response.ok) throw new Error("Failed to delete observer");

      setObservers(prev => prev.filter(o => o.id !== id));
      toast({
        title: "Observer Removed",
        description: `Observer "${observerToRemove.name}" has been removed.`,
        variant: "default"
      });
    } catch (error) {
      toast({title: "Error", description: "Unable to delete observer.", variant: "destructive"});
    }

  };

  return (
      <>
        <div className="space-y-6">

          {/* WORK PRODUCTS */}
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
                  <div className="mb-6 p-4 border border-border rounded-lg bg-muted space-y-4">
                    <h3 className="text-lg font-semibold text-primary">New Work Product</h3>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 items-end">
                      <div>
                        <Label className="text-foreground">Work Product</Label>
                        {renderInputField(newWorkProduct, 'workProduct', 'e.g., Document X')}
                      </div>
                      <div>
                        <Label className="text-foreground">Input/Output</Label>
                        {renderSelectField(newWorkProduct, 'inputOutput', [{ value: 'Input', label: 'Input' }, { value: 'Output', label: 'Output' }])}
                      </div>
                      <div>
                        <Label className="text-foreground">Task Name</Label>
                        {renderInputField(newWorkProduct, 'taskName', 'Task Name')}
                      </div>
                      <div>
                        <Label className="text-foreground">Queue Name</Label>
                        {renderInputField(newWorkProduct, 'queueName', 'Queue Name')}
                      </div>
                      <div>
                        <Label className="text-foreground">Queue Size</Label>
                        {renderInputField(newWorkProduct, 'queueSize', '10', 'number')}
                      </div>
                      <div>
                        <Label className="text-foreground">Initial Quantity</Label>
                        {renderInputField(newWorkProduct, 'queueInitialQuantity', '0', 'number')}
                      </div>
                      <div>
                        <Label className="text-foreground">Policy</Label>
                        {renderSelectField(newWorkProduct, 'policy', [{ value: 'FIFO', label: 'FIFO' }, { value: 'LIFO', label: 'LIFO' }, { value: 'Priority', label: 'Priority' }])}
                      </div>
                      <div className="flex flex-col items-start">
                        <Label className="text-foreground mb-1.5">Generate Activity?</Label>
                        {renderCheckboxField(newWorkProduct, 'generateActivity')}
                      </div>
                    </div>
                    <div className="flex justify-end gap-2 mt-2">
                      <Button variant="outline" onClick={() => setIsAdding(false)} className="text-foreground border-border hover:bg-muted">
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
                      <TableHead className="text-center text-primary w-1/10">Work Product</TableHead>
                      <TableHead className="text-center text-primary w-1/10">Input/Output</TableHead>
                      <TableHead className="text-center text-primary w-1/10">Task Name</TableHead>
                      <TableHead className="text-center text-primary w-1/10">Queue Name</TableHead>
                      <TableHead className="text-center text-primary w-1/10">Queue Type</TableHead>
                      <TableHead className="text-center text-primary w-1/10">Queue Size</TableHead>
                      <TableHead className="text-primary text-center w-1/10">Initial Quantity</TableHead>
                      <TableHead className="text-primary text-center w-1/10">Policy</TableHead>
                      <TableHead className="text-primary text-center w-1/10">Generate Activity?</TableHead>
                      <TableHead className="text-primary text-center w-1/10">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {workProducts.map((wp) => (
                        <TableRow key={wp.id}
                                  className={`border-border hover:bg-muted h-12 cursor-pointer ${
                                      clickedWorkProduct === wp.taskName ? "bg-blue-100" : ""
                                  }`}
                                  onClick={() => setClickedWorkProduct(wp.taskName)}>
                          <TableCell className="text-center min-w-1/10">{wp.workProduct}</TableCell>
                          <TableCell className="text-center min-w-1/10">{wp.inputOutput}</TableCell>
                          <TableCell className="text-center min-w-1/10">{wp.taskName}</TableCell>
                          <TableCell className="text-center min-w-1/10">{wp.queueName}</TableCell>
                          <TableCell className="text-center min-w-1/10">{wp.isEditing ? renderSelectField(wp, 'queueType', [{ value: 'QUEUE', label: 'QUEUE' }, { value: 'SET', label: 'SET' }, { value: 'STACK', label: 'STACK' }]) : wp.queueType}</TableCell>
                          <TableCell className="text-center min-w-1/10">{wp.isEditing ? renderInputField(wp, 'queueSize', '10', 'number') : wp.queueSize}</TableCell>
                          <TableCell className="text-center min-w-1/10">{wp.isEditing ? renderInputField(wp, 'queueInitialQuantity', '0', 'number') : wp.queueInitialQuantity}</TableCell>
                          <TableCell className="text-center min-w-1/10">{wp.isEditing ? renderSelectField(wp, 'policy', [{ value: 'FIFO', label: 'FIFO' }]) : wp.policy}</TableCell>
                          <TableCell className="text-center min-w-1/10">{wp.isEditing ? renderCheckboxField(wp, 'generateActivity') : (wp.generateActivity ? 'Yes' : 'No')}</TableCell>
                          <TableCell className="text-center min-w-1/10">
                            <div className="flex justify-center gap-2">
                              {wp.isEditing ? (
                                  <Button size="sm" onClick={() => saveWorkProduct(wp.id)} className="bg-green-500 hover:bg-green-600 text-white">
                                    <Save className="h-4 w-4" />
                                  </Button>
                              ) : (
                                  <Button size="sm" variant="outline" onClick={() => toggleEdit(wp.id)} className="text-primary border-primary hover:bg-primary hover:text-primary-foreground">
                                    <Edit3 className="h-4 w-4" />
                                  </Button>
                              )}
                            </div>
                          </TableCell>
                        </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
              {workProducts.length === 0 && <p className="text-center text-muted-foreground mt-4">No work products found for this process.</p>}
            </CardContent>
          </Card>

          <div className="flex items-start gap-4 mt-6 w-full">

            {/* OBSERVERS */}
            <Card className="bg-card border-border text-foreground flex-none w-1/2 h-[calc(100vh-200px)] flex flex-col">
              <CardHeader>
                <CardTitle className="text-2xl text-primary">Observers for Work Products queue's</CardTitle>
                <CardDescription className="text-muted-foreground">
                  Manage global observers for queues in this process.
                </CardDescription>
              </CardHeader>
              <CardContent>
                <Button onClick={() => showAddObserverForm(false)} className="bg-primary hover:bg-primary/90 text-primary-foreground mb-4">
                  <PlusCircle className="mr-2 h-5 w-5" /> Add Observer
                </Button>

                {isAddingObserver && (
                    <div className="mb-4 p-4 border border-border rounded-lg bg-muted">
                      <h3 className="text-lg font-semibold text-primary mb-3">Add New Observer</h3>
                      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <div>
                          <Label className="text-foreground mb-2">Select Queue Name</Label>
                          <Select value={selectedWorkProduct} onValueChange={setSelectedWorkProduct}>
                            <SelectTrigger className="bg-card border-border text-foreground">
                              <SelectValue placeholder="Choose a queue" />
                            </SelectTrigger>
                            <SelectContent className="bg-card border-border text-foreground">
                              {workProducts.map((wp) => (
                                  <SelectItem key={wp.id} value={wp.id} className="hover:bg-muted">
                                    {wp.queueName}
                                  </SelectItem>
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
                          <Button
                              onClick={() => handleAddObserver(false)}
                              className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2"
                          >
                            <Save className="h-4 w-4 shrink-0" />
                            <span>Add</span>
                          </Button>

                          <Button
                              onClick={() => cancelAddObserver(false)}
                              variant="outline"
                              className="flex items-center gap-2 text-foreground border-border hover:bg-muted px-4 py-2"
                          >
                            <X className="h-4 w-4 shrink-0" />
                            <span>Cancel</span>
                          </Button>
                        </div>

                      </div>
                    </div>
                )}

                <div className="overflow-x-auto max-h-[430px] border border-border rounded-lg">
                  <Table className="min-w-[500px] w-full table-fixed h-full">
                    <TableHeader className="sticky top-0 bg-muted z-10">
                      <TableRow className="border-border h-12">
                        <TableHead className="text-center text-primary min-w-[150px]">Name</TableHead>
                        <TableHead className="text-center text-primary min-w-[120px]">Type</TableHead>
                        <TableHead className="text-center text-primary text-center min-w-[120px]">Actions</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {observers.map((obs) => (
                          <TableRow key={obs.id} className="border-border hover:bg-muted h-12">
                            <TableCell className="text-center min-w-[150px]">{obs.name}</TableCell>
                            <TableCell className="text-center min-w-[120px]">
                              {obs.isEditing ? (
                                  <Select value={obs.type} onValueChange={(val) => handleObserverTypeChange(val, obs.id)}>
                                    <SelectTrigger className="bg-card border-border text-foreground">
                                      <SelectValue />
                                    </SelectTrigger>
                                    <SelectContent className="bg-card border-border text-foreground">
                                      {observerTypes.map((t) => (
                                          <SelectItem key={t} value={t} className="hover:bg-muted">{t}</SelectItem>
                                      ))}
                                    </SelectContent>
                                  </Select>
                              ) : obs.type}
                            </TableCell>
                            <TableCell className="text-center min-w-[120px]">
                              <div className="flex justify-center gap-2">
                                {obs.isEditing ? (
                                    <>
                                      <Button size="sm" variant="outline" onClick={() => saveUpdateObserver(obs.id)} className="text-green-600 border-green-600 hover:bg-green-600 hover:text-white">
                                        <Save className="h-4 w-4" />
                                      </Button>
                                      <Button size="sm" variant="outline" onClick={() => cancelObserverEdit(obs.id)} className="text-muted-foreground border-border hover:bg-muted">
                                        <X className="h-4 w-4" />
                                      </Button>
                                    </>
                                ) : (
                                    <>
                                      <Button size="sm" variant="outline" onClick={() => toggleObserverEdit(obs.id)} className="text-primary border-primary hover:bg-primary hover:text-primary-foreground">
                                        <Edit3 className="h-4 w-4" />
                                      </Button>
                                      <Button size="sm" variant="outline" onClick={() => handleRemoveObserver(obs.id)} className="text-destructive border-destructive hover:bg-destructive hover:text-destructive-foreground">
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

            <div className="space-y-3 w-full">

              {/* GENERATE ACTIVITY OBSERVERS */}
              <Card className="bg-card border-border text-foreground w-full">
                <CardHeader>
                  <CardTitle className="text-2xl text-primary">Observers for Generate Activity</CardTitle>
                  {selectedWorkProductGenerateActivity ? (
                  <CardDescription>Manage observers for generate activity:{" "}
                    <span className="font-bold text-blue-600">{clickedWorkProduct || "None"}</span>
                  </CardDescription>) : (
                      <></>
                  )}
                </CardHeader>
                {selectedWorkProductGenerateActivity ? (
                    <CardContent>
                      <Button onClick={() => showAddObserverForm(true)} className="bg-primary hover:bg-primary/90 text-primary-foreground mb-4">
                        <PlusCircle className="mr-2 h-5 w-5" /> Add Observer
                      </Button>
                      {isAddingObserverGenerateActivity && (
                          <div className="mb-4 p-4 border border-border rounded-lg bg-muted">
                            <h3 className="text-lg font-semibold text-primary mb-3">Add New Observer for {selectedWorkProductObj.taskName || ""}</h3>
                            <div className="grid grid-cols-1 md:grid-cols-3 gap-2 w-3/8">
                              <div>
                                <Label className="text-foreground mb-2">Queue Name</Label>
                                <Input
                                    value={selectedWorkProductObj.queueName || ""}
                                    disabled
                                    className="bg-card border-border text-foreground"
                                />
                              </div>
                              <div>
                                <Label className="text-foreground mb-2 w-9/12">Observer Type</Label>
                                <Select value={selectedTypeGenerateActivity} onValueChange={setSelectedTypeGenerateActivity}>
                                  <SelectTrigger className="bg-card border-border text-foreground">
                                    <SelectValue />
                                  </SelectTrigger>
                                  <SelectContent className="bg-card border-border text-foreground">
                                    {observerTypesGenerateActivity.map((t) => (
                                        <SelectItem key={t} value={t} className="hover:bg-muted">{t}</SelectItem>
                                    ))}
                                  </SelectContent>
                                </Select>
                              </div>
                              <div className="flex items-end gap-2">
                                <Button
                                    onClick={() => handleAddObserver(true)}
                                    className="flex items-center gap-2 bg-green-600 hover:bg-green-700 text-white px-4 py-2"
                                >
                                  <Save className="h-4 w-4 shrink-0" />
                                  <span>Add</span>
                                </Button>

                                <Button
                                    onClick={() => cancelAddObserver(true)}
                                    variant="outline"
                                    className="flex items-center gap-2 text-foreground border-border hover:bg-muted px-4 py-2"
                                >
                                  <X className="h-4 w-4 shrink-0" />
                                  <span>Cancel</span>
                                </Button>
                              </div>

                            </div>
                          </div>
                      )}

                      <div className="overflow-x-auto max-h-[400px] border border-border rounded-lg">
                        <Table className="min-w-[500px] w-full table-fixed">
                          <TableHeader className="sticky top-0 bg-muted z-10">
                            <TableRow className="border-border h-12">
                              <TableHead className="text-center text-primary min-w-[150px]">Name</TableHead>
                              <TableHead className="text-center text-primary min-w-[120px]">Type</TableHead>
                              <TableHead className="text-center text-primary text-center min-w-[120px]">Actions</TableHead>
                            </TableRow>
                          </TableHeader>
                          <TableBody>
                            {observers
                                .filter((obs) => obs.generateActivity)
                                .map((obs) => (
                                    <TableRow
                                        key={obs.id}
                                        className="border-border hover:bg-muted h-12"
                                        onClick={() => setSelectedObserverGenerateActivity(obs)}
                                    >
                                      <TableCell className="text-center min-w-[150px]">{obs.name}</TableCell>
                                      <TableCell className="text-center min-w-[120px]">
                                        {obs.isEditing ? (
                                            <Select value={obs.type} onValueChange={(val) => handleObserverTypeChange(val, obs.id)}>
                                              <SelectTrigger className="bg-card border-border text-foreground">
                                                <SelectValue />
                                              </SelectTrigger>
                                              <SelectContent className="bg-card border-border text-foreground">
                                                {observerTypesGenerateActivity.map((t) => (
                                                    <SelectItem key={t} value={t} className="hover:bg-muted">{t}</SelectItem>
                                                ))}
                                              </SelectContent>
                                            </Select>
                                        ) : obs.type}
                                      </TableCell>
                                      <TableCell className="text-center text-center min-w-[120px]">
                                        <div className="flex justify-center gap-2">
                                          {obs.isEditing ? (
                                              <>
                                                <Button size="sm" variant="outline" onClick={() => saveUpdateObserver(obs.id)} className="text-green-600 border-green-600 hover:bg-green-600 hover:text-white">
                                                  <Save className="h-4 w-4" />
                                                </Button>
                                                <Button size="sm" variant="outline" onClick={() => cancelObserverEdit(obs.id)} className="text-muted-foreground border-border hover:bg-muted">
                                                  <X className="h-4 w-4" />
                                                </Button>
                                              </>
                                          ) : (
                                              <>
                                                <Button size="sm" variant="outline" onClick={() => toggleObserverEdit(obs.id)} className="text-primary border-primary hover:bg-primary hover:text-primary-foreground">
                                                  <Edit3 className="h-4 w-4" />
                                                </Button>
                                                <Button size="sm" variant="outline" onClick={() => handleRemoveObserver(obs.id)} className="text-destructive border-destructive hover:bg-destructive hover:text-destructive-foreground">
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

                      {observers.filter((obs) => obs.generateActivity).length === 0 && <p className="text-center text-muted-foreground mt-4">No observers for generate activity configured.</p>}
                    </CardContent>
                ) : (
                    <CardContent>
                    <div className="flex flex-col items-center justify-center max-h-full text-center py-16">
                      <h3 className="text-xl font-semibold mb-2 text-foreground">
                        No Generate Activity Selected
                      </h3>

                      <p className="text-muted-foreground text-sm max-w-md">
                        Select a Work Product Element with the Generate Activity field checked 'true' from the table on the top to view its details here.
                      </p>
                    </div>
                    </CardContent>
                )}
              </Card>

              {/* DISTRIBUTION PARAMETERS FOR GENERATE ACTIVITY */}
              <Card className="bg-card border-border text-foreground w-full">
                <CardHeader>
                  <CardTitle className="text-2xl text-primary">Distribution Parameters for Generate Activity</CardTitle>
                  {selectedWorkProductGenerateActivity ? (
                      <CardDescription>
                    Generate activity for demand work product:{" "}
                    <span className="font-bold text-blue-600">{clickedWorkProduct || "None"}</span>
                  </CardDescription>) : (
                      <></>
                  )}
                </CardHeader>
                {selectedWorkProductGenerateActivity ? (
                  <CardContent>
                    <FieldSection>
                      {/*{selectedWorkProduct && (*/}
                      <DistributionField
                          value={distribution}
                          onChange={(updatedDistribution) => {
                            setDistribution(updatedDistribution);
                            // saveDistribution(updatedDistribution);
                          }}
                      />
                      {/*)}*/}
                    </FieldSection>

                  </CardContent>) : (
                    <CardContent>
                      <div className="flex flex-col items-center justify-center max-h-full text-center py-16">
                        <h3 className="text-xl font-semibold mb-2 text-foreground">
                          No Generate Activity Selected
                        </h3>

                        <p className="text-muted-foreground text-sm max-w-md">
                          Select a Work Product Element with the Generate Activity field checked 'true' from the table on the top to view its details here.
                        </p>
                      </div>
                    </CardContent>

                )}
              </Card>

            </div>
          </div>
        </div>
      </>
  );
};

export default WorkProductsTableTab;
