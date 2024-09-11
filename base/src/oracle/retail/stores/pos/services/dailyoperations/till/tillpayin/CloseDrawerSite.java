/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayin/CloseDrawerSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:17 mszekely Exp $
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
 *    4    360Commerce 1.3         1/25/2006 4:10:52 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:27:28 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:02 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/17/2005 16:39:25    Jason L. DeLeau 4345:
 *         Replace any uses of Gateway.log() with the log4j.
 *    3    360Commerce1.2         3/31/2005 15:27:28     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:20:16     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:02     Robert Pearse
 *
 *   Revision 1.3  2004/02/12 16:50:03  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:48:04  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:58:12   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:26:52   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:30:08   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:19:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpayin;

// Foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.till.tilloptions.TillCashDrawer;

//------------------------------------------------------------------------------
/**
    Attempts to close the cash drawer by calling
    TillCashDrawer.tillCloseCashDrawer
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @see TillCashDrawer
**/
//------------------------------------------------------------------------------
public class CloseDrawerSite extends PosSiteActionAdapter
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
        CloseDrawerSite
    **/
    //--------------------------------------------------------------------------

    public static final String SITENAME = "CloseDrawerSite";

    //--------------------------------------------------------------------------
    /**
            Calls TillCashDrawer.tillCloseCashDrawer to close the cash drawer.
            <P>
            @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
    	if (logger.isDebugEnabled()) logger.debug(SITENAME + ".arrive starting...");

        TillCashDrawer.tillCloseCashDrawer(bus);

        if (logger.isDebugEnabled()) logger.debug(SITENAME + ".arrive ending...");
    }

 }
