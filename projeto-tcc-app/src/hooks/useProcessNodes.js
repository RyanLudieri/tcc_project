import { useState, useCallback, useEffect } from 'react';
import { v4 as uuidv4 } from 'uuid';
import { 
  arrayMove, 
  insertNode, 
  removeNode, 
  updateNode, 
  findNode, 
  getParentId, 
  getFlatNodes, 
  getDragDepth, 
  getMaxDepth, 
  getMinDepth, 
  getNodeIndex, 
  countChildren, 
  getPredecessorIdsForNode, 
  transformNodesForBackend as originalTransformNodesForBackend 
} from '@/lib/nodeUtils';
import { useToast } from "@/components/ui/use-toast";

const ALLOWED_CHILDREN = {
  Process: ["Phase"],
  Phase: ["Iteration", "Milestone", "Activity", "Task"],
  Iteration: ["Milestone", "Activity", "Task"],
  Activity: ["Activity", "Task", "Milestone"],
  Task: ["Artifact", "Role"],
  Milestone: [],
  Artifact: [],
  Role: [],
};

const canInsert = (parentType, childType) => {
  if (!parentType || !childType) return false;
  const allowed = ALLOWED_CHILDREN[parentType] || [];
  return allowed.includes(childType);
};

const initialNodesData = [
  {
    id: 'root-process',
    presentationName: 'Meu Processo de Desenvolvimento Ágil',
    optional: false,
    type: 'Process',
    index: null,
    parentId: null,
    predecessors: [],
    description: 'Processo principal para desenvolvimento de software usando metodologia ágil.',
    modelInfo: '',
    children: [
      {
        id: uuidv4(),
        optional: false,
        presentationName: 'Fase de Iniciação',
        type: 'Phase',
        index: "1",
        parentId: 'root-process',
        predecessors: [],
        description: 'Definição do escopo e objetivos do projeto.',
        modelInfo: 'Obrigatório',
        children: [
          {
            id: uuidv4(),
            optional: false,
            presentationName: 'Reunião de Kick-off',
            type: 'Activity',
            index: "2",
            parentId: null, 
            predecessors: [],
            description: 'Alinhamento inicial com stakeholders.',
            modelInfo: '',
            children: [
              {
                id: uuidv4(),
                optional: false,
                presentationName: 'Definir Pauta',
                type: 'Task',
                index: "3",
                parentId: null,
                predecessors: [],
                description: 'Preparar os tópicos da reunião.',
                modelInfo: 'Essencial',
                children: [
                  {
                    id: uuidv4(),
                    optional: false,
                    presentationName: 'Apresentação do Projeto',
                    type: 'Artifact',
                    index: null,
                    parentId: null,
                    predecessors: [],
                    description: 'Slides com visão geral do projeto.',
                    modelInfo: 'Output',
                    children: []
                  },
                  {
                    id: uuidv4(),
                    optional: false,
                    presentationName: 'Analista de Requisitos',
                    type: 'Role',
                    index: null,
                    parentId: null,
                    predecessors: [],
                    description: 'Responsável por levantar e documentar requisitos.',
                    modelInfo: 'Primary Performer',
                    children: []
                  }
                ]
              }
            ]
          }
        ]
      },
      {
        id: uuidv4(),
        optional: false,
        presentationName: 'Fase de Desenvolvimento',
        type: 'Phase',
        index: "4",
        parentId: 'root-process',
        predecessors: [], 
        description: 'Construção do software em sprints.',
        modelInfo: 'Iterativo',
        children: [
          {
            id: uuidv4(),
            optional: false,
            presentationName: 'Sprint 1',
            type: 'Iteration',
            index: "5",
            parentId: null,
            predecessors: [],
            description: 'Primeiro ciclo de desenvolvimento.',
            modelInfo: '',
            children: [
              {
                id: uuidv4(),
                optional: false,
                presentationName: 'Desenvolver Feature X',
                type: 'Activity',
                index: "6",
                parentId: null,
                predecessors: [],
                description: 'Implementar a funcionalidade X.',
                modelInfo: '',
                children: [
                  {
                    id: uuidv4(),
                    optional: false,
                    presentationName: 'Codificar Módulo A',
                    type: 'Task',
                    index: "7",
                    parentId: null,
                    predecessors: [],
                    description: 'Escrever o código para o módulo A.',
                    modelInfo: '',
                    children: [
                      {
                        id: uuidv4(),
                        optional: false,
                        presentationName: 'Desenvolvedor Backend',
                        type: 'Role',
                        index: null,
                        parentId: null,
                        predecessors: [],
                        description: 'Responsável pela lógica do servidor.',
                        modelInfo: 'Primary Performer',
                        children: []
                      },
                      {
                        id: uuidv4(),
                        optional: false,
                        presentationName: 'Código Fonte Feature X',
                        type: 'Artifact',
                        index: null,
                        parentId: null,
                        predecessors: [],
                        description: 'Repositório com o código da funcionalidade X.',
                        modelInfo: 'Output',
                        children: []
                      }
                    ]
                  }
                ]
              }
            ]
          },
          {
            id: uuidv4(),
            optional: false,
            presentationName: 'Revisão de Código',
            type: 'Milestone',
            index: "8",
            parentId: null,
            predecessors: [], 
            description: 'Ponto de verificação da qualidade do código.',
            modelInfo: 'Critico',
            children: []
          }
        ]
      },
      {
        id: uuidv4(),
        optional: false,
        presentationName: 'Fase de Testes',
        type: 'Phase',
        index: "9",
        parentId: 'root-process',
        predecessors: [],
        description: 'Garantia da qualidade do software.',
        modelInfo: '',
        children: [
          {
            id: uuidv4(),
            optional: false,
            presentationName: 'Testar Feature X',
            type: 'Activity',
            index: "10",
            parentId: null,
            predecessors: [],
            description: 'Verificar se a funcionalidade X atende aos requisitos.',
            modelInfo: '',
            children: [
              {
                id: uuidv4(),
                optional: false,
                presentationName: 'Conferir funcionalidade X',
                type: 'Task',
                index: "11",
                parentId: null,
                predecessors: [],
                description: 'Escrever o código para o módulo A.',
                modelInfo: '',
                children: [
                  {
                    id: uuidv4(),
                    optional: false,
                    presentationName: 'Relatório de Testes',
                    type: 'Artifact',
                    index: null,
                    parentId: null,
                    predecessors: [],
                    description: 'Documento com os resultados dos testes.',
                    modelInfo: 'Output',
                    children: []
                  },
                  {
                    id: uuidv4(),
                    optional: false,
                    presentationName: 'Engenheiro de QA',
                    type: 'Role',
                    index: null,
                    parentId: null,
                    predecessors: [],
                    description: 'Responsável pela execução dos testes.',
                    modelInfo: 'Primary Performer',
                    children: []
                  }
                ]
              }
            ]
          },
          {
            id: uuidv4(),
            optional: false,
            presentationName: 'Fase de Implantação',
            type: 'Phase',
            index: "12",
            parentId: 'root-process',
            predecessors: [],
            description: 'Colocar o software em produção.',
            modelInfo: '',
            children: [
              {
                id: uuidv4(),
                optional: false,
                presentationName: 'Deploy em Produção',
                type: 'Activity',
                index: "13",
                parentId: null,
                predecessors: [],
                description: 'Publicar a nova versão do software.',
                modelInfo: '',
                children: [
                  {
                    id: uuidv4(),
                    optional: false,
                    presentationName: 'Fazer deploy',
                    type: 'Task',
                    index: "14",
                    parentId: null,
                    predecessors: [],
                    description: 'Publicar a nova versão do software.',
                    modelInfo: '',
                    children: [
                      {
                        id: uuidv4(),
                        optional: false,
                        presentationName: 'Manual do Usuário',
                        type: 'Artifact',
                        index: null,
                        parentId: null,
                        predecessors: [],
                        description: 'Guia para usuários finais.',
                        modelInfo: 'Output',
                        children: []
                      }
                    ]
                  },
                ]
              },
            ]
          }
        ]
      }
    ]
  }
];
const assignParentIds = (nodes, parentId = null) => {
  return nodes.map(node => {
    const newNode = { ...node, parentId: parentId };
    if (node.children) {
      newNode.children = assignParentIds(node.children, node.id);
    }
    return newNode;
  });
};
const initialNodesWithParents = assignParentIds(initialNodesData);
const flattenedInitialNodes = getFlatNodes(initialNodesWithParents);

