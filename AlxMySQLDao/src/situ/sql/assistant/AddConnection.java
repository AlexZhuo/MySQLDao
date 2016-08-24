package situ.sql.assistant;

import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;




import com.mchange.v2.c3p0.ComboPooledDataSource;


public class AddConnection {
	private static short driverSuccessFLag = 0;
	private static  String URL="";
	private static  String USER ="";
	private static  String PASSWORD = "";
	private static  String DRIVERCLASS = "";
	private static ComboPooledDataSource dataSource=null;
	protected AddConnection(){};//保护构造函数
	static 
	{				//一个静态快（只有第一次调用时会运行）
		//加载jdbc驱动程序
		
				
					Properties properties = new Properties();//加载用户名密码的配置文件
					String path = AddConnection.class.getResource("/").toString()+"pwd.properties";
					path = path.substring(6);//在windows中减去前面的file：/
					try {
						properties.load(AddConnection.class.getResourceAsStream("/pwd.properties"));
						URL = properties.getProperty("url");
						USER = properties.getProperty("username");
						//System.out.println("请确认用户名"+USER+"请确认密码："+properties.getProperty("password"));
						PASSWORD = properties.getProperty("password");
						DRIVERCLASS = properties.getProperty("driverClass");
						System.out.println("配置文件加载成功");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("配置文件加载失败");
						System.out.println("配置应放入地址:"+path+"不要将项目放在有中文的目录中");
								try {
									throw new Exception();
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
						//e.printStackTrace();
					}
					dataSource = new ComboPooledDataSource();
					try {
						dataSource.setDriverClass(DRIVERCLASS);
						dataSource.setJdbcUrl(URL);
						dataSource.setUser(USER);
						dataSource.setPassword(PASSWORD);
						dataSource.setMaxPoolSize(Integer.parseInt((String) properties.get("maxPoolSize")));
						dataSource.setMinPoolSize(Integer.parseInt((String) properties.get("minPoolSize")));
						dataSource.setInitialPoolSize(Integer.parseInt((String) properties.get("initialPoolSize")));
						dataSource.setMaxIdleTime(Integer.parseInt((String) properties.get("maxIdleTime")));
						driverSuccessFLag = 1;
					} catch (PropertyVetoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
	
				
	}
	public static Connection getConnection() throws SQLException{//一个函数,  返回值是一个connection对象，先生成一个匿名connection对象，然后调用它的getconnection方法，然后返回名字为conn的静态连接对象
		if(driverSuccessFLag==1){//检查驱动加载情况
			return dataSource.getConnection();//返回一个
		}else{
			System.out.println("驱动加载失败");
		}
		return null;
	}
	
	
	public static void closeConnection(Connection connection){
		if(connection!=null){
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void closeStatement(Statement statement) {
		if(statement!=null){
			try {
				statement.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static void closeResultSet(ResultSet resultSet){
		try {
			resultSet.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("建立数据库连接失败");
			e.printStackTrace();
		}
	}
	public static String giveURL() {
		if(URL!=null&&URL.length()>0){
			return URL;
		}else {
			return null;
		}
	}
}
