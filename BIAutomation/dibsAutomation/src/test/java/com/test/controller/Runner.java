package com.test.controller;
import org.apache.logging.log4j.*;
import org.openqa.selenium.WebDriver;
import com.test.excelAPI.ExcelOperation;
import com.test.utils.Constant;

public class Runner {

	public WebDriver driver;
	static Logger log = LogManager.getLogger(Runner.class);

  public static void main(String args[]) throws InterruptedException {
		ExcelOperation excelOpr = new ExcelOperation();
		try 
		{
			ExcelOperation.readExecutionVariables(0);

			log.info("Actual Execution in First Iteration is started");
			excelOpr.readTestSuite();
			log.info("Actual Execution in First Iteration is started");

			exitMethod();	

		} catch (Throwable e) {
			e.printStackTrace();
		} 
	}

		public static void exitMethod()	{
			log.info("Generating Report Started in :" + Thread.currentThread().getName());
			Constant.extentReporter.flush();
			Constant.extentReporter.close();
			log.info("Report Generated successfully");
		}

		public static void execute_Actions(String flowID,String testSuiteModuleName) throws Exception {
			FlowMethods flowMethods = new FlowMethods();
			flowMethods.getClass().getMethod(flowID.trim(), String.class).invoke(flowMethods, testSuiteModuleName);
		}		
}


