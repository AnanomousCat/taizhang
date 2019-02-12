var isFirstLoad = true;
// 基于准备好的dom，初始化echarts实例
var myPieChart1 = echarts.init(document.getElementById('pie_tab_1'));
var myPieChart2 = echarts.init(document.getElementById('pie_tab_2'));
var myPieChart3 = echarts.init(document.getElementById('pie_tab_3'));
var barChart = echarts.init(document.getElementById('barId'));
var lineChart = echarts.init(document.getElementById('lineId'));
$(function() {
	$('table.display').dataTable();
	$("input[name='r3']").change(function() {
		refreshTimeSelect();
	});

	$("#searchBtn").click(function() {
		isFirstLoad = false;
		refreshWaterData();
	});
	$('.select2').select2();
 
	refreshTimeSelect();
	getRegionList();
	refreshWaterData();
});

function getRegionList() {
	var queryData = {
		name : ""
	};
	$.ajax({
		url : "/taizhang/water/regionList",
		type : "post",
		data : JSON.stringify(queryData),
		dataType : "json",
		contentType : "application/json;charset=UTF-8",
		success : function(data) {
			var regionList = data.regionList;
			var regionIdList = [];
			$("#regionSelId").empty();
			for ( var i in regionList) {
				var opt = $("<option></option>");
				opt.html(regionList[i].name);
				opt.attr("value", regionList[i].id);
				
				$("#regionSelId").append(opt);
				regionIdList.push(regionList[i].id);
			}
			$("#regionSelId").val(regionIdList);
			$("#regionSelId").trigger("change");
		},
		error : function(xhr, status, error) {
			console.log(xhr);
			console.log(status);
			console.log(error);
		}
	});
}
function refreshWaterData() {
	var queryData = {};
	if (isFirstLoad) {
		var myDate = new Date();
		var year = myDate.getFullYear();
		var month = myDate.getMonth();
		queryData = {
			startTime : year,
			endTime : year,
			timeType : "year",// 年或月
			regionIdList : "all"
		}
	} else {
		queryData = {
			startTime : $("#startTimeId").val(),
			endTime : $("#endTimeId").val(),
			timeType : $("input[name='r3']:checked").val(),// 年或月
			regionIdList : $("#regionSelId").val()
		}
	}
	console.log($("#regionSelId").get(0).selectedindex);
	$.ajax({
		url : "/taizhang/water/waterStatistics",
		type : "post",
		data : JSON.stringify(queryData),
		dataType : "json",
		contentType : "application/json;charset=UTF-8",
		success : function(data) {
			if (isFirstLoad) {
				isFirstLoad = false;
			}
			setTableData(data);
			setPieData(data);
			setBarData(data);
			setLineData(data);
		}
	});
};
function setTableData(data) {
	setData(data.table_1, $("#example1"), $("#theadId1"));
	setData(data.table_2, $("#example2"), $("#theadId2"));
	setData(data.table_3, $("#example3"), $("#theadId3"));

};
function setData(dataList, table, thead) {
	clearTable(table);
	if (dataList.length > 0) {
		var obj = dataList[0];
		var columnArr = [];
		thead.empty();
		var tr = $("<tr></tr>");
		for ( var i in obj) {
			columnArr.push({
				data : i
			});
			var thName = $("<th>" + i + "</th>");
			tr.append(thName);
		}
		thead.append(tr);
		initDataTable(table, dataList, columnArr);

	}
}
function setPieData(data) {

	var data1 = genData(data.pie_1);
	var data2 = genData(data.pie_2);
	var data3 = genData3(data.pie_3);

	var option1 = getPieOption(data1);
	var option2 = getPieOption(data2);
	var option3 = getPieOption(data3);
	myPieChart1.setOption(option1);
	myPieChart2.setOption(option2);
	myPieChart3.setOption(option3);
};
function getPieOption(data) {
	return {
		title : {
			text : $("#startTimeId").val() + "至" + $("#endTimeId").val()
					+ "供水量统计",
			subtext : '',
			x : 'center'
		},
		tooltip : {
			trigger : 'item',
			formatter : "{a} <br/>{b} : {c} ({d}%)"
		},
		toolbox : {
			feature : {
				saveAsImage : {
					show : true
				}
			}
		},
		legend : {
			type : 'scroll',
			orient : 'vertical',
			right : 10,
			top : 20,
			bottom : 20,
			data : data.legendData,

			selected : data.selected
		},
		series : [ {
			name : '',
			type : 'pie',
			radius : '55%',
			center : [ '40%', '50%' ],
			data : data.seriesData,
			itemStyle : {
				emphasis : {
					shadowBlur : 10,
					shadowOffsetX : 0,
					shadowColor : 'rgba(0, 0, 0, 0.5)'
				}
			}
		} ]
	}
}
function genData(dataList) {
	var legendData = [];
	var seriesData = [];
	var selected = {};

	for ( var i in dataList) {
		legendData.push(dataList[i].name);
		seriesData.push({
			name : dataList[i].name,
			value : dataList[i].watervalue
		});
		selected[dataList[i].name] = true;
	}
	;
	return {
		legendData : legendData,
		seriesData : seriesData,
		selected : selected
	};

};

