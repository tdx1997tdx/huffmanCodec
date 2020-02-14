package tang.huffmanCodec;

import java.util.Map;

/**
 * 专门对数据进行编码解码操作的类
 */
public class DataOperation {
    private Map<String,Character> huffmanDecoding;
    private Map<Character,String> huffmanEncoding;

    public Map<String, Character> getHuffmanDecoding() {
        return huffmanDecoding;
    }

    public void setHuffmanDecoding(Map<String, Character> huffmanDecoding) {
        this.huffmanDecoding = huffmanDecoding;
    }

    public Map<Character, String> getHuffmanEncoding() {
        return huffmanEncoding;
    }

    public void setHuffmanEncoding(Map<Character, String> huffmanEncoding) {
        this.huffmanEncoding = huffmanEncoding;
    }

    //数据根据huffman编码变成0101的字符串
    public  String dataToBinary(String data){
        StringBuilder res=new StringBuilder();
        char[] charData=data.toCharArray();
        for(char i:charData){
            res.append(huffmanEncoding.get(i));
        }
        return res.toString();
    }
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



}
