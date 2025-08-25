package jp.aevic.todo.app.controller.todo;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jp.aevic.todo.entity.todo.TodoEntity;
import jp.aevic.todo.form.todo.TodoDeleteForm;
import jp.aevic.todo.form.todo.TodoPostForm;
import jp.aevic.todo.form.todo.TodoPutForm;
import jp.aevic.todo.logic.service.todo.ToDoService;
import jp.aevic.todo.query.todo.GetTodosQuery;
import jp.aevic.todo.queryCondition.todo.GetTodosQueryCondition;
import jp.aevic.todo.util.LocationUtil;
import jp.aevic.todo.util.statics.CreatedLocationPaths;

/**
 * TodoのControllerクラス
 */
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping(value = "/todos")
public class ToDoController {

    // DI対象クラス
    private final LocationUtil locationUtil;
    private final ToDoService service;

    /**
     * コンストラクタ
     * 
     * @param locationUtil
     * @param service
     */
    public ToDoController(LocationUtil locationUtil, ToDoService service) {
        // コンストラクタインジェクション
        this.locationUtil = locationUtil;
        this.service = service;
    }

    /**
     * 新規todo登録
     * 
     * @param form
     * @return
     */
    @PostMapping
    public ResponseEntity<String> postTodo(@RequestBody @Validated TodoPostForm form) {

        // todo登録情報設定
        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setTitle(form.getTitle());
        todoEntity.setContent(form.getContent());

        // 登録処理
        int todoId = service.postTodo(todoEntity, form.getTags());

        // httpレスポンスヘッダにURIを指定するため、ResponseEntityを生成して返す
        URI location = locationUtil.create(CreatedLocationPaths.TODO, todoId);
        return ResponseEntity.created(location).build();
    }

    /**
     * 一件取得
     * 
     * @param todoId
     * @return
     */
    @GetMapping(path = "/{todoId}")
    public TodoEntity getTodoById(@PathVariable String todoId) {
        return service.getTodoById(Integer.parseInt(todoId));
    }

    /**
     * 一覧取得
     * 
     * @param query
     * @return
     */
    @GetMapping
    public List<TodoEntity> getAllTodos(@Validated GetTodosQuery query) {
        GetTodosQueryCondition queryCondition = new GetTodosQueryCondition();
        queryCondition.setTitle(query.getTitle());
        queryCondition.setLimit(query.getLimit());
        return service.getAllTodos(queryCondition);
    }

    /**
     * 更新
     * 
     * @param todoId
     * @param form
     * @return
     */
    @PutMapping(path = "/{todoId}")
    public ResponseEntity<String> postTodo(
            @PathVariable String todoId, @RequestBody @Validated TodoPutForm form) {

        // todo更新情報設定
        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setTodoId(Integer.parseInt(todoId));
        todoEntity.setTitle(form.getTitle());
        todoEntity.setContent(form.getContent());
        todoEntity.setVersion(form.getVersion());

        // 更新処理
        service.putTodo(todoEntity, form.getTags());

        // ResponseEntityを生成して返す
        return ResponseEntity.noContent().build();
    }

    /**
     * 削除
     * 
     * @param todoId
     * @param form
     * @return
     */
    @DeleteMapping(path = "/{todoId}")
    public ResponseEntity<String> deleteTodo(
            @PathVariable String todoId, @RequestBody @Validated TodoDeleteForm form) {
        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setTodoId(Integer.parseInt(todoId));
        todoEntity.setVersion(form.getVersion());

        service.deleteTodo(todoEntity);

        // ResponseEntityを生成して返す
        return ResponseEntity.noContent().build();
    }
}
