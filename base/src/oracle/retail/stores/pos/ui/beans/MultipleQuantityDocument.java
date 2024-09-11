/* ===========================================================================
* Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/MultipleQuantityDocument.java /main/3 2012/09/21 18:29:54 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   09/21/12 - Enabling HYPHEN in the sell screen
 *                         PromptAndResponsePanel area.
 *    cgreene   03/16/11 - implement You Saved feature on reciept and
 *                         AllowMultipleQuantity parameter
 *    cgreene   03/15/11 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;

/**
 * An instance of MultipleQuantityDocument allows for standard alpha-numeric
 * text entry but also a "N*" prefix where N is a number. This number is parsed
 * in to a quantity for use in setting the quantity of the line item.
 * 
 * @author cgreene
 * @since 13.4
 * @version $Revision: /main/3 $
 */
public class MultipleQuantityDocument extends AlphaNumericDocument
{
    private static final long serialVersionUID = 8912919923107289434L;

    private static final Logger logger = Logger.getLogger(MultipleQuantityDocument.class);

    public static final char DELIMITER = '*';
    public static final char HYPHEN = '-';

    private boolean delimiterFound;
    private boolean characterFound;

    /**
     * Constructor.
     */
    public MultipleQuantityDocument()
    {
        this(Integer.MAX_VALUE);
    }

    /**
     * Constructor.
     * 
     * @param maxLength the maximum length
     */
    public MultipleQuantityDocument(int maxLength)
    {
        super(maxLength);
    }

    /**
     * (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.AlphaNumericDocument#insertString(int, java.lang.String, javax.swing.text.AttributeSet)
     */
    @Override
    public void insertString(int offset, String text, AttributeSet attributes) throws BadLocationException
    {
        String currentText = getText(0, getLength());
        delimiterFound = currentText.contains(String.valueOf(DELIMITER));
        try
        {
            Integer.parseInt(currentText);
            characterFound = false;
        }
        catch (NumberFormatException e)
        {
            characterFound = true;
        }
        super.insertString(offset, text, attributes);
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
        if (ch == DELIMITER)
        {
            if (delimiterFound || characterFound)
            {
                return false;
            }
            delimiterFound = true;
            return isDelimiterAllowed();
        }
        if (Character.isLetter(ch) || ch == HYPHEN)
        {
            characterFound = true;
            return true;
        }
        if (Character.isWhitespace(ch))
        {
            return isSpaceAllowed();
        }
        return Character.isDigit(ch);
    }

    /**
     * Determine if the {@link #DELIMITER} value is allowed to be entered by
     * checking the {@link ParameterConstantsIfc#ITEM_AllowMultipleQuantity}
     * parameter. Defaults to true.
     * 
     * @return
     */
    protected boolean isDelimiterAllowed()
    {
        ParameterManagerIfc parmMgr = (ParameterManagerIfc)Dispatcher.getDispatcher().getManager(ParameterManagerIfc.TYPE);
        if (parmMgr != null)
        {
            try
            {
                Boolean allowDelimiter = parmMgr.getBooleanValue(ParameterConstantsIfc.ITEM_AllowMultipleQuantity);
                if (allowDelimiter != null)
                {
                    return allowDelimiter;
                }
            }
            catch (ParameterException e)
            {
                logger.warn("Unable to determine AllowMultipleQuantity parameter. Defaulting to true.", e);
            }
        }
        return true;
    }
}