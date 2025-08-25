package jp.aevic.todo.form.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * タグ更新時のformクラス
 * 
 */
@Data
public class TagPutForm {
    @NotBlank
    @Size(min = 1, max = 30)
    // 名前
    private String name;
    @NotNull
    // 更新回数
    private Integer version;

}