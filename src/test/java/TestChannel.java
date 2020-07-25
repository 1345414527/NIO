import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import top.codekiller.nio.NioSpringBootApplication;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author codekiller
 * @date 2020/7/21 19:59
 * @Description 测试通道
 */
@SpringBootTest(classes = NioSpringBootApplication.class)
public class TestChannel {

    /**
    * @Description 利用通道完成文件的复制（非直接缓冲区）
    * @date 2020/7/21 20:19
    * @return void
    */
    @Test
    public void testChannel1(){
        FileInputStream fis= null;
        FileOutputStream fos= null;
        FileChannel inChannel= null;
        FileChannel outChannel= null;
        try {
            fis = new FileInputStream("F:\\Animation\\hexo\\0.jpg");
            fos = new FileOutputStream("F:\\Animation\\1.jpg");

            //获取通道
            inChannel = fis.getChannel();
            outChannel = fos.getChannel();

            //分配指定大小的缓冲区
            ByteBuffer buf=ByteBuffer.allocate(1024);

            //将通道中的数据存入缓冲区
            while (inChannel.read(buf)!=-1){
                //切换到读取模式
                buf.flip();
                //将缓冲区中的数据写入通道中
                outChannel.write(buf);
                //清空缓冲区
                buf.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(outChannel!=null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inChannel!=null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fos!=null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fis!=null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
    * @Description 使用直接缓冲区完成文件的复制(内存映射方式)
    * @date 2020/7/21 20:24
    * @return void
    */
    @Test
    public void testChannel2(){
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = FileChannel.open(Paths.get("F:\\Animation\\hexo\\0.jpg"), StandardOpenOption.READ);
            outChannel = FileChannel.open(Paths.get("F:\\Animation\\2.jpg"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE_NEW);

            //内存映射文件
            MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
            MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

            //直接对缓冲区进行数据的读写操作
            byte[] dst=new byte[1024];
            inMappedBuf.get(dst);
            outMappedBuf.put(dst);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inChannel!=null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outChannel!=null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testChannel3(){
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = FileChannel.open(Paths.get("F:\\Animation\\hexo\\0.jpg"), StandardOpenOption.READ);
            outChannel = FileChannel.open(Paths.get("F:\\Animation\\3.jpg"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE_NEW);

            inChannel.transferTo(0, inChannel.size(), outChannel);
//            outChannel.transferFrom(inChannel,0,inChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inChannel!=null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(outChannel!=null) {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
