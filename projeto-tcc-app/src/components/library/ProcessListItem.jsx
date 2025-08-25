import React from 'react';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Eye, Edit, Trash2 } from 'lucide-react';

const ProcessListItem = ({ process, onView, onEdit, onDelete, formatDate }) => (
  <motion.div
    layout
    initial={{ opacity: 0, x: -20 }}
    animate={{ opacity: 1, x: 0 }}
    exit={{ opacity: 0, x: 20 }}
    transition={{ duration: 0.2 }}
  >
    <Card className="hover:shadow-md transition-shadow duration-200">
      <CardContent className="p-4">
        <div className="flex items-center justify-between">
          <div className="flex-1 min-w-0">
            <div className="flex items-center gap-3">
              <div className="flex-1">
                <h3 className="font-semibold text-lg truncate">{process.name}</h3>
                <p className="text-sm text-muted-foreground line-clamp-1 mt-1">
                  {process.description}
                </p>
                <div className="flex items-center gap-4 mt-2 text-xs text-muted-foreground">
                  <span>Modified: {formatDate(process.lastModified)}</span>
                  <span>{process.phases} Phases</span>
                  <span>{process.activities} Activities</span>
                  <span>{process.roles} Roles</span>
                </div>
              </div>
            </div>
          </div>
          
          <div className="flex items-center gap-2 ml-4">
            <Button
              variant="outline"
              size="sm"
              onClick={() => onView(process)}
            >
              <Eye className="h-3 w-3 mr-1" />
              Preview
            </Button>
            <Button
              variant="default"
              size="sm"
              onClick={() => onEdit(process.id)}
            >
              <Edit className="h-3 w-3 mr-1" />
              Edit
            </Button>
            <Button
              variant="destructive"
              size="sm"
              onClick={() => onDelete(process.id, process.name)}
            >
              <Trash2 className="h-3 w-3" />
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  </motion.div>
);

export default ProcessListItem;