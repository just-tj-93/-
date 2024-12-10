package kr.just.tj.vo;

import java.util.Date;

import lombok.Data;

@Data
public class DetailVO {
	
	private int detail_id;
	private int plan_id;
	private String whatday;
	private String spot;
	private Date visit_date;
	private String time_info;
	private String info;
	private int img_count;
	private Date reg_date;
	private String name;
	private Double latitude;
	private Double longitude;
	private String address;
}
