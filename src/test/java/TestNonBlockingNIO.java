import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.codekiller.nio.NioSpringBootApplication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @author codekiller
 * @date 2020/7/21 23:18
 * @Description 非阻塞式
 */
@SpringBootTest(classes = NioSpringBootApplication.class)
public class TestNonBlockingNIO {

    /**
    * @Description 客户端
    * @date 2020/7/21 23:23
    * @return void
    */
    @Test
    public void Client() throws IOException {
        //1.获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        //2.切换成非阻塞模式
        sChannel.configureBlocking(false);

        //3.分配指定大小的缓冲区
        ByteBuffer buffer=ByteBuffer.allocate(1024);


        //4.发送数据给服务端
        buffer.put(new Date().toString().getBytes());
        buffer.flip();
        sChannel.write(buffer);

//        Scanner scanner=new Scanner(System.in);
//        while(scanner.hasNext()){
//            String str=scanner.next();
//            buffer.put((new Date().toString()+"\t"+str).getBytes());
//            buffer.flip();
//            sChannel.write(buffer);
//            buffer.clear();
//        }

        //5.关闭通道
        sChannel.close();

    }

    /**
    * @Description 服务端
    * @date 2020/7/21 23:23
    * @return void
    */
    @Test
    public void Server() throws IOException{
        //1.获取通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();

        //2.切换成非阻塞模式
        ssChannel.configureBlocking(false);

        //3.绑定连接
        ssChannel.bind(new InetSocketAddress(9898));

        //4.获取一个选择器
        Selector selector = Selector.open();

        //5.将通道注册到选择器上,并且指定监听事件(这里是接受事件)
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);

        //6.轮询式的获取选择器上已经”准备就绪“的事件
        while(selector.select()>0){
            System.out.println("监听连接事件...");
            //7.获取当前选择器中所有注册的“选择键(已就绪的监听事件)”
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            while(it.hasNext()){
                //8.获取准备就绪的事件
                SelectionKey sk = it.next();

                //9.判断具体是什么事件准备就绪
                if(sk.isAcceptable()){
                    //10.若接受就绪，获取客户端连接
                    SocketChannel sChannel = ssChannel.accept();

                    //11.切换到非阻塞模式
                    sChannel.configureBlocking(false);

                    //12.将通道注册到选择器上
                    sChannel.register(selector,SelectionKey.OP_READ);
                }else if(sk.isReadable()){
                    //13.获取当前选择器上“读就绪”状态的通道
                    SocketChannel sChannel  =(SocketChannel) sk.channel();

                    //14.读取数据
                    ByteBuffer buffer=ByteBuffer.allocate(1024);
                    int len=0;
                    while((len=sChannel.read(buffer))!=-1){
                        buffer.flip();
                        System.out.println(new String(buffer.array(),0,len));
                        buffer.clear();
                    }
                }
                //15. 取消选择键
                it.remove();
            }
        }

    }
}
