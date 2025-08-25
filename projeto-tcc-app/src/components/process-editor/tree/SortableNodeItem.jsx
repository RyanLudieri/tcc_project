import React, { useState } from 'react';
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from '@/components/ui/collapsible';
import { ChevronRight } from 'lucide-react';
import { motion, AnimatePresence } from 'framer-motion';
import { useSortable, SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { SortableNodeItemContent } from '@/components/process-editor/tree/SortableNodeItemContent';
import { NodeDropIndicator, ParentDropIndicator } from '@/components/process-editor/tree/DropIndicators';

export const SortableNodeItem = ({ node, allNodes, onNodeClick, selectedNodeId, level = 0, getChildNodes, dropTargetInfo }) => {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id: node.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition: isDragging ? 'none' : transition,
    zIndex: isDragging ? 100 : 'auto',
    opacity: isDragging ? 0.5 : 1,
  };
  
  const [isOpen, setIsOpen] = useState(true);
  const children = getChildNodes(node.id);
  const isSelected = selectedNodeId === node.id;

  let dropTargetClasses = '';
  if (dropTargetInfo && dropTargetInfo.id === node.id && dropTargetInfo.position === 'child') {
    dropTargetClasses = 'bg-blue-100 dark:bg-blue-900/30';
  }
  
  const showBeforeIndicator = dropTargetInfo && dropTargetInfo.id === node.id && dropTargetInfo.position === 'before';
  const showAfterIndicator = dropTargetInfo && dropTargetInfo.id === node.id && dropTargetInfo.position === 'after';
  
  const showParentDropIndicator = dropTargetInfo && dropTargetInfo.parentId === node.id && dropTargetInfo.position === 'child' && (!dropTargetInfo.targetChildId || children.length === 0);

  return (
    <Collapsible open={isOpen} onOpenChange={setIsOpen} className="w-full relative group" ref={setNodeRef}>
      <NodeDropIndicator isVisible={showBeforeIndicator} position="before" level={level} tabular={true} />
      
      <div 
        style={style} 
        {...attributes} 
        className={`border-b dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-700/30 transition-colors duration-100 ${dropTargetClasses} ${isSelected ? 'bg-sky-50 dark:bg-sky-900/30' : ''}`}
      >
        <SortableNodeItemContent 
          node={node} 
          allNodes={allNodes} 
          isSelected={isSelected} 
          level={level}
          hasChildren={children.length > 0}
          isOpen={isOpen}
          onToggleOpen={() => setIsOpen(!isOpen)}
          dragListeners={listeners}
          onNodeClick={onNodeClick}
        />
      </div>
      
      <CollapsibleContent className="relative">
        <ParentDropIndicator isVisible={showParentDropIndicator} level={level + 1} childrenCount={children.length} tabular={true}/>
        <AnimatePresence>
          {isOpen && children.length > 0 && (
            <SortableContext items={children.map(c => c.id)} strategy={verticalListSortingStrategy}>
              <div>
                {children.map(child => (
                  <SortableNodeItem
                    key={child.id}
                    node={child}
                    allNodes={allNodes}
                    onNodeClick={onNodeClick}
                    selectedNodeId={selectedNodeId}
                    level={level + 1}
                    getChildNodes={getChildNodes}
                    dropTargetInfo={dropTargetInfo}
                  />
                ))}
              </div>
            </SortableContext>
          )}
        </AnimatePresence>
      </CollapsibleContent>
      <NodeDropIndicator isVisible={showAfterIndicator} position="after" level={level} tabular={true} />
    </Collapsible>
  );
};