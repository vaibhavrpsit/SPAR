/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/transaction/EnterExitTrainingModeTransactionADO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:41 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:01 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:24 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/03/17 19:05:43  tfritz
 *  @scr 3884 - Training Mode code review changes.
 *
 *  Revision 1.3  2004/03/16 16:21:04  tfritz
 *  @scr 3884 - New Training Mode functionality.
 *
 *  Revision 1.2  2004/03/15 20:36:48  tfritz
 *  @scr 3884 - Removed reference to no sale.
 *
 *  Revision 1.1  2004/03/14 21:12:41  tfritz
 *  @scr 3884 - New Training Mode Functionality
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.transaction;

import java.util.Map;

import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.foundation.manager.data.DataException;

/**
 *  This transaction represents the Enter/Exit Training Mode transactions
 */
public class EnterExitTrainingModeTransactionADO extends AbstractRetailTransactionADO
{
    /**
     * @see oracle.retail.stores.ado.transaction.AbstractRetailTransactionADO#instantiateTransactionRDO()
     */
    protected TransactionIfc instantiateTransactionRDO()
    {
        transactionRDO = DomainGateway.getFactory().getTransactionInstance();
        return transactionRDO;
    }
    
    /**
     * @see oracle.retail.stores.ado.transaction.RetailTransactionADOIfc#save(oracle.retail.stores.ado.store.RegisterADO)
     */
    public void save(RegisterADO registerADO) throws DataException
    {
        // TODO Auto-generated method stub
    }

    /**
     * @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map getJournalMemento()
    {
        return super.getJournalMemento();
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        return transactionRDO;
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        return toLegacy();
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        transactionRDO = (TransactionIfc) rdo;
    }
}
