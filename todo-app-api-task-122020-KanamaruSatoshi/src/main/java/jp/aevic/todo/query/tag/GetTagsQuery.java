package jp.aevic.todo.query.tag;

import lombok.Data;

/**
 * 一覧getで使用するqueryクラス
 * 
 */
@Data
public class GetTagsQuery {
    // 名前
    private String name;
}
