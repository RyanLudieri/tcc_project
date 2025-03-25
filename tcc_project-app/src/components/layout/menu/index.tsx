import { useState } from "react";

interface MenuItem {
    id: number;
    label: string;
    children?: MenuItem[];
}

interface MenuProps {
    setConteudoAtual: (conteudo: string) => void;
}

export const Menu: React.FC<MenuProps> = ({ setConteudoAtual }) => {
    const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
    const [newLabel, setNewLabel] = useState("");
    const [parentId, setParentId] = useState<number | null>(null);

    const addMenuItem = () => {
        if (newLabel.trim()) {
            const newItem: MenuItem = {
                id: Date.now(),
                label: newLabel,
                children: []
            };

            if (parentId === null) {
                setMenuItems([...menuItems, newItem]);
            } else {
                setMenuItems(
                    menuItems.map(item =>
                        item.id === parentId
                            ? { ...item, children: [...(item.children || []), newItem] }
                            : item
                    )
                );
            }
            setNewLabel("");
            setParentId(null);
        }
    };

    return (
        <aside className="menu column is-2 is-narrow-mobile section is-hidden-mobile">
            <p className="menu-label">Library</p>
            <ul className="menu-list">
                {menuItems.map(item => (
                    <MenuItemComponent key={item.id} item={item} setConteudoAtual={setConteudoAtual} />
                ))}
            </ul>

            {/* Formulário para adicionar novos itens ao menu */}
            <div className="box mt-4">
                <h3 className="title is-6">Adicionar Item</h3>
                <div className="field">
                    <label className="label">Nome do Item</label>
                    <input
                        className="input"
                        type="text"
                        placeholder="Ex: Agile Process"
                        value={newLabel}
                        onChange={(e) => setNewLabel(e.target.value)}
                    />
                </div>
                <div className="field">
                    <label className="label">Adicionar como filho de:</label>
                    <div className="select">
                        <select value={parentId ?? ""} onChange={(e) => setParentId(e.target.value ? Number(e.target.value) : null)}>
                            <option value="">Nenhum (Criar no topo)</option>
                            {menuItems.map(item => (
                                <option key={item.id} value={item.id}>{item.label}</option>
                            ))}
                        </select>
                    </div>
                </div>
                <button className="button is-primary mt-2" onClick={addMenuItem}>
                    Adicionar
                </button>
            </div>
        </aside>
    );
};

const MenuItemComponent: React.FC<{ item: MenuItem; setConteudoAtual: (conteudo: string) => void }> = ({ item, setConteudoAtual }) => {
    return (
        <li>
            <a className="has-text-dark" onClick={() => setConteudoAtual(item.label)}>
                {item.label}
            </a>
            {item.children && item.children.length > 0 && (
                <ul className="menu-list ml-4">
                    {item.children.map(child => (
                        <MenuItemComponent key={child.id} item={child} setConteudoAtual={setConteudoAtual} />
                    ))}
                </ul>
            )}
        </li>
    );
};
