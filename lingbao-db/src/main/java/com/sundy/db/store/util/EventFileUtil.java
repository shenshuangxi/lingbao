package com.sundy.db.store.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sundy.db.store.event.DbEvent;
import com.sundy.db.store.event.WrapEvent;


/**
 * 大致思路:
 * 	收到一个事件，创建一个文件  写入磁盘  并将文件路径索引写入队列
 *  处理完一个事件，
 *  	成功处理  将事件移入 成功处理文件夹
 *  	失败处理 将事件移入 失败处理文件夹
 * 宕机重启 
 * 		第一步 从失败文件夹中 获取事件 进行处理
 * 		第二步  从未处理文件的文件路径索引写入事件处理队列
 * @author Administrator
 *
 * 三个方法  
 * 	1，创建文件 写入事件 返回文件索引
 * 	2，根据索引 读取文件
 *  2，移动文件 返回新的文件索引
 *
 */

/**
 * 第二种思路  
 * 	采用追加事件
 * 
 * 往文件添加事件 
 * 事件处理成功 写入事件所在索引
 * 事件失败 写入失败事件所有
 * 
 * 系统重启 按需求 处理事件 加载机制
 * @author Administrator
 *
 */

public class EventFileUtil {

	private static Logger logger = LoggerFactory.getLogger(EventFileUtil.class);
	
	private final static String eventFileSuffix = "-all-event";
	private final static String successEventFileSuffix = "-success-event";
	private final static String failEventFileSuffix = "-fail-event";
	
	private static String eventDir;
	private static String garbageDir;
	
	private static AtomicLong eventFileindex = new AtomicLong();
	private static AtomicLong successEventFileIndex = new AtomicLong();
	private static AtomicLong failEventFileIndex = new AtomicLong();
	
	private static List<DbEvent> undoEvents = new ArrayList<DbEvent>();
	private static List<DbEvent> failEvents = new ArrayList<DbEvent>();
	
	
	private final static AtomicInteger count = new AtomicInteger();
	private final static int fileMaxCount = 10000;
	private final static AtomicReference<String> fileFlag = new AtomicReference<String>(""+System.currentTimeMillis());
	
	static{
		ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
	}
	
	public static void setEventDir(String eventDir, String garbageDir) {
		long currentTime = System.currentTimeMillis();
		undoEvents.clear();
		failEvents.clear();
		EventFileUtil.eventDir = eventDir;
		EventFileUtil.garbageDir = garbageDir;
		readUndoFailEvent();
		logger.error("初始化事件耗时: "+(System.currentTimeMillis()-currentTime));
	}
	
	
	public static List<DbEvent> getUndoEvents() {
		return undoEvents;
	}

	public static List<DbEvent> getFailEvents() {
		return failEvents;
	}


	public static void refreshFileFlag(){
		count.incrementAndGet();
		if(count.compareAndSet(fileMaxCount, 0)){
			fileFlag.set(""+System.currentTimeMillis());
		}
	}
	
