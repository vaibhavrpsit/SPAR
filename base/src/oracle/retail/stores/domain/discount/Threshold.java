package oracle.retail.stores.domain.discount;

import java.math.BigDecimal;

import oracle.retail.stores.foundation.utility.Util;

/**
 * This is the class for Threshold.
 */
public class Threshold implements ThresholdIfc
{
    /**
     * revision number supplied by source-code control system
     **/
    public static final String revisionNumber = "$Revision: /main/2 $";

    /**
     * the thresholdId
     */
    protected String thresholdID;

    /**
     * the PriceDerivationRuleID
     */
    protected int priceDerivationRuleID;

    protected int thresholdVal = 0; 

    protected BigDecimal discountPercent;

    protected BigDecimal discountAmount;

    protected BigDecimal newPrice;

    /**
     * Sets attributes in new instance of class.
     * 
     * @param newClass new instance of class
     **/
    protected void setCloneAttributes(Threshold newClass)
    { 
        // begin setCloneAttributes()
        newClass.thresholdID = thresholdID;
        newClass.thresholdVal = thresholdVal;
        newClass.discountPercent = discountPercent;
        newClass.discountAmount = discountAmount;
        newClass.newPrice = newPrice;
    } // end setCloneAttributes()

    /**
     * Creates clone of this object.
     * 
     * @return Object clone of this object
     */
    public Object clone()
    {
        Threshold clone = new Threshold();
        setCloneAttributes(clone);
        return clone;
    }

    /**
     * Retrieves thresholdID
     * 
     * @return thresholdID
     **/
    public String getThresholdID()
    {
        return thresholdID;
    }

    /**
     * Sets thresholdID
     * 
     * @param value thresholdID
     **/
    public void setThresholdID(String thresholdID)
    {
        this.thresholdID = thresholdID;
    }

    /**
     * Retrieves priceDerivationRuleID
     * 
     * @return priceDerivationRuleID
     **/
    public int getPriceDerivationRuleID()
    {
        return priceDerivationRuleID;
    }

    /**
     * Sets priceDerivationRuleID
     * 
     * @param value priceDerivationRuleID
     **/
    public void setPriceDerivationRuleID(int priceDerivationRuleID)
    {
        this.priceDerivationRuleID = priceDerivationRuleID;
    }

    /**
     * Retrieves thresholdVal
     * 
     * @return thresholdVal
     **/
    public int getThresholdVal()
    {
        return thresholdVal;
    }

    /**
     * Sets thresholdVal
     * 
     * @param value thresholdVal
     **/
    public void setThresholdVal(int thresholdVal)
    {
        this.thresholdVal = thresholdVal;
    }
    
    /**
     * Retrieves discountPercent
     * 
     * @return discountPercent
     **/
    public BigDecimal getDiscountPercent()
    {
        return discountPercent;
    }

    /**
     * Sets discountPercent
     * 
     * @param value discountPercent
     **/
    public void setDiscountPercent(BigDecimal discountPercent)
    {
        this.discountPercent = discountPercent;
    }

    /**
     * Retrieves discountAmount
     * 
     * @return discountAmount
     **/
    public BigDecimal getDiscountAmount()
    {
        return discountAmount;
    }

    /**
     * Sets discountAmount
     * 
     * @param value discountAmount
     **/
    public void setDiscountAmount(BigDecimal discountAmount)
    {
        this.discountAmount = discountAmount;
    }

    /**
     * Retrieves newPrice
     * 
     * @return newPrice
     **/
    public BigDecimal getNewPrice()
    {
        return newPrice;
    }

    /**
     * Sets newPrice
     * 
     * @param value newPrice
     **/
    public void setNewPrice(BigDecimal newPrice)
    {
        this.newPrice = newPrice;
    }

    /**
     * Determine whether the provided object is the same type and has the same
     * field values as this one.
     * 
     * @param obj the object to compare
     * @return true if the fields are equal; false otherwise
     **/
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        // Only test for equality if the objects are instances of the
        // same class.
        if (obj instanceof Threshold)
        {
            Threshold strategy = (Threshold)obj;
            if (getThresholdID() == strategy.getThresholdID()
                    && getPriceDerivationRuleID() == strategy.getPriceDerivationRuleID())
            {
                isEqual = true;
            }
        }
        return isEqual;
    }

    /**
     * Method to default display string function.
     * 
     * @return String representation of object
     **/
    public String toString()
    { // begin toString()
      // result string
        StringBuffer strResult = new StringBuffer("Class:  Threshold");
        strResult.append(" (Revision ").append(getRevisionNumber()).append(") @").append(hashCode()).append(Util.EOL)
                .append("threshold id:                    [").append(getThresholdID()).append("]").append(Util.EOL)
                .append("threshold val:                   [").append(getThresholdVal()).append("]").append(Util.EOL)
                .append("discount amount:                 [").append(getDiscountAmount()).append("]").append(Util.EOL)
                .append("discount percent:                [").append(getDiscountPercent()).append("]").append(Util.EOL)
                .append("new price:                       [").append(getNewPrice()).append("]").append(Util.EOL);
        // pass back result
        return (strResult.toString());
    } // end toString()

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     **/
    public String getRevisionNumber()
    { // begin getRevisionNumber()
      // return string
        return (revisionNumber);
    } // end getRevisionNumber()

} // end class Threshold

