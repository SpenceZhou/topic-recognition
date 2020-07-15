package me.spencez.topic.recognition.entity;

/**
 * 复杂网络 边的实体定义
 * @author spence
 */
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
