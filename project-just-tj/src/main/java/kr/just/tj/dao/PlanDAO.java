package kr.just.tj.dao;


import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.just.tj.vo.PlanVO;

@Mapper
public interface PlanDAO {
	
	void insert(PlanVO planVO) throws SQLException;
	void delete(int plan_id) throws SQLException;
	List<PlanVO> selectPlanList(HashMap<String, String> map) throws SQLException;
	int selectPlanCount(HashMap<String, String> map) throws SQLException;
	List<PlanVO> selectPlanByUserId(int user_id) throws SQLException;
	PlanVO selectPlanByPlanId(int plan_id) throws SQLException;
}
