## tio-im简介

 tio-im是基于[t-io](http://git.oschina.net/tywo45/t-io)写的**IM**，主要目标降低即时通讯门槛，通过极简洁的消息格式就可以实现多端不同协议间的消息发送如(http、websocket、tcp自定义im协议)等，并可以通过http协议的api接口进行消息发送无需关心接收端属于什么协议，一个消息格式搞定一切！

## 消息格式
```
###  **1.聊天请求消息结构**
 
{
    "from": "来源ID",
    "to": "目标ID",
    "cmd":命令码(11),
    "createTime": 消息创建时间Long类型,
    "msgType": "消息类型(text、image、vedio、news)",
    "content": "内容"
}

说明:请求:COMMAND_CHAT_REQ(1) 响应:COMMAND_CHAT_RESP(2)

 **2.鉴权请求消息结构** 
{
    "cmd":命令码(3),
    "token": "校验码"
}

说明:请求:COMMAND_AUTH_REQ(3) 响应:COMMAND_AUTH_RESP(4)

 **3.握手请求消息结构** 
{
    "cmd":命令码(1),
    "hbyte":"握手1个字节"
}

说明:请求:COMMAND_HANDSHAKE_REQ(1) 响应:COMMAND_HANDSHAKE_RESP(2)

 **4.登录请求消息结构** 
{
    "cmd":命令码(5),
    "loginname": "用户名",
    "password": "密码",
    "token": "校验码(此字段可与logingname、password共存,也可只选一种方式)"
}

说明:请求:COMMAND_LOGIN_REQ(5) 响应:COMMAND_LOGIN_RESP(6)

 **5.心跳请求消息结构** 
{
    "cmd":命令码(13),
    "hbbyte":"心跳1个字节"
}

说明:请求:COMMAND_HEARTBEAT_REQ(13) 响应:无

 **6.关闭、退出请求消息结构** 
{
    "cmd":命令码(14)
}

说明:请求:COMMAND_CLOSE_REQ(14) 响应:无

 **7.获取用户信息请求消息结构** 
{
     "cmd":命令码(17),
     "id":"用户id(只在type为0或是无的时候需要)",
     "type":"获取类型(0:指定用户,1:所有在线用户,2:所有用户[在线+离线])
}

说明:请求:COMMAND_GET_USER_REQ(17) 响应:COMMAND_GET_USER_RESP(18)


```
## 性能
性能不用说，去参考[t-io性能指标](http://git.oschina.net/tywo45/t-io#%E6%9E%81%E9%9C%87%E6%92%BC%E7%9A%84%E6%80%A7%E8%83%BD)，相关版本第一时间与tio保持同步更新！

## 更多
更多相关信息持续关注这家伙：**[t-io不仅仅百万并发框架](http://git.oschina.net/tywo45/t-io)** 


## tio-im一些截图
![输入图片说明](https://git.oschina.net/uploads/images/2017/0920/154315_4882a2cc_410355.jpeg "tio-im-1.jpg")
![输入图片说明](https://git.oschina.net/uploads/images/2017/0830/190054_a128b214_410355.jpeg "tio-im-2.jpg")
![输入图片说明](https://git.oschina.net/uploads/images/2017/0830/190428_474270ae_410355.jpeg "tio-im-3.jpg")

## 说明
tio-im会有一个成长过程，一如t-io的发展历程，会从丑小鸭变成天鹅的，希望大家多提意见！

## 鸣谢
[t-io不仅仅百万并发框架](http://git.oschina.net/tywo45/t-io)