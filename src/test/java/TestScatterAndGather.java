import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.codekiller.nio.NioSpringBootApplication;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author codekiller
 * @date 2020/7/21 21:00
 * @Description 测试分散和聚集
 */
@SpringBootTest(classes = NioSpringBootApplication.class)
public class TestScatterAndGather {
    @Test
    public void test01(){
        RandomAccessFile raf1= null;
        FileChannel channel1 = null;
        RandomAccessFile raf2= null;
        FileChannel channel2 = null;
        try {
            raf1 = new RandomAccessFile( "F:\\Animation\\1.txt","rw");

            //1.获取通道
            channel1 = raf1.getChannel();

            //2.分配指定大小的缓冲区
            ByteBuffer buf1=ByteBuffer.allocate(100);
            ByteBuffer buf2=ByteBuffer.allocate(1024);

            //3.分散读取
            ByteBuffer[] buffers={buf1,buf2};
            channel1.read(buffers);

            System.out.println(new String(buffers[0].array(),0,buffers[0].limit()));
            System.out.println("----------------------");
            System.out.println(new String(buffers[1].array(),0,buffers[1].limit()));

            for (ByteBuffer buffer : buffers) {
                buffer.flip();
            }


            //4.聚集写入
            raf2 = new RandomAccessFile("F:\\Animation\\2.txt","rw");
            channel2 = raf2.getChannel();
            channel2.write(buffers);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(channel2!=null) {
                try {
                    channel2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(channel1!=null) {
                try {
                    channel1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(raf2!=null) {
                try {
                    raf2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(raf1!=null) {
                try {
                    raf1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }





    }
}
