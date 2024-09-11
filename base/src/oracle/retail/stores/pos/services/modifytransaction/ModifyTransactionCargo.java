/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/ModifyTransactionCargo.java /main/25 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/22/14 - Refactor to restore Fulfillment main option flow.
 *    blarsen   05/21/12 - Refactored updateTransactionSalesAssociate() to make
 *                         it callable from MPOS.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/22/11 - nullpointer exception fix
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    nkgautam  10/14/10 - removed duplicate cashdrawer warning boolean
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    cgreene   03/26/10 - replace linefeed with Util EOL and deleted commented
 *                         code
 *    nganesh   02/09/10 - Ejournal formatted in updateTransactionTax
 *    nkgautam  02/01/10 - Added attribute for cash under warning message
 *    abondala  01/03/10 - update header date
 *    nkgautam  04/08/09 - EJ Fixes for extra space and reason codes
 *    djenning  03/28/09 - Enter a single line comment: creating
 *                         isSalesAssociateModifiedAtLineItem(), which is
 *                         similar to getSalesAssociateModified(), and using it
 *                         at receipt to determine whether to print the
 *                         SalesAssociate at the line item. Jack warned against
 *                         modifying the existing method as it is used for
 *                         something else.
 *    vikini    02/03/09 - Incorporating Code review Comments
 *    aariyer   02/02/09 - Added files for Item Basket feature
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         3/25/2008 4:04:51 AM   Vikram Gopinath CD
 *         #29942, ported changes from v12x. Set the
 *         salesAssociateModifiedFlag at the transaction level.
 *    8    360Commerce 1.7         8/13/2007 3:01:32 PM   Charles D. Baker CR
 *         27803 - Remove unused domain property.
 *    7    360Commerce 1.6         7/12/2007 10:55:19 AM  Anda D. Cadar
 *         replaced $ with Amt.
 *    6    360Commerce 1.5         7/10/2007 4:51:51 PM   Charles D. Baker CR
 *         27506 - Updated to remove old fix for truncating extra decimal
 *         places that are used for accuracy. Truncating is no longer
 *         required.
 *    5    360Commerce 1.4         5/21/2007 9:16:21 AM   Anda D. Cadar   EJ
 *         changes
 *    4    360Commerce 1.3         1/22/2006 11:45:12 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse
 *
 *   Revision 1.11  2004/09/27 22:32:04  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.10  2004/08/09 14:50:44  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.9  2004/07/31 18:03:21  jdeleau
 *   @scr 6640 Fix sales associate not being applied to the right
 *   item after multiple transaction->sales associate modifications.
 *
 *   Revision 1.8  2004/07/28 15:18:55  rsachdeva
 *   @scr 4865 Transaction Sales Associate
 *
 *   Revision 1.7  2004/06/03 14:47:45  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:17:06  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/12 18:52:57  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/02/24 16:21:30  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:09  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Jan 15 2004 12:37:38   lzhao
 * update journal and print info when RequireCertificateInfo parameter is set to N.
 * Resolution for 3655: Feature Enhancement:  Tax Exempt Enhancement
 *
 *    Rev 1.0   Aug 29 2003 16:02:10   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Feb 14 2003 16:25:52   crain
 * Refactored getReasonCodeValue()
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.1   Jan 20 2003 17:35:40   sfl
 * Make sure that the transaction level tax override amount
 * is displayed with two digits after decimal point in EJ.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.0   Apr 29 2002 15:14:04   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:38:16   msg
 * Initial revision.
 *
 *    Rev 1.3   27 Feb 2002 13:45:58   baa
 * avoid null pointer when journaling with previous salesAssociate
 * Resolution for POS SCR-1426: Making several transaction level changes crashes the Beetle
 *
 *    Rev 1.2   Feb 05 2002 16:42:50   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.1   31 Jan 2002 17:00:20   sfl
 * Make sure null transaction is not used for retrieveing
 * information for journaling.
 * Resolution for POS SCR-669: Gift Registry screens inconsistent with requirements
 *
 *    Rev 1.0   Sep 21 2001 11:30:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;

