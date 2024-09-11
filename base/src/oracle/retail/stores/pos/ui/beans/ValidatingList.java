/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ValidatingList.java /main/18 2012/10/17 11:51:51 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    dwfung    02/03/10 - fix error message
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:26:43 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:15:30 PM  Robert Pearse   
 *
 *  Revision 1.3  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:13:00   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:56:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   15 Apr 2002 09:36:46   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.SwingConstants;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * Validating list
 * 
 * @version $Revision: /main/18 $
 */
public class ValidatingList extends JList implements ValidatingFieldIfc
{

    private static final long serialVersionUID = 614377645172148311L;

    /** Indicates if the list can be empty */
    protected boolean emptyAllowed = true;

    /** Validation error message */
    protected String errorMessage = "";

    /** Field label */
    protected JLabel label = null;

    /** whether or not this field is required */
    protected boolean required;

    /** Revision number supplied by version control. */
    public static final String revisionNumber = "$Revision: /main/18 $";

    /**
     * Class constructor.
     */
    public ValidatingList()
    {
        super();
        initialize();
        addFocusListener(getFocusListener());
    }

    /**
     * Class constructor.
     */
    public ValidatingList(ListModel data)
    {
        super(data);
        initialize();
        addFocusListener(getFocusListener());
    }

    /**
     * Class constructor.
     */
    public ValidatingList(Object[] data)
    {
        super(data);
        initialize();
        addFocusListener(getFocusListener());
    }

    /**
     * Class constructor.
     */
    public ValidatingList(Vector<?> data)
    {
        super(data);
        initialize();
        addFocusListener(getFocusListener());
    }

    /**
     * Sets the border for the field.
     */
    protected void initialize()
    {
        UIFactory.getInstance().configureUIComponent(this, "ValidatingList");
    }

    /**
     * Sets the selected index to zero when focus is gained, if the current
     * value is not valid.
     */
    protected FocusListener getFocusListener()
    {
        return (new FocusListener()
        {
            public void focusGained(FocusEvent e)
            {
                if (getSelectedIndex() == -1)
                {
                    setSelectedIndex(0);
                }
            }

            public void focusLost(FocusEvent e)
            {
            }
        });
    }

    /**
     * Returns whether the list is required.
     * 
     * @return true if required, false otherwise
     */
    public boolean isRequired()
    {
        return required;
    }

    /**
     * Sets whether the list is required.
     * 
     * @param propValue true if required false if not
     */
    public void setRequired(boolean propValue)
    {
        required = propValue;
    }

    /**
     * Sets the flag for allowing an empty string to be valid.
     * 
     * @param allowEmpty true if empty field is valid, false otherwise
     */
    public void setEmptyAllowed(boolean allowed)
    {
        emptyAllowed = allowed;
    }

    /**
     * Returns the flag for allowing empty to be valid.
     * 
     * @return true if empty field is valid, false otherwise
     */
    public boolean isEmptyAllowed()
    {
        return (emptyAllowed);
    }

    /**
     * Determines whether the current field information is valid and returns the
     * result.
     * 
     * @return true if the current field entry is valid, false otherwise
     */
    public boolean isInputValid()
    {
        setErrorMessage(getFieldName());
        boolean rv = true;
        if (!emptyAllowed && getSelectedIndex() == -1)
        {
            rv = false;
        }
        return rv;
    }

    /**
     * Sets the error message of a field.
     * 
     * @param msg the error message
     */
    public void setErrorMessage(String msg)
    {
        errorMessage = msg;
    }

    /**
     * Returns the error message of a field.
     * 
     * @return the error message
     */
    public String getErrorMessage()
    {
        return errorMessage;
    }

    /**
     * Returns the field name to be used in error messages.
     * 
     * @return the field name
     */
    public String getFieldName()
    {
        String displayText = this.getLabel().getText();
        // retrieve name only
        if (!(displayText.indexOf(':') < 0))
        {
            displayText = displayText.replaceAll(":", "");
        }
        return (displayText);
    }

    /**
     * Returns the label associated with a field.
     * 
     * @return the label associated with the field
     */
    public JLabel getLabel()
    {
        if (label == null)
        {
            label = new JLabel();
            label.setName("Label");
            label.setText("");
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setHorizontalTextPosition(SwingConstants.CENTER);
        }

        return label;
    }

    /**
     * Sets the label associated with the field.
     * 
     * @param label the label to use
     */
    public void setLabel(JLabel label)
    {
        this.label = label;
        String displayText = this.getLabel().getText();
        if (!(displayText.indexOf(':') < 0))
        {
            displayText = displayText.substring(0, displayText.indexOf(':'));
        }
        setErrorMessage(displayText);
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: ValidatingList (Revision " + getRevisionNumber() + ") @" + hashCode());
        return (strResult);
    }

    /**
     * Returns the revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }
}
