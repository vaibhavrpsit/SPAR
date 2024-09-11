/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transform/entity/TransformerUtilities.java /main/5 2013/01/27 23:40:31 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  01/27/13 - extending JPA
 *    icole     11/07/12 - Correct item price being zero when client time is
 *                         less than server time.
 *    abondala  08/09/12 - customer jpa related changes
 *    jswan     07/20/12 - Added to transform JPA Item Entitities into a
 *                         PLUItemIfc.
 * ===========================================================================
 */
package oracle.retail.stores.domain.transform.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.CommonGateway;
import oracle.retail.stores.common.utility._360Date;
import oracle.retail.stores.common.utility._360DateIfc;
import oracle.retail.stores.common.utility._360Time;
import oracle.retail.stores.common.utility._360TimeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;

/**
 *  This class contains static utility methods used to transform JPA Entity objects
 *  in to Domain objects. 
 */
public class TransformerUtilities
{
    /**
     * percentage scale
     */
    protected static final int PERCENTAGE_SCALE = 4;

    /**
     * Retrieves CurrencyIfc reference from database field stored as decimal
     * 
     * @param BigDecimal
     * @return safe CurrencyIfc object
     */
    public static CurrencyIfc getCurrencyFromDecimal(BigDecimal d)
    {
        return getCurrencyFromDecimal(d, DomainGateway.getBaseCurrencyType().getCountryCode());
    }

    /**
     * Retrieves CurrencyIfc reference of the appropriate currency from database
     * field stored as decimal
     * 
     * @param BigDecimal
     * @param currencyCode String
     * @return safe CurrencyIfc object
     */
    public static CurrencyIfc getCurrencyFromDecimal(BigDecimal d, String currencyCode)
    {
        CurrencyIfc c = null;
        if (d == null)
        {
            c = DomainGateway.getCurrencyInstance(currencyCode);
        }
        else
        {
            c = DomainGateway.getCurrencyInstance(currencyCode, d.toString());
        }
        return (c);
    }

    /**
     * If string is null, string is set to empty string.
     * @param String str
     * @return safe str
     */
    public static String getSafeString(String str)
    {
        if (str == null)
        {
            str = "";
        }
        else
        {
            str.trim();
        }
        return str;
    }

    /**
     * If string is null, string is set to the default.
     * @param Integer
     * @return safe int
     */
    public static int getSafeInt(Integer i, int dflt)
    {
        Integer ret = dflt;
        if (i != null)
        {
            ret = i;
        }
        return ret;
    }
    
    /**
     * Converts a string to an int; if the string does not contain
     * a valid integer value, it returns the default. 
     * @param str value to 
     * @param dflt
     * @return
     */
    public static int getSafeInt(String str, int dflt)
    {
        int code = dflt;
        try
        {
            code = Integer.parseInt(TransformerUtilities.getSafeString(str));
        }
        catch(Throwable t)
        {
            // do nothing
        }
        
        return code;
    }

    /**
     * The value is converted from a percentage to a straight BigDecimal 
     * value, i.e. the rate.
     * 
     * @param num BigDecimal
     * @return BigDecimal value
     */
    public static BigDecimal getPercentage(BigDecimal num)
    {
        BigDecimal returnNum = null;
        if (num == null)
        {
            returnNum = BigDecimal.ZERO;
        }
        else
        {
            num = num.setScale(PERCENTAGE_SCALE);
            returnNum = num.movePointLeft(2);
        }
        return (returnNum);
    }

    /**
     * Converts a SQL time stamp to an EYSDate object
     * @param t Timestamp to convert
     * @param dflt value to return if t is null
     * @return EYSDate
     */
    public static EYSDate getEYSDate(Timestamp t, EYSDate dflt)
    {
        EYSDate ed = null;
        if (t == null)
        {
            ed = dflt;
        }
        else
        {
            _360Date date = new _360Date();
            date.initialize(t);
            ed = new EYSDate(date.dateValue());
        }
        
        return ed;
    }
    
    /**
     * Convert a java.sql.Timestamp to a Date.
     * 
     * @param timestamp The timestamp to convert
     * @return EYSDate the equivalent Date object
     */
    public static EYSDate timestampToEYSDate(Timestamp timestamp)
    {
        _360DateIfc date = timestampTo_360Date(timestamp);
        if (date == null)
        {
            return null;
        }
        
        return (new EYSDate(date.dateValue()));
    }    
    
    /**
     * Convert a java.sql.Timestamp to a 360 Date.
     * 
     * @param timestamp The timestamp to convert
     * @return _360DateIfc the equivalent Date object
     */
    public static _360DateIfc timestampTo_360Date(Timestamp timestamp)
    {
        if (timestamp == null)
        {
            return null;
        }
        Date d = timestampToDate(timestamp);
        _360DateIfc returnDate = CommonGateway.getFactory().get_360DateInstance();
        returnDate.initialize(d);
        
        return (returnDate);
    }  
    
    /**
     * Convert a java.sql.Timestamp to a Date.
     * 
     * @param timestamp The timestamp to convert
     * @return Date the equivalent Date object
     */
    public static Date timestampToDate(Timestamp timestamp)
    {
        Date date = null;
        if (timestamp != null)
        {
            String vmversion = System.getProperty("java.vm.version");
            if (vmversion != null && vmversion.startsWith("1.3"))
            {
                return (new Date(timestamp.getTime() + (timestamp.getNanos() / 1000000)));
            }

            return new Date(timestamp.getTime());
        }
        
        return date;
    } 
    
    /**
     * Returns _360TimeIfc object for time string.
     * 
     * @param String time
     * @return _360TimeIfc object
     */
    public static _360TimeIfc timeTo_360Time(String time)
    {
        _360TimeIfc returnTime = _360Time.get_360Time(getSafeString(time));

        return (returnTime);
    }    
}
