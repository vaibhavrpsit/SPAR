/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/manager/tax/TaxGroupCalculation.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:51 mszekely Exp $
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
 *    4    360Commerce 1.3         4/3/2008 3:39:10 PM    Christian Greene
 *         Refactor ID_NOT_DEFINED constants into TaxConstantsIfc
 *    3    360Commerce 1.2         3/31/2005 4:30:19 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:42 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:55  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:14:15  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:26:58  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:33  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:38:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:59:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:05:52   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:25:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 16:16:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:38:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.manager.tax;

import java.io.Serializable;

import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.manager.ifc.tax.SalesTaxRateIfc;

//------------------------------------------------------------------------------------
/**
    TaxGroupCalculation associates the tax rates used for calculating group taxes with
    the tax group. TaxGroupCalculations are associated with TaxManager and TaxTechnician
    and are created from the taxSpecScript.
*/
//------------------------------------------------------------------------------------
public class TaxGroupCalculation implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2491643484121932753L;

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        tax group identifier
    */
    protected int taxGroupCalculationId = TaxConstantsIfc.TAX_GROUP_ID_NOT_DEFINED;
    /**
        tax group name
    */
    protected String taxGroupCalculationName = null;
    /**
        description of tax group
    */
    protected String taxGroupCalculationDescription = null;
    /**
        name of parent tax jurisdiction
    */
    protected String taxJurisdictionName;
    
    /**
        collection of SalesTaxRateIfc's used to calculate group taxes
    */
    protected SalesTaxRateIfc[] taxRates = null;
    
    //--------------------------------------------------------------------
    /**
        Default constructor for TaxGroupCalculation
    */
    //--------------------------------------------------------------------
    public TaxGroupCalculation()
    {
        taxGroupCalculationId = TaxConstantsIfc.TAX_GROUP_ID_NOT_DEFINED;
        taxGroupCalculationName = "";
        taxGroupCalculationDescription = "";
        taxJurisdictionName = "";
        taxRates = null;
    }
    
    //--------------------------------------------------------------------
    /**
        Alternate Constructor for TaxGroupCalculation
        @param taxGroupCalculationId - numeric id of tax group
        @param taxGroupCalculationName - name of tax group
        @param taxGroupCalculationDescription - description of tax group
        @param taxJurisdicionName - name of the tax jurisdiction
        @param taxRates - array of SalesTaxRateIfc's used to calculate taxes
    */
    //--------------------------------------------------------------------
    public TaxGroupCalculation(int taxGroupCalculationId, 
                               String taxGroupCalculationName, 
                               String taxGroupCalculationDescription,
                               String taxJurisdictionName, 
                               SalesTaxRateIfc[] taxRates)
    {
        this.taxGroupCalculationId = taxGroupCalculationId;
        this.taxGroupCalculationName = taxGroupCalculationName;
        this.taxGroupCalculationDescription = taxGroupCalculationDescription;
        this.taxJurisdictionName = taxJurisdictionName;
        this.taxRates = taxRates;
    }
    
    //--------------------------------------------------------------------
    /**
        Set the tax group id
        @param taxGroupCalculationId - numeric id for tax group
    */
    //--------------------------------------------------------------------
    public void setTaxGroupCalculationId(int taxGroupCalculationId)
    {
        this.taxGroupCalculationId = taxGroupCalculationId;
    }
    
    //--------------------------------------------------------------------
    /**
        Returns the numeric tax group id
        @return tax group id
    */
    //--------------------------------------------------------------------
    public int getTaxGroupCalculationId()
    {
        return this.taxGroupCalculationId;
    }
    
    //--------------------------------------------------------------------
    /**
        Sets the tax group name
        @param taxGroupCalculationName - name of the tax group
    */
    //--------------------------------------------------------------------
    public void setTaxGroupCalculationName(String taxGroupCalculationName)
    {
        this.taxGroupCalculationName = taxGroupCalculationName;
    }
    
    //--------------------------------------------------------------------
    /**
        Return the name of the tax group
        @return name of the tax group
    */
    //--------------------------------------------------------------------
    public String getTaxGroupCalculationName()
    {
        return this.taxGroupCalculationName;
    }
    
    //--------------------------------------------------------------------
    /**
        Set the tax group description
        @param taxGroupCalculationDescription - description of tax group
    */
    //--------------------------------------------------------------------
    public void setTaxGroupCalculationDescription(String taxGroupCalculationDescription)
    {
        this.taxGroupCalculationDescription = taxGroupCalculationDescription;
    }
    
    //--------------------------------------------------------------------
    /**
        Return the description of the tax group
        @return description of the tax group
    */
    //--------------------------------------------------------------------
    public String getTaxGroupCalculationDescription()
    {
        return this.taxGroupCalculationDescription;
    }
    

    //--------------------------------------------------------------------
    /**
        Set the name of the tax jurisdiction
        @param jurisdictionName - name of the tax jurisdiction
    */
    //--------------------------------------------------------------------
    public void setTaxJurisdictionName(String jurisdictionName)
    {
        this.taxJurisdictionName = jurisdictionName;
    }
    
    //--------------------------------------------------------------------
    /**
        Returns the name of the tax jurisdiction
        @return name of the tax jurisdiction
    */
    //--------------------------------------------------------------------
    public String getTaxJurisdictionName()
    {
        return this.taxJurisdictionName;
    }
    
    //--------------------------------------------------------------------
    /**
        Sets the sales tax rates used in calculating the group tax
        @param taxRates - array of tax rates used to calculate the group tax
    */
    //--------------------------------------------------------------------
    public void setTaxRates(SalesTaxRateIfc[] taxRates)
    {
        this.taxRates = taxRates;
    }
    
    //--------------------------------------------------------------------
    /**
        Returns the array of rates used to calculate the group tax
        @return array of SalesTaxRateIfc
    */
    //--------------------------------------------------------------------
    public SalesTaxRateIfc[] getTaxRates()
    {
        return this.taxRates;
    }
    
    //--------------------------------------------------------------------
    /**
        Returns the effective rate from this tax group calculation 
        given a taxable amount and a specifier for # digits of precision.
        @return double - effective tax rate at n digits precision
    */
    //--------------------------------------------------------------------
    public double getEffectiveRate(double taxableAmount,int precision)
    {
                double rate = 0;
                
                // sum the rates in the tax group    
                for (int i=0; i < taxRates.length; i++)
                {
                    rate += taxRates[i].getTaxRate(taxableAmount);
                }
                            
                double scale = Math.pow(10, precision);
        double temp = rate * scale;
        rate = Math.rint(temp) / scale;
        
        return rate;
    } 
    
    //----------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number.
        <p>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public static String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        return(revisionNumber);
    }                                   // end getRevisionNumber()

    //----------------------------------------------------------------------
    /**
        Returns a human readable string representation of this object.
        <p>
        @return A human readable string representation of this object.
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        // The string to be returned
        String strResult = "Class:  " + getClass().getName() +
            "(Revision " + getRevisionNumber() + ")@" + hashCode();
        
        strResult +="\nTaxGroupCalculationId: " + taxGroupCalculationId;
            strResult += "\nTaxGroupName:" + this.taxGroupCalculationName;
            strResult += "\nTaxJurisdictionName: " + taxJurisdictionName;
            strResult += "\n\nTaxRates:\n";
            
            for (int i = 0; i < taxRates.length; i++)
            {
                strResult += taxRates[i].toString() + "\n";
            }
        
        return strResult;
    }
   
} // end TaxGroupCalculation
