package com.test.utils;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

public class Constant {
	
	       //Browser:
			public static String environment="";		
			public static  String browser= "USs";
			public static String URL ="";
			public static String Path_TestData =  "";
			public static  String screenshot_path = "";
			public static  String driverPath_chrome = "";
			public static String browserType= null;
			public static  String TestData_BackUp_folderName = "";
			public static  String zipFolderName = "";
			public static  String TestResult_BackUp_folderName = "";
			public static  String TestResultData_BackUp_folderName = "";
			public static  String download_dir ="";
			public static  String backUp_download_dir ="";
			
				
			public static int GLOBAL_MAX_TIMEOUT;
			public static int SLEEP ;
			public static int LOADING_TIMEOUT;
			public static int reRun;
			
			//For reporting
	        public static String statusFlag = null;
	        public static String StatusOfMethod= null;
			public static String pathOfScreenshot="";
			public static String capturedScreenshot=null;
			public static ExtentReports extentReporter=null;
			public static ExtentTest extentTest=null;
			public static String pathOfReport = "";
			public static String logsPath= "";
			public static String extentReportConfigFile ="";
			public static String reportPath = "";
			public static String packagePath="";
			public static String pathOfPassedCasesScreePrints = "";
			public static String pathOfFailedCasesScreePrints = "";
			
}
