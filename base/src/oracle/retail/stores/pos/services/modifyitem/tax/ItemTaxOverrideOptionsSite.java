/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/ItemTaxOverrideOptionsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:24 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:28:35 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:22:33 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:11:42 PM  Robert Pearse   
 * $
 * Revision 1.5  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
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
 * Rev 1.0 Aug 29 2003 16:02:02 CSchellenger Initial revision.
 * 
 * Rev 1.0 Apr 29 2002 15:18:08 msg Initial revision.
 * 
 * Rev 1.0 Mar 18 2002 11:38:06 msg Initial revision.
 * 
 * Rev 1.1 Jan 19 2002 10:28:20 mpm Initial implementation of pluggable-look-and-feel user interface. Resolution for
 * POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 * Rev 1.0 Sep 21 2001 11:29:30 msg Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.tax;

//java imports
import oracle.retail.stores.domain.lineitem.TaxableLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

//--------------------------------------------------------------------------
/**
 * Site for managing item-tax-override options.
 * <P>
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class ItemTaxOverrideOptionsSite extends PosSiteActionAdapter
{

    //--------------------------------------------------------------------------
    /**
     * Revision Number furnished by TeamConnection.
     * <P>
     */
    //--------------------------------------------------------------------------
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
     * This arrive shows the screen for all the options for ModifyItemTaxOverride (rate or amount).
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * 
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // get the POS UI manager
        POSUIManagerIfc uiManager = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // retrieve cargo
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();

        // set model to show the line item
        ListBeanModel beanModel = getModifyItemBeanModel(cargo.getItems());
        //ListBeanModel beanModel = getModifyItemBeanModel(cargo.getItem());
        // show the UI to select the tax option
        uiManager.showScreen(POSUIManagerIfc.ITEM_TAX_OVERRIDE_OPTIONS, beanModel);

    }

    //----------------------------------------------------------------------
    /**
     * Builds the ModifyItemBeanModel; this bean contains the the line item and the model that sets the local
     * navigation buttons to their correct enabled states.
     * <P>
     * 
     * @param lineItem
     *            The item to modify.
     * @return ModifyItemBeanModel
     */
    //----------------------------------------------------------------------
    protected ListBeanModel getModifyItemBeanModel(TaxableLineItemIfc[] lineItems)
    {
        NavigationButtonBeanModel nbbModel = new NavigationButtonBeanModel();

        // If there is no item
        if (lineItems == null)
        { // turn off everything except Inquiry
            nbbModel.setButtonEnabled(CommonActionsIfc.OVERRIDE_TAX_RATE, false);
            nbbModel.setButtonEnabled(CommonActionsIfc.OVERRIDE_TAX_AMOUNT, false);
        }
        else
        {
            nbbModel.setButtonEnabled(CommonActionsIfc.OVERRIDE_TAX_RATE, true);
            nbbModel.setButtonEnabled(CommonActionsIfc.OVERRIDE_TAX_AMOUNT, true);
        }
        ListBeanModel mibModel = new ListBeanModel();
        //mibModel.setListModel(new Object[]{lineItem});
        mibModel.setListModel(lineItems);
        mibModel.setLocalButtonBeanModel(nbbModel);

        return mibModel;
    }

    //----------------------------------------------------------------------
    /**
     * Depart method (a no-op in this case).
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>none
     * </UL>
     * 
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {

    }

    //----------------------------------------------------------------------
    /**
     * Returns a string representation of this object.
     * <P>
     * 
     * @return String representation of object
     */
    //----------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // return string
        return ("Class:  ItemTaxOverrideOptionsSite (Revision " + getRevisionNumber() + ") @" + hashCode());
    } // end toString()

    //----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     * <P>
     * 
     * @return String representation of revision number
     */
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()

    //----------------------------------------------------------------------
    /**
     * Main method used for test purposes.
     * <P>
     * 
     * @param args
     *            Command line parameters
     */
    //----------------------------------------------------------------------
    public static void main(String args[])
    { // begin main()
        // instantiate class
        ItemTaxOverrideOptionsSite clsItemTaxOverrideOptionsSite = new ItemTaxOverrideOptionsSite();

        // output toString()
        System.out.println(clsItemTaxOverrideOptionsSite.toString());
    } // end main()
}
