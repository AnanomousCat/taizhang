package taizhang.modulewater.bean;

import java.util.Date;

import org.apache.commons.compress.utils.ChecksumCalculatingInputStream;

public class DataCheck {

	private int id;
	private int waterId;
	private String  checkUser;
	private Date checkTime;
	private int status;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getWaterId() {
		return waterId;
	}
	public void setWaterId(int waterId) {
		this.waterId = waterId;
	}
	public String getCheckUser() {
		return checkUser;
	}
	public void setCheckUser(String checkUser) {
		this.checkUser = checkUser;
	}
	public Date getCheckTime() {
		return checkTime;
	}
	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
