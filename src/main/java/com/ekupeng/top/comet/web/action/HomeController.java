package com.ekupeng.top.comet.web.action;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ekupeng.top.comet.domain.People;
import com.ekupeng.top.comet.domain.User;

@Controller
public class HomeController {

	@RequestMapping(value="/")
	public ModelAndView test(HttpServletResponse response) throws IOException{
		return new ModelAndView("home");
	}
	
	@RequestMapping(value="test")
	public ModelAndView test1(HttpServletResponse response,@ModelAttribute User user,@ModelAttribute People people) throws IOException{
		System.out.println(user);
		System.out.println(people);
		return new ModelAndView("result");
	}
}
