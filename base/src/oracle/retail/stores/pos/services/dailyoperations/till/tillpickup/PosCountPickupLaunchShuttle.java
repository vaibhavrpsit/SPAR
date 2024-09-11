/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpickup/PosCountPickupLaunchShuttle.java /rgbustores_13.4x_generic_branch/2 2011/08/25 13:13:12 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  08/25/11 - Made cash Text to pick based on foreign currency
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    arathore  02/24/09 - Updated to set actual tender name in flpTender
 *                         instead of localized tender name.
 *    arathore  02/24/09 - Updated to set actual tender name in flpTender
 *                         instead of localized tender name.
 *    miparek   01/27/09 - changes to fix d#1812 Canadian Cash Pickup shows
 *                         unexpected non-mock characters in the prompt
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:22 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:11 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:06 PM  Robert Pearse
 *
 *   Revision 1.5  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:05  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:43  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:58:22   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jun 23 2003 13:44:08   DCobb
 * Canadian Check Till Pickup
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 *
 *    Rev 1.0   Apr 29 2002 15:26:10   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:30:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:19:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:15:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpickup;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.dailyoperations.poscount.PosCountCargo;


//------------------------------------------------------------------------------
/**


    @version $Revision: /rgbustores_13.4x_generic_branch/2 $
**/
//------------------------------------------------------------------------------

public class PosCountPickupLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 6136010365385388199L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.dailyoperations.till.tillpickup.PosCountPickupLaunchShuttle.class);

    public static final String SHUTTLENAME = "PosCountPickupLaunchShuttle";
    public static final String SUMMARY_COUNT_PREFIX = "SC_";
    public static final String SUMMARY_COUNT_SPEC = "SummaryCountSpec";

    protected TillPickupCargo pickupcargo;

    //--------------------------------------------------------------------------
    /**
       ##COMMENT-LOAD##

       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------

    public void load(BusIfc bus)
    {

        pickupcargo = (TillPickupCargo) bus.getCargo();

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
        UtilityManagerIfc utility =(UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        // Setup poscount cargo
        cargo.setRegister(pickupcargo.getRegister());

        cargo.setTillID(pickupcargo.getTillID());

        cargo.setCountType(PosCountCargo.PICKUP);

        cargo.setDefaultExpectedAmount(pickupcargo.getPickupCurrency());

        cargo.setTenderNationality(pickupcargo.getTenderNationality());

        StringBuffer extendedTenderName = new StringBuffer();

        if (!pickupcargo.getTenderNationality().equals(DomainGateway.getBaseCurrencyType().getCountryCode()))
        {
            extendedTenderName.append(pickupcargo.getTenderNationality());
        }
        extendedTenderName.append(pickupcargo.getTenderName());
        String lookup = SUMMARY_COUNT_PREFIX + cargo.removeBlanks(extendedTenderName.toString());
        String flpTender =  pickupcargo.getTenderName();
        cargo.setCurrentForeignCurrency(lookup);
        cargo.setCurrentFLPTender(flpTender);

        switch (pickupcargo.getPickupCountType())
        {
        case FinancialCountIfc.COUNT_TYPE_SUMMARY:
            cargo.setPickupCountDetailLevel(FinancialCountIfc.COUNT_TYPE_SUMMARY);
            break;
        case FinancialCountIfc.COUNT_TYPE_DETAIL:
            cargo.setPickupCountDetailLevel(FinancialCountIfc.COUNT_TYPE_DETAIL);
            break;
        case FinancialCountIfc.COUNT_TYPE_NONE:
            cargo.setPickupCountDetailLevel(FinancialCountIfc.COUNT_TYPE_NONE);
            break;
        default:
            break;
        }
    }
}
