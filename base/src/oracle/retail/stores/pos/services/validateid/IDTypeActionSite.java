/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/validateid/IDTypeActionSite.java /main/12 2011/02/16 09:13:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    abondala  11/03/08 - updated files related to customer id type reason
 *                         code.
 *    abondala  11/03/08 - updated files related to the Patriotic customer ID
 *                         types reason code
 *
 * ===========================================================================


     $Log:
      1    360Commerce 1.0         12/13/2005 4:47:06 PM  Barry A. Pape
     $

 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.validateid;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * This class determines where to go next.
 * 
 * @version $Revision: /main/12 $
 */
public class IDTypeActionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -819662658807663668L;
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
     * This method determines where to go next. If we need to always capture the
     * id issuer or if the card was not swiped, we go to the screen to select
     * the state or the country as appropriate. If we don't and the card was
     * swiped, we don't need to capture the id issuer.
     * 
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        Letter letter = new Letter(CommonLetterIfc.CONTINUE);
        ValidateIDCargoIfc cargo = (ValidateIDCargoIfc) bus.getCargo();

        String idType = cargo.getIdTypeName();

        boolean swiped = (cargo.getMSRModel() != null);
        if (cargo.isAlwaysCaptureIssuer() || !swiped)
        {
            if(idType.equals("DriversLicense") ||
               idType.equals("StateID") ||
               idType.equals("StateRegionID") ||
               idType.equals("StateCard"))
            {
                letter = new Letter("State");
            }
            else if (cargo.isCaptureCountry() &&
                     (idType.equals("Passport") ||
                      idType.equals("MilitaryID") ||
                      idType.equals("ResAlienID")))
            {
                letter = new Letter("Country");
            }
            else if (cargo.isAlwaysCaptureIssuer())
            {
                // StudentID
                // PhotoCreditCard
                throw new RuntimeException("Configuration problem: Cannot determine issuer of selected ID type " + idType);
            }
        }

        bus.mail(letter, BusIfc.CURRENT);
    }
}
