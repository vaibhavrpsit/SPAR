/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/GetEnteredSummaryAmountAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:23 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    ranojha   02/18/09 - Fixed NullPointerException cases
 *    sgu       01/14/09 - use decimal format to set string value of a currency
 *                         object
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         5/8/2007 11:32:25 AM   Anda D. Cadar
 *         currency changes for I18N
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:49 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:10 PM  Robert Pearse
 *
 *   Revision 1.5  2004/06/07 18:29:38  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Add foreign currency counts.
 *
 *   Revision 1.4  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
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
 *    Rev 1.1   Feb 04 2004 18:39:10   DCobb
 * Added SUMMARY_COUNT_PICKUP and SUMMARY_COUNT_LOAN screens.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 *
 *    Rev 1.0   Aug 29 2003 15:56:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.9   Jul 21 2003 18:28:06   sfl
 * Backed up the database reading code.
 * Resolution for POS SCR-3168: Server Offline- Unable to Pickup Canadian Cash.
 *
 *    Rev 1.8   Jul 10 2003 11:54:48   RSachdeva
 * Reconciliation Confirmation Notice
 * Resolution for POS SCR-3067: Canadian Tenders appears with <> on Reconciliation Confirmation Notice during Till Reconcile
 *
 *    Rev 1.7   Jul 09 2003 17:20:24   sfl
 * Before compare the input till pickup amount with the till total amount, need to update the till total amount by re-reading the data from the database because the latest transaction may not be included in the till total amount that was read into memory at  main service before POS login.
 * Resolution for POS SCR-3046: Till PIckup Canadian Cash - Invalid Amount Error- Should be able to pickup cash
 *
 *    Rev 1.6   May 01 2003 10:23:34   RSachdeva
 * Removing toLowerCase(locale)
 * Resolution for POS SCR-2215: Internationlaztion- Till Functions -Pickup- Summar Count Screens
 *
 *    Rev 1.5   Mar 04 2003 11:55:38   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.4   Nov 27 2002 15:55:54   DCobb
 * Add Canadian Check tender.
 * Resolution for POS SCR-1842: POS 6.0 Canadian Check Tender
 *
 *    Rev 1.3   Nov 04 2002 13:06:34   DCobb
 * Add Mall Gift Certificate.
 * Resolution for POS SCR-1821: POS 6.0 Mall Gift Certificates
 *
 *    Rev 1.2   Sep 10 2002 17:49:18   baa
 * add password field
 * Resolution for POS SCR-1810: Adding pasword validating fields
 *
 *    Rev 1.1   Aug 19 2002 14:43:22   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:30:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:14:30   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:27:12   msg
 * Initial revision.
 *
 *    Rev 1.1   12 Dec 2001 13:02:46   epd
 * Added code to allow for counting Store Safe
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:17:00   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    Stores the amount entered.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GetEnteredSummaryAmountAisle extends PosLaneActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = 645223320105101536L;
    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Gets the Summary Amount and saves it in the cargo.
       <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();
    	String currencyText = null;
        // Get the tender amount from the UI
        // need to get the currency value directly from the field
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // This use of toString is safe here. We need to convert string into decimal format
        Number inputNumber = LocaleUtilities.parseCurrency(ui.getInput().trim(), LocaleMap.getLocale(LocaleMap.DEFAULT));
        if (inputNumber != null)
        {
        	currencyText = inputNumber.toString();
        }
        cargo.setCurrentAmountStr(currencyText);

        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }

}
