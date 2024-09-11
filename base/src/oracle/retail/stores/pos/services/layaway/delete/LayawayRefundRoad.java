/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/delete/LayawayRefundRoad.java /main/20 2012/09/12 11:57:22 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   09/11/12 - Merge project Echo (MPOS) into Trunk.
 *    vtemker   03/30/12 - Refactoring of getNumber() method of TenderCheck
 *                         class - returns sensitive data in byte[] instead of
 *                         String
 *    sgu       09/08/11 - add house account as a refund tender
 *    acadar    06/10/10 - refreshed to tip
 *    acadar    06/10/10 - use default locale for currency display
 *    abhayg    06/10/10 - Total incorrect on Receipt for Layaway Delete
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    ranojha   11/04/08 - Code refreshed to tip
 *    acadar    11/03/08 - localization of transaction tax reason codes
 * ===========================================================================
     $Log:
      11   360Commerce 1.10        07/12/2007 10:55:19 AM Anda D. Cadar
           replaced $ with Amt.
      10   360Commerce 1.9         07/10/2007 4:32:27 PM  Alan N. Sinton  CR
           27623 - Modified calls to SaleReturnTransaction.journalLineItems()
           to use the JournalFormatterManager instead.
      9    360Commerce 1.8         05/21/2007 9:16:20 AM  Anda D. Cadar   EJ
           changes
      8    360Commerce 1.7         05/08/2007 5:22:00 PM  Alan N. Sinton  CR
           26486 - Refactor of some EJournal code.
      7    360Commerce 1.6         04/25/2007 8:52:25 AM  Anda D. Cadar   I18N
           merge

      6    360Commerce 1.5         03/29/2007 6:30:39 PM  Michael Boyd    CR
           26172 - v8x merge to trunk

           6    .v8x      1.4.1.0     3/3/2007 2:02:44 PM    Maisa De Camargo
           Replaced "Sub-Total" to "Subtotal" to match receipt
      5    360Commerce 1.4         03/22/2006 7:16:02 AM  Nageshwar Mishra CR
           16135: Modified the code for the duplicate journal string in the
           journalLayawayDelete() method.
      4    360Commerce 1.3         02/24/2006 1:03:43 PM  Brett J. Larsen CR
           10575 - incorrect tax amount in e-journal for tax exempt
           transactions

           replaced faulty code w/ a call to a new helper method in
           JournalUtilities

      3    360Commerce 1.2         03/31/2005 4:28:50 PM  Robert Pearse
      2    360Commerce 1.1         03/10/2005 10:23:03 AM Robert Pearse
      1    360Commerce 1.0         02/11/2005 12:12:17 PM Robert Pearse
     $
     Revision 1.6.2.1  2004/10/15 18:50:30  kmcbride
     Merging in trunk changes that occurred during branching activity

     Revision 1.7  2004/10/12 20:03:59  bwf
     @scr 7318 Fixed layway delete.  Removed unecessary log to screens.

     Revision 1.6  2004/09/30 20:21:52  jdeleau
     @scr 7263 Make printItemTax apply to e-journal as well as receipts.

     Revision 1.5  2004/09/30 18:08:46  cdb
     @scr 7248 Cleaned up inventory location and state in LayawayTransaction object.

     Revision 1.4  2004/06/29 22:03:31  aachinfiev
     Merge the changes for inventory & POS integration

     Revision 1.3.2.1  2004/06/07 16:27:07  aachinfiev
     Added ability to prompt for inventory location as part of inventory & pos
     integration requirements.

     Revision 1.3  2004/02/12 16:50:48  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:22  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.2   13 Jan 2004 14:58:00   aschenk
 * Defect fixes for 3572.
 *
 *    Rev 1.1   Sep 18 2003 09:41:46   bwf
 * Negate the refund since it is an abs value.
 * Resolution for 3386: Layaway Delect- not issusing a refund when required.
 *
 *    Rev 1.0   Aug 29 2003 16:00:32   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:21:10   msg
 * Initial revision.
 *
 *    Rev 1.2   25 Mar 2002 14:44:54   dfh
 * negate total amount paid for the journal string
 * Resolution for POS SCR-560: Total Amount Paid on Layaway Delete not in parentheses
 *
 *    Rev 1.1   22 Mar 2002 16:48:58   dfh
 * cleanup, fix ejournal transaction tax changes
 * Resolution for POS SCR-1256: Layaway Delete, EJ entry does not journal correctly - extra field labels
 *
 *    Rev 1.0   Mar 18 2002 11:34:58   msg
 * Initial revision.
 *
 *    Rev 1.1   20 Dec 2001 16:59:04   jbp
 * set payments date to new business date as opposed to date  of origional transaction.
 * Resolution for POS SCR-473: Return/Layaway Delete where a customer update occurs causes extra info on EJ
 *
 *    Rev 1.0   Sep 21 2001 11:21:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:28   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.layaway.delete;

