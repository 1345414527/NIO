import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.codekiller.nio.NioSpringBootApplication;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * @author codekiller
 * @date 2020/7/22 0:26
 * @Description TODO
 */
@SpringBootTest(classes = NioSpringBootApplication.class)
public class TestPipe {

    @Test
    public void test01() throws IOException {
        //1.获取管道
        Pipe pipe = Pipe.open();

        //2.将缓冲区的数据写入管道
        ByteBuffer buffer=ByteBuffer.allocate(1024);

        Pipe.SinkChannel sinkChannel = pipe.sink();
        buffer.put("通过单向管道发送数据".getBytes());
        buffer.flip();
        sinkChannel.write(buffer);

        //3.pipe读取缓冲区中的数据
        Pipe.SourceChannel sourceChannel = pipe.source();
        buffer.flip();
        sourceChannel.read(buffer);
        System.out.println(new String(buffer.array(),0,buffer.limit()));

        sourceChannel.close();
        sinkChannel.close();

    }
}
