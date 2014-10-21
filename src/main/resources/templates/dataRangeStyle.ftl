////值域选择----分割段数start
    var v_min = ${dataRangeMin};
    var v_max = ${dataRangeMax};
    dataRangeStyle = [
// 0
    {
    	splitNumber: 10,
        min: v_min,
        max: v_max,
        formatter : function(v, v2){
            if (v2 < 1000) { return '低于' + v2}
            else if (v >= 1000) { return '高于' + v}
            else { return '中' }
        }
    },
// 1
    {
    	splitNumber: ${splitNumber},
        orient: 'vertical', // 'horizontal'       
        color:['${maxValueColor}','${minValueColor}'],
        <#if splitNumber = "0">
        text:['高','低'],
        </#if>
        min: v_min,
        max: v_max,
        formatter : '{value} 到 {value2}'
    }
]
////值域选择----分割段数end