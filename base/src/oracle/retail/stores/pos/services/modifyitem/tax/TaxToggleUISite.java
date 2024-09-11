/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/TaxToggleUISite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
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
 *    acadar 10/29/08 - removed the if else
 *    acadar 10/29/08 - cleaned up commented out code
 *    acadar 10/29/08 - unit test fixes
 *    acadar 10/28/08 - localization for item tax reason codes
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         3/29/2007 6:35:34 PM   Michael Boyd    CR
 *       26172 - v8x merge to trunk
 *
 *       4    .v8x      1.2.1.0     3/11/2007 2:43:11 PM   Brett J. Larsen CR
 *       4530 -
 *       changed logic to be consistent wrt selected and default value -
 *       added support for a "blank" no-default value
 *  3    360Commerce 1.2         3/31/2005 4:30:20 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:25:50 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:14:45 PM  Robert Pearse
 * $
 * Revision 1.4  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.3  2004/03/11 23:10:27  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.2  2004/03/11 22:28:39  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.1  2004/03/05 22:57:24  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.4  2004/03/05 00:41:52  bjosserand
 * @scr 3954 Tax Override
 * Revision 1.3 2004/02/12 16:51:07 mcs Forcing head revision
 *
 * Revision 1.2 2004/02/11 21:51:47 rhafernik @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1 2004/02/11 01:04:18 cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.0 Aug 29 2003 16:01:58 CSchellenger Initial revision.
 *
 * Rev 1.1 Feb 14 2003 15:00:02 HDyer Fixed deprecation warning by calling new method to set the reason code.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 * Rev 1.0 Apr 29 2002 15:18:00 msg Initial revision.
 *
 * Rev 1.0 Mar 18 2002 11:38:00 msg Initial revision.
 *
 * Rev 1.0 Sep 21 2001 11:29:40 msg Initial revision.
 *
 * Rev 1.1 Sep 17 2001 13:09:24 msg header update * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 */

package oracle.retail.stores.pos.services.modifyitem.tax;

//java imports
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
import oracle.retail.stores.pos.ui.beans.BooleanWithReasonBeanModel;

//--------------------------------------------------------------------------
/**
 * Site class for toggling tax.
 * <P>
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class TaxToggleUISite extends PosSiteActionAdapter
{

    //--------------------------------------------------------------------------
    /**
     * Revision Number furnished by TeamConnection.
     * <P>
     */
    //--------------------------------------------------------------------------
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * This method shows the UI so the user can toggle the tax.
     * @param bus Service Bus
    */
    public void arrive(BusIfc bus)
    {
        // get the POS UI manager
        POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        // get the cargo
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        // set up the ui model
        BooleanWithReasonBeanModel beanModel = new BooleanWithReasonBeanModel();
        // get reason codes and set list in bean model
        CodeListIfc rcl = utility.getReasonCodes(cargo.getOperator().getStoreID(),CodeConstantsIfc.CODE_LIST_ON_OFF_REASON_CODES);
        cargo.setLocalizedToggleReasons(rcl);

        // get reason code only if toggle in place
        ItemTaxIfc tax = cargo.getItemTax();
        String code = tax.getReason().getCode();
        if (tax.getTaxMode() == TaxIfc.TAX_MODE_TOGGLE_OFF && tax.getTaxScope() == TaxIfc.TAX_SCOPE_ITEM)
        {
            beanModel.setValue(false);
        }
        else
        {
            beanModel.setValue(true);
        }

        beanModel.inject(rcl,code,LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));

        // Show the input screen
        uiManager.showScreen(POSUIManagerIfc.ITEM_TAX_ON_OFF, beanModel);

    }
}
