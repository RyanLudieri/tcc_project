import React, { useState, useEffect } from 'react';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { PlusCircle, Trash2 } from 'lucide-react';
import DistributionField from './DistributionField';

const FieldSection = ({ title, children }) => {
    return (
        <div className="space-y-4 border border-gray-300 shadow-lg rounded-lg p-4 mb-4">
            {title && <h3 className="text-sm font-semibold">{title}</h3>}
            {children}
        </div>
    );
};

const LocalView = ({ selectedItem }) => {
    const [observers, setObservers] = useState([{ name: 'Observer 1', type: 'ACTIVE' }]);
    const [spemType, setSpemType] = useState('');
    const [parent, setParent] = useState('');
    const [dependency, setDependency] = useState('FINISH_TO_START');
    const [timebox, setTimebox] = useState('');
    const [condition, setCondition] = useState('SINGLE_ENTITY_AVAILABLE');
    const [processingQuantity, setProcessingQuantity] = useState('UNIT');
    const [behavior, setBehavior] = useState('MOVE_BACK');
    const [quantity, setQuantity] = useState('');
    const [distribution, setDistribution] = useState({ type: 'CONSTANT', params: {} });

    // Atualiza spemType dinamicamente conforme selectedItem
    useEffect(() => {
        if (!selectedItem) {
            setSpemType('');
            return;
        }
        if (selectedItem.type === 'ITERATION') setSpemType('ITERATION');
        else if (selectedItem.type === 'TASK_DESCRIPTOR') setSpemType('TASK');
    }, [selectedItem]);

    const addObserver = () => {
        const newObserver = { name: `Observer ${observers.length + 1}`, type: 'ACTIVE' };
        setObservers([...observers, newObserver]);
    };

    const removeObserver = (index) => {
        setObservers(observers.filter((_, i) => i !== index));
    };

    return (
        <Card className="h-full shadow-lg">
            <CardHeader className="py-3">
                <CardTitle className="text-2xl text-primary">Local View</CardTitle>
            </CardHeader>
            <CardContent>
                {/* Duration / Distribution Field */}
                <FieldSection title={`Duration for ${selectedItem?.name || '...'}`}>
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
                            WBE{selectedItem ? ` ${selectedItem.name}` : ''}
                        </h3>
                        <button
                            onClick={addObserver}
                            className="flex items-center gap-2 text-white bg-primary hover:bg-primary-dark px-3 py-1 rounded-xl"
                        >
                            <PlusCircle className="w-4 h-4" />
                            Add Observer
                        </button>
                    </div>
                    <div className="max-h-32 overflow-auto">
                        <Table className="table-auto">
                            <TableHeader className="sticky top-0 bg-white shadow-md">
                                <TableRow>
                                    <TableHead className="text-xs">Name</TableHead>
                                    <TableHead className="text-xs">Type</TableHead>
                                    <TableHead className="text-xs">Actions</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {observers.map((observer, index) => (
                                    <TableRow key={index} className="text-xs">
                                        <TableCell>{observer.name}</TableCell>
                                        <TableCell>{observer.type}</TableCell>
                                        <TableCell>
                                            <button
                                                onClick={() => removeObserver(index)}
                                                className="text-red-500 hover:text-red-700"
                                            >
                                                <Trash2 className="w-5 h-5" />
                                            </button>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </div>
                </FieldSection>

                {/* Extended XACDML Attributes */}
                <FieldSection title="Extended XACDML Attributes">
                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-xs font-medium">SPEM Type</label>
                            <Input value={spemType} readOnly className="w-full bg-gray-100 cursor-not-allowed" />
                        </div>

                        <div>
                            <label className="block text-xs font-medium">Parent</label>
                            <Input value={parent} onChange={(e) => setParent(e.target.value)} className="w-full" />
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-xs font-medium">Dependency Type</label>
                            <Select value={dependency} onValueChange={setDependency} className="w-full">
                                <SelectTrigger>
                                    <SelectValue placeholder="Select Dependency Type" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="FINISH_TO_START">FINISH_TO_START</SelectItem>
                                    <SelectItem value="START_TO_START">START_TO_START</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>

                        <div>
                            <label className="block text-xs font-medium">Timebox</label>
                            <Input value={timebox} onChange={(e) => setTimebox(e.target.value)} className="w-1/4" />
                        </div>
                    </div>

                    <div className="space-y-4">
                        <div>
                            <label className="block text-xs font-medium">Condition to Process</label>
                            <Select value={condition} onValueChange={setCondition} className="w-full">
                                <SelectTrigger>
                                    <SelectValue placeholder="Select Condition" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="SINGLE_ENTITY_AVAILABLE">SINGLE_ENTITY_AVAILABLE</SelectItem>
                                    <SelectItem value="MULTIPLE_ENTITY_AVAILABLE">MULTIPLE_ENTITY_AVAILABLE</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>

                        <div>
                            <label className="block text-xs font-medium">Processing Quantity</label>
                            <Select value={processingQuantity} onValueChange={setProcessingQuantity} className="w-full">
                                <SelectTrigger>
                                    <SelectValue placeholder="Select Processing Quantity" />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="UNIT">UNIT</SelectItem>
                                    <SelectItem value="MULTIPLE">MULTIPLE</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                    </div>

                    <div>
                        <label className="block text-xs font-medium">Behavior at the End of an Iteration</label>
                        <Select value={behavior} onValueChange={setBehavior} className="w-full">
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
                        <label className="block text-xs font-medium">Quantity of Resources Needed</label>
                        <Input value={quantity} onChange={(e) => setQuantity(e.target.value)} className="w-full" />
                    </div>
                </FieldSection>
            </CardContent>
        </Card>
    );
};

export default LocalView;
