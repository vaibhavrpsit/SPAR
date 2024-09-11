/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/tax/SetUpdatePolicySite.java /main/10 2011/02/16 09:13:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:12 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/21 23:25:20  dcobb
 *   @scr 4302 Correct compiler warnings.
 *   Use showScreen in place of ShowDialogScreen.
 *
 *   Revision 1.4  2004/02/24 16:21:31  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:14:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:39:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:31:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.tax;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * @version $Revision: /main/10 $
 */
public class SetUpdatePolicySite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 74374888239917490L;
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/10 $";

    /**
     * Checks to see if any items already have a item-tax modifier associated
     * with them. If so, it will show a dialog box to ask the user if they want
     * to update all the items with the new tax rate about to be entered. If no
     * items have a tax rate associated with them TRUE is set to update all
     * items and a Next Letter is sent.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {

        // retrieve cargo
        ModifyTransactionTaxCargo cargo =
            (ModifyTransactionTaxCargo) bus.getCargo();

        // get the POS UI manager
        POSUIManagerIfc uiManager=
            (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        if (cargo.getItemsModifiedFlag() == true)
        {                               // begin get update policy
            // show Yes/No UI to set policy
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("MultiTaxOverride");
            model.setType(DialogScreensIfc.CONFIRMATION);
            uiManager.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);

            // display dialog
            uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
        }                               // end get update policy
        else
        {                               // begin move to entry site
            // set all items to the tax rate that will be entered
            cargo.setUpdateAllItemsFlag(true);
            // get flag to indicate which road to take
            int flag = cargo.getTaxUpdateFlag();
            if (flag == ModifyTransactionTaxCargo.TAX_UPDATE_RATE)
            {
                bus.mail(new Letter("EnterTaxRate"), BusIfc.CURRENT);
            }
            else
            {
                bus.mail(new Letter("EnterTaxAmount"), BusIfc.CURRENT);
            }
        }                               // end move to entry site
    }

    /**
     * Checks to see if this site displayed a screen. If so, the screen is
     * redisplayed. If not, an Undo letter is sent to go back to the previous
     * screen.
     * 
     * @param bus Service Bus
     */
    @Override
    public void reset(BusIfc bus)
    {
        // retrieve cargo
        bus.mail(new Letter(CommonLetterIfc.UNDO), BusIfc.CURRENT);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     **/
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }
}