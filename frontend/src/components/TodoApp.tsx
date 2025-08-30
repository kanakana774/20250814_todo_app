import { useEffect, useState } from "react";
import "./TodoApp.css";
import axios from "axios";

// ===============================================
// 型定義（バックエンド）
// ===============================================

interface TagEntity {
  tagId: number;
  name: string;
  version: number;
}
interface TodoEntity {
  todoId: number;
  title: string;
  content: string;
  version: number;
  tags: TagEntity[];
}
// ===============================================
// 型定義（フロントエンド）
// ===============================================

interface SelectedTag {
  tagId: number;
  name: string;
}
interface BaseTodoForm {
  title: string;
  content: string;
  tags: SelectedTag[];
}
interface PostTodoForm extends BaseTodoForm {
  type: "newTodo";
}
interface PutTodoForm extends BaseTodoForm {
  type: "existingTodo";
  todoId: number;
  version: number;
}
type EditingTodoForm = PostTodoForm | PutTodoForm;

// request
type PostTodoRequest = Omit<PostTodoForm, "type">;
type PutTodoRequest = Omit<PutTodoForm, "type">;

// responce
type TodoResponse = TodoEntity;

// ===============================================
// Modalコンポーネント (Todoの登録・更新フォーム)
// ===============================================

type ModalProps = {
  onClose: () => void;
  onSubmit: (editingTodoForm: EditingTodoForm) => void;
  editingTodo: EditingTodoForm;
  availableTags: TagEntity[];
};

