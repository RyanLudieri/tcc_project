import React from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Rocket } from 'lucide-react';
import NodeDetailForm from './NodeDetailForm';
import NodeDetailActions from './NodeDetailActions';
import { ScrollArea } from "@/components/ui/scroll-area";

const NodeDetailPanel = ({ node, allNodes, onUpdateNode, onDeleteNode, onAddChildNode }) => {
  if (!node) {
    return (
      <Card className="h-full flex items-center justify-center">
        <CardContent className="text-center text-muted-foreground p-6">
          <Rocket className="mx-auto h-12 w-12 text-primary mb-4" />
          <CardTitle className="text-xl mb-2">Process Editor</CardTitle>
          <CardDescription>
            Select a node in the tree to view or edit its details, or add a new 'Process' root node to begin.
          </CardDescription>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="h-full flex flex-col">
      <NodeDetailActions 
        node={node} 
        onDeleteNode={onDeleteNode} 
      />
      <ScrollArea className="flex-1">
        <CardContent className="p-4 space-y-4">
          <NodeDetailForm
            node={node}
            allNodes={allNodes}
            onUpdateNode={onUpdateNode}
            onAddChildNode={onAddChildNode}
          />
        </CardContent>
      </ScrollArea>
    </Card>
  );
};

export default NodeDetailPanel;