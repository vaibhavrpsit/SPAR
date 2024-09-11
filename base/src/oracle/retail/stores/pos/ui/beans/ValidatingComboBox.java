/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ValidatingComboBox.java /main/24 2013/04/23 16:11:57 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  04/23/13 - Set the cursor position to the start for selected
 *                         text
 *    jswan     11/15/12 - Modified to support parameter controlled return
 *                         tenders.
 *    vbongu    10/01/12 - move ui part to EYSValidatingComboBoxUI class
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   11/07/08 - Removed the mapping of the Enter Key in the
 *                         Dropdowns
 *
 * ===========================================================================
 * $Log:
 *   9    I18N_P2    1.4.1.3     2/1/2008 8:52:23 AM    Sandy Gu        fix the
 *         class cast exception while setting validating combobox popup width
 *   8    I18N_P2    1.4.1.2     1/8/2008 4:34:28 PM    Maisa De Camargo CR
 *        29826 - Setting the size of the combo boxes. This change was
 *        necessary because the width of the combo boxes used to grow
 *        according to the length of the longest content. By setting the size,
 *         we allow the width of the combo box to be set independently from
 *        the width of the dropdown menu.
 *   7    I18N_P2    1.4.1.1     1/4/2008 5:00:24 PM    Maisa De Camargo CR
 *        29826 - Setting the size of the combo boxes. This change was
 *        necessary because the width of the combo boxes used to grow
 *        according to the length of the longest content. By setting the size,
 *         we allow the width of the combo box to be set independently from
 *        the width of the dropdown menu.
 *   6    I18N_P2    1.4.1.0     12/26/2007 9:54:39 AM  Maisa De Camargo CR
 *        29822 - I18N - Fixed Collapsing of Input Fields when labels are
 *        expanded.
 *   5    360Commerce 1.4         10/11/2007 12:31:18 PM Peter J. Fierro
 *        Changes to define popup widths independently of max columns
 *   4    360Commerce 1.3         10/8/2007 1:59:28 PM   Maisa De Camargo Added
 *         the method setColumns to set the width of the display field.
 *   3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:26:43 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:15:30 PM  Robert Pearse
 *
 *  Revision 1.5.4.1  2004/12/06 22:13:34  csuehs
 *  @scr 7724 override requestFocusInWindow to delegate to editor component
 *
 *  Revision 1.5  2004/03/22 19:27:00  cdb
 *  @scr 3588 Updating javadoc comments
 *
 *  Revision 1.4  2004/03/22 03:49:28  cdb
 *  @scr 3588 Code Review Updates
 *
 *  Revision 1.3  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.8   Jan 20 2004 17:41:28   cdb
 * Added scroll by keyboard functionality for editable combo box.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.7   Jan 15 2004 17:50:50   epd
 * fixed isInputValid() null pointer
 *
 *    Rev 1.6   Jan 07 2004 10:03:14   cdb
 * If setting selected item to null, text editor was trying to call toString() on the null object. Updated so that null will place an empty string in the text editor. Since this defect exists in a UI Bean, it does not qualify for unit testing.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 * Resolution for 3624: System crashes when PO is selected to tender a transaction
 *
 *    Rev 1.5   Jan 05 2004 13:04:38   cdb
 * Updated to force JComboBox to have same value in text editor as is selected.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.4   Dec 30 2003 16:56:38   cdb
 * Added more powerful way of removing key mapping for Enter Key of editor field of combo box. unregisterKeyboardAction wasn't working.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.3   Dec 29 2003 14:46:18   cdb
 * Cleanup of code, UI quirks.
 *
 *    Rev 1.2   Dec 23 2003 17:41:34   cdb
 * Activating Editable combo box.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.1   Oct 31 2003 16:53:10   epd
 * Product file replaced with GAP ported file
 *
 *    Rev 1.0   Aug 29 2003 16:13:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 14:56:18   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:36:44   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

