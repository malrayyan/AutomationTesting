package com.test.excelAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.net.Inet4Address;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.relevantcodes.extentreports.DisplayOrder;
import com.relevantcodes.extentreports.ExtentReports;
import com.test.controller.FlowMethods;
import com.test.controller.Runner;
import com.test.testData.DashboardTestData;
import com.test.testData.TestDataObjectLogin;
import com.test.utils.Constant;
import com.test.utils.ModuleTestData;




public class ExcelOperation {

	public static Connection connection = null;
	public static Recordset recordset = null;
	public static String noOfThreds;
	public static String country;
	public static String modules;
	public static String sleepTimeout;
	public static String loadingTimeout;
	public static String globalMaxTimeout;
	public static String listOfFailedMethods;
	public static String testSuiteJourneyID;
	public  static String testSuiteModuleName="";
	public  static String testSuiteReportingModuleName="";
	public  static String testSuiteSubModuleName="";
	public  String testSuiteJourneyFlowID;
	public static String previousJourneyID="";
	public static String previousBrowserType="";
	public static List<String> passedJourneysList = new ArrayList<String>();
	public static List<String> executedJourneys = new ArrayList<String>();
	public static List<String> failedJourneys = new ArrayList<String>();
	public static List<String> failedJourneyswithTimeStamp = new ArrayList<String>();
	public static List<String> skippedJourneys = new ArrayList<String>();
	public static List<String> somethingWentWrongJourneys = new ArrayList<String>();
	public static List<String> correlationsList = new ArrayList<String>();
	public static List<String> moduleList_Reporting = new ArrayList<String>();
	public static HashMap<String,String> journey_LinkRef = null;
	public static String executionStartTime = "";
	public static String executionEndTime = "";
	public static String userNameDetails = "";
	public static String exceptionCaptured = "";
	public static String almCaseName = "";
	public static String developer = "";
	public static String owner = "";
	public static String testDataClassName = "";
	public static Boolean testEligibleToSkip = false;

	public static List<String> exclusivejourneys = new ArrayList<String>();

	public static Fillo fillo = new Fillo();
	public static Properties Config = new Properties();;
	static Logger log = LogManager.getLogger(ExcelOperation.class);
	public static String vascoStatus="";
	public static String subModuleName="";
	

