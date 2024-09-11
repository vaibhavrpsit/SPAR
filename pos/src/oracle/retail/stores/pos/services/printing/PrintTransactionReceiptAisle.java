/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/PrintTransactionReceiptAisle.java /main/65 2014/07/15 16:04:02 amishash Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    amisha 07/15/14 - Absorbed Code Review Comments.
 *    amisha 07/14/14 - Added Capbility for configuring the file name of
 *                      transaction which is being sent from email.
 *    swbhas 03/20/14 - setting the value of print to true when a recipt will
 *                      be printed
 *    blarse 03/13/14 - Do not set receiptPrinted to true if the receipt wasn't
 *                      printed.
 *    yiqzha 02/13/14 - When doing an order pickup, the customer should sign on
 *                      paper receipt if sign on device is not available.
 *    yiqzha 02/12/14 - Method printReceipt is missing for isPrintStoreReceipt.
 *                      Put it back.
 *    abhina 01/27/14 - Fix to provide I18n support for item footer messages
 *    yiqzha 11/22/13 - Add isPrintCustomerCopy checking. The value can be
 *                      true/false in MPOS.
 *    abonda 10/04/13 - pass the best match locale to get the localized
 *                      description. passing a locale like 'en_us' will not
 *                      return any messages as we have 'en' as locale in the
 *                      databse.
 *    abonda 09/04/13 - initialize collections
 *    abhina 08/13/13 - Fix to display localized item messages on a receipt
 *    yiqzha 07/05/13 - Print store copy if possible when Email print is
 *                      selected.
 *    abhina 06/21/13 - Fix to prevent NLP exception while printing the rebate
 *                      receipt
 *    mchell 02/27/13 - Fixed transaction level gift receipt printing
 *    mchell 11/23/12 - Receipt enhancement quickwin changes
 *    blarse 08/29/12 - Merge from project Echo (MPOS) into Trunk.
 *    blarse 06/26/12 - Preventing rebates from printing for suspended
 *                      transactions (MPOS).
 *    yiqzha 04/16/12 - refactor store send from transaction totals
 *    blarse 04/13/12 - Added support for new receiptEmailed flag. Fixed
 *                      receiptPrinted flag to be true only when the receipt
 *                      actually printed.
 *    blarse 03/29/12 - Changed traverse method to be synchronized. The
 *                      underlying classes/frameworks for printing were not
 *                      designed for the multi-threaded environment required
 *                      for MPOS.
 *    blarse 03/13/12 - Checking cargo for emailAddress before grabbing it from
 *                      the model. MPOS puts address in the cargo.
 *    cgreen 01/27/12 - XbranchMerge
 *                      cgreene_prevent_reprint_extra_button_presses from
 *                      rgbustores_13.4x_generic_branch
 *    cgreen 01/27/12 - do not unlock container when updating status
 *    cgreen 07/07/11 - print a rebate receipt for each quantity in line item
 *    cgreen 11/03/10 - rename ItemLevelMessageConstants
 *    rrkohl 08/11/10 - fix to print Return Ticket id on reprint receipts
 *    asinto 07/23/10 - Changed references to use the SaleReturnTransactionIfc
 *                      (interface) instead of the concrete class.
 *    cgreen 05/26/10 - convert to oracle packaging
 *    jswan  02/15/10 - Make all receipts go with the email.
 *    jswan  02/04/10 - Fixing two defects (HPQC 261 and 680), issue with gift
 *                      reciepts and returns, email not sent for sales when
 *                      configured for network printer.
 *    jswan  01/29/10 - Additional modifications for attaching rebate and gift
 *                      reciepts to the EReceipt.
 *    jswan  01/28/10 - Modifications to support emailing rebate, gift and
 *                      alteration receipts with the sale reciept.
 *    npoola 01/26/10 - print the Rebate Receipt for items in the Order
 *                      transactions
 *    abonda 01/03/10 - update header date
 *    vikini 06/19/09 - Checking Line Item size for Denied Receipt printing
 *    cgreen 04/22/09 - remove reentryMode from parameter bean since
 *                      transaction knows and call transaction method from
 *                      ankle and header
 *    vikini 04/16/09 - Fixing Exception when Item has no ILRM Footer messages
 *    vikini 04/14/09 - Merging to tip
 *    vikini 04/14/09 - Renaming the paramter to DuplicateReceiptFooterMessages
 *    vikini 04/10/09 - checkin after refresh to tip
 *    vikini 04/10/09 - Group Like Footer Messages for ILRM
 *    cgreen 04/09/09 - removed rebate item references
 *    vikini 04/03/09 - Get Return Ticket ID from RM for Printing on Receipts
 *    vikini 03/20/09 - Printing Denied Returns Receipt
 *    cgreen 03/19/09 - refactoring changes
 *    acadar 03/13/09 - do not print gift receipt for return transactions
 *    vikini 03/01/09 - Incorporate CodeReview Comments
 *    vikini 02/28/09 - Fixing Error in display of RM Footer Messages in Receipt
 *    cgreen 02/20/09 - set default local onto the alteration param bean
 *    atirke 02/19/09 - trans re entry
 *    aratho 02/05/09 - Updated to support multibyte characters font in eReceipt
 *                      pdf.
 *    cgreen 01/13/09 - multiple send and gift receipt changes. deleted
 *                      SendGiftReceipt
 *    cgreen 01/08/09 - use getSendPackage(int)
 *    cgreen 01/08/09 - removed reference to getSendPackagesVector()
 *    vikini 12/22/08 - Method Code formatting
 *    vikini 12/18/08 - rinting Return Ticket ID on the Return Receipt
 *    cgreen 12/16/08 - change receipt failure to error
 *    cgreen 12/16/08 - add locale to rebate printing
 *    abonda 12/02/08 - RM-POS integration
 *    nkgaut 12/02/08 - ILRM CR Code Review Changes
 *    aratho 11/21/08 - updated for ereceipt.
 *    aratho 11/20/08 - updated for ereceipt.
 *    aratho 11/20/08 - updated for ereceipt feature.
 *    aratho 11/19/08 - updated for ereceipt feature.
 *    aratho 11/17/08 - updated for ereceipt feature
 *    cgreen 11/13/08 - configure print beans into Spring context
 *    cgreen 11/11/08 - added printGiftReceipts logic to allow giftReceipt
 *                      blueprints to print
 *    vikini 11/11/08 - Wrap the Rebate Receipt Message when printing
 *    cgreen 11/03/08 - fix some HousePaymentReceipt formatting and implement
 *                      blueprint copies
 *    vikini 10/30/08 - Added new Copyright message
 *    nkgaut 10/01/08 - A new site for browser foundation
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         4/6/2007 11:14:40 AM   Michael Boyd    CR
           26172 - v7.2.2 merge to trunk


           4    .v7x      1.2.1.0     8/11/2006 8:23:02 AM   Dinesh Gautam
           Code
           modified for CR No. 4178
           Open  Reprinted Receipt does not print in customer's preferred
           language
      3    360Commerce 1.2         3/31/2005 4:29:31 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:25 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:13:27 PM  Robert Pearse
     $
     Revision 1.8  2004/06/11 18:59:11  jlemieux
     respecting I18N messages for devices within site code

     Revision 1.7  2004/04/27 22:25:25  dcobb
     @scr 4452 Feature Enhancement: Printing
     Code review updates.

     Revision 1.6  2004/04/26 19:51:14  dcobb
     @scr 4452 Feature Enhancement: Printing
     Add Reprint Select flow.

     Revision 1.5  2004/04/22 17:39:00  dcobb
     @scr 4452 Feature Enhancement: Printing
     Added REPRINT_SELECT screen and flow to Reprint Receipt use case..

     Revision 1.4  2004/03/03 23:15:07  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.3  2004/02/12 16:51:40  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:52:28  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.4   Jan 09 2004 17:58:10   DCobb
 * Removed unused import statements.
 * Resolution for 3502: Remove "Printer Offline Behavior" parameter
 *
 *    Rev 1.3   Jan 08 2004 13:55:36   DCobb
 * Removed the PrinterOfflineBehavior parameter and the Halt behavior.
 * Resolution for 3502: Remove "Printer Offline Behavior" parameter
 *
 *    Rev 1.2   Jan 06 2004 16:45:38   cdb
 * Added more descriptive printing message.
 * Resolution for 3645: Printer = offline when an echeck/check tender transaction is voided.
 *
 *    Rev 1.1   Oct 28 2003 15:55:06   blj
 * fixed a problem with franking money order tenders.
 *
 *    Rev 1.0   Aug 29 2003 16:05:32   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:07:42   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:44:38   msg
 * Initial revision.
 *
 *    Rev 1.10   Mar 10 2002 18:01:12   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.9   Feb 05 2002 16:43:12   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.8   23 Jan 2002 13:11:38   pdd
 * Corrected printer status in catch block to offline.
 * Resolution for POS SCR-138: Printer never shows offline on Device Status screen
 *
 *    Rev 1.7   26 Nov 2001 16:06:20   jbp
 * moved receipt logic form printTransactionReceipt Aisle to Utility Manager
 * Resolution for POS SCR-221: Receipt Design Changes
 *
 *    Rev 1.6   Nov 21 2001 12:39:20   blj
 * Updated to allow gift receipt to be printed for kit items, reprint receipt,
 * journals and suspend.
 * Resolution for POS SCR-237: Gift Receipt Feature
 *
 *    Rev 1.3   Nov 16 2001 09:21:22   blj
 * Removed debugging printouts.
 * Resolution for POS SCR-236: 230
 *
 *    Rev 1.2   Nov 16 2001 09:13:46   blj
 * Changed design so that gift receipts are printed from the print transaction receipt aisle.
 * Resolution for POS SCR-236: 230
 *
 *    Rev 1.1   26 Oct 2001 14:57:46   jbp
 * Implement new reciept printing methodology
 * Resolution for POS SCR-221: Receipt Design Changes
 *
 *    Rev 1.0   Sep 21 2001 11:22:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import oracle.retail.stores.common.constants.ItemLevelMessageConstants;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.stock.MessageDTO;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ReceiptFooterMessageDTO;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.LocalizedDeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
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
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import oracle.retail.stores.utility.EmailInfo;
import oracle.retail.stores.utility.SendEmail;
import static oracle.retail.stores.foundation.tour.gate.Gateway.*;
import static oracle.retail.stores.pos.receipt.ReceiptConstantsIfc.*;

