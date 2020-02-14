package tang.huffmanCodec;

import org.junit.Test;

import java.util.Map;

public class HuffmanTest {
    @Test
    public void test1(){
        HuffmanTool.compress("F:\\daoshi.txt");
    }

    @Test
    public void test2(){
        HuffmanTool.deCompression("F:\\daoshi.txt","F:\\work\\daoshi.txt");
    }
}
