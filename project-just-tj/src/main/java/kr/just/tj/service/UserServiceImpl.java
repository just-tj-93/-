package kr.just.tj.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.just.tj.dao.UserDAO;
import kr.just.tj.vo.UserVO;
import lombok.extern.slf4j.Slf4j;

@Service("userService")
@Slf4j
@Transactional
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserDAO userDAO;

	@Override
	public UserDetails loadUserByUsername(String user_name) throws UsernameNotFoundException {
		log.info("로그인 시도: {}", user_name);
		UserVO vo = null;
		try {
			vo = userDAO.selectByUsername(user_name);
			if(vo == null) {
				throw new UsernameNotFoundException("지정 아이디가 존재하지 않습니다.");
			}
		} catch (SQLException e) {
			log.error("로그인 에러 발생: {}", e.getMessage());
			e.printStackTrace();
		}
		log.info("로그인 성공: {}", vo);
		return vo;
	}
	
	// user_name으로 찾기
	@Override
	public UserVO selectByUsername(String user_name) {
		UserVO userVO = null;
		try {
			userVO = userDAO.selectByUsername(user_name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userVO;
	}
	
	//회원가입
	@Override
	public void insert(UserVO userVO) {
		log.info("회원가입 시도: {}", userVO);
		if(userVO != null) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			userVO.setPassword(passwordEncoder.encode(userVO.getPassword()));
			userVO.setUser_role("USER");
			try {
				userDAO.insert(userVO);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			log.info("회원가입 성공: {}", userVO);
		}
	}

	//아이디 중복 확인
	@Override
	public int selectCountByUsername(String user_name) {
		int countUsername = 1;
		try {
			countUsername = userDAO.selectCountByUsername(user_name);
		} catch (SQLException e) {
			log.error("아이디 중복 확인 중 오류 발생: {}", e.getMessage());
			e.printStackTrace();
		}
		return countUsername;
	}

	@Override
	public UserVO selectUserById(int user_id) {
		UserVO uv = null;
		try {
			uv = userDAO.selectUserById(user_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uv;
	}

	@Override
	public void updateUser(UserVO userVO) {
		userDAO.updateUser(userVO);
	}

	@Override
	public UserVO selectByEmail(String email) {
		UserVO uv = null;
		try {
			uv = userDAO.selectByEmail(email);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uv;
	}

}
