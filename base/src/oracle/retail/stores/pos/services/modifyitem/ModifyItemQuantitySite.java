/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ModifyItemQuantitySite.java /main/14 2011/12/05 12:16:20 cgreene Exp $
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
 *    sgu       02/17/09 - use formatNumber and parseNumber
 *    sgu       02/16/09 - reponse text must be localized
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse
 *
 *   Revision 1.4  2004/07/22 00:06:33  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.3  2004/02/12 16:51:03  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:01:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jul 08 2003 11:42:48   bwf
 * Fixed internationalization problem so that item quantity screen works correctly.
 * Resolution for 3052: Modify Item- Item Quantity- Item that is Unit of measure Item Max should be 6
 *
 *    Rev 1.2   Mar 05 2003 10:41:30   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 09 2002 09:34:16   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:17:34   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:37:26   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:29:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

// foundation imports
import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
 *   This site displays the ITEM_QUANTITY screen.
 *   <p>
 *   @version $Revision: /main/14 $
 */
//--------------------------------------------------------------------------
public class ModifyItemQuantitySite extends PosSiteActionAdapter
{
    //--------------------------------------------------------------------------
    /**
    *   Revision Number furnished by TeamConnection. <P>
    */
    //--------------------------------------------------------------------------
    public static final String revisionNumber = "$Revision: /main/14 $";
    /**
        Unit id tag
    **/
    public static final String UNITID_TAG = "UnitId";
    /**
        Unit id text
    **/
    public static final String UNITID_TEXT = "UN";
    //----------------------------------------------------------------------
    /**
     *   Displays the ITEM_QUANTITY screen.
     *   <P>
     *   @param  bus Service Bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        String screenID = POSUIManagerIfc.ITEM_QUANTITY;
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ItemCargo cargo = (ItemCargo)bus.getCargo();
        SaleReturnLineItemIfc lineItem = cargo.getItem();
        UnitOfMeasureIfc uom = lineItem.getPLUItem().getUnitOfMeasure();

        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        PromptAndResponseModel beanModel = new PromptAndResponseModel();
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String unitId = UNITID_TEXT;
        if ((uom == null) || (unitId.equals(uom.getUnitID())))
        {
            beanModel.setResponseText(Integer.toString(lineItem.getItemQuantityDecimal().intValue()));
        }
        else
        {
            beanModel.setArguments(uom.getName(locale));
            String responseText = LocaleUtilities.formatNumber(lineItem.getItemQuantityDecimal(),LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
            beanModel.setResponseText(responseText);
            screenID = POSUIManagerIfc.ITEM_QUANTITY_UOM;
        }

        baseModel.setPromptAndResponseModel(beanModel);
        ui.showScreen(screenID, baseModel);
    }
}
