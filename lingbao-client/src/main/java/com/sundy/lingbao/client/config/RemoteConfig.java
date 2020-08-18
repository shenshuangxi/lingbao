package com.sundy.lingbao.client.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.TypeReference;
import com.sundy.lingbao.client.listeners.ConfigChangeListener;
import com.sundy.lingbao.client.util.HttpRequest;
import com.sundy.lingbao.client.util.HttpResponse;
import com.sundy.lingbao.client.util.HttpUtil;
import com.sundy.lingbao.core.consts.GloablConst;
import com.sundy.lingbao.core.dto.ServiceDto;
import com.sundy.lingbao.core.foundation.Foundation;
import com.sundy.lingbao.core.schdule.LogarithmicSchedultPolicy;
import com.sundy.lingbao.core.schdule.SchedulePolicy;
import com.sundy.lingbao.core.util.LingbaoThreadFactory;

public class RemoteConfig extends AbstractConfig {

	private static final Logger logger = LoggerFactory.getLogger(Config.class);
	
	private List<ConfigChangeListener> configChangeListeners = new ArrayList<ConfigChangeListener>();
	private List<ServiceDto> serviceDtos = new ArrayList<ServiceDto>();
	private ReentrantLock serviceLock = new ReentrantLock();
	private SchedulePolicy resiterServerschedulePolicy;
	private ScheduledExecutorService resiterServersScheduledExecutorService;
	private long releaseMessageId = -1;
	private SchedulePolicy longPollingConfigServerschedulePolicy;
	private ScheduledExecutorService longPollingConfigServersScheduledExecutorService;
	
	public RemoteConfig() {
		resiterServersScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(LingbaoThreadFactory.create("lingbao-register-service", false));
		resiterServerschedulePolicy = new LogarithmicSchedultPolicy(5, 64);
		longPollingConfigServersScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(LingbaoThreadFactory.create("lingbao-long-polling", false));
		longPollingConfigServerschedulePolicy = new LogarithmicSchedultPolicy(5, 64);
	}
	
	@Override
	public void stop() {
		resiterServersScheduledExecutorService.shutdown();
		longPollingConfigServersScheduledExecutorService.shutdown();
	}


