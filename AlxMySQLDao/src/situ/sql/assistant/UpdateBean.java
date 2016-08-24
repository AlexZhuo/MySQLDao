package situ.sql.assistant;

public class UpdateBean {
	private String colunmName;//列名
	private Object Value;//修改成的值
	private Object id;//id可以为字符串或者数字
	
	public UpdateBean(String colunmName, Object value, Object id) {//有参构造函数，便于赋值
		super();
		this.colunmName = colunmName;
		Value = value;
		this.id = id;
	}
	
	@Override
	public String toString() {
		return getColunmName()+" =?,";
	}

	public Object getId() {
		return id;
	}
	public void setId(Object id) {
		this.id = id;
	}
	public String getColunmName() {
		return colunmName;
	}
	public void setColunmName(String colunmName) {
		this.colunmName = colunmName;
	}
	public Object getValue() {
		return Value;
	}
	public void setValue(Object value) {
		Value = value;
	}
	
	
}
