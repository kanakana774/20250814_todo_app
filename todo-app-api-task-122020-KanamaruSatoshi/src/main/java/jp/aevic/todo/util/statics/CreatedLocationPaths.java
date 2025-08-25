package jp.aevic.todo.util.statics;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 201Createdで返却するロケーションの生成に使用するパスの列挙型
 * 各リソースの1件取得を行うURLに対応しており、"リソース名/{リソースID}"のようなパスで記述される。
 * 詳細なURLについては、API設計に準拠する。
 */
@Getter
@AllArgsConstructor
public enum CreatedLocationPaths {
    //TODO
    TODO("todos/{todoId}"),
    //TAG
    TAG("tags/{tagId}"),
    //SAMPLE
    SAMPLE("samples/{sampleId}"),
    //SPORTS
    SPORTS("sports/{sportsId}");

    //パス
    private final String path;
}