// foundation imports
import java.util.ArrayList;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.LayawayDataTransaction;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.PaymentConstantsIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.PaymentDetailBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

//------------------------------------------------------------------------------
/**
    Creates the Layaway Delete Transaction.
    <P>
    @version $Revision: /main/20 $
**/
//------------------------------------------------------------------------------
public class LayawayRefundRoad extends PosLaneActionAdapter
{
    /**
        class name constant
    **/
    public static final String LANENAME = "LayawayRefundRoad";

    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/20 $";

    //--------------------------------------------------------------------------
    /**
       Create the Layaway Delete Transaction and its payment.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        LayawayCargo layawayDeleteCargo = (LayawayDeleteCargo) bus.getCargo();
        layawayDeleteCargo.setAccessFunctionID(RoleFunctionIfc.LAYAWAY_DELETE);

        // get reference to ui and bean model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        PaymentDetailBeanModel model = (PaymentDetailBeanModel) ui.getModel(POSUIManagerIfc.REFUND_DETAIL);

        // Gets layaway from cargo... set in the LayawayPaymentSite
        LayawayCargo layawayCargo = (LayawayCargo) bus.getCargo();
        LayawayIfc layaway = layawayCargo.getLayaway();

		LayawayTransactionIfc initialLayawayTransaction = layawayCargo
		.getInitialLayawayTransaction();


        // Get changes from ui
        layaway.setDeletionFee(model.getDeletionFee());
        CurrencyIfc balanceDue = model.getBalanceDue();
        layaway.setBalanceDue(balanceDue);
        CurrencyIfc refund = model.getRefund();
        // refund is an abs value, should be negative
        if(refund.signum() == CurrencyIfc.POSITIVE)
        {
            refund = refund.negate();
        }

        // Get layaway transaction
        LayawayTransactionIfc layawayTransaction =
            DomainGateway.getFactory().getLayawayTransactionInstance();
        layawayTransaction.initialize(
            layawayCargo.getSeedLayawayTransaction(),
            initialLayawayTransaction);
        layawayTransaction.setTransactionType(TransactionIfc.TYPE_LAYAWAY_DELETE);
        layawayTransaction.setTransactionStatus(TransactionIfc.STATUS_IN_PROGRESS);
        layawayTransaction.setLayaway(layaway);

        // remove tender line items
        for (int i=0; i<layawayTransaction.getTenderLineItemsSize(); )
        {
            layawayTransaction.removeTenderLineItem(i);
        }

        // set layaway totals
        TransactionTotalsIfc layawayTotals = layawayTransaction.getTransactionTotals();
        layawayTotals.setBalanceDue(refund);
        // negate grand total
        layawayTotals.setGrandTotal(layawayTotals.getGrandTotal());
        layawayTransaction.setTransactionTotals(layawayTotals);

        // build payment
        PaymentIfc payment = DomainGateway.getFactory().getPaymentInstance();
        payment.setReferenceNumber(layaway.getLayawayID());
        payment.setPaymentAccountType(PaymentConstantsIfc.ACCOUNT_TYPE_LAYAWAY);
        payment.setTransactionID(layaway.getInitialTransactionID());
        payment.setPaymentAmount(refund);
        payment.setBusinessDate(layawayCargo.getStoreStatus().getBusinessDate());
        layawayCargo.setPayment(payment);
        layawayTransaction.setPayment(payment);
        layawayTransaction.setReentryMode(layawayCargo.getRegister().getWorkstation().isTransReentryMode());
        // update cargo with payment transaction
        layawayCargo.setTenderableTransaction(layawayTransaction);
        // journal layaway delete info
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        journalLayawayDelete((JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE), layawayTransaction, layawayCargo, bus.getServiceName(), pm);

		//add return items and return tender elements to transaction
		layawayCargo.addOriginalReturnTransaction(initialLayawayTransaction);
		addReturnTenderDetails(layawayTransaction, initialLayawayTransaction);
		addReturnItemDetails(layawayTransaction, initialLayawayTransaction);

    }

    /**
     *
     * @param layawayTransaction
     * @param initialLayawayTransaction
     */
    private void addReturnItemDetails(LayawayTransactionIfc layawayTransaction,
			LayawayTransactionIfc initialLayawayTransaction) {
		SaleReturnLineItemIfc[] lineItems = (SaleReturnLineItemIfc[]) layawayTransaction
				.getLineItems();

		Vector returnablelineItems = new Vector();

		// get all returnable line items
		if (lineItems != null && lineItems.length > 0) {
			SaleReturnLineItemIfc item;
			for (int i = 0; i < lineItems.length; i++) {
				item = (SaleReturnLineItemIfc) lineItems[i];
				if (item.isReturnable()) {
					returnablelineItems.addElement(item);
				}
			}
		}

		int rowsSelected = returnablelineItems.size();
		SaleReturnLineItemIfc[] rsLineItems = new SaleReturnLineItemIfc[rowsSelected];
		ReturnItemIfc[] returnItems = new ReturnItemIfc[rowsSelected];
		for (int i = 0; i < rowsSelected; i++) {
			SaleReturnLineItemIfc item = (SaleReturnLineItemIfc) returnablelineItems
					.elementAt(i);
			rsLineItems[i] = (SaleReturnLineItemIfc) item.clone();
			returnItems[i] = DomainGateway.getFactory().getReturnItemInstance();
			returnItems[i].setItemQuantity(item.getQuantityReturnable());
			returnItems[i].setFromRetrievedTransaction(true);
		}

		PLUItemIfc[] pluItems = null;
		// get all PLU items
		pluItems = getPLUItems(rsLineItems);
		int numItems = 0;
		if (returnItems != null && returnItems.length != 0) {

			numItems = returnItems.length;
			if (pluItems.length < numItems) {
				numItems = pluItems.length;
			}

			// Process each return line item
			for (int i = 0; i < numItems; i++) {
				// Prepare line item.
				SaleReturnLineItemIfc srli = null;
				ReturnItemIfc ri = returnItems[i];
				if (ri != null) {
					// Use the Sale Return item from the transaction
					srli = lineItems[i];
					if (initialLayawayTransaction != null) {
						// cargo
						ri.setOriginalTransactionID(initialLayawayTransaction
								.getTransactionIdentifier());
						ri.setOriginalLineNumber(srli.getLineNumber());
						ri
								.setOriginalTransactionBusinessDate(initialLayawayTransaction
										.getBusinessDay());
                        // Layaways are always retrieved and have receipt during a refund.
						ri.setHaveReceipt(true);
						ri.setItemTax((ItemTaxIfc) srli.getItemTax().clone());
					}
					srli.setReturnItem(ri);
				}
			}
		}
	}

