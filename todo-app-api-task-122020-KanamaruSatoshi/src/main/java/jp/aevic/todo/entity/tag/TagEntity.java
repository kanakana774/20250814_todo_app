package jp.aevic.todo.entity.tag;

import lombok.Data;

/**
 * TagのEntityクラス
 * 
 */
@Data
public class TagEntity {
    // tagID
    private int tagId;
    // 名前
    private String name;
    // 更新回数
    private int version;
}
