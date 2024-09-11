/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReasonBeanModel.java /main/17 2012/09/12 11:57:12 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/29/12 - added generics
 *    vtemker   08/16/11 - Fixed defect with default Reason code (BugDB Base
 *                         Bug ID: 12680277)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *    ranojha   11/11/08 - Fixed error handling for CodeList in inject method
 *    ohorne    10/29/08 - inject(..) now checks for
 *                         CodeConstantsIfc.CODE_UNDEFINED
 *    mdecama   10/20/08 - Refactored Dropdowns to use the new
 *                         CodeListManagerIfc

     $Log:
      4    360Commerce 1.3         3/29/2007 7:33:45 PM   Michael Boyd    CR
           26172 - v8x merge to trunk

           4    .v8x      1.2.1.0     3/11/2007 12:50:11 PM  Brett J. Larsen
           CR 4530
           - added method getDefaultIndex() - some beans need this to support
           default values
      3    360Commerce 1.2         3/31/2005 4:29:34 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:32 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:13:33 PM  Robert Pearse
     $
     Revision 1.5  2004/07/20 22:42:46  dcobb
     @scr 4377 Invalid Reason Code clears markdown fields
     Save the bean model in the cargo and clear the selected reason code.

     Revision 1.4  2004/04/09 21:34:51  mweis
     @scr 4206 JavaDoc updates.

     Revision 1.3  2004/03/16 17:15:18  build
     Forcing head revision

     Revision 1.2  2004/02/11 20:56:27  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.5   Jan 06 2004 11:01:54   cdb
 * Enhanced configurability. When non-editable combo boxes are used, a default value is set if a previously existing reason code hasn't been selected.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.4   Dec 30 2003 18:30:00   cdb
 * Updated to revert to last good selected reason code key to preserve last good value in case a bad one has been entered. Classes that use this can use getSelectedReason to verify the reason code while using getSelectedReasonKey to restore the previous value in the UI.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.3   Dec 23 2003 17:35:26   cdb
 * Removed unnecessary code blocks.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.2   Dec 15 2003 18:04:00   cdb
 * Modified to allow setting reason code by ID as well as by ReasonCodeText.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.1   Oct 17 2003 10:35:32   bwf
 * Added code to store and retrieve reason code keys.
 * Resolution for 3412: Feature Enhancement: Employee Discount
 *
 *    Rev 1.0   Aug 29 2003 16:11:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Feb 27 2003 14:12:12   HDyer
 * Modified setSelectedReasonCode to avoid array index out of bounds exception.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.2   Feb 13 2003 10:22:44   HDyer
 * Added setSelectedReasonCode() methods to keep data in sync. Deprecated old accessor methods. Modified headers.
 * Resolution for POS SCR-2035: I18n Reason Code support
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.ui.beans;

import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;

/**
 * This model controls Reasons.
 * 
 * @version $Revision: /main/17 $
 */
public class ReasonBeanModel extends CountryModel
{
    /**
     * Generated SerialVersionUID
     */
    private static final long serialVersionUID = -3965903785663805720L;

    /**
     * Revision Number supplied by TeamConnection.
     */
    protected static final String revisionNumber = "$Revision: /main/17 $";

    /**
     * Indicates if field is selected.
     */
    protected boolean fieldSelected;
    /**
     * Reason field is selected.
     */
    protected String fieldSelectedReason = "";

    /**
     * Reason field Reference Key
     */
    protected String fieldSelectedReasonKey = "";

    /**
     * Container of Reason codes.
     */
    protected Vector<String> fieldReasonCodes = new Vector<String>();

    /**
     * Container for Reason code keys;
     */
    protected Vector<String> fieldReasonCodeKeys = new Vector<String>();

    /**
     * Index of selected reason code.
     */
    protected int fieldSelectedIndex = 0;

    /**
     * Indicates default reason code description.
     */
    protected String defaultValue = "";

    /**
     * Constructor
     */
    public ReasonBeanModel()
    {
        super();
    }

    /**
     * Gets the reasonCodes property (Vector) value.
     * 
     * @return Vector
     * @see #setReasonCodes(Vector)
     */
    public Vector<String> getReasonCodes()
    {
        return fieldReasonCodes;
    }

    /**
     * Gets the reasonCodeKeys property (Vector) value.
     * 
     * @return Vector
     * @see #setReasonCodeKeys(Vector)
     */
    public Vector<String> getReasonCodeKeys()
    {
        return fieldReasonCodeKeys;
    }

    /**
     * Gets the selectedReason property (java.lang.String) value.
     * 
     * @return String
     * @see #setSelectedReasonCode(String)
     **/
    public String getSelectedReason()
    {
        return fieldSelectedReason;
    }

    /**
     * Gets the selectedReasonKey property (java.lang.String) value.
     * 
     * @return String
     **/
    public String getSelectedReasonKey()
    {
        return fieldSelectedReasonKey;
    }

    /**
     * Gets the index of the selected reason property.
     * 
     * @return int
     * @see #setSelectedIndex(int)
     **/
    public int getSelectedIndex()
    {
        return fieldSelectedIndex;
    }

    /**
     * Gets the index of the default reason property.
     * 
     * @return int
     **/
    public int getDefaultIndex()
    {
        return fieldReasonCodes.indexOf(defaultValue);
    }

    /**
     * Indicates default reason code description.
     * 
     * @return The default value
     */
    public String getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * Gets the selected property (boolean) value.
     * 
     * @return boolean
     * @see #setSelected(boolean)
     */
    public boolean isSelected()
    {
        return fieldSelected;
    }

    /**
     * Sets the reasonCodes property (Vector) value.
     * 
     * @param reasonCodes The new value for the property.
     * @see #getReasonCodes()
     */
    public void setReasonCodes(Vector<String> reasonCodes)
    {
        fieldReasonCodes = reasonCodes;
    }

