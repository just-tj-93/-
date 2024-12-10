package kr.just.tj.service;

import java.util.HashMap;
import java.util.List;

import kr.just.tj.vo.DetailVO;
import kr.just.tj.vo.PagingVO;

public interface DetailService {
	void insert(DetailVO detailVO);
	int selectDetailCount(HashMap<String, String> map);
	PagingVO<DetailVO> selectDetailList(int currentPage, int sizeOfPage, int sizeOfBlock, String field, String search);
	List<DetailVO> selectByPlanId(int plan_id);
}
