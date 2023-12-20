package com.example.demo.controllers;

import com.example.demo.messaging.MessageProducer;
import com.example.demo.models.Clothes;
import com.example.demo.services.ClothesService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Контроллер для работы с мебелью
 */
@Controller
@RequestMapping("/")
public class ClothesController {
    /**
     * Логер для вывода ошибок
     */
    private static final Logger logger = LoggerFactory.getLogger(ClothesController.class);
    /**
     * Сервис для работы с одеждой
     */
    private final ClothesService clothesService;

    private final MessageProducer messageProducer;

    /**
     * Конструктор для внедрения сервиса по работе с одеждой
     *
     * @param clothesService Сервис для работы с одеждой
     */
    @Autowired
    public ClothesController(ClothesService clothesService, MessageProducer messageProducer) {
        this.clothesService = clothesService;
        this.messageProducer = messageProducer;
    }

    /**
     * Показывает список всех предметов одежды
     *
     * @param name  Параметр для фильтрации предметов одежды
     * @param model Модель для передачи данных в представление
     * @return Имя представления для отображения
     */
    @GetMapping("/")
    public String listClothes(@RequestParam(name = "name", required = false) String name, Model model) {
        model.addAttribute("currentPage", "index");
        if (name != null) {
            model.addAttribute("clothes", clothesService.filterByName(name));
        } else {
            model.addAttribute("clothes", clothesService.findAll());
        }
        return "index";
    }

    /**
     * Показывает детали о предмете одежды по его идентификатору
     *
     * @param id    Идентификатор предмета одежды
     * @param model Модель для передачи данных в представление
     * @return Имя представления для отображения
     */
    @GetMapping("clothes/{id}")
    public String viewClothesDetails(@PathVariable("id") int id, Model model) {
        model.addAttribute("currentPage", "details");
        Clothes clothes = clothesService.findOne(id);
        if (clothes != null) {
            model.addAttribute("clothes", clothes);
            return "show";
        } else {
            logger.error("Clothes with id {} not found", id);
            model.addAttribute("error", "Clothes not found");
            return "error";
        }
    }

    /**
     * Форма для редактирования предмета одежды
     *
     * @param id    Идентификатор предмета для редактирования
     * @param model Модель для передачи данных в представление
     * @return Имя представления для отображения
     */
    @GetMapping("clothes/{id}/edit")
    public String editClothesForm(@PathVariable("id") int id, Model model) {
        model.addAttribute("currentPage", "edit");
        Clothes clothes = clothesService.findOne(id);
        if (clothes != null) {
            model.addAttribute("clothes", clothes);
            return "edit";
        } else {
            logger.error("Attempted to edit non-existing clothes with id {}", id);
            return "error";
        }
    }

    /**
     * Форма для создания новойго предмета одежды
     *
     * @param model Модель для передачи данных в представление
     * @return Имя представления для отображения
     */
    @GetMapping("/new")
    public String newClothesForm(Model model) {
        model.addAttribute("currentPage", "new");
        model.addAttribute("clothes", new Clothes());
        return "new";
    }

    /**
     * Создает новую запись о предмете одежды
     *
     * @param clothes            Создаваемый предмет одежды
     * @param bindingResult      Результат привязки для обработки ошибок
     * @param model              Модель для передачи данных в представление
     * @param redirectAttributes Атрибуты для передачи данных при перенаправлении
     * @return Перенаправление на список всех предметов
     */
    @PostMapping
    public String createClothes(@ModelAttribute("clothes") @Valid Clothes clothes,
                                BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentPage", "new");
            messageProducer.sendMessage("Ann error with creating a new clothes item");
            return "new";
        }
        clothesService.save(clothes);
        redirectAttributes.addFlashAttribute("message", "Clothes successfully added.");
        messageProducer.sendMessage("Added an object: " + clothes);
        return "redirect:/";
    }

    /**
     * Обновляет существующую запись предмета одежды
     *
     * @param clothes       Предмет одежды для обновления
     * @param bindingResult Результат привязки для обработки ошибок
     * @param id            Идентификатор предмета для обновления
     * @param model         Модель для передачи данных в представление
     * @return Перенаправление на список предметов одежды
     */
    @PutMapping("/{id}")
    public String updateClothes(@ModelAttribute("clothes") @Valid Clothes clothes,
                                BindingResult bindingResult, @PathVariable("id") int id, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentPage", "edit");
            return "edit";
        }
        if (clothesService.doesNotExist(id)) {
            logger.error("Attempted to update non-existing clothes with id {}", id);
            messageProducer.sendMessage("Error when editing an object with id: " + id);
            return "error";
        }
        clothesService.update(id, clothes);
        messageProducer.sendMessage("Updated an object: " + clothes);
        return "redirect:/";
    }

    /**
     * Удаляет запись о предмете одежды
     *
     * @param id Идентификатор предмета одежды для удаления
     * @return Перенаправление на список предметов одежды
     */
    @DeleteMapping("/{id}")
    public String deleteClothes(@PathVariable("id") int id) {
        if (clothesService.doesNotExist(id)) {
            logger.error("Attempted to delete non-existing clothes with id {}", id);
            messageProducer.sendMessage("Error when deleting an object with id:  " + id);
            return "error";
        }
        Clothes clothes = clothesService.findOne(id);
        clothesService.delete(id);
        messageProducer.sendMessage("Deleted an object: " + clothes);
        return "redirect:/";
    }
    @GetMapping("clothes/{id}/buy")
    public String buy(@PathVariable("id") int id) {
        Clothes clothes = clothesService.findOne(id);
        if(clothesService.doesNotExist(id)){
            messageProducer.sendMessage("There was an error purchasing a product with id: " + id);
            return "error";
        }
        else{
            try {
                clothesService.buy(id);
                messageProducer.sendMessage("The product: " + clothes + " was successfully purchased");
            } catch (IllegalArgumentException e) {
                messageProducer.sendMessage("There was an error purchasing a product with id: " + id);
            }
            return "redirect:/";
        }
    }
}