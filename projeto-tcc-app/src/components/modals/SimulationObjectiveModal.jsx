import React, { useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog";
import { Textarea } from "@/components/ui/textarea.jsx";
import { Button } from "@/components/ui/button.jsx";
import { useNavigate } from "react-router-dom";

const SimulationObjectiveModal = ({ open, setOpen }) => {
    const [objective, setObjective] = useState("");
    const navigate = useNavigate();

    const handleCreateProcess = () => {
        console.log("Objetivo da simulação:", objective);
        setOpen(false);
        navigate(`/processes/new/edit`, { state: { objective } });
    };

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Simulation Objective</DialogTitle>
                    <p className="text-sm text-muted-foreground">Define the simulation objective</p>
                </DialogHeader>

                <Textarea
                    placeholder="Evaluate performance, estimate resources, compare scenarios..."
                    value={objective}
                    onChange={(e) => setObjective(e.target.value)}
                    className="min-h-[80px]"
                />

                <DialogFooter>
                    <Button onClick={handleCreateProcess} disabled={!objective.trim()}>
                        Save & Continue
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
};

export default SimulationObjectiveModal;
