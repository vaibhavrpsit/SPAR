/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpickup/TenderAlternativeZeroSelectedAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:18 mszekely Exp $
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
 *    4    360Commerce 1.3         4/25/2007 8:52:28 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:30:21 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:52 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:47 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/04/29 20:06:21  dcobb
 *   @scr 4098 Open Drawer before detail count screens.
 *   Pickup changed to open drawer before detail count screens.
 *
 *   Revision 1.4  2004/02/16 14:47:31  blj
 *   @scr 3838 - cleanup code
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
 *    Rev 1.1   Feb 04 2004 18:58:58   DCobb
 * Removed conversion from int to String back to int.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 * 
 *    Rev 1.0   Aug 29 2003 15:58:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jun 23 2003 13:44:16   DCobb
 * Canadian Check Till Pickup
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 * 
 *    Rev 1.0   Apr 29 2002 15:26:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:30:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   02 Mar 2002 12:48:00   pdd
 * Converted to use TenderTypeMapIfc.
 * Resolution for POS SCR-627: Make the Tender type list extendible.
 * 
 *    Rev 1.0   Sep 21 2001 11:19:54   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:15:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpickup;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//------------------------------------------------------------------------------
/**
    Set the currency to alternate cash. Mail the letter indicating the pickup 
    cash count type.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class TenderAlternativeZeroSelectedAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1455911925992682898L;

    /**Revision number for this class */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       Set alternate cash currency and pickup count type in the cargo.
       Mail the letter "CountTypeNone", "CountTypeDetail", or "CountTypeSummary".
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        TillPickupCargo cargo = (TillPickupCargo)bus.getCargo();
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        RegisterIfc register = cargo.getRegister();

        int tillCountCashPickup = register.getTillCountCashPickup();
        cargo.setPickupCountType(tillCountCashPickup);

        // Using first index into Alternate Currency Types, set the nationality
        CurrencyTypeIfc[] type = DomainGateway.getAlternateCurrencyTypes();
        CurrencyIfc   currency = DomainGateway.getAlternateCurrencyInstance(type[0].getCountryCode());
        cargo.setPickupCurrency(currency);
        cargo.setTenderNationality(currency.getCountryCode());
        String nationalityDescriptor = utility.retrieveCommonText(
                                           currency.getCountryCode() + TillPickupCargo.NATIONALITY_SUFFIX,
                                           currency.getCountryCode());
        cargo.setNationalityDescriptor(nationalityDescriptor);
        
        cargo.setTenderName(DomainGateway.getFactory()
                                .getTenderTypeMapInstance()
                                .getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH));

        // set letter for the count type
        String letterName = TillLetterIfc.COUNT_TYPE_SUMMARY;
        if (tillCountCashPickup == FinancialCountIfc.COUNT_TYPE_NONE)
        {
            letterName = TillLetterIfc.COUNT_TYPE_NONE;
        }
        else if (tillCountCashPickup == FinancialCountIfc.COUNT_TYPE_DETAIL)
        {
            letterName = TillLetterIfc.COUNT_TYPE_DETAIL;
        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);

    }
}
