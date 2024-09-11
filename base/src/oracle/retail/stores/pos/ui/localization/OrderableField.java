/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/localization/OrderableField.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:36 mszekely Exp $
 * ===========================================================================
 * NOTES
 *  This class encapsulates the components of an orderable UI field.
 *
 * MODIFIED    (MM/DD/YY)
 *  abondala    01/03/10 - update header date
 *  ebthorne    10/31/08 - Code review updates
 *  ebthorne    10/30/08 - Making label a JLabel instead of JComponent
 *  ebthorne    10/24/08 - First pass for OrderableField
 *  ebthorne    10/13/08 - Initial revision
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.localization;

import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 * An OrderableField is an object that encapsulates the label and field
 * components as well as an optional enumeration that identifies the logical
 * field being ordered.
 *
 * @version $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/localization/OrderableField.java /main/3 2010/01/03 18:05:58 abondala Exp $
 * @author  ebthorne
 * @since   13.1
 */
public class OrderableField {
    /**
     * Represents the label component for the orderable field.
     */
    private final JLabel label;

    /**
     * Represents the field component for the orderable field.
     */
    private final JComponent field;

    /**
     * This identifies the logical field represented by the object.
     * If the field is not an orderable field, this value will be null.
     */
    private final Enum logicalField;

    /**
     * Creates an orderable field for a label and field.
     *
     * @param label the JLabel or other component that labels the field
     * @param field the JComponent that is responsible for data display or input
     */
    public OrderableField(JLabel label, JComponent field)
    {
        this(label, field, null);
    }

    /**
     * Creates an orderable field for a label, field, and logical field.
     *
     * @param label
     * @param field
     * @param logicalField
     */
    public OrderableField(JLabel label, JComponent field, Enum logicalField)
    {
        this.label = label;
        this.field = field;
        this.logicalField = logicalField;
    }

    /**
     * Gets the label component for this field.
     *
     * @return the label component
     */
    public JLabel getLabel()
    {
        return label;
    }

    /**
     * Gets the field component for this field.
     *
     * @return the field component
     */
    public JComponent getField()
    {
        return field;
    }

    /**
     * Gets the logical field that this orderable field represents.
     *
     * @return the logical field
     */
    public Enum getLogicalField()
    {
        return logicalField;
    }


}