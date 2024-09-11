/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DataInputBeanModel.java /main/14 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   07/14/11 - tweak search by credit debit and gift card number
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   8    360Commerce 1.7         12/12/2007 6:11:57 PM  Michael P. Barnett In
 *        clearValue(), use utility method to clear potentially sensitive data
 *         from byte array.
 *   7    360Commerce 1.6         11/29/2007 5:15:58 PM  Alan N. Sinton  CR
 *        29677: Protect user entry fields of PAN data.
 *   6    360Commerce 1.5         11/21/2007 1:59:17 AM  Deepti Sharma   CR
 *        29598: changes for credit/debit PAPB
 *   5    360Commerce 1.4         4/25/2007 8:51:30 AM   Anda D. Cadar   I18N
 *        merge
 *   4    360Commerce 1.3         1/22/2006 11:45:23 AM  Ron W. Haight
 *        removed references to com.ibm.math.BigDecimal
 *   3    360Commerce 1.2         3/31/2005 4:27:40 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:20:46 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:27 PM  Robert Pearse   
 *  $
 *  Revision 1.7  2004/07/20 18:41:52  cdb
 *  @scr 6127 Updated to use validation in validator rather than aisles.
 *
 *  Revision 1.6  2004/06/22 17:57:19  cdb
 *  @scr 4308 Removed class cast exception caused by non-date (empty) string entry. Value for field ends up being
 *  stored as string rather than date.
 *
 *  Revision 1.5  2004/03/16 17:15:22  build
 *  Forcing head revision
 *
 *  Revision 1.4  2004/03/16 17:15:17  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/02/13 13:57:21  baa
 *  @scr 3561  Returns enhancements
 *
 *  Revision 1.2  2004/02/11 20:56:25  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.4   Jan 23 2004 16:28:32   baa
 * continue return development
 * 
 *    Rev 1.3   Jan 13 2004 14:36:30   baa
 * move scannedfields method to model
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 * 
 *    Rev 1.2   Dec 29 2003 15:44:36   baa
 * return enhancements
 * 
 *    Rev 1.1   Dec 15 2003 13:51:36   baa
 * return enhancements
 * 
 *    Rev 1.0   Aug 29 2003 16:10:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Mar 20 2003 18:19:00   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.0   Apr 29 2002 14:56:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   25 Apr 2002 18:52:16   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 * 
 *    Rev 1.0   Mar 18 2002 11:53:02   msg
 * Initial revision.
 * 
 *    Rev 1.2   Feb 05 2002 16:43:38   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 * 
 *    Rev 1.1   23 Jan 2002 16:59:52   KAC
 * Fixed potential ClassCastException in getValueAsDecimal()
 * Resolution for POS SCR-372: Modify Parameter UI for register level editing
 * 
 *    Rev 1.0   Jan 19 2002 11:03:20   mpm
 * Initial revision.
 * Resolution for POS SCR-214: ReceiptLogo on Receipt
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.device.MICRModel;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.POSListModel;

/**
 * Data model for the DataInputBean.
 * 
 * @version $Revision: /main/14 $
 */
public class DataInputBeanModel extends POSBaseBeanModel
{
    private static final long serialVersionUID = 1634461282398414648L;

    /** container for data name/value pairs */
    protected HashMap<String,Object> beanData = new HashMap<String,Object>(0);
    /** 
     * Container label arguments to place into the localized text.
     * @since 13.4 
     */
    protected HashMap<String,Object[]> labelArgs = new HashMap<String,Object[]>(0);

    /** indicate if a card has been swiped on this screen */
    protected boolean isCardSwiped = false;

    /** indicate if a check micr has been read */
    protected boolean isCheckMICRed = false;

    /** indicate if a check micr has been read */
    protected boolean isScanned = false;

    protected MSRModel msrModel;
    protected MICRModel chkModel;
    /**
     * Holds on to integer index. This was necessary because for the JComboBox
     * setting the selected "value" had no effect on screen while setting the
     * selected index did.
     */
    protected int selectionIndex = -1;

    protected String scannedFields = null;
    protected String msrField = null;
    protected String micrField = null;

    /**
     * Clears the value for the named field.
     * 
     * @param fieldName the name of the field
     */
    public void clearValue(String fieldName)
    {
        Object toBeCleared = beanData.get(fieldName);

        // Flush byte array that might contain sensitive credit card data
        if (toBeCleared instanceof byte[])
        {
            Util.flushByteArray((byte[])toBeCleared);
        }

        beanData.put(fieldName, null);
    }

    /**
     * Clears the values for all data fields.
     */
    public void clearAllValues()
    {
        for (String key : beanData.keySet())
        {
            beanData.put(key, null);
        }
    }

    /**
     * Gets the value for the named field as a boolean.
     * 
     * @param fieldName the name of the field
     * @return a boolean value
     */
    public boolean getValueAsBoolean(String fieldName)
    {
        String value = (String)getValue(fieldName);
        boolean result = Boolean.valueOf(value).booleanValue();

        return result;
    }

