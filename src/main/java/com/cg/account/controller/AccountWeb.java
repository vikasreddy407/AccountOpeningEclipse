package com.cg.account.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cg.account.service.AccountService;
import com.cg.account.service.LoggerDelegate;
import com.cg.account.service.MyUserDetailsService;
import com.cg.account.utils.JwtUtil;
import com.cg.account.vo.AccountDetails;
import com.cg.account.vo.AuthRequest;
import com.cg.account.vo.TaskStatus;

import org.camunda.bpm.engine.ProcessEngine;
//import org.camunda.bpm.engine.ProcessEngineConfiguration;
//import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * 
 * restcontoller is used to handle the resful webservices
 * we are using cross ordigin to connect the backed to frontend
 *Request mapping is used to to map a specific HTTP request to a method in a controller class.
 *
 */
@RestController
@CrossOrigin(origins="http://localhost:4200") 
@RequestMapping("/account/")
public class AccountWeb {
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    ProcessEngine processEngine;
    
    @Autowired
    private AuthenticationManager authenticationManager;
 
    @Autowired
    private MyUserDetailsService userDetailsService;
    
    @Autowired
    private JwtUtil jwtTokenUtil;
    
    @PostMapping("/new-account")
    public ResponseEntity<AccountDetails> startAccountOpeningJourney(@RequestBody AccountDetails newAccountOp){
    	HashMap<String,Object> newAccountMap = new HashMap<>();
		newAccountMap.put("name",newAccountOp.getName());
		newAccountMap.put("address",newAccountOp.getAddress());
		newAccountMap.put("age",newAccountOp.getAge());
		newAccountMap.put("email",newAccountOp.getEmail());
		newAccountMap.put("salary",newAccountOp.getSalary());
		
    	ProcessInstance processInstance =  runtimeService.startProcessInstanceByKey("Account1234", newAccountMap);
    	if(null != processInstance){
    		newAccountOp.setReferenceNo(processInstance.getProcessInstanceId());
            return new ResponseEntity<>(newAccountOp, HttpStatus.OK);
        }
    	return new ResponseEntity<>( newAccountOp,HttpStatus.OK);

}
 
 
    @PutMapping("/updateTaskStatus")

    public void updateTaskStatus(@RequestBody TaskStatus taskStatus){
       
           RuntimeService runtimeService = processEngine.getRuntimeService();
           TaskService taskService = processEngine.getTaskService();
          
          Task task = taskService.createTaskQuery().taskId(taskStatus.getTaskId()).singleResult(); 
           
           if(taskStatus.getTaskName().equalsIgnoreCase("manager")){
             if(taskStatus.getTaskStatus().equalsIgnoreCase("approved")){
                 runtimeService.setVariable(task.getExecutionId(), "isManagerApproved", true);
                 taskService.complete(task.getId());
            }else if(taskStatus.getTaskStatus().equalsIgnoreCase("rejected")){
                runtimeService.setVariable(task.getExecutionId(), "isManagerApproved", false);
                taskService.complete(task.getId());
            }
          }
   
           else if(taskStatus.getTaskName().equalsIgnoreCase("frontdesk to update customer")){
               if(taskStatus.getTaskStatus().equalsIgnoreCase("approved")){
                   runtimeService.setVariable(task.getExecutionId(), "addVariable", true);
                   taskService.complete(task.getId());
              }else if(taskStatus.getTaskStatus().equalsIgnoreCase("rejected")){
                  runtimeService.setVariable(task.getExecutionId(), "addVariable", false);
                  taskService.complete(task.getId());
              }     
          }
    }
    
    
    @PostMapping("/authenticate")
    public String generateToken(@RequestBody AuthRequest authRequest) throws Exception {
       try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword())
            );
       } catch (BadCredentialsException ex) {
           throw new Exception("inavalid username or password",ex);
           }
       return jwtTokenUtil.generateToken(authRequest.getUserName());
//       final UserDetails userDetails = userDetailsService
//    		   .loadUserByUsername(authRequest.getUserName());
//       final String jwt = jwtTokenUtil.generateToken(userDetails);
//    }
    }  
}