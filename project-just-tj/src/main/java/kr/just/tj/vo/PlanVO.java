package kr.just.tj.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class PlanVO {
	
	private int plan_id;
	private String title;
	private String subtitle;
	private String country;
	private Date reg_date;
	private int likeit;
	private String city;
	private int user_id;
	private int days;
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private Date start_date;
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private Date end_date;
}
