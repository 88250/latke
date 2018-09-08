Latke [![Build Status](https://travis-ci.org/b3log/latke.png?branch=master)](https://travis-ci.org/b3log/latke) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.b3log/latke/badge.svg)](http://repo1.maven.org/maven2/org/b3log/latke)
----

## 简介

[Latke](https://github.com/b3log/latke)（'lɑ:tkə，土豆饼）是一个简单易用的 Java Web 应用开发框架，包含 MVC、IoC/AOP、事件通知、ORM、插件等组件。

在实体模型上使用 JSON 贯穿前后端，使应用开发更加快捷。这是 Latke 不同于其他框架的地方，非常适合小型应用的快速开发。

## 特性

* 基于注解的 MVC
* IoC、AOP
* 支持多种数据库 ORM
* 内存/Redis 缓存
* 定时任务
* 事件机制
* Javamail 客户端
* 多语言支持
* 插件机制

## 案例

* [Demos](https://github.com/b3log/latke-demo)：简单的 Latke 应用示例
* [Solo](https://github.com/b3log/solo)：一个用 Java 实现的博客系统，为你或你的团队创建个博客吧！
* [Symphony](https://github.com/b3log/symphony)：一个用 Java 实现的现代化社区（论坛/BBS/社交网络/博客）平台。

## 安装

Latke 每个版本都会发布到 Maven 中央库，可在 pom.xml 中直接引用：

```xml
<dependency>
    <groupId>org.b3log</groupId>
    <artifactId>latke</artifactId>
    <version>${latke.version}</version>
</dependency>
```

## 文档

* [《提问的智慧》精读注解版](https://hacpai.com/article/1536377163156)
* [为什么又要造一个叫 Latke 的轮子](https://hacpai.com/article/1403847528022)
* [Latke 快速上手指南](https://hacpai.com/article/1466870492857)
* [Latke 配置剖析](https://hacpai.com/article/1474087427032)
* [Latke 源码解析（一）Servlet 部分](https://hacpai.com/article/1493267456529)
* [Latke 源码解析（二）IOC 部分](https://hacpai.com/article/1493620909167)

## 社区

* [讨论区](https://hacpai.com/tag/latke)
* [报告问题](https://github.com/b3log/latke/issues/new/choose)
* [Q 群 17370164](https://shang.qq.com/wpa/qunwpa?idkey=0bb80e452d38d5113c7bb2534cefc8155589d0bdce81c7d2e6d91930bcb6e2d7)

## 鸣谢

Latke 的诞生离不开以下开源项目：

* [FreeMarker](https://github.com/apache/freemarker)：使用广泛的 Java 模版引擎
* [Commons Lang](https://github.com/apache/commons-lang)：Java 语言相关工具库
* [Commons IO](https://github.com/apache/commons-io)：Java IO 相关工具库
* [Commons Codec](https://github.com/apache/commons-codec)：Java 编解码库
* [Javassist](https://github.com/jboss-javassist/javassist)：Java 字节码处理工具库
* [SLF4j](https://github.com/qos-ch/slf4j)：Java 日志门户