package situ.sql.assistant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ExeUpdate2 implements ExeStandard{
	protected ExeUpdate2(){}//保护构造函数
	Connection connection=null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	public boolean ExeDao(String sql,Object...objects){//增，删，改均可用
		try 
		{
			connection = AddConnection.getConnection();
			System.out.println("服务器连接成功");
					try 
					{	
						preparedStatement = connection.prepareStatement(sql);//如果没有形参就执行sql语句
						if(objects!=null&&objects.length>0)//如果有形参那么就写在sql里
						{
							System.out.println(sql);
							for (int i = 0; i < objects.length; i++)//遍历每个占位符 
							{	
								System.out.println((i+1) +":"+objects[i]);
								preparedStatement.setObject(i+1, objects[i]);//把占位符都替换掉
							}
							if(preparedStatement.executeUpdate()==1){//执行增删改语句
								return true;
							}else {
								return false;
							}
						}
					} 
					catch (SQLException e) {
						// TODO Auto-generated catch block
						System.out.println("sql语句错误");
						e.printStackTrace();
					}
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			System.out.println("无法建立连接，请检查用户名密码和服务器是否开启");
			e.printStackTrace();
			return false;
		}
		finally
		{
			AddConnection.closeConnection(connection);
			AddConnection.closeStatement(preparedStatement);
			System.out.println("连接关闭成功");
		}
		return false;
	}
	
	
	
	
	public List getAll(Class model,String sql,Object...args) {//用于查询
		try {
			connection = AddConnection.getConnection();
			List list = new ArrayList();
			System.out.println(sql);
			preparedStatement = connection.prepareStatement(sql);//放入sql语句准备执行
			if(args!=null&&args.length>0){
				int index=1;
				for (int i = 0; i < args.length; i++) {
					System.out.println((i+1)+":"+args[i]);
					if(args[i]==null)continue;
					preparedStatement.setObject(index, args[i]);
					index++;
				}
			}
			resultSet = preparedStatement.executeQuery();//执行sql语句，并获得结果集
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();//获得结果集的底层数据库信息
			int columns = resultSetMetaData.getColumnCount();//获得结果集的列数
			while(resultSet.next()){//对行进行遍历
				Object obj =null;//待输出的行对象，用传入的model class文件生成
						try {
							obj = model.newInstance();
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				for (int i = 0; i < columns; i++) {//对列进行遍历
					String columnName = resultSetMetaData.getColumnLabel(i+1);//数据库中的列名
					String columnJavaClass = resultSetMetaData.getColumnClassName(i+1);//java中每列的数据类型
					String functionName = "set"+columnName.substring(0, 1).toUpperCase()+columnName.substring(1);//model中的成员赋值函数
					//System.out.println("本列的数据类型是"+columnJavaClass+"函数名是"+functionName);
					Method mt1 =null;
					
						try {
							mt1 = model.getMethod(functionName, Class.forName(columnJavaClass));//写上函数名和形参类型,执行复制函数
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							System.out.println("警告：没有找到列名，将会跳过此属性"+columnJavaClass);
							System.out.println("所有table中的int应写成Integer，且成员变量名要与列名一致，且get/set方法不要多写也不要少写");
							//e.printStackTrace();
							continue;
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
					
						try {
							mt1.invoke(obj, resultSet.getObject(i+1));//传入调用者和本列的值做参数
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							System.out.println("数据库中的数据类型与类文件中的不匹配");
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}
				list.add(obj);//加入输出结果
			}
			return list;
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("sql 语句错误");
			e.printStackTrace();
			return null;
		}finally
		{
			AddConnection.closeResultSet(resultSet);
			AddConnection.closeStatement(preparedStatement);
			AddConnection.closeConnection(connection);
			System.out.println("关闭成功");
		}
		
		
		
	}
	
	
	
	/**
	 * 用于查询对象中不存在的列名，如 count(*),sum()等，或单独查询某一列的值，可以加快查询速度，减少内存开销
	 */
	public List<Map<String, Object>> getEveryThing(String sql,Object...args) {//用于多表联查，返回一个map就是一行
		
		try {
			connection = AddConnection.getConnection();
			List list = new ArrayList();//返回用的list
			System.out.println("sql语句为"+sql);
			preparedStatement = connection.prepareStatement(sql);//放入sql语句准备执行
			if(args!=null&&args.length>0){
				int index=1;
				for (int i = 0; i < args.length; i++) {
					if(args[i]==null) continue;
					System.out.println((i+1)+":"+args[i]);
					preparedStatement.setObject(index, args[i]);
					index++;
				}
			}
			resultSet = preparedStatement.executeQuery();//执行sql语句，并获得结果集
			ResultSetMetaData resultSetMetaData = resultSet.getMetaData();//获得结果集的底层数据库信息
			int columns = resultSetMetaData.getColumnCount();//获得结果集的列数
			while(resultSet.next())
			{//行遍历
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < columns; i++) {//列遍历
					map.put(resultSetMetaData.getColumnLabel(i+1),resultSet.getObject(i+1));//得到列名
				}
				list.add(map);//一个map就是一行
			}
			return list;
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}finally
		{
			AddConnection.closeResultSet(resultSet);
			AddConnection.closeStatement(preparedStatement);
			AddConnection.closeConnection(connection);
			System.out.println("关闭成功");
		}
	}
	
	
}
