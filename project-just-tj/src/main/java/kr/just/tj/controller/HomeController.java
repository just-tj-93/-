package kr.just.tj.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
		PlanVO planVO = planService.selectPlanByPlanId(plan_id);
		boolean isLogin = isUserLoggedin();
		UserVO userVO = getUserInfo();
		
		model.addAttribute("dv", dv);
		model.addAttribute("currentDay", 2);
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
	@GetMapping("/myplanview")
	public String myPlanView(@RequestParam(required = false, name = "field") String field,
			@RequestParam(required = false, name = "search") String search,
			@RequestParam(required = false, name = "plan_id") int plan_id,
			@ModelAttribute CommVO commVO, Model model) {
		PagingVO<DetailVO> dv = detailService.selectDetailList(commVO.getCurrentPage(), commVO.getSizeOfPage(), commVO.getSizeOfBlock(), field, search);
		List<DetailVO> list = detailService.selectByPlanId(plan_id);
		PlanVO planVO = planService.selectPlanByPlanId(plan_id);
		boolean isLogin = isUserLoggedin();
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
		return "myplanview";
	}
	@GetMapping("/detailAdd")
	public String detailAddGet() {
		return "redirect:/my";
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
	                    log.info("File size should not exceed 5MB");
	                    return "redirect:/myplanview?plan_id=" + detailVO.getPlan_id();
	                }
            		if(!file.isEmpty()) { 
            			String originalFilename = file.getOriginalFilename();
            			String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            			String fileName = String.format("detail-%d-%d%s", detailVO.getDetail_id(), fileIndex, extension);
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
	
	@GetMapping("/deleteDetail")
	public String detailDeleteGet() {
		return "redirect:/my";
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
	@GetMapping("/deleteOk")
	public String deleteOkGet() {
		return "redirect:/my";
	}
	@PostMapping("/deleteOk")
	public String deleteOkPost(@RequestParam(name = "plan_id") int plan_id) {
		planService.delete(plan_id);
		return "redirect:/my";
	}
}
