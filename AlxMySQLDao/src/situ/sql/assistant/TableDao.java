package situ.sql.assistant;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;




public class TableDao implements ClassDao{
	ExeStandard ExeStandard = null;
	public Table table = null;
	public TableDao(Table clientTable) {
		// TODO Auto-generated constructor stub
		this.ExeStandard = new ExeUpdate2();
		this.table = clientTable;
	}
	protected TableDao(){};//防止用户调用空参构造
	public Class getType() {
		// TODO Auto-generated method stub
		return table.getClass();
	}

	public String getTableName() {
		// TODO Auto-generated method stub
		return table.giveTableName();
	}
	public String getPrimaryColumnName(){
		return table.givePrimaryColumnName();
	}
	public String[] getDualPrimaryName() {
		return table.giveDualPrimaryName();
	}
	
	public boolean insert(Object...objs){
		boolean flag=true;
		for (Object object : objs) {
			if(insert(object)==false)flag=false;
		}
		return flag;
	}
	public static TableDao parseObjectToTable(Object object) throws SecurityException, NoSuchMethodException {
		TableDao tableDao = new TableDao();
		tableDao.table = (Table) object;
		return tableDao;
	}
	/**
	 * 思路：首先把这个类中所有以get方法开头的函数拿出来，并且挨个执行，得到需要传入sql的参数，至于顺序，在拿到
	 * 函数的同时减去get就得到列值，卸载insert into 语句当中
	 */
	public boolean insert(Object obj) {
		// TODO Auto-generated method stub
		if(obj==null){
			System.out.println("警告：传入的要插入的对象为null");
			return false;
		}
		System.out.println("class是"+obj.getClass());
		List<Object> list = new ArrayList<Object>();//用于存放要被插入数据库的参数
		
		Method[] methods = obj.getClass().getMethods();//得到传入obj的所有函数
		int num =0;
		String sqlColumns="";
		for (Method method : methods) {//遍历obj中所有的函数
			String methodName = method.getName();//得到这个函数的名字
			if(methodName.startsWith("get")&& !methodName.equals("getClass"))//看看是不是get函数
			{	//System.out.println("函数名"+methodName);
				sqlColumns += methodName.substring(3)+",";//把函数名的get删掉后就是列名，每个列名后面跟一个逗号
				
				num++;//用于记录有几个函数名，用于控制占位符的数量
				
					try {
						list.add(method.invoke(obj));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						System.out.println("在反射时get的函数名写错");
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}//执行函数，获得需要传入数据库的值
				
			}
		}
		sqlColumns=sqlColumns.substring(0, sqlColumns.length()-1);//把要写入列的最后的逗号删掉
		String sql = "INSERT INTO "+this.getTableName();
		sql+=" ("+sqlColumns+") VALUES (";
		for (int i = 0; i < num; i++) {//有多少个get就加多少个？和逗号
			sql+="?,";
		}
		sql = sql.substring(0, sql.length()-1)+")";//把末尾的逗号删去并加上一个）
		System.out.println("参数个数"+list.size());
		return ExeStandard.ExeDao(sql, list.toArray());
		
	}
	public  boolean appendTo(SqlBuffer sqlBuffer){
		this.ExeStandard=sqlBuffer.getExeUpdate();
		return this.ExeStandard!=null ? true:false;
	}
	/**
	 * 如果被删除的对象有值，就返回true，如果被删除的对象不存在就返回false
	 */
	public boolean Delete(Object...ids) {
		// TODO Auto-generated method stub
		if(ids==null||ids.length==0){
			System.out.println("传入的要删除的id为null");
			return false;
		}
		List<Object> idList = new ArrayList<Object>();
			String sql = "DELETE FROM "+this.getTableName()+" WHERE "+getPrimaryColumnName()+" IN (";
			
		for (int i = 0; i < ids.length; i++) {
			if(ids[i]!=null){//因为数组中有可能出现null元素，所以要新建一个list来接收有值的数组
				idList.add(ids[i]);
				sql+="?,";
			}
		}
		sql=sql.substring(0,sql.length()-1);//去掉末尾逗号
		sql+=")";
		return ExeStandard.ExeDao(sql, idList.toArray());
	}
	/**
	 * 给复合主键删除时使用的方法，在删除复合主键时，将列名和值
	 * @param map
	 * @return
	 */
	public boolean DeleteMulti(Map<String, Object> map) {//一次只能删除一行
		// TODO Auto-generated method stub
		String sql = null;
		Set keys = map.keySet();
		Iterator<String> it = keys.iterator();
		List<Object> ids = new ArrayList<Object>();
		if(keys==null){System.out.println("复合主键没有传值"); return false;}
		sql = "DELETE FROM "+this.getTableName()+" WHERE 1=1 ";
		while(it.hasNext()){
			String key = it.next();
			if(key.length()==0||map.get(key)==null)continue;
			sql+=" AND "+key+" =? ";
			//给参数列表添加对象
			ids.add(map.get(key));
		}
		return ExeStandard.ExeDao(sql, ids.toArray());
	}
	/**
	 * 通过两复合主键的左右两列的值删除一行
	 * @param leftId
	 * @param rightId
	 * @return
	 */
	public boolean DeleteDual(Object leftId,Object rightId){
		if(leftId==null||rightId==null){
			System.out.println("警告:删除参数传入不全，请检查");
			return false;
		}
		String sql = "DELETE FROM "+getTableName()+" WHERE "+getDualPrimaryName()[0]+" =? AND "+getDualPrimaryName()[1]+" =?";
		return ExeStandard.ExeDao(sql, leftId,rightId);
	}
	/**
	 * 通过一个两复合主键表的实例删除一行
	 * @param obj
	 * @return
	 */
	public boolean DeleteDual(Object obj) {
		if(obj==null||getDualPrimaryName()==null){
			System.out.println("要删除的对象为null,或没有定义主键");
			return false;
		}
		String sql = "DELETE FROM "+getTableName()+" WHERE "+getDualPrimaryName()[0]+" =? AND "+getDualPrimaryName()[1]+" =?";
		Object[] ids = getDoubleId(obj);
		return ExeStandard.ExeDao(sql, ids[0],ids[1]);
	}
	
