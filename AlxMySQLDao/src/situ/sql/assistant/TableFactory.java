package situ.sql.assistant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class TableFactory {
	public static String packageName = "sql.alex.model";
	public static String GenerateByName(String tableName,String...primaryKeyNames) throws Exception{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet =null;
		StringBuffer stringBuffer = new StringBuffer();//用于生成成员变量和getset方法
		StringBuffer constructorBuffer = new StringBuffer();//用于生成构造函数
		StringBuffer toStringBuffer = new StringBuffer();
		StringBuffer subConstructor = new StringBuffer();
		//初始化成员头部
		String leiName = tableName.substring(0,1).toUpperCase()+tableName.substring(1);//类名首字母大写
		//获得包路径
		String rootFold = TableFactory.class.getResource("/").getFile()+packageName.replace(".", "/")+"/";
	    System.out.println("rootFold"+rootFold);
		stringBuffer.append("package "+packageName+";\n");//把包路径最后一个逗号删掉，然后把所有斜线换成点号
		stringBuffer.append("import situ.sql.assistant.Table;\n");
		stringBuffer.append("import java.sql.Date;\n");
		stringBuffer.append("import java.sql.Time;\n");
		stringBuffer.append("import java.sql.Timestamp;\n");
		stringBuffer.append("import java.text.ParseException;\n");
		stringBuffer.append("import java.text.SimpleDateFormat;\n");
		stringBuffer.append("import java.math.BigDecimal;\n");
		stringBuffer.append("public class "+ leiName+" implements Table{\n");
		//初始化构造函数
		constructorBuffer.append("public "+leiName+"(");
		subConstructor.append("{\n super();\n");
		//初始化toString方法
		toStringBuffer.append("public String toString(){\n return \"{\"+");
		
		try {
		//获得列名的map，integer从1开始，这个是为了view表防止列名重复准备的，对普通表没有什么卵用
		String sql = "SELECT * FROM "+tableName+" LIMIT 1";
		connection = AddConnection.getConnection();
		System.out.println("sql语句为"+sql);
		preparedStatement = connection.prepareStatement(sql);//放入sql语句准备执行
		resultSet = preparedStatement.executeQuery();//执行sql语句，并获得结果集
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();//获得结果集的底层数据库信息
		int columns = resultSetMetaData.getColumnCount();//获得结果集的列数
        resultSet.first();
        String columnName = null;
        String columnClass = null;
        for (int i = 0; i < columns; i++) {//列遍历,添加成员变量和get,set方法
        	   columnName = resultSetMetaData.getColumnLabel(i+1).toLowerCase(); //getColumnLabel得到是as后的列名，getColumnName是数据库本来的列名
	    	   columnClass = resultSetMetaData.getColumnClassName(i+1);
	    	   System.out.println("列名"+columnName+" 数据类型"+columnClass);
	    	   	//写入成员变量,由于mysql对列名不区分大小写，就全部改为小写，防止前端人员写错
	    	   
	    	   	stringBuffer.append("private "+columnClass+" "+columnName.toLowerCase()+";\n");//写入成员变量
	    	   	stringBuffer.append("public static String "+columnName.toUpperCase()+" = \""+columnName.toLowerCase()+"\";\n");//获取该列名的静态字符串，用于查询的时候写列名
	    	   	//将列名首字母大写
	    	   	String UpcolumnName = columnName.substring(0, 1).toUpperCase()+columnName.substring(1);
	    	   	//添加get方法
	    	   	stringBuffer.append("public "+columnClass+" get"+UpcolumnName+"(){\n");
	    	   	stringBuffer.append("return "+columnName+";\n}\n");
	    	   	//添加set方法
	    	   	stringBuffer.append("public void set"+UpcolumnName+"("+columnClass+" "+columnName+"){\n");
	    	   	stringBuffer.append("this."+columnName+"="+columnName+";\n");
	    	   	stringBuffer.append("\n}\n");
	    	  //添加set String方法
	    	   	if(!columnClass.equals("java.lang.String")){//如果不是字符串类型
	    	   		stringBuffer.append("public void set"+UpcolumnName+"(java.lang.String "+columnName+"){\n");
	    	   		stringBuffer.append("if("+columnName+"==null||"+columnName+".length()==0){System.out.println(\"YOUR INPUT IS NULL\");return;}\n");
	    	   		//date对象
	    	   		if(columnClass.equals("java.sql.Date")){
	    	   			stringBuffer.append("SimpleDateFormat simpleDateFormat = new SimpleDateFormat(\"yyyy-MM-dd\");\n try {\n");
	    	   			stringBuffer.append("this."+columnName+"= new Date(simpleDateFormat.parse("+columnName+").getTime());\n");
	    	   			stringBuffer.append("\n} catch (ParseException e) {\n e.printStackTrace();\n}");
	    	   		}else if(columnClass.equals("java.sql.Time")){
	    	   			stringBuffer.append("SimpleDateFormat simpleDateFormat = new SimpleDateFormat(\"HH:mm:ss\");\n try {\n");
	    	   			stringBuffer.append("this."+columnName+" = new Time(simpleDateFormat.parse("+columnName+").getTime());\n");
	    	   			stringBuffer.append("\n} catch (ParseException e) {\n e.printStackTrace();\n}");
	    	   		}else if(columnClass.equals("java.sql.Timestamp")){
	    	   			stringBuffer.append("SimpleDateFormat simpleDateFormat = new SimpleDateFormat(\"yyyy-MM-dd HH:mm:ss\");\n try {\n");
	    	   			stringBuffer.append("this."+columnName+" = new Timestamp(simpleDateFormat.parse("+columnName+").getTime());\n");
	    	   			stringBuffer.append("\n} catch (ParseException e) {\n e.printStackTrace();\n}");
	    	   			
	    	   		//整形数字
	    	   		}else if(columnClass.equals("java.lang.Integer")){
	    	   			stringBuffer.append("this."+columnName+"=Integer.parseInt("+columnName+");\n");
	    	   		//小数
	    	   		}else if(columnClass.equals("java.lang.Double")){
	    	   			stringBuffer.append("this."+columnName+"=Double.parseDouble("+columnName+");\n");
	    	   		}else if(columnClass.equals("java.lang.Long")){
	    	   			stringBuffer.append("this."+columnName+"=Long.parseLong("+columnName+");\n");
	    	   		}else if(columnClass.equals("java.lang.Float")){
	    	   			stringBuffer.append("this."+columnName+"=Float.parseFloat("+columnName+");\n");
	    	   		}else if(columnClass.equals("java.math.BigDecimal")){
	    	   			stringBuffer.append("this."+columnName+"=new BigDecimal("+columnName+");\n");
	    	   		}else{
	    	   			System.out.println("没找到改类型写入方法");
	    	   		}
	    	   		stringBuffer.append("\n}\n");
	    	   	};
	    	   	//添加构造函数
	    	   	constructorBuffer.append(columnClass+" "+columnName+" ,");
	    	   	subConstructor.append("this."+columnName+" = "+columnName+";\n");
	    	   	//添加toString方法
	    	   	toStringBuffer.append("\""+columnName+":\"+ ("+columnName+"==null?\"null\":\"'\"+"+columnName+"+\"'\")+\",\"+");
        }
        //消除构造方法最后的逗号
        StringBuffer constructBuffer2 = new StringBuffer();
        constructBuffer2.append(constructorBuffer.substring(0, constructorBuffer.length()-1));
        constructBuffer2.append(")");
        //将构造方法合成
        constructBuffer2.append(subConstructor);
        constructBuffer2.append("}\n");
        //添加无参构造
        constructBuffer2.append("public "+leiName+"(){}\n");
        //消除tosting最后的加号
        String itoString = toStringBuffer.substring(0, toStringBuffer.length()-5);
        itoString+="+\"}\"";
        itoString+=" ;\n}\n";
      //添加实现接口的方法
        stringBuffer.append("public String giveTableName() {\n return ");
        stringBuffer.append("\""+tableName+"\";\n}\n");
        //普通表
        if(primaryKeyNames!=null&&primaryKeyNames.length>0){
        	stringBuffer.append("public String givePrimaryColumnName() {\n return ");
        	stringBuffer.append("\""+primaryKeyNames[0]+"\";\n}\n");
	        if(primaryKeyNames.length==2){//如果是两个复合主键
	        	stringBuffer.append("public String[] giveDualPrimaryName() {\n return new String[]{");
	            stringBuffer.append("\""+primaryKeyNames[0]+"\",\""+primaryKeyNames[1]+"\"};\n}\n");
	        }else{//如果没有两个复合主键，由于接口要求，也得写
	        	stringBuffer.append("public String[] giveDualPrimaryName() {\n return ");
	        	stringBuffer.append("null;\n}\n");
	        }
        }else {//如果3个以上主键或不写主键
        	stringBuffer.append("public String givePrimaryColumnName() {\n return ");
        	stringBuffer.append("null;\n}\n");
        	stringBuffer.append("public String[] giveDualPrimaryName() {\n return ");
        	stringBuffer.append("null;\n}\n");
		}
        //全部合成
        stringBuffer.append(constructBuffer2).append(itoString);
        stringBuffer.append("}\n");
        
        String output =rootFold;
        System.out.println("output:"+output);
        
       
        try {
        	File file = null;
        	output = URLDecoder.decode(output, "utf-8");
        	//把rootfold去掉最后面的斜线并且替换classes目录为源文件目录,添加对tomcat的源路径支持
			file = new File(output.substring(1).replace("/bin/", "/src/").replace("/WebRoot/WEB-INF/classes/", "/src/")+leiName+".java");
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			System.out.println("正在写入:"+file);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] data = stringBuffer.toString().getBytes("UTF-8");
			fos.write(data);
			fos.close();
			System.out.println("类文件已成功写入到:"+file+"请右键刷新。"+leiName+"现在已可用");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			System.out.println("数据库连接失败，请检查用户名密码或配置文件");
			e1.printStackTrace();
			return null;
		}
		return stringBuffer.toString();
	}

	public static void writeProperties(String dateBaseName,String user,String password) throws Exception {
		String rootFold = TableFactory.class.getResource("/").getFile();
		rootFold = rootFold.substring(1).replace("/bin/", "/src/").replace("/WebRoot/WEB-INF/classes/", "/src/");
		rootFold = URLDecoder.decode(rootFold, "utf-8");
		File file = new File(rootFold+"pwd.properties");
		System.out.println(file);
		
		
		///保存属性到b.properties文件
		Properties properties = new Properties();
		FileOutputStream oFile;
		try {
			oFile = new FileOutputStream(file);
			//mysql 5.5.45之后需要加上useSSL=false，不然会报一些警告
			String dbName =  "jdbc:mysql://127.0.0.1/"+dateBaseName+"?useSSL=false&zeroDateTimeBehavior=convertToNull";//最后面那个是防止数据库中空的时间列报错
			properties.setProperty("url", dbName);
			 properties.setProperty("username", user);
			 properties.setProperty("password", password);
			 properties.setProperty("driverClass", "com.mysql.jdbc.Driver");
			 properties.setProperty("maxPoolSize","100");
			 properties.setProperty("minPoolSize", "10");
			 properties.setProperty("initialPoolSize", "16");
			 properties.setProperty("maxIdleTime", "5000");
			 
			properties.store(oFile,"");
			oFile.close();
			System.out.println("配置文件已成功写入到src目录下，请右键刷新");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("发现中文目录，请在英文目录下建立项目");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
