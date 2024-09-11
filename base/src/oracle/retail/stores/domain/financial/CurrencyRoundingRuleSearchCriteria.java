/*===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/CurrencyRoundingRuleSearchCriteria.java /main/1 2013/03/07 13:20:43 jswan Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* mkutiana    02/19/13 - Creating Currency Rounding Rule Search Criteria object
* mkutiana    02/19/13 - Creation
* ===========================================================================
*/

package oracle.retail.stores.domain.financial;

import java.io.Serializable;
import java.math.BigDecimal;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.CurrencyRoundingRuleSearchCriteriaIfc;

import org.apache.commons.lang3.builder.ToStringBuilder;
/**
 * This object holds Currency Rounding Rule Search Criteria
 * @author mkutiana
 *
 */
public class CurrencyRoundingRuleSearchCriteria implements CurrencyRoundingRuleSearchCriteriaIfc, Serializable
{

    private static final long serialVersionUID = 4040683429839665064L;
    /**
     * Holds the Currency Rounding type e.g. "Swedish Rounding", "Always Round UP"
     */
    protected String currencyRoundingType = null;
    
    /**
     * Holds the rounding level denomiation e.g. O.O5, O.10
     */
    protected BigDecimal currencyRoundingDenomination= null;
    
    /**
     * Holds the currency code e.g. USD, CAD
     */
    protected String IsoCurrencyCode=null;

    /**
     * @return the currencyRoundingType
     */
    public String getCurrencyRoundingType()
    {
        return currencyRoundingType;
    }

    /**
     * @param currencyRoundingType the currencyRoundingType to set
     */
    public void setCurrencyRoundingType(String currencyRoundingType)
    {
        this.currencyRoundingType = currencyRoundingType;
    }

    /**
     * @return the currencyRoundingDenomination
     */
    public BigDecimal getCurrencyRoundingDenomination()
    {
        return currencyRoundingDenomination;
    }

    /**
     * @param currencyRoundingDenomination the currencyRoundingDenomination to set
     */
    public void setCurrencyRoundingDenomination(BigDecimal currencyRoundingDenomination)
    {
        this.currencyRoundingDenomination = currencyRoundingDenomination;
    }

    /**
     * @return the isoCurrencyCode
     */
    public String getIsoCurrencyCode()
    {
        return IsoCurrencyCode;
    }

    /**
     * @param isoCurrencyCode the isoCurrencyCode to set
     */
    public void setIsoCurrencyCode(String isoCurrencyCode)
    {
        IsoCurrencyCode = isoCurrencyCode;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);
        appendToString(builder);
        return builder.toString();
    }

    /**
     * Add printable objects to the builder.Overriding methods should also
     * call super.appendToString(ToStringBuilder).
     *
     * @param builder a ToStringBuilder object
     * @see #toString()
     */
    protected void appendToString(ToStringBuilder builder)
    {
        builder.append("currencyRoundingType", currencyRoundingType);
        builder.append("currencyRoundingDenomination", currencyRoundingDenomination);
        builder.append("IsoCurrencyCode", IsoCurrencyCode);
    }


    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {                                  
        boolean isEqual = true;
        if (obj instanceof CurrencyRoundingRuleSearchCriteria)
        {
            CurrencyRoundingRuleSearchCriteria c = (CurrencyRoundingRuleSearchCriteria) obj;      // downcast the input object

            if (Util.isObjectEqual(getCurrencyRoundingType(), c.getCurrencyRoundingType()) &&
                Util.isObjectEqual(getCurrencyRoundingDenomination(), c.getCurrencyRoundingDenomination()) &&
                Util.isObjectEqual(getIsoCurrencyCode(), c.getIsoCurrencyCode() ))
            {
                isEqual = true;            
            }
            else
            {
                isEqual = false;         
            }
        }                             
        else
        {
                 isEqual = false;
        }
        return(isEqual);
    }     

    
}