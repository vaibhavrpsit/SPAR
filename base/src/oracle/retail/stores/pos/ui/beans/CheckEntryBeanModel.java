/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CheckEntryBeanModel.java /rgbustores_13.4x_generic_branch/2 2011/07/20 04:31:48 rrkohli Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rrkohli   07/01/11 - Encryption CR
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    abondala  11/03/08 - updated files related to customer id type reason
 *                         code.
 *    abondala  11/03/08 - updated files related to the Patriotic customer ID
 *                         types reason code
 *
 * ===========================================================================

   $Log:
    4    360Commerce 1.3         3/29/2007 7:23:29 PM   Michael Boyd    CR
         26172 - v8x merge to trunk

         4    .v8x      1.2.1.0     3/11/2007 4:55:19 PM   Brett J. Larsen CR
         4530 -
         default reason code not being displayed - adding support for this
    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse
    2    360Commerce 1.1         3/10/2005 10:20:07 AM  Robert Pearse
    1    360Commerce 1.0         2/11/2005 12:09:54 PM  Robert Pearse
   $
   Revision 1.5  2004/04/09 16:56:00  cdb
   @scr 4302 Removed double semicolon warnings.

   Revision 1.4  2004/03/16 17:15:22  build
   Forcing head revision

   Revision 1.3  2004/03/16 17:15:16  build
   Forcing head revision

   Revision 1.2  2004/02/11 20:56:26  rhafernik
   @scr 0 Log4J conversion and code cleanup

   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
   updating to pvcs 360store-current


 *
 *    Rev 1.3   29 Jan 2004 15:39:12   Tim Fritz
 * Changed selectedIDType = -1 to selectedIDType = 0
 *
 *    Rev 1.2   Nov 13 2003 15:30:08   bwf
 * Put blank for id type.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.1   Nov 07 2003 16:19:40   bwf
 * Update for new check stuff.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.0   Aug 29 2003 16:09:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.8   Apr 09 2003 17:50:02   bwf
 * Internationalization Clean up - remove UtilityManager from beans
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.7   Feb 14 2003 17:32:10   HDyer
 * Extend from ReasonBeanModel so the super class can manage the id types. ID types are really reason codes. Modified the id type methods to call the super class methods.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.6   Feb 04 2003 16:34:10   HDyer
 * Display localized strings instead of string tags in the ID type pulldown.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.5   Jan 29 2003 13:48:06   baa
 * merge changes for micr bad read error
 * Resolution for POS SCR-1846: Unsuccessful Check Scan message appears when slip printer cover is opened at bank information screen during check tender process
 *
 *    Rev 1.4   Sep 20 2002 17:56:26   baa
 * country/state fixes and other I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.3   Sep 18 2002 17:15:28   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 03 2002 16:04:58   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.device.MICRModel;
import oracle.retail.stores.foundation.manager.device.MSRModel;

//------------------------------------------------------------------------------
/**
 *  @version $Revision: /rgbustores_13.4x_generic_branch/2 $
**/
//------------------------------------------------------------------------------
public class CheckEntryBeanModel extends CountryModel
{
    /**
        revision number supplied by Team Connection
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";
    /**
        American Banking Association Number
    **/
     protected String fieldABANumber = "";
    /**
        Checking Account Number
    **/
     protected String fieldAccountNumber = "";
    /**
        Check number
    **/
     protected String fieldCheckNumber = "";
    /**
        Identification Number {ie Drivers Licence number}
    **/
     protected String fieldIDNumber = "";

    /**
        Date of Birth
    **/
     protected EYSDate fieldDOB = null;
    /**
        Valid Date of Birth Flag
    **/
     protected boolean fieldDOBValid = false;
    /**
        Set focus onID Number Flag
    **/
     protected boolean fieldFocusOnIDNumber = false;
    /**
        true if check has been Micr read
    **/
     protected boolean checkMICRed = false;
    /**
        true if check has been Micr read
    **/
     protected MICRModel micrData = null;

      /** @deprecated as of release 5.5 replace by stateIndx **/
     protected String stateID = null;

