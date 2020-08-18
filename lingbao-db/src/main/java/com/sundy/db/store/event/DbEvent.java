package com.sundy.db.store.event;

import java.io.Serializable;

import com.sundy.db.session.Session;

public interface DbEvent extends Serializable {

	void execute(Session session);

}
