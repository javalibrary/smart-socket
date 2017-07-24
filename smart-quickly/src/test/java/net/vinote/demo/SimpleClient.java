package net.vinote.demo;

import net.vinote.smart.socket.protocol.Protocol;
import net.vinote.smart.socket.protocol.ProtocolFactory;
import net.vinote.smart.socket.service.Session;
import net.vinote.smart.socket.service.process.AbstractClientDataProcessor;
import net.vinote.smart.socket.transport.IoSession;
import net.vinote.smart.socket.transport.nio.NioQuickClient;

import java.nio.ByteBuffer;

/**
 * Created by zhengjunwei on 2017/7/12.
 */
public class SimpleClient {
    public static void main(String[] args) throws Exception {
        ProtocolFactory<String> factory = new ProtocolFactory<String>() {
            @Override
            public Protocol<String> createProtocol() {
                return new SimpleProtocol();
            }
        };
        AbstractClientDataProcessor processor = new AbstractClientDataProcessor<String>() {


            @Override
            public Session<String> initSession(final IoSession<String> transportSession) {
                return new Session<String>() {
                    @Override
                    public void sendWithoutResponse(String requestMsg) throws Exception {
                        ByteBuffer buffer = ByteBuffer.wrap(requestMsg.getBytes());
                        buffer.position(buffer.limit());
                        transportSession.write(buffer);
                    }

                    @Override
                    public String sendWithResponse(String requestMsg) throws Exception {
                        return null;
                    }

                    @Override
                    public String sendWithResponse(String requestMsg, long timeout) throws Exception {
                        return null;
                    }

                    @Override
                    public boolean notifySyncMessage(String baseMsg) {
                        return false;
                    }
                };
            }

            @Override
            public void process(Session<String> session, String msg) throws Exception {
                System.out.println("Receive:" + msg);
            }
        };
        NioQuickClient client = new NioQuickClient().connect("localhost", 8888)
                .setProcessor(processor)
                .setProtocolFactory(factory);
        client.start();
        for (int i = 0; i < 10; i++)
            client.getSession().sendWithoutResponse("Hi,Server\r\n");
        Thread.sleep(1);
    }
}