import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.itembasket.BasketDTO;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.log4j.Logger;

/**
 * Cargo class for ModifyTransaction service.
 *
 * @version $Revision: /main/25 $
 */
public class ModifyTransactionCargo extends AbstractFinancialCargo implements CodeConstantsIfc
{
    private static final long serialVersionUID = -8332831882458663975L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ModifyTransactionCargo.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/25 $";

    /**
     * transaction object
     */
    protected RetailTransactionIfc transaction;

    /**
     * sales associate
     */
    protected EmployeeIfc salesAssociate;

    /**
     * flag to see if cargo is to update parent cargo(SaleCargo)
     */
    protected boolean updateParentCargoFlag = false;

    /**
     * This vector contains a list of SaleReturnTransacions on which returns
     * have been completed. It will be used if a transaction with returned
     * lineitems is retrieved.
     */
    protected Vector<SaleReturnTransactionIfc> originalReturnTransactions = null;
    /**
     * false if no override is requested, true is override is needed
     */
    protected boolean securityOverrideFlag = false;

    /**
     * employee granting Security override
     */
    protected EmployeeIfc securityOverrideEmployee;

    /**
     * employee attempting Security override
     */
    protected EmployeeIfc securityOverrideRequestEmployee;

    /**
     * employee attempting Access
     */
    protected EmployeeIfc accessEmployee;

    /**
     * Security override Return Letter
     */

    protected String securityOverrideReturnLetter;

    /**
     * sales associate set using modify transaction sales associate
     */
    protected boolean salesAssociateAlreadySet = false;

    /**
     * line items
     */
    protected SaleReturnLineItemIfc[] items;

    /**
     * Basket DTO
     */
    protected BasketDTO itemBasketDTO = null;

    /**
     * From fulfillment flag.  Default is false.
     */
    protected boolean fromFulfillment = false;

    /**
     * Constructs ModifyTransactionCargo object.
     */
    public ModifyTransactionCargo()
    {
    }

    /**
     * Updates transaction, items with the new sales associate.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>Update policy set
     * <LI>Items checked for modifications
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>none
     * </UL>
     *
     * @param newSalesAssociate new sales associate
     * @param updateAllItemsFlag flag indicating update policy
     * @return int number of items updated
     */
    public int updateTransactionSalesAssociate(BusIfc bus, EmployeeIfc newSalesAssociate, boolean updateAllItemsFlag)
    {
        return updateTransactionSalesAssociate(bus, transaction, newSalesAssociate, updateAllItemsFlag);
    }

