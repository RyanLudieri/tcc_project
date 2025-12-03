import React from 'react';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import {
  Eye,
  Edit,
  Settings,
  Trash2,
  Calendar,
  Activity,
  Repeat,
  Flag,
  Users,
  FileText,
  Rocket
} from 'lucide-react';

const ProcessListItem = ({ process, onSetup, onEdit, onSimulate, onDelete, formatDate }) => (
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
            <strong className="truncate text-lg">{process.name}</strong>

            {process.description && (
                <p className="text-sm text-muted-foreground line-clamp-1 mt-1">
                  {process.description}
                </p>
            )}

            <div className="flex flex-wrap gap-3 text-xs text-muted-foreground mt-2">
            <span className="flex items-center gap-1">
              <Calendar className="h-3 w-3" />
              {process.lastModified ? formatDate(process.lastModified) : '—'}
            </span>

              <span className="flex items-center gap-1">
              <Activity className="h-3 w-3" /> {process.phases} Phases
            </span>

              <span className="flex items-center gap-1">
              <Repeat className="h-3 w-3" /> {process.iterations} Iterations
            </span>

              <span className="flex items-center gap-1">
              <Flag className="h-3 w-3" /> {process.milestones || 0} Milestones
            </span>

              <span className="flex items-center gap-1">
              <Users className="h-3 w-3" /> {process.roles} Roles
            </span>

              <span className="flex items-center gap-1">
              <Activity className="h-3 w-3" /> {process.activities} Activities
            </span>

              <span className="flex items-center gap-1">
              <FileText className="h-3 w-3" /> {process.tasks} Tasks
            </span>

              <span className="flex items-center gap-1">
              <FileText className="h-3 w-3" /> {process.artifacts} Artifacts
            </span>
            </div>
          </div>

          {/* AÇÕES */}
          <div className="flex gap-2 ml-4 flex-shrink-0">

            <Button
                variant="outline"
                size="sm"
                onClick={() => onView(process.id)}
            >
              <Eye className="h-3 w-3 mr-1" />
              View
            </Button>

            <Button
                variant="outline"
                size="sm"
                onClick={() => onSetup(process.id)}
            >
              <Settings className="h-3 w-3 mr-1" />
              Setup
            </Button>

            <Button
                variant="default"
                size="sm"
                onClick={() => onSimulate(process.id)}
                className="relative overflow-hidden shimmer-btn"
            >
              <Rocket className="h-3 w-3 mr-1" />
              Simulate
            </Button>

            <Button
                variant="destructive"
                size="sm"
                onClick={() => {

                    onDelete(process.id, process.name);

                }}
            >
              <Trash2 className="h-3 w-3" />
            </Button>

          </div>
        </CardContent>
      </Card>
    </motion.div>
);

export default ProcessListItem;
