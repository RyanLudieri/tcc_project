import React from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button.jsx';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card.jsx';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select.jsx";
import { Label } from "@/components/ui/label.jsx";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table.jsx";
import { ArrowLeft, Download, FileText, BarChart2, LineChart, PieChart as PieChartIcon, Settings, Clock, Package, TrendingUp, DollarSign, Users, AlertCircle, CheckCircle, AlertTriangle, Save } from 'lucide-react';
import { motion } from 'framer-motion';
import { useToast } from "@/components/ui/use-toast.js";
import { useAuth } from "@/contexts/SupabaseAuthContext.jsx";
import {
  ResponsiveContainer,
  LineChart as ReLineChart,
  BarChart as ReBarChart,
  AreaChart as ReAreaChart,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  Line,
  Bar,
  Area,
  Cell
} from 'recharts';

const ChartWrapper = ({ title, icon, children }) => (
  <Card className="h-[400px] flex flex-col shadow-lg hover:shadow-xl transition-shadow duration-300 bg-card">
    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2 border-b border-border">
      <CardTitle className="text-lg font-semibold text-card-foreground">{title}</CardTitle>
      {React.cloneElement(icon, { className: "h-5 w-5 text-primary" })}
    </CardHeader>
    <CardContent className="flex-1 flex items-center justify-center p-4">
      {children || <p className="text-muted-foreground text-center">Dados do gráfico aparecerão aqui após a simulação.</p>}
    </CardContent>
  </Card>
);

const mockTimeData = [
  { name: 'Sprint 1', E_R: 5.2, meta: 5 },
  { name: 'Sprint 2', E_R: 4.8, meta: 5 },
  { name: 'Sprint 3', E_R: 5.5, meta: 5 },
  { name: 'Sprint 4', E_R: 4.5, meta: 4.5 },
  { name: 'Sprint 5', E_R: 4.9, meta: 4.5 },
  { name: 'Sprint 6', E_R: 5.1, meta: 4.5 },
];

const mockResourceData = [
  { name: 'Dev Frontend Sênior', Utilização: 0.85, capacidade: 1 },
  { name: 'Dev Backend Pleno', Utilização: 0.78, capacidade: 1 },
  { name: 'QA Automatizado', Utilização: 0.92, capacidade: 1 },
  { name: 'Designer UX', Utilização: 0.60, capacidade: 1 },
  { name: 'Product Owner', Utilização: 0.70, capacidade: 1 },
  { name: 'Analista de Dados Jr', Utilização: 0.55, capacidade: 1 },
];

const mockThroughputData = [
  { name: 'Semana 1', Throughput: 8, meta: 10 },
  { name: 'Semana 2', Throughput: 12, meta: 10 },
  { name: 'Semana 3', Throughput: 9, meta: 10 },
  { name: 'Semana 4', Throughput: 14, meta: 12 },
  { name: 'Semana 5', Throughput: 11, meta: 12 },
  { name: 'Semana 6', Throughput: 15, meta: 12 },
];

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8A2BE2', '#FF69B4'];

