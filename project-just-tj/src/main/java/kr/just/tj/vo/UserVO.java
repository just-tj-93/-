package kr.just.tj.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;

@Data
public class UserVO implements UserDetails{
	
	//security에서 VO를 사용하려면 UserDetails인터페이스를 구현

	private static final long serialVersionUID = -256563770140122799L;
	
	private int user_id;
	private String user_name;
	private String password;
	private String user_role;
	private String email;
	@DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
	private Date reg_date;	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		//"ROLE_ADMIN, ROLE_USER" 형태의 스트링을 리스트로 변환하여 리턴하도록 구현
		
		List<GrantedAuthority> authorities = new ArrayList<>();
		String[] roles = user_role.split(",");
		if(roles!=null && roles.length>0) {
			for(String role : roles) {
				authorities.add(new SimpleGrantedAuthority(role.trim()));
			}
		}
		return authorities;
	}
	
	@Override
	public String getUsername() {
		return user_name;
	}
	@Override
	public String getPassword() {
		return password;
	}
	@Override
	public boolean isAccountNonExpired() {	//계정이 만료되지 않았는지 리턴한다(true:만료안됨)
		return true;
	}
	@Override
	public boolean isAccountNonLocked() {  //계정이 잠겨있는지 아닌지 리턴한다(true:잠기지 않음)
		return true;
	}
	@Override
	public boolean isCredentialsNonExpired() {  //비밀번호가 만료되지 않았는지 리턴한다(true:만료안됨)
		return true;
	}
	@Override
	public boolean isEnabled() {  //계정이 활성화 여부에 대해 리턴한다(true:활성화됨)
		return true;
	}
	
	//user_id 세션 정보 추가
	public Integer getId() {
		return user_id;
	}
}
