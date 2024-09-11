/* ===========================================================================
* Copyright (c) 2003, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/DisplayRedeemOptionsSite.java /main/15 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   08/16/11 - implement timeout capability for admin menu
 *    asinton   08/12/11 - prevent dropped transactions by checking if
 *                         transction already in progress and ensure proper
 *                         timeout handling by setting DefaultTimeoutModel on
 *                         bean model.
 *    asinton   08/11/11 - set TransasctionStatusBean.setOpenTransaction(true)
 *                         in order to get proper timeout flow.
 *    asinton   06/18/11 - Turned off gift card button when GiftCardsAccepted
 *                         parameter returns false
 *    jswan     12/01/10 - Modified to prevent loss possible loss of
 *                         transaction sequence ID during redeem process.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    asinton   04/08/10 - XbranchMerge asinton_bug-9558370 from
 *                         rgbustores_13.2x_generic_branch
 *    asinton   04/08/10 - Removing fix for timeout issue.
 *    nkgautam  04/08/10 - XbranchMerge nkgautam_bug-9558370 from main
 *    nkgautam  04/08/10 - added timermodel in beanmodel to handle timeout
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         2/26/2008 11:14:01 PM  Manikandan Chellapan
 *      CR#30664 Fixed timeout problem
 * 3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:21:05 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse
 *
 *Revision 1.2  2004/04/12 18:37:47  blj
 *@scr 3872 - fixed a problem with validation occuring after foreign currency has been converted.
 *
 *Revision 1.1  2004/02/26 04:48:54  blj
 *@scr 0 - redeem services has moved to _360commerce.  Redeem is now an ADO service.
 *
 *Revision 1.3  2004/02/12 16:51:41  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:52:30  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Dec 10 2003 18:04:30   nrao
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.transaction.RedeemTransaction;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.JournalableIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.store.StoreADO;
import oracle.retail.stores.pos.ado.store.StoreFactory;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.TransactionPrototypeEnum;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;
import oracle.retail.stores.pos.ui.timer.TimeoutSettingsUtility;

/**
 *  Displays the Redeem Options
 */
@SuppressWarnings("serial")
public class DisplayRedeemOptionsSite extends PosSiteActionAdapter
{
    /**
     * Gift Card button name.
     */
    public static final String GIFT_CARD_BUTTON = "GiftCard";

    /**
     * This site displays the redeem option screen. For GiftCert and StoreCredit
     * we capture the name of the letter selected so that we know what type of
     * redeem tender to create.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        RedeemCargo redeemCargo = (RedeemCargo)bus.getCargo();
        // if transaction already in progress then no need to create a new one.
        if(redeemCargo.getTransaction() == null)
        {
            createRedeemTransaction(bus);
        }
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.REDEEM_OPTIONS);
        boolean giftCardsAccepted = true;
        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            giftCardsAccepted = pm.getBooleanValue(ParameterConstantsIfc.TENDER_GiftCardsAccepted);
        }
        catch(ParameterException pe)
        {
            logger.warn("ParameterManager failed with exception: " + pe);
        }
        NavigationButtonBeanModel localNavigation = new NavigationButtonBeanModel();
        localNavigation.setButtonEnabled(GIFT_CARD_BUTTON, giftCardsAccepted);
        model.setLocalButtonBeanModel(localNavigation);

        // in order to timeout appropriately establish that a transaction
        // is in progress by setting a new DefaultTimerModel on the bean model.
        model.setTimerModel(new DefaultTimerModel(bus, true));
        ui.setModel(POSUIManagerIfc.REDEEM_OPTIONS, model);
        ui.showScreen(POSUIManagerIfc.REDEEM_OPTIONS, model);
    }

    /**
     * Creates the redeem Transaction
     */
    private void createRedeemTransaction(BusIfc bus)
    {
        RedeemCargo cargo = (RedeemCargo)bus.getCargo();
        // create a register
        StoreFactory storeFactory = StoreFactory.getInstance();
        RegisterADO registerADO = storeFactory.getRegisterADOInstance();
        registerADO.fromLegacy(cargo.getRegister());

        // create the store
        StoreADO storeADO = storeFactory.getStoreADOInstance();
        storeADO.fromLegacy(cargo.getStoreStatus());

        // put store in register
        registerADO.setStoreADO(storeADO);

        // Create/convert/set in cargo ADO transaction
        TransactionPrototypeEnum txnType = TransactionPrototypeEnum
         .makeEnumFromTransactionType(TransactionIfc.TYPE_REDEEM);

        RetailTransactionADOIfc txnADO = null;
        try
        {
            txnADO = txnType.getTransactionADOInstance();
        }
        catch (ADOException e1)
        {
            logger.warn("Unable to get Transaction ADO Instance.", e1);
        }

        txnADO = registerADO.createTransaction(txnType, cargo.getCustomerInfo(), cargo.getOperator());

        JournalManagerIfc journal = null;
        journal = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
        journal.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);

        RedeemTransaction redeem = (RedeemTransaction) ((ADO)txnADO).toLegacy();
        cargo.setTransaction(redeem);
        cargo.setTransactionInProgress(true);
        TimeoutSettingsUtility.setTransactionActive(true);
        cargo.setCurrentTransactionADO(txnADO);
    }

    /**
     * Collect data from the UI upon depart.
     * 
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void depart(BusIfc bus)
    {
        // The letter sent from this screen will be the name
        // of the redeem tender, therefore we save a string version of this letter
        // and we store the tender type enum of the selected redeem tender.
        RedeemCargo cargo = ((RedeemCargo)bus.getCargo());
        cargo.getTenderAttributes().put(TenderConstants.TENDER_TYPE, TenderTypeEnum.makeEnumFromString(bus.getCurrentLetter().getName()));
        cargo.setRedeemTypeSelected(bus.getCurrentLetter().getName());
    }
}
