/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpickup/TenderAlternativeZeroChecksSelectedAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:18 mszekely Exp $
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
 *   Revision 1.7  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.6  2004/07/09 23:27:01  dcobb
 *   @scr 5190 Crash on Pickup Canadian Checks
 *   @scr 6101  Pickup of local cash gives "Invalid Pickup" of checks error
 *   Backed out awilliam 5109 changes and fixed crash on pickup of Canadian checks.
 *
 *   Revision 1.5  2004/04/29 20:06:21  dcobb
 *   @scr 4098 Open Drawer before detail count screens.
 *   Pickup changed to open drawer before detail count screens.
 *
 *   Revision 1.4  2004/02/12 16:50:05  mcs
 *   Forcing head revision
 *
 *   Revision 1.3  2004/02/11 22:43:18  rhafernik
 *   @scr  0 fixed warning
 *
 *   Revision 1.2  2004/02/11 21:47:43  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Feb 04 2004 18:58:56   DCobb
 * Removed conversion from int to String back to int.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 * 
 *    Rev 1.0   Aug 29 2003 15:58:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jun 24 2003 10:33:16   DCobb
 * Removed debug print line.
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 *
 *    Rev 1.0   Jun 23 2003 13:04:56   DCobb
 * Initial revision.
 * Resolution for POS SCR-2484: Canadian Check Till Pickup
 *
 *    Rev 1.0   Apr 29 2002 15:26:18   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:30:38   msg
 * Initial revision.
 *
 *    Rev 1.2   02 Mar 2002 12:48:02   pdd
 * Converted to use TenderTypeMapIfc.
 * Resolution for POS SCR-627: Make the Tender type list extendible.
 *
 *    Rev 1.1   29 Oct 2001 16:15:02   epd
 * Updated files to remove reference to Till related parameters.  This information, formerly contained in parameters, now resides as register settings obtained from the RegisterIfc class.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:19:56   msg
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
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//------------------------------------------------------------------------------
/**
    Set the currency to alternate check. Mail a letter indicating the pickup
    check count type.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class TenderAlternativeZeroChecksSelectedAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -6378439141427947748L;

    /** Revision number for this class */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       Set the alternate check currency and pickup count type in the cargo.
       Mail the letter "CountTypeDetail" as checks are always counted.
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        TillPickupCargo cargo = (TillPickupCargo)bus.getCargo();
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        RegisterIfc register = cargo.getRegister();

        int tillCountCheckPickup = register.getTillCountCheckPickup();
        cargo.setPickupCountType(tillCountCheckPickup);

        // Using first index into Alternate Currency Types, set the nationality
        CurrencyTypeIfc[] type = DomainGateway.getAlternateCurrencyTypes();
        CurrencyIfc   currency = DomainGateway.getAlternateCurrencyInstance(type[0].getCountryCode());
        cargo.setPickupCurrency(currency);
        cargo.setTenderNationality(currency.getCountryCode());
        String nationalityDescriptor = utility.retrieveText(POSUIManagerIfc.TILL_FUNCTIONS,
                                   BundleConstantsIfc.COMMON_BUNDLE_NAME,
                                   currency.getCountryCode() + TillPickupCargo.NATIONALITY_SUFFIX,
                                   currency.getCountryCode());
        cargo.setNationalityDescriptor(nationalityDescriptor);
        
        cargo.setTenderName(DomainGateway.getFactory()
                                   .getTenderTypeMapInstance()
                                        .getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK));

        // set letter for the count type - checks are always counted
        String letterName = TillLetterIfc.COUNT_TYPE_DETAIL;

        bus.mail(new Letter(letterName), BusIfc.CURRENT);

    }
}
