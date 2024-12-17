package kr.just.tj.service;

import java.util.HashMap;
import java.util.List;

import kr.just.tj.vo.PagingVO;
import kr.just.tj.vo.TodoVO;

public interface TodoService {
	void insert(TodoVO todoVO);
	void delete(int todo_id);
	int selectTodoCount(HashMap<String, String> map);
	PagingVO<TodoVO> selectTodoList(int currentPage, int sizeOfPage, int sizeOfBlock, String field, String search);
	List<TodoVO> selectTodoByUserId(int user_id);
	TodoVO selectTodoByTodoId(int todo_id);
}
