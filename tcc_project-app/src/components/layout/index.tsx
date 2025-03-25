import { useState, ReactNode } from "react";
import { Menu } from "./menu";

interface LayoutProps {
    titulo?: string;
    children?: ReactNode;
}

export const Layout: React.FC<LayoutProps> = () => {
    const [conteudoAtual, setConteudoAtual] = useState<string>("Selecione um item do menu");

    return (
        <div className="app">
            <section className="main-content columns is-fullheight">
                {/* 📌 Passando a função para o Menu atualizar o conteúdo */}
                <Menu setConteudoAtual={setConteudoAtual} />

                {/* 📌 Área principal que exibe o conteúdo */}
                <div className="container column is-10">
                    <div className="section">
                        <div className="card">
                            <div className="card-header">
                                <p className="card-header-title">{conteudoAtual}</p>
                            </div>
                            <div className="card-content">
                                <div className="content">Aqui aparecerá o conteúdo do item selecionado.</div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    );
};
