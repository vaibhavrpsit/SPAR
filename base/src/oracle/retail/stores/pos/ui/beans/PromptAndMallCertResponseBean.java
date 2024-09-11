/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PromptAndMallCertResponseBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  02/27/09 - Fixed issue related to mall certificate number data
 *                         type
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.JTextField;

import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.behavior.ClearActionListener;
import oracle.retail.stores.pos.ui.behavior.ResponseTextListener;

public class PromptAndMallCertResponseBean extends PromptAndResponseBean implements ClearActionListener,
        ResponseTextListener
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /*
     * Sets the response field by instantiating the class name in the parameter.
     * This field is set from the XML using a BEANPROPERTY.
     * @param responseFieldClassName the response field class name
     */
    public void setResponseField(String responseFieldClassName)
    {
        JTextField responseField = (JTextField)UIUtilities.getNamedClass(responseFieldClassName);

        if (responseField != null)
        {
            super.setActiveResponseField(responseField);
            // If dealing with a CurrencyTextField,
            // then have to set boolean indicating zero allowed.
            if (activeResponseField instanceof CurrencyTextField)
            {
                setZeroAllowed(new Boolean(zeroAllowed).toString());
                setNegativeAllowed(new Boolean(negativeAllowed).toString());
            }
            else if (activeResponseField instanceof NonZeroDecimalTextField)
            {
                setNegativeAllowed(new Boolean(negativeAllowed).toString());
            }
            // UI guidelines requirements state alphaNumeric fields must allow
            // spaces
            else if (activeResponseField instanceof AlphaNumericTextField)
            {
                ((AlphaNumericTextField)activeResponseField).setSpaceAllowed(false);

                setDoubleByteCharsAllowed(new Boolean(doubleByteCharsAllowed).toString());
            }
            else if (activeResponseField instanceof ConstrainedTextField)
            {
                setDoubleByteCharsAllowed(new Boolean(doubleByteCharsAllowed).toString());
            }

        }
    }

}
