/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/capturecustomerinfo/ValidateCustomerInfoSite.java /main/15 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   04/03/12 - removed deprecated methods
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  04/22/09 - Fixed null pointer issue
 *    nkgautam  04/16/09 - fox for displaying name in mail bank check franking
 *                         slip for any customer
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         12/17/2006 4:08:49 PM  Brett J. Larsen CR
 *         21298 - country code appearing where country name should appear
 *    3    360Commerce 1.2         3/31/2005 4:30:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:39 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:28 PM  Robert Pearse
 *
 *   Revision 1.8  2004/08/23 16:15:58  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.7  2004/07/23 16:25:57  aachinfiev
 *   @scr 5008 - Added journalling of captured customer information
 *
 *   Revision 1.6  2004/07/08 14:44:13  khassen
 *   @scr 6039 - updated validation methods/fields/functionality for the postal code.
 *
 *   Revision 1.5  2004/06/23 00:46:36  blj
 *   @scr 5113 - added nullpointer checking
 *
 *   Revision 1.4  2004/06/21 14:22:41  khassen
 *   @scr 5684 - Feature enhancements for capture customer use case: customer/capturecustomer accomodation.
 *
 *   Revision 1.3  2004/06/18 14:22:37  khassen
 *   @scr 5684 - Feature enhancements for capture customer use case.
 *
 *   Revision 1.2  2004/06/18 12:12:26  khassen
 *   @scr 5684 - Feature enhancements for capture customer use case.
 *
 *   Revision 1.1  2004/03/02 04:27:06  khassen
 *   @scr 0 Capture Customer Info use-case - Modifications to tour script and sites.  Added verification for postal code.
 *
 *   Revision 1.3  2004/02/27 21:08:47  khassen
 *   @scr 0 Capture Customer Info use-case - code clean-up and post-review modifications
 *
 *   Revision 1.2  2004/02/27 19:23:02  khassen
 *   @scr 0 Capture Customer Info use-case
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.capturecustomerinfo;

import java.util.zip.DataFormatException;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.tdo.CaptureCustomerInfoTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CaptureCustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site validates any information entered by the user at the
 * CaptureCustomerInfo site. Currently, only the postal code is checked.
 * 
 * @author kph
 */
public class ValidateCustomerInfoSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -3262810430994077343L;

    public static final String SITENAME = "ValidateCustomerInfoSite";

    /**
     * Part of the capture customer info use case.
     * 
     * @param bus the bus.
     */
    @Override
    public void arrive(BusIfc bus)
    {
        CaptureCustomerInfoCargo cargo = (CaptureCustomerInfoCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        CaptureCustomerInfoBeanModel model = (CaptureCustomerInfoBeanModel) ui.getModel(cargo.getScreenType());

        // Check to see if the postal code is required.  The boolean value in the
        // model is set by the bean during the updateModel() call.
        if (!model.isPostalCodeRequired())
        {
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
            return;
        }
        // We store the model (conveniently) in the cargo so that when we return to
        // the CaptureCustomerInfoSite we can reuse the information on the model.
        cargo.setModel(model);

        // Set up an address with the info from the model, and verify the postal code.
        AddressIfc address = DomainGateway.getFactory().getAddressInstance();
        address.setCountry(model.getCountry());
        address.setPostalCode(model.getPostalCode());
        try
        {
            String postalString = address.validatePostalCode(address.getPostalCode(), address.getCountry());
            // If an exception is not thrown, then the following will execute.
            address.setPostalCode(postalString);
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (DataFormatException e)
        {
            // The postal code is invalid, so display a dialog.
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("InvalidPostalCode");
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.RETRY);
            // Display the dialog.
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
    }

    /**
     * Part of the capture customer info use case.
     */
    @Override
    public void depart(BusIfc bus)
    {
        // Check for success before setting up the cargo.
        if (CommonLetterIfc.SUCCESS.equals(bus.getCurrentLetter().getName()))
        {
            TDOUIIfc tdo = null;
            // Create the tdo object.
            try
            {
                tdo = (TDOUIIfc) TDOFactory.create("tdo.tender.CaptureCustomerInfo");
            }
            catch (TDOException tdoe)
            {
                tdoe.printStackTrace();
            }
            CaptureCustomerInfoCargo cargo = (CaptureCustomerInfoCargo) bus.getCargo();
            CaptureCustomerIfc customer = cargo.getCustomer();
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

            // Copy the model back onto the customer.
            CaptureCustomerInfoBeanModel model = (CaptureCustomerInfoBeanModel)ui.getModel(cargo.getScreenType());

            if (customer == null)
            {
                customer = DomainGateway.getFactory().getCaptureCustomerInstance();
            }
            ((CaptureCustomerInfoTDO) tdo).modelToCustomer(model, bus, customer);

            if(model.isBusinessCustomer())
            {
              customer.setBusinessCustomer(true);
              customer.setCustomerName(model.getOrgName());
            }

            // Update the customer object with information from the current
            // transaction.  Necessary for db updates.
            TransactionIfc transaction = cargo.getTransaction();
            if (transaction != null)
            {
                customer.setTransactionID(transaction.getFormattedTransactionSequenceNumber());
                customer.setStoreID(transaction.getWorkstation().getStoreID());
                customer.setWsID(transaction.getWorkstation().getWorkstationID());
                customer.setBusinessDay(transaction.getBusinessDay());
                // Make certain the customer is set properly in the cargo.
                transaction.setCaptureCustomer(customer);
                cargo.setCustomer(customer);

                // Journal captured information
                JournalManagerIfc jmi = (JournalManagerIfc)
                bus.getManager(JournalManagerIfc.TYPE);

                jmi.journal(transaction.getCashier().getEmployeeID(),
                    transaction.getTransactionID(),
                    model.getJournalString());
            }

        }

    }

}
