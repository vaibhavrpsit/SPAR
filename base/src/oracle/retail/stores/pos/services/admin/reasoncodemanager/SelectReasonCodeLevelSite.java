/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/reasoncodemanager/SelectReasonCodeLevelSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:55 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:10  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:48:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:36:39  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jul 19 2003 15:25:08   mrm
 * Handle only store level reason codes
 * Resolution for POS SCR-3172: Remove the Reason Code Level screen - not in req, remove Corp level Reason Codes
 * 
 *    Rev 1.0   Apr 29 2002 15:38:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:32   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:20:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 23 2002 08:09:22   mpm
 * Employed DataInputBean for the reason code bean.
 *
 *    Rev 1.0   Sep 21 2001 11:12:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.reasoncodemanager;

// java imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//------------------------------------------------------------------------------
/**
    Select the location level (e.g., corporation or store) to which the
    reason codes will apply.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class SelectReasonCodeLevelSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
        Sets up the UI to display the potential location levels
        (e.g., corporation or store) to which the reason codes will apply. <p>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // We are starting afresh.
        ReasonCodeCargo cargo = (ReasonCodeCargo)bus.getCargo();
        cargo.reset();

        LetterIfc letter = new Letter(CommonLetterIfc.NEXT);
        bus.mail(letter, BusIfc.CURRENT);
    }
}
