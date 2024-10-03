package com.example.practika.controller;

import com.example.practika.entity.Request;
import com.example.practika.entity.User;
import com.example.practika.repository.RequestRepository;
import com.example.practika.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    public UserController(UserRepository userRepository,
                          RequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
    }
    @GetMapping()
    public String get(){
        return "usermainpage";
    }
    @GetMapping("/repair-form")
    public String getrepairform(){
        return "userzayavka";
    }
    @GetMapping("/lk")
    public String getlk(@AuthenticationPrincipal UserDetails user, Model model){
        User user1 = userRepository.findByUsername(user.getUsername()).get();

        List<Request> all = requestRepository.findByClient_Id(user1.getId());

        List<Request> active = all.stream().filter(x->{
            return x.getStatus() != Request.Status.Completed;
        }).collect(Collectors.toList());

        model.addAttribute("activeRequests", active);
        model.addAttribute("myRequests", all);
        return "userlk";
    }
    @PostMapping("/submit-repair")
    public String submitRepair(
            @RequestParam("fio") String fio,
            @RequestParam("phone") String phone,
            @RequestParam("deviceType") String deviceType,
            @RequestParam("deviceModel") String deviceModel,
            @RequestParam("problemDescription") String problemDescription,
            @AuthenticationPrincipal UserDetails user
    ) {

                User user1 = userRepository.findByUsername(user.getUsername()).get();


                Request request = new Request();
                request.setClient(user1);
                request.setCreationDate(new Date());
                request.setPriority(1);
                request.setStatus(Request.Status.New);
                request.setDescription(fio+"\n"+phone+"\n"+deviceModel+"\n"+deviceType+"\n"+problemDescription);
                requestRepository.save(request);



        return "redirect:/user/lk";
    }
    @GetMapping("/requests/edit/{id}")
    public String editRequest(@PathVariable Integer id, Model model) {
        // Получить заявку по ID и добавить в модель для редактирования
        Request request = requestRepository.findById(id).get();
        model.addAttribute("request", request);
        return "editRequest"; // Вернуть название шаблона редактирования
    }
    @PostMapping("/requests/update")
    public String updateRequest(@RequestParam Integer requestId,
                                @RequestParam String description
                                ) {
        // Получить заявку по ID и обновить ее данные
        Request request = requestRepository.findById(requestId).get();
        request.setDescription(description);

        requestRepository.save(request);

        return "redirect:/user/lk";
    }


    @PostMapping("/requests/delete/{id}")
    public String deleteRequest(@PathVariable Integer id) {
        requestRepository.deleteById(id);
        return "redirect:/user/lk";
    }
}
