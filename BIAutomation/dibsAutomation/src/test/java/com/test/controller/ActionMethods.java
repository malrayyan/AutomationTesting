package com.test.controller;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.test.utils.Constant;

import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;



public class ActionMethods {

	public static WebDriver driver;
	public static WebDriver chromeDriver;
	public static WebDriver driver1;
	Logger log = LogManager.getLogger(ActionMethods.class);
	public WebDriver openBrowser()
	{
		driver = FlowMethods.driver;
		//System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"/lib/chromedriver");
		System.setProperty("webdriver.chrome.driver", Constant.driverPath_chrome);
		Map<String, Object> prefs = new HashMap<String, Object>();


		// Set the notification setting it will override the default setting

		prefs.put("profile.default_content_setting_values.notifications", 2);


		// Create object of ChromeOption class

		//		/ChromeOptions options = new 


		// Set the experimental option



		ChromeOptions co = new ChromeOptions();
		co.setExperimentalOption("prefs", prefs);
		co.addArguments("disable-extensions");
		co.addArguments("--disable-popup-blocking");
		//co.setPageLoadStrategy(PageLoadStrategy.NONE);
		co.setExperimentalOption("useAutomationExtension", false);
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setCapability(ChromeOptions.CAPABILITY, co);
		// To set download path
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		co.setExperimentalOption("prefs", chromePrefs);
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		capabilities.setCapability(ChromeOptions.CAPABILITY, co);
		driver = new ChromeDriver(co);
		driver.manage().deleteAllCookies();
		driver1 = driver;
		return driver;
	}

	
	public void launchURL(String url)
	{
		try
		{
			if(url.isEmpty())
			{
				driver.get("https://www.google.com/intl/en-GB/gmail/about/#");
				driver.manage().window().maximize();
			}
			else
			{
				driver.get(url);
				driver.manage().window().maximize();
			}
			log.info("Application URL Launched");
			Constant.statusFlag = "Passed";
			Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag+"URL : "+url, driver);
		} catch (Exception e) {
			Constant.statusFlag = "Failed";
			Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag, driver);
			log.error("Exception occured while launching browser URL : "+e.getMessage());
		}
	}

	public void launchEtsyWebApp()
	{
		try
		{
			driver.get(Constant.URL);
			log.info("Application : ETSY web app Launched");
			Constant.statusFlag = "Passed";
			Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag+"URL:"+Constant.URL, driver);
		} catch (Exception e) {
			Constant.statusFlag = "Failed";
			Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), Constant.statusFlag, driver);
			log.error("Exception occured while launching browser URL : "+e.getMessage());
		}
	}

	public String captureScreenshot(WebDriver driverA , String statusFlag) {
		try {
			Constant.capturedScreenshot = "";
			log.info("Screenshot Functionality invoked");
			Screenshot fpScreenshot = new AShot().shootingStrategy(ShootingStrategies.scaling(1)).takeScreenshot(driverA);		
			log.info("Screenshot is captured");
			String screenshotNaming = new SimpleDateFormat("dd-MMM-yyyy_hh_mm_ssaa").format(Calendar.getInstance().getTime());
			log.info("Screenshot Naming is Completed : " + screenshotNaming);

			if(statusFlag.contains("Passed") & statusFlag!=null)
			{
				Constant.capturedScreenshot = Constant.pathOfPassedCasesScreePrints + screenshotNaming + ".png";
				ImageIO.write(fpScreenshot.getImage(),"PNG",new File(Constant.capturedScreenshot));
				log.info("Passed Case screenshot is saved at : " + Constant.capturedScreenshot);				
				String [] splitFolderName = Constant.pathOfPassedCasesScreePrints.split("\\\\");
				Constant.capturedScreenshot = "./" + splitFolderName[splitFolderName.length-1] + "/" + screenshotNaming + ".png";

			}else if(statusFlag.contains("Failed") & statusFlag!=null ){
				Constant.capturedScreenshot = Constant.pathOfFailedCasesScreePrints + screenshotNaming + ".png";
				ImageIO.write(fpScreenshot.getImage(),"PNG",new File(Constant.capturedScreenshot));
				log.info("Failed Case Screenshot is saved at : " + Constant.capturedScreenshot);
				//Constant.capturedScreenshot = "./ScreenshotsFailed/" + screenshotNaming + ".png";
				String [] splitFolderName = Constant.pathOfFailedCasesScreePrints.split("\\\\");
				Constant.capturedScreenshot = "./" + splitFolderName[splitFolderName.length-1] + "/" + screenshotNaming + ".png";
			}
		} catch (Exception e) {
			log.error("Failed to take screenshot", e);
			//Constant.isDriverCorruptedDueToScreenPrints = true;
		}
		log.info("Path Of Screenshot :- " + Constant.capturedScreenshot);
		return Constant.capturedScreenshot;
	}
	
	public By findByLocator(String locatorType, String locatorValue)
	{
		By by = null;
		try
		{
			switch(locatorType.toUpperCase().trim())
			{
			case "ID":
				by = By.id(locatorValue);
				break;
			case "XPATH":
				by = By.xpath(locatorValue);
				break;	
			case "CSS":
				by = By.cssSelector(locatorValue);
				break;	
			case "TAGNAME":
				by = By.tagName(locatorValue);
				break;	
			default:
				by = null;
				break;
			}
		} catch (Exception e) {

		}
		return by;
	}

	public boolean isElementPresent(String locatorDetails)
	{
		boolean flag = false;
		try
		{
			String split[] = locatorDetails.split("~");
			String locatorValue = split[0];
			String locatorType = split[1];
			By locator = findByLocator(locatorType, locatorValue);
			if(waitForElementClicable(locator, Constant.GLOBAL_MAX_TIMEOUT).isDisplayed())
			{
				flag = true;
				Constant.statusFlag = "Passed";
			} 	
		} catch(Exception e)
		{
			Constant.statusFlag = "Failed";
			throw e;
		}
		return flag;
	}
	
	public boolean isElementPresentOptional(String locatorDetails)
	{
		boolean flag = false;
		try
		{
			String split[] = locatorDetails.split("~");
			String locatorValue = split[0];
			String locatorType = split[1];
			By locator = findByLocator(locatorType, locatorValue);
			if(waitForElementClicable(locator, Constant.GLOBAL_MAX_TIMEOUT).isDisplayed())
			{
				flag = true;
				
			} 	
		} catch(Exception e)
		{
			System.out.println("Element is not preented");
		}
		return flag;
	}


	public void enterInputMandatoryFiled(String locatorDetails, String testData)
	{
		try
		{
			String split[] = locatorDetails.split("~");
			String locatorValue = split[0];
			String locatorType = split[1];
			By locator = findByLocator(locatorType, locatorValue);
			WebElement ele = waitForElementClicable(locator, Constant.GLOBAL_MAX_TIMEOUT);
		   ele.clear();
			ele.sendKeys(testData);
			Constant.statusFlag = "Passed";
			log.info("Test step executed successfully : "+Thread.currentThread().getStackTrace()[1].getMethodName()+" "+locatorDetails);
		} catch (Exception e) 
		{
			Constant.statusFlag = "Failed";
			log.error("Failed to execute step : "+e);
			throw e;
		}

	}

	
	public WebElement waitForElementClicable(By locator,int waitTime)
	{
		try
		{
			WebElement ele;
			WebDriverWait wait = new WebDriverWait(driver, Constant.GLOBAL_MAX_TIMEOUT);
			ele = wait.until(ExpectedConditions.elementToBeClickable(locator));
			return ele;
		} catch(Exception e)
		{
			Constant.statusFlag = "Failed";
			log.error("Failed to execute step : "+e);
			throw e;
		}
	}

	public void waitFor()
	{
		try
		{
			Thread.sleep(TimeUnit.SECONDS.toMillis(Constant.SLEEP));
		} catch (Exception e) {
			log.error("Failed to execute step : "+e);
		}
	}

	public void click(String locatorDetails)
	{
		try
		{
			String split[] = locatorDetails.split("~");
			String locatorValue = split[0];
			String locatorType = split[1];
			By locator = findByLocator(locatorType, locatorValue);
			waitForElementClicable(locator, Constant.GLOBAL_MAX_TIMEOUT).click();
			Constant.statusFlag = "Passed";
			log.info("Test step executed successfully : "+Thread.currentThread().getStackTrace()[1].getMethodName()+" "+locatorDetails);
		} catch (StaleElementReferenceException e) 
		{
			try
			{
				for(int i=0;i<2;i++)
				{
					click(locatorDetails);
					break;
				}
			} catch (Exception exe) {
				Constant.statusFlag = "Failed";
				log.error("Failed to execute step : "+exe);
				throw e;
			}
		}
	}

	
	public void scrollToElement(String locatorDetails)
	{
		try
		{
			String split[] = locatorDetails.split("~");
			String locatorValue = split[0];
			String locatorType = split[1];
			By locator = findByLocator(locatorType, locatorValue);
			WebDriverWait wait = new WebDriverWait(driver, Constant.GLOBAL_MAX_TIMEOUT);
			wait.until(ExpectedConditions.elementToBeClickable(locator));
			WebElement element = driver.findElement(findByLocator(locatorType, locatorValue));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
			((JavascriptExecutor) driver).executeScript("arguments[0].click();",driver.findElement(locator));

			Constant.statusFlag = "Passed";
			log.info("Test step executed successfully : "+Thread.currentThread().getStackTrace()[1].getMethodName()+" "+locatorDetails);
		} catch (Exception exe) 
		{
			Constant.statusFlag = "Failed";
			log.error("Failed to execute step : "+exe);
			throw exe;
		}

	}

	public String getText(String locatorDetails)
	{
		String getText = "";
		try
		{
			String split[] = locatorDetails.split("~");
			String locatorValue = split[0];
			String locatorType = split[1];
			By locator = findByLocator(locatorType, locatorValue);
			getText = driver.findElement(locator).getText();
			Constant.statusFlag = "Passed";
			log.info("Test step executed successfully : "+Thread.currentThread().getStackTrace()[1].getMethodName()+" "+locatorDetails);
		} catch (Exception exe) 
		{
			Constant.statusFlag = "Failed";
			log.error("Failed to execute step : "+exe);
			throw exe;
		}
		return getText;

	}

	public String getAttributeValue(String locatorDetails,String value)
	{
		String getValue = "";
		try
		{
			String split[] = locatorDetails.split("~");
			String locatorValue = split[0];
			String locatorType = split[1];
			By locator = findByLocator(locatorType, locatorValue);
			getValue = driver.findElement(locator).getAttribute("value");
			Constant.statusFlag = "Passed";
			log.info("Test step executed successfully : "+Thread.currentThread().getStackTrace()[1].getMethodName()+" "+locatorDetails);
		} catch (Exception exe) 
		{
			Constant.statusFlag = "Failed";
			log.error("Failed to execute step : "+exe);
			throw exe;
		}
		return getValue;
	}

	public List<WebElement> getListOfdata(String locatorDetails)
	{
		List<WebElement> userData = null;
		try
		{
			String split[] = locatorDetails.split("~");
			String locatorValue = split[0];
			String locatorType = split[1];
			By locator = findByLocator(locatorType, locatorValue);
			userData = driver.findElements(locator);
			Constant.statusFlag = "Passed";
			log.info("Test step executed successfully : "+Thread.currentThread().getStackTrace()[1].getMethodName()+" "+locatorDetails);
		} catch (Exception exe) 
		{
			Constant.statusFlag = "Failed";
			log.error("Failed to execute step : "+exe);
			throw exe;
		}
		return userData;
	}

	public void selectDropDownData(String locator, String testData)
	{
		try
		{
			waitFor();
			List<WebElement> users = getListOfdata(locator);
			for(WebElement ele : users)
			{
				if(ele.getText().trim().contains(testData.trim()))
				{
					ele.click();
					break;
				} else
					System.out.println(ele.getText()+"--"+testData);
			}
		} catch(Exception e)
		{
			System.out.println("Failed to execute step  : "+e);
		}

	}

	public String getTimeStamp(String input)
	{
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
		return input.concat((String) dateFormat.format(date));
	}

	public Object genarateRandomNumber(int numLength)
	{
		try
		{
			Random r = new Random(System.nanoTime());
			String s = String.format("%0"+numLength+"d", numLength);
			int number = Integer.parseInt("1"+s.substring(0, s.length()-1));
			int uniqueNum = ((r.nextInt(2)) *number+r.nextInt(number));
			if(String.valueOf(uniqueNum).length()==String.valueOf(number).length())
				return uniqueNum;
			else
				return genarateRandomNumber(numLength);
		} catch (Exception e) {
			log.error("Failed to execute step : "+e);
		}
		return genarateRandomNumber(numLength);
	}

	public String genaratePassword()
	{
		String genaratePwd = "";
		try
		{
			int length = 8;
			String digits = "0123456789";
			String specials = "!$%^&*(),.;/'<>?:][{}";
			String all = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
					+ "abcdefghijklmnopqrstuvwxyz"
					+ digits + specials;
			Random rnd = new Random();
			List<String> result = new ArrayList<>();
			//List<String> result = new ArrayList();
			Consumer<String> appendChar = s ->
			result.add("" + s.charAt(rnd.nextInt(s.length())));
			appendChar.accept(digits);
			appendChar.accept(specials);
			while (result.size() < length)
				appendChar.accept(all);
			Collections.shuffle(result, rnd);
			genaratePwd = String.join("", result);
			System.out.println("Password Genarated Successfully : "+genaratePwd);
		} catch(Exception e)
		{
			log.error("Failed to execute step : "+e);
		}
		return genaratePwd;
	}
	
	public boolean isAlertPresent(String action) 
	{ 
	    try 
	    { 
	       Alert alert=  driver.switchTo().alert(); 
	       if(action.equalsIgnoreCase("accept"))
	    	   alert.accept();
	       else
	    	   alert.dismiss();
	        return true; 
	    }   // try 
	    catch (NoAlertPresentException Ex) 
	    { 
	        return false; 
	    }   // catch 
	} 

	public void switvhToFrame(int input)
	{
		try
		{
			driver.switchTo().frame(input);
		} catch(Exception e)
		{
			log.error("Failed to execute step : "+e);
		}
	}


	public void switchToTab()
	{
		try
		{
			ArrayList<String> tabs = new ArrayList<String> (driver.getWindowHandles());
			System.out.println(tabs.size());
			driver.switchTo().window(tabs.get(1));
			Report.getInstance().generateReport(Thread.currentThread().getStackTrace()[1].getMethodName(), "Passed"+"Switch to new tab", driver);
		} catch (Exception e) {
			log.error("Failed to execute step : "+e);
		}
	}

	public void refreshBrowser()
	{
		driver.navigate().refresh();
	}

	public void handleAlerts()
	{
		driver.switchTo().alert().dismiss();
	}

	public void closeWindow()
	{
		driver = driver1;
		chromeDriver.quit();

	}

	public void switchToParentWindow()
	{
		String pWindow = driver.getWindowHandle();
		driver.switchTo().window(pWindow);

	}
	
	public  void uploadFile(String fileLocation) {
        try {
        	
        	waitFor();
        	StringSelection stringSelection = new StringSelection(fileLocation);
  		    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            Robot robot = new Robot();
	
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        } catch (Exception exp) {
        	exp.printStackTrace();
        }
    }
	
	public static void setClipboardData(String string) {
		StringSelection stringSelection = new StringSelection(string);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
	}
	
	/*
	 * public void mouseHoverAction(String locatorDetails) {
	 * 
	 * Actions actions = new Actions(FlowMethods.driver); String split[] =
	 * locatorDetails.split("~"); String locatorValue = split[0]; String locatorType
	 * = split[1]; By locator = findByLocator(locatorType, locatorValue); WebElement
	 * target = FlowMethods.driver.findElement(locator);
	 * actions.moveToElement(target).perform(); }
	 */
	
	public void mouseHoveActions(String source, String destination)
	{
		try
		{
			WebElement mainMenu = driver.findElement(findByLocator(source.split("~")[1], source.split("~")[0]));
			Actions actions = new Actions(driver);
			actions.moveToElement(mainMenu);
			WebElement subMenu = driver.findElement(findByLocator(destination.split("~")[1], destination.split("~")[0]));
			actions.moveToElement(subMenu);
			actions.click().build().perform();
			
		} catch (Exception e) {
			log.error("Failed to execute step : "+e);
		}
	}
	
}

