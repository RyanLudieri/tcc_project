import React from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { Edit, Activity } from 'lucide-react';

const ProcessPreview = ({ process, onClose }) => {
  const navigate = useNavigate();

  const handleEditProcess = (processId) => {
    navigate(`/processes/${processId}/edit`);
  };

  return (
    <motion.div
      initial={{ opacity: 0, x: 20 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: 20 }}
      className="bg-background border rounded-lg p-6 h-full overflow-auto"
    >
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-xl font-bold">Process Preview</h3>
        <Button
          variant="outline"
          size="sm"
          onClick={onClose}
        >
          Close
        </Button>
      </div>
      
      <div className="space-y-4">
        <div>
          <h4 className="font-semibold text-lg">{process.name}</h4>
          <p className="text-muted-foreground mt-1">{process.description}</p>
        </div>
        
        <Separator />
        
        <div className="grid grid-cols-2 gap-4">
          <div>
            <h5 className="font-medium mb-2">Process Statistics</h5>
            <div className="space-y-1 text-sm">
              <div className="flex justify-between">
                <span>Total Nodes:</span>
                <Badge variant="secondary">{process.nodeCount}</Badge>
              </div>
              <div className="flex justify-between">
                <span>Phases:</span>
                <Badge variant="secondary">{process.phases}</Badge>
              </div>
              <div className="flex justify-between">
                <span>Activities:</span>
                <Badge variant="secondary">{process.activities}</Badge>
              </div>
              <div className="flex justify-between">
                <span>Tasks:</span>
                <Badge variant="secondary">{process.tasks}</Badge>
              </div>
              <div className="flex justify-between">
                <span>Artifacts:</span>
                <Badge variant="secondary">{process.artifacts}</Badge>
              </div>
              <div className="flex justify-between">
                <span>Roles:</span>
                <Badge variant="secondary">{process.roles}</Badge>
              </div>
            </div>
          </div>
          
          <div>
            <h5 className="font-medium mb-2">Process Structure</h5>
            <div className="space-y-1 text-sm max-h-48 overflow-auto">
              {process.nodes
                .filter(node => node.type !== 'Artifact' && node.type !== 'Role')
                .sort((a, b) => {
                  if (!a.index) return 1;
                  if (!b.index) return -1;
                  return a.index.localeCompare(b.index, undefined, { numeric: true });
                })
                .map((node, index) => (
                  <div key={index} className="flex items-center gap-2">
                    <Badge variant="outline" className="text-xs">
                      {node.type}
                    </Badge>
                    <span className="truncate">{node.presentationName}</span>
                  </div>
                ))}
            </div>
          </div>
        </div>
        
        <Separator />
        
        <div className="flex gap-2">
          <Button
            onClick={() => handleEditProcess(process.id)}
            className="flex-1"
          >
            <Edit className="h-4 w-4 mr-2" />
            Edit Process
          </Button>
          <Button
            variant="outline"
            onClick={() => navigate(`/processes/${process.id}/simulate`)}
            className="flex-1"
          >
            <Activity className="h-4 w-4 mr-2" />
            Simulate
          </Button>
        </div>
      </div>
    </motion.div>
  );
};

export default ProcessPreview;