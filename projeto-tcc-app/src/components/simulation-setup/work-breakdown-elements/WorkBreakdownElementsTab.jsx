import React, { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import WorkElementDetailsView from "./WorkElementDetailsView.jsx";
import { NodeIcon } from "../../process-editor/tree/NodeIcon.jsx";

function capitalizeFirst(str) {
    return str ? str.charAt(0).toUpperCase() + str.slice(1).toLowerCase() : "";
}

const WorkBreakdownElementsTab = ({ processId }) => {
    const [selectedItem, setSelectedItem] = useState(null);
    const [processTree, setProcessTree] = useState([]);

    useEffect(() => {
        if (!processId || processId === "new") return;

        fetch(`http://localhost:8080/activity-configs/process/${processId}`)
            .then((res) => {
                if (!res.ok) throw new Error("Failed to fetch activities");
                return res.json();
            })
            .then((data) => {
                const tree = buildTree(data);
                setProcessTree(tree);
            })
            .catch((err) => console.error("Failed to fetch activities:", err));
    }, [processId]);

    const buildTree = (flatList) => {
        if (!Array.isArray(flatList)) return [];

        // Create map of all nodes
        const map = {};
        flatList.forEach((item) => {
            map[item.activityId] = {
                id: item.activityId,
                activityConfigId: item.activityConfigId,
                presentationName: item.name,
                type: item.type.toUpperCase(),
                children: [],
            };
        });

        // Build parent-child relationships
        const roots = [];
        flatList.forEach((item) => {
            const node = map[item.activityId];
            if (item.parentId && map[item.parentId]) {
                map[item.parentId].children.push(node);
            } else {
                roots.push(node);
            }
        });

        const processName = flatList[0]?.processName || "Process";
        const processId = flatList[0]?.processId || 0;

        return [
            {
                id: processId,
                presentationName: processName,
                type: "PROCESS",
                children: roots,
            },
        ];
    };

    const renderNode = (node) => (
        <div key={node.id} className="pl-4">
            <button
                onClick={() => setSelectedItem(node)}
                className={`flex items-center gap-1.5 p-1.5 rounded-lg w-fit ${
                    selectedItem?.id === node.id ? "bg-gray-100" : "hover:bg-gray-100"
                }`}
            >
                <NodeIcon
                    type={capitalizeFirst(node.type || "Task")}
                    className="mr-2 flex-shrink-0 h-4 w-4"
                />
                <span>{node.presentationName}</span>
            </button>

            {/* Recursive children */}
            {node.children && node.children.length > 0 && (
                <div className="pl-6 mt-1 space-y-1">
                    {node.children.map((child) => renderNode(child))}
                </div>
            )}
        </div>
    );

    return (
        <div className="flex w-full h-full gap-10 p-6">
            {/* Left side - Work Breakdown Elements View */}
            <div className="flex-1 mb-4">
                <Card className="h-full shadow-lg">
                    <CardHeader className="py-3">
                        <CardTitle className="text-2xl text-primary">
                            Work Breakdown Elements View
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            {processTree.map((process) => (
                                <div key={process.id} className="pl-2">
                                    <div className="flex items-center gap-2 p-2 w-fit">
                                        <NodeIcon
                                            type={capitalizeFirst(process.type)}
                                            className="mr-2 flex-shrink-0 h-4 w-4"
                                        />
                                        <span>{process.presentationName}</span>
                                    </div>
                                    <div className="pl-6 mt-1 space-y-1">
                                        {process.children.map((child) => renderNode(child))}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </CardContent>
                </Card>
            </div>

            {/* Right side - Work Element Details View */}
            <div className="w-[60%] mb-4">
                <WorkElementDetailsView selectedItem={selectedItem} />
            </div>
        </div>
    );
};

export default WorkBreakdownElementsTab;
