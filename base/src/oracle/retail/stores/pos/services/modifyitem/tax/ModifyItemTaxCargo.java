/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/tax/ModifyItemTaxCargo.java /main/11 2011/02/16 09:13:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    acadar 10/28/08 - localization for item tax reason codes
 * ===========================================================================
 * $Log:
 *  4    360Commerce 1.3         7/28/2006 5:44:15 PM   Brett J. Larsen 4530:
 *       default reason code fix
 *       v7x->360Commerce merge
 *  3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse
 * $

 *  4    .v7x      1.2.1.0     6/23/2006 5:02:14 AM   Dinesh Gautam   CR 4530:
 *       Fix for reason code

 * Revision 1.9  2004/09/23 00:07:11  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.8  2004/05/07 20:09:09  dcobb
 * @scr 4654 Added On/Off with Ineligible Items alternate flow.
 *
 * Revision 1.7  2004/05/05 22:18:53  dcobb
 * @scr 4389 Tax Override multiitem select non-taxable & taxable items, then turn off tax on these items: tax is not turned off
 *
 * Revision 1.6  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.5  2004/03/08 21:07:52  bjosserand
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
 * Rev 1.0 Aug 29 2003 16:02:02 CSchellenger Initial revision.
 *
 * Rev 1.1 Feb 12 2003 18:22:36 crain Refactored the methods that retrieve reason code lists Resolution for 1907:
 * Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 * Rev 1.0 Apr 29 2002 15:18:10 msg Initial revision.
 *
 * Rev 1.0 Mar 18 2002 11:38:08 msg Initial revision.
 *
 * Rev 1.2 18 Jan 2002 19:02:50 baa convert to new security model Resolution for POS SCR-309: Convert to new Security
 * Override design.
 *
 * Rev 1.1 08 Jan 2002 17:22:46 baa add tax override to flow when items are sent out of state Resolution for POS
 * SCR-520: Prepare Send code for review
 *
 * Rev 1.0 Sep 21 2001 11:29:38 msg Initial revision.
 *
 * Rev 1.1 Sep 17 2001 13:09:22 msg header update * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 */

package oracle.retail.stores.pos.services.modifyitem.tax;

import java.io.Serializable;

import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.TaxableLineItemIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Cargo class for ModifyItemTax service.
 *
 * @version $Revision: /main/11 $
 */
