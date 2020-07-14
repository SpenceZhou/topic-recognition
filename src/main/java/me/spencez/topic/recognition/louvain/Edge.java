package me.spencez.topic.recognition.louvain;

public class Edge {

    public Double weight;

    public Edge(Double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "" + weight;
    }
}
