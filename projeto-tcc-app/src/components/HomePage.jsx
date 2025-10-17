import React, { useState } from "react";
import { Button } from "@/components/ui/button.jsx";
import { motion } from "framer-motion";
import { PlusCircle, Library } from "lucide-react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "@/contexts/SupabaseAuthContext.jsx";
import SimulationObjectiveModal from "@/components/modals/SimulationObjectiveModal.jsx";

const HomePage = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [modalOpen, setModalOpen] = useState(false);

  return (
      <div className="container mx-auto px-4 py-16 flex flex-col items-center justify-center min-h-[calc(100vh-10rem)]">
        <motion.div
            className="text-center"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
        >
          <motion.h1
              className="text-4xl md:text-6xl font-bold mb-6 text-primary"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: 0.3, duration: 0.8 }}
          >
            Welcome to <span className="text-primary">Software Process Simulator</span>
          </motion.h1>

          <motion.p
              className="text-lg md:text-xl mb-12 max-w-2xl mx-auto text-gray-700 dark:text-gray-300"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: 0.6, duration: 0.8 }}
          >
            Simulate, analyze and optimize your software development processes with our powerful simulation tool.
          </motion.p>

          <motion.div
              className="flex flex-col sm:flex-row gap-4 justify-center items-center"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: 0.9, duration: 0.8 }}
          >
            {/* Botão verde/accent permanece aqui */}
            <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
              <Button
                  onClick={() => setModalOpen(true)}
                  className="bg-accent hover:bg-accent/90 text-accent-foreground font-bold text-lg px-8 py-6 rounded-lg shadow-lg"
              >
                <PlusCircle className="mr-2 h-5 w-5" />
                Create New Simulation
              </Button>
            </motion.div>

            {user && (
                <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                  <Link to="/library">
                    <Button
                        variant="outline"
                        className="font-bold text-lg px-8 py-6 rounded-lg shadow-lg border-2 hover:bg-primary/5 flex items-center"
                    >
                      <Library className="mr-2 h-5 w-5" />
                      Browse Library
                    </Button>
                  </Link>
                </motion.div>
            )}
          </motion.div>
        </motion.div>

        <motion.div
            className="mt-16 w-full max-w-4xl"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 1.2, duration: 0.8 }}
        >
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {[
              { title: "Define", description: "Set up your process parameters and constraints" },
              { title: "Simulate", description: "Run simulations to see how your process performs" },
              { title: "Analyze", description: "Get insights and optimize your development workflow" }
            ].map((item, index) => (
                <motion.div
                    key={index}
                    className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md border border-border dark:border-gray-700"
                    whileHover={{ y: -5, boxShadow: "0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 0 15px rgba(var(--color-primary-rgb), 0.3)" }}
                    transition={{ duration: 0.2 }}
                >
                  <h3 className="text-xl font-bold mb-2 text-gray-800 dark:text-gray-100">{item.title}</h3>
                  <p className="text-gray-600 dark:text-gray-400">{item.description}</p>
                </motion.div>
            ))}
          </div>
        </motion.div>

        {/* Modal separado, modularizado */}
        <SimulationObjectiveModal open={modalOpen} setOpen={setModalOpen} />
      </div>
  );
};

export default HomePage;
