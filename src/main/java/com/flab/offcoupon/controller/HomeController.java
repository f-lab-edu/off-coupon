package com.flab.offcoupon.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/")
@RestController
public class HomeController {

    @GetMapping
    public String home() {
        return "home";
    }
    /** 인가 체크용 **/
    @GetMapping("member")
    public String onlyMember() {
        return "member";
    }
}
