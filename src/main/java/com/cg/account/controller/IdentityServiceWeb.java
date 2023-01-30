package com.cg.account.controller;

import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.identity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * 
 * @author umenredd
 *
 */
@RestController
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/account/")
public class IdentityServiceWeb {
	
	@Autowired
	IdentityService identityService;
	
	/**
	 * 
	 * 
	 * @param groupId
	 * @return
	 */
	@GetMapping("users/{groupId}")
	public List<User> getAllUsersByGroup(@PathVariable String groupId){ 
	System.out.println("List of users "+groupId);   
	List<User> listOfUsers =  identityService.createUserQuery().memberOfGroup(groupId).list();    
	System.out.println(listOfUsers.size());  
	List<String> userList = new ArrayList<>();   
	for(User user:listOfUsers){     
		userList.add(user.getFirstName()); 
		} 
	return listOfUsers;
	}
}
