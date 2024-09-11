/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2021 Lifestyle India Pvt Ltd    All Rights Reserved.
 * 
 * Rev 1.0        Jan 19, 2021       Mohan Yadav             GSTIN AUTOMATION Changes 
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.gstinJob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.gstinJob.utility.MAXGSTINUtility;
import max.retail.stores.gstinJob.utility.gstin.GSTINInvoiceIfc;
import max.retail.stores.gstinJob.utility.gstin.MAXDataErrorReport;
import max.retail.stores.gstinJob.utility.gstin.MAXDataReport;
import max.retail.stores.gstinJob.utility.gstin.MAXDocumentErrorResp;
import max.retail.stores.gstinJob.utility.gstin.MAXDocumentErrorStatusResp;
import max.retail.stores.gstinJob.utility.gstin.MAXDocumentResp;
import max.retail.stores.gstinJob.utility.gstin.MAXDocumentStatusErrorResp;
import max.retail.stores.gstinJob.utility.gstin.MAXDocumentStatusResp;
import max.retail.stores.gstinJob.utility.gstin.MAXEInvoiceErrorResp;
import max.retail.stores.gstinJob.utility.gstin.MAXEInvoiceResp;
import max.retail.stores.gstinJob.utility.gstin.MAXErrorResp;
import max.retail.stores.gstinJob.utility.gstin.MAXGSTINConstantsIfc;
import max.retail.stores.gstinJob.utility.gstin.MAXGSTINTokenResponseIfc;
import max.retail.stores.gstinJob.utility.gstin.MAXPropertyErrorsResp;
import max.retail.stores.gstinJob.utility.gstin.MAXValidationReport;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.foundation.tour.gate.Gateway;

public class GSTINInvoiceAutomationJob  extends QuartzJobBean {

	HashMap inputData = new HashMap();
	HashMap invoiceInput = new HashMap();
	HashMap outputData = new HashMap();
	protected static final Logger logger = Logger.getLogger(GSTINInvoiceAutomationJob.class);
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		logger.error("Test cron job ");
		logger.info("GSTIN AUTOMATION Start here  ######################################## 0000000000000000000000000000000");
		String storeId = Gateway.getProperty("application", "StoreID", "");
		logger.warn("Getproperty");
		
		inputData.put(MAXCodeConstantsIfc.STORE_ID, storeId);
		inputData.put(MAXCodeConstantsIfc.GSTIN_AUTOMATION, 1);
		logger.warn("gstin_automation");
		
		
		
