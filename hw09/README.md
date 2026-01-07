# otus-library-catalog (hw09)

Проект: Spring Boot (REST API) + React (Vite).

В проекте используется **2 Maven-профиля**: `dev` и `prod`.
Это **именно Maven profiles** (ключ `-P`), а не Spring `spring.profiles.active`.

---

## Предварительные требования

- Java 17
- Maven 3.x
- Node.js + npm (для фронта)
- MongoDB (локально на `localhost:27017`)

---

## Как устроено

### Backend (hw09)
- REST API доступен по префиксу: `http://localhost:8080/api/...`
   - пример: `http://localhost:8080/api/books`

### Frontend (React + Vite)
- В **dev** режиме фронт поднимается отдельным Vite dev-server (с HMR).
- В **prod** режиме фронт **собирается** и кладётся в `hw09/target/classes/static`,
  после чего Spring Boot начинает раздавать UI сам (на том же `8080`).

---

## Запуск в DEV (Vite dev-server + Backend)

### Что запускается
- Backend: `http://localhost:8080`
- Frontend: `http://localhost:3000` *(или другой порт, см. `vite.config.ts`)*

Открывать в браузере нужно **фронт**, т.е.:
- `http://localhost:3000/`
- `http://localhost:3000/books`

API при этом будет ходить на backend через proxy Vite:
- `http://localhost:8080/api/...` (вручную открывать можно, но это будет JSON)

### Как запустить через IntelliJ IDEA (без команд)
1. Открой справа окно **Maven**.
2. Выбери проект/модуль `hw09`.
3. Открой раздел **Profiles** (значок/панель Profiles в Maven окне).
4. Поставь галочку **`dev`**.
5. Запусти backend через Maven:
   - `Plugins → spring-boot → spring-boot:run`

После этого открой в браузере: `http://localhost:3000/`.

---

## Запуск в PROD (UI раздаёт Spring Boot с 8080)

### Что запускается
- Один сервер на `http://localhost:8080`
- UI открывается **на backend-порту**:
   - `http://localhost:8080/`
   - `http://localhost:8080/books`

API:
- `http://localhost:8080/api/...` (JSON)

### Важно: настройка Vite для prod
В `vite.config.ts` фронта должен быть настроен вывод сборки в classpath backend:

```ts
build: {
  outDir: "/Users/ahraavidzba/Desktop/java/otus-library-catalog/hw09/target/classes/static",
  emptyOutDir: true,
}