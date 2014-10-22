大区实现思路：
1.配置
  		a 数据来源定义
  		      大区 	地名						sql--建议单独抽出来，后台拼写sql
  		    东北	辽宁，吉林，黑龙江	select sum(),product from table where location in('辽宁','吉林','黑龙江') group by product
  		  长三角    上海，江苏，浙江 		select sum(),product from table where location in('辽宁','吉林','黑龙江') group by product
  		b 数值列名
  		c 地区列名
  		d 序列列名
  		e sql-----通用
  		   select sum(),product from table
  		   where  location in('辽宁','吉林','黑龙江')//此条件根据各个大区定义动态拼写，java字符串转数组，数组内补再转字符串
  		   group by product
  		f 显示大区还是省市 ---若两个都定义了，优先选择大区
  		
 2.生成data[]
     Java实现
     	a.根据1.中的sql，循环查询得到各个大区的各个序列的数据（考虑用union拼写后查询，方便后面4中使用）
     	b.将各个大区的数据，根据大区定义，扩充转换为省市数据 
     	
3.编写formatter显示逻辑
     JS+模板实现
     	由是否显示大区还是省市数据配置决定
     	如果是大区，则根据省市找到大区替换省市文字---替换格式：上海--长三角；辽宁--东北
     	定义数组 [{'上海','长三角'},{'辽宁','东北'}]，用于定位大区替换文字
     	
4.编写底部label table展示大区统计数据
		序列		大区		数据
	 java+sql+模板实现
	    	各个大区sql union并且排序即可
	    	
-------------------------------------------------------
测试配置
西北区 新疆,内蒙古
西南区 西藏,四川

product
quantity
location
select product ,sum(price) quantity from oe_order_all 
group by product;	    	