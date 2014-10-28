//////////////////////////////////////////////////////序列选择后刷新地图上的数字Start
//要先配置是否显示数字
//console.log(${markPointShowOrNot});
//配置显示大区，则markpoint放哪里不好放，故大区的时候不显示markpoint
<#if markPointShowOrNot='true' && areaShow='false' >
myChart.on(echarts.config.EVENT.LEGEND_SELECTED,refreshMarkPoint);
var curLegend; //用于标注markpoint用
function refreshMarkPoint(param){
		var curOption;		
        var curSelected;
        curOption = option;
        if(param==null){
            //页面初始化显示逻辑
            //p_option = myChart.option;
            <#if showLegend = 'true'>
            	curSelected= curOption.legend.selected; //显示序列，则正常取值
            	curLegend = curOption.legend;
            </#if>
            <#if showLegend = 'false'>
            	curSelected= curOption.hiddenLegend.selected;//不显示，则从隐藏值取，默认全选
            	curLegend = curOption.hiddenLegend;
            </#if>
        }else{
            curSelected = param.selected;
            g_series_seleted = param;
        }
        refreshMarkPointCore(curSelected,curOption);
        
        myChart.setOption(curOption, true);//刷新地图
}

function refreshMarkPointCore(curSelected,curOption){       
         
        //alert(curLegend.data.length);
        //得到选择的序列清单
        var debug="";
        var legendIndex = 0 ;//用那个序列显示markPoint:最后一个选择序列
        var selectedSeriesArray  = new Array();
        var selectedSeriesStep = 0;
        for(var i=0;i<curLegend.data.length;i++)
        {            
            var label = curLegend.data[i];
            if(curSelected[label]){
                legendIndex =  i;
                debug = debug + label;
                selectedSeriesArray[selectedSeriesStep] = label;
                selectedSeriesStep++;
            }
        }
        //计算选择序列的总和用于标注显示
        var datas=[];
        
        //alert(datas.length);
        for(var i=0;i<selectedSeriesArray.length;i++){ //选择序列遍历
            var selectedSeriesName =  selectedSeriesArray[i];
            //console.log("选择序列:"+selectedSeriesName);
            
            for(var j=0;j<curOption.series.length;j++){//现有序列遍历
                //找到相等的进行计算放入新的数组中
                var curSeries = curOption.series[j];
                var curSeriesName = curSeries.name;
                if(selectedSeriesName == curSeriesName ){//如冰箱==冰箱
                    var newDatas = [];//存放新结果的数组
                    //console.log("第"+i+"个产品"+selectedSeriesName+"找到序列:"+curSeriesName+" 长度"+curSeries.data.length);

                    for(var k=0;k<curSeries.data.length;k++){//如冰箱序列(地名,值)
                        //得到序列下的一维数组（地名，值）数据
                        curSeriesData =  curSeries.data[k];
                         
                        var curSeriesData_CityName = curSeriesData["name"];
                        var curSeriesData_CityValue = curSeriesData["value"]
                        //console.log("第"+i+"个产品"+selectedSeriesName+"找到序列:"+curSeriesName+" 长度"+curSeries.data.length+"--"+curSeriesData_CityName+":"+curSeriesData_CityValue);
                        var l;
                        var newLocationDatas = new Object() ;
                        //console.log("datas.length"+datas.length);
                        newLocationDatas["name"] = curSeriesData_CityName;
                        var found = false;
                        for(l=0;l<datas.length;l++){//遍历datas，查找是否有相同城市的数据，有则累计，无则新增
                            var cityUnit = datas[l];
                            var cityName = cityUnit["name"];
                            //console.log("-----和---"+cityName+"-----比较");
                            if(cityName == curSeriesData_CityName ){//有则和老值相加
                                //console.log("已有数据"+cityName+"值"+curSeriesData_CityValue);
                                newLocationDatas["value"]=cityUnit["value"]+curSeriesData_CityValue;
                                found=true;
                                //console.log("已有数据"+cityName+"值"+curSeriesData_CityValue);
                            }
                        }
                        if(!found){
                            //无则新增
                            newLocationDatas["value"]  = curSeriesData_CityValue;
                        }
                        //判断数据的地区是否有坐标，无坐标则不显示
                        /*
                        for(var v_location in l_geoCoord){
                        	//console.log(v_location);
                        	if(value(v_location) == newLocationDatas["name"]){
                        		newDatas.push(newLocationDatas);//加入数组
                        	}
                        }*/
                        addToData(newLocationDatas,newDatas,curOption);
                      
                        
                        /*
                        if(datas.length==0){
                            //没有数据则建立合计数组
                            //TODO NOTHING
                        }else{
                            //有数据则累加
                            //取出原有数据后累加
                            var tmp = datas[k];
                            var sum = datas[k].value;
                            locationDatas["value"]  = sum+ curSeriesData["value"];
                        }
                        datas.push(locationDatas);*/
                    }//end for k
                    
                    //对于datas数组中找到未出现在newDatas的元素，补入newDatas中
                    for (var m=0;m<datas.length;m++){
                        var found = false;
                        for(var n=0;n<newDatas.length;n++){
                            if(datas[m]["name"]==newDatas[n]["name"]){
                                found =true;
                            }
                        }//end n
                        //未找到
                        if(!found){
                            //newDatas.push(datas[m]);
                            addToData(datas[m],newDatas,curOption);
                        }
                    }//end m
                    
                    datas = newDatas;
                }//end if


            }//end for j
        }//end for i
        
        //alert(debug);
        //var geoCoord = curOption.series[0].geoCoord;//此行用模板的话可以不用
        /*
        for(var i=0;i<curOption.series.length;i++){
             //console.log( curOption.series[i]["name"]);
             curOption.series[i].markPoint.data=[];
             curOption.series[i].geoCoord = [];
        }*/
    
        //console.log(datas);
        //var dataold = curOption.series[0].markPoint.data;
        legendIndex=curOption.series.length-1;
        curOption.series[legendIndex].markPoint.data=datas; //地图显示数据赋值
        curOption.series[legendIndex].geoCoord = l_geoCoord;//地图坐标赋值
       
        //if(!firstTimeloadingPageFlag){
        	//进入页面同时调用myChart.setOption两次，可能导致地图的数据不一致，最终会导致地图下转出错，
        	//所以第一次进入不执行以下语句
        	
        //}
        
};

//将markpoint坐标加入json数组供页面显示用
//newLocationDatas json对象
//newDatas json数组存放json对象
//有bug，如果是地图模式在二级地图上，此方法也会加入一级省市的数据并在地图上显示
//TODO需要根据地图类型及地图目前的地区名，来加载地区内的geoCoord，并根据此geoCoord过滤需要加载的markpoint
function addToData(newLocationDatas,newDatas,curOption){
	//console.log(newLocationDatas["name"]);
	if(curOption.series[0]["mapType"]!='china'){
			//目前无二级县市坐标信息，所以不是china的话自动返回
			return false;
	}
	for(var v_location in l_geoCoord){//判断是否有坐标，无坐标则不显示markpoint		
		if((v_location) == newLocationDatas["name"]){
    	//if(value(v_location) == newLocationDatas["name"]){
    		//console.log(newLocationDatas["name"]+"====="+v_location);
    		newDatas.push(newLocationDatas);//加入数组
    	}
    }
}

//页面加载时自动标注数字
//var firstTimeloadingPageFlag = true;//是否第一次进入页面
refreshMarkPoint();//页面加载时，初始化显示
//firstTimeloadingPageFlag = false;
</#if>

//////////////////////////////////////////////////////序列选择后刷新地图上的数字End