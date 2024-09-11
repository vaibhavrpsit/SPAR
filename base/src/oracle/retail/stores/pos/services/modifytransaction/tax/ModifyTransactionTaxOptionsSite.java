/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/tax/ModifyTransactionTaxOptionsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:32 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:41 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:14:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:39:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   17 Jan 2002 13:01:56   pjf
 * Modified to use new security override service and correct SCR 403.
 * Resolution for POS SCR-403: Security Override continually loops in Trans Tax
 *
 *    Rev 1.0   Sep 21 2001 11:31:46   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.tax;

// foundation imports
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays the transaction tax options menu.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ModifyTransactionTaxOptionsSite extends PosSiteActionAdapter
{

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Shows the screen for all the options of ModifyTransactionTax
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // get the POS UI manager
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ModifyTransactionTaxCargo cargo = (ModifyTransactionTaxCargo)bus.getCargo();
        TransactionTaxIfc transactionTax = cargo.getTransactionTax();

         if (transactionTax != null
                 && transactionTax.getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("TaxExemptOverrideError");
            dialogModel.setType(DialogScreensIfc.ERROR);

            uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
        }
        else
        {
            // show the transaction tax options menu
            uiManager.showScreen(POSUIManagerIfc.TRANSACTION_TAX_OPTIONS);
        }

    }
}
