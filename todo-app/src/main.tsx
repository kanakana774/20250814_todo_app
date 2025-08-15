import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
// import "./index.css";
import App from "./App.tsx";
import TodoApp from "./components/TodoApp.tsx";
import Card from "./components/Card.tsx";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    {/* <App /> */}
    <TodoApp />
    {/* <Card title="title" description="content" /> */}
  </StrictMode>
);
