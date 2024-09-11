/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/initialize/ContinueOpenDrawerRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:10 mszekely Exp $
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
 * 3    360Commerce 1.2         3/31/2005 4:27:30 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:20:24 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:10:12 PM  Robert Pearse   
 *
 *Revision 1.6  2004/06/03 14:47:43  epd
 *@scr 5368 Update to use of DataTransactionFactory
 *
 *Revision 1.5  2004/04/20 13:10:59  tmorris
 *@scr 4332 -Sorted imports
 *
 *Revision 1.4  2004/04/13 12:57:46  pkillick
 *@scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *Revision 1.3  2004/02/12 16:48:20  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:22:51  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:24:32   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 04 2003 19:03:36   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.initialize;

// foundation imports
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    This road sets the current till and drawer status for manual drawer operation.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ContinueOpenDrawerRoad extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Set the function ID.
        @param bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
        RegisterIfc register = cargo.getRegister();
        register.getDrawer(DrawerIfc.DRAWER_PRIMARY)
                .setDrawerStatus(AbstractStatusEntityIfc.DRAWER_STATUS_OCCUPIED, register.getCurrentTillID());

        // update drawer status in database
        try
        {
           
            FinancialTotalsDataTransaction db = null;
            
            db = (FinancialTotalsDataTransaction) DataTransactionFactory.create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);
            
            db.updateDrawerStatus(register);
        }
        catch (DataException e)
        {
            logger.error( "Exception: Unable to update cash drawer. " + e.getMessage() + "");
        }

    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}
