package me.spencez.topic.recognition;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.NetworkBuilder;
import me.spencez.topic.recognition.louvain.Edge;
import me.spencez.topic.recognition.louvain.Louvain;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

public class App {

    public static void main(String[] args) {


        try {

            MutableNetwork<String, Edge> network = NetworkBuilder.undirected().allowsSelfLoops(true).build();

            File file = new File("/home/spence/Desktop/connected-caveman-graph.csv");
            List<String> lines = FileUtils.readLines(file, Charset.defaultCharset());
            for (String line : lines) {
                String[] arr = line.split(",");
                if(network.hasEdgeConnecting(arr[0],arr[1])){
                    continue;
                }
                network.addEdge(arr[0],arr[1],new Edge(1.0));
            }

            Louvain louvain = new Louvain();
            Map<String, String> communityMap = louvain.communityDetect(network);

            System.out.println(communityMap);

            Set<String> communitySet = new HashSet<>();
            for (String node : communityMap.keySet()) {
                communitySet.add(communityMap.get(node));
            }

            System.out.println(communitySet);
            System.out.println(communitySet.size());


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
