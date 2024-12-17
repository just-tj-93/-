package kr.just.tj.dao;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.just.tj.vo.TodoListVO;

@Mapper
public interface TodoListDAO {
	
	void insert(TodoListVO todoListVO) throws SQLException;
	void deleteTodoList(int todo_list_id) throws SQLException;
	int selectTodoListCount(HashMap<String, String> map) throws SQLException;
	List<TodoListVO> selectTodoList(HashMap<String, String> map) throws SQLException;
	List<TodoListVO> selectByTodoId(int todo_id) throws SQLException;
}
