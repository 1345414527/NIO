import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.codekiller.nio.NioSpringBootApplication;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author codekiller
 * @date 2020/7/21 22:26
 * @Description 阻塞式
 */
@SpringBootTest(classes = NioSpringBootApplication.class)
public class TestBlockingNio {


    /**
    * @Description 客户端
    * @date 2020/7/21 22:37
    * @return void
    */
    @Test
    public void client(){
        SocketChannel sChannel = null;
        FileChannel inChannel = null;
        try {
            //1. 获取通道
            sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",9898));

            inChannel = FileChannel.open(Paths.get("F:\\Animation\\hexo\\0.jpg"), StandardOpenOption.READ);

            //2.分配指定大小的缓冲区
            ByteBuffer buffer=ByteBuffer.allocate(1024);

            //3.读取本地文件，并发送到服务器
            while(inChannel.read(buffer)!=-1){
                buffer.flip();
                sChannel.write(buffer);
                buffer.clear();
            }

            //4.信息发送完毕
            sChannel.shutdownOutput();

            //5.接受反馈
            int len=0;
            while((len=sChannel.read(buffer))!=-1){
                buffer.flip();
                System.out.println(new String(buffer.array(),0,len));
                buffer.clear();
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //4.关闭通道
            if (inChannel != null) {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (sChannel != null) {
                try {
                    sChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
    * @Description 服务端
    * @date 2020/7/21 22:36
    * @return void
    */
    @Test
    public void server(){
        ServerSocketChannel ssChannel = null;
        FileChannel outChannel = null;
        SocketChannel sChannel = null;
        try {
            //1. 获取通道
            ssChannel = ServerSocketChannel.open();

            outChannel = FileChannel.open(Paths.get("F:\\Animation\\8.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);

            //2. 绑定连接
            ssChannel.bind(new InetSocketAddress(9898));

            //3. 获取客户端连接的通道
            sChannel = ssChannel.accept();

            //4.分配指定大小的缓冲区
            ByteBuffer buffer=ByteBuffer.allocate(1024);

            //5. 接收客户端的数据，并保存到本地
            while(sChannel.read(buffer)!=-1){
                buffer.flip();
                outChannel.write(buffer);
                buffer.clear();
            }

            //6. 发送反馈给客户端
            buffer.put("服务端获取数据成功".getBytes());
            buffer.flip();
            sChannel.write(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //4.关闭通道
            if(sChannel!=null) {
                try {
                    sChannel.close();
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
            if(ssChannel!=null) {
                try {
                    ssChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }




    }

}
