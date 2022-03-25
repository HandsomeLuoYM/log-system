package com.system.algorithm.core;


import com.system.algorithm.entity.AlgorithmContext;
import org.ejml.data.DenseMatrix64F;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.*;
import java.util.*;

import static com.system.algorithm.core.PCAAlgorithm.runPCA;

/**
 * @author ming
 * @date 2022/3/6 20:16
 * @desc 词频分析算法
 */
public class TFIDFAlgorithm {

    private Map<String, Integer> indexMap = new HashMap<>();
    private Map<String, Float> tfidfValueMap = new HashMap<>();


    private static List<AlgorithmContext> readDirs(String filepath) throws IOException {
        List<AlgorithmContext> fileList = new ArrayList<>();
        Map<String, Integer> typeCountMap = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
        String s;
        int i = 1;
        // 使用readLine方法，一次读一行
        while((s = reader.readLine())!=null) {
            String[] split = s.split("\t");
            AlgorithmContext context = new AlgorithmContext();
            context.setId(i++);
            context.setLevel(split[0]);
            context.setDate(split[1]);
            context.setOrigin(split[2]);
            context.setEvent(split[3]);
            context.setWorkId(split[4]);
            context.setWorkClass(split[5]);
            fileList.add(context);
            typeCountMap.merge(split[2], 1, Integer::sum);
        }
        reader.close();

        typeCountMap.entrySet().forEach(entry -> {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        });

        return fileList;
    }


    private ArrayList<String> cutWords(String file) throws IOException {
        IKAnalyzer analyzer = new IKAnalyzer();
        return analyzer.split(file);
    }

    private HashMap<String, Integer> normalTF(ArrayList<String> cutwords) {
        HashMap<String, Integer> resTF = new HashMap<>();

        for (String word : cutwords) {
            resTF.merge(word, 1, Integer::sum);
        }
        return resTF;
    }

    private HashMap<String, Float> calcTf(ArrayList<String> words) {
        HashMap<String, Float> resTfMap = new HashMap<>();

        int wordLen = words.size();
        HashMap<String, Integer> intTF = normalTF(words);

        for (Map.Entry<String, Integer> entry : intTF.entrySet()) {
            resTfMap.put(entry.getKey(), Float.parseFloat(entry.getValue().toString()) / wordLen);
        }
        return resTfMap;
    }

//    private HashMap<String, HashMap<String, Integer>> normalTFAllFiles(String dirc) throws IOException {
//        HashMap<String, HashMap<String, Integer>> allNormalTF = new HashMap<>();
//
//        List<String> fileList = readDirs(dirc);
//        for (String file : fileList) {
//            HashMap<String, Integer> dict;
//            ArrayList<String> words = cutWords(file);
//
//            dict = normalTF(words);
//            allNormalTF.put(file, dict);
//        }
//        return allNormalTF;
//    }

    private HashMap<Integer, HashMap<String, Float>> calcTf(List<AlgorithmContext> fileList) throws IOException {
        HashMap<Integer, HashMap<String, Float>> allTf = new HashMap<>();

        for (AlgorithmContext file : fileList) {
            ArrayList<String> words = cutWords(file.getWorkClass());
            HashMap<String, Float> dict = calcTf(words);
            allTf.put(file.getId(), dict);
        }
        return allTf;
    }

    private HashMap<String, Float> calcIdf(HashMap<Integer, HashMap<String, Float>> allTfMap) {
        HashMap<String, Float> resIdf = new HashMap<>();
        HashMap<String, Integer> dict = new HashMap<>();
        int docNum = allTfMap.size();

        for (Map.Entry<Integer, HashMap<String, Float>> mapEntry : allTfMap.entrySet()) {
            for (Map.Entry<String, Float> entry : mapEntry.getValue().entrySet()) {
                String word = entry.getKey();
                dict.merge(word, 1, Integer::sum);
            }
        }
        for (Map.Entry<String, Integer> entry : dict.entrySet()) {
            float value = (float) Math.log(docNum / Float.parseFloat(entry.getValue().toString()));
            resIdf.put(entry.getKey(), value);
        }
        return resIdf;
    }

