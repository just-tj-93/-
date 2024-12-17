package kr.just.tj.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class TodoVO {
	
	private int todo_id;
	private int user_id;
	private String todo_title;
	private Date reg_date;
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private Date start_date;
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private Date end_date;
	private int days;
	private String country;
	private String city;
}
