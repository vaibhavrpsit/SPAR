/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/houseaccount/payment/TenderLaunchShuttle.java /main/14 2014/01/07 18:00:13 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  01/07/14 - fix dereferencing of null objects.
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   07/26/11 - repacked into houseaccount.payment
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:00 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:54 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:51:29  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:05  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.5   Dec 01 2003 18:49:28   nrao
 * Uncommented cargo.getTransaction().
 * 
 *    Rev 1.4   Nov 19 2003 16:21:22   epd
 * Refactoring updates
 * 
 *    Rev 1.3   Nov 04 2003 11:22:04   epd
 * Updates for repackaging
 * 
 *    Rev 1.2   Oct 23 2003 17:24:46   epd
 * Updated to use renamed ADO packages
 * 
 *    Rev 1.1   Oct 17 2003 13:01:28   epd
 * Updated for new ADO tender service
 * 
 *    Rev 1.0   Aug 29 2003 16:04:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:11:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:42:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:32:24   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.houseaccount.payment;

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
import oracle.retail.stores.pos.services.tender.TenderCargo;

import org.apache.log4j.Logger;

/**
 * This shuttle copies information from the cargo used in the POS service to the
 * cargo used in the Tender service.
 * 
 * @version $Revision: /main/14 $
 */
public class TenderLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 858019751391649194L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(TenderLaunchShuttle.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/14 $";
    /**
     * transaction
     */
    protected TenderableTransactionIfc transaction = null;

    /**
     * Loads cargo from POS service. Cargo will contain the selected item.
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        PayHouseAccountCargo cargo = (PayHouseAccountCargo)bus.getCargo();
        transaction = cargo.getTransaction();
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
        cargo.setTransaction(transaction);

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
        TransactionPrototypeEnum txnType = TransactionPrototypeEnum.makeEnumFromTransactionType(cargo.getTransaction()
                .getTransactionType());
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
        ((ADO)txnADO).fromLegacy(cargo.getTransaction());
        cargo.setCurrentTransactionADO(txnADO);
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