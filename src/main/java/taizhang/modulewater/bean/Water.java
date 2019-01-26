package taizhang.modulewater.bean;

import java.util.Date;

import org.springframework.stereotype.Component;

@Component
public class Water {
	
	private int id;
	private int regionId;
	private String regionName;
	private Date createTime;
	private Date waterTime;
	private double waterValue;
	private int checkStatus;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getWaterTime() {
		return waterTime;
	}
	public void setWaterTime(Date waterTime) {
		this.waterTime = waterTime;
	}
	public int getRegionId() {
		return regionId;
	}
	public void setRegionId(int regionId) {
		this.regionId = regionId;
	}
	
	public String getRegionName() {
		return regionName;
	}
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	public double getWaterValue() {
		return waterValue;
	}
	public void setWaterValue(double waterValue) {
		this.waterValue = waterValue;
	}
	public int getCheckStatus() {
		return checkStatus;
	}
	public void setCheckStatus(int checkStatus) {
		this.checkStatus = checkStatus;
	}
	
	
	
}
