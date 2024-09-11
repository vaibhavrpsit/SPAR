/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/CallErrorSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
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
 * 3    360Commerce 1.2         3/31/2005 4:27:19 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:19:57 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:09:46 PM  Robert Pearse   
 *
 *Revision 1.1  2004/06/03 21:58:39  nrao
 *@scr 3916
 *New Site added to handle the condition for Call Error 
 *response returned by the Authorizer.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

// pos imports
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

//--------------------------------------------------------------------------
/**
    This site displays the Call Error/Call Reference dialog message.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CallErrorSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /**
     * Call reference letter
     */
    public static final String CALL_REFERENCE = "CallRef";
    
    /**
     * reference instant credit call
     */
    public static final String INSTANT_CREDIT_CALL_REFERENCE = "InstantCreditCallRef";

    //----------------------------------------------------------------------
    /**
        Displays the Call Error/Call Reference dialog message.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();
        String args[] = { cargo.getReferenceNumber() };
        UIUtilities.setDialogModel(ui, DialogScreensIfc.NOW_LATER, INSTANT_CREDIT_CALL_REFERENCE, args, CALL_REFERENCE);
    }
}
