/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/transaction/VoidTransactionADO.java /main/20 2014/05/12 12:25:09 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     05/12/14 - FORWARD PORT - cash drawer opens for postvoid of
 *                         even exchange transaction.
 *    abondala  09/04/13 - initialize collections
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    jswan     10/19/11 - Set authorization method and code for authorizable
 *                         tenders.
 *    npoola    09/24/10 - changed the parameter name from
 *                         TrainingModeOpenDrawer to OpenDrawerInTrainingMode
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    crain     03/05/10 - Replace CHANGE_DUE
 *    abondala  01/03/10 - update header date
 *    nganesh   02/17/09 - Modified code to change void transaction EJ to
 *                         contain Reason Code for Void Transaction :
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         9/20/2007 12:09:12 PM  Rohit Sachdeva
 *         28813: Initial Bulk Migration for Java 5 Source/Binary
 *         Compatibility of All Products
 *    4    360Commerce 1.3         1/25/2006 4:11:55 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:30:46 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:47 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:34 PM  Robert Pearse
 *:
 *    4    .v700     1.2.2.0     11/30/2005 17:22:24    Deepanshu       CR
 *         6261: Added Postvoid transaction reason code
 *    3    360Commerce1.2         3/31/2005 15:30:46     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:26:47     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:15:34     Robert Pearse
 *
 *   Revision 1.11  2004/08/31 19:12:36  blj
 *   @scr 6855 - cleanup gift card credit code and fix defects found by PBY
 *
 *   Revision 1.10  2004/07/30 14:05:28  khassen
 *   Creating new VoidTransactionADOTest.java JUnit test case.
 *
 *   Revision 1.9  2004/07/26 16:55:42  aschenk
 *   @scr 5990 - Post-void of Payout is now journaled with the correct amount reversed.
 *
 *   Revision 1.8  2004/07/23 22:17:25  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.7  2004/07/14 15:34:11  khassen
 *   @scr 5739 - Fixed reason code journaling
 *
 *   Revision 1.6  2004/06/24 19:33:12  bwf
 *   @scr 5743 Fixed journaling of void transactions.
 *
 *   Revision 1.5  2004/03/31 20:54:04  bjosserand
 *   @scr 4093 Transaction Reentry
 *
 *   Revision 1.4  2004/03/31 20:19:01  bjosserand
 *   @scr 4093 Transaction Reentry
 *
 *   Revision 1.3  2004/03/30 23:52:25  bjosserand
 *   @scr 4093 Transaction Reentry
 *
 *   Revision 1.2  2004/02/12 16:47:57  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.4   Feb 05 2004 13:22:58   rhafernik
 * log4j conversion
 *
 *    Rev 1.3   Dec 08 2003 16:42:20   bwf
 * Update for code review.
 *
 *    Rev 1.2   Nov 19 2003 16:21:12   epd
 * Refactoring updates
 *
 *    Rev 1.1   Nov 19 2003 13:59:26   bwf
 * Make change to check if echeck there causes need to open drawer.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.0   Nov 04 2003 11:14:40   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 12:35:24   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.transaction;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.transaction.VoidTransaction;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.journal.JournalConstants;
import oracle.retail.stores.pos.ado.lineitem.LineItemADOIfc;
import oracle.retail.stores.pos.ado.lineitem.LineItemConstants;
import oracle.retail.stores.pos.ado.lineitem.LineItemFactory;
import oracle.retail.stores.pos.ado.lineitem.LineItemTypeEnum;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.store.RegisterMode;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupADOIfc;
import oracle.retail.stores.pos.ado.tender.group.TenderGroupCheckADO;
import oracle.retail.stores.pos.ado.utility.AuthorizationException;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;

/**
 *
 *
 */
public class VoidTransactionADO extends AbstractRetailTransactionADO
{
    /** The ADO transaction being voided */
    protected RetailTransactionADOIfc originalTxnADO;

    /**
     * Entered by user from UI
     * @deprecated as of 13.1. Use {@link reasonCode}
    */
    protected String voidReasonCode;

    /**
     * Localized void reason code
     */
    protected LocalizedCodeIfc localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();

    /**
     * @see oracle.retail.stores.pos.ado.transaction.AbstractRetailTransactionADO#instantiateTransactionRDO()
     */
    protected TransactionIfc instantiateTransactionRDO()
    {
        transactionRDO = DomainGateway.getFactory().getVoidTransactionInstance();
        return transactionRDO;
    }

