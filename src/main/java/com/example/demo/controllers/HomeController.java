/**
 * Created By shoh@n
 * Date: 8/6/2022
 */

package com.example.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "welcome";
    }
}
