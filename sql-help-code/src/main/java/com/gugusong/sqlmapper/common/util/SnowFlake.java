package com.gugusong.sqlmapper.common.util;

/**
 * 雪花随机数算法
 *
 * @author yousongshu
 *
 */
public class SnowFlake {

	/**
	 * 起始的时间戳
	 */
	private final static long START_STMP = 1608088657109L;

	/**
	 * 每一部分占用的位数
	 */
	private final static long SEQUENCE_BIT = 12; // 序列号占用的位数
	private final static long MACHINE_BIT = 5; // 机器标识占用的位数
	private final static long DATA_CENTER_BIT = 5;// 数据中心占用的位数

	/**
	 * 每一部分的最大值
	 */
	private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATA_CENTER_BIT);
	private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
	private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

	/**
	 * 每一部分向左的位移
	 */
	private final static long MACHINE_LEFT = SEQUENCE_BIT;
	private final static long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
	private final static long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;

	private long dataCenterId; // 数据中心
	private long machineId; // 机器标识
	private long sequence = 0L; // 序列号
	private long lastStamp = -1L;// 上一次时间戳

	/**
	 * 构建随机生成对象
	 * @param dataCenterId 数据中心编号
	 * @param machineId 机器编号
	 */
	public SnowFlake(long dataCenterId, long machineId) {
		if (dataCenterId > MAX_DATACENTER_NUM || dataCenterId < 0) {
			throw new IllegalArgumentException("数据中心编号错误，需大于等于0且小于MAX_DATACENTER_NUM");
		}
		if (machineId > MAX_MACHINE_NUM || machineId < 0) {
			throw new IllegalArgumentException("机器编号错误，需大于等于0且小于MAX_MACHINE_NUM");
		}
		this.dataCenterId = dataCenterId;
		this.machineId = machineId;
	}

	/**
	 * 产生下一个ID
	 *
	 * @return
	 */
	public synchronized long nextId() {
		long currStmp = getNewstmp();
		if (currStmp < lastStamp) {
			throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
		}

		if (currStmp == lastStamp) {
			// 相同毫秒内，序列号自增
			sequence = (sequence + 1) & MAX_SEQUENCE;
			// 同一毫秒的序列数已经达到最大
			if (sequence == 0L) {
				currStmp = getNextMill();
			}
		} else {
			// 不同毫秒内，序列号置为0
			sequence = 0L;
		}

		lastStamp = currStmp;

		return (currStmp - START_STMP) << TIMESTAMP_LEFT // 时间戳部分
				| dataCenterId << DATA_CENTER_LEFT // 数据中心部分
				| machineId << MACHINE_LEFT // 机器标识部分
				| sequence; // 序列号部分
	}

	private long getNextMill() {
		long mill = getNewstmp();
		while (mill <= lastStamp) {
			mill = getNewstmp();
		}
		return mill;
	}

	private long getNewstmp() {
		return System.currentTimeMillis();
	}

	public static void main(String[] args) {
		SnowFlake snowFlake = new SnowFlake(2, 3);


	}
}
