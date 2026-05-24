# Tatar DanceSpace

Танцевальное сообщество Татарстана. Платформа для поиска танцевальных залов, событий, общения и обмена опытом.

## Функциональность

- 🔐 **Аутентификация и авторизация** (регистрация, вход, OAuth через Яндекс)
- 🏛️ **Танцевальные залы** (просмотр, добавление, редактирование, удаление)
- 🎭 **События** (создание, просмотр, лайки, комментарии, избранное)
- ⭐ **Рейтинг залов** (отзывы и оценки)
- 📰 **Новости танцев** (интеграция с NewsAPI, бесконечный скроллинг)
- 👤 **Профиль пользователя** (статистика, избранное, управление)
- 🛡️ **Админ-панель** (модерация залов и событий)

## 🛠️ Технологии

| Категория | Технологии |
|-----------|------------|
| **Backend** | Spring Boot 3.2, Spring Security, Spring Data JPA |
| **Frontend** | Thymeleaf, HTML5, CSS3, JavaScript (AJAX) |
| **База данных** | PostgreSQL, Liquibase (миграции) |
| **Кэширование** | Redis |
| **Контейнеризация** | Docker, Docker Compose |
| **API** | REST API, OpenAPI (Swagger), NewsAPI |
| **Аутентификация** | OAuth 2.0 (Яндекс), BCrypt |

##  Запуск проекта

### Предварительные требования

- Docker и Docker Compose
- Java 17 (для локального запуска)
- Maven

### Способ 1: Docker Compose (рекомендуемый)

# Склонировать репозиторий
git clone https://github.com/your-username/tatardancespace.git
cd tatardancespace

# Создать файл с переменными окружения
cp .env.example .env
# Отредактировать .env, добавив свои ключи

# Запустить контейнеры
docker-compose up -d --build

# Приложение будет доступно по адресу:
# http://localhost:8080/tatardancespace/


# Переменные окружения (.env)
env
NEWS_API_KEY=your_newsapi_key
CLIENT_ID=your_yandex_oauth_client_id
CLIENT_SECRET=your_yandex_oauth_client_secret