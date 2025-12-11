import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button.jsx";
import { LogIn, LogOut, UserCircle, Code2, Library, Home } from "lucide-react";
import { motion } from "framer-motion";
import { useAuth } from "@/contexts/SupabaseAuthContext.jsx";

const Navbar = () => {
  const { user, signOut, loading } = useAuth();
  const navigate = useNavigate();

  const getInitials = (email) => {
    if (!email) return "?";
    const parts = email.split('@')[0];
    return parts.substring(0, 2).toUpperCase();
  };

  return (
    <motion.nav 
      className="bg-gray-900 border-b border-gray-800 shadow-lg"
      initial={{ y: -100, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.5, ease: "easeOut" }}
    >
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          <Link to="/" className="flex items-center space-x-2 text-xl font-bold text-white hover:text-blue-400 transition-colors">
            <Code2 className="h-6 w-6" />
            <span>ProcessSimModeler</span>
          </Link>

          <div className="flex items-center space-x-4">
            <Button variant="ghost" asChild className="text-gray-300 hover:text-white hover:bg-gray-800">
              <Link to="/">
                <Home className="h-4 w-4 mr-2" />
                Home
              </Link>
            </Button>
            
            <Button variant="ghost" asChild className="text-gray-300 hover:text-white hover:bg-gray-800">
              <Link to="/simulations">
                <Library className="h-4 w-4 mr-2" />
                Library
              </Link>
            </Button>
          </div>
        </div>
      </div>
    </motion.nav>
  );
};

export default Navbar;