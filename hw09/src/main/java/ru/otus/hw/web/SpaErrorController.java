package ru.otus.hw.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для поддержки SPA-роутинга (React Router) при раздаче фронта из Spring Boot (В PROD РЕЖИМЕ).
 * <p>
 * Назначение:
 * - Позволяет открывать клиентские маршруты напрямую (например /books, /authors/1),
 *   когда React-приложение лежит в static/ и используется BrowserRouter.
 * <p>
 * Как работает:
 * - Перехватывает ошибки 404.
 * - Если запрос:
 *   - не относится к API (/api/**),
 *   - не является статическим ресурсом (/assets/**),
 *   - и не запрашивает файл (без точки в URL),
 *   то выполняется forward на index.html.
 * <p>
 * В результате:
 * - React загружается,
 * - а дальнейший роутинг обрабатывается на клиенте.
 */

@Controller
public class SpaErrorController implements ErrorController {

    @RequestMapping("/error")
    public String error(HttpServletRequest request) {
        Integer status = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String uri = (String) request.getAttribute("jakarta.servlet.error.request_uri");

        if (status == null || uri == null) {
            return "error";
        }

        if (status != 404) {
            return "error";
        }

        if (uri.startsWith("/api/") || uri.startsWith("/assets/") || uri.contains(".")) {
            return "error";
        }

        return "forward:/index.html";
    }
}