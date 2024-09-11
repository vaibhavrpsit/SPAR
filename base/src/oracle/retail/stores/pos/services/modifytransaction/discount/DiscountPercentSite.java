/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/discount/DiscountPercentSite.java /main/14 2011/02/16 09:13:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    mweis  02/09/10 - instant credit discount never shows up on the user
 *                      interface
 *    abonda 01/03/10 - update header date
 *    acadar 10/30/08 - localization of reason codes for manual transaction
 *                      discounts
 * ===========================================================================
     $Log:
      5    360Commerce 1.4         3/29/2007 6:40:48 PM   Michael Boyd    CR
           26172 - v8x merge to trunk

           5    .v8x      1.3.1.0     3/8/2007 4:16:52 PM    Brett J. Larsen
           CR 4530
           - no value should be displayed when no default value is defined
           for a code list
      4    360Commerce 1.3         1/22/2006 11:45:13 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:27:46 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:20:59 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:10:36 PM  Robert Pearse
     $
     Revision 1.4.4.1  2004/11/11 23:09:53  cdb
     @scr 7693 Updated to make app more robust DB is missing required data.

     Revision 1.5  2004/11/11 22:48:42  cdb
     @scr 7693 Removed crash potential for bad DB data - unable to retrieve reason codes.

     Revision 1.4  2004/03/22 19:27:00  cdb
     @scr 3588 Updating javadoc comments

     Revision 1.3  2004/02/12 16:51:10  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:48  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.7   Jan 06 2004 11:01:56   cdb
 * Enhanced configurability. When non-editable combo boxes are used, a default value is set if a previously existing reason code hasn't been selected.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.6   Jan 02 2004 11:59:46   cdb
 * Added logging of valid reason code IDs.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.5   Dec 30 2003 20:26:02   cdb
 * Modified to NOT set a default reason code.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.4   Nov 21 2003 16:26:26   nrao
 * Added constant and modified logger message.
 *
 *    Rev 1.3   Nov 07 2003 10:55:34   nrao
 * Fixed Discount Percent not appearing when retrieved from parameter for Instant Credit.
 *
 *    Rev 1.2   Oct 31 2003 12:30:12   nrao
 * Added parameter to retrieve default discount amount for Instant Credit Enrollment.
 *
 *    Rev 1.1   Oct 17 2003 10:51:42   bwf
 * Add reason code keys.
 * Resolution for 3412: Feature Enhancement: Employee Discount
 *
 *    Rev 1.0   Aug 29 2003 16:02:24   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Mar 05 2003 13:11:10   HDyer
 * Fixed deprecation warnings.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.0   Apr 29 2002 15:16:28   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:38:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:30:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:36   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.modifytransaction.discount;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageStrategy;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;

import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;
import java.math.BigDecimal;

/**
 * This site will show the UI for the percentage discount.
 * 
 * @version $Revision: /main/14 $
 */
public class DiscountPercentSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -7821363668032359081L;
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * The arrive code will show the UI for the percentage discount
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {

        // model to use for the UI
        DecimalWithReasonBeanModel model = new DecimalWithReasonBeanModel();


        // retrieve cargo
        ModifyTransactionDiscountCargo cargo =
            (ModifyTransactionDiscountCargo) bus.getCargo();

        // get the POS UI manager
        POSUIManagerIfc uiManager =
            (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // get the discount percent object
        TransactionDiscountByPercentageStrategy percentDiscount =
            (TransactionDiscountByPercentageStrategy)cargo.getDiscount();

        // set the reason codes in the model
        // a list of all the reasons

        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        // set up reason code list
        CodeListIfc reasons = utility.getReasonCodes(cargo.getOperator().getStoreID(), CodeConstantsIfc.CODE_LIST_TRANSACTION_DISCOUNT_BY_PERCENTAGE);
        cargo.setLocalizedDiscountPercentReasonCodes(reasons);

        String selectedEntry = null;

        // check to see if there is a percenet discount on the transaction
        if(percentDiscount != null)
        {
            // if so set the current discount values as default on the model
            model.setValue(percentDiscount.getDiscountRate());
             // retrieve the reason code
            selectedEntry = percentDiscount.getReason().getCode();
        }

        // if Instant Credit, then get parameter & calculate percentage.
        if (cargo.isInstantCreditDiscount())
        {
            getPercent(bus, model);
        }

        // set in the model
       model.inject(reasons, selectedEntry, LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));

        // show the screen
        uiManager.showScreen(POSUIManagerIfc.TRANS_DISC_PCNT, model);

    }

    /**
     * This method will get the parameter and calculate discount percentage
     * 
     * @param bus Service Bus
     * @param model Model in which percent is displayed.
     */
    private void getPercent(BusIfc bus, DecimalWithReasonBeanModel model)
    {
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        try
        {
            // parameter range: 100% - 0%
            double per = pm.getIntegerValue(ParameterConstantsIfc.HOUSEACCOUNT_DefaultInstantCreditDiscount).doubleValue();
            // convert the value obtained to a rate
            per = per/100.0;
            BigDecimal value = new BigDecimal(per);
            value = value.setScale(2, BigDecimal.ROUND_HALF_UP);  // range: 1.00 - 0.00
            model.setValue(value);
        }
        catch(ParameterException pe)
        {
            logger.warn( pe.getStackTraceAsString());
        }
    }
}
