package me.spencez.topic.recognition.louvain;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.Network;
import com.google.common.graph.NetworkBuilder;
import me.spencez.topic.recognition.entity.Edge;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 基于最大化模块度的层次聚类算法 lovain 的Java版本实现
 * <p>
 * Algorithm:
 * <p>
 * Vincent D Blondel, Jean-Loup Guillaume, Renaud Lambiotte, Etienne Lefebvre,
 * Fast unfolding of communities in large networks,
 * in Journal of Statistical Mechanics: Theory and Experiment 2008 (10), P1000
 *
 * @author spence
 */
public class Louvain {

    /**
     * 利用Louvain 算法进行社团划分
     * <p>
     * 返回的Map的key为节点，value为社团
     *
     * @param network
     * @return
     */
    public Map<String, String> communityDetect(Network<String, Edge> network) {

        // m 代表网络所有边的权重之和
        Double m = 0.0;
        Set<Edge> edges = network.edges();
        for (Edge edge : edges) {
            m += edge.weight;
        }

        Map<String, Double> KiMap = new HashMap<>();
        for (String i : network.nodes()) {
            KiMap.put(i, getKi(network, i));
        }


        // 层次聚类算法 单层计算
        Double maxQ = 0.0;

        Network<String, Edge> newNetwork = network;
        Map<String, String> communityMap = null;
        int layer = 0;
        while (true) {
            Map<String, String> layerCommunityMap = communityDetectLayer(newNetwork);

            if (communityMap == null) {
                communityMap = layerCommunityMap;
            } else {
                for (String node : communityMap.keySet()) {
                    String community = communityMap.get(node);
                    if (layerCommunityMap.containsKey(community)) {
                        community = layerCommunityMap.get(community);
                        communityMap.put(node, community);
                    }
                }
            }

            Double q = q(network, communityMap, m, KiMap);
            if (q <= maxQ) {
                break;
            }
            maxQ = q;

//            System.out.println("layer " + layer + " network = " + newNetwork);
            System.out.println("layer " + layer + " q = " + q);
//            System.out.println("layer " + layer + " community = " + communityMap);

            //压缩网络
            newNetwork = zipNetwork(newNetwork, layerCommunityMap);
            layer++;
        }

        return communityMap;
    }


    /**
     * 基于 优化模块度 的单层 社团发现
     *
     * @param network
     * @return
     */
    private Map<String, String> communityDetectLayer(Network<String, Edge> network) {

        // m 代表网络所有边的权重之和
        Double m = 0.0;
        Set<Edge> edges = network.edges();
        for (Edge edge : edges) {
            m += edge.weight;
        }

        Map<String, Double> KiMap = new HashMap<>();
        for (String i : network.nodes()) {
            KiMap.put(i, getKi(network, i));
        }

        // step1 将每个节点单独划分为一个社团
        Map<String, String> communityMap = new HashMap<>();

        for (String node : network.nodes()) {
            communityMap.put(node, node);
        }

        // step2 遍历所有节点，依次将节点i划分到相邻节点j所在的社团中，计算每次划分后的模块度增益deltaQ，
        //       取Max deltaQ， 如果Max deltaQ >0 则将节点i划分到 产生Max deltaQ 的社团中

        for (String i : network.nodes()) {
            Double maxDeltaQ = 0.0;
            String maxDeltaQCommunity = null;
            Set<String> adjacentNodes = network.adjacentNodes(i);
            for (String j : adjacentNodes) {
                if (i.equals(j)) {
                    continue;
                }
                String deltaQCommunity = communityMap.get(j);

                Double deltaQ = deltaQ(network, i, getCommunityNodes(j, communityMap), m, KiMap);
                if (deltaQ > maxDeltaQ) {
                    maxDeltaQ = deltaQ;
                    maxDeltaQCommunity = deltaQCommunity;
                }
            }
            if (maxDeltaQ > 0) {
                communityMap.put(i, maxDeltaQCommunity);
            }
        }

        return communityMap;
    }

    /**
     * 获取节点j所在社团的所有节点
     *
     * @param node
     * @param communityMap
     * @return
     */
    private Set<String> getCommunityNodes(String node, Map<String, String> communityMap) {
        Set<String> set = new HashSet<>();
        set.add(node);
        String community = communityMap.get(node);
        for (String key : communityMap.keySet()) {
            String value = communityMap.get(key);
            if (community.equals(value)) {
                set.add(key);
            }
        }
        return set;
    }

    /**
     * 计算在目前社团划分情况下 整个图（网络）的模块度Q
     *
     * @param network
     * @param communityMap 其中 key 表示 节点 value表示节点所属的社团
     * @return
     */
    public double q(Network<String, Edge> network, Map<String, String> communityMap) {

        // m 代表网络所有边的权重之和
        Double m = 0.0;
        Set<Edge> edges = network.edges();
        for (Edge edge : edges) {
            m += edge.weight;
        }
        Set<String> nodes = network.nodes();
        Double sum = 0.0;
        for (String i : nodes) {
            for (String j : nodes) {
                if (i.equals(j)) {
                    continue;
                }
                String communityI = communityMap.get(i);
                String communityJ = communityMap.get(j);
                if (!communityI.equals(communityJ)) {
                    continue;
                }

                // Aij  表示节点 i j 之间的权重
                Edge edgeIJ = network.edgeConnectingOrNull(i, j);

                if (edgeIJ != null && edgeIJ.weight > 0) {
                    Double Aij = edgeIJ.weight;
                    // Ki  表示代表 节点i所有相连边的权重之和
                    Double Ki = getKi(network, i);
                    Double Kj = getKi(network, j);
                    sum += Aij - Ki * Kj / (2 * m);
                }
            }
        }

        return sum / (2 * m);
    }