import org.apache.log4j.Logger;

/**
 * Validating Combo Box
 * 
 * @version $Revision: /main/24 $
 */
public class ValidatingComboBox<E> extends JComboBox<E> implements ValidatingFieldIfc, FocusListener
{
    private static final long serialVersionUID = 2942655456530462497L;

    /** Error and debug logger. */
    private static final Logger logger = Logger.getLogger(ValidatingComboBox.class);

    /** Revision number supplied by version control. */
    public static final String revisionNumber = "$Revision: /main/24 $";

    /** id for look and feel */
    public final static String uiClassID = "ValidatingComboBoxUI";

    /** flag of whether an empty field is valid */
    protected boolean emptyAllowed = true;

    /** error message */
    protected String errorMessage = "";

    /** field label */
    protected JLabel label = null;

    /** True if this element is required */
    protected boolean required;

    /** Border of the validating comboBox */
    protected boolean showDisabled = false;

    /**
     * The document listener responding to keystrokes in text field area of
     * combobox.
     */
    protected DocumentListener POSDocumentListener = null;

    /** The base bean adapter that ultimately contains this object. */
    protected BaseBeanAdapter baseBeanAdapter = null;

    /** the width of the popup menu created by this combobox */
    protected int popupWidth;

    /** the width of a column */
    protected int columnWidth = 0;

    /**
     * Class constructor.
     */
    public ValidatingComboBox()
    {
        initialize();
    }

    /**
     * Class constructor.
     * 
     * @param data The combo box model
     */
    public ValidatingComboBox(ComboBoxModel<E> data)
    {
        super(data);
        initialize();
    }

    /**
     * Class constructor.
     * 
     * @param data An array of objects for insertion into combo box
     */
    public ValidatingComboBox(E[] data)
    {
        super(data);
        initialize();
    }

    /**
     * Class constructor.
     * 
     * @param data A vector of objects for insertion into combo box
     */
    public ValidatingComboBox(Vector<E> data)
    {
        super(data);
        initialize();
    }

    /**
     * Configures the combo box.
     */
    protected void initialize()
    {
        UIFactory.getInstance().configureUIComponent(this, "ValidatingComboBox");
        ((JTextField) getEditor().getEditorComponent()).addFocusListener(this);
    }

    /**
     * Overridden to use the custom combo box ui component.
     * 
     * @return custom combo box ui component ID
     */
    public String getUIClassID()
    {
        return uiClassID;
    }

    /**
     * Returns whether the combobox is required.
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
     * Sets the width of the popup menu.
     * 
     * @param int
     */
    public void setPopupWidth(int width)
    {
        popupWidth = width;
    }

    /**
     * Returns the size of the popup menu.
     * 
     * @param int
     */
    public Dimension getPopupSize()
    {
        Dimension size = getSize();
        if (popupWidth < 1)
        {
            popupWidth = size.width;
        }
        return new Dimension(popupWidth, size.height);
    }

