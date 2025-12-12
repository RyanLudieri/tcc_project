import React, { useMemo, useState, useEffect } from 'react';
import {useParams, Link, useNavigate, useLocation} from 'react-router-dom';
import { Button } from '@/components/ui/button.jsx';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card.jsx';
import { Label } from "@/components/ui/label.jsx";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table.jsx";
import { ArrowLeft, BarChart2, LineChart, TrendingUp, AlertTriangle, Save, Layers, Clock, Users, Package, AlertCircle } from 'lucide-react';
import { motion } from 'framer-motion';
import { useToast } from "@/components/ui/use-toast.js";
import { useAuth } from "@/contexts/SupabaseAuthContext.jsx";
import {
  ResponsiveContainer,
  LineChart as ReLineChart,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  Line,
} from 'recharts';
import {formatDuration} from "@/lib/durationUtils.js";

const ChartWrapper = ({ title, icon, children }) => (
    <Card className="h-[400px] flex flex-col shadow-lg hover:shadow-xl transition-shadow duration-300 bg-card">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2 border-b border-border">
        <CardTitle className="text-lg font-semibold text-card-foreground">{title}</CardTitle>
        {React.cloneElement(icon, { className: "h-5 w-5 text-primary" })}
      </CardHeader>
      <CardContent className="flex-1 flex items-center justify-center p-4">
        {children || <p className="text-muted-foreground text-center">No simulation data available for this visualization yet.</p>}
      </CardContent>
    </Card>
);

