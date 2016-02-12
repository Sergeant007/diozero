package com.diozero;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.diozero.util.SleepUtil;

public class SchedulerTest {
	static int scheduler_instance = 0;
	static long last_call = 0;

	public static void main(String[] args) {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new DaemonThreadFactory());
		scheduler.scheduleAtFixedRate(() -> {
			long now = System.currentTimeMillis();
			long diff = now - last_call;
			last_call = now;
			System.out.format("Thread: %s, Time between calls: %d%n", Thread.currentThread().getName(),
					Long.valueOf(diff));
		} , 100, 100, TimeUnit.MILLISECONDS);
		SleepUtil.sleepSeconds(5);
		System.out.println("Finished");
	}
}

class DaemonThreadFactory implements ThreadFactory {
	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;

	DaemonThreadFactory() {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		namePrefix = "daemon-pool-" + poolNumber.getAndIncrement() + "-thread-";
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
		t.setDaemon(true);
		return t;
	}
}