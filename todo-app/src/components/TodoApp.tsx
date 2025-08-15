import { useEffect, useState } from "react";
import "./TodoApp.css";

// ===============================================
// 型定義
// ===============================================

// タグの型定義
type Tag = {
  id: number;
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
  id: null;
};

// 既存のTodo（idがある）
type ExistingTodo = BaseTodo & {
  id: number;
};

// 編集中のTodoはどちらかの型になる
type EditingTodo = NewTodo | ExistingTodo;

// TodoAppコンポーネントが管理するTodosはidがあるものだけ
type Todo = ExistingTodo;

// 型ガード関数 (新規Todoか既存Todoかを判定)
function isNewTodo(todo: EditingTodo): todo is NewTodo {
  return todo.id === null;
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
    const selectedTag = availableTags.find((tag) => tag.id === tagId);

    if (!selectedTag) return; // 見つからない場合は何もしない

    setTodoForm((prev) => {
      const newTags = checked
        ? [...prev.tags, selectedTag] // タグオブジェクト全体を追加
        : prev.tags.filter((t) => t.id !== selectedTag.id); // IDでフィルタリングして削除

      return { ...prev, tags: newTags };
    });
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <h2 className="modal-content h2">
          {isCreating ? "新しいTodoを登録" : "Todoを更新"}
        </h2>
        <form
          onSubmit={(e) => {
            e.preventDefault();
            onSubmit(todoForm);
          }}
          className="modal-form-group"
        >
          {/* タイトル入力フィールド */}
          <label className="modal-form-group label">
            <span className="modal-form-group span">タイトル:</span>
            <input
              type="text"
              name="title"
              value={todoForm.title}
              onChange={handleChange}
              className="modal-input"
              placeholder="Todoのタイトル"
            />
          </label>

          {/* 内容入力フィールド */}
          <label className="modal-form-group label">
            <span className="modal-form-group span">内容:</span>
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
          <div className="modal-form-group label">
            <span className="modal-tag-label">タグ:</span>
            <div className="modal-tag-list">
              {availableTags.map((tag) => (
                <span key={tag.id} className="modal-tag-item">
                  {" "}
                  {/* keyにtag.idを使用 */}
                  <input
                    type="checkbox"
                    id={`tag-${tag.id}`} // IDを文字列として使用
                    value={tag.name} // valueはタグ名
                    checked={todoForm.tags.some((t) => t.id === tag.id)} // IDで選択されているか判定
                    onChange={handleTagChange}
                    className="modal-checkbox"
                  />
                  <label
                    htmlFor={`tag-${tag.id}`}
                    className="modal-checkbox-label"
                  >
                    {tag.name}
                  </label>
                </span>
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
// Todoコンポーネント (個々のTodoカード)
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
    <div className="todo-card" onClick={() => onCardClick(todo)}>
      <h3>{todo.title}</h3>
      <p>{todo.content}</p>
      <div className="tag-list">
        {todo.tags.map((tag) => (
          <div
            key={tag.id} // keyにtag.idを使用
            className="tag-item"
          >
            {tag.name}
          </div>
        ))}
      </div>
      <button
        onClick={(e) => {
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
  onClick: (searchValue: string) => void;
};
const SearchForm = ({ onClick }: SearchFormProps) => {
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
      <button onClick={() => onClick(searchValue)} className="search-button">
        検索
      </button>
    </div>
  );
};

// ===============================================
// 初期値
// ===============================================

const iniTodo: NewTodo = {
  id: null,
  title: "",
  content: "",
  tags: [],
};

// ===============================================
// メインコンポーネント (TodoApp)
// ===============================================

const TodoApp = () => {
  const [todos, setTodos] = useState<Todo[]>([
    {
      id: new Date().getTime(),
      title: "買い物",
      content: "化粧水",
      tags: [
        { id: 2, name: "#プライベート" },
        { id: 4, name: "#買い物" },
      ],
    },
  ]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingTodo, setEditingTodo] = useState<EditingTodo>(iniTodo);

  // 利用可能なタグのリスト (IDをnumberに変更)
  const availableTags: Tag[] = [
    { id: 1, name: "#仕事" },
    { id: 2, name: "#プライベート" },
    { id: 3, name: "#緊急" },
    { id: 4, name: "#買い物" },
    { id: 5, name: "#学習" },
  ];
  // Todoの登録/更新処理
  const handleTodoUpdate = (editingTodo: EditingTodo): void => {
    if (isNewTodo(editingTodo)) {
      setTodos((prev) => [...prev, { ...editingTodo, id: Date.now() }]);
    } else {
      setTodos((prev) =>
        prev.map((todo) =>
          // 既存のTodoを更新する際は、editingTodoの全てのプロパティを上書きします
          todo.id === editingTodo.id ? { ...todo, ...editingTodo } : todo
        )
      );
    }
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
    setTodos((prev) => prev.filter((todo) => todo.id !== completedTodo.id)); // 該当のTodoを削除
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
      <div className="main-content-wrapper">
        <h1 className="app-title">Reminder App</h1>
        <SearchForm onClick={search} />
        <div className="todo-grid">
          {todos.length === 0 ? (
            <p className="no-todos-message">
              まだTodoがありません。右下の「+」ボタンで追加しましょう！
            </p>
          ) : (
            todos.map((todo) => (
              <Todo
                key={todo.id}
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
            availableTags={availableTags} // 利用可能なタグをModalに渡す
          />
        )}
      </div>
    </div>
  );
};
export default TodoApp;