const SimulationResults = () => {
  const { simulationId, processId } = useParams();
  const { toast } = useToast();
  const { user } = useAuth();
  const navigate = useNavigate();
  const { state } = useLocation();
  const executionId = state?.executionId;

  const [simulationData, setSimulationData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  // ---------------- FETCH ------------------
  useEffect(() => {
    const fetchSimulation = async () => {
      try {
        const response = await fetch(`http://localhost:8080/results/${executionId}`);

        if (!response.ok) throw new Error("Erro ao buscar resultados");

        const data = await response.json();
        setSimulationData(data);
      } catch (error) {
        toast({
          title: "Erro ao carregar",
          description: "Não foi possível obter os resultados da simulação.",
          variant: "destructive",
        });
        console.error(error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchSimulation();
  }, [processId, toast]);

  // ----------------- PROCESSAMENTO (antes dos returns!!) ------------------
  const processedData = useMemo(() => {
    if (!simulationData)
      return {
        summaryStats: [],
        detailedQueueMetrics: [],
        stabilityData: [],
        durationStdDev: 0,
        totalEntitiesProcessed: 0,
        SINK_QUEUE_ID: null,
      };

    const data = simulationData;
    const avgDuration = data.averageDuration;

    const sortedQueueMetrics = data.queueMetrics.slice().sort((a, b) => {
      const numA = parseInt(a.queueName.substring(1));
      const numB = parseInt(b.queueName.substring(1));
      return numA - numB;
    });

    let SINK_QUEUE_ID = null;

    for (let i = sortedQueueMetrics.length - 1; i >= 0; i--) {
      if (sortedQueueMetrics[i].averageSize > 0.001) {
        SINK_QUEUE_ID = sortedQueueMetrics[i].queueName;
        break;
      }
    }

    if (!SINK_QUEUE_ID && sortedQueueMetrics.length > 0) {
      const lastQueue = sortedQueueMetrics.reduce((max, current) =>
          parseInt(current.queueName.substring(1)) > parseInt(max.queueName.substring(1)) ? current : max
      );
      SINK_QUEUE_ID = lastQueue.queueName;
    }

    let totalEntitiesProcessed = 0;
    if (SINK_QUEUE_ID) {
      const sumFinal = data.logs.reduce((sum, log) =>
              sum + (log.queueFinalCounts[SINK_QUEUE_ID] || 0)
          , 0);

      totalEntitiesProcessed = sumFinal / data.totalReplications;
    }

    const globalThroughput =
        totalEntitiesProcessed > 0 && avgDuration > 0
            ? totalEntitiesProcessed / avgDuration
            : 0.0;

    const detailedQueueMetrics = data.queueMetrics
        .map(metric => {
          const lastLog = data.logs[data.logs.length - 1];
          return {
            id: metric.queueName,
            queueId: metric.queueName,
            taskName: metric.taskName,
            averageSize: metric.averageSize,
            stdDevSize: metric.stdDev,
            finalCount: lastLog?.queueFinalCounts[metric.queueName] || 0,
          };
        })
        .sort((a, b) => b.averageSize - a.averageSize);

    const stabilityData = data.logs.map(log => ({
      name: `Replica ${log.replicationNumber}`,
      Duration: log.duration,
      Avg_Duration: avgDuration,
    }));

    const summaryStats = [
      {
        title: "Média de Duração",
        value: formatDuration(avgDuration),     // <<< AQUI
        icon: <Clock />
      },
      {
        title: "Total de Réplicas",
        value: data.totalReplications,
        icon: <Layers />
      },
      {
        title: "Throughput Global Médio",
        value: `${globalThroughput.toFixed(3).replace(".", ",")} / dia`,
        icon: <TrendingUp />
      },
      {
        title: `Entidades Concluídas (via ${SINK_QUEUE_ID})`,
        value: totalEntitiesProcessed.toFixed(0),
        icon: <Package />
      }
    ];

    return {
      summaryStats,
      detailedQueueMetrics,
      stabilityData,
      durationStdDev: data.durationStdDev,
      totalEntitiesProcessed,
      SINK_QUEUE_ID,
    };
  }, [simulationData]);

  // ----------------- RETURNS CONDICIONAIS ------------------
  if (isLoading) {
    return <div className="p-8 text-center text-xl text-muted-foreground">Loading simulation results...</div>;
  }

  if (!simulationData) {
    return <div className="p-8 text-center text-xl text-muted-foreground">No simulation data found.</div>;
  }

  const {
    summaryStats,
    detailedQueueMetrics,
    stabilityData,
    durationStdDev,
    totalEntitiesProcessed,
    SINK_QUEUE_ID,
  } = processedData;

  // ----------------- RENDER ------------------------
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
              <Link
                  to={`/simulations/${simulationId}/processes/${processId}/setup`}
                  className="inline-flex items-center text-primary hover:text-primary/80 transition-colors mb-2 group"
              >
                <ArrowLeft className="mr-2 h-5 w-5 group-hover:-translate-x-1 transition-transform" />
                Generate your simulation again
              </Link>

              <h1 className="text-4xl md:text-5xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-primary to-accent">
                Simulation Results
              </h1>

              <p className="text-lg text-muted-foreground mt-1">
                Detailed analysis for process ID:
                <code className="bg-muted px-2 py-1 rounded-md text-foreground/90 font-semibold">{processId}</code>
              </p>
            </div>
          </div>
        </header>

        {/* SUMMARY */}
        <section className="mb-10">
          <h2 className="text-3xl font-bold mb-6 text-foreground flex items-center">
            <BarChart2 className="mr-3 h-8 w-8 text-primary" />
            Simulation Summary
          </h2>

          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
            {summaryStats.map(stat => (
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

            <Card className="shadow-md hover:shadow-lg transition-shadow duration-300 bg-card">
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-base font-semibold text-card-foreground">Desvio Padrão (Duração)</CardTitle>
                <AlertTriangle className="h-6 w-6 text-red-500" />
              </CardHeader>
              <CardContent>
                <div className="text-3xl font-bold text-red-500">
                  {durationStdDev.toFixed(4).replace('.', ',')}
                </div>
              </CardContent>
            </Card>
          </div>
        </section>

        {/* QUEUE METRICS */}
        <section className="mb-10">
          <h2 className="text-3xl font-bold mb-6 text-foreground flex items-center">
            <Users className="mr-3 h-8 w-8 text-primary" />
            Detailed Queue Metrics (E[N] - Avg Size)
          </h2>

          <Card className="shadow-md overflow-hidden bg-card">
            <CardContent className="p-0">
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader className="bg-muted/50">
                    <TableRow>
                      <TableHead className="text-center">Queue ID</TableHead>
                      <TableHead className="text-center">Task Name</TableHead>
                      <TableHead className="text-center">E[N] - Avg Size</TableHead>
                      <TableHead className="text-center">Std Dev (Size)</TableHead>
                      <TableHead className="text-center">Final Count (Last R.)</TableHead>

                    </TableRow>
                  </TableHeader>

                  <TableBody>
                    {detailedQueueMetrics.map((q, idx) => {
                      const isBottleneck = idx === 0 && q.averageSize > 0.01;

                      return (
                          <TableRow
                              key={q.id}
                              className={`border-b-border hover:bg-muted/30 ${isBottleneck ? 'bg-red-50/40 dark:bg-red-900/20' : ''}`}
                          >
                            <TableCell className={`text-center ${isBottleneck ? 'text-destructive font-bold' : ''}`}>
                              {isBottleneck && (
                                  <AlertTriangle className="inline-block mr-2 h-4 w-4 text-destructive" />
                              )}
                              {q.queueId}
                            </TableCell>

                            <TableCell className={`text-center ${isBottleneck ? 'text-destructive font-bold' : ''}`}>
                              {q.taskName}
                            </TableCell>

                            <TableCell className={`text-center ${isBottleneck ? 'text-destructive font-bold' : ''}`}>
                              {q.averageSize.toFixed(2)}
                            </TableCell>

                            <TableCell className={`text-center ${isBottleneck ? 'text-destructive font-bold' : ''}`}>
                              {q.stdDevSize.toFixed(3)}
                            </TableCell>

                            <TableCell className={`text-center ${isBottleneck ? 'text-destructive font-bold' : ''}`}>
                              {q.finalCount}
                            </TableCell>
                          </TableRow>

                      );
                    })}
                  </TableBody>
                </Table>
              </div>
            </CardContent>
          </Card>
        </section>

        {/* CHARTS */}
        <section>
          <h2 className="text-3xl font-bold mb-6 text-foreground flex items-center">
            <LineChart className="mr-3 h-8 w-8 text-primary" />
            Detailed Graph Visualizations
          </h2>

          <div className="grid gap-8 md:grid-cols-1 lg:grid-cols-2">
            <ChartWrapper title="Simulação Estabilidade (Duração por Réplica)" icon={<Clock />}>
              <ResponsiveContainer width="100%" height="100%">
                <ReLineChart data={stabilityData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                  <XAxis dataKey="name" tick={{ fill: 'hsl(var(--muted-foreground))' }} />
                  <YAxis />
                  <Tooltip />
                  <Legend />
                  <Line dataKey="Duration" stroke="hsl(var(--primary))" strokeWidth={2.5} />
                  <Line dataKey="Avg_Duration" stroke="hsl(var(--accent))" strokeDasharray="5 5" strokeWidth={2} />
                </ReLineChart>
              </ResponsiveContainer>
            </ChartWrapper>

            <ChartWrapper title="Resource Utilization (%)" icon={<Users />} />
            <ChartWrapper title="Stage Throughput" icon={<TrendingUp />} />
          </div>
        </section>
      </motion.div>
  );
};

export default SimulationResults;
