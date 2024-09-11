/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/LookupRegisterStatusSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   12/04/08 - fix possible NPE with register coming from data
 *                         transaction
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:21 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:28 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:17:06  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/13 12:57:46  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:14  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:58  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:01:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 10 2003 17:10:06   DCobb
 * Renamed and moved methods from TillUtility to FinancialTotalsDataTransaction.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.1   Feb 18 2003 16:31:30   DCobb
 * LookupRegisterStatusSite needs to update the register tills.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.0   Apr 29 2002 15:18:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:36:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 08 2002 09:39:02   mpm
 * Modified to read statuses from database, handle multiple cashiers.
 *
 *    Rev 1.0   Mar 06 2002 09:56:22   mpm
 * Initial revision.
 * Resolution for POS SCR-1513: Add Till Status screen
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * This site queries the database for register status.
 * <P>
 * Unlike LookupRegisterSite in dailyoperations.common, this site only retrieves
 * the register status and the tills. This site does not deal with the financial
 * totals.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @see oracle.retail.stores.pos.services.dailyoperations.common.LookupRegisterStatusSite
 */
public class LookupRegisterStatusSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -6151110045681549503L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Send a store status lookup inquiry to the database manager.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get cargo
        ManagerCargo cargo = (ManagerCargo)bus.getCargo();
        // get register
        RegisterIfc register = cargo.getRegister();
        try
        {
            // update register from database
            FinancialTotalsDataTransaction dt = null;

            dt = (FinancialTotalsDataTransaction)DataTransactionFactory
                    .create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);

            register = dt.readRegisterWithTillTotals(register);

            if (register == null)
            {
                if (logger.isInfoEnabled())
                    logger.info("Register can't be read from database: " + cargo.getRegister().getWorkstation().getWorkstationID());
            }
            else
            {
                cargo.setRegisterStatus(register);
            }
        }
        // catch problems on the lookup
        catch (DataException e)
        {
            logger.warn("Register " + register.getWorkstation().getWorkstationID() + " lookup failed.");
            // if any errors, use cached value
            cargo.setRegisterStatus(cargo.getRegister());
        }

        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }
}