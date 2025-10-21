import React from 'react';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Edit, Trash2, MoreVertical, Eye, Calendar } from 'lucide-react';
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';

const SimulationListItem = ({ simulation, onView, onEdit, onDelete, formatDate }) => {
    return (
        <motion.div
            layout
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            exit={{ opacity: 0, x: 20 }}
            transition={{ duration: 0.2 }}
        >
            <Card className="hover:shadow-md transition-shadow duration-200">
                <CardContent className="flex justify-between items-center p-4">
                    <div className="flex-1 min-w-0">
                        <h3 className="font-semibold text-lg truncate">{simulation.objective}</h3>
                        {simulation.description && (
                            <p className="text-sm text-muted-foreground line-clamp-1 mt-1">
                                {simulation.description}
                            </p>
                        )}
                        <div className="flex items-center gap-4 mt-2 text-xs text-muted-foreground">
                            {simulation.lastModified && (
                                <span className="flex items-center gap-1">
                  <Calendar className="h-3 w-3" />
                  Last Modified: {formatDate(simulation.lastModified)}
                </span>
                            )}
                            <span>Simulation ID: {simulation.id}</span>
                        </div>
                    </div>

                    <div className="flex items-center gap-2 ml-4">
                        {/* Botão View */}
                        <Button variant="outline" size="sm" onClick={() => onView(simulation)}>
                            <Eye className="h-3 w-3 mr-1" />
                            View
                        </Button>

                        {/* Dropdown 3 pontinhos */}
                        <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                                <Button variant="ghost" size="icon" className="h-8 w-8">
                                    <MoreVertical className="h-4 w-4" />
                                </Button>
                            </DropdownMenuTrigger>

                            <DropdownMenuContent align="end">
                                <DropdownMenuItem onClick={() => onEdit(simulation.id, 0)}>
                                    <Edit className="h-4 w-4 mr-2" />
                                    Edit Simulation
                                </DropdownMenuItem>
                                <DropdownMenuItem
                                    className="text-destructive focus:text-destructive"
                                    onClick={() => onDelete(simulation.id)}
                                >
                                    <Trash2 className="h-4 w-4 mr-2" />
                                    Delete Simulation
                                </DropdownMenuItem>
                            </DropdownMenuContent>
                        </DropdownMenu>
                    </div>
                </CardContent>
            </Card>
        </motion.div>
    );
};

export default SimulationListItem;
