package com.test.pages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.test.controller.ActionMethods;
import com.test.controller.FlowMethods;
import com.test.excelAPI.ExcelOperation;


public class LoginPage extends FlowMethods {

	Logger log = LogManager.getLogger(ActionMethods.class);	
	ActionMethods actionMethods = new ActionMethods();
	ExcelOperation excelOperation = new ExcelOperation();

  
}