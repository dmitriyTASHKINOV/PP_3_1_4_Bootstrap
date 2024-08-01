package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.SupportingService;
import ru.kata.spring.boot_security.demo.service.UserService;
import ru.kata.spring.boot_security.demo.validator.UserValidator;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping("/users")
public class AdminController {
    private final UserService userService;
    private final SupportingService supportingService;
    private final UserValidator userValidator;

    @Autowired
    public AdminController(UserService userService, SupportingService supportingService, UserValidator userValidator) {
        this.userService = userService;
        this.supportingService = supportingService;
        this.userValidator = userValidator;
    }

    @GetMapping("/admin")
    public String showAllUsers(Model model, Principal principal) {
        model.addAttribute("newUser", new User());
        supportingService.createModelForView(model, principal);
        model.addAttribute("activeTab", "usersTable");
        return "adminPage";
    }

    @PostMapping("/admin")
    public String addUser(@ModelAttribute("newUser") User user, BindingResult bindingResult, Principal principal, Model model) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            supportingService.createModelForView(model, principal);
            model.addAttribute("activeTab", "addUser");
            return "adminPage";
        }

        userService.add(user);
        return "redirect:/users/admin";
    }

    @PatchMapping("/admin")
    public String updateUser(@ModelAttribute("userIter") @Valid User user, BindingResult bindingResult, Model model, Principal principal) {
        model.addAttribute("authUser", userService.findByUsername(principal.getName()));
        if (bindingResult.hasErrors()) return "adminPage";

        userService.update(user);
        return "redirect:/users/admin";
    }

    @DeleteMapping("/admin")
    public String deleteUser(Model model, @RequestParam("id") Long id) {
        model.addAttribute("user", userService.findById(id));
        userService.deleteById(id);
        return "redirect:/users/admin";
    }
}
