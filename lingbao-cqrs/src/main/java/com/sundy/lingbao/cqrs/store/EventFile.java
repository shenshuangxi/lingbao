package com.sundy.lingbao.cqrs.store;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sundy.lingbao.core.exception.LingbaoException;
import com.sundy.lingbao.cqrs.message.EventMessage;


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

public class EventFile {

	private static Logger logger = LoggerFactory.getLogger(EventFile.class);
	
	private final static String eventFileSuffix = "-all-event";
	private final static String successEventFileSuffix = "-success-event";
	private final static String failEventFileSuffix = "-fail-event";
	
	private String eventDir;
	private List<EventMessage<?>> undoEvents = new ArrayList<EventMessage<?>>();
	private List<EventMessage<?>> failEvents = new ArrayList<EventMessage<?>>();
	
	
	
	static{
		ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
	}
	
	public EventFile(String eventDir) {
		long currentTime = System.currentTimeMillis();
		this.eventDir = eventDir;
		createDir(eventDir);
		logger.info("初始化事件耗时: "+(System.currentTimeMillis()-currentTime));
	}

	private static void createDir(String dir){
		File dirFile = new File(dir);
		logger.info(dirFile.getAbsolutePath());
		if(!dirFile.exists()){
			if(dirFile.getParentFile().exists()){
				dirFile.mkdir();
			} else {
				createDir(dirFile.getParentFile().getPath());
				dirFile.mkdir();
			}
		}
	}
	
	
	public List<EventMessage<?>> getUndoEvents() {
		return undoEvents;
	}

	public List<EventMessage<?>> getFailEvents() {
		return failEvents;
	}

	//添加事件
	public EventMessage<?> appendEvent(EventMessage<?> eventMessage){
		long currentTime = System.currentTimeMillis();
		RandomAccessFile randomFile = null;  
		//事件序列化
		try {
			createDir(eventDir+File.separator+eventMessage.getAggregateType()+File.separator+eventMessage.getAggregateIdentifier()+eventFileSuffix);
			String message = toJsonString(eventMessage);
			byte[] buf = message.getBytes();
			//往指定文件 添加事件  返回 事件所在文件位置(文件名  位置)
			randomFile = new RandomAccessFile(eventDir+File.separator+eventMessage.getAggregateType()+File.separator+eventMessage.getAggregateIdentifier()+eventFileSuffix+File.separator+eventMessage.getSequenceNumber(), "rw");
	        randomFile.write(buf);
	        return eventMessage;
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
			throw new LingbaoException(-1, "save event fail");
		} finally {
			if(randomFile!=null){
				try {
					randomFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			logger.info("写入事件耗时: "+(System.currentTimeMillis()-currentTime));
		}
	}
	
	
	public void appendSuccessEvent(EventMessage<?> eventMessage){
		long currentTime = System.currentTimeMillis();
		RandomAccessFile randomFile = null;  
		//事件序列化
		try {
			createDir(eventDir+File.separator+eventMessage.getAggregateType()+File.separator+eventMessage.getAggregateIdentifier()+successEventFileSuffix);
			String message = toJsonString(eventMessage);
			byte[] buf = message.getBytes();
			//往指定文件 添加事件  返回 事件所在文件位置(文件名  位置)
			randomFile = new RandomAccessFile(eventDir+File.separator+eventMessage.getAggregateType()+File.separator+eventMessage.getAggregateIdentifier()+successEventFileSuffix+File.separator+eventMessage.getSequenceNumber(), "rw");
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
			logger.info("写入成功事件耗时: "+(System.currentTimeMillis()-currentTime));
		}
	}
	
	
	public void appendFailEvent(EventMessage<?> eventMessage){
		long currentTime = System.currentTimeMillis();
		RandomAccessFile randomFile = null;  
		//事件序列化
		try {
			createDir(eventDir+File.separator+eventMessage.getAggregateType()+File.separator+eventMessage.getAggregateIdentifier()+failEventFileSuffix);
			String message = toJsonString(eventMessage);
			byte[] buf = message.getBytes();
			//往指定文件 添加事件  返回 事件所在文件位置(文件名  位置)
			randomFile = new RandomAccessFile(eventDir+File.separator+eventMessage.getAggregateType()+File.separator+eventMessage.getAggregateIdentifier()+failEventFileSuffix+File.separator+eventMessage.getSequenceNumber(), "rw");
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
			logger.info("写入失败事件耗时: "+(System.currentTimeMillis()-currentTime));
		}
	}
	
	//读取事件
	public List<EventMessage<?>> readFile(String aggregateType, String aggregateIdentifier, Long version) throws Throwable{
		long currentTime = System.currentTimeMillis();
		File file = new File(eventDir+File.separator+aggregateType+File.separator+aggregateIdentifier+eventFileSuffix);
		if(!file.exists()){
			throw new FileNotFoundException("事件文件找不到");
		}
		List<EventMessage<?>> eventMessages = new ArrayList<EventMessage<?>>();
		File[] eventFiles = file.listFiles();
		for (File eventFile : eventFiles) {
			if (version!=null && Long.parseLong(eventFile.getName())>version) {
				continue;
			}
			String content = readFile(eventFile);
			EventMessage<?> eventMessage = toJavaObject(content);
			eventMessages.add(eventMessage);
		}
		logger.info("读取事件耗时: "+(System.currentTimeMillis()-currentTime));
		return eventMessages;
	}


	private static String toJsonString(EventMessage<?> eventMessage) {
		return JSON.toJSONString(eventMessage, SerializerFeature.WriteClassName);
	}
	
	private static EventMessage<?> toJavaObject(String json){
		EventMessage<?> eventMessage = JSON.parseObject(json, EventMessage.class);
		return eventMessage;
	}
	
	
	public static String readFile(File file) throws IOException{
		if(file==null || !file.exists()){
			return "";
		}
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String content = null;
			while((content=br.readLine())!=null){
				sb.append(content);
			}
			return sb.toString();
		} finally {
			if(br!=null){
				br.close();
			}
		}
	}
	
	
	
}
