package situ.sql.assistant;

import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;


public class StartWith {
	public static String packageName = "com.situ.model";
	public static void Initialize() {
		System.out.println("==========欢迎使用situ Mysql语句生成器v2.2版==============");
		System.out.println("项目目录请不要包含中文名及空格");
		System.out.println("请输入菜单以继续："
				+ "1=写入配置文件"
				+ "2=生成单主键表格对象"
				+ "3=生成双主键表格对象"
				+ "4=输入包名"
				+ "5=单主键表一次搞定（慎用）"
				+ "6=生成view对象");
		Scanner scanner = new Scanner(System.in);
		int m = scanner.nextInt();
		switch (m) {
		case 1:
			System.out.println("请输入数据库IP:端口");
			String ip = scanner.next();
			System.out.println("请输入数据库的名字：");
			String dbName = scanner.next();
			System.out.println("请输入数据库user：");
			String user = scanner.next();
			System.out.println("请输入数据库密码：");
			String password = scanner.next();
			System.out.println("输入完毕，开始写入");
			try {
				TableFactory.writeProperties(ip,dbName, user, password);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("写入失败，可能是中文目录的原因");
				e.printStackTrace();
			}
			break;
		case 2:
			String tableName=null;
			String primary=null;
			String dataBaseName = null;
			dataBaseName = getDateBaseName();
			try {
				tableName = getTableName(dataBaseName,0);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Map<Integer, String> columnMap = null;
			try {
				columnMap = getColumnList(dataBaseName, tableName);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				System.out.println("请输入主键列的序号,或直接输入名字：");
				for (int j = 1; j < columnMap.size()+1; j++) {
					System.out.println(j+" : "+columnMap.get(j));
				}
				try{
					primary = columnMap.get(scanner.nextInt());
				}catch(Exception e){
					primary = scanner.next();
				}
			System.out.println("输入完毕，开始生成");
			try {
				TableFactory.GenerateByName(tableName, primary);
				//开始循环操作
				StartWith.Initialize();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("写入失败，可能是中文目录");
				e.printStackTrace();
			}
			break;
		case 3:
			System.out.println("请输入表名");
			String dualName = null;
			String databaseName = getDateBaseName();
			try {
				dualName = getTableName(databaseName,0);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Map<Integer, String> columnMap2=null;
			try {
				columnMap2 = getColumnList(databaseName, dualName);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("请输入此表左侧主键序号");
			for (int j = 1; j < columnMap2.size()+1; j++) {
				System.out.println(j+" : "+columnMap2.get(j));
			}
			String primaryLeft = columnMap2.get(scanner.nextInt());
			System.out.println("请输入此表右主键序号");
			String primaryRight =columnMap2.get(scanner.nextInt());
			System.out.println("输入完毕，开始生成");
			try {
				TableFactory.GenerateByName(dualName, primaryLeft,primaryRight);
				StartWith.Initialize();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("写入失败，可能是中文目录");
				e.printStackTrace();
			}
			break;
		case 4:
			System.out.println("请输入包名，形如com.situ.mode:");
			packageName = scanner.next().toLowerCase();
			break;
		case 5:
			
			String primary3=null;
			String dataBaseName3 = null;
			dataBaseName3 = getDateBaseName();
			Map<Integer, String> columnMap3 = null;
			Map<Integer, String> tableMap = null;
			try {
				tableMap = getTableNameList(dataBaseName3,0);
			} catch (SQLException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			for (int i = 1; i < tableMap.size()+1; i++) {
				String tableName3 = tableMap.get(i);
				try {
					columnMap3 = getColumnList(dataBaseName3, tableName3);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
					
					primary3 = columnMap3.get(1);
				
				
				try {
					TableFactory.GenerateByName(tableName3, primary3);
					//开始循环操作
					//StartWith.Initialize();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("写入失败，可能是中文目录");
					e.printStackTrace();
				}
			}
			
			break;
		case 6:
			String tableName4=null;
			String primary4=null;
			String dataBaseName4 = null;
			dataBaseName4 = getDateBaseName();
			try {
				tableName4 = getTableName(dataBaseName4,1);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Map<Integer, String> columnMap4 = null;
			try {
				columnMap4 = getColumnList(dataBaseName4, tableName4);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				System.out.println("请输入主键列的序号，或直接输入名字：");
				for (int j = 1; j < columnMap4.size()+1; j++) {
					System.out.println(j+" : "+columnMap4.get(j));
				}
				try{
					primary4 = columnMap4.get(scanner.nextInt());
				}catch(Exception e){
					primary4 = scanner.next();
				}
			System.out.println("输入完毕，开始生成");
			try {
				TableFactory.GenerateByName(tableName4, primary4);
				//开始循环操作
				StartWith.Initialize();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("写入失败，可能是中文目录");
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
	public static String getDateBaseName() {
		String url = AddConnection.giveURL();
		String dataBaseName=null;
		if(url!=null){
			int lastIndex = url.indexOf('?');
			if(lastIndex<=0){
				lastIndex = url.length();
			}
			dataBaseName = url.substring(url.lastIndexOf('/')+1,lastIndex);
		}else{
			System.out.println("配置文件不正确");
			return null;
		}
		return dataBaseName;
	}
	
	private static String getTableName(String dataBaseName,int wtd) throws SQLException{
		String tableName=null;
		Scanner scanner = new Scanner(System.in);

			Map<Integer, String> map =getTableNameList(dataBaseName,wtd);
			System.out.println("请输入表名序号，或直接输入名字：");
			for(int j=1;j<map.size()+1;j++){
				System.out.println(j+" : "+map.get(j)+" ");
			}
			try{
				tableName = map.get(scanner.nextInt());
			}catch (Exception e){
				tableName = scanner.next();
			}
			System.out.println("已选择："+tableName);
			
			return tableName;
		
	}
	
	private static Map<Integer, String> getTableNameList(String dataBaseName,int wtd) throws SQLException {
		String sql = null;
		if(wtd==0){
			sql = "select table_name from user_tables";
		}else{
			sql = "select view_name from user_views";
		}
		System.out.println(sql);
		Connection	connection = AddConnection.getConnection();
			PreparedStatement pStatement = connection.prepareStatement(sql);
			ResultSet resultSet = pStatement.executeQuery();
			Map<Integer, String> map = new HashMap<Integer, String>();
			int i=1;
			while(resultSet.next()){
				map.put(i,resultSet.getString(1));
				i++;
			}
			connection.close();
		return map;
	}
	
	
	public static Map<Integer, String> getColumnList (String dataBaseName,String tableName) throws SQLException {
		
		Connection	connection = AddConnection.getConnection();
		String sql2="SELECT COLUMN_NAME FROM USER_TAB_COLUMNS WHERE TABLE_NAME='"+tableName+"'";
		System.out.print(sql2);
		PreparedStatement pStatement = connection.prepareStatement(sql2);
		ResultSet resultSet2 = pStatement.executeQuery();
		Map<Integer, String> columnMap = new HashMap<Integer, String>();
		int k=1;
		while(resultSet2.next()){
			columnMap.put(k, resultSet2.getString(1));
			k++;
		}
		connection.close();
		return columnMap;
	}
}