	private class LongPollingConfigTask implements Runnable {
		@Override
		public void run() {
			boolean isOk = false;
			try {
				List<ServiceDto> currentServiceDtos = new ArrayList<ServiceDto>();
				serviceLock.lock();
				try {
					currentServiceDtos.addAll(serviceDtos);
					Collections.shuffle(currentServiceDtos);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					serviceLock.unlock();
				}
				for (ServiceDto serviceDto : currentServiceDtos) {
					try {
						Long currentReleaseMessageId = getCurrentReleaseMessageId();
						String params = "?appId="+RemoteConfig.this.appId+"&releaseMessageId="+currentReleaseMessageId+"&clientIp="+Foundation.net().getHostAddress();
						HttpRequest httpRequest = new HttpRequest(parseHost(serviceDto)+"api/v1/notification"+params, 60000, GloablConst.LONG_POLLING_READ_TIMEOUT*2);
						HttpResponse<Long> httpResponse = HttpUtil.doGet(httpRequest, Long.class);
						if (httpResponse.getStatusCode()==200) {
							rewriteLocalConfigFile(serviceDto, httpResponse);
							isOk = true;
							break;
						} else if (httpResponse.getStatusCode()==304) {
							isOk = true;
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
						if (canRetry(e)) {
							logger.warn("retry it", e);
						} else {
							throw e;
						}
					}
				}
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
			}
			if (isOk) {
				RemoteConfig.this.longPollingConfigServersScheduledExecutorService.schedule(new LongPollingConfigTask(), longPollingConfigServerschedulePolicy.success(), TimeUnit.SECONDS);
			} else {
				RemoteConfig.this.longPollingConfigServersScheduledExecutorService.schedule(new LongPollingConfigTask(), longPollingConfigServerschedulePolicy.fail(), TimeUnit.SECONDS);
			}
		}

		private void rewriteLocalConfigFile(ServiceDto serviceDto, HttpResponse<Long> httpResponse) throws Throwable {
			try {
				Long releaseMessageId = httpResponse.getBody();
				String params = "?appId="+RemoteConfig.this.appId+"&releaseMessageId="+releaseMessageId+"&clientIp="+Foundation.net().getHostAddress();
				HttpRequest httpRequest = new HttpRequest(parseHost(serviceDto)+"api/v1/configfile"+params, 60000, 60000);
				HttpResponse<Map<String,Map<Integer, String>>> configHttpResponse = HttpUtil.doGet(httpRequest, new TypeReference<Map<String,Map<Integer, String>>>(){});
				if (configHttpResponse.getBody()!=null) {
					String configDir = GloablConst.configFileDir;
					File configFileDir = new File(configDir);
					if (!configFileDir.exists()) {
						configFileDir.mkdirs();
					}
					
					File tempConfigFileDir = new File(configFileDir+File.separator+"temp");
					if (!tempConfigFileDir.exists()) {
						tempConfigFileDir.mkdirs();
					}
					
					for (Entry<String, Map<Integer, String>> fileEntry : configHttpResponse.getBody().entrySet()) {
						String fileName = fileEntry.getKey();
						Map<Integer, String> fileContents = fileEntry.getValue();
						File configFile = new File(tempConfigFileDir.getPath()+File.separator+fileName);
						configFile.createNewFile();
						String[] contents = new String[fileContents.size()];
						for (Entry<Integer, String> contentEntry : fileContents.entrySet()) {
							Integer line = contentEntry.getKey()-1;
							String value = contentEntry.getValue();
							contents[line] = value;
						}
						BufferedWriter bw = null;
						try {
							bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));
							for (String value : contents) {
								bw.write(value);
								bw.newLine();
							}
							bw.flush();
						} finally {
							if (bw!=null) {
								bw.close();
							}
						}
					}
					
					for (File file : configFileDir.listFiles()) {
						if (file.isFile()) {
							file.delete();
						}
					}
					for (File file : tempConfigFileDir.listFiles()) {
						file.renameTo(new File(configFileDir+File.separator+file.getName()));
					}
					tempConfigFileDir.delete();
				}
				setCurrentReleaseMessageId(releaseMessageId);
				fireConfigChange();
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
				throw e;
			}
		}
	}
	
	
	private class RefreshRegisterServerTask implements Runnable {
		@Override
		public void run() {
			serviceLock.lock();
			try {
				boolean isOk = false;
				try {
					HttpRequest httpRequest = new HttpRequest(RemoteConfig.this.registerServerHost+"/services/config", 60000, 60000);
					HttpResponse<List<ServiceDto>> httpResponse = HttpUtil.doGet(httpRequest, new TypeReference<List<ServiceDto>>(){});
					if (httpResponse.getBody()!=null && httpResponse.getBody().size()>0) {
						serviceDtos = new ArrayList<ServiceDto>(httpResponse.getBody());
						isOk = true;
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
				if (isOk) {
					RemoteConfig.this.resiterServersScheduledExecutorService.schedule(new RefreshRegisterServerTask(), resiterServerschedulePolicy.success(), TimeUnit.SECONDS);
				} else {
					RemoteConfig.this.resiterServersScheduledExecutorService.schedule(new RefreshRegisterServerTask(), resiterServerschedulePolicy.fail(), TimeUnit.SECONDS);
				}
			} finally {
				serviceLock.unlock();
			}
		}
	}
	
	public  void register(ConfigChangeListener listener) {
		configChangeListeners.add(listener);
	}
	
	private String parseHost(ServiceDto serviceDto) {
	    return serviceDto.getHomepageUrl();
	}
	
	private Long getCurrentReleaseMessageId() {
		if (this.releaseMessageId==-1) {
			FileInputStream fis = null;
			try {
				File releaseMessageProperties = new File(GloablConst.configFileDir+File.separator+"releaseMessage.properties");
				if (releaseMessageProperties.exists()&&releaseMessageProperties.isFile()) {
					fis = new FileInputStream(releaseMessageProperties);
					Properties properties = new Properties();
					properties.load(fis);
					this.releaseMessageId = Long.parseLong(properties.getProperty("releaseMessageId"));
				}
			} catch (Throwable e) {
				logger.error("get local releaseMessageId failed", e);
			} finally {
				if (fis!=null) {
					try {
						fis.close();
					} catch (IOException e) {}
				}
			}
		} 
		return this.releaseMessageId;
	}
	
	private void setCurrentReleaseMessageId(Long releaseMessageId) throws Throwable {
		FileOutputStream fops = null;
		try {
			File releaseMessageProperties = new File(GloablConst.configFileDir+File.separator+"releaseMessage.properties");
			if (!releaseMessageProperties.exists())  {
				releaseMessageProperties.createNewFile();
			}
			fops = new FileOutputStream(releaseMessageProperties);
			Properties properties = new Properties();
			properties.put("releaseMessageId", ""+releaseMessageId);
			properties.store(fops, DateFormat.getDateInstance().format(Calendar.getInstance().getTime()));
		} catch (Throwable e) {
			logger.error("store the releaseMessageId error", e);
			throw e;
		} finally {
			if (fops!=null) {
				try {
					fops.close();
				} catch (IOException e) {}
			}
		}
		this.releaseMessageId = releaseMessageId;
	}
	
	private boolean canRetry(Throwable e) {
	    Throwable nestedException = e.getCause();
	    return nestedException instanceof SocketTimeoutException;
	}
	
	private boolean isStart = false;
	private ReentrantLock startLock = new ReentrantLock();
	
	@Override
	protected void tryGetConfig() {
		if (!isStart) {
			startLock.lock();
			try {
				if(!isStart) {
					new RefreshRegisterServerTask().run();
					new LongPollingConfigTask().run();
					isStart = true;
				}
			} finally {
				startLock.unlock();
			}
		}
	}
	
	private void fireConfigChange() {
		if (isStart) {
			for (ConfigChangeListener configChangeListener : configChangeListeners) {
				configChangeListener.onConfigChange();
			}
		}
	}

}
