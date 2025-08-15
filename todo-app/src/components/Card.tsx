import { useState, type JSX } from "react";

interface CardProps {
  title: string;
  description: string;
}

const Card = ({ title, description }: CardProps): JSX.Element => {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleCardClick = (): void => {
    setIsModalOpen(true);
  };

  const handleButtonClick = (e: React.MouseEvent<HTMLButtonElement>): void => {
    e.stopPropagation(); // 親(Card)へのクリック伝搬を止める
    alert("ボタンが押されました！");
  };

  return (
    <>
      <div
        onClick={handleCardClick}
        style={{
          border: "1px solid #ccc",
          borderRadius: "8px",
          padding: "16px",
          cursor: "pointer",
          maxWidth: "300px",
          background: "#fff",
        }}
      >
        <h3>{title}</h3>
        <p>{description}</p>
        <button onClick={handleButtonClick}>ボタン</button>
      </div>

      {isModalOpen && (
        <div
          style={{
            position: "fixed",
            top: 0,
            left: 0,
            width: "100%",
            height: "100%",
            background: "rgba(0,0,0,0.5)",
          }}
          onClick={() => setIsModalOpen(false)}
        >
          <div
            style={{
              background: "#fff",
              padding: "20px",
              borderRadius: "8px",
              margin: "100px auto",
              width: "300px",
              color: "blue",
            }}
            onClick={(e) => e.stopPropagation()} // モーダル内クリックで閉じないように
          >
            <h2>モーダルタイトル</h2>
            <p>ここに詳細情報を表示</p>
            <button onClick={() => setIsModalOpen(false)}>閉じる</button>
          </div>
        </div>
      )}
    </>
  );
};

export default Card;
