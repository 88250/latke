/*
 * Latke - 一款以 JSON 为主的 Java Web 框架
 * Copyright (c) 2009-present, b3log.org
 *
 * LianDi is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package org.b3log.latke.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Http Server based on Netty 4.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Jan 15, 2020
 * @since 3.0.0
 */
public abstract class BaseServer {

    private static final Logger LOGGER = LogManager.getLogger(BaseServer.class);

    private static final EventLoopGroup BOSS_GROUP = new NioEventLoopGroup(1);
    private static final EventLoopGroup WORKER_GROUP = new NioEventLoopGroup();

    public void start(final int listenPort) {
        startServer(listenPort);
    }

    public void shutdown() {
        shutdownServer();
    }

    private void startServer(final int listenPort) {
        try {
            InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
            new ServerBootstrap().
                    group(BOSS_GROUP, WORKER_GROUP).
                    channel(NioServerSocketChannel.class).
                    handler(new LoggingHandler(LogLevel.INFO)).
                    childHandler(new HttpServerInitializer()).
                    bind(listenPort).sync().channel().closeFuture().sync();
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Start server failed, exit process", e);
            System.exit(-1);
        }
    }

    private void shutdownServer() {
        try {
            LOGGER.log(Level.INFO, "HTTP server is shutting down");
            BOSS_GROUP.shutdownGracefully(1, 7, TimeUnit.SECONDS).await();
            WORKER_GROUP.shutdownGracefully(1, 7, TimeUnit.SECONDS).await();
            LOGGER.log(Level.INFO, "HTTP server has shut down");
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Shutdown server failed", e);
        }
    }

    private static final class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        public void initChannel(final SocketChannel ch) {
            final ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(1024 * 1024 * 64));
            pipeline.addLast(new WebSocketHandler());
            pipeline.addLast(new ServerHandler());
        }
    }
}
