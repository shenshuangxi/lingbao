package com.sundy.lingbao.file.old.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.sundy.lingbao.core.dto.ServiceDto;
import com.sundy.lingbao.core.util.LingbaoThreadFactory;
import com.sundy.lingbao.file.old.config.FileConfig;

@Component
public class FileServiceAddressLocator {

	private final static Logger logger = LoggerFactory.getLogger(FileServiceAddressLocator.class);
	
	@Autowired
	private FileConfig fileConfig;
	
	private List<ServiceDto> cacheReadService = new ArrayList<ServiceDto>();
	
	private List<ServiceDto> cacheWriteService = new ArrayList<ServiceDto>();
	
	
	private ReentrantLock readCacheLock = new ReentrantLock();
	private ReentrantLock writeCacheLock = new ReentrantLock();
	
	private ScheduledExecutorService scheduledExecutorService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@PostConstruct
	public void init() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(LingbaoThreadFactory.create("FileServiceAddressLocator", true));
		scheduledExecutorService.schedule(new RefreshFileServiceAddressTask(), fileConfig.getServiceLocatorNormalDelay(), TimeUnit.MILLISECONDS);
	}
	
	private class RefreshFileServiceAddressTask implements Runnable {
		@Override
		public void run() {
			boolean readSuccess = true;
			try {
				List<String> services = FileServiceAddressLocator.this.fileConfig.getFileServices();
				if (Objects.nonNull(services)) {
					List<ServiceDto> serviceDtos = new ArrayList<ServiceDto>();
					for (String service : services) {
						try {
							ServiceDto serviceDto = restTemplate.getForObject(service, ServiceDto.class);
							serviceDtos.add(serviceDto);
						} catch (Throwable e) {
							readSuccess = false;
							e.printStackTrace();
							logger.error("file service connect error", e);
						}
					}
					
					try {
						FileServiceAddressLocator.this.readCacheLock.lock();
						FileServiceAddressLocator.this.cacheReadService.clear();
						FileServiceAddressLocator.this.cacheReadService.addAll(serviceDtos);
						Collections.sort(FileServiceAddressLocator.this.cacheReadService, new Comparator<ServiceDto>() {
							@Override
							public int compare(ServiceDto o1, ServiceDto o2) {
								return o1.getReadWeight() - o2.getReadWeight();
							}
						});
					} finally {
						FileServiceAddressLocator.this.readCacheLock.unlock();
					}
					
					try{
						FileServiceAddressLocator.this.writeCacheLock.lock();
						FileServiceAddressLocator.this.cacheWriteService.clear();
						FileServiceAddressLocator.this.cacheWriteService.addAll(serviceDtos);
						Collections.sort(FileServiceAddressLocator.this.cacheWriteService, new Comparator<ServiceDto>() {
							@Override
							public int compare(ServiceDto o1, ServiceDto o2) {
								return o1.getWriteWeight() - o2.getWriteWeight();
							}
						});
					} finally {
						FileServiceAddressLocator.this.writeCacheLock.unlock();
					}
				} else {
					readSuccess = false;
					logger.error("no file-service configure");
				}
			} catch (Throwable e) {
				e.printStackTrace();
				logger.error("file-service address refresh error", e);
				readSuccess = false;
			}
			if (readSuccess) {
				FileServiceAddressLocator.this.scheduledExecutorService.schedule(new RefreshFileServiceAddressTask(), FileServiceAddressLocator.this.fileConfig.getServiceLocatorNormalDelay(), TimeUnit.MILLISECONDS);
			} else {
				FileServiceAddressLocator.this.scheduledExecutorService.schedule(new RefreshFileServiceAddressTask(), FileServiceAddressLocator.this.fileConfig.getServiceLocatorFatalDelay(), TimeUnit.MILLISECONDS);
			}
		}
	}
	
	public ServiceDto getReadSortFileServiceList(){
		readCacheLock.lock();
		try {
			ServiceDto serviceDto = cacheReadService.get(0);
			serviceDto.setReadWeight(serviceDto.getReadWeight()+1);
			Collections.sort(cacheReadService, new Comparator<ServiceDto>() {
				@Override
				public int compare(ServiceDto o1, ServiceDto o2) {
					return o1.getWriteWeight() - o2.getWriteWeight();
				}
			});
			return serviceDto;
		} finally {
			readCacheLock.unlock();
		}
	}
	
	public ServiceDto getWriteSortFileServiceList(){
		writeCacheLock.lock();
		try {
			ServiceDto serviceDto = cacheWriteService.get(0);
			serviceDto.setWriteWeight(serviceDto.getWriteWeight()+1);
			Collections.sort(cacheWriteService, new Comparator<ServiceDto>() {
				@Override
				public int compare(ServiceDto o1, ServiceDto o2) {
					return o1.getWriteWeight() - o2.getWriteWeight();
				}
			});
			return serviceDto;
		} finally {
			writeCacheLock.unlock();
		}
	}
	
}
