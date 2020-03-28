package com.hbxy.gmall.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Data
public class BaseAttrInfo implements Serializable {
    @Id
    @Column
    //主键自增
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column
    private String attrName;
    @Column
    private String catalog3Id;
    @Transient//表示数据库中没有的字段，但业务中需要
    private List<BaseAttrValue> attrValueList;

}


