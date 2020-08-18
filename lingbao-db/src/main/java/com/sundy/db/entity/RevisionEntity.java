package com.sundy.db.entity;

public interface RevisionEntity extends PersistEntity {

	Long getRevision();
	void setRevision(Long revision);
	
	Long getNextRevision();
	void setNextRevision(Long nextRevision);
	
	
}
