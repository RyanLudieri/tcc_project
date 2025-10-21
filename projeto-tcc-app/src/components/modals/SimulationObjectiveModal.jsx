import React, { useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog";
import { Textarea } from "@/components/ui/textarea.jsx";
import { Button } from "@/components/ui/button.jsx";
import { useNavigate } from "react-router-dom";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

const SimulationObjectiveModal = ({ open, setOpen }) => {
    const [objective, setObjective] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleCreateSimulation = async () => {
        if (!objective.trim()) return;

        setLoading(true);
        try {
            const response = await fetch(`${API_BASE_URL}/simulations`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ objective }),
            });

            if (!response.ok) throw new Error("Failed to create simulation");

            const simulation = await response.json();

            setOpen(false);
            navigate(`/simulations/${simulation.id}/processes/new/edit`);
        } catch (error) {
            console.error(error);
            alert("Failed to create simulation. Try again.");
        } finally {
            setLoading(false);
        }
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
                    <Button onClick={handleCreateSimulation} disabled={!objective.trim() || loading}>
                        {loading ? "Saving..." : "Save & Continue"}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
};

export default SimulationObjectiveModal;
