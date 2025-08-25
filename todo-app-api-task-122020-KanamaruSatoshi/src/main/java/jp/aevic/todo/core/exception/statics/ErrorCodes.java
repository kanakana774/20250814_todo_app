package jp.aevic.todo.core.exception.statics;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * エラーコードの列挙型
 * エラー設計書に基づく
 */
@Getter
@AllArgsConstructor
public enum ErrorCodes {
    //不正なパラメータによるリクエスト
    INVALID_PARAMETER("badRequest.invalid-field"),
    //不正なオブジェクトによるリクエスト
    INVALID_JSON("badRequest.invalid-json"),
    //リソースが存在しない場合
    NOT_FOUND_RESOURCE("notFound.resource"),
    //リクエストパスが存在しない場合
    NOT_FOUND_PATH("notFound.path"),
    //楽観ロックエラーが発生した場合
    OPTIMISTIC_LOCK("conflict.optimistic"),
    //想定外のエラーが生じた場合
    INTERNAL_UNEXPECTED_ERROR("internal-server-error.unexpected"),
    //内部エラーにおいて想定しない引数によるエラーが発生した場合
    INTERNAL_ARGUMENT_ERROR("internal-server-error.invalid-argument");

    //エラーコード
    private final String code;
}