	public static void readExecutionVariables(int currentIteration) {

		Connection	executionvariablesConnection = null;
		Recordset executionvariablesRecordset = null;
		
		try 
		{
			String completeIpAddress = Inet4Address.getLocalHost().getHostAddress();
			String[] ipAddress = completeIpAddress.split("\\.");
			String machine = ipAddress[ipAddress.length-1];
			
			if(Constant.Path_TestData.isEmpty())
			{
				FileInputStream fileInputStream = new FileInputStream("./Config.properties");
				Config.load(fileInputStream);
				Constant.Path_TestData=Config.getProperty("Path_TestData").replace("':'", ":");
			}
			Constant.Path_TestData = System.getProperty("user.dir")+Constant.Path_TestData;
			executionvariablesConnection = fillo.getConnection(Constant.Path_TestData);
			String strQuery = "Select * from Settings";
			executionvariablesRecordset = executionvariablesConnection.executeQuery(strQuery);
			while (executionvariablesRecordset.next()) 
			{
				Constant.environment = (executionvariablesRecordset.getField("Environment"));
				Constant.URL = (executionvariablesRecordset.getField("ApplicationURL"));
				country = (executionvariablesRecordset.getField("Country"));
				modules = (executionvariablesRecordset.getField("Modules"));
				Constant.screenshot_path = (executionvariablesRecordset.getField("PathOfScreenshot"));
				//Constant.screenshot_path = System.getProperty("user.dir")+"//src//test//resources//reportsScreenshots//";
				//Constant.driverPath_chrome = (executionvariablesRecordset.getField("DriverPath_chrome"));
				Constant.driverPath_chrome = System.getProperty("user.dir")+"//src//test//resources//drivers//chromedriver.exe";
				Constant.download_dir = (executionvariablesRecordset.getField("Download_dir"));
				globalMaxTimeout = (executionvariablesRecordset.getField("GLOBAL_MAX_TIMEOUT"));
				sleepTimeout = (executionvariablesRecordset.getField("SLEEP"));
				loadingTimeout = (executionvariablesRecordset.getField("LOADING_TIMEOUT"));
			    //Constant.pathOfReport = (executionvariablesRecordset.getField("PathOfReport"));
				Constant.pathOfReport = System.getProperty("user.dir")+"//src//test//resources//reports//";
				Constant.extentReportConfigFile = (executionvariablesRecordset.getField("ExtentReportConfigFile"));
				Constant.GLOBAL_MAX_TIMEOUT=Integer.parseInt(globalMaxTimeout);
				Constant.SLEEP=Integer.parseInt(sleepTimeout);
				Constant.LOADING_TIMEOUT=Integer.parseInt(loadingTimeout);
				Constant.reRun=Integer.parseInt((executionvariablesRecordset.getField("RERUN")));
				log.info("Initialize Extent Report");

				if(currentIteration==0)
				{
					//Create Folder Structure for current execution - Relative Screen prints
					String currentTimeStamp = getCurrentTimeStamp();
					String correspondingMachineName = currentTimeStamp;					
					correspondingMachineName = correspondingMachineName + "-" + machine;
					Constant.pathOfFailedCasesScreePrints = Constant.pathOfReport + correspondingMachineName + "\\" + "ScreenshotsFailed" + "_Run_" + String.valueOf(currentIteration);
					Constant.pathOfPassedCasesScreePrints = Constant.pathOfReport + correspondingMachineName + "\\" + "ScreenshotsPassed" + "_Run_" + String.valueOf(currentIteration);
					Constant.packagePath = Constant.pathOfReport + correspondingMachineName + "\\";
					new File(Constant.pathOfFailedCasesScreePrints).mkdirs();
					new File(Constant.pathOfPassedCasesScreePrints).mkdirs();
					Constant.reportPath = Constant.pathOfReport + correspondingMachineName + "\\" +("Report_" + currentTimeStamp + "_Run_" + String.valueOf(currentIteration) + ".html");
					Constant.pathOfFailedCasesScreePrints = Constant.pathOfFailedCasesScreePrints + "\\";
					Constant.pathOfPassedCasesScreePrints = Constant.pathOfPassedCasesScreePrints +"\\";
				}
				else
				{
					log.info(Constant.reportPath.replace("_Run_" + (currentIteration-1), "_Run_" + currentIteration));
					Constant.reportPath = Constant.reportPath.replace("_Run_" + (currentIteration-1), "_Run_" + currentIteration);
					Constant.pathOfFailedCasesScreePrints = Constant.pathOfFailedCasesScreePrints.replace("_Run_" + (currentIteration-1), "_Run_" + currentIteration);
					Constant.pathOfPassedCasesScreePrints = Constant.pathOfPassedCasesScreePrints.replace("_Run_" + (currentIteration-1), "_Run_" + currentIteration);
					new File(Constant.pathOfFailedCasesScreePrints).mkdirs();
					new File(Constant.pathOfPassedCasesScreePrints).mkdirs();
				}

				Constant.extentReporter = new ExtentReports(Constant.reportPath, true,DisplayOrder.OLDEST_FIRST );
				Constant.extentReporter.addSystemInfo("Environment", Constant.environment);
				Constant.extentReporter.addSystemInfo("ApplicationURL", Constant.URL);
				Constant.extentReporter.addSystemInfo("Test Data","<a href="+Constant.Path_TestData+"><span class='label info'>Test Data</span></a>" );
				Constant.extentReporter.addSystemInfo("Country", country);
				Constant.extentReporter.addSystemInfo("Browser", Constant.browser);
				Constant.extentReporter.addSystemInfo("Global Max Timeout", Constant.GLOBAL_MAX_TIMEOUT +" sec");
				Constant.extentReporter.addSystemInfo("Loading Timeout", Constant.LOADING_TIMEOUT +" sec");
				Constant.extentReporter.addSystemInfo("Sleep Timeout", Constant.SLEEP +" sec");
				Constant.extentReporter.loadConfig(new File(Constant.extentReportConfigFile));
			}
		} catch (Exception e) {
			log.error("Failed to execute step", e);
		} finally {
			if(executionvariablesConnection!=null) {
				executionvariablesConnection.close();
			} else{
				log.error("Unable to create connection with Test Data sheet.");
			}
			if(executionvariablesRecordset!=null){
				executionvariablesRecordset.close();
			}
		}
	}


