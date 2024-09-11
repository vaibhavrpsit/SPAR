/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AlphaNumericPlusDocument.java /main/2 2014/03/18 16:18:16 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   03/18/14 - Make allowable characters configurable.
 *    yiqzhao   03/17/14 - Allow hypen in item id and serial number.
 *    subrdey   02/14/13 - Allows AlphaNumeric character and Hyphen for Price
 *                         Inquiry Parameter.
 * 
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.beans;

import org.apache.log4j.Logger;

/**
 * This document allows for standard alpha-numeric text entry <b>plus</b> any defined allowable <code>chars</code>.
 * @since 14.0.1 renamed from PriceInquiryDocument”
 * 
 * @author subrdey
 *
 */
public class AlphaNumericPlusDocument extends AlphaNumericDocument
{
    private static final Logger logger = Logger.getLogger(AlphaNumericPlusDocument.class);
    
    protected char[] allowableCharacters = new char[0];
    
    /**
     * Constructor.
     *
     * @param maxLength the maximum length
     */
    public AlphaNumericPlusDocument(int maxLength)
    {
        super(maxLength);
    }
    
    /**
     * Overridden to allow "*" character to be inserted but only if its the only
     * one and a character has not already been inserted.
     * 
     * @param ch
     * @return
     * @see #DELIMITER
     */
    @Override
    protected boolean isCharAllowable(char ch)
    {
        if (Character.isLetter(ch))
        {
            return true;
        }
        if (Character.isWhitespace(ch))
        {
            return isSpaceAllowed();
        }
        for (char c : allowableCharacters)
        {
            if (c==ch)
            { 
                return true;
            }
        }
        return Character.isDigit(ch);
    }   
    
    
    /**
     * Set allowable characters
     * @param allowableCharacters
     */
    public void setAllowableCharacters(char... allowableCharacters) {
        this.allowableCharacters = allowableCharacters;
    }
    

    /**
     * Return all the allowable characters
     * @return allowableCharacters
     */
    public char[] getAllowableCharacters()
    {
        return this.allowableCharacters;
    }
}