    /**
     * Gets the value for the named field as a currency object.
     * 
     * @param fieldName the name of the field
     * @return a currency object
     */
    public CurrencyIfc getValueAsCurrency(String fieldName)
    {
        CurrencyIfc result = DomainGateway.getBaseCurrencyInstance();

        String value = (String)beanData.get(fieldName);

        if (value != null)
        {
            result = DomainGateway.getBaseCurrencyInstance(value);
        }
        return result;
    }

    /**
     * Gets the value for the named field as a currency object.
     * 
     * @param fieldName the name of the field
     * @return a currency object
     */
    public EYSDate getValueAsEYSDate(String fieldName)
    {
        EYSDate returnValue = null;
        Object value = beanData.get(fieldName);
        if (value instanceof EYSDate)
        {
            returnValue = (EYSDate)value;
        }
        return returnValue;
    }

    /**
     * Gets the value for the named field as a big decimal object.
     * 
     * @param fieldName the name of the field
     * @return a BigDecimal object
     */
    public BigDecimal getValueAsDecimal(String fieldName)
    {
        BigDecimal result = null;
        Object value = beanData.get(fieldName);

        if (value instanceof BigDecimal)
        {
            result = (BigDecimal)value;
        }
        else if (value instanceof String)
        {
            try
            {
                result = new BigDecimal((String)value);
            }
            catch (NumberFormatException nfe)
            {
                result = BigDecimal.ZERO;
            }
        }
        return result;
    }

    /**
     * Gets the value for the named field as an integer primative.
     * 
     * @param fieldName the name of the field
     * @return an integer value
     */
    public int getValueAsInt(String fieldName)
    {
        int result;

        String value = (String)beanData.get(fieldName);
        try
        {
            result = Integer.parseInt(value);
        }
        catch (NumberFormatException nfe)
        {
            result = 0;
        }
        return result;
    }

    /**
     * Gets the value for the named field as a string.
     * 
     * @param fieldName the name of the field
     * @return a string value
     */
    public String getValueAsString(String fieldName)
    {
        return (String)beanData.get(fieldName);
    }

    /**
     * Gets the value for the named field as a string.
     * 
     * @param fieldName the name of the field
     * @return a string value
     */
    public byte[] getValueAsByteArray(String fieldName)
    {
        return (byte[])beanData.get(fieldName);
    }

    /**
     * Gets the value from the designated data field.
     * 
     * @param fieldName the name of the data field
     * @return the value from the field
     */
    public Object getValue(String fieldName)
    {
        return beanData.get(fieldName);
    }

    /**
     * Sets the value of the designated data field.
     * 
     * @param fieldName the name of the data field
     * @param fieldValue the value to display in the field
     */
    public void setValue(String fieldName, Object fieldValue)
    {
        beanData.put(fieldName, fieldValue);
    }

    /**
     * Gets the Arg from the designated label.
     * 
     * @param labelName the name of the label
     * @return the Arg from the label
     */
    public Object[] getLabelArg(String labelName)
    {
        return labelArgs.get(labelName);
    }

    /**
     * Sets the Arg of the designated label.
     * 
     * @param labelName the name of the label
     * @param labelArg the Arg to display in the label
     */
    public void setLabelArg(String labelName, Object[] labelArg)
    {
        labelArgs.put(labelName, labelArg);
    }

    /**
     * Gets the index of the selected value from the designated list.
     * 
     * @param fieldName the name of the data list
     * @return the index of the selected value from the list
     */
    public int getSelectionIndex(String fieldName)
    {
        int result = -1;
        Object data = beanData.get(fieldName);
        Object selection = null;
        if (data != null)
        {
            if (data instanceof POSListModel)
            {
                selection = ((POSListModel)data).getSelectedValue();
                result = ((POSListModel)data).indexOf(selection);
            }
            else if (data instanceof ComboBoxModel)
            {
                selection = ((ComboBoxModel)data).getSelectedItem();
                if (data instanceof DefaultListModel)
                {
                    result = ((DefaultListModel)data).indexOf(selection);
                }
            }
        }
        return result;
    }

    /**
     * Gets the selected value from the designated list.
     * 
     * @param fieldName the name of the data list
     * @return the selected value from the list
     */
    public Object getSelectionValue(String fieldName)
    {
        Object result = null;

        Object data = beanData.get(fieldName);

        if (data != null)
        {
            if (data instanceof POSListModel)
            {
                result = ((POSListModel)data).getSelectedValue();
            }
            else if (data instanceof ComboBoxModel)
            {
                result = ((ComboBoxModel)data).getSelectedItem();
            }
        }
        return result;
    }

