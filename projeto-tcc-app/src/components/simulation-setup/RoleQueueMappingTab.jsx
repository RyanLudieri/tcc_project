import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Edit3, PlusCircle, Save, Trash2, X } from 'lucide-react';
import { useToast } from "@/components/ui/use-toast";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { API_BASE_URL } from "@/config/api";


const RoleQueueMappingTab = ({ processId }) => {
  const { toast } = useToast();
  const [mappings, setMappings] = useState([]);
  const [observers, setObservers] = useState([]);
  const [selectedRole, setSelectedRole] = useState("");
  const [selectedType, setSelectedType] = useState("NONE");
  const [isAddingObserver, setIsAddingObserver] = useState(false);

  const observerTypes = ["NONE", "LENGTH", "TIME", "STATIONARY"];
  const queueTypes = ["QUEUE", "SET", "STACK"];

  useEffect(() => {
    const fetchMappings = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/role-configs/process/${processId}`);
        if (!response.ok) throw new Error("Failed to fetch roles");

        const data = await response.json();

        const mapped = data.map((role) => ({
          id: role.id,
          name: role.name,
          queueName: role.queue_name,
          queueType: role.queue_type || "QUEUE",
          initialQuantity: role.initial_quantity ?? 1,
          isEditing: false,
        }));
        setMappings(mapped);

        const mappedObservers = data.flatMap((role) =>
            (role.observers || []).map((obs) => ({
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
        console.error(error);
        toast({
          title: "Error",
          description: "Unable to load roles from process.",
          variant: "destructive",
        });
      }
    };

    if (processId && processId !== "new") fetchMappings();
  }, [processId, toast]);

  // ===== ROLES =====
  const handleSelectChange = (value, id) => {
    setMappings(mappings.map(m => m.id === id ? { ...m, queueType: value } : m));
  };

  const handleQuantityChange = (e, id) => {
    const value = parseInt(e.target.value, 10);
    setMappings(mappings.map(m => m.id === id ? { ...m, initialQuantity: value } : m));
  };

  const toggleEdit = (id) => {
    setMappings(mappings.map(m => m.id === id ? { ...m, isEditing: !m.isEditing } : m));
  };

  const saveRole = async (id) => {
    const roleToSave = mappings.find(m => m.id === id);

    try {
      const response = await fetch(`${API_BASE_URL}/role-configs/${id}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          queueName: roleToSave.queueName,
          queueType: roleToSave.queueType || "QUEUE",
          initialQuantity: roleToSave.initialQuantity ?? 1,
        }),
      });

      if (!response.ok) throw new Error("Failed to update role config");

      toggleEdit(id);
      toast({
        title: "Saved",
        description: `Role config for "${roleToSave.name}" updated successfully.`,
        variant: "default",
      });
    } catch (error) {
      toast({
        title: "Error",
        description: "Unable to save role config.",
        variant: "destructive",
      });
    }
  };

  // ===== OBSERVERS =====
  const showAddObserverForm = () => {
    setIsAddingObserver(true);
    setSelectedRole("");
    setSelectedType("NONE");
  };

  const cancelAddObserver = () => {
    setIsAddingObserver(false);
    setSelectedRole("");
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

  const handleAddObserver = async () => {
    if (!selectedRole) {
      toast({ title: "Error", description: "Please select a role first.", variant: "destructive" });
      return;
    }

    const selectedRoleData = mappings.find(m => m.name === selectedRole);
    if (!selectedRoleData) return;

    const nextIndex = getNextObserverIndex();
    const observerName = `${selectedRole} queue observer ${nextIndex}`;
    const query = selectedType && selectedType !== "NONE" ? `?type=${selectedType}` : "";

    try {
      const response = await fetch(
          `${API_BASE_URL}/role-configs/${selectedRoleData.id}/observers${query}`,
          { method: "POST" }
      );
      if (!response.ok) throw new Error("Failed to add observer");

      const savedObserver = await response.json();

      setObservers([...observers, {
        id: savedObserver.id,
        roleConfigId: selectedRoleData.id,
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

  const handleRemoveObserver = async (id) => {
    const observerToRemove = observers.find(o => o.id === id);
    if (!observerToRemove) return;

    const role = mappings.find(m => m.queueName === observerToRemove.queueName);
    if (!role) {
      toast({ title: "Error", description: "Unable to determine role for this observer.", variant: "destructive" });
      return;
    }

    try {
      const response = await fetch(
          `${API_BASE_URL}/role-configs/${role.id}/observers/${id}`,
          { method: "DELETE" }
      );
      if (!response.ok) throw new Error("Failed to delete observer");

      setObservers(prev => prev.filter(o => o.id !== id));
      toast({ title: "Observer Removed", description: `Observer "${observerToRemove.name}" has been removed.`, variant: "default" });
    } catch (error) {
      toast({ title: "Error", description: "Unable to delete observer.", variant: "destructive" });
    }
  };

  const toggleObserverEdit = (id) => {
    setObservers(observers.map(o => o.id === id ? { ...o, isEditing: !o.isEditing } : o));
  };

  const handleObserverTypeChange = (value, id) => {
    setObservers(observers.map(o => o.id === id ? { ...o, type: value } : o));
  };

  const saveObserver = async (id) => {
    const observerToSave = observers.find(o => o.id === id);
    const body = { type: observerToSave.type, queueName: observerToSave.name };

    try {
      const response = await fetch(`${API_BASE_URL}/role-configs/observers/${id}`, {
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

  return (
      <>
        {/* ROLES */}
        <Card className="bg-card border-border text-foreground">
          <CardHeader>
            <CardTitle className="text-2xl text-primary">Role & Queue Mapping</CardTitle>
            <CardDescription className="text-muted-foreground">
              Roles are generated from the process. You can only edit queue type and initial quantity.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto w-full">
              <Table className="min-w-[700px] table-fixed">
                <TableHeader>
                  <TableRow className="border-border hover:bg-muted/30">
                    <TableHead className="text-primary">Role Name</TableHead>
                    <TableHead className="text-primary">Queue Name</TableHead>
                    <TableHead className="text-primary">Queue Type</TableHead>
                    <TableHead className="text-primary text-right">Initial Quantity</TableHead>
                    <TableHead className="text-primary text-center">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {mappings.map((mapping) => (
                      <TableRow key={mapping.id} className="border-border hover:bg-muted/30">
                        <TableCell>{mapping.name}</TableCell>
                        <TableCell>{mapping.queueName}</TableCell>
                        <TableCell>
                          {mapping.isEditing ? (
                              <Select value={mapping.queueType} onValueChange={(v) => handleSelectChange(v, mapping.id)}>
                                <SelectTrigger className="w-full h-9 px-2 bg-card border-border text-foreground">
                                  <SelectValue />
                                </SelectTrigger>
                                <SelectContent className="bg-card border-border text-foreground">
                                  {queueTypes.map((type) => (
                                      <SelectItem key={type} value={type} className="hover:bg-muted">{type}</SelectItem>
                                  ))}
                                </SelectContent>
                              </Select>
                          ) : mapping.queueType}
                        </TableCell>
                        <TableCell className="text-right">
                          {mapping.isEditing ? (
                              <Input
                                  type="number"
                                  value={mapping.initialQuantity}
                                  onChange={(e) => handleQuantityChange(e, mapping.id)}
                                  className="w-full h-9 px-2 text-right bg-card border-border text-foreground"
                                  min="1"
                              />
                          ) : mapping.initialQuantity}
                        </TableCell>
                        <TableCell className="text-center">
                          <div className="flex justify-center gap-2">
                            {mapping.isEditing ? (
                                <>
                                  <Button size="sm" variant="outline" onClick={() => saveRole(mapping.id)} className="text-green-600 border-green-600 hover:bg-green-600 hover:text-white">
                                    <Save className="h-4 w-4" />
                                  </Button>
                                  <Button size="sm" variant="outline" onClick={() => toggleEdit(mapping.id)} className="text-muted-foreground border-border hover:bg-muted">
                                    <X className="h-4 w-4" />
                                  </Button>
                                </>
                            ) : (
                                <Button size="sm" variant="outline" onClick={() => toggleEdit(mapping.id)} className="text-primary border-primary hover:bg-primary hover:text-primary-foreground">
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
            {mappings.length === 0 && <p className="text-center text-muted-foreground mt-4">No roles found for this process.</p>}
          </CardContent>
        </Card>

        {/* OBSERVERS */}
        <Card className="bg-card border-border text-foreground mt-6">
          <CardHeader>
            <CardTitle className="text-2xl text-primary">Configure Observers</CardTitle>
            <CardDescription className="text-muted-foreground">Manage global observers for queues in this process.</CardDescription>
          </CardHeader>
          <CardContent>
            <Button onClick={showAddObserverForm} className="bg-primary hover:bg-primary/80 text-primary-foreground mb-4">
              <PlusCircle className="mr-2 h-5 w-5" /> Add Observer
            </Button>

            {isAddingObserver && (
                <div className="mb-4 p-4 border border-border rounded-lg bg-muted">
                  <h3 className="text-lg font-semibold text-primary mb-3">Add New Observer</h3>
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-foreground mb-2">Select Role</label>
                      <Select value={selectedRole} onValueChange={setSelectedRole}>
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
                      <label className="block text-sm font-medium text-foreground mb-2">Observer Type</label>
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

            <div className="overflow-x-auto mt-6">
              <Table className="min-w-[500px] table-fixed">
                <TableHeader>
                  <TableRow className="border-border hover:bg-muted/30">
                    <TableHead className="text-primary">Name</TableHead>
                    <TableHead className="text-primary">Type</TableHead>
                    <TableHead className="text-primary text-center">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {observers.map((obs) => (
                      <TableRow key={obs.id} className="border-border hover:bg-muted/30">
                        <TableCell>{obs.name}</TableCell>
                        <TableCell>
                          {obs.isEditing ? (
                              <Select value={obs.type} onValueChange={(v) => handleObserverTypeChange(v, obs.id)}>
                                <SelectTrigger className="w-full h-9 px-2 bg-card border-border text-foreground">
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
                        <TableCell className="text-center">
                          <div className="flex justify-center gap-2">
                            {obs.isEditing ? (
                                <>
                                  <Button size="sm" variant="outline" onClick={() => saveObserver(obs.id)} className="text-green-600 border-green-600 hover:bg-green-600 hover:text-white">
                                    <Save className="h-4 w-4" />
                                  </Button>
                                  <Button size="sm" variant="outline" onClick={() => toggleObserverEdit(obs.id)} className="text-muted-foreground border-border hover:bg-muted">
                                    <X className="h-4 w-4" />
                                  </Button>
                                </>
                            ) : (
                                <>
                                  <Button size="sm" variant="outline" onClick={() => toggleObserverEdit(obs.id)} className="text-primary border-primary hover:bg-primary hover:text-primary-foreground">
                                    <Edit3 className="h-4 w-4" />
                                  </Button>
                                  <Button size="sm" variant="outline" onClick={() => handleRemoveObserver(obs.id)} className="text-red-600 border-red-600 hover:bg-red-600 hover:text-white">
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

export default RoleQueueMappingTab;
