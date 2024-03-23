package com.flab.offcoupon.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RequestMapping("/")
@RestController
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {
        log.info("SSE Connect: {} , httpSession : ", session);

        Authentication aUthentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("aUthentication1 = " + aUthentication);
        SecurityContext context = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        System.out.println("context2 = " + context);
        Authentication authentication1 = context.getAuthentication();
        System.out.println("authentication3 = " + authentication1);
        return "home";
    }

    @GetMapping("/main")
    public ModelAndView main() {
        return new ModelAndView("main");
    }
}
