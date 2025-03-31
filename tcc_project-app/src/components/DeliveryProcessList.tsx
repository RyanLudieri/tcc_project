"use client"; // Next.js App Router

import { useEffect, useState } from "react";
import { getAllDeliveryProcesses, deleteDeliveryProcess } from "../services/deliveryProcessService";

interface DeliveryProcess {
  id: number;
  name: string;
  status: string;
}

export default function DeliveryProcessList() {
  const [processes, setProcesses] = useState<DeliveryProcess[]>([]);

  useEffect(() => {
    loadProcesses();
  }, []);

  const loadProcesses = async () => {
    try {
      const data = await getAllDeliveryProcesses();
      setProcesses(data);
    } catch (error) {
      console.error("Erro ao buscar processos de entrega", error);
    }
  };

  const handleDelete = async (id: number) => {
    if (confirm("Tem certeza que deseja excluir este processo?")) {
      await deleteDeliveryProcess(id);
      loadProcesses(); // Atualiza a lista
    }
  };

  return (
    <div className="p-4">
      <h2 className="text-2xl font-bold mb-4">Processos de Entrega</h2>
      <ul>
        {processes.map((process) => (
          <li key={process.id} className="border p-2 flex justify-between">
            <span>{process.name} - {process.status}</span>
            <button onClick={() => handleDelete(process.id)} className="text-red-500">Excluir</button>
          </li>
        ))}
      </ul>
    </div>
  );
}
