import React, { useState } from 'react';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

const DistributionField = ({ value, onChange }) => {
    const distributions = [
        'CONSTANT',
        'EXPONENTIAL',
        'GAMMA',
        'LOGNORMAL',
        'NEGATIVE_EXPONENTIAL',
        'NORMAL',
        'POISSON',
        'UNIFORM',
        'WEIBULL'
    ];

    const [distributionType, setDistributionType] = useState(value?.type || 'CONSTANT');
    const [params, setParams] = useState({
        constant: value?.params?.constant || '',
        low: value?.params?.low || '',
        high: value?.params?.high || '',
        average: value?.params?.average || '',
        mean: value?.params?.mean || '',
        standardDeviation: value?.params?.standardDeviation || '',
        scale: value?.params?.scale || '',
        shape: value?.params?.shape || ''
    });

    React.useEffect(() => {
        if (!value) return;
        setDistributionType(value.type || 'CONSTANT');
        setParams({
            constant: value?.params?.constant || '',
            low: value?.params?.low || '',
            high: value?.params?.high || '',
            average: value?.params?.average || '',
            mean: value?.params?.mean || '',
            standardDeviation: value?.params?.standardDeviation || '',
            scale: value?.params?.scale || '',
            shape: value?.params?.shape || ''
        });
    }, [value]);



    const handleParamChange = (e) => {
        const { name, value } = e.target;
        let val = value;
        if (Number(val) < 0) val = 0;

        const updatedParams = { ...params, [name]: val };
        setParams(updatedParams);

        onChange({
            type: distributionType,
            params: {
                constant: updatedParams.constant || null,
                average: updatedParams.average || null,
                mean: updatedParams.mean || null,
                standardDeviation: updatedParams.standardDeviation || null,
                low: updatedParams.low || null,
                high: updatedParams.high || null,
                shape: updatedParams.shape || null,
                scale: updatedParams.scale || null,
            },
        });
    };

    const handleDistributionChange = (val) => {
        setDistributionType(val);
        setParams({
            constant: '',
            low: '',
            high: '',
            average: '',
            mean: '',
            standardDeviation: '',
            scale: '',
            shape: ''
        });
        onChange({
            type: val,
            params: {
                constant: null,
                average: null,
                mean: null,
                standardDeviation: null,
                low: null,
                high: null,
                shape: null,
                scale: null,
            }
        });

    };

    return (
        <div className="space-y-2">
            <label className="block text-xs font-medium">Best fit probability distribution</label>
            <Select value={distributionType} onValueChange={handleDistributionChange} className="w-1/3">
                <SelectTrigger>
                    <SelectValue placeholder="Select distribution" />
                </SelectTrigger>
                <SelectContent>
                    {distributions.map((d) => (
                        <SelectItem key={d} value={d}>{d}</SelectItem>
                    ))}
                </SelectContent>
            </Select>

            <div className="mt-2 space-y-2">
                {distributionType === 'CONSTANT' && (
                    <div>
                        <label className="block text-xs font-medium">Value</label>
                        <Input
                            type="number"
                            name="constant"
                            value={params.constant}
                            onChange={handleParamChange}
                            className="w-1/1"
                        />
                    </div>
                )}
                {distributionType === 'UNIFORM' && (
                    <div className="flex gap-2">
                        <div>
                            <label className="block text-xs font-medium">Low</label>
                            <Input
                                type="number"
                                name="low"
                                value={params.low}
                                onChange={handleParamChange}
                                className="w-1/1"
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-medium">High</label>
                            <Input
                                type="number"
                                name="high"
                                value={params.high}
                                onChange={handleParamChange}
                                className="w-1/1"
                            />
                        </div>
                    </div>
                )}
                {distributionType === 'NORMAL' && (
                    <div className="flex gap-2">
                        <div>
                            <label className="block text-xs font-medium">Average</label>
                            <Input
                                type="number"
                                name="average"
                                value={params.average}
                                onChange={handleParamChange}
                                className="w-1/1"
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-medium">Standard Deviation</label>
                            <Input
                                type="number"
                                name="standardDeviation"
                                value={params.standardDeviation}
                                onChange={handleParamChange}
                                className="w-1/1"
                            />
                        </div>
                    </div>
                )}
                {(distributionType === 'LOGNORMAL' || distributionType === 'WEIBULL'  || distributionType === 'GAMMA') && (
                    <div className="flex gap-2">
                        <div>
                            <label className="block text-xs font-medium">Scale</label>
                            <Input
                                type="number"
                                name="scale"
                                value={params.scale}
                                onChange={handleParamChange}
                                className="w-1/1"
                            />
                        </div>
                        <div>
                            <label className="block text-xs font-medium">Shape</label>
                            <Input
                                type="number"
                                name="shape"
                                value={params.shape}
                                onChange={handleParamChange}
                                className="w-1/1"
                            />
                        </div>
                    </div>
                )}
                {distributionType === 'EXPONENTIAL' && (
                    <div>
                        <label className="block text-xs font-medium">Mean</label>
                        <Input
                            type="number"
                            name="mean"
                            value={params.mean}
                            onChange={handleParamChange}
                            className="w-1/1"
                        />
                    </div>
                )}
                {(distributionType === 'POISSON' || distributionType === 'NEGATIVE_EXPONENTIAL') && (
                    <div>
                        <label className="block text-xs font-medium">Average</label>
                        <Input
                            type="number"
                            name="average"
                            value={params.average}
                            onChange={handleParamChange}
                            className="w-1/1"
                        />
                    </div>
                )}
            </div>
        </div>
    );
};

export default DistributionField;
