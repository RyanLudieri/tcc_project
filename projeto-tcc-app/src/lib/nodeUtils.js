import React from 'react';

export const arrayMove = (array, from, to) => {
  const newArray = [...array];
  const [item] = newArray.splice(from, 1);
  newArray.splice(to, 0, item);
  return newArray;
};

export const generateNodeIndices = (nodes) => {
  const nodesWithIndices = [...nodes]; 
  let typeNodeCounter = 1;

  nodesWithIndices.forEach(node => {
    if (node.type && node.type !== 'None' && node.type !== 'Process') {
      node.index = typeNodeCounter.toString();
      typeNodeCounter++;
    } else {
      node.index = ''; 
    }
  });
  
  return nodesWithIndices.map(({ children, ...node }) => node);
};

const mapFrontendTypeToBackendProcessType = (type) => {
  const mapping = {
    "Phase": "PHASE",
    "Iteration": "ITERATION",
    "Milestone": "MILESTONE",
    "Activity": "ACTIVITY",
    "Task": "TASK_DESCRIPTOR", 
  };
  return mapping[type] || type.toUpperCase(); 
};

const mapFrontendTypeToBackendMethodType = (type) => {
  const mapping = {
    "Artifact": "WORKPRODUCT",
    "Role": "ROLE",
  };
  return mapping[type] || type.toUpperCase();
};

const buildProcessElementsRecursive = (nodes, parentId, allNodes) => {
  return nodes
    .filter(node => node.parentId === parentId)
    .filter(node => ["Phase", "Iteration", "Milestone", "Activity", "Task"].includes(node.type))
    .map(node => {
      const predecessorsNames = (node.predecessors || [])
        .map(pId => allNodes.find(n => n.id === pId)?.presentationName)
        .filter(name => name);

      return {
        name: node.presentationName,
        type: mapFrontendTypeToBackendProcessType(node.type),
        predecessors: predecessorsNames,
        children: buildProcessElementsRecursive(nodes, node.id, allNodes),
        optional: false
      };
    });
};

export const transformNodesForBackend = (nodes) => {
  const processNode = nodes.find(node => node.type === 'Process' && !node.parentId);
  if (!processNode) {
    console.error("Process node not found");
    return null; 
  }

  const processElements = buildProcessElementsRecursive(nodes, processNode.id, nodes);
  
  const methodElements = nodes
    .filter(node => ["Artifact", "Role"].includes(node.type))
    .map(node => {
      const parentNode = nodes.find(p => p.id === node.parentId);
      const parentIndex = parentNode ? parseInt(parentNode.index, 10) : null;

      return {
        name: node.presentationName,
        type: mapFrontendTypeToBackendMethodType(node.type),
        modelInfo: node.modelInfo.toUpperCase() || "",
        parentIndex: Number.isFinite(parentIndex) ? parentIndex : null,
        optional: false
      };
    });

  const rootPredecessorsNames = (processNode.predecessors || [])
    .map(pId => nodes.find(n => n.id === pId)?.presentationName)
    .filter(name => name);

  return {
    name: processNode.presentationName,
    predecessors: rootPredecessorsNames,
    processElements: processElements,
    methodElements: methodElements,
    optional: false
  };
};

export const getFlatNodes = (nodes) => {
  const flatNodes = [];
  const flatten = (nodeList, parentId = null) => {
    nodeList.forEach(node => {
      flatNodes.push({ ...node, parentId: parentId });
      if (node.children && node.children.length > 0) {
        flatten(node.children, node.id);
      }
    });
  };
  flatten(nodes);
  return flatNodes.map(({ children, ...node }) => node); 
};

export const findNode = (nodes, nodeId) => {
  return nodes.find(node => node.id === nodeId);
};

export const getParentId = (nodes, nodeId) => {
  const node = findNode(nodes, nodeId);
  return node ? node.parentId : null;
};

export const insertNode = (nodes, newNode) => {
  return [...nodes, newNode];
};

export const updateNode = (nodes, nodeId, updates) => {
  return nodes.map(node => 
    node.id === nodeId ? { ...node, ...updates } : node
  );
};

export const removeNode = (nodes, nodeId) => {
  const nodesToRemove = [nodeId];
  const queue = [nodeId];
  while (queue.length > 0) {
    const currentParentId = queue.shift();
    nodes.forEach(node => {
      if (node.parentId === currentParentId) {
        nodesToRemove.push(node.id);
        queue.push(node.id);
      }
    });
  }
  return nodes.filter(node => !nodesToRemove.includes(node.id));
};

export const getDragDepth = (nodeId, nodes) => {
  let depth = 0;
  let currentId = nodeId;
  while (currentId) {
    const node = findNode(nodes, currentId);
    if (!node || !node.parentId) break;
    depth++;
    currentId = node.parentId;
    if (findNode(nodes, currentId)?.type === 'Process') break; 
  }
  return depth;
};

export const getMaxDepth = (nodeId, nodes) => {
  const node = findNode(nodes, nodeId);
  if (!node || !node.children || node.children.length === 0) {
    return 0;
  }
  return 1 + Math.max(...node.children.map(child => getMaxDepth(child.id, nodes)));
};

export const getMinDepth = (nodeId, nodes) => {
  const node = findNode(nodes, nodeId);
  if (!node || !node.children || node.children.length === 0) {
    return 0;
  }
  return 1 + Math.min(...node.children.map(child => getMinDepth(child.id, nodes)));
};

export const getNodeIndex = (nodes, nodeId) => {
  return nodes.findIndex(node => node.id === nodeId);
};

export const countChildren = (nodes, parentId) => {
  return nodes.filter(node => node.parentId === parentId).length;
};

export const getPredecessorIdsForNode = (nodes, nodeId) => {
  const node = findNode(nodes, nodeId);
  return node && node.predecessors ? node.predecessors : [];
};