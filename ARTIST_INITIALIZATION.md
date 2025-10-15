# Artist Initialization Implementation

## Обзор

Реализована функциональность для парсинга и сохранения художников (Artists) из страницы Numista в базу данных Neo4j.

## Компоненты

### 1. Entity: Artist
**Файл:** `src/main/java/bkv/colligendis/database/entity/numista/Artist.java`

Сущность Artist содержит:
- `nid` - уникальный идентификатор художника из Numista
- `name` - имя художника

### 2. Repository: ArtistRepository
**Файл:** `src/main/java/bkv/colligendis/database/service/numista/ArtistRepository.java`

Репозиторий с методом:
- `findByNid(String nid)` - поиск художника по Numista ID

### 3. Service: ArtistService
**Файл:** `src/main/java/bkv/colligendis/database/service/numista/ArtistService.java`

Сервис с методами:
- `initArtists()` - инициализация всех художников
- `findUuidByNid(String nid)` - поиск UUID по Numista ID
- `findByNid(String nid)` - поиск художника по Numista ID

### 4. Parser: NumistaAllArtistsParser
**Файл:** `src/main/java/bkv/colligendis/utils/numista/artist/NumistaAllArtistsParser.java`

Парсер для страницы художников Numista:
- Загружает страницу `https://en.numista.com/catalogue/artists.php`
- Извлекает все ссылки на художников в формате `/catalogue/artist.php?id=XXX`
- Создает или обновляет записи в базе данных

### 5. REST Controller: ArtistRestController
**Файл:** `src/main/java/bkv/colligendis/rest/ArtistRestController.java`

REST API endpoints:
- `GET /database/artist/init` - инициализация художников из Numista
- `GET /database/artist/all` - получение всех художников из базы

### 6. Integration: NumistaServices
**Файл:** `src/main/java/bkv/colligendis/services/NumistaServices.java`

Добавлен `ArtistService` в общий сервис Numista для централизованного доступа.

## Использование

### Через REST API

1. **Инициализация художников:**
```http
GET http://localhost:8080/database/artist/init
```

2. **Получение всех художников:**
```http
GET http://localhost:8080/database/artist/all
```

### Через код

```java
// Через ArtistService
artistService.initArtists();

// Через N4JUtil
N4JUtil.getInstance().numistaService.artistService.initArtists();
```

## Тестирование

HTTP запросы для тестирования находятся в файле:
**`artist_requests.http`**

## Структура данных

Каждый художник сохраняется в Neo4j как узел с типом `ARTIST` и содержит:
- `uuid` - уникальный идентификатор (UUID)
- `nid` - идентификатор Numista (например, "487", "421")
- `name` - имя художника (например, "A. Romanescu", "Aaron Baggio")

## Логика работы

1. Парсер загружает страницу художников с Numista
2. Извлекает все ссылки на художников используя CSS селектор `a[href^=/catalogue/artist.php]`
3. Для каждого художника:
   - Извлекается `nid` из URL
   - Извлекается `name` из текста ссылки
   - Проверяется существование в базе данных
   - Если художник новый - создается запись
   - Если существует - обновляется имя (если изменилось)
4. Все изменения сохраняются в Neo4j

## Особенности реализации

- Использует JSoup для парсинга HTML
- Применяет cookies и User-Agent для обхода ограничений Numista
- Логирует процесс парсинга через DebugUtil
- Обрабатывает ошибки и выводит информативные сообщения
- Избегает дублирования записей в базе данных

## Зависимости

- Spring Framework (DI, REST)
- Neo4j (база данных)
- JSoup (парсинг HTML)
- Lombok (генерация кода)

