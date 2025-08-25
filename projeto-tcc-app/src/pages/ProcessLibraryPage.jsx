import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Link, useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button.jsx';
import { Input } from '@/components/ui/input.jsx';
import { useToast } from '@/components/ui/use-toast.js';
import ProcessCard from '@/components/library/ProcessCard.jsx';
import ProcessListItem from '@/components/library/ProcessListItem.jsx';
import ProcessPreview from '@/components/library/ProcessPreview.jsx';
import { 
  Search, 
  Plus, 
  ArrowLeft,
  Grid3X3,
  List,
  Activity
} from 'lucide-react';
import { v4 as uuidv4 } from 'uuid';

const ProcessLibraryPage = () => {
  const [processes, setProcesses] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [viewMode, setViewMode] = useState('grid');
  const [selectedProcess, setSelectedProcess] = useState(null);
  const navigate = useNavigate();
  const { toast } = useToast();

  useEffect(() => {
    loadProcesses();
  }, []);

  const loadProcesses = () => {
    try {
      const savedProcesses = [];
      
      for (let i = 0; i < localStorage.length; i++) {
        const key = localStorage.key(i);
        if (key && key.startsWith('processNodes_')) {
          const processId = key.replace('processNodes_', '');
          const processData = localStorage.getItem(key);
          
          if (processData) {
            try {
              const nodes = JSON.parse(processData);
              const processNode = nodes.find(node => node.type === 'Process');
              
              if (processNode) {
                const processInfo = {
                  id: processId,
                  name: processNode.presentationName || 'Untitled Process',
                  description: processNode.description || 'No description available',
                  lastModified: new Date().toISOString(),
                  nodeCount: nodes.length,
                  phases: nodes.filter(n => n.type === 'Phase').length,
                  activities: nodes.filter(n => n.type === 'Activity').length,
                  tasks: nodes.filter(n => n.type === 'Task').length,
                  artifacts: nodes.filter(n => n.type === 'Artifact').length,
                  roles: nodes.filter(n => n.type === 'Role').length,
                  nodes: nodes
                };
                
                savedProcesses.push(processInfo);
              }
            } catch (parseError) {
              console.warn(`Failed to parse process data for ${processId}:`, parseError);
            }
          }
        }
      }
      
      savedProcesses.sort((a, b) => new Date(b.lastModified) - new Date(a.lastModified));
      setProcesses(savedProcesses);
    } catch (error) {
      console.error('Failed to load processes:', error);
      toast({
        title: "Error Loading Processes",
        description: "Failed to load saved processes from storage.",
        variant: "destructive",
      });
    }
  };

  const filteredProcesses = processes.filter(process =>
    process.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    process.description.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const handleCreateNew = () => {
    const newProcessId = uuidv4();
    navigate(`/processes/${newProcessId}/edit`);
  };

  const handleEditProcess = (processId) => {
    navigate(`/processes/${processId}/edit`);
  };

  const handleViewProcess = (process) => {
    setSelectedProcess(process);
  };

  const handleDeleteProcess = (processId, processName) => {
    try {
      localStorage.removeItem(`processNodes_${processId}`);
      setProcesses(prev => prev.filter(p => p.id !== processId));
      
      if (selectedProcess && selectedProcess.id === processId) {
        setSelectedProcess(null);
      }
      
      toast({
        title: "Process Deleted",
        description: `"${processName}" has been removed from your library.`,
        variant: "default",
      });
    } catch (error) {
      console.error('Failed to delete process:', error);
      toast({
        title: "Error",
        description: "Failed to delete the process. Please try again.",
        variant: "destructive",
      });
    }
  };

  const formatDate = (dateString) => {
    try {
      return new Date(dateString).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return 'Unknown';
    }
  };

  return (
    <div className="container mx-auto px-4 py-8 max-w-7xl">
      <div className="flex items-center justify-between mb-8">
        <div className="flex items-center gap-4">
          <Link to="/">
            <Button variant="outline" size="sm">
              <ArrowLeft className="h-4 w-4 mr-2" />
              Back to Home
            </Button>
          </Link>
          <div>
            <h1 className="text-3xl font-bold text-primary">Process Library</h1>
            <p className="text-muted-foreground mt-1">
              Manage and organize your software process definitions
            </p>
          </div>
        </div>
        
        <Button onClick={handleCreateNew} className="bg-accent hover:bg-accent/90">
          <Plus className="h-4 w-4 mr-2" />
          Create New Process
        </Button>
      </div>

      <div className="flex gap-6">
        <div className={`${selectedProcess ? 'lg:w-2/3' : 'w-full'} transition-all duration-300`}>
          <div className="flex items-center gap-4 mb-6">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="Search processes..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-10"
              />
            </div>
            
            <div className="flex items-center gap-2">
              <Button
                variant={viewMode === 'grid' ? 'default' : 'outline'}
                size="sm"
                onClick={() => setViewMode('grid')}
              >
                <Grid3X3 className="h-4 w-4" />
              </Button>
              <Button
                variant={viewMode === 'list' ? 'default' : 'outline'}
                size="sm"
                onClick={() => setViewMode('list')}
              >
                <List className="h-4 w-4" />
              </Button>
            </div>
          </div>

          <div className="flex items-center justify-between mb-4">
            <p className="text-sm text-muted-foreground">
              {filteredProcesses.length} process{filteredProcesses.length !== 1 ? 'es' : ''} found
            </p>
          </div>

          {filteredProcesses.length === 0 ? (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="text-center py-12"
            >
              <div className="max-w-md mx-auto">
                <Activity className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
                <h3 className="text-lg font-semibold mb-2">No Processes Found</h3>
                <p className="text-muted-foreground mb-4">
                  {searchTerm 
                    ? "No processes match your search criteria. Try adjusting your search terms."
                    : "You haven't created any processes yet. Start by creating your first process!"
                  }
                </p>
                {!searchTerm && (
                  <Button onClick={handleCreateNew}>
                    <Plus className="h-4 w-4 mr-2" />
                    Create Your First Process
                  </Button>
                )}
              </div>
            </motion.div>
          ) : (
            <motion.div layout className="space-y-4">
              {viewMode === 'grid' ? (
                <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
                  {filteredProcesses.map((process) => (
                    <ProcessCard 
                      key={process.id} 
                      process={process}
                      onView={handleViewProcess}
                      onEdit={handleEditProcess}
                      onDelete={handleDeleteProcess}
                      formatDate={formatDate}
                    />
                  ))}
                </div>
              ) : (
                <div className="space-y-3">
                  {filteredProcesses.map((process) => (
                    <ProcessListItem 
                      key={process.id} 
                      process={process}
                      onView={handleViewProcess}
                      onEdit={handleEditProcess}
                      onDelete={handleDeleteProcess}
                      formatDate={formatDate}
                    />
                  ))}
                </div>
              )}
            </motion.div>
          )}
        </div>

        {selectedProcess && (
          <div className="hidden lg:block lg:w-1/3">
            <ProcessPreview 
              process={selectedProcess} 
              onClose={() => setSelectedProcess(null)}
            />
          </div>
        )}
      </div>

      {selectedProcess && (
        <div className="lg:hidden fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
          <div className="bg-background rounded-lg max-w-md w-full max-h-[80vh] overflow-auto">
            <ProcessPreview 
              process={selectedProcess} 
              onClose={() => setSelectedProcess(null)}
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default ProcessLibraryPage;