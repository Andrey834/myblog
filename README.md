# MyBlog

## Technologies used

* Frontend – Thymeleaf.
* Backend  – Java 16, Spring Framework 6.
* Database – Postgresql.
* Test     - JUnit 5, TestContainers(Postgresql)

## Installation guide

Set environments in application.properties
* URL DATABASE -> spring.datasource.url
* USERNAME DATABASE -> spring.datasource.username
* PASSWORD DATABASE -> spring.datasource.password
* DIR FOR IMAGE -> app.image.dir

Install Java 21 and maven and run:

```bash
mvn package
cd target
cp myblog.war ${TOMCAT_HOME}/libexec/webapps/
```

### Key Features
* **Post Creation** - Easily create new blog posts with rich text editing, image uploads, and formatting options
* **Content Management** - Organize your posts, edit them anytime, and delete when necessary
* **Engagement Tools** - Allow readers to like your posts and engage with your content
* **Media Integration** - Add images to enhance your posts