	public  void readTestSuite() 
	{		
		String testcaseDescription = null;		
		try {
			testSuiteJourneyID = "";
			testSuiteModuleName="";
			testSuiteSubModuleName="";
			testSuiteJourneyFlowID="";
			ArrayList<String> journeysList = new ArrayList<String>();

			//Dummy conditional block - Just To restrict connection object scope
			if(true)
			{
				Connection readTestSuiteConnection = null;
				Recordset readTestSuiteRecordset = null;
				//To define the scope of Connection and Record Set objects
				readTestSuiteConnection = fillo.getConnection(Constant.Path_TestData);
				log.info("connection to test data established");
				String strQuery = "Select * from TestSuite where Execution_Status='Yes'";
				log.info("Test Suite Query =" + strQuery);
				readTestSuiteRecordset = readTestSuiteConnection.executeQuery(strQuery);

				//Fetch List of "Yes" Marked Cases from excel
				while (readTestSuiteRecordset.next()) 
				{
					journeysList.add(readTestSuiteRecordset.getField("Journey_ID") + "~" + readTestSuiteRecordset.getField("Module_Name") + "~" + readTestSuiteRecordset.getField("Sub_Module_Name") + "~" + readTestSuiteRecordset.getField("FlowMethods"));
				}
				
				readTestSuiteConnection.close();
				readTestSuiteRecordset.close();	
			}

			ListIterator<String> valuesIterator = journeysList.listIterator(); 

			while(valuesIterator.hasNext()) 
			{
				String value=valuesIterator.next();
				String[] journeysArray = value.split("~");

				log.info("*****************************Start of Tessuite =  "+ testSuiteJourneyID +"*****************************");				
				testSuiteJourneyID = journeysArray[0];
				testSuiteModuleName = journeysArray[1];
				testSuiteSubModuleName = journeysArray[2];
				testSuiteJourneyFlowID = journeysArray[3];

				if(testSuiteModuleName.equalsIgnoreCase("Login")) {
					loginModuleTestData(testSuiteModuleName, testSuiteJourneyID);
					testcaseDescription = ModuleTestData.TEST_DESCRIPTION;
				} else if(testSuiteModuleName.equalsIgnoreCase("Dashboard"))
				{
					populateDashTestboardData(testSuiteModuleName, testSuiteJourneyID);
					testcaseDescription = DashboardTestData.TEST_DESCRIPTION;
					TestDataObjectLogin.LOGIN_DETAILS = DashboardTestData.LOGIN_DETAILS;
				}
				
				Constant.extentTest = Constant.extentReporter.startTest(testSuiteJourneyID, testcaseDescription);  
				Constant.extentTest.assignAuthor(System.getProperty("user.name"));				
				if(testSuiteSubModuleName.equalsIgnoreCase(testSuiteModuleName))
					Constant.extentTest.assignCategory(testSuiteModuleName);
				else 
				{
					Constant.extentTest.assignCategory(testSuiteSubModuleName+" ["+testSuiteModuleName+"]");
				}
				executeFlowMethods(testSuiteModuleName, testSuiteJourneyFlowID);
				Constant.extentReporter.endTest(Constant.extentTest);

				log.info("*****************************End of Tessuite =  " +testSuiteJourneyID+Constant.browser+ "*****************************");
			}
			//clearTestDataFromVariables();

		} 
		catch (Exception e) 
		{
			log.error("Failed to execute step", e);
		} 
	}

	public void updateReferencevalue(String moduleName, String columnName, String referenceValue, String linkRef) {

		Connection	updateReferencevalueConnection = null;
		try {
			updateReferencevalueConnection = fillo.getConnection(Constant.Path_TestData);
			String strQuery = "Update "+moduleName+" Set "+columnName+"='"+referenceValue+"'where LINK_REF='"+linkRef+"'";
			updateReferencevalueConnection.executeUpdate(strQuery);
			log.info("Test data Sheet updated for LINK_REF=" +linkRef + "& with REFERENCE_VALUE =" +referenceValue);
		} catch (Exception e) {
			log.error("Failed to execute step", e);
		}  finally {
			if(updateReferencevalueConnection!=null) {
				updateReferencevalueConnection.close();
			}
		}
	}

	public void loginModuleTestData(String ModuleName, String JourneyID) {

		Connection moduleTestDataConnection = null;
		Recordset moduleTestDataRecordset = null;
		try {
			moduleTestDataConnection = fillo.getConnection(Constant.Path_TestData);
			String strQuery = "Select * from "+ ModuleName +" where Journey_ID='"+JourneyID+"'";
			moduleTestDataRecordset = moduleTestDataConnection.executeQuery(strQuery);
			while (moduleTestDataRecordset.next()) {
				
				
			}
		}catch (Exception e) {
			log.error("Failed to execute step", e);
		} finally {
			if(moduleTestDataConnection!=null) {
				moduleTestDataConnection.close();
			} else {
				log.error("Unable to create connection with Test Data sheet.");
			}
			if(moduleTestDataRecordset!=null){
				moduleTestDataRecordset.close();
			}
		}
	}
	
