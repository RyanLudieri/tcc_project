import React from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { Card, CardHeader, CardContent, CardFooter, CardTitle } from '@/components/ui/card';
import { Calendar, Activity, Repeat, Flag, Users, FileText } from 'lucide-react';
import { Edit, Trash2 } from 'lucide-react';

const ProcessCard = ({ process, simulationId, onDelete, formatDate }) => {
    const navigate = useNavigate();

    const handleEdit = () => {
        navigate(`/simulations/${simulationId}/processes/${process.id}/edit`);
    };

    return (
        <motion.div
            layout
            initial={{ opacity: 0, y: 15 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -15 }}
            whileHover={{ scale: 1.02 }}
            transition={{ duration: 0.2 }}
        >
            <Card className="h-full border border-gray-200 shadow-sm hover:shadow-md transition-all duration-200 rounded-xl">
                <CardHeader className="pb-2">
                    <CardTitle className="text-lg font-semibold truncate">{process.name}</CardTitle>
                    <div className="flex items-center gap-2 text-xs text-muted-foreground mt-1">
                        <Calendar className="h-3 w-3" />
                        <span>Last Modified: {process.lastModified ? formatDate(process.lastModified) : '—'}</span>
                    </div>
                </CardHeader>

                <CardContent className="pt-1 pb-2">
                    <div className="flex flex-wrap items-center gap-4 text-sm text-muted-foreground">
                        <div className="flex items-center gap-1">
                            <Activity className="h-4 w-4 text-primary" />
                            <span>{process.phases} Phases</span>
                        </div>
                        <div className="flex items-center gap-1">
                            <Repeat className="h-4 w-4 text-primary" />
                            <span>{process.iterations} Iterations</span>
                        </div>
                        <div className="flex items-center gap-1">
                            <Flag className="h-4 w-4 text-primary" />
                            <span>{process.milestones || 0} Milestones</span>
                        </div>
                        <div className="flex items-center gap-1">
                            <Users className="h-4 w-4 text-primary" />
                            <span>{process.roles} Roles</span>
                        </div>
                        <div className="flex items-center gap-1">
                            <Activity className="h-4 w-4 text-primary" />
                            <span>{process.activities} Activities</span>
                        </div>
                        <div className="flex items-center gap-1">
                            <FileText className="h-4 w-4 text-primary" />
                            <span>{process.tasks} Tasks</span>
                        </div>
                        <div className="flex items-center gap-1">
                            <FileText className="h-4 w-4 text-primary" />
                            <span>{process.artifacts} Artifacts</span>
                        </div>
                    </div>
                </CardContent>

                <CardFooter className="pt-2 flex gap-2">
                    <Button variant="default" size="sm" onClick={handleEdit} className="flex-1">
                        <Edit className="h-3 w-3 mr-1" /> Edit
                    </Button>
                    <Button
                        variant="destructive"
                        size="sm"
                        onClick={() => {
                            if (window.confirm(`Are you sure you want to delete process "${process.name}"?`)) {
                                onDelete(process.id, process.name);
                            }
                        }}
                    >
                        <Trash2 className="h-3 w-3" />
                    </Button>
                </CardFooter>
            </Card>
        </motion.div>
    );
};

export default ProcessCard;
