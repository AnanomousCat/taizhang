package taizhang.modulesys.bean;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class UserInfo {

	private int id;
	private String name;
	private String pswd;
	private String createTime;
	private String updateTime;
	private List<RoleInfo> roleList;
	
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
	public String getPswd() {
		return pswd;
	}
	public void setPswd(String pswd) {
		this.pswd = pswd;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public List<RoleInfo> getRoleList() {
		return roleList;
	}
	public void setRoleList(List<RoleInfo> roleList) {
		this.roleList = roleList;
	}
	
	
}
