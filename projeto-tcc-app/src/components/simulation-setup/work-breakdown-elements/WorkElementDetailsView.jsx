import React, { useState, useEffect } from 'react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import {Card, CardContent, CardDescription, CardHeader, CardTitle} from '@/components/ui/card';
import {Edit3, PlusCircle, Save, Trash2} from 'lucide-react';
import DistributionField from './DistributionField';

const FieldSection = ({ title, children }) => {
    return (
        <div className="space-y-4 border border-gray-300 shadow-lg rounded-lg p-4 mb-4">
            {title && <h3 className="text-sm font-semibold">{title}</h3>}
            {children}
        </div>
    );
};

const WorkElementDetailsView = ({ selectedItem }) => {
    const [observers, setObservers] = useState([{ id: 1, name: 'Observer 1', type: 'ACTIVE', isEditing: false }]);
    const [spemType, setSpemType] = useState('');
    const [parent, setParent] = useState('');
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

    /* OBSERVERS */
    const addObserver = () => {
        const newObserver = {
            id: Date.now(), // id único
            name: `Observer ${observers.length + 1}`,
            type: 'ACTIVE',
            isEditing: false
        };
        setObservers([...observers, newObserver]);
    };

    const removeObserver = (index) => {
        setObservers(observers.filter((_, i) => i !== index));
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

    const saveObserver = (id) => {
        setObservers(
            observers.map((o) =>
                o.id === id ? { ...o, isEditing: false } : o
            )
        );
    };


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
        } else if (selectedItem.type === 'ACTIVITY'){
            setSpemType('ACTIVITY');
        }
    }, [selectedItem]);

    return (
        <Card className="h-full shadow-lg">
            <CardHeader className="py-3">
                <CardTitle className="text-2xl text-primary">Work Element Details View</CardTitle>
                <CardDescription className="text-muted-foreground">
                    Select an element from the Work Breakdown Elements View</CardDescription>
            </CardHeader>
            <CardContent>
                {/* Duration / Distribution Field */}
                <FieldSection title={`Duration for ${selectedItem?.presentationName || '...'}`}>
                    {selectedItem ? (
                        <DistributionField value={distribution} onChange={setDistribution} />
                    ) : (
                        <p className="text-xs text-gray-500">Select an Iteration or Task</p>
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
                                                            onClick={() => removeObserver(index)}
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
                                value={parent}
                                disabled={true}
                                onChange={(e) => setParent(e.target.value)}
                                className="w-full"
                            />
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-xs font-medium">Dependency type</label>
                            <Select
                                value={dependency}
                                onValueChange={setDependency}
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
                                onChange={(e) => setTimebox(e.target.value)}
                                disabled={!isIteration}
                                className="w-1/4" />
                        </div>
                    </div>

                    <div className="space-y-4">
                        <div>
                            <label className="block text-xs font-medium">Condition to process</label>
                            <Select
                                value={condition}
                                onValueChange={setCondition}
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
                                onValueChange={setProcessingQuantity}
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
                            onValueChange={setBehavior}
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
                            onChange={(e) => setQuantity(e.target.value)}
                            disabled={!isTask}
                            className="w-full"
                        />
                    </div>
                </FieldSection>
            </CardContent>
        </Card>
    );
};

export default WorkElementDetailsView;
