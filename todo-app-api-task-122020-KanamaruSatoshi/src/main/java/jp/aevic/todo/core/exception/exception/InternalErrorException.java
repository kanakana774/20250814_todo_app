package jp.aevic.todo.core.exception.exception;

import jp.aevic.todo.core.exception.statics.ErrorCodes;
import org.springframework.http.HttpStatus;

/**
 * InternalServerErrorの独自例外クラス
 */
public class InternalErrorException extends TodoRuntimeException {
    /**
     * コンストラクタ
     *
     * @param errorCodes エラー内容に対応したエラーコード
     */
    public InternalErrorException(ErrorCodes errorCodes) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, errorCodes);
    }
}
