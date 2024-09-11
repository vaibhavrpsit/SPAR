/* ===========================================================================
* Copyright (c) 2004, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/TaxRulesVO.java /main/15 2014/06/10 10:12:42 rahravin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rahravin  06/05/14 - Added setter and getter for geoCode
 *    abondala  09/04/13 - initialize collections
 *    jswan     07/02/12 - Tax cleanup in preparation for JPA conversion.
 *    jswan     06/29/12 - Rename NewTaxRuleIfc to TaxRulesIfc
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:20 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:44 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:30:49  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.4  2004/06/18 18:52:00  jdeleau
 *   @scr 2775 Further updates to the way tax is calculated, correcting table tax calculation errors.
 *
 *   Revision 1.3  2004/06/10 14:21:30  jdeleau
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
package oracle.retail.stores.domain.tax;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * The TaxRules value object is what gets returned when a call to
 * ReadNewTaxRules is made.  If the tax rules were being looked up
 * by geoCode, or by postal code which has a 1-1 relationship to geoCode,
 * then the geoCodeVO array is empty and the taxRules are populated.
 * If there is more than one geoCode for a particular postal code, and the
 * tax rules were being looked up by PostalCode, then the array of geoCodesVO objects
 * will contain data, and taxRules will be an array of size 0.
 * $Revision: /main/15 $
 */
public class TaxRulesVO implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7393844723213290505L;

    /**
     * Tax rules read in from the database.  This is an array of arrays.
     * Initialized with 0 rows.
     */
    HashMap<Integer, List<TaxRuleIfc>> taxRules = new HashMap<Integer, List<TaxRuleIfc>>(0);
    
    /**
     * List of geoCodes that a user must choose from, when
     * tax rules could not be found based on postal code alone.
     */
    GeoCodeVO[] geoCodes = new GeoCodeVO[0];
    
    /** geoCode */
    protected String geoCode = "";
    
    /**
     *  Default taxRulesVO constructor
     */
    public TaxRulesVO()
    {
    }
    
    /**
     * @return Returns the geoCodes.
     */
    public GeoCodeVO[] getGeoCodes()
    {
        return geoCodes;
    }
    
    /**
     * @param geoCodes The geoCodes to set.
     */
    public void setGeoCodes(GeoCodeVO[] geoCodes)
    {
        this.geoCodes = geoCodes;
    }

    /**
     * Set the tax rules for a particular tax group
     *  
     *  @param taxGroupID
     *  @param taxRules
     */
    public void addTaxRules(int taxGroupID, TaxRuleIfc[] taxRules)
    {
        this.taxRules.put(new Integer(taxGroupID), Arrays.asList(taxRules));
    }
    
    /**
     *  Get the valid tax rules for a particular taxGroup.
     *  
     *  @param taxGroupID
     *  @return array of tax rules for the given taxGroupId
     */
    public TaxRuleIfc[] getTaxRules(int taxGroupID)
    {
        List<TaxRuleIfc> taxRules = this.taxRules.get(taxGroupID);
        if(taxRules == null)
        {
            taxRules = new ArrayList<TaxRuleIfc>();
        }
        return taxRules.toArray(new TaxRuleIfc[0]);
    }
    
    /**
     * Get all the tax rules, regardless of taxGroupId
     *  
     *  @return array of all tax rules
     */
    public TaxRuleIfc[] getAllTaxRules()
    {
        // Collection of arrays
        ArrayList<TaxRuleIfc> allRules = new ArrayList<TaxRuleIfc>();
        Collection<List<TaxRuleIfc>> taxRules = this.taxRules.values();
        Iterator<List<TaxRuleIfc>> iter = taxRules.iterator();
        while(iter.hasNext())
        {
            allRules.addAll(iter.next());
        }
        return (TaxRuleIfc[]) allRules.toArray(new TaxRuleIfc[0]);
    }
    
    /**
     * Determine if the value object contains any tax rules.
     *  
     *  @return true if there are tax rules, otherwise false.
     */
    public boolean hasTaxRules()
    {
        return this.taxRules.size() > 0;
    }
       
    /**
     * Returns the <code>geoCode</code> value.
     * 
     * @return the geoCode
     */
    public String getGeoCode()
    {
        return geoCode;
    }

    /**
     * Sets the <code>geoCode</code> value.
     * 
     * @param geoCode the geoCode to set
     */
    public void setGeoCode(String geoCode)
    {
        this.geoCode = geoCode;
    }
}
