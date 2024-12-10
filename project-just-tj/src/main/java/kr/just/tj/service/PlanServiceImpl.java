package kr.just.tj.service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.just.tj.dao.PlanDAO;
import kr.just.tj.vo.PagingVO;
import kr.just.tj.vo.PlanVO;
import lombok.extern.slf4j.Slf4j;

@Service("planService")
@Slf4j
public class PlanServiceImpl implements PlanService{
	
	@Autowired
	private PlanDAO planDAO;
	
	@Override
	public void insert(PlanVO planVO) {
		try {
			planDAO.insert(planVO);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		log.info("vo" + planVO);
	}

	@Override
	public PagingVO<PlanVO> selectPlanList(int currentPage, int sizeOfPage, int sizeOfBlock, String field,
			String search) {
		PagingVO<PlanVO> pv = null;
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("field", field == null || field.trim().length()==0 ? null : field);
			map.put("search", search == null || search.trim().length()==0 ? null : search);
			int totalCount = planDAO.selectPlanCount(map);
			pv = new PagingVO<>(totalCount, currentPage, sizeOfPage, sizeOfBlock);
			if(totalCount > 0) {
				map.put("startNo", pv.getStartNo()+"");
				map.put("endNo", pv.getEndNo()+"");
				List<PlanVO> list = planDAO.selectPlanList(map);
				pv.setList(list);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		return pv;
	}

	@Override
	public int selectPlanCount(HashMap<String, String> map) {
		int totalcount = 0;
		try {
			totalcount = planDAO.selectPlanCount(map);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return totalcount;
	}

	@Override
	public List<PlanVO> selectPlanByUserId(int user_id) {
		List<PlanVO> plist = null;
		try {
			plist = planDAO.selectPlanByUserId(user_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return plist;
	}

}
