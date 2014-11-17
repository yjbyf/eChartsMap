<#if devMode = "no">
<script src="${requestUrl}/echarts/echarts-plain-map.js"></script>
<#elseif devMode = "yes">
<script src="${requestUrl}/echarts/echarts-plain-original-map.js"></script>
</#if>
<script src="${requestUrl}/echarts/esl.js"></script>	
<!--todo no border-->
<div id="graphic" class="col-md-8">
	<div>
		<span id='returnSpan' style="color:#1e90ff"><a href="javascript:returnToCountry();" id="returnToParentMap" style="text-decoration:none;display:none">${returnToCountryHref}</a></span>
	</div>
	<div id="mapDiv" class="mapDiv" style="height:${height}px;width:${width}px;border:0px;padding:10px;"></div>
</div> 
<#if areaTableShow = 'true'>
<div>            
	<#if areaVar ??>
	 <table align="center" border="1" cellpadding="0" cellspacing="0" width="90%">
	    <tr align="center">
	        <th>序列</th>
	        <th>大区</th>
	        <th>值</th>
	    </tr>
	    <#list areaVar as map>
	    <tr align="center">
	        <td>${map['areaTableSeriesLabel']}</td>
	        <td><span class="text-primary">${map['areaTableAreaLabel']}</span></td>
	        <td><span id='console' style="color:#1e90ff">${map['areaTableQuantityLabel']}</span> </td>    
	    </th>
	    </#list>
	</table>
	</#if>
</div>
</#if>
<#if devMode = "yes">
<!--
	${result}
	-------------------
	${debug}
	<#if areaShow = "true">
		大区配置如下:		
		<#if areaDataSql ??>
			大区名:${areaName}
			大区定义:${areaLocations}
			areaDataSql:${areaDataSql}
			大区原始数据:${allAreaData}
			大区明细数据:${areaProvinceData}
			areaData:${areaData}
			大区颜色定义:${areaColor}
			省市映射大区定义:<#if cityToRegion ??>${cityToRegion}</#if>
		</#if>
	</#if>
	<#if openUrl ??>${openUrl}</#if>
-->
</#if>
<script>
	var g_series_seleted;
    var l_geoCoord = {
                    '北京':[116.39564503788,39.92998577808],
                    '天津': [117.4219,39.4189],
                    '上海': [121.4648,31.2891],
                    '重庆':[106.53063501341,29.544606108886],
                    '河北':[117.22029676508,39.173148933924],
                    '河南':[113.836353,34.850195],
                    //'河南':[101.55630729533,34.511389737869],
                    '云南':[101.59295163701,24.864212795483],
                    '辽宁':[122.75359155772,41.621600105958],
                    '黑龙江':[128.04741371499,47.356591643111],
                    '湖南':[111.72066354648,27.695864052356],
                    '安徽':[117.21600520757,31.859252417079],
                    '山东':[118.52766339288,36.099289929728],
                    //'新疆':[126.63770727987,45.611746712731],
                     '新疆':[87.564987741116,43.840380347218],
                    '江苏':[119.36848893836,33.013797169954],
                    '浙江':[119.95720242066,29.159494120761],
                    '江西':[115.6760823667,27.757258443441],
                    '湖北':[112.41056219213,31.20931625014],
                    '广西':[108.92427442706,23.552254688119],
                    '甘肃':[102.45762459934,38.103267343752],
                    '山西':[112.51549586384,37.866565990509],
                    '内蒙古':[114.41586754817,43.468238221949],
                    '陕西':[109.50378929073,35.860026261323],
                    '吉林':[126.56454398883,43.871988334359],
                    '福建':[117.98494311991,26.050118295661],
                    '贵州':[106.7349961033,26.902825927797],
                    '广东':[113.39481755876,23.408003729025],
                    '青海':[96.202543672261,35.499761004275],
                    '西藏':[89.137981684031,31.367315402715],
                    '四川':[102.8991597236,30.367480937958],
                    '宁夏':[106.15548126505,37.321323112295],
                    '海南':[110.31327,20.045687],
                    '香港':[114.18612410257,22.29358599328],
                    '澳门':[113.55751910182,22.204117988443]
                };
    var mapDivName = "mapDiv";            
	//var domMain = document.getElementById("mapDiv");
    //var myChart = echarts.init(domMain);
    var chart_path = "${requestUrl}/echarts/echarts-plain-original-map"; 
    var theme_path = "${requestUrl}/echarts/theme/"; 
    
    require.config({
        paths:{
           		echarts:chart_path,
           		themePath:theme_path //主题目录,themePath的实际目录，以供后面实际替换
        }
    });
    
    var hash = 'default';
    // 按需加载
    require(
        [
            'echarts',
            'themePath/' + hash //在后面require(['themePath/' + theme]中，以此判定书写格式是否符合规范
        ]
        
    );
    
    var domMain ;
    var myChart ;
    
    domMain = document.getElementById(mapDivName);
    myChart = echarts.init(domMain);
    
    ${themeParts}
	${dataRangeStyle}

