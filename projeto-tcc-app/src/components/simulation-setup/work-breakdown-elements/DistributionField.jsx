import React, { useState, useEffect } from 'react';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Save } from "lucide-react";
import { Button } from "@/components/ui/button.jsx";
import {useToast} from "@/components/ui/use-toast.js";

const DistributionField = ({ value, onChange, onSave, showSaveButton }) => {
    const [isDirty, setIsDirty] = useState(false);
    const { toast } = useToast();

    const DISTRIBUTIONS = [
        "CONSTANT",
        "EXPONENTIAL",
        "GAMMA",
        "LOGNORMAL",
        "NEGATIVE_EXPONENTIAL",
        "NORMAL",
        "POISSON",
        "UNIFORM",
        "WEIBULL",
    ];

    // ⭐ Conversão backend → frontend
    const backendToFrontend = (t) => (t === "CONST" ? "CONSTANT" : t);
    const frontendToBackend = (t) => (t === "CONSTANT" ? "CONST" : t);

    const [distributionType, setDistributionType] = useState("CONSTANT");
    const [params, setParams] = useState({});

    // ===========
    // LOAD VALUE
    // ===========
    useEffect(() => {
        if (!value) return;

        setDistributionType(backendToFrontend(value.type || "CONST"));

        setParams({
            constant: value?.params?.constant ?? "",
            low: value?.params?.low ?? "",
            high: value?.params?.high ?? "",
            average: value?.params?.average ?? "",
            mean: value?.params?.mean ?? "",
            standardDeviation: value?.params?.standardDeviation ?? "",
            scale: value?.params?.scale ?? "",
            shape: value?.params?.shape ?? "",
        });
    }, [value]);

    // ===========
    // PARAM CHANGE
    // ===========
    const handleParamChange = (e) => {
        setIsDirty(true);

        const { name, value } = e.target;
        const sanitized = Math.max(0, Number(value));

        const newParams = { ...params, [name]: sanitized };
        setParams(newParams);

        onChange({
            type: frontendToBackend(distributionType),
            params: {
                constant: newParams.constant || null,
                low: newParams.low || null,
                high: newParams.high || null,
                average: newParams.average || null,
                mean: newParams.mean || null,
                standardDeviation: newParams.standardDeviation || null,
                scale: newParams.scale || null,
                shape: newParams.shape || null,
            },
        });
    };

    // ===========
    // TYPE CHANGE
    // ===========
    const handleDistributionChange = (val) => {
        setIsDirty(true);

        setDistributionType(val);
        setParams({
            constant: "",
            low: "",
            high: "",
            average: "",
            mean: "",
            standardDeviation: "",
            scale: "",
            shape: "",
        });

        onChange({
            type: frontendToBackend(val),
            params: {
                constant: null,
                low: null,
                high: null,
                average: null,
                mean: null,
                standardDeviation: null,
                scale: null,
                shape: null,
            },
        });
    };

    // ===========
    // RENDER PARAM FIELDS
    // ===========
    const renderParamInput = (label, name) => (
        <div>
            <label className="block text-xs font-medium">{label}</label>
            <Input
                type="number"
                name={name}
                value={params[name]}
                onChange={handleParamChange}
                className="w-1/1"
            />
        </div>
    );

    const saveButton = (
        <div>
            <label className="block text-xs font-medium text-transparent">Button</label>
            <Button
                onClick={() => {
                    onSave(true);
                    setIsDirty(false);

                    toast({
                        title: "Saved",
                        description: "Distribution updated successfully!",
                    });
                }}
                disabled={!isDirty}
                className="
          flex items-center gap-2
          bg-green-600 hover:bg-green-700
          text-white px-4 py-2
          disabled:bg-gray-300 disabled:text-gray-600
          disabled:hover:bg-gray-300 disabled:cursor-not-allowed
        "
            >
                <Save className="h-4 w-4" />
                Save
            </Button>
        </div>
    );

    return (
        <div className="space-y-2">
            <label className="block text-xs font-medium">Best fit probability distribution</label>

            <Select value={distributionType} onValueChange={handleDistributionChange}>
                <SelectTrigger>
                    <SelectValue placeholder="Select distribution" />
                </SelectTrigger>
                <SelectContent>
                    {DISTRIBUTIONS.map((d) => (
                        <SelectItem key={d} value={d}>
                            {d}
                        </SelectItem>
                    ))}
                </SelectContent>
            </Select>

            {/* PARAMETER INPUTS */}
            <div className="mt-2 space-y-2">
                {distributionType === "CONSTANT" && (
                    <div className="flex gap-2">
                        {renderParamInput("Value", "constant")}
                        {showSaveButton && saveButton}
                    </div>
                )}

                {distributionType === "UNIFORM" && (
                    <div className="flex gap-2">
                        {renderParamInput("Low", "low")}
                        {renderParamInput("High", "high")}
                        {showSaveButton && saveButton}
                    </div>
                )}

                {distributionType === "NORMAL" && (
                    <div className="flex gap-2">
                        {renderParamInput("Average", "average")}
                        {renderParamInput("Std Dev", "standardDeviation")}
                        {showSaveButton && saveButton}
                    </div>
                )}

                {["LOGNORMAL", "WEIBULL", "GAMMA"].includes(distributionType) && (
                    <div className="flex gap-2">
                        {renderParamInput("Scale", "scale")}
                        {renderParamInput("Shape", "shape")}
                        {showSaveButton && saveButton}
                    </div>
                )}

                {distributionType === "EXPONENTIAL" && (
                    <div className="flex gap-2">
                        {renderParamInput("Mean", "mean")}
                        {showSaveButton && saveButton}
                    </div>
                )}

                {["POISSON", "NEGATIVE_EXPONENTIAL"].includes(distributionType) && (
                    <div className="flex gap-2">
                        {renderParamInput("Average", "average")}
                        {showSaveButton && saveButton}
                    </div>
                )}
            </div>
        </div>
    );
};

export default DistributionField;
