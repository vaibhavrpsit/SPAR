/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/check/CheckIDTypeActionSite.java /rgbustores_13.4x_generic_branch/1 2011/04/26 17:28:46 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    ohorne    03/28/11 - code correction: StateRegionID
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  03/10/09 - check for correct tender flow of check with
 *                         different ID selected
 *    mahising  02/21/09 - Fixed issue for check tender validation for
 *                         telephone number field
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:08 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:56 PM  Robert Pearse
 *
 *   Revision 1.1  2004/04/13 21:07:36  bwf
 *   @scr 4263 Decomposition of check.
 *
 *   Revision 1.3  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Nov 07 2003 16:11:44   bwf
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.check;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    This class determines where to go next.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckIDTypeActionSite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        This method determines where to go next.
        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();

        HashMap tenderAttributes = cargo.getTenderAttributes();
        String idType = (String)
            cargo.getTenderAttributes().get(TenderConstants.ID_TYPE);
        Letter letter;

        boolean swiped = (tenderAttributes.get(TenderConstants.MSR_MODEL) != null);

        if((idType.equals("DriversLicense") || idType.equals("StateRegionID")) && !swiped)
        {
            letter = new Letter("Validate");
        }
        else
        {
            letter = new Letter("NoValidate");
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
}
