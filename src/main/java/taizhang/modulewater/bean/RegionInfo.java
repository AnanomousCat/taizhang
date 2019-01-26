package taizhang.modulewater.bean;

import org.springframework.stereotype.Component;

@Component
public class RegionInfo {

	private int id;
	private String name;
	private boolean isFarming;
	private boolean isIndustry;
	private boolean isShengTai;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isFarming() {
		return isFarming;
	}
	public void setFarming(boolean isFarming) {
		this.isFarming = isFarming;
	}
	public boolean isIndustry() {
		return isIndustry;
	}
	public void setIndustry(boolean isIndustry) {
		this.isIndustry = isIndustry;
	}
	public boolean isShengTai() {
		return isShengTai;
	}
	public void setShengTai(boolean isShengTai) {
		this.isShengTai = isShengTai;
	}
	
	
}
