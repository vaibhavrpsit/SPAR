/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/lookup/FindBusinessInfoInvalidAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:28 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.lookup;

// foundation imports
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

//--------------------------------------------------------------------------
/**
   Prompts for confirmation of invalid search data in business customer
   search screen.
   <p>
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class FindBusinessInfoInvalidAisle extends PosLaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "";

    //----------------------------------------------------------------------
    /**
       Prompts for confirmation of invalid search data in business customer
       search screen.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
    UtilityManagerIfc utility =   (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        String args[] = new String[3];
        args[0] =  utility.retrieveText("BusCustomerInfoSpec"
                    ,BundleConstantsIfc.CUSTOMER_BUNDLE_NAME
                    ,"BusNameWarningMsg"
                    ,"Business Name must be at least 2 characters long."
                    ,LocaleConstantsIfc.USER_INTERFACE); 

        args[1] = utility.retrieveText("BusCustomerInfoSpec"
                       ,BundleConstantsIfc.CUSTOMER_BUNDLE_NAME
                       ,"PostalCodeWarningMsg"
                       ,"Postal Code must be at least 5 characters long."
                       ,LocaleConstantsIfc.USER_INTERFACE); 

        args[2] = utility.retrieveText("BusCustomerInfoSpec"
                       ,BundleConstantsIfc.CUSTOMER_BUNDLE_NAME
                       ,"PhoneNumberWarningMsg"
                       ,"Phone Number must be at least 10 characters long."
                       ,LocaleConstantsIfc.USER_INTERFACE); 
        
        String letters[] = new String[1];
        letters[0] = "BusInfo";

        int buttons[] = new int[1];
        buttons[0] = DialogScreensIfc.BUTTON_OK;

        UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, 
                                   "INVALID_DATA", args,
                                   buttons, letters);
    }
}
