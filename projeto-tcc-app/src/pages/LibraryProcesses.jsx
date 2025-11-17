import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useToast } from '@/components/ui/use-toast';
import ProcessCard from '@/components/library/ProcessCard.jsx';
import ProcessListItem from '@/components/library/ProcessListItem.jsx';
import { Search, ArrowLeft, Grid3X3, List, Activity, Plus } from 'lucide-react';
import { API_BASE_URL } from '@/config/api';

const LibraryProcesses = () => {
    const { simulationId } = useParams();
    const [simulation, setSimulation] = useState(null);
    const [processes, setProcesses] = useState([]);
    const [viewMode, setViewMode] = useState('grid');
    const [searchTerm, setSearchTerm] = useState('');
    const navigate = useNavigate();
    const { toast } = useToast();

    useEffect(() => {
        loadSimulation();
    }, [simulationId]);

    const loadSimulation = async () => {
        try {
            const res = await fetch(`${API_BASE_URL}/simulations/${simulationId}`);
            if (!res.ok) throw new Error('Failed to fetch simulation');
            const data = await res.json();
            setSimulation(data);
            setProcesses(data.processes || []);
        } catch (error) {
            console.error(error);
            toast({
                title: 'Error',
                description: 'Failed to load simulation or its processes.',
                variant: 'destructive',
            });
        }
    };

    const handleDeleteProcess = async (id, name) => {
        if (!confirm(`Are you sure you want to delete process "${name}"?`)) return;

        try {
            const res = await fetch(`${API_BASE_URL}/simulations/${simulationId}/processes/${id}`, {
                method: 'DELETE',
            });
            if (!res.ok) throw new Error('Failed to delete process');

            setProcesses((prev) => prev.filter((p) => p.id !== id));

            toast({
                title: 'Deleted',
                description: `Process "${name}" was successfully removed.`,
                variant: 'success',
            });
        } catch (error) {
            console.error(error);
            toast({
                title: 'Error',
                description: 'Failed to delete process.',
                variant: 'destructive',
            });
        }
    };

    const filteredProcesses = processes.filter(p =>
        p.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        p.description?.toLowerCase()?.includes(searchTerm.toLowerCase())
    );

    const handleAddProcess = () => {
        navigate(`/simulations/${simulationId}/processes/new/edit`);
    };

    return (
        <div className="container mx-auto px-4 py-8 max-w-7xl">
            {/* Header */}
            <div className="flex flex-col md:flex-row items-start justify-between mb-8 gap-4">
                <div className="flex items-start gap-4">
                    <Link to="/simulations">
                        <Button variant="ghost" size="icon" className="h-8 w-8 hover:bg-primary/10 transition-all">
                            <ArrowLeft className="h-5 w-5 text-primary" />
                        </Button>
                    </Link>
                    <div className="flex flex-col justify-start">
                        <h1 className="text-3xl font-bold text-primary truncate">
                            {simulation?.objective || 'Simulation'}
                        </h1>
                        <p className="text-muted-foreground mt-1">
                            Viewing processes of simulation with ID: {simulationId}
                        </p>
                    </div>
                </div>

                <Button
                    onClick={handleAddProcess}
                    className="flex items-center text-primary gap-2 bg-transparent border hover:bg-primary hover:text-secondary transition-all"
                >
                    <Plus className="h-4 w-4" />
                    Add New Process
                </Button>
            </div>

            {/* Search + View Mode */}
            <div className="mb-6 flex flex-col md:flex-row items-start md:items-center gap-4">
                <div className="relative flex-1 w-full md:w-auto">
                    <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                    <Input
                        placeholder="Search processes..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="pl-10 w-full"
                    />
                </div>
                <div className="flex items-center gap-2">
                    <Button variant={viewMode === 'grid' ? 'default' : 'outline'} size="sm" onClick={() => setViewMode('grid')}>
                        <Grid3X3 className="h-4 w-4" />
                    </Button>
                    <Button variant={viewMode === 'list' ? 'default' : 'outline'} size="sm" onClick={() => setViewMode('list')}>
                        <List className="h-4 w-4" />
                    </Button>
                </div>
            </div>

            <div className="flex items-center justify-between mb-4">
                <p className="text-sm text-muted-foreground">
                    {filteredProcesses.length} process{filteredProcesses.length !== 1 ? 'es' : ''} found
                </p>
            </div>

            {/* Processes */}
            {filteredProcesses.length === 0 ? (
                <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="text-center py-12">
                    <div className="max-w-md mx-auto">
                        <Activity className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
                        <h3 className="text-lg font-semibold mb-2">No Processes Found</h3>
                        <p className="text-muted-foreground mb-4">
                            {searchTerm
                                ? "No processes match your search criteria."
                                : "This simulation has no processes yet."}
                        </p>
                        {!searchTerm && (
                            <Button onClick={handleAddProcess}>
                                <Plus className="h-4 w-4 mr-2" />
                                Create Your First Process For This Simulation
                            </Button>
                        )}
                    </div>
                </motion.div>
            ) : (
                <motion.div
                    layout
                    className={viewMode === 'grid' ? 'grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6' : 'space-y-3'}
                >
                    {filteredProcesses.map((process) =>
                        viewMode === 'grid' ? (
                            <ProcessCard
                                key={process.id}
                                process={process}
                                simulationId={simulationId}
                                onDelete={handleDeleteProcess}
                                formatDate={(date) =>
                                    new Date(date).toLocaleDateString('en-US', {
                                        month: 'short',
                                        day: '2-digit',
                                        year: 'numeric',
                                        hour: '2-digit',
                                        minute: '2-digit',
                                    })
                                }
                            />
                        ) : (
                            <ProcessListItem
                                key={process.id}
                                process={process}
                                onView={(p) => navigate(`/simulations/${simulationId}/processes/${p.id}/edit`)}
                                onEdit={(id) => navigate(`/simulations/${simulationId}/processes/${id}/edit`)}
                                onSetup={(id) => navigate(`/simulations/${simulationId}/processes/${id}/setup`)}
                                onSimulate={(id) => navigate(`/simulations/${simulationId}/processes/${id}/results`)}
                                onDelete={handleDeleteProcess}
                                formatDate={(date) =>
                                    new Date(date).toLocaleDateString('en-US', {
                                        month: 'short',
                                        day: '2-digit',
                                        year: 'numeric',
                                        hour: '2-digit',
                                        minute: '2-digit',
                                    })
                                }
                            />
                        )
                    )}
                </motion.div>
            )}
        </div>
    );
};

export default LibraryProcesses;
