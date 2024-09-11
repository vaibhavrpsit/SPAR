/* ===========================================================================
* Copyright (c) 2009, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ValidatingFormattedTextField.java /main/20 2013/09/24 15:33:43 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   09/24/13 - setOverrideMode to false to avoid replace mode on
 *                         the field
 *    abhinavs  09/06/13 - Fix to manage delete button correctly
 *    vbongu    03/01/13 - setOverrideMode to false to avoid insert mode on the
 *                         fields
 *    rrkohli   08/17/11 - fix for delete button not functioning in Enter phone
 *                         screen in check tender flow
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.util.regex.Pattern;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.MaskFormatter;
import javax.swing.text.PlainDocument;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * This field is the base class for JFormattedTextField fields that can be
 * validated. This is similar to the ValidatingTextField class but this one uses
 * JFormattedTextField instead of JTextField. This class allows you to have a
 * field that can take a format pattern
 */
public class ValidatingFormattedTextField extends JFormattedTextField implements ValidatingFieldIfc, FocusListener
{
    private static final long serialVersionUID = 2057174396671459717L;

    /** id for look and feel */
    public final static String uiClassID = "RequiredFieldUI";

    /** flag of whether an empty field is valid */
    protected boolean emptyAllowed = true;

    /** whether or not this field is required */
    protected boolean required;

    /** error message */
    protected String errorMessage = "";

    /** Label that ID's the field on the screen. */
    protected JLabel label = null;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/20 $";

    protected String emptyTextFormat = "";

    /** contains the regular expression string for validation **/
    private String regexp = "";

    /**
     * specifies whether the field value returned on getValue should contain the
     * format characters or not
     */
    private boolean containsLiterals = false;

    /** Label that ID's the field on the screen. */
    protected DocumentListener POSDocumentListener = null;

    /** The base bean adapter that ultimately contains this object. */
    protected BaseBeanAdapter baseBeanAdapter = null;
    
    private static final Logger logger = Logger.getLogger(ValidatingFormattedTextField.class);
    /**
     * Class constructor.
     */
    public ValidatingFormattedTextField(String pattern, boolean containsLiterals, String maxlength)
    {
        int max = Integer.parseInt(maxlength);
        setDocument(new ValidatingFormattedTextDocument(max));
        setFocusLostBehavior(JFormattedTextField.PERSIST);
        this.containsLiterals = containsLiterals;
        setFormat(pattern);
        UIFactory.getInstance().configureUIComponent(this, "ValidatingField");
        addFocusListener(this);
    }

    /**
     * Sets the format for the field
     * 
     * @param pattern The format pattern
     * @see MaskFormatter for more info on patterns
     */
    public void setFormat(String pattern)
    {
        try
        {
            DefaultFormatter mf = null;
            if (pattern == null || pattern.equals(""))
            {
                mf = new DefaultFormatter();
                mf.setOverwriteMode(false);
            }
            else
            {
                mf = new MaskFormatter(pattern);
                ((MaskFormatter) mf).setValueContainsLiteralCharacters(containsLiterals);
            }            
            setText("");
            DefaultFormatterFactory factory = new DefaultFormatterFactory(mf);
            setFormatterFactory(factory);
            setValue("");
            emptyTextFormat = getText();
            addFocusListener(this);
        }
        catch (ParseException e)
        {
        }
    }

