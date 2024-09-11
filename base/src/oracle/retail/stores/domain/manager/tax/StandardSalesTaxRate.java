/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/manager/tax/StandardSalesTaxRate.java /main/12 2014/07/22 16:26:18 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  07/22/14 - Fortify null deference fix for POS v14.1 phase II
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    mchellap  09/09/11 - Removed test main method to fix path manipulation
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/3/2008 3:38:29 PM    Christian Greene
 *         Refactor ID_NOT_DEFINED constants into TaxConstantsIfc
 *    4    360Commerce 1.3         1/22/2006 11:41:43 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:30:09 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:22 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:30:55  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.6  2004/04/09 16:55:48  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:43  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:54  rhafernik
 *   @scr 0 log4j conversion
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
 *    Rev 1.0   Jun 03 2002 16:59:34   msg
 * Initial revision.
 * 
 *    Rev 1.3   25 Apr 2002 10:28:08   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 * 
 *    Rev 1.2   Apr 02 2002 18:58:32   mpm
 * Corrected instantiation, cloning of BigDecimal.
 * Resolution for Domain SCR-46: Correct initialization of BigDecimal objects
 *
 *    Rev 1.1   Mar 18 2002 23:05:46   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:25:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Feb 05 2002 16:36:06   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 *
 *    Rev 1.0   Sep 20 2001 16:16:52   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:38:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.manager.tax;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.manager.ifc.tax.SalesTaxRateIfc;
