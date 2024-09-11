/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/itemvalidate/StoreCouponItemInvalidDateAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:30 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:26 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:38  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:10  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Jan 15 2004 08:26:12   sfl
 * Initial revision.
 * Resolution for 3707: Store coupon pricing rule expiration message display not working
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry.itemvalidate;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;


//--------------------------------------------------------------------------
/**
 *   This aisle shows the Invalid Date Screen for StoreCoupon.<P>
 *   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class StoreCouponItemInvalidDateAisle extends LaneActionAdapter
{
    //--------------------------------------------------------------------------
    /**
     *   Revision Number furnished by source control system. <P>
     */
    //--------------------------------------------------------------------------
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static final String INVALID_STORE_COUPON_DATE = "Store Coupon Date Invalid";

    //----------------------------------------------------------------------
    /**
     *   Shows the Invalid Date Screen.
     *   <P>
     *   @param  bus     Service Bus
     */
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        Letter letterName = null;
        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean".
        DialogBeanModel model = new DialogBeanModel();

        // Set model to same name as dialog in config\posUI.properties
        // Set button and arugments
        // set and display the model
        model.setResourceID("InvalidStoreCouponItemDate");
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Invalid");
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    //----------------------------------------------------------------------
    /**
     *   Returns a string representation of this object.
     *   <P>
     *   @return String representation of object
     */
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  StoreCouponItemInvalidDateAisle (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
     *   Returns the revision number of the class. <P>
     *   @return String representation of revision number
     */
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
