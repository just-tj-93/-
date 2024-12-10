package kr.just.tj.service;


import org.springframework.security.core.userdetails.UserDetailsService;

import kr.just.tj.vo.UserVO;

public interface UserService extends UserDetailsService{
	void insert(UserVO userVO);
	UserVO selectByUsername(String user_name);
	UserVO selectByEmail(String email);
	int selectCountByUsername(String user_name);
	UserVO selectUserById(int user_id);
	void updateUser(UserVO userVO);
}
