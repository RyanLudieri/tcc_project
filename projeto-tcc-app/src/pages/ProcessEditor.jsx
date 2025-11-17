import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import { Rocket } from 'lucide-react';
import {
  DndContext,
  PointerSensor,
  KeyboardSensor,
  useSensor,
  useSensors,
  DragOverlay,
  closestCorners,
} from '@dnd-kit/core';
import { sortableKeyboardCoordinates } from '@dnd-kit/sortable';
import { motion } from 'framer-motion';
import NodeDetailPanel from '@/components/process-editor/NodeDetailPanel';
import ProcessTreeView from '@/components/process-editor/ProcessTreeView';
import { SortableNodeItemContent } from '@/components/process-editor/tree/SortableNodeItemContent';
import AddNodeDialog from '@/components/process-editor/AddNodeDialog';
import { useToast } from "@/components/ui/use-toast";
import { useProcessNodes } from '@/hooks/useProcessNodes';
import { transformNodesForBackend } from '@/lib/nodeUtils';
import { API_BASE_URL } from "@/config/api";

const ProcessEditor = () => {
  const { simulationId, processId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const isNewProcess = location.pathname.includes("/new");
  const { toast } = useToast();

  const {
    nodes,
    addNode,
    updateNode,
    deleteNode,
    deleteAllNodes,
    findNode,
    getChildNodes,
    activeDragItem,
    dropTargetInfo,
    handleDragStartLogic,
    handleDragOverLogic,
    handleDragEndLogic,
    getAllowedChildTypes,
  } = useProcessNodes(processId);

  const [selectedNodeId, setSelectedNodeId] = useState(null);
  const [isAddNodeDialogOpen, setIsAddNodeDialogOpen] = useState(false);
  const [parentNodeForDialog, setParentNodeForDialog] = useState(null);
  const [isSaving, setIsSaving] = useState(false);

  const sensors = useSensors(
      useSensor(PointerSensor, { activationConstraint: { distance: 8 } }),
      useSensor(KeyboardSensor, { coordinateGetter: sortableKeyboardCoordinates })
  );

  const hasRootNode = nodes.some(node => !node.parentId && node.type === 'Process');

  useEffect(() => {
    if (selectedNodeId) {
      const nodeExists = nodes.some(node => node.id === selectedNodeId);
      if (!nodeExists) setSelectedNodeId(null);
    }
  }, [nodes, selectedNodeId]);

  const handleUINodeAdd = (nodeData, parentId = null) => {
    const result = addNode(nodeData, parentId);
    if (result.success && result.newNode) {
      setSelectedNodeId(result.newNode.id);
      setIsAddNodeDialogOpen(false);
      toast({
        title: "Node Added",
        description: `${result.newNode.presentationName} (${result.newNode.type || 'No Type'}) was successfully added.`,
      });
    } else if (result.error) {
      toast({ title: "Error", description: result.error, variant: "destructive" });
    }
  };

  const openAddChildDialog = (parentId) => {
    const parentNode = findNode(parentId);
    const allowed = getAllowedChildTypes(parentNode?.type);
    if (!allowed || allowed.length === 0) {
      toast({
        title: "Not allowed",
        description: `The type "${parentNode?.type}" doesn't allow children.`,
        variant: "destructive",
      });
      return;
    }
    setParentNodeForDialog(parentNode);
    setIsAddNodeDialogOpen(true);
  };

  const handleNodeClick = (nodeId) => setSelectedNodeId(nodeId);

  const handleUINodeUpdate = (id, updates) => {
    updateNode(id, updates);
    toast({ title: "Node Updated", description: `Node details saved.` });
  };

  const handleUINodeDelete = useCallback(
      (id) => {
        const deletedNodeName = findNode(id)?.presentationName || "Node";
        deleteNode(id);
        if (
            selectedNodeId === id ||
            (selectedNodeId && nodes.find(n => n.id === selectedNodeId)?.parentId === id)
        ) {
          setSelectedNodeId(null);
        }
        toast({
          title: "Node Deleted",
          description: `${deletedNodeName} and its children were deleted.`,
          variant: "destructive",
        });
      },
      [selectedNodeId, deleteNode, findNode, nodes, toast]
  );

  const handleUIDeleteAllNodes = useCallback(() => {
    deleteAllNodes();
    setSelectedNodeId(null);
    toast({
      title: "All Nodes Deleted",
      description: "The entire process model has been cleared.",
      variant: "destructive",
    });
  }, [deleteAllNodes, toast]);

  const selectedNode = findNode(selectedNodeId) || null;

  const onDragStart = (event) => handleDragStartLogic(event.active.id);
  const onDragOver = (event) => {
    const { active, over, draggingRect } = event;
    handleDragOverLogic(active.id, over?.id, draggingRect, over?.rect);
  };
  const onDragEnd = (event) => {
    const { active } = event;
    const result = handleDragEndLogic(active.id, dropTargetInfo);
    if (result.success) {
      toast({ title: "Node Moved", description: result.message });
    } else if (result.error) {
      toast({ title: "Invalid Move", description: result.error, variant: "destructive" });
    }
  };

  const handleSaveAndSimulate = async () => {
    setIsSaving(true);
    const payload = transformNodesForBackend(nodes);

    if (!payload) {
      toast({
        title: "Error Preparing Data",
        description: "Ensure a 'Process' root node exists.",
        variant: "destructive",
      });
      setIsSaving(false);
      return;
    }

    if (!isNewProcess) {
      try {
        const response = await fetch(`${API_BASE_URL}/process/${processId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload),
        });

        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(errorText || "Failed to update process");
        }

        const updatedProcess = await response.json();

        toast({
          title: "Process Updated!",
          description: "Your changes were saved successfully.",
        });

        navigate(`/simulations/${simulationId}/processes/${updatedProcess.id}/simulate`);

      } catch (error) {
        console.error(error);
        toast({
          title: "Update Failed",
          description: error.message,
          variant: "destructive",
        });
      } finally {
        setIsSaving(false);
      }

      return;

    } else {
      try {
          const response = await fetch(`${API_BASE_URL}/process`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload),
          });

          if (!response.ok) throw new Error(await response.text());

          const savedProcess = await response.json();

          if (simulationId) {
            await fetch(`${API_BASE_URL}/simulations/${simulationId}/delivery-process/${savedProcess.id}`, {
              method: 'PATCH',
              headers: { 'Content-Type': 'application/json' },
            });
          }

          toast({
            title: "Process Saved!",
            description: "Your process model has been successfully saved.",
          });

          navigate(`/simulations/${simulationId}/processes/${savedProcess.id}/simulate`);
        } catch (error) {
        console.error(error);
        toast({
          title: "Save Failed",
          description: error.message,
          variant: "destructive",
        });
      } finally {
        setIsSaving(false);
      }
    }


  };

  const allowedTypesForDialog = parentNodeForDialog
      ? getAllowedChildTypes(parentNodeForDialog.type)
      : ["Process"];

  return (
      <DndContext
          sensors={sensors}
          collisionDetection={closestCorners}
          onDragStart={onDragStart}
          onDragOver={onDragOver}
          onDragEnd={onDragEnd}
      >
        <div className="flex-1 grid grid-cols-1 md:grid-cols-[1fr_350px] gap-6 p-6 bg-gray-100 dark:bg-gray-900 h-[calc(100vh-4rem)]">
          <motion.div
              initial={{ scale: 0.95, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              transition={{ duration: 0.4, delay: 0.1, ease: "circOut" }}
              className="h-[calc(100vh-10rem)] shadow-xl rounded-xl overflow-y-auto"
          >
            <ProcessTreeView
                nodes={nodes}
                onNodeClick={handleNodeClick}
                selectedNodeId={selectedNodeId}
                onAddRootNode={() => {
                  if (hasRootNode) {
                    toast({
                      title: "Action Denied",
                      description: "A root 'Process' node already exists.",
                      variant: "destructive",
                    });
                    return;
                  }
                  setParentNodeForDialog(null);
                  setIsAddNodeDialogOpen(true);
                }}
                hasRootNode={hasRootNode}
                getChildNodes={getChildNodes}
                dropTargetInfo={dropTargetInfo}
                onDeleteAllNodes={handleUIDeleteAllNodes}
            />
          </motion.div>

          <motion.div
              initial={{ x: 100, opacity: 0 }}
              animate={{ x: 0, opacity: 1 }}
              transition={{ duration: 0.4, ease: "circOut" }}
              className="h-full flex flex-col"
          >
            <div className="flex-1 mb-4 overflow-hidden shadow-xl rounded-xl bg-white dark:bg-gray-800 ">
              <NodeDetailPanel
                  key={selectedNodeId}
                  node={selectedNode}
                  allNodes={nodes}
                  onUpdateNode={handleUINodeUpdate}
                  onDeleteNode={handleUINodeDelete}
                  onAddChildNode={openAddChildDialog}
              />
            </div>
            <div>
              <Button
                  onClick={handleSaveAndSimulate}
                  disabled={isSaving}
                  className="w-full bg-gradient-to-r from-sky-500 to-indigo-600
                         hover:from-sky-600 hover:to-indigo-700 text-white
                         font-bold py-3 text-base shadow-lg rounded-lg
                         disabled:opacity-70 disabled:cursor-not-allowed"
              >
                <Rocket className={`mr-2 h-5 w-5 ${isSaving ? 'animate-spin' : ''}`} />
                {isSaving ? 'Saving...' : 'Save & Continue'}
              </Button>
            </div>
          </motion.div>
        </div>

        <AddNodeDialog
            isOpen={isAddNodeDialogOpen}
            onClose={() => setIsAddNodeDialogOpen(false)}
            onAddNode={handleUINodeAdd}
            parentNode={parentNodeForDialog}
            allNodes={nodes}
            hasRootNode={hasRootNode}
            allowedTypes={allowedTypesForDialog}
        />

        <DragOverlay dropAnimation={null} zIndex={2000}>
          {activeDragItem ? (
              <SortableNodeItemContent node={activeDragItem} allNodes={nodes} isDraggingOverlay={true} />
          ) : null}
        </DragOverlay>
      </DndContext>
  );
};

export default ProcessEditor;
