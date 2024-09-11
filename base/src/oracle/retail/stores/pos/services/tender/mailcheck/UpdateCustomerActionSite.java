/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/mailcheck/UpdateCustomerActionSite.java /main/15 2012/05/21 11:50:45 hyin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    hyin      05/18/12 - rollback changes made to CustomerUI for AddressType.
 *                         Change required field to phone number from
 *                         postalcode.
 *    asinton   03/07/12 - Use new CustomerManager instead of DataTransaction
 *                         method to access customer data.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:30:39 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:26:34 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:15:25 PM  Robert Pearse   
 *
 * Revision 1.8  2004/07/28 19:54:29  dcobb
 * @scr 6355 Can still search on original business name after it was changed
 * Modified JdbcSelectBusiness to search for name from pa_cnct table.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.mailcheck;

import java.util.zip.DataFormatException;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.tdo.MailBankCheckTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel;
/**
 * Present the Mail Bank Check UI
 * 
 */
@SuppressWarnings("serial")
public class UpdateCustomerActionSite extends PosSiteActionAdapter
{
    /**
     * Name of this site.
     */
    public static final String SITENAME = "UpdateCustomerActionSite";

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        boolean mailLetter = false;

        TenderCargo cargo = (TenderCargo) bus.getCargo();
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

        if (model.getChangeState()) // data was changed at MBC Customer screen
        {
            // attempt to validate postal code and do the database update
            try
            {
                TenderableTransactionIfc transaction = cargo.getTransaction();
                CaptureCustomerIfc customer = (CaptureCustomerIfc)((MailBankCheckTDO) tdo).copyFromModelToNewCustomer(model);
                if (transaction.getCustomer() != null)
                    customer.setCustomerID(cargo.getTransaction().getCustomer().getCustomerID());
                if (cargo.getCustomer()!= null)
                    customer.setCustomerID(cargo.getCustomer().getCustomerID());

                AddressIfc address = DomainGateway.getFactory().getAddressInstance();
                String postalString = address.validatePostalCode(model.getPostalCode(), model.getCountry());
                model.setPostalCode(postalString);
                
                if (customer.getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_UNSPECIFIED) != null)
                {
                    customer.getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_UNSPECIFIED).setAddressType(
                        AddressConstantsIfc.ADDRESS_TYPE_HOME);
                }

                // If there is a linked customer and we are not in training mode
                // update the database
                //
                if (customer != null && !cargo.getRegister().getWorkstation().isTrainingMode())
                {
                    CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
                    customerManager.saveCustomer(customer);
                }
                else
                {
                    logger.warn("No customer to save or register in training mode!");
                }

                // set the captured customer in the transaction
                customer.setTransactionID(transaction.getFormattedTransactionSequenceNumber());
                customer.setStoreID(transaction.getWorkstation().getStoreID());
                customer.setWsID(transaction.getWorkstation().getWorkstationID());
                customer.setBusinessDay(transaction.getBusinessDay());
                transaction.setCaptureCustomer(customer);
                
                cargo.setCustomer(customer);
                cargo.setFindOrAddOrUpdateLinked(true);
                
                mailLetter = true;
            }
            catch (DataFormatException e)
            {
                displayErrorDialog(ui, "InvalidPostalCode");

                return;
            }
            catch (DataException de)
            {
                logger.error("UpdateCustomerActionSite.arrive() - Database error while updating customer", de);

                displayErrorDialog(ui, "CustDatabaseError");

                return;
            }
        }
        else
        {
            cargo.setUpdateWithoutModify(true);
            displayErrorDialog(ui, "UpdateModificationError");

            return;
        }

        // Send the Accept letter if save succeeded.
        if (mailLetter)
        {
            // Using "generic dialog bean".
            DialogBeanModel dialogModel = new DialogBeanModel();

            dialogModel.setResourceID("UpdateSuccessful");
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Success");

            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
    }

    /**
     * Display the specified Error Dialog
     * 
     * @param String
     *            name of the Error Dialog to display
     * @param POSUIManagerIfc
     *            UI Manager to handle the IO
     */
    protected void displayErrorDialog(POSUIManagerIfc ui, String name)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(name);
        dialogModel.setType(DialogScreensIfc.ERROR);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

}
