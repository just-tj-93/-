package kr.just.tj.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.just.tj.vo.UserVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		log.info("로그인 성공 : " + authentication.getName());
		log.info("로그인 성공 : " + authentication.getPrincipal());
		
		// 세션에 저장하고 싶다.
		request.getSession().setAttribute("user", authentication.getPrincipal());
		response.sendRedirect("/");
	
		//user_id에 세션 저장 코드 추가

		UserVO user = (UserVO) authentication.getPrincipal();
		request.getSession().setAttribute("user_id", user.getUser_id());
		
	}
}