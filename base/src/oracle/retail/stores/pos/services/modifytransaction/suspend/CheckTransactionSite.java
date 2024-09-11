/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/suspend/CheckTransactionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:31 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:14 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:00 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/24 16:21:27  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:16  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:47  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:15:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:39:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:31:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.suspend;

// Foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    Confirms state of trasnaction is Ok for suspend action.  If
    no transaction is in progress or active transaction
    has been tendered, an error message is displayed. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class CheckTransactionSite extends PosSiteActionAdapter
{                                       // begin class CheckTransactionSite

    /**
       revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       site name constant
    **/
    public static final String SITENAME = "CheckTransactionSite";

    //--------------------------------------------------------------------------
    /**
       Confirms state of trasnaction is Ok for suspend action.  If
       no transaction is in progress or active transaction
       has been tendered, an error message is displayed. <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                   // begin arrive()

        // get status of transaction
        ModifyTransactionSuspendCargo cargo = (ModifyTransactionSuspendCargo) bus.getCargo();
        RetailTransactionIfc transaction = cargo.getTransaction();

        // if no transaction is in progress, issue error message
        if (transaction == null)
        {
            POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

            // Using "generic dialog bean".
            DialogBeanModel model = new DialogBeanModel();

            // Set model to same name as dialog in config\posUI.properties
            // Set button and arguments
            model.setResourceID("SuspendTransactionsDisallowed");
            model.setType(DialogScreensIfc.ERROR);

            // set and display the model
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

        }
        // if transaction exists, confirm no tender items exist
        else
        {
            // if no tender, issue success letter
            if (transaction.getTenderLineItems() == null || transaction.getTenderLineItems().length == 0)
            {
                bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
            }
            // if tender items exist, issue error message
            else
            {
                POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

                // Using "generic dialog bean".
                DialogBeanModel model = new DialogBeanModel();

                // Set model to same name as dialog in config\posUI.properties
                // Set button and arguments
                model.setResourceID("SuspendTransactionsWithTenderDisallowed");
                model.setType(DialogScreensIfc.ERROR);

                // set and display the model
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }
        }

    }                                   // end arrive()

}                                       // end class class CheckTransactionSite