const SimulationResultsPage = () => {
  const { id: processId } = useParams();
  const { toast } = useToast();
  const { user } = useAuth();
  const navigate = useNavigate();

  const handleExport = (type) => {
    toast({
      title: `Exportar ${type}`,
      description: "🚧 Esta funcionalidade ainda não foi implementada. Mas não se preocupe! Você pode solicitá-la na sua próxima mensagem! 🚀",
      variant: "default",
    });
  };

  const handleSaveProcess = () => {
    if (!user) {
      const processDataString = localStorage.getItem(`processNodes_${processId}`);
      if (processDataString) {
        try {
          const processData = JSON.parse(processDataString);
          navigate('/login', {
            state: {
              from: `/processes/${processId}/results`,
              processToSave: processData,
              processId: processId,
            },
          });
        } catch (error) {
          toast({
            title: "Erro ao ler dados",
            description: "Não foi possível ler os dados do processo para salvá-lo.",
            variant: "destructive",
          });
        }
      } else {
        toast({
          title: "Erro",
          description: "Não foi possível encontrar os dados do processo para salvar.",
          variant: "destructive",
        });
      }
    }
  };

  const summaryStats = [
    { title: "Tempo Total Simulado", value: "98.2 dias", icon: <Clock className="h-6 w-6 text-blue-500" /> },
    { title: "Total Entidades Processadas", value: "723", icon: <Package className="h-6 w-6 text-green-500" /> },
    { title: "Throughput Médio Global", value: "7.36 unid/dia", icon: <TrendingUp className="h-6 w-6 text-purple-500" /> },
    { title: "Custo Total Estimado", value: "$15,820.50", icon: <DollarSign className="h-6 w-6 text-yellow-500" /> },
  ];

  const queueData = [
    { id: "q_entrada_demanda", name: "Fila de Novas Demandas", count: 8, avgTime: "1.8d", maxTime: "3.5d", throughput: "5/dia" },
    { id: "q_desenv_frontend", name: "Fila Desenvolvimento Frontend", count: 15, avgTime: "3.2d", maxTime: "7.1d", throughput: "2/dia" },
    { id: "q_desenv_backend", name: "Fila Desenvolvimento Backend", count: 10, avgTime: "4.5d", maxTime: "8.0d", throughput: "1.5/dia" },
    { id: "q_testes_integrados", name: "Fila Testes Integrados", count: 5, avgTime: "2.1d", maxTime: "4.2d", throughput: "3/dia" },
    { id: "q_revisao_po", name: "Fila Revisão P.O.", count: 2, avgTime: "0.8d", maxTime: "1.5d", throughput: "4/dia" },
    { id: "q_deploy_prod", name: "Fila Deploy Produção", count: 1, avgTime: "0.5d", maxTime: "1.0d", throughput: "N/A" },
  ];


  return (
    <motion.div 
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="flex-1 flex flex-col p-4 md:p-6 lg:p-8 bg-gradient-to-br from-slate-100 to-gray-200 dark:from-slate-900 dark:to-gray-800"
    >
      <header className="mb-8">
        <div className="flex justify-between items-center">
          <div>
            <Link to={`/processes/${processId}/simulate`} className="inline-flex items-center text-primary hover:text-primary/80 transition-colors mb-2 group">
              <ArrowLeft className="mr-2 h-5 w-5 group-hover:-translate-x-1 transition-transform" />
              Voltar para Configuração da Simulação
            </Link>
            <h1 className="text-4xl md:text-5xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-primary to-accent">
              Resultados da Simulação
            </h1>
            <p className="text-lg text-muted-foreground mt-1">Análise detalhada para o Processo ID: <code className="bg-muted px-2 py-1 rounded-md text-foreground/90 font-semibold">{processId}</code></p>
          </div>
          {!user && (
            <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
              <Button onClick={handleSaveProcess} size="lg" className="bg-accent hover:bg-accent/90">
                <Save className="mr-2 h-5 w-5" />
                Salvar Processo na Biblioteca
              </Button>
            </motion.div>
          )}
        </div>
      </header>

      <Card className="mb-8 shadow-xl bg-card">
        <CardHeader>
          <CardTitle className="text-2xl text-card-foreground flex items-center"><Settings className="mr-3 h-7 w-7 text-primary"/>Controles de Visualização e Exportação</CardTitle>
        </CardHeader>
        <CardContent className="flex flex-col md:flex-row gap-6 items-center">
          <div className="flex-1 w-full md:w-auto">
            <Label htmlFor="metric-select" className="text-sm font-medium text-muted-foreground mb-1 block">Métrica Principal do Painel</Label>
            <Select onValueChange={(value) => console.log("Métrica selecionada:", value)}>
              <SelectTrigger id="metric-select" className="w-full md:w-[300px] bg-background border-border focus:ring-primary">
                <SelectValue placeholder="Selecionar Métrica Principal" />
              </SelectTrigger>
              <SelectContent className="bg-popover border-border">
                <SelectItem value="e_n">E[N] - Número Médio na Fila</SelectItem>
                <SelectItem value="e_r">E[R] - Tempo Médio de Resposta</SelectItem>
                <SelectItem value="utilization">Utilização de Recursos</SelectItem>
                <SelectItem value="throughput">Throughput por Etapa</SelectItem>
                <SelectItem value="cost">Análise de Custos</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="flex gap-3 flex-wrap justify-center md:justify-end">
            <Button variant="outline" onClick={() => handleExport('CSV')} className="border-primary text-primary hover:bg-primary/10"><FileText className="mr-2 h-4 w-4" />Exportar Dados (CSV)</Button>
            <Button variant="outline" onClick={() => handleExport('Gráfico')} className="border-primary text-primary hover:bg-primary/10"><Download className="mr-2 h-4 w-4" />Exportar Gráficos (PNG)</Button>
            <Button variant="outline" onClick={() => handleExport('PDF')} className="border-primary text-primary hover:bg-primary/10"><Download className="mr-2 h-4 w-4" />Salvar Relatório (PDF)</Button>
          </div>
        </CardContent>
      </Card>
      
      <section className="mb-10">
        <h2 className="text-3xl font-bold mb-6 text-foreground flex items-center"><BarChart2 className="mr-3 h-8 w-8 text-primary"/>Resumo Geral da Simulação</h2>
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
          {summaryStats.map((stat) => (
            <Card key={stat.title} className="shadow-md hover:shadow-lg transition-shadow duration-300 bg-card">
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-base font-semibold text-card-foreground">{stat.title}</CardTitle>
                {stat.icon}
              </CardHeader>
              <CardContent>
                <div className="text-3xl font-bold text-primary">{stat.value}</div>
              </CardContent>
            </Card>
          ))}
        </div>
      </section>

      <section className="mb-10">
        <h2 className="text-3xl font-bold mb-6 text-foreground flex items-center"><Users className="mr-3 h-8 w-8 text-primary"/>Métricas Detalhadas das Filas</h2>
        <Card className="shadow-md overflow-hidden bg-card">
          <CardContent className="p-0">
            <div className="overflow-x-auto">
              <Table>
                <TableHeader className="bg-muted/50">
                  <TableRow>
                    <TableHead className="font-semibold text-muted-foreground">Nome da Fila</TableHead>
                    <TableHead className="text-right font-semibold text-muted-foreground">Entidades Atuais</TableHead>
                    <TableHead className="text-right font-semibold text-muted-foreground">Tempo Médio (Fila)</TableHead>
                    <TableHead className="text-right font-semibold text-muted-foreground">Tempo Máximo (Fila)</TableHead>
                    <TableHead className="text-right font-semibold text-muted-foreground">Throughput (Fila)</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {queueData.map((queue) => (
                    <TableRow key={queue.id} className="border-b-border hover:bg-muted/30">
                      <TableCell className="font-medium text-card-foreground py-3">{queue.name}</TableCell>
                      <TableCell className="text-right text-card-foreground py-3">{queue.count}</TableCell>
                      <TableCell className="text-right text-card-foreground py-3">{queue.avgTime}</TableCell>
                      <TableCell className="text-right text-card-foreground py-3">{queue.maxTime}</TableCell>
                      <TableCell className="text-right text-card-foreground py-3">{queue.throughput}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          </CardContent>
        </Card>
      </section>

      <section>
        <h2 className="text-3xl font-bold mb-6 text-foreground flex items-center"><LineChart className="mr-3 h-8 w-8 text-primary"/>Visualizações Gráficas Detalhadas</h2>
        <div className="grid gap-8 md:grid-cols-1 lg:grid-cols-2">
          <ChartWrapper title="Tempo Médio de Resposta (E[R]) por Sprint" icon={<Clock />}>
            <ResponsiveContainer width="100%" height="100%">
              <ReLineChart data={mockTimeData} margin={{ top: 5, right: 30, left: 0, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                <XAxis dataKey="name" tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 12 }} />
                <YAxis tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 12 }} label={{ value: 'Dias', angle: -90, position: 'insideLeft', fill: 'hsl(var(--muted-foreground))', fontSize: 12 }}/>
                <Tooltip contentStyle={{ backgroundColor: 'hsl(var(--popover))', borderColor: 'hsl(var(--border))', borderRadius: '0.5rem' }} />
                <Legend wrapperStyle={{ fontSize: "14px", paddingTop: '10px' }} />
                <Line type="monotone" dataKey="E_R" name="Tempo Médio" stroke="hsl(var(--primary))" strokeWidth={2.5} activeDot={{ r: 7 }} dot={{ r: 4 }} />
                <Line type="monotone" dataKey="meta" name="Meta SLA" stroke="hsl(var(--accent))" strokeWidth={2} strokeDasharray="5 5" dot={false} />
              </ReLineChart>
            </ResponsiveContainer>
          </ChartWrapper>
          
          <ChartWrapper title="Utilização dos Recursos (%)" icon={<Users />}>
            <ResponsiveContainer width="100%" height="100%">
              <ReBarChart data={mockResourceData} layout="vertical" margin={{ top: 5, right: 30, left: 30, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                <XAxis type="number" domain={[0, 1]} tickFormatter={(tick) => `${tick * 100}%`} tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 12 }} />
                <YAxis dataKey="name" type="category" width={150} tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 12 }} />
                <Tooltip formatter={(value) => `${(value * 100).toFixed(1)}%`} contentStyle={{ backgroundColor: 'hsl(var(--popover))', borderColor: 'hsl(var(--border))', borderRadius: '0.5rem' }} />
                <Legend wrapperStyle={{ fontSize: "14px", paddingTop: '10px' }} />
                <Bar dataKey="Utilização" name="Utilização Atual" radius={[0, 4, 4, 0]} barSize={20}>
                  {mockResourceData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.Utilização > 0.85 ? 'hsl(var(--destructive))' : COLORS[index % COLORS.length]} />
                  ))}
                </Bar>
                 <Bar dataKey="capacidade" name="Capacidade (100%)" fill="hsl(var(--border))" radius={[0, 4, 4, 0]} barSize={20} stackId="a" background={{ fill: 'hsl(var(--muted)/0.3)' }} />
              </ReBarChart>
            </ResponsiveContainer>
          </ChartWrapper>

          <ChartWrapper title="Throughput Acumulado por Semana" icon={<TrendingUp />}>
             <ResponsiveContainer width="100%" height="100%">
              <ReAreaChart data={mockThroughputData} margin={{ top: 5, right: 30, left: 0, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                <XAxis dataKey="name" tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 12 }} />
                <YAxis tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 12 }} label={{ value: 'Unidades', angle: -90, position: 'insideLeft', fill: 'hsl(var(--muted-foreground))', fontSize: 12 }}/>
                <Tooltip contentStyle={{ backgroundColor: 'hsl(var(--popover))', borderColor: 'hsl(var(--border))', borderRadius: '0.5rem' }} />
                <Legend wrapperStyle={{ fontSize: "14px", paddingTop: '10px' }} />
                <Area type="monotone" dataKey="Throughput" name="Realizado" stroke="hsl(var(--primary))" fill="hsl(var(--primary) / 0.2)" strokeWidth={2.5} />
                <Line type="monotone" dataKey="meta" name="Meta" stroke="hsl(var(--accent))" strokeWidth={2} strokeDasharray="5 5" dot={false} />
              </ReAreaChart>
            </ResponsiveContainer>
          </ChartWrapper>

           <Card className="h-[400px] flex flex-col shadow-lg hover:shadow-xl transition-shadow duration-300 bg-card lg:col-span-1">
            <CardHeader className="border-b border-border">
              <CardTitle className="text-lg font-semibold text-card-foreground flex items-center"><AlertCircle className="mr-2 h-5 w-5 text-yellow-500"/>Pontos de Atenção</CardTitle>
              <CardDescription className="text-sm text-muted-foreground">Métricas que requerem análise ou otimização.</CardDescription>
            </CardHeader>
            <CardContent className="space-y-3 pt-4">
              <div className="flex items-start p-2 rounded-md bg-destructive/10 border border-destructive/30">
                <AlertTriangle className="h-5 w-5 text-destructive mr-3 mt-0.5 flex-shrink-0" />
                <div>
                  <p className="text-sm font-semibold text-destructive-foreground">Gargalo Identificado: <span className="font-bold">QA Automatizado (92% Util.)</span></p>
                  <p className="text-xs text-destructive-foreground/80">Considerar alocação de mais recursos ou otimização dos testes.</p>
                </div>
              </div>
              <div className="flex items-start p-2 rounded-md bg-yellow-500/10 border border-yellow-500/30">
                <Clock className="h-5 w-5 text-yellow-600 mr-3 mt-0.5 flex-shrink-0" />
                <div>
                  <p className="text-sm font-semibold text-yellow-700 dark:text-yellow-300">Tempo Médio Elevado: <span className="font-bold">Dev Backend (4.5d)</span></p>
                  <p className="text-xs text-yellow-600/80 dark:text-yellow-400/80">Investigar possíveis bloqueios ou complexidade excessiva nas tarefas.</p>
                </div>
              </div>
               <div className="flex items-start p-2 rounded-md bg-green-500/10 border border-green-500/30">
                <CheckCircle className="h-5 w-5 text-green-600 mr-3 mt-0.5 flex-shrink-0" />
                <div>
                  <p className="text-sm font-semibold text-green-700 dark:text-green-300">Bom Desempenho: <span className="font-bold">Revisão P.O. (0.8d)</span></p>
                  <p className="text-xs text-green-600/80 dark:text-green-400/80">Processo de revisão ágil e eficiente.</p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </section>
    </motion.div>
  );
};

export default SimulationResultsPage;