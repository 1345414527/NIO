## 介绍

### 基本介绍

&emsp;Java NIO（New IO）是从Java 1.4版本开始引入的一个新的IO API，可以替代标准的Java IO API。NIO与原来的IO有同样的作用和目的，但是使用的方式完全不同，NIO支持面向缓冲区的、基于通道的IO操作。NIO将以更加高效的方式进行文件的读写操作。

&emsp;**NIO** **是 面向缓冲区** ，**或者面向 块 编程的**。数据读取到一个它稍后处理的缓冲区，需要时可在缓冲区中前后 移动，这就增加了处理过程中的灵活性，使用它可以提供非阻塞式的高伸缩性网络 

&emsp;Java NIO 的非阻塞模式，使一个线程从某通道发送请求或者读取数据，但是它仅能得到目前可用的数据，如果 目前没有数据可用时，就什么都不会获取，而不是保持线程阻塞，所以直至数据变的可以读取之前，该线程可 以继续做其他的事情。 非阻塞写也是如此，一个线程请求写入一些数据到某通道，但不需要等待它完全写入， 这个线程同时可以去做别的事情。



<br/>



### Nio和Bio的区别 





| BIO                     | NIO                                                          |
| ----------------------- | ------------------------------------------------------------ |
| 面向流(Stream Oriented) | 面向缓冲区(Buffer Oriented),数据总是从通道读取到缓冲区中，或者从缓冲区写入到通道中。 |
| 以流的方式处理数据      | 以块的方式处理数据                                           |
| 阻塞IO(Blocking IO)     | 非阻塞IO(Non Blocking IO)                                    |
| (无)                    | 选择器(Selectors),用于监听多个通道的事件                     |
|                         |                                                              |



<br/>



### 通道和缓冲区

&emsp;Java NIO系统的核心在于：通道(Channel)和缓冲区(Buffer)。通道表示打开到 IO 设备(例如：文件、套接字)的连接。若需要使用 NIO 系统，需要`获取用于连接 IO 设备的通道`以及`用于容纳数据的缓冲区`。然后操作缓冲区，对数据进行处理。



> **简而言之，** **Channel 负责传输**，  **Buffer** **负责存储**



![](https://img-blog.csdnimg.cn/20200722124040324.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)

- 每个 channel 都会对应一个 Buffer 
-  Selector 对应一个线程， 一个线程对应多个 channel(连接) 
-  该图反应了有三个 channel 注册到 该 selector //程序 
- 程序切换到哪个 channel 是有事件决定的, Event 就是一个重要的概念 
- Selector 会根据不同的事件，在各个通道上切换 
-  Buffer 就是一个内存块 ， 底层是有一个数组 
- 数据的读取写入是通过 Buffer, 这个和 BIO , BIO 中要么是输入流，或者是 输出流, 不能双向，但是 NIO 的 Buffer 是可以读也可以写, 需要 flip 方法切换 channel 是双向的, 可以返回底层操作系统的情况, 比如 Linux ， 底层的操作系统 通道就是双向的





<br/><br/>





## 缓冲区

### 介绍

&emsp;**缓冲区**（Buffer)：一个用于特定基本数据类型的容器。由 java.nio 包定义的，所有缓冲区都是 Buffer 抽象类的子类。

&emsp;Java NIO 中的 Buffer 主要用于与 NIO 通道进行交互，数据是从通道读入缓冲区，从缓冲区写入通道中的

&emsp;缓冲区本质上是一个**可以读写数据的内存块**，可以理解成是一个**容器对象**(含数组)，该对 象提供了一组方法，可以更轻松地使用内存块，缓冲区对象内置了一些机制，能够跟踪和记录缓冲区的状态变化情况。Channel 提供从文件、网络读取数据的渠道，但是读取或写入的数据都必须经由 Buffer。

<br/>



&emsp;Buffer 就像一个数组，可以保存多个相同类型的数据。`根据数据类型不同`(==boolean 除外==)，有以下 Buffer 常用子类：

- ByteBuffer
-  CharBuffer
-  ShortBuffer
-  IntBuffer
-  LongBuffer
- FloatBuffer
-  DoubleBuffer

