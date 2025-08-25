import { cn } from '@/lib/utils';

export const getNodeBaseStyle = (type) => {
  
  return ''; 
};

export const getNodeStyle = (type, isSelected, isDraggingOverlay = false, dropTargetStyle = '') => {
  
  let baseClasses = 'p-0 rounded-none shadow-none cursor-pointer transition-all duration-150 ease-in-out group relative mb-0 border-b dark:border-gray-700 last:border-b-0';
  
  const typeSpecificClasses = ''; 

  const selectedClasses = ''; 
  
  if (isSelected && !isDraggingOverlay) {
    
  }
  
  const draggingOverlayClasses = isDraggingOverlay ? 'opacity-80 shadow-xl scale-105 bg-white dark:bg-gray-800 rounded-md p-3' : '';
  
  return cn(baseClasses, typeSpecificClasses, selectedClasses, draggingOverlayClasses, dropTargetStyle);
};