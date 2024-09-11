/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/CheckPostalCodeSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:26 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:58 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:40:12  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   May 27 2003 09:14:56   baa
 * modify to retrieve correct address info
 * Resolution for 2532: Last Name not required on Ship-to-Address Screen
 * 
 *    Rev 1.1   Sep 18 2002 17:15:18   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:33:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:11:14   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:23:58   msg
 * Initial revision.
 * 
 *    Rev 1.2   19 Nov 2001 16:16:26   baa
 * customer & inquiry options cleanup
 * Resolution for POS SCR-98: Invalid Data Notice cites Postal Code vs. Ext Postal Code
 * 
 *    Rev 1.1   05 Nov 2001 17:36:36   baa
 * Code Review changes. Customer, Customer history Inquiry Options
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.0   Sep 21 2001 11:15:08   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

// java imports
import java.util.zip.DataFormatException;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Check the postal code for validity.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckPostalCodeSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Validates the postal code.
        <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();

        // If valid, save formatted postal code to cargo.
        // If invalid based on the country, then validatePostalCode will throw a
        // DataFormatException. The exception is caught here, initiating display
        // of an error dialog screen.
        try
        {
            AddressIfc address = (AddressIfc)cargo.getCustomer().getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_HOME);
            
            String postalString = address.validatePostalCode(address.getPostalCode(), address.getCountry());

            // save formatted postal code that was returned from the validation method
            address.setPostalCode(postalString);

            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        catch (DataFormatException e)
        {
            // Using "generic dialog bean".
            DialogBeanModel dialogModel = new DialogBeanModel();

            // Set model to same name as dialog in config\posUI.properties
            dialogModel.setResourceID("InvalidPostalCode");
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.RETRY);

            // set and display the model
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
