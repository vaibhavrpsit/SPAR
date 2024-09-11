/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/ShowKitComponentsLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    jswan     01/11/10 - Modified to fix issue with return for lowest price
 *                         in X days with kit items.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/25/2007 8:52:14 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    5    360Commerce 1.4         5/12/2006 5:25:32 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    4    360Commerce 1.3         1/22/2006 11:45:18 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:30:00 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:18 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:14 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/05/27 19:31:33  jdeleau
 *   @scr 2775 Remove unused imports as a result of tax engine rework
 *
 *   Revision 1.7  2004/05/27 17:12:48  mkp1
 *   @scr 2775 Checking in first revision of new tax engine.
 *
 *   Revision 1.6  2004/03/12 19:36:48  epd
 *   @scr 3561 Updates for handling kit items in non-retrieved no receipt returns
 *
 *   Revision 1.5  2004/03/11 23:39:48  epd
 *   @scr 3561 New work to accommodate returning kit items
 *
 *   Revision 1.4  2004/02/23 14:58:52  baa
 *   @scr 0 cleanup javadocs
 *
 *   Revision 1.3  2004/02/12 16:51:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 02 2003 14:22:02   sfl
 * Implement repeating algorithm during reading tax table based tax rules.
 * Resolution for POS SCR-3315: Implement Repeating Tax Table Algorithm
 *
 *    Rev 1.0   Aug 29 2003 16:06:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Jun 30 2003 16:34:48   sfl
 * Need to keep the advanced pricing rules for the manual return kit items.
 * Resolution for POS SCR-2359: Return with No Receipt on one component of a kit item is not calculating correctly
 *
 *    Rev 1.1   Apr 07 2003 15:34:56   sfl
 * Assigned the tax rules to the non-receipt return kit component items.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.0   Apr 29 2002 15:05:40   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:46:04   msg
 * Initial revision.
 *
 *    Rev 1.1   21 Nov 2001 12:26:04   pjf
 * Clear pricing rules from ItemKit before processing manual component returns.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.0   20 Nov 2001 09:12:10   pjf
 * Initial revision.
 * Resolution for POS SCR-8: Item Kits
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

// Java imports

import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.ItemKitIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.returns.returncommon.AbstractReturnLaunchShuttle;
import oracle.retail.stores.pos.services.returns.returnkit.ReturnKitCargo;

//--------------------------------------------------------------------------
/**
    This shuttle sets up the Return Transaction service to retrieve kit component
    items for manual return.  A temporary transaction is created and initialized
    with tax values using the utility manager.  The transaction is used to
    initialize the kit component line items by adding the kit header plu item.
    The kit component line items from the temporary transaction are
    returned to the POS service and added to the transaction in progress.
**/
//--------------------------------------------------------------------------
public class ShowKitComponentsLaunchShuttle extends AbstractReturnLaunchShuttle
{
    /**  */
    private static final long serialVersionUID = -7908213472612310625L;

    /**
     *  zero amount
     */
    public static final CurrencyIfc ZERO_AMOUNT = DomainGateway.getBaseCurrencyInstance(BigDecimal.ZERO);

    /**
     * Kit header plu item
     */
    ItemKitIfc kitHeaderItem = null;

    /**
     * Customer
     */
    SaleReturnTransactionIfc transaction = null;

    //----------------------------------------------------------------------
    /**
       Store data from parent service in the shuttle
       <P>
       @param  bus     Parent Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        // retrieve cargo from the parent
        ReturnItemCargo returnItemCargo = (ReturnItemCargo)bus.getCargo();
        //since this is a manual return, clear any advanced pricing rules
        //that came with the plu item
        kitHeaderItem = (ItemKitIfc)returnItemCargo.getPLUItem();
        transaction   = returnItemCargo.getTransaction();
    }

    //----------------------------------------------------------------------
    /**
       Transfer parent data to child cargo.
       <P>
       @param  bus     Child Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        //give returntransaction.xml the temporary transaction to update
        ReturnKitCargo cargo = (ReturnKitCargo)bus.getCargo();
        cargo.setPLUItem(kitHeaderItem);
        cargo.setTransaction(transaction);
    }

}
