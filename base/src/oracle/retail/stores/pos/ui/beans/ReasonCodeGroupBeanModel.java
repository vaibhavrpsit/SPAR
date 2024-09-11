/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReasonCodeGroupBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/22/10 - update to use java.lang.Comparable
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         5/4/2007 4:27:15 PM    Owen D. Horne
 *         CR#26038 added isDefaultRequired()
 *    3    360Commerce 1.2         3/31/2005 4:29:34 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:32 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:33 PM  Robert Pearse
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Oct 01 2003 13:47:28   lzhao
 * They were sorted by parameter names defined in application.xml rather than the parameter name value in parameterText properties file.
 * Resolution for 3094: List of parameters not in alphabetical order in Tender parameter group
 *
 *    Rev 1.0   Aug 29 2003 16:11:48   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   May 21 2003 11:23:08   adc
 * Put back the missing method
 *
 *    Rev 1.1   May 20 2003 10:40:48   adc
 * changes for internationalization
 * Resolution for 2286: Edit Reason Code Screen not displaying correct Name and ID information
 *
 *    Rev 1.0   Apr 29 2002 14:48:00   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:57:06   msg
 * Initial revision.
 *
 *    Rev 1.2   21 Jan 2002 12:21:12   KAC
 * Added parameterGroup.
 * Resolution for POS SCR-672: Create List Parameter Editor
 *
 *    Rev 1.0   Sep 21 2001 11:36:04   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:16:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Enumeration;
import java.util.Vector;

