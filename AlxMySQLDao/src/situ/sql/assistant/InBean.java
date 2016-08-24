package situ.sql.assistant;

public class InBean {
	public String AND = null;
	public String colName = null;
	public String IN = null;
	public Object[] values = null;
	public Object[] getValues() {
		return values;
	}
	public void setValues(Object[] values) {
		this.values = values;
	}
	public InBean(String and, String colName, String iN, Object...values) {
		AND = and;
		this.colName = colName;
		IN = iN;
		this.values = values;
	}
	public InBean(String colName, Object...values) {
		AND = "AND";
		this.colName = colName;
		IN = "IN";
		this.values = values;
	}
	public InBean() {}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(' ').append(AND).append(' ');
		sBuffer.append(colName).append(' ').append(IN).append(' ');
		sBuffer.append('(');
		for (int i = 0; i < values.length; i++) {
			if(values[i]==null)continue;
			sBuffer.append("?,");
		}
		sBuffer.deleteCharAt(sBuffer.length()-1);
		sBuffer.append(") ");
		return sBuffer.toString();
	}
	
}
