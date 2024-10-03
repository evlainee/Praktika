package com.example.practika.controller;

import com.example.practika.config.CustomUserService;
import com.example.practika.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class RegistrationController {

    @Autowired
    private CustomUserService userService; // Ваш сервис для работы с пользователями

    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        return "registration"; // Имя HTML файла без расширения
    }

    @PostMapping("/registration")
    public String registerUser(@RequestParam String fullname,
                               @RequestParam String email,
                               @RequestParam String username,
                               @RequestParam String password) {
        // Создание нового пользователя
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFullname(fullname);
        newUser.setEmail(email);

        userService.register(newUser); // Вызов метода для регистрации
        return "redirect:/login"; // Перенаправление на страницу входа
    }

    @GetMapping("/login")
    public String login(Model model, @RequestParam(required = false) String error, HttpSession session) {
        model.addAttribute("error", error);
        return "login"; // Возвращает имя представления для страницы входа
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            @RequestParam(required = false) String captchaInput,
                            HttpSession session, Model model) {
        Integer attempts = (Integer) session.getAttribute("attempts");
        Boolean isBlocked = (Boolean) session.getAttribute("blocked");

        if (isBlocked != null && isBlocked) {
            return "redirect:/login?error=blocked"; // Пользователь заблокирован
        }

        if (attempts == null) {
            attempts = 0;
        }

        if (isValidCredentials(username, password)) {
            // Проверка капчи, если это необходимо
            if (attempts >= 2) {
                String generatedCaptcha = (String) session.getAttribute("captcha");
                if (captchaInput == null || !captchaInput.equals(generatedCaptcha)) {
                    attempts++;
                    session.setAttribute("attempts", attempts);
                    return "redirect:/login?error=captcha"; // Неверная капча
                }
            }
            session.removeAttribute("attempts");
            session.removeAttribute("blocked");
            session.removeAttribute("captcha");
            return "redirect:/dashboard"; // Успешный вход
        } else {
            attempts++;
            session.setAttribute("attempts", attempts);

            // Если попыток стало 3, блокируем пользователя
            if (attempts >= 3) {
                session.setAttribute("blocked", true);
                return "redirect:/login?error=blocked"; // Пользователь заблокирован
            }

            return "redirect:/login?error=true"; // Неверные учетные данные
        }
    }

    // Метод для проверки учетных данных (замените на вашу логику)
    private boolean isValidCredentials(String username, String password) {
        // Пример проверки логина и пароля
        return "admin".equals(username) && "password".equals(password);
    }
}

