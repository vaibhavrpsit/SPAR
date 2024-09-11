/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/payment/TenderLaunchShuttle.java /main/16 2014/01/07 18:00:13 mjwallac Exp $
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
 *
 * ===========================================================================
 * $Log:
 |    6    360Commerce 1.5         6/13/2008 6:29:21 AM   Manas Sahu      When
 |         calling the Tender Tour from Layaway delete we need to set the
 |         transaction object into TenderCargo else in case of Refund the
 |         CaptureCustomer tour cannot be invoked as it gives a
 |         NullPointerException. Code Changed by Mani. Reviewed by Manas
 |    5    360Commerce 1.4         4/30/2008 4:26:10 PM   Charles D. Baker CR
 |         31522 - Updated shuttles to transfer knowledge of transaction in
 |         progress for the purpose of ejournal of gift card tender. Code
 |         review by Deepti Sharma.
 |    4    360Commerce 1.3         2/24/2008 2:51:06 PM   Pardee Chhabra  CR
 |         30468:Tender refund options are not displayed as per specification
 |         for layaway delete feature.
 |    3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse   
 |    2    360Commerce 1.1         3/10/2005 10:26:00 AM  Robert Pearse   
 |    1    360Commerce 1.0         2/11/2005 12:14:54 PM  Robert Pearse   
 |   $
 |   Revision 1.4  2004/09/23 00:07:16  kmcbride
 |   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 |
 |   Revision 1.3  2004/02/12 16:50:53  mcs
 |   Forcing head revision
 |
 |   Revision 1.2  2004/02/11 21:51:22  rhafernik
 |   @scr 0 Log4J conversion and code cleanup
 |
 |   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 |   updating to pvcs 360store-current
 |
 |    Rev 1.5   Nov 19 2003 16:21:18   epd
 | Refactoring updates
 |
 |    Rev 1.4   Nov 10 2003 09:25:56   epd
 | fixed bug
 |
 |    Rev 1.3   Nov 04 2003 11:21:54   epd
 | Updates for repackaging
 |
 |    Rev 1.2   Oct 23 2003 17:24:36   epd
 | Updated to use renamed ADO packages
 |
 |    Rev 1.1   Oct 17 2003 12:57:48   epd
 | Updated for new ADO tender service
 |
 |    Rev 1.0   Aug 29 2003 16:00:54   CSchellenger
 | Initial revision.
 |
 |    Rev 1.0   Apr 29 2002 15:20:18   msg
 | Initial revision.
 |
 |    Rev 1.0   Mar 18 2002 11:35:38   msg
 | Initial revision.
 |
 |    Rev 1.1   06 Mar 2002 16:29:44   baa
 | Replace get/setAccessEmployee with get/setOperator
 | Resolution for POS SCR-802: Security Access override for Reprint Receipt does not journal to requirements
 |
 |    Rev 1.0   Sep 21 2001 11:21:40   msg
 | Initial revision.
 |
 |    Rev 1.1   Sep 17 2001 13:08:40   msg
 | header update
 |
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.payment;

import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.store.StoreADO;
import oracle.retail.stores.pos.ado.store.StoreFactory;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.TransactionPrototypeEnum;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.services.tender.TenderCargo;

/**
 * Shuttle used to transfer layaway payment related data.
 * 
 * @version $Revision: /main/16 $
 */
public class TenderLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -4592805265619114794L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * Layaway Cargo being transferred
     */
    protected LayawayCargo layawayCargo = null;

    /**
     * This array contains a list of SaleReturnTransacions on which returns have
     * been completed.
     */
    protected SaleReturnTransactionIfc[] originalReturnTransactions = null;

    /**
     * This contains the original layaway transaction
     */
    protected TenderableTransactionIfc layawayTransaction = null;

    /**
     * Loads the shuttle data from the parent service's cargo into this shuttle.
     * The data elements to transfer are determined by the interfaces that the
     * parent cargo implements. For example, none of the layawaySearchCargo data
     * elements will be transferred if the calling service's cargo implements
     * layawaySummaryCargoIfc.
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        layawayCargo = (LayawayCargo)bus.getCargo();
        originalReturnTransactions = layawayCargo.getOriginalReturnTransactions();
        layawayTransaction = layawayCargo.getTransaction();
    }

    /**
     * Unloads the shuttle data into the cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        TenderCargo cargo = (TenderCargo) bus.getCargo();

        ////////////////////////////////////
        // Construct ADO's
        ////////////////////////////////////

        // set the transaction
        cargo.setTransaction(layawayTransaction);

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
        TransactionPrototypeEnum txnType = TransactionPrototypeEnum
                        .makeEnumFromTransactionType(layawayCargo.getTransaction().getTransactionType());
        RetailTransactionADOIfc txnADO = null;
        try
        {
            txnADO = txnType.getTransactionADOInstance();
        }
        catch (ADOException e)
        {
            // Exception getting ADO instance, log exception and return
            logger.error("Exception while getting ADO Instance " + e.getMessage());
            return;
        }
        ((ADO)txnADO).fromLegacy(layawayCargo.getTransaction());
        cargo.setCurrentTransactionADO(txnADO);
        cargo.setTransactionInProgress(layawayCargo.getTransaction() != null);

        //  Create/convert/set in cargo original return ADO transactions
        if (originalReturnTransactions != null)
        {
            RetailTransactionADOIfc[] originalReturnTxnADOs =
                    new RetailTransactionADOIfc[originalReturnTransactions.length];
            for (int i=0; i< originalReturnTransactions.length; i++)
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


        ///////////////////////////////////
        // End ADO
        ///////////////////////////////////
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.FinancialCargoShuttle#toString()
     */
    @Override
    public String toString()
    {
        String strResult =
            new String("Class: "    + getClass().getName() +
                       "(Revision " + getRevisionNumber()  +
                       ") @" + hashCode());

        return(strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

}
