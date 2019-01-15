Latke [![Build Status](https://travis-ci.org/b3log/latke.png?branch=master)](https://travis-ci.org/b3log/latke) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.b3log/latke-parent/badge.svg)](http://repo1.maven.org/maven2/org/b3log/latke-parent)
----

## 简介

[Latke](https://github.com/b3log/latke)（'lɑ:tkə，土豆饼）是一个简单易用的 Java Web 应用开发框架，包含 MVC、IoC、事件通知、ORM、插件等组件。

在实体模型上使用 JSON 贯穿前后端，使应用开发更加快捷。这是 Latke 不同于其他框架的地方，非常适合小型应用的快速开发。

## 特性

* 注解式、函数式路由
* 依赖注入
* 多种数据库 ORM
* 多语言
* 内存/Redis 缓存
* 事件机制
* 插件机制

## 案例

* [Demo](https://github.com/b3log/latke-demo)：简单的 Latke 应用示例
* [Solo](https://github.com/b3log/solo)：一款小而美的 Java 博客系统
* [Symphony](https://github.com/b3log/symphony)：一款用 Java 实现的现代化社区（论坛/BBS/社交网络/博客）平台

## 安装

```xml
<dependency>
    <groupId>org.b3log</groupId>
    <artifactId>latke-core</artifactId>
    <version>${latke.version}</version>
</dependency>
```

## 控制器层用法

**注解声明式路由**

```java
@RequestProcessing("/")
public void index(final RequestContext context) {
    context.setRenderer(new SimpleFMRenderer("index.ftl"));
    final Map<String, Object> dataModel = context.getRenderer().getRenderDataModel();
    dataModel.put("greeting", "Hello, Latke!");
}
```

**函数式路由**

```java
DispatcherServlet.post("/register", registerProcessor::register);
DispatcherServlet.mapping();
```

**路径变量和查询字符串**

```java
@RequestProcessing("/var/{pathVar}")
public void paraPathVar(final RequestContext context) {
    final String paraVar = context.param("paraVar");
    final String pathVar = context.pathVar("pathVar");
    context.renderJSON(new JSONObject().put("paraVar", paraVar).put("pathVar", pathVar));
}
```

**JSON 解析**

```java
final JSONObject requestJSON = context.requestJSON();
```

**Servlet 封装**

```java
final String remoteAddr = context.remoteAddr();
final String requestURI = context.requestURI();
final Object att = context.attr("name");
final String method = context.method();
context.sendRedirect("https://b3log.org");
final HttpServletRequest request = context.getRequest();
final HttpServletResponse response = context.getResponse();
```

## 服务层用法

**依赖注入、事务**

```java
@Service
public class UserService {

    private static final Logger LOGGER = Logger.getLogger(UserService.class);

    @Inject
    private UserRepository userRepository;

    @Transactional
    public void saveUser(final String name, final int age) {
        final JSONObject user = new JSONObject();
        user.put("name", name);
        user.put("age", age);

        String userId;

        try {
            userId = userRepository.add(user);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Saves user failed", e);

            // 抛出异常后框架将回滚事务
            throw new IllegalStateException("Saves user failed");
        }

        LOGGER.log(Level.INFO, "Saves a user successfully [userId={0}]", userId);
    }
}
```

## 持久层用法

**构造 ORM**

```java
@Repository
public class UserRepository extends AbstractRepository {

    public UserRepository() {
        super("user");
    }
}
```

**单表 CRUD**

```java
public interface Repository {
    String add(final JSONObject jsonObject) throws RepositoryException;
    void update(final String id, final JSONObject jsonObject) throws RepositoryException;
    void remove(final String id) throws RepositoryException;
    void remove(final Query query) throws RepositoryException;
    JSONObject get(final String id) throws RepositoryException;
    long count(final Query query) throws RepositoryException;
}
```

**条件查询**

```java
public JSONObject getByName(final String name) throws RepositoryException {
    final List<JSONObject> records = getList(new Query().
            setFilter(new PropertyFilter("name", FilterOperator.EQUAL, name)));
    if (records.isEmpty()) {
        return null;
    }

    return records.get(0);
}
```

**分页查询**

```java
new Query().setCurrentPageNum(1).setPageSize(50)
```

**按字段排序**

```java
new Query().addSort("name", SortDirection.DESCENDING);
```

**仅获取需要字段**

```java
new Query().addProjection("name", String.class);
```

**原生 SQL**

```java
final List<JSONObject> records = select("SELECT * FROM `user` WHERE `name` = ?", name);
```

## 文档

* [《提问的智慧》精读注解版](https://hacpai.com/article/1536377163156)
* [为什么又要造一个叫 Latke 的轮子](https://hacpai.com/article/1403847528022)
* [Latke 快速上手指南](https://hacpai.com/article/1466870492857)
* [Latke 配置剖析](https://hacpai.com/article/1474087427032)
* [Latke 贡献指南](https://github.com/b3log/latke/blob/master/CONTRIBUTING.md)

## 社区

* [讨论区](https://hacpai.com/tag/latke)
* [报告问题](https://github.com/b3log/latke/issues/new/choose)

## 鸣谢

Latke 的诞生离不开以下开源项目：

* [FreeMarker](https://github.com/apache/freemarker)：使用广泛的 Java 模版引擎
* [Commons Lang](https://github.com/apache/commons-lang)：Java 语言相关工具库
* [Commons IO](https://github.com/apache/commons-io)：Java IO 相关工具库
* [Commons Codec](https://github.com/apache/commons-codec)：Java 编解码库
* [Javassist](https://github.com/jboss-javassist/javassist)：Java 字节码处理工具库
* [SLF4j](https://github.com/qos-ch/slf4j)：Java 日志门户
* [Gin](https://github.com/gin-gonic/gin)：又快又好用的 golang HTTP web 框架