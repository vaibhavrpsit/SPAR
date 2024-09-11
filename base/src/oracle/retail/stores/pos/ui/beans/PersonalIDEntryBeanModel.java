/* ===========================================================================
* Copyright (c) 1999, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PersonalIDEntryBeanModel.java /rgbustores_13.4x_generic_branch/3 2011/09/02 13:05:38 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/19/11 - encryption CR
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  02/17/09 - fixed personal id business customer issue
 *    mdecama   12/03/08 - Updates per Code Review
 *    mdecama   12/03/08 - Added firstName and lastName
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:20 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:24:03 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:13:02 PM  Robert Pearse
 *
 * Revision 1.4  2004/04/09 16:56:00  cdb
 * @scr 4302 Removed double semicolon warnings.
 *
 * Revision 1.3  2004/03/16 17:15:18  build
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 20:56:26  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 * updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:11:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Apr 10 2003 13:34:04   bwf
 * Remove instanceof UtilityManagerIfc and replaced with UIUtilities.
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.3   Apr 09 2003 15:49:04   HDyer
 * Cleanup from code review.
 * Resolution for POS SCR-1854: Return Prompt for ID feature for POS 6.0
 *
 *    Rev 1.2   Feb 18 2003 12:41:32   HDyer
 * Extend this class from the ReasonBeanModel to make use of common functionality.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.1   Feb 05 2003 11:23:10   HDyer
 * Display the localized strings for ID types rather than the key/tag value.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.0   Dec 16 2002 09:40:34   HDyer
 * Initial revision.
 * Resolution for POS-SCR 1854: Return Prompt for ID feature for POS 6.0
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Vector;

import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.factory.FoundationObjectFactoryIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;

import org.apache.log4j.Logger;

/**
 * Model for personal ID entry UI
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/3 $
 */
public class PersonalIDEntryBeanModel extends ReasonBeanModel
{
    /**
     * Generated Serial Version UID
     */
    private static final long serialVersionUID = 8254425299995724544L;
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/3 $";
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(PersonalIDEntryBeanModel.class);

    /**
     * Identification Number {ie Drivers License number}
     * @deprecated as of 13.4. Use {@link #personalID} instead.
     */
    protected String fieldIDNumber = "";
    /**
     * Personal IDNumber 
     */
    protected EncipheredDataIfc personalID;
    /**
     * Set focus onID Number Flag
     */
    protected boolean fieldFocusOnIDNumber = false;
    /**
     * First Name
     */
    protected String firstName = "";
    /**
     * Last Name
     */
    protected String lastName = "";

    // business customer indicator
    protected boolean businessCustomer = false;
    // customer name field
    protected String fieldCustomerName = "";

    // editable indicator
    protected boolean editableFields = true;
    // customer name
    protected String customerName = "";

    /**
     * Constructs PersonalIDEntryBeanModel object.
     */
    public PersonalIDEntryBeanModel()
    {
    }

    /**
     * Gets the value of the IDNumber field
     * 
     * @return the value of IDNumber
     * @deprecated as of 13.4. use {@link #getPersonalID()} instead.
     */
    public String getIDNumber()
    {
        return fieldIDNumber;
    }

    /**
     * Gets the value of the FocusOnIDNumber field
     * 
     * @return the value of FocusOnIDNumber
     */
    public boolean isFocusOnIDNumber()
    {
        return fieldFocusOnIDNumber;
    }

    /**
     * Sets the business customer indicator. True is for business customer false
     * is for regular customers
     * 
     * @param enabled boolean to set editableFields
     */
    public void setBusinessCustomer(boolean value)
    {
        businessCustomer = value;
    }

    /**
     * Returns true if this is a business customer false otherwise
     * 
     * @return boolean business customer indicator
     */
    public boolean isBusinessCustomer()
    {
        return (businessCustomer);
    }

    /**
     * Gets the business organization name.
     * 
     * @return String organization name
     * @see #setOrgName
     */
    public String getOrgName()
    {
        return fieldCustomerName;
    }

