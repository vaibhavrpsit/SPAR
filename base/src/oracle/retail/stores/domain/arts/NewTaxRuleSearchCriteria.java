/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/NewTaxRuleSearchCriteria.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:08 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:45 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.5  2004/06/15 20:41:36  cschellenger
 *   @scr 2775 Tax rules for unknown item
 *
 *   Revision 1.4  2004/06/10 15:34:53  jdeleau
 *   @scr 2775 Make Flat File PLU lookups contain tax rules
 *
 *   Revision 1.3  2004/06/10 14:21:29  jdeleau
 *   @scr 2775 Use the new tax data for the tax flat files
 *
 *   Revision 1.2  2004/06/07 18:19:31  jdeleau
 *   @scr 2775 Add tax Service, Multiple Geo Codes screens
 *
 *   Revision 1.1  2004/06/03 16:22:41  jdeleau
 *   @scr 2775 Initial Drop of send item tax support.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;
/**
 * SearchCriteria object, that defines what kind of tax rules we want to retrieve.  Tax rules
 * are retrieved as a combination of GeoCode/TaxGroupID.
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class NewTaxRuleSearchCriteria implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5613392994980064673L;

    /**
     * A tax rule is retrieved by a geoCode/taxGroupID key.  If
     * the GeoCode is known, searchType is set to this.
     */
    public final static int SEARCH_BY_GEO_CODE = 0;
    
    /**
     * A tax rule is retrieved by a geoCode/taxGroupID key.  If
     * the GeoCode is not known, but postal code is known, search type is set to this.
     */
    public final static int SEARCH_BY_POSTAL_CODE = 1;
    
    /**
     * A tax rule is retrieved by a geoCode/taxGroupID key.  If
     * the storeId is given, geoCode can be retrieved.
     * 
     */
    public final static int SEARCH_BY_STORE = 2;
    
    /**
     * A tax rule is retrieved by a geoCode/taxGroupID key.  If
     * departmentID is given the default taxGroupID can be retrieved.
     * The GeoCode also must be set when using this option
     */
    public final static int SEARCH_BY_DEPARTMENT = 3;
    
    /**
     * Indicate whether to search by geo code or postal code.
     */
    private int searchType = NewTaxRuleSearchCriteria.SEARCH_BY_GEO_CODE;
    
    /**
     * GeoCode which is one half of the tax rules key.
     */
    private String geoCode = null;
    
    /**
     * TaxGroupIDs which is one half of the tax rules key.
     */
    private Set taxGroupIDs = new HashSet();
    
    /**
     * PostalCode which can be used to lookup geoCode.  This is not necessarily
     * a 1-1 mapping, a postal code can have many geo codes.
     */
    private String postalCode = null;
    
    /**
     * StoreId can be used to lookup geoCode, and retrieve tax rules.
     */
    private String storeId = null;
    
    
    private String departmentId = null;
    /**
     * Constructor
     *  
     * @param code either GeoCode or postalCode, depending on searchFlag.
     * @param taxGroupID TaxGroupID to search for tax rules on
     * @param searchType SEARCH_BY_GEO_CODE or SEARCH_BY_POSTAL_CODE.
     */
    public NewTaxRuleSearchCriteria(String code, int taxGroupID, int searchType)
    {
        if(searchType == NewTaxRuleSearchCriteria.SEARCH_BY_GEO_CODE)
        {
            setGeoCode(code);
        }
        else if(searchType == NewTaxRuleSearchCriteria.SEARCH_BY_POSTAL_CODE)
        {
            setPostalCode(code);
        }
        else if(searchType == NewTaxRuleSearchCriteria.SEARCH_BY_STORE)
        {
            setStoreId(code);
        }
        addTaxGroupID(taxGroupID);
        setSearchType(searchType);
    }
    
    /**
     * Constructor
     *  
     * @param code either GeoCode or postalCode, depending on searchFlag.
     * @param taxGroupIDs TaxGroupIDs to search for tax rules on
     * @param searchType SEARCH_BY_GEO_CODE or SEARCH_BY_POSTAL_CODE.
     */
    public NewTaxRuleSearchCriteria(String code, Collection taxGroupIDs, int searchType)
    {
        if(searchType == NewTaxRuleSearchCriteria.SEARCH_BY_GEO_CODE)
        {
            setGeoCode(code);
        }
        else if(searchType == NewTaxRuleSearchCriteria.SEARCH_BY_POSTAL_CODE)
        {
            setPostalCode(code);
        }
        else if(searchType == NewTaxRuleSearchCriteria.SEARCH_BY_STORE)
        {
            setStoreId(code);
        }
        setTaxGroupIDs(taxGroupIDs);
        setSearchType(searchType);
    }
    
    /**
     * Constructor
     * 
     * @param departmentIdValue
     * @param geoCodeValue
     */
    public NewTaxRuleSearchCriteria(String geoCodeValue, String departmentIdValue)
    {
        setDepartmentId(departmentIdValue);
        setGeoCode(geoCodeValue);
        setSearchType(SEARCH_BY_DEPARTMENT);
    }
    
    /**
     * This should only be used when doing flat file searches,
     * where the geoCode is pre-defined by the flat file. Any
     * other usage will result in a DataException if the
     * searchType and either geoCode or storeId are not set.
     *  
     * @param taxGroupIDs
     */
    public NewTaxRuleSearchCriteria(Collection taxGroupIDs)
    {
        setTaxGroupIDs(taxGroupIDs);
    }
    
    
    /**
     * @return Returns the geoCode.
     */
    public String getGeoCode()
    {
        return geoCode;
    }
    
    /**
     * @param geoCode The geoCode to set.
     */
    public void setGeoCode(String geoCode)
    {
        this.geoCode = geoCode;
    }
    
    /**
     * @return Returns the postalCode.
     */
    public String getPostalCode()
    {
        return postalCode;
    }
    
    /**
     * @param postalCode The postalCode to set.
     */
    public void setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
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
     * @return Array of taxGroupIDS.
     */
    public int[] getTaxGroupIDs()
    {
        Iterator iter = this.taxGroupIDs.iterator();
        int[] taxIds = new int[this.taxGroupIDs.size()];
        int i=0;
        while(iter.hasNext())
        {
            Integer taxGroupID = (Integer) iter.next();
            taxIds[i] = taxGroupID.intValue();
            i++;
        }
        return taxIds;
    }
    
    /**
     * @param collection Collection of Integers representing
     * taxGroupIDS.
     */
    public void setTaxGroupIDs(Collection collection)
    {
        if(collection != null)
        {
            this.taxGroupIDs = new HashSet(collection);
        }
        else
        {
            this.taxGroupIDs = new HashSet();
        }
    }
    
    /**
     * Add a taxGroupID to the set of taxGroupIDs being
     * that tax rules are to be retrieved for.
     *  
     *  @param taxGroupID
     */
    public void addTaxGroupID(int taxGroupID)
    {
        this.taxGroupIDs.add(new Integer(taxGroupID));
    }
    
    /**
     * @return Returns the searchType.
     */
    public int getSearchType()
    {
        return searchType;
    }
    
    /**
     * @param searchType The searchType to set.
     */
    public void setSearchType(int searchType)
    {
        this.searchType = searchType;
    }
    
    /**
     * @return Returns the departmentId.
     */
    public String getDepartmentId()
    {
        return departmentId;
    }
    /**
     * @param departmentId The departmentId to set.
     */
    public void setDepartmentId(String departmentId)
    {
        this.departmentId = departmentId;
    }
}
