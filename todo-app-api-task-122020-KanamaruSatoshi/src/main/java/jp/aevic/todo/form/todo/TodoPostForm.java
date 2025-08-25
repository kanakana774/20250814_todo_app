package jp.aevic.todo.form.todo;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Todo新規作成時のFormクラス
 */
@Data
public class TodoPostForm {
    @NotBlank
    @Size(min = 1, max = 30)
    // タイトル
    private String title;
    @NotBlank
    @Size(min = 1, max = 100)
    // コンテンツ
    private String content;
    @NotNull
    @Size(min = 0, max = 5)
    // todoに紐づくtag
    private List<Integer> tags;
}