	public boolean DeleteByObjects(Object...objects) {
		if(objects==null||objects.length==0){
			System.out.println("警告:要删除的对象为null");
			return false;
		}
		List<Object> ids= new ArrayList<Object>();
		for (Object object : objects) {
			try {
				ids.add(getObjectId((Table)object));
				
			} catch (Exception e) {
				System.out.println("主键的列名写错，请去表对象修改");
			}
		}
		return Delete(ids.toArray());
	}
	/**
	 * //根据id进行修改列值，一次只能修改任意一列的某个值
	 * 但是每修改一个值就要建立一个连接
	 * @param updateBeans
	 * @return
	 */
	public boolean updateRows(UpdateBean...updateBeans) {
		// TODO Auto-generated method stub
		//本函数调用方法
		//teacherDao.updateRow(new UpdateBean("teacher_name", "老师封装2", 17),new UpdateBean("teacher_age", 99, 17));
		if(updateBeans==null||updateBeans.length==0){
			System.out.println("条件为null");
			return false;
		}
		for (UpdateBean updateBean : updateBeans) {//如果要给多个列赋值，那就执行多次
			if(updateBean==null)continue;
			if(updateBean.getColunmName()==null||updateBean.getColunmName().length()==0)continue;
			String sql = "UPDATE "+this.getTableName()+" SET "+updateBean.getColunmName()+ " = ? WHERE "+getPrimaryColumnName()+" = ? ";
			if(updateBean.getId()==null){System.out.println("没有传id值");return false;}
			ExeStandard.ExeDao(sql,updateBean.getValue(),updateBean.getId());
		}
		return true;
	}
	
