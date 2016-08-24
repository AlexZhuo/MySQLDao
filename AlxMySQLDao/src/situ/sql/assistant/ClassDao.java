package situ.sql.assistant;

import java.util.List;

public interface ClassDao {
	Class getType();
	String getTableName();
	List select(QueryBean[] queryBeans);
	List select(int rowCount,int pageNum,QueryBean...queryBeans);
	boolean update(Object...objs);
	String getPrimaryColumnName();
	boolean insert(Object...objs);
	boolean Delete(Object[] ids);
	
}