    /**
     * Updates transaction, items with the new sales associate.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>Update policy set
     * <LI>Items checked for modifications
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>none
     * </UL>
     *
     * @param transaction the transaction to update
     * @param newSalesAssociate new sales associate
     * @param updateAllItemsFlag flag indicating update policy
     * @return int number of items updated
     */
    static public int updateTransactionSalesAssociate(BusIfc bus, RetailTransactionIfc transaction, EmployeeIfc newSalesAssociate, boolean updateAllItemsFlag)
    {
        // get items from transaction
        Vector<AbstractTransactionLineItemIfc> lineItems = transaction.getLineItemsVector();
        // line item object, update counter
        SaleReturnLineItemIfc srli;
        int updateCounter = 0;
        // set up enumeration
        Enumeration<AbstractTransactionLineItemIfc> e = lineItems.elements();
        // set up loop
        while (e.hasMoreElements())
        {
            // get element
            srli = (SaleReturnLineItemIfc)e.nextElement();
            // check here to see if the element is an instance of
            // OrderLineItemIfc
            // if so, that element should not be updated.
            if (!(srli instanceof OrderLineItemIfc))
            {
                // check update-all-items flag
                if (updateAllItemsFlag)
                {
                    // if updating all items, set new gift registry and
                    // set modified flag to false
                    srli.setSalesAssociate(newSalesAssociate);
                    srli.setSalesAssociateModifiedFlag(!newSalesAssociate.getEmployeeID().equals(
                            transaction.getCashier().getEmployeeID()));

                    // we are updating the line items to have the same
                    // associate. as the transaction. so this should always be
                    // false.
                    srli.setSalesAssociateModifiedAtLineItem(false);
                }
                // check if item has been modified
                else if (!srli.getSalesAssociateModifiedFlag())
                {
                    // update line item
                    srli.setSalesAssociate(newSalesAssociate);
                    srli.setSalesAssociateModifiedFlag(!newSalesAssociate.getEmployeeID().equals(
                            transaction.getCashier().getEmployeeID()));

                    // we are updating the line items to have the same associate
                    // as the transaction. so this should always be false.
                    srli.setSalesAssociateModifiedAtLineItem(false);

                    updateCounter++;
                }
                else
                {
                    // update the unchanging line items to reflect whether or
                    // not they have the same sales associate as the
                    // transaction.
                    srli.setSalesAssociateModifiedAtLineItem(!newSalesAssociate.getEmployeeID().equals(
                            srli.getSalesAssociate().getEmployeeID()));
                }
            }
        }

        // update transaction sales associate
        EmployeeIfc oldSalesAssociate = transaction.getSalesAssociate();
        transaction.setSalesAssociate(newSalesAssociate);
        if (transaction instanceof SaleReturnTransactionIfc)
        {
            ((SaleReturnTransactionIfc)transaction).setSalesAssociateModifiedFlag(true);
        }

        String oldAssociateID = "";
        if (oldSalesAssociate != null)
        {
            oldAssociateID = oldSalesAssociate.getEmployeeID();
        }
        // make journal entry
        JournalManagerIfc mgr;
        mgr = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        // String message = "TRANS: Sales Assoc.\n"
        // + "  Sales Assoc.: " + oldAssociateID + " Removed\n"
        // + "  Sales Assoc.: " + newSalesAssociate.getEmployeeID();
        //

        String message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.TRANSACTION_SALESASSOC_LABEL, null);
        message += Util.EOL;

