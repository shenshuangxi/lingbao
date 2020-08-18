package com.sundy.db.action;

import java.util.Date;

import com.sundy.db.entity.PersistEntity;
import com.sundy.db.session.Session;

@SuppressWarnings("serial")
public class DbActionUpdate implements DbAction {

	private PersistEntity persistEntity; 
	
	public DbActionUpdate(PersistEntity persistEntity) {
		this.persistEntity = persistEntity;
		persistEntity.setUpdateTime(new Date());
	}
	
	public PersistEntity getPersistEntity() {
		return persistEntity;
	}

	public void setPersistEntity(PersistEntity persistEntity) {
		this.persistEntity = persistEntity;
	}

	@Override
	public void execute(Session session) {
		session.update(persistEntity);

	}

}
