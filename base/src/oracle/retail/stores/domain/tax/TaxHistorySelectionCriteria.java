/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/TaxHistorySelectionCriteria.java /main/12 2012/07/02 10:12:52 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/02/12 - Tax cleanup in preparation for JPA conversion.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         12/8/2006 5:00:34 PM   Brendan W. Farrell 
 *
 *   Revision 1.1  2004/06/15 00:44:30  jdeleau
 *   @scr 2775 Support register reports and financial totals with the new
 *   tax engine.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;

import oracle.retail.stores.domain.utility.EYSDate;

/**
 * Define what kind of tax rules to retrieve.
 * $Revision: /main/12 $
 */
public class TaxHistorySelectionCriteria implements TaxHistorySelectionCriteriaIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = 7729478347325124475L;
    /**
     * Search the tax history for all transactions in a store
     */
    public final static int SEARCH_BY_STORE = 0;
    /**
     * Search the tax history for all transactions in a till
     */
    public final static int SEARCH_BY_TILL = 1;
    /**
     * Search the tax history for all transactions in a register
     */
    public final static int SEARCH_BY_WORKSTATION = 2;
    
    private String storeId;
    private String workstationId;
    private String tillId;
    private EYSDate businessDate;
    private int criteriaType;
    
    /**
     * @return Returns the criteriaType.
     */
    public int getCriteriaType()
    {
        return criteriaType;
    }
    /**
     * @param criteriaType The criteriaType to set.
     */
    public void setCriteriaType(int criteriaType)
    {
        this.criteriaType = criteriaType;
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
}
