import React, { useState, useEffect } from 'react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from '@/components/ui/card';
import {Edit3, PlusCircle, Save, Trash2} from 'lucide-react';
import DistributionField from './DistributionField';
import {useToast} from "@/components/ui/use-toast.js";
import { Activity } from "lucide-react";


const FieldSection = ({ title, children }) => {
    return (
        <div className="space-y-4 border border-gray-300 shadow-lg rounded-lg p-4 mb-4">
            {title && <h3 className="text-sm font-semibold">{title}</h3>}
            {children}
        </div>
    );
};

const WorkElementDetailsView = ({ selectedItem }) => {
    const [observers, setObservers] = useState([]);
    const [spemType, setSpemType] = useState('');
    const [parentName, setParentName] = useState('');
    const [dependency, setDependency] = useState('FINISH_TO_START');
    const [timebox, setTimebox] = useState('');
    const [condition, setCondition] = useState('SINGLE_ENTITY_AVAILABLE');
    const [processingQuantity, setProcessingQuantity] = useState('UNIT');
    const [behavior, setBehavior] = useState('MOVE_BACK');
    const [quantity, setQuantity] = useState('');
    const [distribution, setDistribution] = useState({ type: 'CONSTANT', params: {} });

    const isIteration = selectedItem?.type === 'ITERATION';
    const isTask = selectedItem?.type === 'TASK_DESCRIPTOR';
    const isActivity = selectedItem?.type === 'ACTIVITY';
    const hasPredecessor = !!selectedItem?.predecessors?.length;
    const isRootProcess = selectedItem?.type === 'PROCESS';
    const isEmptyItem = !selectedItem;

    const observerTypes = ["ACTIVE", "DELAY", "PROCESSOR"];

    const { toast } = useToast();

    /* OBSERVERS */
    useEffect(() => {
        if (!selectedItem || !selectedItem.id) return;

        const fetchActivityConfig = async () => {
            try {
                const res = await fetch(`http://localhost:8080/activity-configs/${selectedItem.id}`);
                if (!res.ok) throw new Error('Failed to fetch activity config');
                const data = await res.json();

                // seta observers
                setObservers(data.observers || []);

                // seta distribuição
                if (data.distributionType && data.distributionParameter) {
                    setDistribution({
                        type: data.distributionType,
                        params: data.distributionParameter,
                    });
                } else {
                    setDistribution({ type: 'CONSTANT', params: {} });
                }
            } catch (err) {
                console.error('Failed to fetch activity config:', err);
                setObservers([]);
                setDistribution({ type: 'CONSTANT', params: {} });
            }
        };

        fetchActivityConfig();
    }, [selectedItem]);

    const addObserver = async () => {
        if (!selectedItem?.id) {
            toast({
                title: "Error",
                description: "Please select a valid Work Breakdown Element first.",
                variant: "destructive",
            });
            return;
        }

        const newObserver = {
            name: `Observer ${observers.length + 1}`,
            type: 'ACTIVE',
        };

        try {
            const res = await fetch(
                `http://localhost:8080/activity-configs/observers/${selectedItem.activityConfigId}`,
                {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(newObserver),
                }
            );

            if (!res.ok) throw new Error('Failed to add observer');
            const created = await res.json();

            setObservers((prev) => [...prev, { ...created, isEditing: false }]);
            toast({
                title: "Observer Added",
                description: `Observer "${created.name}" has been successfully created.`,
                variant: "default",
            });
        } catch (err) {
            console.error("Error adding observer:", err);
            toast({
                title: "Error",
                description: "Unable to add observer. Please try again.",
                variant: "destructive",
            });
        }
    };

    const removeObserver = async (observerId) => {
        if (!selectedItem?.id) {
            toast({
                title: "Error",
                description: "Please select a valid Work Breakdown Element first.",
                variant: "destructive",
            });
            return;
        }

        try {
            const res = await fetch(
                `http://localhost:8080/activity-configs/observers/${observerId}`,
                { method: "DELETE" }
            );

            if (!res.ok) throw new Error("Failed to delete observer");

            setObservers((prev) => prev.filter((o) => o.id !== observerId));

            toast({
                title: "Observer Removed",
                description: `Observer was successfully deleted.`,
                variant: "default",
            });
        } catch (err) {
            console.error("Error deleting observer:", err);
            toast({
                title: "Error",
                description: "Unable to delete observer. Please try again.",
                variant: "destructive",
            });
        }
    };

    const toggleObserverEdit = (id) => {
        setObservers(
            observers.map((o) =>
                o.id === id ? { ...o, isEditing: !o.isEditing } : o
            )
        );
    };

    const handleObserverTypeChange = (value, id) => {
        setObservers(
            observers.map((o) =>
                o.id === id ? { ...o, type: value } : o
            )
        );
    };

    const saveObserver = async (id) => {
        const observerToSave = observers.find((o) => o.id === id);
        if (!observerToSave) return;

        try {
            const res = await fetch(
                `http://localhost:8080/activity-configs/observers/${id}`,
                {
                    method: "PATCH",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        name: observerToSave.name,
                        type: observerToSave.type,
                    }),
                }
            );

            if (!res.ok) throw new Error("Failed to update observer");

            const updated = await res.json();

            setObservers((prev) =>
                prev.map((o) =>
                    o.id === id ? { ...updated, isEditing: false } : o
                )
            );

            toast({
                title: "Observer Updated",
                description: `Observer "${updated.name}" was successfully updated.`,
                variant: "default",
            });
        } catch (err) {
            console.error("Error updating observer:", err);
            toast({
                title: "Error",
                description: "Unable to update observer. Please try again.",
                variant: "destructive",
            });
        }
    };

    /* DISTRIBUTION */
    const saveDistribution = async (newDistribution) => {
        if (!selectedItem?.id) {
            toast({
                title: "Error",
                description: "Please select a valid Work Breakdown Element first.",
                variant: "destructive",
            });
            return;
        }

        const bodyToSend = {
            distributionType: newDistribution.type,
            distributionParameter: {
                id: newDistribution.params?.id || null,
                constant: newDistribution.params?.constant || null,
                average: newDistribution.params?.average || null,
                mean: newDistribution.params?.mean || null,
                standardDeviation: newDistribution.params?.standardDeviation || null,
                low: newDistribution.params?.low || null,
                high: newDistribution.params?.high || null,
                shape: newDistribution.params?.shape || null,
                scale: newDistribution.params?.scale || null,
            },
        };

        try {
            const res = await fetch(
                `http://localhost:8080/activity-configs/${selectedItem.id}`,
                {
                    method: "PATCH",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(bodyToSend),
                }
            );

            if (!res.ok) throw new Error("Failed to update distribution");

            const updated = await res.json();

            setDistribution({
                type: updated.distributionType,
                params: updated.distributionParameter,
            });

            toast({
                title: "Distribution Updated",
                description: `Distribution type "${updated.distributionType}" was successfully updated.`,
            });
        } catch (err) {
            console.error("Error updating distribution:", err);
            toast({
                title: "Error",
                description: "Unable to update distribution. Please try again.",
                variant: "destructive",
            });
        }
    };

    /* XACDML */
    useEffect(() => {
        if (!selectedItem) {
            setSpemType('');
            setDependency('N/A');
            setQuantity('N/A');
            setCondition('N/A');
            setProcessingQuantity('N/A');
            setBehavior('N/A');
            setTimebox('N/A');
            return;
        }

        if (selectedItem.type === 'ITERATION') {
            setSpemType('ITERATION');
            setDependency('FINISH_TO_START');
            setQuantity('');
        } else if (selectedItem.type === 'TASK_DESCRIPTOR') {
            setSpemType('TASK');
        } else if (selectedItem.type === 'PHASE') {
            setSpemType('PHASE');
        } else if (selectedItem.type === 'MILESTONE') {
            setSpemType('MILESTONE');
        } else if (selectedItem.type === 'ACTIVITY'){
            setSpemType('ACTIVITY');
        }
    }, [selectedItem]);

    useEffect(() => {
        if (!selectedItem || !selectedItem.id) {
            setParentName('N/A');
            return;
        }

        fetch(`http://localhost:8080/activity-configs/${selectedItem.id}`)
            .then((res) => (res.ok ? res.json() : null))
            .then((data) => {
                if (!data) {
                    setParentName('N/A');
                    return;
                }

                // se não tiver parentId (root), seta direto
                if (!data.parentId || data.parentId === null) {
                    setParentName('N/A'); // ou 'Root', se quiser
                    return;
                }

                setDependency(data.dependencyType);
                setTimebox(data.timeBox);
                setCondition(data.conditionToProcess);
                setProcessingQuantity(data.processingQuantity);
                setBehavior(data.iterationBehavior);
                setQuantity(data.requiredResources);

                // senão, busca o pai pra pegar o nome
                fetch(`http://localhost:8080/activity-configs/${data.parentId}`)
                    .then((res) => (res.ok ? res.json() : null))
                    .then((parentData) => {
                        setParentName(parentData?.name || parentData?.presentationName || 'N/A')
                    })
                    .catch((err) => {
                        console.error('Erro ao buscar pai:', err);
                        setParentName('N/A');
                    });
            })
            .catch((err) => {
                console.error('Erro ao buscar item:', err);
                setParentName('N/A');
            });
    }, [selectedItem]);

    const saveXACDML = async (newXACDML, updatedField) => {
        if (!selectedItem?.id) {
            toast({
                title: "Error",
                description: "Please select a valid Work Breakdown Element first.",
                variant: "destructive",
            });
            return;
        }

        const bodyToSend = {
            dependencyType: newXACDML.dependencyType,
            timeBox: newXACDML.timeBox,
            conditionToProcess: newXACDML.conditionToProcess,
            processingQuantity: newXACDML.processingQuantity,
            iterationBehavior: newXACDML.iterationBehavior,
            requiredResources: newXACDML.requiredResources,
        };

        try {
            const res = await fetch(
                `http://localhost:8080/activity-configs/${selectedItem.id}`,
                {
                    method: "PATCH",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(bodyToSend),
                }
            );

            if (!res.ok) throw new Error("Failed to update XACDML attributes");

            const updated = await res.json();

            toast({
                title: "XACDML Attributes Updated",
                description: `XACDML ${updatedField} for "${updated.name}" was successfully updated.`,
            });
        } catch (err) {
            console.error("Error updating distribution:", err);
            toast({
                title: "Error",
                description: "Unable to update distribution. Please try again.",
                variant: "destructive",
            });
        }
    };

    const handleXACDMLChange = (field, value) => {
        const updatedXACDML = {
            dependencyType: dependency,
            timeBox: timebox,
            conditionToProcess: condition,
            processingQuantity: processingQuantity,
            iterationBehavior: behavior,
            requiredResources: quantity,
            [field]: value,
        };

        let updatedField;

        // Atualiza o estado local correspondente
        switch (field) {
            case "dependencyType": setDependency(value); updatedField = "dependency type"; break;
            case "timeBox": setTimebox(value); updatedField = "timebox"; break;
            case "conditionToProcess": setCondition(value);  updatedField = "condition to process"; break;
            case "processingQuantity": setProcessingQuantity(value);  updatedField = "processing quantity";break;
            case "iterationBehavior": setBehavior(value);  updatedField = "iteration behavior";break;
            case "requiredResources": setQuantity(value); updatedField = "required resources"; break;
        }

        saveXACDML(updatedXACDML, updatedField);
    };




    return (
        <Card className="h-full shadow-lg">
            <CardHeader className="py-3">
                <CardTitle className="text-2xl text-primary">Work Element Details View</CardTitle>
            </CardHeader>
            {!selectedItem ? (
                <div className="flex flex-col items-center justify-center max-h-full text-center py-16">
                    <div className="p-4 bg-muted/40 rounded-full mb-4">
                        <Activity className="h-10 w-10 text-muted-foreground" />
                    </div>

                    <h3 className="text-xl font-semibold mb-2 text-foreground">
                        No Work Element Selected
                    </h3>

                    <p className="text-muted-foreground text-sm max-w-md">
                        Please select a Work Breakdown Element from the panel on the left to view its details here.
                    </p>
                </div>
            ) : (
                <>
                <CardContent>

                    {/* Duration / Distribution Field */}
                    <FieldSection title={`Duration for ${selectedItem?.presentationName || '...'}`}>
                        {selectedItem ? (
                            <DistributionField
                                value={distribution}
                                onChange={(updatedDistribution) => {
                                    setDistribution(updatedDistribution);
                                    saveDistribution(updatedDistribution);
                                }}
                            />
                        ) : (
                            <p className="text-xs text-gray-500">Select an element from the Work Breakdown Elements View</p>
                        )}
                    </FieldSection>

                    {/* Observers */}
                    <FieldSection title="Observers">
                        <div className="flex justify-between items-center">
                            <h3 className="text-sm font-semibold">
                                WBE{selectedItem ? ` ${selectedItem.presentationName}` : ' ...'}
                            </h3>
                            <button
                                onClick={addObserver}
                                className="flex items-center gap-2 text-white bg-primary hover:bg-primary-dark px-3 py-1 rounded-xl"
                            >
                                <PlusCircle className="w-4 h-4" />
                                Add Observer
                            </button>
                        </div>
                        <div className="max-h-48 overflow-y-auto border border-gray-200 rounded-md">
                            <Table className="table-fixed w-full">
                                <TableHeader className="sticky top-0 z-20 bg-white border-b border-gray-300">
                                    <TableRow>
                                        <TableHead className="text-xs w-40 px-2 py-0.5 text-left">Name</TableHead>
                                        <TableHead className="text-xs w-24 px-2 py-0.5 text-left">Type</TableHead>
                                        <TableHead className="text-xs w-24 px-2 py-0.5 text-left">Actions</TableHead>
                                    </TableRow>
                                </TableHeader>

                                <TableBody>
                                    {observers.map((observer, index) => (
                                        <TableRow
                                            key={observer.id}
                                            className="text-xs border-b border-gray-200 hover:bg-gray-50"
                                        >
                                            <TableCell className="w-40 px-2 py-1 truncate">{observer.name}</TableCell>
                                            <TableCell className="w-24 px-2 py-1">
                                                {observer.isEditing ? (
                                                    <Select
                                                        value={observer.type}
                                                        onValueChange={(v) => handleObserverTypeChange(v, observer.id)}
                                                    >
                                                        <SelectTrigger className="w-full h-8 px-2 text-xs">
                                                            <SelectValue />
                                                        </SelectTrigger>
                                                        <SelectContent>
                                                            {observerTypes.map((t) => (
                                                                <SelectItem key={t} value={t} className="text-xs">
                                                                    {t}
                                                                </SelectItem>
                                                            ))}
                                                        </SelectContent>
                                                    </Select>
                                                ) : (
                                                    observer.type
                                                )}
                                            </TableCell>
                                            <TableCell className="w-24 px-2 py-1">
                                                <div className="flex gap-2">
                                                    {observer.isEditing ? (
                                                        <>
                                                            <Button
                                                                size="sm"
                                                                variant="outline"
                                                                onClick={() => saveObserver(observer.id)}
                                                                className="text-green-600 border-green-600 hover:bg-green-600 hover:text-white text-xs px-2 py-1"
                                                            >
                                                                <Save className="h-4 w-4" />
                                                            </Button>
                                                            <Button
                                                                size="sm"
                                                                variant="outline"
                                                                onClick={() => toggleObserverEdit(observer.id)}
                                                                className="text-gray-500 border-gray-300 hover:bg-gray-200 text-xs px-2 py-1"
                                                            >
                                                                Cancel
                                                            </Button>
                                                        </>
                                                    ) : (
                                                        <>
                                                            <Button
                                                                size="sm"
                                                                variant="outline"
                                                                onClick={() => toggleObserverEdit(observer.id)}
                                                                className="text-primary border-primary hover:bg-primary hover:text-white text-xs px-2 py-1"
                                                            >
                                                                <Edit3 className="h-4 w-4" />
                                                            </Button>
                                                            <Button
                                                                size="sm"
                                                                variant="outline"
                                                                onClick={() => removeObserver(observer.id)}
                                                                className="text-red-600 border-red-600 hover:bg-red-600 hover:text-white text-xs px-2 py-1"
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
                            <p className="text-center text-xs text-muted-foreground mt-4">
                                No observers configured.
                            </p>
                        )}
                    </FieldSection>
                    {/* Extended XACDML Attributes */}
                    <FieldSection title="Extended XACDML Attributes">
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-xs font-medium">SPEM type</label>
                                <Input
                                    value={spemType}
                                    disabled
                                    className="w-full"
                                />
                            </div>

                            <div>
                                <label className="block text-xs font-medium">Parent</label>
                                <Input
                                    value={parentName ?? 'N/A'}
                                    disabled
                                    className="w-full"
                                />
                            </div>
                        </div>

                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <label className="block text-xs font-medium">Dependency type</label>
                                <Select
                                    value={dependency}
                                    onValueChange={(v) => handleXACDMLChange("dependencyType", v)}
                                    className="w-full"
                                    disabled={isIteration || !hasPredecessor}
                                >
                                    <SelectTrigger>
                                        <SelectValue placeholder="Select Dependency Type" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="FINISH_TO_START">FINISH_TO_START</SelectItem>
                                        <SelectItem value="FINISH_TO_FINISH">FINISH_TO_FINISH</SelectItem>
                                        <SelectItem value="START_TO_FINISH">START_TO_FINISH</SelectItem>
                                        <SelectItem value="START_TO_START">START_TO_START</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>

                            <div>
                                <label className="block text-xs font-medium">Timebox</label>
                                <Input
                                    value={isIteration || isActivity ? timebox : 'N/A'}
                                    onChange={(e) => handleXACDMLChange("timeBox", e.target.value)}
                                    disabled={!isIteration}
                                    className="w-1/4" />
                            </div>
                        </div>

                        <div className="space-y-4">
                            <div>
                                <label className="block text-xs font-medium">Condition to process</label>
                                <Select
                                    value={condition}
                                    onValueChange={(v) => handleXACDMLChange("conditionToProcess", v)}
                                    disabled={isRootProcess || isEmptyItem}
                                    className="w-full">
                                    <SelectTrigger >
                                        <SelectValue placeholder="Select Condition" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="SINGLE_ENTITY_AVAILABLE">SINGLE_ENTITY_AVAILABLE</SelectItem>
                                        <SelectItem value="ALL_ENTITIES_AVAILABLE">ALL_ENTITIES_AVAILABLE</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>

                            <div>
                                <label className="block text-xs font-medium">Processing quantity</label>
                                <Select
                                    value={processingQuantity}
                                    onValueChange={(v) => handleXACDMLChange("processingQuantity", v)}
                                    className="w-full"
                                    disabled={isRootProcess || isEmptyItem}>
                                    <SelectTrigger>
                                        <SelectValue placeholder="Select Processing Quantity" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="UNIT">UNIT</SelectItem>
                                        <SelectItem value="BATCH">BATCH</SelectItem>
                                    </SelectContent>
                                </Select>
                            </div>
                        </div>

                        <div>
                            <label className="block text-xs font-medium">Behavior at the end of an iteration</label>
                            <Select
                                value={isIteration? behavior : `MOVE_BACK`}
                                onValueChange={(v) => handleXACDMLChange("iterationBehavior", v)}
                                disabled={!isIteration}
                                className="w-full">
                                <SelectTrigger>
                                    <SelectValue placeholder="Select Behavior" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="MOVE_BACK">MOVE_BACK</SelectItem>
                                    <SelectItem value="DO_NOT_MOVE_BACK">DO_NOT_MOVE_BACK</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>

                        <div>
                            <label className="block text-xs font-medium">
                                Quantity of resources needed to perform the task
                            </label>
                            <Input
                                value={isTask ? quantity: "N/A" }
                                onChange={(e) => handleXACDMLChange("requiredResources", e.target.value)}
                                disabled={!isTask}
                                className="w-full"
                            />
                        </div>
                    </FieldSection>
                </CardContent>
            </>)}
        </Card>
    );
};

export default WorkElementDetailsView;