    /**
     * Add all the tenders associated to the layaway.
     * The tenders are applied to the layaway during creation and payments
     *
     * @param layawayTransaction
     * @param initialLayawayTransaction
     */
    private void addReturnTenderDetails(LayawayTransactionIfc layawayTransaction,
            LayawayTransactionIfc initialLayawayTransaction)
    {
        // We need to include the tenders collected as layaway payments as well.
        LayawayDataTransaction dataTransaction = null;
        dataTransaction = (LayawayDataTransaction) DataTransactionFactory
                .create(DataTransactionKeys.LAYAWAY_DATA_TRANSACTION);
        try
        {
            TenderLineItemIfc[] tenderLineItems = dataTransaction.readLayawayTenders(layawayTransaction);
            layawayTransaction.setReturnTenderElements(getOriginalTenders(tenderLineItems));
        }
        catch (DataException de)
        {
            logger.info("Fail to retrieve the tenders for the all the layaway transactions." +
                        "Using only the tenders associated to the Layaway Initiate Transaction");
            layawayTransaction.setReturnTenderElements(getOriginalTenders(initialLayawayTransaction
                    .getTenderLineItems()));
        }
    }

	// ----------------------------------------------------------------------
	/**
	 * Retrieve tenders from original transaction
	 *
	 * @param tenderList
	 * @return ReturnTenderDataElement[] list of tenders
	 */
	// ----------------------------------------------------------------------
    protected ReturnTenderDataElementIfc[] getOriginalTenders(
            TenderLineItemIfc[] tenderList) {
        ReturnTenderDataElementIfc[] tenders = new ReturnTenderDataElementIfc[tenderList.length];
        for (int i = 0; i < tenderList.length; i++) {
            tenders[i] = DomainGateway.getFactory()
            .getReturnTenderDataElementInstance();
            tenders[i].setTenderType(tenderList[i].getTypeCode());
            if (tenderList[i].getTypeCode() == TenderLineItemIfc.TENDER_TYPE_CHARGE)
            {
                tenders[i].setCardType(((TenderChargeIfc)tenderList[i]).getCardType());
            }
            tenders[i].setAccountNumber(new String(tenderList[i].getNumber()));
            tenders[i].setTenderAmount(tenderList[i].getAmountTender());
        }
        return tenders;
    }

