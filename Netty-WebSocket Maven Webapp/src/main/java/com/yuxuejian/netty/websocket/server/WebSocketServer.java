package com.yuxuejian.netty.websocket.server;

import com.yuxuejian.netty.websocket.handler.WebSocketHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {
	
	public void run() {
		// ��������������࣬��������TCP��ز���
		ServerBootstrap bootstrap = new ServerBootstrap();
		// ��ȡReactor�̳߳�
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		// ����Ϊ�����߳�ģ��
		bootstrap.group(bossGroup, workGroup)
		// ���÷����NIOͨ������
		.channel(NioServerSocketChannel.class)
		// ����ChannelPipeline��Ҳ����ҵ��ְ�������ɴ����Handler�������ɣ��ɴ��̳߳ش���
		.childHandler(new ChannelInitializer<Channel>() {
			// ��Ӵ����Handler��ͨ��������Ϣ����롢ҵ����Ҳ��������־��Ȩ�ޡ����˵�
			@Override
			protected void initChannel(Channel ch) throws Exception {
				// ��ȡְ����
				ChannelPipeline pipeline = ch.pipeline();
				// 
				pipeline.addLast("http-codec", new HttpServerCodec());
				pipeline.addLast("aggregator", new HttpObjectAggregator(65535));
				pipeline.addLast("http-chunked", new ChunkedWriteHandler());
				pipeline.addLast("handler", new WebSocketHandler());
			}
		})
		// bootstrap ����������TCP������������Ҫ���Էֱ��������̳߳غʹ��̳߳ز��������Ż���������ܡ�
		// �������̳߳�ʹ��option���������ã����̳߳�ʹ��childOption�������á�
		// backlog��ʾ���̳߳������׽ӿ��Ŷӵ����������������δ���Ӷ��У���������δ��ɵģ��������Ӷ���
		.option(ChannelOption.SO_BACKLOG, 5)
		// ��ʾ���ӱ���൱���������ƣ�Ĭ��Ϊ7200s
		.childOption(ChannelOption.SO_KEEPALIVE, true);
		
		try {
			// �󶨶˿ڣ�����select�̣߳���ѯ����channel�¼����������¼�֮��ͻύ�����̳߳ش���
			Channel channel = bootstrap.bind(8081).sync().channel();
			// �ȴ�����˿ڹر�
			channel.closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// �����˳����ͷ��̳߳���Դ
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) {

	}

}