    /**
     * Set the original transaction on this void transaction.
     * This sets the original ADO transaction as an attribute to this
     * ADO void as well as setting the original RDO transaction on
     * the RDO void transaction.
     * @param origTxn The transaction being voided.
     */
    public void setOriginalTransaction(RetailTransactionADOIfc origTxn)
    {
        originalTxnADO = origTxn;
        ((VoidTransactionIfc) transactionRDO).setOriginalTransaction(
            (TenderableTransactionIfc) ((ADO) origTxn).toLegacy());
        ((VoidTransactionIfc) transactionRDO).setReason(localizedReasonCode);
    }

    /**
     * Returns the enumerated transaction type of the
     * original transaction.
     * @return
     */
    public TransactionPrototypeEnum getOriginalTranactionType()
    {
        return originalTxnADO.getTransactionType();
    }

    /**
     * @param voidReasonCode
     * @deprecated as of 13.1. Use {@link setLocalizedReasonCode(LocalizedCodeIfc)}
     */
    public void setVoidReasonCode(String voidReasonCode)
    {
        this.voidReasonCode = voidReasonCode;
    }

    /**
     * Returns the Void Reason Code
     * @deprecated as of 13.1. Use{@linkgetLocalizedReasonCode()}
     */
    public String getVoidReasonCode()
    {
        return this.voidReasonCode;
    }

    /**
     * Gift card items may exist on the transaction that
     * may need to be activated/deactivated, depending on
     * transaction type.  This method lets the caller know
     * whether that action needs to occur. Also check for
     * gift card tenders.  Gift Card Credit tenders need to be
     * deactivated and not voidauth.
     * @return boolean flag indicating Gift Card items need reversal or not.
     */
    public boolean isGiftCardReversalRequired()
    {
        boolean giftCardReversalRequired = (getLineItemsForType(LineItemTypeEnum.TYPE_GIFT_CARD).length > 0) ? true : false;
        boolean hasGiftCardTender = false;

        if (!giftCardReversalRequired)
        {
            Iterator iter = tenderGroupMap.values().iterator();
            while (iter.hasNext())
            {
                TenderGroupADOIfc group = (TenderGroupADOIfc) iter.next();
                // iterate through tenders in group and only get tenders
                // needing authorization
                TenderADOIfc[] tenders = group.getTenders();

                for (int i=0; i<tenders.length; i++)
                {
                    if (tenders[i].getTenderType() == TenderTypeEnum.GIFT_CARD)
                    {
                        hasGiftCardTender = true;
                        break;
                    }
                }
            }
            if (hasGiftCardTender)
            {
                giftCardReversalRequired = true;
            }
        }

        return giftCardReversalRequired;
    }

    /**
     * Given a line item type, return an array of RDO line items.
     * NOTE: This is overridden to change the way line items are retrieved.
     * They are retrieved with the amounts negated.
     * @param type The desired type to retrieve
     * @return an array of items that match the desired type.
     */
    protected LineItemADOIfc[] getLineItemsForType(LineItemTypeEnum type)
    {
        SaleReturnLineItemIfc[] items = ((VoidTransactionIfc) transactionRDO).getProductGroupLineItems(type.toString());

        LineItemADOIfc[] adoItems = null;
        if (items != null)
        {
            adoItems = new LineItemADOIfc[items.length];
            // HashMap for creating ADO line item
            HashMap lineItemAttributes = new HashMap(1);
            lineItemAttributes.put(LineItemConstants.LINE_ITEM_TYPE, type);

            for (int i = 0; i < items.length; i++)
            {
                // create ADO line item
                LineItemADOIfc lineItemADO = LineItemFactory.getInstance().createLineItem(lineItemAttributes);
                ((ADO) lineItemADO).fromLegacy(items[i]);
                adoItems[i] = lineItemADO;
            }
        }
        else
        {
            adoItems = new LineItemADOIfc[0];
        }

        return adoItems;
    }

