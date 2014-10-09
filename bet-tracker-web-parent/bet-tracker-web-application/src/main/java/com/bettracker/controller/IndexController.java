package com.bettracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Tomer Cohen
 */
@Controller
@RequestMapping("index")
public class IndexController {

    @RequestMapping(method = RequestMethod.GET)
    public String view() {
        return "index/index";
    }
}
