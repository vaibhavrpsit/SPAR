/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/TaxInformationContainer.java /main/17 2014/07/24 15:23:28 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/23/14 - add tax authority name
 *    abhinavs  12/11/12 - Fixing HP Fortify missing null check issues
 *    sgu       09/22/11 - add function to set sign for tax amounts
 *    sgu       09/22/11 - negate return tax in post void case
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   04/22/09 - filter tax-rule based info when printing exempt
 *                         infos
 *    cgreene   03/31/09 - sort tax informations for printing purposes
 *    cgreene   03/25/09 - move determination of tax receipt code to
 *                         TaxInformation.getReceiptCode since the code should
 *                         be determined at request time, not at set time
 *    cgreene   12/08/08 - add getReasonCode method to taxcontainer for bpts to
 *                         query when there are no taxinfos
 *
 * ===========================================================================
 * $Log:
 * 6    360Commerce 1.5         4/30/2007 5:38:35 PM   Sandy Gu        added api
 *       to handle inclusive tax
 * 5    360Commerce 1.4         4/25/2007 10:00:25 AM  Anda D. Cadar   I18N
 *      merge
 * 4    360Commerce 1.3         4/2/2007 5:52:35 PM    Snowber Khan
 *      modules/domain/src/oracle/retail/stores/domain/tax/TaxInformationContainer.java
 *
 * 3    360Commerce 1.2         3/31/2005 4:30:19 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:25:47 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:14:42 PM  Robert Pearse
 *$ 4    360Commerce1.3         4/2/2007 5:52:35 PM    Snowber Khan
 *      modules/domain/src/oracle/retail/stores/domain/tax/TaxInformationContainer
 *      .java
 * 3    360Commerce1.2         3/31/2005 4:30:19 PM   Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:25:47 AM  Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:14:42 PM  Robert Pearse
 *$ 4    .v8x      1.2.1.0     3/9/2007 6:23:51 PM    Charles D. Baker CR 25856 -
 *       Updating to preserve "tax exempt amount" for record keeping - without
 *      treating it as a charged tax.
 * 3    360Commerce1.2         3/31/2005 3:30:19 PM   Robert Pearse
 * 2    360Commerce1.1         3/10/2005 10:25:47 AM  Robert Pearse
 * 1    360Commerce1.0         2/11/2005 12:14:42 PM  Robert Pearse
 *$$
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.utility.Util;

/**
 * @author mkp1
 */
