import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { ScrollArea } from "@/components/ui/scroll-area";
import { Separator } from "@/components/ui/separator";
import { Tooltip, TooltipContent, TooltipTrigger } from '@/components/ui/tooltip';
import { PlusCircle } from 'lucide-react';
import { initialNodeTypes } from '@/components/process-editor/nodeTypes';

const NodeDetailForm = ({ node, allNodes, onUpdateNode, onAddChildNode }) => {
  const [presentationName, setPresentationName] = useState('');
  const [modelInfo, setModelInfo] = useState('');
  const [nodeType, setNodeType] = useState('');
  const [predecessors, setPredecessors] = useState([]);
  const [artifactURL, setArtifactURL] = useState('');

  useEffect(() => {
    if (node) {
      setPresentationName(node.presentationName || '');
      setModelInfo(node.modelInfo || '');
      setNodeType(node.type || 'None'); 
      setPredecessors(node.predecessors || []);
      setArtifactURL(node.artifactURL || '');
    } else {
      setPresentationName('');
      setModelInfo('');
      setNodeType('None');
      setPredecessors([]);
      setArtifactURL('');
    }
  }, [node]);

  const handleUpdate = () => {
    if (node) {
      const updates = { 
        presentationName, 
        modelInfo,
        type: nodeType === 'None' ? '' : nodeType, 
        predecessors,
      };
      if (nodeType === 'Artifact') { 
        updates.artifactURL = artifactURL;
      } else if (node.type === 'Artifact' && nodeType !== 'Artifact') {
        updates.artifactURL = ''; 
      }
      onUpdateNode(node.id, updates);
    }
  };

  const handleAddChild = () => {
    if (node) {
      onAddChildNode(node.id);
    }
  };

  const canAddChild = () => {
    if (!node) return false;
    if (node.type === 'Process') {
        return initialNodeTypes.some(nt => nt.name === 'Phase');
    }
    if (node.type === 'Task') {
      return initialNodeTypes.some(nt => (nt.name === 'Role' || nt.name === 'Artifact'));
    }
    if (node.type === 'Artifact' || node.type === 'Role') {
        return false; 
    }
    return initialNodeTypes.some(nt => nt.name !== 'Process');
  };

  const selectablePredecessors = allNodes.filter(n => n.id !== node?.id && !isDescendant(n.id, node?.id, allNodes));

  function isDescendant(childId, parentId, nodes) {
    if (!childId || !parentId) return false;
    let currentParentId = nodes.find(n => n.id === childId)?.parentId;
    while (currentParentId) {
      if (currentParentId === parentId) return true;
      currentParentId = nodes.find(n => n.id === currentParentId)?.parentId;
    }
    return false;
  }
  
  const nodeTypeOptions = [{ name: 'None', description: 'No specific type' }, ...initialNodeTypes.filter(nt => nt.name !== 'Process')];
  
  const displayIndex = node?.index ? `Index: ${node.index}` : 'N/A (No Type)';

  return (
    <>
      <div>
        <Label htmlFor="node-pname-detail" className="text-sm font-medium">Presentation Name</Label>
        <Input
          id="node-pname-detail"
          value={presentationName}
          onChange={(e) => setPresentationName(e.target.value)}
          onBlur={handleUpdate}
          className="mt-1"
          placeholder="Visible node name"
        />
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div>
          <Label htmlFor="node-index-detail-readonly" className="text-sm font-medium">Index</Label>
          <Input
            id="node-index-detail-readonly"
            value={displayIndex}
            readOnly
            className="mt-1 bg-muted/50 cursor-default"
          />
        </div>
        <div>
          <Label htmlFor="node-type-detail" className="text-sm font-medium">Type</Label>
          <Select 
            value={nodeType} 
            onValueChange={(value) => { 
              setNodeType(value); 
              if (node) {
                  const newUpdates = { type: value === 'None' ? '' : value };
                  if (value !== 'Artifact') newUpdates.artifactURL = ''; 
                  onUpdateNode(node.id, { ...node, ...newUpdates, presentationName, modelInfo, predecessors, artifactURL: value === 'Artifact' ? artifactURL : '' });
              }
            }}
            disabled={node?.type === 'Process'}
          >
            <SelectTrigger className="w-full mt-1">
              <SelectValue placeholder="Select type" />
            </SelectTrigger>
            <SelectContent>
              {nodeTypeOptions.map(typeOpt => (
                <SelectItem key={typeOpt.name} value={typeOpt.name} disabled={typeOpt.name === 'Process' && node?.type !== 'Process'}>
                  {typeOpt.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          {node?.type === 'Process' && <p className="text-xs text-muted-foreground mt-1">The 'Process' node type cannot be changed.</p>}
        </div>
      </div>

      {nodeType === 'Artifact' && ( 
        <div>
          <Label htmlFor="node-artifacturl-detail" className="text-sm font-medium">Artifact URL</Label>
          <Input
            id="node-artifacturl-detail"
            value={artifactURL}
            onChange={(e) => setArtifactURL(e.target.value)}
            onBlur={handleUpdate}
            className="mt-1"
            placeholder="e.g., https://example.com/document.pdf"
          />
        </div>
      )}

      <div>
        <Label htmlFor="node-modelinfo-detail" className="text-sm font-medium">Model Info (Description)</Label>
        <Input
          id="node-modelinfo-detail"
          value={modelInfo}
          onChange={(e) => setModelInfo(e.target.value)}
          onBlur={handleUpdate}
          className="mt-1 min-h-[80px]"
          placeholder="Detailed textual description of the node..."
          as="textarea"
        />
      </div>
      <div>
        <Label htmlFor="node-predecessors-detail" className="text-sm font-medium">Predecessors</Label>
        <Select
          onValueChange={(value) => { 
            const selected = predecessors.includes(value) 
              ? predecessors.filter(p => p !== value)
              : [...predecessors, value]; 
            setPredecessors(selected);
          }}
           value={predecessors.length > 0 ? predecessors[predecessors.length-1] : ""} 
        >
          <SelectTrigger className="w-full mt-1">
             <SelectValue placeholder="Select predecessors (optional)" />
          </SelectTrigger>
          <SelectContent>
            <ScrollArea className="h-[150px]">
              {selectablePredecessors.map(pNode => (
                <SelectItem key={pNode.id} value={pNode.id}
                  className={predecessors.includes(pNode.id) ? "bg-accent text-accent-foreground" : ""}
                >
                  {pNode.presentationName} ({pNode.index || (pNode.type || 'No Type')})
                </SelectItem>
              ))}
              {selectablePredecessors.length === 0 && <p className="p-2 text-sm text-muted-foreground">No other nodes available to select.</p>}
            </ScrollArea>
          </SelectContent>
        </Select>
        {predecessors.length > 0 && (
          <div className="mt-1 text-xs text-muted-foreground">
            Selected: {predecessors.map(pId => allNodes.find(n=>n.id===pId)?.presentationName).join(', ')}
          </div>
        )}
         <Button variant="link" size="sm" className="p-0 h-auto mt-1 text-xs" onClick={handleUpdate}>Apply Predecessor Changes</Button>
      </div>
      <Separator />
      <div className="flex space-x-2">
        <Tooltip>
          <TooltipTrigger asChild>
            <Button variant="outline" size="sm" onClick={handleAddChild} disabled={!canAddChild()}>
              <PlusCircle className="mr-2 h-4 w-4" /> Add Child
            </Button>
          </TooltipTrigger>
          <TooltipContent><p>{canAddChild() ? "Add Child Node" : (node ? `Cannot add children to a '${node.type || 'node without type'}'.` : "Select a node")}</p></TooltipContent>
        </Tooltip>
      </div>
    </>
  );
};

export default NodeDetailForm;