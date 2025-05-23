package com.smart.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;



@Controller
public class HomeController {
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping("/")
	public String home(Model model)
	{	
		model.addAttribute("title","Home-Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model)
	{	
		model.addAttribute("title","About-Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model)
	{	
		model.addAttribute("title","Register-Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	

	
	//handler for registering user
	@RequestMapping(value="/do_register",method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult reslt,@RequestParam(value="agreement",defaultValue="false") boolean agreement,Model model,HttpSession session)
	{	
		try {
			if(!agreement)
			{
				System.out.println("You have not agreed the terms and conditions");
				throw new Exception("You have not agreed the terms and conditions");
			}
			
			if(reslt.hasErrors())
			{
				System.out.println("ERROR "+reslt.toString());
				model.addAttribute("user",user);
				return "signup";
			}
			
			 // ✅ Check if email already exists
	        User existingUser = userRepository.findByEmail(user.getEmail());
	        if (existingUser != null) {
	            model.addAttribute("user", user);
	            session.setAttribute("message", new Message("Email already registered. Try another one!", "alert-warning"));
	            return "signup";
	        }
	        
	        // Set default values
	        user.setRole("ROLE_USER");
	        user.setEnabled(true);
	        user.setImageUrl("default.png");
	       user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("Agreement "+agreement);
			System.out.println("User "+user);
			
			User result=this.userRepository.save(user);
			
			
			model.addAttribute("user",new User());
			session.setAttribute("message", new Message("Successfully registerd..!!","alert-success"));
			return "signup";
		}
		catch(Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Somethimg Went Wrong..!!"+e.getMessage(),"alert-error"));
		}
		return "signup";
	}
	
	

	    @GetMapping("/signin")
    public String customLogin(Model model) 
	    {
	    	model.addAttribute("title","Login Page");
           return "login"; 
	    }
	

}

