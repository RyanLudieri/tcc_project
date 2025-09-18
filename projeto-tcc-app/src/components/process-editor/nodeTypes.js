import React from 'react';
import { Rocket, ActivitySquare as SquareActivity, Briefcase, User, FileText, Package, Zap, Target, FolderOpen } from 'lucide-react';

export const initialNodeTypes = [
  { name: 'Process', icon: Rocket, color: 'bg-purple-600', description: 'The main process container.' },
  { name: 'Phase', icon: Package, color: 'bg-blue-500', description: 'A major stage in the process.' },
  { name: 'Iteration', icon: Zap, color: 'bg-teal-500', description: 'A cycle within a phase.' },
  { name: 'Milestone', icon: Target, color: 'bg-indigo-500', description: 'A significant checkpoint.' },
  { name: 'Activity', icon: FolderOpen, color: 'bg-amber-500', description: 'A specific task or action group.' },
  { name: 'Task', icon: Briefcase, color: 'bg-orange-500', description: 'A detailed description of a task.' },
  { name: 'Role', icon: User, color: 'bg-pink-500', description: 'A person or team involved.' },
  { name: 'Artifact', icon: FileText, color: 'bg-slate-300', description: 'A document or output.' },
  // 'None' type is handled implicitly by allowing empty type string or specific 'None' value in UI
];

// You can add more utility functions or constants related to node types here if needed.