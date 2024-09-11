/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/NoTransactionsErrorSite.java /rgbustores_13.4x_generic_branch/2 2011/08/18 08:44:03 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     08/17/11 - Modified to prevent the return of Gift Cards as
 *                         items and part of a transaction. Also cleaned up
 *                         references to gift cards objects in the return
 *                         tours.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:10 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:47 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/03/10 19:23:15  epd
 *   @scr 3561 fixed letter being mailed by dialog
 *
 *   Revision 1.5  2004/03/10 14:16:46  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.4  2004/02/27 22:43:50  baa
 *   @scr 3561 returns add trans not found flow
 *
 *   Revision 1.3  2004/02/12 16:51:46  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:05:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   May 12 2003 09:45:02   baa
 * change flow
 * 
 *    Rev 1.1   Aug 14 2002 14:13:20   jriggins
 * Deprecated displayNoTransactions() and displayInvalidTransaction() in favor of displayNoTransactionsForCustomer() and displayNoTransactionsForNumber() and displayInvalidTransactionNoSellItems() and displayInvalidTransactionNoQuantites() in order to move away from substituting phrases that make sense in Englsh in the bundles.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:06:44   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:12   msg
 * Initial revision.
 * 
 *    Rev 1.2   Mar 10 2002 18:01:14   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 * 
 *    Rev 1.1   Feb 05 2002 16:43:16   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 * 
 *    Rev 1.0   Sep 21 2001 11:24:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

// java imports

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This abstract class provides a common method for displaying the
    NO TRANSACTIONS ERROR SCREEN.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/2 $
**/
//--------------------------------------------------------------------------
public abstract class NoTransactionsErrorSite extends PosSiteActionAdapter implements NoTransactionsErrorIfc
{

    //----------------------------------------------------------------------
    /**
        Show the NO TRANSACTIONS FOR CUSTOMER ERROR SCREEN
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void displayNoTransactionsForCustomer(BusIfc bus)
    {
        // Get the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(NO_TRANSACTIONS_FOUND_CUSTOMER);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"ReturnItem");

        // display the screen
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    //----------------------------------------------------------------------
    /**
        Show the INVALID TRANSACTION: NO SELL ITEMS ERROR SCREEN
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void displayInvalidTransactionNoSellItems(BusIfc bus)
    {
      // Get the ui manager
      POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
      DialogBeanModel dialogModel = new DialogBeanModel();
      dialogModel.setResourceID(INVALID_RETURN_ITEMS);
      dialogModel.setType(DialogScreensIfc.ERROR);
      dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Invalid");

      // display the screen
      ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    //----------------------------------------------------------------------
    /**
        Show the INVALID TRANSACTION: NO QUANTITIES ERROR SCREEN
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void displayInvalidTransactionNoQuantites(BusIfc bus)
    {
      // Get the ui manager
      POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
      DialogBeanModel dialogModel = new DialogBeanModel();
      dialogModel.setResourceID(INVALID_TRANSACTION_NO_QUANTITIES);
      dialogModel.setType(DialogScreensIfc.ERROR);
      dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Invalid");

      // display the screen
      ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