var ecConfig = echarts.config;//require('echarts/config');
var zrEvent = zrender.tool.event;
//var zrEvent = require('zrender/tool/event');
var curIndx = 0;
var mapType = [
    'china',
    // 23个省
    '广东', '青海', '四川', '海南', '陕西', 
    '甘肃', '云南', '湖南', '湖北', '黑龙江',
    '贵州', '山东', '江西', '河南', '河北',
    '山西', '安徽', '福建', '浙江', '江苏', 
    '吉林', '辽宁', '台湾',
    // 5个自治区
    '新疆', '广西', '宁夏', '内蒙古', '西藏', 
    // 4个直辖市
    '北京', '天津', '上海', '重庆',
    // 2个特别行政区
    '香港', '澳门'
];	
///////////////////////////////////////////////////////地图跳转优先级优于地图缩放优于地图缩放前面
///////////////////////////////////////////////////////地图跳转start
<#if openUrl ??>
	function endwith(wholeStr,str){     
	  var reg=new RegExp(str+"$");     
	  return reg.test(wholeStr);        
	}
	//页面跳转公用函数
	function openURL(mt){
		var openUrl = "${openUrl}";
		
		openUrl = openUrl.replace('%40LOCATION%40', mt);
		if(option.series!=null&&option.series[0]!=null&&option.series[0].mapType!=null){
		 	 var parentMT  = option.series[0].mapType;
		  	 openUrl = openUrl.replace('%40PARENTLOCATION%40', parentMT);
		}
		//得到选择的序列
		var seriesValue ="";
		getDefaultLegend();
		if(gSelectedLegendArray!=null){
		 for(var i=0;i<gSelectedLegendArray.length;i++){
		 	seriesValue = seriesValue + gSelectedLegendArray[i] + ",";
		 }
		 if(endwith(seriesValue,",")){
		 	seriesValue = seriesValue.substring(0,seriesValue.length-1);
		 }
		} 
		openUrl = openUrl.replace('%40SERIES%40', seriesValue);
		var openUrlType ='${openUrlType}';
		<#if openUrlType=='_blank'>
			window.open(openUrl);
		<#elseif openUrlType=='_self'>
			document.location=openUrl;
		<#elseif openUrlType=='_dialog'>	
			var popupActionDialog = new PopupDialog(openUrl);
	    	popupActionDialog.init();
		</#if>
	}
</#if>
<#if openUrlFlag = 'true' >
   //全国地图模式下地图下转
	myChart.on(ecConfig.EVENT.MAP_SELECTED, function (param){
	    var len = mapType.length;
		var mt ;
        var selected = param.selected;
        for (var i in selected) {
            if (selected[i]) {
                mt = i;            
                break;
            }
        }    	
		openURL(mt);
	});
///////////////////////////////////////////////////////地图缩放start
<#elseif showProvince = 'true' >
myChart.on(echarts.config.EVENT.LEGEND_SELECTED,storeSelected);//用于省级页面跳转
document.getElementById(mapDivName).onmousewheel = function (e){
    var event = e || window.event;
    curIndx += zrEvent.getDelta(event) > 0 ? (-1) : 1;
    if (curIndx < 0) {
        curIndx = mapType.length - 1;
    }
    var mt = mapType[curIndx % mapType.length];
    if (mt == 'china') {
        //option.tooltip.formatter = '滚轮切换或点击进入该省<br/>{b}';
    }
    else{
        //option.tooltip.formatter = '滚轮切换省份或点击返回全国<br/>{b}';
    }
    for (var i=0;i<option.series.length;i++){
        option.series[i].mapType = mt;
    }
    option.title.subtext = mt + ' （滚轮或点击切换）';
    myChart.setOption(option, true);
    
    zrEvent.stop(event);
};

