import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Link, useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button.jsx';
import { Input } from '@/components/ui/input.jsx';
import { useToast } from '@/components/ui/use-toast.js';
import {
    Search,
    Plus,
    ArrowLeft,
    Grid3X3,
    List,
    Activity, PlusCircle
} from 'lucide-react';
import SimulationCard from '@/components/library/SimulationCard.jsx';
import SimulationListItem from '@/components/library/SimulationListItem.jsx';
// import SimulationPreview from '@/components/library/SimulationPreview.jsx';
import SimulationObjectiveModal from '@/components/modals/SimulationObjectiveModal.jsx';
import { API_BASE_URL } from "@/config/api";
import { v4 as uuidv4 } from 'uuid';


const SimulationLibraryPage = () => {
    const [simulations, setSimulations] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [viewMode, setViewMode] = useState('grid');
    const [selectedSimulation, setSelectedSimulation] = useState(null);
    const [open, setOpen] = useState(false);
    const navigate = useNavigate();
    const { toast } = useToast();


    useEffect(() => {
        loadSimulations();
    }, []);

    const loadSimulations = async () => {
        try {
            const response = await fetch(`${API_BASE_URL}/simulations`);
            if (!response.ok) throw new Error('Failed to fetch simulations');
            const data = await response.json();
            setSimulations(data);
        } catch (error) {
            console.error('Failed to load simulations:', error);
            toast({
                title: "Error Loading Simulations",
                description: "Failed to load saved simulations from server.",
                variant: "destructive",
            });
        }
    };

    const filteredSimulations = simulations.filter(sim =>
        sim.objective?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        sim.description?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const handleCreateNew = () => {
        setOpen(true);
    };

    const handleEditSimulation = (simulationId, processId) => {
        navigate(`/simulations/${simulationId}/processes/${processId}/edit`);
    };

    const handleViewSimulation = (simulation) => {
        setSelectedSimulation(simulation);
    };

    const handleDeleteSimulation = async (simulationId) => {
        try {
            await fetch(`${API_BASE_URL}/simulations/${simulationId}`, { method: 'DELETE' });
            setSimulations(prev => prev.filter(s => s.id !== simulationId));
            if (selectedSimulation && selectedSimulation.id === simulationId) {
                setSelectedSimulation(null);
            }
            toast({
                title: "Simulation Deleted",
                description: `Simulation ${simulationId} has been removed.`,
                variant: "default",
            });
        } catch (error) {
            console.error('Failed to delete simulation:', error);
            toast({
                title: "Error",
                description: "Failed to delete the simulation. Please try again.",
                variant: "destructive",
            });
        }
    };

    const formatDate = (dateString) => {
        try {
            return new Date(dateString).toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'short',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            });
        } catch {
            return 'Unknown';
        }
    };

    return (
        <div className="container mx-auto px-4 py-8 max-w-7xl">
            <div className="flex items-center justify-between mb-8">
                <div className="flex items-center gap-4">
                    <div>
                        <h1 className="text-3xl font-bold text-primary">Simulations Library</h1>
                        <p className="text-muted-foreground mt-1">
                            Manage and organize your simulations
                        </p>
                    </div>
                </div>

                <Button
                    onClick={handleCreateNew}
                    variant="outline"
                    className="flex items-center gap-2 border border-foreground text-foreground hover:bg-accent hover:border-transparent hover:text-accent-foreground transition-all"
                >
                    <Plus className="h-4 w-4" />
                    New Simulation
                </Button>


            </div>

            <div className="flex gap-6">
                <div className={`${selectedSimulation ? 'lg:w-2/3' : 'w-full'} transition-all duration-300`}>
                    <div className="flex items-center gap-4 mb-6">
                        <div className="relative flex-1">
                            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                            <Input
                                placeholder="Search simulations..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                className="pl-10"
                            />
                        </div>

                        <div className="flex items-center gap-2">
                            <Button
                                variant={viewMode === 'grid' ? 'default' : 'outline'}
                                size="sm"
                                onClick={() => setViewMode('grid')}
                            >
                                <Grid3X3 className="h-4 w-4" />
                            </Button>
                            <Button
                                variant={viewMode === 'list' ? 'default' : 'outline'}
                                size="sm"
                                onClick={() => setViewMode('list')}
                            >
                                <List className="h-4 w-4" />
                            </Button>
                        </div>
                    </div>

                    <div className="flex items-center justify-between mb-4">
                        <p className="text-sm text-muted-foreground">
                            {filteredSimulations.length} simulation{filteredSimulations.length !== 1 ? 's' : ''} found
                        </p>
                    </div>

                    {filteredSimulations.length === 0 ? (
                        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="text-center py-12">
                            <div className="max-w-md mx-auto">
                                <Activity className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
                                <h3 className="text-lg font-semibold mb-2">No Simulations Found</h3>
                                <p className="text-muted-foreground mb-4">
                                    {searchTerm
                                        ? "No simulations match your search criteria."
                                        : "You haven't created any simulations yet."}
                                </p>
                                {!searchTerm && (
                                    <Button onClick={handleCreateNew}>
                                        <Plus className="h-4 w-4 mr-2" />
                                        Create Your First Simulation
                                    </Button>
                                )}
                            </div>
                        </motion.div>
                    ) : (
                        <motion.div layout className="space-y-4">
                            {viewMode === 'grid' ? (
                                <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
                                    {filteredSimulations.map((simulation) => (
                                        <SimulationCard
                                            key={simulation.id}
                                            simulation={simulation}
                                            onView={handleViewSimulation}
                                            onEdit={handleEditSimulation}
                                            onDelete={handleDeleteSimulation}
                                        />
                                    ))}
                                </div>
                            ) : (
                                <div className="space-y-3">
                                    {filteredSimulations.map((simulation) => (
                                        <SimulationListItem
                                            key={simulation.id}
                                            simulation={simulation}
                                            onView={handleViewSimulation}
                                            onEdit={handleEditSimulation}
                                            onDelete={handleDeleteSimulation}
                                            formatDate={formatDate}
                                        />
                                    ))}
                                </div>
                            )}
                        </motion.div>
                    )}
                </div>

                {selectedSimulation && (
                    <div className="hidden lg:block lg:w-1/3">
                        {/*<SimulationPreview*/}
                        {/*    simulation={selectedSimulation}*/}
                        {/*    onClose={() => setSelectedSimulation(null)}*/}
                        {/*/>*/}
                    </div>
                )}
            </div>

            {selectedSimulation && (
                <div className="lg:hidden fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
                    <div className="bg-background rounded-lg max-w-md w-full max-h-[80vh] overflow-auto">
                        {/*<SimulationPreview*/}
                        {/*    simulation={selectedSimulation}*/}
                        {/*    onClose={() => setSelectedSimulation(null)}*/}
                        {/*/>*/}
                    </div>
                </div>
            )}

            {/* Modal de definição do objetivo */}
            <SimulationObjectiveModal
                open={open}
                setOpen={setOpen}
                onConfirm={() => {
                  setOpen(false);
                }}
            />

        </div>
    );
};

export default SimulationLibraryPage;
