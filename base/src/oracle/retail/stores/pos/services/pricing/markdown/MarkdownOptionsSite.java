/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/markdown/MarkdownOptionsSite.java /main/11 2011/01/14 15:48:01 npoola Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         11/15/2007 11:13:08 AM Christian Greene
 *         CR28668 - backed out changes for CR25850. When determing ifdiscount
 *          allowed at CheckDiscountAllowedSite, reset lineitems in cargo to
 *         only those that are allowable.
 *    4    360Commerce 1.3         5/16/2007 3:02:12 PM   Owen D. Horne
 *         CR25850 - Fix merged from v8.0.1
 *         4    .v8x       1.2.1.0     4/19/2007 6:12:41 AM   Sujay
 *         Purkayastha For
 *         fix of CR25850
 *    3    360Commerce 1.2         3/31/2005 4:29:01 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:28 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:34 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/03/22 18:35:04  cdb
 *   @scr 3588 Corrected some javadoc
 *
 *   Revision 1.4  2004/03/17 23:03:10  dcobb
 *   @scr 3911 Feature Enhancement: Markdown
 *   Code review modifications.
 *
 *   Revision 1.3  2004/02/12 16:51:37  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:05  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Feb 03 2004 15:44:50   cdb
 * Removed "cut and paste" carryovers.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 * 
 *    Rev 1.0   Feb 03 2004 14:26:38   cdb
 * Initial revision.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.markdown;

// foundation imports
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays pricing options.
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class MarkdownOptionsSite extends PosSiteActionAdapter
{
    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    //----------------------------------------------------------------------
    /**
       Displays the pricing options
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Get the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        PricingCargo pricingCargo = (PricingCargo)bus.getCargo();
        
        SaleReturnLineItemIfc[] items = pricingCargo.getItems();
        ListBeanModel model = getListBeanModel(items);
        configureLocalButtons(model, pricingCargo);
        ui.showScreen(POSUIManagerIfc.MARKDOWN_OPTIONS, model);
    }

    //----------------------------------------------------------------------
    /**
     *   Builds the ListBeanModel; this bean contains the line item
     *   and the model that sets the local navigation buttons to their correct
     *   enabled states.
     *   @param  lineItemList     The itemlist to modify.
     *   @return the model
     */
    //----------------------------------------------------------------------
    protected ListBeanModel getListBeanModel(SaleReturnLineItemIfc[] lineItemList)
    {
        ListBeanModel model = new ListBeanModel();

        if (lineItemList != null)
        {
            model.setListModel(lineItemList);
            model.setUpdateStatusBean(false);
        }
        return model;
    }

    //----------------------------------------------------------------------
    /**
     *   Configures the local buttons
     *   @param  model ListBeanModel
     *   @param  pricingCargo PricingCargo
     */
    //----------------------------------------------------------------------
    public void configureLocalButtons(ListBeanModel model, PricingCargo pricingCargo)
    {
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();

        if (pricingCargo.getItems() == null || pricingCargo.getItems().length == 0)
            // nothing selected
        {
            navModel.setButtonEnabled(CommonActionsIfc.MARKDOWN_AMT, false);
            navModel.setButtonEnabled(CommonActionsIfc.MARKDOWN_PER, false);
        }
        else
        // single or multi select
        {
            navModel.setButtonEnabled(CommonActionsIfc.MARKDOWN_AMT, true);
            navModel.setButtonEnabled(CommonActionsIfc.MARKDOWN_PER, true);
        }

        model.setLocalButtonBeanModel(navModel);
    }
}
