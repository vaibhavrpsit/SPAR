/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/TaxTotalsContainer.java /main/13 2014/07/24 15:23:29 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/24/14 - add tax container by authority
 *    abondala  09/04/13 - initialize collections
 *    abhinavs  12/11/12 - Fixing HP Fortify missing null check issues
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         9/20/2007 11:29:19 AM  Rohit Sachdeva
 *         28813: Initial Bulk Migration for Java 5 Source/Binary
 *         Compatibility of All Products
 *    4    360Commerce 1.3         8/7/2006 3:10:15 PM    Brendan W. Farrell
 *         Change fix from v7.x to meet coding standards.
 *    3    360Commerce 1.2         3/31/2005 4:30:21 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:45 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/06/24 09:10:36  mwright
 *   Corrected assignment inside conditional error
 *
 *   Revision 1.2  2004/06/18 20:18:21  jdeleau
 *   @scr 2775 Add clone/equals methods to domain objects for tax
 *
 *   Revision 1.1  2004/06/15 00:44:31  jdeleau
 *   @scr 2775 Support register reports and financial totals with the new
 *   tax engine.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import java.util.HashMap;
import java.util.Iterator;

import oracle.retail.stores.domain.tax.TaxInformationContainerIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

/**
 * Container object holding various taxes for financial totals
 * 
 * $Revision: /main/13 $
 */
