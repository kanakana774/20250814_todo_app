package jp.aevic.todo.core.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import jp.aevic.todo.core.exception.exception.BadRequestException;
import jp.aevic.todo.core.exception.exception.InternalErrorException;
import jp.aevic.todo.core.exception.exception.NotFoundException;
import jp.aevic.todo.core.exception.exception.OptimisticLockException;
import jp.aevic.todo.core.exception.exception.TodoRuntimeException;
import jp.aevic.todo.core.exception.statics.ErrorCodes;
import lombok.AllArgsConstructor;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * エラーレスポンスの生成とログの生成をするクラス
 * DefaultErrorAttributesより、以下の項目で取得可能な値が取得される
 * 1. エラー発生日時
 * 2. ステータスコード
 * 3. エラー理由
 * 4. 例外クラス名
 * 5. 例外メッセージ
 * 6. BindingResultなどから受け取るオブジェクトエラー
 * 7. スタックトレース
 * 8. 例外が発生したURLのパス
 */
@Component
@AllArgsConstructor
public class MyErrorAttributes extends DefaultErrorAttributes {
    //依存クラス
    private MyExceptionLogger logger;
    private final MessageSource messageSource;
    //ErrorAttributesのMapのキーとなる文字列の定数化
    private static final String TITLE = "title";
    private static final String STATUS = "status";
    private static final String CODE = "code";
    private static final String MESSAGE = "message";