/**
 * Print the receipt.
 *
 */
public class PrintTransactionReceiptAisle extends PosLaneActionAdapter {
	private static final long serialVersionUID = 2465321947356780336L;

	/**
	 * Print the receipt and send a letter
	 *
	 * @param bus the bus traversing this lane
	 */
	@Override
	synchronized public void traverse(BusIfc bus) {
		// get transaction from cargo
		PrintingCargo cargo = (PrintingCargo) bus.getCargo();
		TenderableTransactionIfc trans = cargo.getTransaction();
		List<ReceiptFooterMessageDTO> returnedFooterMessages = null; // Grouped Return Messages from Returns Management
		List<ReceiptFooterMessageDTO> itemLevelReceiptFooterMessages = null; // ILRM Footer Messages
		ReceiptFooterMessageDTO[] receiptFooterMessages = null; // Group the above 2 into 1 array

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
			UIUtilities.setUILocaleForCustomer(locale);
		}

		// initialize variables
		boolean duplicateReceipt = cargo.isDuplicateReceipt();
		boolean sendMail = true;
		boolean mailServerOffline = false;
		boolean groupLikeFooterMessages = !(getBooleanProperty("domain", "DuplicateReceiptFooterMessages", false));

		String defaultFileAddition = getProperty(APPLICATION_PROPERTIES_GROUP, ERECEIPT_FILENAME_DEFAULT,
				SALE_RETURN_FILE_NAME_ADDITION);
		// get ui and utility manager
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

