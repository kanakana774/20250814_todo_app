import { useEffect, useState } from "react";
import "./TodoApp.css";
import axios from "axios";

// ===============================================
// 型定義
// ===============================================

// タグの型定義
type Tag = {
  tag_id: number;
  name: string;
};

// Todoの基本型
type BaseTodo = {
  title: string;
  content: string;
  tags: Tag[];
};

// 新規作成用のTodo（idがない）
type NewTodo = BaseTodo & {
  todoId: null;
};

// 既存のTodo（idがある）
type ExistingTodo = BaseTodo & {
  todoId: number;
  version: number;
};

// 編集中のTodoはどちらかの型になる
type EditingTodo = NewTodo | ExistingTodo;

// TodoAppコンポーネントが管理するTodosはidがあるものだけ
type Todo = ExistingTodo;

// 型ガード関数 (新規Todoか既存Todoかを判定)
function isNewTodo(todo: EditingTodo): todo is NewTodo {
  return todo.todoId === null;
}

// ===============================================
// Modalコンポーネント (Todoの登録・更新フォーム)
// ===============================================

type ModalProps = {
  onClose: () => void;
  onSubmit: (editingTodo: EditingTodo) => void;
  editingTodo: NewTodo | ExistingTodo;
  availableTags: Tag[];
};

