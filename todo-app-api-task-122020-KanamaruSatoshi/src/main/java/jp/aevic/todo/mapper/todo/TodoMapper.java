package jp.aevic.todo.mapper.todo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import jp.aevic.todo.entity.todo.TodoEntity;
import jp.aevic.todo.queryCondition.todo.GetTodosQueryCondition;

/**
 * TodoMapperクラス
 */
@Mapper
public interface TodoMapper {

    /**
     * 新規todo登録
     * 
     * @param todoEntity
     */
    public void insertTodo(TodoEntity todoEntity);

    /**
     * 一件取得
     * 
     * @param todoId
     * @return
     */
    public TodoEntity selectById(int todoId);

    /**
     * 一覧取得
     * 
     * @param queryCondition
     * @return
     */
    public List<TodoEntity> selectAll(GetTodosQueryCondition queryCondition);

    /**
     * 更新
     * 
     * @param todoEntity
     * @return
     */
    public int updateTodo(TodoEntity todoEntity);

    /**
     * 削除
     * 
     * @param todoEntity
     * @return
     */
    public int deleteTodo(TodoEntity todoEntity);
}
