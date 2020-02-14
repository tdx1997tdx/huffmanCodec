[TOC]

# 简介

冠状病毒肆虐，在家闲来无聊，复习了数据结构，写了个huffman编解码器。

# 需求分析

设计一个中文小说压缩解压程序需求如下：

- 使用huffman编码压缩指定路径的文件，同目录下生成.huf后缀的压缩文件。
- 使用huffman解码解压.huf文件，生成原文件。



# 设计思路

## 压缩思路

1. 以byte的形式读取文件的内容
2. 统计各个byte出现次数
3. 创建hufman树
4. 生成huffman编码
5. 压缩
6. 存储压缩文件

## 解压思路

1. 获取huffman编码表
2. 根据编码表生成解码表
3. 解压
4. 存储源文件

# 具体过程

## 压缩

### 1. 读取源文件内容

```java
	/**
     * @param fileUrl 想要读取的文件路径
     * @return 文件的内容
     */
    public static String reader(String fileUrl){
        BufferedReader br;
        StringBuilder res=new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(fileUrl));
            String line = null;//用于存储读到的字符串
            while( (line = br.readLine()) != null) {
                res.append(line);
                res.append(System.getProperty("line.separator"));
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res.toString();
    }
```

### 2. 统计内容中字符出现的个数

```java
    //统计字符出现的次数
    private Map<Character,Integer> statistics(String data){
        Map<Character,Integer> res=new HashMap<>();
        char[] charData=data.toCharArray();
        for(char i:charData){
            res.merge(i,1,(oldVal, newVal) -> oldVal + newVal);
        }
        return res;
    }
```

### 3. 创建huffman树

节点类如下：

```java
public class Node {
    public int value;
    public Character byteValue;
    public Node left;
    public Node right;
}
```



```java
    /**
     * 创建huffman树
     * 1. 将Map中的值全变成Node，放入优先队列中
     * 2. 优先队列根据node中的value创建最小堆
     * 3. 每次pop出最小的node，以这两个node作为子节点组树，两个子节点的value的和作为父节点value的值
     * 4. 循环直到队列size==1
     */
    private Node buildTree(Map<Character,Integer> staticRes){
        //定义优先队列
        PriorityQueue<Node> priorityQueue=new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node n1, Node n2) {
                return n1.value-n2.value;
            }
        });
        //放入优先队列
        for(Map.Entry<Character, Integer> entry : staticRes.entrySet()){
            Node n=new Node();
            n.value=entry.getValue();
            n.byteValue=entry.getKey();
            priorityQueue.add(n);
        }
        //建树
        while (priorityQueue.size()>1){
            Node child1=priorityQueue.poll();
            Node child2=priorityQueue.poll();
            Node parent=new Node();
            parent.value=child1.value+child2.value;
            parent.left=child1;
            parent.right=child2;
            priorityQueue.offer(parent);
        }
        Node root=priorityQueue.poll();
        return root;
    }
```



### 4. 由huffman树获得huffman编码表

以递归的方式扫描

```java
	/**
     * @param root huffman树
     * @return huffman编码
     */
    private Map<Character,String> getHuffmanEncoding(Node root){
        Map<Character,String> res=new HashMap<>();
        buildMap(root,"",res);
        return res;
    }

    private void buildMap(Node node,String encoding,Map<Character,String> result){
        if(node==null) return;
        if(node.byteValue==null){
            //左0右1
            buildMap(node.left,encoding+"0",result);
            buildMap(node.right,encoding+"1",result);
        }else {
            result.put(node.byteValue,encoding);
        }
    }
```



### 5. 根据huffman编码表把原数据转换成0101字符串

```java
	//数据根据huffman编码变成0101的字符串
    public  String dataToBinary(String data){
        StringBuilder res=new StringBuilder();
        char[] charData=data.toCharArray();
        for(char i:charData){
            res.append(huffmanEncoding.get(i));
        }
        return res.toString();
    }
```



### 6. 将0101字符串转化为byte数组

```java
	//0101字符串转byte数组
    public byte[] binaryToByteArray(String data){
        StringBuilder in = new StringBuilder(data);
        //不是8的倍数在后面补0
        // 注：这里in.length() 不可在for循环内调用，因为长度在变化
        int remainder = in.length() % 8;
        if (remainder > 0)
            for (int i = 0; i < 8 - remainder; i++)
                in.append("0");
        byte[] bts = new byte[in.length() / 8];

        // Step 8 Apply compression
        for (int i = 0; i < bts.length; i++)
            bts[i] = (byte) Integer.parseInt(in.substring(i * 8, i * 8 + 8), 2);

        return bts;
    }
```

