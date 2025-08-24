import React from 'react';
import { motion } from 'framer-motion';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Eye, Edit, Trash2, Calendar, Users, Activity } from 'lucide-react';

const ProcessCard = ({ process, onView, onEdit, onDelete, formatDate }) => (
  <motion.div
    layout
    initial={{ opacity: 0, y: 20 }}
    animate={{ opacity: 1, y: 0 }}
    exit={{ opacity: 0, y: -20 }}
    whileHover={{ y: -5 }}
    transition={{ duration: 0.2 }}
  >
    <Card className="h-full hover:shadow-lg transition-shadow duration-200 border-2 hover:border-primary/20">
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div className="flex-1 min-w-0">
            <CardTitle className="text-lg font-bold truncate">{process.name}</CardTitle>
            <CardDescription className="text-sm mt-1 line-clamp-2">
              {process.description}
            </CardDescription>
          </div>
        </div>
      </CardHeader>
      
      <CardContent className="pt-0 pb-3">
        <div className="grid grid-cols-2 gap-2 text-sm">
          <div className="flex items-center gap-1">
            <Activity className="h-3 w-3 text-muted-foreground" />
            <span className="text-muted-foreground">Phases:</span>
            <Badge variant="secondary" className="text-xs">{process.phases}</Badge>
          </div>
          <div className="flex items-center gap-1">
            <Users className="h-3 w-3 text-muted-foreground" />
            <span className="text-muted-foreground">Roles:</span>
            <Badge variant="secondary" className="text-xs">{process.roles}</Badge>
          </div>
          <div className="flex items-center gap-1 col-span-2">
            <Calendar className="h-3 w-3 text-muted-foreground" />
            <span className="text-muted-foreground text-xs">Modified: {formatDate(process.lastModified)}</span>
          </div>
        </div>
        
        <div className="mt-3 flex flex-wrap gap-1">
          <Badge variant="outline" className="text-xs">
            {process.activities} Activities
          </Badge>
          <Badge variant="outline" className="text-xs">
            {process.tasks} Tasks
          </Badge>
          <Badge variant="outline" className="text-xs">
            {process.artifacts} Artifacts
          </Badge>
        </div>
      </CardContent>
      
      <CardFooter className="pt-0 flex gap-2">
        <Button
          variant="outline"
          size="sm"
          onClick={() => onView(process)}
          className="flex-1"
        >
          <Eye className="h-3 w-3 mr-1" />
          Preview
        </Button>
        <Button
          variant="default"
          size="sm"
          onClick={() => onEdit(process.id)}
          className="flex-1"
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
      </CardFooter>
    </Card>
  </motion.div>
);

export default ProcessCard;