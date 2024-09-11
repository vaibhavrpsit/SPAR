/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/common/LookupStoreStatusSite.java /main/14 2011/02/16 09:13:26 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                      from st_rgbustores_techissueseatel_generic_branch
 *    cgreen 02/05/10 - compare register and store date with Util
 *    cgreen 02/05/10 - added logging of compare of store date
 *    abonda 01/03/10 - update header date
 *
 * ===========================================================================

     $Log:
      7    360Commerce 1.6         4/1/2008 2:29:33 PM    Deepti Sharma   CR
           31016 forward port from v12x -> trunk
      6    360Commerce 1.5         1/10/2008 7:45:40 AM   Manas Sahu      Event
            Originator changes
      5    360Commerce 1.4         7/12/2007 5:46:55 PM   Alan N. Sinton  CR
           27494 Enhanced initialization failure conditions.
      4    360Commerce 1.3         7/3/2007 2:03:25 PM    Alan N. Sinton  CR
           27474 - Read store information even if store history table is
           empty.
      3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:23:22 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:12:28 PM  Robert Pearse   
     $
     Revision 1.6  2004/06/03 14:47:43  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.5  2004/04/20 13:13:09  tmorris
     @scr 4332 -Sorted imports

     Revision 1.4  2004/04/14 15:17:09  pkillick
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.3  2004/02/12 16:49:36  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:40:02  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 15:56:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   23 May 2002 17:44:02   vxs
 * Removed unneccessary concatenations in logging statements.
 * Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 *    Rev 1.0   Apr 29 2002 15:31:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:13:34   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:26:24   msg
 * Initial revision.
 *
 *    Rev 1.2   18 Feb 2002 09:55:26   epd
 * Fixed bug concerning business date entry
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   15 Feb 2002 09:20:52   epd
 * Removed prompt for user to choose from multiple business dates
 * Resolution for POS SCR-1253: Got the Enter Business Date screen at Register Close.  NTier/Multi Registers
 * Resolution for POS SCR-1254: Enter Busniess Date on Reg Close selected other date.  Got Tills Not Closed
 * Resolution for POS SCR-1255: Closed Reg after selecting latest business date.  EOD get DBC. But DB is up
 *
 *    Rev 1.0   29 Nov 2001 08:28:32   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.dailyoperations.common;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.StoreStatusCargo;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.StoreDataTransaction;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.EventOriginatorInfoBean;

/**
 * Query the database for store status.
 * 
 * @version $Revision: /main/14 $
 */
public class LookupStoreStatusSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -8678381790489740871L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * Send a store status lookup inquiry to the database manager.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        Letter letter = new Letter(CommonLetterIfc.CONTINUE);// letter to mail at end

        // get cargo, store status
        StoreStatusCargo cargo = (StoreStatusCargo) bus.getCargo();
        String storeID = cargo.getStoreStatus().getStore().getStoreID();
        StoreStatusIfc[] s = null;

        StoreStatusIfc storeStatusFromHardTotals = cargo.getStoreStatus();

        StoreDataTransaction dt = (StoreDataTransaction) DataTransactionFactory.create(DataTransactionKeys.STORE_DATA_TRANSACTION);
        
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        LocaleRequestor localeRequestor = utility.getRequestLocales();
        
        // attempt to do the database lookup
        try
        {
            // begin read store statuses        
            s =  dt.readStoreStatuses(storeID, localeRequestor);

            // save the store status list in the cargo
            cargo.setStoreStatusList(s);

            // if more than one business date, we'll have to look at some more information
            if (s.length > 1)
            {
                // check business date on register.  If register's business date is earlier
                // than any found that are open, choose the earliest found business date.
                // Otherwise, if we match, use the date we matched on.
                RegisterIfc register = cargo.getRegister();
                boolean dateMatched = false;
                for (int i = 0; i < s.length; i++)
                {
                    EYSDate registerDate = register.getBusinessDate();
                    EYSDate storeStatusDate = s[i].getBusinessDate();
                    if (logger.isInfoEnabled())
                    {
                        logger.info("Comparing Register date " + registerDate + " to store date " + i + ": " + storeStatusDate);
                    }
                    if (Util.isObjectEqual(registerDate, storeStatusDate))
                    {
                        dateMatched = true;
                        cargo.setStoreStatus(s[i]);
                        break;
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

                // If business date from cargo is before db business date
                // reset Hard Totals.  This is to accommodate the store
                // being opened from Back Office
                if (storeStatusFromHardTotals.getBusinessDate().before(s[0].getBusinessDate()))
                {
                    letter = new Letter("ResetHardTotals");
                }

            }
            cargo.getStoreStatus().setStale(false);
        }                               // end read store statuses
        // catch problems on the lookup
        catch (DataException e)
        {                               // begin handle data exception
            // set error code
            cargo.setDataExceptionErrorCode(e.getErrorCode());
            logger.error("Store status is stale; store status lookup error: \n" + e);
            cargo.getStoreStatus().setStale(true);
            
            // check store status already in cargo (read from hard totals)
            StoreStatusIfc storeStatus = cargo.getStoreStatus();

            // attempt to read the store info
            if(e.getErrorCode() == DataException.NO_DATA)
            {
                // get the store info like geocode, address, etc.
                try
                {
                    StoreIfc store = dt.readRegionDistrict(storeStatus.getStore().getStoreID(), localeRequestor);
                    storeStatus.setStore(store);
                }
                catch(DataException de)
                {
                    logger.error("Attempt to read store information failed: " + de);
                }
                // get the valid safe tenders
                try
                {
                    TenderDescriptorIfc[] safeTenderDescriptors = dt.readSafeTenders();
                    for(int i = 0; i < safeTenderDescriptors.length; i++)
                    {
                        storeStatus.addSafeTenderDesc(safeTenderDescriptors[i]);
                    }
                }
                catch(DataException de)
                {
                    logger.error("Attempt to read safe tender descriptors failed.", de);
                }
            }

            // if nothing in hard totals, we're dead meat
            if (storeStatus.getBusinessDate() == null || Util.isEmpty(storeStatus.getStore().getGeoCode()))
            {
                letter = new Letter(CommonLetterIfc.FAILURE);
            }
            // if date in hard totals, we will use it to prime business date
            else
            {
                if (logger.isInfoEnabled()) logger.info(
                            "Using hard totals store status:  business date [" + storeStatus.getBusinessDate() + "]");
            }
        }                               // end handle data exception

        EventOriginatorInfoBean.setEventOriginator("LookupStoreStatusSite.arrive");
        // mail letter
        bus.mail(letter, BusIfc.CURRENT);
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        String strResult = new String("Class:  LookupStoreStatusSite (Revision " + getRevisionNumber() + ") @"
                + hashCode());

        // pass back result
        return (strResult);
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
