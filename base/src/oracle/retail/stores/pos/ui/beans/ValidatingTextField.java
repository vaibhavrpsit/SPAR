/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ValidatingTextField.java /main/27 2013/06/04 17:39:14 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/04/13 - removed toString method since it is less useful than
 *                         the normal toString
 *    cgreene   10/29/12 - tweak implementation of search field with icon
 *    vbongu    10/11/12 - Item inquiry with magnifying glass icon changes
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    ranojha   12/01/08 - Fixed paddings and text for error messages
 *
 * ===========================================================================
 * $Log:
 *   4    I18N_P2    1.2.1.0     12/26/2007 9:54:39 AM  Maisa De Camargo CR
 *        29822 - I18N - Fixed Collapsing of Input Fields when labels are
 *        expanded.
 *   3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:26:43 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:15:30 PM  Robert Pearse
 *
 *  Revision 1.5  2004/03/22 19:27:00  cdb
 *  @scr 3588 Updating javadoc comments
 *
 *  Revision 1.4  2004/03/22 06:17:50  baa
 *  @scr 3561 Changes for handling deleting return items
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
 *    Rev 1.2   Jan 27 2004 17:12:20   cdb
 * Altered behavior so that an entry that is only spaces does not count as valid required input.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.1   Sep 16 2003 17:53:42   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:13:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Apr 16 2003 12:23:12   baa
 * defect fixes
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.1   Aug 07 2002 19:34:28   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:56:24   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:36:50   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * This field is the base class for fields that can be validated.
 * 
 * @version $Revision: /main/27 $;
 * @deprecated as of 13.4.1. Use {@link oracle.retail.stores.foundation.manager.ui.jfc.ValidatingTextField} instead.
 */
public class ValidatingTextField extends JTextField implements ValidatingFieldIfc, FocusListener
{
    private static final long serialVersionUID = -5071612781384562230L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/27 $";

    /** id for look and feel */
    public final static String uiClassID = "RequiredFieldUI";
    /** Search field button icon name for plaf properties. */
    public static final String ICON_SEARCH_FIELD = "searchFieldIcon";
    public static final String ICON_PRESSED_SEARCH_FIELD = "searchFieldIconPressed";

    /** flag of whether an empty field is valid */
    protected boolean emptyAllowed = true;

    /** the minimum length of the field */
    protected int minLength = 1;

    /** whether or not this field is required */
    protected boolean required;

    /** error message */
    protected String errorMessage = "";

    /** Label that ID's the field on the screen. */
    protected JLabel label = null;

    /** Label that ID's the field on the screen. */
    protected DocumentListener POSDocumentListener = null;

    /** The base bean adapter that ultimately contains this object. */
    protected BaseBeanAdapter baseBeanAdapter = null;

    /** Search field button members. */
    protected List<ActionListener> searchListenerList = new ArrayList<ActionListener>();
    protected JButton searchButton;
    protected int horizontalSearchButtonAlignment = TRAILING;

    /**
     * Class constructor.
     */
    public ValidatingTextField()
    {
        this("");
    }

    /**
     * Class constructor.
     * 
     * @param msg the default text to appear in the field
     */
    public ValidatingTextField(String msg)
    {
        super(msg);
        // TODO this is being called twice. Once here and again in UIFactory#createXXX.
        // Likely this does not need to be here and should only be in UIFactory.
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
        if (!emptyAllowed && (Util.isEmpty(getText()) || Util.isEmpty(getText().trim())))
        {
            rv = false;
        }
        return rv;
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
        // retrieve name only
        if (!(displayText.indexOf(':') < 0))
        {
            displayText = displayText.replaceAll(":", "");
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
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setHorizontalTextPosition(SwingConstants.CENTER);
        }

        return label;
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
     * Returns user interface locale.
     * 
     * @return String representation of object
     */
    public Locale getLocale()
    {
        return (LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
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
     * Position of the search button. Defaults to {@link SwingConstants#TRAILING}.
     * 
     * @return the horizontalSearchButtonAlignment
     * @see {@link SwingConstants#LEADING}
     * @see {@link SwingConstants#TRAILING}
     */
    public int getHorizontalSearchButtonAlignment()
    {
        return horizontalSearchButtonAlignment;
    }

    /**
     * Position of the search button. Defaults to {@link SwingConstants#TRAILING}.
     * 
     * @param horizontalSearchButtonAlignment the horizontalSearchButtonAlignment to set
     * @see {@link SwingConstants#LEADING}
     * @see {@link SwingConstants#TRAILING}
     */
    public void setHorizontalSearchButtonAlignment(int horizontalSearchButtonAlignment)
    {
        this.horizontalSearchButtonAlignment = horizontalSearchButtonAlignment;
    }

    /**
     * Adds an action listener for the search field. The {@link #searchButton}
     * is lazily initialized when the action listeners are added.
     * 
     * @param l an action listener for the search button
     */
    public void addSearchActionListener(ActionListener l)
    {
        if (l != null && searchButton == null)
        {
            searchButton = initializeSearchButton();
        }
        searchButton.addActionListener(l);
    }

    /**
     * Remove the specified search button action listener. If there are no
     * more listeners, the search button is removed.
     * 
     * @param l
     */
    public void removeSearchActionListener(ActionListener l)
    {
        if (l != null && searchButton != null)
        {
            searchButton.removeActionListener(l);
            if (searchButton.getActionListeners().length == 0)
            {
                remove(searchButton);
                searchButton = null;
            }
        }
    }

    /**
     * Returns whether the field should display a search button.
     */
    public boolean isSearchField()
    {
        return (searchButton != null);
    }

    /**
     * Create the JButton for the search field that is not focusable, is
     * transparent, and has a small inset border. The {@link #searchFieldIcon}
     * is used to render an icon and any action events trigger {@link #fireActionPerformed()};
     *
     * @return
     */
    protected JButton initializeSearchButton()
    {
        setLayout(new BorderLayout());
        JButton button = new JButton();
        button.setFocusable(false);
        button.setName("searchButton");
        button.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        button.setActionCommand(CommonActionsIfc.NEXT);
        button.setIcon(new ImageIcon(UIFactory.getInstance().getImage(ICON_SEARCH_FIELD, button)));
        button.setPressedIcon(new ImageIcon(UIFactory.getInstance().getImage(ICON_PRESSED_SEARCH_FIELD, button)));
        button.setOpaque(false);
        button.addActionListener(new ActionListener()
        {
           public void actionPerformed(ActionEvent e)
           {
               fireActionPerformed();
           }
        });
        if (getHorizontalSearchButtonAlignment() == TRAILING)
        {
            add(button, BorderLayout.EAST);
        }
        else
        {
            add(button, BorderLayout.WEST);
        }
        return button;
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
        private static final long serialVersionUID = 3697707071912092482L;

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
}
