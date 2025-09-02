import React, { useState, useEffect } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { ScrollArea } from "@/components/ui/scroll-area";
import { initialNodeTypes } from '@/components/process-editor/nodeTypes'; 

const AddNodeDialog = ({ isOpen, onClose, onAddNode, parentNode, allNodes, hasRootNode }) => {
  const [presentationName, setPresentationName] = useState('');
  const [nodeType, setNodeType] = useState(parentNode ? (parentNode.type === 'Process' ? 'Phase' : 'Activity') : (hasRootNode ? 'Phase' : 'Process'));
  const [modelInfo, setModelInfo] = useState('');
  const [predecessors, setPredecessors] = useState([]);
  const [artifactURL, setArtifactURL] = useState('');
  const [optional, setOptional] = useState(false);


  useEffect(() => {
    if (isOpen) {
      // Reset form when dialog opens
      setPresentationName('');
      setModelInfo('');
      setPredecessors([]);
      setArtifactURL('');
      setOptional(false);
      // Set default type based on context
      if (parentNode) {
        if (parentNode.type === 'Process') setNodeType('Phase');
        else if (parentNode.type === 'TaskDescriptor') setNodeType('Role'); // Example restriction
        else setNodeType('Activity');
      } else {
        setNodeType(hasRootNode ? 'Phase' : 'Process'); // Should be 'Process' if no root, but dialog might not open then
      }
    }
  }, [isOpen, parentNode, hasRootNode]);

  const handleSubmit = () => {
    const nodeData = {
      presentationName: presentationName.trim() || `${nodeType === 'None' ? 'Untitled Node' : nodeType} (New)`,
      type: nodeType,
      modelInfo,
      predecessors,
      artifactURL: nodeType === 'Artifact' ? artifactURL : '',
      optional: false
    };
    onAddNode(nodeData, parentNode ? parentNode.id : null);
    onClose();
  };

  const availableNodeTypes = parentNode 
    ? initialNodeTypes.filter(nt => {
        if (nt.name === 'Process') return false; // Cannot add Process as a child
        if (parentNode.type === 'TaskDescriptor' && nt.name === 'Activity') return false; // Task cannot have Activity child
        if ((parentNode.type === 'Artifact' || parentNode.type === 'Role') && nt.name !== 'None') return false; // Artifacts/Roles are typically leaf-like
        return true;
      })
    : (hasRootNode ? initialNodeTypes.filter(nt => nt.name !== 'Process') : initialNodeTypes.filter(nt => nt.name === 'Process'));
  
  // Add "None" type if it's not a root node being added or if it's a child
  const finalNodeTypes = (parentNode || hasRootNode) ? [{ name: 'None', description: 'No specific type' }, ...availableNodeTypes] : availableNodeTypes;


  const selectablePredecessors = allNodes.filter(n => n.id !== parentNode?.id); // Simplified

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-[480px] bg-card">
        <DialogHeader>
          <DialogTitle>{parentNode ? `Add Child to '${parentNode.presentationName}'` : "Add Root Node"}</DialogTitle>
          <DialogDescription>
            Configure the details for the new node.
          </DialogDescription>
        </DialogHeader>
        <ScrollArea className="max-h-[60vh] p-1">
          <div className="grid gap-4 py-4 pr-3">
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="node-pname" className="text-right">
                Name
              </Label>
              <Input
                id="node-pname"
                value={presentationName}
                onChange={(e) => setPresentationName(e.target.value)}
                className="col-span-3"
                placeholder="Visible node name"
              />
            </div>
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="node-type" className="text-right">
                Type
              </Label>
              <Select 
                value={nodeType} 
                onValueChange={setNodeType}
                disabled={!parentNode && hasRootNode && nodeType !== 'Process'}
              >
                <SelectTrigger className="col-span-3">
                  <SelectValue placeholder="Select node type" />
                </SelectTrigger>
                <SelectContent>
                  {finalNodeTypes.map(typeOpt => (
                    <SelectItem 
                      key={typeOpt.name} 
                      value={typeOpt.name}
                      disabled={typeOpt.name === 'Process' && (parentNode || hasRootNode)}
                    >
                      {typeOpt.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            
            {nodeType === 'Artifact' && (
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="node-artifacturl" className="text-right">
                  Artifact URL
                </Label>
                <Input
                  id="node-artifacturl"
                  value={artifactURL}
                  onChange={(e) => setArtifactURL(e.target.value)}
                  className="col-span-3"
                  placeholder="e.g., https://example.com/doc.pdf"
                />
              </div>
            )}

            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="node-modelinfo" className="text-right">
                Model Info
              </Label>
              <Input
                id="node-modelinfo"
                value={modelInfo}
                onChange={(e) => setModelInfo(e.target.value)}
                className="col-span-3 min-h-[80px]"
                placeholder="Description..."
                as="textarea"
              />
            </div>
            <div className="grid grid-cols-4 items-center gap-4">
              <Label htmlFor="node-predecessors" className="text-right">
                Predecessors
              </Label>
              <Select
                onValueChange={(value) => {
                  const selected = predecessors.includes(value) 
                    ? predecessors.filter(p => p !== value)
                    : [...predecessors, value];
                  setPredecessors(selected);
                }}
                value={predecessors.length > 0 ? "selected" : ""} 
              >
                <SelectTrigger className="col-span-3">
                  <SelectValue placeholder="Select predecessors (optional)" />
                </SelectTrigger>
                <SelectContent>
                  <ScrollArea className="h-[150px]">
                    {selectablePredecessors.map(pNode => (
                      <SelectItem key={pNode.id} value={pNode.id}
                        className={predecessors.includes(pNode.id) ? "bg-accent text-accent-foreground" : ""}
                      >
                        {pNode.presentationName} ({pNode.index || pNode.type || 'No Type'})
                      </SelectItem>
                    ))}
                    {selectablePredecessors.length === 0 && <p className="p-2 text-sm text-muted-foreground">No other nodes to select.</p>}
                  </ScrollArea>
                </SelectContent>
              </Select>
            </div>
             {predecessors.length > 0 && (
              <div className="col-start-2 col-span-3 text-xs text-muted-foreground">
                Selected: {predecessors.map(pId => allNodes.find(n=>n.id===pId)?.presentationName).join(', ')}
              </div>
            )}
          </div>
        </ScrollArea>
        <DialogFooter>
          <Button variant="outline" onClick={onClose}>Cancel</Button>
          <Button onClick={handleSubmit}>Add Node</Button>
          {/*setOptional(false);*/}
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default AddNodeDialog;