	/**
	 * Get all PLU items from sales return items
	 *
	 * @param returnSaleLineItems
	 * @return PLUItemIfc[]
	 */
	private PLUItemIfc[] getPLUItems(SaleReturnLineItemIfc[] returnSaleLineItems) {
		ArrayList items = null;
		if (returnSaleLineItems != null) {
			items = new ArrayList();
			for (int i = 0; i < returnSaleLineItems.length; i++) {
				if (returnSaleLineItems[i] != null) {
					items.add(returnSaleLineItems[i].getPLUItem());
				}
			}
		}
		// copy data to array
		PLUItemIfc[] itemList = null;
		if (items != null) {
			itemList = new PLUItemIfc[items.size()];
			items.toArray(itemList);
		}
		return itemList;
	}


    /**
    *   Journals the layaway delete information.
    *    <P>
    * @param trans LayawayTransactionIfc layaway delete transaction
    * @param layawayCargo cargo
    * @param serviceName service name used in logging
    **/
    protected void journalLayawayDelete(JournalManagerIfc jmgr, LayawayTransactionIfc trans,
                                        LayawayCargo layawayCargo, String serviceName)
    {
        journalLayawayDelete(jmgr, trans, layawayCargo, serviceName, null);
    }

    /**
    *  Journals the layaway delete information.
    *   <P>
    * @param trans LayawayTransactionIfc layaway delete transaction
    * @param layawayCargo cargo
    * @param serviceName service name used in logging
    * @param pm ParameterManager
    */
    protected void journalLayawayDelete(JournalManagerIfc jmgr, LayawayTransactionIfc trans,
                                        LayawayCargo layawayCargo, String serviceName, ParameterManagerIfc pm)
    {
        String EOL = "" + Util.EOL;
        Object[] dataArgs = new Object[2];
        String buf = "";
        LayawayIfc layaway = trans.getLayaway();
        TransactionTotalsIfc totals = trans.getTransactionTotals();
        JournalFormatterManagerIfc formatter =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);

