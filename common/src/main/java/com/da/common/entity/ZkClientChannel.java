package com.da.common.entity;

import lombok.Data;

@Data
public class ZkClientChannel extends BaseEntity {
    /** 客户端的clientId */
    private String clientId;
    /** 客户端的channelId */
    private String channelId;
}
