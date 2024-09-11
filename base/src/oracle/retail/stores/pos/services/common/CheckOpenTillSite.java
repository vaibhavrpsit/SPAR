/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/CheckOpenTillSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:53 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:58 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/27 22:32:03  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
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
 *    Rev 1.0   Aug 29 2003 15:54:16   CSchellenger
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
 *    Rev 1.1   Feb 12 2003 18:40:04   DCobb
 * Check for floating till belonging to operator & show Till Suspended dialog.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.0   Apr 29 2002 15:35:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:08:36   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:22:18   msg
 * Initial revision.
 * 
 *    Rev 1.1   30 Jan 2002 16:12:58   epd
 * now reports on open AND suspended tills
 * Resolution for POS SCR-946: With Till Suspended select POS, used ID that opened till & got wrong error
 * 
 *    Rev 1.0   Sep 21 2001 11:13:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:06:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

// foundation imports
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.common.TillUtility;


//--------------------------------------------------------------------------
/**
    This site makes sure the till is open.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckOpenTillSite extends PosSiteActionAdapter
{
    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Check if the till is open.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        AbstractFinancialCargo cargo = (AbstractFinancialCargo) bus.getCargo();
        RegisterIfc register = cargo.getRegister();
        String letterName = null;

        if (aTillIsOpen(register))
        {
            letterName = CommonLetterIfc.SUCCESS;
        }
        else if (register.getAccountability() == AbstractStatusEntityIfc.ACCOUNTABILITY_CASHIER)
        {
            // Check for floating till belonging to this operator
            EmployeeIfc operator = cargo.getOperator();
            StoreIfc store = cargo.getStoreStatus().getStore();
            EYSDate businessDate = cargo.getStoreStatus().getBusinessDate();
            String serviceName = bus.getServiceName();
            if (TillUtility.hasFloatingTill(operator, store, businessDate, serviceName))
            {
                letterName = "ResumeTill";
            }
            else
            {
                letterName = CommonLetterIfc.FAILURE;
            }
        }
        else
        {
            letterName = CommonLetterIfc.FAILURE;
        }

        if (letterName != null)
        {
            bus.mail(new Letter(letterName), BusIfc.CURRENT);
        }
    }

    //----------------------------------------------------------------------
    /**
       Returns true if there is an open or suspended till for this register.
       <P>
       @param the register
       @return true is there is an open or suspended till.
    **/
    //----------------------------------------------------------------------
    protected boolean aTillIsOpen(RegisterIfc register)
    {
        // Check register tills
        TillIfc[] tills = register.getTills();
        boolean open    = false;

        if (tills != null)
        {
            for (int i = 0; i < tills.length; i++)
            {
                TillIfc t = tills[i];
                int status = t.getStatus();
                if (status == AbstractStatusEntityIfc.STATUS_OPEN ||
                    (status == AbstractStatusEntityIfc.STATUS_SUSPENDED &&
                     tills[i].getTillType() == AbstractStatusEntityIfc.TILL_TYPE_STATIONARY))
                {
                    open = true;
                    break;
                }
            }
        }

        return(open);
    }
}
