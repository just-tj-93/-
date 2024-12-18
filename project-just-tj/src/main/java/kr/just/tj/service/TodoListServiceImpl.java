package kr.just.tj.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.just.tj.dao.TodoListDAO;
import kr.just.tj.vo.PagingVO;
import kr.just.tj.vo.TodoListVO;
import lombok.extern.slf4j.Slf4j;

@Service("todoListService")
@Slf4j
public class TodoListServiceImpl implements TodoListService{
	
	@Autowired
	private TodoListDAO todoListDAO;

	@Override
	public void insert(TodoListVO todoListVO) {
		try {
			todoListDAO.insert(todoListVO);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public int selectTodoListCount(HashMap<String, String> map) {
		int totalcount = 0;
		try {
			totalcount = todoListDAO.selectTodoListCount(map);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totalcount;
	}

	@Override
	public PagingVO<TodoListVO> selectTodoList(int currentPage, int sizeOfPage, int sizeOfBlock, String field,
			String search) {
		
		PagingVO<TodoListVO> tv = null;
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("field", field == null || field.trim().length()==0 ? null : field);
			map.put("search", search == null || search.trim().length()==0 ? null : search);
			int totalCount = todoListDAO.selectTodoListCount(map);
			tv = new PagingVO<>(totalCount, currentPage, sizeOfPage, sizeOfBlock);
			if(totalCount > 0) {
				map.put("startNo", tv.getStartNo()+"");
				map.put("endNo", tv.getEndNo()+"");
				List<TodoListVO> list = todoListDAO.selectTodoList(map);
				tv.setList(list);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return tv;
	}

	@Override
	public List<TodoListVO> selectByTodoId(int todo_id) {
		List<TodoListVO> list = null;
		try {
			list = todoListDAO.selectByTodoId(todo_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void deleteTodoList(int todo_list_id) {
		try {
			todoListDAO.deleteTodoList(todo_list_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void checked(TodoListVO todoListVO) {
		try {
			todoListDAO.checked(todoListVO);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


}
