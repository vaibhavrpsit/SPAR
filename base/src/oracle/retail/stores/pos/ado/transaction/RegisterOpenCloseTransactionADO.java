/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/transaction/RegisterOpenCloseTransactionADO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:41 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:39 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 16:47:57  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 04 2003 11:14:34   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 12:35:20   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.transaction;

import java.util.Map;

import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.RegisterOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.foundation.manager.data.DataException;

/**
 *  
 */
public class RegisterOpenCloseTransactionADO extends AbstractRetailTransactionADO
{
    /** our RDO transaction */
    protected RegisterOpenCloseTransactionIfc transactionRDO;
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.transaction.AbstractRetailTransactionADO#instantiateTransactionRDO()
     */
    protected TransactionIfc instantiateTransactionRDO()
    {
        transactionRDO = DomainGateway.getFactory().getRegisterOpenCloseTransactionInstance();
        return transactionRDO;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.transaction.RetailTransactionADOIfc#save(oracle.retail.stores.ado.store.RegisterADO)
     */
    public void save(RegisterADO registerADO) throws DataException
    {
        // TODO Auto-generated method stub
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map getJournalMemento()
    {
        return super.getJournalMemento();
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        return transactionRDO;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        return toLegacy();
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        transactionRDO = (RegisterOpenCloseTransactionIfc)rdo;
    }
}
