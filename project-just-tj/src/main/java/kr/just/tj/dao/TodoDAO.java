package kr.just.tj.dao;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.just.tj.vo.TodoVO;

@Mapper
public interface TodoDAO {
	
	void insert(TodoVO todoVO) throws SQLException;
	void delete(int todo_id) throws SQLException;
	int selectTodoCount(HashMap<String, String> map) throws SQLException;
	List<TodoVO> selectTodoList(HashMap<String, String> map) throws SQLException;
	List<TodoVO> selectTodoByUserId(int user_id) throws SQLException;
	TodoVO selectTodoByTodoId(int todo_id) throws SQLException;
}
