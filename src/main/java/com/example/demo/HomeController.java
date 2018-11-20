package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.jws.WebParam;
import javax.validation.Valid;
import java.security.Principal;

@Controller
public class HomeController {

  @Autowired
  MessageRepository messageRepository;


  @Autowired
  private UserService userService;

  @Autowired
  UserRepository userRepository;


  @RequestMapping("/")
  public String listCourses(Model model) {
    model.addAttribute("messages", messageRepository.findAll());
    if(userService.getUser() != null) {
      model.addAttribute("user_id", userService.getUser().getId());
    }
    return "list";
  }

  @GetMapping("/register")
  public String showRegistrationPage(Model model) {
    model.addAttribute("user", new User());
    return "registration";
  }

  @PostMapping("/register")
  public String processRegistrationPage(@Valid
                                        @ModelAttribute("user") User user, BindingResult result,
                                        Model model) {
    model.addAttribute("user", user);
    if (result.hasErrors()) {
      return "registration";
    } else {
      userService.saveUser(user);
      model.addAttribute("message", "User Account Created");
    }
    return "login";
  }

  @RequestMapping("/login")
  public String login() {
    return "login";
  }

  @GetMapping("/add")
  public String listNow(Model model) {
    model.addAttribute("message", new Message());
    return "listform";
  }

  @PostMapping("/process")
  public String processList(@Valid @ModelAttribute("message") Message message, BindingResult result) {
    User user = userService.getUser();
    user.setGravatar(gravatar(user.getId()));
    if (result.hasErrors()) {
      return "listform";
    }
    message.setUser(userService.getUser());
    messageRepository.save(message);
    return "redirect:/";
  }

  @RequestMapping("/edit/{id}")
  public String updateMessage(@PathVariable("id") long id, Model model) {
      model.addAttribute("message", messageRepository.findById(id));
      return "listform";
    }

    @RequestMapping("/details/{id}")
    public String showMessage ( @PathVariable("id") long id, Model model){
      model.addAttribute("message", messageRepository.findById(id).get());
      return "show";
    }
    @RequestMapping("/delete/{id}")
    public String delMessage ( @PathVariable("id") long id){
      messageRepository.deleteById(id);
      return "redirect:/";
    }
  public String gravatar(@PathVariable("id") long id){
    User user = userService.getUser();
    String hash = userService.md5Hex(user.getEmail());
    String gravurl = "https://s.gravatar.com/avatar/";
    String gravatar = gravurl + hash + "?s=21";
    user.setGravatar(gravatar);
    gravatar = user.getGravatar();
    return gravatar;
  }

  @RequestMapping("/userpage/{id}")
  public String userPage(@PathVariable("id") long id, Model model){
    model.addAttribute("users", userRepository.findById(id).get());
    return "userpage";
  }

  }
