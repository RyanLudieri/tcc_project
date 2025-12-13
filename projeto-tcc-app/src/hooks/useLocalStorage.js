import { useState, useEffect } from 'react';

function useLocalStorage(rawKey, initialValue) {
    const [key, setKey] = useState(rawKey);

    const [storedValue, setStoredValue] = useState(() => {
        try {
            const item = window.sessionStorage.getItem(rawKey);
            return item ? JSON.parse(item) : initialValue;
        } catch (error) {
            console.error("Error reading localStorage key: " + rawKey, error);
            return initialValue;
        }
    });

    // Atualiza o estado quando a chave muda
    useEffect(() => {
        if (rawKey !== key) {
            setKey(rawKey);
            try {
                const item = window.sessionStorage.getItem(rawKey);
                setStoredValue(item ? JSON.parse(item) : initialValue);
            } catch (error) {
                console.error("Error reading new key value:", error);
                setStoredValue(initialValue);
            }
        }
    }, [rawKey, key, initialValue]);

    const setValue = (value) => {
        try {
            const newValue = value instanceof Function ? value(storedValue) : value;
            setStoredValue(newValue);
            window.sessionStorage.setItem(rawKey, JSON.stringify(newValue));
        } catch (error) {
            console.error("Error setting value for key: " + rawKey, error);
        }
    };

    return [storedValue, setValue];
}

export default useLocalStorage;