	/**
	 * //本函数调用方法
		//teacherDao.updateRow(new UpdateBean("teacher_name", "老师封装2", 17),new UpdateBean("teacher_age", 99, 17));
	需要在第一个updateBean里面传id值
	用一个updateBean即可实现修改主键值的作用，弥补了上面update方法不能修改主键的缺陷
	每次修改一行只需一个连接
	 */
	private boolean updateOneRow(UpdateBean...updateBeans) {//一次可以修改某一列的多个值，以第一个传入的updateBean的id为准，之后的id作废
		String sql = "UPDATE "+this.getTableName()+" SET ";
		List<Object>values = new ArrayList<Object>();//多加的一个是要修改行的id值
		for (int j = 0; j < updateBeans.length; j++) {//减一是减掉id
			if(updateBeans[j]==null)continue;
			sql+=updateBeans[j].toString()+" =?,";
			values.add(updateBeans[j].getValue().toString());
		}
		sql=sql.substring(0, sql.length()-1);//删掉最后一个参数后面的逗号
		sql+="WHERE "+getPrimaryColumnName()+" =?";
		if(updateBeans[0].getId()==null){System.out.println("没有传id值");return false;}
		values.add(updateBeans[0].getId());//把定位用的id放到参数列表最后一个
		
		return ExeStandard.ExeDao(sql,values);
	}
	/**
	 * 相比较上一个，优势在于不用字符串操作，可移植性强，但是问题是看起来很复杂，细节问题比较多
	 * @param updateBeans
	 * @return
	 */
//	private boolean updateOneRow2(UpdateBean...updateBeans) {
//		if(updateBeans==null||updateBeans.length==0)return false;
//		List<Object> values = new ArrayList<Object>();//多加的一个是要修改行的id值
//		String sql = "UPDATE "+this.getTableName()+" SET "+updateBeans[0].getColunmName().toString()+" =? ";//提前写一个减少末尾逗号产生
//		for (int j = 1; j < updateBeans.length; j++) {//从第二个开始写上问号占位符，目的是减少末尾逗号产生
//			if(updateBeans[j]==null)continue;
//			sql+=" , "+updateBeans[j].getColunmName()+" = ? ";
//			values.add(updateBeans[j].getValue());
//		}
//		sql+=" WHERE "+getPrimaryColumnName()+" =? ";//用id来寻址
//		if(updateBeans[0].getId()==null){System.out.println("没有传id值");return false;}
//		values.add(updateBeans[0].getId());//把定位用的id放到参数列表最后一个
//		
//		return ExeStandard.ExeDao(sql,values.toArray());
//	}
	
