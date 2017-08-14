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



public class ExeUpdate implements ExeStandard{
	protected ExeUpdate(){};//保护构造函数
	List<PreparedStatement> pStatements= new ArrayList<PreparedStatement>();
	Connection connection=null;//一个ExeUpdate对象给一个用户使用
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	public boolean ExeDao(String sql,Object...objects){//增，删，改均可用
		try 
			{	
				if(connection==null)//因为一次执行多行的话，连接必须一致，都使用成员变量便于其他函数调用
				{
					connection = AddConnection.getConnection();
					connection.setAutoCommit(false);
				}
					try 
					{
						preparedStatement = connection.prepareStatement(sql);//如果没有形参就执行sql语句
						if(objects!=null&&objects.length>0)//如果有形参那么就写在sql里
						{
							System.out.println("服务器连接正常");
							System.out.println("sql:"+sql);
							for (int i = 0; i < objects.length; i++)//遍历每个占位符 
							{	
								System.out.println((i+1) +":"+objects[i]);
								preparedStatement.setObject(i+1, objects[i]);//把占位符都替换掉
							}
							
						}
						pStatements.add(preparedStatement);
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
		
		return false;
	}

	/**
	 * 本函数调用方法
	 * ExeUpdate exeUpdate = new ExeUpdate();
		studentDao.AddRow(exeUpdate,new Student(null, "事件学生新4", 8, 1));
		teacherDao.AddRow(exeUpdate,new Teacher(null, "事件老师新4", 88, 1));
		studentDao.AddRow(exeUpdate,new Student(null, "事件学生新4", 8, 1));
		exeUpdate.flush();
	 * @return
	 */
	public  boolean flush() {
		System.out.println("共有"+pStatements.size()+"个sql语句");
		if(pStatements.size()==0)return false;
			for (PreparedStatement p : pStatements) 
			{
			
					try {
						p.executeUpdate();
						p.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						System.out.println("sql语句错误，或主键重复");
						e.printStackTrace();
						return false;
					}
			}
		try {
			connection.commit();
			System.out.println("全部执行成功");
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("执行失败，正在回滚");
			
			e.printStackTrace();
			return false;
		}finally{
			for (PreparedStatement preparedStatement : pStatements) {//关闭所有statement
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			pStatements = new ArrayList<PreparedStatement>();
			AddConnection.closeConnection(connection);
			connection = null;
			System.out.println("关闭成功");
		}
		
	}
	
	
	
	
	public List getAll(Class model, String sql, CustomConverter converter, Object...args) {//用于查询,会返回一个传入类的对象存储查询到的数据
		Connection queryConnection = null;
		try {
			queryConnection = AddConnection.getConnection();
			List list = new ArrayList();
			System.out.println(sql);
			preparedStatement = queryConnection.prepareStatement(sql);//放入sql语句准备执行
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
					String functionName = "set"+columnName.substring(0, 1).toUpperCase()+columnName.substring(1).toLowerCase();//model中的成员赋值函数
					//System.out.println("本列的数据类型是"+columnJavaClass+"函数名是"+functionName);
					Method mt1 =null;
					
						try {
							mt1 = model.getMethod(functionName, Class.forName(columnJavaClass));
						} catch (SecurityException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							System.out.println("警告：没有找到列名"+functionName+"，将会跳过此属性"+columnJavaClass);
							System.out.println("所有table中的int应写成Integer，且成员变量名要与列名一致，且get/set方法不要多写也不要少写");
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}//写上函数名和形参类型,执行复制函数
					
						try {
							mt1.invoke(obj, resultSet.getObject(i+1));
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
						}//传入调用者和本列的值做参数
				}
				list.add(obj);//加入输出结果
			}
			return list;
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("sql 语句错误");
			e.printStackTrace();
			return null;
		}finally{
			AddConnection.closeStatement(preparedStatement);
			AddConnection.closeResultSet(resultSet);
			AddConnection.closeConnection(queryConnection);
		}
		
		
		
	}
	
	
	
	
	public List<Map<String, Object>> getEveryThing(String sql,Object...args) {//用于多表联查，返回一个map就是一行，键名就是列名，值就是本行本咧的数据
		
		try {
			connection = AddConnection.getConnection();
			List list = new ArrayList();//返回用的list
			System.out.println("sql语句为"+sql);
			preparedStatement = connection.prepareStatement(sql);//放入sql语句准备执行
			if(args!=null&&args.length>0){
				for (int i = 0; i < args.length; i++) {
					System.out.println((i+1)+":"+args[i]);
					preparedStatement.setObject(i+1, args[i]);
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
