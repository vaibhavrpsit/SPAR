/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillloan/PosCountLoanLaunchShuttle.java /main/13 2013/03/21 18:55:27 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    arabalas  03/21/13 - added the missing imports
 *    arabalas  03/19/13 - set the value of foreign currency
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
 *    Rev 1.0   Aug 29 2003 15:57:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:28:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:29:10   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:18:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:14:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillloan;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;


//------------------------------------------------------------------------------
/**


    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
public class PosCountLoanLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -6370148937330385095L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.dailyoperations.till.tillloan.PosCountLoanLaunchShuttle.class);

    public static final String SUMMARY_COUNT_PREFIX = "SC_";

    public static final String SHUTTLENAME = "PosCountLoanLaunchShuttle";

    private TillLoanCargo loancargo;

    //--------------------------------------------------------------------------
    /**
       ##COMMENT-LOAD##

       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        loancargo = (TillLoanCargo) bus.getCargo();

    }

    //--------------------------------------------------------------------------
    /**
       ##COMMENT-UNLOAD##

       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        PosCountCargo cargo = (PosCountCargo) bus.getCargo();

        // Setup poscount cargo
        cargo.setRegister(loancargo.getRegister());

        cargo.setTillID(loancargo.getTillID());

        cargo.setCountType(PosCountCargo.LOAN);

        unloadCurrentForeignCurrency(cargo);
                
        if (loancargo.getLoanCountType() == FinancialCountIfc.COUNT_TYPE_SUMMARY)
        {
            cargo.setSummaryFlag(true);
        }
        else
        {
            cargo.setSummaryFlag(false);
        }

    }

    /**
     * Apply the current foreign currency to the specified cargo and apply the
     * cargo's tender nationality if its is different from base.
     */
    protected void unloadCurrentForeignCurrency(PosCountCargo countCargo)
    {
        StringBuilder extendedTenderName = new StringBuilder();
        if (!DomainGateway.getBaseCurrencyType().getCountryCode().equals(countCargo.getTenderNationality()))
        {
            extendedTenderName.append(countCargo.getTenderNationality());
        }
        extendedTenderName.append(countCargo.getCurrentFLPTender());
        String lookup = SUMMARY_COUNT_PREFIX + countCargo.removeBlanks(extendedTenderName.toString());
        countCargo.setCurrentForeignCurrency(lookup);
    }
    
}
