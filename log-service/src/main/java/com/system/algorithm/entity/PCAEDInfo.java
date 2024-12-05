package com.system.algorithm.entity;

import org.ejml.data.DenseMatrix64F;

/**
 * @author ming
 * @date 2022/3/6 2:17
 * @desc
 */
public class PCAEDInfo {


    private DenseMatrix64F values;

    private double[] vectors;


    public PCAEDInfo(DenseMatrix64F values, double[] vectors) {
        this.values = values;
        this.vectors = vectors;
    }

    public DenseMatrix64F getValues() {
        return values;
    }

    public void setValues(DenseMatrix64F values) {
        this.values = values;
    }

    public double[] getVectors() {
        return vectors;
    }

    public void setVectors(double[] vectors) {
        this.vectors = vectors;
    }

}
