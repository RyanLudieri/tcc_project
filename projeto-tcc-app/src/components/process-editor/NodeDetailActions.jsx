import React from 'react';
import { Button } from '@/components/ui/button';
import { CardHeader, CardTitle } from '@/components/ui/card';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import { Trash2 } from 'lucide-react';

const NodeDetailActions = ({ node, onDeleteNode }) => {
  if (!node) return null;

  return (
    <CardHeader className="flex flex-row items-center justify-between py-3 px-4 border-b">
      <CardTitle className="text-lg font-semibold text-secondary-foreground truncate" title={node.presentationName}>
        Details: {node.presentationName}
      </CardTitle>
      <Tooltip>
        <TooltipTrigger asChild>
          <Button 
            variant="ghost" 
            size="icon" 
            className="text-destructive hover:bg-destructive/10" 
            onClick={() => onDeleteNode(node.id)} 
            disabled={node.type === 'Process'}
          >
            <Trash2 className="h-4 w-4" />
          </Button>
        </TooltipTrigger>
        <TooltipContent>
          <p>{node.type === 'Process' ? "Cannot delete root Process node" : "Delete Node & Children"}</p>
        </TooltipContent>
      </Tooltip>
    </CardHeader>
  );
};

export default NodeDetailActions;