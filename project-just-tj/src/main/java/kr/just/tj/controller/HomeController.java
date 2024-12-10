package kr.just.tj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import kr.just.tj.service.PlanService;
import kr.just.tj.service.UserService;
import kr.just.tj.vo.CommVO;
import kr.just.tj.vo.PagingVO;
import kr.just.tj.vo.PlanVO;
import kr.just.tj.vo.UserVO;
import lombok.extern.slf4j.Slf4j;

@Controller
@Configuration
@Slf4j
public class HomeController {

	@Autowired
	private UserService userService;
	@Autowired
	private PlanService planService;

	// 로그인 여부 확인
	public boolean isUserLoggedin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.isAuthenticated()
				&& !(authentication instanceof AnonymousAuthenticationToken);
	}

	// 회원 정보 가져오기
	public UserVO getUserInfo() {
		UserVO userVO = new UserVO();
		if (isUserLoggedin()) {
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			userVO = userService.selectByUsername(username);

		}
		return userVO;
	}

	@GetMapping("/")
	public String index(@RequestParam(required = false, name = "field") String field,
			@RequestParam(required = false, name = "search") String search,
			@ModelAttribute CommVO commVO,
			Model model) {
		PagingVO<PlanVO> pv = planService.selectPlanList(commVO.getCurrentPage(), commVO.getSizeOfPage(), commVO.getSizeOfBlock(), field, search);
		boolean isLogin = isUserLoggedin();
		UserVO userVO = getUserInfo();
		model.addAttribute("pv", pv);
		model.addAttribute("isLogin", isLogin);
		model.addAttribute("uservo", userVO);
		model.addAttribute("field", field);
		model.addAttribute("search", search);
		model.addAttribute("newLine", "\n");
		model.addAttribute("br", "<br>");

		return "main";
	}
	@GetMapping("/planview")
	public String planView(Model model) {
		
		return "plan_view";
	}
	@GetMapping("/country")
	public String country(
			Model model) {
		model.addAttribute("newLine", "\n");
		model.addAttribute("br", "<br>");
		
		return "country";
	}

	
}
