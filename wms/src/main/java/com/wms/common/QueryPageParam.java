package com.wms.common;

import lombok.Data;

import java.util.HashMap;

@Data
/**
 * 返回数据分页处理
 */
public class QueryPageParam {
    //默认
    private static int PAGE_SIZE=20;//默认数据数
    private static int PAGE_NUM=1;//默认当前页

    private int pageSize=PAGE_SIZE;
    private int pageNum=PAGE_NUM;

    private HashMap param = new HashMap();

}