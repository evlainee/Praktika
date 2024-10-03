package com.example.practika.controller;

import com.example.practika.entity.Request;
import com.example.practika.entity.User;
import com.example.practika.repository.RequestRepository;
import com.example.practika.repository.UserRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/operator")
public class OperatorController {
private final RequestRepository requestRepository;
private final UserRepository userRepository;

    public OperatorController(RequestRepository requestRepository,
                              UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String get(Model model){
        List<Request> all = requestRepository.findAll();
        model.addAttribute("activeRequests", all);
        return "operatorlk";
    }
    @GetMapping("/lk")
    public String getLk(@AuthenticationPrincipal UserDetails user, Model model){
        User user1 = userRepository.findByUsername(user.getUsername()).get();
        boolean showQr = user1.getRoles().contains("ROLE_MANAGER");
        List<Request> all = requestRepository.findAll();
        model.addAttribute("activeRequests", all);
        model.addAttribute("showQr", showQr);

        return "operatorlk";
    }
    @GetMapping("/requests/edit/{id}")
    public String editRequest(@PathVariable Integer id, Model model) {
        // Получить заявку по ID и добавить в модель для редактирования
        Request request = requestRepository.findById(id).get();
        List<User> masters = userRepository.findByRolesContaining("ROLE_MASTER");
        model.addAttribute("request", request);
        model.addAttribute("masters", masters);
        return "editRequestByOperator"; // Вернуть название шаблона редактирования
    }
    @PostMapping("/requests/update")
    public String updateRequest(@RequestParam Integer requestId,
                                @RequestParam String description,
                                @RequestParam String status,
                                @RequestParam int priority,
                                @RequestParam(required = false) Long master,
                                @RequestParam("completionDate")
                                    @DateTimeFormat(pattern = "yyyy-MM-dd")
                                    Date completionDate

    ) {
        // Получить заявку по ID и обновить ее данные
        Request request = requestRepository.findById(requestId).get();
        request.setDescription(description);
        request.setStatus(Request.Status.fromValue(status));
        request.setPriority(priority);
        request.setCompletionDate(completionDate);
        if( master != null || master != 0)
            request.setMaster(userRepository.getById(master));

        requestRepository.save(request); // Сохранить обновленную заявку

        return "redirect:/operator/lk"; // Перенаправление на страницу с заявками
    }

    // Метод для удаления заявки
    @PostMapping("/requests/delete/{id}")
    public String deleteRequest(@PathVariable Integer id) {
        requestRepository.deleteById(id);
        return "redirect:/operator/lk"; // Перенаправление после удаления
    }
}
