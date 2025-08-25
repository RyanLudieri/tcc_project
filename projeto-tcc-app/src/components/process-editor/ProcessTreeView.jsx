import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import { Button } from '@/components/ui/button';
import { ScrollArea } from '@/components/ui/scroll-area';
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger } from "@/components/ui/alert-dialog";
import { PlusCircle, Rocket, Trash2 } from 'lucide-react';
import { SortableNodeItem } from '@/components/process-editor/tree/SortableNodeItem';
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { AnimatePresence } from 'framer-motion';

const ProcessTreeViewHeader = ({ onAddRootNode, hasRootNode, onDeleteAllNodes, nodesCount }) => (
  <CardHeader className="flex flex-row items-center justify-between py-3 px-4 border-b bg-gray-50 dark:bg-gray-700/50 sticky top-0 z-10">
    <CardTitle className="text-lg font-semibold text-gray-800 dark:text-gray-100">Process Model</CardTitle>
    <div className="flex space-x-2">
      <Tooltip>
        <TooltipTrigger asChild>
          <Button variant="outline" size="sm" onClick={onAddRootNode} disabled={hasRootNode}>
            <PlusCircle className="mr-2 h-4 w-4" /> Add Process
          </Button>
        </TooltipTrigger>
        <TooltipContent>
          <p>{hasRootNode ? "A root 'Process' node already exists." : "Add the main 'Process' node"}</p>
        </TooltipContent>
      </Tooltip>
      
      <AlertDialog>
        <Tooltip>
          <TooltipTrigger asChild>
            <AlertDialogTrigger asChild>
              <Button variant="destructiveOutline" size="sm" disabled={nodesCount === 0}>
                <Trash2 className="mr-2 h-4 w-4" /> Delete All
              </Button>
            </AlertDialogTrigger>
          </TooltipTrigger>
          <TooltipContent>
            <p>{nodesCount > 0 ? "Delete all nodes in the tree" : "No nodes to delete"}</p>
          </TooltipContent>
        </Tooltip>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Are you absolutely sure?</AlertDialogTitle>
            <AlertDialogDescription>
              This action cannot be undone. This will permanently delete all nodes
              in your process model.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction onClick={onDeleteAllNodes} className="bg-destructive hover:bg-destructive/90 text-destructive-foreground">
              Yes, delete all
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  </CardHeader>
);

const EmptyState = ({ onAddRootNode }) => (
  <div className="flex flex-col items-center justify-center h-full min-h-[200px] text-center p-4">
    <Rocket className="mx-auto h-12 w-12 text-primary mb-4" />
    <p className="text-muted-foreground mb-2">Your process model is empty.</p>
    <Button variant="default" size="sm" onClick={onAddRootNode}>
      <PlusCircle className="mr-2 h-4 w-4" /> Add 'Process' Root Node
    </Button>
  </div>
);

const RootNodeExistsEmptyState = () => (
  <div className="flex flex-col items-center justify-center h-full min-h-[200px] text-center p-4">
    <p className="text-muted-foreground">The 'Process' root node exists. Add child nodes to build your model.</p>
  </div>
);

const RootDropIndicator = () => (
  <div className="my-2 mx-4 h-10 border-2 border-dashed border-blue-400 rounded-md flex items-center justify-center text-blue-500 text-xs">
    Drop here to make root (if not Process type)
  </div>
);

const TableHeader = () => (
  <div className="sticky top-0 z-10 bg-gray-100 dark:bg-gray-700 px-4 py-2 border-b dark:border-gray-600">
    <div className="flex items-center text-xs font-medium text-muted-foreground uppercase">
      <div className="w-[45%] pl-10">Presentation Name</div>
      <div className="w-[10%] text-center">Index</div>
      <div className="w-[15%] text-center">Predecessors</div>
      <div className="w-[15%] text-center">Model Info</div>
      <div className="w-[15%] text-center pr-8">Type</div>
    </div>
  </div>
);


const ProcessTreeView = ({ nodes, onNodeClick, selectedNodeId, onAddRootNode, hasRootNode, getChildNodes, dropTargetInfo, onDeleteAllNodes }) => {
  const rootNodes = nodes.filter(node => !node.parentId);
  
  return (
    <Card className="h-full flex flex-col relative overflow-hidden bg-white dark:bg-gray-800 shadow-none border-0">
      <ProcessTreeViewHeader onAddRootNode={onAddRootNode} hasRootNode={hasRootNode} onDeleteAllNodes={onDeleteAllNodes} nodesCount={nodes.length} />
      <TableHeader />
      <ScrollArea className="flex-1 bg-white dark:bg-gray-800/80">
         <CardContent className="p-0">
          {nodes.length === 0 && !hasRootNode && <EmptyState onAddRootNode={onAddRootNode} />}
          {nodes.length === 0 && hasRootNode && <RootNodeExistsEmptyState />}

           {dropTargetInfo && dropTargetInfo.parentId === null && dropTargetInfo.position === 'root' && (
            <RootDropIndicator />
          )}

          <SortableContext items={rootNodes.map(n => n.id)} strategy={verticalListSortingStrategy}>
            <AnimatePresence>
              {rootNodes.map(node => (
                <SortableNodeItem
                  key={node.id}
                  node={node}
                  allNodes={nodes}
                  onNodeClick={onNodeClick}
                  selectedNodeId={selectedNodeId}
                  getChildNodes={getChildNodes}
                  dropTargetInfo={dropTargetInfo}
                  level={0}
                />
              ))}
            </AnimatePresence>
          </SortableContext>
         </CardContent>
      </ScrollArea>
    </Card>
  );
};

export default ProcessTreeView;