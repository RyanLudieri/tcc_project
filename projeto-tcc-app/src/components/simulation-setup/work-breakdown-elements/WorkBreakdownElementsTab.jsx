import React, { useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Folder, CheckSquare } from 'lucide-react';
import LocalView from './LocalView';

// ---- MOCK ITERATIONS + TASKS ----
const mockIterations = [
    {
        id: 1,
        name: "Iteration 1",
        type: "ITERATION",
        children: [
            { id: 101, name: "Release planning", type: "TASK_DESCRIPTOR" },
            { id: 102, name: "Pair programming with TDD", type: "TASK_DESCRIPTOR" },
            { id: 103, name: "Code review", type: "TASK_DESCRIPTOR" }
        ]
    },
    {
        id: 2,
        name: "Iteration 2",
        type: "ITERATION",
        children: [
            { id: 201, name: "Daily meeting", type: "TASK_DESCRIPTOR" },
            { id: 202, name: "Refactoring", type: "TASK_DESCRIPTOR" }
        ]
    }
];

const WorkBreakdownElementsTab = () => {
    const [selectedItem, setSelectedItem] = useState(null);

    return (
        <div className="flex w-full h-full gap-10 p-6">
            {/* Container 1 - Work Breakdown */}
            <div className="flex-1 mb-4">
                <Card className="h-full shadow-lg">
                    <CardHeader className="py-3">
                        <CardTitle className="text-2xl text-primary">Process</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-4">
                            {mockIterations.map((iteration) => (
                                <div key={iteration.id} className="pl-2">
                                    <button
                                        onClick={() => setSelectedItem(iteration)}
                                        className={`flex items-center gap-2 p-2 rounded-lg w-fit ${
                                            selectedItem === iteration.name ? "bg-blue-100" : "hover:bg-gray-100"
                                        }`}
                                    >
                                        <Folder className="h-5 w-5 text-blue-600" />
                                        <span className="font-semibold">{iteration.name}</span>
                                    </button>
                                    <div className="pl-6 mt-1 space-y-1">
                                        {iteration.children.map((task) => (
                                            <button
                                                key={task.id}
                                                onClick={() => setSelectedItem(task)}
                                                className={`flex items-center gap-2 p-2 rounded-lg w-fit ${
                                                    selectedItem === task.name ? "bg-green-100" : "hover:bg-gray-100"
                                                }`}
                                            >
                                                <CheckSquare className="h-4 w-4 text-green-600" />
                                                <span>{task.name}</span>
                                            </button>
                                        ))}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </CardContent>
                </Card>
            </div>

            {/* Container 2 - Local View */}
            <div className="w-[65%] mb-4">
                <LocalView selectedItem={selectedItem} />
            </div>
        </div>
    );
};

export default WorkBreakdownElementsTab;
