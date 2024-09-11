/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/store/StoreADO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:42 mszekely Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:30:10 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:25:30 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:14:25 PM  Robert Pearse   
 *
 * Revision 1.5  2004/09/23 00:07:12  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.4  2004/07/15 16:13:22  kmcbride
 * @scr 5954 (Services Impact): Adding logging to these ADOs, also fixed some exception handling issues.
 *
 * Revision 1.3  2004/04/08 20:33:02  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * 
 * Rev 1.4 Nov 21 2003 08:34:56 rwh Added implements StoreADOIfc Resolution for
 * Foundation SCR-266: Add Factory Class Support for ADO
 * 
 * Rev 1.3 Nov 21 2003 08:11:40 rwh updated packaging for relocated
 * ADOException Resolution for Foundation SCR-266: Add Factory Class Support
 * for ADO
 * 
 * Rev 1.2 Nov 12 2003 10:07:18 rwh Added getContext() method to ADO base class
 * Resolution for Foundation SCR-265: Add ADOContext reference to ADO base
 * class
 * 
 * Rev 1.1 Nov 12 2003 09:26:36 rwh Added setChildContexts() method Resolution
 * for Foundation SCR-265: Add ADOContext reference to ADO base class
 * 
 * Rev 1.0 Nov 04 2003 11:12:22 epd Initial revision.
 * 
 * Rev 1.0 Oct 17 2003 12:32:52 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.store;

import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.domain.financial.StoreStatus;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

/**
 * 
 *  
 */
public class StoreADO extends ADO implements StoreADOIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -9212184352353879280L;

    /** Our encapsulated RDO */
    protected StoreStatusIfc storeStatusRDO;

    /**
     * Returns the business date of this store
     * 
     * @return the current business date.
     */
    public EYSDate getBusinessDate()
    {
        return storeStatusRDO.getBusinessDate();
    }

    /**
     * Gets the store number
     * 
     * @return the store number
     */
    public String getStoreID()
    {
        return storeStatusRDO.getStore().getStoreID();
    }

    /**
     * Initialize a txn with the business date. Encapsulated here due to work
     * that must be done with the RDO objects.
     * 
     * @param txn
     *            The transaction to be initialized.
     */
    public void initializeTransaction(RetailTransactionADOIfc txn)
    {
        if(logger.isInfoEnabled())
        {
            logger.info("Initializing transaction...");
        }
        
        TransactionIfc txnRDO = (TransactionIfc) ((ADO) txn).toLegacy();
        txnRDO.setBusinessDay(storeStatusRDO.getBusinessDate());
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        //assert(rdo instanceof StoreStatusIfc) : "Illegal Argument: " +
        // rdo.getClass().getName();

        StoreStatusIfc storeStatus = (StoreStatusIfc) rdo;
        storeStatusRDO = storeStatus;
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        return storeStatusRDO;
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        EYSDomainIfc result = null;
        if (type == StoreStatusIfc.class || type == StoreStatus.class)
        {
            result = toLegacy();
        }
        return result;
    }

}