	public void populateDashTestboardData(String ModuleName, String JourneyID) {

		Connection moduleTestDataConnection = null;
		Recordset moduleTestDataRecordset = null;
		try {
			moduleTestDataConnection = fillo.getConnection(Constant.Path_TestData);
			String strQuery = "Select * from "+ ModuleName +" where Journey_ID='"+JourneyID+"'";
			moduleTestDataRecordset = moduleTestDataConnection.executeQuery(strQuery);
			while (moduleTestDataRecordset.next()) {
				DashboardTestData.JOURNEY_ID = (moduleTestDataRecordset.getField("Journey_ID"));
				DashboardTestData.LOGIN_DETAILS = (moduleTestDataRecordset.getField("LoginDetails"));
				DashboardTestData.TEST_DESCRIPTION = (moduleTestDataRecordset.getField("TEST_CASE_DESCRIPTION"));
				DashboardTestData.PRODUCT_NAME = (moduleTestDataRecordset.getField("ProductName"));
				
				
			}
		}catch (Exception e) {
			log.error("Failed to execute step", e);
		} finally {
			if(moduleTestDataConnection!=null) {
				moduleTestDataConnection.close();
			} else {
				log.error("Unable to create connection with Test Data sheet.");
			}
			if(moduleTestDataRecordset!=null){
				moduleTestDataRecordset.close();
			}
		}
	}
	
	
	public void executeFlowMethods(String testSuiteModuleName, String testSuiteJourneyFlowID) {
		try {
			testEligibleToSkip = false;
			String[] flowMethodDetails = testSuiteJourneyFlowID.split(",");
			for(int flowIndex=0; flowIndex<flowMethodDetails.length;flowIndex++) {				
				if(!testEligibleToSkip)
				{
					String[] flowMetodsWithFlags = flowMethodDetails[flowIndex].split("\\|\\|");
					log.info("flowID= " + flowMetodsWithFlags[0]);
					
					if(flowIndex>0 && FlowMethods.driver==null)
						break;
					Runner.execute_Actions(flowMetodsWithFlags[0], testSuiteModuleName);
				}
			}
		} catch (Exception e) {
			log.error("Failed to execute step", e);
		}
	}
	
	
	public void createTempFile(int iteration)
	{
		try {

			//Record Start Time
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MMMM-dd-HH-mm-ss");
			Date date = new Date();
			executionStartTime = dateFormat.format(date);

			if(Constant.Path_TestData.equalsIgnoreCase(""))
			{
				FileInputStream fileInputStream = new FileInputStream("./Config.properties");
				Config.load(fileInputStream);
				Constant.Path_TestData=Config.getProperty("Path_TestData").replace("':'", ":");
			}	

			//Connecting to Main Test Data Sheet and loading the content to required Objects
			log.info("Connecting to Main Test Data Sheet");
			XSSFWorkbook mainTestData_WorkBook = new XSSFWorkbook(new FileInputStream(Constant.Path_TestData));	
			mainTestData_WorkBook.setMissingCellPolicy(Row.RETURN_NULL_AND_BLANK);

			//Copying "Settings" sheet from Main Test Data Sheet to Newly created workbook
			XSSFSheet mainTD_SettingsSheet = mainTestData_WorkBook.getSheet("Settings");			
			XSSFWorkbook newlyCreated_TestData_WorkBook = new XSSFWorkbook();			
			XSSFSheet sheetOfSettings = newlyCreated_TestData_WorkBook.createSheet("Settings");
			newlyCreated_TestData_WorkBook.setMissingCellPolicy(Row.RETURN_NULL_AND_BLANK);

			//Code to move content inside Settings sheet Main Test Data Sheet to Newly created workbook
			for (int rowIndex=0;rowIndex <= mainTD_SettingsSheet.getLastRowNum(); rowIndex++ )
			{
				if(rowIndex>1)
					break;
				sheetOfSettings.createRow(rowIndex);
				for (int cellIndex=0;cellIndex < mainTD_SettingsSheet.getRow(rowIndex).getLastCellNum(); cellIndex++ )
				{
					Cell exictingCell = mainTD_SettingsSheet.getRow(rowIndex).getCell(cellIndex, Row.RETURN_NULL_AND_BLANK);
					if(!(exictingCell==null))
					{
						switch (exictingCell.getCellType()) {
						case Cell.CELL_TYPE_BOOLEAN:
							sheetOfSettings.getRow(sheetOfSettings.getLastRowNum()).createCell(cellIndex).setCellValue(exictingCell.getBooleanCellValue());
							break;
						case Cell.CELL_TYPE_NUMERIC:
							sheetOfSettings.getRow(sheetOfSettings.getLastRowNum()).createCell(cellIndex).setCellValue(exictingCell.getNumericCellValue());
							break;
						case Cell.CELL_TYPE_STRING:
							sheetOfSettings.getRow(sheetOfSettings.getLastRowNum()).createCell(cellIndex).setCellValue(exictingCell.getStringCellValue());
							break;
						case Cell.CELL_TYPE_BLANK:
							sheetOfSettings.getRow(sheetOfSettings.getLastRowNum()).createCell(cellIndex).setCellValue("");
							break;
						default:
							sheetOfSettings.getRow(sheetOfSettings.getLastRowNum()).createCell(cellIndex).setCellValue("");
						}
					}
					else
						sheetOfSettings.getRow(sheetOfSettings.getLastRowNum()).createCell(cellIndex).setCellValue("");
				}
			}
			log.info("Settings Sheet Copied From Main WorkBook to New WorkBook");

			//Copy Header Row of TestSuite from main Test Data Sheet to Newly created workbook
			XSSFSheet sheetOfTestSuite = newlyCreated_TestData_WorkBook.createSheet("TestSuite") ;
			newlyCreated_TestData_WorkBook.setMissingCellPolicy(Row.RETURN_NULL_AND_BLANK);
			XSSFSheet mainTestSuite = mainTestData_WorkBook.getSheet("TestSuite");
			sheetOfTestSuite.createRow(0);

			//Fetch and Store indexes of required columns from TestSuite sheet. which will be used in further steps
			int executionStatusIndex =0;
			int moduleNameIndex =0;
			int journeyIDIndex =0;
			for (int cellIndex=0;cellIndex < mainTestSuite.getRow(0).getLastCellNum(); cellIndex++ )
			{
				Cell exictingCell = mainTestSuite.getRow(0).getCell(cellIndex, Row.RETURN_NULL_AND_BLANK);
				if(!(exictingCell==null))
				{
					switch (exictingCell.getCellType()) {
					case Cell.CELL_TYPE_STRING:
						sheetOfTestSuite.getRow(0).createCell(cellIndex).setCellValue(exictingCell.getStringCellValue());
						if(exictingCell.getStringCellValue().equalsIgnoreCase("Execution_Status"))
							executionStatusIndex = cellIndex;
						else if(exictingCell.getStringCellValue().equalsIgnoreCase("Journey_ID"))
							journeyIDIndex = cellIndex;
						else if(exictingCell.getStringCellValue().equalsIgnoreCase("Module_Name"))
						{
							moduleNameIndex = cellIndex;
							break;
						}
					default:
						System.out.println("Inside Test Suite Header Copy Section");
					}
				}
			}
			log.info("TestSuite Sheet - Header Section Copied From Main WorkBook to New WorkBook");

			//Keep list of distinct modules for which journeys are marked as YES
			ArrayList<String> Available_ModuleList = new ArrayList<String>();
			//Keep list of Journeys and corresponding modules for each journeys
			ArrayList<String> JourneyList = new ArrayList<String>();			
			//Moving "Yes" Marked Journeys from Main Test Suite to newly created test suite
			//Need to analyze this variable
			int testSuiteRowIndex = 0;
			for (int rowIndex=0;rowIndex <= mainTestSuite.getLastRowNum(); rowIndex++ )
			{
				if(mainTestSuite.getRow(rowIndex).getCell(executionStatusIndex, Row.RETURN_NULL_AND_BLANK).getStringCellValue().equalsIgnoreCase("Yes"))
				{
					sheetOfTestSuite.createRow(sheetOfTestSuite.getLastRowNum()+1);

					//Adding modules names to list - Only Distinct module names will be available in this list
					if(!Available_ModuleList.contains(mainTestSuite.getRow(rowIndex).getCell(moduleNameIndex, Row.RETURN_NULL_AND_BLANK).getStringCellValue()))
						Available_ModuleList.add(mainTestSuite.getRow(rowIndex).getCell(moduleNameIndex, Row.RETURN_NULL_AND_BLANK).getStringCellValue());
					//Add each Journey along with corresponding module name to the list (Format-JourneyID~ModuleName)
					JourneyList.add(mainTestSuite.getRow(rowIndex).getCell(journeyIDIndex, Row.RETURN_NULL_AND_BLANK).getStringCellValue() + "~" + mainTestSuite.getRow(rowIndex).getCell(moduleNameIndex, Row.RETURN_NULL_AND_BLANK).getStringCellValue());
					for (int cellIndex=0;cellIndex < mainTestSuite.getRow(rowIndex).getLastCellNum(); cellIndex++ )
					{
						Cell exictingCell = mainTestSuite.getRow(rowIndex).getCell(cellIndex, Row.RETURN_NULL_AND_BLANK);
						if(!(exictingCell==null))
						{
							switch (exictingCell.getCellType()) {
							case Cell.CELL_TYPE_BOOLEAN:
								sheetOfTestSuite.getRow(sheetOfTestSuite.getLastRowNum()).createCell(cellIndex).setCellValue(exictingCell.getBooleanCellValue());
								break;
							case Cell.CELL_TYPE_NUMERIC:
								sheetOfTestSuite.getRow(sheetOfTestSuite.getLastRowNum()).createCell(cellIndex).setCellValue(exictingCell.getNumericCellValue());
								break;
							case Cell.CELL_TYPE_STRING:
								sheetOfTestSuite.getRow(sheetOfTestSuite.getLastRowNum()).createCell(cellIndex).setCellValue(exictingCell.getStringCellValue());
								break;
							case Cell.CELL_TYPE_BLANK:
								sheetOfTestSuite.getRow(sheetOfTestSuite.getLastRowNum()).createCell(cellIndex).setCellValue("");
								break;
							default:
								sheetOfTestSuite.getRow(sheetOfTestSuite.getLastRowNum()).createCell(cellIndex).setCellValue("");
							}
						}
						else
							sheetOfTestSuite.getRow(sheetOfTestSuite.getLastRowNum()).createCell(cellIndex).setCellValue("");
					}
					testSuiteRowIndex++;
				}
			}
			log.info("Required data - Only YES Marked Journeys - copied to TestSuite Sheet From Main WorkBook to New WorkBook");			

			//Update File Name Accordingly - To male filename unique
			String [] removeExtension=Constant.Path_TestData.split("\\.xlsx");
			String completeFileName=removeExtension[0];
			Random randomValue=new Random();
			double randomStr1=randomValue.nextInt(20000);
			String[] ipAddress = Inet4Address.getLocalHost().getHostAddress().split("\\.");
			String machine = "";
			if(ipAddress.length>0)
				machine = ipAddress[ipAddress.length-1];

			if(iteration==0)
				completeFileName = completeFileName + String.valueOf((int)randomStr1) + "_Machine_" + machine + "_Run_0.xlsx";
			else if(iteration>0)
				completeFileName = completeFileName.replace("_Run_" + (iteration-1), "_Run_" + iteration) + ".xlsx";
			else
				completeFileName = completeFileName + "_Run_" + iteration + ".xlsx";

			//Keep index of LINK_REF column and corresponding module name
			HashMap<String,Integer> mapLinkRefIndex = new HashMap<String,Integer>();
			//New instance of MAP for every iteration
			journey_LinkRef = new HashMap<String,String>();

			//Create Sheet for each module - newly created document - Also copy header to respective module sheet - Store index of "Link_Ref" column for each module 
			Iterator<String> moduleslistIterator = Available_ModuleList.iterator();
			while(moduleslistIterator.hasNext())
			{
				String currentModuleName = moduleslistIterator.next().trim();										
				newlyCreated_TestData_WorkBook.createSheet(currentModuleName);
				log.info("Sheet Created: " + currentModuleName + " in Iteration: " + iteration);
				XSSFSheet sheetofTD = mainTestData_WorkBook.getSheet(currentModuleName);
				XSSFSheet sheetofNewDoc = newlyCreated_TestData_WorkBook.getSheet(currentModuleName);				
				//Creating row for current module and copying header row from main workbook 
				sheetofNewDoc.createRow(0);
				for (int cellIndex=0;cellIndex < sheetofTD.getRow(0).getLastCellNum(); cellIndex++ )
				{
					Cell exictingCell = sheetofTD.getRow(0).getCell(cellIndex, Row.RETURN_NULL_AND_BLANK);
					if(!(exictingCell==null))
					{
						switch (exictingCell.getCellType()) 
						{
						case Cell.CELL_TYPE_BOOLEAN:
							sheetofNewDoc.getRow(0).createCell(cellIndex).setCellValue(exictingCell.getBooleanCellValue());
							break;
						case Cell.CELL_TYPE_NUMERIC:
							sheetofNewDoc.getRow(0).createCell(cellIndex).setCellValue(exictingCell.getNumericCellValue());
							break;
						case Cell.CELL_TYPE_STRING:
							sheetofNewDoc.getRow(0).createCell(cellIndex).setCellValue(exictingCell.getStringCellValue());
							//Fetching and storing the index of LINK_REF for this module - which will be used in next steps
							if(exictingCell.getStringCellValue().equalsIgnoreCase("LINK_REF"))
								mapLinkRefIndex.put(currentModuleName, cellIndex);
							break;

						case Cell.CELL_TYPE_BLANK:
							sheetofNewDoc.getRow(0).createCell(cellIndex).setCellValue("");
							break;
						default:
							sheetofNewDoc.getRow(0).createCell(cellIndex).setCellValue("");
						}
					}
					else
						sheetofNewDoc.getRow(0).createCell(cellIndex).setCellValue("");
				}
			}
			log.info("New Sheet is created for each Module and respective header sections are copied From Main WorkBook to New WorkBook");

			//Copy test data for each YES marked journey from respective module sheet (MAIN workbook to Newly created workbook)
			Iterator<String> journeyvaluesIterator = JourneyList.iterator(); 
			int newRowIndex = 1;
			while(journeyvaluesIterator.hasNext()) 
			{
				String currentItem = journeyvaluesIterator.next();
				String currentJourney = currentItem.split("~")[0];
				String currentModule = currentItem.split("~")[1];
				XSSFSheet sheetofTD = mainTestData_WorkBook.getSheet(currentModule);
				XSSFSheet sheetofNewDoc = newlyCreated_TestData_WorkBook.getSheet(currentModule);				
				int k = 0;
				int JouneyIndex=0;
				for (int rowIndex=0;rowIndex <= sheetofTD.getLastRowNum(); rowIndex++ )
				{
					Cell journeyCell = sheetofTD.getRow(rowIndex).getCell(0, Row.CREATE_NULL_AS_BLANK);

					//Fetching the index of Journey_ID inside the sheet
					if(k==0)
					{
						journeyCell = sheetofTD.getRow(rowIndex).getCell(0, Row.CREATE_NULL_AS_BLANK);
						JouneyIndex = 0;
						//Check if first column of header is "Journey_ID" - else look for "Journey_ID" column in next cells - currently looking in first 3 cells						
						if(journeyCell.getCellType()==Cell.CELL_TYPE_NUMERIC)
						{
							journeyCell = sheetofTD.getRow(rowIndex).getCell(1, Row.CREATE_NULL_AS_BLANK);
							JouneyIndex = 1;
						}
						else if(!journeyCell.getStringCellValue().equalsIgnoreCase("Journey_ID"))
						{
							journeyCell = sheetofTD.getRow(rowIndex).getCell(1, Row.CREATE_NULL_AS_BLANK);
							JouneyIndex = 1;
						}
						//If First cell is not Journey_ID the Next cell will get loaded in previous condition - Check if this cell is Journey_ID else get next cell and assume it as Journey_ID 
						if(JouneyIndex==1)
						{
							if(journeyCell.getCellType()==Cell.CELL_TYPE_NUMERIC)
							{
								journeyCell = sheetofTD.getRow(rowIndex).getCell(2, Row.CREATE_NULL_AS_BLANK);
								JouneyIndex = 2;
							}
							else if(!journeyCell.getStringCellValue().equalsIgnoreCase("Journey_ID"))
							{
								journeyCell = sheetofTD.getRow(rowIndex).getCell(1, Row.CREATE_NULL_AS_BLANK);
								JouneyIndex = 2;
							}
						}
						k++;
					}					
					else
					{
						//Start fetching value from Journey_ID column and Comparing with our value - If matching pull the data to newly created sheet
						journeyCell = sheetofTD.getRow(rowIndex).getCell(JouneyIndex, Row.CREATE_NULL_AS_BLANK);
						if(journeyCell.getStringCellValue().equalsIgnoreCase(currentJourney))
						{
							boolean linkRefIdentified = false;
							sheetofNewDoc.createRow(sheetofNewDoc.getLastRowNum()+1);		
							for (int cellIndex=0;cellIndex < sheetofTD.getRow(rowIndex).getLastCellNum(); cellIndex++ )
							{					
								Cell exictingCell = sheetofTD.getRow(rowIndex).getCell(cellIndex, Row.CREATE_NULL_AS_BLANK);
								if(!(exictingCell==null))
								{
									switch (exictingCell.getCellType()) 
									{
									case Cell.CELL_TYPE_BOOLEAN:
										sheetofNewDoc.getRow(sheetofNewDoc.getLastRowNum()).createCell(cellIndex).setCellValue(exictingCell.getBooleanCellValue());
										break;
									case Cell.CELL_TYPE_NUMERIC:
										sheetofNewDoc.getRow(sheetofNewDoc.getLastRowNum()).createCell(cellIndex).setCellValue(exictingCell.getNumericCellValue());
										break;
									case Cell.CELL_TYPE_STRING:
										sheetofNewDoc.getRow(sheetofNewDoc.getLastRowNum()).createCell(cellIndex).setCellValue(exictingCell.getStringCellValue());
										//If the index is "Link_Ref" for this journey - Pull Journey_ID and its Link_Ref
										//This will be used for marking failed cases and dependent journeys for next iterations
										if(cellIndex==mapLinkRefIndex.get(currentModule))
										{
											journey_LinkRef.put(currentJourney,exictingCell.getStringCellValue());
											linkRefIdentified = true;
										}
										break;
									case Cell.CELL_TYPE_BLANK:
										sheetofNewDoc.getRow(sheetofNewDoc.getLastRowNum()).createCell(cellIndex).setCellValue("");
										break;
									default:
										sheetofNewDoc.getRow(sheetofNewDoc.getLastRowNum()).createCell(cellIndex).setCellValue("");
									}
								}
								else
									sheetofNewDoc.getRow(sheetofNewDoc.getLastRowNum()).createCell(cellIndex).setCellValue("");
								//Start *********************************************

							}
							if(!linkRefIdentified)
								journey_LinkRef.put(currentJourney,"NA");
							newRowIndex ++;
							break;
						}
					}
				}
			}

			log.info("Test Data for all the YES marked journeys are copied From Main WorkBook to New WorkBook - Copying Process Successful");
			FileOutputStream fileOut = new FileOutputStream(completeFileName);
			newlyCreated_TestData_WorkBook.write(fileOut);
			fileOut.flush();
			fileOut.close();
			log.info("New File Generated Successfully and path: " + completeFileName);
			Constant.Path_TestData = completeFileName;
		}
		catch (Exception e) {
			log.error("Failed to create new Test data sheet with required content and execption is", e);
		} 
	}

