package jp.aevic.todo.queryCondition.todo;

import lombok.Data;

/**
 * todo検索条件用クラス
 */
@Data
public class GetTodosQueryCondition {
    // 取得上限数
    private Integer limit;
    // タイトル
    private String title;
}