function genData3(data) {
	console.log(data);
	var legendData = [];
	var seriesData = [];
	var selected = {};
	for ( var i in data) {
		var obj = data[i];
		for ( var key in obj) {
			console.log(key);
			console.log(obj[key])
			legendData.push(key);
			seriesData.push({
				name : key,
				value : obj[key]
			});
			selected[key] = true;
		}
	}

	return {
		legendData : legendData,
		seriesData : seriesData,
		selected : selected
	};

};

function setBarData(data) {

	barChart.clear();
	var labelOption = {
		normal : {
			show : false,
			position : 'top',
			distance : 15,
			align : 'left',
			verticalAlign : 'middle',
			rotate : 60,
			formatter : '{c} ',
			fontSize : 16,
			rich : {
				name : {
					textBorderColor : '#fff'
				}
			}
		}
	};

	var dataList = data.table_1;
	var xdata = [];
	var nameArr = [];
	var yseries = [];

	for ( var i in dataList) {
		var obj = dataList[i];
		xdata.push(obj['时间']);
	}
	if (dataList.length > 0) {
		var obj = dataList[0];
		for ( var key in obj) {
			if (key == '时间') {
				continue;
			}
			nameArr.push(key);
		}
	}
	for ( var i in nameArr) {
		var ydata = [];
		for ( var j in dataList) {
			var obj = dataList[j];
			for ( var key in obj) {
				if (key == nameArr[i]) {
					ydata.push(obj[key]);
				}
			}
		}
		yseries.push({
			name : nameArr[i],
			type : 'bar',
			barGap : 0,
			label : labelOption,
			data : ydata
		});
	}

	baroption = {
		tooltip : {
			trigger : 'axis',
			axisPointer : {
				type : 'shadow'
			}
		},
		legend : {
			data : nameArr
		},
		toolbox : {
			feature : {
				saveAsImage : {
					show : true
				}
			}
		},
		calculable : true,
		xAxis : [ {
			type : 'category',
			axisTick : {
				show : false
			},
			data : xdata
		} ],
		yAxis : [ {
			type : 'value'
		} ],
		series : yseries
	};
	barChart.setOption(baroption);
};
function setLineData(data) {

	lineChart.clear();
	var labelOption = {
		normal : {
			show : false,
			position : 'top',
			distance : 15,
			align : 'left',
			verticalAlign : 'middle',
			rotate : 60,
			formatter : '{c} ',
			fontSize : 16,
			rich : {
				name : {
					textBorderColor : '#fff'
				}
			}
		}
	};
	var dataList = data.table_1;
	var xdata = [];
	var nameArr = [];
	var yseries = [];

	for ( var i in dataList) {
		var obj = dataList[i];
		xdata.push(obj['时间']);
	}
	if (dataList.length > 0) {
		var obj = dataList[0];
		for ( var key in obj) {
			if (key == '时间') {
				continue;
			}
			nameArr.push(key);
		}
	}
	for ( var i in nameArr) {
		var ydata = [];
		for ( var j in dataList) {
			var obj = dataList[j];
			for ( var key in obj) {
				if (key == nameArr[i]) {
					ydata.push(obj[key]);
				}
			}
		}
		yseries.push({
			name : nameArr[i],
			type : 'line',
			barGap : 0,
			label : labelOption,
			data : ydata
		});
	}
	lineoption = {
		title : {
			text : '折线图堆叠'
		},
		tooltip : {
			trigger : 'axis'
		},
		legend : {
			data : nameArr
		},
		grid : {
			left : '3%',
			right : '4%',
			bottom : '3%',
			containLabel : true
		},
		toolbox : {
			feature : {
				saveAsImage : {}
			}
		},
		xAxis : {
			type : 'category',
			boundaryGap : false,
			data : xdata
		},
		yAxis : {
			type : 'value'
		},
		series : yseries
	};
	lineChart.setOption(lineoption);
};
function initDataTable(table, data, columnArr) {
	table.DataTable({
		"destroy" : true,// 如果需要重新加载的时候请加上这个
		"data" : data,
		"columns" : columnArr,
		"paging" : true,
		"lengthChange" : false,
		"searching" : false,
		"ordering" : true,
		"info" : true,
		"autoWidth" : false,

		language : {
			"sProcessing" : "处理中...",
			"sLengthMenu" : "显示 _MENU_ 项结果",
			"sZeroRecords" : "没有匹配结果",
			"sInfo" : "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
			"sInfoEmpty" : "显示第 0 至 0 项结果，共 0 项",
			"sInfoFiltered" : "(由 _MAX_ 项结果过滤)",
			"sInfoPostFix" : "",
			"sSearch" : "搜索:",
			"sUrl" : "",
			"sEmptyTable" : "当前条件下没有数据",
			"sLoadingRecords" : "载入中...",
			"sInfoThousands" : ",",
			"oPaginate" : {
				"sFirst" : "首页",
				"sPrevious" : "上页",
				"sNext" : "下页",
				"sLast" : "末页"
			},
			"oAria" : {
				"sSortAscending" : ": 以升序排列此列",
				"sSortDescending" : ": 以降序排列此列"
			}
		}
	})
}
function clearTable(table) {
	console.log("1");
	var tableApi = table.DataTable({
		"destroy" : true,
		"lengthChange" : false,
		"searching" : false,
		"ordering" : false,
		"paging" : true,
		"info" : true,
		"autoWidth" : false,
		language : {
			"sProcessing" : "处理中...",
			"sLengthMenu" : "显示 _MENU_ 项结果",
			"sZeroRecords" : "没有匹配结果",
			"sInfo" : "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
			"sInfoEmpty" : "显示第 0 至 0 项结果，共 0 项",
			"sInfoFiltered" : "(由 _MAX_ 项结果过滤)",
			"sInfoPostFix" : "",
			"sSearch" : "搜索:",
			"sUrl" : "",
			"sEmptyTable" : "当前条件下没有数据",
			"sLoadingRecords" : "载入中...",
			"sInfoThousands" : ",",
			"oPaginate" : {
				"sFirst" : "首页",
				"sPrevious" : "上页",
				"sNext" : "下页",
				"sLast" : "末页"
			},
			"oAria" : {
				"sSortAscending" : ": 以升序排列此列",
				"sSortDescending" : ": 以降序排列此列"
			}
		}
	});
	tableApi.clear().draw();
}
function refreshTimeSelect() {
	// 初始化表单值，
	var myDate = new Date();// 获取系统当前时间
	var myYear = myDate.getFullYear(); // 获取完整的年份(4位,1970-????)
	var myMonth = myDate.getMonth(); // 获取当前月份(0-11,0代表1月)
	$("#startTimeId").empty();
	$("#endTimeId").empty();

	if ($('input:radio[name=r3]:checked').val() == "year") {
		var minYear = 2013;
		var maxYear = myYear;
		for (var year = minYear; year <= maxYear; year++) {
			var opt1 = $("<option></option>");
			var opt2 = $("<option></option>");
			opt1.html(year + "年");
			opt1.attr("value", year);
			opt2.html(year + "年");
			opt2.attr("value", year);
			$("#startTimeId").append(opt1);
			$("#endTimeId").append(opt2);
		}
		$("#startTimeId").val(maxYear);
		$("#endTimeId").val(maxYear);
	} else {
		var minYear = 2013;
		var maxYear = myYear;
		var maxMonth = 12;
		for (var year = minYear; year <= maxYear; year++) {
			if (year == maxYear) {
				maxMonth = myMonth + 1;
			}
			for (var i = 1; i <= maxMonth; i++) {
				var year_Month = "";
				if (i < 10) {
					year_Month = year + "-0" + i;
				} else {
					year_Month = year + "-" + i;
				}
				var opt1 = $("<option></option>");
				var opt2 = $("<option></option>");
				opt1.html(year_Month);
				opt1.attr("value", year_Month);
				opt2.html(year_Month);
				opt2.attr("value", year_Month);
				$("#startTimeId").append(opt1);
				$("#endTimeId").append(opt2);
				$("#startTimeId").val(year_Month);
				$("#endTimeId").val(year_Month);
			}
		}

	}
}
