/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/CheckAllChargesSite.java /main/12 2011/02/16 09:13:26 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:04 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:53 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:49:38  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:40  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jul 03 2003 15:42:44   RSachdeva
 * To set the correct letter in StartCount and not make other tenders entered back to expected amounts
 * Resolution for POS SCR-2425: Credit and Canadian Tender amounts missing from Select Tender screen when Blind Close = No
 * 
 *    Rev 1.0   Apr 29 2002 15:30:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:14:12   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:26:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   07 Jan 2002 13:01:44   epd
 * Removed UI screen and associated logic
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.0   Sep 21 2001 11:17:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Check to make sure that all tender have been counted.
 * 
 * @version $Revision: /main/12 $
 */
public class CheckAllChargesSite extends PosSiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 1889239842935109240L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * Site name for logging
     */
    public static final String SITENAME = "CheckAllChargesSite";

    /**
     * Check to make sure that all tender have been counted.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Get the cargo
        PosCountCargo cargo = (PosCountCargo) bus.getCargo();
        // To set the correct letter in StartCount and
        // not make other tenders entered back to expected amounts
        cargo.setCurrentActivity(PosCountCargo.CREDIT_AMT_ENTER);
        cargo.setCurrentCharge(PosCountCargo.CREDIT_AMT_ENTER);
        cargo.updateAcceptedCountWithCharge();

        // Tell the site to go on.
        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
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
