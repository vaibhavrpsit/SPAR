/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/journal/TransactionFormatter.java /main/32 2014/01/24 16:15:30 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   01/24/14 - Remove class cast since the return type can be
 *                         EntryMethod.
 *    mkutiana  02/20/13 - Fix for NPE at order tender process
 *    mkutiana  02/13/13 - Removed references to printChangeDue - deprecated it
 *    mkutiana  02/12/13 - Added RoundingAdjustment and ChangeGiven to the
 *                         journal
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   09/07/11 - masked personal id
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    hyin      08/24/11 - bug 12860810: change postvoid EJ to match new req.
 *    hyin      08/16/11 - change post void ejournal, bug 12860810
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    acadar    04/05/10 - use default locale for currency and date/time
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mchellap  04/07/09 - Fixed customer address EJ format
 *    nganesh   02/17/09 - Modified Void Transaction Ejournal to journal
 *                         ReasonCodeID
 *    nganesh   02/17/09 - Adding transactionID to the void transaction
 *    deghosh   02/05/09 - EJ i18n defect fixes
 *    deghosh   12/23/08 - EJ i18n changes
 *    vchengeg  12/16/08 - ej defect fixes
 *    ranojha   11/11/08 - Fixed expiration date for StoreCredit Issuance from
 *                         EJournal
 *    abondala  11/06/08 - updated files related to reason codes
 *    sswamygo  11/05/08 - Checkin after merges
 *
 * ===========================================================================
 * $Log:
 *  11   360Commerce 1.10        5/21/2007 9:16:18 AM   Anda D. Cadar   EJ
 *       changes
 *  10   360Commerce 1.9         4/25/2007 8:52:56 AM   Anda D. Cadar   I18N
 *       merge
 *
 *  9    360Commerce 1.8         3/29/2007 3:57:19 PM   Michael Boyd    CR
 *       26172 - v8x merge to trunk
 *
 *
 *       9    .v8x      1.7.1.0     2/8/2007 6:57:48 AM    Manas Sahu      CR#
 *       22742: EJournal should have "Subtotal' field header to be consistent
 *       with receipt in Post Void Transactions
 *
 *       Change in Line 423 and 424
 *
 *       CODE_NEEDS_TRUNK_MERGE
 *  8    360Commerce 1.7         7/20/2006 8:36:05 PM   Charles D. Baker CR
 *       6,102 - Merging from v7.x. Negating Sub-Total Reversed and Total
 *       Sales Tax Reversed in EJournal Post Voids of Sale Return
 *       transactions.
 *  7    360Commerce 1.6         3/28/2006 12:53:49 AM  Dinesh Gautam
 *       Appended 'Change Due' for Store Credit Tendered transaction
 *  6    360Commerce 1.5         2/24/2006 6:34:38 AM   Akhilashwar K. Gupta
 *       Updated formatTotalSalesTax () method to save the tax information in
 *       correct format in Journal for post void transaction
 *  5    360Commerce 1.4         1/25/2006 4:11:53 PM   Brett J. Larsen merge
 *       7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *  4    360Commerce 1.3         12/13/2005 4:42:32 PM  Barry A. Pape
 *       Base-lining of 7.1_LA
 *  3    360Commerce 1.2         3/31/2005 4:30:34 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:26:22 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:15:14 PM  Robert Pearse
 *
 *
 *
 *  5    .v710     1.2.2.0     9/21/2005 13:40:26     Brendan W. Farrell
 *       Initial Check in merge 67.
 *  4    .v700     1.2.3.0     10/31/2005 18:02:00    Deepanshu       CR 6102:
 *       Updated code to save the tax information in EJournal when a Returned
 *       transaction is post void transaction
 *  3    360Commerce1.2         3/31/2005 15:30:34     Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:26:22     Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:15:14     Robert Pearse
 *
 * Revision 1.8  2004/10/01 16:01:57  lzhao
 * @scr 7260: add scr number and modify text
 *
 * Revision 1.7  2004/09/30 18:21:50  lzhao
 * @scr add orignal transaction type and id in journal message of void transaction
 *
 * Revision 1.6  2004/04/27 20:50:13  epd
 * @scr 4513 Fixes for printing and journalling when forced cash change is present
 *
 * Revision 1.5  2004/04/08 20:33:02  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * Rev 1.0 Nov 04 2003 11:11:16 epd Initial revision.
 *
 * Rev 1.0 Oct 17 2003 12:31:24 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.journal;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.customer.IRSCustomerIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderStoreCreditADO;
