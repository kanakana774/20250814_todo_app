package jp.aevic.todo.logic.service.todo;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.aevic.todo.core.exception.exception.NotFoundException;
import jp.aevic.todo.core.exception.exception.OptimisticLockException;
import jp.aevic.todo.core.exception.statics.ErrorCodes;
import jp.aevic.todo.entity.tag.TagEntity;
import jp.aevic.todo.entity.todo.TodoEntity;
import jp.aevic.todo.entity.todo.TodoTagEntity;
import jp.aevic.todo.mapper.tag.TagMapper;
import jp.aevic.todo.mapper.todo.TodoMapper;
import jp.aevic.todo.mapper.todo.TodoTagMapper;
import jp.aevic.todo.queryCondition.todo.GetTodosQueryCondition;

/**
 * ToDoServiceクラス
 */
@Service
public class ToDoService {

    // DI対象クラス
    private TodoMapper todoMapper;
    private TagMapper tagMapper;
    private TodoTagMapper todoTagMapper;

    /**
     * コンストラクタ
     * 
     * @param todoMapper
     * @param tagMapper
     * @param todoTagMapper
     */
    public ToDoService(TodoMapper todoMapper, TagMapper tagMapper, TodoTagMapper todoTagMapper) {
        this.todoMapper = todoMapper;
        this.tagMapper = tagMapper;
        this.todoTagMapper = todoTagMapper;
    }

    /**
     * 新規todo登録
     * 
     * @param todoEntity
     * @param requestTagIds
     * @return todoId
     */
    @Transactional
    public int postTodo(TodoEntity todoEntity, List<Integer> requestTagIds) {
        // tagId存在チェック
        findTagsOrThrow(requestTagIds);

        // 新規todo登録処理
        todoMapper.insertTodo(todoEntity);
        // 自動採番されたtodoId取得
        int resultTodoId = todoEntity.getTodoId();

        // todo_tag登録処理
        if (!requestTagIds.isEmpty()) {
            List<TodoTagEntity> todoTagEntities = requestTagIds.stream()
                    .map(tagId -> {
                        TodoTagEntity todoTagEntity = new TodoTagEntity();
                        todoTagEntity.setTagId(tagId);
                        todoTagEntity.setTodoId(resultTodoId);
                        return todoTagEntity;
                    }).toList();
            // todo_tag登録
            todoTagMapper.insertTodoTag(todoTagEntities);
        }

        return resultTodoId;
    }

    /**
     * 一件取得
     * 
     * @param todoId
     * @return
     */
    public TodoEntity getTodoById(int todoId) {
        return findTodoOrThrow(todoId);
    }

    /**
     * 一覧取得
     * 
     * @param queryCondition
     * @return
     */
    public List<TodoEntity> getAllTodos(GetTodosQueryCondition queryCondition) {
        return todoMapper.selectAll(queryCondition);
    }

    /**
     * 更新
     * 
     * @param todoEntity
     * @param requestTagIds
     */
    @Transactional
    public void putTodo(TodoEntity todoEntity, List<Integer> requestTagIds) {
        // todoId存在チェック
        findTodoOrThrow(todoEntity.getTodoId());
        // tagId存在チェック
        findTagsOrThrow(requestTagIds);

        // todo更新処理
        int updatedNum = todoMapper.updateTodo(todoEntity);
        updateOrDeleteFailedOrThrow(updatedNum);

        // todo_tag更新処理
        if (!requestTagIds.isEmpty()) {
            // todoTagEntityに登録する情報を設定
            List<TodoTagEntity> todoTagEntities = requestTagIds.stream()
                    .map(tagId -> {
                        TodoTagEntity todoTagEntity = new TodoTagEntity();
                        todoTagEntity.setTagId(tagId);
                        todoTagEntity.setTodoId(todoEntity.getTodoId());
                        return todoTagEntity;
                    }).toList();
            // 既存todo_tag削除後、新todo_tagに更新
            todoTagMapper.deleteTodoTagByTodoId(todoEntity.getTodoId());
            todoTagMapper.insertTodoTag(todoTagEntities);
        }
    }

    /**
     * 削除
     * 
     * @param todoEntity
     */
    @Transactional
    public void deleteTodo(TodoEntity todoEntity) {
        // todoId存在チェック
        findTodoOrThrow(todoEntity.getTodoId());

        // todo削除
        int deletedNum = todoMapper.deleteTodo(todoEntity);
        updateOrDeleteFailedOrThrow(deletedNum);

        // todo_tag削除
        todoTagMapper.deleteTodoTagByTodoId(todoEntity.getTodoId());
    }

    /**
     * 指定されたtodoIdが存在しなかった場合に404を投げる
     * 
     * @param todoId
     * @return
     */
    private TodoEntity findTodoOrThrow(int todoId) {
        // todoIdよりtodoを検索
        TodoEntity resultEntity = todoMapper.selectById(todoId);
        // todoが存在しなかった場合、404NotFoundを投げる
        if (Objects.isNull(resultEntity)) {
            // 404 notFound
            throw new NotFoundException(ErrorCodes.NOT_FOUND_RESOURCE);
        }
        return resultEntity;
    }

    /**
     * 指定されたtagが一つでも存在しなかった場合に404を投げる
     * 
     * @param requestTagIds
     */
    private void findTagsOrThrow(List<Integer> requestTagIds) {
        // tagIdが一つも指定されていなかったら、処理を抜ける
        if (requestTagIds.isEmpty()) {
            return;
        }
        // 指定されたtagIdたちよりtagを検索
        List<TagEntity> existsTags = tagMapper.selectByIds(requestTagIds);
        // tagが一つでも存在しなかった場合、404NotFoundを投げる
        if (requestTagIds.size() != existsTags.size()) {
            // 404 notFound
            throw new NotFoundException(ErrorCodes.NOT_FOUND_RESOURCE);
        }
    }

    /**
     * 楽観ロックにより更新/削除できなかった場合に409を投げる
     * 
     * @param resultNum
     */
    private void updateOrDeleteFailedOrThrow(int resultNum) {
        // 更新できなかった場合は409を投げる
        if (resultNum == 0) {
            // 409 optimistic
            throw new OptimisticLockException(ErrorCodes.OPTIMISTIC_LOCK);
        }
    }
}