    /**
     * Sets a list's selection choices from a vector of values.
     * 
     * @param fieldName the name of the list
     * @param choices the choices to display in the list
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setSelectionChoices(String fieldName, Vector choices)
    {
        Object data = beanData.get(fieldName);

        Object select = null;

        if (data == null)
        {
            data = new POSListModel(choices);
        }
        else
        {
            if (data instanceof POSListModel)
            {
                select = ((POSListModel)data).getSelectedValue();
                ((POSListModel)data).removeAllElements();
                data = new POSListModel(choices);
                ((POSListModel)data).setSelectedValue(select);
            }
            else if (data instanceof ComboBoxModel)
            {
                select = ((ComboBoxModel)data).getSelectedItem();
                ((Vector)data).removeAllElements();
                ((Vector)data).addAll(choices);
                ((ComboBoxModel)data).setSelectedItem(select);
            }
        }
        beanData.put(fieldName, data);
    }

    /**
     * Sets a list's selection choices from a list of values.
     * 
     * @param fieldName the name of the list
     * @param choices the choices to display in the list
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void setSelectionChoices(String fieldName, ArrayList choices)
    {
        Object data = beanData.get(fieldName);

        Object select = null;

        if (data == null)
        {
            data = new POSListModel(choices.toArray());
        }
        else
        {
            if (data instanceof POSListModel)
            {
                select = ((POSListModel)data).getSelectedValue();
                ((POSListModel)data).removeAllElements();

                data = new POSListModel(choices.toArray());
                ((POSListModel)data).setSelectedValue(select);
            }
            else if (data instanceof ComboBoxModel)
            {
                select = ((ComboBoxModel)data).getSelectedItem();
                ((List)data).clear();
                ((List)data).addAll(choices);
                ((ComboBoxModel)data).setSelectedItem(select);
            }
        }
        beanData.put(fieldName, data);
    }

    /**
     * Sets the index to select in a list.
     * 
     * @param fieldName the name of the list
     * @param propValue the index to be selected in the list
     */
    public void setSelectionIndex(String fieldName, int propValue)
    {
        Object data = beanData.get(fieldName);
        Object selection = null;

        if (data == null)
        {
            data = new POSListModel();
        }
        if (data instanceof POSListModel)
        {
            if (propValue < ((POSListModel)data).getSize())
            {
                selection = ((POSListModel)data).getElementAt(propValue);
                ((POSListModel)data).setSelectedValue(selection);
            }
        }
        else if (data instanceof ComboBoxModel)
        {
            if (propValue < ((ComboBoxModel)data).getSize())
            {
                selection = ((ComboBoxModel)data).getElementAt(propValue);
                ((ComboBoxModel)data).setSelectedItem(selection);
            }
        }
        beanData.put(fieldName, data);
    }

    /**
     * Sets a list's selected value.
     * 
     * @param fieldName the name of the list
     * @param propValue the value to be selected in the list
     */
    public void setSelectionValue(String fieldName, Object propValue)
    {
        Object data = beanData.get(fieldName);

        if (data == null)
        {
            data = new POSListModel();
        }
        if (data instanceof POSListModel)
        {
            ((POSListModel)data).setSelectedValue(propValue);
        }
        else if (data instanceof ComboBoxModel)
        {
            ((ComboBoxModel)data).setSelectedItem(propValue);
        }
        beanData.put(fieldName, data);
    }

    /**
     * @return
     */
    public MICRModel getChkModel()
    {
        return chkModel;
    }

    /**
     * @return
     */
    public boolean isCardSwiped()
    {
        return isCardSwiped;
    }

    /**
     * @return
     */
    public boolean isCheckMICRed()
    {
        return isCheckMICRed;
    }

    /**
     * @return
     */
    public MSRModel getMsrModel()
    {
        return msrModel;
    }

    /**
     * @param model
     */
    public void setChkModel(MICRModel model)
    {
        chkModel = model;
    }

    /**
     * @param b
     */
    public void setCardSwiped(boolean b)
    {
        isCardSwiped = b;
    }

    /**
     * @param b
     */
    public void setCheckMICRed(boolean b)
    {
        isCheckMICRed = b;
    }

    /**
     * @param model
     */
    public void setMsrModel(MSRModel model)
    {
        msrModel = model;
    }

    /**
     * @return
     */
    public boolean isScanned()
    {
        return isScanned;
    }

    /**
     * @param b
     */
    public void setScanned(boolean b)
    {
        isScanned = b;
    }

    /**
     * @param string
     */
    public void setScannedFields(String string)
    {
        scannedFields = string;
    }

    /**
     * @return
     */
    public String getScannedFields()
    {
        return scannedFields;
    }

    /**
     * @return
     */
    public String getMicrField()
    {
        return micrField;
    }

    /**
     * @return
     */
    public String getMsrField()
    {
        return msrField;
    }

    /**
     * @param string
     */
    public void setMicrField(String string)
    {
        micrField = string;
    }

    /**
     * @param string
     */
    public void setMsrField(String string)
    {
        msrField = string;
    }
}
