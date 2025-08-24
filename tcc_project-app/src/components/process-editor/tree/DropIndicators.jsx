import React from 'react';
import { cn } from '@/lib/utils';

export const NodeDropIndicator = ({ position, level = 0, isVisible }) => {
  if (!isVisible) return null;
  return (
    <div
      className={cn(
        "absolute left-0 right-0 h-1 bg-blue-500 rounded-full z-50",
        position === 'before' ? '-top-1.5' : '-bottom-1.5'
      )}
      style={{ marginLeft: `${level * 1.25}rem` }}
    />
  );
};

export const ParentDropIndicator = ({ level = 0, childrenCount = 0, isVisible }) => {
  if (!isVisible) return null;
  if (childrenCount === 0) {
    return (
      <div className="ml-4 my-1 h-8 border-2 border-dashed border-blue-400 rounded-md flex items-center justify-center text-blue-500 text-xs" style={{ marginLeft: `${(level + 1) * 1.25}rem` }}>
        Drop here to add as child
      </div>
    );
  }
  return (
    <div className="ml-4 my-1 h-1 bg-blue-500 z-10" style={{ marginLeft: `${(level + 1) * 1.25}rem` }} />
  );
};