export const useProcessNodes = (processId) => {

  const { toast } = useToast();
  const [nodes, setNodes] = useState(() => {
    const savedNodes = localStorage.getItem(`processNodes_${processId}`);
    if (savedNodes) {
      try {
        const parsedNodes = JSON.parse(savedNodes);
        return parsedNodes.length > 0 ? parsedNodes : flattenedInitialNodes;
      } catch (error) {
        console.error("Failed to parse nodes from localStorage", error);
        return flattenedInitialNodes;
      }
    }
    return flattenedInitialNodes;
  });

  const [selectedNodeId, setSelectedNodeId] = useState(null);
  const [openStates, setOpenStates] = useState(() => {
    const initialOpenStates = {};
    flattenedInitialNodes.forEach(node => {
      if (node.children && node.children.length > 0) {
        initialOpenStates[node.id] = true; 
      }
    });
    return initialOpenStates;
  });

  const [activeDragItemId, setActiveDragItemId] = useState(null);
  const [dropTargetInfo, setDropTargetInfo] = useState(null);
  const activeDragItem = activeDragItemId ? findNode(nodes, activeDragItemId) : null;

  useEffect(() => {
    try {
      localStorage.setItem(`processNodes_${processId}`, JSON.stringify(nodes));
    } catch (error)
{
      console.error("Failed to save nodes to localStorage", error);
      toast({
        title: "Erro ao Salvar",
        description: "Não foi possível salvar as alterações no armazenamento local. Verifique o espaço disponível.",
        variant: "destructive",
      });
    }
  }, [nodes, processId, toast]);

  const handleNodeSelect = useCallback((nodeId) => {
    setSelectedNodeId(nodeId);
  }, []);

  const handleToggleOpen = useCallback((nodeId) => {
    setOpenStates(prev => ({ ...prev, [nodeId]: !prev[nodeId] }));
  }, []);

  const addNodeInternal = useCallback((nodeData, parentId = null) => {
    const actualParentId = parentId || (selectedNodeId || (nodes.find(n => n.type === 'Process') || nodes[0])?.id);
    const parentNode = findNode(nodes, actualParentId);

    if (!canInsert(parentNode?.type, nodeData.type)) {
      return { success: false, error: `Not allowed to insert "${nodeData.type}" inside "${parentNode?.type || 'Root'}".` };
    }

    let newIndex = null;
    if (nodeData.type !== 'Artifact' && nodeData.type !== 'Role') {
        const siblings = parentNode ? nodes.filter(n => n.parentId === actualParentId && n.type !== 'Artifact' && n.type !== 'Role') : nodes.filter(n => !n.parentId && n.type !== 'Artifact' && n.type !== 'Role');
        const lastSiblingWithIndex = [...siblings].sort((a, b) => {
            if (a.index === null) return 1;
            if (b.index === null) return -1;
            const aParts = String(a.index).split('.').map(Number);
            const bParts = String(b.index).split('.').map(Number);
            for (let i = 0; i < Math.min(aParts.length, bParts.length); i++) {
                if (aParts[i] !== bParts[i]) return aParts[i] - bParts[i];
            }
            return aParts.length - bParts.length;
        }).pop();

        if (parentNode && parentNode.index !== null) {
            const parentIndexParts = String(parentNode.index).split('.');
            let lastSiblingPart = 0;
            if(lastSiblingWithIndex && lastSiblingWithIndex.index) {
                const lastSiblingIndexParts = String(lastSiblingWithIndex.index).split('.');
                lastSiblingPart = parseInt(lastSiblingIndexParts[lastSiblingIndexParts.length -1], 10);
            }
            newIndex = `${parentNode.index}.${lastSiblingPart + 1}`;

        } else if (parentNode && parentNode.index === null && parentNode.type === 'Process') {
             const topLevelSiblings = nodes.filter(n => n.parentId === actualParentId && n.type !== 'Artifact' && n.type !== 'Role');
             newIndex = topLevelSiblings.length > 0 ? Math.max(...topLevelSiblings.map(s => parseInt(String(s.index).split('.')[0], 10))) + 1 : 1;
        } else {
            newIndex = lastSiblingWithIndex ? parseFloat(String(lastSiblingWithIndex.index).split('.').pop()) + 1 : (siblings.length + 1);
        }
        newIndex = String(newIndex);
    }

    const newNode = {
      id: uuidv4(),
      ...nodeData,
      parentId: actualParentId,
      index: newIndex,
      children: [],
      predecessors: nodeData.predecessors || [],
      optional: false
    };
    
    setNodes(prevNodes => insertNode(prevNodes, newNode));
    if (actualParentId) {
      setOpenStates(prev => ({ ...prev, [actualParentId]: true }));
    }
    setSelectedNodeId(newNode.id);
    return { success: true, newNode };
  }, [selectedNodeId, nodes]);

  const updateNodeDataInternal = useCallback((nodeId, dataToUpdate) => {
    setNodes(prevNodes => {
        let newNodes = updateNode(prevNodes, nodeId, dataToUpdate);
        if (dataToUpdate.predecessors !== undefined) {
            const affectedNode = findNode(newNodes, nodeId);
            if (affectedNode) {
                const updatedPredecessors = dataToUpdate.predecessors.map(p => typeof p === 'object' ? p.id : p);
                newNodes = updateNode(newNodes, nodeId, { predecessors: updatedPredecessors });
            }
        }
        return newNodes;
    });
  }, []);
  
  const deleteNodeInternal = useCallback((nodeId) => {
    const nodeToDelete = findNode(nodes, nodeId);
    if (!nodeToDelete) return { success: false, error: "Node not found." };

    if (nodeToDelete.type === 'Process') {
      return { success: false, error: "The root 'Process' node cannot be deleted." };
    }
    
    setNodes(prevNodes => removeNode(prevNodes, nodeId));
    if (selectedNodeId === nodeId) {
      setSelectedNodeId(nodeToDelete.parentId || null);
    }
    return { success: true, deletedNodeName: nodeToDelete.presentationName };
  }, [nodes, selectedNodeId]);

  const deleteAllNodesInternal = useCallback(() => {
    const processNode = nodes.find(node => node.type === 'Process');
    if (processNode) {
      const newNodes = [{ ...processNode, children: [], predecessors: [] }];
      setNodes(getFlatNodes(assignParentIds(newNodes)));
      setSelectedNodeId(processNode.id); 
      setOpenStates({ [processNode.id]: true });
    } else {
       setNodes([]);
       setSelectedNodeId(null);
       setOpenStates({});
    }
  }, [nodes]);
  
  const reorderAndReindexNodes = (newOrderedNodes) => {
    const reindexRecursively = (items, currentParentId = null, prefix = "") => {
      let elementCounter = 1;
      items.forEach(item => {
        if (item.parentId === currentParentId) {
          if (item.type !== 'Artifact' && item.type !== 'Role') {
            item.index = prefix ? `${prefix}.${elementCounter}` : `${String(elementCounter)}`;
            elementCounter++;
          } else {
            item.index = null; 
          }
          const childrenOfCurrentItem = newOrderedNodes.filter(n => n.parentId === item.id);
          if (childrenOfCurrentItem.length > 0) {
            reindexRecursively(childrenOfCurrentItem, item.id, item.index || prefix); 
          }
        }
      });
    };
  
    const rootProcessNode = newOrderedNodes.find(n => n.type === 'Process');
    const rootParentId = rootProcessNode ? rootProcessNode.id : null;
    
    const topLevelNodes = newOrderedNodes.filter(node => node.parentId === rootParentId || (rootParentId === null && !node.parentId));
    reindexRecursively(topLevelNodes, rootParentId, "");

    return newOrderedNodes;
  };

  const handleDragStartLogic = useCallback((draggedNodeId) => {
    setActiveDragItemId(draggedNodeId);
  }, []);

  const handleDragOverLogic = useCallback((activeId, overId, activeRect, overRect) => {
    if (!overId || activeId === overId) {
      setDropTargetInfo(null);
      return;
    }
  
    const activeNode = findNode(nodes, activeId);
    const overNode = findNode(nodes, overId);
    if (!activeNode || !overNode) return;
  
    const overNodeDepth = getDragDepth(overNode.id, nodes);
    
    let position = 'child'; 
    if (overRect && activeRect) {
        const hoverOffsetY = activeRect.top + activeRect.height / 2 - overRect.top;
        if (hoverOffsetY < overRect.height * 0.25) {
          position = 'before';
        } else if (hoverOffsetY > overRect.height * 0.75) {
          position = 'after';
        }
    }
    
    const isSameParent = activeNode.parentId === overNode.parentId;
    const isDirectChildAttempt = activeNode.parentId === overNode.id;

    if (position === 'child' && overNode.type === 'Artifact' || overNode.type === 'Role') {
        position = 'after';
    }
    if (activeNode.type === 'Process') {
        setDropTargetInfo(null);
        return;
    }
    if (position === 'child' && (overNode.type === 'Task' || overNode.type === 'Milestone')) {
        if (activeNode.type !== 'Artifact' && activeNode.type !== 'Role') {
            position = 'after';
        }
    }

    let tentativeParent = null;

    if (position === 'child') {
      tentativeParent = overNode; // will become child of overNode
    } else if (position === 'before' || position === 'after') {
      tentativeParent = findNode(nodes, overNode.parentId); // keeps overNode's parent
    }

    if (!canInsert(tentativeParent?.type, activeNode.type)) {
      setDropTargetInfo(null);
      return;
    }


    setDropTargetInfo({
      nodeId: overNode.id,
      position: position,
      depth: overNodeDepth,
    });
  }, [nodes]);

  const handleDragEndLogicInternal = (activeId, currentDropTargetInfo) => {
    setActiveDragItemId(null);
    setDropTargetInfo(null);

    if (!currentDropTargetInfo || !activeId || activeId === currentDropTargetInfo.nodeId) {
      return { success: false, error: "No valid drop target." };
    }

    const activeNode = findNode(nodes, activeId);
    const overNode   = findNode(nodes, currentDropTargetInfo.nodeId);
    if (!activeNode || !overNode) {
      return { success: false, error: "Node not found." };
    }
    if (activeNode.type === 'Process') {
      return { success: false, error: "The root Process node cannot be moved." };
    }

    // ✅ NEW: determine new parent and validate with centralized rules
    let newParentId;
    if (currentDropTargetInfo.position === 'child') {
      newParentId = overNode.id;            // will become a child of overNode
    } else {
      newParentId = overNode.parentId;      // before/after keeps overNode's parent
    }
    const newParentNode = findNode(nodes, newParentId);
    if (!canInsert(newParentNode?.type, activeNode.type)) {
      return {
        success: false,
        error: `Not allowed to move "${activeNode.type}" inside "${newParentNode?.type || 'Root'}".`
      };
    }

    // Move and reindex (your current flow)
    let newNodes = [...nodes];
    const fromIdx = newNodes.findIndex(n => n.id === activeNode.id);
    const [movedNode] = newNodes.splice(fromIdx, 1);

    const overIdx = newNodes.findIndex(n => n.id === overNode.id);
    let insertionIndex = overIdx;

    if (currentDropTargetInfo.position === 'before') {
      insertionIndex = overIdx;
    } else if (currentDropTargetInfo.position === 'after') {
      insertionIndex = overIdx + 1;
    } else { // child
      const childrenOfOver = newNodes.filter(n => n.parentId === overNode.id);
      insertionIndex = overIdx + 1 + childrenOfOver.length;
    }

    movedNode.parentId = newParentId;
    newNodes.splice(insertionIndex, 0, movedNode);

    const reorderedNodes = reorderAndReindexNodes([...newNodes]);
    setNodes(reorderedNodes);
    if (newParentId) setOpenStates(prev => ({ ...prev, [newParentId]: true }));

    return { success: true, message: `"${activeNode.presentationName}" was reorganized.` };
  };
  
  const transformNodesForBackend = useCallback(() => {
    return originalTransformNodesForBackend(nodes);
  }, [nodes]);

  const selectedNodeDetails = selectedNodeId ? findNode(nodes, selectedNodeId) : null;
  const selectedNodePredecessors = selectedNodeDetails ? getPredecessorIdsForNode(nodes, selectedNodeId) : [];

  const getChildNodes = useCallback((parentId) => {
    return nodes.filter(node => node.parentId === parentId);
  }, [nodes]);

  const getAllowedChildTypes = useCallback((parentType) => {
    if (!parentType) return [];
    return ALLOWED_CHILDREN[parentType] || [];
  }, []);

  return {
    nodes,
    setNodes,
    selectedNodeId,
    selectedNodeDetails,
    selectedNodePredecessors,
    openStates,
    activeDragItem,
    dropTargetInfo,
    handleNodeSelect,
    handleToggleOpen,
    addNode: addNodeInternal,
    updateNode: updateNodeDataInternal,
    deleteNode: deleteNodeInternal,
    deleteAllNodes: deleteAllNodesInternal,
    handleDragStartLogic,
    handleDragOverLogic,
    handleDragEndLogic: handleDragEndLogicInternal,
    transformNodesForBackend,
    findNode: (nodeId) => findNode(nodes, nodeId),
    getChildNodes,
    getAllowedChildTypes,
    flattenedNodes: getFlatNodes(assignParentIds(nodes)), 
  };
};