package com.system.algorithm.core;

import com.system.algorithm.entity.AlgorithmContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author ming
 * @date 2022/3/18 0:48
 * @desc
 */
public class KmeansAllgorithm {

    private int numOfCluster;// 分成多少簇
    private int timeOfIteration;// 迭代次数
    private int dataSetLength;// 数据集元素个数，即数据集的长度
    private List<AlgorithmContext> dataSet;// 数据集链表
    private ArrayList<Double[]> center;// 中心链表
    private ArrayList<ArrayList<AlgorithmContext>> cluster; //簇
    private ArrayList<Double> sumOfErrorSquare;// 误差平方和
    private Random random;

    /**
     * 设置需分组的原始数据集
     *
     * @param dataSet
     */

    public void setDataSet(List<AlgorithmContext> dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * 获取结果分组
     *
     * @return 结果集
     */

    public ArrayList<ArrayList<AlgorithmContext>> getCluster() {
        return cluster;
    }

    /**
     * 构造函数，传入需要分成的簇数量
     *
     * @param numOfCluster 簇数量,若numOfCluster<=0时，设置为1，若numOfCluster大于数据源的长度时，置为数据源的长度
     */
    public KmeansAllgorithm(int numOfCluster) {
        if (numOfCluster <= 0) {
            numOfCluster = 1;
        }
        this.numOfCluster = numOfCluster;
    }

    /**
     * 初始化
     */
    private void init() {
        timeOfIteration = 0;
        random = new Random();
        dataSetLength = dataSet.size();
        //若numOfCluster大于数据源的长度时，置为数据源的长度
        if (numOfCluster > dataSetLength) {
            numOfCluster = dataSetLength;
        }
        center = initCenters();
        cluster = initCluster();
        sumOfErrorSquare = new ArrayList<Double>();
    }

    /**
     * 初始化中心数据链表，分成多少簇就有多少个中心点
     *
     * @return 中心点集
     */
    private ArrayList<Double[]> initCenters() {
        ArrayList<Double[]> center = new ArrayList<>();
        int[] randoms = new int[numOfCluster];
        boolean flag;
        int temp = random.nextInt(dataSetLength);
        randoms[0] = temp;
        //randoms数组中存放数据集的不同的下标
        for (int i = 1; i < numOfCluster; i++) {
            flag = true;
            while (flag) {
                temp = random.nextInt(dataSetLength);

                int j;
                for (j = 0; j < i; j++) {
                    if (randoms[j] == temp) {
                        break;
                    }
                }

                if (j == i) {
                    flag = false;
                }
            }
            randoms[i] = temp;
        }
        for (int i = 0; i < numOfCluster; i++) {
            // 生成初始化中心链表
            center.add(dataSet.get(randoms[i]).getPcaResult());
        }
        return center;
    }

    /**
     * 初始化簇集合
     *
     * @return 一个分为k簇的空数据的簇集合
     */
    private ArrayList<ArrayList<AlgorithmContext>> initCluster() {
        ArrayList<ArrayList<AlgorithmContext>> cluster = new ArrayList<>();
        for (int i = 0; i < numOfCluster; i++) {
            cluster.add(new ArrayList<>());
        }

        return cluster;
    }

    /**
     * 计算两个点之间的距离
     *
     * @param element 点1
     * @param center  点2
     * @return 距离
     */
    private double distance(AlgorithmContext element, Double[] center) {
        Double[] point1 = element.getPcaResult();
        double distance = 0.0d;
        int length = point1.length;
        for (int i = 0; i < length; i++) {
            distance += ((point1[i] - center[i]) * (point1[i] - center[i]));
        }
        distance = Math.sqrt(distance);
        return distance;
    }

    /**
     * 获取距离集合中最小距离的位置
     *
     * @param distance 距离数组
     * @return 最小距离在距离数组中的位置
     */
    private int minDistance(Double[] distance) {
        Double minDistance = distance[0];
        int minLocation = 0;
        for (int i = 1; i < distance.length; i++) {
            if (distance[i] <= minDistance) {
                minDistance = distance[i];
                minLocation = i;
            }
        }
        return minLocation;
    }

    /**
     * 核心，将当前元素放到最小距离中心相关的簇中
     */
    private void clusterSet() {
        Double[] distance = new Double[numOfCluster];
        for (int i = 0; i < dataSetLength; i++) {
            for (int j = 0; j < numOfCluster; j++) {
                distance[j] = distance(dataSet.get(i), center.get(j));
            }
            int minLocation = minDistance(distance);
            // 核心，将当前元素放到最小距离中心相关的簇中
            cluster.get(minLocation).add(dataSet.get(i));

        }
    }

    /**
     * 求两点误差平方的方法
     *
     * @param element 点1
     * @param center  点2
     * @return 误差平方
     */
    private Double errorSquare(AlgorithmContext element, Double[] center) {
        Double[] point = element.getPcaResult();
        double distance = 0.0d;
        int length = point.length;
        for (int i = 0; i < length; i++) {
            distance += ((point[i] - center[i]) * (point[i] - center[i]));
        }
        return distance;
    }

    /**
     * 计算误差平方和准则函数方法
     */
    private void countRule() {
        Double jcF = 0D;
        for (int i = 0; i < cluster.size(); i++) {
            for (int j = 0; j < cluster.get(i).size(); j++) {
                jcF += errorSquare(cluster.get(i).get(j), center.get(i));

            }
        }
        sumOfErrorSquare.add(jcF);
    }

    /**
     * 设置新的簇中心方法
     */
    private void setNewCenter() {
        for (int i = 0; i < numOfCluster; i++) {
            int n = cluster.get(i).size();
            if (n != 0) {
                int length = center.get(i).length;
                Double[] newCenter = new Double[length];
                for (int j = 0; j < length; j++) {
                    newCenter[j] = (double) 0;
                }

                for (int j = 0; j < n; j++) {
                    for (int k = 0; k < length; k++) {
                        newCenter[k] += cluster.get(i).get(j).getPcaResult()[k];
                    }
                }
                // 设置一个平均值
                for (int k = 0; k < length; k++) {
                    newCenter[k] /= n;
                }
                center.set(i, newCenter);
            }
        }
    }

    /**
     * 打印数据，测试用
     *
     * @param dataArray     数据集
     * @param dataArrayName 数据集名称
     */
    public void printDataArray(ArrayList<Double[]> dataArray,
                               String dataArrayName) {
        for (int i = 0; i < dataArray.size(); i++) {
            System.out.println("print:" + dataArrayName + "[" + i + "]={"
                    + Arrays.toString(dataArray.get(i)) + "}");
        }
        System.out.println("===================================");
    }

    /**
     * Kmeans算法核心过程方法
     */
    private void kmeans() {
        init();

        // 循环分组，直到误差不变为止
        while (true) {
            clusterSet();
            countRule();
            // 误差不变了，分组完成
            if (timeOfIteration != 0) {
                if (sumOfErrorSquare.get(timeOfIteration) - sumOfErrorSquare.get(timeOfIteration - 1) == 0) {
                    break;
                }
            }

            setNewCenter();
            timeOfIteration++;
            cluster.clear();
            cluster = initCluster();
        }
    }

    /**
     * 执行算法
     */
    public void execute() {
        long startTime = System.currentTimeMillis();
        System.out.println("kmeans begins");
        kmeans();
        long endTime = System.currentTimeMillis();
        System.out.println("kmeans running time=" + (endTime - startTime)
                + "ms");
        System.out.println("kmeans ends");
        System.out.println();
    }
}
