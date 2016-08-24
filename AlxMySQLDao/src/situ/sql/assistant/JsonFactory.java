package situ.sql.assistant;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JsonFactory {
	
	//只开销一个stringBuffer，但是可维护性巨差，而且添加了各种判断，实际效率不一定高
	StringBuffer elementBuffer = new StringBuffer("$");//用于站位，防止首字母被删掉
	
	boolean flag = true;//用于判断是否已经输出的标志,用一个stringbuffer的开销换了一个boolean；
	
	
	/**
	 * 这个方法执行以后不会添加值的单引号
	 * @param key
	 * @param jsonFactory
	 * @return
	 */
	public boolean addJsonObject(String key,String jsonObject) {
		if(flag==false){
			elementBuffer.deleteCharAt(elementBuffer.length()-1);
			elementBuffer.append(',');
			flag=true;
		}
		
		String mykey=null;
		if(key!=null&&key.length()>0){
			mykey=key+":";
		}else{
			mykey="";
		}
		
		if(jsonObject!=null||jsonObject.length()>0){
			elementBuffer.append(mykey+jsonObject+',');//去掉末尾的}
		}else{
			elementBuffer.append(mykey+"null,");
		}
		
		return true;
		
	}
	
	
	/**
	 * 向json字符串中添加table对象，因为table对象的toString方法返回的就是json字符串，直接写进去就好啦
	 * @param name
	 * @param table
	 * @return
	 */
	public JsonFactory addTable(String name,Table table) {
		if(flag==false){
			elementBuffer.deleteCharAt(elementBuffer.length()-1);
			elementBuffer.append(',');
			flag=true;
		}
		
		String mykey=null;
		if(name!=null&&name.length()>0){
			mykey=name+":";
		}else{
			mykey="";
		}
		
		if(table!=null){
			elementBuffer.append(mykey+table.toString()+',');
		}else{
			elementBuffer.append(mykey+"null,");
		}
		return this;
	
	}
	
	/**
	 * 一次添加一个table的集合，然后一起写到json里，然后用数组输出
	 * @param list
	 * @return
	 */
	public JsonFactory addTable(String key,Collection<Table> list){
		
		if(flag==false){
			elementBuffer.deleteCharAt(elementBuffer.length()-1);
			elementBuffer.append(',');
			flag=true;
		}
		if(list==null) {
			elementBuffer.append("null,");
			return this;
		}
		if(key!=null&&key.length()>0){
			elementBuffer.append(key+":");
			if(list.size()==0){
				//如果list为空
				System.out.println("没有要转换的table");
				elementBuffer.append("[],");
				return this;
			}else{
				elementBuffer.append("[");
			}
		}
		for (Table table : list) {
			addTable(null, table);
		}
		if(key!=null&&key.length()>0){
			elementBuffer.deleteCharAt(elementBuffer.length()-1);//删掉最后一个table后面的逗号
			elementBuffer.append("],");
		}
		return this;
	}
	/**
	 * 向json中添加单个元素，如果key为null，那么就是以数组的格式传值，如['haha1','haha2','haha3','haha4']
	 * @param key
	 * @param value
	 * @return
	 */
	public JsonFactory addElement(String key,Object value){
		if(flag==false){
			elementBuffer.deleteCharAt(elementBuffer.length()-1);
			elementBuffer.append(',');
			flag=true;
		}
		if(key!=null&&key.length()>0){
			elementBuffer.append(key+":");
		}
		if(value!=null){
			elementBuffer.append("'"+value+"',");
		}else{
			elementBuffer.append("'null',");
		}
		
		return this;
	}
	public void addArray(String arrayName,Map<Object, Object>...maps) {
		if(flag==false){
			elementBuffer.deleteCharAt(elementBuffer.length()-1);
			elementBuffer.append(',');
			flag=true;
		}
		if(arrayName!=null&&arrayName.length()>0){
		elementBuffer.append(arrayName+":");
		}
		elementBuffer.append("[");
		int j=maps.length;
		for (Map<Object, Object> map : maps) {
			j--;
			elementBuffer.append("{");
			Set keys = map.keySet();
			int i =keys.size();
			for (Object key:keys) {
				i--;
				elementBuffer.append(key+":");
				elementBuffer.append(map.get(key));
				if(i>0){
				elementBuffer.append(',');
				}
				
			}
			elementBuffer.append("}");
			if(j>0){elementBuffer.append(',');}
		}
		//在末尾加上】
		elementBuffer.append("],");
		
	}
	
	/**
	 * 写json数组，
	 * @param arrayName 这个数组的名字
	 * @param set 一个Set<String[]>对象，String[]里面是数组的一个元素
	 * @param arrs 每一个元素的名字，其顺序要与上面的对齐
	 * 本函数调用方法
	 * JsonFactory jsonFactory = new JsonFactory();
		Set<String[]> set = new HashSet<String[]>();
		String[] person={"1','li","22","1"};
		String[] person2={"1","wang","22","0"};//一个string[]就是一个数组的元素，里面的顺序要与本函数最后一组参数对应
		set.add(person);
		set.add(person2);
		jsonFactory.addArray("haha", set, "id","name","age","sex");
		jsonFactory.addElement("page", 2);//可以添加单个元素，顺序不限
		jsonFactory.addElement("col", 10);
		System.out.println(jsonFactory.getJson());
		输出格式：{haha:[{id:'1',name:'wang',age:'22',sex:'0'},{id:'1',name:'li',age:'22',sex:'1'}],page:'2',col:'10'}
	 */
	public void addArray(String arrayName,Set<String[]> set,String...arrs) {
		if(flag==false){
			elementBuffer.deleteCharAt(elementBuffer.length()-1);
			elementBuffer.append(',');
			flag=true;
		}
		//设置此数组的名字
		if(arrayName!=null&&arrayName.length()>0){
			elementBuffer.append(arrayName+":");
		}
		elementBuffer.append("[");
		int j=set.size();
		for (String[] elements : set) {
			j--;
			elementBuffer.append("{");
				for (int i = 0; i < arrs.length; i++) {
					if(arrs[i]!=null&&arrs[i].length()>0){
						elementBuffer.append(arrs[i]+":");
					}
					if(i<elements.length){
						elementBuffer.append("'"+elements[i]+"'");
					}else{
						elementBuffer.append("null'");
					}
					if(i<arrs.length-1){
						elementBuffer.append(',');
					}
				}
				
			elementBuffer.append("}");
			if(j>0)elementBuffer.append(',');
		}
		//在末尾加上】
		elementBuffer.append("],");
	}
		
	public boolean addOneArray(String key,Object...objs){
		if(flag==false){
			elementBuffer.deleteCharAt(elementBuffer.length()-1);
			elementBuffer.append(',');
			flag=true;
		}
		if(key!=null&&key.length()>0){
			elementBuffer.append(key+":");
		}
		elementBuffer.append("[");
		for (int i = 0; i < objs.length; i++) {
			if(objs[i]!=null){
			elementBuffer.append("'"+objs[i]+"'");
			}else{
				elementBuffer.append("null");
			}
			if(i<objs.length-1){
				elementBuffer.append(',');
			}
		}
		elementBuffer.append("],");
		
		return true;
	}
	
	public String getJson() {
		//去掉末尾逗号
//		System.out.println("element"+elementBuffer);
//		StringBuffer sbBuffer = new StringBuffer().append("{");
		
		if(elementBuffer.length()>4){
			//防止多次输出出现重复信息，在开头先加个逗号，然后再删去换成{
			elementBuffer.deleteCharAt(0);
			elementBuffer.insert(0, "{");
			//删去结尾的逗号
			//if(elementBuffer.charAt(elementBuffer.length()-1)==',')
			//加上结尾
			//elementBuffer.insert(elementBuffer.length(), "}");
			
				elementBuffer.deleteCharAt(elementBuffer.length()-1);
				elementBuffer.append('}');
				flag=false;
			
			//sbBuffer.append(elementBuffer.substring(0,elementBuffer.length()-1));
			//sbBuffer.append("}");
			return elementBuffer.toString();
		}else{
			return "null";
		}
		
	}
	public String getArrayFormat() {
		if(elementBuffer.length()>4){
			//删去开头
			elementBuffer.deleteCharAt(0);
			//加上开头
			elementBuffer.insert(0, "[");
			//删去结尾
			//if(elementBuffer.charAt(elementBuffer.length()-1)==',')
			//加上结尾
			//elementBuffer.insert(elementBuffer.length(), "]");
			elementBuffer.deleteCharAt(elementBuffer.length()-1);
			elementBuffer.append(']');
			flag=false;
			
			//StringBuffer sbBuffer = new StringBuffer().append("[");
			//sbBuffer.append(elementBuffer.substring(0,elementBuffer.length()-1));
			//sbBuffer.append("]");
			return elementBuffer.toString();
		}else{
			System.out.println("JsonFactory里没有数组元素");
			return "null";
		}
	}
	
	@Override
	public String toString() {
		return getJson();
	}
	
}
