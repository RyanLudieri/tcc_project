import React, { useMemo, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { Button } from '@/components/ui/button.jsx';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card.jsx';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select.jsx";
import { Label } from "@/components/ui/label.jsx";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table.jsx";
import { ArrowLeft, Download, FileText, BarChart2, LineChart, Settings, Clock, Package, TrendingUp, DollarSign, Users, AlertCircle, CheckCircle, AlertTriangle, Save, Layers } from 'lucide-react';
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
// import { API_BASE_URL } from "@/config/api";


// --- DADOS REAIS DA SIMULAÇÃO (ATUALIZADOS) ---
const SIMULATION_DATA_PLACEHOLDER = {
  "id": 1,
  "executionDate": "2025-12-05T17:17:15.787417",
  "totalReplications": 50,
  "averageDuration": 63.34117521158854,
  "durationStdDev": 0.6699000022826771,
  "queueMetrics": [
    {
      "queueName": "q1",
      "averageSize": 0.0,
      "stdDev": 0.0
    },
    {
      "queueName": "q10",
      "averageSize": 95.96,
      "stdDev": 11.517440191788651
    },
    {
      "queueName": "q2",
      "averageSize": 0.0,
      "stdDev": 0.0
    },
    {
      "queueName": "q3",
      "averageSize": 0.0,
      "stdDev": 0.0
    },
    {
      "queueName": "q4",
      "averageSize": 0.0,
      "stdDev": 0.0
    },
    {
      "queueName": "q5",
      "averageSize": 0.0,
      "stdDev": 0.0
    },
    {
      "queueName": "q6",
      "averageSize": 0.0,
      "stdDev": 0.0
    },
    {
      "queueName": "q7",
      "averageSize": 0.0,
      "stdDev": 0.0
    },
    {
      "queueName": "q8",
      "averageSize": 0.0,
      "stdDev": 0.0
    },
    {
      "queueName": "q9",
      "averageSize": 0.0,
      "stdDev": 0.0
    },
    {
      "queueName": "q0",
      "averageSize": 0.0,
      "stdDev": 0.0
    }
  ],
  "logs": [
    {
      "replicationNumber": 1,
      "duration": 63.002079264322916,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 110,
        "q0": 0
      }
    },
    {
      "replicationNumber": 2,
      "duration": 63.32166748046875,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 91,
        "q0": 0
      }
    },
    {
      "replicationNumber": 3,
      "duration": 63.00173746744792,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 104,
        "q0": 0
      }
    },
    {
      "replicationNumber": 4,
      "duration": 63.46024169921875,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 101,
        "q0": 0
      }
    },
    {
      "replicationNumber": 5,
      "duration": 63.2218994140625,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 98,
        "q0": 0
      }
    },
    {
      "replicationNumber": 6,
      "duration": 63.3444580078125,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 84,
        "q0": 0
      }
    },
    {
      "replicationNumber": 7,
      "duration": 63.03446858723958,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 100,
        "q0": 0
      }
    },
    {
      "replicationNumber": 8,
      "duration": 63.06692301432292,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 123,
        "q0": 0
      }
    },
    {
      "replicationNumber": 9,
      "duration": 63.02421875,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 88,
        "q0": 0
      }
    },
    {
      "replicationNumber": 10,
      "duration": 63.70067545572917,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 103,
        "q0": 0
      }
    },
    {
      "replicationNumber": 11,
      "duration": 63.018697102864586,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 100,
        "q0": 0
      }
    },
    {
      "replicationNumber": 12,
      "duration": 63.0138671875,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 86,
        "q0": 0
      }
    },
    {
      "replicationNumber": 13,
      "duration": 63.111897786458336,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 89,
        "q0": 0
      }
    },
    {
      "replicationNumber": 14,
      "duration": 63.56114908854167,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 106,
        "q0": 0
      }
    },
    {
      "replicationNumber": 15,
      "duration": 63.00354817708333,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 96,
        "q0": 0
      }
    },
    {
      "replicationNumber": 16,
      "duration": 63.16066080729167,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 99,
        "q0": 0
      }
    },
    {
      "replicationNumber": 17,
      "duration": 64.38050130208333,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 91,
        "q0": 0
      }
    },
    {
      "replicationNumber": 18,
      "duration": 63.21853841145833,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 113,
        "q0": 0
      }
    },
    {
      "replicationNumber": 19,
      "duration": 63.21931559244792,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 97,
        "q0": 0
      }
    },
    {
      "replicationNumber": 20,
      "duration": 63.125325520833336,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 91,
        "q0": 0
      }
    },
    {
      "replicationNumber": 21,
      "duration": 63.18771565755208,
      "queueFinalCounts": {
        "q1": 0,
        "q2": 0,
        "q3": 0,
        "q4": 0,
        "q5": 0,
        "q6": 0,
        "q7": 0,
        "q8": 0,
        "q9": 0,
        "q10": 96,
        "q0": 0
      }
    },
    { "replicationNumber": 22, "duration": 63.29828287760416, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 95, "q0": 0 } },
    { "replicationNumber": 23, "duration": 63.660685221354164, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 98, "q0": 0 } },
    { "replicationNumber": 24, "duration": 63.02434493001302, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 99, "q0": 0 } },
    { "replicationNumber": 25, "duration": 63.021028645833336, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 100, "q0": 0 } },
    { "replicationNumber": 26, "duration": 63.267794509710815, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 95, "q0": 0 } },
    { "replicationNumber": 27, "duration": 63.22013346354167, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 95, "q0": 0 } },
    { "replicationNumber": 28, "duration": 63.018595377604164, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 97, "q0": 0 } },
    { "replicationNumber": 29, "duration": 63.15923987507916, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 95, "q0": 0 } },
    { "replicationNumber": 30, "duration": 63.30397033691406, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 94, "q0": 0 } },
    { "replicationNumber": 31, "duration": 64.30154622395833, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 94, "q0": 0 } },
    { "replicationNumber": 32, "duration": 63.48622233072917, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 98, "q0": 0 } },
    { "replicationNumber": 33, "duration": 63.40794158935547, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 97, "q0": 0 } },
    { "replicationNumber": 34, "duration": 63.02534586588542, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 95, "q0": 0 } },
    { "replicationNumber": 35, "duration": 63.07823944091797, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 93, "q0": 0 } },
    { "replicationNumber": 36, "duration": 63.4907958984375, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 98, "q0": 0 } },
    { "replicationNumber": 37, "duration": 63.504222005208336, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 94, "q0": 0 } },
    { "replicationNumber": 38, "duration": 63.30396499633789, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 98, "q0": 0 } },
    { "replicationNumber": 39, "duration": 63.31055740443423, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 96, "q0": 0 } },
    { "replicationNumber": 40, "duration": 63.36018371582031, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 98, "q0": 0 } },
    { "replicationNumber": 41, "duration": 63.05600868858167, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 96, "q0": 0 } },
    { "replicationNumber": 42, "duration": 63.40798369344075, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 96, "q0": 0 } },
    { "replicationNumber": 43, "duration": 64.32187050098923, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 98, "q0": 0 } },
    { "replicationNumber": 44, "duration": 63.026413661603525, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 97, "q0": 0 } },
    { "replicationNumber": 45, "duration": 63.46199859203672, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 96, "q0": 0 } },
    { "replicationNumber": 46, "duration": 63.30397266710485, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 95, "q0": 0 } },
    { "replicationNumber": 47, "duration": 63.490790807663235, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 95, "q0": 0 } },
    { "replicationNumber": 48, "duration": 63.4906005859375, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 98, "q0": 0 } },
    { "replicationNumber": 49, "duration": 63.34446487426758, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 97, "q0": 0 } },
    { "replicationNumber": 50, "duration": 63.02082824707031, "queueFinalCounts": { "q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0, "q9": 0, "q10": 96, "q0": 0 } }
  ]
};
// -------------------------------------------------------------------------

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

  // ATUALIZAÇÃO: Usa o novo mock de dados.
  const [data, setData] = useState(SIMULATION_DATA_PLACEHOLDER);

  // Lógica de processamento dos dados
  const processedData = useMemo(() => {
    if (!data) return { summaryStats: [], detailedQueueMetrics: [], stabilityData: [], durationStdDev: 0, totalEntitiesProcessed: 0, SINK_QUEUE_ID: null };

    const avgDuration = data.averageDuration;

    // --- LÓGICA DINÂMICA PARA ENCONTRAR O DISSIPADOR (SINK_QUEUE_ID) ---

    // 1. ORDENAR as filas numericamente por ID (q0, q1, q2, ...)
    const sortedQueueMetrics = data.queueMetrics.slice().sort((a, b) => {
      // Extrai o número da string 'qX'
      const numA = parseInt(a.queueName.substring(1));
      const numB = parseInt(b.queueName.substring(1));
      return numA - numB;
    });

    let SINK_QUEUE_ID = null;

    // 2. ITERAR INVERSAMENTE para encontrar a ÚLTIMA FILA POPULADA
    for (let i = sortedQueueMetrics.length - 1; i >= 0; i--) {
      const metric = sortedQueueMetrics[i];
      // Verifica se o tamanho médio da fila é significativamente maior que zero.
      // O novo mock tem q10 como a última fila populada
      if (metric.averageSize > 0.001) {
        SINK_QUEUE_ID = metric.queueName;
        break;
      }
    }

    // 3. FALLBACK: Se nenhuma fila estava populada (como no seu exemplo),
    // assumimos que a fila de MAIOR ID é o ponto final teórico (q10 neste caso, a última do array ordenado).
    if (!SINK_QUEUE_ID && sortedQueueMetrics.length > 0) {
      // Encontrar a fila de maior ID (maior número)
      const lastQueue = sortedQueueMetrics.reduce((max, current) => {
        const numMax = parseInt(max.queueName.substring(1));
        const numCurrent = parseInt(current.queueName.substring(1));
        return numCurrent > numMax ? current : max;
      }, sortedQueueMetrics[0]);
      SINK_QUEUE_ID = lastQueue.queueName;
    }


    // 4. CALCULA ENTIDADES CONCLUÍDAS (totalEntitiesProcessed)
    let totalEntitiesProcessed = 0;
    if (SINK_QUEUE_ID) {
      const sumFinalCounts = data.logs.reduce((sum, log) => {
        const finalCount = log.queueFinalCounts[SINK_QUEUE_ID] || 0;
        return sum + finalCount;
      }, 0);
      totalEntitiesProcessed = sumFinalCounts / data.totalReplications;
    }


    // 5. CALCULA THROUGHPUT GLOBAL
    const globalThroughput = totalEntitiesProcessed > 0 && avgDuration > 0
        ? (totalEntitiesProcessed / avgDuration)
        : 0.000;

    // A. Estatísticas Globais
    const summaryStats = [
      { title: "Média de Duração", value: `${avgDuration.toFixed(4).replace('.', ',')} dias`, icon: <Clock className="h-6 w-6 text-blue-500" /> },
      { title: "Total de Réplicas", value: data.totalReplications, icon: <Layers className="h-6 w-6 text-orange-500" /> },
      { title: "Throughput Global Médio", value: `${globalThroughput.toFixed(3).replace('.', ',')} / dia`, icon: <TrendingUp className="h-6 w-6 text-purple-500" /> },
      {
        title: `Entidades Concluídas (via ${SINK_QUEUE_ID})`, // Nome dinâmico
        value: totalEntitiesProcessed.toFixed(0),
        icon: <Package className="h-6 w-6 text-green-500" />
      },
    ];

    // B. Métricas de Fila (E[N])
    const detailedQueueMetrics = data.queueMetrics
        .map(metric => {
          const lastLog = data.logs[data.logs.length - 1];
          const finalCount = lastLog?.queueFinalCounts[metric.queueName] ?? 0;

          return {
            id: metric.queueName,
            name: metric.queueName,
            averageSize: metric.averageSize,
            stdDevSize: metric.stdDev,
            finalCount: finalCount,
          };
        })
        .sort((a, b) => b.averageSize - a.averageSize); // O maior averageSize vai para o topo (GARGALO)

    // C. Dados para Gráfico de Estabilidade
    const stabilityData = data.logs.map(log => ({
      name: `Replica ${log.replicationNumber}`,
      Duration: log.duration,
      Avg_Duration: avgDuration
    }));

    return { summaryStats, detailedQueueMetrics, stabilityData, durationStdDev: data.durationStdDev, totalEntitiesProcessed, SINK_QUEUE_ID };
  }, [data]);

  const { summaryStats, detailedQueueMetrics, stabilityData, durationStdDev, totalEntitiesProcessed, SINK_QUEUE_ID } = processedData;

  // Handlers (Mantidos)
  const handleExport = (type) => {
    toast({
      title: `Export ${type}`,
      description: "🚧 This feature has not been implemented yet. Feel free to request it anytime! 🚀",
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
            title: "Error reading data",
            description: "Could not read process data to save it.",
            variant: "destructive",
          });
        }
      } else {
        toast({
          title: "Error",
          description: "Could not find process data to save.",
          variant: "destructive",
        });
      }
    }
  };

  if (!data) {
    return <div className="p-8 text-center text-xl text-muted-foreground">Loading simulation results...</div>;
  }


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
              <Link to={`/simulations/${simulationId}/processes/${processId}/simulate`} className="inline-flex items-center text-primary hover:text-primary/80 transition-colors mb-2 group">
                <ArrowLeft className="mr-2 h-5 w-5 group-hover:-translate-x-1 transition-transform" />
                Back to Simulation Experimentation Setup
              </Link>
              <h1 className="text-4xl md:text-5xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-primary to-accent">
                Simulation Results
              </h1>
              <p className="text-lg text-muted-foreground mt-1">
                Detailed analysis for process with ID:
                <code className="bg-muted px-2 py-1 rounded-md text-foreground/90 font-semibold">{processId}</code>
              </p>
            </div>
            {!user && (
                <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                  <Button onClick={handleSaveProcess} size="lg" className="bg-accent hover:bg-accent/90">
                    <Save className="mr-2 h-5 w-5" />
                    Save Results to Library
                  </Button>
                </motion.div>
            )}
          </div>
        </header>

        <section className="mb-10">
          <h2 className="text-3xl font-bold mb-6 text-foreground flex items-center">
            <BarChart2 className="mr-3 h-8 w-8 text-primary"/>
            Simulation Summary
          </h2>
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
            <Card key="stdDev" className="shadow-md hover:shadow-lg transition-shadow duration-300 bg-card">
              <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-base font-semibold text-card-foreground">Desvio Padrão (Duração)</CardTitle>
                <AlertTriangle className="h-6 w-6 text-red-500" />
              </CardHeader>
              <CardContent>
                <div className="text-3xl font-bold text-red-500">{durationStdDev.toFixed(4).replace('.', ',')}</div>
              </CardContent>
            </Card>
          </div>
        </section>

        <section className="mb-10">
          <h2 className="text-3xl font-bold mb-6 text-foreground flex items-center">
            <Users className="mr-3 h-8 w-8 text-primary"/>
            Detailed Queue Metrics (E[N] - Avg Size)
          </h2>
          <Card className="shadow-md overflow-hidden bg-card">
            <CardContent className="p-0">
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader className="bg-muted/50">
                    <TableRow>
                      <TableHead className="font-semibold text-muted-foreground">Queue ID</TableHead>
                      <TableHead className="text-right font-semibold text-muted-foreground">E[N] - Avg Size</TableHead>
                      <TableHead className="text-right font-semibold text-muted-foreground">Std Dev (Size)</TableHead>
                      <TableHead className="text-right font-semibold text-muted-foreground">Final Count (Last R.)</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {detailedQueueMetrics.map((queue, index) => {
                      // Se a fila é a de maior averageSize E o averageSize é maior que um pequeno limiar (para evitar 0.000 como gargalo)
                      const isBottleneck = index === 0 && queue.averageSize > 0.01;

                      return (
                          <TableRow key={queue.id} className="border-b-border hover:bg-muted/30">

                            <TableCell className={`font-medium py-3 flex items-center ${isBottleneck ? 'text-destructive font-bold' : 'text-card-foreground'}`}>
                              {isBottleneck ? <AlertTriangle className="mr-2 h-4 w-4 text-destructive" /> : null}
                              {queue.name}
                              {isBottleneck && <span className="ml-2 px-2 text-xs font-semibold rounded-full bg-destructive/20 text-destructive">GARGALO</span>}
                            </TableCell>

                            <TableCell className={`text-right py-3 ${isBottleneck ? 'text-destructive font-bold' : 'text-card-foreground'}`}>
                              {queue.averageSize.toFixed(2)}
                            </TableCell>
                            <TableCell className="text-right text-card-foreground py-3">{queue.stdDevSize.toFixed(3)}</TableCell>
                            <TableCell className="text-right text-card-foreground py-3">{queue.finalCount}</TableCell>
                          </TableRow>
                      );
                    })}
                  </TableBody>
                </Table>
              </div>
            </CardContent>
          </Card>
        </section>

        <section>
          <h2 className="text-3xl font-bold mb-6 text-foreground flex items-center">
            <LineChart className="mr-3 h-8 w-8 text-primary"/>
            Detailed Graph Visualizations
          </h2>

          <div className="grid gap-8 md:grid-cols-1 lg:grid-cols-2">

            <ChartWrapper title="Simulação Estabilidade (Duração por Réplica)" icon={<Clock />}>
              <ResponsiveContainer width="100%" height="100%">
                <ReLineChart data={stabilityData} margin={{ top: 5, right: 30, left: 0, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                  <XAxis dataKey="name" tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 12 }} />
                  <YAxis tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 12 }} label={{ value: 'Duração (dias)', angle: -90, position: 'insideLeft', fill: 'hsl(var(--muted-foreground))', fontSize: 12 }}/>
                  <Tooltip formatter={(value, name) => [value.toFixed(4).replace('.', ','), name]} contentStyle={{ backgroundColor: 'hsl(var(--popover))', borderColor: 'hsl(var(--border))', borderRadius: '0.5rem' }} />
                  <Legend wrapperStyle={{ fontSize: "14px", paddingTop: '10px' }} />
                  <Line type="monotone" dataKey="Duration" name="Duração da Réplica" stroke="hsl(var(--primary))" strokeWidth={2.5} activeDot={{ r: 7 }} dot={{ r: 4 }} />
                  <Line type="monotone" dataKey="Avg_Duration" name="Média Geral" stroke="hsl(var(--accent))" strokeWidth={2} strokeDasharray="5 5" dot={false} />
                </ReLineChart>
              </ResponsiveContainer>
            </ChartWrapper>

            <ChartWrapper title="Resource Utilization (%) (Waiting for data)" icon={<Users />} />
            <ChartWrapper title="Stage Throughput (Waiting for data)" icon={<TrendingUp />} />

            <Card className="h-[400px] flex flex-col shadow-lg hover:shadow-xl transition-shadow duration-300 bg-card lg:col-span-1">
              <CardHeader className="border-b border-border">
                <CardTitle className="text-lg font-semibold text-card-foreground flex items-center">
                  <AlertCircle className="mr-2 h-5 w-5 text-yellow-500"/>
                  Attention Points (Análise Dinâmica Ativa)
                </CardTitle>
                <CardDescription className="text-sm text-muted-foreground">
                  A fila de saída (`Dissipador`) é determinada dinamicamente pela **Última Fila Populada** no processo.
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-3 pt-4">
                <p className="text-muted-foreground text-center pt-10">
                  <span className="font-bold text-foreground">SINK_QUEUE_ID Detectado:</span> <code className="bg-muted px-2 py-1 rounded-md text-primary font-semibold">{SINK_QUEUE_ID}</code>
                </p>
                <p className="text-muted-foreground text-center">
                  <span className="font-bold text-foreground">Total Médio de Entidades Processadas:</span> <span className="font-semibold text-primary">{totalEntitiesProcessed.toFixed(0)}</span>
                </p>
                <p className="text-muted-foreground text-center">
                  <span className="font-bold text-foreground">Gargalo (Maior E[N]):</span> {detailedQueueMetrics.length > 0 && detailedQueueMetrics[0].averageSize > 0.01 ? <span className="text-destructive font-bold">{detailedQueueMetrics[0].name}</span> : 'Não detectado ou E[N] próximo de zero.'}
                </p>
                <p className="text-muted-foreground text-center pt-5">
                  <AlertCircle className="inline h-4 w-4 mr-1 text-yellow-500"/> Análise automatizada de gargalos e tempos altos virá aqui.
                </p>
              </CardContent>
            </Card>

          </div>
        </section>
      </motion.div>
  );
};

export default SimulationResults;