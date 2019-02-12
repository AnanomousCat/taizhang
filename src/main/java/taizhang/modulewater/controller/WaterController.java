package taizhang.modulewater.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import taizhang.modulesys.bean.UserInfo;
import taizhang.modulewater.bean.DataCheck;
import taizhang.modulewater.bean.RegionInfo;
import taizhang.modulewater.bean.Water;
import taizhang.modulewater.mapper.WaterMapper;

@Controller
@RequestMapping("water")
public class WaterController {

	@Autowired
	WaterMapper waterMapper;

	@RequestMapping("addWater")
	@ResponseBody
	public Object addWater(@RequestBody Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Water water = new Water();
		water.setCheckStatus(0);
		water.setRegionId(Integer.parseInt(map.get("regionId").toString()));
		String waterTime = map.get("waterTime").toString();
		int year = Integer.parseInt(waterTime.split("-")[0]);
		int month = Integer.parseInt(waterTime.split("-")[1]);
		water.setWaterTime(new Date(year - 1900, month - 1, 1));
		water.setWaterValue(Double.parseDouble(map.get("waterValue").toString()));
		water.setCreateTime(new Date());
		waterMapper.addWaterData(water);
		resultMap.put("result", "success");
		return resultMap;
	}

	@RequestMapping("updateWater")
	@ResponseBody
	public Object updateWaterValue(@RequestBody Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		double waterValue = Double.parseDouble(map.get("waterValue").toString());
		int id = Integer.parseInt(map.get("id").toString());
		waterMapper.updateWaterData(waterValue, id);
		resultMap.put("result", "success");
		return resultMap;
	}

	@RequestMapping("deleteWater")
	@ResponseBody
	public Object deleteWater(@RequestBody Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String idList = map.get("idList").toString();
		idList = idList.substring(0, idList.length() - 1);
		waterMapper.deleteWaterDataById(idList);
		resultMap.put("result", "success");
		return resultMap;
	}

	@RequestMapping("updateStatus")
	@ResponseBody
	public Object updateCheckStatus(@RequestBody Map<String, Object> map, HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		UserInfo userInfo = (UserInfo) session.getAttribute("userInfo");
		String username = userInfo.getName();
		String idList = map.get("idList").toString();
		idList = idList.substring(0, idList.length() - 1);
		int status = Integer.parseInt(map.get("checkStatus").toString());
		waterMapper.updateCheckStatus(status, idList);

		String[] idArr = idList.split(",");
		for (int i = 0; i < idArr.length; i++) {
			DataCheck check = new DataCheck();
			check.setStatus(status);
			check.setWaterId(Integer.parseInt(idArr[i]));
			check.setCheckUser(username);
			waterMapper.addCheckData(check);
		}
		resultMap.put("result", "success");
		return resultMap;
	}

	@RequestMapping("statusDic")
	@ResponseBody
	public Object getStatusDic(@RequestBody Map<String, Object> map) {

		return waterMapper.getCheckStatusDic();
	}

	@RequestMapping("waterList")
	@ResponseBody
	public Object getWaterList(@RequestBody Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String createTime = map.containsKey("createTime")?map.get("createTime").toString():"";
		String waterTime =  map.containsKey("waterTime")?map.get("waterTime").toString():"";
		String regionIdList ="";
		ArrayList<String> regionIdArr =map.containsKey("regionIdList")? (ArrayList<String>) map.get("regionIdList"):null;
		if (regionIdArr!=null && regionIdArr.size()>0) {
			for (int i = 0; i < regionIdArr.size(); i++) {
				regionIdList += regionIdArr.get(i) + ",";
			}
			regionIdList =regionIdList.substring(0, regionIdList.length()-1);
		}
		int checkStatus = Integer.parseInt(map.get("checkStatus").toString());
		 List<Water> waterList = waterMapper.getWaterList(checkStatus,regionIdList,createTime,waterTime);
		 resultMap.put("waterList", waterList);
		return resultMap;
	}

