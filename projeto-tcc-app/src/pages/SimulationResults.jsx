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
import { API_BASE_URL } from "@/config/api";


const ChartWrapper = ({ title, icon, children }) => (
    <Card className="h-[400px] flex flex-col shadow-lg hover:shadow-xl transition-shadow duration-300 bg-card">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2 border-b border-border">
        <CardTitle className="text-lg font-semibold text-card-foreground">{title}</CardTitle>
        {React.cloneElement(icon, { className: "h-5 w-5 text-primary" })}
      </CardHeader>
      <CardContent className="flex-1 flex items-center justify-center p-4">
        {children || <p className="text-muted-foreground text-center">Chart data will appear here after the simulation.</p>}
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
  { name: 'Senior Frontend Dev', Utilização: 0.85, capacidade: 1 },
  { name: 'Mid-level Backend Dev', Utilização: 0.78, capacidade: 1 },
  { name: 'Automation QA', Utilização: 0.92, capacidade: 1 },
  { name: 'UX Designer', Utilização: 0.60, capacidade: 1 },
  { name: 'Product Owner', Utilização: 0.70, capacidade: 1 },
  { name: 'Junior Data Analyst', Utilização: 0.55, capacidade: 1 },
];

const mockThroughputData = [
  { name: 'Week 1', Throughput: 8, meta: 10 },
  { name: 'Week 2', Throughput: 12, meta: 10 },
  { name: 'Week 3', Throughput: 9, meta: 10 },
  { name: 'Week 4', Throughput: 14, meta: 12 },
  { name: 'Week 5', Throughput: 11, meta: 12 },
  { name: 'Week 6', Throughput: 15, meta: 12 },
];

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8A2BE2', '#FF69B4'];

