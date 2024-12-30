package kr.just.tj.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimplePrivateKeySupplier;
import com.oracle.bmc.http.client.Options;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.ObjectSummary;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.ListObjectsResponse;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import kr.just.tj.service.DetailService;
import kr.just.tj.service.PlanService;
import kr.just.tj.service.TodoListService;
import kr.just.tj.service.TodoService;
import kr.just.tj.service.UserService;
import kr.just.tj.vo.CommVO;
import kr.just.tj.vo.DetailVO;
import kr.just.tj.vo.PagingVO;
import kr.just.tj.vo.PlanVO;
import kr.just.tj.vo.TodoListVO;
import kr.just.tj.vo.TodoVO;
import kr.just.tj.vo.UserVO;
import lombok.extern.slf4j.Slf4j;

@Controller
@Configuration
@Slf4j
public class HomeController {

	@Value("${oracle.oci.user}")
    private String user;
    
    @Value("${oracle.oci.tenancy}")
    private String tenancy;
    
    @Value("${oracle.oci.fingerprint}")
    private String fingerprint;
    
    @Value("${oracle.oci.namespace}")
    private String namespace;
    
    @Value("${oracle.oci.region}")
    private String region;
	
    @Value("${oracle.oci.bucket-name}")
    private String bucketName;
    
    @Value("${oracle.oci.privateKeyPath}")
    private String privateKeyPath;
    
    private ObjectStorageClient objectStorageClient;
    
    @PostConstruct
    public void objectStorageClient() throws Exception {
    	try {
    	AuthenticationDetailsProvider provider = SimpleAuthenticationDetailsProvider.builder()
	            .userId(user)
	            .tenantId(tenancy)
	            .fingerprint(fingerprint)
	            .privateKeySupplier(new SimplePrivateKeySupplier(privateKeyPath))
	            .region(Region.valueOf(region))
	            .build();
		
    	Options.shouldAutoCloseResponseInputStream(true);
    	this.objectStorageClient = ObjectStorageClient.builder().build(provider);
        System.out.println("ObjectStorageClient initialized successfully");
	    } catch (Exception e) {
	        System.err.println("Error initializing ObjectStorageClient: " + e.getMessage());
	        e.printStackTrace();
	    }
    }
   
