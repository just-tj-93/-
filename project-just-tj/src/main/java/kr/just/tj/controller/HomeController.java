package kr.just.tj.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import kr.just.tj.service.DetailService;
import kr.just.tj.service.PlanService;
import kr.just.tj.service.UserService;
import kr.just.tj.vo.CommVO;
import kr.just.tj.vo.DetailVO;
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
	@Autowired
	private DetailService detailService;

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
			String user_name = SecurityContextHolder.getContext().getAuthentication().getName();
			userVO = userService.selectByUsername(user_name);

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
	public String planView(@RequestParam(required = false, name = "field") String field,
			@RequestParam(required = false, name = "search") String search,
			@RequestParam(required = false, name = "plan_id") int plan_id,
			@ModelAttribute CommVO commVO, Model model) {
		PagingVO<DetailVO> dv = detailService.selectDetailList(commVO.getCurrentPage(), commVO.getSizeOfPage(), commVO.getSizeOfBlock(), field, search);
		List<DetailVO> list = detailService.selectByPlanId(plan_id);
		boolean isLogin = isUserLoggedin();
		UserVO userVO = getUserInfo();
		model.addAttribute("dv", dv);
		model.addAttribute("list", list);
		model.addAttribute("isLogin", isLogin);
		model.addAttribute("uservo", userVO);
		model.addAttribute("field", field);
		model.addAttribute("search", search);
		model.addAttribute("newLine", "\n");
		model.addAttribute("br", "<br>");
		return "planview";
	}
	@GetMapping("/country")
	public String country(
			Model model) {
		model.addAttribute("newLine", "\n");
		model.addAttribute("br", "<br>");
		
		return "country";
	}
	@GetMapping("/my")
	public String my(HttpSession session,RedirectAttributes redirectAttributes,
			Model model) {
		// 사용자 로그인 여부 확인
			Integer user_id = (Integer) session.getAttribute("user_id");
			System.out.println("세션 아이디:" + user_id);
			if (user_id == null) {
				redirectAttributes.addFlashAttribute("message", "로그인 후 이용 할 수 있습니다");
				return "redirect:/login"; // 로그인 페이지로 리다이렉트
			}
		List<PlanVO> plist = planService.selectPlanByUserId(user_id);
		model.addAttribute("plist", plist);
		return "myPage";
	}
	@GetMapping("/form")
	public String form(HttpSession session,RedirectAttributes redirectAttributes,
			Model model, @ModelAttribute UserVO userVO) {
		Integer user_id = (Integer) session.getAttribute("user_id");
		System.out.println("세션 아이디:" + user_id);
		if (user_id == null) {
			redirectAttributes.addFlashAttribute("message", "로그인 후 이용 할 수 있습니다");
			return "redirect:/login"; // 로그인 페이지로 리다이렉트
		}
		model.addAttribute("user_id",userVO.getUser_id());
		return "form";
	}
	@GetMapping("/formOk")
	public String formOkGet() {
		return "redirect:/my";
	}
	@PostMapping("/formOk")
	public String formOkPost(HttpSession session,RedirectAttributes redirectAttributes,
			@ModelAttribute PlanVO planVO) {
		Integer user_id = (Integer) session.getAttribute("user_id");
		planVO.setUser_id(user_id);
		planService.insert(planVO);
		return "redirect:/my";
	}
}
