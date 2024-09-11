/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/manager/tax/TaxBucket.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:52 mszekely Exp $
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
 *    4    360Commerce 1.3         4/3/2008 3:38:50 PM    Christian Greene
 *         Refactor ID_NOT_DEFINED constants into TaxConstantsIfc
 *    3    360Commerce 1.2         3/31/2005 4:30:18 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:46 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:40 PM  Robert Pearse   
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
 *    Rev 1.0   Jun 03 2002 16:59:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:05:48   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:25:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 16:16:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:38:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.manager.tax;

import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.manager.ifc.tax.TaxBucketIfc;

//------------------------------------------------------------------------
/**
    Tax Bucket is a utility class implementing the TaxBucketIfc. TaxBucket
    provides a lightweight class for holding tax information.
*/
//-------------------------------------------------------------------------
public class TaxBucket implements TaxBucketIfc
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /**
        identifier of the tax group
    */
    protected int taxGroupId;

    /**
        name of the tax group
    */
    protected String taxGroupName;
    
    /**
        quantity of items
    */
    protected long quantity;
    
    /**
        amount 
    */
    protected long amount;
    
    /**
        mode of bucket
    */
    protected int taxMode = 0;
    
    /**
      standard applied tax based on total amount
    */
    protected long standardAmountTax;
    
    /**
      standard quantity tax based on quantity
    */
    protected long standardQuantityTax;
    
    /**
      override tax on amount
    */
    protected long overrideQuantityTax;
    
    /**
      override tax on quantity
    */
    protected long overrideAmountTax;
    
    /**
      number of digits to support
    */
    protected int digits = 2;
    
    /**
      conversion factor equal to 10 ^ digits
    */
    protected double scalefactor = 100;
    
    //---------------------------------------------------------------
    /**
        Default constructor for TaxBucket
    **/
    //---------------------------------------------------------------

    public TaxBucket()
    {
        taxGroupName = "";
        taxGroupId = TaxConstantsIfc.TAX_GROUP_ID_NOT_DEFINED;
        this.quantity = 0;
        this.amount = 0;
        clearTaxAmounts();
    }
    
    //---------------------------------------------------------------
    /**
        Construct a new instance of a TaxBucket
        @param taxGroupName - name of the associated tax group
        @param taxGroupId - identifier of the tax group
        @param quantity - number of items
        @param amount - net extended amount
        @param mode - tax mode
        @param overrideAmount - override tax on amount
        @param overrideQuantity - override tax on quantity
    **/
    //---------------------------------------------------------------
    public TaxBucket(String taxGroupName, int taxGroupId, long quantity, double amount, 
                         int mode, double overrideAmount, double overrideQuantity)
    {
        this.taxGroupName = taxGroupName;
        this.taxGroupId = taxGroupId;
        this.quantity = quantity;
        this.amount = Math.round((amount*quantity)* scalefactor);
        this.taxMode = mode;
        this.overrideAmountTax = Math.round(overrideAmount * scalefactor);
        this.overrideQuantityTax = Math.round(overrideQuantity * scalefactor);
    }
    
    //-------------------------------------------------------------------------------------
    // TaxBucketIfc methods
    //-------------------------------------------------------------------------------------
    
    //---------------------------------------------------------------
    /**
        Return the name of the tax group.
        @return name of the associated tax group
    **/
    //---------------------------------------------------------------
    public String getTaxGroupName()
    {
        return this.taxGroupName;
    }

     
    //---------------------------------------------------------------
    /**
        Set the name of the tax group.
        @param name of the associated tax group
    **/
    //---------------------------------------------------------------
     public void setTaxGroupName(String name)
     {
        this.taxGroupName = name;
     }
    
    //---------------------------------------------------------------
    /**
        Numeric id of the tax group.
        @return int id of the associated tax group
    **/
    //---------------------------------------------------------------
    public int getTaxGroupId()
    {
        return this.taxGroupId;
    }
    
    //---------------------------------------------------------------
    /**
        Return the quantity of items associated with the tax bucket.
        @return quantity of items
    **/
    //---------------------------------------------------------------  
    public long getTaxableQuantity()
    {
        return this.quantity;
    }
    
    //---------------------------------------------------------------
    /**
        Return the net extended taxable amount.
        @return net extended mount
    **/
    //---------------------------------------------------------------    
    public double getTaxableAmount()
    {
        return (double)amount / scalefactor;
    }
    
    //---------------------------------------------------------------
    /**
        Return the tax mode of the bucket. Use the constants as defined
        in TaxIfc.
        @return mode of the tax bucket
    **/
    //---------------------------------------------------------------    
    public int getTaxMode()
    {
        return this.taxMode;
    }
    
    //---------------------------------------------------------------
    /**
        Returns the amount of total tax applied to the bucket. Total
        tax is comprised of standard amount tax, standard quantity tax,
        override amount tax, and override quantity tax applied by the
        tax calculations.
        @return amount of total tax applied to this bucket.
    **/
    //---------------------------------------------------------------
    public double getTotalTax()
    {
        return (double) (standardAmountTax + standardQuantityTax + overrideAmountTax + overrideQuantityTax) / scalefactor;
    }

    //---------------------------------------------------------------
    /**
       Set the amount of tax applied using standard tax calculations on
       the amount of the item(s) associated with the bucket.
       @param amountTax - standard tax applied to the bucket
    **/
    //---------------------------------------------------------------
    public void setStandardAmountTax(double taxAmount)
    {
         this.standardAmountTax = Math.round(taxAmount * scalefactor);
    }

    
    //---------------------------------------------------------------
    /**
       Increase/Decrease the amount of tax applied using standard tax 
       calculations on the amount of the item(s) associated with the bucket.
       @param amountTax - standard tax applied to the bucket
    **/
    //---------------------------------------------------------------
    public void modifyStandardAmountTax(double taxAmount)
    {
        this.standardAmountTax += Math.round(taxAmount * scalefactor);
    }
    
    //---------------------------------------------------------------
    /**
       Get the amount of tax applied using standard tax calculations on
       the amount of the item(s) associated with the bucket.
       @return standard tax applied to the bucket based on item(s) amount
    **/
    //---------------------------------------------------------------
    public double getStandardAmountTax()
    {
        return (double)standardAmountTax / scalefactor;
    }


    //---------------------------------------------------------------
    /**
       Set the amount of tax applied using standard tax calculations on
       the quantity of the item(s) associated with the bucket.
       @param quantityTax - standard tax applied to the bucket
    
    **/
    //---------------------------------------------------------------
    public void setStandardQuantityTax(double quantityTax)
    {
        this.standardQuantityTax = Math.round(quantityTax * scalefactor);
    }
    
    //---------------------------------------------------------------
    /**
       Increase/Decrease the amount of tax applied using standard tax 
       calculations on the quantity of the item(s) associated with the bucket.
       @param amountTax - standard tax applied to the bucket
    **/
    //---------------------------------------------------------------
    public void modifyStandardQuantityTax(double taxAmount)
    {
        this.standardQuantityTax += Math.round(taxAmount * scalefactor);
    }

    //---------------------------------------------------------------
    /**
       Get the amount of tax applied using standard tax calculations on
       the quantity of the item(s) associated with the bucket.
       @return standard tax applied to the bucket based on quantity of item(s)
    **/
    //---------------------------------------------------------------    
    public double getStandardQuantityTax()
    {
        return (double)standardQuantityTax / scalefactor;
    }
    
    //---------------------------------------------------------------
    /**
        Sets the amount of override tax on the extended amount of the
        items.
        @param overrideAmount - amount of tax
    
    **/
    //---------------------------------------------------------------
    
    public void setOverrideAmountTax(double overrideAmount)
    {
        this.overrideAmountTax = Math.round(overrideAmount*scalefactor);
    }

    //---------------------------------------------------------------
    /**
        Returns the amount of override Tax calculated on the net extended 
        amount of the items.
        @return amount of tax
    
    **/
    //---------------------------------------------------------------
    public double getOverrideAmountTax()
    {
        return (double)overrideAmountTax / scalefactor;
    }

    //---------------------------------------------------------------
    /**
       Increase/Decrease the amount of tax applied override standard tax 
       calculations on the amount of the item(s) associated with the bucket.
       @param amountTax - override tax applied to the bucket
    **/
    //---------------------------------------------------------------
    public void modifyOverrideAmountTax(double taxAmount)
    {
        this.overrideAmountTax += Math.round(taxAmount * scalefactor);
    }

    //---------------------------------------------------------------
    /**
        Sets the amount of override tax on the quantity of items.
        @param overrideQuantity - amount of tax
    **/
    //---------------------------------------------------------------
    public void setOverrideQuantityTax(double overrideQuantity)
    {
        this.overrideQuantityTax = Math.round(overrideQuantity * scalefactor);
    }
    
    //---------------------------------------------------------------
    /**
       Increase/Decrease the amount of tax applied using override tax 
       calculations on the quantityt of the item(s) associated with the bucket.
       @param amountTax - override tax applied to the bucket
    **/
    //---------------------------------------------------------------
    public void modifyOverrideQuantityTax(double taxAmount)
    {
        this.overrideQuantityTax += Math.round(taxAmount * scalefactor);
    }

    //---------------------------------------------------------------
    /**
        Returns the amount of override Tax calculated on the quantity
        of items.
        @return amount of tax
    **/
    //---------------------------------------------------------------
    public double getOverrideQuantityTax()
    {
        return (double)overrideQuantityTax / scalefactor;
    }
    
    //---------------------------------------------------------------
    /**
        Called if an override flag is set. The bucket needs to determine
        the appropriate override taxes.
    **/
    //---------------------------------------------------------------
     public void calculateOverrideTax()
     {
     }
     
    //---------------------------------------------------------------
    /**
        Clear the tax amounts within the bucket in preparation for 
        new tax calculations
    **/
    //---------------------------------------------------------------
     public void clearTaxAmounts()
     {
        this.standardAmountTax = 0;
        this.standardQuantityTax = 0;
        this.overrideAmountTax = 0;
        this.overrideQuantityTax = 0;
     }
    
    
    
     
     //==================================================================================
     
     
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
        
        strResult += "TaxBucket: " + this.taxGroupId;
        strResult += "\n\t\t\t\t\t\tAmount\t\tQuantity";
        strResult += "\nStandard Taxes:\t" + convertToDouble(standardAmountTax) + "\t" + convertToDouble(standardQuantityTax);
        strResult += "\nOverride Taxes:\t" + convertToDouble(overrideAmountTax) + "\t" + convertToDouble(overrideQuantityTax);
        strResult += "\nTotal Taxes:   \t:" + convertToDouble(getTotalAmountTax()) + "\t" + convertToDouble(getTotalQuantityTax());
        
        return strResult;
        
     }
     
    //---------------------------------------------------------------
    /**
        protected utiltity function to convert a long value to a scaled
        double.
        @param val - long value to be converted
        @return long value shifted to support the desired number of digits
    */
    //---------------------------------------------------------------
     protected double convertToDouble(long val)
     {
        return (double)val / scalefactor;
     }
     
    //---------------------------------------------------------------
    /**
        Return the total tax based on the amount of purchase
        @return long value for sum of standard amount tax and 
                the override amount tax
    */
    //---------------------------------------------------------------
     protected long getTotalAmountTax()
     {
        return standardAmountTax + overrideAmountTax;
     }
     
    //---------------------------------------------------------------
    /**
        Returns the total tax based on quantity of items purchased
        @return sum of standard quantity tax and override quantity tax
    */
    //---------------------------------------------------------------
     protected long getTotalQuantityTax()
     {
        return standardQuantityTax + overrideQuantityTax;
     }
}


