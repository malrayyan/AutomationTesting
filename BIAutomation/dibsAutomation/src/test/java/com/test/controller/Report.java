package com.test.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.relevantcodes.extentreports.LogStatus;
import com.test.excelAPI.ExcelOperation;
import com.test.pages.LoginPage;
import com.test.utils.Constant;

public final class Report
{
	Logger log = LogManager.getLogger(Report.class);
	ActionMethods action_method = new ActionMethods();
	private volatile static Report INSTANCE;
	LoginPage login = new LoginPage();

	Report(){

		if( Report.INSTANCE != null ) {
			throw new InstantiationError( "Creating of object is not allowed." );
		}
	}
	public  static Report getInstance(){

		if(INSTANCE == null){
			synchronized(Report.class){
				if(INSTANCE == null){
					INSTANCE = new Report();
				}
			}

		}
		return INSTANCE;	
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("Cloning of report class is not allowed"); 
	}

	//Handling Serialization
	private Object readResolve(){
		return INSTANCE;
	}

	public  void generateReport(String currentMethodName, String statusFlag, WebDriver driverInstance)
	{
		try
		{
			String currentJourney = Constant.extentTest.getTest().getName();
			ExcelOperation excelOperation = new ExcelOperation();
			String screenshotPath ;
			if(statusFlag == null)
				log.info("Status not Provided. check statusflag variable in Method : " + currentMethodName);
			else if(statusFlag.contains("Passed") & statusFlag!=null)
			{
				log.info("Status in pass block for method  : " + currentMethodName + " And Status " + statusFlag);
				if(currentMethodName.contains("getScreenShot")) {
					screenshotPath= action_method.captureScreenshot(driverInstance ,statusFlag);
					Constant.extentTest.log(LogStatus.PASS, currentMethodName.replace("getScreenShot", "") , "<font size="+3+" color='#7CFC00'>"+"Passed" +"</font>"+ " <br />" + statusFlag.replace("Passed", "")+Constant.extentTest.addBase64ScreenShot(screenshotPath));
				}	
				else
				{
					Constant.extentTest.log(LogStatus.PASS, currentMethodName , "<font size="+3+" color='#7CFC00'>"+"Passed" +"</font>"+ " <br />" + statusFlag.replace("Passed", ""));
				}	
			}
			else if(statusFlag.contains("Failed") & statusFlag!=null )
			{
				boolean isEligibleForInclusion = false;
				if(currentMethodName.contains("getScreenShot"))
					currentMethodName=currentMethodName.replace("getScreenShot", "");				
				log.info("Status in Failed block for method : " + currentMethodName + " And Status " + statusFlag);

				String loginIssue = "";

				if(!excelOperation.exclusivejourneys.contains(currentJourney))
				{
					isEligibleForInclusion = true;
					excelOperation.failedJourneys.add(currentJourney + "||" + excelOperation.testSuiteReportingModuleName);
					excelOperation.skippedJourneys.remove(currentJourney + "||" + excelOperation.testSuiteReportingModuleName);
					excelOperation.exclusivejourneys.add(currentJourney);
				}

				screenshotPath= action_method.captureScreenshot(driverInstance ,statusFlag);
				
				log.info("Returned from screen shot method to Generate Report");				
				log.info("<span title='"+Constant.statusFlag.replaceAll("'", "'")+"'><a href='"+Constant.logsPath+"/automationFramework.log'><font size="+3+" color='#DC143C'>FAILED</font></a></span>");
				log.info(statusFlag.replace("Failed", "")+Constant.extentTest.addBase64ScreenShot(screenshotPath));
				log.info("statusFlag for Current Journey :"+currentJourney+" is "+statusFlag);
				Constant.extentTest.log(LogStatus.FAIL, 
						currentMethodName , 
						"<span title='"+Constant.statusFlag.replaceAll("'", "'")+"'><a href='"+Constant.logsPath+"/automationFramework.log'><font size="+3+" color='#DC143C'>FAILED</font></a></span>"+
								statusFlag.replaceAll("[<]","").trim().replace("Failed", "")+Constant.extentTest.addBase64ScreenShot(screenshotPath));
				//.replace("Failed", "")
				log.info("Screen shot added to Report");
				statusFlag = statusFlag.replace("Failed", "");
				if(excelOperation.listOfFailedMethods.equalsIgnoreCase(""))
					excelOperation.listOfFailedMethods = currentMethodName + "-" + statusFlag;
				else
					excelOperation.listOfFailedMethods = excelOperation.listOfFailedMethods + "," + currentMethodName + "-" + statusFlag;
			}
			else if(statusFlag.contains("Skipped") & statusFlag!=null)
			{
				if(!excelOperation.skippedJourneys.contains(currentJourney+ "||" + excelOperation.testSuiteReportingModuleName) && !excelOperation.exclusivejourneys.contains(currentJourney))
				{
					excelOperation.skippedJourneys.add(currentJourney+ "||" + excelOperation.testSuiteReportingModuleName);
					//excelOperation.updateTestExecutionStatus("Failed",currentJourney);
				}

				log.info("Status in Skipped block in Method : " + currentMethodName + " And Status " + statusFlag);
				Constant.extentTest.log(LogStatus.SKIP,currentMethodName, statusFlag);
			}
			else if(statusFlag.contains("Info") & statusFlag!=null )
			{
				log.info("Status in Info block  : " + statusFlag);
				Constant.extentTest.log(LogStatus.INFO,currentMethodName, statusFlag);
			}
			else
				log.info("Status not updated to report.");
			Constant.statusFlag=null;
		}
		catch (Exception e) 
		{
			log.error("Failed in Adding Test Step to Report : " + currentMethodName , e);
		}
	}
}


