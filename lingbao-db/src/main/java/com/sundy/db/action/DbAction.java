package com.sundy.db.action;

import java.io.Serializable;

import com.sundy.db.session.Session;

public interface DbAction extends Serializable {

	void execute(Session session);
	
}
