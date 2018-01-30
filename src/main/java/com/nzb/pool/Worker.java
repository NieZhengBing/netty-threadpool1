package com.nzb.pool;

import java.nio.channels.SocketChannel;

public interface Worker {
	
	public void registerNewChannelTask(SocketChannel channel);

}
