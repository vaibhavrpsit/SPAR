/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/delete/DisplayLayawayRefundSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    acadar    04/13/09 - cleanup
 *    acadar    04/13/09 - make layaway location required; refactor the way we
 *                         handle layaway reason codes
 *    cgreene   03/30/09 - implement printing of layaway location on receipt by
 *                         adding new location code to layaway object and
 *                         deprecating the old string
 *    abondala  02/27/09 - LayawayLocation and OrderLocation parameters are
 *                         changed to ReasonCodes.
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/25/2007 8:52:24 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    5    360Commerce 1.4         5/4/2006 5:11:50 PM    Brendan W. Farrell
 *         Remove inventory.
 *    4    360Commerce 1.3         4/27/2006 7:07:07 PM   Brett J. Larsen CR
 *         17307 - inventory functionality removal - stage 2
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:04 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:39 PM  Robert Pearse
 *
 *   Revision 1.5.2.1  2004/10/15 18:50:30  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.7  2004/10/12 16:38:51  mweis
 *   @scr 7012 Make common getters/setters for Inventory methods in preparation for Sale, Layaway, and Order sharing code.
 *
 *   Revision 1.6  2004/10/11 21:35:06  mweis
 *   @scr 7012 Begin consolidating inventory location loading for Layaways and Orders.
 *
 *   Revision 1.5  2004/09/17 19:36:11  mweis
 *   @scr 7012 Correctly account for inventory for the Layaway and its items when the Layaway is deleted/canceled.
 *
 *   Revision 1.4  2004/06/29 22:03:31  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.3.2.1  2004/06/20 20:22:38  aachinfiev
 *   Changed to use Layaway Utilities to load inventory locations
 *
 *   Revision 1.3  2004/02/12 16:50:48  mcs
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
 *    Rev 1.0   Aug 29 2003 16:00:28   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Mar 19 2003 12:17:14   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.1   Aug 29 2002 14:18:34   jriggins
 * Replaced concat of customer name in favor of formatting the text from the CustomerAddressSpec.CustomerName bundle in customerText.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:20:58   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:34:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 09 2002 18:36:40   mpm
 * Externalized text.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:21:04   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.delete;

//foundation imports
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.PaymentDetailBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the layaway refund detail screen.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class DisplayLayawayRefundSite extends PosSiteActionAdapter
{
    /**
        class name constant
    **/
    public static final String SITENAME = "DisplayLayawayRefundSite";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
     /**
       Customer name bundle tag
     **/
     protected static final String CUSTOMER_NAME_TAG = "CustomerName";
     /**
       Customer name default text
     **/
     protected static final String CUSTOMER_NAME_TEXT = "{0} {1}";

    //--------------------------------------------------------------------------
    /**
       Displays the Layaway Refund Detail screen.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui =
                        (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        LayawayDeleteCargo layawayCargo = (LayawayDeleteCargo)bus.getCargo();
        PaymentDetailBeanModel model = new PaymentDetailBeanModel();
        LayawayIfc layaway = layawayCargo.getLayaway();

        CustomerIfc customer = layaway.getCustomer();

        // Create the customer name string from the bundle.
        UtilityManagerIfc utility =
          (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        Object parms[] = { customer.getFirstName(), customer.getLastName() };
        String pattern =
          utility.retrieveText("CustomerAddressSpec",
                               BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                               CUSTOMER_NAME_TAG,
                               CUSTOMER_NAME_TEXT);
        String customerName =
          LocaleUtilities.formatComplexMessage(pattern, parms);

        // Set the payment detail model with layaway data
        model.setDeleteLayawayFlag(true);
        model.setNewLayawayFlag(false);
        model.setPickupLayawayFlag(false);
        model.setLayawayNumber(layaway.getLayawayID());
        model.setCustomerName(customerName);
        model.setExpirationDate(layaway.getExpirationDate());
        model.setBalanceDue(layaway.getBalanceDue());
        model.setAmountPaid(layaway.getTotalAmountPaid());
        model.setLayawayFee(layaway.getCreationFee());
        model.setLayawayStatus(layaway.getStatus());

        String storeId = layawayCargo.getOperator().getStoreID();
        CodeListIfc layawayLocationsList =  utility.getReasonCodes(storeId, CodeConstantsIfc.CODE_LIST_LAYAWAY_LOCATION_REASON_CODES);
        model.inject(layawayLocationsList, layaway.getLocationCode().getCode(), LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));

        model.setLayawayNumber(layaway.getLayawayID());

        // set deletion fee so that customer will not have to pay
        // more to delete this layaway
        if (layaway.getTotalAmountPaid().subtract(
            layaway.getCreationFee()).subtract(
            layaway.getDeletionFee()).compareTo(
            DomainGateway.getBaseCurrencyInstance()) == CurrencyIfc.LESS_THAN)
        {
            // set so balance of transaction is zero
            layaway.setDeletionFee(
                layaway.getTotalAmountPaid().subtract(
                layaway.getCreationFee()));
        }
        model.setDeletionFee(layaway.getDeletionFee());

        // set the customer's name in the status area
        StatusBeanModel statusModel = new StatusBeanModel();

        statusModel.setCustomerName(customerName);

        model.setStatusBeanModel(statusModel);

        ui.showScreen(POSUIManagerIfc.REFUND_DETAIL, model);
    }

}
