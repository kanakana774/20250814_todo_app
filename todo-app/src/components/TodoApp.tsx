import { useState } from "react";
import "./TodoApp.css";

type ModalProps = {
  onClose: () => void;
  onSubmit: (editingTodo: EditingTodo) => void;
  editingTodo: EditingTodo;
  setEditingTodo: React.Dispatch<React.SetStateAction<EditingTodo>>;
};
type EditingTodo = {
  id: number | null;
  title: string;
  content: string;
  tags: string[];
};

const Modal = ({
  onClose,
  onSubmit,
  editingTodo,
  setEditingTodo,
}: ModalProps) => {
  const isCreating = !editingTodo;

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
                setEditingTodo((prev) => {
                  return {
                    ...(prev ?? { id: null, title: "", content: "", tags: [] }),
                    title: e.target.value,
                  };
                })
              }
            />
          </label>
          <label>
            内容:
            <textarea
              value={editingTodo.content}
              onChange={(e) =>
                setEditingTodo((prev) => {
                  return {
                    ...(prev ?? { id: null, title: "", content: "", tags: [] }),
                    content: e.target.value,
                  };
                })
              }
            ></textarea>
          </label>
          <button type="submit">登録/更新</button>
          <button type="button" onClick={onClose}>
            キャンセル
          </button>
        </form>
      </div>
    </div>
  );
};

type TodoProps = {
  todo: Todo;
  onCardClick: (todo: Todo) => void;
  onCompButtonClick: (
    e: React.MouseEvent<HTMLButtonElement, MouseEvent>,
    todo: Todo
  ) => void;
};
const Todo = ({ todo, onCardClick, onCompButtonClick }: TodoProps) => {
  return (
    <div className="todo-card" onClick={() => onCardClick(todo)}>
      <h3>{todo.title}</h3>
      <p>{todo.content}</p>
      <div className="tag-container">
        {todo.tags.map((tag) => {
          return (
            <div key={tag} className="tag-card">
              {tag}
            </div>
          );
        })}
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

type Todo = {
  id: number;
  title: string;
  content: string;
  tags: string[];
};

const iniTodo = {
  id: null,
  title: "",
  content: "",
  tags: [],
};

const TodoApp = () => {
  const [todos, setTodos] = useState<Todo[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingTodo, setEditingTodo] = useState<EditingTodo>(iniTodo);

  // Todo登録/更新
  const handleTodoUpdate = (editingTodo: EditingTodo): void => {
    debugger;
    if (editingTodo.id === null) {
      setTodos((prev) => [
        ...prev,
        {
          id: new Date().getTime(),
          title: editingTodo.title,
          content: editingTodo.content,
          tags: editingTodo.tags,
        },
      ]);
    } else {
      setTodos((prev) => {
        return prev.map((todo) => {
          if (todo.id === editingTodo.id) {
            return {
              ...todo,
              title: editingTodo.title,
              content: editingTodo.content,
              tags: editingTodo.tags,
            };
          } else {
            return todo;
          }
        });
      });
    }

    handleCloseModal();
  };

  // Todoカードをクリックしたときの処理（更新用）
  const handleTodoClick = (todo: Todo): void => {
    setEditingTodo(todo); // クリックされたTodoを編集対象に設定
    setIsModalOpen(true);
  };

  // ＋ボタンをクリックしたときの処理（新規作成用）
  const handleCreateButtonClick = (): void => {
    setEditingTodo(iniTodo); // 新規作成のため編集対象をnullに設定
    setIsModalOpen(true);
  };

  // モーダルを閉じる処理
  const handleCloseModal = (): void => {
    setIsModalOpen(false);
    setEditingTodo(iniTodo); // モーダルを閉じるときに編集対象をリセット
  };

  // 完了ボタン
  const handleButtonClick = (
    e: React.MouseEvent<HTMLButtonElement>,
    completedTodo: Todo
  ): void => {
    e.stopPropagation(); // 親(Card)へのクリック伝搬を止める
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
        {todos.map((todo) => {
          return (
            <Todo
              key={todo.id}
              todo={todo}
              onCardClick={handleTodoClick}
              onCompButtonClick={handleButtonClick}
            />
          );
        })}
      </div>
      <div className="create-todo-button-container">
        <button onClick={handleCreateButtonClick}>+</button>
      </div>
      {isModalOpen && (
        <Modal
          onClose={handleCloseModal}
          onSubmit={handleTodoUpdate}
          editingTodo={editingTodo} // editingTodoをpropsとして渡す
          setEditingTodo={setEditingTodo}
        />
      )}
    </div>
  );
};

export default TodoApp;
