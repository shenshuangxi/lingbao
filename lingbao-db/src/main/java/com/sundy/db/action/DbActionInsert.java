package com.sundy.db.action;

import java.util.Date;

import com.sundy.db.entity.PersistEntity;
import com.sundy.db.entity.RevisionEntity;
import com.sundy.db.session.Session;

@SuppressWarnings("serial")
public class DbActionInsert implements DbAction {

	private PersistEntity persistEntity;
	
	public DbActionInsert(PersistEntity persistEntity) {
		Date currentTime = new Date();
		persistEntity.setCreateTime(currentTime);
		persistEntity.setUpdateTime(currentTime);
		persistEntity.setStatus(1);
		if(persistEntity instanceof RevisionEntity){
			((RevisionEntity) persistEntity).setRevision(0l);
			((RevisionEntity) persistEntity).setNextRevision(1l);
		}
		this.persistEntity = persistEntity;
	}
	
	public PersistEntity getPersistEntity() {
		return persistEntity;
	}

	public void setPersistEntity(PersistEntity persistEntity) {
		this.persistEntity = persistEntity;
	}

	@Override
	public void execute(Session session) {
		session.insert(persistEntity);

	}

}