var curCountryData;//= [];
myChart.on(ecConfig.EVENT.MAP_SELECTED, function (param){
    var len = mapType.length;
    var mt = mapType[curIndx % len];
    var selected = param.selected;
    if (mt == 'china') {
        // 全国选择时指定到选中的省份
        for (var i in selected) {
            if (selected[i]) {
                mt = i;
                refreshReturnParentHref(true);
                while (len--) {                	
                    if (mapType[len] == mt) {
                        curIndx = len;
                    }
                }
                //console.log("地图切换前:");
                //console.log(option.series[0].mapType);
                //数据切换
                var legendIndex = getMarkPointIndex();
                curCountryData = option.series[legendIndex].markPoint.data;//保存数据供切换回全国地图使用
                option.series[legendIndex].markPoint.data = [];
                
                /*
                for(var i=0;i<option.series.length;i++){
                	curCountryData[i]=option.series[i].markPoint.data;
                	option.series[i].markPoint.data = [];
                	option.series[i].geoCoord = [];
                }*/                
                
                
                break;
            }
        }
        //option.tooltip.formatter = '滚轮切换省份或点击返回全国<br/>{b}';
    }
    else {
    		//判定二级省市点击的时候地图是否需要跳转页面
    		var openUrlProvinceFlag = ${openUrlProvinceFlag};
    		if(openUrlProvinceFlag){
    			for (var i in selected) {
	            if (selected[i]) {
	                mt = i;
	             }
             }   
    			openURL(mt);
    		}
        //curIndx = 0;
        //mt = 'china';
        return false;
        /*
        for(var i=0;i<option.series.length;i++){
            	option.series[i].markPoint.data = curCountryData[i];   
            	option.series[i].geoCoord = l_geoCoord;         	
        }*/
        var legendIndex = getMarkPointIndex();
        option.series[legendIndex].markPoint.data = curCountryData;
        
        //option.tooltip.formatter = '滚轮切换或点击进入该省<br/>{b}';
    }
    
    for (var i=0;i<option.series.length;i++){
        option.series[i].mapType = mt;
    }
    
    //console.log("地图切换后:");
    //console.log(option.series[0].mapType);
    //option.title.subtext = mt + ' （滚轮或点击切换）';
    //console.log(option.legend.getSelectedMap());
    
    //地图切换后，切换前选择的序列和切换后选择的序列可能因为客户操作变更了，但是markpoint未做刷新
    //所以为了保持一致，切换后重新计算markpoint
    //需要切换前后的序列数据要保持刷新一致，则定义个全局变量数组g_series_seleted，存放用户选择的序列，然后调用在if判断之间加入refreshMarkPoint(g_series_seleted)生成markpoint，
    //并且修改refreshMarkPoint的逻辑，当用户选择变更时，对全局变量数组进行写操作，保持数据一致
    //写操作：g_series_seleted=param
    <#if markPointShowOrNot='true' && areaShow='false'>
    	refreshMarkPoint(g_series_seleted);//其中已带有myChart.setOption(option, true);操作，所以用if判断
    </#if>	
    <#if markPointShowOrNot='false' || areaShow='true'>
    	myChart.setOption(option, true);//true后，原有markpoint都失效
    </#if>
});
///////////////////////////////////////////////////////地图缩放end

///////////////////////////////////////////////////////地图跳转end
///////////////////////////////////////////////////////混搭地图start
<#elseif showSubDetail = 'true' >
	myChart.on(ecConfig.EVENT.MAP_SELECTED,refreshPie);
	myChart.on(echarts.config.EVENT.LEGEND_SELECTED,refreshPieFromLegend);
///////////////////////////////////////////////////////混搭地图end
<#else>
   
