/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/CheckExpirationSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:13 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:55 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:20:26   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:35:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:21:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

//foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//------------------------------------------------------------------------------
/**
    Tests the layaway object in the cargo to see if it's expired,
    but within the grace period.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class CheckExpirationSite extends PosSiteActionAdapter
{
    /**
        class name constant
    **/
    public static final String SITENAME = "CheckExpirationSite";

    /**
        revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
            Tests the layaway object in the cargo to see if it's expired,
            but within the grace period.
            <P>
            @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        Letter result = new Letter (CommonLetterIfc.CONTINUE); // default value

        FindLayawayCargoIfc cargo = (FindLayawayCargoIfc)bus.getCargo();

        LayawayIfc layaway = cargo.getLayaway();

        // get current date and set to date-type-only
        EYSDate currentDate = DomainGateway.getFactory().getEYSDateInstance();
        currentDate.initialize(EYSDate.TYPE_DATE_ONLY);

        // if the layaway's active, and the expiration date is later than today
        if ((layaway.getStatus() == LayawayConstantsIfc.STATUS_ACTIVE ||
             layaway.getStatus() == LayawayConstantsIfc.STATUS_NEW) &&
            currentDate.after(layaway.getExpirationDate()))
        {
            result = new Letter ("Expiration");
        }

        bus.mail(result, BusIfc.CURRENT);

    }

}
