import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.codekiller.nio.NioSpringBootApplication;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * @author codekiller
 * @date 2020/7/21 21:33
 * @Description 测试字符集：charset
 */
@SpringBootTest(classes = NioSpringBootApplication.class)
public class TestCharset {

    @Test
    public void test01(){
        SortedMap<String, Charset> map = Charset.availableCharsets();
        Set<Map.Entry<String, Charset>> entries = map.entrySet();
        for (Map.Entry<String, Charset> entry : entries) {
            System.out.println(entry.getKey()+"="+entry.getValue());
        }
    }

    @Test
    public void tset02() throws CharacterCodingException {
        Charset cs1=Charset.forName("GBK");

        //获取编码器
        CharsetEncoder ce=cs1.newEncoder();

        //获取解码器
        CharsetDecoder cd = cs1.newDecoder();

        CharBuffer buffer = CharBuffer.allocate(1024);
        buffer.put("来吧！去吧！");
        buffer.flip();

        //编码
        ByteBuffer bBuf = ce.encode(buffer);

        System.out.print("GBK编码: ");
        for(int i=0;i<12;i++){
            System.out.print(bBuf.get()+"  ");
        }
        System.out.println();


        //解码
        bBuf.flip();
        CharBuffer cBuf1 = cd.decode(bBuf);
        System.out.println("GBK解码："+cBuf1);

        //解码
        bBuf.rewind();
        Charset cs2 = Charset.forName("UTF-8");
        CharBuffer cBuf2 = cs2.decode(bBuf);
        System.out.println("UTF-8解码："+cBuf2);
    }
}
