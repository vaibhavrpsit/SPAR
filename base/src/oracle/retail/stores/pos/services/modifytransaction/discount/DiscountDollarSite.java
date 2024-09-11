/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/discount/DiscountDollarSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:32 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    acadar 10/30/08 - localization of reason codes for manual transaction
 *                      discounts
 * ===========================================================================
     $Log:
      5    360Commerce 1.4         3/29/2007 6:40:17 PM   Michael Boyd    CR
           26172 - v8x merge to trunk

           5    .v8x      1.3.1.0     3/8/2007 4:16:51 PM    Brett J. Larsen
           CR 4530
           - no value should be displayed when no default value is defined
           for a code list
      4    360Commerce 1.3         1/22/2006 11:45:13 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:27:45 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:20:58 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:10:36 PM  Robert Pearse
     $
     Revision 1.3.4.1  2004/11/11 23:09:53  cdb
     @scr 7693 Updated to make app more robust DB is missing required data.

     Revision 1.4  2004/11/11 22:48:42  cdb
     @scr 7693 Removed crash potential for bad DB data - unable to retrieve reason codes.

     Revision 1.3  2004/02/12 16:51:10  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:48  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.3   Jan 06 2004 11:02:16   cdb
 * Enhanced configurability. When non-editable combo boxes are used, a default value is set if a previously existing reason code hasn't been selected.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.2   Jan 05 2004 13:18:10   cdb
 * Modified to use editable combo box for reason code entry.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.1   Oct 17 2003 10:51:40   bwf
 * Add reason code keys.
 * Resolution for 3412: Feature Enhancement: Employee Discount
 *
 *    Rev 1.0   Aug 29 2003 16:02:22   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Mar 05 2003 13:11:18   HDyer
 * Fixed deprecation warnings.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.0   Apr 29 2002 15:16:24   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:38:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 05 2002 16:42:52   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:30:38   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:36   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.modifytransaction.discount;



import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountStrategy;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;

import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;
import java.math.BigDecimal;

//--------------------------------------------------------------------------
/**
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class DiscountDollarSite extends PosSiteActionAdapter
{

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       The arrive code shows the UI for the Discount Dollar.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
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

        // get the discount amount object
        TransactionDiscountByAmountStrategy amountDiscount =
            (TransactionDiscountByAmountStrategy) cargo.getDiscount();
        // a list of all the reasons
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        // set up reason code list
        CodeListIfc reasons = utility.getReasonCodes(cargo.getOperator().getStoreID(), CodeConstantsIfc.CODE_LIST_TRANSACTION_DISCOUNT_BY_AMOUNT);
        cargo.setLocalizedDiscountAmountReasonCodes(reasons);

        String defaultEntry = null;

        // check to see if there is an  amount discount on the transaction
        if(amountDiscount != null)
        {
            // if so set the current discount values as default on the model
            model.setValue(new BigDecimal(amountDiscount.getDiscountAmount().getStringValue()));
            defaultEntry  = amountDiscount.getReason().getCode();
        }

        model.inject(reasons, defaultEntry, LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));

        // show the screen
        uiManager.showScreen(POSUIManagerIfc.TRANS_DISC_AMT, model);

    }

}
