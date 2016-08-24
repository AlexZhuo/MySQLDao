package situ.sql.assistant;

public class ProcessBean {
	private int index;
	private Object[] args;
	public ProcessBean(int index, Object[] args) {
		super();
		this.index = index;
		this.args = args;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	
	
}
