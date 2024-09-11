/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/ForeignCurrencySelectedAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:23 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    mchellap  03/30/09 - Code review comments
 *    mchellap  03/26/09 - Fixed foreign currency nationality retrieval
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:13 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:45 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:07 PM  Robert Pearse
 *
 *   Revision 1.2  2004/06/17 22:36:28  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Add foreign currency to tender detail count interface.
 *
 *   Revision 1.1  2004/06/07 18:29:38  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Add foreign currency counts.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.utility.CountryCodeMap;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    Sets the currency selected.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ForeignCurrencySelectedAisle extends PosLaneActionAdapter
{
    /**
        revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        suffix for looking up the nationality of the country code in the bundles
    **/
    protected static final String NATIONALITY_SUFFIX = "_Nationality";

    //----------------------------------------------------------------------
    /**
        Sets the currency selected in the cargo. Mails "Continue" letter.
        <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();
        String currentForeignCurrency = bus.getCurrentLetter().getName();

        cargo.setCurrentForeignCurrency(currentForeignCurrency);

        String nationality = CountryCodeMap.getCountryDescriptor(cargo.getCountryCodeForForeignCurrency(currentForeignCurrency));

        cargo.setCurrentForeignNationality(nationality);
        cargo.setForeignTenderCountModelAssigned(false);

        String letterName = CommonLetterIfc.CONTINUE;
        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }
}
