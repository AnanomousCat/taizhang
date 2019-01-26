//建表和序列
create table zy_user(
	id int primary key not null,
	username text,
	pswd text,
	createtime date,
	updatetime date
)
CREATE SEQUENCE seq_userid
	 INCREMENT 1
	 minvalue 1
	 START 1;
	 
SELECT nextval('seq_userid');


create table zy_permission(
	id int primary key not null,
	permname text,
	description char(512),
	createtime date,
	updatetime date,
	code text
)
CREATE SEQUENCE seq_permid
	 INCREMENT 1
	 minvalue 1
	 START 1;
	 
create table zy_role(
	id int primary key not null,
	rolename text,
	description char(512),
	createtime date,
	updatetime date
)
CREATE SEQUENCE seq_roleid
	 INCREMENT 1
	 minvalue 1
	 START 1;
	 
create table  zy_user_role(
	userid int,
	roleid int,
	createtime date
)
create table zy_perm_role(
	permid int,
	roleid int,
	createtime date
)


create table zy_water(
	id int primary key not null,
	regionid int,
	watertime date,
	watervalue double PRECISION,
	createtime date,
	checkstatus int
)
CREATE SEQUENCE seq_waterid
	 INCREMENT 1
	 minvalue 1
	 START 1;
	 
	
create table zy_region(
	id int primary key not null,
	name text,
	isfarming bool,
	isindustry bool
)
CREATE SEQUENCE seq_regionid
	 INCREMENT 1
	 minvalue 1
	 START 1;
//初始化数据
//用户
INSERT INTO public.zy_user(
	id, username, pswd, createtime, updatetime)
	VALUES (1, 'root', '123456', current_date, current_date);
	
//权限
INSERT INTO public.zy_permission(
	id, permname, description, createtime, updatetime, code)
	VALUES (nextval('seq_permid'), '数据录入权限', '录入供水数据',current_date, current_date, 'water_data_input');
INSERT INTO public.zy_permission(
	id, permname, description, createtime, updatetime, code)
	VALUES (nextval('seq_permid'), '科室审核权限', '科室审核',current_date, current_date, 'keshi_check');
INSERT INTO public.zy_permission(
	id, permname, description, createtime, updatetime, code)
	VALUES (nextval('seq_permid'), '中心审核权限', '中心审核',current_date, current_date, 'center_check');

INSERT INTO public.zy_permission(
	id, permname, description, createtime, updatetime, code)
	VALUES (nextval('seq_permid'), '新增用户权限', '新增用户权限',current_date, current_date, 'add_user');
INSERT INTO public.zy_permission(
	id, permname, description, createtime, updatetime, code)
	VALUES (nextval('seq_permid'), '编辑用户权限', '编辑用户权限',current_date, current_date, 'edit_user');
INSERT INTO public.zy_permission(
	id, permname, description, createtime, updatetime, code)
	VALUES (nextval('seq_permid'), '删除用户权限', '删除用户权限',current_date, current_date, 'delete_user');
	
INSERT INTO public.zy_permission(
	id, permname, description, createtime, updatetime, code)
	VALUES (nextval('seq_permid'), 'Excel上传权限', 'Excel上传权限',current_date, current_date, 'upload_file');
INSERT INTO public.zy_permission(
	id, permname, description, createtime, updatetime, code)
	VALUES (nextval('seq_permid'), '录入供水数据权限', '录入供水数据权限',current_date, current_date, 'add_water');
INSERT INTO public.zy_permission(
	id, permname, description, createtime, updatetime, code)
	VALUES (nextval('seq_permid'), '修改供水数据权限', '修改供水数据权限',current_date, current_date, 'edit_water');
INSERT INTO public.zy_permission(
	id, permname, description, createtime, updatetime, code)
	VALUES (nextval('seq_permid'), '删除供水数据权限', '删除供水数据权限',current_date, current_date, 'delete_water');
	
INSERT INTO public.zy_permission(
	id, permname, description, createtime, updatetime, code)
	VALUES (nextval('seq_permid'), '提交审核权限', '提交审核权限',current_date, current_date, 'submit_check');
//地区
INSERT INTO public.zy_region(
	id, name, isfarming, isindustry)
	VALUES (nextval('seq_regionid'), '平阴灌溉', true, false);
	
INSERT INTO public.zy_region(
	id, name, isfarming, isindustry)
	VALUES (nextval('seq_regionid'), '肥城灌溉', true, false);

INSERT INTO public.zy_region(
	id, name, isfarming, isindustry)
	VALUES (nextval('seq_regionid'), '沿黄村庄灌溉', true, false);

INSERT INTO public.zy_region(
	id, name, isfarming, isindustry)
	VALUES (nextval('seq_regionid'), '玫瑰湖湿地用水', true, false);
	
INSERT INTO public.zy_region(
	id, name, isfarming, isindustry)
	VALUES (nextval('seq_regionid'), '平阴用水', false, false);

INSERT INTO public.zy_region(
	id, name, isfarming, isindustry)
	VALUES (nextval('seq_regionid'), '肥城工业', false, true);
	
INSERT INTO public.zy_region(
	id, name, isfarming, isindustry)
	VALUES (nextval('seq_regionid'), '琦泉热电', false, false);
	
INSERT INTO public.zy_region(
	id, name, isfarming, isindustry)
	VALUES (nextval('seq_regionid'), '济南用水', false, false);

INSERT INTO public.zy_region(
	id, name, isfarming, isindustry)
	VALUES (nextval('seq_regionid'), '胶东用水', false, false);
	
	
//审核

create table zy_check(
	id int primary key not null,
	waterid  int,
	checkuser text,
	checktime date,
	status int
)
CREATE SEQUENCE seq_checkid
	 INCREMENT 1
	 minvalue 1
	 START 1;
	 

create table zy_dictionary(
	tablename text,
	columnname text,	
	value int,
	descrition text
);

insert into zy_dictionary(tablename,columnname,value,descrition)values('zy_water','checkstatus',0,'待提交审核');
insert into zy_dictionary(tablename,columnname,value,descrition)values('zy_water','checkstatus',1,'待科室审核');
insert into zy_dictionary(tablename,columnname,value,descrition)values('zy_water','checkstatus',2,'科室审核退回');
insert into zy_dictionary(tablename,columnname,value,descrition)values('zy_water','checkstatus',3,'待中心审核');
insert into zy_dictionary(tablename,columnname,value,descrition)values('zy_water','checkstatus',4,'中心审核退回');
insert into zy_dictionary(tablename,columnname,value,descrition)values('zy_water','checkstatus',5,'归档');

