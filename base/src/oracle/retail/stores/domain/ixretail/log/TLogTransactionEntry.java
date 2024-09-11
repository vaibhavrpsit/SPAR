/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/ixretail/log/TLogTransactionEntry.java /main/1 2013/01/16 11:47:57 vtemker Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* vtemker     12/24/12 - CR 204 - Added isVoidedTransaction attribute
* vtemker     12/20/12 - initial version
* vtemker     12/20/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.domain.ixretail.log; 

public class TLogTransactionEntry extends POSLogTransactionEntry implements TLogTransactionEntryIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 4291077223083631617L;
    
    /**
     * is voided transaction
     */
    protected boolean isVoidedTransaction = false;
    
    /**
     * the voided transaction has already been batch exported
     */
    protected boolean isExported = false;
    
    /**
     * Check if its a voided transaction
     * @return
     */
    public boolean isVoidedTransaction()
    {
        return isVoidedTransaction;
    }

    /**
     * Set the flag if voided transaction
     * @param isVoidedTransaction
     */
    public void setVoidedTransaction(boolean isVoidedTransaction)
    {
        this.isVoidedTransaction = isVoidedTransaction;
    }

    /**
     * Check if the voided transaction has been exported
     */
    @Override
    public boolean isExported()
    {
        return isExported;
    }

    /**
     * Set the isExported flag
     */
    @Override
    public void setExported(boolean exported)
    {
        this.isExported = exported;
    }
    
}