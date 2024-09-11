/* ===========================================================================
* Copyright (c) 2003, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/RedeemLaunchShuttle.java /main/17 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    asinton   08/12/11 - set the tour context to prevent null pointer
 *                         exception
 *    jswan     12/01/10 - Modified to prevent loss possible loss of
 *                         transaction sequence ID during redeem process.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 |      5    360Commerce 1.4         5/23/2008 6:34:48 AM   subramanyaprasad gv
 |            CR 31359: Code reviewed by Mani.
 |      4    360Commerce 1.3         4/27/2008 5:28:27 PM   Charles D. Baker CR
 |            31482 - Updated the journalResponse method of GetResponseSite to
 |            intelligently journal entries with the appropriate journal type
 |           (Trans or Not Trans). Code Review by Tony Zgarba.
 |      3    360Commerce 1.2         3/31/2005 4:29:36 PM   Robert Pearse   
 |      2    360Commerce 1.1         3/10/2005 10:24:35 AM  Robert Pearse   
 |      1    360Commerce 1.0         2/11/2005 12:13:36 PM  Robert Pearse   
 |     $
 |     Revision 1.11.2.1  2004/12/10 17:04:55  lzhao
 |     @scr 7824: Cancel button print/save cancelled txn, Undo goes to previous screen
 |
 |     Revision 1.11  2004/09/23 00:07:11  kmcbride
 |     @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 |
 |     Revision 1.10  2004/04/22 22:35:55  blj
 |     @scr 3872 - more cleanup
 |
 |     Revision 1.9  2004/04/09 16:56:01  cdb
 |     @scr 4302 Removed double semicolon warnings.
 |
 |     Revision 1.8  2004/04/08 22:04:15  bjosserand
 |     @scr 4093 Transaction Reentry
 |
 |     Revision 1.7  2004/04/07 22:49:41  blj
 |     @scr 3872 - fixed problems with foreign currency, fixed ui labels, redesigned to do validation and adding tender to transaction in separate sites.
 |
 |     Revision 1.6  2004/03/11 20:03:23  blj
 |     @scr 3871 - added/updated shuttles to/from redeem, to/from tender, to/from completesale.
 |     also updated sites cargo for new redeem transaction.
 |
 |     Revision 1.5  2004/03/09 15:12:44  blj
 |     @scr 3871 - updated redeem launch shuttle.
 |
 |     Revision 1.4  2004/02/26 04:46:55  blj
 |     @scr 0 redeem service has moved to _360commerce.  Redeem is an ADO service.
 |
 |     Revision 1.3  2004/02/12 16:48:17  mcs
 |     Forcing head revision
 |
 |     Revision 1.2  2004/02/11 21:22:50  rhafernik
 |     @scr 0 Log4J conversion and code cleanup
 |
 |     Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 |     updating to pvcs 360store-current
 |
 |
 | 
 |    Rev 1.0   Dec 10 2003 18:06:10   nrao
 | Initial revision.
 |    
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.redeem.RedeemCargo;

import org.apache.log4j.Logger;

/**
 * This class set up the data for the Redeem tour
 */
public class RedeemLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    static final long serialVersionUID = -6106101499794375874L;

    /** The logger to which log messages will be sent. */
    protected static final Logger logger = Logger.getLogger(RedeemLaunchShuttle.class);

    /**
     * shuttle name constant
     */
    public static final String SHUTTLENAME = "RedeemLaunchShuttle";

    /**
     * @param bus the bus being loaded
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
    }

    /**
     * @param bus the bus being unloaded
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);

        RedeemCargo cargo = (RedeemCargo)bus.getCargo();
        cargo.setTransactionInProgress(false);
    }
}
