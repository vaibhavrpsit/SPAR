/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/mailcheck/CheckMailingActionSite.java /main/13 2012/05/21 11:50:45 hyin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    hyin      05/18/12 - rollback changes made to CustomerUI for AddressType.
 *                         Change required field to phone number from
 *                         postalcode.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.mailcheck;

import java.util.HashMap;
import java.util.zip.DataFormatException;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.tdo.MailBankCheckTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel;
//-----------------------------------------------------------------------------
/**
 * Present the Mail Bank Check UI
 */
//-----------------------------------------------------------------------------
@SuppressWarnings("serial")
public class CheckMailingActionSite extends PosSiteActionAdapter
{
    // Site name
    public static final String SITENAME = "CheckMailingActionSite";

    //-------------------------------------------------------------------------
    /**
     * Get the CustomerIfc data if linked Put up the MBC entry screen
     * 
     * @param bus
     *            the bus arriving at this site
     */
    //-------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();

        HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // get the info from the ui here
        MailBankCheckInfoBeanModel model =
            (MailBankCheckInfoBeanModel) ui.getModel(POSUIManagerIfc.MAIL_BANK_CHECK_INFO);

        TDOUIIfc tdo = null;
        try
        {
            tdo = (TDOUIIfc) TDOFactory.create("tdo.tender.MailBankCheck");
        }
        catch (TDOException tdoe)
        {
            logger.error("Error creating MailBankCheck TDO object", tdoe);
        }

        // attempt to validate postal code and do the database update
        try
        {
            AddressIfc address = DomainGateway.getFactory().getAddressInstance();
            String postalString = address.validatePostalCode(model.getPostalCode(), model.getCountry());
            model.setPostalCode(postalString);
        }
        catch (DataFormatException e)
        {
            CustomerIfc customer = ((MailBankCheckTDO) tdo).copyFromModelToNewCustomer(model);
            cargo.setFindOrAddOrUpdateLinked(true);
            cargo.setCustomer(customer);

            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("InvalidPostalCode");
            dialogModel.setType(DialogScreensIfc.ERROR);

            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

            return;
        }

        // Send UI model's data to TenderAttributes map in preparation for creation of
        // TenderMailCheckADO object.
         ((MailBankCheckTDO) tdo).copyFromModelToMap(model, tenderAttributes);

        TenderADOIfc mailCheckTender = null;

        if (cargo.getTenderADO() == null)
        {
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
        }
        else
        {
            mailCheckTender = cargo.getTenderADO();
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

            model.setCountryIndex(0);
            model.setStateIndex(0);
            model.setSelectedReasonCode(0); // reset ID type to first one
            model.setChangeState(true);

            //moved letter mailing from here to after catch statement
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

    //-------------------------------------------------------------------------
    /**
     * Display the specified Error Dialog
     * 
     * @param String
     *            name of the Error Dialog to display
     * @param POSUIManagerIfc
     *            UI Manager to handle the IO
     */
    //-------------------------------------------------------------------------
    protected void displayErrorDialog(POSUIManagerIfc ui, String name)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(name);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.INVALID);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    // end getRevisionNumber()
}
