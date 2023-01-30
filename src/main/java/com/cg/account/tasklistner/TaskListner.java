package com.cg.account.tasklistner;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

public class TaskListner implements TaskListener {

	private final Logger logger = Logger.getLogger(TaskListner.class.getName());
	@Override
	public void notify(DelegateTask delegateTask) {
	
		logger.info("Task " + delegateTask.getName() + " has been assigned to " + delegateTask.getAssignee());
		 String variableValue = (String) delegateTask.getVariable("name");
		    logger.info("Accessing process variable myVariable with value: "+variableValue);
		
		String taskId = delegateTask.getId();
		    String processInstanceId = delegateTask.getProcessInstanceId();
		    logger.info("Task id: "+taskId+" Process instance id: "+processInstanceId);
	}

}
