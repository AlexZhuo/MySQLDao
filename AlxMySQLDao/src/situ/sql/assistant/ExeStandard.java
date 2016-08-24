package situ.sql.assistant;

import java.util.List;
import java.util.Map;

public interface ExeStandard {
	public boolean ExeDao(String sql,Object...objects);
	public List getAll(Class model,String sql,Object...args);
	public List<Map<String, Object>> getEveryThing(String sql,Object...args);
}
