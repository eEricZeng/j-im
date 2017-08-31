## tio-im简介

 tio-im是基于[t-io](http://git.oschina.net/tywo45/t-io)写的**IM**，主要目标降低即时通讯门槛，实现多端不同协议间的消息发送如http、websocket、tcp自定义协议等！并可以通过http协议的api接口进行消息发送无需关心接收端属于什么协议，一个消息格式搞定一切！

## 消息格式
```
{
    "from": "来源ID",
    "to": "目标ID",
    "createTime": 消息创建时间Long类型,
    "msgType": "消息类型",
    "content": "内容"
}
```
## 性能
性能不用说，去参考[t-io性能指标](http://git.oschina.net/tywo45/t-io#%E6%9E%81%E9%9C%87%E6%92%BC%E7%9A%84%E6%80%A7%E8%83%BD)，相关版本第一时间与tio保持同步更新！

## 更多
更多相关信息持续关注这家伙：**[t-io不仅仅百万并发框架](http://git.oschina.net/tywo45/t-io)** 


## tio-im一些截图
![输入图片说明](https://git.oschina.net/uploads/images/2017/0830/190038_eb44e170_410355.jpeg "tio-im-1.jpg")
![输入图片说明](https://git.oschina.net/uploads/images/2017/0830/190054_a128b214_410355.jpeg "tio-im-2.jpg")
![输入图片说明](https://git.oschina.net/uploads/images/2017/0830/190428_474270ae_410355.jpeg "tio-im-3.jpg")

## 说明
tio-im会有一个成长过程，一如t-io的发展历程，会从丑小鸭变成天鹅的，希望大家多提意见！

## 鸣谢
[t-io不仅仅百万并发框架](http://git.oschina.net/tywo45/t-io)