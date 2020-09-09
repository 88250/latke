<p align = "center">
<img alt="Latke" src="https://b3log.org/images/brand/latke-128.png">
<br><br>
一款以 JSON 为主的 Java Web 框架
<br><br>
<a title="Build Status" target="_blank" href="https://travis-ci.org/88250/latke"><img src="https://img.shields.io/travis/88250/latke.svg?style=flat-square"></a>
<a title="MulanPSL2" target="_blank" href="https://license.coscl.org.cn/MulanPSL2"><img src="http://img.shields.io/badge/license-MulanPSL2-orange.svg?style=flat-square"></a>
<a title="Maven Central" target="_blank" href="https://repo1.maven.org/maven2/org/b3log/latke-parent"><img src="https://img.shields.io/maven-central/v/org.b3log/latke-parent?style=flat-square&color=blueviolet"></a>
<a title="Hits" target="_blank" href="https://github.com/88250/hits"><img src="https://hits.b3log.org/88250/latke.svg"></a>
</p>

<p align="center">
<a href="https://github.com/88250/latke/blob/master/README_en_US.md">English</a>
</p>

## 💡 简介

[Latke](https://github.com/88250/latke)（'lɑ:tkə，土豆饼）是一个简单易用的 Java Web 应用开发框架，包含 MVC、IoC、事件通知、ORM、插件等组件。在实体模型上使用 JSON 贯穿前后端，使应用开发更加快捷。这是 Latke 不同于其他框架的地方，比较适合小型应用的快速开发。

欢迎到 [Latke 官方讨论区](https://ld246.com/tag/latke)了解更多。同时也欢迎关注 B3log 开源社区微信公众号 `B3log开源`：

![b3logos.jpg](https://b3logfile.com/file/2020/08/b3logos-032af045.jpg)

## ✨ 特性

* 函数式路由
* 依赖注入
* MySQL/H2 ORM
* 多语言
* 内存/Redis 缓存
* 事件机制
* 插件机制

## 🗃 案例

* [Demo](https://github.com/88250/latke-demo)：简单的 Latke 应用示例
* [Solo](https://github.com/88250/solo)：一款小而美的 Java 开源博客系统，专为程序员设计
* [Symphony](https://github.com/88250/symphony)：一款用 Java 实现的现代化社区（论坛/BBS/社交网络/博客）平台

## 🛠️ 使用

### Maven

```xml
<dependency>
    <groupId>org.b3log</groupId>
    <artifactId>latke-core</artifactId>
    <version>${latke.version}</version>
</dependency>
```

### 控制器层用法

**函数式路由**

```java
final Dispatcher.RouterGroup routeGroup = Dispatcher.group();
routeGroup.get("/", helloProcessor::index).
        get("/register", registerProcessor::showRegister).
        post("/register", registerProcessor::register).
        get("/var/{pathVar}", registerProcessor::paraPathVar).
        router().get().post().uri("/greeting").handler(helloProcessor::greeting);
```

**JSON 解析**

```java
final JSONObject requestJSON = context.requestJSON();
```

**HTTP 封装**

```java
final String remoteAddr = context.remoteAddr();
final String requestURI = context.requestURI();
final Object att = context.attr("name");
final String method = context.method();
context.sendRedirect("https://b3log.org");
final Request request = context.getRequest();
final Response response = context.getResponse();
```

### 服务层用法

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

### 持久层用法

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
    return getFirst(new Query().setFilter(new PropertyFilter("name", FilterOperator.EQUAL, name)));
}
```

**分页查询**

```java
new Query().setPage(1, 50)
```

**按字段排序**

```java
new Query().addSort("name", SortDirection.DESCENDING);
```

**仅获取需要字段**

```java
new Query().select("name", "age");
```

**原生 SQL**

```java
final List<JSONObject> records = select("SELECT * FROM `user` WHERE `name` = ?", name);
```

## 📜 文档

* [《提问的智慧》精读注解版](https://ld246.com/article/1536377163156)
* [Latke 一款以 JSON 为主的 Java Web 框架](https://ld246.com/article/1574210028252)
* [为什么又要造一个叫 Latke 的轮子](https://ld246.com/article/1403847528022)
* [Latke 快速上手指南](https://ld246.com/article/1466870492857)
* [Latke 配置剖析](https://ld246.com/article/1474087427032)

## 🏘️ 社区

* [讨论区](https://ld246.com/tag/latke)
* [报告问题](https://github.com/88250/latke/issues/new/choose)

## 📄 授权

Latke 使用 [木兰宽松许可证, 第2版](http://license.coscl.org.cn/MulanPSL2) 开源协议。

## 🙏 鸣谢

* [Netty](https://github.com/netty/netty)：事件驱动的异步网络应用框架
* [FreeMarker](https://github.com/apache/freemarker)：使用广泛的 Java 模版引擎
* [Javassist](https://github.com/jboss-javassist/javassist)：Java 字节码处理工具库
* [Apache Commons](http://commons.apache.org)：Java 相关工具库
* [Apache Log4j](https://logging.apache.org/log4j/2.x)：Java 日志库
