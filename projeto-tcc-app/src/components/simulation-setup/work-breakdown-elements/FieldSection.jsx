import React from 'react';

const FieldSection = ({ title, children }) => {
    return (
        <div className="space-y-4 border border-gray-300 shadow-lg rounded-lg p-4 mb-4">
            {title && <h3 className="text-sm font-semibold">{title}</h3>}
            {children}
        </div>
    );
};

export default FieldSection;