    /**
     * 各例外に対応したエラーレスポンスとしてのErrorAttributesを生成し返却する
     * 独自にカスタムしたErrorAttributeを返却したかったので、
     * SpringBootのBasicErrorControllerのgetErrorAttributeで呼び出されている、
     * ErrorAttributesのgetErrorAttributeの実装に該当する、DefaultErrorAttributesを継承し,overrideした。
     * この実装を行うことで、BasicErrorControllerのgetErrorAttributeを呼び出すと、
     * 独自にカスタムしたErrorAttributeを返却できる。
     *
     * @param webRequest リクエストのデータ
     * @param options    ErrorAttributesのコンテンツの設定
     * @return 生成されたErrorAttributes
     */
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest,
                                                  ErrorAttributeOptions options) {
        //DefaultのErrorAttributeを取得
        Map<String, Object> defaultErrorAttributes = super.getErrorAttributes(webRequest, options);
        //ErrorResponseに不要な情報をErrorAttributeから取り除く
        Map<String, Object> initializeErrorAttributes =
                initializationOfErrorAttributes(defaultErrorAttributes);
        //error情報を取得する
        Throwable cause = getError(webRequest);
        //返却用のErrorAttributeの型を用意する(再代入の防止のため)
        Map<String, Object> errorAttributes;
        //TodoRuntimeException(独自実装例外クラス)の継承クラスの例外の場合
        if (cause instanceof TodoRuntimeException error) {
            errorAttributes =
                    todoRuntimeExceptionHandler(error, initializeErrorAttributes, webRequest);
        }
        //SpringBoot独自の例外の場合
        else if (cause instanceof MethodArgumentNotValidException error) {
            errorAttributes =
                    methodArgumentNotValidExceptionHandler(error, initializeErrorAttributes,
                            webRequest);
        } else if (cause instanceof BindException error) {
            errorAttributes = bindExceptionHandler(error, initializeErrorAttributes, webRequest);
        } else if (cause instanceof MissingRequestHeaderException) {
            errorAttributes = missingRequestHeaderExceptionHandler(initializeErrorAttributes);
        } else if (cause instanceof HttpMessageNotReadableException error) {
            errorAttributes =
                    httpMessageNotReadableExceptionHandler(error, initializeErrorAttributes,
                            webRequest);
        } else if (cause instanceof NoHandlerFoundException error) {
            errorAttributes = noHandlerFoundExceptionHandler(initializeErrorAttributes, error);
        }
        //ハンドリングできない例外(getErrorでエラーを抽出できないような詳細不明なエラー)の場合
        else if (cause == null) {
            errorAttributes = unexpectedErrorHandler(initializeErrorAttributes, webRequest);
        }
        //TodoRuntimeException(独自の例外)及びSpringBoot独自の例外以外のExceptionのハンドリングを行う
        else {
            errorAttributes = exceptionHandler(cause, initializeErrorAttributes, webRequest);
        }
        return errorAttributes;
    }

    /**
     * TodoRuntimeException(独自実装例外)固有のハンドリング
     *
     * @param ex                    ErrorAttributesの生成及びログの出力に使用するTodoRuntimeException
     * @param initializedAttributes 初期化後のErrorAttributes
     * @param webRequest            ログの出力に使用するリクエストデータ
     * @return ハンドリング後のErrorAttributes
     */
    private Map<String, Object> todoRuntimeExceptionHandler(
            TodoRuntimeException ex, Map<String, Object> initializedAttributes,
            WebRequest webRequest) {
        //ErrorAttributesに基本的な情報を詰める
        Map<String, Object> errorAttributes =
                putBasicContents(initializedAttributes, ex.getStatusCode(),
                        ex.getDetailMessageCode(), ex.getDetailMessageArguments());

        //InternalErrorExceptionの場合はERRORレベルのログを出す(URI,method,エラーメッセージを出力)
        if (ex instanceof InternalErrorException internalErrorException) {
            logger.printErrorLog(internalErrorException, webRequest);
        }
        //BadRequestExceptionの場合はDEBUGレベルのログを出す
        // (クラス名,エラーコード,エラーメッセージと不正のあったパラメーターのfield名,fieldエラーコード,エラーの起きた理由を出力)
        else if (ex instanceof BadRequestException badRequestException) {
            logger.printBadRequestDebugLog(badRequestException, webRequest);
        }
        //NotFoundResourceExceptionの場合はDEBUGレベルのログを出す(URI,method,エラーメッセージを出力)
        else if (ex instanceof NotFoundException notFoundResourceException) {
            logger.printDebugLog(notFoundResourceException, webRequest);
        }
        //OptimisticLockExceptionの場合はDEBUGレベルのログを出す(URI,method,エラーメッセージを出力)
        else if (ex instanceof OptimisticLockException optimisticLockException) {
            logger.printDebugLog(optimisticLockException, webRequest);
        }
        //その他独自例外の場合はDEBUGレベルのログを出す(クラス名,エラーコード,エラーメッセージを出力)
        else {
            logger.printDebugLog(ex, webRequest);
        }
        return errorAttributes;
    }

    /**
     * springBoot独自のExceptionであるMethodArgumentNotValidException固有のハンドリング
     *
     * @param ex                        ErrorAttributes
     *                                  の生成及びログの出力に使用するMethodArgumentNotValidException
     * @param initializeErrorAttributes 初期化後のErrorAttributes
     * @return ハンドリング後のErrorAttributes
     */
    private Map<String, Object> methodArgumentNotValidExceptionHandler(
            MethodArgumentNotValidException ex, Map<String, Object> initializeErrorAttributes,
            WebRequest webRequest) {
        //ErrorAttributesに基本的な情報を詰める
        Map<String, Object> errorAttributes =
                putBasicContents(initializeErrorAttributes, HttpStatus.BAD_REQUEST,
                        ErrorCodes.INVALID_PARAMETER.getCode(), new String[]{});

        //DEBUGログの出力(クラス名,エラーコード,エラーメッセージと不正のあったパラメーターのfield名,fieldエラーコード,エラーの起きた理由を出力)
        logger.printBadRequestDebugLog(createBadRequestException(
                Objects.requireNonNull(ex.getBindingResult().getTarget()).getClass()), webRequest);

        return errorAttributes;
    }

    /**
     * リクエストヘッダーがなかったときに使用されるMissingRequestHeaderException固有のハンドリング
     *
     * @param initializeErrorAttributes 初期化後のErrorAttributes
     * @return ハンドリング後のErrorAttributes
     */
    @Deprecated
    private Map<String, Object> missingRequestHeaderExceptionHandler(
            Map<String, Object> initializeErrorAttributes) {
        //ErrorAttributesに基本的な情報を詰める
        Map<String, Object> errorAttributes =
                putBasicContents(initializeErrorAttributes, HttpStatus.BAD_REQUEST,
                        ErrorCodes.INVALID_PARAMETER.getCode(), new String[]{});
        return errorAttributes;
    }

    /**
     * 型違いによりJSONからFormを生成できない場合などにスローされるHttpMessageNotReadableException固有のハンドリング
     *
     * @param ex                        例外の原因を生成するためのHttpMessageNotReadableException
     * @param initializeErrorAttributes 初期化後のErrorAttributes
     * @return ハンドリング後のErrorAttributes
     */
    private Map<String, Object> httpMessageNotReadableExceptionHandler(
            HttpMessageNotReadableException ex, Map<String, Object> initializeErrorAttributes,
            WebRequest webRequest) {
        //ErrorAttributesに基本的な情報を詰める
        //型違いエラーはでは要素が一つの想定だが、Form全体のパースエラーの場合、要素がないため、
        // こちらを先にセットしておき、フィールドエラーの場合は上書きする
        Map<String, Object> errorAttributes =
                putBasicContents(initializeErrorAttributes, HttpStatus.BAD_REQUEST,
                        ErrorCodes.INVALID_JSON.getCode(), new String[]{});

        //HttpMessageNotReadableExceptionの原因の生成
        Throwable causeOfError = ex.getCause();
        //不正な値のリストの生成
        Class<?> originClass = GlobalErrorController.class;

        //JsonをJavaクラスへマッピングした際に発生したエラーだった場合
        if (causeOfError instanceof JsonMappingException jsonMappingException) {
            //例外の発生したクラスの取得(JsonMappingExceptionが発生した際、Form内のフィールドならば、必ず1つはスローされたものが存在するためget(0)で取得)
            List<JsonMappingException.Reference> referenceList = jsonMappingException.getPath();
            if (!referenceList.isEmpty()) {
                // 不正のあったパラメーターとそのエラー内容のリストの生成
                originClass = referenceList.get(0).getFrom().getClass();
                errorAttributes =
                        putBasicContents(initializeErrorAttributes, HttpStatus.BAD_REQUEST,
                                ErrorCodes.INVALID_PARAMETER.getCode(), new String[]{});
            }
        }

        //DEBUGログの出力(クラス名,エラーコード,エラーメッセージと不正のあったパラメーターのfield名,fieldエラーコード,エラーの起きた理由を出力)
        logger.printBadRequestDebugLog(createBadRequestException(originClass), webRequest);

        return errorAttributes;
    }

    /**
     * 存在しないパスが呼び出された時のNoHandlerFoundException固有のハンドリング
     *
     * @param initializeErrorAttributes 初期化後のErrorAttributes
     * @param ex                        ログの出力に使用するException
     * @return ハンドリング後のErrorAttributes
     */
    private Map<String, Object> noHandlerFoundExceptionHandler(
            Map<String, Object> initializeErrorAttributes, NoHandlerFoundException ex) {
        //ErrorAttributesに基本的な情報を詰める
        Map<String, Object> errorAttributes =
                putBasicContents(initializeErrorAttributes, HttpStatus.NOT_FOUND,
                        ErrorCodes.NOT_FOUND_PATH.getCode(), new String[]{});

        //DEBUGログの出力(URI,エラーコード,エラーメッセージを出力する)
        logger.printNoHandlerFoundExceptionDebugLog(ex);

        return errorAttributes;
    }

    /**
     * TodoRuntimeException(独自の例外)及びSpringBoot独自の例外以外のハンドリング
     *
     * @param ex                        ログの出力に使用するThrowable
     * @param initializeErrorAttributes 初期化後のErrorAttributes
     * @param webRequest                ログの出力に使用するリクエストデータ
     * @return ハンドリング後のErrorAttributes
     */
    private Map<String, Object> exceptionHandler(Throwable ex,
                                                 Map<String, Object> initializeErrorAttributes,
                                                 WebRequest webRequest) {
        //ErrorAttributesに基本的な情報を詰める
        Map<String, Object> errorAttributes =
                putBasicContents(initializeErrorAttributes, HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorCodes.INTERNAL_UNEXPECTED_ERROR.getCode(), new String[]{});

        //ログに出力(URI,メソッド,例外の生じたクラス名,エラーメッセージを出力)
        logger.printExceptionErrorLog(ex, webRequest);

        return errorAttributes;
    }

    /**
     * ハンドリングできない例外(getErrorでエラーを抽出できないような詳細不明なエラー)の場合のハンドリング
     *
     * @param initializeErrorAttributes 初期化後のErrorAttributes
     * @param webRequest                ログの出力に使用するリクエストデータ
     * @return ハンドリング後のErrorAttributes
     */
    private Map<String, Object> unexpectedErrorHandler(
            Map<String, Object> initializeErrorAttributes, WebRequest webRequest) {
        //ErrorAttributesに基本的な情報を詰める
        Map<String, Object> errorAttributes =
                putBasicContents(initializeErrorAttributes, HttpStatus.BAD_REQUEST,
                        ErrorCodes.INTERNAL_UNEXPECTED_ERROR.getCode(), new String[]{});

        //ログに出力(URI,メソッド,エラーメッセージを出力)
        logger.printUnexpectedErrorLog(webRequest);

        return errorAttributes;
    }

    /**
     * ErrorAttributesの初期化
     * レスポンスとして返却する内容に不要なものが含まれないように、不要な内容は取り除く
     *
     * @param errorAttributes 初期化するErrorAttributes
     * @return 初期化したErrorAttributes
     */
    private Map<String, Object> initializationOfErrorAttributes(
            Map<String, Object> errorAttributes) {
        errorAttributes.remove("timestamp");
        errorAttributes.remove("error");
        errorAttributes.remove("exception");
        errorAttributes.remove("status");
        errorAttributes.remove("message");
        errorAttributes.remove("path");
        return errorAttributes;
    }

    /**
     * 基本的なErrorAttributesの生成メソッド
     *
     * @param errorAttributes  内容をセットするErrorAttributes
     * @param httpStatusCode   ErrorAttributesにセットするHttpStatusCode
     * @param errorMessageCode ErrorAttributesにセットするメッセージコードこれを元にErrorAttributesにセットするメッセージも生成する
     * @param messageArgs      ErrorAttributesにセットするメッセージでプロパティファイルで変数化している部分のメッセージ構成要素
     * @return 基本的な情報をセットしたErrorAttributes
     */
    private Map<String, Object> putBasicContents(Map<String, Object> errorAttributes,
                                                 HttpStatusCode httpStatusCode,
                                                 String errorMessageCode, Object[] messageArgs) {
        errorAttributes.put(TITLE, HttpStatus.valueOf(httpStatusCode.value()));
        errorAttributes.put(STATUS, httpStatusCode.value());
        errorAttributes.put(CODE, errorMessageCode);
        errorAttributes.put(MESSAGE,
                messageSource.getMessage(errorMessageCode, messageArgs, Locale.getDefault()));
        return errorAttributes;
    }

    /**
     * リクエストパラメーターのバリデーションで使用されるBindException固有のハンドリング
     *
     * @param ex                    ErrorAttributesの生成及びログの出力に使用するBindException
     * @param initializedAttributes 初期化後のErrorAttributes
     * @return ハンドリング後のErrorAttributes
     */
    private Map<String, Object> bindExceptionHandler(BindException ex,
                                                     Map<String, Object> initializedAttributes,
                                                     WebRequest webRequest) {
        //ErrorAttributesに基本的な情報を詰める
        Map<String, Object> errorAttributes =
                putBasicContents(initializedAttributes, HttpStatus.BAD_REQUEST,
                        ErrorCodes.INVALID_PARAMETER.getCode(), new String[]{});

        //DEBUGログの出力(クラス名,エラーコード,エラーメッセージと不正のあったパラメーターのfield名,fieldエラーコード,エラーの起きた理由を出力)
        logger.printBadRequestDebugLog(createBadRequestException(
                Objects.requireNonNull(ex.getBindingResult().getTarget()).getClass()), webRequest);

        return errorAttributes;
    }

    /**
     * BadRequestを生成する処理
     *
     * @param originClass BadRequestを生成するための例外が発生したクラス情報
     * @return BadRequestException
     */
    private BadRequestException createBadRequestException(Class<?> originClass) {
        return new BadRequestException(ErrorCodes.INVALID_PARAMETER);
    }
}