    /**
     * Let the original transaction handle its own
     * void processing.
     */
    public void process() throws AuthorizationException
    {
        tenderGroupMap = ((AbstractRetailTransactionADO) originalTxnADO).processVoid();

        // Make sure RDO transaction gets new tenders
        updateRDOTxnWithTenders();

    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.journal.JournalableIfc#getJournalMemento()
     */
    public Map getJournalMemento()
    {
        Map memento = initJournalMemento();

        memento.put(JournalConstants.VOID_REASON_CODE, localizedReasonCode.getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
        if (this.getOriginalTranactionType() == TransactionPrototypeEnum.TILL_ADJUSTMENT)
        {
            VoidTransactionIfc   voidTrans = (VoidTransactionIfc) transactionRDO;
            TransactionTotalsIfc totals    = voidTrans.getTransactionTotals();
            memento.put(JournalConstants.TOTAL_TENDER, totals.getAmountTender().negate());
        }
        else
        {
            AbstractRetailTransactionADO abstractTrans = (AbstractRetailTransactionADO) originalTxnADO;
            memento.put(JournalConstants.TOTAL_TENDER, (abstractTrans.getAmountTender().negate()));
        }
        memento.put(JournalConstants.BALANCE_DUE, originalTxnADO.getBalanceDue().negate());

        return memento;
    }

    /*
     * calls the super.getJournalMemento() method to initialize and
     * preload the memento HashMap with data.
     */
    protected Map initJournalMemento()
    {
        return super.getJournalMemento();
    }

    /**
     * @see oracle.retail.stores.pos.ado.transaction.TransactionADOIfc#save()
     */
    public void save(RegisterADO registerADO) throws DataException
    {
        // Save this void transaction
        BusIfc bus = TourContext.getInstance().getTourBus();
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        RegisterIfc registerRDO = (RegisterIfc) registerADO.toLegacy();
        TillIfc tillRDO = registerRDO.getCurrentTill();
        utility.saveTransaction(transactionRDO, tillRDO, registerRDO);

        // Update original transaction
        originalTxnADO.updateForVoid();
    }

    /**
     * Based on the state of this transaction, determine
     * whether the drawer needs to be popped.
     * @return
     */
    public boolean openDrawerRequired()
    {
        boolean result = true;

        // First determine whether we are in training mode
        if (ContextFactory.getInstance().getContext().getRegisterADO().isInMode(RegisterMode.TRAINING))
        {
            // check the appropriate parameter
            result = openDrawerForTraining();
        }
        else
        {
            // if we are in a split tender situation, pop the drawer,
            // otherwise check to make sure the tender contained in the
            // transaction is included in the parameter setting
            // for popping the drawer for void
            if (!splitTender())
            {
                // Make sure that for the one tender type we have, that it exists
                // as a value in the open drawer for void parameter
                String[] openDrawerTenders = getOpenDrawerForPostVoidSetting();
                // Find the group with tenders in it and make sure the type exists
                // int the openDrawerTenders array
                Iterator iter = tenderGroupMap.values().iterator();
                boolean found = false;
                while (iter.hasNext() && !found)
                {
                    TenderGroupADOIfc group = (TenderGroupADOIfc) iter.next();
                    if (group.getTenderCount() > 0)
                    {
                        for (int i = 0; i < openDrawerTenders.length; i++)
                        {
                            if (openDrawerTenders[i].equalsIgnoreCase(group.getGroupType().toString()))
                            {
                                if (group.getGroupType().equals("Check")
                                    && !isECheckOpenDrawerTender()
                                    && ((TenderGroupCheckADO) group).isECheckPresent())
                                {
                                    // found is not true
                                }
                                else
                                {
                                    // found our tender in the group
                                    found = true;
                                }
                                break;
                            }
                        }
                    }
                }
                if (!found)
                {
                    // Do not pop the drawer.  The tender doesn't exist in the parameter
                    result = false;
                }
            }
        }
        return result;
    }

    //----------------------------------------------------------------------
    /**
        This method determines whether or not echeck opens the register.
        @return
    **/
    //----------------------------------------------------------------------
    protected boolean isECheckOpenDrawerTender()
    {
        boolean eCheckOpens = false;
        String[] openDrawerTenders = getOpenDrawerForPostVoidSetting();
        for (int i = 0; i < openDrawerTenders.length; i++)
        {
            if (openDrawerTenders[i].equalsIgnoreCase("ECheck"))
            {
                eCheckOpens = true;
                break;
            }
        }
        return eCheckOpens;
    }

    /**
     * Checks the parameter setting determining whether
     * the cash drawer should be popped for training mode
     * @return
     */
    protected boolean openDrawerForTraining()
    {
        boolean result = false;

        // Check the parameter
        BusIfc bus = TourContext.getInstance().getTourBus();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        try
        {
            result = pm.getBooleanValue("OpenDrawerInTrainingMode");
        }
        catch (ParameterException pe)
        {
            logger.error(pe);
        }
        return result;
    }

    /**
     * Determines whether this transaction
     * has multiple tenders of different types
     * @return
     */
    protected boolean splitTender()
    {
        boolean result = false;
        // Loop through tender groups to get tender counts.
        // If more than one group has a count of 1 or more, then
        // this is a split tender
        int tenderTypeCount = 0;
        Iterator iter = tenderGroupMap.values().iterator();
        while (iter.hasNext())
        {
            TenderGroupADOIfc group = (TenderGroupADOIfc) iter.next();
            // if this group has at least one tender
            if (group.getTenderCount() > 0)
            {
                tenderTypeCount++;
            }
            // if we found more than one group with tenders
            if (tenderTypeCount > 1)
            {
                result = true;
                break;
            }
        }
        return result;
    }

    protected String[] getOpenDrawerForPostVoidSetting()
    {
        BusIfc bus = TourContext.getInstance().getTourBus();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        // initialize with default values
        String[] result =
            new String[] {
                "Cash",
                "Credit",
                "Check",
                "TravCheck",
                "GiftCert",
                "Debit",
                "Coupon",
                "GiftCard",
                "StoreCredit",
                "MallCert",
                "PurchaseOrder" };
        try
        {
            result = pm.getStringValues("OpenDrawerForPostVoid");
        }
        catch (ParameterException e)
        {
            // TODO: log
        }

        return result;
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        //assert(rdo instanceof VoidTransaction);

        // our RDO
        transactionRDO = (VoidTransaction) rdo;

        // Create an ADO from original RDO transaction
        TenderableTransactionIfc origTxnRDO = ((VoidTransactionIfc) transactionRDO).getOriginalTransaction();
        TransactionPrototypeEnum enumer =
            TransactionPrototypeEnum.makeEnumFromTransactionType(origTxnRDO.getTransactionType());
        try
        {
            originalTxnADO = enumer.getTransactionADOInstance();
        }
        catch (ADOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ((ADO) originalTxnADO).fromLegacy(origTxnRDO);
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        return transactionRDO;
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Converts all ADO tenders to RDO tenders
     * and adds them to the RDO transaction
     */
    protected void updateRDOTxnWithTenders()
    {
        // iterate through groups to get each RDO tender and add it to the RDO transaction
        for (Iterator groupIter = tenderGroupMap.values().iterator(); groupIter.hasNext();)
        {
            TenderGroupADOIfc group = (TenderGroupADOIfc) groupIter.next();
            TenderADOIfc[] tenders = group.getTenders();
            for (int i = 0; i < tenders.length; i++)
            {
                TenderLineItemIfc tenderRDO = (TenderLineItemIfc) ((ADO) tenders[i]).toLegacy();
                if (tenderRDO instanceof AuthorizableTenderIfc)
                {
                    AuthorizableTenderIfc authorizableTender = (AuthorizableTenderIfc)tenderRDO;
                    authorizableTender.setAuthorizationMethod(
                            AuthorizeTransferResponseIfc.AuthorizationMethod.System.toString());
                    BusIfc bus = TourContext.getInstance().getTourBus();
                    ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
                    String authCode = "";
                    try
                    {
                        authCode = pm.getStringValue("SystematicApprovalAuthCode");
                    }
                    catch (ParameterException pe)
                    {
                        // do nothing
                    }
                    authorizableTender.setAuthorizationCode(authCode);
                }
                ((TenderableTransactionIfc) transactionRDO).addTender(tenderRDO);
            }
        }
    }

    /**
     * @return the localizedReasonCode
     */
    public LocalizedCodeIfc getLocalizedReasonCode()
    {
        return localizedReasonCode;
    }

    /**
     * @param localizedReasonCode the localizedReasonCode to set
     */
    public void setLocalizedReasonCode(LocalizedCodeIfc localizedReasonCode)
    {
        this.localizedReasonCode = localizedReasonCode;
    }

    /***
     * 
     * @return void transaction
     */
    public VoidTransactionIfc getVoidTransaction()
    {
        VoidTransactionIfc origTxnRDO = (VoidTransactionIfc) transactionRDO;
         return origTxnRDO;
    } 
}
