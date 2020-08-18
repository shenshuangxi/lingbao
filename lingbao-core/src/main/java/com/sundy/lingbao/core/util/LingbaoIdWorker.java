package com.sundy.lingbao.core.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * 工作ID(3位16进制数) 
 * 数据ID(3位16进制数) 
 * 年月日时分秒毫秒(17位10进制数)
 * 递增序列(5位16进制数)
 * 总共28位
 * @author Administrator
 *
 */
public class LingbaoIdWorker {

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private String workerId;
	private String datacenterId;
	private long lastTimestamp;
	private int sequence = 0;
	private final int sequenceMax = 0x100000;
	private final int sequenceMask = 0xfffff;
	
	public LingbaoIdWorker(String workerId, String datacenterId) {
		if (!(Long.parseLong(workerId, 16)<0xFFF && workerId.length()==3)) {
			throw new IllegalArgumentException(String.format("worker Id [%s] mast be a hex string and less than 0xfff and big than 0x000", workerId));
		}
		
		if (!(Long.parseLong(datacenterId, 16)<0xFFF  && datacenterId.length()==3)) {
			throw new IllegalArgumentException(String.format("datacenter Id [%s] mast be a hex string and less than 0xfff and big than 0x000", datacenterId));
		}
		
        this.workerId = workerId.toUpperCase();
        this.datacenterId = datacenterId.toUpperCase();
    }
	
	/**
     * 获得下一个ID (该方法是线程安全的)
     */
    public synchronized String nextId() {
        long timestamp = timeGen();
        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d and now %d", lastTimestamp , timestamp));
        }
        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
        	sequence = (sequence + 1) & sequenceMask;
        	if (sequence==0) {
        		//阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
        	}
        } else {
        	//时间戳改变，毫秒内序列重置
            sequence = 0;
        }
        //上次生成ID的时间截
        lastTimestamp = timestamp;
        //通过或运算拼到一起组成32位的ID
        return workerId+datacenterId+dateFormat.format(new Date(lastTimestamp))+Long.toHexString(sequence+sequenceMax).toUpperCase().substring(1);
    }
	
	private long tilNextMillis(Long lastTimestamp) {
		long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
	}

	private long timeGen() {
		return Calendar.getInstance().getTimeInMillis();
	}
	
	public static void main(String[] args) {
		List<String> ids = new ArrayList<String>();
		LingbaoIdWorker idWorker = new LingbaoIdWorker("001","001");
		int count = 1000;
		Thread[] threads = new Thread[count];
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(){
				@Override
				public void run() {
					for (int i = 0; i < 100; i++) {
			            String id = idWorker.nextId();
			            ids.add(id);
			        }
				}
			};
			threads[i].start();
		}
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(System.currentTimeMillis()-startTime);
		System.out.println(ids);
	}
	
}
