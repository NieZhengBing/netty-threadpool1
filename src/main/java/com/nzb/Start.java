package com.nzb;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.nzb.pool.NioSelectorRunnnablePool;

public class Start {

	public static void main(String[] args) {
		NioSelectorRunnnablePool nioSelectorRunnnablePool = new NioSelectorRunnnablePool(
				Executors.newCachedThreadPool(), Executors.newCachedThreadPool());

		ServerBoostrap bootstrap = new ServerBoostrap(nioSelectorRunnnablePool);

		bootstrap.bind(new InetSocketAddress(10101));

		System.out.println("start");

	}

}
