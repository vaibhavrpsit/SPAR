/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/TaxAmountUISite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    acadar 10/29/08 - cleaned up commented out code
 *    acadar 10/28/08 - localization for item tax reason codes
 * ===========================================================================

 * $Log:
 *  5    360Commerce 1.4         3/29/2007 6:34:32 PM   Michael Boyd    CR
 *       26172 - v8x merge to trunk
 *
 *       5    .v8x      1.3.1.0     3/11/2007 12:48:49 PM  Brett J. Larsen CR
 *       4530 -
 *       when default value is not specified, the 1st in list is selected
 *       (when no value should be preselected)
 *  4    360Commerce 1.3         1/22/2006 11:45:12 AM  Ron W. Haight   removed
 *        references to com.ibm.math.BigDecimal
 *  3    360Commerce 1.2         3/31/2005 4:30:18 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:25:45 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:14:40 PM  Robert Pearse
 * $
 * Revision 1.6  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.5  2004/03/11 23:10:27  bjosserand
 * @scr 3954 Tax Override
 *
 *  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 */

package oracle.retail.stores.pos.services.modifyitem.tax;


import java.math.BigDecimal;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel;
//--------------------------------------------------------------------------
/**
 * Site for entering tax amount data.
 * <P>
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class TaxAmountUISite extends PosSiteActionAdapter
{
    /**
     * constant for error dialog screen
     */
    public static final String TAX_OVERRIDE_NOT_ALLOWED = "TaxOverrideNotAllowed";

    //--------------------------------------------------------------------------
    /**
     * Revision Number furnished by TeamConnection.
     * <P>
     */
    //--------------------------------------------------------------------------
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

     /**
     * This method shows the UI so the user can enter an override tax amount.
     * @param bus Service Bus
    */
    public void arrive(BusIfc bus)
    {
        // get the POS UI manager
        POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        // get the cargo
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();
        cargo.setFinalFlag(false);
        // set up the ui model
        DecimalWithReasonBeanModel beanModel = new DecimalWithReasonBeanModel();
        // get reason codes and set list in bean model

        CodeListIfc codeList = utility.getReasonCodes(cargo.getOperator().getStoreID(),CodeConstantsIfc.CODE_LIST_ITEM_TAX_AMOUNT_OVERRIDE_REASON_CODES);
        cargo.setLocalizedOverrideAmountReasons(codeList);

        String selectedCodeKey = null;

        // get default amount, only if item tax by amount override is in place
        ItemTaxIfc tax = cargo.getItemTax();

        if (tax.getTaxMode() == TaxIfc.TAX_MODE_OVERRIDE_AMOUNT && tax.getTaxScope() == TaxIfc.TAX_SCOPE_ITEM)
        {
            selectedCodeKey = tax.getReason().getCode();
            beanModel.setValue(new BigDecimal(tax.getOverrideAmount().getStringValue()));
        }


        beanModel.inject(codeList, selectedCodeKey,LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));


        // Show the input screen
        uiManager.showScreen(POSUIManagerIfc.ITEM_TAX_OVERRIDE_AMOUNT, beanModel);
    }
}
