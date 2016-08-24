# MySQLDao
一个轻量级的Java MySQL增删改查框架，部署简单，可以非常方便快速的通过反射进行mysql数据和JAVA Bean之间的转换，特别适合小型数据的操作场合，可以用于简单的数据库数据分析，我常常用它做小型手机应用的后端服务，快速搭建用于测试的小型服务器，免受Hibernate等框架复杂的环境配置之苦

1、支持单主键，双主键表增删改查功能 

2、支持五花八门各种各样的查询方式 

3、支持自动生成model层Java文件 

4、支持自动生成数据库配置文件 

5、使用c3p0连接池

6、查询结果可以以JSON格式输出

7、支持tomcat服务器

例如有这样一个数据库表格：

![image](https://github.com/AlexZhuo/MySQLDao/blob/master/tutorial/6.PNG)

	
		//插入操作
		//因为是自增主键所以student_id传null
		Student_info stu1 = new Student_info(null, "小明", 1);
		Student_info stu2 = new Student_info(null, "小李", 1);
		Student_info stu3 = new Student_info(null, "小王", 2);
		Student_info stu4 = new Student_info(null, "小刘", 2);
		TableDao stuDao = new TableDao(stu1);
		stuDao.insert(stu1,stu2,stu3,stu4);
		
		//查询操作
		//查询全部一班的同学
		List<Student_info> studentClassOne = stuDao.select(new QueryBean(Student_info.CLASS_ID,1));
		System.out.println(studentClassOne);
		//返回结果为json：[{student_id:'1',student_name:'小明',class_id:'1'}, {student_id:'2',student_name:'小李',class_id:'1'}]
		
		//修改操作
		//修改id为1的学生班级为2
		//注意只能通过主键寻找要修改的行，某参数传null代表不修改该列
		stuDao.update(new Student_info(1, null, 2));
		List<Student_info> result = stuDao.select(new QueryBean(Student_info.CLASS_ID,2));
		System.out.println(result);
		//结果2班多了一个人[{student_id:'1',student_name:'小明',class_id:'2'}, {student_id:'3',student_name:'小王',class_id:'2'}, {student_id:'4',student_name:'小刘',class_id:'2'}]

		//删除操作
		//删除名字叫小明的同学
		//首先搜索小明同学的id
		List<Integer> deleteIds = (List)stuDao.selectIds(new QueryBean(Student_info.STUDENT_NAME, "小明"));
		//然后通过id进行删除
		if(deleteIds.size()>0)stuDao.Delete(deleteIds.get(0));
		//如果成功返回delete()函数返回true
	
除了上面列举的简单功能，还有很多实用的api，如果想了解可以加我qq：382226007

环境配置：
	
首先在JAVA环境中执行src/start/Init.java里面的主函数
	
或者如果是导入jar包，那么随便建立一个主函数执行StartWith.Initialize();这个方法
	
运行后首先按1键，在src目录下生成pwd.properties数据库配置文件
	
这里需要输入数据库用户名，密码，库名等等
	
![image](https://github.com/AlexZhuo/MySQLDao/blob/master/tutorial/2.PNG)
	
然后再src目录下点F5刷新，就能看到生成好的配置文件，大约是这个样子：
	
![image](https://github.com/AlexZhuo/MySQLDao/blob/master/tutorial/1.PNG)
	
其中标黄色的部分是数据库名
	
然后需要将本项目/libs目录下的三个jar包分别add to build path，以使用JDBC和c3p0
	
然后就可以让计算机自动生成java bean文件了，免去了手写之苦
	
还是执行刚才的主函数，点2键，在列表里选择相应的数据库表，然后选择相应的主键列，最后生成一个.java文件，默认是src/sql/alex/model目录下，生成完毕后需要在src目录下点F5刷新
	
![image](https://github.com/AlexZhuo/MySQLDao/blob/master/tutorial/5.PNG)

程序会自动的根据mysql中定义的列属性为每一个成员设定数据类型，支持多数的mysql类型，可以在控制台中观察每个列的数据类型映射，正常情况下，生成的java bean文件应该是没有报错的

![image](https://github.com/AlexZhuo/MySQLDao/blob/master/tutorial/4.PNG)
	
现在你就可以向上面的代码一样进行增删改查操作了

提示：如果需要进行多表联查的操作，我一般都是建View视图再配合上面的查询代码进行查询


	
	
