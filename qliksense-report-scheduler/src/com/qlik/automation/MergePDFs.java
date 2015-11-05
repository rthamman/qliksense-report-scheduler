package com.qlik.automation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.log4j.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class MergePDFs {
	
	final static Logger logger = Logger.getLogger(MergePDFs.class);

	public static void merge(List<String> files, String directory,	String mergedFileName) throws Exception {
		try {
			ByteArrayOutputStream boas = new ByteArrayOutputStream();
			MergePDFs.concatPDFs(files, boas, directory);
			FileOutputStream fos = new FileOutputStream(new File(directory+ mergedFileName));
			boas.writeTo(fos);
		} catch (Exception e) {
			logger.error(e, e);
			throw e;
		}
	}

	private static void concatPDFs(List<String> pdfs,OutputStream outputStream, String tempDiv) {
		ByteArrayOutputStream byteStream = null;
		Document document = new Document();
		try {
			PdfCopy copy = new PdfCopy(document, outputStream);
			document.open();
			for (String pdf : pdfs) {
				String fileLocation = tempDiv + pdf;
				InputStream templateIs = new FileInputStream(fileLocation);
				PdfReader reader = new PdfReader(templateIs);
				byteStream = new ByteArrayOutputStream();
				PdfStamper stamper = new PdfStamper(reader, byteStream);
				stamper.setFreeTextFlattening(true);
				stamper.setFormFlattening(true);
				stamper.close();
				PdfReader pdfReader = new PdfReader(byteStream.toByteArray());
				for (int page = 0; page < pdfReader.getNumberOfPages();) {
					copy.addPage(copy.getImportedPage(pdfReader, ++page));
				}
				pdfReader.close();
				reader.close();
			}
			document.close();
			copy.close();
		} catch (Exception e) {
			logger.error(e,e);
		} finally {
			if (document.isOpen())
				document.close();
			try {
				if (outputStream != null)
					outputStream.close();
			} catch (IOException ioe) {
				logger.error(ioe,ioe);
			}
		}
	}
}