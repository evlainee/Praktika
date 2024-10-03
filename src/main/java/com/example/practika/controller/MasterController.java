package com.example.practika.controller;

import com.example.practika.entity.Parts;
import com.example.practika.entity.Reports;
import com.example.practika.entity.Request;
import com.example.practika.repository.ReportRepository;
import com.example.practika.repository.RequestRepository;
import com.example.practika.repository.PartRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/master")
public class MasterController {

    private final RequestRepository requestRepository;
    private final PartRepository partRepository;
    private final ReportRepository reportRepository;

    public MasterController(RequestRepository requestRepository, PartRepository partRepository,
                            ReportRepository reportRepository) {
        this.requestRepository = requestRepository;
        this.partRepository = partRepository;
        this.reportRepository = reportRepository;
    }

    @GetMapping("/lk")
    public String getLk(Model model) {
        List<Request> allRequests = requestRepository.findAll();
        List<Reports> reports=reportRepository.findAll();
        model.addAttribute("activeRequests", allRequests);

        model.addAttribute("reports", reports);
        return "masterlk";
    }

    // Метод для редактирования заявки
    @GetMapping("/requests/edit/{id}")
    public String editRequest(@PathVariable Integer id, Model model) {
        Request request = requestRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid request ID: " + id));
        List<Parts> allParts = partRepository.findAll(); // Получение всех запчастей для выбора
        model.addAttribute("request", request);
        model.addAttribute("parts", allParts); // Передаем список запчастей в шаблон
        return "editRequestByMaster";
    }

    // Метод для обновления заявки
    @PostMapping("/requests/update")
    public String updateRequest(
            @RequestParam Integer requestId,
            @RequestParam String status,
            @RequestParam Integer partId,  // Получаем ID запчасти из формы
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date completionDate) {

        Request request = requestRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Invalid request ID: " + requestId));
        Parts part = partRepository.findById(partId).orElseThrow(() -> new IllegalArgumentException("Invalid part ID: " + partId));

        // Обновляем данные заявки
        request.setStatus(Request.Status.fromValue(status));
        request.getParts().add(part); // Связываем с выбранной запчастью
        request.setCompletionDate(completionDate);
        part.setRequest(request);
        partRepository.save(part);
        requestRepository.save(request);// Сохраняем изменения

        if(request.getStatus().equals(Request.Status.Completed)){

            Reports reports = new Reports();
            reports.setRequest(request);
            long diffInMillis = request.getCompletionDate().getTime() - request.getCreationDate().getTime();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24); // Перевод миллисекунд в дни
            reports.setTotalTime((int) diffInDays);
            String partsStr = "";
            BigDecimal costs = BigDecimal.ZERO;
            for(Parts part2:request.getParts()){
                partsStr+=part2.getName()+" ";
                costs.add(part2.getPrice());
            }
            reports.setCost(costs);
            reports.setUsedParts(partsStr);
            reportRepository.save(reports);

        }

        return "redirect:/master/lk"; // Возвращаемся к списку заявок
    }

    // Метод для удаления заявки
    @PostMapping("/requests/delete/{id}")
    public String deleteRequest(@PathVariable Integer id) {
        requestRepository.deleteById(id);
        return "redirect:/master/lk"; // Перенаправление после удаления
    }

    // Метод для заказа запчасти
    @PostMapping("/orderPart")
    public String orderPart(@RequestParam Integer requestId, @RequestParam String name,@RequestParam Integer price, @RequestParam Integer quantity ) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Invalid request ID: " + requestId));
        Parts part = new Parts();
        part.setName(name);
        part.setPrice(BigDecimal.valueOf(price));
        part.setQuantity(quantity);

        // Логика заказа запчасти (например, обновление статуса или инвентаря)
        // Пример: часть добавляется к заявке
        request.getParts().add(part);
        part.setRequest(request);
        partRepository.save(part);
        requestRepository.save(request); // Сохраняем изменения

        return "redirect:/master/lk"; // Возвращаемся к списку заявок
    }
}

