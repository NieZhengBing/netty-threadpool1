package com.nzb;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;

import com.nzb.pool.NioSelectorRunnnablePool;
import com.nzb.pool.Worker;

public class NioServerWorker extends AbstractNioSelector implements Worker {

	public NioServerWorker(Executor executor, String threadName, NioSelectorRunnnablePool selectorRunnablePool) {
		super(executor, threadName, selectorRunnablePool);
	}

	public void registerNewChannelTask(final SocketChannel channel) {
		final Selector selector = this.selector;
		registerTask(new Runnable() {
			public void run() {
				try {
					channel.register(selector, SelectionKey.OP_READ);
				} catch (ClosedChannelException e) {
					e.printStackTrace();
				}
			}

		});
	}

	@Override
	protected int select(Selector selector) throws IOException {
		return 0;
	}

	@Override
	protected void process(Selector selector) throws IOException {
		Set<SelectionKey> selectedKeys = selector.selectedKeys();
		if (selectedKeys.isEmpty()) {
			return;
		}
		Iterator<SelectionKey> ite = this.selector.selectedKeys().iterator();
		while (ite.hasNext()) {
			SelectionKey key = ite.next();

			ite.remove();

			SocketChannel channel = (SocketChannel) key.channel();

			int ret = 0;
			boolean failure = true;
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			try {
				ret = channel.read(buffer);
				failure = false;
			} catch (Exception e) {

			}
			if (ret < 0 || failure) {
				key.channel();
				System.out.println("client broken connect");
			} else {
				System.out.println("get data: " + new String(buffer.array()));

				ByteBuffer outBuffer = ByteBuffer.wrap("get\n".getBytes());
				channel.write(outBuffer);
			}
		}
	}

}
