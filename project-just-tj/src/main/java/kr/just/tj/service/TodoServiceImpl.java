package kr.just.tj.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.just.tj.dao.TodoDAO;
import kr.just.tj.vo.PagingVO;
import kr.just.tj.vo.TodoVO;
import lombok.extern.slf4j.Slf4j;

@Service("todoService")
@Slf4j
public class TodoServiceImpl implements TodoService{
	
	@Autowired
	private TodoDAO todoDAO;
	
	@Override
	public void insert(TodoVO todoVO) {
		try {
			todoDAO.insert(todoVO);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		log.info("vo" + todoVO);
	}

	@Override
	public PagingVO<TodoVO> selectTodoList(int currentPage, int sizeOfPage, int sizeOfBlock, String field,
			String search) {
		PagingVO<TodoVO> pv = null;
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("field", field == null || field.trim().length()==0 ? null : field);
			map.put("search", search == null || search.trim().length()==0 ? null : search);
			int totalCount = todoDAO.selectTodoCount(map);
			pv = new PagingVO<>(totalCount, currentPage, sizeOfPage, sizeOfBlock);
			if(totalCount > 0) {
				map.put("startNo", pv.getStartNo()+"");
				map.put("endNo", pv.getEndNo()+"");
				List<TodoVO> list = todoDAO.selectTodoList(map);
				pv.setList(list);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return pv;
	}

	@Override
	public int selectTodoCount(HashMap<String, String> map) {
		int totalcount = 0;
		try {
			totalcount = todoDAO.selectTodoCount(map);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totalcount;
	}

	@Override
	public List<TodoVO> selectTodoByUserId(int user_id) {
		List<TodoVO> tlist = null;
		try {
			tlist = todoDAO.selectTodoByUserId(user_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tlist;
	}

	@Override
	public void delete(int todo_id) {
		try {
			todoDAO.delete(todo_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public TodoVO selectTodoByTodoId(int todo_id) {
		TodoVO todoVO = null;
		try {
			todoVO = todoDAO.selectTodoByTodoId(todo_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return todoVO;
	}

}
