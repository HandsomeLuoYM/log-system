package com.system.algorithm.entity;

import lombok.Data;

/**
 * @author ming
 * @date 2022/3/16 0:25
 * @desc
 */
@Data
public class AlgorithmContext {

    private Integer id;
    private String level;
    private String date;
    private String origin;
    private String event;
    private String workId;
    private String workClass;

    private float[] tfidfResult;
    private Double[] pcaResult;


}
