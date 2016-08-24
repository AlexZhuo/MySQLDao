package situ.sql.assistant;

public class QueryBean {
	private String and =null;
	private String colunmName=null;
	private String eqs=null;
	private Object Value=null;
	private String[] selectedCols=null;
	private String orderBy=null;
	private String asc=null;
	private String groupBy=null;
	private InBean inbean = null;
	
	
	
	public InBean getInbean() {
		return inbean;
	}
	public void setInbean(InBean inbean) {
		this.inbean = inbean;
	}
	public void makeInbean(String colName, Object...values) {
		this.inbean = new InBean(colName, values);
	}
	@Override
	public String toString() {
		return getAon()+" "+getColunmName()+" "+getEqs()+" ? ";
	}
	public QueryBean(String and, String colunmName, String eqs, Object value,String...selectedObjects) {//能够选择返回列
		super();
		this.and = and;
		this.colunmName = colunmName;
		this.eqs = eqs;
		Value = value;
		this.selectedCols = selectedObjects;
	}
	public QueryBean(String and, String colunmName, String eqs, Object value) {//不能选择返回列
		super();
		this.and = and;
		this.colunmName = colunmName;
		this.eqs = eqs;
		Value = value;
	}
	public QueryBean(String colunmName,Object value){//默认逻辑为and，条件为=,不能选择返回列的快捷方式
		this.and = "AND";
		this.colunmName = colunmName;
		this.eqs = "=";
		Value = value;
	}
	public QueryBean(){};
	
	public final static String  AND = "AND";
	public final static String  OR = "OR";
	public final static String  BIGGER = ">";
	public final static String  LESS = "<";
	public final static String  IN = "IN";
	public final static String  NOTIN = "NOT IN";
	
	public String[] getSelectedCols() {
		return selectedCols;
	}
	public void setSelectedCols(String...selectedCols) {
		this.selectedCols = selectedCols;
	}
	
	
	
	
	public String getGroupBy() {
		return groupBy;
	}
	public QueryBean setGroupBy(String groupBy) {
		this.groupBy = groupBy;
		return this;
	}
	public String getAon() {
		return and;
	}
	public void setAon(String aon) {
		this.and = aon;
	}
	public String getColunmName() {
		return colunmName;
	}
	public void setColunmName(String colunmName) {
		this.colunmName = colunmName;
	}
	public String getEqs() {
		return eqs;
	}
	public void setEqs(String eqs) {
		this.eqs = eqs;
	}
	public Object getValue() {
		return Value;
	}
	public void setValue(Object value) {
		Value = value;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public QueryBean  setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	public String getAsc() {
		return asc;
	}
	public QueryBean setAsc(String asc) {
		this.asc = asc;
		return this;
	}
	public QueryBean setOrderBy(String columnName,String asc){
		setOrderBy(columnName);
		setAsc(asc);
		return this;
	}

	
	
}
