<p align = "center">
<img alt="Latke" src="https://b3log.org/images/brand/latke-128.png">
<br><br>
A Java Web framework based on JSON
<br><br>
<a title="Build Status" target="_blank" href="https://travis-ci.org/88250/latke"><img src="https://img.shields.io/travis/88250/latke.svg?style=flat-square"></a>
<a title="MulanPSL2" target="_blank" href="https://license.coscl.org.cn/MulanPSL2"><img src="http://img.shields.io/badge/license-MulanPSL2-orange.svg?style=flat-square"></a>
<a title="Maven Central" target="_blank" href="https://repo1.maven.org/maven2/org/b3log/latke-parent"><img src="https://img.shields.io/maven-central/v/org.b3log/latke-parent?style=flat-square&color=blueviolet"></a>
<a title="Hits" target="_blank" href="https://github.com/88250/hits"><img src="https://hits.b3log.org/88250/latke.svg"></a>
</p>

<p align="center">
<a href="https://github.com/88250/latke/blob/master/README.md">中文</a>
</p>

## 💡 Introduction

[Latke](https://github.com/88250/latke) ('lɑ:tkə, potato cake) is a simple and easy-to-use Java Web application development framework, including MVC, IoC, event notification, ORM, plugins and other components. The use of JSON on the entity model runs through the front and back ends, making application development faster. This is where Latke is different from other frameworks, and is more suitable for the rapid development of small applications.

Welcome to [Latke Official Discussion Forum](https://ld246.com/tag/latke) to learn more.

## ✨ Features

* Functional routing
* Dependency injection
* MySQL/H2 ORM
* Multi-language
* Memory / Redis cache
* Event mechanism
* Plug-in mechanism

## 🗃 Showcases

* [Demo](https://github.com/88250/latke-demo): Simple Latke application example
* [Solo](https://github.com/88250/solo): A small and beautiful Java open source blog system, designed for programmers
* [Symphony](https://github.com/88250/symphony): A modern community (forum/BBS/SNS/blog) platform implemented in Java

## 🛠️ Usages

### Maven

```xml
<dependency>
    <groupId>org.b3log</groupId>
    <artifactId>latke-core</artifactId>
    <version>${latke.version}</version>
</dependency>
```

### Controller layer usage

**Functional routing**

```java
final Dispatcher.RouterGroup routeGroup = Dispatcher.group();
routeGroup.get("/", helloProcessor::index).
        get("/register", registerProcessor::showRegister).
        post("/register", registerProcessor::register).
        get("/var/{pathVar}", registerProcessor::paraPathVar).
        router().get().post().uri("/greeting").handler(helloProcessor::greeting);
```

**JSON parsing**

```java
final JSONObject requestJSON = context.requestJSON();
```

**HTTP encapsulation**

```java
final String remoteAddr = context.remoteAddr();
final String requestURI = context.requestURI();
final Object att = context.attr("name");
final String method = context.method();
context.sendRedirect("https://b3log.org");
final Request request = context.getRequest();
final Response response = context.getResponse();
```

### Service layer usage

**Dependency injection, transaction**

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

            // The framework will roll back the transaction after throwing an exception
            throw new IllegalStateException("Saves user failed");
        }

        LOGGER.log(Level.INFO, "Saves a user successfully [userId={0}]", userId);
    }
}
```

### Persistence layer usage

**Construct repository**

```java
@Repository
public class UserRepository extends AbstractRepository {

    public UserRepository() {
        super("user");
    }
}
```

**Single table CRUD**

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

**Conditional query**

```java
public JSONObject getByName(final String name) throws RepositoryException {
    return getFirst(new Query().setFilter(new PropertyFilter("name", FilterOperator.EQUAL, name)));
}
```

**Paging query**

```java
new Query().setPage(1, 50)
```

**Sort by field**

```java
new Query().addSort("name", SortDirection.DESCENDING);
```

**Get only required fields**

```java
new Query().select("name", "age");
```

**Native SQL**

```java
final List<JSONObject> records = select("SELECT * FROM `user` WHERE `name` = ?", name);
```

## 📜 Documentation

* [Latke is a Java web framework based on JSON](https://ld246.com/article/1574210028252)
* [Why make another wheel called Latke](https://ld246.com/article/1403847528022)
* [Latke Quick Start Guide](https://ld246.com/article/1466870492857)
* [Anatomy of a Latke configuration](https://ld246.com/article/1474087427032)

## 🏘️ Community

* [Forum](https://ld246.com/tag/latke)
* [Issues](https://github.com/88250/latke/issues/new/choose)

## 📄 License

Latke uses the [Mulan Permissive Software License, Version 2](http://license.coscl.org.cn/MulanPSL2) open source license.

## 🙏 Acknowledgement

* [Netty](https://github.com/netty/netty): An event-driven asynchronous network application framework
* [FreeMarker](https://github.com/apache/freemarker): A widely used Java template engine
* [Javassist](https://github.com/jboss-javassist/javassist): Java bytecode processing tool library
* [Apache Commons](http://commons.apache.org): Java related tool library
* [Apache Log4j](https://logging.apache.org/log4j/2.x): Java logging library