public class TaxInformationContainer implements TaxInformationContainerIfc, TaxConstantsIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -824728337055634561L;

    /**
     * Container of tax informations. It should be okay to keep them ordered by
     * rule since there are never more than a couple.
     */
    private Map<String,TaxInformationIfc> container = new TreeMap<String,TaxInformationIfc>();
    /**
     * Container of tax informations grouped by tax authority
     */
    private Map<Integer,TaxInformationIfc> containerByAuthority = new TreeMap<Integer,TaxInformationIfc>();
    private CurrencyIfc taxAmount = DomainGateway.getBaseCurrencyInstance();
    private CurrencyIfc inclusiveTaxAmount = DomainGateway.getBaseCurrencyInstance();
    private CurrencyIfc taxExemptAmount = DomainGateway.getBaseCurrencyInstance();
    private int taxScope = TaxConstantsIfc.TAX_SCOPE_TRANSACTION;

    /**
     * Reset this container to 0 or initial values.
     * @see oracle.retail.stores.domain.tax.TaxInformationContainerIfc#reset()
     */
    public void reset()
    {
        container.clear();
        containerByAuthority.clear();
        taxAmount.setZero();
        inclusiveTaxAmount.setZero();
        taxExemptAmount.setZero();
        taxScope = TaxConstantsIfc.TAX_SCOPE_TRANSACTION;
    }

    /**
     * Add a taxInformation object to the container
     *
     * @param taxInformation information to add to the container
     * @see oracle.retail.stores.domain.tax.TaxInformationContainerIfc#addTaxInformation(oracle.retail.stores.domain.tax.TaxInformationIfc)
     */
    public void addTaxInformation(TaxInformationIfc taxInformation)
    {
        if (taxInformation.getInclusiveTaxFlag())
        {
            inclusiveTaxAmount = inclusiveTaxAmount.add(taxInformation.getTaxAmount());
        }
        else
        {
            taxAmount = taxAmount.add(taxInformation.getTaxAmount());
        }

        TaxInformationIfc checkTaxInformation = container.get(taxInformation.getUniqueID());
        if(checkTaxInformation == null)
        {
            container.put(taxInformation.getUniqueID(), (TaxInformationIfc)taxInformation.clone());
        }
        else
        {
            checkTaxInformation.add(taxInformation);
        }
        
        checkTaxInformation = containerByAuthority.get(taxInformation.getTaxAuthorityID());
        if(checkTaxInformation == null)
        {
            containerByAuthority.put(taxInformation.getTaxAuthorityID(), (TaxInformationIfc)taxInformation.clone());
        }
        else
        {
            checkTaxInformation.add(taxInformation);
        }
    }

    /**
     * Add a tax exempt information to the container
     *
     * @param tax exempt information to add to the container
     * @see oracle.retail.stores.domain.tax.TaxInformationContainerIfc#addTaxExemptInformation(oracle.retail.stores.domain.tax.TaxInformationIfc)
     */
    public void addTaxExemptInformation(CurrencyIfc taxExemptAmount)
    {
        this.taxExemptAmount = this.taxExemptAmount.add(taxExemptAmount);
    }

    /**
     * Add all of the tax information contained in the array.
     * @param taxInformationArray
     * @see oracle.retail.stores.domain.tax.TaxInformationContainerIfc#addTaxInformation(oracle.retail.stores.domain.tax.TaxInformationIfc[])
     */
    public void addTaxInformation(TaxInformationIfc[] taxInformationArray)
    {
        for(int i = 0; i < taxInformationArray.length; i++)
        {
            addTaxInformation(taxInformationArray[i]);
        }
    }


    /**
     * Get an array of all TaxInformationIfc objects in the container
     * @return taxInformation
     * @see oracle.retail.stores.domain.tax.TaxInformationContainerIfc#getTaxInformation()
     */
    public TaxInformationIfc[] getTaxInformation()
    {
        return container.values().toArray(new TaxInformationIfc[container.size()]);
   }

    /**
     * For a given uniqueID, get the TaxInformationIfc it corresponds to
     *
     * @param uniqueID
     * @return TaxInformationIfc object
     * @see oracle.retail.stores.domain.tax.TaxInformationContainerIfc#getTaxInformation(java.lang.String)
     */
    public TaxInformationIfc getTaxInformation(String uniqueID)
    {
        return container.get(uniqueID);
    }

    /**
     * Return an ordered list of tax information objects contained
     * within this container, ordered by tax rule name.
     *
     * @return ordered list of taxes
     * @see oracle.retail.stores.domain.tax.TaxInformationContainerIfc#getOrderedTaxInformation()
     */
    public TaxInformationIfc[] getOrderedTaxInformation()
    {
        ArrayList<TaxInformationIfc> list = new ArrayList<TaxInformationIfc>(container.values());

        // remove non-needed tax exempt infos
        for (Iterator<TaxInformationIfc> iter = list.iterator(); iter.hasNext();)
        {
            TaxInformationIfc info = iter.next();
            if (info.getTaxMode() == TAX_MODE_EXEMPT &&
                    info.getUsesTaxRate())
            {
                iter.remove();
            }
        }

        // sort
        Collections.sort(list);

        return list.toArray(new TaxInformation[list.size()]);
    }
    
    /**
     * Get an array of tax information grouped by tax authority and
     * ordered by authority name. 
     * 
     * @return Array of TaxInformationIfc grouped and sorted by tax 
     * authority.
     */
    public TaxInformationIfc[] getTaxInformationByAuthority()
    {
        return containerByAuthority.values().toArray(new TaxInformationIfc[containerByAuthority.size()]);
    }

    /**
     * This method is here as a convenience to the receipt blueprints in order
     * get the 'N' code from this object when there are no actual tax informations
     * in the {@link #container} to query.
     *
     * @see oracle.retail.stores.domain.tax.TaxInformationIfc#getReceiptCode()
     */
    public String getReceiptCode()
    {
        if (!container.isEmpty())
        {
            return getTaxInformation()[0].getReceiptCode();
        }
        return TAX_MODE_CHAR[TAX_MODE_NON_TAXABLE];
    }

    /**
     * Get the amount of tax charged
     *
     * @return taxAmount
     * @see oracle.retail.stores.domain.tax.TaxInformationContainerIfc#getTaxAmount()
     */
    public CurrencyIfc getTaxAmount()
    {
        return taxAmount;
    }

    /**
     * Get the amount of inclusive tax charged
     *
     * @return inclusiveTaxAmount
     * @see oracle.retail.stores.domain.tax.TaxInformationContainerIfc#getInclusiveTaxAmount()
     */
    public CurrencyIfc getInclusiveTaxAmount()
    {
        return inclusiveTaxAmount;
    }

    /**
     * Get the tax exempt amount
     *
     * @return taxExemptAmount
     * @see oracle.retail.stores.domain.tax.TaxInformationContainerIfc#getTaxExemptAmount()
     */
    public CurrencyIfc getTaxExemptAmount()
    {
        return taxExemptAmount;
    }

    /**
     * Get the tax scope
     *
     * @return scope
     * @see oracle.retail.stores.domain.tax.TaxInformationContainerIfc#getTaxScope()
     */
    public int getTaxScope()
    {
        return this.taxScope;
    }

    /**
     * Set the tax scope
     *
     * @param scope
     * @see oracle.retail.stores.domain.tax.TaxInformationContainerIfc#setTaxScope(int)
     */
    public void setTaxScope(int scope)
    {
        this.taxScope = scope;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
    	if(obj == this)
    	{
    		return true;
    	}
    	boolean isEqual=false;
    	if(obj instanceof TaxInformationContainer)
    	{
    		TaxInformationContainer otherObj = (TaxInformationContainer) obj;
    		if (Util.isObjectEqual(taxAmount, otherObj.getTaxAmount()) &&
    				Util.isObjectEqual(inclusiveTaxAmount, otherObj.getInclusiveTaxAmount()) &&
    				Util.isObjectEqual(taxExemptAmount, otherObj.getTaxExemptAmount()) &&
    				getTaxScope() == otherObj.getTaxScope() &&
    				Util.isObjectEqual(container, otherObj.container) &&
    				Util.isObjectEqual(containerByAuthority, otherObj.containerByAuthority))
    		{
    			isEqual=true;
    		}
    	}
    	return isEqual;

    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone()
    {
        TaxInformationContainer newClass = new TaxInformationContainer();
        setCloneAttributes(newClass);
        return newClass;
    }

    /**
     * Set attributes for clone. <P>
     * @param newClass new instance of TaxInformationContainer
     */
    public void setCloneAttributes(TaxInformationContainer newClass)
    {
        if(taxAmount != null)
        {
            newClass.taxAmount = (CurrencyIfc) taxAmount.clone();
        }

        if (inclusiveTaxAmount != null)
        {
           newClass.inclusiveTaxAmount = (CurrencyIfc) inclusiveTaxAmount.clone();
        }

        if(taxExemptAmount != null)
        {
            newClass.taxExemptAmount = (CurrencyIfc) taxExemptAmount.clone();
        }

        newClass.setTaxScope(getTaxScope());
        if(container != null)
        {
            TaxInformationIfc[] array = getTaxInformation();
            for(int i = 0; i < array.length; i++)
            {
                newClass.container.put(array[i].getUniqueID(), (TaxInformationIfc)array[i].clone());
            }
        }
        if(containerByAuthority != null)
        {
            TaxInformationIfc[] array = getTaxInformationByAuthority();
            for(int i = 0; i < array.length; i++)
            {
                newClass.containerByAuthority.put(array[i].getTaxAuthorityID(), (TaxInformationIfc)array[i].clone());
            }
        }
    }

    /**
     * Retrieves the accumulation of all the uniqueID's passed in.
     * @param arrayUniqueID the uniqueID's to retrieve tax information for.
     * @return the accumulation of tax information for all uniqueID's passed in.
     */
    public TaxInformationIfc getTaxInformation(String[] arrayUniqueID)
    {
        TaxInformationIfc taxInformation = DomainGateway.getFactory().getTaxInformationInstance();
        TaxInformationIfc testTaxInformation = null;
        for(int i = 0; i < arrayUniqueID.length; i++)
        {
            testTaxInformation = getTaxInformation(arrayUniqueID[i]);
            if(testTaxInformation != null)
            {
                taxInformation.add(testTaxInformation);
            }
        }
        return taxInformation;
    }

    /**
     * Update all the internals to negative values, because this was a
     * return transaction.
     *
     */
    public void negate()
    {
        TaxInformationIfc[] taxInfo = getTaxInformation();
        for(int i=0; i<taxInfo.length; i++)
        {
            taxInfo[i].negate();
        }

        this.taxAmount = this.taxAmount.negate();
        this.inclusiveTaxAmount = this.inclusiveTaxAmount.negate();
        this.taxExemptAmount = this.taxExemptAmount.negate();
    }

    /**
     * Set all currency values to be the specified sign
     *
     * @param sign CurrencyIfc.POSITIVE or CurrencyIfc.Negative
     */
    public void setSign(int sign)
    {
        if (sign == CurrencyIfc.POSITIVE || sign == CurrencyIfc.NEGATIVE)
        {
            TaxInformationIfc[] taxInfo = getTaxInformation();
            for(int i=0; i<taxInfo.length; i++)
            {
                taxInfo[i].setSign(sign);
            }

            int negatedSign = sign * -1;
            if (this.taxAmount.signum() == negatedSign)
            {
                this.taxAmount = this.taxAmount.negate();
            }
            if (this.inclusiveTaxAmount.signum() == negatedSign)
            {
                this.inclusiveTaxAmount = this.inclusiveTaxAmount.negate();
            }
            if (this.taxExemptAmount.signum() == negatedSign)
            {
                this.taxExemptAmount = this.taxExemptAmount.negate();
            }
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuffer strResult = new StringBuffer(super.toString()).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxAmount", taxAmount)).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("inclusiveTaxAmount", inclusiveTaxAmount)).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxExemptAmount", taxExemptAmount)).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("taxScope", taxScope)).append(Util.EOL);
        strResult.append("===> Begin container elements <====").append(Util.EOL);
        for (String key : container.keySet())
        {
            strResult.append("----> Begin element for key ");
            strResult.append(key);
            strResult.append("<----");
            strResult.append(Util.EOL);
            strResult.append(container.get(key).toString());
            strResult.append("----> Begin element for key ");
            strResult.append(key);
            strResult.append("<----");
            strResult.append(Util.EOL);

        }
        strResult.append("===> End container elements <====").append(Util.EOL);
        return strResult.toString();
    }

}
