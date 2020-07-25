import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.codekiller.nio.NioSpringBootApplication;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Date;
import java.util.Iterator;

/**
 * @author codekiller
 * @date 2020/7/22 0:06
 * @Description TODO
 */
@SpringBootTest(classes = NioSpringBootApplication.class)
public class TestNonBlockingNIO2 {
    @Test
    public void client() throws IOException {
        DatagramChannel dc = DatagramChannel.open();

        dc.configureBlocking(false);

        ByteBuffer buffer=ByteBuffer.allocate(1024);
        buffer.put(new Date().toString().getBytes());
        buffer.flip();
        dc.send(buffer,new InetSocketAddress("127.0.0.1",9898));

        dc.close();

    }

    @Test
    public void server() throws IOException{
        DatagramChannel dc = DatagramChannel.open();
        dc.configureBlocking(false);
        dc.bind(new InetSocketAddress(9898));

        Selector selector = Selector.open();
        dc.register(selector, SelectionKey.OP_READ);

        while(selector.select(60000)>0){
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            while(it.hasNext()){
                SelectionKey sk = it.next();
                if(sk.isReadable()){
                    ByteBuffer buffer=ByteBuffer.allocate(1024);
                    dc.receive(buffer);
                    buffer.flip();
                    System.out.println(new String(buffer.array(),0,buffer.limit()));
                    buffer.clear();
                }
                it.remove();
            }

        }
    }
}
