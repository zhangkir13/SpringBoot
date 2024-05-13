package com.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/error")
public class ErrController implements ErrorController{

	@Override
	public String getErrorPath() {
		// TODO Auto-generated method stub
		return "index";
	}
	
	@RequestMapping
    public String error() {
        return getErrorPath();
    }

}
