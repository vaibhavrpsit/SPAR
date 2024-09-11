/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ValidatingPasswordField.java /main/19 2014/01/28 11:05:44 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   01/24/14 - Fortify: Prevent heap inspection of passwords by
 *                         avoiding using Strings
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * This field is the base class for password fields that can be validated.
 * 
 * @version $Revision: /main/19 $
 */
public class ValidatingPasswordField extends JPasswordField implements ValidatingFieldIfc, FocusListener
{
    private static final long serialVersionUID = -90120038382693299L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/19 $";;

    /** flag of whether an empty field is valid */
    protected boolean emptyAllowed = true;

    /** id for look and feel */
    public final static String uiClassID = "RequiredPasswordFieldUI";

    /** the minimum length of the field */
    protected int minLength = 1;

    /** error message */
    protected String errorMessage = "";

    /** whether or not this field is required */
    protected boolean required;

    /** Label that ID's the field on the screen. */
    protected JLabel label = null;

    /** Label that ID's the field on the screen. */
    protected DocumentListener POSDocumentListener = null;

    /** The base bean adapter that ultimately contains this object. */
    protected BaseBeanAdapter baseBeanAdapter = null;

    /**
     * This is to allow flexibility that a password field may not be needed for
     * match. Default is true
     */
    protected boolean passwordMatchAllowed = true;

    /**
     * Class constructor.
     */
    public ValidatingPasswordField()
    {
        this("");
    }

    /**
     * Class constructor.
     * 
     * @param msg the default text to appear in the field
     */
    public ValidatingPasswordField(String msg)
    {
        super(msg);
        UIFactory.getInstance().configureUIComponent(this, "ValidatingField");
        addFocusListener(this);

    }

    /**
     * Returns the flag for allowing empty to be valid.
     * 
     * @return true if empty field is valid, false otherwise
     */
    public boolean isEmptyAllowed()
    {
        return emptyAllowed;
    }

    /**
     * Determines whether the current field information is valid and returns the
     * result.
     * 
     * @return true if the current field entry is valid, false otherwise
     */
    public boolean isInputValid()
    {
        boolean rv = true;
        if (!emptyAllowed && Util.isEmptyCharArray(getPassword()))
        {
            rv = false;
        }
        return rv;
    }

    /**
     * Returns whether the field is required.
     * 
     * @return true if required, false otherwise
     */
    public boolean isRequired()
    {
        return required;
    }

    /**
     * Sets whether the field is required.
     * 
     * @param propValue true if required false if not
     */
    public void setRequired(boolean propValue)
    {
        required = propValue;
    }

    /**
     * Returns the minimum length of a valid field.
     * 
     * @return the minimum length of a valid field
     */
    public int getMinLength()
    {
        return minLength;
    }

    /**
     * Returns the class type.
     * 
     * @return the class type
     */
    public String getUIClassID()
    {
        return uiClassID;
    }

    /**
     * Sets the minimum length of a valid field.
     * 
     * @param minLength the minimum length for a valid field
     */
    public void setMinLength(int minLength)
    {
        this.minLength = minLength;
    }

    /**
     * Returns the field name to be used in error messages.
     * 
     * @return the field name
     */
    public String getFieldName()
    {
        String displayText = this.getLabel().getText();
        if (!(displayText.indexOf(':') < 0))
        {
            displayText = displayText.substring(0, displayText.indexOf(':'));
        }
        return (displayText);
    }

