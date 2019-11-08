package com.activity.smallgame.utils;

/**
 * @author: Mr.dong
 * @create: 2019-08-20 17:49
 **/
public class IdGenerator {

    /**
     * 分布式id 生成器
     * 以微秒为单位的timestamp时间差(基于TWEPOCH)
     * 2 bits 机器id,最大3
     * 2 bits 数据中心id,最大3
     * 12 bits 自增序列 同一时间截，同一机器，可以生成4096个id
     */
    private final static long TWEPOCH = 1288834974657L;

    // 机器标识位数
    private final static long WORKER_ID_BITS = 2L;

    // 数据中心标识位数
    private final static long DATA_CENTER_ID_BITS = 2L;

    // 机器ID最大值3
    private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    // 数据中心ID最大值3
    private final static long MAX_DATA_CENTER_ID = ~(-1L << DATA_CENTER_ID_BITS);

    // 毫秒内自增位
    private final static long SEQUENCE_BITS = 12L;

    // 机器ID偏左移12位
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;

    private final static long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    // 时间毫秒左移22位
    private final static long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;

    private final static long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
    private final long workerId;
    private final long dataCenterId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;
    private static IdGenerator instance;

    public static synchronized IdGenerator getInstance() {
        if (instance == null) {
            instance = new IdGenerator();
        }
        return instance;
    }

    private IdGenerator() {
        this.workerId = 0;
        this.dataCenterId = 0;
    }


    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        sequence = sequence + 1 & SEQUENCE_MASK;
        if (sequence == 0) {
            timestamp = tilNextMillis(lastTimestamp);// 重新生成timestamp
        }

        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("clock moved backwards.Refusing to generate id for %d milliseconds", (lastTimestamp - timestamp)));
        }

        lastTimestamp = timestamp;
        return ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT) | (workerId << WORKER_ID_SHIFT) | sequence;
    }

    private static long tilNextMillis(final long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
