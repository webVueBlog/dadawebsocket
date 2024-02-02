# 基于Netty和Kafka集群的websocket消息推送服务器

### Netty
请求的netty服务器地址，默认是我写的负载均衡算法。

### 客户端如何鉴权？
客户端ID和Netty管道进行绑定，需要发送的内容：da_sms_channel@#@1

可以使用在线工具：http://www.easyswoole.com/wstool.html

ws://localhost:8001/notice

ws://localhost:8002/notice

ws://localhost:8003/notice

### 客户端如何发送心跳包
具体内容：heart_beat
见：`constant.com.da.common.MyConstant.HEART_BEAT`

### 目前自定义的Kafka的topic
1. da_sms_topic
2. da_trash_topic
3. da_global_topic

### 目前自定义的Netty的channel
1. da_sms_channel
2. da_trash_channel
3. da_global_channel

### 发送通知数据至Kafka
访问：http://localhost:9000/api/v1/kafka/pushMsgToMq
请求体：
```json
{
	"noticeLabel": "测试推送消息1",
	"publisher": "lzhpo",
	"receiverChannelIds": ["1", "2"],
	"channelGroup": "da_sms_topic",
	"message": "我是测试推送消息内容1",
	"goToUrl": "https://www.lzhpo.com"
}
```
消息属性说明见`entity.com.da.common.NoticePackage`

因为Id为1的客户端绑定了Netty的`da_sms_topic`管道，其它客户端没有绑定`da_sms_topic`管道，所以只有这个客户端收得到消息。

