/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ValidatingTextAreaField.java /main/16 2012/10/17 11:51:51 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:43 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:30 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:13:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:56:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   15 Apr 2002 09:36:48   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:52:50   msg
 * Initial revision.
 *
 *    Rev 1.3   16 Feb 2002 10:17:20   baa
 * fix required field logic
 * Resolution for POS SCR-1306: Invalid Data Notice missing after selecting Enter with no data for text area fields
 *
 *    Rev 1.2   15 Feb 2002 16:33:42   baa
 * ui fixes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   Jan 19 2002 10:32:38   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   19 Dec 2001 17:44:44   baa
 * Initial revision.
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.1   27 Oct 2001 10:24:54   mpm
 * Merged Pier 1, Virginia ABC changes.
 *
 *    Rev 1.0   Sep 21 2001 11:34:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:16:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;

import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * This field is the base class for fields that can be validated.
 * 
 * @version $Revision: /main/16 $
 **/
public class ValidatingTextAreaField extends JTextArea implements ValidatingFieldIfc, FocusListener
{
    private static final long serialVersionUID = -7552100789352385546L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/16 $";

    /** id for look and feel */
    public final static String uiClassID = "RequiredFieldUI";

    /** flag of whether an empty field is valid */
    protected boolean emptyAllowed = true;

    /** the minimum length of the field */
    protected int minLength = 1;

    /** error message */
    protected String errorMessage = "";

    /** Label that ID's the field on the screen. */
    protected JLabel label = null;

    /** whether or not this field is required */
    protected boolean required;

    /** Label that ID's the field on the screen. */
    protected DocumentListener POSDocumentListener = null;

    /** The base bean adapter that ultimately contains this object. */
    protected BaseBeanAdapter baseBeanAdapter = null;

    /**
     * Class constructor.
     */
    public ValidatingTextAreaField()
    {
        this("");
    }

    /**
     * Class constructor.
     * 
     * @param msg the default text to appear in the field
     */
    public ValidatingTextAreaField(String msg)
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
     * Returns whether the field is required.
     * 
     * @return true if required, false otherwise
     */
    public boolean isRequired()
    {
        return required;
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
        if (!emptyAllowed && "".equals(getText()))
        {
            rv = false;
        }
        return rv;
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
            label.setText("");
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
     * Sets whether the field is required.
     * 
     * @param propValue true if required false if not
     */
    public void setRequired(boolean propValue)
    {
        required = propValue;
    }

    /**
     * Called when the component gets focus.
     * 
     * @param e the focus event
     */
    public void focusGained(FocusEvent e)
    {
        String text = getText();
        if (isEditable() && text != null && text.length() > 0)
        {
            selectAll();
        }
        if (!isEditable())
        {
            transferFocus();
        }
        else
        {
            if (POSDocumentListener != null)
            {
                getDocument().addDocumentListener(POSDocumentListener);
                ValidatingTextDocumentEvent evt = new ValidatingTextDocumentEvent(getDocument());
                POSDocumentListener.changedUpdate(evt);
            }
            if (baseBeanAdapter != null)
            {
                baseBeanAdapter.setCurrentComponent(this);
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
            // set length to -1 to indicate focus lost
            // this allows next button to stay enabled when non text field
            // components have the focus
            evt.setLength(-1);
            POSDocumentListener.changedUpdate(evt);
            getDocument().removeDocumentListener(POSDocumentListener);
        }
    }

    /**
     * Sets the document listener container. It is used to get the document
     * listner to use when focus has been gained.
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
        String strResult = new String("Class: ValidatingTextAreaField (Revision " + getRevisionNumber() + ") @"
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
        return (revisionNumber);
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
         * length of field
         */
        int length = 0;

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
         * Gets the length of the document.
         * 
         * @return length of the document
         */
        public int getLength()
        {
            return length;
        }

        /**
         * Sets the length of the document.
         * 
         * @param aValue length of the document
         */
        protected void setLength(int aValue)
        {
            length = aValue;
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
        private static final long serialVersionUID = -9158289227075865980L;

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
         * @return length of the document
         */
        public int getLength()
        {
            return 0;
        }
    }
}