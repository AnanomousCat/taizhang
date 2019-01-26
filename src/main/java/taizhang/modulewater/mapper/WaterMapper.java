package taizhang.modulewater.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import taizhang.modulewater.bean.DataCheck;
import taizhang.modulewater.bean.RegionInfo;
import taizhang.modulewater.bean.Water;

@Component
public interface WaterMapper {

	ArrayList<RegionInfo> getRegionList();

	void addWaterData(Water water);

	List<Map> executeSql(String sql);

	List<Water> getWaterList(@Param("checkStatus") int checkStatus, 
			@Param("regionIdList")String regionIdList,
			@Param("createTime")String createTime,
			@Param("waterTime")String waterTime);

	void updateWaterData(@Param("waterValue")Double waterValue,@Param("id") int id);

	void deleteWaterDataById(@Param("idList")String idList);

	void updateCheckStatus(@Param("status")int status, @Param("idList")String idList);

	List<Map> getCheckStatusDic();
	
	void addCheckData(DataCheck check);
}
