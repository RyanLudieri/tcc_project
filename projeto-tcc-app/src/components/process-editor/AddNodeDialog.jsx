import React, { useState, useEffect } from 'react';
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogFooter
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue
} from '@/components/ui/select';
import { ScrollArea } from "@/components/ui/scroll-area";

const AddNodeDialog = ({
                         isOpen,
                         onClose,
                         onAddNode,
                         parentNode,
                         allNodes,
                         hasRootNode,
                         allowedTypes,
                       }) => {
  const [presentationName, setPresentationName] = useState('');
  const [nodeType, setNodeType] = useState('');
  const [modelInfo, setModelInfo] = useState('');
  const [predecessors, setPredecessors] = useState([]);
  const [optional, setOptional] = useState(false);

  useEffect(() => {
    if (isOpen) {
      // reset form
      setPresentationName('');
      setModelInfo('');
      setPredecessors([]);
      setOptional(false);

      // define tipo inicial com base na lista permitida
      if (allowedTypes && allowedTypes.length > 0) {
        setNodeType(allowedTypes[0]);
      } else {
        setNodeType('');
      }
    }
  }, [isOpen, allowedTypes]);

  const handleSubmit = () => {
    if (!nodeType) return;

    const nodeData = {
      presentationName: presentationName.trim() || `${nodeType} (New)`,
      type: nodeType,
      modelInfo,
      predecessors,
      optional: false,
    };
    onAddNode(nodeData, parentNode ? parentNode.id : null);
    onClose();
  };

  const selectablePredecessors = allNodes.filter(n => n.id !== parentNode?.id);

  return (
      <Dialog open={isOpen} onOpenChange={onClose}>
        <DialogContent className="sm:max-w-[480px] bg-card">
          <DialogHeader>
            <DialogTitle>
              {parentNode
                  ? `Add Child to '${parentNode.presentationName}'`
                  : "Add Root Node"}
            </DialogTitle>
            <DialogDescription>
              Configure the details for the new node.
            </DialogDescription>
          </DialogHeader>

          <ScrollArea className="max-h-[60vh] p-1">
            <div className="grid gap-4 py-4 pr-3">
              {/* Nome */}
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="node-pname" className="text-right">Name</Label>
                <Input
                    id="node-pname"
                    value={presentationName}
                    onChange={(e) => setPresentationName(e.target.value)}
                    className="col-span-3"
                    placeholder="Visible node name"
                />
              </div>

              {/* Tipo */}
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="node-type" className="text-right">Type</Label>
                <Select
                    value={nodeType}
                    onValueChange={setNodeType}
                >
                  <SelectTrigger className="col-span-3">
                    <SelectValue placeholder="Select node type" />
                  </SelectTrigger>
                  <SelectContent>
                    {allowedTypes?.map(typeName => (
                        <SelectItem key={typeName} value={typeName}>
                          {typeName}
                        </SelectItem>
                    ))}
                    {(!allowedTypes || allowedTypes.length === 0) && (
                        <div className="p-2 text-sm text-muted-foreground">
                          No valid child types for this parent.
                        </div>
                    )}
                  </SelectContent>
                </Select>
              </div>

              {/* Model Info */}
              <div className="grid grid-cols-4 items-center gap-4">
                <Label htmlFor="node-modelinfo" className="text-right">Model Info</Label>
                <Select
                    id="node-modelinfo"
                    value={modelInfo}
                    onValueChange={setModelInfo}
                >
                  <SelectTrigger className="col-span-3 mt-1">
                    <SelectValue
                        placeholder="Select Model Info"
                        className="text-muted-foreground" // <--- deixa clarinho igual placeholder do Name
                    />
                  </SelectTrigger>
                  <SelectContent>
                    {["-", "MANDATORY_INPUT", "OPTIONAL_INPUT", "OUTPUT", "PRIMARY_PERFORMER", "SECONDARY_PERFORMER"].map(option => (
                        <SelectItem key={option} value={option}>
                          {option}
                        </SelectItem>
                    ))}
                  </SelectContent>
                </Select>

              </div>



              {/* Predecessores */}
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
                          <SelectItem
                              key={pNode.id}
                              value={pNode.id}
                              className={predecessors.includes(pNode.id)
                                  ? "bg-accent text-accent-foreground"
                                  : ""}
                          >
                            {pNode.presentationName} ({pNode.index || pNode.type || 'No Type'})
                          </SelectItem>
                      ))}
                      {selectablePredecessors.length === 0 && (
                          <p className="p-2 text-sm text-muted-foreground">
                            No other nodes to select.
                          </p>
                      )}
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
            <Button onClick={handleSubmit} disabled={!nodeType}>
              Add Node
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
  );
};

export default AddNodeDialog;
