import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.codekiller.nio.NioSpringBootApplication;

import java.nio.ByteBuffer;

/**
 * @author codekiller
 * @date 2020/7/21 11:59
 * @Description
 *     测试缓冲区,根据数据类型不同（Boolean除外），提供了相同类型的缓冲区
 *              - ByteBuffer
 *              - CharBuffer
 *              - ShortBuffer
 *              - IntBuffer
 *              - LongBuffer
 *              - FloatBuffer
 *              - DoubleBuffer
 *            上述缓冲区的管理方式几乎一致，通过allocate()获取缓冲区
 *     缓冲区存储数据的两个核心方法：
 *              - put(): 存入数据到缓冲区中
 *              - get(): 获取缓冲区中的数据
 *
 */
@SpringBootTest(classes = NioSpringBootApplication.class)
public class TestBuffer {

    @Test
    public void testBuffer(){
        String str="abcdf";

        //1.分配一个指定大小的缓冲区
        ByteBuffer buf=ByteBuffer.allocate(1024);

        System.out.println("初始化分配：");
        System.out.println("容量："+buf.capacity());
        System.out.println("限制："+buf.limit());
        System.out.println("位置："+buf.position());
        System.out.println("-------------------------------");

        //2.利用put存储数据到缓冲区
        buf.put(str.getBytes());
        System.out.println("put存储数据到缓冲区：");
        System.out.println("容量："+buf.capacity());
        System.out.println("限制："+buf.limit());
        System.out.println("位置："+buf.position());
        System.out.println("-------------------------------");

        //3.切换成读取数据的模式
        buf.flip();
        System.out.println("flip切换成读取数据的模式：");
        System.out.println("容量："+buf.capacity());
        System.out.println("限制："+buf.limit());
        System.out.println("位置："+buf.position());
        System.out.println("-------------------------------");

        //4.利用get读取缓冲区中的数据
        byte[] dst=new byte[buf.limit()];
        buf.get(dst);
        System.out.println("get读取缓冲区中的数据：");
        System.out.println("读取的数据："+new String(dst,0,dst.length));
        System.out.println("容量："+buf.capacity());
        System.out.println("限制："+buf.limit());
        System.out.println("位置："+buf.position());
        System.out.println("-------------------------------");

        //5. rewind：可重复读取数据
        buf.rewind();
        System.out.println("rewind可重复读：");
        System.out.println("容量："+buf.capacity());
        System.out.println("限制："+buf.limit());
        System.out.println("位置："+buf.position());
        System.out.println("-------------------------------");

        //6. clear：清空缓冲区,但是缓冲区中的数据依然存在，处于“被遗忘”状态
        buf.clear();
        System.out.println("clear清空缓冲区：");
        System.out.println("查看第一个字节："+(char)buf.get(0));
        System.out.println("容量："+buf.capacity());
        System.out.println("限制："+buf.limit());
        System.out.println("位置："+buf.position());
        System.out.println("-------------------------------");
    }

    @Test
    public void testMark(){
        String str="abcdf";

        //1.分配一个指定大小的缓冲区
        ByteBuffer buf=ByteBuffer.allocate(1024);

        buf.put(str.getBytes());
        buf.flip();

        byte[] dst=new byte[buf.limit()];
        buf.get(dst,0,2);
        System.out.println("第一次获取数据："+new String(dst,0,2));
        System.out.println("此时position的位置："+buf.position());
        System.out.println("-------------------------------");

        //mark标记一下
        buf.mark();
        buf.get(dst,2,2);
        System.out.println("第二次获取数据（mark后）："+new String(dst,2,2));
        System.out.println("此时position的位置："+buf.position());
        System.out.println("-------------------------------");

        //reset：恢复到mark的位置
        buf.reset();
        System.out.println("reset之后\n此时position的位置："+buf.position());
        System.out.println("-------------------------------");

        //判断缓冲区中是否还有剩余数据
        if(buf.hasRemaining()){
            //获取缓冲区中可以操作的数量
            System.out.println("缓冲区中可以操作的数量："+buf.remaining());
        }
    }

    /**
    * @Description 测试直接缓冲区
    * @date 2020/7/21 19:53
    * @return void
    */
    @Test
    public void testDirectBuffer(){
        //分配直接缓冲区
        ByteBuffer buffer=ByteBuffer.allocateDirect(1024);

        System.out.println("是否是直接缓冲区："+buffer.isDirect());
    }
}
