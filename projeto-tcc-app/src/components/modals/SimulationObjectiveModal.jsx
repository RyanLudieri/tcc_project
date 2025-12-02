import React, { useState } from "react";
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogFooter
} from "@/components/ui/dialog";
import { Textarea } from "@/components/ui/textarea.jsx";
import { Button } from "@/components/ui/button.jsx";

const SimulationObjectiveModal = ({ open, setOpen, onSave }) => {
    const [objective, setObjective] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault(); // previne reload da página
        if (!objective.trim()) return;
        onSave(objective);
        setObjective(""); // opcional: limpa o campo
    };

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogContent className="rounded-2xl p-6 shadow-xl">
                <DialogHeader className="space-y-1">
                    <DialogTitle className="text-xl font-semibold">
                        Simulation Objective
                    </DialogTitle>
                    <p className="text-sm text-muted-foreground">
                        Define what you want to analyze or evaluate in this simulation.
                    </p>
                </DialogHeader>

                {/* ✨ Coloquei o form para capturar Enter */}
                <form onSubmit={handleSubmit}>
                    <Textarea
                        placeholder="Evaluate performance, estimate resources, compare scenarios..."
                        value={objective}
                        onChange={(e) => setObjective(e.target.value)}
                        className="min-h-[100px] rounded-xl mb-4"
                        onKeyDown={(e) => {
                            if (
                                e.key === "Enter" &&
                                !e.shiftKey &&
                                !e.ctrlKey &&
                                !e.metaKey &&
                                !e.altKey
                            ) {
                                e.preventDefault();
                                if (objective.trim()) {
                                    onSave(objective);
                                    setObjective("");
                                }
                            }
                        }}
                    />


                    <DialogFooter>
                        <Button
                            type="submit"
                            disabled={!objective.trim()}
                            className="rounded-xl px-6"
                        >
                            Save & Continue
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
};

export default SimulationObjectiveModal;
