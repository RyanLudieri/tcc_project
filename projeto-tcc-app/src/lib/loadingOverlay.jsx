import React, { useState, useEffect } from "react";
import { Rocket } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";

const LoadingOverlay = ({ isLoading }) => {
    const messages = [
        "Preparing simulation…",
        "Running calculations…",
        "Optimizing parameters…",
        "Almost there…",
        "Finalizing results…",
    ];

    const [currentMessageIndex, setCurrentMessageIndex] = useState(0);

    useEffect(() => {
        if (!isLoading) return;
        const interval = setInterval(() => {
            setCurrentMessageIndex((prev) => (prev + 1) % messages.length);
        }, 4000); // MAIS DEVAGAR: muda a cada 4s

        return () => clearInterval(interval);
    }, [isLoading]);

    if (!isLoading) return null;

    return (
        <div className="fixed inset-0 z-50 flex flex-col items-center justify-center bg-black/70 backdrop-blur-sm">
            {/* Foguete girando */}
            <div className="relative">
                <Rocket className="h-24 w-24 text-white drop-shadow-xl animate-spin" />

                {/* Sparkles animados */}
                {[...Array(5)].map((_, i) => (
                    <motion.div
                        key={i}
                        className="absolute w-2 h-2 bg-white rounded-full"
                        initial={{ x: -20 + i * 10, y: -40, opacity: 0 }}
                        animate={{ y: [-10, -50], opacity: [0, 1, 0] }}
                        transition={{ repeat: Infinity, duration: 4 + i * 0.5, delay: i * 0.5 }}
                    />
                ))}
            </div>

            {/* Mensagens */}
            <AnimatePresence mode="wait">
                <motion.p
                    key={currentMessageIndex}
                    className="mt-6 text-3xl font-bold text-white tracking-wide drop-shadow-lg text-center max-w-xs"
                    initial={{ opacity: 0, y: 10 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, y: -10 }}
                    transition={{ duration: 0.7 }}
                >
                    {messages[currentMessageIndex]}
                </motion.p>
            </AnimatePresence>

            <motion.p
                className="mt-2 text-white font-medium font-sans text-center max-w-xs"
                animate={{ opacity: [0.5, 1, 0.5] }}
                transition={{ repeat: Infinity, duration: 3 }}
            >
                Please wait while your simulation runs…
            </motion.p>
        </div>
    );
};

export default LoadingOverlay;