	public static String getCurrentTimeStamp() 
	{
		String currentTime = "";
		try
		{
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MMMM-dd-HH-mm-a");
			Date date = new Date();
			currentTime = dateFormat.format(date);			
		}
		catch (Exception e) 
		{
			System.out.println("Error Occured whhile Fetchiing Time Stamp");
		}		
		return currentTime;
	}
	
	public int markFailedCasesAlone()
	{
		int FailedCasesCount=0;
		try {
			//Connecting to TD Sheet and loading the content to required Objects
			XSSFWorkbook mainTD_WorkBook = new XSSFWorkbook(new FileInputStream(Constant.Path_TestData));	
			mainTD_WorkBook.setMissingCellPolicy(Row.RETURN_NULL_AND_BLANK);
			XSSFSheet mainTestSuite = mainTD_WorkBook.getSheet("TestSuite");

			//Store indexes of required columns from TestSuite sheet. which is required in further steps
			int testRunStatusIndex =0;
			int journeyIDIndex =0;
			int executionStatus =0;
			for (int cellIndex=0;cellIndex < mainTestSuite.getRow(0).getLastCellNum(); cellIndex++ )
			{
				Cell exictingCell = mainTestSuite.getRow(0).getCell(cellIndex, Row.RETURN_NULL_AND_BLANK);				
				switch (exictingCell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					if(exictingCell.getStringCellValue().equalsIgnoreCase("Test_Run_Status"))
						testRunStatusIndex = cellIndex;
					else if(exictingCell.getStringCellValue().equalsIgnoreCase("Journey_ID"))
						journeyIDIndex = cellIndex;
					else if(exictingCell.getStringCellValue().equalsIgnoreCase("Execution_Status"))
						executionStatus = cellIndex;
					else
						break;
				default:
					int test = 0;
				}
			}

			//Fetch List of Journeys which are failed in past execution
			ArrayList<String> failedJourneyList = new ArrayList<String>();
			for (int rowIndex=0;rowIndex <= mainTestSuite.getLastRowNum(); rowIndex++ )
			{
				if(mainTestSuite.getRow(rowIndex).getCell(testRunStatusIndex, Row.RETURN_NULL_AND_BLANK).getStringCellValue().equalsIgnoreCase("Failed"))
					failedJourneyList.add(mainTestSuite.getRow(rowIndex).getCell(journeyIDIndex, Row.RETURN_NULL_AND_BLANK).getStringCellValue());
			}

			log.info("Re Executable Journeys Addition Started");
			//Fetch All Dependent Journeys of Failed Cases
			ArrayList<String> reExecutableJourneysList = new ArrayList<String>();
			for(int failedJourneyIndex=0;failedJourneyIndex<failedJourneyList.size();failedJourneyIndex++)
			{
				String currentLinkRef = journey_LinkRef.get(failedJourneyList.get(failedJourneyIndex));

				if(!currentLinkRef.equalsIgnoreCase("") && !currentLinkRef.equalsIgnoreCase("NA"))
				{
					Iterator it = journey_LinkRef.entrySet().iterator();
					while(it.hasNext())
					{
						Map.Entry pair = (Map.Entry)it.next();
						if(String.valueOf(pair.getValue()).equalsIgnoreCase(currentLinkRef) && !reExecutableJourneysList.contains(String.valueOf(pair.getKey())))
						{
							reExecutableJourneysList.add(String.valueOf(pair.getKey()));
							log.info("Jorney Added :" + String.valueOf(pair.getKey()) + " And Link Ref :" + currentLinkRef);
						}
					}
				}
				else
				{
					reExecutableJourneysList.add(failedJourneyList.get(failedJourneyIndex));
					log.info("Jorney Added in NA Case :" + failedJourneyList.get(failedJourneyIndex) + " And Link Ref :" + currentLinkRef);
				}
			}

			//Mark all other Journeys as "NO"
			for (int rowIndex=1;rowIndex <= mainTestSuite.getLastRowNum(); rowIndex++ )
			{
				String currentJourney = mainTestSuite.getRow(rowIndex).getCell(journeyIDIndex, Row.RETURN_NULL_AND_BLANK).getStringCellValue();
				if(!reExecutableJourneysList.contains(currentJourney))
					mainTestSuite.getRow(rowIndex).createCell(executionStatus).setCellValue("NO");
			}

			//Get Count of failed cases needs to be executed
			if(reExecutableJourneysList.size()>0)
				FailedCasesCount = reExecutableJourneysList.size();
			FileOutputStream fileOut = new FileOutputStream(Constant.Path_TestData);
			mainTD_WorkBook.write(fileOut);
			fileOut.flush();
			fileOut.close();

			log.info("Failed Cases Marked Successfully - Failed and Dependant cases count : " + FailedCasesCount);
		}
		catch (Exception e) {
			log.error("Failed to mark YES for failed test cases", e);
		}

		return FailedCasesCount;
	}

	public static void clearTestDataFromVariables() 
	{
		try
		{			
			String[] testDataClassNamesList = testDataClassName.split("\\|\\|");			
			for(int classIndex=0; classIndex<testDataClassNamesList.length;classIndex++)
			{
				for(Field field : Class.forName("com.standardchartered.s2b.testData." + testDataClassNamesList[classIndex]).getFields())
				{
					field.set(Class.forName("com.standardchartered.s2b.testData." + testDataClassNamesList[classIndex]), "");
				}
			}
			log.info("Successfully Cleared Data in variables for module : " + testSuiteModuleName + "Classes : " + testDataClassName);
		}
		catch (Throwable e) 
		{
			log.error("Failed to Cleared Data in variables for modlue : " + testSuiteModuleName, e);
		}		
	}
	
	public static String fetchElementFromRecordSet(Recordset executionvariablesRecordset, String columnName)
	{
		String currentColumnValue = "";
		try
		{
			currentColumnValue = executionvariablesRecordset.getField(columnName);
		}
		catch(Exception e)
		{
			log.error("Column Missing in Excel : " + columnName, e);
		}	
		return currentColumnValue;
	}

    
}




	