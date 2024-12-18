package kr.just.tj.vo;

import java.util.Date;

import lombok.Data;

@Data
public class TodoListVO {
	
	private int todo_list_id;
	private int todo_id;
	private int whatday;
	private Date reg_date;
	private String spot;
	private String time_info;
	private String info;
	private Double latitude;
	private Double longitude;
	private String address;
	private String checked;
	
}
