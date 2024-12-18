package kr.just.tj.service;

import java.util.HashMap;
import java.util.List;

import kr.just.tj.vo.PagingVO;
import kr.just.tj.vo.TodoListVO;

public interface TodoListService {
	void insert(TodoListVO todoListVO);
	void deleteTodoList(int todo_list_id);
	void checked(TodoListVO todoListVO);
	int selectTodoListCount(HashMap<String, String> map);
	PagingVO<TodoListVO> selectTodoList(int currentPage, int sizeOfPage, int sizeOfBlock, String field, String search);
	List<TodoListVO> selectByTodoId(int todo_id);
}
