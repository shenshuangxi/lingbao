package com.sundy.db.store.util;

//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.RandomAccessFile;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.serializer.SerializerFeature;
//import com.sundy.db.store.DbEventStore;
//import com.sundy.db.store.event.DbEvent;
//import com.sundy.db.store.event.EventIndex;
//import com.sundy.db.store.event.WrapEventAndIndex;


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

public class EventFileUtil_copy {

//	private static Logger logger = LoggerFactory.getLogger(EventFileUtil.class);
//	
//	private final static String eventFileSuffix = "-event";
//	private final static String eventIndexFileSuffix = "-event-index";
//	private final static String successEventIndexFileSuffix = "-success-event-index";
//	private final static String failEventIndexFileSuffix = "-fail-event-index";
//	private static String eventDir;
//	
//	private static AtomicLong eventFileindex = new AtomicLong();
//	private static AtomicLong eventIndexFileIndex = new AtomicLong();
//	private static AtomicLong successEventIndexFileIndex = new AtomicLong();
//	private static AtomicLong failEventIndexFileIndex = new AtomicLong();
//	
//	private final static AtomicInteger count = new AtomicInteger();
//	private final static int fileMaxCount = 10000;
//	private final static AtomicReference<String> fileFlag = new AtomicReference<String>(""+System.currentTimeMillis());
//	
//	public static void setEventDir(String eventDir) {
//		EventFileUtil.eventDir = eventDir;
//	}
//	
//	
//	public static void refreshFileFlag(){
//		count.incrementAndGet();
//		if(count.compareAndSet(fileMaxCount, 0)){
//			fileFlag.set(""+System.currentTimeMillis());
//		}
//	}
//	
//	//添加事件
//	public static WrapEventAndIndex appendEvent(DbEvent dbEvent){
//		long currentTime = System.currentTimeMillis();
//		RandomAccessFile randomFile = null;  
//		long startIndex = 0l;
//		long endIndex = 0l;
//		//事件序列化
//		try {
//			byte[] buf = (jsonString(dbEvent) + System.getProperty("line.separator")).getBytes();
//			refreshFileFlag();
//			//往指定文件 添加事件  返回 事件所在文件位置(文件名  位置)
//			randomFile = new RandomAccessFile(eventDir+File.separator+fileFlag+eventFileSuffix, "rw");
//			do{
//				startIndex = eventFileindex.get();
//				endIndex = startIndex + buf.length;
//			}while(!eventFileindex.compareAndSet(startIndex, endIndex));
//			randomFile.seek(startIndex);     
//	        randomFile.write(buf);
//	        WrapEventAndIndex wrapEventAndIndex = new WrapEventAndIndex(new EventIndex(startIndex, endIndex,fileFlag+eventFileSuffix), dbEvent, fileFlag+eventIndexFileSuffix, fileFlag+successEventIndexFileSuffix, fileFlag+failEventIndexFileSuffix);
//	        appendEventIndex(wrapEventAndIndex);
//	        return wrapEventAndIndex;
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if(randomFile!=null){
//				try {
//					randomFile.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			logger.error("写入事件耗时: "+(System.currentTimeMillis()-currentTime));
//		}
//		return null;
//	}
//	
//	public static void appendEventIndex(WrapEventAndIndex wrapEventAndIndex){
//		long currentTime = System.currentTimeMillis();
//		//往指定文件 添加事件  返回 事件所在文件位置(文件名  位置)
//		RandomAccessFile randomFile = null;  
//		long startIndex = 0l;
//		long endIndex = 0l;
//		//事件序列化
//		try {
//			String jsonString = JSON.toJSONString(wrapEventAndIndex.getEventIndex())+System.getProperty("line.separator");
//			//往指定文件 添加事件  返回 事件所在文件位置(文件名  位置)
//			randomFile = new RandomAccessFile(eventDir+File.separator+wrapEventAndIndex.getEventIndexFileName(), "rw");  
//			do{
//				startIndex = eventIndexFileIndex.get();
//				endIndex = startIndex + jsonString.getBytes().length;
//			}while(!eventIndexFileIndex.compareAndSet(startIndex, endIndex));
//			 randomFile.seek(startIndex);     
//	         randomFile.writeBytes(jsonString);
//		} catch (IOException e) {
//			e.printStackTrace();
//			logger.error("写入索引失败 "+eventDir+File.separator+fileFlag+eventIndexFileSuffix+"文件 开始-结束: "+wrapEventAndIndex.getEventIndex().getStartIndex()+"-"+wrapEventAndIndex.getEventIndex().getEndIndex());
//		} finally {
//			if(randomFile!=null){
//				try {
//					randomFile.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			logger.error("写入事件索引耗时: "+(System.currentTimeMillis()-currentTime));
//		}
//	}
//	
//	
//	//添加事件索引
//	public static void appendSuccessEventIndex(WrapEventAndIndex wrapEventAndIndex){
//		long currentTime = System.currentTimeMillis();
//		//往指定文件 添加事件  返回 事件所在文件位置(文件名  位置)
//		RandomAccessFile randomFile = null;  
//		long startIndex = 0l;
//		long endIndex = 0l;
//		//事件序列化
//		try {
//			String jsonString = JSON.toJSONString(wrapEventAndIndex.getEventIndex())+System.getProperty("line.separator");
//			//往指定文件 添加事件  返回 事件所在文件位置(文件名  位置)
//			randomFile = new RandomAccessFile(eventDir+File.separator+wrapEventAndIndex.getSuccessEventIndexFileName(), "rw");  
//			do{
//				startIndex = successEventIndexFileIndex.get();
//				endIndex = startIndex + jsonString.getBytes().length;
//			}while(!successEventIndexFileIndex.compareAndSet(startIndex, endIndex));
//			 randomFile.seek(startIndex);     
//	         randomFile.writeBytes(jsonString);
//		} catch (IOException e) {
//			e.printStackTrace();
//			logger.error("写入索引失败 "+eventDir+File.separator+fileFlag+successEventIndexFileSuffix+"文件 开始-结束: "+wrapEventAndIndex.getEventIndex().getStartIndex()+"-"+wrapEventAndIndex.getEventIndex().getEndIndex());
//		} finally {
//			if(randomFile!=null){
//				try {
//					randomFile.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			logger.error("写入事件索引耗时: "+(System.currentTimeMillis()-currentTime));
//		}
//	}
//	
//	public static void appendFailEventIndex(WrapEventAndIndex wrapEventAndIndex){
//		long currentTime = System.currentTimeMillis();
//		//往指定文件 添加事件  返回 事件所在文件位置(文件名  位置)
//		RandomAccessFile randomFile = null;  
//		long startIndex = 0l;
//		long endIndex = 0l;
//		//事件序列化
//		try {
//			String jsonString = JSON.toJSONString(wrapEventAndIndex.getEventIndex())+System.getProperty("line.separator");
//			//往指定文件 添加事件  返回 事件所在文件位置(文件名  位置)
//			randomFile = new RandomAccessFile(eventDir+File.separator+wrapEventAndIndex.getFialEventIndexFileName(), "rw");  
//			do{
//				startIndex = failEventIndexFileIndex.get();
//				endIndex = startIndex + jsonString.getBytes().length;
//			}while(!failEventIndexFileIndex.compareAndSet(startIndex, endIndex));
//			 randomFile.seek(startIndex);     
//	         randomFile.writeBytes(jsonString);
//		} catch (IOException e) {
//			e.printStackTrace();
//			logger.error("写入索引失败 "+eventDir+File.separator+fileFlag+failEventIndexFileSuffix+"文件 开始-结束: "+wrapEventAndIndex.getEventIndex().getStartIndex()+"-"+wrapEventAndIndex.getEventIndex().getEndIndex());
//		} finally {
//			if(randomFile!=null){
//				try {
//					randomFile.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			logger.error("写入失败事件索引耗时: "+(System.currentTimeMillis()-currentTime));
//		}
//	}
//	
//	
//	
//	public static String jsonString(Object obj){
//		return JSON.toJSONString(obj);
//	}
//	
//	public static Object jsonObject(String json){
//		return JSON.parse(json);
//	}
//	
//	
//	
//	public static List<String> readFile(File file) throws IOException{
//		List<String> contents = new ArrayList<String>();
//		BufferedReader br = null;
//		try {
//			br = new BufferedReader(new FileReader(file));
//			String content = null;
//			while((content=br.readLine())!=null){
//				contents.add(content);
//			}
//			return contents;
//		} finally {
//			if(br!=null){
//				br.close();
//			}
//		}
//	}
//	
//	
//	
//	
//	
//	
//	//创建文件
//	public static String newFile(String parentPath, DbEvent dbEvent) throws Throwable{
//		long currentTime = System.currentTimeMillis();
//		FileOutputStream fos = null;
//		try {
//			File dirfile = new File(parentPath);
//			if(!dirfile.exists()){
//				dirfile.mkdir();
//			}
//			File file = new File(parentPath+File.separator+dbEvent.toString()+"-"+System.currentTimeMillis());
//			fos = new FileOutputStream(file);
//			
//			byte[] buf = serialize(dbEvent);
//			fos.write(buf);
//			return file.getAbsolutePath();
//		} catch (Throwable e) {
//			e.printStackTrace();
//			throw e;
//		}finally{
//			if(fos!=null){
//				try {
//					fos.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			logger.error("写入事件耗时: "+(System.currentTimeMillis()-currentTime));
//		}
//	}
//	
//	//读取事件
//	public static DbEvent readFile(String filePath) throws Throwable{
//		long currentTime = System.currentTimeMillis();
//		File file = new File(filePath);
//		if(!file.exists()){
//			throw new FileNotFoundException("文件找不到");
//		}
//		FileInputStream fis = new FileInputStream(file);
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		try {
//			byte[] buf = new byte[1024];
//			int length = 0;
//			
//			while((length = fis.read(buf))>0){
//				baos.write(buf, 0, length);
//			}
//			byte[] objByte = baos.toByteArray();
//			DbEvent dbEvent = (DbEvent) deSerialize(objByte);
//			return dbEvent;
//		} finally{
//			fis.close();
//			baos.close();
//			logger.error("读取事件耗时: "+(System.currentTimeMillis()-currentTime));
//		}
//	}
//	
//	//删除文件
//	public static boolean deleteFile(String path){
//		File file = new File(path);
//		if(file.exists()){
//			file.delete();
//			return true;
//		}
//		return false;
//	}
//	
//	//移动文件
//	public static boolean moveFile(String filePath, String targetDir){
//		File file = new File(filePath);
//		if(file.exists()){
//			File dir = new File(targetDir);
//			if(!dir.exists()){
//				dir.mkdir();
//			}
//			file.renameTo(new File(targetDir+file.getName()));
//			return true;
//		}
//		return false;
//	}
//	
//	
//	private static byte[] serialize(Object obj) throws IOException{
//		ObjectOutputStream oos = null;
//		try {
//			if(obj==null){
//				return null;
//			}
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			oos = new ObjectOutputStream(baos);
//			oos.writeObject(obj);
//			oos.flush();
//			return baos.toByteArray();
//		} finally{
//			if(oos!=null){
//				oos.close();
//			}
//			oos = null;
//		}
//	}
//	
//	private static Object deSerialize(byte[] buf) throws Exception{
//		ObjectInputStream ois = null;
//		try {
//			if(buf==null){
//				return null;
//			}
//			ByteArrayInputStream bais = new ByteArrayInputStream(buf);
//			ois = new ObjectInputStream(bais);
//			return ois.readObject();
//		} finally{
//			if(ois!=null){
//				ois.close();
//			}
//			ois = null;
//		}
//	}
//
//	public static List<String> readEventIndex(String eventDir) {
//		File file = new File(eventDir);
//		if(file.exists()){
//			File[] eventFiles = file.listFiles();
//			List<String> eventIndexs = new ArrayList<String>();
//			for(File eventFile : eventFiles){
//				if(eventFile.isFile()){
//					eventIndexs.add(eventFile.getAbsolutePath());
//				}
//			}
//			return eventIndexs; 
//		}
//		return null;
//	}

	
	
}
