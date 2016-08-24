package situ.sql.assistant;


public class SqlBuffer extends TableDao {
	
	TableDao tableDao = null;
	ExeUpdate exeUpdate = null;
	public ExeUpdate getExeUpdate() {
		return exeUpdate;
	}
	public void setExeUpdate(ExeUpdate exeUpdate) {
		this.exeUpdate = exeUpdate;
	}
	public SqlBuffer(TableDao tableDao){
		this.exeUpdate = new ExeUpdate();
		this.ExeStandard =this.exeUpdate;
		this.tableDao = tableDao;
	}
	public SqlBuffer(Table clientTable){
		this.tableDao = new TableDao(clientTable);
		this.exeUpdate = new ExeUpdate();
		this.ExeStandard =this.exeUpdate;
	}
	public Class getType() {
		// TODO Auto-generated method stub
		
		return tableDao.getType();
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return tableDao.getTableName();
	}
	public String getPrimaryColumnName(){
		return tableDao.getPrimaryColumnName();
	}
	public boolean flush(){
		return exeUpdate.flush();
	}
	
}
