package com.nzb.pool;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import com.nzb.NioServerBoss;
import com.nzb.NioServerWorker;

public class NioSelectorRunnnablePool {

	private final AtomicInteger bossIndex = new AtomicInteger();
	private Boss[] bosses;

	private final AtomicInteger workerIndex = new AtomicInteger();
	private Worker[] workeres;

	public NioSelectorRunnnablePool(Executor boss, Executor worker) {
		initBoss(boss, 1);
		initWorker(worker, Runtime.getRuntime().availableProcessors() * 2);

	}

	private void initWorker(Executor worker, int count) {
		this.workeres = new NioServerWorker[count];
		for (int i = 0; i < workeres.length; i++) {
			workeres[i] = new NioServerWorker(worker, "worker thread " + (i + 1), this);
		}
	}

	private void initBoss(Executor boss, int count) {
		this.bosses = new NioServerBoss[count];
		for (int i = 0; i < bosses.length; i++) {
			bosses[i] = new NioServerBoss(boss, "boss thread " + (i + 1), this);
		}
	}

	public Worker nextWorker() {
		return workeres[Math.abs(workerIndex.getAndIncrement() % workeres.length)];
	}

	public Boss nextBoss() {
		return bosses[Math.abs(bossIndex.getAndIncrement() % bosses.length)];
	}

}
