package jp.aevic.todo.entity.todo;

import java.util.List;

import jp.aevic.todo.entity.tag.TagEntity;
import lombok.Data;

/**
 * TodoのEntityクラス
 */
@Data
public class TodoEntity {
    // todoID
    private int todoId;
    // タイトル
    private String title;
    // コンテンツ
    private String content;
    // 更新回数
    private int version;
    // todoに紐づいているタグ
    private List<TagEntity> tags;
}