        // add header
        dataArgs[0] = layaway.getLayawayID();
        buf += I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LAYAWAY_NUMBER_LABEL, dataArgs) + EOL + EOL;
        buf += I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LAYAWAY_DELETE_LABEL, null) + EOL;

        StringBuffer sb = new StringBuffer();
        TransactionDiscountStrategyIfc[] discounts = trans.getTransactionDiscounts();
        if (discounts != null &&
            discounts.length > 0)
        {
            for (int i = 0; i < discounts.length; i++)
            {
                sb.append(discounts[i].toJournalString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
            }
        }

        TransactionTaxIfc tax = trans.getTransactionTax();
        if (tax.getTaxMode() != TaxIfc.TAX_MODE_STANDARD)
        {
			String reasonCode = Util.EOL
					+ I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
							JournalConstantsIfc.REASON_CODE_TAG_LABEL, null);
			StringBuffer message = new StringBuffer();
			String reasonText = "";
			switch (tax.getTaxMode()) {
			case TaxIfc.TAX_MODE_EXEMPT: {
				String customer = "";
				if (trans.getCustomer() != null) {
					dataArgs[0] = trans.getCustomer().getCustomerID();
					customer = Util.EOL
							+ I18NHelper.getString(
									I18NConstantsIfc.EJOURNAL_TYPE,
									JournalConstantsIfc.CUSTOMER_ID_LABEL,
									dataArgs);
				}
				reasonText = tax.getReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
				dataArgs[0] = tax.getTaxExemptCertificateID() + reasonCode
						+ reasonText;
				message
						.append(
								I18NHelper
										.getString(
												I18NConstantsIfc.EJOURNAL_TYPE,
												JournalConstantsIfc._TRANS_TAX_EXEMPT_LABEL,
												null))
						.append(customer)
						.append(Util.EOL)
						.append(
								I18NHelper
										.getString(
												I18NConstantsIfc.EJOURNAL_TYPE,
												JournalConstantsIfc.TAX_CERTIFICATE_TAG_LABEL,
												dataArgs));
				/*
				 * // message = new StringBuffer("\nTRANS: Tax Exempt") +
				 * customer + "\n Tax Certificate " +
				 * tax.getTaxExemptCertificateID() + reasonCode + reasonText;
				 */
				break;
			}
			case TaxIfc.TAX_MODE_OVERRIDE_AMOUNT: {

				reasonText = tax.getReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
				dataArgs[0] = tax.getOverrideAmount() + reasonCode + reasonText;
				message.append(Util.EOL
						+ I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
								JournalConstantsIfc.TRANS_TAX_OVERRIDE_LABEL,
								null)
						+ Util.EOL
						+ I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
								JournalConstantsIfc.TAX_OVERRIDE_AMT_LABEL,
								dataArgs));
				/*
				 * message = "\nTRANS: Tax Override" + "\n Tax Override Amt. " +
				 * tax.getOverrideAmount() + reasonCode + reasonText;
				 */
				break;
			}
			case TaxIfc.TAX_MODE_OVERRIDE_RATE: {

				reasonText = tax.getReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
				dataArgs[0] = tax.getOverrideRate() * 100 + reasonCode
						+ reasonText;
				message.append(Util.EOL
						+ I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
								JournalConstantsIfc.TRANS_TAX_OVERRIDE_LABEL,
								null)
						+ Util.EOL
						+ I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
								JournalConstantsIfc.TAX_OVERRIDE_PERCENT_LABEL,
								dataArgs));
				/*
				 * message = "\nTRANS: Tax Override" + "\n Tax Override % " +
				 * tax.getOverrideRate() * 100 + reasonCode + reasonText;
				 */
				break;
			}
			} // switch tax mode
			sb.append(message);
		}  // not standard

        if (trans.getDefaultRegistry() != null)
        {
        	 dataArgs[0] =  trans.getDefaultRegistry().getID();
        	sb.append(Util.EOL)
              .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANS_GIFT_REG_TAG_LABEL, null))
              .append(Util.EOL)
              .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_REG_TAG_LABEL, dataArgs));
        }
        sb.append(Util.EOL)
            .append(formatter.journalLineItems(trans))
            .append(Util.EOL);
        buf += sb.toString();



        // add totals, etc.
        dataArgs[0] = totals.getSubtotal().toGroupFormattedString();
        buf += I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SUBTOTAL_LABEL, dataArgs)+ EOL;

        sb = new StringBuffer(buf);
        formatter.formatTotals(trans, sb, totals, tax, pm);
        buf = sb.toString();

        dataArgs[0] = layaway.getCreationFee().toGroupFormattedString();
        buf += I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LAYAWAY_FEE_LABEL, dataArgs) + EOL + EOL;
        CurrencyIfc total =
            layaway.getBalanceDue().add(
            layaway.getTotalAmountPaid());
        dataArgs[0] = total.toGroupFormattedString();
        buf += I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_LABEL, dataArgs)+ EOL + EOL;
        dataArgs[0] = layaway.getTotalAmountPaid().toGroupFormattedString();
        buf += I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_AMOUNT_PAID_LABEL, dataArgs) + EOL + EOL;
        dataArgs[0] =  layaway.getDeletionFee().toGroupFormattedString();
        buf += I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DELETION_FEE_LABEL, dataArgs);

        // journal it or log error
        if (jmgr != null)
        {
            jmgr.journal(trans.getCashier().getLoginID(), trans.getTransactionID(), buf);
        }
        else
        {
            logger.error( "No JournalManager found");
        }
    }

    //--------------------------------------------------------------------------
    /**
        Creates a String line of amount information for journalling.
         <P>
        @param text the String to display at the start of the line
        @param amount the CurrencyIfc amount object for journalling
        @return String the formatted String for journalling
    **/
    //--------------------------------------------------------------------------
    protected String line(String text, CurrencyIfc amount)
    {
        String retStr = new String(text);
        String amountStr = amount.toGroupFormattedString();

        retStr += Util.SPACES.substring(retStr.length() + amountStr.length(), 38);


        return retStr + amountStr;
    }

}
