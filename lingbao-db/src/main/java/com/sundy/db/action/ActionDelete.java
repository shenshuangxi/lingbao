package com.sundy.db.action;

import java.util.Date;

import com.sundy.db.entity.PersistEntity;
import com.sundy.db.session.Session;

@SuppressWarnings("serial")
public class ActionDelete implements DbAction {

	private boolean isLogic;
	
	private PersistEntity persistEntity; 
	
	public ActionDelete(PersistEntity persistEntity) {
		this(false, persistEntity);
	}
	
	public ActionDelete(boolean isLogic, PersistEntity persistEntity) {
		this.isLogic = isLogic;
		this.persistEntity = persistEntity;
		if(isLogic){
			persistEntity.setUpdateTime(new Date());
			persistEntity.setStatus(0);
		}
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