import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This class describes a related group of reason codes.
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class ReasonCodeGroupBeanModel extends POSBaseBeanModel implements Comparable<Object>
{
    private static final long serialVersionUID = 6496635288754983489L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected String groupName = "";
    protected String groupLevel = ReasonCodesCommon.STORE;
    protected String defaultReasonCode = "";

    /** The value displayed on the parameter list. **/
    protected String parameterNameContent = "";

    /** The name of the parameter group, e.g. "tender"   **/
    protected String parameterGroup = "";

    /** The value displayed for a modifiable group.  **/
    public static final String MODIFIABLE = "Yes";
    /** The value displayed for a nonmodifiable group.  **/
    public static final String NONMODIFIABLE = "No";

    /**
     * The choices from which the user can pick for modifiability - Yes or No.
     */
    protected static Vector<String> modifiableChoices = new Vector<String>(2);

    /** The selected modifiable value. **/
    protected String modifiableValue = MODIFIABLE;

    /** The choices from which the user can pick. **/
    protected Vector<ReasonCode> reasonCodes = null;

    /** The selected reason code. **/
    protected ReasonCode reasonCodeSelected = null;

    /**
     * Flag indicates whether the original fields of the parameter have been
     * modified.
     */
    protected boolean modified = false;

    /**
     * This service handles both reason codes, which reside in the DB and lists
     * of text, which are parameters.
     */
    protected boolean modifyingParameter = false;

    /**
     * Some of the reason code values are numeric and some are alpha numeric
     * this boolean indicates that ui should accept numeric characters only for
     * this code.
     */
    protected boolean idIsNumeric = true;

    /**
     * Database source of data; this data must preserved in order to rebuild the
     * bussiness object so that it can be saved back the the DB.
     */
    protected String source = "";

    /**
     * store identifier
     */
    protected String storeID = "";

    /**
     * The index of the selected item in the value choices, ranges from 0 to
     * length - 1.
     */
    protected int reasonCodeSelectionIndex = 0;

    /** Possible choices. **/
    static
    {
        modifiableChoices.addElement(MODIFIABLE);
        modifiableChoices.addElement(NONMODIFIABLE);
    }

    /**
     * Returns the value of the GroupName field.
     *
     * @return the value of GroupName
     */
    public String getGroupName()
    {
        return groupName;
    }

    public String getParameterGroup()
    {
        return parameterGroup;
    }

    /**
     * Returns the value of the ParameterNameContent field.
     *
     * @return the value of ParameterNameContent
     */
    public String getParameterNameContent()
    {
        return parameterNameContent;
    }

    /**
     * Returns the value of the GroupLevel field.
     *
     * @return the value of GroupLevel
     */
    public String getGroupLevel()
    {
        return groupLevel;
    }

    /**
     * Returns whether this reasonCode is modifiable.
     *
     * @return true if modifiable; false otherwise
     */
    public boolean getModifiable()
    {
        return (MODIFIABLE.equals(modifiableValue));
    }

    /**
     * Returns whether this reasonCode was modifiable.
     *
     * @return true if modified; false otherwise
     */
    public boolean getModified()
    {
        return modified;
    }

    /**
     * Returns the selected value for modifiable.
     *
     * @return the selected value for modifiable
     */
    public String getModifiableValue()
    {
        return modifiableValue;
    }

    /**
     * Returns the modifiable choices.
     *
     * @return the modifiable choices
     */
    public Vector<String> getModifiableChoices()
    {
        return modifiableChoices;
    }

    /**
     * Returns the value of the DefaultReasonCode field.
     *
     * @return the value of DefaultReasonCode
     */
    public String getDefaultReasonCode()
    {
        return defaultReasonCode;
    }

    /**
     * Returns indicator for modifying Parameter or Reason Code.
     *
     * @return true if modifying parameter, false for reason code
     */
    public boolean getModifyingParameter()
    {
        return modifyingParameter;
    }

    /**
     * Sets the GroupName field.
     *
     * @param the value to be set for GroupName
     */
    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public void setParameterGroup(String parameterGroup)
    {
        this.parameterGroup = parameterGroup;
    }

    /**
     * Sets the parameterNameContent field.
     *
     * @param the value to be set for parameterNameContent
     */
    public void setParameterNameContent(String parameterNameContent)
    {
        this.parameterNameContent = parameterNameContent;
    }

    /**
     * Returns numeric flag.
     *
     * @return numeric flag
     */
    public boolean getIdIsNumeric()
    {
        return idIsNumeric;
    }

    /**
     * Sets numeric flag.
     *
     * @param value id is numeric flag
     */
    public void setIdIsNumeric(boolean value)
    {
        idIsNumeric = value;
    }

    /**
     * Sets the GroupLevel field.
     *
     * @param the value to be set for GroupLevel
     */
    public void setGroupLevel(String groupLevel)
    {
        this.groupLevel = groupLevel;
    }

    /**
     * Sets whether this reasonCode is modifiable.
     *
     * @param modifiable true if the reason code can be modified, false
     *            otherwise.
     */
    public void setModifiable(boolean modifiable)
    {
        String value = MODIFIABLE;

        if (!modifiable)
        {
            value = NONMODIFIABLE;
        }
        setModifiableValue(value);
        modified = true;
    }

    /**
     * Sets whether this reason code is modifiable based on the String returned
     * from the UI.
     *
     * @param modifiable MODIFIABLE is this reason code should be modifiable,
     *            NONMODIFIABLE otherwise
     */
    public void setModifiableValue(String modifiable)
    {
        modifiableValue = modifiable;
        modified = true;
    }

    /**
     * Sets the DefaultReasonCode field.
     *
     * @param the value to be set for DefaultReasonCode
     */
    public void setDefaultReasonCode(String defaultReasonCode)
    {
        this.defaultReasonCode = defaultReasonCode;
        modified = true;
    }

    /**
     * Returns the default text.
     *
     * @return the default field text
     */
    public ReasonCode getReasonCodeSelected()
    {
        return reasonCodeSelected;
    }

    /**
     * Returns the list of reasonCodes.
     *
     * @return the reasonCodes
     */
    public Vector<ReasonCode> getReasonCodes()
    {
        return reasonCodes;
    }

    /**
     * Sets the selected reason code.
     *
     * @param value the selected reason code.
     */
    public void setReasonCodeSelected(ReasonCode value)
    {
        reasonCodeSelected = value;
        modified = true;
    }

    /**
     * Sets the selected reason code.
     *
     * @param int the index of the selected reason code.
     */
    public void setReasonCodeSelected(int value)
    {
        if (!reasonCodes.isEmpty())
        {
            reasonCodeSelected = reasonCodes.elementAt(value);
            modified = true;
        }

    }

    /**
     * Sets the selected reason code.
     *
     * @param text the selected reason code.
     */
    public void setReasonCodeSelected(String value)
    {
        Enumeration<ReasonCode> e = reasonCodes.elements();
        while (e.hasMoreElements())
        {
            ReasonCode reasonCode = e.nextElement();
            if (reasonCode.getReasonCodeName().equals(value))
            {
                reasonCodeSelected = reasonCode;
                modified = true;
                break;
            }
        }
    }

    /**
     * Sets the reasonCodes. Set the selection index to the first element if
     * none has been specified.
     *
     * @param reasonCodes the reason codes from which the user can choose
     */
    public void setReasonCodes(Vector<ReasonCode> reasonCodes)
    {
        this.reasonCodes = reasonCodes;

        // By default, select the first item
        if (getReasonCodeSelected() == null)
        {
            setReasonCodeSelected(reasonCodes.firstElement());
        }
        modified = true;
    }

    public void setModified(boolean mod)
    {
        modified = mod;
    }

    /**
     * Returns source of data.
     *
     * @return source of data
     */
    public String getSource()
    {
        return (source);
    }

    /**
     * Sets source of data.
     *
     * @param value source of data
     */
    public void setSource(String value)
    {
        source = value;
    }

    /**
     * Sets indicator for modifying Parameter or Reason Code.
     *
     * @param value true if modifying a parameter, false for a reason code
     */
    public void setModifyingParameter(boolean value)
    {
        modifyingParameter = value;
    }

    /**
     * Sets the store identifier field.
     *
     * @param value the value to be set for store identifier
     */
    public void setStoreID(String value)
    {
        storeID = value;
    }

    /**
     * Returns the store identifier field.
     *
     * @return the store identifier
     */
    public String getStoreID()
    {
        return (storeID);
    }

    /**
     * Gets the selection index.
     *
     * @return the index selected
     */
    public int getReasonCodeSelectionIndex()
    {
        return reasonCodeSelectionIndex;
    }

    /**
     * Sets the selection indes.
     *
     * @param index selected
     */
    public void setReasonCodeSelectionIndex(int index)
    {
        reasonCodeSelectionIndex = index;
    }

    /**
     * Returns true if this Reason Code Group requires a default Reason Code
     *
     * @return true if default is required
     */
    public boolean isDefaultRequired()
    {
        return CodeConstantsIfc.CODE_LIST_ITEM_TAX_AMOUNT_OVERRIDE_REASON_CODES.equals(getGroupName()) ||
             CodeConstantsIfc.CODE_LIST_ITEM_TAX_RATE_OVERRIDE_REASON_CODES.equals(getGroupName()) ||
             CodeConstantsIfc.CODE_LIST_ON_OFF_REASON_CODES.equals(getGroupName()) ||
             CodeConstantsIfc.CODE_LIST_TRANSACTION_TAX_AMOUNT_OVERRIDE_REASON_CODES.equals(getGroupName()) ||
             CodeConstantsIfc.CODE_LIST_TRANSACTION_TAX_RATE_OVERRIDE_REASON_CODES.equals(getGroupName()) ||
             CodeConstantsIfc.CODE_LIST_TRANSACTION_SUSPEND_REASON_CODES.equals(getGroupName()) ||
             CodeConstantsIfc.CODE_LIST_NO_SALE_REASON_CODES.equals(getGroupName());
    }

    /**
     * Compare current object with the object passed in.
     *
     * @param object the object to be compared with current object, the type of
     *            the object can be RetailParameter or ReasonCodeGroupBeanModel
     */
    public int compareTo(Object object)
    {
       int result = -1;

       if (object instanceof RetailParameter)
       {
           RetailParameter param = (RetailParameter)object;
           if (param.getParameterNameContent() != null)
           {
               result = parameterNameContent.compareTo(param.getParameterNameContent());
           }
       }
       else if (object instanceof ReasonCodeGroupBeanModel)
       {
           ReasonCodeGroupBeanModel param = (ReasonCodeGroupBeanModel)object;
           if (param.getParameterNameContent() != null)
           {
               result = parameterNameContent.compareTo(param.getParameterNameContent());
           }
       }
       return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder buff = new StringBuilder();

        buff.append("Class: " + getClass().getName() + " Revision: " +
                    revisionNumber + "\n");
        buff.append("Name [" + parameterGroup + "]\n");
        buff.append("Name [" + groupName + "]\n");
        buff.append("Level [" + groupLevel + "]\n");
        buff.append("Default [" + defaultReasonCode + "]\n");
        buff.append("Modifiable [" + getModifiable() + "]\n");
        buff.append("Modified [" + modified + "]\n");
        buff.append("Store ID [" + storeID + "]" + Util.EOL);

        return(buff.toString());
    }
}
