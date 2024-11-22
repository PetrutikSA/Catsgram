<div align="center">
  <br />
    <img src="https://raw.githubusercontent.com/PetrutikSA/Catsgram/refs/heads/master/logo.png" alt="Project Banner">
  <br />
<h2>Catsgram</h2>
</div>

### 📋 <a name="table">Содержание</a>

1. [Описание](#Описание)
2. [Технологии](#Технологии)
3. [Запуск проекта](#Запуск_проекта)
4. [Эндпойнты](#Эндпойнты)

### <a name="Описание">Описание</a>

Бэкенд приложение для владельцев котов и кошек, чтобы они могли делиться фотографиями своих питомцев.
Реализовано взаимодействие с пользователями, публикация постов, обмен лайками и комментариями. 
Для хранения этой информации использована база данных PostgreSQL.

### <a name="Технологии">Технологии</a>
- Java
- Spring (Core, Web)
- PostgreSQL
- Docker, Maven
- GSON

### <a name="Запуск_проекта">Запуск проекта</a>
- Склонируйте репозиторий ```git clone https://github.com/PetrutikSA/Catsgram.git```
- Перейдите в директорию проекта ```cd Catsgram```
- Запустите docker-compose: ```docker-compose up -d```. Запустятся контейнер с БД
- Создайте исполняемый jar ```mvn clean package```
- Запустите исполняемый jar ```java -jar target/Catsgram-1.0-SNAPSHOT.jar```
- Проект доступен по ссылке: ```http://localhost:8080/```

### <a name="Эндпойнты">Эндпойнты</a>
####  Пользователи - API для работы с пользователями
- __POST__ _/users_ - Добавление нового пользователя
- __GET__ _/users_ - Получение всех зарегистрированных пользователя
- __GET__ _/users/{id}_ - Получение пользователя по Id
- __PUT__ _/users/{id}_ - Обновление информации о пользователе по Id

####  Посты - API для работы с постами пользователей
- __POST__ _/posts_ - Добавление поста
- __GET__ _/posts?sort{sort}_ - Получение всех постов с указанной сортировкой
- __GET__ _/posts/{id}_ - Обновление информации о поста по Id
- __PUT__ _/posts/{id}_ - Удаление поста по Id
- __GET__ _/posts/search?authorId={authorId}&date={date}_ - поиск поста по автору и дате

####  Изображения - API для работы с изображениями к постам
- __POST__ _/posts/{postId}/images_ - Добавить изображение к посту
- __GET__ _/posts/{postId}/images_ - Получить все изображения к посту
- __GET__ _/images/{imageId}_ - скачать изображение

####  Видео - API для работы с видео к постам
- __POST__ _/posts/{postId}/images_ - Добавить видео к посту
- __GET__ _/images/{imageId}_ - скачать видео
