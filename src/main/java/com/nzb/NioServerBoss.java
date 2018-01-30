package com.nzb;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;

import com.nzb.pool.Boss;
import com.nzb.pool.NioSelectorRunnnablePool;
import com.nzb.pool.Worker;

public class NioServerBoss extends AbstractNioSelector implements Boss {

	public NioServerBoss(Executor executor, String threadName, NioSelectorRunnnablePool selectorRunnablePool) {
		super(executor, threadName, selectorRunnablePool);
	}

	public void registerAcceptChannelTask(final ServerSocketChannel serverChannel) {
		@SuppressWarnings("unused")
		final Selector selecotr = this.selector;
		registerTask(new Runnable() {
			public void run() {
				try {
					serverChannel.register(selector, SelectionKey.OP_ACCEPT);
				} catch (ClosedChannelException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected int select(Selector selector) throws IOException {
		return selector.select();
	}

	@Override
	protected void process(Selector selector) throws IOException {
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		if (selectedKeys.isEmpty()) {
			return;
		}
		for (Iterator<SelectionKey> i = selectedKeys.iterator(); i.hasNext();) {
			SelectionKey key = i.next();
			i.remove();
			ServerSocketChannel server = (ServerSocketChannel) key.channel();
			SocketChannel channel = server.accept();
			channel.configureBlocking(false);
			Worker nextWorker = getSelectorRunnablePool().nextWorker();
			nextWorker.registerNewChannelTask(channel);

			System.out.println("new client connectng");
		}
	}

}
