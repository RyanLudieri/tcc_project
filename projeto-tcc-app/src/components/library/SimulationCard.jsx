import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardFooter, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Eye, Edit, Trash2, MoreVertical, Activity, Save, X, Calendar } from 'lucide-react';
import { Input } from '@/components/ui/input';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { useNavigate } from 'react-router-dom';

const SimulationCard = ({ simulation, onEditObjective, onDelete }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [newObjective, setNewObjective] = useState(simulation.objective || '');
  const navigate = useNavigate();

  const handleSave = () => {
    onEditObjective(simulation.id, newObjective);
    setIsEditing(false);
  };

  const statusColor = {
    Empty: 'secondary',
    Setup: 'warning',
    Simulated: 'success',
    Failed: 'destructive',
  }[simulation.status] || 'default';

  return (
      <motion.div
          layout
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -20 }}
          whileHover={{ y: -5 }}
          transition={{ duration: 0.2 }}
      >
        <Card className="h-full hover:shadow-lg transition-shadow duration-200 border-2 hover:border-primary/20">
          <CardHeader className="flex flex-row items-start justify-between">
            <div className="flex flex-col flex-1">
              {isEditing ? (
                  <div className="flex gap-2 items-center w-full">
                    <Input
                        value={newObjective}
                        onChange={(e) => setNewObjective(e.target.value)}
                        placeholder="Edit objective..."
                        className="flex-1"
                    />
                    <Button size="sm" variant="ghost" onClick={handleSave}>
                      <Save className="h-4 w-4" />
                    </Button>
                    <Button size="sm" variant="ghost" onClick={() => setIsEditing(false)}>
                      <X className="h-4 w-4" />
                    </Button>
                  </div>
              ) : (
                  <>
                    <CardTitle className="text-lg font-bold truncate">
                      {simulation.objective || 'Untitled Simulation'}
                    </CardTitle>
                  </>
              )}

              <CardDescription className="text-xs text-muted-foreground mt-1">
                <span>Simulation ID: {simulation.id}</span>
              </CardDescription>

              {simulation.status && (
                  <div className="mt-2.5">
                    <Badge variant={statusColor} className="mt-1 text-xs w-max">
                      {simulation.status}
                    </Badge>
                  </div>
              )}
            </div>

            {/* Options menu */}
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="icon" className="h-8 w-8">
                  <MoreVertical className="h-4 w-4" />
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                <DropdownMenuItem onClick={() => setIsEditing(true)}>
                  <Edit className="h-4 w-4 mr-2" /> Edit Objective
                </DropdownMenuItem>
                <DropdownMenuItem
                    className="text-destructive focus:text-destructive"
                    onClick={() => onDelete(simulation.id)}
                >
                  <Trash2 className="h-4 w-4 mr-2" /> Delete Simulation
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </CardHeader>

          <CardContent className="pt-0">
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <Activity className="h-4 w-4" />
              <span>Processes:</span>
              <Badge variant="secondary">{simulation.processCount || 0}</Badge>
            </div>

            {simulation.lastModified && (
                <div className="flex items-center gap-1 mt-2 text-xs text-muted-foreground">
                  <Calendar className="h-3 w-3" />
                  <span>
                Last Modified: {new Date(simulation.lastModified).toLocaleDateString('en-US', {
                    year: 'numeric',
                    month: 'short',
                    day: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit',
                    hour12: true,
                  })}
              </span>
                </div>
            )}
          </CardContent>

          <CardFooter className="pt-0">
            {/* Apenas mostra o botão View se não estiver editando */}
            {!isEditing && (
                <Button
                    variant="outline"
                    size="sm"
                    onClick={() => navigate(`/simulations/${simulation.id}`)}
                    className="w-full"
                >
                  <Eye className="h-3 w-3 mr-1" />
                  View
                </Button>
            )}
          </CardFooter>
        </Card>
      </motion.div>
  );
};

export default SimulationCard;
