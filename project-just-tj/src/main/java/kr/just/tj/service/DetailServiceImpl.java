package kr.just.tj.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.just.tj.dao.DetailDAO;
import kr.just.tj.vo.DetailVO;
import kr.just.tj.vo.PagingVO;
import lombok.extern.slf4j.Slf4j;

@Service("detailService")
@Slf4j
public class DetailServiceImpl implements DetailService{
	
	@Autowired
	private DetailDAO detailDAO;

	@Override
	public void insert(DetailVO detailVO) {
		try {
			detailDAO.insert(detailVO);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public int selectDetailCount(HashMap<String, String> map) {
		int totalcount = 0;
		try {
			totalcount = detailDAO.selectDetailCount(map);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totalcount;
	}

	@Override
	public PagingVO<DetailVO> selectDetailList(int currentPage, int sizeOfPage, int sizeOfBlock, String field,
			String search) {
		
		PagingVO<DetailVO> dv = null;
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("field", field == null || field.trim().length()==0 ? null : field);
			map.put("search", search == null || search.trim().length()==0 ? null : search);
			int totalCount = detailDAO.selectDetailCount(map);
			dv = new PagingVO<>(totalCount, currentPage, sizeOfPage, sizeOfBlock);
			if(totalCount > 0) {
				map.put("startNo", dv.getStartNo()+"");
				map.put("endNo", dv.getEndNo()+"");
				List<DetailVO> list = detailDAO.selectDetailList(map);
				dv.setList(list);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return dv;
	}

	@Override
	public List<DetailVO> selectByPlanId(int plan_id) {
		List<DetailVO> list = null;
		try {
			list = detailDAO.selectByPlanId(plan_id);
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void deleteDetail(int detail_id) {
		try {
			detailDAO.deleteDetail(detail_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public DetailVO selectByDetailId(int detail_id) {
		DetailVO detailVO = null;
		try {
			detailVO = detailDAO.selectByDetailId(detail_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return detailVO;
	}


}
