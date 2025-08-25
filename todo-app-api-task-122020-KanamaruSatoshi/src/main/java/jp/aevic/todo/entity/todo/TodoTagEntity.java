package jp.aevic.todo.entity.todo;

import lombok.Data;

/**
 * Todo_TagテーブルのEntityクラス
 */
@Data
public class TodoTagEntity {
    // todoID
    private int todoId;
    // tagID
    private int tagId;
}
