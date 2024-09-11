/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PromptAndResponseModel.java /main/16 2013/06/04 17:39:14 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/04/13 - implement manager override as dialogs
 *    blarsen   02/04/11 - a fingerprint reader method was renamed to aid
 *                         clarity.
 *    blarsen   06/09/10 - XbranchMerge blarsen_biometrics-poc from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    blarsen   05/25/10 - Added fingerprint used flag and
 *                         FingerprintReaderModel to support new fingerprint
 *                         reader device.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   09/17/09 - added canceled boolean for dialoging. removed
 *                         deprecated fields
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         11/21/2007 1:59:17 AM  Deepti Sharma   CR
 *         29598: changes for credit/debit PAPB
 *    4    360Commerce 1.3         11/13/2007 2:38:51 PM  Jack G. Swan
 *         Modified to support retrieving a byte array from the UI for card
 *         numbers instead of a String object. 
 *    3    360Commerce 1.2         3/31/2005 4:29:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:26 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:28 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Oct 22 2003 19:18:10   epd
 * added support for date field
 * 
 *    Rev 1.0   Aug 29 2003 16:11:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   23 Jul 2003 01:04:56   baa
 * add grabFocus Property
 * 
 *    Rev 1.2   Jul 16 2003 12:37:56   vxs
 * added null check inside getPINBlock()
 * Resolution for POS SCR-2779: Ingenico Device- Enter PIN and Pressing Cancal Crashes App
 * 
 *    Rev 1.1   May 08 2003 11:28:50   bwf
 * Added new getMSRDataEmployee
 * Resolution for 1933: Employee Login enhancements
 * 
 *    Rev 1.0   Apr 29 2002 14:47:50   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:57:02   msg
 * Initial revision.
 * 
 *    Rev 1.5   Jan 19 2002 12:15:08   mpm
 * Fixed merge problems.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.3   02 Nov 2001 13:42:04   jbp
 * Code Review Changes
 * Resolution for POS SCR-207: Prompt for Customer Info
 *
 *    Rev 1.2   01 Nov 2001 10:03:06   jbp
 * code review changes
 * Resolution for POS SCR-207: Prompt for Customer Info
 *
 *    Rev 1.1   19 Oct 2001 11:11:12   jbp
 * Added dynamic functionality in setting the response field class name and the max and min length of the response field text.
 * Resolution for POS SCR-207: Prompt for Customer Info
 *
 *    Rev 1.0   Sep 21 2001 11:35:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:16:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.pos.device.FormModel;

import oracle.retail.stores.foundation.manager.device.FingerprintReaderModel;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.device.PINPadModel;

/**
 * This class is the model for the PromptAndResponseBean. It is used to access
 * the information in the PromptAndResponsePanel.
 * 
 * @version $Revision: /main/16 $
 */
public class PromptAndResponseModel extends POSBaseBeanModel
{
    private static final long serialVersionUID = -8374907215330687832L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /** Register number to display to the user */
    protected String promptText = null;

    /** Text used to display various runtime replacement text to the user */
    protected String[] arguments = null;

    /** Register number to display to the user */
    protected String responseText = null;

    /** If the response was a button press, this will be the actionCommand. */
    protected String responseCommand = null;

    /** Response data as bytes   */
    protected byte[] responseBytes = null;

    /** Cashier name to display to the user */
    protected Boolean responseEnabledClass   = null;

    /** Flag indicating whether the response is editable */
    protected boolean responseEditable = true;

    /** Swiping indicator */
    protected boolean swiped = false;

    /** Fingerprint Read indicator */
    protected boolean fingerprintRead = false;

    /** Scanning indicator */
    protected boolean scanned = false;

    /** PINPad data */
    private PINPadModel pinPadData  = null;

    /** Form Model for CPOI form input */
    protected FormModel formModel = null;

    /** Response Field Class */
    protected String responseFieldType = null;

    /** Response Field MaxLength */
    protected String maxLength = null;

    /** Response Field MinLength */
    protected String minLength = null;
    
    /** MSR Model for use with employee card login */
    protected MSRModel msrModel = null;
    
    /** Fingerprint Model for use with fingerprint reader login */
    protected FingerprintReaderModel fingerprintModel = null;
    
