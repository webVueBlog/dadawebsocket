package com.da.common.entity;

import lombok.Data;

@Data
public class ConnectNettyServer {
    /** Netty连接地址和端口 */
    private String nettyServerUrl;// Netty服务端地址
}
