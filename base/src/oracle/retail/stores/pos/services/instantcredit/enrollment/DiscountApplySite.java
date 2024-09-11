/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/enrollment/DiscountApplySite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:27:45 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:20:58 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:10:35 PM  Robert Pearse   
 * $
 * Revision 1.3  2004/02/12 16:50:42  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:51:22  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 * updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Jan 14 2004 15:53:14   nrao
 * Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.enrollment;

// foundation imports
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
 *  Displays the apply discount dialog.
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class DiscountApplySite extends PosSiteActionAdapter
{
    /** PVCS revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** dialog resource id constant */
    public static final String RESOURCE_ID = "InstantCreditDiscount";
    
    /* constant for letter */
    public static final String VALID_LETTER = "Valid";

    //--------------------------------------------------------------------------
    /**
     *  Displays the apply discount dialog if enrolling within a transaction
     *  @param bus the bus traversing this site
     */
    //---------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();

        // if sale transaction, display the dialog        
        if(cargo.getTransaction().getTransactionType() != TransactionIfc.TYPE_INSTANT_CREDIT_ENROLLMENT)
        {
            UIUtilities.setDialogModel(ui, DialogScreensIfc.CONFIRMATION, RESOURCE_ID, null);
        }
        // otherwise mail letter
        else
        {
            bus.mail(new Letter(VALID_LETTER), BusIfc.CURRENT); 
        }
    }
}