    /**
     * Sets the default error message of a field.
     */
    public void setErrorMessage()
    {
        setErrorMessage(getFieldName());
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
            label.setText("A field on this screen");
            label.setForeground(Color.black);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setHorizontalTextPosition(SwingConstants.CENTER);
        }
        return label;
    }

    /**
     * Sets the label associated with the field and configures the error message
     * based on the label text.
     * 
     * @param label the label to use
     */
    public void setLabel(JLabel label)
    {
        this.label = label;
        setErrorMessage();
    }

    /**
     * Sets the flag for allowing an empty string to be valid.
     * 
     * @param allowEmpty true if empty field is valid, false otherwise
     */
    public void setEmptyAllowed(boolean allowEmpty)
    {
        this.emptyAllowed = allowEmpty;
    }

    /**
     * Called when the component gets focus.
     * 
     * @param e the focus event
     */
    public void focusGained(FocusEvent e)
    {
        if (isEditable() && !Util.isEmptyCharArray(getPassword()))
        {
            selectAll();
        }
        if (!isFocusable() || !isEditable())
        {
            transferFocus();
        }
        else
        {
            if (baseBeanAdapter != null)
            {
                baseBeanAdapter.setCurrentComponent(this);
            }
            if (POSDocumentListener != null)
            {
                getDocument().addDocumentListener(POSDocumentListener);
                ValidatingTextDocumentEvent evt = new ValidatingTextDocumentEvent(getDocument());
                POSDocumentListener.changedUpdate(evt);
            }
        }
    }

    /**
     * Called when the component looses focus.
     * 
     * @param e the focus event
     */
    public void focusLost(FocusEvent e)
    {
        if (POSDocumentListener != null)
        {
            ValidatingTextDocument doc = new ValidatingTextDocument();

            ValidatingTextDocumentEvent evt = new ValidatingTextDocumentEvent(doc);
            POSDocumentListener.changedUpdate(evt);
            getDocument().removeDocumentListener(POSDocumentListener);
        }
    }

    /**
     * Sets the document listener container. It is used to get the document
     * listener to use when focus has been gained.
     * 
     * @param container the document listener container.
     */
    protected void setPOSDocumentListener(DocumentListener listener, BaseBeanAdapter bean)
    {
        POSDocumentListener = listener;
        baseBeanAdapter = bean;
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        String strResult = new String("Class: ValidatingTextField (Revision " + getRevisionNumber() + ") @"
                + hashCode());
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

    /**
     * This inner class provides a document event class to send to the document
     * listener when the focus changes.
     */
    public class ValidatingTextDocumentEvent implements DocumentEvent
    {
        /** Document */
        Document document = null;

        /**
         * Constructs the event
         * 
         * @param doc the document
         */
        public ValidatingTextDocumentEvent(Document doc)
        {
            document = doc;
        }

        /**
         * Returns the type of event.
         * 
         * @return the event type as a DocumentEvent.EventType
         */
        public DocumentEvent.EventType getType()
        {
            return DocumentEvent.EventType.REMOVE;
        }

        /**
         * Returns the offset within the document of the start of the change.
         * 
         * @return the offset >= 0
         */
        public int getOffset()
        {
            return 0;
        }

        /**
         * Returns the length of the change.
         * 
         * @return the length
         */
        public int getLength()
        {
            return 0;
        }

        /**
         * Gets the document that sourced the change event.
         * 
         * @return the document
         */
        public Document getDocument()
        {
            return document;
        }

        /**
         * Gets the changes for an element.
         * 
         * @param elem the element
         * @return the changes
         */
        public DocumentEvent.ElementChange getChange(Element elem)
        {
            return null;
        }

    }

    /**
     * This inner class provides a document that always returns 0 on the
     * getLength() call. This forces the clear key to be disabled.
     */
    public class ValidatingTextDocument extends PlainDocument
    {
        private static final long serialVersionUID = -5123126226483953693L;

        /**
         * Constructs the document.
         * 
         * @param doc the document
         */
        public ValidatingTextDocument()
        {
            super();
        }

        /**
         * Gets the length of the document.
         * 
         * @return always returns 0;
         */
        public int getLength()
        {
            return 0;
        }
    }

    /**
     * This is to allow flexibility that a password field may not be needed for
     * match. Default is true
     * 
     * @return boolean password match allowed
     */
    public boolean isPasswordMatchAllowed()
    {
        return passwordMatchAllowed;
    }

    /**
     * This is to allow flexibility that a password field may not be needed for
     * match. Default is true
     * 
     * @param passwordMatchAllowed password match allowed
     */
    public void setPasswordMatchAllowed(boolean passwordMatchAllowed)
    {
        this.passwordMatchAllowed = passwordMatchAllowed;
    }

    /**
     * Sets the minimum Size (minimum width) of the field
     * 
     * @param minimumSize
     */
    public void setMinimumSize(int minimumSize)
    {
        Dimension minimumFieldSizeDimension = getPreferredSize();
        minimumFieldSizeDimension.width = minimumSize * getColumnWidth();
        setMinimumSize(minimumFieldSizeDimension);
    }
}