import oracle.retail.stores.domain.manager.ifc.tax.TaxBucketIfc;
import oracle.retail.stores.domain.manager.ifc.tax.TaxSpecIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLUtility;
//--------------------------------------------------------------------------
/**
  Performs basic sales tax calculations based on rates and lookup tables.
  Calculates amount taxes based total taxable amounts allocates across the
  sources. Quantity taxes are calculated based on individual source quantities
  and total tax based on quantities is the sum of the individual amounts.
  Calculations are performed using either rates or lookup tables.
*/
//--------------------------------------------------------------------------
public class StandardSalesTaxRate implements SalesTaxRateIfc, TaxConstantsIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 3886346011327220628L;

    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.manager.tax.StandardSalesTaxRate.class);

    /**
       Toggles system output on/off for debugging
    */
    protected static final boolean DEBUG = false;

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
       Internal constants used for array indexes
    */
    protected static final int STDTAXABLEAMOUNT = 0;
    protected static final int STDTAXABLEQTY = 1;
    protected static final int STDAMOUNTTAX = 2;
    protected static final int STDQTYTAX = 3;
    protected static final int OVRAMOUNTTAX = 4;
    protected static final int OVRQTYTAX = 5;

    /**
       numeric identifier of the tax rate
    */
    protected int taxRateId;
    /**
       name of the tax rate
    */
    protected String taxRateName;
    /**
       description of the tax rate
    */
    protected String taxRateDescription;
    /**
       percent rate to apply to the amount
    */
    protected double amtTaxRate = 0;
    /**
       rate to apply to quantity of items
    */
    protected double qtyTaxRate = 0;
    /**
       Lookup table for amount based taxes
    */
    protected Vector amountTable = null;
    /**
       Lookup table for quantity based taxes
    */
    protected Vector quantityTable = null;
    /**
       Number of digits to use for double values
    */
    protected int digits = 2;
    /**
       Scale factor to convert doubles to longs and longs to doubles
    */
    protected double scaleFactor = 100;

    //--------------------------------------------------------------------------
    /**
       Constructor for the StandardSalesTaxRate class.
       @param taxRateId - numeric identifier for the tax rate
       @param taxRateName - name of the tax rate
       @param taxRateDescription - description of the taxRate
       @param amtTaxRate - percentage rate to apply to total amount
       @param qtyTaxRate - amount to apply to each unit

    */
    //--------------------------------------------------------------------------
    public StandardSalesTaxRate(int taxRateId, String taxRateName, String taxRateDescription,
                                double amtTaxRate, double qtyTaxRate)
    {
        this.taxRateId = taxRateId;
        this.taxRateName = taxRateName;
        this.taxRateDescription = taxRateDescription;
        this.amtTaxRate = amtTaxRate;
        this.qtyTaxRate = qtyTaxRate;

    }

    public StandardSalesTaxRate()
    {
        this.taxRateId = TAX_RATE_ID_NOT_DEFINED;
        this.taxRateName = "";
        this.taxRateDescription = "";
        this.amtTaxRate = 0.0;
        this.qtyTaxRate = 0.0;
    }

    //============================================================================
    //SalesTaxCalculationIfc members
    //============================================================================
    /**
       Returns the tax rate value
       @return numeric rate used for tax calculations
    */
    //----------------------------------------------------------------------------
    public BigDecimal getTaxRate(BigDecimal taxableAmount)
    {
        BigDecimal rate = BigDecimal.ZERO;

        if (taxableAmount.signum() == 0)
        {
            return rate;
        }

        if (amountTable == null)
        {
            rate = new BigDecimal(amtTaxRate); //??? .setScale(4,BigDecimal.ROUND_HALF_UP);
        }
        else
        {
            double  dTaxable    = taxableAmount.doubleValue();
            long    lTaxable    = convertToLong(dTaxable,digits);
            long    lTax        = lookupAmountTax(lTaxable);

            BigDecimal bdTax = new BigDecimal(new BigInteger(Long.toString(lTax)),2);

            rate =  bdTax.divide(
                                 taxableAmount,
                                 4, BigDecimal.ROUND_HALF_UP);
        }

        return rate;
    }

    /**
       Returns the tax rate value
       @return numeric rate used for tax calculations
       @deprecated use BigDecimal getTaxRate(BigDecimal b) instead
    */
    //----------------------------------------------------------------------------
    public double getTaxRate(double taxableAmount)
    {
        double rate = 0.0;

        if (taxableAmount == 0)
        {
            return rate;
        }

        if (amountTable == null)
        {
            rate = amtTaxRate;
        }
        else
        {
            long    lTaxableAmount  = convertToLong(taxableAmount,digits);
            long    lTax            = lookupAmountTax(lTaxableAmount);

            rate = round(
                         ((double)lTax/(double)lTaxableAmount),10
                         );
        }

        return rate;
    }
    //----------------------------------------------------------------------------
    /**
       Returns the numeric id of the tax group
       @return numeric identifier of the tax group
    */
    //----------------------------------------------------------------------------
    public int getTaxRateId()
    {
        return taxRateId;
    }


    //----------------------------------------------------------------------------
    /**
       Sets the numeric id of the tax group.
       @param taxRateId
    */
    //----------------------------------------------------------------------------

    public void setTaxRateId(int taxRateId)
    {
        this.taxRateId = taxRateId;
    }

    //----------------------------------------------------------------------------
    /**
       Returns the name of the tax rate
       @return name of tax rate
    */
    //----------------------------------------------------------------------------
    public String getTaxRateName()
    {
        return this.taxRateName;
    }

    //----------------------------------------------------------------------------
    /**
       Sets the name of the tax rate.
       @param taxGroupName
    */
    //----------------------------------------------------------------------------
    public void setTaxRateName(String taxRateName)
    {
        this.taxRateName = taxRateName;
    }

    //----------------------------------------------------------------------------
    /**
       Returns the tax rate description
       @return description of tax rate
    */
    //----------------------------------------------------------------------------
    public String getTaxRateDescription()
    {
        return taxRateDescription;
    }

    //----------------------------------------------------------------------------
    /**
       Sets the tax rate description
       @param taxRateDescription - description of tax rate
    */
    //----------------------------------------------------------------------------
    public void setTaxRateDescription(String taxRateDescription)
    {
        this.taxRateDescription = taxRateDescription;
    }

    //----------------------------------------------------------------------------
    /**
       Calculates the sales tax based on the information from the source buckets and
       places the amounts in the destination bucket. The tax amount is allocated across
       the individual source buckets based after the total tax is calculated. If the
       overrideAsGroup flag is set, calculations will only be performed for buckets with
       a standard tax mode set. If the overrideAsGroup flag is not set, all source
       buckets will be used in the calculations.
       @param destinationBucket - output for the tax calculations
       @param sourceBuckets - collection of TaxBucketIfc elements providing the input
       information for the tax calculations
       @param overrideAsGroup - If true, do not calculate override amounts within the
       rate calculations
       @exception TaxCalculationException is thrown if a calculation error occurs
    */
    //----------------------------------------------------------------------------

    public void calculateSalesTax(TaxBucketIfc destinationBucket, Vector sourceBuckets,
                                  boolean overrideAsGroup) throws TaxCalculationException
    {
        int taxGroupId = destinationBucket.getTaxGroupId();
        long[] taxAmounts = new long[6];
        Vector groupStd = new Vector();

        // Iterate through the source buckets and perform the tax calculations
        // calculation method is dependent on the tax mode and overrideAsGroup parameter

        for (int i = 0; i < sourceBuckets.size(); i ++)
        {
            try
            {
                TaxBucketIfc source = (TaxBucketIfc) sourceBuckets.elementAt(i);
                if (source.getTaxGroupId() == taxGroupId)
                {

                    switch (source.getTaxMode())
                    {
                        case TAX_MODE_TOGGLE_ON:    //Need to check on this
                        case TAX_MODE_STANDARD:
                        case TAX_MODE_RETURN_RATE:
                        {
                            //Amount is calculated on overall amount
                            taxAmounts[STDTAXABLEAMOUNT]+= convertToLong(source.getTaxableAmount(), digits);

                            //Calculate tax on quantity
                            calculateStandardQuantityTax(source);
                            taxAmounts[STDQTYTAX] += convertToLong(source.getStandardQuantityTax(), digits);
                            groupStd.addElement(source);
                            break;
                        }
                        case TAX_MODE_TOGGLE_OFF:   // Need to check on this
                        case TAX_MODE_EXEMPT:
                        case TAX_MODE_NON_TAXABLE:
                        {
                            // Don't add to tax totals
                            break;
                        }
                        case TAX_MODE_OVERRIDE_AMOUNT:
                        case TAX_MODE_OVERRIDE_RATE:
                        {
                            if (!overrideAsGroup)
                            {
                                //Calculate override taxes only if not calculated as group
                                source.calculateOverrideTax();
                                taxAmounts[OVRAMOUNTTAX] += convertToLong(source.getOverrideAmountTax(), digits);
                                taxAmounts[OVRQTYTAX] += convertToLong(source.getOverrideQuantityTax(), digits);
                            }
                            break;
                        }

                        default:
                        {
                            break;
                        }
                    }
                } // end switch
            }  // end if

            catch (Exception ecalc)
            {
                logger.error( "" + ecalc.toString() + "");
                throw new TaxCalculationException(TaxCalculationException.EXCEPTION_DURING_CALC, ecalc.toString());
            }
        } // end for

        //Calculate the amount of tax on the total amount
        taxAmounts[STDAMOUNTTAX] = calculateStandardAmountTax(taxAmounts[STDTAXABLEAMOUNT]);


        allocateStandardAmountTax(groupStd,taxAmounts[STDTAXABLEAMOUNT], taxAmounts[STDAMOUNTTAX]);

        //Update the destination bucket with the tax amounts
        destinationBucket.modifyStandardAmountTax(convertToDouble(taxAmounts[STDAMOUNTTAX], digits));
        destinationBucket.modifyStandardQuantityTax(convertToDouble(taxAmounts[STDQTYTAX], digits));
        destinationBucket.modifyOverrideAmountTax(convertToDouble(taxAmounts[OVRAMOUNTTAX], digits));
        destinationBucket.modifyOverrideQuantityTax(convertToDouble(taxAmounts[OVRQTYTAX], digits));

        if (DEBUG)
        {
            //TEST CODE
            System.out.println("-------------------------------------------------------");
            System.out.println("After tax calc for " + this.taxRateId + ":" + this.taxRateDescription);
            System.out.println(destinationBucket.toString());
            System.out.println("-------------------------------------------------------");
        }
    }
    //============================================================================
    // End SalesTaxCalculationIfc members
    //============================================================================

    //---------------------------------------------------------------------------------
    // TaxCalculation Methods
    //---------------------------------------------------------------------------------


    //----------------------------------------------------------------------------------
    /**
       Calculate the standard quantity tax. If a lookup table is available, pass the
       paramaters to the lookup method. Added the calculation results to the source bucket.
       @param source - the TaxBucketIfc to calculate the tax
    */
    //----------------------------------------------------------------------------------
    protected void calculateStandardQuantityTax(TaxBucketIfc source)
    {
        if (this.quantityTable != null)
        {
            source.modifyStandardQuantityTax(lookupQuantityTax(source.getTaxableQuantity()));
        }
        else
        {
            // Calculate simple quantity tax by multiplying quantity by rate
            source.modifyStandardQuantityTax(source.getTaxableQuantity()*this.qtyTaxRate);
        }
    }

    //----------------------------------------------------------------------------------
    /**
       Performs the standard amount tax calculation. There are two different options
       for calculating the tax, simple percentage and lookup table. The options are
       specified in the configuration script under the elements for the tax rate.
       @param amountTaxable - amount to apply the taxes on
       @return calculated taxes on the amount
    */
    //----------------------------------------------------------------------------------
    protected long calculateStandardAmountTax(long amountTaxable)
    {
        //Two possible calculation methods, by lookup or simple rate.

        if (amountTable != null)
        {
            if (amountTaxable < 0)
            {
                amountTaxable *= -1;
                return (-1 * lookupAmountTax(amountTaxable));
            }

            return lookupAmountTax(amountTaxable);
        }
        else
        {
            // Simple percentage is multipy amount by rate
            long temp = Math.round(amtTaxRate* amountTaxable);
            return temp;
        }
    }

    //----------------------------------------------------------------------------------
    /**
       Allocates the total standard amount tax calculated across the various buckets.
       @param buckets - collection of TaxBucketIfc which were used in calculation the
       total standard amount tax
       @param totalAmount - total amount used to calculate the standard amount tax
       @param totalTax - total standard tax amount
    */
    //----------------------------------------------------------------------------------
    protected void allocateStandardAmountTax(Vector buckets,long totalAmount, long totalTax)
    {
        long prevAlloc = 0;
        long targetAllocation;
        long unitAlloc;
        long runningTotal = 0;

        // Need to allocate the standard amount tax across the individual source buckets so
        // that the sum of the buckets is equal to the total applied amount tax.
        // The amount allocated to the bucket is the the difference of the target allocation and
        // the amount previously allocated to the source buckets. The target allocation is
        // the running total of the amount buckets divided by the total amount

        for (int i = 0; i < buckets.size(); i++)
        {
            TaxBucketIfc bucket = (TaxBucketIfc)buckets.elementAt(i);
            runningTotal += convertToLong(bucket.getTaxableAmount(), digits);

            double percent = (double)runningTotal / (double)totalAmount;
            double target = (double)totalTax * percent;
            targetAllocation = Math.round(target);

            unitAlloc = targetAllocation - prevAlloc;

            bucket.modifyStandardAmountTax(convertToDouble(unitAlloc, digits));
            prevAlloc = targetAllocation;

            if (DEBUG)
            {
                System.out.println("**************************************");
                System.out.println(convertToDouble(totalAmount, digits));
                System.out.println(convertToDouble(totalTax, digits));
                System.out.println(convertToDouble(runningTotal, digits));
                System.out.println(convertToDouble(targetAllocation, digits));
                System.out.println(convertToDouble(unitAlloc, digits));
                System.out.println(convertToDouble(prevAlloc, digits));
                System.out.println("**************************************");
            }
        }

        if (DEBUG)
        {
            long taxTest = 0;
            for (int i = 0; i < buckets.size(); i++)
            {
                TaxBucketIfc bucket = (TaxBucketIfc)buckets.elementAt(i);
                taxTest += convertToLong(bucket.getStandardAmountTax(), digits);
            }

            System.out.println("//////////////////////////////////////////");
            System.out.println("Completed Allocated Taxes:");
            System.out.println(convertToDouble(totalTax, digits));
            System.out.println(convertToDouble(taxTest, digits));
            System.out.println("//////////////////////////////////////////");
        }

    }

    //----------------------------------------------------------------------------------
    /**
       Uses a lookup table to calculate the amount of taxes. If the amount is greater than
       the highest lookup cell, the amount is divided by the highest lookup amount specified.
       The integer portion of the result, n,is multiplied by the tax amount specified for the
       highest range. The remainder of the tax is calculated by locating the lookup cell whose
       range contains the the difference of the amount and n times the amount. This tax is added
       to the amount calculated for n times the high range amount.
       @param amount - amount to apply tax on.
       @return tax amount based on the lookup calculations
    */
    //----------------------------------------------------------------------------------

    protected long lookupAmountTax(long amount)
    {
        long balance = amount;
        long tax = 0;

        TaxLookup upperLookup = (TaxLookup) amountTable.lastElement();
        int timesMax = (int)( amount / upperLookup.getUpper());
        tax = timesMax * upperLookup.getTaxRate();
        balance = amount - timesMax * upperLookup.getUpper();

        for (int i = 0; i < amountTable.size(); i++)
        {
            TaxLookup current = (TaxLookup) amountTable.elementAt(i);
            if ((balance >= current.getLower()) && (balance <= current.getUpper()))
            {
                balance = 0;
                tax += current.getTaxRate();
                return tax;
            }
        }
        return tax;
    }

    //----------------------------------------------------------------------------------
    /**
       Calculates the quantity tax based on a lookup table. If the quantity exceeds the
       upper range, the amount specified in the upper range is applied to the input quantity
       parameter.
       @param quantity - number of units to apply the tax
       @return calculated quantity tax based on the lookup table
    */
    //----------------------------------------------------------------------------------
    protected long lookupQuantityTax(double quantity)
    {
        double amount = 0.0;
        TaxLookup last = (TaxLookup) quantityTable.lastElement();
        TaxLookup current = null;

        if (quantity >= last.getUpper())
        {
            amount = quantity * last.getTaxRate();
        }
        else
        {
            for (int i = 0; i < quantityTable.size(); i++)
            {
                current = (TaxLookup) quantityTable.elementAt(i);
                if (quantity >= current.getLower() && quantity <= current.getUpper())
                {
                    amount = quantity * current.getTaxRate();
                    break;
                }
            }
        }
        if (DEBUG)
        {
            System.out.println("QuantityTax: " + amount + " for " + quantity + " units");
        }
        return convertToLong(amount, digits);
    }

    //----------------------------------------------------------------------------------
    /**
       Configures the tax rate using information provided in a TaxSpecScript file.
       This node contains the rate and lookup table information
       @param configNode - a node of type TaxSpecIfc.ELEM_STDTAXCONFIG. The dtd file
       is located in the oracle.retail.stores.foundation.tour.dtd package
    */
    //----------------------------------------------------------------------------------

    public boolean configure(Element configNode) throws TaxCalculationException
    {
        try
        {
            configureRate(configNode);
            return true;
        }
        catch (TaxCalculationException etax)
        {
            if (DEBUG)
            {
                etax.printStackTrace();
                System.out.println(etax);
            }
            throw etax;
        }

        catch (Exception e)
        {
            if (DEBUG)
            {
                e.printStackTrace();
                System.out.println(e);
            }

            return false;
        }
    }
    //---------------------------------------------------------------------------
    /**
       Configures the tax rates and lookup tables from the byte array passed into the
       initialization method
       @param  xmlConfig root element of the XML tree
    */
    //---------------------------------------------------------------------------
    protected void configureRate(Element xmlConfig) throws TaxCalculationException
    {
        //  Get the document element for the XML file that will be
        //  used for searching the file structure
        //Element xmlRoot = (Element) xmlConfig.getDocumentElement();
        // The Element should be a STDTAXCONFIG

        if (!xmlConfig.getTagName().equalsIgnoreCase(TaxSpecIfc.ELEM_STDTAXCONFIG))
        {
            // should throw exception here
            if (DEBUG) System.out.println("StandardSalesTaxRate.configureRate - invalid configuration node " + xmlConfig.getTagName());
            throw new TaxCalculationException(TaxCalculationException.INVALID_CONFIGURATION_NODE,
                                              "Element node was not named " + TaxSpecIfc.ELEM_STDTAXCONFIG
                                              + ", encountered node name " + xmlConfig.getTagName());
        }

        Element [] xmlAmountRate = XMLUtility.getChildElements(xmlConfig, TaxSpecIfc.ELEM_TAXAMOUNTRATE);
        Element [] xmlQuantityRate = XMLUtility.getChildElements(xmlConfig, TaxSpecIfc.ELEM_TAXQUANTITYRATE);

        Element [] xmlAmountTable = XMLUtility.getChildElements(xmlConfig, TaxSpecIfc.ELEM_TAXAMOUNTTABLE);
        Element [] xmlQuantityTable = XMLUtility.getChildElements(xmlConfig, TaxSpecIfc.ELEM_TAXQUANTITYTABLE);

        if (xmlAmountRate.length >= 1)
        {
            String amountRate = xmlAmountRate[0].getAttribute(TaxSpecIfc.ATTR_AMOUNTRATE);
            this.amtTaxRate = (new Double(amountRate)).doubleValue();
        }
        else
        {

            if (xmlAmountTable.length > 0)    //else   // this is a lookup table
            {
                this.amountTable = new Vector();
                Element[] taxAmountLookups = XMLUtility.getChildElements(xmlAmountTable[0], TaxSpecIfc.ELEM_TAXAMOUNTLOOKUP);
                buildAmountLookupTable(taxAmountLookups);
            }
        }

        if (xmlQuantityRate.length >= 1)
        {
            String quantityRate = xmlAmountRate[0].getAttribute(TaxSpecIfc.ATTR_QUANTITYRATE);
            if(quantityRate != null)
            {
                if (quantityRate.length() == 0)
                {
                    this.qtyTaxRate = 0.0;
                }
                else
                {
                    try
                    {
                        this.qtyTaxRate = (new Double(quantityRate)).doubleValue();
                    }
                    catch (Exception e)
                    {
                        throw new TaxCalculationException(TaxCalculationException.INVALID_CONFIGURATION_INFO,
                                "StandardSalesTaxRate.configureRate() for " + taxRateId + " " + taxRateName
                                        + " invalid quantity tax rate " + quantityRate);
                    }
                }
            }
        }
        else
        {
            if (xmlQuantityTable.length > 0)
            {
                this.quantityTable = new Vector();
                Element[] taxQuantityLookups = XMLUtility.getChildElements(xmlQuantityTable[0], TaxSpecIfc.ELEM_TAXQUANTITYLOOKUP);
                buildQuantityLookupTable(taxQuantityLookups);

            }
        }

        if (DEBUG) System.out.println("Configured StandardSalesTaxCalc");
    }

    protected void buildAmountLookupTable(Element[] xmlAmtNodes)
    {

        for (int i = 0; i < xmlAmtNodes.length; i++)
        {
            String low = xmlAmtNodes[i].getAttribute(TaxSpecIfc.ATTR_RANGELOW);
            String high = xmlAmtNodes[i].getAttribute(TaxSpecIfc.ATTR_RANGEHIGH);
            String amount = xmlAmtNodes[i].getAttribute(TaxSpecIfc.ATTR_RANGEAMOUNT);

            if (DEBUG)
            {
                System.out.println("TaxLookupTable (low, high, amount): "
                                   + low + ", " + high + ", " + amount);
            }
            addTaxLookup(amountTable, Double.valueOf(low).doubleValue(),
                         Double.valueOf(high).doubleValue(),
                         Double.valueOf(amount).doubleValue());
        }

    }

    protected void buildQuantityLookupTable(Element[] xmlQtyNodes)
    {
        for (int i = 0; i < xmlQtyNodes.length; i++)
        {
            String low = xmlQtyNodes[i].getAttribute(TaxSpecIfc.ATTR_QUANTITYLOW);
            String high = xmlQtyNodes[i].getAttribute(TaxSpecIfc.ATTR_QUANTITYHIGH);
            String amount = xmlQtyNodes[i].getAttribute(TaxSpecIfc.ATTR_QUANTITYRATE);

            if (DEBUG) System.out.println("TaxLookupTable (low, high, amount): " + low + ", " + high + ", " + amount);

            addTaxLookup(quantityTable, Double.valueOf(low).doubleValue(),
                         Double.valueOf(high).doubleValue(),
                         Double.valueOf(amount).doubleValue());
        }

    }

    protected void addTaxLookup(Vector lookupTable, double rangelow, double rangehigh, double rangeamount)
    {
        TaxLookup lookup = new TaxLookup(rangelow, rangehigh, rangeamount, digits);
        TaxLookup next = null;

        for (int i = 0; i < lookupTable.size(); i++)
        {
            next =  (TaxLookup) lookupTable.elementAt(i);
            if (lookup.getUpper() < next.getLower())
            {
                //insert element
                lookupTable.insertElementAt(lookup, i - 1);
                return;
            } // end if

        }  // end for

        //fall through or no elements add at end
        lookupTable.addElement(lookup);

    }


    protected double round(double val, int digits)
    {
        // Utility function to round a double value to specified
        // number of digits
        double scale = Math.pow(10, digits);
        double temp = val * scale;
        return Math.rint(temp) / scale;
    }

    protected long convertToLong(double val, int digits)
    {
        // Utility method to convert a double value to a long value with
        // the specified number of digits
        double scale = Math.pow(10, digits);
        double temp = val * scale;
        return Math.round(temp);
    }

    protected double convertToDouble(long val, int digits)
    {
        // Utility method to convert a long into a double with the
        // specified number of digits
        return val / Math.pow(10,digits);
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

        strResult +="\nTaxRateId: " + taxRateId;
        strResult += "\nTaxRateName:" + taxRateName;
        strResult += "\nDescription: " + taxRateDescription + "\n\n";

        if (this.amountTable == null)
        {
            strResult += "Amount Rate: " + this.amtTaxRate;
        }
        else
        {
            strResult += "Tax Amount Lookup Table:\n";

            for (int i = 0; i < amountTable.size(); i++)
            {
                TaxLookup lu = (TaxLookup) amountTable.elementAt(i);
                strResult += lu.toString() + "\n";
            }
        }

        if (quantityTable == null)
        {
            strResult += "\nQuantity Rate: " + this.qtyTaxRate;
        }
        else
        {
            strResult += "\nTax Quantity Lookup Table:\n";

            for (int i = 0; i < quantityTable.size(); i++)
            {
                TaxLookup lu = (TaxLookup) quantityTable.elementAt(i);
                strResult += lu.toString() + "\n";
            }
        }
        return strResult;
    }

    //================================================================================
    // TaxLookup
    //================================================================================

    //---------------------------------------------------------------------------------------
    /**
       Provide a helper class for the tax lookup tables.
    */
    //---------------------------------------------------------------------------------------
    protected class TaxLookup implements Serializable
    {
        /**
           lower limit of lookup range
        */
        long lower = 0;
        /**
           upper limit of lookup range
        */
        long upper = 0;
        /**
           tax to apply within the range
        */
        long taxRate = 0;
        /**
           number of digits to use in calculations
        */
        int digits = 2;

        //---------------------------------------------------------------------------------------
        /**
           Constructor for a TaxLookup instance.
           @param lower - lower limit of lookup range
           @param upper - upper limit of lookup range
           @param amount - rate to apply to range
           @param digits - conversion factor
        */
        //---------------------------------------------------------------------------------------
        TaxLookup(double lower, double upper, double rate, int digits)
        {
            this.digits = digits;
            this.lower = convertToLong(lower, digits);
            this.upper = convertToLong(upper, digits);
            this.taxRate = convertToLong(rate, digits);
        }

        //---------------------------------------------------------------------------------------
        /**
           Returns the lower limit
           @return lower limit of this range
        */
        //---------------------------------------------------------------------------------------
        long getLower()
        {
            return this.lower;
        }

        //---------------------------------------------------------------------------------------
        /**
           Returns the upper limit of the range
           @param upper limit
        */
        //---------------------------------------------------------------------------------------
        long getUpper()
        {
            return this.upper;
        }

        //---------------------------------------------------------------------------------------
        /**
           Returns the rate to apply to this range
           @return rate
        */
        //---------------------------------------------------------------------------------------
        long getTaxRate()
        {
            return this.taxRate;
        }

        //---------------------------------------------------------------------------------------
        /**
           Return a human readable representation of the TaxLookup class
           @return string representation of this instance
        */
        //---------------------------------------------------------------------------------------
        public String toString()
        {
            return convertToDouble(lower, digits) + " - " + convertToDouble(upper, digits) + " @" + convertToDouble(taxRate, digits);
        }

    } // end TaxLookup class
    //================================================================================
    //  End TaxLookup
    //================================================================================

} // end StandardSalesTaxRate class
