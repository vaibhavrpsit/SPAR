/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/endofday/CheckStoreTillsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:22 mszekely Exp $
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
 *    Rev 1.1   Mar 07 2003 10:34:16   DCobb
 * Code cleanup from code review.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.0   Dec 20 2002 11:22:32   DCobb
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.endofday;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    This site confirms that all registers are closed. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckStoreTillsSite extends PosSiteActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        Open Till Error letter name
    **/
    public static final String OPEN_TILL_ERROR = "OpenTillError";

    //----------------------------------------------------------------------
    /**
        This site confirms that all tills are closed. <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        EndOfDayCargo cargo = (EndOfDayCargo)bus.getCargo();
        StoreStatusIfc storeStatus = cargo.getStoreStatus();

        String letterName = CommonLetterIfc.SUCCESS;
        LetterIfc letter = new Letter(CommonLetterIfc.SUCCESS);

        try
        {
            // pull business data from cargo
            EYSDate businessDate = storeStatus.getBusinessDate();
            StoreIfc store = storeStatus.getStore();

            // set transaction and execute
            FinancialTotalsDataTransaction dt = null;
            
            dt = (FinancialTotalsDataTransaction) DataTransactionFactory.create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);
            
            TillIfc[] tills = dt.readStoreTills(store, businessDate);

            // loop through records and check status
            for (int i = 0; i < tills.length; i++)
            {
                if ((tills[i].getStatus() == AbstractStatusEntityIfc.STATUS_OPEN) ||
                    (tills[i].getStatus() == AbstractStatusEntityIfc.STATUS_SUSPENDED))
                {
                    letterName = OPEN_TILL_ERROR;
                    break;
                }
            }
        }
        catch (DataException e)
        {
            if (e.getErrorCode() != DataException.NO_DATA)
            {
                letterName = CommonLetterIfc.DB_ERROR;
            }
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }
}