    /**
     * Sets organization name.
     * 
     * @param value organization name
     * @see #setOrgName
     */
    public void setOrgName(String value)
    {
        fieldCustomerName = value;
    }

    /**
     * Sets the editableFields attribute.
     * 
     * @param enabled boolean to set editableFields
     */
    public void setEditableFields(boolean value)
    {
        editableFields = value;
    }

    /**
     * Get the editableFields attribute.
     * 
     * @return boolean editableFields returned
     */
    public boolean getEditableFields()
    {
        return (editableFields);
    }

    /**
     * Gets the value of the reasonCodes property containing the IDTypes vector.
     * 
     * @return the value of IDTypes
     */
    public Vector getIDTypes()
    {
        // Note that the ID types are managed by the ReasonBeanModel. ID types
        // are stored and managed as Reason Codes.
        return super.getReasonCodes();
    }

    /**
     * Gets the index of the selectedReason property which is the selected
     * IDType.
     * 
     * @return the value of SelectedIDType
     */
    public int getSelectedIDType()
    {
        // Note that the ID types are managed by the ReasonBeanModel. ID types
        // are stored and managed as Reason Codes.
        return super.getSelectedIndex();
    }

    /**
     * Sets the IDNumber field
     * 
     * @param the value to be set for IDNumber
     * @deprecated as of 13.4. use {@link #setPersonalID(EncipheredDataIfc)} instead.
     */
    public void setIDNumber(String iDNumber)
    {
        fieldIDNumber = iDNumber;
    }

    /**
     * Gets Personal ID Number Enciphered Object
     * 
     * @return EncipheredDataIfc personalID
     */
    public EncipheredDataIfc getPersonalID()
    {
        if (personalID == null)
        {
            FoundationObjectFactoryIfc factory = FoundationObjectFactory.getFactory();
            personalID = factory.createEncipheredDataInstance();
        }
        return personalID;
    }

    /**
     * Sets Personal ID Number Enciphered Object
     * 
     * @param EncipheredDataIfc personalID
     */
    public void setPersonalID(EncipheredDataIfc personalID)
    {
        this.personalID = personalID;
    }

    /**
     * Sets the FocusOnIDNumber field
     * 
     * @param the value to be set for FocusOnIDNumber
     */
    public void setFocusOnIDNumber(boolean focusOnIDNumber)
    {
        fieldFocusOnIDNumber = focusOnIDNumber;
    }

    /**
     * Sets the reasonCodes field containing the IDTypes
     * 
     * @param the value to be set for IDTypes
     */
    public void setIDTypes(Vector types)
    {
        // Note that the ID types are managed by the ReasonBeanModel. ID types
        // are stored and managed as Reason Codes.
        super.setReasonCodes(types);
    }

    /**
     * Sets the selected reason code properties containing the selected ID type.
     * 
     * @param the value to be set for SelectedIDType
     */
    public void setSelectedIDType(int selected)
    {
        // Note that the ID types are managed by the ReasonBeanModel. ID types
        // are stored and managed as Reason Codes.
        super.setSelectedReasonCode(selected);
    }

    /**
     * @return the firstName
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    /**
     * @param customerName the lastName to set
     */
    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    /**
     * @return the customerName
     */
    public String getCustomerName()
    {
        return customerName;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ReasonBeanModel#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder buff = new StringBuilder();

        buff.append(super.toString());
        buff.append("Class: PersonalIDEntryBeanModel Revision: " + revisionNumber + "\n");
        buff.append("IDNumber [ " + fieldIDNumber + "]\n");
        buff.append("FocusOnIDNumber [ " + fieldFocusOnIDNumber + "]\n");
        buff.append("idTypes [ " + super.getReasonCodes() + "]\n");
        buff.append("FistName [ " + firstName + "]\n");
        buff.append("LastName [ " + lastName + "]\n");

        return buff.toString();
    }
}