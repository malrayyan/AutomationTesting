package com.dibs.pages;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;

import com.dibs.controller.ActionMethods;
import com.dibs.controller.FlowMethods;
import com.dibs.controller.Report;
import com.dibs.excelAPI.ExcelOperation;
import com.dibs.utils.Constant;

public class ToysAndEntertainment extends FlowMethods {

	Logger log = LogManager.getLogger(ActionMethods.class);	
	ActionMethods actionMethods = new ActionMethods();
	ExcelOperation excelOperation = new ExcelOperation();
	
	public void navigateToysAndEntireTainment(String productName)
	{
		String locator,locator2;
		try
		{
			locator = objectRepo.getProperty("TandE.header");
			locator2 = objectRepo.getProperty("TandE.Products");
			actionMethods.mouseHoveActions(locator, locator2.replace("Dummy", productName));
			
			Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag+"User navigated to  : "+productName, driver);
		} catch (Exception e) {
			 Constant.statusFlag = "Failed";			
			 Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag+"Failed to search : "+productName, driver);
		}
	}
	
	public void selectSpecialOffers(String splOfferes)
	{
		String locator, msg = "";
		try
		{
			locator = objectRepo.getProperty("TandE.SpecialOfferList");
			List<String> inputs = Arrays.asList(splOfferes.split("/"));
			List<WebElement> splOff = actionMethods.getListOfdata(locator);
				for(int j=0;j<=inputs.size();j++)
				{
					for(int i=0;i<splOff.size();i++)
					{
						if(splOff.get(i).getText().trim().equalsIgnoreCase(inputs.get(j)))
						{
							splOff.get(i).click();
							actionMethods.waitFor();
						} else
							msg = "Not avilable : "+splOff.get(i).getText();
					}
				}

			Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag+"User selected  : "+splOfferes, driver);
		} catch (Exception e) {
			 Constant.statusFlag = "Failed";
			 Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag+"Failed to search : "+msg, driver);
			 throw e;
		}
	}
	
	public void selectpriceRange(String price)
	{
		String locator, msg = "";List<String> inputs;
		try
		{
			locator = objectRepo.getProperty("TandE.PriceRange");
			inputs = Arrays.asList(price.split("/"));
			List<WebElement> splOff = actionMethods.getListOfdata(locator);
			for(int i=0;i<splOff.size();i++)
			{
				for(int j=0;j<inputs.size();j++)
				{
					if(splOff.get(i).getText().trim().equalsIgnoreCase(inputs.get(j)))
					{
						splOff.get(i).click();
						actionMethods.waitFor();
					} else
						msg = "Not avilable : "+inputs.get(i);
				}
			}

			Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag+"User selected  : "+price, driver);
		} catch (Exception e) {
			 Constant.statusFlag = "Failed";			
			 Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag+"Failed to search : "+msg, driver);
		}
	}
	
	public void selectProductLink(String itemName)
	{
		String locator;
		try
		{
			locator = objectRepo.getProperty("TandE.selectProductLink");
			actionMethods.click(locator.replace("Dummy", itemName));
			
			Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag+"User selected : "+itemName, driver);
		} catch (Exception e) {
			 Constant.statusFlag = "Failed";			
			 Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag+"Failed to search : "+itemName, driver);
		}
	}
}