    /**
     * Sets the reasonCodeKeys property (Vector) value.
     * 
     * @param reasonCodeKeys The new value for the property.
     * @see #getReasonCodeKeys()
     */
    public void setReasonCodeKeys(Vector<String> reasonCodeKeys)
    {
        fieldReasonCodeKeys = reasonCodeKeys;
    }

    /**
     * Sets the selected property (boolean) value.
     * 
     * @param selected whether selected or not
     * @see #isSelected()
     */
    public void setSelected(boolean selected)
    {
        fieldSelected = selected;
    }

    /**
     * Sets the selectedReason property (java.lang.String) value.
     * 
     * @param selectedReason the reason of selection
     * @see #getSelectedReason()
     * @see #setSelectedReasonCode(int)
     * @deprecated as of release 6.0. Use {@link setSelectedReasonCode(int)}
     */
    public void setSelectedReason(String selectedReason)
    {
        fieldSelectedReason = selectedReason;
    }

    /**
     * Sets the index for the selectedReason property (java.lang.String) value.
     * 
     * @param value selectedReason index
     * @see #getSelectedIndex()
     * @see #setSelectedReasonCode(int)
     * @deprecated as of release 6.0. Use {@link setSelectedReasonCode(int)}
     */
    public void setSelectedIndex(int value)
    {
        fieldSelectedIndex = value;
    }

    /**
     * Sets the fieldSelectedIndex property and uses it to set the
     * fieldSelectedReason property.
     * 
     * @param selectedReasonIndex index of selected reason
     * @see #getSelectedReason()
     * @see #getSelectedIndex()
     */
    public void setSelectedReasonCode(int selectedReasonIndex)
    {
        fieldSelectedIndex = selectedReasonIndex;
        if (fieldSelectedIndex >= 0 && fieldSelectedIndex < fieldReasonCodes.size())
        {
            fieldSelectedReason = fieldReasonCodes.get(fieldSelectedIndex);
            if (!fieldReasonCodeKeys.isEmpty())
            {
                fieldSelectedReasonKey = fieldReasonCodeKeys.get(fieldSelectedIndex);
            }
        }
    }

    /**
     * Sets the fieldSelectedReason property and uses it to set the
     * fieldSelectedIndex property.
     * 
     * @param selectedReason reason for selection
     * @see #getSelectedReason()
     * @see #getSelectedIndex()
     */
    public void setSelectedReasonCode(String selectedReason)
    {
        String oldfieldSelectedReasonKey = fieldSelectedReasonKey;
        fieldSelectedReason = selectedReason;
        fieldSelectedIndex = fieldReasonCodes.indexOf(selectedReason);

        if (!fieldReasonCodeKeys.isEmpty() && fieldSelectedIndex > -1)
        {
            fieldSelectedReasonKey = fieldReasonCodeKeys.get(fieldSelectedIndex);
        }
        else
            try
            {
                Integer.parseInt(selectedReason);
                fieldSelectedReasonKey = selectedReason;
                if (fieldReasonCodeKeys.contains(selectedReason))
                {
                    fieldSelectedIndex = fieldReasonCodeKeys.indexOf(fieldSelectedReasonKey);
                    fieldSelectedReason = fieldReasonCodes.get(fieldSelectedIndex);
                }
            }
            catch (NumberFormatException nfe)
            {
                // Revert to last value - if possible
                fieldSelectedReasonKey = oldfieldSelectedReasonKey;
            }
    }

    /**
     * Clears the selected reason code when an invalid code has been entered.
     **/
    public void clearSelectedReason()
    {
        fieldSelected = false;
        fieldSelectedReason = "";
        fieldSelectedReasonKey = "";
        fieldSelectedIndex = 0;
    }

    /**
     * Indicates default reason code description.
     * 
     * @param newValue The new value
     */
    public void setDefaultValue(String newValue)
    {
        defaultValue = newValue;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder buff = new StringBuilder();

        buff.append("Class: ReasonBeanModel Revision: " + revisionNumber + "\n");
        buff.append("fieldReasonCodes [" + fieldReasonCodes + "]\n");
        buff.append("fieldSelectedIndex [" + fieldSelectedIndex + "]\n");
        buff.append("fieldSelectedReason [" + fieldSelectedReason + "]\n");
        buff.append("fieldSelectedReasonKey [" + fieldSelectedReasonKey + "]\n");
        buff.append("fieldSelected [" + fieldSelected + "]\n");
        return (buff.toString());
    }

    /**
     * This method injects a CodeList into the Model
     * 
     * @param list
     * @param selectedReasonCode
     * @param locale
     */
    public void inject(CodeListIfc list, String selectedReasonCode, Locale locale)
    {
        if (list != null)
        {
            setReasonCodes(list.getTextEntries(locale));
            setReasonCodeKeys(list.getKeyEntries());
            setDefaultValue(list.getDefaultOrEmptyString(locale));
            if (selectedReasonCode == null || selectedReasonCode.length() == 0
                    || (CodeConstantsIfc.CODE_UNDEFINED.equals(selectedReasonCode)))
            {
                selectedReasonCode = list.getDefaultCodeString();
            }
            // Check if there is a previous selection
            if (!Util.isEmpty(selectedReasonCode) && !CodeConstantsIfc.CODE_UNDEFINED.equals(selectedReasonCode))
            {
                CodeEntryIfc codeEntry = list.findListEntryByCode(selectedReasonCode);
                if (codeEntry != null)
                {
                    String selectedReason = codeEntry.getText(locale);
                    setSelectedReasonCode(selectedReason);
                }

            }
            setSelected(true);
        }
    }
}