### 7. 将该数组写入指定路径存储

```java
	/**
     * @param url 写入数据的url
     * @param data 要写入的数据
     */
    public static void byteWriter(String url,byte[] data){
        File file=new File(url);
        FileOutputStream fos = null;
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```



### 8. 同时将编码表对象也进行存储

```java
	public static void objectWriter(String url,Object obj){
        File file=new File(url);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            //打开文件
            FileOutputStream fileStream=new FileOutputStream(url);
            //创建对象输出流
            ObjectOutputStream oStream=new ObjectOutputStream(fileStream);
            //写入对象
            oStream.writeObject(obj);
            //关闭对象输出流
            oStream.close();
            //关闭文件流
            fileStream.close();
        }catch (Exception e) {
            e.printStackTrace();// : handle exception
        }
    }
```



## 解压

### 1. 读取压缩数据

```java
 	/**
     * @param url 读取数据的url
     * @return 字节数组
     */
    public static byte[] byteReader(String url){
        File file=new File(url);
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            System.out.println("file too big...");
            return null;
        }
        byte[] buffer = null;
        try (FileInputStream fi = new FileInputStream(file)) {
            buffer = new byte[(int) fileSize];
            int offset = 0;
            int numRead = 0;
            while (offset < buffer.length
                    && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {
                offset += numRead;
            }
            // 确保所有数据均被读取
            if (offset != buffer.length) {
                throw new IOException("Could not completely read file "
                        + file.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }
```



### 2. 读取编码表对象

```java
	public static Object objectReader(String url){
        File file=new File(url);
        Object oneObject=null;
        try {
            //打开文件
            FileInputStream fileStream=new FileInputStream(file);
            //创建对象输入流
            ObjectInputStream iStream=new ObjectInputStream(fileStream);
            //读取对象
            oneObject=iStream.readObject();
            //关闭对象输入流
            iStream.close();
            //关闭文件输入流
            fileStream.close();
        } catch (Exception e) {
            e.printStackTrace();// TODO: handle exception
        }
        return oneObject;
    }
```



### 3. 由编码表获取解码表

```java
	public void decoding(){
        decodingMap=new HashMap<>();
        for(Map.Entry<Character, String> entry : encodingMap.entrySet()){
            Character mapKey = entry.getKey();
            String mapValue = entry.getValue();
            decodingMap.put(mapValue,mapKey);
        }
    }
```



### 4. 根据byte数组转化为0101字符串

```java
	//byte数组转0101字符串
    public static String bytes2String(byte[] bts) {
        String[] dic = { "0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111",
                "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111" };
        StringBuilder out = new StringBuilder();
        for (byte b : bts) {
            String s = String.format("%x", b);
            s = s.length() == 1? "0" + s: s;
            out.append(dic[Integer.parseInt(s.substring(0, 1), 16)]);
            out.append(dic[Integer.parseInt(s.substring(1, 2), 16)]);
        }
        return out.toString();
    }
```



### 5. 根据0101字符串和解码表还原数据

```java
	//0101串更具huffman解码还原数据
    public String stringToData(String rawData){
        StringBuilder res=new StringBuilder();
        StringBuilder character=new StringBuilder();
        int index=0;
        while(index<rawData.length()){
            character.append(rawData.charAt(index));
            index++;
            if(huffmanDecoding.get(character.toString())!=null){
                res.append(huffmanDecoding.get(character.toString()));
                character=new StringBuilder();
            }
        }
        return res.toString();
    }
```



### 6. 将数据存储到指定路径

```java
	/**
     * @param url 文件保存路径
     * @param data 写入文件的数据
     */
    public static void writer(String url,String data){
        File file=new File(url);
        BufferedWriter writer = null;
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(data);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```



# huffman压缩的优缺点

对文本的压缩率很高。对非文本文件几乎没有压缩效果。

实现Huffman编码的基础是统计源数据集中各信号的概率分布。非文本文件字符出现概率随机，使用huffman编码无法得到较好的压缩效果

