/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/endofday/CheckStoreRegistersSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:22 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:13 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:59 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:13:09  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/13 12:57:46  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:37  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:17  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 04 2003 08:46:12   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Oct 09 2002 16:06:20   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:31:04   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:13:42   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:26:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:16:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.endofday;
// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This site confirms that all registers are closed. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckStoreRegistersSite extends PosSiteActionAdapter
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
                This site confirms that all registers are closed. <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        EndOfDayCargo cargo = (EndOfDayCargo)bus.getCargo();
        StoreStatusIfc storeStatus = cargo.getStoreStatus();
        LetterIfc letter = new Letter(CommonLetterIfc.SUCCESS);

        try
        {
            // pull business data from cargo
            EYSDate businessDate = storeStatus.getBusinessDate();
            StoreIfc store = storeStatus.getStore();

            // set transaction and execute
            FinancialTotalsDataTransaction dt = null;
            
            dt = (FinancialTotalsDataTransaction) DataTransactionFactory.create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);
            
            RegisterIfc[] r = dt.readStoreRegisters(store, businessDate);

            // loop through records and check status
            for (int i = 0; i < r.length; i++)
            {
                if (r[i].getStatus() == AbstractStatusEntityIfc.STATUS_OPEN)
                {
                    letter = new Letter(CommonLetterIfc.REGISTER_OPEN_ERROR);
                    break;
                }
            }

            // Mail the letter only if the look is ok.
            bus.mail(letter, BusIfc.CURRENT);
        }
        catch (DataException e)
        {
            // if no-data exception occurs, ignore it
            if (e.getErrorCode() == DataException.NO_DATA)
            {
                bus.mail(letter, BusIfc.CURRENT);
            }
            else
            {
                displayDBError(bus, e.getErrorCode());
            }
        }
    }

    //----------------------------------------------------------------------
    /**
        Displays the Database error
        <P>
        @param  BusIfc the bus.
        @param  int the DB error code.
    **/
    //----------------------------------------------------------------------
    public void displayDBError(BusIfc bus, int dbError)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean".
        DialogBeanModel model = new DialogBeanModel();

        // Set model to same name as dialog in config\posUI.properties
        // Set button and arugments
        model.setResourceID("DatabaseError");

        model.setType(DialogScreensIfc.ERROR);
        String[] args = new String[1];
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        args[0] = utility.getErrorCodeString(dbError);
        model.setArgs(args);

        // set and display the model
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
