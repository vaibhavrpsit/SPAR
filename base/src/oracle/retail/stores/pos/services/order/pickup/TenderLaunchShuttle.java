/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/pickup/TenderLaunchShuttle.java /main/17 2014/01/07 18:00:13 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  01/07/14 - fix dereferencing of null objects.
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    asinton   06/11/09 - Added the transaction to the tender cargo.
 *    mahising  02/23/09 - Fixed variation issue in total among a sale
 *                         transaction and order transaction
 *
 * ===========================================================================
 * $Log:
 |    5    360Commerce 1.4         4/30/2008 3:50:30 PM   Charles D. Baker CR
 |         31539 - Corrected ejournalling of special order gift card tenders.
 |         Code review by Jack Swan.
 |    4    360Commerce 1.3         2/22/2008 10:30:34 AM  Pardee Chhabra  CR
 |         30191: Tender Refund options are not displayed as per specification
 |          for Special Order Cancel feature.
 |    3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse   
 |    2    360Commerce 1.1         3/10/2005 10:26:00 AM  Robert Pearse   
 |    1    360Commerce 1.0         2/11/2005 12:14:54 PM  Robert Pearse   
 |   $
 |   Revision 1.4  2004/09/23 00:07:17  kmcbride
 |   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 |
 |   Revision 1.3  2004/02/12 16:51:26  mcs
 |   Forcing head revision
 |
 |   Revision 1.2  2004/02/11 21:51:37  rhafernik
 |   @scr 0 Log4J conversion and code cleanup
 |
 |   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 |   updating to pvcs 360store-current
 | 
 |    Rev 1.5   Nov 19 2003 16:21:20   epd
 | Refactoring updates
 | 
 |    Rev 1.4   Nov 17 2003 15:08:12   epd
 | fixed boo boo
 | 
 |    Rev 1.3   Nov 04 2003 11:22:00   epd
 | Updates for repackaging
 | 
 |    Rev 1.2   Oct 23 2003 17:24:42   epd
 | Updated to use renamed ADO packages
 | 
 |    Rev 1.1   Oct 17 2003 13:00:48   epd
 | Updated for new ADO tender service
 | 
 |    Rev 1.0   Aug 29 2003 16:03:54   CSchellenger
 | Initial revision.
 | 
 |    Rev 1.0   Apr 29 2002 15:12:04   msg
 | Initial revision.
 | 
 |    Rev 1.0   Mar 18 2002 11:41:48   msg
 | Initial revision.
 | 
 |    Rev 1.0   15 Jan 2002 18:49:46   cir
 | Initial revision.
 | Resolution for POS SCR-260: Special Order feature for release 5.0
 | 
 |    Rev 1.0   Sep 21 2001 11:33:14   msg
 | Initial revision.
 | 
 |    Rev 1.1   Sep 17 2001 13:10:56   msg
 | header update
 |
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.pickup;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.store.StoreADO;
import oracle.retail.stores.pos.ado.store.StoreFactory;
import oracle.retail.stores.pos.ado.transaction.OrderTransactionADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.TransactionPrototypeEnum;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

/**
 * This shuttle copies information from the cargo used in the POS service to the
 * cargo used in the Tender service.
 * 
 * @version $Revision: /main/17 $
 */
public class TenderLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -4610208674545029025L;
    /** Handle to the logger */
    protected static final Logger logger = Logger.getLogger(TenderLaunchShuttle.class);
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/17 $";
    /**
     * transaction
     */
    protected RetailTransactionIfc transaction = null;
    /**
     * This array contains a list of SaleReturnTransacions on which returns have
     * been completed.
     */
    protected SaleReturnTransactionIfc[] originalReturnTransactions = null;

    /**
     * Loads cargo from Pickup service. Cargo will contain the selected item.
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        PickupOrderCargo cargo = (PickupOrderCargo)bus.getCargo();
        transaction = cargo.getTransaction();
        originalReturnTransactions = cargo.getOriginalReturnTransactions();
    }

    /**
     * Loads data into tender service. Cargo will contain the selected item.
     * 
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        TenderCargo cargo = (TenderCargo)bus.getCargo();

        // //////////////////////////////////
        // Construct ADO's
        // //////////////////////////////////

        // create a register
        StoreFactory storeFactory = StoreFactory.getInstance();
        RegisterADO registerADO = storeFactory.getRegisterADOInstance();
        registerADO.fromLegacy(cargo.getRegister());

        // create the store
        StoreADO storeADO = storeFactory.getStoreADOInstance();
        storeADO.fromLegacy(cargo.getStoreStatus());

        // put store in register
        registerADO.setStoreADO(storeADO);

        // Create/convert/set in cargo ADO transaction
        TransactionPrototypeEnum txnType = TransactionPrototypeEnum.makeEnumFromTransactionType(transaction
                .getTransactionType());
        OrderTransactionADO txnADO = null;
        try
        {
            txnADO = (OrderTransactionADO)txnType.getTransactionADOInstance();
        }
        catch (ADOException e)
        {
            // Exception getting ADO instance, log exception and return
            logger.error("Exception while getting ADO Instance " + e.getMessage());
            return;
        }
        ((ADO)txnADO).fromLegacy(transaction);
        cargo.setCurrentTransactionADO(txnADO);
        cargo.setTransactionInProgress(transaction != null);
        cargo.setTransaction(transaction);

        // Create/convert/set in cargo original return ADO transactions
        if (originalReturnTransactions != null)
        {
            RetailTransactionADOIfc[] originalReturnTxnADOs = new RetailTransactionADOIfc[originalReturnTransactions.length];
            for (int i = 0; i < originalReturnTransactions.length; i++)
            {
                TransactionPrototypeEnum returnTxnType = TransactionPrototypeEnum
                        .makeEnumFromTransactionType(originalReturnTransactions[i].getTransactionType());
                RetailTransactionADOIfc returnTxnADO = null;
                try
                {
                    returnTxnADO = returnTxnType.getTransactionADOInstance();
                }
                catch (ADOException e2)
                {
                    // Exception getting ADO instance, log exception and return
                    logger.error("Exception while getting ADO Instance " + e2.getMessage());
                    return;
                }
                ((ADO)returnTxnADO).fromLegacy(originalReturnTransactions[i]);
                originalReturnTxnADOs[i] = returnTxnADO;
            }
            cargo.setOriginalReturnTxnADOs(originalReturnTxnADOs);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.FinancialCargoShuttle#toString()
     */
    @Override
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
    {
        // return string
        return (revisionNumber);
    }

}