package jp.aevic.todo.core.exception;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * エラーハンドリングを行う、独自のエラーコントローラークラス
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class GlobalErrorController extends BasicErrorController {
    /**
     * コンストラクタ
     *
     * @param errorAttributes  エラーレスポンスの生成とログの生成をするクラス
     * @param serverProperties ConfigurationProperties(portやpath設定)
     */
    public GlobalErrorController(MyErrorAttributes errorAttributes,
                                 ServerProperties serverProperties) {
        super(errorAttributes, serverProperties.getError());
    }

    /**
     * エラーハンドリングを行う実装
     *
     * @param request リクエストデータ
     * @return 返却するエラーレスポンス
     */
    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        //レスポンスに使用するエラーの情報の入ったErrorAttributesを取得
        //詳細は割愛するが、最終的にはmyErrorAttributesに定義したgetErrorAttributesが呼び出される
        Map<String, Object> errorAttributes =
                super.getErrorAttributes(request, ErrorAttributeOptions.of());

        //errorを返す(error内容と、ステータスコードでResponseを生成する)
        return new ResponseEntity<>(errorAttributes,
                HttpStatusCode.valueOf(Integer.parseInt(errorAttributes.get("status").toString())));
    }
}
