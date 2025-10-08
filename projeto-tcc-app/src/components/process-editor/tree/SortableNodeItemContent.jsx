import React from 'react';
import { NodeIcon } from '@/components/process-editor/tree/NodeIcon';
import { GripVertical, ChevronRight } from 'lucide-react';
import { motion } from 'framer-motion';
import { Badge } from '@/components/ui/badge';

export const SortableNodeItemContent = ({
                                          node,
                                          allNodes,
                                          isSelected,
                                          level,
                                          hasChildren,
                                          isOpen,
                                          onToggleOpen,
                                          dragListeners,
                                          onNodeClick,
                                          onRepeatableChange,
                                          isDraggingOverlay = false
                                        }) => {

  const predecessorIndices = node.predecessors && node.predecessors.length > 0
      ? node.predecessors.map(pId => {
        const pNode = allNodes.find(n => n.id === pId);
        return pNode && pNode.index ? pNode.index : null;
      }).filter(Boolean).join(', ')
      : '-';

  const itemStyle = { paddingLeft: `${level * 1.5}rem` };

  const typeDisplay = node.type || 'No Type';
  const indexDisplay = node.index || '-';

  if (isDraggingOverlay) {
    return (
        <div className="flex items-center w-full text-sm bg-white dark:bg-gray-800 shadow-lg rounded-md p-3 border border-primary">
          <div className="flex items-center w-[45%]" style={{ paddingLeft: '0rem' }}>
            {hasChildren && (
                <ChevronRight className="h-4 w-4 mr-1 flex-shrink-0 opacity-50" style={{ transform: isOpen ? 'rotate(90deg)' : 'rotate(0deg)' }} />
            )}
            {!hasChildren && <div className="w-5 h-5 mr-1 flex-shrink-0"></div>}
            <NodeIcon type={node.type} className="mr-2 flex-shrink-0 h-4 w-4" />
            <span className="font-medium truncate text-gray-800 dark:text-gray-100" title={node.presentationName}>
            {node.presentationName}
          </span>
          </div>
          <div className="w-[10%] text-center text-gray-600 dark:text-gray-400">{indexDisplay}</div>
          <div className="w-[15%] text-center text-gray-600 dark:text-gray-400 truncate px-1" title={predecessorIndices}>{predecessorIndices}</div>
          <div className="w-[15%] text-center text-gray-600 dark:text-gray-400 truncate px-1" title={node.modelInfo}>{node.modelInfo || '-'}</div>
          <div className="w-[15%] text-center text-gray-600 dark:text-gray-400 truncate pr-2">{typeDisplay}</div>
          <div className="w-[15%] text-center text-gray-600 dark:text-gray-400 truncate pr-2">{node.repeatable ? 'True' : 'False'}</div>
        </div>
    );
  }

  return (
      <div
          className={`flex items-center w-full text-sm cursor-pointer transition-colors duration-150 px-4 py-2 min-h-[40px] relative`}
          onClick={() => onNodeClick(node.id)}
      >
        <div className="flex items-center w-[45%]" style={itemStyle}>
          {hasChildren ? (
              <motion.button
                  className="p-0.5 rounded hover:bg-gray-200 dark:hover:bg-gray-600 focus:outline-none focus-visible:ring-1 focus-visible:ring-ring mr-1 flex-shrink-0"
                  animate={{ rotate: isOpen ? 90 : 0 }}
                  transition={{ duration: 0.2 }}
                  onClick={(e) => { e.stopPropagation(); onToggleOpen(); }}
              >
                <ChevronRight className="h-4 w-4" />
              </motion.button>
          ) : (
              <div className="w-5 h-5 mr-1 flex-shrink-0"></div>
          )}
          <NodeIcon type={node.type} className="mr-2 flex-shrink-0 h-4 w-4" />
          <span className="font-medium truncate text-gray-800 dark:text-gray-100" title={node.presentationName}>
          {node.presentationName}
        </span>
        </div>

        <div className="w-[10%] text-center text-gray-600 dark:text-gray-400">
          {indexDisplay}
        </div>

        <div className="w-[10%] text-center text-gray-600 dark:text-gray-400 truncate px-1" title={predecessorIndices}>
          {predecessorIndices}
        </div>

        <div className="w-[20%] text-center text-gray-600 dark:text-gray-400 truncate px-1" title={node.modelInfo}>
          {node.modelInfo || '-'}
        </div>

        <div className="w-[8%] text-center text-gray-600 dark:text-gray-400 truncate pr-2">
          {typeDisplay}
        </div>

        <div className="flex-shrink-0 ml-auto" {...dragListeners}>
          <GripVertical className="h-5 w-5 text-gray-400 dark:text-gray-500 opacity-0 group-hover:opacity-100 transition-opacity cursor-grab touch-none" />
        </div>
      </div>
  );
};
