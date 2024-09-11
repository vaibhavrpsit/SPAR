/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/LookupStoreStatusSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *
 * ===========================================================================

     $Log:
      3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:23:22 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:28 PM  Robert Pearse   
     $
     Revision 1.7  2004/06/03 14:47:44  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.6  2004/04/20 13:17:06  tmorris
     @scr 4332 -Sorted imports

     Revision 1.5  2004/04/14 15:17:11  pkillick
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.4  2004/03/03 23:15:14  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.3  2004/02/12 16:50:58  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:37  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 16:01:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:18:38   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:36:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 08 2002 09:39:40   mpm
 * Initial revision.
 * Resolution for POS SCR-1518: Register Status screen does not display all Cashiers when in Register accountability
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.manager;
// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.StoreDataTransaction;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    This site queries the database for store status. <P>
    Unlike LookupStoreStatusSite in dailyoperations.common, this site only
    retrieves the store status.  This site does not deal hard totals or
    financials.  The business date currentyly in hard totals is used when
    multiple business days are open.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @see oracle.retail.stores.pos.services.dailyoperations.common.LookupStoreStatusSite
**/
//--------------------------------------------------------------------------
public class LookupStoreStatusSite
extends PosSiteActionAdapter
{                                       // begin class LookupStoreStatusSite

    private static final long serialVersionUID = -1014654051368965546L;

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Send a store status lookup inquiry to the database manager. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                   // begin arrive()
        // get cargo, store status
        ManagerCargo cargo = (ManagerCargo) bus.getCargo();
        String storeID = cargo.getStoreStatus().getStore().getStoreID();
        StoreStatusIfc[] s = null;

        StoreStatusIfc storeStatusFromHardTotals = cargo.getStoreStatus();

        // attempt to do the database lookup
        try
        {                               // begin read store statuses
            StoreDataTransaction dt = null;
            
            dt = (StoreDataTransaction) DataTransactionFactory.create(DataTransactionKeys.STORE_DATA_TRANSACTION);
            
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);   
            s =  dt.readStoreStatuses(storeID, utility.getRequestLocales());

            // if more than one business date, we'll have to look at some more information
            if (s.length > 1)
            {
                // check business date on register.  If register's business date is earlier
                // than any found that are open, choose the earliest found business date.
                // Otherwise, if we match, use the date we matched on.
                RegisterIfc register = cargo.getRegister();
                boolean dateMatched = false;
                for (int i=0; i<s.length; i++)
                {
                    if (register.getBusinessDate().equals(s[i].getBusinessDate()))
                    {
                        dateMatched = true;
                        cargo.setStoreStatus(s[i]);
                        i = s.length; // break;
                    }
                }
                if (dateMatched == false)
                {
                    cargo.setStoreStatus(s[0]);
                }
            }
            // if only one status, set it and move on
            else
            {
                cargo.setStoreStatus(s[0]);
            }
        }                               // end read store statuses
        // catch problems on the lookup
        catch (DataException e)
        {                               // begin handle data exception
            // set error code
            logger.warn(
                         "Store status lookup error: \n" + e + "");
        }                               // end handle data exception

        // mail letter
        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }                                   // end arrive()

}                                       // end class LookupStoreStatusSite
