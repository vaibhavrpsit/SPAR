/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/delete/ConfirmCustomerDeleteAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:26 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:22 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:11 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:14  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:26  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:41:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:33:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:11:56   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:24:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   04 Jan 2002 16:22:00   baa
 * change letter for  negative response
 * Resolution for POS SCR-508: Select 'No' on Confirm Customer Delete returns to wrong screen
 *
 *    Rev 1.0   16 Nov 2001 17:40:18   baa
 * Initial revision.
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.0   Sep 21 2001 11:15:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.delete;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

//--------------------------------------------------------------------------
/**
    Prompts the user for confirmation of their intent
    to delete the customer record.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ConfirmCustomerDeleteAisle extends PosLaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Prompts for confirmation of delete.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // display dialog
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        String letters[] = new String[2];
        letters[0] = CommonLetterIfc.CONTINUE;
        letters[1] = CommonLetterIfc.RETRY;

        int buttons[] = new int[2];
        buttons[0] = DialogScreensIfc.BUTTON_YES;
        buttons[1] = DialogScreensIfc.BUTTON_NO;

        UIUtilities.setDialogModel(ui,DialogScreensIfc.CONFIRMATION,"CustDelConfirm",null,
                                   buttons, letters);
     }

}
