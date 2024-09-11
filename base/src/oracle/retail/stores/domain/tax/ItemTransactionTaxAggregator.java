/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/ItemTransactionTaxAggregator.java /main/5 2014/06/23 13:03:35 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         06/19/14 - fix item tax aggregator
* sgu         06/11/14 - combine multiple order transactions into one final one
* tksharma    12/10/12 - commons-lang update 3.1
* sgu         09/13/12 - handle tax exemption
* sgu         09/05/12 - use aggregator to calculate combined order transaction
*                        tax
* sgu         09/05/12 - refactor transaction tax transformation
* sgu         09/03/12 - use IllegalArgumentException
* sgu         09/03/12 - refactor transaction tax transformation
* sgu         09/02/12 - add new file
* sgu         09/02/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.domain.tax;

import java.math.BigDecimal;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.log4j.Logger;

public class ItemTransactionTaxAggregator implements ItemTransactionTaxAggregatorIfc
{
    /* Static Logger for this class */
    protected static Logger logger = Logger.getLogger(ItemTransactionTaxAggregator.class);
    
    /**
     * Aggregate line item tax into transaction tax
     * @param lineItems an array of line items
     * @return transaction tax
     */
    public TransactionTaxIfc aggregate(SaleReturnLineItemIfc[] lineItems)
    {
        TransactionTaxIfc transactionTax = null;
        boolean isItemTaxAllZero = true;
        
        for (SaleReturnLineItemIfc lineItem : lineItems)
        {
            ItemTaxIfc itemTax = lineItem.getItemTax();
            boolean taxOverrideAmtFlag = (itemTax.getTaxMode() == TaxConstantsIfc.TAX_MODE_OVERRIDE_AMOUNT);
            boolean taxOverrideRateFlag = (itemTax.getTaxMode() == TaxConstantsIfc.TAX_MODE_OVERRIDE_RATE);
            boolean taxExemptFlag = (itemTax.getTaxMode() == TaxConstantsIfc.TAX_MODE_EXEMPT);
            boolean transactionTaxScope = itemTax.getTaxScope() == TaxConstantsIfc.TAX_SCOPE_TRANSACTION;

            if (itemTax.getItemTaxAmount().getDecimalValue().compareTo(BigDecimal.ZERO) != 0)
            {
                isItemTaxAllZero = false;
            }
            
            // if the item tax is transaction scope tax override
            if ((taxOverrideAmtFlag || taxOverrideRateFlag || taxExemptFlag) && transactionTaxScope)
            {
                if (transactionTax == null)
                {
                    transactionTax = createTransactionTax(itemTax);
                }
                else if (checkCompatibility(transactionTax, itemTax))
                {
                    if (taxOverrideAmtFlag)
                    {
                        transactionTax.setOverrideAmount(transactionTax.getOverrideAmount()
                                .add(itemTax.getItemTaxAmount()));
                    }
                }
                else
                {
                    // The tax mode from the item tax is not compatible with the transaction tax.
                    // Just create a standard transaction tax and return.
                    transactionTax = createDefaultTransactionTax();
                    break;
                } 
            }
           
        }
        
        // Create a standard transaction tax if there is no tax override or exempt
        if (transactionTax == null)
        {
            transactionTax = createDefaultTransactionTax();
        }
        else if ((transactionTax.getTaxMode() == TaxConstantsIfc.TAX_MODE_EXEMPT) && !isItemTaxAllZero)
        {
            logger.warn("An item cannot have non-zero tax if tax exempt flag is on.");
            transactionTax = createDefaultTransactionTax();
        }

        return transactionTax;
    }

    /**
     * Create a transaction tax from an item tax
     *
     * @param itemTax the item tax
     * @return transaction tax
     */
    protected TransactionTaxIfc createTransactionTax(ItemTaxIfc itemTax)
    {
        TransactionTaxIfc transactionTax = DomainGateway.getFactory().getTransactionTaxInstance();

        transactionTax.setUseItemRulesForTaxOverride(true);
        transactionTax.setTaxMode(itemTax.getTaxMode());
        transactionTax.setReason(itemTax.getReason());
        if (transactionTax.getTaxMode() == TaxConstantsIfc.TAX_MODE_OVERRIDE_RATE)
        {
            transactionTax.setOverrideRate(itemTax.getOverrideRate());
        }
        else if (transactionTax.getTaxMode() == TaxConstantsIfc.TAX_MODE_OVERRIDE_AMOUNT)
        {
            transactionTax.setOverrideAmount(itemTax.getItemTaxAmount());
        }

        return transactionTax;
    }

    /**
     * Throw message exception if the item tax and transaction tax are not compatible
     * @param transactionTax a transaction tax
     * @param itemTax an item tax
     */
    protected boolean checkCompatibility(TransactionTaxIfc transactionTax, ItemTaxIfc itemTax)
    {
        boolean isCompatible = true;
        if (transactionTax.getTaxMode() != itemTax.getTaxMode())
        {
            // The tax mode for transaction level tax override should be the same across all the tax line items.
            logger.warn("The tax mode for order level tax override cannot vary from item to item.");
            isCompatible = false;
        }
        else if ((transactionTax.getTaxMode() == TaxConstantsIfc.TAX_MODE_OVERRIDE_RATE) &&
                (transactionTax.getOverrideRate() != itemTax.getOverrideRate()))
        {
            logger.warn("The rate for order level tax override cannot vary from item to item.");
            isCompatible = false;          
        }
        else if ((transactionTax.getTaxMode() == TaxConstantsIfc.TAX_MODE_EXEMPT) && 
                (itemTax.getItemTaxAmount().getDecimalValue().compareTo(BigDecimal.ZERO) != 0))
        {
            logger.warn("An item cannot have non-zero tax if tax exempt flag is on.");
            isCompatible = false;  
        }
        else
        {
            String transactionTaxReasonCode = null;
            if (transactionTax.getReason() != null)
            {
                transactionTaxReasonCode = transactionTax.getReason().getCode();
            }

            String itemTaxReasonCode = null;
            if (itemTax.getReason() != null)
            {
                itemTaxReasonCode = itemTax.getReason().getCode();
            }

            if (!ObjectUtils.equals(transactionTaxReasonCode, itemTaxReasonCode))
            {
                logger.warn("The reason code for order level tax override cannot vary from item to item.");
                isCompatible = false; 
            }
        }
        
        return isCompatible;
    }
    
    /**
     * @return a default transaction tax
     */
    protected TransactionTaxIfc createDefaultTransactionTax()
    {
        TransactionTaxIfc transactionTax = DomainGateway.getFactory().getTransactionTaxInstance();
        transactionTax.setTaxMode(TaxConstantsIfc.TAX_MODE_STANDARD);

        return transactionTax;
    }
}


