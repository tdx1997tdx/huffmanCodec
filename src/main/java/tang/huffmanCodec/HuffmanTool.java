package tang.huffmanCodec;

import java.util.HashMap;
import java.util.Map;

public class HuffmanTool {
    /**
     * @param fileName 要压缩的文件路径
     */
    public static void compress(String fileName){
        //读取数据
        String data=IoOperation.reader(fileName);
        //编码
        HuffmanCore huffmanCore=new HuffmanCore();
        huffmanCore.encoding(data);
        DataOperation dataOperation=new DataOperation();
        Map<Character,String> encodingMap=huffmanCore.getEncodingMap();
        dataOperation.setHuffmanEncoding(encodingMap);
        //写数据
        String binaryData=dataOperation.dataToBinary(data);
        byte[] byteData=dataOperation.binaryToByteArray(binaryData);
        System.out.println(fileName+".huf");
        IoOperation.byteWriter(fileName+".huf",byteData);
        //写编码对象
        IoOperation.objectWriter(fileName+".obj",encodingMap);
    }

    /**
     * @param fileUrl 压缩文件路径，不需要加后缀
     * @param deCompressUrl 要保存的路径
     */
    public static void deCompression(String fileUrl,String deCompressUrl){
        //读取压缩数据
        byte[] data=IoOperation.byteReader(fileUrl+".huf");
        //读取编码对象
        Map<Character,String> encodingMap=(HashMap)IoOperation.objectReader(fileUrl+".obj");
        //获取解码表
        HuffmanCore huffmanCore=new HuffmanCore();
        huffmanCore.setEncodingMap(encodingMap);
        huffmanCore.decoding();
        Map<String,Character> decodingMap=huffmanCore.getDecodingMap();
        //解码数据
        DataOperation dataOperation=new DataOperation();
        dataOperation.setHuffmanDecoding(decodingMap);
        String binary=DataOperation.bytes2String(data);
        String writeData=dataOperation.stringToData(binary);
        //写入文件
        IoOperation.writer(deCompressUrl,writeData);

    }
}
