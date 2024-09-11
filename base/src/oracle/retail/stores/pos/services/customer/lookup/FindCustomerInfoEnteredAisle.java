/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/lookup/FindCustomerInfoEnteredAisle.java /main/11 2012/12/13 10:05:11 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  12/13/12 - customer search criteria fields are all optional.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:06 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:09  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:32  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:00  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:00   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Mar 20 2003 18:18:52   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.0   Apr 29 2002 15:32:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:52   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:38   msg
 * Initial revision.
 * 
 *    Rev 1.3   18 Feb 2002 18:43:42   baa
 * save original customer info
 * Resolution for POS SCR-1242: Selecting 'Enter' on Duplicate ID screen in Customer returns the wrong information
 *
 *    Rev 1.2   14 Jan 2002 11:25:34   baa
 * rename updateCustomer method
 * Resolution for POS SCR-567: Customer Select Add, Find, Delete display telephone type as Home/Work
 *
 *    Rev 1.1   16 Nov 2001 10:33:54   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:15:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.lookup;

// foundation imports
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerInfoEnteredAisle;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
//--------------------------------------------------------------------------
/**
    The CustomerInfoEnteredAisle calls the validate postal code method in
    the Address domain object.  If the postal code is valid, the
    information is stored and a continue letter is mailed. Otherwise an
    error dialog screen is displayed stating that the postal code is
    invalid.
    <p>
    $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class FindCustomerInfoEnteredAisle extends CustomerInfoEnteredAisle
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    //----------------------------------------------------------------------
    /**
        Saves the customer information in the cargo and mails
        a Continue letter.<p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        
        String firstName = "";
        String lastName = "";
        String phoneNumber = "";
        String postalCode = "";
        
        // Store the search criteria in cargo.
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        CustomerInfoBeanModel model = (CustomerInfoBeanModel)ui.getModel(POSUIManagerIfc.FIND_CUSTOMER_INFO);

        if ( model.getTelephoneNumber() != null )
        {
            phoneNumber = model.getTelephoneNumber();
        }
        if ( model.getFirstName() != null )
        {
            firstName = model.getFirstName();
        }
        if (model.getLastName() != null)
        {
            lastName = model.getLastName();
        }
        if (model.getPostalCode() != null)
        {
           postalCode = model.getPostalCode();
        }
        
        if ( phoneNumber.isEmpty() && firstName.isEmpty() && lastName.isEmpty() && postalCode.isEmpty() )
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("InvalidCustomerInfo");
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.ERROR);
            // Display the dialog.
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            CustomerCargo cargo = (CustomerCargo)bus.getCargo();
            CustomerIfc customer = cargo.getCustomer();

            // update the customer from the model
            cargo.setCustomer(updateCustomer(customer, model));
            cargo.setOriginalCustomer(cargo.getCustomer());
            bus.mail(new Letter(CommonLetterIfc.OK), BusIfc.CURRENT);
        }
        
    }


}
