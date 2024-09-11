/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/common/TillUtility.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:16 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:08 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/06/25 22:37:36  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Added check for till status suspended..
 *
 *   Revision 1.7  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:13:09  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/13 12:57:46  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:49:36  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:40:02  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Mar 10 2003 17:10:08   DCobb
 * Renamed and moved methods from TillUtility to FinancialTotalsDataTransaction.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.2   Feb 17 2003 15:43:26   DCobb
 * Added Register Open flow to Resume Till service.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.1   Feb 12 2003 18:43:30   DCobb
 * Added methods getTillsFromDatabase(), getRegisterFromDatabase(), & operatorHasFloatingTill().
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.0   Dec 20 2002 11:20:08   DCobb
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.common;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
   Utility class to access tills and registers.
   <P>
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TillUtility
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.dailyoperations.common.TillUtility.class);

    //----------------------------------------------------------------------------
    /**
     * Returns true if the operator has a floating till.
     * @param the operator
     * @param the store
     * @param the businesss date
     * @return true if the operator has a floating till.
     **/
    //----------------------------------------------------------------------------
    public static boolean hasFloatingTill(EmployeeIfc operator,
                                          StoreIfc store,
                                          EYSDate businessDate,
                                          String serviceName)
    {
        boolean floatingTill = false;
        try {
           
            FinancialTotalsDataTransaction dt = null;
            
            dt = (FinancialTotalsDataTransaction) DataTransactionFactory.create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);
            
            TillIfc[] tills = dt.readStoreTills(store, businessDate);
            for (int i = 0; i < tills.length; i++)
            {
                if ((tills[i].getTillType() == AbstractStatusEntityIfc.TILL_TYPE_FLOATING) &&
                    (tills[i].getStatus() == AbstractStatusEntityIfc.STATUS_SUSPENDED) &&
                    (tills[i].getSignOnOperator().equals(operator)))
                {
                    floatingTill = true;
                    break;
                }
            }
        }
        catch (DataException de)
        {
            // log
            String stackTrace = Util.throwableToString(de);
            logger.warn( "Unable to determine if operator has floating till. " + de.getMessage() + "");

        }
        return floatingTill;
    }

}
