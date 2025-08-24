import React from 'react';
import { cn } from '@/lib/utils';
import { Rocket, Package, Target, Zap, Users, FileText, FolderOpen, Briefcase } from 'lucide-react';

export const NodeIcon = ({ type }) => {
  const iconClass = "h-5 w-5 mr-2 flex-shrink-0";
  switch (type) {
    case 'Process': return <Rocket className={cn(iconClass, "text-purple-200")} />;
    case 'Phase': return <Package className={cn(iconClass, "text-blue-200")} />;
    case 'Iteration': return <Zap className={cn(iconClass, "text-teal-200")} />;
    case 'Milestone': return <Target className={cn(iconClass, "text-indigo-200")} />;
    case 'Activity': return <FolderOpen className={cn(iconClass, "text-amber-800")} />;
    case 'TaskDescriptor': return <Briefcase className={cn(iconClass, "text-orange-200")} />;
    case 'Role': return <Users className={cn(iconClass, "text-pink-200")} />;
    case 'Artifact': return <FileText className={cn(iconClass, "text-slate-600")} />;
    default: return <FolderOpen className={cn(iconClass, "text-gray-500")} />;
  }
};