&emsp;上述 Buffer 类 他们都采用相似的方法进行管理数据，只是各自管理的数据类型不同而已。都是通过如下方法获取一个 Buffer对象：

&emsp;==static XxxBuffer allocate(int capacity) : 创建一个容量为 capacity 的 XxxBuffer 对象==

&emsp;缓冲区存储数据的两个核心方法：

- put(): 存入数据到缓冲区中
- get(): 获取缓冲区中的数据

<br/>



### 核心属性

&emsp;Buffer 中的重要概念：

- **容量** **(capacity)** **：**表示 Buffer 最大数据容量，缓冲区容量不能为负，并且`创建后不能更改`。

- **限制** **(limit)**：第一个不应该读取或写入的数据的索引，即`位于 limit 后的数据不可读写`。缓冲区的限制不能为负，并且不能大于其容量。

-  **位置** **(position)**：下一个要读取或写入的数据的索引。缓冲区的位置不能为负，并且不能大于其限制

- **标记** **(mark)**与重置 **(reset)**：标记是一个索引，通过 Buffer 中的 mark() 方法指定 Buffer 中一个特定的 position，之后可以通过调用 reset() 方法恢复到这个 position.



> **标记、位置、限制、容量遵守以下不变式：** **0 <= mark <= position <= limit <= capacity**



<br/>



### 常用方法

| 方法                   | 描述                                                      |
| ---------------------- | --------------------------------------------------------- |
| **Buffer clear()**     | **清空缓冲区并返回对缓冲区的引用**                        |
| **Buffer flip()**      | **将缓冲区的界限设置为当前位置，并将当前位置充值为 0**    |
| int capacity()         | 返回 Buffer 的 capacity 大小                              |
| boolean hasRemaining() | 判断缓冲区中是否还有元素                                  |
| int limit()            | 返回 Buffer 的界限(limit) 的位置                          |
| Buffer limit(int n)    | 将设置缓冲区界限为 n, 并返回一个具有新 limit 的缓冲区对象 |
| Buffer mark()          | 对缓冲区设置标记                                          |
| int position()         | 返回缓冲区的当前位置 position                             |
| Buffer position(int n) | 将设置缓冲区的当前位置为 n , 并返回修改后的 Buffer 对象   |
| int remaining()        | 返回 position 和 limit 之间的元素个数                     |
| Buffer reset()         | 将位置 position 转到以前设置的 mark 所在的位置            |
| Buffer rewind()        | 将位置设为为 0， 取消设置的 mark                          |







