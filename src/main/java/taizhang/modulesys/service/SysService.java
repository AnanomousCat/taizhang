package taizhang.modulesys.service;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import taizhang.modulesys.bean.RoleInfo;
import taizhang.modulesys.bean.UserInfo;
import taizhang.modulesys.mapper.SysMapper;

public class SysService {
	
	SysMapper sysMapper;
	
	public int getUserId() {
		return sysMapper.getUserId();
	}
	public int getRoleId() {
		return sysMapper.getRoleId();
	}
	public List<UserInfo>  getUserList(Map<String, Object> map) {
		
		List<UserInfo> userList = sysMapper.getUserList(map.get("name").toString());
		return userList;
	}
	public List<RoleInfo>  getRoleList() {
		
		List<RoleInfo> roleList = sysMapper.getRoleList();
		return roleList;
	}
	
	public UserInfo getUserById(int userid) {
		return sysMapper.getUserById(userid);
	}
	public boolean deleteUserById(int userId) {
		sysMapper.deleteUserById(userId);
		return true;
	}
	public boolean deleteRoleById(int roleId) {
		sysMapper.deleteRoleById(roleId);
		return true;
	}
	
	public boolean addUser(UserInfo user) {
		sysMapper.addUser(user);
		return true;
	}
	public boolean addRole(RoleInfo roleInfo) {
		
		
		sysMapper.addRole(roleInfo);
		return true;
	}
	
	public boolean updateUser(UserInfo user) {
		sysMapper.updateUser(user);
		return true;
	}
	public boolean updateRole(RoleInfo role) {
		sysMapper.updateRole(role);
		return true;
	}
}