	@Autowired
	private UserService userService;
	@Autowired
	private PlanService planService;
	@Autowired
	private DetailService detailService;
	@Autowired
	private TodoService todoService;
	@Autowired
	private TodoListService todoListService;

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
		model.addAttribute("commVO", commVO);
		model.addAttribute("field", field);
		model.addAttribute("search", search);
		model.addAttribute("newLine", "\n");
		model.addAttribute("br", "<br>");
		return "main";
	}
	@GetMapping("/planview")
	public String planView(HttpSession session,
			@RequestParam(required = false, name = "field") String field,
			@RequestParam(required = false, name = "search") String search,
			@RequestParam(required = false, name = "plan_id") int plan_id,
			@ModelAttribute CommVO commVO, Model model) {
		Integer user_id = (Integer) session.getAttribute("user_id");
		PagingVO<DetailVO> dv = detailService.selectDetailList(commVO.getCurrentPage(), commVO.getSizeOfPage(), commVO.getSizeOfBlock(), field, search);
		List<DetailVO> list = detailService.selectByPlanId(plan_id);
		PlanVO planVO = planService.selectPlanByPlanId(plan_id);
		boolean isLogin = isUserLoggedin();
		if (user_id != null) {
			List<TodoVO> tlist = todoService.selectTodoByUserId(user_id);
			model.addAttribute("tlist", tlist);
		}
		UserVO userVO = getUserInfo();
		model.addAttribute("dv", dv);
		model.addAttribute("list", list);
		model.addAttribute("isLogin", isLogin);
		model.addAttribute("uservo", userVO);
		model.addAttribute("planVO", planVO);
		model.addAttribute("field", field);
		model.addAttribute("search", search);
		model.addAttribute("newLine", "\n");
		model.addAttribute("br", "<br>");
		return "planview";
	}
	@GetMapping("/fetchRelatedDays")
    @ResponseBody
    public int fetchRelatedData(@RequestParam("todo_id") int todo_id) {
		TodoVO todoVO = todoService.selectTodoByTodoId(todo_id);
        return todoVO.getDays();
    }
	@GetMapping("/check-session")
    public ResponseEntity<Boolean> checkSession(HttpSession session) {
		Boolean isLoggedIn = session.getAttribute("user_id") != null;
        return ResponseEntity.ok(isLoggedIn);
    }
	
	@GetMapping("/myplanview")
	public String myPlanView(HttpSession session,RedirectAttributes redirectAttributes,
			@RequestParam(required = false, name = "plan_id") int plan_id,
			@ModelAttribute CommVO commVO, Model model) {
		Integer user_id = (Integer) session.getAttribute("user_id");
		if (user_id == null) {
			redirectAttributes.addFlashAttribute("message", "로그인 후 이용 할 수 있습니다");
			return "redirect:/login"; // 로그인 페이지로 리다이렉트
		}
		List<DetailVO> list = detailService.selectByPlanId(plan_id);
		PlanVO planVO = planService.selectPlanByPlanId(plan_id);
		boolean isLogin = isUserLoggedin();
		UserVO userVO = getUserInfo();
		ZoneId zoneId = ZoneId.of("Asia/Seoul");
		LocalDateTime localtime =  planVO.getStart_date().toInstant().atZone(zoneId).toLocalDateTime();
		model.addAttribute("list", list);
		model.addAttribute("isLogin", isLogin);
		model.addAttribute("uservo", userVO);
		model.addAttribute("planVO", planVO);
		model.addAttribute("localtime", localtime);
		model.addAttribute("newLine", "\n");
		model.addAttribute("br", "<br>");
		return "myplanview";
	}
	@GetMapping("/mytodoview")
	public String myTodoView(HttpSession session,RedirectAttributes redirectAttributes,
			@RequestParam(required = false, name = "todo_id") int todo_id,
			@ModelAttribute CommVO commVO, Model model) {
		Integer user_id = (Integer) session.getAttribute("user_id");
		if (user_id == null) {
			redirectAttributes.addFlashAttribute("message", "로그인 후 이용 할 수 있습니다");
			return "redirect:/login"; // 로그인 페이지로 리다이렉트
		}
		List<TodoListVO> list = todoListService.selectByTodoId(todo_id);
		TodoVO todoVO = todoService.selectTodoByTodoId(todo_id);
		boolean isLogin = isUserLoggedin();
		UserVO userVO = getUserInfo();
		ZoneId zoneId = ZoneId.of("Asia/Seoul");
		LocalDateTime localtime =  todoVO.getStart_date().toInstant().atZone(zoneId).toLocalDateTime();
		model.addAttribute("list", list);
		model.addAttribute("isLogin", isLogin);
		model.addAttribute("uservo", userVO);
		model.addAttribute("todoVO", todoVO);
		model.addAttribute("localtime", localtime);
		model.addAttribute("newLine", "\n");
		model.addAttribute("br", "<br>");
		return "mytodoview";
	}
	@PostMapping("/updateChecked")
    public String updateChecked(@RequestParam("todo_list_id") int todo_list_id,
    		@RequestParam("checked") boolean checked) {
        TodoListVO todoListVO = new TodoListVO();
        todoListVO.setTodo_list_id(todo_list_id);
        todoListVO.setChecked(checked ? "Y" : "N");
        todoListService.checked(todoListVO);
        return "redirect:/";
    }
	
	@GetMapping("/detailAdd")
	public String detailAddGet() {
		return "redirect:/myplan";
	}
	@PostMapping("/detailAdd")
	public String detailAddPost(@RequestParam(name = "plan_id") int plan_id,
			@RequestParam(name = "file") MultipartFile[] files,
			@ModelAttribute DetailVO detailVO, Model model
			) {
		if(!files[0].isEmpty()) {
			detailVO.setImg_count(files.length);
		}else {
			detailVO.setImg_count(0);
		}
		detailService.insert(detailVO);
		log.info("저장 :"  + detailVO);
		long maxFileSize = 5 * 1024 * 1024; // 5MB
		
		try {
			if (files != null && files.length > 0 && !files[0].isEmpty()) {
	            int fileIndex = 1;
	            for (MultipartFile file : files) {
	            	if (file.getSize() > maxFileSize) {
	                    log.info("5MB 이하의 파일만 등록 가능합니다");
	                    return "redirect:/myplanview?plan_id=" + detailVO.getPlan_id();
	                }
            		if(!file.isEmpty()) { 
            		//	String originalFilename = file.getOriginalFilename();
            		//	String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            			String fileName = String.format("detail-%d-%d%s", detailVO.getDetail_id(), fileIndex, ".jpg");
			            byte[] fileBytes = file.getBytes();
			            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(fileBytes);
			            Options.shouldAutoCloseResponseInputStream(true);
			            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			                    .namespaceName(namespace)
			                    .bucketName(bucketName)
			                    .objectName(fileName)
			                    .putObjectBody(byteArrayInputStream)
			                    .contentLength(file.getSize())
			                    .build();
			
			            objectStorageClient.putObject(putObjectRequest);
			            log.info("버켓 업로드 성공 " + fileName);
			            fileIndex++;
            		}
            	}
            }
        } catch (IOException e) {
        	log.info("Failed to upload file: " + e.getMessage());
        }
		return "redirect:/myplanview?plan_id=" + detailVO.getPlan_id();
	}
	@GetMapping("/todoListAdd")
	public String todoListAddGet() {
		return "redirect:/todo";
	}
	@PostMapping("/todoListAdd")
	public String todoListAddPost(@RequestParam(name = "todo_id") int todo_id,
			@ModelAttribute TodoListVO todoListVO, Model model
			) {
		todoListService.insert(todoListVO);
		log.info("저장 :"  + todoListVO);
		return "redirect:/mytodoview?todo_id=" + todoListVO.getTodo_id();
	}
	@PostMapping("/deleteTodoList")
	public String deleteTodo(@RequestParam(name = "todo_list_id") int todo_list_id,
			@RequestParam(name = "todo_id") int todo_id) {
		todoListService.deleteTodoList(todo_list_id);
		return "redirect:/mytodoview?todo_id=" + todo_id;
	}
	
	@GetMapping("/deleteDetail")
	public String detailDeleteGet() {
		return "redirect:/myplan";
	}
	@PostMapping("/deleteDetail")
	public String detailDeletePost(@RequestParam(name = "detail_id") int detail_id,
			@RequestParam(name = "plan_id") int plan_id, @ModelAttribute DetailVO detailVO) {
		detailService.deleteDetail(detail_id);
		
		ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
				.namespaceName(namespace)
	            .bucketName(bucketName)
	            .prefix("detail-" + detail_id + "-")  // 파일명 prefix로 필터링
	            .build();
		
		ListObjectsResponse response = objectStorageClient.listObjects(listObjectsRequest);
		List<ObjectSummary> objects = response.getListObjects().getObjects();
		
		if (objects != null) {
			for (ObjectSummary object : objects) {
				DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
		                .namespaceName(namespace)
		                .bucketName(bucketName)
		                .objectName(object.getName())
		                .build();
				
				try {
		        objectStorageClient.deleteObject(deleteObjectRequest);
		        log.info("버킷 내 이미지 삭제 성공 " + object.getName());
				} catch (Exception e) {
                    log.error("파일 삭제 실패: " + object.getName() + ", 에러: " + e.getMessage());
                }
			}
		}
		return "redirect:/myplanview?plan_id=" + plan_id;
	}
	
	@GetMapping("/myplan")
	public String my(HttpSession session,RedirectAttributes redirectAttributes,
			Model model) {
		// 사용자 로그인 여부 확인
			Integer user_id = (Integer) session.getAttribute("user_id");
			if (user_id == null) {
				redirectAttributes.addFlashAttribute("message", "로그인 후 이용 할 수 있습니다");
				return "redirect:/login"; // 로그인 페이지로 리다이렉트
			}
		List<PlanVO> plist = planService.selectPlanByUserId(user_id);
		model.addAttribute("plist", plist);
		return "myplan";
	}
	@GetMapping("/planform")
	public String form(HttpSession session,RedirectAttributes redirectAttributes,
			Model model, @ModelAttribute UserVO userVO) {
		Integer user_id = (Integer) session.getAttribute("user_id");
		if (user_id == null) {
			redirectAttributes.addFlashAttribute("message", "로그인 후 이용 할 수 있습니다");
			return "redirect:/login"; // 로그인 페이지로 리다이렉트
		}
		model.addAttribute("user_id",userVO.getUser_id());
		return "planform";
	}
	
	@GetMapping("/formOk")
	public String formOkGet() {
		return "redirect:/myplan";
	}
	@PostMapping("/formOk")
	public String formOkPost(HttpSession session,RedirectAttributes redirectAttributes,
			@ModelAttribute PlanVO planVO) {
		Integer user_id = (Integer) session.getAttribute("user_id");
		if (user_id == null) {
			redirectAttributes.addFlashAttribute("message", "로그인 후 이용 할 수 있습니다");
			return "redirect:/login"; 
		}
		planVO.setUser_id(user_id);
		long diff = planVO.getEnd_date().getTime() - planVO.getStart_date().getTime();
		planVO.setDays((int)diff/(1000*60*60*24)+1);
		planService.insert(planVO);
		return "redirect:/myplan";
	}
	@GetMapping("/deleteOk")
	public String deleteOkGet() {
		return "redirect:/myplan";
	}
	@PostMapping("/deleteOk")
	public String deleteOkPost(@RequestParam(name = "plan_id") int plan_id) {
		planService.delete(plan_id);
		return "redirect:/myplan";
	}
	
	@GetMapping("/todo")
	public String todo(HttpSession session,RedirectAttributes redirectAttributes,
			Model model) {
		Integer user_id = (Integer) session.getAttribute("user_id");
		if (user_id == null) {
			redirectAttributes.addFlashAttribute("message", "로그인 후 이용 할 수 있습니다");
			return "redirect:/login"; 
		}
		List<TodoVO> tlist = todoService.selectTodoByUserId(user_id);
		model.addAttribute("tlist", tlist);
		return "todo";
	}
	@GetMapping("/todoForm")
	public String todoForm(HttpSession session,RedirectAttributes redirectAttributes,
			Model model, @ModelAttribute UserVO userVO) {
		Integer user_id = (Integer) session.getAttribute("user_id");
		if (user_id == null) {
			redirectAttributes.addFlashAttribute("message", "로그인 후 이용 할 수 있습니다");
			return "redirect:/login";
		}
		model.addAttribute("user_id",userVO.getUser_id());
		return "todoForm";
	}
	
	@GetMapping("/todoFormOk")
	public String todoFormOkGet() {
		return "redirect:/todo";
	}
	@PostMapping("/todoFormOk")
	public String todoFormOkPost(HttpSession session,RedirectAttributes redirectAttributes,
			@ModelAttribute TodoVO todoVO) {
		Integer user_id = (Integer) session.getAttribute("user_id");
		if (user_id == null) {
			redirectAttributes.addFlashAttribute("message", "로그인 후 이용 할 수 있습니다");
			return "redirect:/login"; // 로그인 페이지로 리다이렉트
		}
		todoVO.setUser_id(user_id);
		long diff = todoVO.getEnd_date().getTime() - todoVO.getStart_date().getTime();
		todoVO.setDays((int)diff/(1000*60*60*24)+1);
		todoService.insert(todoVO);
		return "redirect:/todo";
	}
	@GetMapping("/todoDeleteOk")
	public String todoDeleteOkGet() {
		return "redirect:/todo";
	}
	@PostMapping("/todoDeleteOk")
	public String todoDeleteOkPost(@RequestParam(name = "todo_id") int todo_id) {
		todoService.delete(todo_id);
		return "redirect:/todo";
	}
	@GetMapping("/addTodoOk")
	public String addTodoOkGet() {
		return "redirect:/";
	}
	@PostMapping("/addTodoOk")
	public String addTodoOkPost(HttpSession session,RedirectAttributes redirectAttributes,
			@RequestParam(name = "plan_id") int plan_id,
			@RequestParam(name = "detail_id") int detail_id,
			@RequestParam(name = "todo_id") int todo_id,
			@RequestParam(name = "whatday") int whatday) {
		Integer user_id = (Integer) session.getAttribute("user_id");
		if (user_id == null) {
			redirectAttributes.addFlashAttribute("message", "로그인 후 이용 할 수 있습니다");
			return "redirect:/login"; // 로그인 페이지로 리다이렉트
		}
		TodoListVO todoListVO = new TodoListVO();
		DetailVO detailVO = detailService.selectByDetailId(detail_id);
		todoListVO.setTodo_id(todo_id);
		todoListVO.setAddress(detailVO.getAddress());
		todoListVO.setInfo(detailVO.getInfo());
		todoListVO.setLatitude(detailVO.getLatitude());
		todoListVO.setLongitude(detailVO.getLongitude());
		todoListVO.setSpot(detailVO.getSpot());
		todoListVO.setTime_info(detailVO.getTime_info());
		todoListVO.setWhatday(whatday);
		todoListService.insert(todoListVO);
		log.info("TODO추가 성공 : "+todoListVO);
		return "redirect:/planview?plan_id="+ plan_id;
	}
}
