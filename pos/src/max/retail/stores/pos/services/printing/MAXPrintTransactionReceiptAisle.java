/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	Rev 1.8		Apr 21, 2022		   Kajal Nautiyal	 	Receiept Changes to print receipt
 *  Rev 1.7		Nov 28, 2018		    Purushotham Reddy 	Receiept Changes
 *  Rev 1.6		May 04, 2017		    Kritica Agarwal 	GST Changes
 *	Rev 1.5		Apr 26, 2017			Mansi Goel			Changes to resolve discount amount is coming as 0.00 in second item
 *															of send transaction for source based rules 
 *  Rev 1.4     Mar 29,	2017            Hitesh.dua			changes for hire purchase auto cut
 *  Rev 1.3     feb 14,	2017            Hitesh.dua			merge changes with base 14
 *  Rev 1.2     Jan 31,	2017            Hitesh.dua			Bug_fix:unexpected error is coming while printing layaway payment.
 *  Rev 1.1     Jan 10,	2017            Hitesh.dua			Changes for max receipt changes and remove unnecessary code of max12
 *  Rev 1.0     Nov 17, 2016	        Ashish Yadav		Changes for Employee Discount FES
 *
 ********************************************************************************/
package max.retail.stores.pos.services.printing;

import static oracle.retail.stores.foundation.tour.gate.Gateway.APPLICATION_PROPERTIES_GROUP;
import static oracle.retail.stores.foundation.tour.gate.Gateway.getBooleanProperty;
import static oracle.retail.stores.foundation.tour.gate.Gateway.getProperty;
import static oracle.retail.stores.pos.receipt.ReceiptConstantsIfc.ALTERATION_FILE_NAME_ADDITION;
import static oracle.retail.stores.pos.receipt.ReceiptConstantsIfc.ERECEIPT_FILENAME_DEFAULT;
import static oracle.retail.stores.pos.receipt.ReceiptConstantsIfc.GIFT_FILE_NAME_ADDITION;
import static oracle.retail.stores.pos.receipt.ReceiptConstantsIfc.REBATE_FILE_NAME_ADDITION;
import static oracle.retail.stores.pos.receipt.ReceiptConstantsIfc.SALE_RETURN_FILE_NAME_ADDITION;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
// foundation imports
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.domain.transaction.MAXTransactionTotals;
import max.retail.stores.domain.transaction.MAXTransactionTotalsIfc;
import max.retail.stores.pos.receipt.MAXReceiptParameterBeanIfc;
import max.retail.stores.pos.receipt.MAXReceiptTypeConstantsIfc;
import max.retail.stores.pos.services.inquiry.iteminquiry.itemvalidate.MAXGetItemInLocalOrWebStoreSite;
import oracle.retail.stores.common.constants.ItemLevelMessageConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.stock.MessageDTO;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ReceiptFooterMessageDTO;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCashIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.transaction.VoidTransaction;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.device.LocalizedDeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.AlterationReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.GiftReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManager;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.ReceiptTypeConstantsIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.printing.PrintingCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import oracle.retail.stores.utility.EmailInfo;
import oracle.retail.stores.utility.SendEmail;

//------------------------------------------------------------------------------
/**
 * Print the receipt.
 * 
 * @version $Revision: 5$
 **/
