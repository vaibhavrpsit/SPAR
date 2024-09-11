/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/HardTotalsUtility.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:12 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    ohorne    10/09/08 - Created Utility
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import java.util.Locale;
import java.util.Set;

import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.common.utility.LocalizedText;
import oracle.retail.stores.common.utility.LocalizedTextIfc;

/**
 * A Utility class for setting and getting Hard Totals Data
 * from Objects that do not (and can not) implement HardTotalsDataIfc 
 */
public class HardTotalsUtility
{
    /**
     * Configures a LocalizedText Object from HardTotals 
     * @param builder the HardTotalsBuilder with a serialized LocalizedText
     * @return  the configured LocalizedText Object
     * @throws HardTotalsFormatException
     */
    public static LocalizedTextIfc setHardTotalsData(HardTotalsBuilderIfc builder) throws HardTotalsFormatException
    {
        LocalizedTextIfc lclTxt = new LocalizedText();
 
        String defLcl = builder.getStringField();
        if (!"null".equals(defLcl))
        {
            Locale defaultLocale = LocaleUtilities.getLocaleFromString(defLcl);
            lclTxt.setDefaultLocale(defaultLocale);
        }
  
        int count = builder.getIntField();        
        for (int i = 0; i < count; i++)
        {
            Locale lcl = LocaleUtilities.getLocaleFromString(builder.getStringField());
            String txt =  builder.getStringField();
            lclTxt.putText(lcl, txt);            
        }
        return lclTxt;        
    }

    /**
     * Writes a LocalizedText Object to HardTotals
     * @param lclTxt the LocalizedText Object 
     * @param builder the HardTotalsBuilder
     */
    public static void getHardTotalsData(LocalizedTextIfc lclTxt, HardTotalsBuilderIfc builder)
    {
        //default locale
        if (lclTxt.getDefaultLocale() == null)
        {
            builder.appendString("null");
        }
        else
        {
            builder.appendString(lclTxt.getDefaultLocale().toString());
        }
        
        //number of locales
        builder.appendInt(lclTxt.size());
        
        //all locales
        Set<Locale> lcls = lclTxt.getLocales();
        for (Locale lcl : lcls)
        {
            builder.appendString(lcl.toString());
            
            if (lclTxt.getText(lcl) == null)
            {
                builder.appendStringObject("null");
            }
            else
            {
                builder.appendString(lclTxt.getText(lcl));
            }
        }
    }
}
