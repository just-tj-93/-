package kr.just.tj.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.just.tj.service.UserService;
import kr.just.tj.vo.UserVO;
import lombok.extern.slf4j.Slf4j;


@Controller
@Configuration
@Slf4j
public class UserController {
	
	@Autowired
	private UserService userService;

	// 회원가입 페이지
    @GetMapping("/join")
    public String joinForm(Model model) {
        model.addAttribute("user", new UserVO()); // 빈 UserVO 객체를 모델에 추가
        return "join";
    }

    @PostMapping("/join")
    @ResponseBody // JSON 형식으로 응답
    public Map<String, String> joinUser(@ModelAttribute UserVO user) {
        Map<String, String> response = new HashMap<>();

        // 중복 확인
        int countUsername = userService.selectCountByUsername(user.getUsername());

        if (countUsername > 0) {
            response.put("status", "fail");
            response.put("message", "이미 사용 중인 아이디입니다.");
            return response;
        }

        // 회원가입 성공
        userService.insert(user);
        response.put("status", "success");
        response.put("message", "회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.");
        return response;
    }
    
    //아이디 중복확인
    @GetMapping(value = "/test/usernameCheck", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String usernameCheck(@RequestParam("user_name") String user_name) {
        int count = userService.selectCountByUsername(user_name);
        log.info("아이디 중복 확인 요청: {}, 결과: {}", user_name, count);
        return String.valueOf(count);
    }
    //이메일 중복확인
    @GetMapping(value = "/test/emailCheck", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String emailCheck(@RequestParam("email") String email) {
    	int count = userService.selectCountByEmail(email);
    	log.info("이메일 중복 확인 요청: {}, 결과: {}", email, count);
    	return String.valueOf(count);
    }
    //로그인페이지
    @GetMapping("/login")
    public String login() {
        return "login"; 
    }
    
    //아이디찾기페이지
    @GetMapping("/find-username")
    public String findUsernameForm() {
    	return "find-username";
    }
    
    //아이디찾기처리
    @PostMapping("/find-username")
    public String findUsername(@RequestParam("email") String email, Model model) {
    	log.info("아이디찾기 요청: 이메일 = {}",  email);
    	UserVO userVO = userService.selectByEmail(email);
    	
    	if(userVO.getUser_name() != null) {
    		log.info("아이디찾기 성공: {}", userVO.getUser_name());
    		model.addAttribute("user_name", userVO.getUser_name());
    		return "find-username-result";
    	} else {
    		log.warn("아이디 찾기 실패:  이메일 = {}", email);
    		model.addAttribute("error", "입력하신 정보와 일치하는 아이디를 찾을 수 없습니다.");
    		return "find-username";
    	}
    }
    
    //비밀번호찾기페이지
    @GetMapping("/find-password")
    public String findPasswordForm() {
    	return "find-password";
    }
    
    //비밀번호찾기(사용자조회)
    @PostMapping("/find-password")
    public String findPassword(@RequestParam("user_name") String user_name,
    							@RequestParam("email") String email,
    							Model model) {
    	UserVO user = userService.selectByUsername(user_name);
    	
    	if (user != null) {
    		model.addAttribute("user_name", user_name);
    		return "reset-password";
    	} else {
    		model.addAttribute("error", "입력한 정보와 일치하는 계정이 없습니다");
    		return "find-password";
    	}
    }
    
    //비밀번호찾기(새비밀번호변경)
    @PostMapping("/reset-password")
    public String resetPassword(@ModelAttribute UserVO userVO,
    							Model model) {
    	userService.updateUser(userVO);
    	model.addAttribute("message", "비밀번호가 변경되었습니다.");
    	return "login";
    }
}