    /**
        true if we are to ignore the printer
    **/
    private boolean printerIgnored = false;

    /**
        flag whether ID card is swiped
    **/
    protected boolean cardSwiped;
    /**
        MSR model for ID
    **/
    protected MSRModel msrModel;
    /**
        telephone number
    **/
    protected String fieldPhoneNumber = "";
    /**
        MICR number
    **/
    protected String fieldMICRNumber = "";
    /**
       display MICR Line Flag
    **/
    protected boolean displayMicrLineFlag = true;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.CheckEntryBeanModel.class);

    /**
        the list of ID types
    **/
    private Vector idTypes = null;


    /**
        the selected ID type
    **/
    private int selectedIDType = 0;

    /**
        the default ID type
    **/
    private int defaultIDType = 0;
    
    private EncipheredDataIfc idNumberEncipheredData = null;

    //---------------------------------------------------------------------
    /**
        Constructs CheckEntryBeanModel object. <P>
    **/
    //---------------------------------------------------------------------
    public CheckEntryBeanModel()
    {
        idTypes = new Vector();
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the ABANumber field
        @return the value of ABANumber
    **/
    //----------------------------------------------------------------------------
    public String getABANumber()
    {
        return fieldABANumber;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the AccountNumber field
        @return the value of AccountNumber
    **/
    //----------------------------------------------------------------------------
    public String getAccountNumber()
    {
        return fieldAccountNumber;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the CheckNumber field
        @return the value of CheckNumber
    **/
    //----------------------------------------------------------------------------
    public String getCheckNumber()
    {
        return fieldCheckNumber;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the IDNumber field
        @return the value of IDNumber
    **/
    //----------------------------------------------------------------------------
    public String getIDNumber()
    {
        return fieldIDNumber;
    }


    //----------------------------------------------------------------------------
    /**
        Get the value of the DOB field
        @return the value of DOB
    **/
    //----------------------------------------------------------------------------
    public EYSDate getDOB()
    {
        return fieldDOB;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the DOBValid field
        @return the value of DOBValid
    **/
    //----------------------------------------------------------------------------
    public boolean isDOBValid()
    {
        return fieldDOBValid;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the FocusOnIDNumber field
        @return the value of FocusOnIDNumber
    **/
    //----------------------------------------------------------------------------
    public boolean isFocusOnIDNumber()
    {
        return fieldFocusOnIDNumber;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the CheckMICRed field
        @return the value of CheckMICRed
    **/
    //----------------------------------------------------------------------------
    public boolean isCheckMICRed()
    {
        return checkMICRed;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the MICRData field
        @return the value of MICRData
    **/
    //----------------------------------------------------------------------------
    public MICRModel getMICRData()
    {
        return micrData;
    }
    //----------------------------------------------------------------------------
    /**
        Get the value of the reasonCodes property containing the IDTypes vector.
        @return the value of IDTypes
    **/
    //----------------------------------------------------------------------------
    public Vector getIDTypes()
    {
        return idTypes;
    }
    //----------------------------------------------------------------------------
    /**
        Get the index of the selectedReason property which is the selected
        IDType.
        @return the value of SelectedIDType
    **/
    //----------------------------------------------------------------------------
    public int getSelectedIDType()
    {
        return selectedIDType;
    }
    //----------------------------------------------------------------------
    /**
     @return Returns the defaultIDType.
     **/
    //----------------------------------------------------------------------
    public int getDefaultIDType()
    {
        return defaultIDType;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the ABANumber field
        @param the value to be set for ABANumber
    **/
    //----------------------------------------------------------------------------
    public void setABANumber(String aBANumber)
    {
        fieldABANumber = aBANumber;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the AccountNumber field
        @param the value to be set for AccountNumber
    **/
    //----------------------------------------------------------------------------
    public void setAccountNumber(String accountNumber)
    {
        fieldAccountNumber = accountNumber;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the CheckNumber field
        @param the value to be set for CheckNumber
    **/
    //----------------------------------------------------------------------------
    public void setCheckNumber(String checkNumber)
    {
        fieldCheckNumber = checkNumber;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the IDNumber field
        @param the value to be set for IDNumber
    **/
    //----------------------------------------------------------------------------
    public void setIDNumber(String iDNumber)
    {
        fieldIDNumber = iDNumber;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the IDState field
        @param the value to be set for IDState
        @deprecated as of release 5.5  replace by setStateIndex()
    **/
    //----------------------------------------------------------------------------
    public void setIDState(String iDState)
    {
        // there is no way of knowing the default country with the previos method
        // state index is set to the first one
        stateID = iDState;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the IDState field
        @param the value to be set for IDState
        @deprecated as of release 5.5  replace by setStateIndex()
    **/
    //----------------------------------------------------------------------------
    public String getIDState()
    {
        // there is no way of knowing the default country with the previos method
        // state index is set to the first one
        return stateID;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the DOB field
        @param the value to be set for DOB
    **/
    //----------------------------------------------------------------------------
    public void setDOB(EYSDate dOB)
    {
        fieldDOB = dOB;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the DOBValid field
        @param the value to be set for DOBValid
    **/
    //----------------------------------------------------------------------------
    public void setDOBValid(boolean dOBValid)
    {
        fieldDOBValid = dOBValid;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the FocusOnIDNumber field
        @param the value to be set for FocusOnIDNumber
    **/
    //----------------------------------------------------------------------------
    public void setFocusOnIDNumber(boolean focusOnIDNumber)
    {
        fieldFocusOnIDNumber = focusOnIDNumber;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the CheckMICRed field
        @param the value to be set for CheckMICRed
    **/
    //----------------------------------------------------------------------------
    public void setCheckMICRed(boolean micred)
    {
        checkMICRed = micred;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the MICRData field
        @param the value to be set for MICRData
    **/
    //----------------------------------------------------------------------------
    public void setMICRData(MICRModel data)
    {
        micrData = data;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the reasonCodes field containing the IDTypes
        @param the value to be set for IDTypes
    **/
    //----------------------------------------------------------------------------
    public void setIDTypes(Vector types)
    {
        idTypes = types;
    }
    //----------------------------------------------------------------------------
    /**
        Sets the selected reason code properties containing the selected ID type.
        @param the value to be set for SelectedIDType
    **/
    //----------------------------------------------------------------------------
    public void setSelectedIDType(int selected)
    {
        selectedIDType = selected;
    }

    //----------------------------------------------------------------------
    /**

     @param defaultIDType The defaultIDType to set.
     **/
    //----------------------------------------------------------------------
    public void setDefaultIDType(int defaultIDType)
    {
        this.defaultIDType = defaultIDType;
    }
    //--------------------------------------------------------------------------
    /**
     * Returns the printerIgnore.
     * @return boolean
     */
    //--------------------------------------------------------------------------
    public boolean isPrinterIgnored() {
        return printerIgnored;
    }

    //--------------------------------------------------------------------------
    /**
     * Sets the printerIgnore flag.
     * @param printerIgnore The printerIgnore to set
     */
    //--------------------------------------------------------------------------
    public void setPrinterIgnored(boolean value) {
        printerIgnored = value;
    }

    //---------------------------------------------------------------------
    /**
     * Gets the tphoneNumber property (java.lang.String) value.
     * @return The phoneNumber property value.
     * @see #setPhoneNumber
     */
    //---------------------------------------------------------------------
    public String getPhoneNumber()
    {
        return fieldPhoneNumber;
    }

    //---------------------------------------------------------------------
    /**
     * Sets the phoneNumber property (java.lang.String) value.
     * @param phoneNumber The new value for the property.
     * @see #getPhoneNumber
     */
    //---------------------------------------------------------------------
    public void setPhoneNumber(String value)
    {
        fieldPhoneNumber = value;
    }

    //---------------------------------------------------------------------
    /**
     * Gets the MICRNumber property (java.lang.String) value.
     * @return The MICRNumber property value.
     * @see #setMICRNumber
     */
    //---------------------------------------------------------------------
    public String getMICRNumber()
    {
        return fieldMICRNumber;
    }

    //---------------------------------------------------------------------
    /**
     * Sets the MICRNumber property (java.lang.String) value.
     * @param MICRNumber The new value for the property.
     * @see #getMICRNumber
     */
    //---------------------------------------------------------------------
    public void setMICRNumber(String value)
    {
        fieldMICRNumber = value;
    }

    //---------------------------------------------------------------------------
    /**
        Get the value of the CardSwiped field
        @return the value of CardSwiped
    **/
    //---------------------------------------------------------------------------
    public boolean isCardSwiped()
    {
        return cardSwiped;
    }

    //---------------------------------------------------------------------------
    /**
        Sets the CardSwiped field
        @param swiped value to be set for CardSwiped
    **/
    //---------------------------------------------------------------------------
    public void setCardSwiped(boolean swiped)
    {
        cardSwiped = swiped;
    }

    //---------------------------------------------------------------------
    /**
       Gets the MSR model
       @return MSRModel
    **/
    //---------------------------------------------------------------------
    public MSRModel getMSRModel()
    {
        return msrModel;
    }

    //---------------------------------------------------------------------
    /**
       Sets the MSR model
       @param  model MSRModel
    **/
    //---------------------------------------------------------------------
    public void setMSRModel(MSRModel model)
    {
        msrModel = model;
    }

    //----------------------------------------------------------------------------
    /**
        Returns the displayMicrLineFlag flag
        @return the value to be set for displayMicrLineFlag
    **/
    //----------------------------------------------------------------------------
    public boolean getDisplayMicrLineFlag()
    {
        return displayMicrLineFlag;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the displayMicrLineFlag flag
        @param the value to be set for displayMicrLineFlag
    **/
    //----------------------------------------------------------------------------
    public void setDisplayMicrLineFlag(boolean value)
    {
        displayMicrLineFlag = value;
    }
    
    // ----------------------------------------------------------------------------
    /**
     * This method gets the idNumberEncipheredData.
     * 
     *@return idNumberEncipheredData EncipheredDataIfc
     */
    // ----------------------------------------------------------------------------
    public EncipheredDataIfc getIdNumberEncipheredData()
    {
        return idNumberEncipheredData;
    }

    // ----------------------------------------------------------------------------
    /**
     * This method sets the CheckNumberEncipheredData.
     * 
     * @param value checkNumberEncipheredData
     */
    // ----------------------------------------------------------------------------
    public void setIdNumberEncipheredData(EncipheredDataIfc idNumberEncipheredData)
    {
        this.idNumberEncipheredData = idNumberEncipheredData;
    }


    //----------------------------------------------------------------------------
    /**
        Converts to a string representing the data in this Object
        @returns string representing the data in this Object
    **/
    //----------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: CheckEntryBeanModel Revision: " + revisionNumber + "\n");
        buff.append("ABANumber [ " + fieldABANumber + "]\n");
        buff.append("AccountNumber [ " + fieldAccountNumber + "]\n");
        buff.append("CheckNumber [ " + fieldCheckNumber + "]\n");
        buff.append("IDNumber [ " + fieldIDNumber + "]\n");
        buff.append("IDState [ " + getState() + "]\n");
        buff.append("DOB [ " + fieldDOB + "]\n");
        buff.append("DOBValid [ " + fieldDOBValid + "]\n");
        buff.append("FocusOnIDNumber [ " + fieldFocusOnIDNumber + "]\n");
        buff.append("checkMICRed [ " + checkMICRed + "]\n");
        buff.append("micrData [ " + micrData + "]\n");
        buff.append("idTypes [ " + idTypes + "]\n");
        buff.append("fieldPhoneNumber [ " + fieldPhoneNumber + "]\n");
        buff.append("fieldMICRNumber [ " + fieldMICRNumber + "]\n");
        buff.append("CardSwiped     [" + cardSwiped + "]\n");
        buff.append("MSRModel       [" + msrModel + "]\n");
        buff.append("displayMicrLineFlag     [" + displayMicrLineFlag + "]\n");

        return(buff.toString());
    }
}

