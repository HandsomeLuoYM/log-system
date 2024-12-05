package com.system.algorithm.entity;

import lombok.Data;

/**
 * @author ming
 * @date 2022/3/18 0:27
 * @desc
 */
@Data
public class KmeansNode {
    int label;// label 用来记录点属于第几个 cluster
    double[] attributes;

    public KmeansNode() {
        attributes = new double[100];
    }
}