	//添加事件
	public static WrapEvent appendEvent(DbEvent dbEvent){
		long currentTime = System.currentTimeMillis();
		RandomAccessFile randomFile = null;  
		long startIndex = 0l;
		long endIndex = 0l;
		//事件序列化
		try {
			byte[] buf = (toJsonString(dbEvent) + System.getProperty("line.separator")).getBytes();
			refreshFileFlag();
			//往指定文件 添加事件  返回 事件所在文件位置(文件名  位置)
			randomFile = new RandomAccessFile(eventDir+File.separator+fileFlag+eventFileSuffix, "rw");
			do{
				startIndex = eventFileindex.get();
				endIndex = startIndex + buf.length;
			}while(!eventFileindex.compareAndSet(startIndex, endIndex));
			randomFile.seek(startIndex);     
	        randomFile.write(buf);
	        WrapEvent wrapEvent = new WrapEvent(dbEvent, fileFlag+successEventFileSuffix, fileFlag+failEventFileSuffix);
	        return wrapEvent;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(randomFile!=null){
				try {
					randomFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			logger.error("写入事件耗时: "+(System.currentTimeMillis()-currentTime));
		}
		return null;
	}
	
	
	public static void appendSuccessEvent(WrapEvent wrapEvent){
		long currentTime = System.currentTimeMillis();
		RandomAccessFile randomFile = null;  
		long startIndex = 0l;
		long endIndex = 0l;
		//事件序列化
		try {
			byte[] buf = (toJsonString(wrapEvent.getDbEvent()) + System.getProperty("line.separator")).getBytes();
			//往指定文件 添加事件  返回 事件所在文件位置(文件名  位置)
			randomFile = new RandomAccessFile(eventDir+File.separator+wrapEvent.getSuccessEventFileName(), "rw");
			do{
				startIndex = successEventFileIndex.get();
				endIndex = startIndex + buf.length;
			}while(!successEventFileIndex.compareAndSet(startIndex, endIndex));
			randomFile.seek(startIndex);     
	        randomFile.write(buf);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(randomFile!=null){
				try {
					randomFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			logger.error("写入成功事件耗时: "+(System.currentTimeMillis()-currentTime));
		}
	}
	
	
	public static void appendFailEvent(WrapEvent wrapEvent){
		long currentTime = System.currentTimeMillis();
		RandomAccessFile randomFile = null;  
		long startIndex = 0l;
		long endIndex = 0l;
		//事件序列化
		try {
			byte[] buf = (toJsonString(wrapEvent.getDbEvent()) + System.getProperty("line.separator")).getBytes();
			//往指定文件 添加事件  返回 事件所在文件位置(文件名  位置)
			randomFile = new RandomAccessFile(eventDir+File.separator+fileFlag+failEventFileSuffix, "rw");
			do{
				startIndex = failEventFileIndex.get();
				endIndex = startIndex + buf.length;
			}while(!failEventFileIndex.compareAndSet(startIndex, endIndex));
			randomFile.seek(startIndex);     
	        randomFile.write(buf);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(randomFile!=null){
				try {
					randomFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			logger.error("写入失败事件耗时: "+(System.currentTimeMillis()-currentTime));
		}
	}


	//加载未处理或失败事件
	private static void readUndoFailEvent(){
		long currentTime = System.currentTimeMillis();
		//读取所有文件
		Map<String,File> eventFiles = new HashMap<String,File>();
		Map<String,File> successEventFiles = new HashMap<String,File>();
		Map<String,File> failEventFiles = new HashMap<String,File>();
		File file = new File(eventDir);
		File[] files = file.listFiles();
		for(File temp : files){
			if(temp.isFile()) {
				String fileName = temp.getName();
				String key = fileName.split("-")[0];
				if(fileName.endsWith(successEventFileSuffix)){
					successEventFiles.put(key, temp);
				} else if(fileName.endsWith(failEventFileSuffix)){
					failEventFiles.put(key, temp);
				} else if(fileName.endsWith(eventFileSuffix)){
					eventFiles.put(key, temp);
				} 
			}
		}
		
		//
		for(String key : eventFiles.keySet()){
			File eventFile = eventFiles.get(key);
			File successEventFile = successEventFiles.get(key);
			File failEventFile = failEventFiles.get(key);
			try {
				List<String> eventContents = readFile(eventFile);
				List<String> successEventContents = readFile(successEventFile);
				List<String> failEventContents = readFile(failEventFile);
				Map<String,String> eventContentsMap = new HashMap<String, String>();
				Map<String,String> failEventContentsMap = new HashMap<String, String>();
				
				if(eventContents.size()==successEventContents.size()){
					moveFile(eventFile, successEventFile, failEventFile);
					continue;
				} else if(eventContents.size()==(successEventContents.size()+failEventContents.size())){
					for(String content : failEventContents){
						failEventContentsMap.put(content, null);
					}
					for(String content : failEventContentsMap.keySet()){
						DbEvent dbEvent = toJavaObject(content);
						failEvents.add(dbEvent);
					}
					moveFile(eventFile, successEventFile, failEventFile);
				} else {
					for(String content : eventContents){
						eventContentsMap.put(content, null);
					}
					
					for(String content : successEventContents){
						if(eventContentsMap.containsKey(content)){
							eventContentsMap.remove(content);
						}
					}
					
					for(String content : failEventContents){
						if(eventContentsMap.containsKey(content)){
							eventContentsMap.remove(content);
						}
						failEventContentsMap.put(content, null);
					}
					
					for(String content : eventContentsMap.keySet()){
						DbEvent dbEvent = toJavaObject(content);
						undoEvents.add(dbEvent);
					}
					
					for(String content : failEventContentsMap.keySet()){
						DbEvent dbEvent = toJavaObject(content);
						failEvents.add(dbEvent);
					}
					
					moveFile(eventFile, successEventFile, failEventFile);
				}
			} catch (Throwable e) {
				e.printStackTrace();
				logger.error(eventFile.getPath() + "初始化失败");
			}
			
		}
		logger.error("加载未处理或失败事件耗时: "+(System.currentTimeMillis()-currentTime));
	}


	private static void moveFile(File eventFile, File successEventFile, File failEventFile) {
		if(eventFile!=null && eventFile.exists()){
			moveFile(eventFile.getPath(), garbageDir);
		}
		if(successEventFile!=null && successEventFile.exists()){
			moveFile(successEventFile.getPath(), garbageDir);
		}
		if(failEventFile!=null && failEventFile.exists()){
			moveFile(failEventFile.getPath(), garbageDir);
		}
	}
	
	public static String toJsonString(DbEvent dbEvent) {
		return JSON.toJSONString(dbEvent, SerializerFeature.WriteClassName);
	}
	
	public static DbEvent toJavaObject(String json){
		DbEvent dbEvent = (DbEvent) JSON.parse(json);
		return dbEvent;
	}
	
	
	public static List<String> readFile(File file) throws IOException{
		List<String> contents = new ArrayList<String>();
		if(file==null || !file.exists()){
			return contents;
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String content = null;
			while((content=br.readLine())!=null){
				contents.add(content);
			}
			return contents;
		} finally {
			if(br!=null){
				br.close();
			}
		}
	}
	
	
	
	//创建文件
	public static String newFile(String parentPath, DbEvent dbEvent) throws Throwable{
		long currentTime = System.currentTimeMillis();
		FileOutputStream fos = null;
		try {
			File dirfile = new File(parentPath);
			if(!dirfile.exists()){
				dirfile.mkdir();
			}
			File file = new File(parentPath+File.separator+dbEvent.toString()+"-"+System.currentTimeMillis());
			fos = new FileOutputStream(file);
			
			byte[] buf = serialize(dbEvent);
			fos.write(buf);
			return file.getAbsolutePath();
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		}finally{
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			logger.error("写入事件耗时: "+(System.currentTimeMillis()-currentTime));
		}
	}
	
	//读取事件
	public static DbEvent readFile(String filePath) throws Throwable{
		long currentTime = System.currentTimeMillis();
		File file = new File(filePath);
		if(!file.exists()){
			throw new FileNotFoundException("文件找不到");
		}
		FileInputStream fis = new FileInputStream(file);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buf = new byte[1024];
			int length = 0;
			
			while((length = fis.read(buf))>0){
				baos.write(buf, 0, length);
			}
			byte[] objByte = baos.toByteArray();
			DbEvent dbEvent = (DbEvent) deSerialize(objByte);
			return dbEvent;
		} finally{
			fis.close();
			baos.close();
			logger.error("读取事件耗时: "+(System.currentTimeMillis()-currentTime));
		}
	}
	
	//删除文件
	public static boolean deleteFile(String path){
		File file = new File(path);
		if(file.exists()){
			file.delete();
			return true;
		}
		return false;
	}
	
	//移动文件
	public static boolean moveFile(String filePath, String targetDir){
		File file = new File(filePath);
		if(file.exists()){
			File dir = new File(targetDir);
			if(!dir.exists()){
				dir.mkdir();
			}
			file.renameTo(new File(targetDir+File.separator+file.getName()));
			return true;
		}
		return false;
	}
	
	
	private static byte[] serialize(Object obj) throws IOException{
		ObjectOutputStream oos = null;
		try {
			if(obj==null){
				return null;
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
			return baos.toByteArray();
		} finally{
			if(oos!=null){
				oos.close();
			}
			oos = null;
		}
	}
	
	private static Object deSerialize(byte[] buf) throws Exception{
		ObjectInputStream ois = null;
		try {
			if(buf==null){
				return null;
			}
			ByteArrayInputStream bais = new ByteArrayInputStream(buf);
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} finally{
			if(ois!=null){
				ois.close();
			}
			ois = null;
		}
	}

	public static List<String> readEventIndex(String eventDir) {
		File file = new File(eventDir);
		if(file.exists()){
			File[] eventFiles = file.listFiles();
			List<String> eventIndexs = new ArrayList<String>();
			for(File eventFile : eventFiles){
				if(eventFile.isFile()){
					eventIndexs.add(eventFile.getAbsolutePath());
				}
			}
			return eventIndexs; 
		}
		return null;
	}

	
	
}
