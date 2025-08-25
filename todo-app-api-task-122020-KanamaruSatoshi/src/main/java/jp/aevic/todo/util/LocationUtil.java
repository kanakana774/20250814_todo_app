package jp.aevic.todo.util;

import jp.aevic.todo.util.context.UriContext;
import jp.aevic.todo.util.statics.CreatedLocationPaths;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * フロントに返却するロケーションを扱うUtil
 * 作成したタグやTODOにアクセスするためのURLの生成を行います。
 */
@Component
@AllArgsConstructor
public class LocationUtil {
    //依存クラス
    private final UriContext uriContext;

    /**
     * URLを生成するメソッド
     * 1. uriContextが取得したlocationの末尾に引数で受け取ったパスを追加する
     * 2. 追加したパスに含まれるプレースホルダーに対して、第２引数以降のidをセットする
     *
     * @param path URL生成用のパスの列挙型
     * @param id   URL生成用のID
     * @return 生成したURL
     */
    public URI create(CreatedLocationPaths path, Object... id) {
        //外部設定されたURLを取得
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromUriString(uriContext.getLocation());
        //パスとIDを組み合わせてURLを生成し返却
        return uriComponentsBuilder.path(path.getPath())
                .buildAndExpand(id)
                .toUri();
    }
}