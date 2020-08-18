package com.sundy.lingbao.core.schdule;

public class ExponentialSchedulePolicy implements SchedulePolicy {

	private final long delayTimeLowBound;
	
	private final long dealyTimeUpBound;
	
	private long lastDelayTime;
	
	public ExponentialSchedulePolicy(long delayTimeLowBound, long dealyTimeUpBound) {
		this.delayTimeLowBound = delayTimeLowBound;
		this.dealyTimeUpBound = dealyTimeUpBound;
	}

	@Override
	public long fail() {
		long delayTime = 0;
		
		if (lastDelayTime==0) {
			delayTime = delayTimeLowBound;
		} else {
			delayTime = Math.min(lastDelayTime<<1, dealyTimeUpBound);
		}
		lastDelayTime = delayTime;
		return delayTime;
	}

	@Override
	public long success() {
		lastDelayTime = 0;
		return dealyTimeUpBound;
	}

}
