package com.nzb.pool;

import java.nio.channels.ServerSocketChannel;

public interface Boss {
	
	public void registerAcceptChannelTask(ServerSocketChannel serverChannel);

}
