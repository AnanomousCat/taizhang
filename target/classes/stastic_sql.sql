SELECT   w.watertime, string_agg(re.name||':'||w.watertime,',') as tmp
	FROM public.zy_water w,zy_region re
	where w.regionid = re.id  group by w.watertime order by w.watertime
select version();	
--农业工业

select 
 sum(case when re.isFarming = true then w.watervalue else 0 end) as "农业",  
 sum(case when re.isIndustry = true then  w.watervalue else 0 end) as "工业"
 from zy_water w,zy_region re  
 where w.regionid = re.id and (re.isFarming=true or re.isIndustry=true) and w.watertime >= to_date('2016','YYYY') AND w.watertime <to_date('2017','YYYY')

select to_char(w.watertime, 'YYYY-MM') as "时间",
 sum(case when re.isFarming = true then w.watervalue else 0 end) as "农业",  
  sum(case when re.isIndustry = true then  w.watervalue else 0 end) as "工业"
 from zy_water w,zy_region re  
 where w.regionid = re.id and w.watertime >= to_date('2016','YYYY') AND w.watertime < to_date('2017','YYYY')
    group by to_char(w.watertime, 'YYYY-MM')   order by "时间"
	
--农业非农业

select re.isFarming,
 sum(w.watervalue) 
 from zy_water w,zy_region re  
 where w.regionid = re.id and w.watertime >= to_date('2016','YYYY') AND w.watertime <to_date('2017','YYYY')
    group by re.isFarming ；
	
select to_char(w.watertime, 'YYYY-MM') as "时间",
 sum(case when re.isFarming = true then w.watervalue else 0 end) as "农业",  
  sum(case when re.isFarming = false then  w.watervalue else 0 end) as "非农业"
 from zy_water w,zy_region re  
 where w.regionid = re.id and w.watertime >= to_date('2016','YYYY') AND w.watertime <= to_date('2017','YYYY')
    group by to_char(w.watertime, 'YYYY-MM')   order by "时间"；


--地区
select  re.name  ,
      sum( w.watervalue)
 from zy_water w,zy_region re  
 where w.regionid = re.id and w.watertime >= to_date('2016','YYYY') AND w.watertime < to_date('2017','YYYY')
    group by re.name   ；
	
	

select to_char(w.watertime, 'YYYY-MM') as "时间",
 sum(case when re.name = '平阴灌溉' then w.watervalue else 0 end) as "平阴灌溉",  
  sum(case when re.name = '肥城灌溉' then  w.watervalue else 0 end) as "肥城灌溉",  
 sum(case when re.name = '沿黄村庄灌溉'   then w.watervalue else 0 end) as "沿黄村庄灌溉",   
 sum(case when re.name = '玫瑰湖湿地用水'   then w.watervalue else 0 end) as "玫瑰湖湿地用水",   
 sum(case when re.name = '平阴用水'   then w.watervalue else 0 end) as "平阴用水",
 sum(case when re.name = '肥城工业'   then w.watervalue else 0 end) as "肥城工业",
 sum(case when re.name = '琦泉热电'   then w.watervalue else 0 end) as "琦泉热电",
 sum(case when re.name = '济南用水'   then w.watervalue else 0 end) as "济南用水",
 sum(case when re.name = '胶东用水'   then w.watervalue else 0 end) as "胶东用水"
 from zy_water w,zy_region re  
 where w.regionid = re.id --and w.watertime >= to_date('2016','YYYY') AND w.watertime <= to_date('2017','YYYY')
    group by to_char(w.watertime, 'YYYY-MM')   order by "时间"
	
	
	
	

	select name,string_agg(subject||':'||score,',') as tmp from score group by name order by name desc