package com.nzb;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import com.nzb.pool.NioSelectorRunnnablePool;

public abstract class AbstractNioSelector implements Runnable {

	private final Executor executor;

	protected Selector selector;

	protected final AtomicBoolean wakenUp = new AtomicBoolean();

	private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<Runnable>();

	private String threadName;

	protected NioSelectorRunnnablePool selectorRunnablePool;

	public AbstractNioSelector(Executor executor, String threadName, NioSelectorRunnnablePool selectorRunnablePool) {
		super();
		this.executor = executor;
		this.threadName = threadName;
		this.selectorRunnablePool = selectorRunnablePool;
		openSelector();
	}

	private void openSelector() {
		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			throw new RuntimeException("Failed to create a selector");
		}
		executor.execute(this);
	}

	protected abstract int select(Selector selector) throws IOException;

	protected abstract void process(Selector selector) throws IOException;

	public void run() {
		Thread.currentThread().setName(this.threadName);
		while (true) {
			try {
				wakenUp.set(false);
				select(selector);
				processTaskQueue();
				process(selector);
			} catch (Exception e) {

			}
		}
	}

	private void processTaskQueue() {
		for (;;) {
			final Runnable task = taskQueue.poll();
			if (task == null) {
				break;
			}
			task.run();
		}
	}

	protected final void registerTask(Runnable task) {
		taskQueue.add(task);
		Selector selector = this.selector;

		if (selector != null) {
			if (wakenUp.compareAndSet(false, true)) {
				selector.wakeup();
			}
		} else {
			taskQueue.remove(task);
		}
	}

	public NioSelectorRunnnablePool getSelectorRunnablePool() {
		return selectorRunnablePool;
	}

}
