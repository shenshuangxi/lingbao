package com.sundy.lingbao.client.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import com.sundy.lingbao.client.listeners.ConfigChangeListener;
import com.sundy.lingbao.core.consts.GloablConst;

public abstract class AbstractConfig implements Config {

	private ReentrantLock lock = new ReentrantLock();
	private AtomicBoolean isStart = new AtomicBoolean(false);
	protected List<ConfigChangeListener> configChangeListeners = new ArrayList<ConfigChangeListener>();
	protected String appId;
	protected String registerServerHost;
	
	@Override
	public String getAppId() {
		return appId;
	}

	@Override
	public void setAppId(String appId) {
		this.appId = appId;
	}

	@Override
	public String getRegisterServerHost() {
		return registerServerHost;
	}

	@Override
	public void setRegisterServerHost(String registerServerHost) {
		this.registerServerHost = registerServerHost;
	}
	
	@Override
	public List<String> getLocations() {
		lock.lock();
		try{
			if (isStart.compareAndSet(false, true)) {
				tryGetConfig();
			}
		} finally {
			lock.unlock();
		}
		List<String> locations = new ArrayList<String>();
		File file = new File(GloablConst.configFileDir);
		for (File temp : file.listFiles()) {
			locations.add(temp.toURI().toString());
		}
		return locations;
	}
	
	@Override
	public void register(ConfigChangeListener configChangeListener) {
		configChangeListeners.add(configChangeListener);
		Collections.sort(configChangeListeners, new Comparator<ConfigChangeListener>() {
			@Override
			public int compare(ConfigChangeListener o1, ConfigChangeListener o2) {
				return o1.getOrdered()-o2.getOrdered();
			}
		});
	}


	protected abstract void tryGetConfig();

}
