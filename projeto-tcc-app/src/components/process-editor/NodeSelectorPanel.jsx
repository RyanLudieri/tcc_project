import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import { ScrollArea } from '@/components/ui/scroll-area';
import { motion } from 'framer-motion';
import { initialNodeTypes } from '@/components/process-editor/nodeTypes';

// This component might not be directly used if AddNodeDialog handles type selection directly.
// However, keeping it for now as it defines the visual representation of node types.

const NodeSelectorPanel = ({ onNodeSelect, parentNodeType, hasRootNode }) => {

  const getFilteredNodeTypes = () => {
    if (!parentNodeType && !hasRootNode) { // Adding the first node, must be Process
      return initialNodeTypes.filter(nt => nt.name === 'Process');
    }
    if (!parentNodeType && hasRootNode) { // Root Process exists, cannot add another root
        return [];
    }
    if (parentNodeType) {
      // TaskDescriptor cannot have Activity as child
      if (parentNodeType === 'Task') {
        return initialNodeTypes.filter(nt => nt.name !== 'Activity' && nt.name !== 'Process');
      }
      // Process node can only have Phase as direct child
      if (parentNodeType === 'Process') {
         return initialNodeTypes.filter(nt => nt.name === 'Phase');
      }
      // General case: cannot add Process as a child
      return initialNodeTypes.filter(nt => nt.name !== 'Process');
    }
    return initialNodeTypes.filter(nt => nt.name !== 'Process'); // Default for children
  };

  const filteredNodeTypes = getFilteredNodeTypes();

  return (
    <Card className="h-full flex flex-col">
      <CardHeader>
        <CardTitle className="text-lg font-semibold text-secondary-foreground">Select Node Type</CardTitle>
      </CardHeader>
      <ScrollArea className="flex-1">
        <CardContent className="p-4 space-y-3">
          {filteredNodeTypes.map((nodeTypeInfo) => (
            <Tooltip key={nodeTypeInfo.name}>
              <TooltipTrigger asChild>
                <motion.button
                  onClick={() => onNodeSelect(nodeTypeInfo.name)}
                  className={`w-full p-3 rounded-md flex items-center space-x-3 transition-all duration-200 ease-in-out shadow-sm hover:shadow-md ${
                    nodeTypeInfo.name === 'Artifact' ? 'border border-muted-foreground hover:bg-muted/20 text-foreground' : (nodeTypeInfo.color || 'bg-gray-500') + ' text-primary-foreground hover:opacity-90'
                  }`}
                  whileHover={{ scale: 1.03 }}
                  whileTap={{ scale: 0.98 }}
                >
                  <nodeTypeInfo.icon className={`h-5 w-5`} />
                  <span className={`font-medium`}>{nodeTypeInfo.name}</span>
                </motion.button>
              </TooltipTrigger>
              <TooltipContent side="right">
                <p>{nodeTypeInfo.description}</p>
              </TooltipContent>
            </Tooltip>
          ))}
          {filteredNodeTypes.length === 0 && (
            <p className="text-sm text-muted-foreground text-center">
                {(!parentNodeType && hasRootNode) ? "A 'Process' root node already exists." : `No available node types for this parent '${parentNodeType}'.`}
            </p>
          )}
        </CardContent>
      </ScrollArea>
    </Card>
  );
};

export default NodeSelectorPanel;