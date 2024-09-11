/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/CheckOfflineSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:52 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:57 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/29 19:06:43  jdeleau
 *   @scr 6838 Add a message to the error dialog about register close completing
 *   when the database is back up.
 *
 *   Revision 1.5  2004/04/12 17:35:40  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Corrected name of variable.
 *
 *   Revision 1.4  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 03 2003 10:55:00   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Oct 09 2002 14:41:58   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:34:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:08:32   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:22:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:14:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:06:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;
// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.DispatcherIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This site makes sure the data manager is online and that the Persistent
    Queue(s) is empty.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckOfflineSite extends PosSiteActionAdapter
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    private String databaseErrorResourceID = "DatabaseError";

    //----------------------------------------------------------------------
    /**
        Checks the status of all the registers in the store.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Get the data manager
        DispatcherIfc       d = Gateway.getDispatcher();
        DataManagerIfc     dm = (DataManagerIfc)d.getManager(DataManagerIfc.TYPE);
        POSUIManagerIfc    ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel model = new DialogBeanModel();

        // If the transactions are offline, display an error message
        if (transactionsAreOffline(dm))
        {
            // Create args
            String[] args = new String[1];
            UtilityManagerIfc utility = 
              (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            args[0] = utility.getErrorCodeString(DataException.CONNECTION_ERROR);
            // Set model and update ui
            model.setArgs(args);
            model.setType(DialogScreensIfc.ERROR);
            model.setResourceID(getDatabaseErrorResourceID());
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        else
        if (!dm.areQueuesEmpty())
        {
            // Create args
            String[] args = new String[1];
            int iCount    = dm.getTotalQueuedTransactionCount();
            args[0]       = Integer.toString(iCount);

            // Set model and update ui
            model.setArgs(args);
            model.setType(DialogScreensIfc.ERROR);
            model.setResourceID("QueuesNotEmpty");
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
        // Otherwise go on.
        else
        {
            LetterIfc letter = new Letter(CommonLetterIfc.SUCCESS);
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

    //----------------------------------------------------------------------
    /**
        Checks the status of the required transactions.
        @param  dm DataManagerIfc
        @return boolean true if the transactions are offline.
    **/
    //----------------------------------------------------------------------
    protected boolean transactionsAreOffline(DataManagerIfc dm)
    {
        boolean        offline = true;

        try
        {
            if (dm.getTransactionOnline
                  (UtilityManagerIfc.CLOSE_REGISTER_TRANSACTION_NAME) ||
                dm.getTransactionOnline
                  (UtilityManagerIfc.CLOSE_STORE_REGISTER_TRANSACTION_NAME))
            {
                offline = false;
            }
        }
        catch (DataException de)
        {
            // If not found then running default datatech and offline will not
            // be detected.
        }

        return offline;
    }
    
    /**
     * @return Returns the databaseErrorResourceID.
     */
    public String getDatabaseErrorResourceID()
    {
        return databaseErrorResourceID;
    }
    /**
     * @param databaseErrorResourceID The databaseErrorResourceID to set.
     */
    public void setDatabaseErrorResourceID(String databaseErrorResourceID)
    {
        this.databaseErrorResourceID = databaseErrorResourceID;
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
