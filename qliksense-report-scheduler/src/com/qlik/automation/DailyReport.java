package com.qlik.automation;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class DailyReport {
	
	final static Logger logger = Logger.getLogger(DailyReport.class);

	public static void generateDailyReports()	{
		try {
			Properties props = PropertiesUtil.getApplicationProperties();
			String HOST_NAME = props.getProperty("hostName");
			String TEMP_DIR = props.getProperty("tempDirectory");
			
			//get list of sheets to be printed
			List <String> sheetNames = new ArrayList<String> ();			
			String sheetsNames[] = props.getProperty("dailyReport.sheets").split(",");
			for(int i = 0; i< sheetsNames.length;i++){
				sheetNames.add(sheetsNames[i]+"/state/analysis");
			}
			
			//Use firefox profile
			FirefoxProfile firefoxProfile = new FirefoxProfile();
			firefoxProfile.setPreference("extensions.firebug.currentVersion", "1.8.1"); 
			firefoxProfile.setPreference("network.automatic-ntlm-auth.trusted-uris", "https://localhost");
			firefoxProfile.setAcceptUntrustedCertificates(true);
			WebDriver driver = new FirefoxDriver(firefoxProfile);
			driver.manage().window().maximize();
			List <String> pdfNames = new ArrayList<String>();
			for(String sheetName: sheetNames) {
				driver.get(HOST_NAME+sheetName);
				Thread.sleep(10000);
				WebElement menuButton = driver.findElement(By.cssSelector("button.qui-popover-button:nth-child(2)"));
				menuButton.click();
				Thread.sleep(1000);
				WebElement printButton = driver.findElement(By.xpath("//*[@q-title-translation=\"GlobalMenu.ExportSheetToPdf\"]"));
				printButton.click();
				Thread.sleep(1000);
				WebElement fitToPage = driver.findElement(By.cssSelector("label.print-dialog-radio-single:nth-child(3) > div:nth-child(2) > span:nth-child(1)"));
				fitToPage.click();
				Thread.sleep(1000);
				WebElement export = driver.findElement(By.cssSelector(".dm-footer > button:nth-child(2)"));
				export.click();
				Thread.sleep(30000);
				WebElement link = driver.findElement(By.linkText("Click here to download your PDF file."));
				String linkLocation = link.getAttribute("href");
				logger.debug("Link Location : " + linkLocation);
				URI uri = new URI(linkLocation);
				String path = uri.getPath();
				String pathArr[] = path.split("/");
				String idStr = pathArr[pathArr.length-2] +"/"+ pathArr[pathArr.length-1];
				String pdfName = idStr;
				logger.debug("Pdf name : " + pdfName);
				pdfNames.add(pdfName);
			}
			firefoxProfile.setAcceptUntrustedCertificates(false);
			if(pdfNames.size() >0) {
				String mergedFileName = "reports.pdf";//create a file name with random number and date timestamp
				MergePDFs.merge(pdfNames, TEMP_DIR, mergedFileName);
				MailUtil.sendReports(TEMP_DIR+mergedFileName, mergedFileName);
			}
			driver.quit();
		    logger.debug("Reports sent and Browser closed.");
		} catch(Exception ex) {
			logger.error(ex,ex);
		}
	}
}