const Modal = ({
  onClose,
  onSubmit,
  editingTodo,
  availableTags,
}: ModalProps) => {
  // モーダル内部のフォーム状態
  const [todoForm, setTodoForm] = useState(editingTodo);

  // propsのeditingTodoが変更されたら、内部のフォーム状態を同期する
  // これにより、別のTodoを編集する際にフォームの内容が正しくリセットされる
  useEffect(() => {
    setTodoForm(editingTodo);
  }, [editingTodo]);

  // 入力フィールドの変更ハンドラ
  const handleTodoChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ): void => {
    const { name, value } = e.target;
    // jsの算出プロパティ名記法
    // オブジェクトのプロパティを動的に変えたい場合、角括弧 [] の中に式を記述することができ、それが計算されてプロパティ名として使用される
    setTodoForm((prev) => ({ ...prev, [name]: value }));
  };

  // タグの選択/解除ハンドラ (IDで管理)
  const handleTagChange = (e: React.ChangeEvent<HTMLInputElement>): void => {
    const { id, checked } = e.target;
    const tagId = parseInt(id.replace("tag-", ""), 10); // IDを数値に変換
    const selectedTag = availableTags.find((tag) => tag.tagId === tagId);

    if (!selectedTag) return; // 見つからない場合は何もしない

    setTodoForm((prev) => {
      const newTags = checked
        ? [...prev.tags, selectedTag] // タグオブジェクト全体を追加
        : prev.tags.filter((t) => t.tagId !== selectedTag.tagId); // IDでフィルタリングして削除

      return { ...prev, tags: newTags };
    });
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2 className="modal-title">
          {todoForm.type === "newTodo" ? "新しいTodoを登録" : "Todoを更新"}
        </h2>
        <form
          onSubmit={(e) => {
            e.preventDefault();
            onSubmit(todoForm);
          }}
          className="modal-form"
        >
          <label className="modal-label-input-group">
            タイトル:{" "}
            <input
              type="text"
              name="title"
              value={todoForm.title}
              onChange={handleTodoChange}
              className="modal-input"
              placeholder="Todoのタイトル"
            />
          </label>

          <label className="modal-label-input-group">
            内容:{" "}
            <textarea
              name="content"
              value={todoForm.content}
              onChange={handleTodoChange}
              rows={4}
              className="modal-textarea"
              placeholder="Todoの詳細内容"
            ></textarea>
          </label>

          {/* タグ選択セクション */}
          <div className="modal-form-group">
            {" "}
            <span className="modal-tag-label">タグ:</span>
            <div className="modal-tag-list">
              {availableTags.map((tag) => (
                <div key={tag.tagId} className="modal-tag-item">
                  {" "}
                  <input
                    type="checkbox"
                    id={`tag-${tag.tagId}`} // IDを文字列として使用
                    value={tag.name} // valueはタグ名
                    checked={todoForm.tags.some((t) => t.tagId === tag.tagId)} // IDで選択されているか判定
                    onChange={handleTagChange}
                    className="modal-checkbox"
                  />
                  <label
                    htmlFor={`tag-${tag.tagId}`}
                    className="modal-checkbox-label"
                  >
                    {tag.name}
                  </label>
                </div>
              ))}
            </div>
          </div>

          {/* ボタン群 */}
          <div className="modal-buttons">
            <button type="submit" className="modal-submit-button">
              {todoForm.type === "newTodo" ? "登録" : "更新"}
            </button>
            <button
              type="button"
              onClick={onClose}
              className="modal-cancel-button"
            >
              キャンセル
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

// ===============================================
// Todoコンポーネント (個々のTodoカード)
// ===============================================

type TodoProps = {
  todo: PutTodoForm;
  onTodoCardClick: (todo: PutTodoForm) => void;
  onCompButtonClick: (
    e: React.MouseEvent<HTMLButtonElement>,
    todo: PutTodoForm
  ) => void;
};

const Todo = ({ todo, onTodoCardClick, onCompButtonClick }: TodoProps) => {
  return (
    // divにrole="button"とtabIndex="0"を追加することでアクセシビリティを向上
    // role="button": スクリーンリーダーにボタンとして認識させる
    // tabIndex="0": キーボードのTabキーでフォーカスできるようにする
    <div
      className="todo-card"
      onClick={() => onTodoCardClick(todo)}
      role="button"
      tabIndex={0} // キーボード操作を可能にする
      // EnterキーやSpaceキーでの操作も可能にする (ReactのonClickはこれらを自動的に処理する場合が多い)
      onKeyDown={(e) => {
        if (e.key === "Enter" || e.key === " ") {
          onTodoCardClick(todo);
        }
      }}
    >
      <h3>{todo.title}</h3>
      <p>{todo.content}</p>
      <div className="tag-list">
        {todo.tags.map((tag) => (
          <div
            key={tag.tagId} // keyにtag.idを使用
            className="tag-item"
          >
            {tag.name}
          </div>
        ))}
      </div>
      <button
        onClick={(e) => {
          // e.stopPropagation(); // 親(Todoカード)へのクリック伝搬を止める。
          onCompButtonClick(e, todo);
        }}
        className="complete-button"
      >
        完了
      </button>
    </div>
  );
};

// ===============================================
// CreateTodoButtonコンポーネント (+)ボタン
// ===============================================

type CreateTodoButtonProps = {
  onClick: () => void;
};
const CreateTodoButton = ({ onClick }: CreateTodoButtonProps) => {
  return (
    <div className="create-button-container">
      <button onClick={onClick} className="create-button">
        +
      </button>
    </div>
  );
};

// ===============================================
// SearchFormコンポーネント (検索フォーム)
// ===============================================

type SearchFormProps = {
  onSearch: (searchValue: string) => void;
};
const SearchForm = ({ onSearch }: SearchFormProps) => {
  const [searchValue, setSearchValue] = useState("");
  return (
    <div className="search-form-container">
      <input
        type="text"
        placeholder="タイトルを検索"
        value={searchValue}
        onChange={(e) => {
          setSearchValue(e.target.value);
        }}
        className="search-input"
      />
      <button onClick={() => onSearch(searchValue)} className="search-button">
        {" "}
        検索
      </button>
    </div>
  );
};

// ===============================================
// 初期値
// ===============================================

const newTodo: PostTodoForm = {
  type: "newTodo",
  title: "",
  content: "",
  tags: [],
};

// ===============================================
// メインコンポーネント (TodoApp)
// ===============================================

// 利用可能なタグのリスト
const availableTags: TagEntity[] = [
  { tagId: 1, name: "#仕事", version: 0 },
  { tagId: 2, name: "#プライベート", version: 0 },
  { tagId: 3, name: "#緊急", version: 0 },
  { tagId: 4, name: "#買い物", version: 0 },
  { tagId: 5, name: "#学習", version: 0 },
];

const TodoApp = () => {
  const [todos, setTodos] = useState<TodoEntity[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingTodo, setEditingTodo] = useState<EditingTodoForm>(newTodo);

  // 初期表示
  useEffect(() => {
    fetchTodos();
  }, []);

  // todo取得
  const fetchTodos = async (): Promise<void> => {
    try {
      const response = await axios.get<TodoResponse[]>(
        "http://localhost:8080/todos"
      );
      setTodos(mapTodoResponseToEntity(response.data));
    } catch (error) {
      console.error("Todoの全件取得に失敗しました:", error);
    }
  };

  // 変換
  const mapTodoResponseToEntity = (responses: TodoResponse[]): TodoEntity[] => {
    return responses.map((res) => ({
      todoId: res.todoId,
      title: res.title,
      content: res.content,
      version: res.version,
      tags: res.tags,
    }));
  };

  // Todoの登録/更新処理
  const handleTodoSubmit = async (
    editingTodo: EditingTodoForm
  ): Promise<void> => {
    switch (editingTodo.type) {
      case "newTodo":
        try {
          // POSTリクエストで新しいTodoを登録
          await axios.post<PostTodoRequest>("http://localhost:8080/todos", {
            title: editingTodo.title,
            content: editingTodo.content,
            tags: editingTodo.tags,
          });
        } catch (error) {
          console.error("Todoの登録に失敗しました:", error);
        }
        break;
      case "existingTodo":
        try {
          // PUTリクエストで新しいTodoを登録
          await axios.put<PutTodoRequest>(
            `http://localhost:8080/todos/${editingTodo.todoId}`,
            {
              title: editingTodo.title,
              content: editingTodo.content,
              version: editingTodo.version,
              tags: editingTodo.tags,
            }
          );
        } catch (error) {
          console.error("Todoの更新に失敗しました:", error);
        }
        break;
    }
    fetchTodos(); // 成功したら全todoをfetch
    handleCloseModal(); // 更新後モーダルを閉じる
  };

  // Todoカードをクリックしたときの処理（更新用）
  const handleTodoCardClick = (todo: PutTodoForm): void => {
    setEditingTodo(todo); // クリックされたTodoを編集対象に設定
    setIsModalOpen(true); // モーダルを開く
  };

  // ＋ボタンをクリックしたときの処理（新規作成用）
  const handleCreateButtonClick = (): void => {
    setEditingTodo(newTodo); // 新規作成のため編集対象を初期値に設定
    setIsModalOpen(true); // モーダルを開く
  };

  // モーダルを閉じる処理
  const handleCloseModal = (): void => {
    setIsModalOpen(false); // モーダルを閉じる
    setEditingTodo(newTodo); // モーダルを閉じるときに編集対象をリセット
  };

  // 完了ボタンをクリックしたときの処理
  const handleCompButtonClick = (
    e: React.MouseEvent<HTMLButtonElement>,
    completedTodo: PutTodoForm
  ): void => {
    e.stopPropagation(); // 親(Todoカード)へのクリック伝搬を止める
    setTodos((prev) =>
      prev.filter((todo) => todo.todoId !== completedTodo.todoId)
    ); // 該当のTodoを削除
  };

  // 検索処理 (現在はダミー)
  const search = (searchValue: string) => {
    // 実際にはここでAPIを呼び出し、結果に基づいてtodosを更新します
    console.log("Searching for:", searchValue);
  };

  return (
    <div className="app-container">
      <main className="main-content-wrapper">
        <h1 className="app-title">Reminder App</h1>
        <SearchForm onSearch={search} />
        <div className="todo-grid">
          {todos.length === 0 ? (
            <p className="no-todos-message">
              まだTodoがありません。右下の「+」ボタンで追加しましょう！
            </p>
          ) : (
            todos.map((todo) => (
              <Todo
                key={todo.todoId}
                todo={{ ...todo, type: "existingTodo" }}
                onTodoCardClick={handleTodoCardClick}
                onCompButtonClick={handleCompButtonClick}
              />
            ))
          )}
        </div>
        <CreateTodoButton onClick={handleCreateButtonClick} />
        {isModalOpen && (
          <Modal
            onClose={handleCloseModal}
            onSubmit={handleTodoSubmit}
            editingTodo={editingTodo}
            availableTags={availableTags}
          />
        )}
      </main>
    </div>
  );
};
export default TodoApp;
