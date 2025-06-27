# 🎓 projeto-tcc: Plataforma Web para Modelagem e Simulação de Processos

Este é o repositório da API (backend) do meu Trabalho de Conclusão de Curso (TCC).

Através desta plataforma, o **usuário (cliente)** poderá:
1.  **Modelar qualquer tipo de processo**, de forma flexível e intuitiva.
2.  **Definir as medidas e parâmetros necessários para a simulação** desse processo.
3.  A partir da modelagem e dos parâmetros, a aplicação **gerará um arquivo XACDML** (eXtensible ACtivity Diagram Markup Language) com as medições setadas.
4.  Este arquivo XACDML será então **convertido e utilizado para executar a simulação** do processo.

## 🛠️ Tecnologias Utilizadas

* **Backend:**
    * Java 21
    * Spring Boot (Web, Data JPA)
    * PostgreSQL (Banco de Dados)
    * Lombok
    * Apache Commons Math3 (para distribuições estatísticas)
* **Acesso a Dados:** Spring Data JPA
* **Gerenciador de Dependências:** Maven
