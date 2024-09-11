/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/EnterPriceCodeSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:28:02 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:21:26 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse   
 * $
 * Revision 1.2  2004/03/26 06:16:41  baa
 * @scr 3561 update header
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

// foundation imports
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.GiftReceiptLookupBeanModel;

//--------------------------------------------------------------------------
/**
 * This site will show the return item screen which allows the user to enter an
 * item to be returned.
 * <p>
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class EnterPriceCodeSite extends PosSiteActionAdapter
{

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
     * Show the UI screen for the enter item.
     * <P>
     * 
     * @param bus Service Bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // get the UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ReturnItemCargo cargo = (ReturnItemCargo) bus.getCargo();

        GiftReceiptLookupBeanModel beanModel = new GiftReceiptLookupBeanModel();
        PLUItemIfc item = cargo.getPLUItem();
        if (item != null)
        {
          beanModel.setItemNumber(item.getItemID());
          beanModel.setDescription(item.getDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
        }
        ui.showScreen(POSUIManagerIfc.GIFT_RECEIPT_ITEM, beanModel);

    }

}