	@RequestMapping("waterStatistics")
	@ResponseBody
	public Object waterStatistics(@RequestBody Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		String startTime = map.get("startTime").toString();
		String endTime = map.get("endTime").toString();
		String timeType = map.get("timeType").toString();
		String regionStr = map.get("regionIdList").toString();

		String timeFormat = "";
		if ("year".equals(timeType)) {
			timeFormat = "YYYY";
		} else {
			timeFormat = "YYYY-MM";
		}

		String regionSql = " ";
		String reNameSql = "";
		List<RegionInfo> regionList = waterMapper.getRegionList();
		if (!regionStr.equals("all")) {
			String idStr = "";
			ArrayList<String> regionIdList = (ArrayList<String>) map.get("regionIdList");
			for (int i = 0; i < regionIdList.size(); i++) {
				idStr += regionIdList.get(i) + ",";
			}
			regionSql += " and re.id in(" + idStr.substring(0, idStr.length() - 1) + ")";
			String regionName = "";
			for (int i = 0; i < regionList.size(); i++) {
				RegionInfo regionInfo = regionList.get(i);
				String id = "" + regionInfo.getId();
				
				if (regionIdList.contains(id)) {
					reNameSql += " sum(case when re.name = '" + regionInfo.getName()
							+ "' then w.watervalue else 0 end) as \"" + regionInfo.getName() + "\",";
					regionName +=  "'"+regionInfo.getName()+"',";
				}
			}
			regionName = regionName.substring(0, regionName.length() - 1);
			reNameSql += "sum(case when re.name in("+regionName+") then w.watervalue else 0 end)  \"合计\"";
		} else {
			for (int i = 0; i < regionList.size(); i++) {
				RegionInfo regionInfo = regionList.get(i);
				reNameSql += " sum(case when re.name = '" + regionInfo.getName()
						+ "' then w.watervalue else 0 end) as \"" + regionInfo.getName() + "\",";
			}
			reNameSql +=" sum(w.watervalue)  \"合计\"";
		}

		// 1.列表各地区 农业非农业 农业工业
		// 列表1 地区
		String tableSql1 = "select to_char(w.watertime, '" + timeFormat + "') as \"时间\",\r\n" + reNameSql
//				+ " sum(case when re.name = '平阴灌溉' then w.watervalue else 0 end) as \"平阴灌溉\",  \r\n"
//				+ "  sum(case when re.name = '肥城灌溉' then  w.watervalue else 0 end) as \"肥城灌溉\",  \r\n"
//				+ " sum(case when re.name = '沿黄村庄灌溉'   then w.watervalue else 0 end) as \"沿黄村庄灌溉\",   \r\n"
//				+ " sum(case when re.name = '玫瑰湖湿地用水'   then w.watervalue else 0 end) as \"玫瑰湖湿地用水\",   \r\n"
//				+ " sum(case when re.name = '平阴用水'   then w.watervalue else 0 end) as \"平阴用水\",\r\n"
//				+ " sum(case when re.name = '肥城工业'   then w.watervalue else 0 end) as \"肥城工业\",\r\n"
//				+ " sum(case when re.name = '琦泉热电'   then w.watervalue else 0 end) as \"琦泉热电\",\r\n"
//				+ " sum(case when re.name = '济南用水'   then w.watervalue else 0 end) as \"济南用水\",\r\n"
//				+ " sum(case when re.name = '胶东用水'   then w.watervalue else 0 end) as \"胶东用水\"\r\n"
				+ " from zy_water w,zy_region re  \r\n" + " where w.regionid = re.id and w.watertime >= to_date('"
				+ startTime + "','" + timeFormat + "') AND w.watertime <= to_date('" + endTime + "','" + timeFormat
				+ "')\r\n" + " " + regionSql + "    group by to_char(w.watertime, '" + timeFormat
				+ "')  order by \"时间\"";
		List<Map> tableMapList1 = waterMapper.executeSql(tableSql1);
		resultMap.put("table_1", tableMapList1);
		// 列表2农业非农业
		String tableSql2 = "select to_char(w.watertime, '" + timeFormat + "') as \"时间\",\r\n"
				+ " sum(case when re.isFarming = true then w.watervalue else 0 end) as \"农业\",  \r\n"
				+ "  sum(case when re.isFarming = false then  w.watervalue else 0 end) as \"非农业\"\r\n"
				+ " from zy_water w,zy_region re  \r\n" + " where w.regionid = re.id " + regionSql
				+ " and w.watertime >= to_date('" + startTime + "','" + timeFormat + "') AND w.watertime <= to_date('"
				+ endTime + "','" + timeFormat + "')\r\n" + "    group by to_char(w.watertime, '" + timeFormat
				+ "')   order by \"时间\"";
		List<Map> tableMapList2 = waterMapper.executeSql(tableSql2);
		resultMap.put("table_2", tableMapList2);
		// 列表3 农业 工业 生态
		String tableSql3 = "select to_char(w.watertime, '" + timeFormat + "') as \"时间\",\r\n"
				+ " sum(case when re.isFarming = true then w.watervalue else 0 end) as \"农业\",  \r\n"
				+ "  sum(case when re.isIndustry = true then  w.watervalue else 0 end) as \"工业\",\r\n"
				+ "  sum(case when re.issheng = true then  w.watervalue else 0 end) as \"生态\"\r\n"
				+ " from zy_water w,zy_region re  \r\n" + " where w.regionid = re.id " + regionSql
				+ " and w.watertime >= to_date('" + startTime + "','" + timeFormat + "') AND w.watertime <= to_date('"
				+ endTime + "','" + timeFormat + "')\r\n" + "    group by to_char(w.watertime, '" + timeFormat
				+ "')   order by \"时间\"";
		List<Map> tableMapList3 = waterMapper.executeSql(tableSql3);
		resultMap.put("table_3", tableMapList3);
		// 2.饼图
		// 饼图1 各地区统计
		String pieSql1 = "select  re.name  ,\r\n" + "      sum( w.watervalue) watervalue \r\n"
				+ " from zy_water w,zy_region re  \r\n" + " where w.regionid = re.id  " + regionSql
				+ " and w.watertime >= to_date('" + startTime + "','" + timeFormat + "') AND w.watertime <= to_date('"
				+ endTime + "','" + timeFormat + "')\r\n" + "    group by re.name   ";
		List<Map> pieMapList1 = waterMapper.executeSql(pieSql1);
		resultMap.put("pie_1", pieMapList1);
		// 饼图2 农业非农业
		String pieSql2 = "select (case when re.isFarming = true then '农业' else '非农业' end) as name ,\r\n"
				+ " sum(w.watervalue) watervalue \r\n" + " from zy_water w,zy_region re  \r\n"
				+ " where w.regionid = re.id and w.watertime >= to_date('" + startTime + "','" + timeFormat + "') "
				+ " AND w.watertime <to_date('" + endTime + "','" + timeFormat + "')\r\n"
				+ "    group by re.isFarming ";
		List<Map> pieMapList2 = waterMapper.executeSql(pieSql2);
		resultMap.put("pie_2", pieMapList2);
		// 饼图3 农业工业
		String pieSql3 = "select \r\n"
				+ " sum(case when re.isFarming = true then w.watervalue else 0 end) as \"农业\",  \r\n"
				+ " sum(case when re.isIndustry = true then  w.watervalue else 0 end) as \"工业\",\r\n"
				+ " sum(case when re.issheng = true then  w.watervalue else 0 end) as \"生态\"\r\n"
				+ " from zy_water w,zy_region re  \r\n"
				+ " where w.regionid = re.id and (re.isFarming=true or re.isIndustry=true or re.issheng=true) and w.watertime >= to_date('"
				+ startTime + "','" + timeFormat + "') AND w.watertime <to_date('" + endTime + "','" + timeFormat
				+ "')";
		List<Map> pieMapList3 = waterMapper.executeSql(pieSql3);
		resultMap.put("pie_3", pieMapList3);

		System.out.println(resultMap);
		return resultMap;

	}

