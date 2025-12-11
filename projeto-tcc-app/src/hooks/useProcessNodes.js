import { useState, useCallback, useEffect } from 'react';
import { v4 as uuidv4 } from 'uuid';
import {
  insertNode,
  removeNode,
  updateNode,
  findNode,
  getFlatNodes,
  getDragDepth,
  getPredecessorIdsForNode,
  transformNodesForBackend as originalTransformNodesForBackend
} from '@/lib/nodeUtils';
import { useToast } from "@/components/ui/use-toast";
// import useLocalStorage from "@/hooks/useLocalStorage.js"; // Removido para simplificar e garantir a lógica interna

// DEFINIÇÕES CONSTANTES
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

const assignParentIds = (nodes, parentId = null) => {
  return nodes.map(node => {
    const newNode = { ...node, parentId: parentId };
    if (node.children) {
      newNode.children = assignParentIds(node.children, node.id);
    }
    return newNode;
  });
};

// DADOS INICIAIS
const initialNodesData = [
  {
    id: "root-process",
    presentationName: "DevOps_Process_Simulation",
    optional: false,
    type: "Process",
    index: null,
    parentId: null,
    predecessors: [],
    description: "",
    modelInfo: "",
    children: [
      // PROCESS ELEMENTS (10 tasks)
      {
        id: uuidv4(),
        presentationName: "Implement",
        type: "Task",
        index: 1,
        parentId: "root-process",
        optional: false,
        predecessors: [],
        description: "",
        modelInfo: "",
        children: [
          // methodElements with parentIndex 1
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "MANDATORY_INPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "OUTPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "Team_Developers",
            type: "Role",
            optional: false,
            description: "",
            modelInfo: "PRIMARY_PERFORMER",
            children: []
          }
        ]
      },
      {
        id: uuidv4(),
        presentationName: "Review",
        type: "Task",
        index: 2,
        parentId: "root-process",
        optional: false,
        predecessors: [1],
        description: "",
        modelInfo: "",
        children: [
          // parentIndex 2
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "MANDATORY_INPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "OUTPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "Team_Developers",
            type: "Role",
            optional: false,
            description: "",
            modelInfo: "PRIMARY_PERFORMER",
            children: []
          }
        ]
      },
      {
        id: uuidv4(),
        presentationName: "Committed",
        type: "Task",
        index: 3,
        parentId: "root-process",
        optional: false,
        predecessors: [2],
        description: "",
        modelInfo: "",
        children: [
          // parentIndex 3
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "MANDATORY_INPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "OUTPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "Team_Developers",
            type: "Role",
            optional: false,
            description: "",
            modelInfo: "PRIMARY_PERFORMER",
            children: []
          }
        ]
      },
      {
        id: uuidv4(),
        presentationName: "Static analysis",
        type: "Task",
        index: 4,
        parentId: "root-process",
        optional: false,
        predecessors: [3],
        description: "",
        modelInfo: "",
        children: [
          // parentIndex 4
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "MANDATORY_INPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "OUTPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "Server_CI_CD",
            type: "Role",
            optional: false,
            description: "",
            modelInfo: "PRIMARY_PERFORMER",
            children: []
          }
        ]
      },
      {
        id: uuidv4(),
        presentationName: "Unit testing",
        type: "Task",
        index: 5,
        parentId: "root-process",
        optional: false,
        predecessors: [4],
        description: "",
        modelInfo: "",
        children: [
          // parentIndex 5
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "MANDATORY_INPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "OUTPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "Server_CI_CD",
            type: "Role",
            optional: false,
            description: "",
            modelInfo: "PRIMARY_PERFORMER",
            children: []
          }
        ]
      },
      {
        id: uuidv4(),
        presentationName: "Build validation",
        type: "Task",
        index: 6,
        parentId: "root-process",
        optional: false,
        predecessors: [5],
        description: "",
        modelInfo: "",
        children: [
          // parentIndex 6
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "MANDATORY_INPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "OUTPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "Server_CI_CD",
            type: "Role",
            optional: false,
            description: "",
            modelInfo: "PRIMARY_PERFORMER",
            children: []
          }
        ]
      },
      {
        id: uuidv4(),
        presentationName: "Integration tests",
        type: "Task",
        index: 7,
        parentId: "root-process",
        optional: false,
        predecessors: [6],
        description: "",
        modelInfo: "",
        children: [
          // parentIndex 7
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "MANDATORY_INPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "OUTPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "Server_CI_CD",
            type: "Role",
            optional: false,
            description: "",
            modelInfo: "PRIMARY_PERFORMER",
            children: []
          }
        ]
      },
      {
        id: uuidv4(),
        presentationName: "Staging deployment",
        type: "Task",
        index: 8,
        parentId: "root-process",
        optional: false,
        predecessors: [7],
        description: "",
        modelInfo: "",
        children: [
          // parentIndex 8
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "MANDATORY_INPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "OUTPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "Server_Staging",
            type: "Role",
            optional: false,
            description: "",
            modelInfo: "PRIMARY_PERFORMER",
            children: []
          }
        ]
      },
      {
        id: uuidv4(),
        presentationName: "Release approval",
        type: "Task",
        index: 9,
        parentId: "root-process",
        optional: false,
        predecessors: [8],
        description: "",
        modelInfo: "",
        children: [
          // parentIndex 9
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "MANDATORY_INPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "OUTPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "Manager",
            type: "Role",
            optional: false,
            description: "",
            modelInfo: "PRIMARY_PERFORMER",
            children: []
          }
        ]
      },
      {
        id: uuidv4(),
        presentationName: "Monitor",
        type: "Task",
        index: 10,
        parentId: "root-process",
        optional: false,
        predecessors: [9],
        description: "",
        modelInfo: "",
        children: [
          // parentIndex 10
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "MANDATORY_INPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "User Story",
            type: "Artifact",
            optional: false,
            description: "",
            modelInfo: "OUTPUT",
            children: []
          },
          {
            id: uuidv4(),
            presentationName: "Server_Staging",
            type: "Role",
            optional: false,
            description: "",
            modelInfo: "PRIMARY_PERFORMER",
            children: []
          }
        ]
      }
    ]
  }
];
const initialNodesWithParents = assignParentIds(initialNodesData);
const flattenedInitialNodes = getFlatNodes(initialNodesWithParents);


