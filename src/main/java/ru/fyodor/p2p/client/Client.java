package ru.fyodor.p2p.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import ru.fyodor.p2p.Node;
import ru.fyodor.p2p.message.Message;
import ru.fyodor.p2p.message.MsgSerializer;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private Node node;
    private List<ClientConnection> connections = new ArrayList<>();
    public Client(Node node) {
        this.node = node;
    }

    public void connectToServer(String inetHost, int inetPort) {
        new Thread(() -> {
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                                     @Override
                                     protected void initChannel(SocketChannel socketChannel) throws Exception {
                                         connections.add(
                                                 new ClientConnection(socketChannel)
                                         );
                                     }
                                 }
                        );
                //нода подключается к другой ноде, прослушивающей подключений по переданному адресу
                ChannelFuture future = bootstrap.connect(inetHost, inetPort).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
        }).run();
    }

    //сообщение отправляется на все подключенные к сети узлы
    public void sendMessage(Message msg) {
        ByteBuf buf = Unpooled.copiedBuffer(
                MsgSerializer.serialize(msg)
        );
        for (var con : connections) {
            con.getChannel().writeAndFlush(buf);
        }
    }
}
