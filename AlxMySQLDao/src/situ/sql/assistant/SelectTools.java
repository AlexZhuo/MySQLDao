package situ.sql.assistant;

import java.util.Date;
import java.util.Map;


public class SelectTools {
		
	public static int getInt(String columnName , Map<String, Object> map) {
		Object obj = map.get(columnName);
		if(obj instanceof Integer){
			return Integer.parseInt(obj.toString());
		}else if(obj instanceof String){
			return Integer.parseInt((String) obj);
		}else if(obj instanceof Float||obj instanceof Float){
			return Integer.parseInt(obj.toString());
		}
		return 0;
	}
	
	public static String getString(String columnName , Map<String, Object> map) {
		Object obj = map.get(columnName);
		if(obj instanceof String){
			return (String)obj;
		}else{
			return obj.toString();
		}
	}
	
	public static Date getDate(String columnName , Map<String, Object> map) {
		Object obj = map.get(columnName);
		if(obj instanceof java.sql.Date){
			return (Date)obj;
		}
		return null;
	}
	
}