public class ModifyItemTaxCargo extends UserAccessCargo implements CargoIfc, Serializable
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 3750855769431902259L;

    /**
     * revision number supplied by Team Connection
     */
    public static String revisionNumber = "$Revision: /main/11 $";
    /**
     * flag indicating this record has been modified
     */
    protected boolean dirtyFlag = false;
    /**
     * flag for final signal
     */
    protected boolean finalFlag = false;
    /**
     * flag indicating type of tax update -- rate or amount
     */

    protected boolean outOfArea = false;
    /**
     * flag indicating type of tax update -- rate or amount
     */
    protected int taxUpdateFlag = 0;
    /**
     * constant for update rate
     */
    public static int TAX_UPDATE_RATE = 0;
    /**
     * constant for update amount
     */
    public static int TAX_UPDATE_AMOUNT = 1;
    /**
     * constant for toggle tax
     */
    public static int TAX_TOGGLE = 2;

    protected CodeListIfc localizedOverrideRateReasons = null;

    /**
     * override-amount reason code list
     */
    protected CodeListIfc localizedOverrideAmountReasons = null;

    /**
     * toggle reason code list
     */
    protected CodeListIfc localizedToggleReasons = null;
    /**
     * item tax object
     */
    protected ItemTaxIfc itemTax = null;
    /**
     * sale return line item array
     */
    protected TaxableLineItemIfc[] items = null;

    protected ItemTaxModControllerIfc cntl;

    /**
     * Constructs ModifyItemTaxCargo object.

     */
    public ModifyItemTaxCargo()
    {
    }

    /**
     * Initialize cargo, setting item tax attribute.
     *
     * @param pItems  The selected line items
     * @param tax     The new tax for the modified line items.
     */
    public void initialize(TaxableLineItemIfc[] pItems, ItemTaxIfc tax)
    {
        items = pItems;
        itemTax = tax;
    }

    /**
     * Sets dirty flag.
     * 
     * @param value
     *            new dirty-flag setting
     */
    public void setDirtyFlag(boolean value)
    {
        // set value
        dirtyFlag = value;
    }

    /**
     * Retrieves dirty flag.
     * 
     * @return dirty flag
     */
    public boolean getDirtyFlag()
    {
        // pass back value
        return (dirtyFlag);
    }

    /**
     * Sets outOfArea Flag.
     * 
     * @param value
     *            new outOfArea-flag setting
     */
    public void setSendOutOfArea(boolean value)
    {
        // set value
        outOfArea = value;
    }

    /**
     * Retrieves sendOutOfArea flag.
     * 
     * @return outOfArea flag
     */
    public boolean isSendOutOfArea()
    {
        // pass back value
        return (outOfArea);
    }
    /**
     * Sets final flag.
     * 
     * @param value
     *            new final-flag setting
     */
    public void setFinalFlag(boolean value)
    {
        // set value
        finalFlag = value;
    }

    /**
     * Retrieves final flag.
     * 
     * @return final flag
     */
    public boolean getFinalFlag()
    {
        // pass back value
        return (finalFlag);
    }

    /**
     * Retrieves tax-update flag.
     * 
     * @return tax-update flag
     */
    public int getTaxUpdateFlag()
    {
        // pass back value
        return (taxUpdateFlag);
    }

    /**
     * Sets tax-update flag.
     * 
     * @param value
     *            new tax-update-flag setting
     */
    public void setTaxUpdateFlag(int value)
    {
        // set value
        taxUpdateFlag = value;
    }

    /**
     * Retrieves selected item.
     * 
     * @return selected item
     */
    public TaxableLineItemIfc[] getItems()
    {
        // pass back value
        return (items);
    }

    /**
     * Sets selected item.
     * 
     * @param value
     *            new selected item
     */
    public void setItems(TaxableLineItemIfc[] value)
    {
        // set value
        items = value;
    }


    /**
     * Sets the localized override rate reason code
     * @param value
     */
    public void setLocalizedOverrideRateReasons(CodeListIfc value)
    {
        // set value
        localizedOverrideRateReasons = value;
    }

    /**
     * Gets the localized override rate reason code
     * @return
     */
    public CodeListIfc getLocalizedOverrideRateReasons()
    {
        return localizedOverrideRateReasons;
    }


    public void setLocalizedOverrideAmountReasons(CodeListIfc value)
    {
        localizedOverrideAmountReasons = value;
    }

    /**
     * Gets the localized override amount reason codes
     * @return CodeListIfc
     */
    public CodeListIfc getLocalizedOverrideAmountReasons()
    {
         return localizedOverrideAmountReasons;
    }



    /**
     * Sets the localized toggle reason codes
     * @param value
     */
    public void setLocalizedToggleReasons(CodeListIfc value)
    {
        localizedToggleReasons = value;
    }

    /**
     * Gets the localized toggle reasons
     * @return
     */
    public CodeListIfc getLocalizedToggleReasons()
    {
        return localizedToggleReasons;
    }

    /**
     * Sets the item tax.
     * 
     * @param value  new item tax
     */
    public void setItemTax(ItemTaxIfc value)
    {
        // set value
        itemTax = value;
    }

    /**
     * Retrieves item tax.
     * <P>
     * @return item tax
     */
    public ItemTaxIfc getItemTax()
    {
        // pass back value
        return (itemTax);
    }
    /**
     * Returns the function ID whose access is to be checked.
     *
     * @return int Role Function ID
     */
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.TAX_MODIFICATION;
    }
    /**
     * Method to default display string function.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult =
            new String("Class:  ModifyItemTaxCargo (Revision " + getRevisionNumber() + ") @" + hashCode());
        // pass back result
        return (strResult);
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * Get the controller.
     * @return the Modify Item Tax controller.
     */
    public ItemTaxModControllerIfc getController()
    {
        return cntl;
    }

    /**
     * Set the controller.
     * @param cntl  The Modify Item Tax controller.
     */
    public void setController(ItemTaxModControllerIfc cntl)
    {
        this.cntl = cntl;
    }

    /**
     * Display the specified Error Dialog
     *
     * @param ui            UI Manager to handle the IO
     * @param name          The Error Dialog to display
     * @param dialogType    The dialog type
     */
    public void displayDialog(POSUIManagerIfc ui, String name, int dialogType)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(name);
        dialogModel.setType(dialogType);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

}