    /**
     * Sets the regular expression validation format for the field
     * 
     * @param regexp The regular expresssion against which the field value is
     *            validated
     */
    public void setValidationRegexp(String regexp)
    {
        this.regexp = regexp;
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
     * Sets the flag for allowing an empty string to be valid.
     * 
     * @param allowEmpty true if empty field is valid, false otherwise
     */
    public void setEmptyAllowed(boolean allowEmpty)
    {
        this.emptyAllowed = allowEmpty;
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
     * Sets the label associated with the field and configures the error message
     * based on the label text.
     * 
     * @param label the label to use
     */
    public void setLabel(JLabel label)
    {
        this.label = label;
        setErrorMessage(getFieldName());
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
     * Determines whether the current field information is valid and returns the
     * result.
     * 
     * @return true if the current field entry is valid, false otherwise
     */
    public boolean isInputValid()
    {
        boolean rv = true;

        // check for empty required fields
        String txt = getText();
        if (required && ((txt == null) || txt.trim().equals("")))
        {
            rv = false;
            return rv;
        }

        try
        {
            String text = getText();
            if (!text.equals(emptyTextFormat))
            {
                // check whether the text is valid for the current field format
                commitEdit();
            }
        }
        catch (ParseException ex)
        {
            rv = false;
        }

        // validate the text against the regular expression specified
        if (rv)
        {
            if (regexp != null && (regexp.length() > 0))
            {
                rv = Pattern.matches(regexp, getText());
            }
        }

        return rv;
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
     * Returns the field name to be used in error messages.
     * 
     * @return the field name
     */
    public String getFieldName()
    {
        String displayText = this.getLabel().getText();
        // retrieve name only
        String terminator = UIUtilities.retrieveCommonText("LabelTerminator");
        if (!(displayText.indexOf(terminator) < 0))
        {
            displayText = displayText.substring(0, displayText.indexOf(terminator));
        }
        return (displayText);
    }

    /**
     * Returns the empty text format
     * 
     * @return String representation of the empty text format
     */
    public String emptyTextFormat()
    {
        return (emptyTextFormat);
    }

    /**
     * Returns the field value
     * 
     * @return String representation of the field input
     */
    public String getFieldValue()
    {
        String value = "";
        try
        {
            commitEdit();
            value = (String) getValue();
        }
        catch (ParseException ex)
        {
        }
        return value;
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
     * Sets the document listener container. It is used to get the document
     * listner to use when focus has been gained.
     * 
     * @param listener the document listener container.
     * @param bean The POS Base Bean Adapter
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
        String strResult = new String("Class: ValidatingFormattedTextField (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        return (strResult);
    }

    /**
     * Sets minimum size for the component.
     */
    public void setMinimumSize(int minimumSize)
    {
        Dimension minimumFieldSizeDimension = getPreferredSize();
        minimumFieldSizeDimension.width = minimumSize * getColumnWidth();
        setMinimumSize(minimumFieldSizeDimension);
    }

    /**
     * This inner class provides a document that allows maxlength input
     */
    public class ValidatingFormattedTextDocument extends PlainDocument
    {
        private static final long serialVersionUID = -5805377601418859692L;

        private int max;

        ValidatingFormattedTextDocument(int maxlength)
        {
            max = maxlength;
        }

        public void replace(int offs, int length, String str, AttributeSet a) throws BadLocationException
        {
            if ((getLength() + str.length()) > max)
            {
                return;
            }
            super.replace(offs, length, str, a);
        }
        
        /**
         * Returns true if the document has user input. Currently, this method
         * is simply looking for the existence of alpha-numeric characters.
         *
         * @return
         */
        public boolean hasInput()
        {
            try
            {
                String text = getText(0, getLength());
                for (int i = text.length()-1; i >= 0; i--)
                {
                    if (Character.isLetterOrDigit(text.charAt(i)))
                    {
                        return true;
                    }
                }
            }
            catch (BadLocationException e)
            {
                logger.error("Unable to get text from validating document.", e);
            }
            return false;
        }

    }

    /**
     * Overridden to use the custom text field ui component.
     * 
     * @return custom text field ui component ID
     */
    public String getUIClassID()
    {
        return uiClassID;
    }

    @Override
    public void focusGained(FocusEvent e)
    {
        String text = getText();
        if (isEditable() && text != null && text.length() > 0)
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
                ValidatingFormattedTextDocumentEvent evt = new ValidatingFormattedTextDocumentEvent(getDocument());
                POSDocumentListener.changedUpdate(evt);
            }
        }
    }

    @Override
    public void focusLost(FocusEvent e)
    {
        if (POSDocumentListener != null)
        {
            ValidatingFormattedTextDocument doc = new ValidatingFormattedTextDocument(30);

            ValidatingFormattedTextDocumentEvent evt = new ValidatingFormattedTextDocumentEvent(doc);
            POSDocumentListener.changedUpdate(evt);
            getDocument().removeDocumentListener(POSDocumentListener);
        }

    }

    /**
     * This inner class provides a document event class to send to the document
     * listener when the focus changes.
     */
    public class ValidatingFormattedTextDocumentEvent implements DocumentEvent
    {
        /** Document */
        Document document = null;

        /**
         * Constructs the event
         * 
         * @param doc the document
         */
        public ValidatingFormattedTextDocumentEvent(Document doc)
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
}