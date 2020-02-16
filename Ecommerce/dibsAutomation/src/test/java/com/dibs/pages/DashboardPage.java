package com.dibs.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dibs.controller.ActionMethods;
import com.dibs.controller.FlowMethods;
import com.dibs.controller.Report;
import com.dibs.excelAPI.ExcelOperation;
import com.dibs.utils.Constant;

public class DashboardPage extends FlowMethods{

	Logger log = LogManager.getLogger(ActionMethods.class);	
	ActionMethods actionMethods = new ActionMethods();
	ExcelOperation excelOperation = new ExcelOperation();
	
	public void enterSearchforItems(String productName)
	{
		String locator;
		try
		{
			locator = objectRepo.getProperty("Dashboard.SearchforItem");
			actionMethods.enterInputMandatoryFiled(locator, productName);
			locator = objectRepo.getProperty("Dashboard.SearchIocon");
			actionMethods.click(locator);
			
			Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag+"User searched for : "+productName, driver);
		} catch (Exception e) {
			 Constant.statusFlag = "Failed";			
			 Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag+"Failed to search : "+productName, driver);
		}
	}
	
	public void validateProducts(String productName)
	{
		String locator, expResult = "";
		try
		{
			locator = objectRepo.getProperty("Dashboard.Common");
			if(actionMethods.isElementPresent(locator.replace("Dummy", productName)))
			{
				locator = objectRepo.getProperty("Dashboard.ExpREsult");
				expResult = actionMethods.getText(locator.replace("Dummy", productName));
			}
			
			Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag+"User got : "+productName+expResult+" Products", driver);
		} catch (Exception e) {
			 Constant.statusFlag = "Failed";			
			 Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag+"Products are not avilable : "+productName, driver);
		}
	}
}
