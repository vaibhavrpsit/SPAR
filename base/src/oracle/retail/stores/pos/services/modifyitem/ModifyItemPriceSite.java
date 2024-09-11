/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ModifyItemPriceSite.java /main/13 2011/02/16 09:13:33 cgreene Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 02/15/11 - in
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    acadar 10/28/08 - localization for item tax reason codes
 *    acadar 10/27/08 - use localized price override reason codes
 *    acadar 10/25/08 - localization of price override reason codes
 *
 * ===========================================================================
     $Log:
      5    360Commerce 1.4         4/25/2007 8:52:23 AM   Anda D. Cadar   I18N
           merge

      4    360Commerce 1.3         1/22/2006 11:45:12 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse
     $
     Revision 1.3  2004/02/12 16:51:03  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:39:28  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:01:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Mar 27 2003 09:03:34   KLL
 * Fixed deprecation warning
 * Resolution for POS SCR-2103: Remove uses of deprecated items in POS.
 *
 *    Rev 1.2   Feb 24 2003 13:46:46   HDyer
 * Fixed deprecation warning.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.1   02 May 2002 17:27:52   jbp
 * deprecated - now in itemDiscount service
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Apr 29 2002 15:17:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:37:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 05 2002 16:42:44   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:29:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:06   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.modifyitem;

//java imports
import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
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
 *   This shows the Modify Item Price screen.<P>
 *   @version $Revision: /main/13 $
 *   @deprecated as of release 5.5.0  use itemdiscount service
 */
//--------------------------------------------------------------------------

public class ModifyItemPriceSite extends PosSiteActionAdapter
{

    /**
     *   Revision Number furnished by TeamConnection. <P>
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * shows the Modify Item Price screen.
     *
     * @param bus
     */
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        // get price from cargo
        ItemCargo ic = (ItemCargo) bus.getCargo();
        // get old price, old reason code from item
        CurrencyIfc oldPrice = ic.getItem().getItemPrice().getSellingPrice();
        String oldReasonCode = ic.getItem().getItemPrice().getItemPriceOverrideReason().getCode();
        // set value in bean model
        DecimalWithReasonBeanModel beanModel = new DecimalWithReasonBeanModel();

        // display the old value on the screen
        beanModel.setValue(new BigDecimal(oldPrice.getStringValue()));
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        // set up reason code list
        CodeListIfc rcl = utility.getReasonCodes(ic.getOperator().getStoreID(), CodeConstantsIfc.CODE_LIST_PRICE_OVERRIDE_REASON_CODES);
        ic.setLocalizedPriceOverrideReasons(rcl);
        String selectedReasonCode = null ;
        // if doesn't exist
        if (!oldReasonCode.equals(CodeConstantsIfc.CODE_UNDEFINED))
        {

            selectedReasonCode = oldReasonCode;
        }

        beanModel.inject(rcl, selectedReasonCode, LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));


        // set up the ui model
        ui.showScreen(POSUIManagerIfc.PRICE_OVERRIDE, beanModel);
    }
}