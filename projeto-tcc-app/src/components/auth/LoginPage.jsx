import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useNavigate, useLocation } from 'react-router-dom';
import { Button } from '@/components/ui/button.jsx';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card.jsx';
import { useAuth } from '@/contexts/SupabaseAuthContext.jsx';
import { LogIn, ArrowLeft, Code2 } from 'lucide-react';
import { useToast } from '@/components/ui/use-toast.js';

const LoginPage = () => {
  const { user, signInWithGoogle, loading } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { toast } = useToast();
  const [isLoggingIn, setIsLoggingIn] = useState(false);

  const from = location.state?.from || '/';
  const processToSave = location.state?.processToSave;
  const processId = location.state?.processId;

  useEffect(() => {
    if (user && !loading) {
      handlePostLoginActions();
    }
  }, [user, loading]);

  const handlePostLoginActions = async () => {
    if (processToSave && processId) {
      try {
        localStorage.setItem(`processNodes_${processId}`, JSON.stringify(processToSave));
        
        toast({
          title: "Processo Salvo!",
          description: "Seu processo foi salvo com sucesso na sua biblioteca.",
          variant: "default",
        });

        navigate('/library');
      } catch (error) {
        console.error('Failed to save process:', error);
        toast({
          title: "Erro ao Salvar",
          description: "Não foi possível salvar o processo. Tente novamente.",
          variant: "destructive",
        });
        navigate(from);
      }
    } else {
      navigate(from);
    }
  };

  const handleLogin = async () => {
    setIsLoggingIn(true);
    try {
      await signInWithGoogle();
    } catch (error) {
      console.error('Login failed:', error);
      setIsLoggingIn(false);
    }
  };

  const handleGoBack = () => {
    navigate(-1);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-100 to-gray-200 dark:from-slate-900 dark:to-gray-800 p-4">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-md"
      >
        <Card className="shadow-xl border-2 border-primary/10">
          <CardHeader className="text-center space-y-4">
            <div className="flex justify-center">
              <div className="p-3 bg-primary/10 rounded-full">
                <Code2 className="h-8 w-8 text-primary" />
              </div>
            </div>
            <div>
              <CardTitle className="text-2xl font-bold text-primary">
                Entre na sua conta
              </CardTitle>
              <CardDescription className="text-muted-foreground mt-2">
                {processToSave 
                  ? "Faça login para salvar seu processo na biblioteca"
                  : "Acesse sua biblioteca de processos e muito mais"
                }
              </CardDescription>
            </div>
          </CardHeader>

          <CardContent className="space-y-6">
            {processToSave && (
              <motion.div
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                className="p-4 bg-accent/10 border border-accent/20 rounded-lg"
              >
                <h4 className="font-semibold text-accent-foreground mb-2">
                  Processo Pronto para Salvar
                </h4>
                <p className="text-sm text-muted-foreground">
                  Após fazer login, seu processo será automaticamente salvo na sua biblioteca pessoal.
                </p>
              </motion.div>
            )}

            <Button
              onClick={handleLogin}
              disabled={loading || isLoggingIn}
              className="w-full bg-primary hover:bg-primary/90 text-primary-foreground font-semibold py-3"
              size="lg"
            >
              {loading || isLoggingIn ? (
                <div className="flex items-center">
                  <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                  Entrando...
                </div>
              ) : (
                <>
                  <LogIn className="mr-2 h-5 w-5" />
                  Entrar com Google
                </>
              )}
            </Button>

            <div className="text-center">
              <Button
                variant="ghost"
                onClick={handleGoBack}
                className="text-muted-foreground hover:text-foreground"
              >
                <ArrowLeft className="mr-2 h-4 w-4" />
                Voltar
              </Button>
            </div>

            <div className="text-center text-xs text-muted-foreground">
              <p>
                Ao fazer login, você concorda com nossos termos de uso e política de privacidade.
              </p>
            </div>
          </CardContent>
        </Card>
      </motion.div>
    </div>
  );
};

export default LoginPage;