    /** Flag indicating that focus should stay on the response field */        
    protected Boolean grabFocus = null;

    /** The date format of the response field (used if response field is EYSDateFormat) */
    protected int responseFieldDateFormat = -1;

    /** Flag to indicate that the prompt was canceled instead of confirmed. */
    protected boolean canceled;

    /**
     * Default constructor.
     */
    public PromptAndResponseModel()
    {
    }

    /**
     * Return whether the prompt was canceled by the user instead of confirmed.
     * 
     * @return
     */
    public boolean isCanceled()
    {
        return canceled;
    }

    /**
     * This flag should be set to true if the used presses cancel.
     *
     * @param canceled
     */
    public void setCanceled(boolean canceled)
    {
        this.canceled = canceled;
    }

    /**
     * Gets the Prompt Text.
     * 
     * @return the promptText
     */
    public String getPromptText()
    {
        return promptText;
    }

    /**
     * Sets the Prompt Text.
     * 
     * @param value the promptText
     */
    public void setPromptText(String value)
    {
        promptText = value;
    }

    /**
     * List of arguments used for runtime text replacement
     * 
     * @return arguments
     */
    public String[] getArguments()
    {
        return arguments;
    }

    /**
     * Set the array of replacement text to be used at runtime
     * 
     * @param values
     */
    public void setArguments(String[] values)
    {
        this.arguments = values;
    }

    /**
     * Convenience method to set the array of items to be replaced to an array
     * of size one containing the string passed in
     * 
     * @param value argument to use
     */
    public void setArguments(String value)
    {
        this.arguments = new String[] { value };
    }

    /**
     * Gets the Response Text.
     * 
     * @return the responseText
     */
    public String getResponseText()
    {
        return responseText;
    }

    /**
     * Sets the Response Text.
     * 
     * @param value the responseText
     */
    public void setResponseText(String value)
    {
        responseText = value;
    }

    /**
     * @return the responseCommand
     */
    public String getResponseCommand()
    {
        return responseCommand;
    }

    /**
     * @param responseCommand the responseCommand to set
     */
    public void setResponseCommand(String responseCommand)
    {
        this.responseCommand = responseCommand;
    }

    /**
     * Gets the responseEnabled indicator
     * 
     * @return the responseEnabled indicator
     */
    public boolean getResponseEnabled()
    {
        return responseEnabledClass.booleanValue();
    }

    /**
     * Gets the responseEnabledClass Boolean
     * 
     * @return the responseEnabled indicator
     */
    public Boolean getResponseEnabledClass()
    {
        return responseEnabledClass;
    }

    /**
     * Sets the responseEnabled indicator
     * 
     * @param value the responseEnabled indicator
     */
    public void setResponseEnabled(boolean value)
    {
        responseEnabledClass = new Boolean(value);
    }

    /**
     * Get swiping flag
     * 
     * @return boolean flag
     */
    public boolean isSwiped()
    {
        return swiped;
    }

    /**
     * Sets the swiping flag
     * 
     * @param value boolean
     */
    public void setSwiped(boolean value)
    {
        swiped = value;
    }


    /**
     * Get fingerprint read flag
     * 
     * @return boolean flag
     */
    public boolean isFingerprintRead()
    {
        return fingerprintRead;
    }

    /**
     * Sets the fingerprint flag
     * 
     * @param value boolean
     */
    public void setFingerprintRead(boolean value)
    {
        fingerprintRead = value;
    }
    /**
     * Get scanning flag
     * 
     * @return boolean flag
     */
    public boolean isScanned()
    {
        return scanned;
    }

    /**
     * Sets the scanning flag
     * 
     * @param value boolean
     */
    public void setScanned(boolean value)
    {
        scanned = value;
    }

    /**
     * Get editable flag
     * 
     * @return boolean flag
     */
    public boolean isResponseEditable()
    {
        return responseEditable;
    }

    /**
     * Sets the editable flag
     * 
     * @param value boolean
     */
    public void setResponseEditable(boolean value)
    {
        responseEditable = value;
    }

    /**
     * Gets the grab focus Boolean
     * 
     * @return the grabFocus indicator
     */
    public Boolean getGrabFocus()
    {
        return grabFocus;
    }

