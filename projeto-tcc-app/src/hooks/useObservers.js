import { useState } from "react";
import { API_BASE_URL } from "@/config/api";

export default function useObservers({ workProducts, toast }) {
    const [observers, setObservers] = useState([]);
    const [isAddingObserver, setIsAddingObserver] = useState(false);
    const [selectedWorkProduct, setSelectedWorkProduct] = useState("");
    const [selectedType, setSelectedType] = useState("LENGTH");

    const observerTypes = ['LENGTH', 'TIME'];

    const getNextObserverIndex = () => {
        if (observers.length === 0) return 1;
        const existingIndices = observers.map(obs => {
            const match = obs.name.match(/observer (\d+)$/);
            return match ? parseInt(match[1], 10) : 0;
        });
        return Math.max(...existingIndices) + 1;
    };

    const showAddObserverForm = () => {
        setIsAddingObserver(true);
        setSelectedWorkProduct("");
        setSelectedType("LENGTH");
    };

    const cancelAddObserver = () => {
        setIsAddingObserver(false);
        setSelectedWorkProduct("");
        setSelectedType("LENGTH");
    };

    const handleObserverTypeChange = (value, id) => {
        setObservers(observers.map(o => o.id === id ? { ...o, type: value } : o));
    };

    const toggleObserverEdit = (id) => {
        setObservers(observers.map(o => o.id === id ? { ...o, isEditing: !o.isEditing } : o));
    };

    const handleAddObserver = async () => {
        if (!selectedWorkProduct) {
            toast({ title: "Error", description: "Please select a work product.", variant: "destructive" });
            return;
        }

        const wp = workProducts.find(wp => wp.id === selectedWorkProduct);
        if (!wp) return;

        const nextIndex = getNextObserverIndex() + 1;
        const query = selectedType !== "NONE" ? `?type=${selectedType}` : "";

        try {
            const resp = await fetch(
                `${API_BASE_URL}/work-product-configs/${wp.id}/observers${query}`,
                { method: "POST" }
            );
            if (!resp.ok) throw new Error();
            const saved = await resp.json();

            setObservers(prev => [...prev, {
                id: saved.id,
                name: saved.name,
                queueName: saved.queue_name,
                type: saved.type,
                position: saved.position,
                isEditing: false,
            }]);

            setIsAddingObserver(false);
            toast({ title: "Observer Added", description: `${saved.name} added.`, variant: "default" });

        } catch {
            toast({ title: "Error", description: "Unable to save observer.", variant: "destructive" });
        }
    };

    const saveUpdateObserver = async (id) => {
        const obs = observers.find(o => o.id === id);
        if (!obs) return;

        try {
            const resp = await fetch(`${API_BASE_URL}/work-product-configs/observers/${id}`, {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ type: obs.type, queueName: obs.name }),
            });

            if (!resp.ok) throw new Error();

            toggleObserverEdit(id);
            toast({ title: "Observer Updated", description: `${obs.name} updated.`, variant: "default" });
        } catch {
            toast({ title: "Error", description: "Unable to update observer.", variant: "destructive" });
        }
    };

    const handleRemoveObserver = async (id) => {
        const obs = observers.find(o => o.id === id);
        if (!obs) return;

        const wp = workProducts.find(w => w.queueName === obs.queueName);
        if (!wp) return;

        try {
            const resp = await fetch(
                `${API_BASE_URL}/work-product-configs/${wp.id}/observers/${id}`,
                { method: "DELETE" }
            );
            if (!resp.ok) throw new Error();

            setObservers(prev => prev.filter(o => o.id !== id));

            toast({ title: "Observer Removed", description: `${obs.name} removed.`, variant: "default" });

        } catch {
            toast({ title: "Error", description: "Unable to delete observer.", variant: "destructive" });
        }
    };

    return {
        observers,
        setObservers,

        observerTypes,
        selectedWorkProduct,
        setSelectedWorkProduct,
        selectedType,
        setSelectedType,
        isAddingObserver,

        showAddObserverForm,
        cancelAddObserver,
        handleAddObserver,
        toggleObserverEdit,
        handleObserverTypeChange,
        saveUpdateObserver,
        handleRemoveObserver,
    };
}
