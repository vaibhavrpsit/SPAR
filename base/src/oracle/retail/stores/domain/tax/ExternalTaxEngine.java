/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/ExternalTaxEngine.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:49 mszekely Exp $
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
 * 3    360Commerce 1.2         3/31/2005 4:28:08 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:21:33 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:10:58 PM  Robert Pearse
 *$$
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;





/**
 * This class is used if there is an external tax manager present
 * It no-ops the tax calculation because it is performed by an external
 * source.
  */
public class ExternalTaxEngine implements TaxEngineIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 6167459007844939019L;


    /**
     * Constructs the external tax engine
     *
     */
    public ExternalTaxEngine()
    {
    }

   
    /**
     * This provides a no-op so tax is not calculated since we
     * are using an outside source. <P> 
     * @see oracle.retail.stores.domain.tax.TaxEngineIfc#calculateTax(oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc[], oracle.retail.stores.domain.transaction.TransactionTotalsIfc, oracle.retail.stores.domain.transaction.TransactionTaxIfc)
     */
    public void calculateTax(TaxLineItemInformationIfc[] lineItems, TransactionTotalsIfc totals
            , TransactionTaxIfc transactionTax)    
    {
    }


    /**
     * Creates a deep copy of this object
     * @see oracle.retail.stores.domain.utility.EYSDomainIfc#clone()
     */
    public Object clone()
    {
        ExternalTaxEngine newClass = new ExternalTaxEngine();
        setCloneAttributes(newClass);
        return newClass;
    }

     /**
     * Set attributes for clone. <P>
     * @param newClass new instance of TaxProrateRule
     */
    
     public void setCloneAttributes(ExternalTaxEngine newClass)
    {
    }
    
}
