import React, { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import WorkElementDetailsView from "./WorkElementDetailsView.jsx";
import { NodeIcon } from "../../process-editor/tree/NodeIcon.jsx";

// ---- MOCK PROCESS + ITERATIONS + TASKS ----
const mockProcess = [
    {
        id: 0,
        presentationName: "Process",
        type: "PROCESS",
        children: [
            {
                id: 1,
                presentationName: "Iteration 1",
                type: "ITERATION",
                children: [
                    {
                        id: 101,
                        presentationName: "Release planning",
                        type: "TASK_DESCRIPTOR",
                        predecessors: [],
                    },
                    {
                        id: 102,
                        presentationName: "Pair programming with TDD",
                        type: "TASK_DESCRIPTOR",
                        predecessors: [101],
                    },
                    {
                        id: 103,
                        presentationName: "Code review",
                        type: "TASK_DESCRIPTOR",
                        predecessors: [102],
                    },
                ],
            },
            {
                id: 2,
                presentationName: "Iteration 2",
                type: "ITERATION",
                children: [
                    {
                        id: 201,
                        presentationName: "Daily meeting",
                        type: "TASK_DESCRIPTOR",
                        predecessors: [],
                    },
                    {
                        id: 202,
                        presentationName: "Refactoring",
                        type: "TASK_DESCRIPTOR",
                        predecessors: [],
                    },
                ],
            },
        ],
    },
];

function capitalizeFirst(str) {
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

const WorkBreakdownElementsTab = () => {
    const [selectedItem, setSelectedItem] = useState(null);

    return (
        <div className="flex w-full h-full gap-10 p-6">
            {/* Container 1 - Work Breakdown */}
            <div className="flex-1 mb-4">
                <Card className="h-full shadow-lg">
                    <CardHeader className="py-3">
                        <CardTitle className="text-2xl text-primary">
                            Work Breakdown Elements View
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            {mockProcess.map((process) => (
                                <div key={process.id} className="pl-2">

                                    {/* Process*/}
                                    <div className="flex items-center gap-2 p-2 w-fit">
                                        <NodeIcon
                                            type={capitalizeFirst(process.type)}
                                            className="mr-2 flex-shrink-0 h-4 w-4"
                                        />
                                        <span>{process.presentationName}</span>
                                    </div>

                                    {/* Iterations e Tasks*/}
                                    <div className="pl-6 mt-1 space-y-1">
                                        {process.children.map((iteration) => (
                                            <div key={iteration.id}>
                                                <button
                                                    onClick={() => setSelectedItem(iteration)}
                                                    className={`flex items-center gap-2 p-2 rounded-lg w-fit ${
                                                        selectedItem?.id === iteration.id
                                                            ? "bg-gray-100"
                                                            : "hover:bg-gray-100"
                                                    }`}
                                                >
                                                    <NodeIcon
                                                        type={capitalizeFirst(iteration.type)}
                                                        className="mr-2 flex-shrink-0 h-4 w-4"
                                                    />
                                                    <span>{iteration.presentationName}</span>
                                                </button>

                                                <div className="pl-6 mt-1 space-y-1">
                                                    {iteration.children.map((task) => (
                                                        <button
                                                            key={task.id}
                                                            onClick={() => setSelectedItem(task)}
                                                            className={`flex items-center gap-2 p-2 rounded-lg w-fit ${
                                                                selectedItem?.id === task.id
                                                                    ? "bg-gray-100"
                                                                    : "hover:bg-gray-100"
                                                            }`}
                                                        >
                                                            <NodeIcon
                                                                type={capitalizeFirst(task.type)}
                                                                className="mr-2 flex-shrink-0 h-4 w-4"
                                                            />
                                                            <span>{task.presentationName}</span>
                                                        </button>
                                                    ))}
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            ))}

                        </div>
                    </CardContent>
                </Card>
            </div>

            {/* Container 2 - Work Element Details */}
            <div className="w-[65%] mb-4">
                <WorkElementDetailsView selectedItem={selectedItem} />
            </div>
        </div>
    );
};

export default WorkBreakdownElementsTab;