</#if>

    var option = {
    title : {
        text: '${mapLabel}',
        subtext: '${mapSubLabel}',
        x:'center'
    },
    tooltip : {
        trigger: 'item'
        <#if tooltipShowDetail='false'>
        ,formatter:''
        /*,formatter: function (params) { 
        	var res = params[0];
        	return res;
        }*/
        </#if>
        <#if tooltipShowDetail='true'>
        //,formatter: "{b}<br/>{c}<br>{a}"
        ,formatter: function (params,ticket,callback) {
            var res = getRegionLabel(params[1])+"<br/>"+params[2]+"<br>"+params[0];
            return res;
        }
        </#if>
    },
    <#if showLegend = 'true'>
    legend: {
        orient: 'vertical',
        x:'left',
        //data:['iphone3','iphone4','iphone5']
        <#if MultiShowSeries='false'>
        selectedMode : 'single',        
        </#if>
        <#if allSeriesLabelsList?size !=0 >
        selected : {0:false
        	<#list allSeriesLabelsList as allSeriesLabel>
        		<#if allSeriesLabel_index=0>
        		,${allSeriesLabel}:true
        		</#if>
        		<#if allSeriesLabel_index!=0>
        		,${allSeriesLabel}:false
        		</#if>
        	</#list>
        },	
        </#if>
        data: ${allSeriesLabels}
    },
    </#if>
    hiddenLegend: {//用于 showLegend=false ,legend不显示的情况
        orient: 'vertical',
        x:'left',
        <#if MultiShowSeries='false'>
        selectedMode : 'single',        
        </#if>
        <#if allSeriesLabelsList?size !=0 >
        selected : {0:false
        	<#list allSeriesLabelsList as allSeriesLabel>
        		,${allSeriesLabel}:true
        	</#list>
        },	
        </#if>
        data: ${allSeriesLabels}
    },
    <#if showDataRange = 'true'>
    dataRange:dataRangeStyle[1],
    </#if>
    /*
    dataRange: {
        min: 0,
        max: 2500,
        x: 'left',
        y: 'bottom',
        text:['高','低'],           // 文本，默认为数值文本
        calculable : true
    },*/
    <#if showToolBox='true'>
    toolbox: {
        show: true,
        orient : 'vertical',
        x: 'right',
        y: 'center',
        feature : {
            mark : {show: true},
            dataView : {show: true, readOnly: false},
            restore : {show: true},
            saveAsImage : {show: true}
        }
    },
    roamController: {
        show: true,
        x: 'right',
        mapTypeControl: {
            'china': true
        }
    },
   </#if> 
   series : [
   	<#if dataVar ??> 	
		<#list dataVar as map>
        {
	            name: '${map['seriesLabel']}',
	            type: 'map',
	            mapType: 'china',
	            selectedMode : 'single',
	            itemStyle:{
	                normal:{
	                	label:{
	                		show:true,
	                		textStyle: {
                            color: '${fontColor}'
                        	}
	                	}
	                	<#if backGroundColor != ''>,color:'${backGroundColor}'</#if>
	                },
	                emphasis:{
	                	label:{
	                		show:true,
	                		textStyle: {
                            color: '${fontColor}'
                        	}
	                	}	                	
	                }
	            },
	            data:${map['data']}
	            /*,
	            markPoint : {
                    itemStyle : {
                        normal:{
                            color:'${markPointColor}'
                        }
                    },
                    data : [
                    ]
                },
                geoCoord: {}*/
        },
        </#list>
     </#if>   
        {name: '',
                type: 'map',
                mapType: 'china',
                selectedMode : 'single',
                itemStyle:{
                    normal:{label:{show:false}},
                    emphasis:{label:{show:false}}
                },
                type: 'map',
                mapType: 'china',
                selectedMode : 'single',
                itemStyle:{
                    normal:{label:{show:false}},
                    emphasis:{label:{show:false}}
                },
                data:[],
                markPoint : {
                    itemStyle : {
                        normal:{
                            color:'${markPointColor}'
                        }
                    },                    
                    data : [] 
                },
                geoCoord: l_geoCoord
       }
       <#if showSubDetail = 'true' && areaShow = 'true'>
       ,
       {
            name:'大区内数据对比',
            type:'pie',
            roseType : 'area',
            tooltip: {
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c} ({d}%)"
            },
            center: [document.getElementById(mapDivName).offsetWidth - ${vPos}, document.getElementById(mapDivName).offsetHeight - ${hPos}],
            radius: [${inRadius}, ${outRadius}],
            data:[              
            ]
        }
        </#if>
    ]
};
    
    
    ${markPoint};
    
    myChart.setOption(option);
    myChart.restore();
    
    
    //省市到大区转换
    function getRegionLabel(location){
    	<#if cityToRegion ??>	
	    var regionDefine = ${cityToRegion};
	    var returnLabel = location;
	    for(var i=0;i<regionDefine.length;i++){
	        if(regionDefine[i][location]!=undefined){
	            returnLabel = regionDefine[i][location];
	        }
	    }
    	return returnLabel;
    	</#if>
    	//无大区定义直接返回原字符串
    	return location;
    };	
	
	function getMarkPointIndex(){
		var result = -1;
		for(var i=0;i<option.series.length;i++){        	
        	if(option.series[i].type!='map'){        		
        		break;
        	}
        	result = i ;
        }
        return result;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	//大区混搭图js start
	function getPieIndex(){
        var result = -1;
        for(var i=0;i<option.series.length;i++){            
            if(option.series[i].type!='map'){  
                result = i ;             
                break;
            }            
        }
        return result;
    }

    var gSelectedProvince;
    function refreshPie(param){
        getSelectdMap(param);
        var data  = getPieDataCore(gSelectedProvince)
        //console.log(getPieIndex());    
        option.series[getPieIndex()].data = data;  
        myChart.setOption(option);  
    }

    var gSelectedLegendArray;
    function getSelectedLegend(param){
        //得到选择的序列
        var legendSelected = param.selected;
        gSelectedLegendArray  = new Array();
        var selectedSeriesStep = 0;
        for(var i=0;i<option.legend.data.length;i++)
        {            
            var label = option.legend.data[i];
            if(legendSelected[label]){
                legendIndex =  i;
                
                gSelectedLegendArray[selectedSeriesStep] = label;
                selectedSeriesStep++;
            }
        }
        return gSelectedLegendArray;
    }

	function storeSelected(param){
		getSelectedLegend(param);
	}
	
    function refreshPieFromLegend(param){
       getSelectedLegend(param);
       var data =  getPieDataCore(param);   
       option.series[getPieIndex()].data = data;  
       myChart.setOption(option);       
    }

    function getSelectdMap(param){
        //得到省市对应的大区
        var len = mapType.length;
        var mt ;
        var selected = param.selected;
        for (var i in selected) {
            if (selected[i]) {
                mt = i;            
                break;
            }
        } 
        gSelectedProvince = mt;
        return mt;
    }

   function getDefaultLegend(){
   		 if(gSelectedLegendArray==undefined&&option.legend!=null&&option.legend.data!=null&&option.legend.data.length>0){
            gSelectedLegendArray = new Array();
            gSelectedLegendArray[0] = option.legend.data[0];
        }
   }
    function getPieDataCore(){
        //为选择省市，直接返回
        if(gSelectedProvince==undefined){return false;}
        //默认选择是第一个
       getDefaultLegend();
       
        console.log(gSelectedProvince);
        console.log(gSelectedLegendArray);
        var detailData = [];
        <#if areaProvinceData ??>        
            detailData = ${areaProvinceData};
        </#if>
        var data  = [] ;
        
        var region = getRegionLabel(gSelectedProvince);
        //每个序列扫描
        console.log("gSelectedLegendArray.length--->"+gSelectedLegendArray.length);
        for(var j=0;j<gSelectedLegendArray.length;j++){
            //扫描原始数据
            for(var i=0;i<detailData.length;i++){
                var cityData = detailData[i];
                //同一个大区下
                if(region==getRegionLabel(cityData.name) && gSelectedLegendArray[j]==cityData.series){
                    var found = false;
                    //确认原来是否已有改城市数据
                    for(var k=0;k<data.length;k++){
                        var element = data[k];
                        console.log("element:"+element.name);
                        if (element.name == cityData.name){
                            console.log("找到相等:"+element.name);
                            element.value =  element.value + parseFloat(cityData.value);
                            found = true;
                        }
                    }                     
                    if(!found){
                        var newElement  = new Object() ;
                        newElement["name"]=cityData.name;
                        newElement["value"]=parseFloat(cityData.value);
                        data.push(newElement);
                    }
                }

            }
            console.log("data:"+data);
        }
        return data;
    }
    //大区混搭图js end
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////////////////////////
	//大区返回按钮相关script
	function refreshReturnParentHref(show){
	    if(show){
	        $("#returnToParentMap").show();
	    }else{
	        $("#returnToParentMap").hide();
	    }
	}

	function returnToCountry(){
		curIndx=0;
	    for (var i=0;i<option.series.length;i++){
	        option.series[i].mapType = 'china';
	    }	  
	    myChart.setOption(option, true);
	    refreshReturnParentHref(false);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
</script>