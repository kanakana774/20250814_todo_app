import { useState } from "react";
import "./TodoApp.css";

// 型定義
type NewTodo = {
  id: null;
  title: string;
  content: string;
  tags: string[];
};

type ExistingTodo = {
  id: number;
  title: string;
  content: string;
  tags: string[];
};

type EditingTodo = NewTodo | ExistingTodo;

// 型ガード関数
function isNewTodo(todo: EditingTodo): todo is NewTodo {
  return todo.id === null;
}

// Modalコンポーネント
type ModalProps = {
  onClose: () => void;
  onSubmit: (editingTodo: EditingTodo) => void;
  editingTodo: EditingTodo;
  setEditingTodo: React.Dispatch<React.SetStateAction<EditingTodo>>;
};

const Modal = ({
  onClose,
  onSubmit,
  editingTodo,
  setEditingTodo,
}: ModalProps) => {
  const isCreating = isNewTodo(editingTodo);

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2>{isCreating ? "登録" : "更新"}</h2>
        <form
          onSubmit={(e) => {
            e.preventDefault();
            onSubmit(editingTodo);
          }}
        >
          <label>
            タイトル:
            <input
              type="text"
              value={editingTodo.title}
              onChange={(e) =>
                setEditingTodo((prev) => ({ ...prev, title: e.target.value }))
              }
            />
          </label>
          <label>
            内容:
            <textarea
              value={editingTodo.content}
              onChange={(e) =>
                setEditingTodo((prev) => ({ ...prev, content: e.target.value }))
              }
            />
          </label>

          <button type="submit">{isCreating ? "登録" : "更新"}</button>
          <button type="button" onClick={onClose}>
            キャンセル
          </button>
        </form>
      </div>
    </div>
  );
};

// Todoコンポーネント
type TodoProps = {
  todo: ExistingTodo;
  onCardClick: (todo: ExistingTodo) => void;
  onCompButtonClick: (
    e: React.MouseEvent<HTMLButtonElement>,
    todo: ExistingTodo
  ) => void;
};

const Todo = ({ todo, onCardClick, onCompButtonClick }: TodoProps) => {
  return (
    <div className="todo-card" onClick={() => onCardClick(todo)}>
      <h3>{todo.title}</h3>
      <p>{todo.content}</p>
      <div className="tag-container">
        {todo.tags.map((tag) => (
          <div key={tag} className="tag-card">
            {tag}
          </div>
        ))}
      </div>
      <button
        onClick={(e) => {
          onCompButtonClick(e, todo);
        }}
      >
        comp
      </button>
    </div>
  );
};

// 初期値
const iniTodo: NewTodo = {
  id: null,
  title: "",
  content: "",
  tags: [],
};

// メインコンポーネント
const TodoApp = () => {
  const [todos, setTodos] = useState<ExistingTodo[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingTodo, setEditingTodo] = useState<EditingTodo>(iniTodo);

  // 登録/更新
  const handleTodoUpdate = (editingTodo: EditingTodo): void => {
    if (isNewTodo(editingTodo)) {
      setTodos((prev) => [...prev, { ...editingTodo, id: Date.now() }]);
    } else {
      setTodos((prev) =>
        prev.map((todo) =>
          todo.id === editingTodo.id ? { ...todo, ...editingTodo } : todo
        )
      );
    }
    handleCloseModal();
  };

  // Todoカードクリック（更新）
  const handleTodoClick = (todo: ExistingTodo): void => {
    setEditingTodo(todo);
    setIsModalOpen(true);
  };

  // 新規作成ボタンクリック
  const handleCreateButtonClick = (): void => {
    setEditingTodo(iniTodo);
    setIsModalOpen(true);
  };

  // モーダル閉じる
  const handleCloseModal = (): void => {
    setIsModalOpen(false);
    setEditingTodo(iniTodo);
  };

  // 完了ボタンクリック
  const handleButtonClick = (
    e: React.MouseEvent<HTMLButtonElement>,
    completedTodo: ExistingTodo
  ): void => {
    e.stopPropagation();
    setTodos((prev) => prev.filter((todo) => todo.id !== completedTodo.id));
  };

  return (
    <div>
      <h1>Reminder</h1>
      <div className="form-container">
        <input type="text" />
        <button>search</button>
      </div>
      <div className="todo-container">
        {todos.map((todo) => (
          <Todo
            key={todo.id}
            todo={todo}
            onCardClick={handleTodoClick}
            onCompButtonClick={handleButtonClick}
          />
        ))}
      </div>
      <div className="create-todo-button-container">
        <button onClick={handleCreateButtonClick}>+</button>
      </div>
      {isModalOpen && (
        <Modal
          onClose={handleCloseModal}
          onSubmit={handleTodoUpdate}
          editingTodo={editingTodo}
          setEditingTodo={setEditingTodo}
        />
      )}
    </div>
  );
};

export default TodoApp;
