/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillloan/PosCountLoanReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:17 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:22 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:06 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/04/30 18:16:03  dcobb
 *   @scr 4098 Open drawer before detail count screens.
 *   Loan changed to open drawer before detail count screens.
 *
 *   Revision 1.4  2004/04/09 16:56:02  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:49:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:54  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Feb 04 2004 18:55:42   DCobb
 * Return the To/From registers.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 * 
 *    Rev 1.0   Aug 29 2003 15:57:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:28:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:29:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:18:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:14:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillloan;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;

//------------------------------------------------------------------------------
/**


    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class PosCountLoanReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7793436880874462566L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.dailyoperations.till.tillloan.PosCountLoanReturnShuttle.class);

    public static final String SHUTTLENAME = "PosCountLoanReturnShuttle";

    protected FinancialTotalsIfc posCountTotal;
    protected String toRegister;
    protected String fromRegister;

    //--------------------------------------------------------------------------
    /**
       ##COMMENT-LOAD##

       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        PosCountCargo cargo = (PosCountCargo) bus.getCargo();

        posCountTotal = cargo.getUpdateTotals();
        toRegister = cargo.getPickupAndLoanToRegister();
        fromRegister = cargo.getPickupAndLoanFromRegister();

    }

    //--------------------------------------------------------------------------
    /**
       ##COMMENT-UNLOAD##

       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        TillLoanCargo cargo = (TillLoanCargo) bus.getCargo();

        cargo.setLoanTotals(posCountTotal);
        TillAdjustmentTransactionIfc transaction = cargo.getTransaction();
        transaction.setFromRegister(fromRegister);
        transaction.setToRegister(toRegister);

    }
}
