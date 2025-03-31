import { useEffect, useState } from "react";
import { getAllDeliveryProcesses, deleteDeliveryProcess, createDeliveryProcess } from "../services/deliveryProcessService";

interface DeliveryProcess {
  id: number;
  type: string;
  index: number;
  modelInfo: string;
}

export default function Home() {
  const [processes, setProcesses] = useState<DeliveryProcess[]>([]);
  const [newProcess, setNewProcess] = useState<Omit<DeliveryProcess, "id">>({
    type: "",
    index: 0,
    modelInfo: "",
  });

  useEffect(() => {
    fetchProcesses();
  }, []);

  const fetchProcesses = async () => {
    try {
      const data: DeliveryProcess[] = await getAllDeliveryProcesses();
      setProcesses(data);
    } catch (error) {
      console.error("Erro ao buscar processos de entrega:", error);
    }
  };

  const handleDelete = async (id: number) => {
    if (confirm("Tem certeza que deseja excluir este processo?")) {
      try {
        await deleteDeliveryProcess(id);
        setProcesses(prevProcesses => prevProcesses.filter(process => process.id !== id));
      } catch (error) {
        console.error("Erro ao excluir o processo:", error);
      }
    }
  };

  const handleCreate = async () => {
    try {
      const createdProcess = await createDeliveryProcess(newProcess);
      setProcesses([...processes, createdProcess]);
      setNewProcess({ type: "", index: 0, modelInfo: "" }); // Reseta o formulário
    } catch (error) {
      console.error("Erro ao criar processo:", error);
    }
  };

  return (
    <div>
      <h1>Dashboard</h1>
      <h2>Processos de Entrega</h2>

      {/* Formulário para Adicionar Novo Processo */}
      <div style={{ marginBottom: "20px", padding: "10px", border: "1px solid #ccc" }}>
        <h3>Adicionar Processo</h3>
        <label>Índice:</label>
        <input
          type="number"
          value={newProcess.index}
          onChange={(e) => setNewProcess({ ...newProcess, index: Number(e.target.value) })}
        />
        <br />
        <label>Tipo:</label>
        <input
          type="text"
          value={newProcess.type}
          onChange={(e) => setNewProcess({ ...newProcess, type: e.target.value })}
        />
        <br />
        <label>Model Info:</label>
        <input
          type="text"
          value={newProcess.modelInfo}
          onChange={(e) => setNewProcess({ ...newProcess, modelInfo: e.target.value })}
        />
        <br />
        <button onClick={handleCreate} style={{ marginTop: "10px", backgroundColor: "green", color: "white" }}>
          Adicionar Processo
        </button>
      </div>

      {/* Lista de Processos */}
      <ul>
        {processes.length === 0 ? (
          <p>Nenhum processo encontrado.</p>
        ) : (
          processes.map((process) => (
            <li key={process.id} style={{ marginBottom: "10px", borderBottom: "1px solid #ccc", paddingBottom: "10px" }}>
              <strong>Índice:</strong> {process.index} <br />
              <strong>Tipo:</strong> {process.type} <br />
              <strong>Model Info:</strong> {process.modelInfo} <br />
              <button onClick={() => handleDelete(process.id)} style={{ backgroundColor: "red", color: "white", marginTop: "5px" }}>
                Excluir
              </button>
            </li>
          ))
        )}
      </ul>
    </div>
  );
}
