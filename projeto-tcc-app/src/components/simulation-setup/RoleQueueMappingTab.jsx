import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Edit3, PlusCircle, Save, Trash2, X, Check } from 'lucide-react';
import { useToast } from "@/components/ui/use-toast";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';

const RoleQueueMappingTab = ({ processId }) => {
  const { toast } = useToast();
  const [mappings, setMappings] = useState([]);
  const [observers, setObservers] = useState([]);
  const [selectedRole, setSelectedRole] = useState("");
  const [selectedType, setSelectedType] = useState("NONE");
  const [isAddingObserver, setIsAddingObserver] = useState(false);

  const observerTypes = ["NONE", "LENGTH", "TIME", "STATIONARY"];
  const queueTypes = ["QUEUE", "SET", "STACK"];

  // Fetch roles from backend
  useEffect(() => {
    const fetchMappings = async () => {
      try {
        const response = await fetch(`http://localhost:8080/role-configs/process/${processId}`);
        if (!response.ok) throw new Error("Failed to fetch roles");

        const data = await response.json();

        // Roles
        const mapped = data.map((role) => ({
          id: role.id,
          name: role.name,
          queueName: role.queue_name,
          queueType: role.queue_type || "QUEUE",
          initialQuantity: role.initial_quantity ?? 1,
          isEditing: false,
        }));
        setMappings(mapped);

        // Observers
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

        const mappedActivities = data.flatMap((role) =>
            role.activities || []
        );


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

  // Handle queue type change in mapping table
  const handleSelectChange = (value, id) => {
    setMappings(mappings.map(m => m.id === id ? { ...m, queueType: value } : m));
  };

  // Handle quantity change in mapping table
  const handleQuantityChange = (e, id) => {
    const value = parseInt(e.target.value, 10);
    setMappings(mappings.map(m => m.id === id ? { ...m, initialQuantity: value } : m));
  };

  // Toggle edit mode for mapping
  const toggleEdit = (id) => {
    setMappings(mappings.map(m => m.id === id ? { ...m, isEditing: !m.isEditing } : m));
  };

  // Save Role changes
  const saveRole = async (id) => {
    const roleToSave = mappings.find(m => m.id === id);

    try {
      const response = await fetch(`http://localhost:8080/role-configs/${id}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          queueName: roleToSave.queueName,
          queueType: roleToSave.queueType || "QUEUE",
          initialQuantity: roleToSave.initialQuantity ?? 1,
        }),
      });

      if (!response.ok) {
        throw new Error("Failed to update role config");
      }

      toggleEdit(id);
      toast({
        title: "Saved",
        description: `Role config for "${roleToSave.name}" updated successfully.`,
        variant: "default",
      });
    } catch (error) {
      console.error(error);
      toast({
        title: "Error",
        description: "Unable to save role config.",
        variant: "destructive",
      });
    }
  };

  // Show add observer form
  const showAddObserverForm = () => {
    setIsAddingObserver(true);
    setSelectedRole("");
    setSelectedType("NONE");
  };

  // Cancel add observer
  const cancelAddObserver = () => {
    setIsAddingObserver(false);
    setSelectedRole("");
    setSelectedType("NONE");
  };

  // Get next observer index (sequential across all observers)
  const getNextObserverIndex = () => {
    if (observers.length === 0) return 1;

    // Extract all existing indices and find the highest
    const existingIndices = observers.map(obs => {
      const match = obs.name.match(/observer (\d+)$/);
      return match ? parseInt(match[1], 10) : 0;
    });

    return Math.max(...existingIndices) + 1;
  };

  // Add observer
  const handleAddObserver = async () => {
    if (!selectedRole) {
      toast({
        title: "Error",
        description: "Please select a role first.",
        variant: "destructive",
      });
      return;
    }

    const selectedRoleData = mappings.find(m => m.name === selectedRole);
    if (!selectedRoleData) return;

    const nextIndex = getNextObserverIndex();
    const observerName = `${selectedRole} queue observer ${nextIndex}`;

    try {
      const response = await fetch(
          `http://localhost:8080/role-configs/${selectedRoleData.id}/observers`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify({
              type: selectedType,
              queueName: observerName,
            }),
          }
      );

      if (!response.ok) throw new Error("Failed to add observer");

      const savedObserver = await response.json();

      // Adiciona no estado local usando o ID real retornado do backend
      setObservers([...observers, {
        id: savedObserver.id,
        name: savedObserver.name,
        queueName: savedObserver.queue_name,
        type: savedObserver.type,
        position: savedObserver.position,
        isEditing: false,
      }]);

      setIsAddingObserver(false);
      setSelectedRole("");
      setSelectedType("NONE");

      toast({
        title: "Observer Added",
        description: `Observer "${observerName}" has been added.`,
        variant: "default",
      });
    } catch (error) {
      console.error(error);
      toast({
        title: "Error",
        description: "Unable to save observer.",
        variant: "destructive",
      });
    }
  };


  // Remove observer
  const handleRemoveObserver = (id) => {
    const observerToRemove = observers.find(o => o.id === id);
    if (observerToRemove) {
      setObservers(prevObservers => prevObservers.filter(o => o.id !== id));
      toast({
        title: "Observer Removed",
        description: `Observer "${observerToRemove.name}" has been removed.`,
        variant: "default",
      });
    }
  };

  // Toggle observer edit mode
  const toggleObserverEdit = (id) => {
    setObservers(observers.map(o => o.id === id ? { ...o, isEditing: !o.isEditing } : o));
  };

  // Handle observer type change
  const handleObserverTypeChange = (value, id) => {
    setObservers(observers.map(o => o.id === id ? { ...o, type: value } : o));
  };

  // Save observer changes
  const saveObserver = async (id) => {
    const observerToSave = observers.find(o => o.id === id);

    const body = {
      type: observerToSave.type,
      queueName: observerToSave.name,
    };

    try {
      const response = await fetch(`http://localhost:8080/role-configs/observers/${id}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(body),
      });

      if (!response.ok) {
        throw new Error(`Erro ao atualizar: ${response.statusText}`);
      }

      const updated = await response.json();

      toggleObserverEdit(id);
      toast({
        title: "Observer Updated",
        description: `Observer "${updated.queueName}" type updated to "${updated.type}".`,
        variant: "default",
      });
    } catch (error) {
      toast({
        title: "Error",
        description: error.message,
        variant: "destructive",
      });
    }
  };


  // Cancel observer edit
  const cancelObserverEdit = (id) => {
    toggleObserverEdit(id);
  };

  return (
      <>
        <Card className="bg-slate-800 border-slate-700 text-slate-50">
          <CardHeader>
            <CardTitle className="text-2xl text-sky-400">Role & Queue Mapping</CardTitle>
            <CardDescription className="text-slate-400">Roles are generated from the process. You can only edit queue type and initial quantity.</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="overflow-x-auto">
              <Table className="min-w-full">
                <TableHeader>
                  <TableRow className="border-slate-700 hover:bg-slate-700/30">
                    <TableHead className="text-sky-300">Role Name</TableHead>
                    <TableHead className="text-sky-300">Queue Name</TableHead>
                    <TableHead className="text-sky-300">Queue Type</TableHead>
                    <TableHead className="text-sky-300 text-right">Initial Quantity</TableHead>
                    <TableHead className="text-sky-300 text-center">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {mappings.map((mapping) => (
                      <TableRow key={mapping.id} className="border-slate-700 hover:bg-slate-700/30">
                        <TableCell>{mapping.name}</TableCell>
                        <TableCell>{mapping.queueName}</TableCell>
                        <TableCell>
                          {mapping.isEditing ? (
                              <Select
                                  value={mapping.queueType}
                                  onValueChange={(value) => handleSelectChange(value, mapping.id)}
                              >
                                <SelectTrigger className="bg-slate-600 border-slate-500 text-slate-100 w-32">
                                  <SelectValue />
                                </SelectTrigger>
                                <SelectContent className="bg-slate-700 border-slate-600">
                                  {queueTypes.map((type) => (
                                      <SelectItem key={type} value={type} className="text-slate-100 hover:bg-slate-600">
                                        {type}
                                      </SelectItem>
                                  ))}
                                </SelectContent>
                              </Select>
                          ) : (
                              mapping.queueType
                          )}
                        </TableCell>
                        <TableCell className="text-right">
                          {mapping.isEditing ? (
                              <Input
                                  type="number"
                                  value={mapping.initialQuantity}
                                  onChange={(e) => handleQuantityChange(e, mapping.id)}
                                  className="bg-slate-600 border-slate-500 text-slate-100 w-20 text-right"
                                  min="1"
                              />
                          ) : (
                              mapping.initialQuantity
                          )}
                        </TableCell>
                        <TableCell className="text-center">
                          <div className="flex justify-center gap-2">
                            {mapping.isEditing ? (
                                <>
                                  <Button
                                      size="sm"
                                      variant="outline"
                                      onClick={() => saveRole(mapping.id)}
                                      className="text-green-400 border-green-400 hover:bg-green-400 hover:text-white"
                                  >
                                    <Save className="h-4 w-4" />
                                  </Button>
                                  <Button
                                      size="sm"
                                      variant="outline"
                                      onClick={() => toggleEdit(mapping.id)}
                                      className="text-slate-400 border-slate-400 hover:bg-slate-400 hover:text-white"
                                  >
                                    <X className="h-4 w-4" />
                                  </Button>
                                </>
                            ) : (
                                <Button
                                    size="sm"
                                    variant="outline"
                                    onClick={() => toggleEdit(mapping.id)}
                                    className="text-sky-400 border-sky-400 hover:bg-sky-400 hover:text-slate-900"
                                >
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
            {mappings.length === 0 && (
                <p className="text-center text-slate-500 mt-4">No roles found for this process.</p>
            )}
          </CardContent>
        </Card>

        <Card className="bg-slate-800 border-slate-700 text-slate-50 mt-6">
          <CardHeader>
            <CardTitle className="text-2xl text-sky-400">Configure Observers</CardTitle>
            <CardDescription className="text-slate-400">Manage global observers for queues in this process.</CardDescription>
          </CardHeader>
          <CardContent>
            <Button
                onClick={showAddObserverForm}
                className="bg-sky-500 hover:bg-sky-600 text-white mb-4"
            >
              <PlusCircle className="mr-2 h-5 w-5" /> Add Observer
            </Button>

            {isAddingObserver && (
                <div className="mb-4 p-4 border border-slate-600 rounded-lg bg-slate-700">
                  <h3 className="text-lg font-semibold text-sky-300 mb-3">Add New Observer</h3>
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    <div>
                      <label className="block text-sm font-medium text-slate-300 mb-2">Select Role</label>
                      <Select value={selectedRole} onValueChange={setSelectedRole}>
                        <SelectTrigger className="bg-slate-600 border-slate-500 text-slate-100">
                          <SelectValue placeholder="Choose a role" />
                        </SelectTrigger>
                        <SelectContent className="bg-slate-700 border-slate-600">
                          {mappings.map((mapping) => (
                              <SelectItem key={mapping.id} value={mapping.name} className="text-slate-100 hover:bg-slate-600">
                                {mapping.name}
                              </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-slate-300 mb-2">Observer Type</label>
                      <Select value={selectedType} onValueChange={setSelectedType}>
                        <SelectTrigger className="bg-slate-600 border-slate-500 text-slate-100">
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent className="bg-slate-700 border-slate-600">
                          {observerTypes.map((type) => (
                              <SelectItem key={type} value={type} className="text-slate-100 hover:bg-slate-600">
                                {type}
                              </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>
                    <div className="flex items-end gap-2">
                      <Button
                          onClick={handleAddObserver}
                          className="bg-green-600 hover:bg-green-700 text-white"
                      >
                        <Save className="h-4 w-4 mr-1" /> Add
                      </Button>
                      <Button
                          onClick={cancelAddObserver}
                          variant="outline"
                          className="text-slate-300 border-slate-500 hover:bg-slate-600"
                      >
                        <X className="h-4 w-4 mr-1" /> Cancel
                      </Button>
                    </div>
                  </div>
                </div>
            )}

            <div className="overflow-x-auto">
              <Table className="min-w-full">
                <TableHeader>
                  <TableRow className="border-slate-700 hover:bg-slate-700/30">
                    <TableHead className="text-sky-300">Name</TableHead>
                    <TableHead className="text-sky-300">Type</TableHead>
                    <TableHead className="text-sky-300 text-center">Actions</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {observers.map((obs) => (
                      <TableRow key={obs.id} className="border-slate-700 hover:bg-slate-700/30">
                        <TableCell>{obs.name}</TableCell>
                        <TableCell>
                          {obs.isEditing ? (
                              <Select
                                  value={obs.type}
                                  onValueChange={(value) => handleObserverTypeChange(value, obs.id)}
                              >
                                <SelectTrigger className="bg-slate-600 border-slate-500 text-slate-100 w-32">
                                  <SelectValue />
                                </SelectTrigger>
                                <SelectContent className="bg-slate-700 border-slate-600">
                                  {observerTypes.map((type) => (
                                      <SelectItem key={type} value={type} className="text-slate-100 hover:bg-slate-600">
                                        {type}
                                      </SelectItem>
                                  ))}
                                </SelectContent>
                              </Select>
                          ) : (
                              obs.type
                          )}
                        </TableCell>
                        <TableCell className="text-center">
                          <div className="flex justify-center gap-2">
                            {obs.isEditing ? (
                                <>
                                  <Button
                                      size="sm"
                                      variant="outline"
                                      onClick={() => saveObserver(obs.id)}
                                      className="text-green-400 border-green-400 hover:bg-green-400 hover:text-white"
                                  >
                                    <Save className="h-4 w-4" />
                                  </Button>
                                  <Button
                                      size="sm"
                                      variant="outline"
                                      onClick={() => cancelObserverEdit(obs.id)}
                                      className="text-slate-400 border-slate-400 hover:bg-slate-400 hover:text-white"
                                  >
                                    <X className="h-4 w-4" />
                                  </Button>
                                </>
                            ) : (
                                <>
                                  <Button
                                      size="sm"
                                      variant="outline"
                                      onClick={() => toggleObserverEdit(obs.id)}
                                      className="text-sky-400 border-sky-400 hover:bg-sky-400 hover:text-slate-900"
                                  >
                                    <Edit3 className="h-4 w-4" />
                                  </Button>
                                  <Button
                                      size="sm"
                                      variant="outline"
                                      onClick={() => handleRemoveObserver(obs.id)}
                                      className="text-red-400 border-red-400 hover:bg-red-400 hover:text-white"
                                  >
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
            {observers.length === 0 && (
                <p className="text-center text-slate-500 mt-4">No observers configured.</p>
            )}
          </CardContent>
        </Card>
      </>
  );
};

export default RoleQueueMappingTab;