		if (trans instanceof SaleReturnTransactionIfc) {
			SaleReturnTransactionIfc saleReturnTransactionIfc = (SaleReturnTransactionIfc) trans;
			checkforDuplicateLineItems((SaleReturnLineItemIfc[]) saleReturnTransactionIfc.getLineItems());
			itemLevelReceiptFooterMessages = groupLikeFooterMessages(
					(SaleReturnLineItemIfc[]) saleReturnTransactionIfc.getLineItems(), groupLikeFooterMessages, locale);
			returnedFooterMessages = groupDuplicateReturnMessages(trans, groupLikeFooterMessages);
			returnedFooterMessages.addAll(itemLevelReceiptFooterMessages);
			receiptFooterMessages = new ReceiptFooterMessageDTO[returnedFooterMessages.size()];
			receiptFooterMessages = returnedFooterMessages.toArray(receiptFooterMessages);
		}

		try {
			ReceiptParameterBeanIfc parameters = ((PrintableDocumentManagerIfc) bus
					.getManager(PrintableDocumentManagerIfc.TYPE))
					.getReceiptParameterBeanInstance((SessionBusIfc) bus, trans);
			if (trans.getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_COMPLETE
					|| trans.getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_PARTIAL) {
				OrderTransactionIfc orderTransaction = (OrderTransactionIfc) trans;
				if (orderTransaction.getOrderRecipient() != null
						&& orderTransaction.getOrderRecipient().getCustomerSignature() != null) {
					// To avoid customers sign their signature on paper
					parameters.setSignatureCaptureImage(true);
				}
			}
			if (receiptFooterMessages != null && receiptFooterMessages.length > 0) {
				parameters.setReturnReceiptFooterMsgs(receiptFooterMessages);
			}
			parameters.setDuplicateReceipt(duplicateReceipt);
			// set only if Type 2
			if (PrintableDocumentManagerIfc.STYLE_VAT_TYPE_2.equalsIgnoreCase(cargo.getReceiptStyle())) {
				parameters.setReceiptStyle(cargo.getReceiptStyle());
			}
			// reset cargo receipt style
			cargo.setReceiptStyle(PrintableDocumentManagerIfc.STYLE_NORMAL);

			// If SaleReturnTransaction and
			// if the is a return ticket (i.e. used returns management) and
			// if there are no line items...
			if (trans instanceof SaleReturnTransactionIfc && trans.getReturnTicket() != null
					&& ((SaleReturnTransactionIfc) trans).getLineItemsSize() == 0) {
				// The returns denied document
				parameters.setDocumentType(ReceiptTypeConstantsIfc.RETURNS_DENIED);
			}

			// Initialize this

			// Print eReceipt; this will be true only for Sale Return Transactions
			if (cargo.isPrintEreceipt()) {
				int transactionType = parameters.getTransaction().getTransactionType();
				String eReceiptFileAddition = getProperty(APPLICATION_PROPERTIES_GROUP,
						"EReceipt.FileNameAddition." + transactionType, defaultFileAddition);
				parameters.setEReceiptFileNameAddition(eReceiptFileAddition);
				try {
					// Set flag to print eReceipt
					parameters.setEreceipt(true);
					// Print Receipt
					ArrayList<String> fileAdditions = printAllReciepts((SessionBusIfc) bus, parameters);
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
				// This value is always true when running on ORPOS. On MPOS, the value can be
				// true or false
				if (cargo.isPrintCustomerCopy()) {
					printAllReciepts((SessionBusIfc) bus, parameters);
				}
			} else if (parameters.isPrintStoreReceipt()) // Print only store receipts.
			{
				// Print Receipt
				PrintableDocumentManagerIfc printManager = (PrintableDocumentManagerIfc) bus
						.getManager(PrintableDocumentManagerIfc.TYPE);
				printManager.printReceipt((SessionBusIfc) bus, parameters);
				// reset PrintStoreReceipt flag
				parameters.setPrintStoreReceipt(false);
				cargo.setReceiptPrinted(true);
			} else if (cargo.isPrintEreceipt() && !cargo.isPrintPaperReceipt()) {
				// still print pater store receipt even if selecting print eReceipt only
				parameters.setPrintStoreReceipt(true);
				// Print Store Receipt
				PrintableDocumentManagerIfc printManager = (PrintableDocumentManagerIfc) bus
						.getManager(PrintableDocumentManagerIfc.TYPE);
				printManager.printReceipt((SessionBusIfc) bus, parameters);
				// reset PrintStoreReceipt flag
				parameters.setPrintStoreReceipt(false);
				cargo.setReceiptPrinted(true);
			}

			// save the count of reprinted receipts for the reprint receipt
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
			logger.error("Unable to print receipt: " + e.getMessage());
			// Update printer status
			setPrinterStatus(false, bus);

			if (e.getCause() != null) {
				logger.error("DeviceException.NestedException:\n" + Util.throwableToString(e.getCause()));
			}

			String msg[] = new String[1];

			if (e.getCause() instanceof LocalizedDeviceException) {
				msg[0] = e.getCause().getLocalizedMessage();
			} else if (e.getCause() instanceof DeviceException
					&& ((DeviceException) e.getCause()).getErrorCode() != DeviceException.UNKNOWN) {
				msg[0] = utility.retrieveDialogText("RetryContinue.PrinterOffline", "Printer is offline.");
			} else {
				msg[0] = utility.retrieveDialogText("RetryContinue.UnknownPrintingError",
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

		if (sendMail) {
			bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
		}
	}

	/**
	 * Print all the reciepts that can accompany a sale or return.
	 * 
	 * @param bus
	 * @param parameters
	 * @return the list of additons to file name. This list only matters to email
	 *         reciept code; each different type of
	 * @throws PrintableDocumentException
	 */
	protected ArrayList<String> printAllReciepts(SessionBusIfc bus, ReceiptParameterBeanIfc parameters)
			throws PrintableDocumentException {
		ArrayList<String> fileAdditions = new ArrayList<String>();
		// Print the regular sale return reciept
		PrintingCargo cargo = (PrintingCargo) bus.getCargo();
		fileAdditions.add(parameters.getEReceiptFileNameAddition());
		PrintableDocumentManagerIfc printManager = (PrintableDocumentManagerIfc) bus
				.getManager(PrintableDocumentManagerIfc.TYPE);
		printManager.printReceipt(bus, parameters);
		cargo.setReceiptPrinted(true);

		if (parameters.isPrintGiftReceipt()) {
			ArrayList<String> additions = printGiftReceipts(bus, parameters);
			if (!additions.isEmpty()) {
				fileAdditions.addAll(additions);
			}
		}

		if ((parameters.getTransaction().getTransactionType() == TransactionIfc.TYPE_SALE
				|| parameters.getTransaction().getTransactionType() == TransactionIfc.TYPE_RETURN
				|| parameters.getTransaction().getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
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
	protected String printAlterationReceipts(SessionBusIfc bus, ReceiptParameterBeanIfc parameters)
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
		String eReceiptFileAddition = getProperty(APPLICATION_PROPERTIES_GROUP, "EReceipt.FileNameAddition.Alteration",
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
	protected String printRebateReceipts(SessionBusIfc bus, ReceiptParameterBeanIfc parameters)
			throws PrintableDocumentException {
		String addition = null;
		AbstractTransactionLineItemIfc[] li = parameters.getLineItems();
		for (int i = li.length - 1; i >= 0; i--) {
			if (li[i] instanceof SaleReturnLineItemIfc) {
				String rebateMessage = ((SaleReturnLineItemIfc) li[i]).getItemRebateMessage();
				if (!Util.isEmpty(rebateMessage)) {
					String eReceiptFileAddition = getProperty(APPLICATION_PROPERTIES_GROUP,
							"EReceipt.FileNameAddition.Rebate", REBATE_FILE_NAME_ADDITION);
					parameters.setEReceiptFileNameAddition(eReceiptFileAddition);
					addition = parameters.getEReceiptFileNameAddition();
					String documentType = parameters.getDocumentType();
					parameters.setDocumentType(ReceiptTypeConstantsIfc.REBATE);

					// Get the print mannger and print the rebates
					PrintableDocumentManagerIfc printManager = (PrintableDocumentManagerIfc) bus
							.getManager(PrintableDocumentManagerIfc.TYPE);

					// print a rebate for each item in the line
					for (int j = 0; j < li[i].getItemQuantityDecimal().abs().intValue(); j++) {
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
	protected ArrayList<String> printGiftReceipts(SessionBusIfc bus, ReceiptParameterBeanIfc receiptParameters)
			throws PrintableDocumentException {
		// Initial return
		ArrayList<String> additions = new ArrayList<String>();
		int giftReceiptCounter = 1;

		PrintableDocumentManagerIfc printManager = (PrintableDocumentManagerIfc) bus
				.getManager(PrintableDocumentManagerIfc.TYPE);
		// Get the lineItems from the transaction.
		SaleReturnTransactionIfc srTrans = (SaleReturnTransactionIfc) receiptParameters.getTransaction();
		AbstractTransactionLineItemIfc[] lineItems = srTrans.getLineItems();
		GiftReceiptParameterBeanIfc giftReceiptParameterBean = printManager.getGiftReceiptParameterBeanInstance(bus,
				receiptParameters);
		giftReceiptParameterBean.setEreceipt(receiptParameters.isEreceipt());

		if (srTrans.isTransactionGiftReceiptAssigned()) {
			ArrayList<SaleReturnLineItemIfc> srliList = new ArrayList<SaleReturnLineItemIfc>();
			for (int i = 0; i < lineItems.length; i++) {
				if (lineItems[i] instanceof SaleReturnLineItemIfc
						&& !((SaleReturnLineItemIfc) lineItems[i]).isReturnLineItem()) {
					srliList.add((SaleReturnLineItemIfc) lineItems[i]);
				}
			}

			if (!srliList.isEmpty()) {
				// set only the salereturnlineitems
				giftReceiptParameterBean
						.setSaleReturnLineItems(srliList.toArray(new SaleReturnLineItemIfc[srliList.size()]));
				// print the transaction
				giftReceiptParameterBean.setEReceiptFileNameAddition(getGiftCardAddition(giftReceiptCounter));
				printManager.printReceipt(bus, giftReceiptParameterBean);
				additions.add(giftReceiptParameterBean.getEReceiptFileNameAddition());
				giftReceiptCounter++;
			}
		} else if (!((PrintingCargo) bus.getCargo()).isPrintMultipleGiftReceipt()) {
			Map<CustomerIfc, List<SaleReturnLineItemIfc>> mapSendGifts = new HashMap<CustomerIfc, List<SaleReturnLineItemIfc>>(
					0);
			ArrayList<SaleReturnLineItemIfc> srliList = new ArrayList<SaleReturnLineItemIfc>();
			for (int i = 0; i < srTrans.getLineItemsSize(); i++) {
				if (lineItems[i] instanceof SaleReturnLineItemIfc
						&& !((SaleReturnLineItemIfc) lineItems[i]).isReturnLineItem()) {
					SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) lineItems[i];

					if (!((PrintableDocumentManager) printManager).hasDamageDiscounts(srli)) {
						// Get gift registry for this item
						RegistryIDIfc giftRegistry = srli.getRegistry();

						// If item is marked for a gift receipt, or the item is
						// linked to a gift
						// registry and the AutoPrintGiftReceipt parameter is
						// set, then print a gift receipt.
						if (srli.isGiftReceiptItem()
								|| (giftRegistry != null && receiptParameters.isAutoPrintGiftReceiptGiftRegistry())
								|| (srli.getItemSendFlag() && receiptParameters.isAutoPrintGiftReceiptItemSend())) {
							if (srli.getItemSendFlag()) {
								SendPackageLineItemIfc spli = srTrans.getSendPackage(srli.getSendLabelCount() - 1);
								// add the item to list mapped by send
								// destination
								List<SaleReturnLineItemIfc> list = mapSendGifts.get(spli.getCustomer());
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
				giftReceiptParameterBean
						.setSaleReturnLineItems(srliList.toArray(new SaleReturnLineItemIfc[srliList.size()]));
				// print the transaction
				giftReceiptParameterBean.setEReceiptFileNameAddition(getGiftCardAddition(giftReceiptCounter));
				printManager.printReceipt(bus, giftReceiptParameterBean);
				additions.add(giftReceiptParameterBean.getEReceiptFileNameAddition());
				giftReceiptCounter++;
			}

			// if any send gifts were found, print them in groups
			if (mapSendGifts.size() > 0) {
				for (List<SaleReturnLineItemIfc> items : mapSendGifts.values()) {
					giftReceiptParameterBean
							.setSaleReturnLineItems(items.toArray(new SaleReturnLineItemIfc[items.size()]));
					giftReceiptParameterBean.setEReceiptFileNameAddition(getGiftCardAddition(giftReceiptCounter));
					printManager.printReceipt(bus, giftReceiptParameterBean);
					additions.add(giftReceiptParameterBean.getEReceiptFileNameAddition());
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
						&& !((SaleReturnLineItemIfc) lineItems[i]).isReturnLineItem()) {
					SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc) lineItems[i];

					if (!((PrintableDocumentManager) printManager).hasDamageDiscounts(srli)) {
						// Get gift registry for this item
						RegistryIDIfc giftRegistry = srli.getRegistry();

						// If item is marked for a gift receipt, or the item is
						// linked to a gift
						// registry and the AutoPrintGiftReceipt parameter is
						// set, then print a gift receipt.
						if (srli.isGiftReceiptItem()
								|| (giftRegistry != null && receiptParameters.isAutoPrintGiftReceiptGiftRegistry())
								|| (srli.getItemSendFlag() && receiptParameters.isAutoPrintGiftReceiptItemSend())) {
							if (srli.getItemSendFlag()) {
								SendPackageLineItemIfc spli = srTrans.getSendPackage(srli.getSendLabelCount() - 1);
								// add the item to list mapped by send
								// destination
								List<SaleReturnLineItemIfc> list = mapSendGifts.get(spli.getCustomer());
								if (list == null) {
									list = new ArrayList<SaleReturnLineItemIfc>();
									mapSendGifts.put(spli.getCustomer(), list);
								}
								list.add(srli);
							} else
							// if not send gift, print normal gift receipt
							{
								// set only the current lineitem
								giftReceiptParameterBean.setSaleReturnLineItems(new SaleReturnLineItemIfc[] { srli });
								giftReceiptParameterBean
										.setEReceiptFileNameAddition(getGiftCardAddition(giftReceiptCounter));
								printManager.printReceipt(bus, giftReceiptParameterBean);
								additions.add(giftReceiptParameterBean.getEReceiptFileNameAddition());
								giftReceiptCounter++;
							}
						}
					} // end if no damage discounts
				}
			} // end for
				// if any send gifts were found, print them in groups
			if (mapSendGifts.size() > 0) {
				for (List<SaleReturnLineItemIfc> items : mapSendGifts.values()) {
					giftReceiptParameterBean
							.setSaleReturnLineItems(items.toArray(new SaleReturnLineItemIfc[items.size()]));
					giftReceiptParameterBean.setEReceiptFileNameAddition(getGiftCardAddition(giftReceiptCounter));
					printManager.printReceipt(bus, giftReceiptParameterBean);
					additions.add(giftReceiptParameterBean.getEReceiptFileNameAddition());
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
		String addition = getProperty(APPLICATION_PROPERTIES_GROUP, "EReceipt.FileNameAddition.Gift",
				GIFT_FILE_NAME_ADDITION);
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
		model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CONTINUE);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
	}

	/**
	 * This method email the eReceipt.
	 *
	 * @param bus
	 * @throws PrintableDocumentException
	 */
	private void emailEreceipt(BusIfc bus, ArrayList<String> fileAdditions) throws PrintableDocumentException {
		PrintingCargo cargo = (PrintingCargo) bus.getCargo();
		TenderableTransactionIfc trans = cargo.getTransaction();

		String email = cargo.getEmailAddress();
		// if email address was not provided in cargo by calling service
		// then the operator was prompted for address
		// use address from model
		if (Util.isEmpty(email)) {
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			// get the UIModel
			DataInputBeanModel model = (DataInputBeanModel) ui.getModel(POSUIManagerIfc.ERECEIPT_EMAIL_SCREEN);
			// get the email id from model.
			email = (String) model.getValue("email");
		}
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
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
		String host = getProperty(APPLICATION_PROPERTIES_GROUP, "mail.smtp.host", "");
		// read the mail server port from application.properties
		String port = getProperty(APPLICATION_PROPERTIES_GROUP, "mail.smtp.port", "");
		// read the eReceipt sender email id from from application.properties
		String user = getProperty(APPLICATION_PROPERTIES_GROUP, "mail.ereceipt.sender", "");

		// read the email server timeout from application.properties
		String mailServerTimeout = getProperty(APPLICATION_PROPERTIES_GROUP, "mail.smtp.timeout", "1000");

		// read the email server connection timeout from application.properties
		String mailServerConnectionTimeout = getProperty(APPLICATION_PROPERTIES_GROUP, "mail.smtp.connection.timeout",
				"1000");

		// create the eReceipt pdf file name from transaction id
		ArrayList<String> fileNames = new ArrayList<String>();
		for (String addition : fileAdditions) {
			fileNames.add(trans.getTransactionID() + addition + ".pdf");
		}

		String[] fileList = new String[fileNames.size()];
		fileNames.toArray(fileList);

		// create EmailInfo object with all email information.
		EmailInfo info = new EmailInfo(host, port, mailServerConnectionTimeout, mailServerTimeout, user, email,
				fileList, completeSubect.toString(), completeMsg.toString());
		try {
			// send the Email.
			SendEmail.send(info);
			cargo.setReceiptEmailed(true);
		} catch (Exception e) {
			throw new PrintableDocumentException("Unable to email pdf receipt.", e);
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
	protected void checkforDuplicateLineItems(SaleReturnLineItemIfc[] lineItemArray) {
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
	 * Groups all the like footer messages. Also this method stops the printing of
	 * duplicate Item IDs on the receipt footer.
	 * 
	 * @param locale
	 *
	 * @param ReceiptFooterMessageDTO[]
	 */
	protected List<ReceiptFooterMessageDTO> groupLikeFooterMessages(SaleReturnLineItemIfc[] lineItemArray,
			boolean isGroupMsgs, Locale locale) {
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
				saleFooterMsgID = lineItemArray[ctr].getItemMessageID(ItemLevelMessageConstants.SALE,
						ItemLevelMessageConstants.FOOTER);
				returnFooterMsgID = lineItemArray[ctr].getItemMessageID(ItemLevelMessageConstants.RETURN,
						ItemLevelMessageConstants.FOOTER);

				if ((saleFooterMsgID != null && saleFooterMsgID.length() > 0)
						|| (returnFooterMsgID != null && returnFooterMsgID.length() > 0)) {
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

						footerMessages.put(msgID, lineItemArray[ctr].getItemFooterMessage(locale));
						itemList = new ArrayList<String>();
						itemList.add(lineItemArray[ctr].getItemID());
						itemIds.put(msgID, itemList);
						msgIdlst.add(msgID);
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
				footerMsg.setItemMessage(footerMessages.get(msgIdlst.get(msgIdCtr)));
				rmFooterMsgs.add(footerMsg);
			}
		} else // Do not Group , just Add item ID and The Item Footer Message
		{
			rmFooterMsgs = new ArrayList<ReceiptFooterMessageDTO>(msgIdlst.size());

			for (int ctr = 0; ctr < lineItemArray.length; ctr++) {
				footerMsg = new ReceiptFooterMessageDTO();
				footerMsg.setItemIds(lineItemArray[ctr].getItemID());
				footerMsg.setItemMessage(lineItemArray[ctr].getItemFooterMessage());
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
	 * @param ReceiptFooterMessageDTO[]
	 */
	protected List<ReceiptFooterMessageDTO> groupDuplicateReturnMessages(TenderableTransactionIfc trans,
			boolean isGroupMsgs) {

		SaleReturnTransactionIfc saleReturnTransaction = (SaleReturnTransactionIfc) trans;
		SaleReturnLineItemIfc[] lineItemArray = (SaleReturnLineItemIfc[]) saleReturnTransaction.getLineItems();
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
				msgID = (lineItemArray[ctr].getReturnMessage().getMessageID()).toString();
				if (msgIdlst.contains(msgID)) {
					itemList = itemIds.get(msgID);
					if (!itemList.contains(lineItemArray[ctr].getItemID())) {
						itemList.add(lineItemArray[ctr].getItemID());
					}
					lineItemArray[ctr].getReturnMessage().setDuplicate(true);
				} else {
					returnMessages.put(msgID, lineItemArray[ctr].getReturnMessage().getReturnMessage());
					itemList = new ArrayList<String>();
					itemList.add(lineItemArray[ctr].getItemID());
					itemIds.put(msgID, itemList);
					msgIdlst.add(lineItemArray[ctr].getReturnMessage().getMessageID().toString());
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
			footerMsg.setItemMessage(returnMessages.get(msgIdlst.get(msgIdCtr)));
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
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

		StatusBeanModel statusModel = new StatusBeanModel();
		statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, online);
		POSBaseBeanModel baseModel = new POSBaseBeanModel();
		baseModel.setStatusBeanModel(statusModel);
		ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel, false);
	}
}
