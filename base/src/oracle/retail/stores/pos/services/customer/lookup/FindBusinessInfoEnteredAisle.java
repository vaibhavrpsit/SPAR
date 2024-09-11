/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/lookup/FindBusinessInfoEnteredAisle.java /main/11 2012/12/13 10:05:11 abondala Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:28:11 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:21:42 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:11:06 PM  Robert Pearse   
 * $
 * Revision 1.4  2004/03/03 23:15:09  bwf
 * @scr 0 Fixed CommonLetterIfc deprecations.
 *
 * Revision 1.3  2004/02/12 16:49:32  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:45:00  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 * updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 03 2003 15:23:58   baa
 * Initial revision.
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.3   Mar 26 2003 16:41:46   baa
 * fix minor bugs with customer refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.2   Mar 20 2003 18:18:50   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.1   Oct 09 2002 11:28:14   kmorneau
 * fill in countries array
 * Resolution for 1814: Customer find by BusinessInfo crashes POS
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.lookup;


// foundation imports
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    The FindBusCustomerInfoEnteredAisle is called to retrieve the search
    criteria from the business customer search screen. If any of the
    search field is valid, a continue letter is mailed. Otherwise an
    error dialog screen is displayed stating that one of the search
    field has to be valid.
    <p>
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class FindBusinessInfoEnteredAisle  extends LaneActionAdapter

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
        // Store the search criteria in cargo.
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        CustomerInfoBeanModel model = (CustomerInfoBeanModel)ui.getModel(POSUIManagerIfc.BUSINESS_SEARCH);
        
        String custName = "";
        String phoneNumber = "";
        String postalCode = "";
        

        if ( model.getTelephoneNumber() != null )
        {
            phoneNumber = model.getTelephoneNumber();
        }
        if ( model.getCustomerName() != null )
        {
            custName = model.getCustomerName();
        }
        if (model.getPostalCode() != null)
        {
           postalCode = model.getPostalCode();
        }
        
        if ( phoneNumber.isEmpty() && custName.isEmpty() && postalCode.isEmpty() )
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
            CustomerIfc customer = CustomerUtilities.updateCustomer(cargo.getCustomer(), model);
            customer.setCompanyName(model.getCustomerName());
         
            // update the customer from the model
            cargo.setCustomer(customer);
            
            cargo.setOriginalCustomer(cargo.getCustomer());
            bus.mail(new Letter(CommonLetterIfc.OK), BusIfc.CURRENT);
            
        }

    }


}
