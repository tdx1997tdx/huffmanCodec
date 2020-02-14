package tang.huffmanCodec;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanCore {

    private Map<Character,String> encodingMap;
    private Map<String,Character> decodingMap;

    public Map<Character, String> getEncodingMap() {
        return encodingMap;
    }

    public Map<String, Character> getDecodingMap() {
        return decodingMap;
    }

    public void setEncodingMap(Map<Character, String> encodingMap) {
        this.encodingMap = encodingMap;
    }

    public void setDecodingMap(Map<String, Character> decodingMap) {
        this.decodingMap = decodingMap;
    }

    /**
     * huffman编码
     * @param data 文本数据
     */
    public void encoding(String data){
        //1. 获取统计自处出现的次数map
        Map<Character,Integer> charStatistics=statistics(data);
        //2. 根据统计结果创建huffman树
        Node root=buildTree(charStatistics);
        //3. 根据huffman树获取huffman编码
        encodingMap= getHuffmanEncoding(root);
    }

    public void decoding(){
        decodingMap=new HashMap<>();
        for(Map.Entry<Character, String> entry : encodingMap.entrySet()){
            Character mapKey = entry.getKey();
            String mapValue = entry.getValue();
            decodingMap.put(mapValue,mapKey);
        }
    }

    //统计字符出现的次数
    private Map<Character,Integer> statistics(String data){
        Map<Character,Integer> res=new HashMap<>();
        char[] charData=data.toCharArray();
        for(char i:charData){
            res.merge(i,1,(oldVal, newVal) -> oldVal + newVal);
        }
        return res;
    }

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




}
