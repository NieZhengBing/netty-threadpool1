package com.nzb;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;

import com.nzb.pool.Boss;
import com.nzb.pool.NioSelectorRunnnablePool;

public class ServerBoostrap {
	private NioSelectorRunnnablePool selectorRunnablePool;

	public ServerBoostrap(NioSelectorRunnnablePool selectorRunnablePool) {
		super();
		this.selectorRunnablePool = selectorRunnablePool;
	}

	public void bind(final SocketAddress localAddress) {
		try {
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(localAddress);
			@SuppressWarnings("unused")
			Boss nextBoss = selectorRunnablePool.nextBoss();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
