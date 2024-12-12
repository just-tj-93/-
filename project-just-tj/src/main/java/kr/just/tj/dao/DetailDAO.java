package kr.just.tj.dao;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.just.tj.vo.DetailVO;

@Mapper
public interface DetailDAO {
	
	void insert(DetailVO detailVO) throws SQLException;
	void deleteDetail(int detail_id) throws SQLException;
	List<DetailVO> selectDetailList(HashMap<String, String> map) throws SQLException;
	int selectDetailCount(HashMap<String, String> map) throws SQLException;
	List<DetailVO> selectByPlanId(int plan_id) throws SQLException;
}