// ------------------------------------------------------------------------------
public class MAXPrintTransactionReceiptAisle extends PosLaneActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4461512749127491922L;
	public static final String NumberReturnReceipts = "NumberReturnReceipts";
	boolean hirePurchase = false;
	boolean sendReceipt = false;
	protected TenderLineItemIfc[] tenders;

	// --------------------------------------------------------------------------
	/**
	 * Print the receipt and send a letter
	 * 
	 * @param bus
	 *            the bus traversing this lane
	 **/
	// --------------------------------------------------------------------------
	synchronized public void traverse(BusIfc bus) {
		// get transaction from cargo
		PrintingCargo cargo = (PrintingCargo) bus.getCargo();
		TenderableTransactionIfc trans = cargo.getTransaction();
		if(cargo.getTransaction() instanceof SaleReturnTransaction){
			trans.setDuplicateReceipt(cargo.isDuplicateReceipt());
		}
		// Changes for Rev 1.5 : Starts
		if (trans instanceof MAXSaleReturnTransactionIfc) {
			Vector<SendPackageLineItemIfc> sendPackages = ((MAXSaleReturnTransactionIfc) trans)
					.getSendPackageVector();
			if (sendPackages.size() > 0) {
				TransactionTotalsIfc totals = (TransactionTotalsIfc) ((MAXSaleReturnTransactionIfc) trans)
						.getTransactionTotals();
				for (int i = 0; i < sendPackages.size(); i++)
					((MAXTransactionTotals) totals).addSendPackage(sendPackages.get(i));
							
			}
		}

		// Changes for Rev 1.5 : Ends
		trans.setTransactionAttributes(cargo.getTransaction());
		List<ReceiptFooterMessageDTO> returnedFooterMessages = null; // Grouped
																		// Return
																		// Messages
																		// from
																		// Returns
																		// Management
		List<ReceiptFooterMessageDTO> itemLevelReceiptFooterMessages = null; // ILRM
																				// Footer
																				// Messages
		ReceiptFooterMessageDTO[] receiptFooterMessages = null; // Group the
																// above 2 into
																// 1 array

		// Create the object of customer.
		CustomerIfc cust = trans.getCustomer();
		Locale locale = null;
		// If customer is already selected, object of Locale should be stored in
		// locale reference
		if (cust != null) {
			locale = cust.getPreferredLocale();
		}
		// If locale is not null, put the object into the LocaleMap.
		if (locale != null) {
			LocaleMap.putLocale(LocaleConstantsIfc.RECEIPT, locale);
			UIUtilities.setUILocaleForCustomer(locale);
		}

		// initialize variables
		boolean duplicateReceipt = cargo.isDuplicateReceipt();
		boolean sendMail = true;
		cargo.setReceiptPrinted(false);
		boolean mailServerOffline = false;
		boolean groupLikeFooterMessages = !(getBooleanProperty("domain",
				"DuplicateReceiptFooterMessages", false));

		String defaultFileAddition = getProperty(APPLICATION_PROPERTIES_GROUP,
				ERECEIPT_FILENAME_DEFAULT, SALE_RETURN_FILE_NAME_ADDITION);
		if (trans instanceof SaleReturnTransactionIfc) {
			SaleReturnTransactionIfc saleReturnTransactionIfc = (SaleReturnTransactionIfc) trans;
			checkforDuplicateLineItems((SaleReturnLineItemIfc[]) saleReturnTransactionIfc.getLineItems());
			itemLevelReceiptFooterMessages = groupLikeFooterMessages((SaleReturnLineItemIfc[]) saleReturnTransactionIfc.getLineItems(),groupLikeFooterMessages, locale);
			returnedFooterMessages = groupDuplicateReturnMessages(trans,groupLikeFooterMessages);
			returnedFooterMessages.addAll(itemLevelReceiptFooterMessages);
			receiptFooterMessages = new ReceiptFooterMessageDTO[returnedFooterMessages
					.size()];
			receiptFooterMessages = returnedFooterMessages
					.toArray(receiptFooterMessages);
		}
		// get ui and utility manager
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		UtilityManagerIfc utility = (UtilityManagerIfc) bus
				.getManager(UtilityManagerIfc.TYPE);
		PrintableDocumentManagerIfc printManager = (PrintableDocumentManagerIfc) bus
				.getManager(PrintableDocumentManagerIfc.TYPE);

		try {
			ReceiptParameterBeanIfc parameters = printManager
					.getReceiptParameterBeanInstance((SessionBusIfc) bus, trans);
			if (trans.getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_COMPLETE
					|| trans.getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_PARTIAL) {
				OrderTransactionIfc orderTransaction = (OrderTransactionIfc) trans;
				if (orderTransaction.getOrderRecipient() != null
						&& orderTransaction.getOrderRecipient()
								.getCustomerSignature() != null) {
					// To avoid customers sign their signature on paper
					parameters.setSignatureCaptureImage(true);
				}
			}
			if (receiptFooterMessages != null
					&& receiptFooterMessages.length > 0) {
				parameters.setReturnReceiptFooterMsgs(receiptFooterMessages);
			}
			parameters.setDuplicateReceipt(duplicateReceipt);
			
			
			// set only if Type 2
			if (PrintableDocumentManagerIfc.STYLE_VAT_TYPE_2
					.equalsIgnoreCase(cargo.getReceiptStyle())) {
				parameters.setReceiptStyle(cargo.getReceiptStyle());
			}
			// reset cargo receipt style
			cargo.setReceiptStyle(PrintableDocumentManagerIfc.STYLE_NORMAL);
			// changes for rev 1.2 start
			if (cargo.getTransaction() instanceof SaleReturnTransaction
					&& (((MAXTransactionTotalsIfc) cargo.getTransaction()
							.getTransactionTotals()).getItemSendPackagesCount() > 0 || ((SaleReturnTransaction) cargo
							.getTransaction()).getItemSendPackagesCount() > 0)) {
				parameters.setTransactionHasSendItem(true);
				sendReceipt = true;
			}
			// changes for rev 1.4
			if (cargo.getTransaction().containsTenderLineItems(
					TenderLineItemIfc.TENDER_TYPE_PURCHASE_ORDER)) {
				if (!(cargo.getTransaction() instanceof VoidTransaction))
					parameters
							.setDocumentType(MAXReceiptTypeConstantsIfc.HIRE_PURCHASE);
				hirePurchase = true;
			}

			/** Changes by shruti **/

			// below code is added by atul shukla for employee discount company
			// Name
			MAXSaleReturnTransaction maxSaleTrx = null;
			MAXReceiptParameterBeanIfc receiptparameter = null;
			String companyName = null;
			if (trans instanceof MAXSaleReturnTransaction) {
				try {
					maxSaleTrx = (MAXSaleReturnTransaction) trans;
					if (maxSaleTrx.getEmployeeCompanyName() != null) {
						companyName = maxSaleTrx.getEmployeeCompanyName()
								.trim().toString();
					}
				} catch (NullPointerException ne) {
					ne.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (parameters instanceof MAXReceiptParameterBeanIfc) {
				receiptparameter = (MAXReceiptParameterBeanIfc) parameters;
			}
			
			
			receiptparameter.setEmployeeCompanyName(companyName);
			// atul's changes end here
			// If SaleReturnTransaction and
			// if the is a return ticket (i.e. used returns management) and
			// if there are no line items...
			// changes Start by Bhanu priya for PAN Changes

			if (trans instanceof MAXSaleReturnTransaction) {

				try {
					String panno = ((MAXSaleReturnTransaction) trans)
							.getPanNumber();
					String formuid = ((MAXSaleReturnTransaction) trans)
							.getForm60IDNumber();
					String passportnum = ((MAXSaleReturnTransaction) trans)
							.getPassportNumber();
					if (panno != null || formuid != null || passportnum != null) {
						String maskedstring = null;
						if (parameters instanceof MAXReceiptParameterBeanIfc) {
							receiptparameter = (MAXReceiptParameterBeanIfc) parameters;
						};
						/*
						 * String substrng=panno.substring(0, 5); String
						 * substrng1=substrng.replaceAll(substrng, "*"); String
						 * maskedPanno=substrng1.concat(panno.substring(6, 9));
						 */
						if (panno != null && panno != "") {
							EncipheredDataIfc panData = FoundationObjectFactory
									.getFactory().createEncipheredDataInstance(
											panno.getBytes());
							maskedstring = panData.getMaskedNumber().toString();
						}
						receiptparameter.setPanCardNumber(maskedstring);
						receiptparameter.setForm60Number(formuid);
						receiptparameter.setPassportrdNumber(passportnum);
						///for gst printing
					//	System.out.println( "Test print transaction ailse : "+((MAXSaleReturnTransaction) trans).getGSTINNumber());
					//	System.out.println( "Test print transaction ailse12 : "+((MAXSaleReturnTransaction) trans).getGstinresp());
						receiptparameter.setTransaction(trans);
					}
				} catch (NullPointerException ne) {
					ne.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			// changes End by Bhanu priya for PAN Changes

			if (trans instanceof SaleReturnTransactionIfc
					&& trans.getReturnTicket() != null
					&& ((SaleReturnTransactionIfc) trans).getLineItemsSize() == 0) {
				// The returns denied document
				parameters.setDocumentType(ReceiptTypeConstantsIfc.RETURNS_DENIED);
			}
			
			// Changes for  E-Receipt Integration With Karnival

			if (trans instanceof MAXSaleReturnTransaction && parameters.isDuplicateReceipt()
					&& parameters.getTransaction().getTransactionType() == TransactionIfc.TYPE_SALE ) {
				if (parameters instanceof MAXReceiptParameterBeanIfc) {
					receiptparameter = (MAXReceiptParameterBeanIfc) parameters;
					receiptparameter
							.setEReceiptOTPNumber(((MAXSaleReturnTransaction) trans).getEReceiptOTP());
				}

			}
			
			// Initialize this

			// Print eReceipt; this will be true only for Sale Return
			// Transactions
			if (cargo.isPrintEreceipt()) {
				int transactionType = parameters.getTransaction()
						.getTransactionType();
				String eReceiptFileAddition = getProperty(
						APPLICATION_PROPERTIES_GROUP,
						"EReceipt.FileNameAddition." + transactionType,
						defaultFileAddition);
				parameters.setEReceiptFileNameAddition(eReceiptFileAddition);
				try {
					// Set flag to print eReceipt
					parameters.setEreceipt(true);
					// Print Receipt
					ArrayList<String> fileAdditions = printAllReciepts(
							(SessionBusIfc) bus, parameters);
					// Email the printed receipt
					emailEreceipt(bus, fileAdditions);
				} catch (Exception e) {
					logger.warn("Unable to send eReceipt:", e);
					// set mail server offline.
					mailServerOffline = true;
					// set to print paper copy
					cargo.setPrintPaperReceipt(true);
				}

				// Reset the ereceipt flag.
				parameters.setEreceipt(false);
			}

			// Print paper receipt
			if (cargo.isPrintPaperReceipt()) {
				// reset PrintStoreReceipt flag to print all receipts.
				parameters.setPrintStoreReceipt(false);
				// Print Receipt
				// This value is always true when running on ORPOS. On MPOS, the
				// value can be true or false
				if (cargo.isPrintCustomerCopy()) {
					printAllReciepts((SessionBusIfc) bus, parameters);
				}
			} 
			else if (parameters.isPrintStoreReceipt()){

				if (this.tenders == null) {
					if (trans.getTransactionType() == 25) {
						this.tenders = ((TenderableTransactionIfc) trans).getTenderLineItems();
					} else {
						TenderCashIfc cash = null;
						final Vector<TenderLineItemIfc> tenderLineItemsVector = (Vector<TenderLineItemIfc>) ((TenderableTransactionIfc) trans).getTenderLineItemsVector();
						final List<TenderLineItemIfc> list = new ArrayList<TenderLineItemIfc>(tenderLineItemsVector.size());
						for (TenderLineItemIfc tender : tenderLineItemsVector) {
							if (tender.isCollected()) {
								if (tender instanceof TenderCashIfc && tender.getAmountTender().signum() > -1
										&& tender instanceof TenderAlternateCurrencyIfc
										&& ((TenderAlternateCurrencyIfc) tender).getAlternateCurrencyTendered() == null) {
									if (cash != null) {
										cash.setAmountTender(cash.getAmountTender().add(tender.getAmountTender()));
										continue;
									}
									tender = (TenderLineItemIfc) tender.clone();
									cash = (TenderCashIfc) tender;
								}
								list.add(tender);
							} else {
								if (trans.getTransactionType() != 22 || tender.isCollected()) {
									continue;
								}
								list.add(tender);
							}
						}
						this.tenders = list.toArray(new TenderLineItemIfc[list.size()]);
					}
				}
				//return this.tenders;
			
				printManager.printReceipt((SessionBusIfc) bus, parameters);
				System.out.println("Receipt Printed");
				// reset PrintStoreReceipt flag
				parameters.setPrintStoreReceipt(false);
				cargo.setReceiptPrinted(true);
			} else if (cargo.isPrintEreceipt() && !cargo.isPrintPaperReceipt()) {
				// still print pater store receipt even if selecting print
				// eReceipt only
				parameters.setPrintStoreReceipt(true);

				printManager.printReceipt((SessionBusIfc) bus, parameters);
				// reset PrintStoreReceipt flag
				parameters.setPrintStoreReceipt(false);
				cargo.setReceiptPrinted(true);
			}
			// printManager.printReceipt((SessionBusIfc)bus, parameters);
			cargo.setReceiptPrinted(true);

			// save the count of reprinted receipts for the reprintreceipt
			// service
			cargo.setReprintReceiptCount(cargo.getReprintReceiptCount() + 1);
			// Update printer status
			setPrinterStatus(true, bus);

			// display MailServerOffline mesasge if email server is down.
			if (mailServerOffline) {
				displayEmailServerOfflineDialog(ui);
				sendMail = false;
			}
		}
		// handle device exception
		catch (PrintableDocumentException e) {
			logger.warn("Unable to print receipt: " + e.getMessage());
			// Update printer status
			setPrinterStatus(false, bus);

			if (e.getNestedException() != null) {
				logger.warn("DeviceException.NestedException:\n"
						+ Util.throwableToString(e.getNestedException()));
			}

			String msg[] = new String[1];

			if (e.getNestedException() instanceof LocalizedDeviceException) {
				msg[0] = e.getNestedException().getLocalizedMessage();
			} else if (e.getNestedException() instanceof DeviceException
					&& ((DeviceException) e.getNestedException())
							.getErrorCode() != DeviceException.UNKNOWN) {
				msg[0] = utility.retrieveDialogText(
						"RetryContinue.PrinterOffline", "Printer is offline.");
			} else {
				msg[0] = utility.retrieveDialogText(
						"RetryContinue.UnknownPrintingError",
						"An unknown error occurred while printing.");
			}

			DialogBeanModel model = new DialogBeanModel();
			model.setResourceID("RetryContinue");
			model.setType(DialogScreensIfc.RETRY_CONTINUE);
			model.setArgs(msg);
			// display dialog
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

			sendMail = false;
		}
		// parameter exception handled in utility manager
		catch (ParameterException pe) {
			logger.error("A receipt parameter could not be retrieved from the ParameterManager.  "
					+ "The following exception occurred: " + pe.getMessage());
		}
		
	////move temp file to transaction folder
		if (trans instanceof MAXSaleReturnTransactionIfc) {
			logger.info("write receipt to rtf...");
			moveTempFileToTransactionFolder((MAXSaleReturnTransactionIfc)trans);
		}

		if (sendMail) {
			bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
		}
		
	//	MAXGetItemInLocalOrWebStoreSite.barCodeNo =null;
			
		
	}	
	private void moveTempFileToTransactionFolder(MAXSaleReturnTransactionIfc invoice )  {
		try {
			invoice.setReceiptData(FileUtils.readFileToByteArray(new File("temp.rtf")));
			File file = new File("temp.rtf");
			file.delete();//as work is done
			/*
			 * String txnID =
			 * invoice.getWorkstation().getStoreID().concat(invoice.getWorkstation().
			 * getWorkstationID()).concat(String.valueOf(invoice.
			 * getTransactionSequenceNumber())); String
			 * folderDate=convertDateFolder(invoice.getBusinessDay().asISODate()); String
			 * eReceiptDirectory="D:\\ReceiptCopyServer\\".concat(
			 * (folderDate).concat("\\").concat((txnID.substring(0, 5)).concat("
			 * \\").concat((txnID.substring(5, 8)).concat("\\"))));
			 * System.out.println(eReceiptDirectory+txnID+folderDate+".rtf"); new
			 * File("temp.rtf").renameTo(new
			 * File(eReceiptDirectory+txnID+folderDate+".rtf"));
			 */
			logger.error("invoice.getReceiptData() "+ invoice.getReceiptData());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
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

	public TenderLineItemIfc[] getTenders2(TenderableTransactionIfc trans) {
		if (this.tenders == null) {
			if (trans.getTransactionType() == 25) {
				this.tenders = ((TenderableTransactionIfc) trans).getTenderLineItems();
			} else {
				TenderCashIfc cash = null;
				final Vector<TenderLineItemIfc> tenderLineItemsVector = (Vector<TenderLineItemIfc>) ((TenderableTransactionIfc) trans).getTenderLineItemsVector();
				final List<TenderLineItemIfc> list = new ArrayList<TenderLineItemIfc>(tenderLineItemsVector.size());
				for (TenderLineItemIfc tender : tenderLineItemsVector) {
					if (tender.isCollected()) {
						if (tender instanceof TenderCashIfc && tender.getAmountTender().signum() > -1
								&& tender instanceof TenderAlternateCurrencyIfc
								&& ((TenderAlternateCurrencyIfc) tender).getAlternateCurrencyTendered() == null) {
							if (cash != null) {
								cash.setAmountTender(cash.getAmountTender().add(tender.getAmountTender()));
								continue;
							}
							tender = (TenderLineItemIfc) tender.clone();
							cash = (TenderCashIfc) tender;
						}
						list.add(tender);
					} else {
						if (trans.getTransactionType() != 22 || tender.isCollected()) {
							continue;
						}
						list.add(tender);
					}
				}
				this.tenders = list.toArray(new TenderLineItemIfc[list.size()]);
			}
		}
		return this.tenders;
	}

	/**
	 * Print all the reciepts that can accompany a sale or return.
	 * 
	 * @param bus
	 * @param parameters
	 * @return the list of additons to file name. This list only matters to
	 *         email reciept code; each different type of
	 * @throws PrintableDocumentException
	 */
	protected ArrayList<String> printAllReciepts(SessionBusIfc bus,
			ReceiptParameterBeanIfc parameters)
			throws PrintableDocumentException {
		ArrayList<String> fileAdditions = new ArrayList<String>();
		// Print the regular sale return reciept
		PrintingCargo cargo = (PrintingCargo) bus.getCargo();
		fileAdditions.add(parameters.getEReceiptFileNameAddition());
		PrintableDocumentManagerIfc printManager = (PrintableDocumentManagerIfc) bus
				.getManager(PrintableDocumentManagerIfc.TYPE);
		// Changes start (Ashish: Restrict receipts in tarining mode)
		boolean trainingModeFlag = ((ReceiptParameterBeanIfc) parameters)
				.getTransaction().isTrainingMode();
		// changes for rev 1.4 start
		if (!trainingModeFlag) {
			if (hirePurchase || sendReceipt) {
				int receiptCount = 2;
				
				for (int i = 1; i <= receiptCount; i++) {
					// Rev 1.7 Receipt Changes
					((MAXReceiptParameterBeanIfc) parameters).setCopyReportText("Store Copy");
					if (i == 2) {
						((MAXReceiptParameterBeanIfc) parameters).setCopyReportText("");
					}
					printManager.printReceipt(bus, parameters);
				}
				if (sendReceipt && parameters.hasSendPackages()) {
					parameters.setDocumentType(MAXReceiptTypeConstantsIfc.SHIPPING_SLIP);
					printManager.printReceipt(bus, parameters);
					sendReceipt = false;
				}
				hirePurchase = false;
			} else
				printManager.printReceipt(bus, parameters);
		}

		// changes for rev 1.2 end
		// Changes start (Ashish: Restrict receipts in tarining mode)
		cargo.setReceiptPrinted(true);

		if (parameters.isPrintGiftReceipt()) {
			ArrayList<String> additions = printGiftReceipts(bus, parameters);
			if (!additions.isEmpty()) {
				fileAdditions.addAll(additions);
			}
		}

		if ((parameters.getTransaction().getTransactionType() == TransactionIfc.TYPE_SALE
				|| parameters.getTransaction().getTransactionType() == TransactionIfc.TYPE_RETURN || parameters
				.getTransaction().getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
				&& (parameters.getTransaction().getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED && parameters
						.getTransaction().getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED_CANCELED)) {
			String addition = printRebateReceipts(bus, parameters);
			if (addition != null) {
				fileAdditions.add(addition);
			}
		}
		

		if (parameters.isPrintAlterationReceipt()) {
			String addition = printAlterationReceipts(bus, parameters);
			if (addition != null) {
				fileAdditions.add(addition);
			}
		}
		// Changes for Rev 1.8 : Starts
		if (parameters.getTransaction().getTransactionType() == TransactionIfc.TYPE_RETURN) {
			//int receiptCount=2;
			try {
			ParameterManagerIfc pm =(ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		
			  int parameterValue = Integer.parseInt(pm.getStringValue(NumberReturnReceipts));
			  
			  if(parameterValue==0 || pm.getStringValue(NumberReturnReceipts) .equals(null)) {
				  printManager.printReceipt(bus, parameters);
				  logger.info(" NumberReturnReceipts Perameter not exists in application .xml");
				  //System.out.println("NumberReturnReceipts Perameter not exists in application .xml");
			  }
			  else {
				  logger.info(" NumberReturnReceipts Perameter set to count"+parameterValue);
				//  System.out.println(" NumberReturnReceipts Perameter set to count"+parameterValue);
					
			for (int i = 1; i < parameterValue; i++) {
			
				((MAXReceiptParameterBeanIfc) parameters).setCopyReportText("Store Copy");
				if (i == 2) {
					((MAXReceiptParameterBeanIfc) parameters).setCopyReportText("");
				}
				printManager.printReceipt(bus, parameters);
			}
			// System.out.println("NumberReturnReceipts Perameter set to count at end"+parameterValue);
				
			}
			}
			catch(Exception e) {
				// printManager.printReceipt(bus, parameters);
				  logger.error(" NumberReturnReceipts Perameter not exists in application .xml");
				 // System.out.println("Exception NumberReturnReceipts Perameter not exists in application .xml");
			}
			
		}// Changes for Rev 1.8 : Ends
		return fileAdditions;
	}

	/**
	 * Print Alteration Receipts
	 * 
	 * @param bus
	 * @param parameters
	 * @return String additional file name text
	 * @throws PrintableDocumentException
	 */
	protected String printAlterationReceipts(SessionBusIfc bus,
			ReceiptParameterBeanIfc parameters)
			throws PrintableDocumentException {
		// Get the print mannger
		PrintableDocumentManagerIfc printManager = (PrintableDocumentManagerIfc) bus
				.getManager(PrintableDocumentManagerIfc.TYPE);

		// Get the aleration bean and initialize it.
		AlterationReceiptParameterBeanIfc altBean = (AlterationReceiptParameterBeanIfc) printManager
				.getParameterBeanInstance(ReceiptTypeConstantsIfc.ALTERATION);
		altBean.setLocale(parameters.getLocale());
		altBean.setDefaultLocale(parameters.getDefaultLocale());
		altBean.setTransaction(parameters.getTransaction());
		altBean.setEreceipt(parameters.isEreceipt());
		String eReceiptFileAddition = getProperty(APPLICATION_PROPERTIES_GROUP,
				"EReceipt.FileNameAddition.Alteration",
				ALTERATION_FILE_NAME_ADDITION);
		altBean.setEReceiptFileNameAddition(eReceiptFileAddition);

		// Print the receipt
		printManager.printReceipt(bus, altBean);
		return altBean.getEReceiptFileNameAddition();
	}

	/**
	 * Print Rebate Receipts
	 * 
	 * @param bus
	 * @param parameters
	 * @return String additional file name text
	 * @throws PrintableDocumentException
	 */
	protected String printRebateReceipts(SessionBusIfc bus,
			ReceiptParameterBeanIfc parameters)
			throws PrintableDocumentException {
		String addition = null;
		AbstractTransactionLineItemIfc[] li = parameters.getLineItems();
		for (int i = li.length - 1; i >= 0; i--) {
			if (li[i] instanceof SaleReturnLineItemIfc) {
				String rebateMessage = ((SaleReturnLineItemIfc) li[i])
						.getItemRebateMessage();
				if (!Util.isEmpty(rebateMessage)) {
					String eReceiptFileAddition = getProperty(
							APPLICATION_PROPERTIES_GROUP,
							"EReceipt.FileNameAddition.Rebate",
							REBATE_FILE_NAME_ADDITION);
					parameters
							.setEReceiptFileNameAddition(eReceiptFileAddition);
					addition = parameters.getEReceiptFileNameAddition();
					String documentType = parameters.getDocumentType();
					parameters.setDocumentType(ReceiptTypeConstantsIfc.REBATE);

					// Get the print mannger and print the rebates
					PrintableDocumentManagerIfc printManager = (PrintableDocumentManagerIfc) bus
							.getManager(PrintableDocumentManagerIfc.TYPE);

					// print a rebate for each item in the line
					for (int j = 0; j < li[i].getItemQuantityDecimal().abs()
							.intValue(); j++) {
						printManager.printReceipt(bus, parameters);
					}

					// Reset the document type the original type
					parameters.setDocumentType(documentType);

					// For the document type REBATE, the printManager prints
					// all the rebates at once. This breaks out of the loop.
					break;
				}
			}
		}

		return addition;
	}

	/**
	 * Method to print gift receipts
	 * 
	 * @param bus
	 * @param receiptParameters
	 * @throws PrintableDocumentException
	 */
	protected ArrayList<String> printGiftReceipts(SessionBusIfc bus,
			ReceiptParameterBeanIfc receiptParameters)
			throws PrintableDocumentException {
		// Initial return
		ArrayList<String> additions = new ArrayList<String>();
		int giftReceiptCounter = 1;

		PrintableDocumentManagerIfc printManager = (PrintableDocumentManagerIfc) bus
				.getManager(PrintableDocumentManagerIfc.TYPE);
		// Get the lineItems from the transaction.
		SaleReturnTransactionIfc srTrans = (SaleReturnTransactionIfc) receiptParameters
				.getTransaction();
		AbstractTransactionLineItemIfc[] lineItems = srTrans.getLineItems();
		GiftReceiptParameterBeanIfc giftReceiptParameterBean = printManager
				.getGiftReceiptParameterBeanInstance(bus, receiptParameters);
		giftReceiptParameterBean.setEreceipt(receiptParameters.isEreceipt());

		if (srTrans.isTransactionGiftReceiptAssigned()) {
			ArrayList<SaleReturnLineItemIfc> srliList = new ArrayList<SaleReturnLineItemIfc>();
			for (int i = 0; i < lineItems.length; i++) {
				if (lineItems[i] instanceof SaleReturnLineItemIfc
						&& !((SaleReturnLineItemIfc) lineItems[i])
								.isReturnLineItem()) {
					srliList.add((SaleReturnLineItemIfc) lineItems[i]);
				}
			}

			if (!srliList.isEmpty()) {
				// set only the salereturnlineitems
				giftReceiptParameterBean.setSaleReturnLineItems(srliList
						.toArray(new SaleReturnLineItemIfc[srliList.size()]));
				// print the transaction
				giftReceiptParameterBean
						.setEReceiptFileNameAddition(getGiftCardAddition(giftReceiptCounter));
				printManager.printReceipt(bus, giftReceiptParameterBean);
				additions.add(giftReceiptParameterBean
						.getEReceiptFileNameAddition());
				giftReceiptCounter++;
			}
		} else if (!((PrintingCargo) bus.getCargo())
				.isPrintMultipleGiftReceipt()) {
			Map<CustomerIfc, List<SaleReturnLineItemIfc>> mapSendGifts = new HashMap<CustomerIfc, List<SaleReturnLineItemIfc>>(
					0);
			ArrayList<SaleReturnLineItemIfc> srliList = new ArrayList<SaleReturnLineItemIfc>();
			for (int i = 0; i < srTrans.getLineItemsSize(); i++) {
				if (lineItems[i] instanceof SaleReturnLineItemIfc
						&& !((SaleReturnLineItemIfc) lineItems[i])
								.isReturnLineItem()) {
					SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) lineItems[i];

					if (!((PrintableDocumentManager) printManager)
							.hasDamageDiscounts(srli)) {
						// Get gift registry for this item
						RegistryIDIfc giftRegistry = srli.getRegistry();

						// If item is marked for a gift receipt, or the item is
						// linked to a gift
						// registry and the AutoPrintGiftReceipt parameter is
						// set, then print a gift receipt.
						if (srli.isGiftReceiptItem()
								|| (giftRegistry != null && receiptParameters
										.isAutoPrintGiftReceiptGiftRegistry())
								|| (srli.getItemSendFlag() && receiptParameters
										.isAutoPrintGiftReceiptItemSend())) {
							if (srli.getItemSendFlag()) {
								SendPackageLineItemIfc spli = srTrans
										.getSendPackage(srli
												.getSendLabelCount() - 1);
								// add the item to list mapped by send
								// destination
								List<SaleReturnLineItemIfc> list = mapSendGifts
										.get(spli.getCustomer());
								if (list == null) {
									list = new ArrayList<SaleReturnLineItemIfc>();
									mapSendGifts.put(spli.getCustomer(), list);
								}
								list.add(srli);
							} else
							// if not send gift, print normal gift receipt
							{
								// set only the current lineitem
								srliList.add((SaleReturnLineItemIfc) lineItems[i]);
							}
						}
					} // end if no damage discounts
				}
			} // end for

			if (srliList.size() > 0) {
				// set only the salereturnlineitems
				giftReceiptParameterBean.setSaleReturnLineItems(srliList
						.toArray(new SaleReturnLineItemIfc[srliList.size()]));
				// print the transaction
				giftReceiptParameterBean
						.setEReceiptFileNameAddition(getGiftCardAddition(giftReceiptCounter));
				printManager.printReceipt(bus, giftReceiptParameterBean);
				additions.add(giftReceiptParameterBean
						.getEReceiptFileNameAddition());
				giftReceiptCounter++;
			}

			// if any send gifts were found, print them in groups
			if (mapSendGifts.size() > 0) {
				for (List<SaleReturnLineItemIfc> items : mapSendGifts.values()) {
					giftReceiptParameterBean.setSaleReturnLineItems(items
							.toArray(new SaleReturnLineItemIfc[items.size()]));
					giftReceiptParameterBean
							.setEReceiptFileNameAddition(getGiftCardAddition(giftReceiptCounter));
					printManager.printReceipt(bus, giftReceiptParameterBean);
					additions.add(giftReceiptParameterBean
							.getEReceiptFileNameAddition());
					giftReceiptCounter++;
				}
			}
		} else
		// else not transaction receipt, print a receipt for each line item
		{
			Map<CustomerIfc, List<SaleReturnLineItemIfc>> mapSendGifts = new HashMap<CustomerIfc, List<SaleReturnLineItemIfc>>(
					0);
			for (int i = 0; i < srTrans.getLineItemsSize(); i++) {
				if (lineItems[i] instanceof SaleReturnLineItemIfc
						&& !((SaleReturnLineItemIfc) lineItems[i])
								.isReturnLineItem()) {
					SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) lineItems[i];

					if (!((PrintableDocumentManager) printManager)
							.hasDamageDiscounts(srli)) {
						// Get gift registry for this item
						RegistryIDIfc giftRegistry = srli.getRegistry();

						// If item is marked for a gift receipt, or the item is
						// linked to a gift
						// registry and the AutoPrintGiftReceipt parameter is
						// set, then print a gift receipt.
						if (srli.isGiftReceiptItem()
								|| (giftRegistry != null && receiptParameters
										.isAutoPrintGiftReceiptGiftRegistry())
								|| (srli.getItemSendFlag() && receiptParameters
										.isAutoPrintGiftReceiptItemSend())) {
							if (srli.getItemSendFlag()) {
								SendPackageLineItemIfc spli = srTrans
										.getSendPackage(srli
												.getSendLabelCount() - 1);
								// add the item to list mapped by send
								// destination
								List<SaleReturnLineItemIfc> list = mapSendGifts
										.get(spli.getCustomer());
								if (list == null) {
									list = new ArrayList<SaleReturnLineItemIfc>();
									mapSendGifts.put(spli.getCustomer(), list);
								}
								list.add(srli);
							} else
							// if not send gift, print normal gift receipt
							{
								// set only the current lineitem
								giftReceiptParameterBean
										.setSaleReturnLineItems(new SaleReturnLineItemIfc[] { srli });
								giftReceiptParameterBean
										.setEReceiptFileNameAddition(getGiftCardAddition(giftReceiptCounter));
								printManager.printReceipt(bus,
										giftReceiptParameterBean);
								additions.add(giftReceiptParameterBean
										.getEReceiptFileNameAddition());
								giftReceiptCounter++;
							}
						}
					} // end if no damage discounts
				}
			} // end for
				// if any send gifts were found, print them in groups
			if (mapSendGifts.size() > 0) {
				for (List<SaleReturnLineItemIfc> items : mapSendGifts.values()) {
					giftReceiptParameterBean.setSaleReturnLineItems(items
							.toArray(new SaleReturnLineItemIfc[items.size()]));
					giftReceiptParameterBean
							.setEReceiptFileNameAddition(getGiftCardAddition(giftReceiptCounter));
					printManager.printReceipt(bus, giftReceiptParameterBean);
					additions.add(giftReceiptParameterBean
							.getEReceiptFileNameAddition());
					giftReceiptCounter++;
				}
			}
		}

		return additions;
	}

	/*
	 * Build and return the gift card addtion based on the counter
	 */
	private String getGiftCardAddition(int giftReceiptCounter) {
		String addition = getProperty(APPLICATION_PROPERTIES_GROUP,
				"EReceipt.FileNameAddition.Gift", GIFT_FILE_NAME_ADDITION);
		if (giftReceiptCounter != 1) {
			addition = addition + giftReceiptCounter;
		}
		return addition;
	}

	/**
	 * This method displays the Email Server Offline dialog.
	 * 
	 * @param ui
	 */
	private void displayEmailServerOfflineDialog(POSUIManagerIfc ui) {
		DialogBeanModel model = new DialogBeanModel();
		model.setResourceID("MailServerOffline");
		model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
		model.setButtonLetter(DialogScreensIfc.BUTTON_OK,
				CommonLetterIfc.CONTINUE);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}

	/**
	 * This method email the eReceipt.
	 * 
	 * @param bus
	 * @throws PrintableDocumentException
	 */
	private void emailEreceipt(BusIfc bus, ArrayList<String> fileAdditions)
			throws PrintableDocumentException {
		PrintingCargo cargo = (PrintingCargo) bus.getCargo();
		TenderableTransactionIfc trans = cargo.getTransaction();

		String email = cargo.getEmailAddress();
		// if email address was not provided in cargo by calling service
		// then the operator was prompted for address
		// use address from model
		if (Util.isEmpty(email)) {
			POSUIManagerIfc ui = (POSUIManagerIfc) bus
					.getManager(UIManagerIfc.TYPE);
			// get the UIModel
			DataInputBeanModel model = (DataInputBeanModel) ui
					.getModel(POSUIManagerIfc.ERECEIPT_EMAIL_SCREEN);
			// get the email id from model.
			email = (String) model.getValue("email");
		}
		ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
		// read the email subject from parameter
		String[] subject = cargo.getReceiptText(pm, "eReceiptSubject");
		StringBuilder completeSubect = new StringBuilder();
		for (String i : subject) {
			completeSubect.append(i).append(" ");
		}
		// read the email text from parameter
		String[] message = cargo.getReceiptText(pm, "eReceiptText");
		StringBuilder completeMsg = new StringBuilder();
		for (String i : message) {
			completeMsg.append(i).append("\n");
		}
		// read the mail server host from application.properties
		String host = getProperty(APPLICATION_PROPERTIES_GROUP,
				"mail.smtp.host", "");
		// read the mail server port from application.properties
		String port = getProperty(APPLICATION_PROPERTIES_GROUP,
				"mail.smtp.port", "");
		// read the eReceipt sender email id from from application.properties
		String user = getProperty(APPLICATION_PROPERTIES_GROUP,
				"mail.ereceipt.sender", "");

		// read the email server timeout from application.properties
		String mailServerTimeout = getProperty(APPLICATION_PROPERTIES_GROUP,
				"mail.smtp.timeout", "1000");

		// read the email server connection timeout from application.properties
		String mailServerConnectionTimeout = getProperty(
				APPLICATION_PROPERTIES_GROUP, "mail.smtp.connection.timeout",
				"1000");

		// create the eReceipt pdf file name from transaction id
		ArrayList<String> fileNames = new ArrayList<String>();
		for (String addition : fileAdditions) {
			fileNames.add(trans.getTransactionID() + addition + ".pdf");
		}

		String[] fileList = new String[fileNames.size()];
		fileNames.toArray(fileList);

		// create EmailInfo object with all email information.
		EmailInfo info = new EmailInfo(host, port, mailServerConnectionTimeout,
				mailServerTimeout, user, email, fileList,
				completeSubect.toString(), completeMsg.toString());
		try {
			// send the Email.
			SendEmail.send(info);
			cargo.setReceiptEmailed(true);
		} catch (Exception e) {
			throw new PrintableDocumentException(
					"Unable to email pdf receipt.", e);
		} finally {
			for (String fileName : fileNames) {
				// delete the pdf file if exists.
				File pdfFile = new File(fileName);
				if (pdfFile.exists()) {
					pdfFile.delete();
				}
			}
		}
	}

	/**
	 * Help create a clean receipt.
	 * 
	 * @param lineItemArray
	 */
	protected void checkforDuplicateLineItems(
			SaleReturnLineItemIfc[] lineItemArray) {
		int lengthOfLineItems = lineItemArray.length;
		PLUItemIfc item = null;
		Map<String, List<MessageDTO>> itemMessages = null;
		List<MessageDTO> messageDTOList = null;
		Set<String> keyset = null;
		Iterator<String> iter = null;
		MessageDTO msg = null;
		List<String> itemIds = new ArrayList<String>(lengthOfLineItems);

		for (int ctr = 0; ctr < lengthOfLineItems; ctr++) {

			item = lineItemArray[ctr].getPLUItem();
			if (itemIds.contains(item.getItemID())) {
				itemMessages = item.getAllItemLevelMessages();
				if (itemMessages != null) {
					keyset = itemMessages.keySet();
					iter = keyset.iterator();
					while (iter.hasNext()) {
						messageDTOList = itemMessages.get(iter.next());
						if (messageDTOList != null) {
							for (int msgctr = 0; msgctr < messageDTOList.size(); msgctr++) {
								msg = messageDTOList.get(msgctr);
								if (msg != null) {
									msg.setDuplicate(true);
								}
							}
						}
					}
				} else {
					itemIds.add(item.getItemID());
				}
			} else {
				itemIds.add(item.getItemID());
			}
		}
	}

	/**
	 * Groups all the like footer messages. Also this method stops the printing
	 * of duplicate Item IDs on the receipt footer.
	 * 
	 * @param locale
	 * 
	 * @param ReceiptFooterMessageDTO
	 *            []
	 */
	protected List<ReceiptFooterMessageDTO> groupLikeFooterMessages(
			SaleReturnLineItemIfc[] lineItemArray, boolean isGroupMsgs,
			Locale locale) {
		int lengthOfLineItems = lineItemArray.length;
		Map<String, String> footerMessages = new HashMap<String, String>(0);
		Map<String, List<String>> itemIds = new HashMap<String, List<String>>(0);
		String msgID = null;
		String saleFooterMsgID = null;
		String returnFooterMsgID = null;
		List<String> itemList = null;
		List<String> msgIdlst = new ArrayList<String>(lengthOfLineItems);
		List<ReceiptFooterMessageDTO> rmFooterMsgs = null;
		ReceiptFooterMessageDTO footerMsg = null;

		if (isGroupMsgs) {
			for (int ctr = 0; ctr < lengthOfLineItems; ctr++) {
				saleFooterMsgID = lineItemArray[ctr].getItemMessageID(
						ItemLevelMessageConstants.SALE,
						ItemLevelMessageConstants.FOOTER);
				returnFooterMsgID = lineItemArray[ctr].getItemMessageID(
						ItemLevelMessageConstants.RETURN,
						ItemLevelMessageConstants.FOOTER);

				if ((saleFooterMsgID != null && saleFooterMsgID.length() > 0)
						|| (returnFooterMsgID != null && returnFooterMsgID
								.length() > 0)) {
					msgID = saleFooterMsgID;
					if (msgID == null || msgID.length() == 0) {
						msgID = returnFooterMsgID;
					}
					if (msgIdlst.contains(msgID)) {
						itemList = itemIds.get(msgID);
						if (!itemList.contains(lineItemArray[ctr].getItemID())) {
							itemList.add(lineItemArray[ctr].getItemID());
						}
					} else {

						footerMessages
								.put(msgID, lineItemArray[ctr]
										.getItemFooterMessage(locale));
						itemList = new ArrayList<String>();
						itemList.add(lineItemArray[ctr].getItemID());
						itemIds.put(msgID, itemList);
						msgIdlst.add(msgID);
					}
				}
			}

			// Now returnMessages has unique Messages and itemIDs has unique
			// item
			// IDs
			// both bound by message ID -- msgID and unique MessageIDs in the
			// list
			rmFooterMsgs = new ArrayList<ReceiptFooterMessageDTO>(
					msgIdlst.size());

			for (int msgIdCtr = 0; msgIdCtr < msgIdlst.size(); msgIdCtr++) {
				footerMsg = new ReceiptFooterMessageDTO();
				itemList = itemIds.get(msgIdlst.get(msgIdCtr));
				footerMsg.setItemIds(getStringFromList(itemList));
				footerMsg.setItemMessage(footerMessages.get(msgIdlst
						.get(msgIdCtr)));
				rmFooterMsgs.add(footerMsg);
			}
		} else // Do not Group , just Add item ID and The Item Footer Message
		{
			rmFooterMsgs = new ArrayList<ReceiptFooterMessageDTO>(
					msgIdlst.size());

			for (int ctr = 0; ctr < lineItemArray.length; ctr++) {
				footerMsg = new ReceiptFooterMessageDTO();
				footerMsg.setItemIds(lineItemArray[ctr].getItemID());
				footerMsg.setItemMessage(lineItemArray[ctr]
						.getItemFooterMessage());
				rmFooterMsgs.add(footerMsg);
			}
		}

		// cleanup
		{
			footerMessages = null;
			itemIds = null;
			msgID = null;
			itemList = null;
			msgIdlst = null;
		}

		return rmFooterMsgs;// This is an Array of Messages along with comma
							// separated grouped Item IDs
	}

	/**
	 * Groups all the like messages which have been returned from RM. Also this
	 * method stops the printing of duplicate Item IDs on the receipt footer.
	 * 
	 * @param ReceiptFooterMessageDTO
	 *            []
	 */
	protected List<ReceiptFooterMessageDTO> groupDuplicateReturnMessages(
			TenderableTransactionIfc trans, boolean isGroupMsgs) {

		SaleReturnTransactionIfc saleReturnTransaction = (SaleReturnTransactionIfc) trans;
		SaleReturnLineItemIfc[] lineItemArray = (SaleReturnLineItemIfc[]) saleReturnTransaction
				.getLineItems();
		int lengthOfLineItems = lineItemArray.length;
		Map<String, String> returnMessages = new HashMap<String, String>(0);
		Map<String, List<String>> itemIds = new HashMap<String, List<String>>(0);
		String msgID = null;
		List<String> itemList = null;
		List<String> msgIdlst = new ArrayList<String>(lengthOfLineItems);
		List<ReceiptFooterMessageDTO> rmFooterMsgs = null;
		ReceiptFooterMessageDTO footerMsg = null;

		if (lengthOfLineItems > 0) {
			saleReturnTransaction.setReturnTicketID(trans.getReturnTicket());
		}

		for (int ctr = 0; ctr < lengthOfLineItems; ctr++) {
			if (lineItemArray[ctr].getReturnMessage() != null
					&& lineItemArray[ctr].getReturnMessage().getMessageID() != null) {
				msgID = (lineItemArray[ctr].getReturnMessage().getMessageID())
						.toString();
				if (msgIdlst.contains(msgID)) {
					itemList = itemIds.get(msgID);
					if (!itemList.contains(lineItemArray[ctr].getItemID())) {
						itemList.add(lineItemArray[ctr].getItemID());
					}
					lineItemArray[ctr].getReturnMessage().setDuplicate(true);
				} else {
					returnMessages.put(msgID, lineItemArray[ctr]
							.getReturnMessage().getReturnMessage());
					itemList = new ArrayList<String>();
					itemList.add(lineItemArray[ctr].getItemID());
					itemIds.put(msgID, itemList);
					msgIdlst.add(lineItemArray[ctr].getReturnMessage()
							.getMessageID().toString());
				}
			}
		}

		// Now returnMessages has unique Messages and itemIDs has unique item
		// IDs
		// both bound by message ID -- msgID and unique MessageIDs in the list
		rmFooterMsgs = new ArrayList<ReceiptFooterMessageDTO>(msgIdlst.size());

		for (int msgIdCtr = 0; msgIdCtr < msgIdlst.size(); msgIdCtr++) {
			footerMsg = new ReceiptFooterMessageDTO();
			itemList = itemIds.get(msgIdlst.get(msgIdCtr));
			footerMsg.setItemIds(getStringFromList(itemList));
			footerMsg
					.setItemMessage(returnMessages.get(msgIdlst.get(msgIdCtr)));
			rmFooterMsgs.add(footerMsg);
		}

		// cleanup just in case...
		{
			returnMessages = null;
			itemIds = null;
			msgID = null;
			itemList = null;
			msgIdlst = null;
		}

		return rmFooterMsgs;// This is an Array of Messages along with comma
							// separated grouped Item IDs
	}

	private String getStringFromList(List<String> itemLst) {
		StringBuffer commaSeparatedStrBuf = new StringBuffer();
		if (itemLst != null) {
			for (int itemCtr = 0; itemCtr < itemLst.size(); itemCtr++) {
				commaSeparatedStrBuf.append(itemLst.get(itemCtr));
				if (itemCtr != itemLst.size() - 1) {
					commaSeparatedStrBuf.append(",");
				}
			}
		}
		return commaSeparatedStrBuf.toString();
	}

	protected static void setPrinterStatus(boolean online, BusIfc bus) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);

		StatusBeanModel statusModel = new StatusBeanModel();
		statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, online);
		POSBaseBeanModel baseModel = new POSBaseBeanModel();
		baseModel.setStatusBeanModel(statusModel);
		ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel, false);
	}
}
