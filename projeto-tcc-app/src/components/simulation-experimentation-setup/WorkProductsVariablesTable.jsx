import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Table, TableBody, TableCell, TableRow } from '@/components/ui/table';
import {Edit3, Rocket, Save} from 'lucide-react';
import { useToast } from "@/components/ui/use-toast";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { API_BASE_URL } from "@/config/api";

const WorkProductsVariablesTable = ({ processId, onChangeSimulationParams }) => {
    const { toast } = useToast();
    const variableTypes = ['INDEPENDENT', 'DEPENDENT', 'INTERMEDIATE'];
    const [clickedWorkProduct, setClickedWorkProduct] = useState(null);
    const [workProducts, setWorkProducts] = useState([]);
    const [observers, setObservers] = useState([]);
    const [selectedWorkProduct, setSelectedWorkProduct] = useState("");
    const [selectedType, setSelectedType] = useState("NONE");
    const [selectedTypeGenerateActivity, setSelectedTypeGenerateActivity] = useState("NONE");
    const [distribution, setDistribution] = useState({ type: 'CONSTANT', params: {} });
    const selectedWorkProductObj = workProducts.find(wp => wp.queueName === clickedWorkProduct);
    const [duration, setDuration] = useState("");
    const [replications, setReplications] = useState("");

    useEffect(() => {
        if (onChangeSimulationParams) {
            onChangeSimulationParams({ duration, replications });
        }
    }, [duration, replications]);

    useEffect(() => {
        const fetchWorkProducts = async () => {
            try {
                const response = await fetch(`${API_BASE_URL}/work-product-configs/process/${processId}/variables`);
                if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
                const data = await response.json();

                const workProducts = data.map((wp) => ({
                    id: wp.id,
                    workProduct: wp.workProductName,
                    inputOutput: wp.input_output,
                    taskName: wp.task_name,
                    queueName: wp.queue_name,
                    variableType: wp.variableType,
                    isEditing: false
                }));
                setWorkProducts(workProducts);

            } catch (error) {
                console.error("Unable to load work products from process:", error);
                setWorkProducts([]);
            }
        };
        fetchWorkProducts();
    }, [processId]);

    /* ===== WORK PRODUCTS ===== */
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
                    variableType: wpToSave.variableType
                }),
            });

            if (!response.ok) throw new Error("Failed to update work product config");

            toggleEdit(id);
            toast({
                title: "Saved",
                description: `Variable type for "${wpToSave.queueName}" updated successfully.`,
            });
        } catch (error) {
            toast({
                title: "Error",
                description: "Unable to save variable type.",
                variant: "destructive",
            });
        }
    };


    return (
        <>
            <div className="space-y-6">

                {/*CARDS*/}
                <div className="space-y-3 w-full">

                    <div className="flex flex-col md:flex-row gap-3 w-full">

                        {/* PROJECT ENDING CONDITION — agora do mesmo tamanho */}
                        <Card className="bg-card border-border text-foreground flex-[2] h-fit">
                            <CardHeader className="pb-3">
                                <CardTitle className="text-2xl text-primary">Project Ending Condition</CardTitle>
                                <CardDescription className="text-muted-foreground">
                                    Define how the simulation stops: by max duration or when a selected work product is exhausted.
                                </CardDescription>
                            </CardHeader>

                            <CardContent className="pb-4">
                                <div className="flex items-center justify-center gap-6 py-2">

                                    {/* Duration time */}
                                    <div className="flex items-center gap-2">
                                        <label className="text-sm font-medium">Duration time</label>
                                        <Input
                                            type="number"
                                            className="w-24 appearance-none"
                                            placeholder="0"
                                            value={duration}
                                            onChange={(e) => setDuration(e.target.value)}
                                        />
                                    </div>

                                    <span className="text-lg font-semibold">OR</span>

                                    {/* No more temporary entity */}
                                    <div className="flex items-center gap-2">
                                        <label className="text-sm font-medium">No more temporary entity</label>

                                        <select
                                            className="border border-border rounded-md px-3 py-2 bg-background text-sm"
                                            value={selectedWorkProduct}
                                            onChange={(e) => setSelectedWorkProduct(e.target.value)}
                                        >
                                            <option value="">Select work product</option>
                                            {workProducts.map((wp) => (
                                                <option key={wp.id} value={wp.queueName}>
                                                    {wp.workProduct}
                                                </option>
                                            ))}
                                        </select>
                                    </div>

                                </div>
                            </CardContent>
                        </Card>

                        {/* RUN CONTROL — agora do mesmo tamanho */}
                        <Card className="bg-card border-border text-foreground flex- h-fit">
                            <CardHeader className="pb-3">
                                <CardTitle className="text-2xl text-primary">Run Control</CardTitle>
                                <CardDescription>
                                    Set the number of replications to improve statistical confidence.
                                </CardDescription>
                            </CardHeader>

                            <CardContent className="pb-4">
                                <div className="flex items-center justify-center gap-6 py-2">

                                    <div className="flex items-center gap-2">
                                        <label className="text-sm font-medium">Number of replications</label>
                                        <Input
                                            type="number"
                                            className="w-24 appearance-none"
                                            placeholder="0"
                                            value={replications}
                                            onChange={(e) => setReplications(e.target.value)}
                                        />
                                    </div>

                                </div>
                            </CardContent>
                        </Card>

                    </div>




                </div>

                {/* WORK PRODUCTS */}
                <Card className="bg-card border-border text-foreground">
                    <CardHeader>
                        <CardTitle className="text-2xl text-primary">Work Products Variables Table</CardTitle>
                        <CardDescription className="text-muted-foreground">
                            Configure work products variables for the simulation.
                        </CardDescription>
                    </CardHeader>
                    <CardContent>

                        {/* =============== Work Products Variables Table ===================*/}
                        <div className="overflow-x-auto max-h-[400px] border border-border rounded-lg">
                            <div className="sticky top-0 z-10 flex bg-muted border-b border-border h-12 items-center">
                                <div className="w-[16%] text-sm text-primary text-center">Work Product</div>
                                <div className="w-[16%] text-sm text-primary text-center">Input/Output</div>
                                <div className="w-[16%] text-sm text-primary text-center">Task Name</div>
                                <div className="w-[16%] text-sm text-primary text-center">Variable Type</div>
                                <div className="w-[16%] text-sm text-primary text-center">Queue Name</div>
                                <div className="w-[16%] text-sm text-primary text-center">Actions</div>
                            </div>
                            <Table className="min-w-[800px] w-full table-fixed">
                                <TableBody>
                                    {workProducts.map((wp) => (
                                        <TableRow key={wp.id}
                                                  className={` border-border h-12 cursor-pointer active:bg-primary/20
                                      ${clickedWorkProduct === wp.queueName
                                                      ? "bg-blue-100 hover:bg-blue-200"
                                                      : "hover:bg-muted"
                                                  }
                                  `}
                                                  onClick={() => setClickedWorkProduct(wp.queueName)}>
                                            <TableCell className="text-center min-w-1/6">{wp.workProduct}</TableCell>
                                            <TableCell className="text-center min-w-1/6">{wp.inputOutput}</TableCell>
                                            <TableCell className="text-center min-w-1/6">{wp.taskName}</TableCell>
                                            <TableCell className="text-center min-w-1/6">
                                                {wp.isEditing ? (
                                                    <select
                                                        className="border border-border rounded-md px-2 py-1 bg-background text-sm"
                                                        value={wp.variableType}
                                                        onChange={(e) =>
                                                            setWorkProducts(workProducts.map(r =>
                                                                r.id === wp.id ? { ...r, variableType: e.target.value } : r
                                                            ))
                                                        }
                                                    >
                                                        {variableTypes.map((type) => (
                                                            <option key={type} value={type}>
                                                                {type}
                                                            </option>
                                                        ))}
                                                    </select>
                                                ) : (
                                                    wp.variableType
                                                )}
                                            </TableCell>

                                            <TableCell className="text-center min-w-1/6">{wp.queueName}</TableCell>
                                            <TableCell className="text-center min-w-1/6">
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



                </div>
            </div>
        </>
    );
};

export default WorkProductsVariablesTable;