    /**
     * 计算在目前社团划分情况下 整个图（网络）的模块度Q
     *
     * @param network
     * @param communityMap 其中 key 表示 节点 value表示节点所属的社团
     * @param m
     * @param KiMap
     * @return
     */
    public double q(Network<String, Edge> network, Map<String, String> communityMap, Double m, Map<String, Double> KiMap) {

        Set<String> nodes = network.nodes();
        Double sum = 0.0;
        for (String i : nodes) {
            for (String j : nodes) {
                if (i.equals(j)) {
                    continue;
                }
                String communityI = communityMap.get(i);
                String communityJ = communityMap.get(j);
                if (!communityI.equals(communityJ)) {
                    continue;
                }

                // Aij  表示节点 i j 之间的权重
                Edge edgeIJ = network.edgeConnectingOrNull(i, j);

                if (edgeIJ != null && edgeIJ.weight > 0) {
                    Double Aij = edgeIJ.weight;
                    // Ki  表示代表 节点i所有相连边的权重之和
                    Double Ki = KiMap.get(i);
                    Double Kj = KiMap.get(j);
                    sum += Aij - Ki * Kj / (2 * m);
                }
            }
        }

        return sum / (2 * m);
    }

    /**
     * 获取网络中 节点i 的相连边权重之和
     *
     * @param network
     * @param i
     * @return
     */
    private Double getKi(Network<String, Edge> network, String i) {

        Double value = 0.0;
        Set<Edge> connectedEdgesI = network.incidentEdges(i);
        if (connectedEdgesI != null) {
            for (Edge edge : connectedEdgesI) {
                value += edge.weight;
            }
        }
        return value;
    }

    /**
     * 将节点i 划分到 节点j 后网络的 模块度增益
     * <p>
     * deltaQ = (Ki,in/m) - Ki*∑tot/(2*m*m)
     * <p>
     * m 代表网络所有边的权重之和
     * <p>
     * Ki,in  代表 节点i与所属社团内部所有边的权重之和 用 Ki_in 表示
     * Ki     代表 节点i所有相连边的权重之和
     * ∑tot   代表 与社团内所有节点相连边的权重之和  用 Sigma_tot 表示
     *
     * @param network
     * @param i
     * @param communityJSet 节点j所在社团的 节点集合
     * @param m
     * @param KiMap
     * @return
     */

    public Double deltaQ(Network<String, Edge> network, String i, Set<String> communityJSet, Double m, Map<String, Double> KiMap) {

        Double Ki_in = 0.0;
        for (String j : communityJSet) {
            Edge edge = network.edgeConnectingOrNull(i, j);
            if (edge != null) {
                Ki_in += edge.weight;
            }
        }

        Double Ki = KiMap.get(i);

        Double Sigma_tot = 0.0;
        for (String j : communityJSet) {
            Sigma_tot += KiMap.get(j);
        }

        //deltaQ = (Ki_in/m) - Ki*Sigma_tot/(2*m*m)
        Double deltaQ = (Ki_in / m) - Ki * Sigma_tot / (2 * m * m);
        return deltaQ;
    }


    /**
     * 将图按照社团结构进行压缩
     * <p>
     * 同一个社团的所有节点压缩成一个节点，并在此节点上 添加一个 self-loop，权重为 社团内所有边的权重之和
     * 并更新新的节点之间的权重
     * <p>
     * 返回压缩后的图
     *
     * @param network
     * @param communityMap
     * @return
     */
    public Network<String, Edge> zipNetwork(Network<String, Edge> network, Map<String, String> communityMap) {
        Map<String, Set<String>> communityNodesMap = new HashMap<>();
        for (String node : communityMap.keySet()) {
            String community = communityMap.get(node);
            Set<String> set;
            if (communityNodesMap.containsKey(community)) {
                set = communityNodesMap.get(community);
            } else {
                set = new HashSet<>();
            }
            set.add(node);
            communityNodesMap.put(community, set);
        }

        MutableNetwork<String, Edge> zipNetwork = NetworkBuilder.undirected().allowsSelfLoops(true).build();
        for (String i : communityNodesMap.keySet()) {
            Set<String> communityINodes = communityNodesMap.get(i);
            for (String j : communityNodesMap.keySet()) {

                if (zipNetwork.hasEdgeConnecting(i, j)) {
                    continue;
                }
                Set<String> communityJNodes = communityNodesMap.get(j);
                Double weight = 0.0;
                for (String x : communityINodes) {
                    for (String y : communityJNodes) {
                        Edge edge = network.edgeConnectingOrNull(x, y);
                        if (edge != null) {
                            weight += edge.weight;
                        }
                    }
                }
                if (weight > 0) {
                    zipNetwork.addEdge(i, j, new Edge(weight));
                }
            }
        }
        return zipNetwork;
    }


    public static void main(String[] args) {

        MutableNetwork<String, Edge> network = NetworkBuilder.undirected().allowsSelfLoops(true).build();

        network.addEdge("0", "1", new Edge(1.0));
        network.addEdge("1", "2", new Edge(1.0));
        network.addEdge("0", "3", new Edge(1.0));

        network.addEdge("4", "5", new Edge(1.0));
        network.addEdge("4", "6", new Edge(1.0));
        network.addEdge("4", "7", new Edge(1.0));


        Louvain louvain = new Louvain();
        Map<String, String> communityMap = louvain.communityDetect(network);

        System.out.println(communityMap);
    }
}
