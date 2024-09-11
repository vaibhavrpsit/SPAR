/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/AlterationsLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    08/16/10 - cleanup
 *    acadar    08/16/10 - refactoring changes
 *    sgu       06/08/10 - fix item interactive screen prompts to include item
 *                         # and description
 *    sgu       06/08/10 - add item # & desc to the screen prompt. fix unknow
 *                         item screen to disable price and quantity for
 *                         external item
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:36 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:27 PM  Robert Pearse
 *
 *   Revision 1.5  2004/05/14 20:54:12  dfierling
 *   @scr 3830 -  Modification for Alteration printing instructions
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
 *    Rev 1.0   Aug 29 2003 16:00:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Oct 14 2002 15:19:00   DCobb
 * Initial revision.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.alterations.AlterationsCargo;

/**
 * This shuttle copies information from the cargo used in the modifyItem service
 * to the cargo used in the Alterations service.
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class AlterationsLaunchShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = -6301522350619274059L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(AlterationsLaunchShuttle.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * transaction
     */
    protected SaleReturnTransactionIfc transaction;

    /**
     * The line item to apply alterations
     */
    protected PLUItemIfc item = null;

    /**
     * line item index
     */
    protected int index;

    /**
     * Flag to indicate whether a service item was added thru inquiry/services
     */
    protected boolean serviceItemFlag = false;

    /**
     * This flag indicates whether to skip enter price even if the price entry
     * required flag of a PLU item is set to true.
     */
    protected boolean skipPriceEntryFlag = false;

    /**
     * Loads cargo from modifyitem service.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>Cargo will contain the selected item
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>
     * </UL>
     *
     * @param bus Service Bus
     */
    public void load(BusIfc bus)
    {
        // load financial cargo
        super.load(bus);

        // retrieve cargo from the parent
        ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();
        transaction = (SaleReturnTransactionIfc) cargo.getTransaction();
        item = cargo.getPLUItem();
        index = -1;
        serviceItemFlag = true;
        skipPriceEntryFlag = cargo.getExternalPrice() != null;
    }

    /**
     * Loads data into alterations service.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>Cargo will contain the selected item
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>
     * </UL>
     *
     * @param bus Service Bus
     */
    public void unload(BusIfc bus)
    {
        // unload financial cargo
        super.unload(bus);

        // retrieve cargo from the child
        AlterationsCargo cargo = (AlterationsCargo) bus.getCargo();
        cargo.setPLUItem(item);
        cargo.setIndex(index);
        cargo.setTransaction(transaction);
        cargo.setNewPLUItem(serviceItemFlag);
        cargo.setModifyItemService(true);
        cargo.setSkipPriceEntryFlag(skipPriceEntryFlag);
        if (serviceItemFlag == false)
        {
            cargo.setPriceEntered(true);
        }
        else
        {
            cargo.setPriceEntered(false);
        }
    }

    /**
     * Returns a string representation of this object.
     *
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        String strResult = new String("Class:  TenderLaunchShuttle (Revision " + getRevisionNumber() + ") @"
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
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    }
}
