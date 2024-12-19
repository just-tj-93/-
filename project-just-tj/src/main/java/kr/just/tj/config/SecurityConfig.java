package kr.just.tj.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import kr.just.tj.service.LoginSuccessHandler;
import kr.just.tj.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity 
@Slf4j
public class SecurityConfig{
	
	@Autowired
	private UserService userService;
	
	// SecurityFilterChain객체 등록 : 자원별 접근 권한을 설정한다.
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		// csrf 토근을 사용하지 않겠다.
		http.csrf(AbstractHttpConfigurer::disable);
		// 인증 설정
		http.authorizeHttpRequests((authorize)->{
			authorize
			.requestMatchers("/","/*","/join","/login").permitAll()
			.requestMatchers("/auth/send-verification-code", "/auth/verify-code").permitAll()
            .requestMatchers("/css/**").permitAll()
            //.requestMatchers("/error/**").permitAll()
            .requestMatchers("/myplan","/myplanview","/mytodoview","/form","/todo","/todoForm").hasAnyRole("USER", "ADMIN")
			.anyRequest().authenticated();  // 그 외 모든 요청은 인증 필요
		});
		// 사용자가 만든 로그인 폼을 사용하겠다.
		http.formLogin((form) -> {
			
	        form.loginPage("/login").permitAll()
	            .usernameParameter("user_name")
	            .passwordParameter("password")
	            .defaultSuccessUrl("/")
	            .failureUrl("/login?error=true")
	        	.successHandler(new LoginSuccessHandler());
	    });

		http.logout((logout)->{
			// 로그아웃에 누구나 접근 가능
			 logout.permitAll()
			// 로그아웃 후 이동할 주소
			.logoutSuccessUrl("/")
			// 세션정보를 지울것인지 지정
			.invalidateHttpSession(true);
		});
		
		// 완성해서 객체 등록
		return http.build();
	}

	@Autowired
	public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
    	//회원 정보를 가져와 인증 영역에 정보를 저장할 서비스를 등록해 준다.
		auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }
	
	// 암호화 객체 등록 
	@Bean("passwordEncoder")
	BCryptPasswordEncoder getBCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
		
}