        Object dataArgs[] = { oldAssociateID };
        String oldRemovedSalesAssoc = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.TRANSACTION_SALESASSOC_REMOVED, dataArgs);

        Object salesAssocDataArgs[] = { newSalesAssociate.getEmployeeID() };
        String salesAssocID = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.TRANSACTION_SALESASSOC_ID, salesAssocDataArgs);

        message += oldRemovedSalesAssoc;
        message += Util.EOL + salesAssocID;

        mgr.journal(transaction.getCashier().getLoginID(), transaction.getTransactionID(), message);

        return (updateCounter);
    }

    /**
     * Updates transaction, items with new gift registry values, implementing
     * update policy.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>Update policy set
     * <LI>New gift registry value set
     * <LI>Items checked for modifications
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>none
     * </UL>
     *
     * @param newGiftRegistry new gift registry
     * @param updateAllItemsFlag flag indicating update policy
     * @return int number of items updated
     */
    public int updateTransactionGiftRegistry(BusIfc bus, RegistryIDIfc newGiftRegistry, boolean updateAllItemsFlag)
    {
        SaleReturnTransactionIfc trans = (SaleReturnTransactionIfc)transaction;
        int updateCounter = 0;
        // Get Journal Manager
        String message = null;
        JournalManagerIfc mgr;
        mgr = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        if (trans != null)
        {
            if (trans.getDefaultRegistry() != null)
            {
                // make journal entry for old gift registry
                // message = "TRANS: Gift Reg."
                // + "\n  Gift Reg. Removed "
                // + trans.getDefaultRegistry().getID();

                // TRANSACTION_GIFT_REG
                // TRANSACTION_GIFTREG_REMOVED
                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANSACTION_GIFT_REG, null);
                message += Util.EOL;

                Object dataArgs[] = { trans.getDefaultRegistry().getID() };
                String giftRegisterRemoved = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANSACTION_GIFTREG_REMOVED, dataArgs);

                message += giftRegisterRemoved;

                mgr.journal(trans.getCashier().getEmployeeID(), trans.getTransactionID(), message);
            }

            // get items from transaction
            Vector<AbstractTransactionLineItemIfc> lineItems = trans.getLineItemsVector();
            // line item object, update counter
            SaleReturnLineItemIfc srli;
            updateCounter = 0;
            // set up enumeration
            Enumeration<AbstractTransactionLineItemIfc> e = lineItems.elements();
            // set up loop
            while (e.hasMoreElements())
            {
                // get element
                srli = (SaleReturnLineItemIfc)e.nextElement();

                // see if item is eligible for gift registry
                if (!srli.getPLUItem().getItemClassification().isRegistryEligible())
                {
                    continue;
                }

                // check update-all-items flag
                if (updateAllItemsFlag)
                {
                    // if updating all items, set new gift registry and
                    // set modified flag to false
                    srli.modifyItemRegistry(newGiftRegistry, false);
                }
                // check if item has been modified
                else if (!srli.getRegistryModifiedFlag())
                {
                    // update line item
                    srli.modifyItemRegistry(newGiftRegistry, false);
                    updateCounter++;
                }
            }

            // update transaction default gift registry
            trans.setDefaultRegistry(newGiftRegistry);

            // make journal entry for new gift registry
            // message = "TRANS: Gift Reg."
            // + "\n  Gift Reg. "
            // + newGiftRegistry.getID();

            message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_GIFT_REG,
                    null);
            message += Util.EOL
                    + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_REG, null);
            message += newGiftRegistry.getID();

            mgr.journal(trans.getCashier().getEmployeeID(), trans.getTransactionID(), message);

        }
        return (updateCounter);
    }

    /**
     * Updates transaction with tax changes.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>needs specification
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>none
     * </UL>
     *
     * @param newTax new transaction tax settings
     * @param updateAllItemsFlag flag indicating all items should be updated
     */
    public void updateTransactionTax(BusIfc bus, TransactionTaxIfc newTax, boolean updateAllItemsFlag)
    {
        /*
         * A lot of the methods needed here aren't available in
         * RetailTransactionIfc (yet), so we need to directly access a
         * SaleReturnTransactionIfc
         */
        SaleReturnTransactionIfc saleReturnTransaction = (SaleReturnTransactionIfc)transaction;

        Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);

        // Grab journal manager
        JournalManagerIfc mgr = null;
        mgr = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        // journal tax changes
        if (mgr != null)
        {
            // journal old transaction tax
            TransactionTaxIfc oldTax = saleReturnTransaction.getTransactionTax();
            String message = null;

            String reasonCode = oldTax.getReason().getText(journalLocale);
            Object reasonCodeDataArgs[] = { reasonCode };
            String reason = Util.EOL
                    + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.REASON_CODE_LABEL,
                            reasonCodeDataArgs);

            switch (oldTax.getTaxMode())
            {
            case TaxIfc.TAX_MODE_EXEMPT:
            {
                String customer = "";
                if (saleReturnTransaction.getCustomer() != null)
                {
                    Object customerDataArgs[] = { saleReturnTransaction.getCustomer().getCustomerID() };
                    customer = Util.EOL
                            + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CUSTOMER_ID,
                                    customerDataArgs);
                }

                Object taxDataArgs[] = { oldTax.getTaxExemptCertificateID() };
                String removedTaxCert = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANSACTION_REMOVED_TAX_CERT, taxDataArgs);

                message = new StringBuilder(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANSACTION_TAX_EXEMPT, null))
                        + customer
                        + Util.EOL
                        + removedTaxCert
                        + reason + Util.EOL;
                break;
            }
            case TaxIfc.TAX_MODE_OVERRIDE_AMOUNT:
            {
                String taxOverrideAmount = "";
                taxOverrideAmount = oldTax.getOverrideAmount().toString();

                Object taxDataArgs[] = { taxOverrideAmount };
                String removedTaxOverrideAmt = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANSACTION_REMOVED_TAX_OVERRIDE_AMT, taxDataArgs);

                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANSACTION_TAX_OVERRIDE, null)
                        + Util.EOL
                        + removedTaxOverrideAmt
                        + reason
                        + Util.EOL;
                break;
            }
            case TaxIfc.TAX_MODE_OVERRIDE_RATE:
            {

                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANSACTION_TAX_OVERRIDE, null);

                Object taxDataArgs[] = { formatTaxRate(oldTax.getOverrideRate()) };
                String removedTaxOverridePer = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANSACTION_REMOVED_TAX_OVERRIDE_PER, taxDataArgs);

                message += Util.EOL + removedTaxOverridePer + reason + Util.EOL;

                break;
            }
            case TaxIfc.TAX_MODE_STANDARD:
            default:
            {
                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANSACTION_TAX_LABEL, null);

                Object taxDataArgs[] = { formatTaxRate(oldTax.getDefaultRate()) };
                String removedTaxPer = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANSACTION_REMOVED_TAX_PER, taxDataArgs);

                message += Util.EOL + removedTaxPer + Util.EOL;

                break;
            }
            }

            mgr.journal(saleReturnTransaction.getCashier().getEmployeeID(), saleReturnTransaction.getTransactionID(),
                    message);

            // journal new transaction tax
            message = null;
            switch (newTax.getTaxMode())
            {
            case TaxIfc.TAX_MODE_EXEMPT:
            {
                if (newTax.getReason().getCode().equals(CodeConstantsIfc.CODE_UNDEFINED))
                {
                    reason = "";
                }
                else
                {
                    String reasonCodeValue = newTax.getReason().getText(journalLocale);
                    Object reasonCodeValueDataArgs[] = { reasonCodeValue };
                    reason = Util.EOL
                            + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                    JournalConstantsIfc.REASON_CODE_LABEL, reasonCodeValueDataArgs);
                }
                String customer = "";
                if (saleReturnTransaction.getCustomer() != null)
                {
                    Object customerDataArgs[] = { saleReturnTransaction.getCustomer().getCustomerID() };
                    customer = Util.EOL
                            + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CUSTOMER_ID,
                                    customerDataArgs);
                }

                String certificate = "";
                if (newTax.getTaxExemptCertificate().getMaskedNumber() != null
                        && newTax.getTaxExemptCertificate().getMaskedNumber().length() > 0)
                {
                    Object taxDataArgs[] = { newTax.getTaxExemptCertificate().getMaskedNumber() };
                    certificate = Util.EOL
                            + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                    JournalConstantsIfc.TRANSACTION_TAX_CERTIFICATE_LABEL, taxDataArgs);
                }

                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANSACTION_TAX_EXEMPT, null) + customer + certificate + reason + Util.EOL;

                break;
            }
            case TaxIfc.TAX_MODE_OVERRIDE_AMOUNT:
            {
                String taxOverrideAmount = "";
                taxOverrideAmount = newTax.getOverrideAmount().toGroupFormattedString();

                String reasonCodeValue = newTax.getReason().getText(journalLocale);
                Object reasonCodeValueDataArgs[] = { reasonCodeValue };
                reason = Util.EOL
                        + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.REASON_CODE_LABEL,
                                reasonCodeValueDataArgs);

                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANSACTION_TAX_OVERRIDE, null)
                        + Util.EOL
                        + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.TRANSACTION_TAX_OVERRIDE_AMT, null)
                        + taxOverrideAmount
                        + reason
                        + Util.EOL;

                break;
            }
            case TaxIfc.TAX_MODE_OVERRIDE_RATE:
            {
                String reasonCodeValue = newTax.getReason().getText(journalLocale);
                Object reasonCodeValueDataArgs[] = { reasonCodeValue };
                reason = Util.EOL
                        + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.REASON_CODE_LABEL,
                                reasonCodeValueDataArgs);

                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANSACTION_TAX_OVERRIDE, null)
                        + Util.EOL
                        + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.TRANSACTION_TAX_OVERRIDE_PERC, null)
                        + " "
                        + formatTaxRate(newTax.getOverrideRate()) + reason + Util.EOL;

                break;
            }
            case TaxIfc.TAX_MODE_STANDARD:
            default:
            {
                message = "TRANS: Tax" + "\n  Tax % Restored " + formatTaxRate(newTax.getDefaultRate());

                Object taxRestoredDataArgs[] = { formatTaxRate(newTax.getDefaultRate()) };
                String taxRestored = Util.EOL
                        + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.TRANSACTION_TAX_RESTORED, taxRestoredDataArgs);

                message = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANSACTION_TAX_LABEL, null) + Util.EOL + taxRestored + Util.EOL;

                break;
            }
            }

            mgr.journal(saleReturnTransaction.getCashier().getEmployeeID(), saleReturnTransaction.getTransactionID(),
                    message);
        }

        // update tax based on tax mode
        switch (newTax.getTaxMode())
        {
        case TaxIfc.TAX_MODE_OVERRIDE_AMOUNT:
            if (logger.isInfoEnabled())
                logger.info("Overriding transaction tax amount ...");
            // override tax amount on transaction
            saleReturnTransaction.overrideTaxAmount(newTax.getOverrideAmount(), updateAllItemsFlag, newTax.getReason());
            break;

        case TaxIfc.TAX_MODE_OVERRIDE_RATE:
            if (logger.isInfoEnabled())
                logger.info("Overriding transaction tax rate ...");
            // override tax rate on transaction
            saleReturnTransaction.overrideTaxRate(newTax.getOverrideRate(), updateAllItemsFlag, newTax.getReason());
            break;

        case TaxIfc.TAX_MODE_EXEMPT:
            if (logger.isInfoEnabled())
                logger.info("Setting transaction tax exempt ...");

            // set tax exempt transaction
            saleReturnTransaction.setTaxExempt(newTax.getTaxExemptCertificateID(), newTax.getReason());
            break;

        case TaxIfc.TAX_MODE_STANDARD:
        default:
            if (logger.isInfoEnabled())
                logger.info("Resetting standard tax ...");
            // check current tax mode
            if (saleReturnTransaction.getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
            {
                // clear tax exempt (everything reverts to standard)
                saleReturnTransaction.clearTaxExempt();
            }
            else
            {
                // reset standard (except on items modified, if desired)
                saleReturnTransaction.clearTaxOverride(updateAllItemsFlag);
            }
            break;
        }
    }

    /**
     * Formats tax rate (corrects doubles problem).
     *
     * @deprecated As of release 4.0.0
     * @param value rate
     * @return formatted tax rate
     */
    public String formatTaxRate(double taxRate)
    {
        BigDecimal bRate = new BigDecimal(taxRate).movePointRight(2);
        if (bRate.scale() > 4)
        {
            bRate = bRate.setScale(4, BigDecimal.ROUND_HALF_UP);
        }
        return (bRate.toString());
    }

    /**
     * Sets transaction.
     *
     * @param value new transaction
     */
    public void setTransaction(RetailTransactionIfc value)
    {
        transaction = value;
    }

    /**
     * Returns transaction.
     *
     * @return transaction
     */
    public RetailTransactionIfc getTransaction()
    {
        return (transaction);
    }

    /**
     * Sets the sales associate.
     *
     * @param value sales associate
     */
    public void setSalesAssociate(EmployeeIfc value)
    {
        salesAssociate = value;
    }

    /**
     * Returns sales associate.
     *
     * @return sales associate
     */
    public EmployeeIfc getSalesAssociate()
    {
        return (salesAssociate);
    }

    /**
     * Sets UpdateParentCargoFlag flag.
     *
     * @param value new UpdateParentCargoFlag setting
     */
    public void setUpdateParentCargoFlag(boolean value)
    {
        updateParentCargoFlag = value;
    }

    /**
     * Returns updateParentCargoFlag flag.
     *
     * @return UpdateParentCargoFlag
     */
    public boolean getUpdateParentCargoFlag()
    {
        return (updateParentCargoFlag);
    }

    /**
     * Retrieve the array of transactions on which items have been returned.
     * This cargo does not track this data.
     *
     * @return SaleReturnTransactionIfc[]
     */
    public SaleReturnTransactionIfc[] getOriginalReturnTransactions()
    {
        SaleReturnTransactionIfc[] transactions = null;

        if (originalReturnTransactions != null)
        {
            transactions = new SaleReturnTransactionIfc[originalReturnTransactions.size()];
            originalReturnTransactions.copyInto(transactions);
        }
        return transactions;
    }

    /**
     * Retrieve the array of transactions on which items have been returned.
     * This cargo does not track this data.
     *
     * @return SaleReturnTransactionIfc[]
     */
    public void resetOriginalReturnTransactions()
    {
        originalReturnTransactions = null;
    }

    /**
     * Add a transaction to the vector of transactions on which items have been
     * returned. This cargo does not track this data.
     *
     * @param SaleReturnTransactionIfc
     */
    public void addOrignalReturnTransaction(SaleReturnTransactionIfc transaction)
    {
        // check to see if an array already exist; if not make one.
        if (originalReturnTransactions == null)
        {
            originalReturnTransactions = new Vector<SaleReturnTransactionIfc>();
        }
        else
        {
            // Check to see if this transaction is already in the array.
            // if so, remove the current reference.
            int size = originalReturnTransactions.size();
            for (int i = 0; i < size; i++)
            {
                SaleReturnTransactionIfc temp = originalReturnTransactions.get(i);
                if (areTransactionIDsTheSame(temp, transaction))
                {
                    originalReturnTransactions.remove(i);
                    // Stop the loop.
                    i = size;
                }
            }
        }
        originalReturnTransactions.add(transaction);
    }

    /**
     * Test the two SaleReturnTransactionIfc objects to see if they refer to the
     * same transaction. Cannot use the equals because the numbers of returned
     * items in the SaleReturnLineItems will not be the same.
     *
     * @return boolean true if the transaction objects refer to the same
     *         transaction.
     */
    static public boolean areTransactionIDsTheSame(SaleReturnTransactionIfc tran1, SaleReturnTransactionIfc tran2)
    {
        boolean theSame = false;
        if (Util.isObjectEqual(tran1.getTransactionIdentifier(), tran1.getTransactionIdentifier())
                && Util.isObjectEqual(tran1.getBusinessDay(), tran1.getBusinessDay()))
        {
            theSame = true;
        }
        return theSame;
    }

    /**
     * Returns the securityOverrideFlag boolean.
     *
     * @return The securityOverrideFlag boolean.
     */
    public boolean getSecurityOverrideFlag()
    {
        return securityOverrideFlag;
    }

    /**
     * Sets the securityOverrideFlag boolean.
     *
     * @param value The ssecurityOverrideFlag boolean.
     */
    public void setSecurityOverrideFlag(boolean value)
    {
        securityOverrideFlag = value;

    }

    /**
     * Returns the securityOverrideEmployee object.
     *
     * @return The securityOverrideEmployee object.
     */
    public EmployeeIfc getSecurityOverrideEmployee()
    {
        return securityOverrideEmployee;
    }

    /**
     * Sets the security override employee object.
     *
     * @param value The security override employee object.
     */
    public void setSecurityOverrideEmployee(EmployeeIfc value)
    {
        securityOverrideEmployee = value;
    }

    /**
     * Returns the securityOverrideRequestEmployee object.
     *
     * @return The securityOverrideRequestEmployee object.
     */
    public EmployeeIfc getSecurityOverrideRequestEmployee()
    {
        return securityOverrideRequestEmployee;
    }

    /**
     * Sets the securityOverrideRequestEmployee object.
     *
     * @param value securityOverrideRequestEmployee object.
     */
    public void setSecurityOverrideRequestEmployee(EmployeeIfc value)
    {
        securityOverrideRequestEmployee = value;
    }

    /**
     * The access employee returned by this cargo is the currently logged on
     * cashier or an Override Security Employee
     *
     * @return the void
     */
    public void setAccessEmployee(EmployeeIfc value)
    {
        accessEmployee = value;
    }

    /**
     * The access employee returned by this cargo is the currently logged on
     * cashier or an Override Security Employee
     *
     * @return the EmployeeIfc value
     */
    public EmployeeIfc getAccessEmployee()
    {
        return accessEmployee;
    }

    /**
     * Get the Reason Codes from the DB;
     *
     * @param listKey
     * @param code <P>
     * @return the String code
     */
    protected String getReasonCodeValue(String listKey, String code)
    {
        CodeListIfc list = null;
        String desc = "";

        UtilityManagerIfc utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String storeID = null;

        if (transaction != null)
        {
            storeID = transaction.getWorkstation().getStore().getStoreID();

        }

        list = utility.getReasonCodes(storeID, listKey);
        if (list != null)
        {
            CodeEntryIfc clei = list.findListEntryByCode(code);
            if (clei != null)
            {
                desc = clei.getText(locale);
            }
        }

        return desc;
    }

    /**
     * The securityOverrideReturnLetter returned by this cargo is to indecated
     * where the security override will return
     *
     * @return the void
     */
    public void setSecurityOverrideReturnLetter(String value)
    {
        securityOverrideReturnLetter = value;
    }

    /**
     * The securityOverrideReturnLetter returned by this cargo is to indecated
     * where the security override will return
     *
     * @return the String value
     */
    public String getSecurityOverrideReturnLetter()
    {
        return securityOverrideReturnLetter;
    }

    /**
     * This is to keep track if sales associate set using transaction options
     *
     * @param value true if being set first time
     */
    public void setAlreadySetTransactionSalesAssociate(boolean value)
    {
        salesAssociateAlreadySet = value;
    }

    /**
     * Already set sales associate using transaction options return boolean true
     * is sales associate is already set
     */
    public boolean isAlreadySetTransactionSalesAssociate()
    {
        return salesAssociateAlreadySet;
    }

    /**
     * Sets list of line items.
     *
     * @param values list of line items
     */
    public void setItems(SaleReturnLineItemIfc[] values)
    {
        items = values;
    }

    /**
     * Retrieves list of line items
     *
     * @return SaleReturnLineItemIfc[] list of line items
     */
    public SaleReturnLineItemIfc[] getItems()
    {
        return items;
    }

    /**
     * Returns the string representation of this object.
     *
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  ModifyTransactionCargo (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        return (strResult);
    }

    /**
     * Returns the revision number of this class.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /**
     * Getter for BasketDTO Gets the Item Basket DTO which conaints item
     * information from the Item basket
     */
    public BasketDTO getBasketDTO()
    {
        return itemBasketDTO;
    }

    /**
     * Sets the Basket DTO into the Cargo Setter for BasketDTO
     */
    public void setBasketDTO(BasketDTO itemBasket)
    {
        this.itemBasketDTO = itemBasket;
    }

    /**
     * Sets the <code>fromFulfillment</code> flag.
     * @param value the new value for <code>fromFulfillment</code>
     */
    public void setFromFulfillment(boolean value)
    {
        this.fromFulfillment = value;
    }

    /**
     * Returns the <code>fromFulfillment</code> value.
     * @return the <code>fromFulfillment</code> value
     */
    public boolean isFromFulfillment()
    {
        return this.fromFulfillment;
    }

}