// FUNÇÃO DE REORDENAÇÃO (Movemos para fora do hook para evitar recriação desnecessária)
const reorderAndReindexNodes = (newOrderedNodes) => {
  let counter = 1;

  const assignGlobalIndices = (nodesList, parentId = null) => {
    // Filtra apenas os filhos diretos do parentId e itera
    nodesList
        .filter(n => n.parentId === parentId)
        .forEach(node => {
          // Apenas elementos de fluxo (não Artifact ou Role) recebem o índice global
          if (node.type !== 'Artifact' && node.type !== 'Role') {
            node.index = String(counter++);
          } else {
            node.index = null;
          }
          // Recursão para processar os filhos
          assignGlobalIndices(nodesList, node.id);
        });
  };

  const rootProcessNode = newOrderedNodes.find(n => n.type === 'Process');
  const rootParentId = rootProcessNode ? rootProcessNode.id : null;

  // Começa a indexação a partir do nó raiz (Process)
  assignGlobalIndices(newOrderedNodes, rootParentId);

  return newOrderedNodes;
};


export const useProcessNodes = (processId) => {
  // ✅ 1. DEFINIÇÃO DA CHAVE DE ARMAZENAMENTO CORRETA
  const storageKey = `processNodes_${processId}`;

  const { toast } = useToast();

  // ✅ 2. CARREGAMENTO DO ESTADO INICIAL
  const [nodes, setNodes] = useState(() => {
    const savedNodes = localStorage.getItem(storageKey); // Usa a chave definida acima
    if (savedNodes) {
      try {
        const parsedNodes = JSON.parse(savedNodes);
        // Usa os dados salvos se existirem e não estiverem vazios
        return parsedNodes.length > 0 ? parsedNodes : flattenedInitialNodes;
      } catch (error) {
        console.error("Failed to parse nodes from localStorage", error);
        // Em caso de erro, usa o mock inicial
        return flattenedInitialNodes;
      }
    }
    // Se não houver nada salvo, usa o mock inicial
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

  // ✅ 3. PERSISTÊNCIA (SALVANDO)
  useEffect(() => {
    try {
      localStorage.setItem(storageKey, JSON.stringify(nodes)); // Usa a chave definida
    } catch (error)
    {
      console.error("Failed to save nodes to localStorage", error);
      toast({
        title: "Erro ao Salvar",
        description: "Não foi possível salvar as alterações no armazenamento local. Verifique o espaço disponível.",
        variant: "destructive",
        duration: 1000,
      });
    }
  }, [nodes, storageKey, toast]); // storageKey é constante, mas listada para clareza

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

    const newNode = {
      id: uuidv4(),
      ...nodeData,
      parentId: actualParentId,
      index: null, // será recalculado globalmente
      children: [],
      predecessors: nodeData.predecessors || [],
      optional: false
    };

    setNodes(prevNodes => {
      const insertedNodes = insertNode(prevNodes, newNode);
      return reorderAndReindexNodes([...insertedNodes]);
    });

    if (actualParentId) setOpenStates(prev => ({ ...prev, [actualParentId]: true }));
    setSelectedNodeId(newNode.id);

    return { success: true, newNode };
  }, [selectedNodeId, nodes]);

  const updateNodeDataInternal = useCallback((nodeId, dataToUpdate) => {
    setNodes(prevNodes => {
      let newNodes = updateNode(prevNodes, nodeId, dataToUpdate);
      if (dataToUpdate.predecessors !== undefined) {
        const affectedNode = findNode(newNodes, nodeId);
        if (affectedNode) {
          // Mapeia para garantir que predecessores sejam apenas IDs (strings ou números)
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

    setNodes(prevNodes => {
      const removedNodes = removeNode(prevNodes, nodeId);
      return reorderAndReindexNodes(removedNodes); // Reindexa após remover
    });

    if (selectedNodeId === nodeId) {
      setSelectedNodeId(nodeToDelete.parentId || null);
    }
    return { success: true, deletedNodeName: nodeToDelete.presentationName };
  }, [nodes, selectedNodeId]);

  const deleteAllNodesInternal = useCallback(() => {
    const processNode = nodes.find(node => node.type === 'Process');
    if (processNode) {
      // Cria um novo nó Process sem filhos e reindexa
      const newRootNode = { ...processNode, children: [], predecessors: [] };
      const newNodes = getFlatNodes(assignParentIds([newRootNode]));
      setNodes(reorderAndReindexNodes(newNodes));
      setSelectedNodeId(processNode.id);
      setOpenStates({ [processNode.id]: true });
    } else {
      setNodes([]);
      setSelectedNodeId(null);
      setOpenStates({});
    }
  }, [nodes]);

// OBS: A função reorderAndReindexNodes foi movida para fora do hook.

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

    // Regras de Drag&Drop
    if (activeNode.type === 'Process') {
      setDropTargetInfo(null);
      return;
    }
    // Não pode ser filho de Artifact ou Role, volta para "after"
    if (position === 'child' && (overNode.type === 'Artifact' || overNode.type === 'Role')) {
      position = 'after';
    }
    // Task/Activity/Milestone não podem ter Task/Activity/Milestone como filhos, apenas Artifact/Role
    if (position === 'child' && (overNode.type === 'Task' || overNode.type === 'Activity' || overNode.type === 'Milestone')) {
      if (activeNode.type !== 'Artifact' && activeNode.type !== 'Role') {
        position = 'after';
      }
    }


    let tentativeParent = null;

    if (position === 'child') {
      tentativeParent = overNode; // O nó ativo se tornará filho de overNode
    } else if (position === 'before' || position === 'after') {
      tentativeParent = findNode(nodes, overNode.parentId); // O nó ativo manterá o pai de overNode
    }

    // Validação de inserção
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

    // 1. Determina o novo ParentId e faz a validação final
    let newParentId;
    if (currentDropTargetInfo.position === 'child') {
      newParentId = overNode.id;
    } else {
      newParentId = overNode.parentId;
    }
    const newParentNode = findNode(nodes, newParentId);
    if (!canInsert(newParentNode?.type, activeNode.type)) {
      return {
        success: false,
        error: `Not allowed to move "${activeNode.type}" inside "${newParentNode?.type || 'Root'}".`
      };
    }

    // 2. Realiza a movimentação (atualizando a estrutura plana)
    let newNodes = [...nodes];
    const fromIdx = newNodes.findIndex(n => n.id === activeNode.id);
    const [movedNode] = newNodes.splice(fromIdx, 1); // Remove o nó

    const overIdx = newNodes.findIndex(n => n.id === overNode.id);
    let insertionIndex;

    if (currentDropTargetInfo.position === 'before') {
      insertionIndex = overIdx;
    } else if (currentDropTargetInfo.position === 'after') {
      insertionIndex = overIdx + 1;
    } else { // 'child'
      // Insere o nó movido APÓS todos os filhos existentes do overNode (se overNode for o novo pai)
      const childrenOfOver = newNodes.filter(n => n.parentId === overNode.id);
      insertionIndex = overIdx + 1 + childrenOfOver.length;
    }

    movedNode.parentId = newParentId;
    newNodes.splice(insertionIndex, 0, movedNode); // Insere o nó na nova posição

    // 3. Reordena e Reindexa (fundamental para atualizar `index` e `parentId` corretos)
    const reorderedNodes = reorderAndReindexNodes([...newNodes]);
    setNodes(reorderedNodes);

    // 4. Abre o novo pai no Tree View
    if (newParentId) setOpenStates(prev => ({ ...prev, [newParentId]: true }));

    return { success: true, message: `"${activeNode.presentationName}" was reorganized.` };
  };

  const transformNodesForBackend = useCallback(() => {
    return originalTransformNodesForBackend(nodes);
  }, [nodes]);

  const selectedNodeDetails = selectedNodeId ? findNode(nodes, selectedNodeId) : null;
  const selectedNodePredecessors = selectedNodeDetails ? getPredecessorIdsForNode(nodes, selectedNodeId) : [];

  const getChildNodes = useCallback((parentId) => {
    // Busca e retorna apenas os filhos diretos
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
    // Garante que o estado retornado para a UI esteja sempre com parentIds corretos
    flattenedNodes: nodes, // O hook já manipula nodes como a lista plana atualizada
  };
};