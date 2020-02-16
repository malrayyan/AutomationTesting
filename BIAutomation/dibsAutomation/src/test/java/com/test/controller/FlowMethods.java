package com.test.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.openqa.selenium.WebDriver;

import com.test.pages.DashboardPage;
import com.test.pages.LoginPage;
import com.test.testData.DashboardTestData;
import com.test.utils.Constant;

public class FlowMethods {

	public static WebDriver driver;
	static  LoginPage login = new LoginPage();
	static ActionMethods actionMethod = new ActionMethods();
	public static Properties objectRepo = new Properties();
	
	static
	{
		File src = new File("./ObjectRepo.properties");
		try
		{
			FileInputStream stream = new FileInputStream(src);
		    objectRepo.load(stream);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

	
	public static void login(String moduleName) throws Exception
	{
		try
		{
			if(driver==null)
			{
				driver = actionMethod.openBrowser();
				actionMethod.launchURL(Constant.URL);
				
			} else
			{
				actionMethod.waitFor();

			}
		} catch (Exception e) {
			actionMethod.refreshBrowser();
			Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag+"getScreenShot", driver);
			throw e;
		}
	}
	
	public void selectProductFromDashboard(String moduleName)
	{
		DashboardPage dashboard = new DashboardPage();
		try
		{
			dashboard.enterSearchforItems(DashboardTestData.PRODUCT_NAME);
			dashboard.validateProducts(DashboardTestData.PRODUCT_NAME);
			Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag, driver);
		} catch (Exception e) {
			
			Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag, driver);
		}
	}	
	
}
