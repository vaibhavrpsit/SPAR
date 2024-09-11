/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/mailcheck/CheckMailingOTCCActionSite.java /main/14 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  04/16/09 - fox for displaying name in mail bank check franking
 *                         slip for any customer
 *    acadar    04/14/09 - use localized code for send mail bank
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.mailcheck;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.tdo.MailBankCheckTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
//-----------------------------------------------------------------------------
/**
 * Present the Mail Bank Check UI
 *
 * @version $Revision: /main/14 $
 */
//-----------------------------------------------------------------------------
public class CheckMailingOTCCActionSite extends PosSiteActionAdapter
{

    /**
     * revision number for this class
     */
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:77; $EKW:";

    // Site name
    public static final String SITENAME = "CheckMailingOTCCActionSite";

    //-------------------------------------------------------------------------
    /**
     * CheckMailing Action site for use with One Time Customer Capture
     *
     * @param bus
     *            the bus arriving at this site
     */
    //-------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        CustomerIfc customer = cargo.getCustomer();
        if (customer != null)
        {
            customer.setBusinessCustomer(customer.isBusinessCustomer());

            // set the customer's name in the status area
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            StatusBeanModel statusModel = new StatusBeanModel();
            statusModel.setCustomerName(customer.getCustomerName());
            POSBaseBeanModel baseModel = new POSBaseBeanModel();
            baseModel.setStatusBeanModel(statusModel);
            ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);
        }

        if (cargo != null && cargo.getTransaction() != null)
        {
            CustomerUtilities.journalCustomerExit(bus, 
                cargo.getTransaction().getTransactionID(),
                cargo.getTransaction().getCashier().getEmployeeID());
        }

        HashMap tenderAttributes = cargo.getTenderAttributes();
        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.MAIL_CHECK);
        tenderAttributes.put(TenderConstants.LOCALIZED_ID_TYPE, cargo.getLocalizedPersonalIDCode());
        tenderAttributes.put(TenderConstants.PHONE_TYPE, new Integer(cargo.getPhoneType()));

        TDOUIIfc tdo = null;
        try
        {
            tdo = (TDOUIIfc) TDOFactory.create("tdo.tender.MailBankCheck");
        }
        catch (TDOException tdoe)
        {
            logger.error("Error creating MailBankCheck TDO object", tdoe);
        }

        // Send customer data to TenderAttributes map in preparation for creation of
        // TenderMailCheckADO object.
         ((MailBankCheckTDO) tdo).copyFromCustomerToMap(customer, tenderAttributes);

        TenderADOIfc mailCheckTender = null;

        try
        {
            TenderFactoryIfc factory = (TenderFactoryIfc) ADOFactoryComplex.getFactory("factory.tender");
            mailCheckTender = factory.createTender(tenderAttributes);
        }
        catch (ADOException adoe)
        {
            logger.error("Error creating MailBankCheck ADO object", adoe);
        }
        catch (TenderException e)
        {
            TenderErrorCodeEnum error = e.getErrorCode();
            if (error == TenderErrorCodeEnum.INVALID_AMOUNT)
            {
                assert(false) : "This should never happen, because UI enforces proper format";
            }
        }

        // add the tender to the transaction
        try
        {
            RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
            txnADO.addTender(mailCheckTender);
            cargo.setLineDisplayTender(mailCheckTender);

            // journal the added tender
            JournalFactoryIfc jrnlFact = null;
            try
            {
                jrnlFact = JournalFactory.getInstance();
            }
            catch (ADOException e)
            {
                logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
                throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
            }
            RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();
            registerJournal.journal(mailCheckTender, JournalFamilyEnum.TENDER, JournalActionEnum.ADD);
        }
        catch (TenderException e)
        {
            // save tender in cargo
            cargo.setTenderADO(mailCheckTender);

            System.out.println("TenderException while creating, adding, & journaling TenderMBCADO");

            logger.error("Error obtaining journaling Mail Bank Check", e);
        }

        // mail a letter
        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }
    // end getRevisionNumber()
}
