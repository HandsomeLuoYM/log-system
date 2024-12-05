package com.system.algorithm.core;

import com.system.algorithm.entity.PCAEDInfo;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

/**
 * @author ming
 * @date 2022/3/6 2:18
 * @desc 主成分分析法
 */
public class PCAAlgorithm {

    public static int[] argsort(double[] input) {

        int[] rs = new int[input.length];

        for (int i = 0; i < input.length; i++) {
            rs[i] = i;
        }

        for (int i = 0; i < input.length - 1; i++) {
            for (int j = i + 1; j < input.length; j++) {
                if (input[i] < input[j]) {

                    double tmp = input[i];
                    int tmpIndex = rs[j];

                    input[i] = input[j];
                    input[j] = tmp;

                    rs[j] = rs[i];
                    rs[i] = tmpIndex;

                }
            }
        }

        return rs;
    }

    public static PCAEDInfo JacobiCount(DenseMatrix64F src, double diff, int iter) {

        DenseMatrix64F values = new DenseMatrix64F(src.numRows, src.numCols);

        for (int i = 0; i < src.numRows; i++) {
            for (int j = 0; j < src.numCols; j++) {
                if (i == j) {
                    values.set(i, j, 1);
                } else {
                    values.set(i, j, 0);
                }
            }
        }

        int nCount = 0;

        while (true) {

            double dbMax = Double.MIN_VALUE;
            int nRow = 0;
            int nCol = 1;

            for (int i = 0; i < src.numRows; i++) {
                for (int j = 0; j < src.numCols; j++) {
                    if (i != j && Math.abs(src.get(i, j)) > dbMax) {
                        dbMax = Math.abs(src.get(i, j));
                        nRow = i;
                        nCol = j;
                    }
                }
            }

            if (dbMax < diff) {
                break;
            }

            if (nCount > iter) {
                break;
            }

            nCount++;

            double dbApp = src.get(nRow, nRow);
            double dbApq = src.get(nRow, nCol);
            double dbAqq = src.get(nCol, nCol);

            double dbAngle = 0.5 * Math.atan2(-2 * dbApq, dbAqq - dbApp);
            double dbSinTheta = Math.sin(dbAngle);
            double dbCosTheta = Math.cos(dbAngle);
            double dbSin2Theta = Math.sin(2 * dbAngle);
            double dbCos2Theta = Math.cos(2 * dbAngle);


            src.set(nRow, nRow, dbApp * dbCosTheta * dbCosTheta +
                    dbAqq * dbSinTheta * dbSinTheta + 2 * dbApq * dbCosTheta * dbSinTheta);
            src.set(nCol, nCol, dbApp * dbSinTheta * dbSinTheta +
                    dbAqq * dbCosTheta * dbCosTheta - 2 * dbApq * dbCosTheta * dbSinTheta);
            src.set(nRow, nCol, 0.5 * (dbAqq - dbApp) * dbSin2Theta + dbApq * dbCos2Theta);
            src.set(nCol, nRow, src.get(nRow, nCol));

            for (int i = 0; i < src.numRows; i++) {
                if ((i != nCol) && (i != nRow)) {
                    dbMax = src.get(i, nRow);
                    src.set(i, nRow, src.get(i, nCol) * dbSinTheta + dbMax * dbCosTheta);
                    src.set(i, nCol, src.get(i, nCol) * dbCosTheta - dbMax * dbSinTheta);
                }
            }


            for (int j = 0; j < src.numRows; j++) {
                if ((j != nCol) && (j != nRow)) {
                    dbMax = src.get(nRow, j);
                    src.set(nRow, j, src.get(nCol, j) * dbSinTheta + dbMax * dbCosTheta);
                    src.set(nCol, j, src.get(nCol, j) * dbCosTheta - dbMax * dbSinTheta);
                }
            }


            for (int i = 0; i < src.numRows; i++) {
                dbMax = values.get(i, nRow);
                values.set(i, nRow, values.get(i, nCol) * dbSinTheta + dbMax * dbCosTheta);
                values.set(i, nCol, values.get(i, nCol) * dbCosTheta - dbMax * dbSinTheta);
            }


        }


        double[] eig = new double[src.numRows];


        for (int i = 0; i < src.numRows; i++) {
            eig[i] = src.get(i, i);
        }


        int[] sortInx = argsort(eig);

        DenseMatrix64F tmpValues = new DenseMatrix64F(src.numRows, src.numCols);

        for (int i = 0; i < src.numRows; i++) {

            for (int j = 0; j < src.numRows; j++) {

                tmpValues.set(i, j, values.get(j, sortInx[i]));
            }

            eig[i] = src.get(sortInx[i], sortInx[i]);

        }


        for (int i = 0; i < src.numRows; i++) {
            double dSumVec = 0;
            for (int j = 0; j < src.numRows; j++) {
                dSumVec += tmpValues.get(j, i);
            }
            if (dSumVec < 0) {
                for (int j = 0; j < src.numRows; j++) {
                    tmpValues.set(j, i, tmpValues.get(j, i) * -1);
                }
            }
        }

        return new PCAEDInfo(tmpValues, eig);

    }

    public static DenseMatrix64F runPCA(DenseMatrix64F src, int k) {

        DenseMatrix64F rs = new DenseMatrix64F(src.numRows, k);

        //计算输入矩阵每个元素和特征值平均的差值矩阵
        DenseMatrix64F norm_X = new DenseMatrix64F(src.numRows, src.numCols);

        for (int i = 0; i < src.numCols; i++) {
            double tmp = 0;
            for (int j = 0; j < src.numRows; j++) {
                tmp += src.get(j, i);
            }
            tmp /= src.numRows;
            for (int j = 0; j < src.numRows; j++) {
                norm_X.set(j, i, src.get(j, i) - tmp);
            }
        }


        //计算协方差矩阵
        DenseMatrix64F norm_X_T = new DenseMatrix64F(src.numCols, src.numRows);

        CommonOps.transpose(norm_X, norm_X_T);

        DenseMatrix64F scatter_matrix = new DenseMatrix64F(src.numCols, src.numCols);

        CommonOps.mult(norm_X_T, norm_X, scatter_matrix);


        //特征向量分解
        PCAEDInfo ed = JacobiCount(new DenseMatrix64F(scatter_matrix), 0.001, 1000);
        //选取前k个特征
        DenseMatrix64F feature = new DenseMatrix64F(k, src.numCols);

        for (int i = 0; i < k; i++) {
            for (int j = 0; j < src.numCols; j++) {
                feature.set(i, j, ed.getValues().get(j, i));
            }
        }
        DenseMatrix64F feature_T = new DenseMatrix64F(src.numCols, k);
        CommonOps.transpose(feature, feature_T);
        CommonOps.mult(norm_X, feature_T, rs);
        return rs;
    }



    public static void main(String[] args) {
        double[][] datas = {{-1, 1}, {-2, -1}, {-3, -2}, {1, 1}, {2, 1}, {3, 2}};
        System.out.println(runPCA(new DenseMatrix64F(datas),2));
    }
}
