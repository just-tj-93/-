package kr.just.tj.dao;


import java.sql.SQLException;

import org.apache.ibatis.annotations.Mapper;

import kr.just.tj.vo.UserVO;

@Mapper
public interface UserDAO {
	// 회원가입
	void insert(UserVO userVO) throws SQLException;
	// 이름으로 찾기
	UserVO selectByUsername(String user_name) throws SQLException;
	// 이메일로 찾기
	UserVO selectByEmail(String email) throws SQLException;
	// 동일한 아이디의 개수를 센다.(아이디 중복확인)
	int selectCountByUsername(String user_name) throws SQLException;
	// (이메일 중복확인)
	int selectCountByEmail(String email) throws SQLException;
	// id로 유저 정보 확인
	UserVO selectUserById(int user_id) throws SQLException;
	// 유저 정보 업데이트
	void updateUser(UserVO userVO);
}