	@RequestMapping("regionList")
	@ResponseBody
	public Object getRegionList(@RequestBody Map<String, Object> map) {
		System.out.println(map);
		Map<String, Object> resultMap = new HashMap<String, Object>();

		List<RegionInfo> regionList = waterMapper.getRegionList();
		resultMap.put("regionList", regionList);

		return resultMap;
	}

//	public String uploadFileHandler(@RequestParam("fileinfo") MultipartFile file) {
//		if (!file.isEmpty()) {
//			try {
//				// 文件存放服务端的位置
//				String rootPath = "d:/tmp";
//				File dir = new File(rootPath + File.separator + "tmpFiles");
//				if (!dir.exists())
//					dir.mkdirs();
//				// 写文件到服务器
//				File serverFile = new File(dir.getAbsolutePath() + File.separator + file.getOriginalFilename());
//				file.transferTo(serverFile);
//				return "You successfully uploaded file=" + file.getOriginalFilename();
//			} catch (Exception e) {
//				return "You failed to upload " + file.getOriginalFilename() + " => " + e.getMessage();
//			}
//		} else {
//			return "You failed to upload " + file.getOriginalFilename() + " because the file was empty.";
//		}
//	}
	// 自定义上传文件存储目录
	private static final String UPLOAD_DIRECTORY = "uploadFlie";
	@RequestMapping("uploadFile")
	@ResponseBody
	public Object uploadFileHandler(@RequestParam(value = "fileinfo", required = false) MultipartFile file,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("result", "success");

		HashMap<String, Integer> regionMap = new HashMap<String, Integer>();
		List<RegionInfo> regionList = waterMapper.getRegionList();
		for (int i = 0; i < regionList.size(); i++) {
			regionMap.put(regionList.get(i).getName(), regionList.get(i).getId());
		}

		String filePath = getFilePath(file, request, response);
		if(filePath == null) {
			resultMap.put("result", "上传失败，失败原因：上传文件为空！");
			return resultMap;
		}
		FileInputStream fileInputStream;
		BufferedInputStream bufferedInputStream;
		POIFSFileSystem fileSystem;
		XSSFWorkbook xss;
		try {
			fileInputStream = new FileInputStream(filePath);
			bufferedInputStream = new BufferedInputStream(fileInputStream);
			xss = new XSSFWorkbook(bufferedInputStream);
			XSSFSheet sheet = xss.getSheetAt(0);
			// 1.获取年份 第0行0列
			int year = parseYear(sheet);
			if (year == 0) {
				resultMap.put("result", "年份不正确，请确认在1行1列放有年份信息，xxxx年***供水台账");
				return resultMap;
			}
			
			// 2.获取地区 第2行 1列到9列
			// 存列和地区名的关系
			HashMap<Integer, String> cellRegionMap = parseCellRegion(sheet);
			if(cellRegionMap.size()==0) {
				resultMap.put("result", "项目类型不正确,请确认第3行 第2列到10列存放有项目名称");
				return resultMap;
			}

			// 3.获取各月份的值 第3行1列 到 第14行9列
			ArrayList<Water> waterList = new ArrayList<Water>();
			Date curTime = Calendar.getInstance().getTime();
			for (int rowNum = 3; rowNum <= 14; rowNum++) {

				XSSFRow row = sheet.getRow(rowNum);
				Date waterTime = new Date(year - 1900, rowNum - 3, 1, 1, 1, 0);
				Date createTime = new Date();
				for (int cellNum = 1; cellNum <= 9; cellNum++) {
					try {
						Water water = new Water();
						water.setCreateTime(curTime);
						water.setWaterTime(waterTime);// 供水时间	
//						water.setCreateTime(createTime);
						String region = cellRegionMap.get(cellNum);// 供水地区
						int regionId = regionMap.get(region);
						water.setRegionId(regionId);
						// 供水量
						XSSFCell cell = row.getCell(cellNum);
						Object cellValue = null;
						org.apache.poi.ss.usermodel.CellType cellType = cell.getCellType();
						if (cellType == cellType.NUMERIC) {
							if (DateUtil.isCellDateFormatted(cell)) {
								cellValue = cell.getDateCellValue();
							} else {

								cellValue = cell.getNumericCellValue();
								water.setWaterValue((double) cellValue);
								waterList.add(water);

							}
						}  else if (cellType == cellType.FORMULA) {
							cellValue = Double.parseDouble(cell.getCellFormula());
							water.setWaterValue((double) cellValue);
							waterList.add(water);
						} 
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						resultMap.put("result", "第" + (rowNum + 1) + "行第" + (cellNum + 1) + "列数据格式不正确");
						return resultMap;
					}
				}

			}

			for (Water water : waterList) {
				waterMapper.addWaterData(water);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resultMap.put("result", "导入失败！");
		}

		return resultMap;

	}
	private int parseYear(XSSFSheet sheet) {
		int year = 0;
		try {
			XSSFRow rowYear = sheet.getRow(0);
			XSSFCell cellYear = rowYear.getCell(0);
			String title = cellYear.getStringCellValue();
			year = Integer.parseInt(title.substring(0, title.indexOf("年")));
			System.out.println("年份:" + year);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return year;
	}
	private HashMap<Integer, String> parseCellRegion(XSSFSheet sheet){
		HashMap<Integer, String> cellRegionMap = new HashMap<Integer, String>();
		try {
			XSSFRow rowRegion = sheet.getRow(2);
			for (int cellNum = 1; cellNum <= 9; cellNum++) {
				cellRegionMap.put(cellNum, rowRegion.getCell(cellNum).getStringCellValue());
			}
		} catch (Exception e) {
			cellRegionMap = null;
			e.printStackTrace();
		}		
		return cellRegionMap;
	}
	private String getFilePath(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
		String filePath = new String();
		String uploadPath = request.getSession().getServletContext().getRealPath("./") + UPLOAD_DIRECTORY;

		// 如果目录不存在则创建
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		// 判断文件是否为空
		if (!file.isEmpty()) {
			try {
				System.out.print("文件名:" + file.getOriginalFilename());
				// 文件的保存路径
				filePath = request.getSession().getServletContext().getRealPath("/") + UPLOAD_DIRECTORY + File.separator
						+ Calendar.getInstance().getTimeInMillis() + ".xlsx";

				// 转存文件
				file.transferTo(new File(filePath));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			return null;
		}
		return filePath;
	}
}