const SimulationResults = () => {
  const { simulationId, processId } = useParams();
  const { toast } = useToast();
  const { user } = useAuth();
  const navigate = useNavigate();

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

  const summaryStats = [
    { title: "Total Simulated Time", value: "98.2 days", icon: <Clock className="h-6 w-6 text-blue-500" /> },
    { title: "Total Entities Processed", value: "723", icon: <Package className="h-6 w-6 text-green-500" /> },
    { title: "Global Average Throughput", value: "7.36 units/day", icon: <TrendingUp className="h-6 w-6 text-purple-500" /> },
    { title: "Total Estimated Cost", value: "$15,820.50", icon: <DollarSign className="h-6 w-6 text-yellow-500" /> },
  ];

  const queueData = [
    { id: "q_entrada_demanda", name: "New Demand Queue", count: 8, avgTime: "1.8d", maxTime: "3.5d", throughput: "5/day" },
    { id: "q_desenv_frontend", name: "Frontend Dev Queue", count: 15, avgTime: "3.2d", maxTime: "7.1d", throughput: "2/day" },
    { id: "q_desenv_backend", name: "Backend Dev Queue", count: 10, avgTime: "4.5d", maxTime: "8.0d", throughput: "1.5/day" },
    { id: "q_testes_integrados", name: "Integration Testing Queue", count: 5, avgTime: "2.1d", maxTime: "4.2d", throughput: "3/day" },
    { id: "q_revisao_po", name: "P.O. Review Queue", count: 2, avgTime: "0.8d", maxTime: "1.5d", throughput: "4/day" },
    { id: "q_deploy_prod", name: "Production Deploy Queue", count: 1, avgTime: "0.5d", maxTime: "1.0d", throughput: "N/A" },
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

        <Card className="mb-8 shadow-xl bg-card">
          <CardHeader>
            <CardTitle className="text-2xl text-card-foreground flex items-center">
              <Settings className="mr-3 h-7 w-7 text-primary"/>
              Visualization & Export Controls
            </CardTitle>
          </CardHeader>
          <CardContent className="flex flex-col md:flex-row gap-6 items-center">
            <div className="flex-1 w-full md:w-auto">
              <Label htmlFor="metric-select" className="text-sm font-medium text-muted-foreground mb-1 block">
                Dashboard Primary Metric
              </Label>
              <Select onValueChange={(value) => console.log("Selected metric:", value)}>
                <SelectTrigger id="metric-select" className="w-full md:w-[300px] bg-background border-border focus:ring-primary">
                  <SelectValue placeholder="Select Primary Metric" />
                </SelectTrigger>
                <SelectContent className="bg-popover border-border">
                  <SelectItem value="e_n">E[N] — Average Queue Size</SelectItem>
                  <SelectItem value="e_r">E[R] — Average Response Time</SelectItem>
                  <SelectItem value="utilization">Resource Utilization</SelectItem>
                  <SelectItem value="throughput">Stage Throughput</SelectItem>
                  <SelectItem value="cost">Cost Analysis</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div className="flex gap-3 flex-wrap justify-center md:justify-end">
              <Button variant="outline" onClick={() => handleExport('CSV')} className="border-primary text-primary hover:bg-primary/10">
                <FileText className="mr-2 h-4 w-4" />
                Export Data (CSV)
              </Button>
              <Button variant="outline" onClick={() => handleExport('Charts')} className="border-primary text-primary hover:bg-primary/10">
                <Download className="mr-2 h-4 w-4" />
                Export Charts (PNG)
              </Button>
              <Button variant="outline" onClick={() => handleExport('PDF')} className="border-primary text-primary hover:bg-primary/10">
                <Download className="mr-2 h-4 w-4" />
                Save Report (PDF)
              </Button>
            </div>
          </CardContent>
        </Card>

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
          </div>
        </section>

        <section className="mb-10">
          <h2 className="text-3xl font-bold mb-6 text-foreground flex items-center">
            <Users className="mr-3 h-8 w-8 text-primary"/>
            Detailed Queue Metrics
          </h2>
          <Card className="shadow-md overflow-hidden bg-card">
            <CardContent className="p-0">
              <div className="overflow-x-auto">
                <Table>
                  <TableHeader className="bg-muted/50">
                    <TableRow>
                      <TableHead className="font-semibold text-muted-foreground">Queue Name</TableHead>
                      <TableHead className="text-right font-semibold text-muted-foreground">Current Entities</TableHead>
                      <TableHead className="text-right font-semibold text-muted-foreground">Avg Time (Queue)</TableHead>
                      <TableHead className="text-right font-semibold text-muted-foreground">Max Time (Queue)</TableHead>
                      <TableHead className="text-right font-semibold text-muted-foreground">Throughput (Queue)</TableHead>
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
          <h2 className="text-3xl font-bold mb-6 text-foreground flex items-center">
            <LineChart className="mr-3 h-8 w-8 text-primary"/>
            Detailed Graph Visualizations
          </h2>

          <div className="grid gap-8 md:grid-cols-1 lg:grid-cols-2">
            <ChartWrapper title="Average Response Time (E[R]) per Sprint" icon={<Clock />}>
              <ResponsiveContainer width="100%" height="100%">
                <ReLineChart data={mockTimeData} margin={{ top: 5, right: 30, left: 0, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                  <XAxis dataKey="name" tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 12 }} />
                  <YAxis tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 12 }} label={{ value: 'Days', angle: -90, position: 'insideLeft', fill: 'hsl(var(--muted-foreground))', fontSize: 12 }}/>
                  <Tooltip contentStyle={{ backgroundColor: 'hsl(var(--popover))', borderColor: 'hsl(var(--border))', borderRadius: '0.5rem' }} />
                  <Legend wrapperStyle={{ fontSize: "14px", paddingTop: '10px' }} />
                  <Line type="monotone" dataKey="E_R" name="Avg Time" stroke="hsl(var(--primary))" strokeWidth={2.5} activeDot={{ r: 7 }} dot={{ r: 4 }} />
                  <Line type="monotone" dataKey="meta" name="SLA Target" stroke="hsl(var(--accent))" strokeWidth={2} strokeDasharray="5 5" dot={false} />
                </ReLineChart>
              </ResponsiveContainer>
            </ChartWrapper>

            <ChartWrapper title="Resource Utilization (%)" icon={<Users />}>
              <ResponsiveContainer width="100%" height="100%">
                <ReBarChart data={mockResourceData} layout="vertical" margin={{ top: 5, right: 30, left: 30, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                  <XAxis type="number" domain={[0, 1]} tickFormatter={(tick) => `${tick * 100}%`} tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 12 }} />
                  <YAxis dataKey="name" type="category" width={150} tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 12 }} />
                  <Tooltip formatter={(value) => `${(value * 100).toFixed(1)}%`} contentStyle={{ backgroundColor: 'hsl(var(--popover))', borderColor: 'hsl(var(--border))', borderRadius: '0.5rem' }} />
                  <Legend wrapperStyle={{ fontSize: "14px", paddingTop: '10px' }} />
                  <Bar dataKey="Utilização" name="Current Utilization" radius={[0, 4, 4, 0]} barSize={20}>
                    {mockResourceData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.Utilização > 0.85 ? 'hsl(var(--destructive))' : COLORS[index % COLORS.length]} />
                    ))}
                  </Bar>
                  <Bar dataKey="capacidade" name="Capacity (100%)" fill="hsl(var(--border))" radius={[0, 4, 4, 0]} barSize={20} stackId="a" background={{ fill: 'hsl(var(--muted)/0.3)' }} />
                </ReBarChart>
              </ResponsiveContainer>
            </ChartWrapper>

            <ChartWrapper title="Weekly Throughput" icon={<TrendingUp />}>
              <ResponsiveContainer width="100%" height="100%">
                <ReAreaChart data={mockThroughputData} margin={{ top: 5, right: 30, left: 0, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                  <XAxis dataKey="name" tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 12 }} />
                  <YAxis tick={{ fill: 'hsl(var(--muted-foreground))', fontSize: 12 }} label={{ value: 'Units', angle: -90, position: 'insideLeft', fill: 'hsl(var(--muted-foreground))', fontSize: 12 }}/>
                  <Tooltip contentStyle={{ backgroundColor: 'hsl(var(--popover))', borderColor: 'hsl(var(--border))', borderRadius: '0.5rem' }} />
                  <Legend wrapperStyle={{ fontSize: "14px", paddingTop: '10px' }} />
                  <Area type="monotone" dataKey="Throughput" name="Completed" stroke="hsl(var(--primary))" fill="hsl(var(--primary) / 0.2)" strokeWidth={2.5} />
                  <Line type="monotone" dataKey="meta" name="Target" stroke="hsl(var(--accent))" strokeWidth={2} strokeDasharray="5 5" dot={false} />
                </ReAreaChart>
              </ResponsiveContainer>
            </ChartWrapper>

            <Card className="h-[400px] flex flex-col shadow-lg hover:shadow-xl transition-shadow duration-300 bg-card lg:col-span-1">
              <CardHeader className="border-b border-border">
                <CardTitle className="text-lg font-semibold text-card-foreground flex items-center">
                  <AlertCircle className="mr-2 h-5 w-5 text-yellow-500"/>
                  Attention Points
                </CardTitle>
                <CardDescription className="text-sm text-muted-foreground">
                  Metrics that require analysis or optimization.
                </CardDescription>
              </CardHeader>

              <CardContent className="space-y-3 pt-4">
                <div className="flex items-start p-2 rounded-md bg-destructive/10 border border-destructive/30">
                  <AlertTriangle className="h-5 w-5 text-destructive mr-3 mt-0.5 flex-shrink-0" />
                  <div>
                    <p className="text-sm font-semibold text-destructive-foreground">
                      Bottleneck Identified: <span className="font-bold">Automation QA (92% Util.)</span>
                    </p>
                    <p className="text-xs text-destructive-foreground/80">
                      Consider allocating more resources or optimizing test execution.
                    </p>
                  </div>
                </div>

                <div className="flex items-start p-2 rounded-md bg-yellow-500/10 border border-yellow-500/30">
                  <Clock className="h-5 w-5 text-yellow-600 mr-3 mt-0.5 flex-shrink-0" />
                  <div>
                    <p className="text-sm font-semibold text-yellow-700 dark:text-yellow-300">
                      High Avg Time: <span className="font-bold">Backend Dev (4.5d)</span>
                    </p>
                    <p className="text-xs text-yellow-600/80 dark:text-yellow-400/80">
                      Investigate potential blockers or excessive task complexity.
                    </p>
                  </div>
                </div>

                <div className="flex items-start p-2 rounded-md bg-green-500/10 border border-green-500/30">
                  <CheckCircle className="h-5 w-5 text-green-600 mr-3 mt-0.5 flex-shrink-0" />
                  <div>
                    <p className="text-sm font-semibold text-green-700 dark:text-green-300">
                      Good Performance: <span className="font-bold">P.O. Review (0.8d)</span>
                    </p>
                    <p className="text-xs text-green-600/80 dark:text-green-400/80">
                      Agile and efficient review process.
                    </p>
                  </div>
                </div>

              </CardContent>
            </Card>

          </div>
        </section>
      </motion.div>
  );
};

export default SimulationResults;