const Modal = ({
  onClose,
  onSubmit,
  editingTodo,
  availableTags,
}: ModalProps) => {
  // モーダル内部のフォーム状態
  const [todoForm, setTodoForm] = useState(editingTodo);
  // 新規作成モードかどうかの判定
  const isCreating = isNewTodo(editingTodo);

  // propsのeditingTodoが変更されたら、内部のフォーム状態を同期する
  // これにより、別のTodoを編集する際にフォームの内容が正しくリセットされます
  useEffect(() => {
    setTodoForm(editingTodo);
  }, [editingTodo]);

  // 入力フィールドの変更ハンドラ
  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    // jsの算出プロパティ名記法
    // オブジェクトのプロパティを動的に変えたい場合、角括弧 [] の中に式を記述することができ、それが計算されてプロパティ名として使用されます
    setTodoForm((prev) => ({ ...prev, [name]: value }));
  };

  // タグの選択/解除ハンドラ (IDで管理)
  const handleTagChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { id, checked } = e.target;
    const tagId = parseInt(id.replace("tag-", ""), 10); // IDを数値に変換
    const selectedTag = availableTags.find((tag) => tag.tag_id === tagId);

    if (!selectedTag) return; // 見つからない場合は何もしない

    setTodoForm((prev) => {
      const newTags = checked
        ? [...prev.tags, selectedTag] // タグオブジェクト全体を追加
        : prev.tags.filter((t) => t.tag_id !== selectedTag.tag_id); // IDでフィルタリングして削除

      return { ...prev, tags: newTags };
    });
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2 className="modal-title">
          {isCreating ? "新しいTodoを登録" : "Todoを更新"}
        </h2>
        <form
          onSubmit={(e) => {
            e.preventDefault();
            onSubmit(todoForm);
          }}
          className="modal-form"
        >
          <label className="modal-label-input-group">
            タイトル:
            <input
              type="text"
              name="title"
              value={todoForm.title}
              onChange={handleChange}
              className="modal-input"
              placeholder="Todoのタイトル"
            />
          </label>

          <label className="modal-label-input-group">
            内容:
            <textarea
              name="content"
              value={todoForm.content}
              onChange={handleChange}
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
                <div key={tag.tag_id} className="modal-tag-item">
                  {" "}
                  <input
                    type="checkbox"
                    id={`tag-${tag.tag_id}`} // IDを文字列として使用
                    value={tag.name} // valueはタグ名
                    checked={todoForm.tags.some((t) => t.tag_id === tag.tag_id)} // IDで選択されているか判定
                    onChange={handleTagChange}
                    className="modal-checkbox"
                  />
                  <label
                    htmlFor={`tag-${tag.tag_id}`}
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
              {isCreating ? "登録" : "更新"}
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
// Todoコンポーネント (個々のTodoカード) - 以前の提案を統合
// ===============================================

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
    // divにrole="button"とtabIndex="0"を追加することでアクセシビリティを向上
    // role="button": スクリーンリーダーにボタンとして認識させる
    // tabIndex="0": キーボードのTabキーでフォーカスできるようにする
    <div
      className="todo-card"
      onClick={() => onCardClick(todo)}
      role="button"
      tabIndex={0} // キーボード操作を可能にする
      // EnterキーやSpaceキーでの操作も可能にする (ReactのonClickはこれらを自動的に処理する場合が多い)
      onKeyDown={(e) => {
        if (e.key === "Enter" || e.key === " ") {
          onCardClick(todo);
        }
      }}
    >
      <h3>{todo.title}</h3>
      <p>{todo.content}</p>
      <div className="tag-list">
        {todo.tags.map((tag) => (
          <div
            key={tag.tag_id} // keyにtag.idを使用
            className="tag-item"
          >
            {tag.name}
          </div>
        ))}
      </div>
      <button
        onClick={(e) => {
          // e.stopPropagation(); // 親(Todoカード)へのクリック伝搬を止める。これは非常に重要。
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

const iniTodo: NewTodo = {
  todoId: null,
  title: "",
  content: "",
  tags: [],
};

// ===============================================
// メインコンポーネント (TodoApp)
// ===============================================

// 利用可能なタグのリスト
const availableTags: Tag[] = [
  { tag_id: 1, name: "#仕事" },
  { tag_id: 2, name: "#プライベート" },
  { tag_id: 3, name: "#緊急" },
  { tag_id: 4, name: "#買い物" },
  { tag_id: 5, name: "#学習" },
];

const TodoApp = () => {
  const [todos, setTodos] = useState<Todo[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingTodo, setEditingTodo] = useState<EditingTodo>(iniTodo);

  // 初期表示
  useEffect(() => {
    fetchTodos();
  }, []);

  // fetch
  const fetchTodos = async () => {
    try {
      const response = await axios.get<Todo[]>("http://localhost:8080/todos");
      setTodos(response.data);
    } catch (error) {
      console.error("Todoの全件取得に失敗しました:", error);
    }
  };

  // Todoの登録/更新処理
  const handleTodoUpdate = async (editingTodo: EditingTodo): Promise<void> => {
    if (isNewTodo(editingTodo)) {
      try {
        // POSTリクエストで新しいTodoを登録
        await axios.post<Todo>("http://localhost:8080/todos", {
          title: editingTodo.title,
          content: editingTodo.content,
          tags: editingTodo.tags,
        });
      } catch (error) {
        console.error("Todoの登録に失敗しました:", error);
      }
    } else {
      try {
        // PUTリクエストで新しいTodoを登録
        await axios.put<Todo>(
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
    }
    fetchTodos(); // 成功したら全todoをfetch
    handleCloseModal(); // 更新後モーダルを閉じる
  };

  // Todoカードをクリックしたときの処理（更新用）
  const handleTodoClick = (todo: ExistingTodo): void => {
    setEditingTodo(todo); // クリックされたTodoを編集対象に設定
    setIsModalOpen(true); // モーダルを開く
  };

  // ＋ボタンをクリックしたときの処理（新規作成用）
  const handleCreateButtonClick = (): void => {
    setEditingTodo(iniTodo); // 新規作成のため編集対象を初期値に設定
    setIsModalOpen(true); // モーダルを開く
  };

  // モーダルを閉じる処理
  const handleCloseModal = (): void => {
    setIsModalOpen(false); // モーダルを閉じる
    setEditingTodo(iniTodo); // モーダルを閉じるときに編集対象をリセット
  };

  // 完了ボタンをクリックしたときの処理
  const handleButtonClick = (
    e: React.MouseEvent<HTMLButtonElement>,
    completedTodo: ExistingTodo
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
    // 例:
    // fetch(`/api/todos?q=${searchValue}`)
    //   .then(response => response.json())
    //   .then(data => setTodos(data));
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
                todo={todo}
                onCardClick={handleTodoClick}
                onCompButtonClick={handleButtonClick}
              />
            ))
          )}
        </div>
        <CreateTodoButton onClick={handleCreateButtonClick} />
        {isModalOpen && (
          <Modal
            onClose={handleCloseModal}
            onSubmit={handleTodoUpdate}
            editingTodo={editingTodo}
            availableTags={availableTags}
          />
        )}
      </main>
    </div>
  );
};
export default TodoApp;
