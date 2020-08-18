package com.sundy.db.action;

import java.util.Date;

import com.sundy.db.entity.PersistEntity;
import com.sundy.db.session.Session;

@SuppressWarnings("serial")
public class DbActionDelete implements DbAction {

	private Boolean isLogic;
	
	private PersistEntity persistEntity; 
	
	public DbActionDelete(boolean isLogic, PersistEntity persistEntity) {
		this.isLogic = isLogic;
		this.persistEntity = persistEntity;
		if(isLogic){
			persistEntity.setUpdateTime(new Date());
			persistEntity.setStatus(0);
		}
	}
	
	public boolean isLogic() {
		return isLogic;
	}


	public void setLogic(boolean isLogic) {
		this.isLogic = isLogic;
	}


	public PersistEntity getPersistEntity() {
		return persistEntity;
	}


	public void setPersistEntity(PersistEntity persistEntity) {
		this.persistEntity = persistEntity;
	}


	@Override
	public void execute(Session session) {
		if(isLogic){
			session.update(persistEntity);
		} else {
			session.delete(persistEntity);
		}

	}

}
