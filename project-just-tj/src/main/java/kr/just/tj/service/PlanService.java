package kr.just.tj.service;

import java.util.HashMap;

import kr.just.tj.vo.PagingVO;
import kr.just.tj.vo.PlanVO;

public interface PlanService {
	void insert(PlanVO planVO);
	int selectPlanCount(HashMap<String, String> map);
	PagingVO<PlanVO> selectPlanList(int currentPage, int sizeOfPage, int sizeOfBlock, String field, String search);
}
