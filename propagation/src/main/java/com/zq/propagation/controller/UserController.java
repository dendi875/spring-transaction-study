package com.zq.propagation.controller;

import com.zq.propagation.model.User;
import com.zq.propagation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	// 非事务版本
	@GetMapping("/addUser")
	public String addUser() {
		userService.addUser(User.builder().name("小张").age(31).build());
		return "SUCCESS";
	}

	@GetMapping("/addUserException")
	public String addUserException() throws Exception {
		userService.addUserException(User.builder().name("小张-exception").age(31).build());
		return "SUCCESS";
	}

	@GetMapping("/addUserRequired")
	public String addUserRequired() {
		userService.addUserRequired(User.builder().name("小张-required").age(31).build());
		return "SUCCESS";
	}

	@GetMapping("/addUserRequiresNew")
	public String addUserRequiresNew() {
		userService.addUserRequiresNew(User.builder().name("小张-requiresNew").age(31).build());
		return "SUCCESS";
	}

	@GetMapping("/addUserSupports")
	public String addUserSupports() {
		userService.addUserSupports(User.builder().name("小张-supports").age(31).build());
		return "SUCCESS";
	}

	@GetMapping("/addUserMandatory")
	public String addUserMandatory() {
		userService.addUserMandatory(User.builder().name("小张-mandatory").age(31).build());
		return "SUCCESS";
	}

	@GetMapping("/addUserNotSupports")
	public String addUserNotSupports() {
		userService.addUserNotSupports(User.builder().name("小张-notSupports").age(31).build());
		return "SUCCESS";
	}

	@GetMapping("/addUserNever")
	public String addUserNever() {
		userService.addUserNever(User.builder().name("小张-never").age(31).build());
		return "SUCCESS";
	}

	@GetMapping("/addUserNested")
	public String addUserNested() {
		userService.addUserNested(User.builder().name("小张-nested").age(31).build());
		return "SUCCESS";
	}
}
