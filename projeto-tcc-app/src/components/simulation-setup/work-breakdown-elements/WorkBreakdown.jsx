import React from 'react';
import { Folder, CheckSquare } from 'lucide-react';

const WorkBreakdown = ({ iterations, selected, setSelected }) => {
    return (
        <div className="space-y-4">
            {iterations.map((iteration) => (
                <div key={iteration.id} className="pl-2">
                    <button
                        onClick={() => setSelected(iteration.name)}
                        className={`flex items-center gap-2 p-2 rounded-lg w-fit ${
                            selected === iteration.name ? "bg-blue-100" : "hover:bg-gray-100"
                        }`}
                    >
                        <Folder className="h-5 w-5 text-blue-600" />
                        <span className="font-semibold">{iteration.name}</span>
                    </button>
                    <div className="pl-6 mt-1 space-y-1">
                        {iteration.children.map((task) => (
                            <button
                                key={task.id}
                                onClick={() => setSelected(task.name)}
                                className={`flex items-center gap-2 p-2 rounded-lg w-fit ${
                                    selected === task.name ? "bg-green-100" : "hover:bg-gray-100"
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
    );
};

export default WorkBreakdown;
