package me.spencez.topic.recognition.data.mock;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;
import me.spencez.topic.recognition.entity.Edge;
import me.spencez.topic.recognition.louvain.Louvain;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 模拟网络数据
 *
 * @author spence
 */
public class MockNetWork {

    /**
     * 模拟生成复杂网络
     *
     * @param nodeCount      节点数量
     * @param communityCount 社团数量
     * @return
     */
    public Network<String, Edge> mock(int nodeCount, int communityCount) {

        MutableNetwork<String, Edge> network = NetworkBuilder.undirected().allowsSelfLoops(true).build();

        // 节点i 与社团内部各个节点的连接 概率为 0.7   与 社团外点连接的概率为0.1


        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < nodeCount; j++) {
                if (i == j) {
                    continue;
                }
                if (network.hasEdgeConnecting(i + "", j + "")) {
                    continue;
                }
                network.addNode(i + "");
                network.addNode(j + "");

                float p = i % communityCount == j % communityCount ? 0.51f : 0.01f;
                boolean isConnect = p(p);
                if (isConnect) {
                    network.addEdge(i + "", j + "", new Edge(1.0));
                }
            }
        }

        System.out.println("network node size:"+network.nodes().size());
        System.out.println("network edge size:"+network.edges().size());
        return network;
    }


    /**
     * 按照 p 的概率返回 true
     * 1-p的概率返回 false
     *
     * @param p
     * @return
     */
    public boolean p(float p) {
        return new Random().nextFloat() <= p;
    }

    public static void main(String[] args) {
        MockNetWork mockNetWork = new MockNetWork();


        Network network = mockNetWork.mock(20000, 50);

        Louvain louvain = new Louvain();

        long start = System.currentTimeMillis();
        Map<String, String> communityMap = louvain.communityDetect(network);

        System.out.println("time: " + (System.currentTimeMillis() - start)/1000.0 + "s");

//        System.out.println(communityMap);

        Set<String> set = communityMap.values().stream().collect(Collectors.toSet());

        System.out.println(set);
        System.out.println(set.size());

    }


}
