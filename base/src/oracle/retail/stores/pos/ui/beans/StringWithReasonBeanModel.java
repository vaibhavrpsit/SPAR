/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/StringWithReasonBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.io.Serializable;
//-------------------------------------------------------------------------
/**
This bean model contains a string and a reason code. It is used by the
following beans:
<UL>
<LI>TransTaxExempt
</UL>
@see oracle.retail.stores.pos.ui.beans.TransTaxExempt;
@author  $KW=@(#); $Own=michaelm; $EKW;
@version $KW=@(#); $Ver=rapp.q_2.6.1:2; $EKW;
**/
//-------------------------------------------------------------------------
public class StringWithReasonBeanModel extends ReasonBeanModel implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8071455054604778339L;

    protected String fieldValue = new String();
/**
 * DiscPercentBeanModel constructor comment.
 */
public StringWithReasonBeanModel() {
    super();
}
/**
 * Gets the value property (java.lang.String) value.
 * @return The value property value.
 * @see #setValue
 */
public String getValue() {
    return fieldValue;
}
/**
 * Sets the value property (java.lang.String) value.
 * @param value The new value for the property.
 * @see #getValue
 */
public void setValue(String value) {
    String oldValue = fieldValue;
    fieldValue = value;
//    firePropertyChange("value", oldValue, value);
}
}