import oracle.retail.stores.pos.ado.transaction.VoidTransactionADO;
import oracle.retail.stores.pos.journal.JournalFormatterManagerIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 *
 */
public class TransactionFormatter implements RegisterJournalFormatterIfc
{
    public static final int    LENGTH_OF_CURRENCY_DISPLAY_LINE = 38;
    public static final String TOTAL_TENDER_STRING = "Total Tender";
    public static final String TOTAL_REFUND_STRING = "Total Refund";
    public static final String STORE_CREDIT_ISSUED_STRING = "Store Credit Issued";
    public static final String CHANGE_DUE_STRING = "Change Due";
    public static final String SUBTOTAL_REVERSED_STRING = "Subtotal Reversed";
    public static final String TOTAL_SALES_TAX_REVERSED_STRING = " Total Sales Tax Reversed";
	protected static Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);

	protected static Object [] dataArgs;

    /**
     * Handle to the ParameterManagerIfc.
     */
    protected ParameterManagerIfc parameterManager;

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ado.journal.RegisterJournalFormatterIfc#format(oracle.retail.stores.pos.ado.journal.JournalTemplateIfc, oracle.retail.stores.pos.ado.journal.JournalableADOIfc, oracle.retail.stores.pos.ado.journal.JournalActionEnum)
     */
    public String format(
        JournalTemplateIfc template,
        JournalableADOIfc journalable,
        JournalActionEnum action)
    {
        // initialize the StringBuffer to a sufficient length to avoid costly
        // dynamic memory allocation while building string.
        StringBuffer sb = new StringBuffer(256);

        // invoke the proper message based on the action
        if (action == JournalActionEnum.CREATE)
        {
            formatStartTransaction(template, journalable, sb);
        }
        else if (action == JournalActionEnum.ORIG_TRANS)
        {
            formatOrignalTransaction(journalable, sb);
        }
        else if (action == JournalActionEnum.VOID_REASON_CODE)
        {
            formatVoidReasonCode(template, journalable, sb);
        }
        else if (action == JournalActionEnum.CANCEL)
        {
            formatCancelTransaction(template, journalable, sb);
        }
        else if (action == JournalActionEnum.TENDER_TOTAL)
        {
            formatTenderTotals(template, journalable, sb);
        }
        else if (action == JournalActionEnum.ORIG_TOTAL)
        {
        	formatTotalSalesTax(journalable, sb);
        }
        else if (action == JournalActionEnum.IRS_CUSTOMER)
        {
            formatIRSCustomer(template, journalable, sb);
        }
        return sb.toString();
    }

    /**
     * Creates a journal String for a new transaction
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatStartTransaction(
        JournalTemplateIfc template,
        JournalableADOIfc journalable,
        StringBuffer sb)
    {
        // Deferring to RDO journalling for now for expediency, but
        // This should really be doing all the formatting here.
        JournalFormatterManagerIfc formatterManager = null;
        if(Gateway.getDispatcher() != null)
        {
            formatterManager =
                (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);
        }
        TransactionIfc txnRDO = (TransactionIfc) ((ADO) journalable).toLegacy();
        if(formatterManager != null)
        {
            sb.append(formatterManager.toJournalString(txnRDO, parameterManager));
        }
        else
        {
			sb.append(txnRDO.toJournalString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
        }
    }

    /**
     * Journal String documents orignal transaction info for void transaction
     *
     * @param journalable
     * @param sb
     */
    protected void formatOrignalTransaction(JournalableADOIfc journalable,
                                            StringBuffer sb)
    {
        TransactionIfc txnRDO = (TransactionIfc) ((ADO) journalable).toLegacy();
        if ( txnRDO instanceof VoidTransactionIfc )
        {
            VoidTransactionIfc voidTrans = (VoidTransactionIfc)txnRDO;
            TransactionIfc origTrans = voidTrans.getOriginalTransaction();

			dataArgs = new Object[]{TransactionConstantsIfc.TYPE_DESCRIPTORS[origTrans.getTransactionType()]};
			sb.append(Util.EOL);
			sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORIG_TRANS_TYPE_LABEL, dataArgs,journalLocale));

			dataArgs = new Object[]{origTrans.getTransactionID()};
			sb.append(Util.EOL);
			sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORIGINAL_TRANS_LABEL, dataArgs,journalLocale));

			if (origTrans instanceof RetailTransactionIfc &&
					((RetailTransactionIfc)origTrans).getIRSCustomer() != null)
			{
				sb.append(Util.EOL);
				sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORIGINAL_TRANSACTION_CONTAINED_IRS_INFO_LABEL, null,journalLocale));
            }
        }
    }

    /**
     * Journal String documents reason code for void transaction
     * 
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatVoidReasonCode(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuffer sb)
    {
        Map<String,Object> memento = journalable.getJournalMemento();

        String reasonCode = (String)memento.get(JournalConstants.VOID_REASON_CODE);

        VoidTransactionADO voidTrans = (VoidTransactionADO)journalable;

        String reasonCodeID = voidTrans.getLocalizedReasonCode().getCode();

        dataArgs = new Object[] { "" };
        sb.append(Util.EOL);
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.VOID_TRANS_LABEL, dataArgs,
                journalLocale));

        String args = reasonCodeID + "---" + reasonCode;
        sb.append(Util.EOL);
        dataArgs = new Object[] { args };
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.REASON_CODE_LABEL, dataArgs,
                journalLocale));
        sb.append(Util.EOL);
    }

    /**
     * Adds the canceled transaction journal entry when a transaction is
     * canceled
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatCancelTransaction(
        JournalTemplateIfc template,
        JournalableADOIfc journalable,
        StringBuffer sb)
    {

		sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_CANCELED_LABEL, dataArgs,journalLocale));
		sb.append(Util.EOL);
    }

    /**
     * Journal the tender totals. Note that this
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatTenderTotals(
        JournalTemplateIfc template,
        JournalableADOIfc journalable,
        StringBuffer sb)
    {
        Map<String,Object> txnMap = journalable.getJournalMemento();

        int transType = ((Integer)txnMap.get(JournalConstants.TRANSACTION_RDO_TYPE));
        CurrencyIfc tenderTotal =
            (CurrencyIfc) txnMap.get(JournalConstants.TOTAL_TENDER);
        String tenderTotalStr =
			tenderTotal.toGroupFormattedString();

        // do not journal this for layaway initiate, layaway delete, or order
        // cancel.
        if (transType != TransactionIfc.TYPE_LAYAWAY_INITIATE
            && transType != TransactionIfc.TYPE_LAYAWAY_DELETE
            && transType != TransactionIfc.TYPE_ORDER_CANCEL)
        {
			dataArgs = new Object[]{tenderTotalStr};
			sb.append(Util.EOL).append(Util.EOL)
				.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_TENDER_LABEL, dataArgs,journalLocale));
			sb.append(Util.EOL);

        }

        CurrencyIfc balance =
            (CurrencyIfc) txnMap.get(JournalConstants.BALANCE);
        if (transType == TransactionIfc.TYPE_LAYAWAY_DELETE
            || transType == TransactionIfc.TYPE_ORDER_CANCEL
            || ((transType == TransactionIfc.TYPE_ORDER_COMPLETE
                || transType == TransactionIfc.TYPE_ORDER_PARTIAL)
                && balance.signum() == CurrencyIfc.NEGATIVE))
        {
			dataArgs = new Object[]{tenderTotalStr};
			sb.append(Util.EOL)
				.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_REFUND_LABEL, dataArgs,journalLocale));
			sb.append(Util.EOL);
        }

        // Have any store credits been issued?
        if (txnMap.get(JournalConstants.ISSUED_STORE_CREDITS) != null)
        {
            List storeCredits = (List)txnMap.get(JournalConstants.ISSUED_STORE_CREDITS);
            for (Iterator iter = storeCredits.iterator(); iter.hasNext();)
            {
                TenderStoreCreditADO storeCredit =
                    (TenderStoreCreditADO) iter.next();

				dataArgs = new Object[]{storeCredit.getAmount().toGroupFormattedString()};
				sb.append(Util.EOL)
					.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.STORE_CREDIT_ISSUED_LABEL, dataArgs,journalLocale));
				sb.append(Util.EOL);

				Map storeCreditMap = storeCredit.getJournalMemento();

				dataArgs = new Object[]{(String) storeCreditMap.get(TenderConstants.NUMBER)};
				sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NUMBER_LABEL, dataArgs,journalLocale));
				sb.append(Util.EOL);


				if (storeCreditMap.get(TenderConstants.ENTRY_METHOD) != null)
				{
					dataArgs = new Object[]{storeCreditMap.get(TenderConstants.ENTRY_METHOD)};
					sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ENTRY_METHOD_LABEL, dataArgs,journalLocale));
					sb.append(Util.EOL);
                }
                else
                {
					dataArgs = new Object[]{I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NOTAPPLICABLE_LABEL, null,journalLocale)};
					sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ENTRY_LABEL, dataArgs,journalLocale));
					sb.append(Util.EOL);
                }

                if (storeCreditMap.get(TenderConstants.EXPIRATION_DATE)
                    != null)
                {
					dataArgs = new Object[]{(String) storeCreditMap.get(TenderConstants.EXPIRATION_DATE)};
					sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.EXPIRATION_DATE_LABEL, dataArgs,journalLocale));
					sb.append(Util.EOL);
                }

                TenderStoreCreditIfc tsc = (TenderStoreCreditIfc) storeCredit.toLegacy();

                if (tsc.getFirstName() != null && !(tsc.getFirstName().equals("")))
                {
					dataArgs = new Object[]{tsc.getFirstName()};
					sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.FIRST_NAME_LABEL, dataArgs,journalLocale));
					sb.append(Util.EOL);
                }

                if (tsc.getLastName() != null && !(tsc.getLastName().equals("")))
                {
					dataArgs = new Object[]{tsc.getLastName()};
					sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LAST_NAME_LABEL, dataArgs,journalLocale));
					sb.append(Util.EOL);
                }

                if (tsc.getPersonalIDType(journalLocale) != null && !(tsc.getPersonalIDType(journalLocale).equals("")))
                {
					dataArgs = new Object[]{tsc.getPersonalIDType(journalLocale)};
					sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ID_TYPE_LABEL, dataArgs,journalLocale));
					sb.append(Util.EOL);
                }
            }
            if (storeCredits.size() == 0)
            {
	            CurrencyIfc change = (CurrencyIfc)txnMap.get(JournalConstants.TOTAL_CHANGE);	            
	            appendCurrencytoSB(sb, change, false, JournalConstantsIfc.CHANGE_DUE_LABEL);
            }
        }
        else
        {
            // The change due is a sum of the balance due PLUS
            // forced cash tender (negative cash tender) due to
            // depleted gift cards.
            CurrencyIfc change = (CurrencyIfc)txnMap.get(JournalConstants.TOTAL_CHANGE);            
            appendCurrencytoSB(sb, change, false, JournalConstantsIfc.CHANGE_DUE_LABEL);
            
            CurrencyIfc roundingAdjustment = (CurrencyIfc)txnMap.get(JournalConstants.ROUNDING_ADJUSTMENT);
            CurrencyIfc changeGiven = (CurrencyIfc)txnMap.get(JournalConstants.CHANGE_GIVEN);
            if ( roundingAdjustment != null && roundingAdjustment.signum() != CurrencyIfc.ZERO)
            {
                appendCurrencytoSB(sb, roundingAdjustment, false, JournalConstantsIfc.JOURNAL_ENTRY_PREFIX + JournalConstants.ROUNDING_ADJUSTMENT);
                appendCurrencytoSB(sb, changeGiven, true, JournalConstantsIfc.JOURNAL_ENTRY_PREFIX + JournalConstants.CHANGE_GIVEN);
            }
        }
    }
    /**
	 * Journal String documents total sales tax info for void transaction
	 * @param journalable
	 * @param sb
	 */
	protected void formatTotalSalesTax(JournalableADOIfc journalable, StringBuffer sb)
	{
		TransactionIfc txnRDO = (TransactionIfc) ((ADO) journalable).toLegacy();
        JournalFormatterManagerIfc formatterManager =
            (JournalFormatterManagerIfc)Gateway.getDispatcher().getManager(JournalFormatterManagerIfc.TYPE);
		//Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
		if (txnRDO instanceof VoidTransactionIfc)
		{
			VoidTransactionIfc voidTrans = (VoidTransactionIfc) txnRDO;
            formatterManager.formatTotals(voidTrans, sb, parameterManager);
			/*TransactionIfc origTrans = voidTrans.getOriginalTransaction();
			if (origTrans instanceof SaleReturnTransactionIfc)
			{
				TransactionTotalsIfc totals = ((SaleReturnTransactionIfc) origTrans).getTransactionTotals();
				//I18 adc: I don't understand why we negate then we remove the paranthesis
				CurrencyIfc subTotalAmount = totals.getPreTaxSubtotal().negate();
				String subTotal = subTotalAmount.toGroupFormattedString(locale);
                sb.append(SUBTOTAL_REVERSED_STRING);
                int numSpaces = LENGTH_OF_CURRENCY_DISPLAY_LINE - SUBTOTAL_REVERSED_STRING.length();
				sb.append(Util.SPACES.substring(subTotal.length(), numSpaces)).append(
						subTotal);

				//i18n changes: why do we negate then remove paranthesis?
				CurrencyIfc taxTotalAmount = totals.getTaxTotal().negate();
				String taxTotal = taxTotalAmount.toGroupFormattedString(locale);
				sb.append(Util.EOL).append(TOTAL_SALES_TAX_REVERSED_STRING);
                numSpaces = LENGTH_OF_CURRENCY_DISPLAY_LINE - TOTAL_SALES_TAX_REVERSED_STRING.length();
				sb.append(Util.SPACES.substring(taxTotal.length(), numSpaces)).append(
						taxTotal);
			}*/
		}
	}

	/**
	 * Converts the taxAmount for display purpose by removing parentheses from the input taxAmount. <P>
	 * @return String taxAmount to display
	 * @param taxAmount String
	 */
	protected String getformatedTotal(String taxAmount)
	{
		if ((taxAmount.indexOf('(') == 0) && (taxAmount.indexOf(')') == (taxAmount.length() - 1)))
		{
			taxAmount = taxAmount.substring(1, taxAmount.length() - 1);
		}
		return taxAmount;
	}

    /**
     * Journal the IRS Customer if attached.
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatIRSCustomer(
        JournalTemplateIfc template,
        JournalableADOIfc journalable,
        StringBuffer sb)
    {
        Map<String,Object> txnMap = journalable.getJournalMemento();
		String data;
        IRSCustomerIfc irsCustomer = (IRSCustomerIfc)txnMap.get(JournalConstants.IRS_CUSTOMER);

        if (irsCustomer != null)
        {
            sb.append(Util.EOL);
			sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.IRS_CUSTOMER_LABEL,dataArgs,journalLocale)).append(Util.EOL);


			data = irsCustomer.getFirstName();
			dataArgs=new Object[]{data==null?"null":data};
			sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.FIRST_NAME_LABEL,dataArgs,journalLocale));
			sb.append(Util.EOL);

			if (!Util.isEmpty(irsCustomer.getMiddleName()))
			{
				data = irsCustomer.getMiddleName();
				dataArgs=new Object[]{data==null?"null":data};
				sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.MIDDLE_INITIAL_LABEL,dataArgs,journalLocale));
				sb.append(Util.EOL);
			}
			data = irsCustomer.getLastName();
			dataArgs=new Object[]{data==null?"null":data};
			sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.LAST_NAME_LABEL,dataArgs,journalLocale));
			sb.append(Util.EOL);

			data = irsCustomer.getBirthdate().toFormattedString();
			dataArgs=new Object[]{data==null?"null":data};
			sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.DOB_LABEL,dataArgs,journalLocale));
			sb.append(Util.EOL);

			data = irsCustomer.getEncipheredTaxID().getMaskedNumber();
			dataArgs=new Object[]{data==null?"null":data};
			sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.TAXPAYER_ID_LABEL,dataArgs,journalLocale));
			sb.append(Util.EOL);

			data = irsCustomer.getOccupation();
			dataArgs=new Object[]{data==null?"null":data};
			sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.OCCUPATION_LABEL,dataArgs,journalLocale));
			sb.append(Util.EOL);

			sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.ADDRESS_LABEL,dataArgs,journalLocale));
			sb.append(" ");
            List<AddressIfc> addresses = irsCustomer.getAddressList();
            if (addresses.size() > 0)
            {
                AddressIfc address = addresses.get(0);
                for (Iterator<String> i = address.getLinesIterator(); i.hasNext();)
                {
                    String addressLine = i.next();
                    if (!Util.isEmpty(addressLine))
                    {
                        sb.append(addressLine).append(Util.EOL);
                    }
                }
                sb.append(address.getCity()).append(", ");
                sb.append(address.getState()).append(" ");
                sb.append(address.getPostalCode());
                if (!Util.isEmpty(address.getPostalCodeExtension()))
                {
                    sb.append("-").append(address.getPostalCodeExtension());
                }
                sb.append(Util.EOL);
                sb.append(address.getCountry()).append(Util.EOL);
            }


			data = irsCustomer.getLocalizedPersonalIDCode().getText(journalLocale);
			dataArgs=new Object[]{data==null?"null":data};
			sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.VALIDATING_ID_TYPE_LABEL,dataArgs,journalLocale));
			sb.append(Util.EOL);

			data = irsCustomer.getVerifyingID().getMaskedNumber();
			dataArgs=new Object[]{data==null?"null":data};
			sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.VALIDATING_ID_NUMBER_LABEL,dataArgs,journalLocale));
			sb.append(Util.EOL);

			data = irsCustomer.getVerifyingIdIssuingCountry();
			dataArgs=new Object[]{data==null?"null":data};
			sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.VALIDATING_ID_COUNTRY_LABEL,dataArgs,journalLocale));
			sb.append(Util.EOL);

			if (!Util.isEmpty(irsCustomer.getVerifyingIdIssuingState()))
			{
				data = irsCustomer.getVerifyingIdIssuingState();
				dataArgs=new Object[]{data==null?"null":data};
				sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.VALIDATING_ID_STATE_LABEL,dataArgs,journalLocale));
				sb.append(Util.EOL);
			}
        }
    }

    /**
     * @param label The name of the value
     * @param value The value to be recorded
     * @return The label value pair.
     */
    protected String safeValue(String label, String value)
    {
        if (value == null)
        {
            value = "null";
        }
        StringBuffer entry = new StringBuffer(label);
        entry.append(": ").append(value).append(Util.EOL);
        return(entry.toString());

    }

    /**
     * @param sb Ejournal text
     * @param change Amount left as change
     * @deprecated As of release 14.0, replaced by {@link #appendCurrencytoSB(StringBuffer sb, CurrencyIfc value, boolean negate, String localePropertyKey)}
     */
    protected void printChangeDue(StringBuffer sb,CurrencyIfc change)
    {
		appendCurrencytoSB(sb, change, false, JournalConstantsIfc.CHANGE_DUE_LABEL);
    }
    
    /**
     * Utility method to print currency to journal
     * @param sb StringBuffer Ejournal to add currency value to
     * @param value currency value to be appended to buffer
     * @param negate boolean if currency is to be negated
     * @param localePropertyKey Display label for the currency value
     */
    protected void appendCurrencytoSB(StringBuffer sb, CurrencyIfc value, boolean negate, String localePropertyKey)
    {
        if (negate){
            value = value.negate();        
        }
        dataArgs = new Object[]{value.toGroupFormattedString()};
        sb.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, localePropertyKey, dataArgs, journalLocale));
    }

    /**
     * Sets the ParmaeterManager instance.
     * @param pm
     */
    public void setParameterManager(ParameterManagerIfc pm)
    {
        parameterManager = pm;
    }


}
