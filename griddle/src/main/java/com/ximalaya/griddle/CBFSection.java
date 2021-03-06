package com.ximalaya.griddle;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.ximalaya.bloomfilterext.bloom.Key;
import com.ximalaya.bloomfilterext.bloom.ThreadSafeCBloomFilter;

/**
 * CBF数据区封装类
 * @author will
 *
 */
public class CBFSection {
	
	private ThreadSafeCBloomFilter cbf;
	
	/*
	 * 只有useCount为0并且canGC为true时才可以回收内存
	 */
	private AtomicInteger useCount = new AtomicInteger(0);     // 当前正在使用cbf的计数
	private AtomicBoolean canGC = new AtomicBoolean(false);   // 标记是否GC候选，默认为false

	public CBFSection(ThreadSafeCBloomFilter cbf) {
		if(cbf == null) {
			throw new IllegalArgumentException("cbf should not be null");
		}
		
		this.cbf = cbf;   
	}
	
	/**
	 * 获取cbf
	 * @return
	 */
	public ThreadSafeCBloomFilter getCBF() {
		return cbf;
	}
	
	/**
	 * 用户使用计数加1
	 */
	public void increaseUseCount() {
		useCount.incrementAndGet();
	}
	
	/**
	 * 用户使用计数减1
	 */
	public void decreaseUseCount() {
		useCount.decrementAndGet();
	}
	
	/**
	 * 获取用户使用计数
	 * @return
	 */
	public int getUseCount() {
		return useCount.get();
	}
	
	/**
	 * 设置canGC标记为true，使得在useCount为0时可以清理CBF
	 */
	public void markToEnableCanGC() {
		canGC.set(true);
	}
	
	/**
	 * 是否可以GC，必须配合useCount一起判断后才能决定是否清理CBF
	 * @return
	 */
	public boolean canGC() {
		return canGC.get();
	}
	
	/**
	 * 获取已插入次数
	 * @param key
	 * @return
	 */
	public int getInsertedCount(Key key) {
		return cbf.approximateCount(key);
	}
	
	/**
	 * 往cbf中插入Key
	 * @param key
	 */
	public void insertKey(Key key) {
		this.cbf.add(key);
	}
	
}
