/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ModifyItemTaxLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:25 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:04 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:23:34 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:40 PM  Robert Pearse   
 * $
 * Revision 1.9  2004/09/23 00:07:12  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.8  2004/05/05 22:18:53  dcobb
 * @scr 4389 Tax Override multiitem select non-taxable & taxable items, then turn off tax on these items: tax is not turned off
 *
 * Revision 1.7  2004/03/16 18:30:46  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.6  2004/03/11 22:28:39  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.5  2004/03/08 21:07:52  bjosserand
 * @scr 3954 Tax Override
 *
 * Revision 1.4  2004/03/05 00:41:52  bjosserand
 * @scr 3954 Tax Override
 * Revision 1.3 2004/02/12 16:51:03 mcs Forcing head revision
 * 
 * Revision 1.2 2004/02/11 21:39:28 rhafernik @scr 0 Log4J conversion and code cleanup
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:18 cschellenger updating to pvcs 360store-current
 * 
 * 
 * 
 * Rev 1.0 Aug 29 2003 16:01:46 CSchellenger Initial revision.
 * 
 * Rev 1.1 Feb 14 2003 16:26:52 crain Removed deprecated calls Resolution for 1907: Remove deprecated calls to
 * AbstractFinancialCargo.getCodeListMap()
 * 
 * Rev 1.0 Apr 29 2002 15:17:42 msg Initial revision.
 * 
 * Rev 1.0 Mar 18 2002 11:37:32 msg Initial revision.
 * 
 * Rev 1.1 18 Jan 2002 19:01:48 baa convert to new security model Resolution for POS SCR-309: Convert to new Security
 * Override design.
 * 
 * Rev 1.0 Sep 21 2001 11:29:18 msg Initial revision.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.TaxableLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.modifyitem.tax.ModifyItemTaxCargo;
import oracle.retail.stores.pos.services.modifyitem.tax.ItemTaxModController;
import oracle.retail.stores.pos.services.modifyitem.tax.ItemTaxModControllerIfc;

//------------------------------------------------------------------------------
/**
 * Launch shuttle class for ModifyItemTax service.
 * <P>
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class ModifyItemTaxLaunchShuttle implements ShuttleIfc
{ // begin class ModifyItemTaxLaunchShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7871604713342505547L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger =
        Logger.getLogger(oracle.retail.stores.pos.services.modifyitem.ModifyItemTaxLaunchShuttle.class);
    ;

    /**
     * revision number supplied by Team Connection
     */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * Cargo from the Item service.
     */
    protected ItemCargo iCargo = null;

    //---------------------------------------------------------------------
    /**
     * Load parent (ModifyItem) cargo class.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>Cargo in bus is instance of ItemCargo class
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>Cargo loaded
     * </UL>
     * 
     * @param b
     *            bus interface
     */
    //---------------------------------------------------------------------
    public void load(BusIfc bus)
    { // begin load()
        // log entry
        // retrieve cargo
        iCargo = (ItemCargo) bus.getCargo();

        // log exit
    } // end load()

    //---------------------------------------------------------------------
    /**
     * Unload to child (ModifyItemTax) cargo class.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>Cargo in bus is instance of ModifyItemTaxCargo class
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>Cargo unloaded
     * </UL>
     * 
     * @param b
     *            bus interface
     */
    //---------------------------------------------------------------------
    public void unload(BusIfc bus)
    { // begin unload()
        // log entry

        // retrieve cargo
        ModifyItemTaxCargo cargo = (ModifyItemTaxCargo) bus.getCargo();

        ItemTaxIfc itemTax = null;
        // initialize cargo with item, item tax object
        //TaxableLineItemIfc item = (TaxableLineItemIfc)iCargo.getItem().clone();
        TaxableLineItemIfc[] items = (TaxableLineItemIfc[]) iCargo.getItems();

        if ((items != null) && (items.length > 0))
        {            
            for (int i = 0; i < items.length; i++)
            {
                items[i] = (TaxableLineItemIfc) items[i].clone();
            }    
            // set item tax to the value from the TaxableLineItemIfc array
            itemTax = (ItemTaxIfc)items[0].getItemTax().clone();
        }
        else
        {
            logger.error("ModifyItemTaxLaunchShuttle.arrive() - no items in list");
        }

        cargo.initialize(items, itemTax);

        cargo.setOperator(iCargo.getOperator());

        ItemTaxModControllerIfc controller = new ItemTaxModController();

        cargo.setController(controller);

    } // end unload()

    //---------------------------------------------------------------------
    /**
     * Method to default display string function.
     * <P>
     * 
     * @return String representation of object
     */
    //---------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult =
            new String("Class:  ModifyItemTaxLaunchShuttle (Revision " + getRevisionNumber() + ")" + hashCode());
        // pass back result
        return (strResult);
    } // end toString()

    //---------------------------------------------------------------------
    /**
     * Retrieves the Team Connection revision number.
     * <P>
     * 
     * @return String representation of revision number
     */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()

} // end class ModifyItemTaxLaunchShuttle
