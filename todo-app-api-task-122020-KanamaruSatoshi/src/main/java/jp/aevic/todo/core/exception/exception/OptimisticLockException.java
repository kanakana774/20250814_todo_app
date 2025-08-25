package jp.aevic.todo.core.exception.exception;

import jp.aevic.todo.core.exception.statics.ErrorCodes;
import org.springframework.http.HttpStatus;

/**
 * OptimisticLockExceptionの独自例外クラス
 */
public class OptimisticLockException extends TodoRuntimeException {
    /**
     * コンストラクタ
     *
     * @param errorCodes エラー内容に対応したエラーコード
     */
    public OptimisticLockException(ErrorCodes errorCodes) {
        super(HttpStatus.CONFLICT, errorCodes);
    }
}