    private void tfIdf(HashMap<Integer, HashMap<String, Float>> allTfMap, HashMap<String, Float> idfs) {
        Map<String, HashMap<String, Float>> resTfIdf = new HashMap<>();
        int index = 0;

        for (Map.Entry<Integer, HashMap<String, Float>> mapEntry : allTfMap.entrySet()) {
            for (Map.Entry<String, Float> entry : mapEntry.getValue().entrySet()) {
                String word = entry.getKey();
                String key = getKey(entry.getKey(), entry.getValue());
                if (!indexMap.containsKey(key)) {
                    indexMap.put(key, index++);
                    tfidfValueMap.put(key, entry.getValue() * idfs.get(word));
                }
            }
        }
    }

    private String getKey(String word, Float tfValue) {
        return word + "---" + tfValue;
    }

    private void giveTfidfValue(List<AlgorithmContext> fileList, HashMap<Integer, HashMap<String, Float>> tfMap) {
        int length = indexMap.size();

        for (AlgorithmContext context : fileList) {
            float[] tfidf = new float[length];

            HashMap<String, Float> wordTfMap = tfMap.get(context.getId());
            for (Map.Entry<String, Float> entry : wordTfMap.entrySet()) {
                String key = getKey(entry.getKey(), entry.getValue());
                Integer index = indexMap.get(key);
                tfidf[index] = tfidfValueMap.get(key);
            }

            context.setTfidfResult(tfidf);

        }

    }


    private void calcTfidf(List<AlgorithmContext> fileList) throws IOException {
        HashMap<Integer, HashMap<String, Float>> tfMap = calcTf(fileList);
        HashMap<String, Float> idfs = calcIdf(tfMap);
        tfIdf(tfMap, idfs);
        giveTfidfValue(fileList, tfMap);
    }

    public static void main(String[] args) throws IOException {
        // 16 种日志
        String file = "D:\\log\\log\\log2.txt";
        int k = 49;
        List<AlgorithmContext> fileList = readDirs(file);

        // 计算tfidf
        TFIDFAlgorithm tfidfAlgorithm = new TFIDFAlgorithm();
        tfidfAlgorithm.calcTfidf(fileList);

        // 计算pca
        double[][] datas = new double[fileList.size()][fileList.get(0).getTfidfResult().length];
        for (int i = 0; i < fileList.size(); i++) {
            int length = fileList.get(i).getTfidfResult().length;
            for (int j = 0; j < length; j++) {
                datas[i][j] = fileList.get(i).getTfidfResult()[j];
            }
        }
        DenseMatrix64F denseMatrix64F = runPCA(new DenseMatrix64F(datas), k);
        for (int i = 0; i < fileList.size(); i++) {
            AlgorithmContext context = fileList.get(i);
            context.setPcaResult(new Double[k]);
            for (int j = 0; j < k; j++) {
                context.getPcaResult()[j] = denseMatrix64F.get(i, j);
            }
        }

        // 计算kmean
        KmeansAllgorithm kmeansAllgorithm = new KmeansAllgorithm(16);
        //设置原始数据集
        kmeansAllgorithm.setDataSet(fileList);
        //执行算法
        kmeansAllgorithm.execute();
        //得到聚类结果
        ArrayList<ArrayList<AlgorithmContext>> cluster= kmeansAllgorithm.getCluster();
        //查看结果
        for(int i = 0; i < cluster.size(); i++) {
            ArrayList<AlgorithmContext> contexts = cluster.get(i);
            for (int j = 0; j < contexts.size(); j++) {
                System.out.println(contexts.get(j).getOrigin() + "----"
                        + contexts.get(j).getWorkClass());
            }
            System.out.println("===================================");
        }
    }

}

