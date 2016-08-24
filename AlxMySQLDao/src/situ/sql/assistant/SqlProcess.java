package situ.sql.assistant;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



public class SqlProcess {
	Connection connection = null;
	CallableStatement cs = null;
	/**
	 * 传入存储过程名，in的参数，out的下标，获得一个list，每个list的元素是该out下标所对的一整列，用sql数组封装
	 * 调用方法List<Object> list = new SqlProcess().getProcess("proc_2", new int[]{3,4,5},20,30);
	 * @param processName
	 * @param outNum
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	public  List<Object> getProcess(String processName,int[] outNum,Object...args) throws SQLException {
		List<Object> list = new ArrayList<Object>();
	try{
		connection=AddConnection.getConnection();
		String sql ="{call "+processName+" (";
		for (int i = 0; i < outNum.length+args.length; i++) {
			sql+=" ?,";
		}
		sql=sql.substring(0,sql.length()-1);
		sql+=" )}";
		System.out.println("sql:"+sql);
		cs = connection.prepareCall(sql);
		if(args!=null&&args.length>0){
			for (int i = 0; i < args.length; i++) {
				cs.setObject(i+1, args[i]);
			}
		}
		for (int i = 0; i < outNum.length; i++) {//把输出都能成数组
			cs.registerOutParameter(outNum[i], java.sql.Types.ARRAY);
		}
		cs.execute();
		System.out.println("语句执行成功");
		for (int i = 0; i < outNum.length; i++) {
			list.add(cs.getObject(outNum[i]));//数据表中的一列(数组)为list的一个元素
		}
	}finally{
		AddConnection.closeStatement(cs);
		AddConnection.closeConnection(connection);
	}
		return  list;
		
	}
}
