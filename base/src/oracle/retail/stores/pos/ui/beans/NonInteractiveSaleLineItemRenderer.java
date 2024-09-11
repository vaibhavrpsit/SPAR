/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   09/08/14 - added this line renderer in order to remove the
 *                         hyper link on the item description.
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.beans;

import javax.swing.JLabel;

/**
 * NonInteractiveSaleLineItemRenderer extends {@link SaleLineItemRenderer} 
 * in order to remove the hyper link in the item description field.
 * @since 14.1
 *
 */
public class NonInteractiveSaleLineItemRenderer extends SaleLineItemRenderer
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -9071992406088801970L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.SaleLineItemRenderer#createLabel(int)
     */
    @Override
    protected JLabel createLabel(int index)
    {
        // do what super's super does to prevent creation of hyper link label
        // on the description field
        return uiFactory.createLabel("", "", null, LABEL_PREFIX);
    }

    
}