public class TaxTotalsContainer implements TaxTotalsContainerIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1839032355724085097L;

    /**
     * Container of tax totals grouped by unique id
     */
    private HashMap<String, TaxTotalsIfc> taxTotalsContainer;

    /**
     * Container of tax totals grouped by tax authority id
     */
    private HashMap<Integer,TaxTotalsIfc> taxTotalsContainerByAuthority;
    
    private String storeId;
    private String workstationId;
    private String tillId;
    private EYSDate businessDate;
    
    /**
     * Default constructor
     */
    public TaxTotalsContainer()
    {
        taxTotalsContainer = new HashMap<String, TaxTotalsIfc>(0);
        taxTotalsContainerByAuthority = new HashMap<Integer, TaxTotalsIfc>(0);
    }
    
    /**
     * Construct tax totals from tax information
     *  
     * @param container
     */
    public TaxTotalsContainer(TaxInformationContainerIfc container)
    {
        taxTotalsContainer = new HashMap<String, TaxTotalsIfc>(0);
        taxTotalsContainerByAuthority = new HashMap<Integer, TaxTotalsIfc>(0);
        setTaxInfoContainer(container);
        
    }
    
    /**
     * Sets up the tax information from the TaxInformationContainerIfc
     * This should only be used when the taxTotalsContainer is brand new.
     * Most of the time you should use addTaxTotals
     * @param container
     */
    public void setTaxInfoContainer(TaxInformationContainerIfc container)
    {
        if (taxTotalsContainer == null)
        {
            taxTotalsContainer = new HashMap<String, TaxTotalsIfc>(0);
        }
        if (taxTotalsContainerByAuthority == null)
        {
            taxTotalsContainerByAuthority = new HashMap<Integer, TaxTotalsIfc>(0);
        }
        TaxInformationIfc[] taxInfo = container.getTaxInformation();
        for(int i=0; i<taxInfo.length; i++)
        {
            TaxTotalsIfc taxTotals = new TaxTotals(taxInfo[i]);
            addTaxTotals(taxTotals);
        }
    }

    /**
     * @param totals TaxTotals to add to the container
     * @see oracle.retail.stores.domain.financial.TaxTotalsContainerIfc#addTaxTotals(oracle.retail.stores.domain.financial.TaxTotalsIfc)
     */
    public void addTaxTotals(TaxTotalsIfc totals)
    {
        if(taxTotalsContainer.get(totals.getUniqueId()) == null)
        {
            taxTotalsContainer.put(totals.getUniqueId(), (TaxTotalsIfc)totals.clone());
        }
        else
        {
            TaxTotalsIfc oldTotals = (TaxTotalsIfc) taxTotalsContainer.get(totals.getUniqueId());
            oldTotals.add(totals);
        }
        
        if(taxTotalsContainerByAuthority.get(totals.getTaxAuthorityId()) == null)
        {
            taxTotalsContainerByAuthority.put(totals.getTaxAuthorityId(), (TaxTotalsIfc)totals.clone());
        }
        else
        {
            TaxTotalsIfc oldTotals = (TaxTotalsIfc) taxTotalsContainerByAuthority.get(totals.getTaxAuthorityId());
            oldTotals.add(totals);
        }
    }

    /**
     * Subtract this taxTotals object from the container
     *  
     *  @param totals
     */
    public void subtractTaxTotals(TaxTotalsIfc totals)
    {
        if(taxTotalsContainer.get(totals.getUniqueId()) == null)
        {
            // Treat this as a negative
            taxTotalsContainer.put(totals.getUniqueId(), ((TaxTotalsIfc)totals.clone()).negate());
        }
        else
        {
            TaxTotalsIfc oldTotals = (TaxTotalsIfc) taxTotalsContainer.get(totals.getUniqueId());
            oldTotals.subtract(totals);
        }
        
        if(taxTotalsContainerByAuthority.get(totals.getTaxAuthorityId()) == null)
        {
            // Treat this as a negative
            taxTotalsContainerByAuthority.put(totals.getTaxAuthorityId(), ((TaxTotalsIfc)totals.clone()).negate());
        }
        else
        {
            TaxTotalsIfc oldTotals = (TaxTotalsIfc) taxTotalsContainerByAuthority.get(totals.getTaxAuthorityId());
            oldTotals.subtract(totals);
        }
    }

    /**
     * @return Array of tax totals
     * @see oracle.retail.stores.domain.financial.TaxTotalsContainerIfc#getTaxTotals()
     */
    public TaxTotalsIfc[] getTaxTotals()
    {
        return (TaxTotalsIfc[]) taxTotalsContainer.values().toArray(new TaxTotalsIfc[0]);
    }
    
    /**
     * Get the array of all tax totals by authority
     *  
     * @return all the totals for each authority
     */
    public TaxTotalsIfc[] getTaxTotalsByAuthority()
    {
        return (TaxTotalsIfc[]) taxTotalsContainerByAuthority.values().toArray(new TaxTotalsIfc[0]);
    }
    
    /**
     * Add a container to this one, which really adds the contents
     * of the container to this one.
     *  
     * @param container Object to add to this container
     * @see oracle.retail.stores.domain.financial.TaxTotalsContainerIfc#add(oracle.retail.stores.domain.financial.TaxTotalsContainerIfc)
     */
    public void add(TaxTotalsContainerIfc container)
    {
        TaxTotalsIfc[] taxTotals = container.getTaxTotals();
        for(int i=0; i<taxTotals.length; i++)
        {
            this.addTaxTotals(taxTotals[i]);
        }
    }
    
    /**
     * Subtract a container fromt his one
     *  
     * @param container Object to add to this container
     * @see oracle.retail.stores.domain.financial.TaxTotalsContainerIfc#subtract(oracle.retail.stores.domain.financial.TaxTotalsContainerIfc)
     */
    public void subtract(TaxTotalsContainerIfc container)
    {
        TaxTotalsIfc[] taxTotals = container.getTaxTotals();
        for(int i=0; i<taxTotals.length; i++)
        {
            this.subtractTaxTotals(taxTotals[i]);
        }
    }
    
    /**
     * Clone this object
     *  
     * @return cloned object
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        TaxTotalsContainer newClass = new TaxTotalsContainer();
        TaxTotalsIfc[] taxTotals = getTaxTotals();
        for(int i=0; i<taxTotals.length; i++)
        {
            newClass.addTaxTotals((TaxTotalsIfc) taxTotals[i]);
        }
        return newClass;
    }
    
    /**
     * Returns true if all attributes are equal.
     *  
     * @param obj
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
    	if(obj == this)
    	{
    		return true;
    	}
    	boolean equal = false;
    	if(obj instanceof TaxTotalsContainer)
    	{
    		TaxTotalsContainer otherContainer = (TaxTotalsContainer) obj;
    		Iterator<String> iter1 = this.taxTotalsContainer.keySet().iterator();
    		while(iter1.hasNext())
    		{
    			Object key = iter1.next();
    			TaxTotalsIfc value = (TaxTotalsIfc) taxTotalsContainer.get(key);

    			equal = value.equals(otherContainer.taxTotalsContainer.get(key));

    			if(!equal)
    			{
    				break;
    			}

    		}
    		
    		if (equal == true)
    		{
    		    Iterator<Integer> iter2 = this.taxTotalsContainerByAuthority.keySet().iterator();
    		    while(iter2.hasNext())
    		    {
    		        Object key = iter2.next();
    		        TaxTotalsIfc value = (TaxTotalsIfc) taxTotalsContainerByAuthority.get(key);

    		        equal = value.equals(otherContainer.taxTotalsContainerByAuthority.get(key));

    		        if(!equal)
    		        {
    		            break;
    		        }

    		    }
    		}
    		
    		if(equal == true)
    		{
    			equal = otherContainer.getStoreId() == this.getStoreId() &&
    					otherContainer.getWorkstationId() == this.getWorkstationId() &&
    					otherContainer.getTillId() == this.getTillId() &&
    					Util.isObjectEqual(otherContainer.getBusinessDate(), this.getBusinessDate());
    		}
    	}
    	return equal;

    }
    
    /**
     * @return Returns the businessDate.
     */
    public EYSDate getBusinessDate()
    {
        return businessDate;
    }
    /**
     * @param businessDate The businessDate to set.
     */
    public void setBusinessDate(EYSDate businessDate)
    {
        this.businessDate = businessDate;
    }
    /**
     * @return Returns the storeId.
     */
    public String getStoreId()
    {
        return storeId;
    }
    /**
     * @param storeId The storeId to set.
     */
    public void setStoreId(String storeId)
    {
        this.storeId = storeId;
    }
    /**
     * @return Returns the tillId.
     */
    public String getTillId()
    {
        return tillId;
    }
    /**
     * @param tillId The tillId to set.
     */
    public void setTillId(String tillId)
    {
        this.tillId = tillId;
    }
    /**
     * @return Returns the workstationId.
     */
    public String getWorkstationId()
    {
        return workstationId;
    }
    /**
     * @param workstationId The workstationId to set.
     */
    public void setWorkstationId(String workstationId)
    {
        this.workstationId = workstationId;
    }
    
    /**
     * Return a string representation of this classes contents
     *  
     * @return This class as a string
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuffer strResult = new StringBuffer(super.toString()).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("storeId", this.getStoreId())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("workstationId", this.getWorkstationId())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("tillId", this.getTillId())).append(Util.EOL);
        strResult.append(Util.formatToStringEntry("businessDate", this.getBusinessDate())).append(Util.EOL);
        TaxTotalsIfc[] taxTotals = this.getTaxTotals();
        strResult.append("List Contents:").append(Util.EOL);
        for(int i=0; i<taxTotals.length; i++)
        {
            strResult.append(taxTotals[i].toString());
        }
        taxTotals = this.getTaxTotalsByAuthority();
        strResult.append("List Contents by authority:").append(Util.EOL);
        for(int i=0; i<taxTotals.length; i++)
        {
            strResult.append(taxTotals[i].toString());
        }
        strResult.append("** END OF TAX TOTALS CONTAINER **").append(Util.EOL);
        return strResult.toString();
    }
}
