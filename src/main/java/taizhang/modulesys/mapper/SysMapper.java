package taizhang.modulesys.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import taizhang.modulesys.bean.PermInfo;
import taizhang.modulesys.bean.RoleInfo;
import taizhang.modulesys.bean.UserInfo;

@Component
public interface SysMapper {

	//用户管理
	int getUserId();
	
	ArrayList<UserInfo> getUserList(@Param("name")String name);
	
	UserInfo getUserById(int userid);
	 
	void deleteUserById(int userId);
	
	void addUser( UserInfo user);
	
	void updateUser(UserInfo user);
	
	//角色权限
	List<RoleInfo> getRoleList();
	
	int getRoleId();
	
	void addRole(RoleInfo roleInfo);
	
	void deleteRoleById(@Param("roleid")int roleId);
	
	void updateRole(RoleInfo roleInfo);
	
//    List<PermInfo> getPermissionByRoleId(@Param("roleid")int roleId);
    
    //用户——角色
    void addUserRole(@Param("userid")int userid,@Param("roleid")int roleid);
    
    int getUserCountByRoleid(int roleid);
    
    void deleteUserRoleByUserid(@Param("userid")int userid);
    
    //角色——权限
    void addRolePermission(@Param("permid")int permid,@Param("roleid")int roleid);
    
    void deletePermissionByRoleid(@Param("roleid")int roleid);
    
    List<PermInfo> getPermList();
    
    List<UserInfo> getUserByName(@Param("userName")String userName);
    
    ArrayList<String> getPermissionByUserid(@Param("userid")int userid);
}
