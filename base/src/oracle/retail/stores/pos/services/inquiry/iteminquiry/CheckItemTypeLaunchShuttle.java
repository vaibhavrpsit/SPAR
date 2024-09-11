/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/CheckItemTypeLaunchShuttle.java /main/14 2012/10/11 14:08:30 hyin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    hyin      10/11/12 - enable WebStore search flow.
 *    tksharma  10/11/11 - skipUOMCheck introduced in cargo
 *    cgreene   03/18/11 - XbranchMerge cgreene_124_receipt_quick_wins from
 *                         main
 *    cgreene   03/16/11 - implement You Saved feature on reciept and
 *                         AllowMultipleQuantity parameter
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:10:52 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:09 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:56 PM  Robert Pearse   
 *:
 *    5    .v700     1.2.1.1     10/31/2005 11:56:16    Deepanshu       CR
 *         6092: Set the Sales Associate in the ItemInquiryCargo
 *    4    .v700     1.2.1.0     9/13/2005 15:37:41     Jason L. DeLeau Ifan
 *         id_itm_pos maps to multiple id_itms, let the user choose which one
 *         to use.
 *    3    360Commerce1.2         3/31/2005 15:27:25     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:20:09     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:09:56     Robert Pearse
 *
 *   Revision 1.7  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.6  2004/08/05 22:17:54  dcobb
 *   @scr 6655 Remove letter checks from shuttles.
 *   Modified itemcheck service to initialize the modifyFlag to false and set to true when the item is ready to add to the sale.
 *
 *   Revision 1.5  2004/07/30 22:02:55  aschenk
 *   @scr 4960 - Selling a kit with a UOM item now asks for the qty.
 *
 *   Revision 1.4  2004/04/09 16:55:59  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:30  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Dec 12 2003 14:20:26   lzhao
 * move gift card issue related task to giftcard/issue package.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.1   04 Dec 2003 16:41:32   Tim Fritz
 * Fixed the problem where an Alteration SKU (40010001) was causing the POS app to crash.  Resolution for SCR 3530
 *
 *    Rev 1.0   13 Nov 2003 10:44:04   jriggins
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.stock.ItemKitIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

/**
 * This shuttle copies information from the cargo used in the modifyItem service
 * to the cargo used in the Alterations service.
 * 
 * @version $Revision: /main/14 $
 */
public class CheckItemTypeLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    static final long serialVersionUID = 8727086284286608011L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(CheckItemTypeLaunchShuttle.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    protected ItemInquiryCargo itemInquiryCargo = null;

    /**
     * Loads cargo from iteminquiry service.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>Cargo has set the search criteria for a particular item
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>Shuttle has a reference to the search criteria for a particular item
     * </UL>
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        itemInquiryCargo = (ItemInquiryCargo) bus.getCargo();
    }

    /**
     * Loads data into itemvalidate service.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * Calling service has set the search criteria for a particular item
     * <LI>
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>Cargo will contain the search criteria for a particular item
     * </UL>
     * 
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();

        cargo.setRegister(itemInquiryCargo.getRegister());
        cargo.setPLUItem(itemInquiryCargo.getPLUItem());
        cargo.setItemQuantity(itemInquiryCargo.getItemQuantity());
        cargo.setTransaction(itemInquiryCargo.getTransaction());
        cargo.setStoreStatus(itemInquiryCargo.getStoreStatus());
        cargo.setOperator(itemInquiryCargo.getOperator());
        cargo.setCustomerInfo(itemInquiryCargo.getCustomerInfo());
        cargo.setTenderLimits(itemInquiryCargo.getTenderLimits());
        cargo.setItemList(itemInquiryCargo.getItemList());
        cargo.setSalesAssociate(itemInquiryCargo.getSalesAssociate());
        cargo.skipUOMCheck(itemInquiryCargo.isSkipUOMCheck());
        cargo.setItemFromWebStore(itemInquiryCargo.isItemFromWebStore());

        if (itemInquiryCargo.getPLUItem() != null && itemInquiryCargo.getPLUItem().isKitHeader())
        {
            ((ItemKitIfc) cargo.getPLUItem()).setindex(-1);
        }

        cargo.setModifiedFlag(false);
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  ValidateItemLaunchShuttle (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}
