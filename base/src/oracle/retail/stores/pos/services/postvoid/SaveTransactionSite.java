/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/SaveTransactionSite.java /main/12 2011/02/16 09:13:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    abhayg    08/13/10 - STOPPING POS TRANSACTION IF REGISTER HDD IS FULL
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         8/4/2006 2:14:29 PM    Brett J. Larsen CR
 *         3929 - register totals should be updated before saving transaction
 *
 *         v7x->360Commerce
 *    3    360Commerce 1.2         3/31/2005 4:29:50 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:03 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:03 PM  Robert Pearse   
 *
 *
 *    4    .v7x      1.2.1.0     5/26/2006 8:12:55 AM   Michael Wisbauer moved
 *         saving adding register totals before saving
 *
 *   Revision 1.5  2004/09/27 22:32:04  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.4  2004/07/23 22:17:26  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.3  2004/02/12 16:48:15  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:28:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 04 2003 11:16:06   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 23 2003 17:28:36   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 13:03:24   epd
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:08:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:44:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:22:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.postvoid;

import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Save the transaction and notify user of completion,
 * 
 * @version $Revision: /main/12 $
 */
public class SaveTransactionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 5271527977390207562L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Mails the Continue letter.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        AbstractFinancialCargo cargo = (AbstractFinancialCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();
        try
        {
            // Add the transaction to the register so totals are updated
            ContextFactory.getInstance().getContext().getRegisterADO().addTransaction(txnADO);

            RegisterADO register = ContextFactory.getInstance().getContext().getRegisterADO();
            txnADO.save(register);
        }
        catch (DataException dataException)
        {
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            DialogBeanModel dialogModel = utility.createErrorDialogBeanModel(dataException);
            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            return;
        }

        // set last reprintable txn_id
        cargo.setLastReprintableTransactionID(txnADO.getTransactionID());

        // update hard totals
        try
        {
            ContextFactory.getInstance().getContext().getRegisterADO().writeHardTotals();
        }
        catch (DeviceException de)
        {
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("WriteHardTotalsError");
            model.setType(DialogScreensIfc.ERROR);
            // show dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            return;
        }

        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}
