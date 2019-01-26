package taizhang.modulesys.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import taizhang.modulesys.bean.PermInfo;
import taizhang.modulesys.bean.RoleInfo;
import taizhang.modulesys.bean.UserInfo;
import taizhang.modulesys.mapper.SysMapper;

@Controller
@RequestMapping("sys")
public class SysController {

	@Autowired
	SysMapper sysMapper;

	@RequestMapping("isLogin")
	@ResponseBody

	public Object isLogin(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		if (session.getAttribute("userInfo") != null) {

			UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
			ArrayList<String> permList = sysMapper.getPermissionByUserid(userInfo.getId());
			resultMap.put("result", "success");
			resultMap.put("userInfo", userInfo);
			resultMap.put("permList", permList);
		} else {
			resultMap.put("result", "error");
		}

		return resultMap;
	}

	@RequestMapping("login")
	@ResponseBody
	public Object login(@RequestBody Map<String, Object> map, HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<UserInfo> userList = sysMapper.getUserByName(map.get("name").toString());
		if (userList.size() > 0) {
			UserInfo userInfo = userList.get(0);
			if (userInfo.getPswd().equals(map.get("pswd").toString())) {
				HttpSession session = request.getSession();
				session.setAttribute("userInfo", userInfo);
				resultMap.put("result", "success");
			} else {
				resultMap.put("result", "密码错误");
			}

			return resultMap;
		} else {
			resultMap.put("result", "用户不存在");
			return resultMap;
		}
	}

	@RequestMapping("loginOut")
	@ResponseBody
	public Object loginOut(@RequestBody Map<String, Object> map, HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.removeAttribute("userInfo");

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("result", "success");
		return resultMap;
	}

	/**
	 * 查用户列表 关联角色
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping("userList")
	@ResponseBody
	public Object getUserList(@RequestBody Map<String, Object> map) {

		System.out.println(map);
		ArrayList<UserInfo> userList = sysMapper.getUserList(map.get("name").toString());
		return userList;
	}

	/**
	 * 查角色列表 关联权限
	 * 
	 * @param map
	 * @return
	 */
	@RequestMapping("roleList")
	@ResponseBody
	public Object getRoleList(@RequestBody Map<String, Object> map) {

		System.out.println(map);
		Map<String, Object> resultMap = new HashMap<String, Object>();

		List<RoleInfo> roleList = sysMapper.getRoleList();
		resultMap.put("roleList", roleList);

		List<PermInfo> permList = sysMapper.getPermList();
		resultMap.put("permList", permList);

		return resultMap;
	}

	/**
	 * 编辑用户
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping("getUserById")
	@ResponseBody
	public Object getUserById(@RequestParam(value = "userid") int userId) {
		System.out.println(userId);
		Map<String, Object> resultMap = new HashMap<String, Object>();

		UserInfo userInfo = sysMapper.getUserById(userId);
		resultMap.put("user", userInfo);

		List<RoleInfo> roleList = sysMapper.getRoleList();
		resultMap.put("roleList", roleList);

		return resultMap;
	}

	/**
	 * 删除用户 删除用户-角色关系
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping("deleteUser")
	@ResponseBody
	public Object deleteUser(@RequestParam(value = "id") int userId) {
		System.out.println(userId);
		sysMapper.deleteUserById(userId);
		sysMapper.deleteUserRoleByUserid(userId);
		return true;
	}

	/**
	 * 删除角色 查看是否关联用户
	 * 
	 * @param roleId
	 * @return
	 */
	@RequestMapping("deleteRole")
	@ResponseBody
	public Object deleteRole(@RequestParam(value = "id") int roleId) {
		System.out.println(roleId);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		int cn = sysMapper.getUserCountByRoleid(roleId);
		if (cn > 0) {
			resultMap.put("result", "fail");
		} else {
			sysMapper.deleteRoleById(roleId);
			sysMapper.deletePermissionByRoleid(roleId);
			resultMap.put("result", "success");
		}

		return resultMap;
	}

	@RequestMapping("addUser")
	@ResponseBody
	public Object addUser(@RequestBody Map<String, Object> map) {

		System.out.println(map);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<UserInfo> userList = sysMapper.getUserByName(map.get("name").toString());
		if (userList.size() > 0) {
			resultMap.put("result", "用户名已存在");
			return resultMap;
		}
		// 添加用户
		UserInfo userInfo = new UserInfo();
		userInfo.setId(sysMapper.getUserId());
		userInfo.setName(map.get("name").toString());
		userInfo.setPswd(map.get("pswd").toString());

		sysMapper.addUser(userInfo);
		// 添加用户角色
		ArrayList<String> roleidList = (ArrayList<String>) map.get("roleidList");
		for (int i = 0; i < roleidList.size(); i++) {
			sysMapper.addUserRole(userInfo.getId(), Integer.parseInt(roleidList.get(i)));
		}
		resultMap.put("result", "success");
		return resultMap;
	}

	@RequestMapping("addRole")
	@ResponseBody
	public Object addRole(@RequestBody Map<String, Object> map) {
		System.out.println(map);
		// 添加角色
		RoleInfo roleInfo = new RoleInfo();
		roleInfo.setId(sysMapper.getRoleId());
		roleInfo.setName(map.get("name").toString());
		sysMapper.addRole(roleInfo);
		// 添加角色权限关系
		ArrayList<String> permidList = (ArrayList<String>) map.get("permidList");
		for (int i = 0; i < permidList.size(); i++) {
			sysMapper.addRolePermission(Integer.parseInt(permidList.get(i)), roleInfo.getId());
		}
		return true;
	}

	@RequestMapping("updateUser")
	@ResponseBody
	public Object updateUser(@RequestBody Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		System.out.println(map);
		UserInfo userInfo = new UserInfo();
		userInfo.setId(Integer.parseInt(map.get("id").toString()));
		userInfo.setName(map.get("name").toString());
		userInfo.setPswd(map.get("pswd").toString());

		List<UserInfo> userList = sysMapper.getUserByName(map.get("name").toString());
		if (userList.size() > 0) {
			resultMap.put("result", "用户名已存在");
			return resultMap;
		}

		sysMapper.updateUser(userInfo);

		// 修改用户角色 即先删除再新增

		sysMapper.deleteUserRoleByUserid(userInfo.getId());
		ArrayList<String> roleidList = (ArrayList<String>) map.get("roleidList");
		for (int i = 0; i < roleidList.size(); i++) {
			sysMapper.addUserRole(userInfo.getId(), Integer.parseInt(roleidList.get(i)));
		}
		resultMap.put("result", "success");
		return resultMap;
	}

	@RequestMapping("updateRole")
	@ResponseBody
	public Object updateRole(@RequestBody Map<String, Object> map) {
		System.out.println(map);
		RoleInfo roleInfo = new RoleInfo();
		roleInfo.setId(Integer.parseInt(map.get("id").toString()));
		roleInfo.setName(map.get("name").toString());
		sysMapper.updateRole(roleInfo);

		// 修改角色权限关系 先删除再新增
		sysMapper.deletePermissionByRoleid(roleInfo.getId());
		ArrayList<String> permidList = (ArrayList<String>) map.get("permidList");
		for (int i = 0; i < permidList.size(); i++) {
			sysMapper.addRolePermission(Integer.parseInt(permidList.get(i)), roleInfo.getId());
		}
		return true;
	}

}