![](https://img-blog.csdnimg.cn/20200721123633248.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)



<br/>



### 测试

```java
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
```



&emsp;运行

```java
初始化分配：
容量：1024
限制：1024
位置：0
-------------------------------
put存储数据到缓冲区：
容量：1024
限制：1024
位置：5
-------------------------------
flip切换成读取数据的模式：
容量：1024
限制：5
位置：0
-------------------------------
get读取缓冲区中的数据：
读取的数据：abcdf
容量：1024
限制：5
位置：5
-------------------------------
rewind可重复读：
容量：1024
限制：5
位置：0
-------------------------------
clear清空缓冲区：
查看第一个字节：a
容量：1024
限制：1024
位置：0
-------------------------------
```



<br/>

```java
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
```



&emsp;运行

```java
第一次获取数据：ab
此时position的位置：2
-------------------------------
第二次获取数据（mark后）：cd
此时position的位置：4
-------------------------------
reset之后
此时position的位置：2
-------------------------------
缓冲区中可以操作的数量：3
```



<br>



### 直接与非直接缓冲区

&emsp;字节缓冲区要么是**直接的**，要么是**非直接的**。如果为直接字节缓冲区，则 Java 虚拟机会尽最大努力直接在此缓冲区上执行本机 I/O 操作。也就是说，在每次调用基础操作系统的一个本机 I/O 操作之前（或之后），虚拟机都会尽量避免将缓冲区的内容复制到中间缓冲区中（或从中间缓冲区中复制内容）。

&emsp;直接字节缓冲区可以通过调用此类的 `allocateDirect()` 工厂方法来创建。此方法返回的缓冲区进行分配和取消分配所需成本通常高于非直接缓冲区。直接缓冲区的内容可以驻留在常规的垃圾回收堆之外，因此，它们对应用程序的内存需求量造成的影响可能并不明显。所以，建议将直接缓冲区主要分配给那些易受基础系统的本机 I/O 操作影响的大型、持久的缓冲区。一般情况下，最好仅在直接缓冲区能在程序性能方面带来明显好处时分配它们。

&emsp;直接字节缓冲区还可以通过 `FileChannel 的 map()` 方法 将文件区域直接映射到内存中来创建。该方法返回MappedByteBuffer 。Java 平台的实现有助于通过 JNI 从本机代码创建直接字节缓冲区。如果以上这些缓冲区中的某个缓冲区实例指的是不可访问的内存区域，则试图访问该区域不会更改该缓冲区的内容，并且将会在访问期间或稍后的某个时间导致抛出不确定的异常。

&emsp;字节缓冲区是直接缓冲区还是非直接缓冲区可通过调用其 isDirect() 方法来确定。提供此方法是为了能够在性能关键型代码中执行显式缓冲区管理



<br/>



### 非直接缓冲区

![](https://img-blog.csdnimg.cn/20200721194914684.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)



<BR/>



### 直接缓冲区

![](https://img-blog.csdnimg.cn/20200721195012603.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)



```java
//分配直接缓冲区
ByteBuffer buffer=ByteBuffer.allocateDirect(1024);

System.out.println("是否是直接缓冲区："+buffer.isDirect());
```

&emsp;运行

```java
是否是直接缓冲区：true
```





<br/>



## 通道

### 介绍

&emsp;通道（Channel）：由 java.nio.channels 包定义的。Channel 表示 IO 源与目标打开的连接。Channel 类似于传统的“流”。只不过 Channel 本身不能直接访问数据，Channel 只能与Buffer 进行交互。

![](https://img-blog.csdnimg.cn/20200721200046240.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)



![](https://img-blog.csdnimg.cn/20200721200116660.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)





![](https://img-blog.csdnimg.cn/20200721200135325.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)



<br/>



###  主要实现类

- **FileChannel**：用于读取、写入、映射和操作文件的通道。
- **DatagramChannel**：通过 UDP 读写网络中的数据通道。
- **SocketChannel**：通过 TCP 读写网络中的数据。
- **ServerSocketChannel**：可以监听新进来的 TCP 连接，对每一个新进来的连接都会创建一个 SocketChannel。

<br/>



### 获取通道

1. 获取通道的一种方式是对支持通道的对象调用`getChannel() `方法。支持通道的类如下：
   - FileInputStream
   - FileOutputStream
   -  RandomAccessFile
   -  DatagramSocket
   -  Socket
   -  ServerSocket

2. 使用 Files 类的静态方法 `newByteChannel()` 获取字节通道。
3. 通过通道的静态方法`open() `打开并返回指定通道。



<br/>



### 测试

#### 利用通道完成文件的复制（非直接缓冲区）

```java
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
```



<br/>



#### 使用直接缓冲区完成文件的复制(内存映射方式)

```java
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
```



<br/>



#### 直接通过通道进行传输

```java
FileChannel inChannel = null;
FileChannel outChannel = null;
try {
    inChannel = FileChannel.open(Paths.get("F:\\Animation\\hexo\\0.jpg"), StandardOpenOption.READ);
    outChannel = FileChannel.open(Paths.get("F:\\Animation\\3.jpg"), StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE_NEW);

    inChannel.transferTo(0, inChannel.size(), outChannel);
    //outChannel.transferFrom(inChannel,0,inChannel.size());
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
```



>transferTo底层使用的就是零拷贝
>在linux下，一个transferTo方法就可以全部传输
>在win下，一个transferTo只能发送2G，就需要分段传输文件，而且要指明传输的位置。transferFrom只能发送8M
>
>看源码:2147483647L/(1024\*1024\*1024)≈2G, 8388608L/(1024*1024)=8M



<br/>



## **分散(Scatter)和聚集(Gather)**

&emsp;分散读取（Scattering Reads）是指从 Channel 中读取的数据“分散”到多个 Buffer 中。

![](https://img-blog.csdnimg.cn/20200721205743180.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)

> 注意：按照缓冲区的顺序，从 Channel 中读取的数据依次将 Buffer 填满。

<br/>



&emsp;聚集写入（Gathering Writes）是指将多个 Buffer 中的数据“聚集”到 Channel

![](https://img-blog.csdnimg.cn/20200721212734795.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)

> 注意：按照缓冲区的顺序，写入 position 和 limit 之间的数据到 Channel 。



```java
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
```



&emsp;运行

![](https://img-blog.csdnimg.cn/20200721212638173.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)

![](https://img-blog.csdnimg.cn/20200721212658165.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)



<br/>

<br/>



## 编码和解码

### 查看编码的格式

```java
SortedMap<String, Charset> map = Charset.availableCharsets();
Set<Map.Entry<String, Charset>> entries = map.entrySet();
for (Map.Entry<String, Charset> entry : entries) {
    System.out.println(entry.getKey()+"="+entry.getValue());
}
```

![](https://img-blog.csdnimg.cn/20200721215540753.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)



<br/>



### 进行编码和解码

```java
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
```



&emsp;运行

```java
GBK编码: -64  -76  -80  -55  -93  -95  -56  -91  -80  -55  -93  -95  
GBK解码：来吧！去吧！
UTF-8解码：���ɣ�ȥ�ɣ�
```



> 可以看到UTF-8解GBK的编码会乱码。
>
> ps：GBK一个中文是两个字节



<br/>



## 阻塞和非阻塞

### 介绍

&emsp;传统的 IO 流都是阻塞式的。也就是说，**当一个线程调用 read() 或 write() 时，该线程被阻塞，直到有一些数据被读取或写入，该线程在此期间不能执行其他任务**。因此，在完成网络通信进行 IO 操作时，由于线程会阻塞，所以服务器端必须为每个客户端都提供一个独立的线程进行处理，当服务器端需要处理大量客户端时，性能急剧下降。

&emsp;Java NIO 是非阻塞模式的。**当线程从某通道进行读写数据时，若没有数据可用时，该线程可以进行其他任务**。线程通常将非阻塞 IO 的空闲时间用于在其他通道上执行 IO 操作，所以单独的线程可以管理多个输入和输出通道。因此，NIO 可以让服务器端使用一个或有限几个线程来同时处理连接到服务器端的所有客户端。

<br/>

> &emsp;完成网络通信的三个核心：通道(Channel)，缓冲区(Buffer)，选择器(Selector)



<br/>



### 阻塞式

&emsp;客户端发送请求给服务端以后，服务端不能确定客户端发送来的请求，此时线程就会处于一个阻塞状态。

&emsp;客户端发送读写请求，服务端不能确定数据是否有效，此时的线程一直处于阻塞状态，等待有正确的数据之后才会立即执行，此时任何操作都做不了。

&emsp;如果有大量请求，前方的阻塞，后方的请求也会阻塞，都会进入一个队列。

![](https://img-blog.csdnimg.cn/20200721222248190.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)



&emsp;解决方法：多线程。

&emsp;此时的问题：服务器的线程也是有限的，此时的cpu利用率没有完全的合理利用

![](https://img-blog.csdnimg.cn/2020072122233744.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)



<br/>



### 非阻塞式

&emsp;提出一个核心的组件：**选择器**

&emsp;选择器：会把每一个通道都注册到选择器上，**监控**这些通道的IO状况（读写、连接、接收数据....），在任务**完全准备好**了以后，在把任务分配给服务端的一个或多个线程上，如果没准备就绪，就不会将任务分配给服务器上，此时可以进一步利用cpu的资源。

![](https://img-blog.csdnimg.cn/20200721222547182.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)

<br/>



### 选择器(Selector)

&emsp;选择器（Selector） 是 SelectableChannle 对象的多路复用器，Selector 可以同时监控多个 SelectableChannel 的 IO 状况，也就是说，**利用 Selector 可使一个单独的线程管理多个 Channel**。Selector 是非阻塞 IO 的核心。

![](https://img-blog.csdnimg.cn/20200722124527739.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)

&emsp;SelectableChannle 的结构如下图：

![](https://img-blog.csdnimg.cn/20200721221435615.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)



<br/>



### 选择器的应用

- 创建 Selector ：通过调用 Selector.open() 方法创建一个 Selector。 

- 向选择器注册通道：SelectableChannel.register(Selector sel, int ops)

  - 当调用 register(Selector sel, int ops) 将通道注册选择器时，选择器对通道的监听事件，需要通过第二个参数 ops 指定。
  - 可以监听的事件类型（**可使用** **SelectionKey** **的四个常量表示**）
    - 读 : SelectionKey.OP_READ （1）
    - 写 : SelectionKey.OP_WRITE （4）
    - 连接 : SelectionKey.OP_CONNECT （8） 
    - 接收 : SelectionKey.OP_ACCEPT （16）

  - 若注册时不止监听一个事件，则可以使用“位或”操作符连接。

    - ```java
      SelectionKey.OP_ACCEPT|SelectionKey.OP_READ
      ```



<br/>



### **SelectionKey**

&emsp;表示 SelectableChannel 和 Selector 之间的注册关系。每次向选择器注册通道时就会选择一个事件(选择键)。选择键包含两个表示为整数值的操作集。操作集的每一位都表示该键的通道所支持的一类可选择操作。

| 方法                        | 描述                             |
| --------------------------- | -------------------------------- |
| int interestOps()           | 获取感兴趣事件集合               |
| int readyOps()              | 获取通道已经准备就绪的操作的集合 |
| SelectableChannel channel() | 获取注册通道                     |
| Selector selector()         | 返回选择器                       |
| boolean isReadable()        | 检测 Channal 中读事件是否就绪    |
| boolean isWritable()        | 检测 Channal 中写事件是否就绪    |
| boolean isConnectable()     | 检测 Channel 中连接是否就绪      |
| boolean isAcceptable()      | 检测 Channel 中接收是否就绪      |



<br/>



### **Selector** **的常用方法**

| 方法                     | 描述                                                         |
| ------------------------ | ------------------------------------------------------------ |
| Set<SelectionKey> keys() | 所有的 SelectionKey 集合。代表注册在该Selector上的Channel    |
| selectedKeys()           | 被选择的 SelectionKey 集合。返回此Selector的已选择键集       |
| int select()             | 监控所有注册的Channel，当它们中间有需要处理的 IO 操作时，该方法返回，并将对应得的 SelectionKey 加入被选择的SelectionKey 集合中，该方法返回这些 Channel 的数量。`阻塞` |
| int select(long timeout) | 可以设置超时时长的 select() 操作,单位是毫秒。`阻塞 timeout 毫秒，在 timeout毫秒后返回` |
| int selectNow()          | 执行一个立即返回的 select() 操作，该方法`不会阻塞线程`       |
| Selector wakeup()        | 使一个还未返回的 select() 方法立即返回，唤醒 selector        |
| void close()             | 关闭该选择器                                                 |







<br/>



### 阻塞式例子

#### 客户端

```java
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
```

<br/>



#### 服务端

```java
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
```



![](https://img-blog.csdnimg.cn/20200721231752845.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)



<br/>



### 非阻塞式例子(TCP )

#### 客户端

```java
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
```

<br/>



#### 服务端

```java
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
```



![](https://img-blog.csdnimg.cn/20200721235555804.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)



> 服务端进行一个轮询。
>
> 我这个测试在控制台不能输入信息，郁闷，就不搞scanner了



<br/>





### 非阻塞例子2(UDP)

#### 客户端

```java
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
```



<br/>





#### 服务端

```java
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
```





![](https://img-blog.csdnimg.cn/20200722002536247.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)



> 我设置了1分钟的超时时间





<br/>





## 管道

### 介绍

&emsp;Java NIO 管道是2个线程之间的单向数据连接。Pipe有一个source通道和一个sink通道。数据会被写到sink通道，从source通道读取。

![](https://img-blog.csdnimg.cn/20200722003328963.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQ0NzY2ODgz,size_16,color_FFFFFF,t_70)



```java
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
```



&emsp;运行

```java
通过单向管道发送数据
```



<br/>



