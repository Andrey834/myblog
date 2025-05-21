# MyBlog

## Technologies used

* Frontend – Thymeleaf.
* Backend – Java 21, Spring Boot 3.4.5
* Database – Postgresql, Liquibase.
* Test - JUnit 5, TestContainers(Postgresql)

## Installation guide

Set environments

* APP_BLOG_NAME -> Application name
* APP_BLOG_PORT -> Application port
* APP_BLOG_PG_USERNAME -> Postgresql username
* APP_BLOG_PG_PASSWORD -> Postgresql password
* APP_BLOG_PG_PORT -> Postgresql external port

## Run application

Install Docker with Compose:

```bash
docker compose up -d --build
```

### Key Features

* **Post Creation** - Easily create new blog posts with rich text editing, image uploads, and formatting options
* **Content Management** - Organize your posts, edit them anytime, and delete when necessary
* **Engagement Tools** - Allow readers to like your posts and engage with your content
* **Media Integration** - Add images to enhance your posts
