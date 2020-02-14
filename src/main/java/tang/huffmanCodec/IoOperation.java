package tang.huffmanCodec;

import java.io.*;

public class IoOperation {

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

}
