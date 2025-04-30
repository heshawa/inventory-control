package org.springboot.java17.api.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class HelloWorld {
	
	@GetMapping()
	public String hello() {
		return "Hello, World!";
	}
	
	@GetMapping("/{name}")
	public String helloName(@PathVariable(name = "name") String userName) {
		return "Hello, " + userName + "!";
	}
}
