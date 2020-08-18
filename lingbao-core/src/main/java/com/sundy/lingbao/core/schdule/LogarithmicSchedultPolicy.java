package com.sundy.lingbao.core.schdule;

/**
 * >> 右移 相当于 除以2
 * << 左移 相当于乘以2
 * >>> 无符号右移 相当于 右移一位高位补0
 * @author Administrator
 *
 */
public class LogarithmicSchedultPolicy implements SchedulePolicy {

	private final long delayTimeLowBound;
	
	private final long dealyTimeUpBound;
	
	private long lastDelayTime;
	
	public LogarithmicSchedultPolicy(long delayTimeLowBound, long dealyTimeUpBound) {
		this.delayTimeLowBound = delayTimeLowBound;
		this.dealyTimeUpBound = dealyTimeUpBound;
		this.lastDelayTime = dealyTimeUpBound;
	}

	@Override
	public long fail() {
		lastDelayTime = Math.max(lastDelayTime>>2, delayTimeLowBound);
		return lastDelayTime;
	}

	@Override
	public long success() {
		lastDelayTime = dealyTimeUpBound;
		return lastDelayTime;
	}

}