    /**
     * Override parent to remove escape and enter behavior.
     */
    public void addNotify()
    {
        super.addNotify();

        InputMap iMap = (InputMap) UIManager.get("ComboBox.ancestorInputMap");
        iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "none");
        iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "none");

        ((JTextField) getEditor().getEditorComponent()).getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "none");

        if (isEditable())
        {
            activateScrollFromTextField();
        }
    }

    /**
     * Adds the ability to popup and scroll through the list.
     */
    protected void activateScrollFromTextField()
    {
        // More complicated behavior required for sake of up and down arrows
        // in editable validating combo boxes
        final Action upAction = new AbstractAction("upAction")
        {
            /**
             * 
             */
            private static final long serialVersionUID = 7296443294978916226L;

            public void actionPerformed(ActionEvent e)
            {
                if (isPopupVisible() && getSelectedIndex() > 0)
                {
                    setSelectedIndex(getSelectedIndex() - 1);
                }
                setPopupVisible(true);
            }
        };

        final Action downAction = new AbstractAction("downAction")
        {
            /**
             * 
             */
            private static final long serialVersionUID = 5734790116140598818L;

            public void actionPerformed(ActionEvent e)
            {
                if (isPopupVisible() && getSelectedIndex() < getItemCount() - 1)
                {
                    setSelectedIndex(getSelectedIndex() + 1);
                }
                setPopupVisible(true);
            }
        };
        ((JTextField) getEditor().getEditorComponent()).getKeymap().addActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), upAction);
        ((JTextField) getEditor().getEditorComponent()).getKeymap().addActionForKeyStroke(
                KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), downAction);

    }

    /**
     * Sets the flag for allowing an empty string to be valid.
     * 
     * @param allowed true if empty field is valid, false otherwise
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
        boolean rv = true;

        if (!emptyAllowed
                && (getSelectedItem() == null || ((String) getSelectedItem()).length() == 0 || (!isEditable() && getSelectedIndex() == -1)))
        {
            rv = false;
        }
        return rv;
    }

    /**
     * Overrides setSelectedItem in JComboBox. Ensures item in combo box's text
     * field matches the selected value
     * 
     * @param anObject The object representing the selected value
     */
    @Override
    public void setSelectedItem(Object anObject)
    {
        if (anObject == null)
        {
            anObject = "";
        }
        super.setSelectedItem(anObject);
        // This is to ensure text field and list have same selection
        if (!((JTextField) getEditor().getEditorComponent()).getText().equals(anObject))
        {
            ((JTextField) getEditor().getEditorComponent()).setText(anObject.toString());
        }
        // This is to set the cursor to the begining of the text
        ((JTextField) getEditor().getEditorComponent()).setCaretPosition(0);
    }

    /**
     * Overrides getSelectedItem in JComboBox. Ensure's item in combo box's text
     * field matches the selected value
     * 
     * @return The object representing the selected value
     */
    public Object getSelectedItem()
    {
        // This is to ensure text field and list have same selection
        if (!((JTextField) getEditor().getEditorComponent()).getText().equals(super.getSelectedItem()))
        {
            setSelectedItem(((JTextField) getEditor().getEditorComponent()).getText());
        }
        return super.getSelectedItem();
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
        return (label);
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
        String displayText = this.getLabel().getText();
        if (!(displayText.indexOf(':') < 0))
        {
            displayText = displayText.replaceAll(":", "");
        }
        setErrorMessage(displayText);
    }

    /**
     * Sets the width of the display field This method is equivalent to the
     * JTextField.getColumnWidth() but adapted to the JComboBox.
     * 
     * @param columns
     */
    @SuppressWarnings("unchecked")
    public void setColumns(int columns)
    {
        if (columns > 0)
        {
            StringBuilder prototypeDisplayValue = new StringBuilder();
            for (int i = 0; i < columns; i++)
            {
                prototypeDisplayValue.append('m');
            }
            try
            {
                setPrototypeDisplayValue((E)prototypeDisplayValue.toString());
            }
            catch (Exception e)
            {
                logger.debug("Attempted to setPrototypeDisplayValue for column width, but error occurred.", e);
            }
        }
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
     * Work around bug in IBM's AWT on Linux and 4690.
     */
    public boolean requestFocusInWindow()
    {
        if (isEditable())
        {
            return ((JTextField) getEditor().getEditorComponent()).requestFocusInWindow();
        }
        
        return super.requestFocusInWindow();
    }

    /**
     * Called when the component gets focus.
     * 
     * @param e the focus event
     */
    public void focusGained(FocusEvent e)
    {
        if (isEditable())
        {
            String text = (String) getEditor().getItem();
            if (text != null && text.length() > 0)
            {
                getEditor().selectAll();
            }
            if (!isFocusable())
            {
                transferFocus();
            }
            else
            {
                if (baseBeanAdapter != null)
                {
                    baseBeanAdapter.setCurrentComponent((JTextField) getEditor().getEditorComponent());
                }
                if (POSDocumentListener != null)
                {
                    ((JTextField) getEditor().getEditorComponent()).getDocument().addDocumentListener(
                            POSDocumentListener);
                    ValidatingTextDocumentEvent evt = new ValidatingTextDocumentEvent(((JTextField) getEditor()
                            .getEditorComponent()).getDocument());
                    POSDocumentListener.changedUpdate(evt);
                }
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
        if (isEditable())
        {
            if (POSDocumentListener != null)
            {
                ValidatingTextDocument doc = new ValidatingTextDocument();

                ValidatingTextDocumentEvent evt = new ValidatingTextDocumentEvent(doc);
                POSDocumentListener.changedUpdate(evt);
                ((JTextField) getEditor().getEditorComponent()).getDocument().removeDocumentListener(
                        POSDocumentListener);
            }
        }
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

    /* (non-Javadoc)
     * @see java.awt.Component#toString()
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: ValidatingComboBox (Revision " + getRevisionNumber() + ") @" + hashCode());
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
        private static final long serialVersionUID = -5531947083178115552L;

        /**
         * Constructs the document.
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

    /**
     * Returns the column width. The meaning of what a column is can be
     * considered a fairly weak notion for some fonts. This method is used to
     * define the width of a column. By default this is defined to be the width
     * of the character <em>m</em> for the font used. This method can be
     * redefined to be some alternative amount
     * 
     * @return the column width >= 1
     */
    protected int getColumnWidth()
    {
        if (columnWidth == 0)
        {
            FontMetrics metrics = getFontMetrics(getFont());
            columnWidth = metrics.charWidth('m');
        }
        return columnWidth;
    }

    /**
     * Overriding the setModel method in order to set the popupWidth before
     * setting up the model
     * 
     * @param model
     */
    public void setModel(ValidatingComboBoxModel<E> model)
    {
        super.setModel(model);
    }

    /**
     * Overriding the setModel method in order to set the popupWidth before
     * setting up the model
     * 
     * @param model
     */
    public void setModel(POSListModel<E> model)
    {
        super.setModel(model);
    }

    /**
     * Set the PopupWidth based on the size of the largest element
     * 
     * @param dropdownChoices
     * @deprecated as of 14.1. No replacement.
     */
    protected void setPopupWidth(Object[] dropdownChoices)
    {
        // This seems like a bad idea. Create a completely different combobox to determine the width?
        // Deprecateing this method
        ValidatingComboBox<Object> tmp = new ValidatingComboBox<Object>(dropdownChoices);
        Dimension d = tmp.getPreferredSize();
        setPopupWidth(d.width);
        tmp = null;
    }

    /**
     * Set the PopupWidth based on elements in the model
     * @deprecated as of 14.1. No replacement.
     */
    protected void resetPopupWidth()
    {
        // waste of CPU converting to array. Deprecating.
        ComboBoxModel<E> model = getModel();
        Object[] dropdownChoices = new Object[model.getSize()];
        for (int i = 0; i < model.getSize(); i++)
        {
            dropdownChoices[i] = model.getElementAt(i);
        }
        setPopupWidth(dropdownChoices);
    }

    /**
     * Add an item to the combo box
     * 
     * @param objects the items to add
     */
    public void addItems(E[] objects)
    {
        for (int i = 0; i < objects.length; i++)
        {
            super.addItem(objects[i]); // call the super class addItem so that
                                       // we only reset popup width once
        }
    }

    /**
     * @return the showDisabled
     */
    public boolean isShowDisabled()
    {
        return showDisabled;
    }

    /**
     * @param showDisabled the showDisabled to set
     */
    public void setShowDisabled(boolean showDisabled)
    {
        this.showDisabled = showDisabled;
    }

}