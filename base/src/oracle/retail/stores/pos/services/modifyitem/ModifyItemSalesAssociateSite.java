/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ModifyItemSalesAssociateSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:25 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse   
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
 *    Rev 1.3   Feb 03 2004 16:50:30   bwf
 * remove unused imports.
 * 
 *    Rev 1.2   Jan 27 2004 16:03:28   bwf
 * Remove deprecations.  No longer needed.
 * 
 *    Rev 1.1   Jan 27 2004 15:28:28   bwf
 * Update for sales associate with multi item select.
 * Resolution for 3765: Modify Item Feature
 * 
 *    Rev 1.0   Aug 29 2003 16:01:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:17:34   msg
 * Initial revision.
 * 
 *    Rev 1.2   26 Mar 2002 10:35:38   vxs
 * Modified comments
 * Resolution for POS SCR-83: Item sales assoc does not default in if one is entered
 *
 *    Rev 1.1   25 Mar 2002 17:20:42   vxs
 * Customized PromptAndResponse panel added in arrive() which displays sales associate in response field.
 * Resolution for POS SCR-83: Item sales assoc does not default in if one is entered
 *
 *    Rev 1.0   Mar 18 2002 11:37:26   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:29:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
 *      This Site shows the Modify Sales Associate screen
 *   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class ModifyItemSalesAssociateSite extends PosSiteActionAdapter
{

    //--------------------------------------------------------------------------
    /**
     *   Revision Number furnished by TeamConnection. <P>
     */
    //--------------------------------------------------------------------------
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";


    //--------------------------------------------------------------------------
    /**
     *   This method shows the ITEM_SALES_ASSC screen<P>
     *   <B>Pre-Condition(s)</B>
     *   <UL>
     *   <LI>
     *   </UL>
     *   <B>Post-Condition(s)</B>
     *   <UL>
     *   <LI>
     *   </UL>
     *   @param  BusIfc bus
     *   @return void
     *   @exception
     */
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();

        ItemCargo cargo = (ItemCargo)bus.getCargo();

        // show the sales associate entry screen
        ui.showScreen(POSUIManagerIfc.ITEM_SALES_ASSC, baseModel);

    }

    //---------------------------------------------------------------------
    /**
     *   Main to run a test.. <P>
     *   @return void
     */
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()

        // instantiate class
        ModifyItemSalesAssociateSite clsModifyItemSalesAssociateSite = new ModifyItemSalesAssociateSite();

        // output toString()
        System.out.println(clsModifyItemSalesAssociateSite.toString());

    }                                  // end main()
}
