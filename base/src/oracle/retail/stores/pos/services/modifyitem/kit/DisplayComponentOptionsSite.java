/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/kit/DisplayComponentOptionsSite.java /main/12 2011/01/14 15:48:00 npoola Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   01/07/11 - XbranchMerge
 *                         blarsen_bug10624300-discount-flag-change-side-effects
 *                         from rgbustores_13.3x_generic_branch
 *    blarsen   01/06/11 - Changed discount eligibility check to use
 *                         DiscountUtility helper method. SRLI was changed to
 *                         return the simple item eligibility flag.
 *                         DiscountUtility includes additional checks (e.g.
 *                         external pricing).
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    jswan     05/20/09 - Moved ButtonAction constants to this class from
 *                         ModifyItemSite.
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/25/2008 4:33:49 AM   Sujay Beesnalli
 *         Forward ported from v12x. If VAT is enabled, Tax button is disabled
 *          in item components screen.
 *    3    360Commerce 1.2         3/31/2005 4:27:47 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/05/11 22:43:47  dcobb
 *   @scr 4922 Update service to work with Tax Override multi-item select.
 *
 *   Revision 1.3  2004/02/12 16:51:05  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:01:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:18:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:37:50   msg
 * Initial revision.
 * 
 *    Rev 1.5   21 Jan 2002 14:26:12   pjf
 * Correct defect introduced by UI overhaul.
 * Resolution for POS SCR-808: Kit Components don't display on Component Options screen.
 *
 *    Rev 1.4   02 Dec 2001 13:04:36   pjf
 * Disable discount buttons if a kit component is not eligible.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.3   06 Nov 2001 13:57:40   pjf
 * Re-enabled price override button.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.2   30 Oct 2001 12:43:08   pjf
 * UI cleanup for kits.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.1   26 Oct 2001 13:29:50   pjf
 * Disable Inquiry and Price Override on component options screen until kit feature is complete.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.0   26 Oct 2001 10:04:48   pjf
 * Initial revision.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.0   Sep 21 2001 11:28:58   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.kit;

// foundation imports
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.services.modifyitem.ModifyItemSite;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays the kit component options screen for the Modify Item service.
    <p>
    @version $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
public class DisplayComponentOptionsSite extends ModifyItemSite
{
    //--------------------------------------------------------------------------
    /**
     *   Revision Number furnished by TeamConnection. <P>
     */
    //--------------------------------------------------------------------------
    public static final String revisionNumber = "$Revision: /main/12 $";


    //----------------------------------------------------------------------
    /**
     *   Displays the COMPONENT_OPTIONS screen.
     *   <P>
     *   @param  bus     Service bus.
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ItemCargo cargo = (ItemCargo)bus.getCargo();

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Initialize a model with the kit component line items
        LineItemsModel          model       = new LineItemsModel();
        SaleReturnLineItemIfc[] components  = cargo.getKitHeader().getKitComponentLineItemArray();
        model.setLineItems(components);
        model.setLocalButtonBeanModel(getNavigationButtonBeanModel(components));

        ui.showScreen(POSUIManagerIfc.COMPONENT_OPTIONS, model);
    }

    //----------------------------------------------------------------------
    /**
     *   Retrieves the selected item from the component options screen.
     *   <P>
     *   @param  bus     Service bus.
     */
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        ItemCargo       cargo       = (ItemCargo)bus.getCargo();
        POSUIManagerIfc ui          = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        LineItemsModel  model       = (LineItemsModel)ui.getModel(POSUIManagerIfc.COMPONENT_OPTIONS);

        cargo.setItem((KitComponentLineItemIfc)model.getSelectedValue());
        
        // set Items array for Tax Override multi-item select
        SaleReturnLineItemIfc[] items = new SaleReturnLineItemIfc[1];
        items[0] = (KitComponentLineItemIfc)model.getSelectedValue();
        cargo.setItems(items);
    }

    //----------------------------------------------------------------------
    /**
     *   Builds the NavigationButtonBeanModel; this method sets the local
     *   navigation buttons to their correct enabled states.
     *
     *   @param  lineItemList     The itemlist used to determine button states.
     *   @return NavigationButtonBeanModel
     */
    //----------------------------------------------------------------------
    protected NavigationButtonBeanModel getNavigationButtonBeanModel(SaleReturnLineItemIfc[] lineItemList)
    {
        NavigationButtonBeanModel nbbModel = new NavigationButtonBeanModel();

        //if any component item is eligible for discounts,
        //all components are, so enable the discount buttons
        if (DiscountUtility.isDiscountEligible(lineItemList[0]))
        {
            nbbModel.setButtonEnabled(CommonActionsIfc.DISCOUNT_AMOUNT, true);
            nbbModel.setButtonEnabled(CommonActionsIfc.DISCOUNT_PERCENT, true);
        }
        else
        {
            nbbModel.setButtonEnabled(CommonActionsIfc.DISCOUNT_AMOUNT, false);
            nbbModel.setButtonEnabled(CommonActionsIfc.DISCOUNT_PERCENT, false);
        }
        
        // If InclusiveTaxEnabled is true, then disable the "TAX" buttons for VAT.
        boolean taxInclusiveFlag = Gateway.getBooleanProperty("application", "InclusiveTaxEnabled", false);
        if(taxInclusiveFlag)
        {
            nbbModel.setButtonEnabled(ACTION_TAX, false);
        }
        return nbbModel;
    }
}