	/**
	 * 用反射写的一次修改一整行所有列的方法
	 * 调用方式
	 * //studentDao2.updateWholeRow(new Student(78, "修改学生", 100, 100));
		//teacherDao2.updateWholeRow(new Teacher(28, "秦慧芳", 21, 0));
	 * @param obj
	 * @return
	 */
	public boolean update(Object...objs) {
		if(objs==null){
			System.out.println("要删除的对象为null");
			return false;
		}
		for (Object object : objs) {
			update(object);
		}
		return true;
	}
	public Object[] getDoubleId(Object obj){
		if(obj==null||getDualPrimaryName()==null){
			System.out.println("传入参数为null,或没有规定主键列");
			return null;
		}
		Object valueLeft = null;
		Object valueRight = null;
		String methodLeft = "get"+getDualPrimaryName()[0].substring(0,1).toUpperCase()+getDualPrimaryName()[0].substring(1,getDualPrimaryName()[0].length());
		String methodRight = "get"+getDualPrimaryName()[1].substring(0,1).toUpperCase()+getDualPrimaryName()[1].substring(1,getDualPrimaryName()[1].length());
		try {
			Method method1 = obj.getClass().getMethod(methodLeft);
			Method method2 = obj.getClass().getMethod(methodRight);
			valueLeft = method1.invoke(obj);
			valueRight = method2.invoke(obj);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("没有指定id列或指定错误");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("出现了奇怪的错误");
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new Object[]{valueLeft,valueRight};
	}
	/**
	 * 修改两个复合主键表的方法，因为要改的往往是主键（单主键的修改方法不能修改主键--但用updateBEAN除外）所以得写一个原来的来定位，写一个要修改成的来赋值
	 * @param source
	 * @param target
	 * @return
	 */
	public boolean updateDual(Object source,Object target) {
		if(source==null||target==null||getDualPrimaryName()==null){
			System.out.println("传入对象参数为null，请检查");
			return false;
		}
		Method[] methods = target.getClass().getMethods();
		List<Object> values = new ArrayList<Object>();
		Object[] ids = getDoubleId(source);
		String sql = "UPDATE "+getTableName()+" SET ";
		for (Method method : methods) {
			if(method.getName().startsWith("get")&&!method.getName().equals("getClass")){
			sql+=method.getName().substring(3)+" =? ,";
			try {
				values.add(method.invoke(target));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
		sql=sql.substring(0,sql.length()-1);
		sql+=" WHERE "+getDualPrimaryName()[0]+"=? AND "+getDualPrimaryName()[1]+" =?";
		values.add(ids[0]);
		values.add(ids[1]);
		return ExeStandard.ExeDao(sql,values.toArray());
	}
	public boolean update(Object obj){
		if(obj==null){
			System.out.println("要更新的对象为null");
			return false;
		}
		boolean idFlag=false;
		//System.out.println("class是"+obj.getClass());
		Method[] methods = obj.getClass().getMethods();
		List<Object> values = new ArrayList<Object>();
		Object id = "";
		String sql = "UPDATE "+this.getTableName()+" SET ";
		for (Method method : methods) 
		{
			if(method.getName().startsWith("get")&&method.getName()!="getClass")
			{
				try {
					
					if(idFlag==false && method.getName().substring(3).toLowerCase().equals(this.getPrimaryColumnName().toLowerCase()))
					{
							System.out.println("找到id列"+method.getName());
							id = method.invoke(obj);
							idFlag=true;
							continue;//如果碰到id（自增长）的列，就停止修改，进行下一个
					}
					//System.out.println("正在修改属性"+method.getName().substring(3));
					if(method.invoke(obj)!=null){//如果类里这个属性为空，则不修改此属性，这样可以实现单个元素的修改
						sql+=method.getName().substring(3)+" =? ,";//写上列名和值的占位符
						values.add(method.invoke(obj));//获取对象里的值
					}
					
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					System.out.println("在反射时get的函数名写错");
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		sql = sql.substring(0,sql.length()-1);//去掉最后的逗号
		if(id==null){
			System.out.println("没有传入要修改的行id，修改动作终止");
			return false;
		}
		if(this.getPrimaryColumnName()==null||this.getPrimaryColumnName().length()==0){
			System.out.println("没有传入该表的主键");
			return false;
		}
		sql+=" WHERE "+this.getPrimaryColumnName()+"= ?";
		values.add(id);//id值是最后一个问号
		return ExeStandard.ExeDao(sql, values.toArray());
		
	}
	/**
	 * 用于复合主键表的修改，比较难用，但至少不用写sql语句了
	 * @param map
	 * @param updateBeans
	 * @return
	 */
	public boolean updateMulti(Map<String, Object> map,UpdateBean...updateBeans) {
		String sql = "UPDATE "+getTableName()+" SET "+updateBeans[0].getColunmName()+" =? ";
		List<Object> values = new ArrayList<Object>();
		values.add(updateBeans[0].getValue());
		for (int i = 1; i < updateBeans.length; i++) {
			sql+=", "+updateBeans[i]+" =? ";
			values.add(updateBeans[i].getValue());
		}
		sql+=" WHERE 1=1";
		Set keys = map.keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String key = it.next();
			sql+=" AND "+key+" =? ";
			values.add(map.get(key));
		}
		return ExeStandard.ExeDao(sql, values.toArray());
	}
	
	public  List select(QueryBean...queryBeans) {
		// TODO Auto-generated method stub
		
		if(queryBeans!=null&&queryBeans.length>0){
			return select(0, 1, queryBeans);
		}else{
			return select(0, 1, null);
		}
		//本函数的调用方法
//		QueryBean queryBean = new QueryBean("AND", "teacher_id", ">", 10,"teacher_name","teacher_id");
//		QueryBean queryBean2 = new QueryBean("AND", "teacher_id", "<", 13);
	}
	
	public String parseQueryBean(QueryBean[] queryBeans){
		String sql = "SELECT ";
		if(queryBeans!=null&&queryBeans.length>0)//检查是否有筛选条件
		{
			System.out.println("有"+queryBeans.length+"个筛选条件");
			int selectedFlag = 0;
			
			for (int i = 0; i < queryBeans.length; i++) {
				if(queryBeans[i].getSelectedCols()!=null&&queryBeans[i].getSelectedCols().length>0){
					for (int j = 0; j < queryBeans[i].getSelectedCols().length; j++) //添加要输出的列名
					{
						sql+=queryBeans[i].getSelectedCols()[j].toString()+",";//去掉最后一个参数后面的逗号
						if(selectedFlag==0){
							selectedFlag=1;
						}
					}
				}
			}
			
			if(selectedFlag==0)//若没有输出列数限制
			{
				sql+=" * ";
			}else{//如果有筛选条件，减掉最后一个条件后面的逗号
				sql=sql.substring(0,sql.length()-1);//减掉最后的逗号
			}
			sql += " FROM "+this.getTableName()+" WHERE 1=1 ";
			
			for (int i = 0; i < queryBeans.length; i++) //从此处开始添加约束条件
			{	
				if(queryBeans[i].getValue()!=null&&queryBeans[i].getAon()!=null&&queryBeans[i].getColunmName()!=null&&queryBeans[i].getEqs()!=null)
				{
				sql+=queryBeans[i].toString();//把值放到？中避免单引号的处理，但是需要把传入的所有值放到一个数组里
				}
			}//约束条件添加完毕
			groupBy:for (int i = 0; i < queryBeans.length; i++) {
				if(queryBeans[i].getGroupBy()!=null){
					sql+=" GROUP BY "+queryBeans[i].getGroupBy();
					break groupBy;
				}
			}
			
			
			sql+=" ORDER BY NULL";//从此处开始添加排序条件
			for (int i = 0; i < queryBeans.length; i++) {//若有排序条件
				if(queryBeans[i].getOrderBy()!=null&&queryBeans[i].getAsc()!=null){//若用户定义了排序条件
					sql+=" , "+queryBeans[i].getOrderBy()+" "+queryBeans[i].getAsc();//把第一个条件拿出来
				}
			}
		
		}else{//若没有筛选条件
			sql+="* FROM "+this.getTableName();
		}
		return sql;
	}
	public List select(int rowCount,int pageNum,QueryBean...queryBeans) {
		// TODO Auto-generated method stub
		if(rowCount<0||pageNum<1){
			System.out.println("传入的查询参数错误，请检查");
			return null;
		}
		List list = new ArrayList();
		List<Object> values = new ArrayList<Object>();//用于存放查询条件的具体数值
		String sql = "SELECT ";
		if(queryBeans!=null&&queryBeans.length>0)//检查是否有筛选条件
		{
			System.out.println("有"+queryBeans.length+"个筛选条件");
			int selectedFlag = 0;
			
			for (int i = 0; i < queryBeans.length; i++) {
				if(queryBeans[i]==null)continue;
				if(queryBeans[i].getSelectedCols()!=null&&queryBeans[i].getSelectedCols().length>0){
					for (int j = 0; j < queryBeans[i].getSelectedCols().length; j++) //添加要输出的列名
					{
						sql+=queryBeans[i].getSelectedCols()[j].toString()+",";//去掉最后一个参数后面的逗号
						if(selectedFlag==0){
							selectedFlag=1;
						}
					}
				}
			}
			
			if(selectedFlag==0)//若没有输出列数限制
			{
				sql+=" * ";
			}else{//如果有筛选条件，减掉最后一个条件后面的逗号
				sql=sql.substring(0,sql.length()-1);//减掉最后的逗号
			}
			sql += " FROM "+this.getTableName()+" WHERE 1=1 ";
			
			for (int i = 0; i < queryBeans.length; i++) {
				if(queryBeans[i]==null)continue;
				InBean inbean = queryBeans[i].getInbean();
				if(inbean!=null){
					sql+=inbean.toString();
					Object[] inbeanVals = inbean.getValues();
					for (int j = 0; j < inbeanVals.length; j++) {
						if(inbeanVals[j]==null) continue;
						values.add(inbeanVals[j]);
					}
				}
			}
			for (int i = 0; i < queryBeans.length; i++) //从此处开始添加约束条件
			{	
				if(queryBeans[i]==null)continue;
				if(queryBeans[i].getValue()!=null&&queryBeans[i].getAon()!=null&&queryBeans[i].getColunmName()!=null&&queryBeans[i].getEqs()!=null)
				{
				sql+=queryBeans[i].toString();//把值放到？中避免单引号的处理，但是需要把传入的所有值放到一个数组里
				values.add(queryBeans[i].getValue());
				}
			}//约束条件添加完毕

			groupBy:for (int i = 0; i < queryBeans.length; i++) {
				if(queryBeans[i]==null)continue;
				if(queryBeans[i].getGroupBy()!=null){
					sql+=" GROUP BY "+queryBeans[i].getGroupBy();
					break groupBy;
				}
			}
			
			
		sql+=" ORDER BY NULL";//从此处开始添加排序条件
		for (int i = 0; i < queryBeans.length; i++) {//若有排序条件
			if(queryBeans[i]==null)continue;
			if(queryBeans[i].getOrderBy()!=null&&queryBeans[i].getAsc()!=null){//若用户定义了排序条件
				sql+=" , "+queryBeans[i].getOrderBy()+" "+queryBeans[i].getAsc();//把第一个条件拿出来
			}
		}
		
		}else{//若没有筛选条件
			sql+="* FROM "+this.getTableName();
		}
		if(rowCount>0&&pageNum>0){//若有分页要求，如果只限制抽取数量就把页数填1
		sql+=" LIMIT "+rowCount*(pageNum-1)+","+rowCount;
		}
		
		try {
			list = ExeStandard.getAll(this.getType(), sql, values.toArray());//把一堆值穿进去替换占位符

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("列名不存在");
		}
		
		return list;
	}

	public static String[] getColumNames(Table table){
		if (table == null){
			System.out.print("反射出错！");
			return null;
		}
		Field[] fields = table.getClass().getFields();
		String[] names = new String[fields.length];

		for(int i=0;i<fields.length;i++){
			try {
//				System.out.println("发现列："+fields[i].getName());
				names[i] = fields[i].get(table).toString();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return names;
	}

	/**
	 * 本函数是静态的，作用是从一个既有的object里面获取其id值，当然首先里面要有
	 * 本函数不需要查数据库，可减少连接
	 * 方法是通过table对象的记载的主键名，加上set获得一个方法，执行这个方法得到id
	 * @param myTable
	 * @return
	 */
	public static Object getObjectId(Table myTable) {
		if(myTable==null){
			System.out.println("传入的table对象为null，请检查");
			return null;
		}
		Class tableClass = myTable.getClass();
		//获得主键的列名
		String primaryName=myTable.givePrimaryColumnName();
		Method method = null;
		try {
			method = tableClass.getMethod("get"+primaryName.substring(0,1).toUpperCase()+primaryName.substring(1).toLowerCase());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			System.out.println("列名写错");
			e.printStackTrace();
			return null;
		}
		Object id = null;
		try {
			id = method.invoke(myTable);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			System.out.println("调用方法的对象里面恐怕没这个方法");
			e.printStackTrace();
		}
		return id;
	}
	/**
	 * 返回符合条件的id，Object数组
	 * @param queryBeans
	 * @return
	 */
	public  List<Object> selectIds(QueryBean...queryBeans) {
		// TODO Auto-generated method stub
		
		if(queryBeans!=null&&queryBeans.length>0){
			return selectIds(0, 1, queryBeans);
		}else{
			return selectIds(0, 1, null);
		}
		//本函数的调用方法
	}
	public List<Object> selectIds (int rowCount,int pageNum,QueryBean...queryBeans){//通过查询条件找到所有符合调节的id值，便于删除和修改
		if(rowCount<0||pageNum<1){
			System.out.println("传入的查询参数错误，请检查");
			return null;
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		//Object[] values = new Object[queryBeans.length];
		
		String sql = "SELECT "+getPrimaryColumnName();
		sql += " FROM "+this.getTableName()+" WHERE 1=1 ";
		List<Object> values=new ArrayList<Object>();
		if(queryBeans!=null&&queryBeans.length>0){
			values = new ArrayList<Object>();
			for (int i = 0; i < queryBeans.length; i++) {
				if(queryBeans[i]==null)continue;
				InBean inbean = queryBeans[i].getInbean();
				if(inbean!=null){
					sql+=inbean.toString();
					Object[] inbeanVals = inbean.getValues();
					for (int j = 0; j < inbeanVals.length; j++) {
						if(inbeanVals[j]==null) continue;
						values.add(inbeanVals[j]);
					}
				}
			}
			for (int i = 0; i < queryBeans.length; i++) {
				if(queryBeans[i]==null)continue;
				if(queryBeans[i].getValue()!=null&&queryBeans[i].getAon()!=null&&queryBeans[i].getColunmName()!=null&&queryBeans[i].getEqs()!=null){
					sql+=queryBeans[i].toString();//把值放到？中避免单引号的处理，但是需要把传入的所有值放到一个数组里
					values.add(queryBeans[i].getValue());
				}
			}
			groupBy:for (int i = 0; i < queryBeans.length; i++) {
				if(queryBeans[i]==null)continue;
				if(queryBeans[i].getGroupBy()!=null){
					sql+=" GROUP BY "+queryBeans[i].getGroupBy();
					break groupBy;
				}
			}
			sql+=" ORDER BY NULL";//从此处开始添加排序条件
			for (int i = 0; i < queryBeans.length; i++) {//若有排序条件
				if(queryBeans[i]==null)continue;
				if(queryBeans[i].getOrderBy()!=null&&queryBeans[i].getAsc()!=null){//若用户定义了排序条件
					sql+=" , "+queryBeans[i].getOrderBy()+" "+queryBeans[i].getAsc();//把第一个条件拿出来
				}
			}
		}
		if(rowCount>0&&pageNum>0){//若有分页要求，如果只限制抽取数量就把页数填1
			sql+=" LIMIT "+rowCount*(pageNum-1)+","+rowCount;
		}
		list =  ExeStandard.getEveryThing(sql, values.toArray());
		List<Object> outList = new ArrayList<Object>(); 
		for (int i = 0; i < list.size(); i++) {
			outList.add(list.get(i).get(this.getPrimaryColumnName()));//list返回的是一个map列名为键，通过列名找对应的值
		}
		return outList;
	}
	
	public Object getAllById(Object id){
		if(id==null||this.getPrimaryColumnName()==null){
			System.out.println("传入的id为null,或没规定主键");
			return null;
		}
		List<Object> list= select(new QueryBean(this.getPrimaryColumnName(), id));
		if(list==null||list.size()==0)return null;
		return list.get(0);
	}
	
	/**
	 * 本方法是通过一堆id获得一行的全部信息,有分页功能，但注意传入ids的顺序
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List getAllByIds(Object...ids) {
		if(ids!=null&&ids.length>0){
			return getAllByIds(0, 1, ids);
		}else{
			return null;
		}
	}
	public List getAllByIds(int rowCount,int pageNum,Object...ids) {
		if(rowCount<0||pageNum<1){
			System.out.println("传入的查询参数错误，请检查");
			return null;
		}
		if(ids==null||ids.length==0){
			System.out.println("没有传入要查询的ids");
			return null;
		}
		List<Object> values = new ArrayList<Object>();
		String sql = "SELECT * FROM "+this.getTableName()+" WHERE "+getPrimaryColumnName()+" IN (";
		if(rowCount==0){//如果没有分页要求
			for (int i = 0; i < ids.length; i++) {
				if(ids[i]==null)continue;
				sql+="?,";
				values.add(ids[i]);
			}
		}else {//如果有分页要求
			for(int i=rowCount*(pageNum-1);i<rowCount*(pageNum-1)+rowCount&&i<ids.length;i++){
				if(ids[i]==null)continue;
				sql+="?,";
				values.add(ids[i]);
			}
		}
		if(values.size()==0)return null;
		sql=sql.substring(0,sql.length()-1);//去掉末尾的逗号
		sql+=")";
		return ExeStandard.getAll(this.getType(),sql, values.toArray());
	}
	
	
	public List<Map<String, Object>> selectAnything(String sql,Object...args) {
		return ExeStandard.getEveryThing(sql, args);
	}

	public List selectBySQL(String sql,Object...values){
		try {
			return ExeStandard.getAll(this.getType(), sql, values);//把一堆值穿进去替换占位符
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("列名不存在");
			return null;
		}

	}

	/**
	 * 得出特定查询条件下返回结果的行数
	 * @param queryBeans
	 * @return
	 */
	public long selectCount(QueryBean...queryBeans) {
		String sql = "SELECT COUNT(*) AS situnum";
		sql += " FROM "+this.getTableName()+" WHERE 1=1 ";
		List<Object> values=new ArrayList<Object>();
		for (int i = 0; i < queryBeans.length; i++) {
			if(queryBeans[i]==null)continue;
			InBean inbean = queryBeans[i].getInbean();
			if(inbean!=null){
				sql+=inbean.toString();
				Object[] inbeanVals = inbean.getValues();
				for (int j = 0; j < inbeanVals.length; j++) {
					if(inbeanVals[j]==null) continue;
					values.add(inbeanVals[j]);
				}
			}
		}
		if(queryBeans!=null&&queryBeans.length>0){
			//values = new Object[queryBeans.length];
			for (int i = 0; i < queryBeans.length; i++) {
				if(queryBeans[i]==null)continue;
				if(queryBeans[i].getValue()!=null&&queryBeans[i].getAon()!=null&&queryBeans[i].getColunmName()!=null&&queryBeans[i].getEqs()!=null){
					sql+=queryBeans[i].toString();//把值放到？中避免单引号的处理，但是需要把传入的所有值放到一个数组里
					values.add(queryBeans[i].getValue());
				}
			}
		}
		Map<String, Object> map = ExeStandard.getEveryThing(sql, values.toArray()).get(0);
		return (Long)map.get("situnum");
	}
	
	/**
	 * 按照一定查询条件得到分页的最大页数
	 * @param pageSize
	 * @param queryBeans
	 * @return
	 */
	public long MaxPageNum(int pageSize,QueryBean...queryBeans){
		if(pageSize<=0){return 10000;}
		long allItems = selectCount(queryBeans);
		long maxPage = 0;
		if(allItems%pageSize!=0){
			maxPage= allItems/pageSize+1;
		}else{
			maxPage= allItems/pageSize;
		}
		return maxPage;
	}
	
	/**
	 * 获取本table中所有以get方法开头的函数，用于获取table中的值
	 * @return
	 */
	public List<Method> getMethodList()  {
		List<Method> getMethodsList = new ArrayList<Method>();
		Method[] myMethods =  getType().getMethods();
		 //把所有以get开头的方法取出来，放在一个list里面
			for(Method m:myMethods){
				if(m.getName().startsWith("get")&&!m.getName().equals("getClass")){
					getMethodsList.add(m);
				}
			}
		return getMethodsList;
	}
	/**
	 * 通过表名和列名来检查用户名是否已存在，如果存在返回false，不存在返回true
	 * 自带底层代码，为的是加快查询速度减少内存开销
	 * @param columnName
	 * @param username
	 * @param shortTableName
	 * @return
	 * @throws SQLException
	 */
	public  boolean checkDuplicate(String columnName,Object username){
		if(columnName==null||username==null){
			System.out.println("传入为空");
			return false;
		}
		String sql = "select COUNT(*) as num from "+getTableName()+" where "+columnName+" = ?";
		System.out.println("sql:"+sql);
		System.out.println("1:"+username);
		Connection connection=null;
		try {
			connection = AddConnection.getConnection();
		 
		PreparedStatement pstatement =  connection.prepareStatement(sql);
		pstatement.setObject(1, username);
		ResultSet resultSet = pstatement.executeQuery();
		resultSet.next();
		if(resultSet.getInt("num")==0){return true;}else{return false;}
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("传入参数格式不正确");
			e.printStackTrace();
			return false;
		}finally{
			if(connection!=null){
				AddConnection.closeConnection(connection);
			}
		}
	}
	
	/**
	 * 检测一个复合主键表有没有已存在的值，自带底层代码，加快查询速度，减少内存开销
	 * @param columnName1 左侧主键
	 * @param columnName2 右侧主键
	 * @param username1
	 * @param username2
	 * @param shortTableName
	 * @return
	 * @throws SQLException
	 */
	public  boolean checkDualDuplicate(Object username1,Object username2) {
		if(username1==null||username2==null||getDualPrimaryName()==null){
			System.out.println("传入为空");
			return false;
		}
		String sql = "select COUNT(*) as num from "+getTableName()+" where "+getDualPrimaryName()[0]+" = ? and "+getDualPrimaryName()[1]+"=?";
		System.out.println("sql:"+sql);
		System.out.println("1:"+username1);
		System.out.println("2:"+username2);
		Connection connection = null;
		try {
			connection = AddConnection.getConnection();
		
		PreparedStatement pstatement =  connection.prepareStatement(sql);
		pstatement.setObject(1, username1);
		pstatement.setObject(2, username2);
		ResultSet resultSet = pstatement.executeQuery();
		
		resultSet.next();
		if(resultSet.getInt("num")==0){return true;}else{return false;}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("传入参数格式错误");
			e.printStackTrace();
			return false;
		}finally{
			if(connection!=null)AddConnection.closeConnection(connection);
		}
	}

	/**
	 * 用于省市县三级联动这种表格，根据id和列名查询出所有子元素的name,返回一个由中文名组成的json数组字符串
	 * @param id
	 * @param tableDao
	 * @param colomnBelong
	 * @return
	 */
	public String getChildrenJson(Object id,String colomnBelong){
		List list = select(new QueryBean(colomnBelong, id));
		JsonFactory jsonFactory = new JsonFactory();
		jsonFactory.addTable(null,(Collection)list);
		System.out.println(jsonFactory.getArrayFormat());
		return jsonFactory.getArrayFormat();
	}
	/**
	 * 用于省市县三级联查的表格，给出一个子元素的id，返回他的父元素对象
	 * 如果没有父元素，就返回null
	 * @param id 要查找的id值
	 * @param colomnbelong 记载所属关系的列名
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	public Object getFatherObject(Object id,String colomnBelong) {
		if(id!=null){
			Object source =getAllById(id);
			Method nameMethod = null;
			try {
				nameMethod = getType().getMethod("get"+colomnBelong.substring(0, 1).toUpperCase()+colomnBelong.substring(1,colomnBelong.length()).toLowerCase());
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Object superOrign = null;
			try {
				superOrign = getAllById(nameMethod.invoke(source));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(superOrign!=null){
				return superOrign;
			}else{
				return null;
			}
			
		}else{
			return null;
		}
	}
	/**
	 * 对于省市县三级表，传入一个父类的类型id，返回属于这个父类所有的子类和孙类的type id
	 * 通过传入一个type_id,返回这个直接属于这个id所有子id并存到一个list里,这是一个递归
	 * 
	 * @param type
	 * list 用于存放结果的list
	 * @return
	 */
	public void getChildrenIds(Object type,List list,String col_belong) {
		List<Object> chilIds = selectIds(new QueryBean(col_belong, type));
		list.addAll(chilIds);
		for (Object object : chilIds) {
			getChildrenIds(object, list,col_belong);
		}
	}
}