		MAXGSTINAutomationTransaction gstinTransaction = (MAXGSTINAutomationTransaction) DataTransactionFactory.create(MAXDataTransactionKeys.GSTIN_INVOICE_AUTOMATION);
		try {
			outputData = gstinTransaction.getGstinCongiguration(inputData);
			logger.warn("gstinconfiguration");
			if(outputData !=null && outputData.size()>0){

				Serializable resp =MAXGSTINUtility.generateGstinToken(outputData);
				logger.warn("serializable ");
				
				
				if(resp != null && resp instanceof MAXGSTINTokenResponseIfc) {
					MAXGSTINTokenResponseIfc token = null;
					logger.warn(token + "instanceof");
					try {
						token = (MAXGSTINTokenResponseIfc) resp;
						String tokenId = null;
						String referenceID = null;
						if(token != null && token.getToken() != null ) {
							tokenId = token.getToken();
							
							if(tokenId != null) {
								
								invoiceInput.put(MAXCodeConstantsIfc.STORE_ID, storeId);
								invoiceInput.put(MAXCodeConstantsIfc.GSTIN_AUTOMATION, 2);
								logger.warn("GSTIN_AUTOMATION");
								
								logger.warn("INPUTDATA"+MAXCodeConstantsIfc.GSTIN_TTL_DAYS);
								invoiceInput.put(MAXCodeConstantsIfc.GSTIN_TTL_TXN, outputData.get(MAXGSTINConstantsIfc.GSTIN_TXN_TTL));
								logger.warn(MAXCodeConstantsIfc.GSTIN_TTL_TXN +"OUTPUTDATA");
								if(outputData.get(MAXGSTINConstantsIfc.GSTIN_DAYS)!="" && outputData.get(MAXGSTINConstantsIfc.GSTIN_DAYS)!=null) {
									logger.warn("Hi-----------------------");
									invoiceInput.put(MAXCodeConstantsIfc.GSTIN_TTL_DAYS, outputData.get(MAXGSTINConstantsIfc.GSTIN_DAYS));
								}
								else {
									
									invoiceInput.put(MAXCodeConstantsIfc.GSTIN_TTL_DAYS, "0");
								}
								ArrayList<GSTINInvoiceIfc> invoices = new ArrayList<GSTINInvoiceIfc>(); ///////  get invoice 
							
								invoices = gstinTransaction.getInvoiceData(invoiceInput);
								ArrayList<GSTINInvoiceIfc> savedinvoices = new ArrayList<GSTINInvoiceIfc>();
								logger.warn("arraylist");
								for(int i=0; i<invoices.size(); i++) {
									logger.warn("nothing");
									GSTINInvoiceIfc invoice = invoices.get(i);
									logger.warn("gstin"+invoices);
									Serializable saveDoc = MAXGSTINUtility.saveDocument(outputData, invoice.getInvoiceRequest(), tokenId);
									logger.warn("Seriaalisavle "+ saveDoc);

									HashMap updateInvoice = new HashMap();
									if(saveDoc != null && saveDoc instanceof MAXDocumentResp) {
										referenceID = ((MAXDocumentResp) saveDoc).getReferenceId();
										invoice.setInvRefID(referenceID);
										
										invoice.setInvoiceStatus("1");
										updateInvoice.put(MAXCodeConstantsIfc.STORE_ID, storeId);
										updateInvoice.put(MAXCodeConstantsIfc.GSTIN_AUTOMATION, 3);
										updateInvoice.put(MAXCodeConstantsIfc.GSTIN_INVOICE, invoice);
										gstinTransaction.updateGstinDetails(updateInvoice);
										savedinvoices.add(invoice);
									}else {
										if(saveDoc != null && saveDoc instanceof MAXDocumentErrorResp) {
											ArrayList<MAXErrorResp> errorList = ((MAXDocumentErrorResp) saveDoc).getErrors();
											String error = "";
											for(int j = 0; j<errorList.size(); j++) {
												error = error.concat(errorList.get(j).getMessage());

											}
											logger.error(error);
										}else {
											logger.error("###################### Unable to connect cygnet api ###################");
										}				
									}
								}
								Thread.sleep(10000);// wait 10 second here

								for(int i=0; i<savedinvoices.size(); i++) {

									HashMap updateInvoice = new HashMap();
									GSTINInvoiceIfc invoice = savedinvoices.get(i);
									logger.error("invoice.getTxnID() "+invoice.getInvRefID());
									Serializable docStatus = MAXGSTINUtility.getStatus(outputData, invoice.getInvRefID(), tokenId);
									if(docStatus != null && docStatus instanceof MAXDocumentStatusResp) {
										logger.error("instanceof");
										
										String documentId = ((MAXDocumentStatusResp)docStatus).getDataReport().get(0).getDocumentNumber();
										MAXEInvoiceResp eInv = ((MAXDocumentStatusResp)docStatus).getDataReport().get(0).geteInv();
										logger.error("invoicestatus2");
										invoice.setInvoiceStatus("2");
										updateInvoice.put(MAXCodeConstantsIfc.STORE_ID, storeId);
										updateInvoice.put(MAXCodeConstantsIfc.GSTIN_AUTOMATION, 4);
										updateInvoice.put(MAXCodeConstantsIfc.GSTIN_EINV, eInv);
										updateInvoice.put(MAXCodeConstantsIfc.GSTIN_DOC_ID, documentId);
										updateInvoice.put(MAXCodeConstantsIfc.GSTIN_INVOICE, invoice);
										// update invoice success status here
										gstinTransaction.updateGstinDetails(updateInvoice);
										invoice.setSignedQRCode(eInv.getSignedInvoice());
										invoice.setIrn(eInv.getIrn());
										savePdf(invoice, outputData);
										logger.warn("outputdata");
									}else {
										if(docStatus != null && (docStatus instanceof MAXDocumentStatusErrorResp || docStatus instanceof MAXDocumentErrorStatusResp)) {
											String docError = "";
											if(docStatus instanceof MAXDocumentStatusErrorResp ) {
												ArrayList<MAXValidationReport> validationList = ((MAXDocumentStatusErrorResp)docStatus).getValidationReport();

												for(int x = 0; x<validationList.size(); x++) {
													MAXValidationReport propertyErrors = validationList.get(x);
													for(int j = 0; j<propertyErrors.getPropertyErrors().size() ; j++) {
														MAXPropertyErrorsResp propertyError = propertyErrors.getPropertyErrors().get(j);
														for(int k = 0; k<propertyError.getErrors().size(); k++) {
															String error = propertyError.getErrors().get(k).getMessage();
															docError = docError.concat(" ").concat(error);
														}
													}
												}

												if(((MAXDocumentStatusErrorResp)docStatus).getStatus().equals("IP") || ((MAXDocumentStatusErrorResp)docStatus).getStatus().equals("YNS")) {
													logger.error("Document saving under process..."+docError);
												}else if(((MAXDocumentStatusErrorResp)docStatus).getStatus().equals("PE") || ((MAXDocumentStatusErrorResp)docStatus).getStatus().equals("ER")){
													// update invoice error
													//invoice = storeOpsManager.updateInvoiceError(businessDate, txnID, docError.toString());
													invoice.setInvoiceStatus("3");	
													updateInvoice.put(MAXCodeConstantsIfc.STORE_ID, storeId);
													updateInvoice.put(MAXCodeConstantsIfc.GSTIN_AUTOMATION, 5);
													updateInvoice.put(MAXCodeConstantsIfc.GSTIN_DOC_ERROR, docError.toString());
													updateInvoice.put(MAXCodeConstantsIfc.GSTIN_INVOICE, invoice);
													gstinTransaction.updateInvoiceError(updateInvoice);
													logger.warn("updateinvoice");
													logger.error("Document saving Error..."+docError);
												}else {
													logger.error("Document saving unknow error..."+docError);
												}
											}else {






												ArrayList<MAXDataErrorReport> validationList = ((MAXDocumentErrorStatusResp)docStatus).getDataReport();

												for(int x = 0; x<validationList.size(); x++) {
													MAXDataErrorReport propertyErrors = validationList.get(x);
													MAXEInvoiceErrorResp eInv = propertyErrors.geteInv();
													for(int j = 0; j<eInv.getErrors().size() ; j++) {
														MAXErrorResp propertyError = eInv.getErrors().get(j);
														String error = propertyError.getMessage();
														docError = docError.concat(" ").concat(error);

													}
												}

												if(((MAXDocumentErrorStatusResp)docStatus).getStatus().equals("IP") || ((MAXDocumentErrorStatusResp)docStatus).getStatus().equals("YNS")) {
													logger.error("Document saving under process..." + docError);
												}else if(((MAXDocumentErrorStatusResp)docStatus).getStatus().equals("PE") || ((MAXDocumentErrorStatusResp)docStatus).getStatus().equals("ER")){
													invoice.setInvoiceStatus("3");	
													updateInvoice.put(MAXCodeConstantsIfc.STORE_ID, storeId);
													updateInvoice.put(MAXCodeConstantsIfc.GSTIN_AUTOMATION, 5);
													updateInvoice.put(MAXCodeConstantsIfc.GSTIN_DOC_ERROR, docError.toString());
													updateInvoice.put(MAXCodeConstantsIfc.GSTIN_INVOICE, invoice);
													gstinTransaction.updateInvoiceError(updateInvoice);
													logger.error("Document saving Error..."+docError);										
												}else {
													logger.error("please try after some time..."+docError);
												}





											}

										}else {
											logger.error("Unable to connect cygnet api");
										}

									}


								}
								Thread.sleep(10000);	// wait 10 second here
								HashMap failedInvoice = new HashMap();
								
								failedInvoice.put(MAXCodeConstantsIfc.STORE_ID, storeId);
								failedInvoice.put(MAXCodeConstantsIfc.GSTIN_AUTOMATION, 6);
								failedInvoice.put(MAXCodeConstantsIfc.GSTIN_TTL_DAYS, outputData.get(MAXGSTINConstantsIfc.GSTIN_DAYS));
								failedInvoice.put(MAXCodeConstantsIfc.GSTIN_TTL_TXN, outputData.get(MAXGSTINConstantsIfc.GSTIN_TXN_TTL));

								ArrayList<GSTINInvoiceIfc> failedinvoices = new ArrayList<GSTINInvoiceIfc>(); ///////  get invoice 
								invoices = gstinTransaction.getFailedInvoiceData(failedInvoice);
								for(int i=0; i<invoices.size(); i++) {
									GSTINInvoiceIfc invoice = invoices.get(i);
									//Serializable saveDoc = MAXGSTINUtility.checkInvoiceStatus(outputData, invoice.getInvoiceRequest(), tokenId);
									Serializable saveDoc = MAXGSTINUtility.getStatus(outputData, invoice.getInvoiceRequest(), tokenId);
									
									HashMap updateInvoice = new HashMap();
									if(saveDoc != null && saveDoc instanceof MAXDataReport) {

										String documentId = ((MAXDataReport) saveDoc).getDocumentNumber();
										MAXEInvoiceResp eInv = ((MAXDataReport) saveDoc).geteInv();
										invoice.setInvoiceStatus("2");
										updateInvoice.put(MAXCodeConstantsIfc.STORE_ID, storeId);
										updateInvoice.put(MAXCodeConstantsIfc.GSTIN_AUTOMATION, 4);
										updateInvoice.put(MAXCodeConstantsIfc.GSTIN_EINV, eInv);
										updateInvoice.put(MAXCodeConstantsIfc.GSTIN_DOC_ID, documentId);
										updateInvoice.put(MAXCodeConstantsIfc.GSTIN_INVOICE, invoice);
										// update invoice success status here
										gstinTransaction.updateGstinDetails(updateInvoice);
										invoice.setSignedQRCode(eInv.getSignedInvoice());
										invoice.setIrn(eInv.getIrn());
										savePdf(invoice, outputData);
									}else {
										if(saveDoc != null && saveDoc instanceof MAXDocumentErrorResp) {
											ArrayList<MAXErrorResp> errorList = ((MAXDocumentErrorResp) saveDoc).getErrors();
											String error = "";
											for(int j = 0; j<errorList.size(); j++) {
												error = error.concat(errorList.get(j).getMessage());

											}
											logger.error(error);
										}else {
											logger.error("###################### Unable to connect cygnet api ###################");
										}				
									}
								}

							}else {
								logger.error("###################### Invalid GSTIN Token id ###################");
							}
						}

					}catch (Exception e) {
						logger.warn("EXCEPTION"+ e);
						e.printStackTrace();
						//logger.error(e.printStackTrace());
						logger.error(e);
					}
               }else {
					if(resp != null &&  resp instanceof  MAXDocumentErrorResp && ((MAXDocumentErrorResp)resp).getErrors().size() >0) {
						String error = ((MAXDocumentErrorResp)resp).getErrors().get(0).getMessage();
						logger.info("Unable to generate token :::::::::::::::"+error);
					}else {
						logger.info("Unable to connect cygnet api");
					}
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}



	}
	private void savePdf(GSTINInvoiceIfc invoice, HashMap map) {

		String strReceiptDirectory = (String) map.get(MAXGSTINConstantsIfc.RTF_LOCATION);
		String gstInvoiceDirectory = (String) map.get(MAXGSTINConstantsIfc.PDF_LOCATION); 
		String txnNo = invoice.getTxnID();
		while(txnNo.length()<7) {
			txnNo = "0".concat(txnNo);
		}
		String txnID = invoice.getStoreID().concat(invoice.getRegID()).concat(txnNo);
		String folderDate=convertDateFolder(invoice.getBusinessDate());
		String eReceiptDirectory=strReceiptDirectory.concat(
				(folderDate).concat("\\").concat((txnID.substring(0, 5)).concat("\\").concat((txnID.substring(5, 8)).concat("\\"))));
		String receiptPdf = getReceiptPdf(eReceiptDirectory+txnID+folderDate+".rtf");
		if(receiptPdf !=null && !receiptPdf.equals("") && !receiptPdf.equals("FileNotFound")) {


			//save pdf
			//	String directory = "C:/ReceiptCopyServer_bkp";
			String receiptNo = txnID+folderDate;
			//	if (Files.exists(Paths.get(gstInvoiceDirectory))) {
			if (receiptNo != null && receiptNo.length() > 16) {

				gstInvoiceDirectory = gstInvoiceDirectory.concat((receiptNo.substring(15)).concat("/").concat((receiptNo.substring(0, 5)).concat("/")
						.concat((receiptNo.substring(5, 8)).concat("/"))));
				File theDir = new File(gstInvoiceDirectory);
				if (Files.notExists(Paths.get(gstInvoiceDirectory))) {
					try {
						theDir.mkdirs();
					} catch (SecurityException se) {
						// handle it
						logger.error("invoice number: "+txnID+"   "+"Security Exception during creating directory" + se);
					}
				}
			}
			//} 

			String receiptSaveDirectory = (gstInvoiceDirectory).concat(receiptNo).concat(".pdf");

			File file = new File(receiptSaveDirectory);
			Path pdfPath = Paths.get(receiptSaveDirectory);
			byte[] documentInBytes = null;
			if(file.exists()) {
				try {
					documentInBytes = Files.readAllBytes(pdfPath);
				} catch (IOException ex) {
					logger.info(">>>>>   Gstin E-Invoice pdf already generated");

				}			
			}else {
				//ByteArrayOutputStream baos = new ByteArrayOutputStream();
				Document document = new Document(PageSize.A4, 0f, 0f, 0f, 0f);
				try
				{
					PdfWriter writer =  PdfWriter.getInstance(document, new FileOutputStream(receiptSaveDirectory));
					generatePdf(txnID , document, writer, receiptPdf, invoice);
					document.close();
					writer.close();
					documentInBytes = Files.readAllBytes(pdfPath);
				} catch (Exception e)
				{
					logger.error(">>>>>  Unable to save gstin invoice", e);
				}
			}
		}else {
			logger.error("Receipt rtf file not found please try after some time");
		}

	}

	public static String convertDateFolder( String stringData)  {

		String businessDate = null;

		Date initDate;
		try {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			df.setLenient(false);
			initDate = df.parse(stringData);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			businessDate = formatter.format(initDate);
		} catch (Exception e) {
			logger.error(e);
		}

		return businessDate;

	}
	private String getReceiptPdf(String string) {


		StringBuffer sb = new StringBuffer("");
		BufferedReader br = null;
		try {
			File f = new File(string);
			InputStream is = new FileInputStream(f);
			br = new BufferedReader(new InputStreamReader(is));
			String value = "";
			while((value = br.readLine()) != null) {
				//char c = (char)value;
				while(value.length()<42){
					value=value.concat(" ");
				}
				sb.append(value+System.lineSeparator());
				//sb.append(value.replaceFirst("\\s++$", "")+System.lineSeparator());
			}
			is.close();
		}
		catch (FileNotFoundException e) {
			sb.append("FileNotFound");
		}
		catch (IOException e) {
			logger.info("rtf can not converted in string........" +e);
		}
		catch (Exception e) {
			logger.info("rtf can not converted in string........" +e);
		}

		return sb.toString();
	}
	private boolean generatePdf(String rtfNo, Document document, PdfWriter writer, String rtf, GSTINInvoiceIfc invoice) {
		boolean receiptStatus = false;
		float fntSize, lineSpacing;
		fntSize = 10f;
		lineSpacing = 10f;
		try {
			document.setMargins(20, 20, 100, 100);
			document.open();
			//PdfHeaderEvent event = new PdfHeaderEvent(status.getConceptName());
			//writer.setPageEvent(event);

			String[] words = rtf.split("\r\n");
			boolean qrcodeFlag = false;
			//	boolean logoPrint = false;
			//HashMap<String, MAXEReceiptConceptDetalsIfc> eReceiptDetails = EReceiptConceptDetails.getEReceiptInstance().getReceiptMap();
			//MAXEReceiptConceptDetalsIfc eReceiptConceptDetal= eReceiptDetails.get(CodeConstantsIfc.sms+"="+status.getConceptName());
			for (String word : words) {
				if(word.contains("ITEM/HSN/Desc")) {
					qrcodeFlag = true;
				}
				
				if (word.contains("Barcode")) {
					String barCode = word.replaceAll("Barcode", "").trim();
					logger.warn("BARCODE");
					PdfContentByte pdfContentByte = writer.getDirectContent();
					Barcode128 barcode128 = new Barcode128();
					barcode128.setCode(barCode);
					barcode128.setCodeType(Barcode128.CODE128);
					Image code128Image = barcode128.createImageWithBarcode(pdfContentByte, null, null);
					/*
					 * code128Image.setAbsolutePosition(10, 700); code128Image.scalePercent(100);
					 */
					code128Image.setAlignment(Element.ALIGN_CENTER);
					document.add(code128Image);
					if(qrcodeFlag) {
						if(invoice.getIrn() != null) {
							String irnNo = "IRN : "+invoice.getIrn();
							while(irnNo.length() >= 40) {
								Paragraph pars = new Paragraph(new Phrase(lineSpacing, irnNo.substring(0, 40),
										FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD, new CMYKColor(0, 0, 0, 255))));
								pars.setAlignment(Element.ALIGN_CENTER);
								document.add(pars);
								irnNo = irnNo.substring(40);
							}
							if(irnNo.length() < 40) {
								while(irnNo.length() != 40) {
									irnNo = irnNo.concat(" ");
								}
								Paragraph pars = new Paragraph(new Phrase(lineSpacing, irnNo,
										FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD, new CMYKColor(0, 0, 0, 255))));
								pars.setAlignment(Element.ALIGN_CENTER);
								document.add(pars);
								//irnNo = irnNo.substring(40);
							}
						}
						if(invoice.getSignedQRCode()!= null) {
							BarcodeQRCode barcodeQRCode = new BarcodeQRCode(invoice.getSignedQRCode(), 1000, 1000, null);
							Image codeQrImage = barcodeQRCode.getImage();
							codeQrImage.scaleAbsolute(100, 100);
							codeQrImage.setAlignment(Element.ALIGN_CENTER);
							document.add(codeQrImage);
						}
						qrcodeFlag = false;
					}
				} else {
					/*Paragraph pars = new Paragraph(new Phrase(lineSpacing, word,
							FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD, new CMYKColor(255, 0, 0, 0))));*/
					Paragraph pars = new Paragraph(new Phrase(lineSpacing, word,
							FontFactory.getFont(FontFactory.COURIER, fntSize, Font.BOLD, new CMYKColor(0, 0, 0, 255))));
					pars.setAlignment(Element.ALIGN_CENTER);
					document.add(pars);
				}
			}
			/*
			 * document.close(); writer.close();
			 */
			receiptStatus = true;
		} catch (Exception e) {
			logger.error("invoice number: "+rtfNo+"   "+"Could not save receipt, please check concept name and logo image name " + e);
		}
		return receiptStatus;
	}
}
