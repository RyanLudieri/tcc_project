import React, { useState } from 'react';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

const DistributionField = ({ value, onChange }) => {
    const distributions = [
        'CONSTANT',
        'UNIFORM',
        'NORMAL',
        'LOGNORMAL',
        'EXPONENTIAL',
        'POISSON',
        'NEGATIVE_EXPONENTIAL'
    ];

    const [distributionType, setDistributionType] = useState('CONSTANT');
    const [params, setParams] = useState({
        value: value || '',
        low: '',
        high: '',
        average: '',
        mean: '',
        stdDev: '',
        scale: '',
        shape: ''
    });

    const handleParamChange = (e) => {
        const { name, value } = e.target;
        let val = value;
        if (Number(val) < 0) val = 0;
        setParams({ ...params, [name]: val });
        onChange({ type: distributionType, params: { ...params, [name]: val } });
    };

    const handleDistributionChange = (val) => {
        setDistributionType(val);
        setParams({
            value: '',
            low: '',
            high: '',
            average: '',
            mean: '',
            stdDev: '',
            scale: '',
            shape: ''
        });
        onChange({ type: val, params: {} });
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
                            name="value"
                            value={params.value}
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
                                name="stdDev"
                                value={params.stdDev}
                                onChange={handleParamChange}
                                className="w-1/1"
                            />
                        </div>
                    </div>
                )}
                {distributionType === 'LOGNORMAL' && (
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