    /**
     * Sets the grab focus indicator
     * 
     * @param value the grabFocus indicator
     */
    public void setGrabFocus(boolean value)
    {
        grabFocus = new Boolean(value);
    }

    /**
     * Get the value of the PINBlock field
     * 
     * @return byte[] value of PINBlock
     */
    public byte[] getPINBlock()
    {
        return (pinPadData == null || pinPadData.getEncryptedPIN() == null) ? null : pinPadData.getEncryptedPIN()
                .getBytes();
    }

    /**
     * Get the value of the CardData field
     * 
     * @return PINPadModel
     */
    public PINPadModel getPINPadData()
    {
        return pinPadData;
    }

    /***
     * Sets the PINPadData field.
     * 
     * @param arg PINPadModel
     */
    public void setPINPadData(PINPadModel arg)
    {
        pinPadData = arg;
    }

    /**
     * Get the response field class name
     * 
     * @return responseFieldClassName
     */
    public String getResponseFieldType()
    {
        return responseFieldType;
    }

    /***
     * Sets the responseField to null. The response field type by default is
     * defined in the xml.
     * 
     * @param None
     */
    public void setResponseTypeDefault()
    {
        // set response field type to null.
        // response field type defined in xml.
        this.responseFieldType = null;
    }

    /***
     * Sets the responseField to AlphaNumeric.
     * 
     * @param None
     */
    public void setResponseTypeAlphaNumeric()
    {
        this.responseFieldType = "oracle.retail.stores.pos.ui.beans.AlphaNumericTextField";
    }

    /***
     * Sets the responseField to NumericTextField.
     * 
     * @param None
     */
    public void setResponseTypeNumeric()
    {
        this.responseFieldType = "oracle.retail.stores.pos.ui.beans.NumericTextField";
    }

    public void setResponseTypeDate()
    {
        this.responseFieldType = "oracle.retail.stores.pos.ui.beans.EYSDateField";
    }

    public void setResponseTypeDateFormat(int dateFormat)
    {
        this.responseFieldDateFormat = dateFormat;
    }

    public int getResponseTypeDateFormat()
    {
        return this.responseFieldDateFormat;
    }

    /***
     * Sets the response field max length.
     * 
     * @param arg max length
     */
    public void setMaxLength(String value)
    {
        this.maxLength = value;
    }

    /**
     * Get the response field max length
     * 
     * @return maxLength
     */
    public String getMaxLength()
    {
        return maxLength;
    }

    /***
     * Sets the response field min length.
     * 
     * @param arg min length
     */
    public void setMinLength(String value)
    {
        this.minLength = value;
    }

    /**
     * Get the response field min length
     * 
     * @return min length
     */
    public String getMinLength()
    {
        return minLength;
    }

    /***
     * Sets the msr model Used for employee login cards
     * 
     * @param arg MSRModel
     */
    public void setMSRModel(MSRModel value)
    {
        this.msrModel = value;
    }

    /**
     * Get the MSR model Used for employee login cards
     * 
     * @return MSRModel
     */
    public MSRModel getMSRModel()
    {
        return msrModel;
    }

    /***
     * Sets the Fingerprint model Used for employee login 
     * 
     * @param arg FingerprintReaderModel
     */
    public void setFingerprintModel(FingerprintReaderModel value)
    {
        this.fingerprintModel = value;
    }

    /**
     * Get the Fingerprint model Used for employee login
     * 
     * @return FingerprintReaderModel
     */
    public FingerprintReaderModel getFingerprintModel()
    {
        return fingerprintModel;
    }

    /**
     * @return Returns the responseBytes.
     */
    public byte[] getResponseBytes()
    {
        return responseBytes;
    }

    /**
     * @param responseBytes The responseBytes to set.
     */
    public void setResponseBytes(byte[] responseBytes)
    {
        this.responseBytes = responseBytes;
    }

    /***
     * Sets the form model Used for CPOI form input
     * 
     * @param arg FormModel
     */
    public void setFormModel(FormModel value)
    {
        this.formModel = value;
    }

    /**
     * Get the Form model Used for CPOI form input
     * 
     * @return FormModel
     */
    public FormModel getFormModel()
    {
        return formModel;
    }

}
