/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/deposit/TenderLaunchShuttle.java /main/15 2014/01/07 18:00:13 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  01/07/14 - fix dereferencing of null objects.
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    jswan     07/27/11 - Set the order transaction on the tender cargo. This
 *                         prevents a null pointer exception in authorization
 *                         launch shuttle.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 |    4    360Commerce 1.3         4/30/2008 3:50:30 PM   Charles D. Baker CR
 |         31539 - Corrected ejournalling of special order gift card tenders.
 |         Code review by Jack Swan.
 |    3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse   
 |    2    360Commerce 1.1         3/10/2005 10:26:00 AM  Robert Pearse   
 |    1    360Commerce 1.0         2/11/2005 12:14:54 PM  Robert Pearse   
 |   $
 |   Revision 1.5  2004/09/23 00:07:16  kmcbride
 |   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 |
 |   Revision 1.4  2004/04/08 22:14:55  cdb
 |   @scr 4206 Cleaned up class headers for logs and revisions.
 |
 |   Revision 1.3  2004/02/12 16:52:03  mcs
 |   Forcing head revision
 |
 |   Revision 1.2  2004/02/11 21:52:29  rhafernik
 |   @scr 0 Log4J conversion and code cleanup
 |
 |   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 |   updating to pvcs 360store-current
 | 
 |    Rev 1.5   Nov 19 2003 16:21:24   epd
 | Refactoring updates
 | 
 |    Rev 1.4   Nov 10 2003 09:22:16   epd
 | fixed bug in Shuttle 
 | 
 |    Rev 1.3   Nov 04 2003 11:22:10   epd
 | Updates for repackaging
 | 
 |    Rev 1.2   Oct 23 2003 17:24:56   epd
 | Updated to use renamed ADO packages
 | 
 |    Rev 1.1   Oct 17 2003 13:05:46   epd
 | Updated for new ADO tender service
 | 
 |    Rev 1.0   Aug 29 2003 16:07:22   CSchellenger
 | Initial revision.
 | 
 |    Rev 1.0   Apr 29 2002 15:01:54   msg
 | Initial revision.
 | 
 |    Rev 1.0   Mar 18 2002 11:48:22   msg
 | Initial revision.
 | 
 |    Rev 1.1   Dec 04 2001 16:09:00   dfh
 | No change.
 | Resolution for POS SCR-260: Special Order feature for release 5.0
 | 
 |    Rev 1.0   Dec 04 2001 15:11:22   dfh
 | Initial revision.
 | Resolution for POS SCR-260: Special Order feature for release 5.0
 |
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder.deposit;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.store.StoreADO;
import oracle.retail.stores.pos.ado.store.StoreFactory;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.TransactionPrototypeEnum;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;

/**
 * This shuttle copies information from the cargo used in the special order
 * payment service to the cargo used in the Tender service.
 * 
 * @version $Revision: /main/15 $
 */
public class TenderLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -4415375827605154815L;

    /** The logger to which log messages will be sent. */
    protected static final Logger logger = Logger.getLogger(TenderLaunchShuttle.class);
    /** revision number supplied by source control system */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * special order cargo reference
     */
    protected SpecialOrderCargo specialOrderCargo = null;

    /**
     * Loads cargo from special order payment service.
     * 
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        specialOrderCargo = (SpecialOrderCargo)bus.getCargo();
    }

    /**
     * Loads data into tender service.
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
        TransactionPrototypeEnum txnType = TransactionPrototypeEnum.makeEnumFromTransactionType(specialOrderCargo
                .getTenderableTransaction().getTransactionType());
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
        ((ADO)txnADO).fromLegacy(specialOrderCargo.getTenderableTransaction());
        cargo.setCurrentTransactionADO(txnADO);
        cargo.setTransactionInProgress(specialOrderCargo.getTenderableTransaction() != null);

        // /////////////////////////////////
        // End ADO
        // /////////////////////////////////

        cargo.setTransaction(specialOrderCargo.getTenderableTransaction());
    } 

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.FinancialCargoShuttle#toString()
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
    {
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }

}