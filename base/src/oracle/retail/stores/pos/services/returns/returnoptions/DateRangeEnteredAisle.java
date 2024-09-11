/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/DateRangeEnteredAisle.java /main/15 2011/12/05 12:16:21 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    blarsen   07/09/09 - XbranchMerge
 *                         blarsen_bug8629786-transaction-lookup-by-card-number-fails
 *                         from rgbustores_13.1x_branch
 *    blarsen   07/07/09 - Removing previous change that includes store number
 *                         in search criteria. This limits the scope in cases
 *                         where it should not. CO now uses default hierarchy
 *                         when no store criteria us specified.
 *    blarsen   07/01/09 - XbranchMerge
 *                         blarsen_bug8629786-customer-transaction-history-retrieval-fails-on-was
 *                         from main
 *    blarsen   06/29/09 - Adding the current store id to the search criteria.
 *                         Some early assumtions in this feature were not
 *                         valid. The assumption that the creditionals sent to
 *                         CO is associated with a store hierarchy is not valid
 *                         for WAS. In this case no creditials are sent. CO was
 *                         modified to *not* filter on logged in user. In some
 *                         cases a store criteria is required when a users
 *                         hierarchy is not available. Requirements at this
 *                         time are to only lookup current stores transactions.
 *    mkochumm  02/12/09 - use default locale for dates
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:29 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.4  2004/02/23 14:58:52  baa
 *   @scr 0 cleanup javadocs
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 15 2002 13:30:50   jriggins
 * Replaced deprecated EYSDate.toFormattedString() calls in favor of calls which use the locale.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:04:48   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:25:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import java.util.Locale;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DateSearchBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    @version $Revision: /main/15 $
**/
//------------------------------------------------------------------------------

public class DateRangeEnteredAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4607476262549955074L;


    //--------------------------------------------------------------------------
    /**     Gets the store number from UI
            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {


        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ReturnOptionsCargo cargo = (ReturnOptionsCargo)bus.getCargo();
        DateSearchBeanModel model = null;
        EYSDate startDate = null;
        EYSDate endDate = null;
        Letter letter = null;

        model = (DateSearchBeanModel)ui.getModel(POSUIManagerIfc.PURCHASE_DATE);
        startDate = model.getStartDate();
        endDate = model.getEndDate();

        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        String startDateString = startDate.toFormattedString(locale);
        String endDateString = endDate.toFormattedString(locale);
        String[] errorString = new String[2];

        // Check first to see if the dates are in ascending order
        if(startDate.after(endDate))
        {
            startDate.setMonth(-1);
            errorString[0] = startDateString;
            errorString[1] = endDateString;

            // put up error dialog screen
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("INVALID_DATE_RANGE");
            dialogModel.setArgs(errorString);
            dialogModel.setType(DialogScreensIfc.ERROR);

            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else if ((!startDate.isValid()) || (!endDate.isValid())) // valid date
        {
            errorString[0] = startDateString;
            errorString[1] = endDateString;

            // put up error dialog screen
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("InvalidBusinessDate");
            dialogModel.setArgs(errorString);
            dialogModel.setType(DialogScreensIfc.ERROR);

            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            // Create a new object search criteria and set store number and date range
            SearchCriteriaIfc searchCriteria =
                DomainGateway.getFactory().getSearchCriteriaInstance();
            EYSDate[] dateRange= new EYSDate[2];
            dateRange[0] = startDate;
            dateRange[1] = endDate;
            searchCriteria.setDateRange(dateRange);
            searchCriteria.setStoreNumber(null);

            // set the search criteria in the cargo
            cargo.setSearchCriteria(searchCriteria);

            letter = new Letter(CommonLetterIfc.LINK);

            SaleReturnTransactionIfc transaction = cargo.getTransaction();
            if (transaction != null)
            {
                CustomerIfc customer = transaction.getCustomer();
                if (customer != null)
                {
                    searchCriteria.setCustomer(customer);
                    letter = new Letter(CommonLetterIfc.CUSTOMER);
                }
            }
        }

        if (letter != null)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }

    }
}
