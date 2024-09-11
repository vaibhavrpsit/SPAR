/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmployeeMasterBeanModel.java /main/16 2013/10/15 14:16:22 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   10/10/13 - removed references to social security number and
 *                         replaced with locale agnostic government id
 *    subrdey   04/05/13 - Change combobox to texfiled for temp employee.
 *    cgreene   10/25/11 - added a renderer to the EmployeeMasterBean for
 *                         renderering Locales instead of dealing with Strings
 *                         directly.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  6    360Commerce 1.5         3/5/2008 2:54:53 PM    Anil Bondalapati
 *       updated to fix the display of storeID on the backoffice
 *  5    360Commerce 1.4         9/26/2006 9:13:09 AM   Christian Greene Moving
 *        password fields to new site
 *  4    360Commerce 1.3         4/2/2006 11:56:21 PM   Dinesh Gautam   Added
 *       code for new fields �Employee login Id� & �Verify Password�
 *  3    360Commerce 1.2         3/31/2005 4:27:58 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:21:20 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:10:50 PM  Robert Pearse   
 *
 * Revision 1.3  2004/04/08 20:33:02  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * 
 * Rev 1.3 Dec 22 2003 17:20:30 jriggins Renamed certain methods and members
 * Resolution for 3597: Employee 7.0 Updates
 * 
 * Rev 1.2 Dec 17 2003 08:53:08 jriggins Added new instance variables and
 * get/sets Resolution for 3597: Employee 7.0 Updates
 * 
 * Rev 1.1 Dec 16 2003 15:29:08 jriggins Added support for the Add Temporary
 * Employee usecase. Resolution for 3597: Employee 7.0 Updates
 * 
 * Rev 1.0 Aug 29 2003 16:10:26 CSchellenger Initial revision.
 * 
 * Rev 1.2 Apr 16 2003 19:18:18 baa add status field Resolution for POS
 * SCR-2165: System crashes if FIND or ADD is selected from blank MBC Customer
 * screen
 * 
 * Rev 1.1 Dec 18 2002 17:40:22 baa add employee preferred locale support
 * Resolution for POS SCR-1843: Multilanguage support
 * 
 * Rev 1.0 Apr 29 2002 14:51:36 msg Initial revision.
 * 
 * Rev 1.0 Mar 18 2002 11:54:26 msg Initial revision.
 * 
 * Rev 1.3 Jan 19 2002 12:15:06 mpm Fixed merge problems. Resolution for POS
 * SCR-228: Merge VABC, Pier 1 changes Resolution for POS SCR-798: Implement
 * pluggable-look-and-feel user interface
 * 
 * Rev 1.1 27 Oct 2001 08:45:54 mpm Merged employee changes from Virginia ABC
 * demonstration.
 * 
 * Rev 1.0 Sep 21 2001 11:36:46 msg Initial revision.
 * 
 * Rev 1.1 Sep 17 2001 13:17:34 msg header update 
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.employee.EmployeeTypeEnum;

/**
 * This is model for the editing the employee.
 * 
 * @version $Revision: /main/16 $
 */
public class EmployeeMasterBeanModel extends POSBaseBeanModel
{
    private static final long serialVersionUID = 4671302016257091420L;
    /** Revision number */
    public static final String revisionNumber = "$Revision: /main/16 $";
    /**
     * First name
     */
    protected String fieldFirstName = "";
    /**
     * Middle name
     */
    protected String fieldMiddleName = "";
    /**
     * Last name
     */
    protected String fieldLastName = "";
    /**
     * id number
     */
    protected String fieldIDNumber = "";
    /**
     * login id number
     */
    protected String fieldLoginIDNumber = "";
    /**
     * Array of Role titles
     */
    protected String[] roles = null;
    /**
     * Array of supported languages
     */
    protected Locale[] supportedLanguages = null;
    /**
     * Index of role selected
     */
    protected int selectedRole = 0;
    /**
     * Index of language selected. Defaults to no selection.
     */
    protected int selectedLanguage = -1;
    /**
     * Preselected language
     */
    protected Locale preferredLanguage;

    /**
     * Role editable indicator
     */
    protected boolean editableIDNumber = true;

    protected String[] statusValues = null;
    protected int selectedStatus = 0;

    /**
     * Employee type. Influences the information that will be shown on the
     * screen.
     * 
     * @see oracle.retail.stores.domain.employee.EmployeeIfc
     */
    protected EmployeeTypeEnum employeeType = EmployeeTypeEnum.STANDARD;
    
    /**
     * Represents the days valid value that was entered in the UI. Days Valid.
     * Only used for temporary employees
     */
    protected int daysValidValue;  
 

    /**
     * Get the value of the FirstName field
     * 
     * @return the value of FirstName
     */
    public String getFirstName()
    {
        return fieldFirstName;
    }

    /**
     * Get the value of the MiddleName field
     * 
     * @return the value of MiddleName
     */
    public String getMiddleName()
    {
        return fieldMiddleName;
    }

    /**
     * Get the value of the LastName field
     * 
     * @return the value of LastName
     */
    public String getLastName()
    {
        return fieldLastName;
    }

    /**
     * Get the value of the IDNumber field
     * 
     * @return the value of IDNumber
     */
    public String getIDNumber()
    {
        return fieldIDNumber;
    }

    /**
     * Get the value of the LoginIDNumber field
     * 
     * @return the value of LoginIDNumber
     */
    public String getLoginIDNumber()
    {
        return fieldLoginIDNumber;
    }

    /**
     * Get the value of the Roles field
     * 
     * @return the value of Roles
     */
    public String[] getRoles()
    {
        return roles;
    }

    /**
     * Get the value of the SelectedRole field
     * 
     * @return the value of SelectedRole
     */
    public int getSelectedRole()
    {
        return selectedRole;
    }

    /**
     * Get languages to display as options in the "Preferred Language" list.
     * 
     * @return the value of Roles
     */
    public Locale[] getSupportedLanguages()
    {
        return supportedLanguages;
    }

    /**
     * Get the value of the selected language. If no selection has bee made yet,
     * i.e. this value is -1, then this method will return the index of the
     * {@link #preferredLanguage} in the {@link #supportedLanguages}.
     * 
     * @return the value of SelectedRole
     */
    public int getSelectedLanguage()
    {
        if (selectedLanguage == -1 && getPreferredLanguage() != null)
        {
            Locale preferredLocale = LocaleMap.getBestMatch(getPreferredLanguage());
            Locale[] supportedLocales = getSupportedLanguages();
            if (supportedLocales != null)
            {
                for (int i = supportedLocales.length - 1; i >= 0; i--)
                {
                    if (preferredLocale.equals(supportedLocales[i]))
                    {
                        return i;
                    }
                }
            }
        }
        return selectedLanguage;
    }

    /**
     * Get the default value of the preferred language field
     * 
     * @return the default value of the preferred language field
     */
    public Locale getPreferredLanguage()
    {
        return preferredLanguage;
    }

    /**
     * Sets the FirstName field
     * 
     * @param firstName the value to be set for FirstName
     */
    public void setFirstName(String firstName)
    {
        fieldFirstName = firstName;
    }

    /**
     * Sets the MiddleName field
     * 
     * @param middleName the value to be set for MiddleName
     */
    public void setMiddleName(String middleName)
    {
        fieldMiddleName = middleName;
    }

    /**
     * Sets the LastName field
     * 
     * @param lastName the value to be set for LastName
     */
    public void setLastName(String lastName)
    {
        fieldLastName = lastName;
    }

    /**
     * Sets the IDNumber field
     * 
     * @param iDNumber the value to be set for IDNumber
     */
    public void setIDNumber(String iDNumber)
    {
        fieldIDNumber = iDNumber;
    }

    /**
     * Sets the LoginIDNumber field
     * 
     * @param loginIDNumber the value to be set for loginIDNumber
     */
    public void setLoginIDNumber(String loginIDNumber)
    {
        fieldLoginIDNumber = loginIDNumber;
    }

    /**
     * Sets the Roles field
     * 
     * @param values the value to be set for Roles
     */
    public void setRoles(String[] values)
    {
        roles = values;
    }

    /**
     * Sets the SelectedRole field
     * 
     * @param value the value to be set for SelectedRole
     */
    public void setSelectedRole(int value)
    {
        selectedRole = value;
    }

    /**
     * Set languages to display as options in the "Preferred Language" list.
     * 
     * @param values the value to be set for languages
     */
    public void setSupportedLanguages(Locale[] values)
    {
        supportedLanguages = values;
    }

    /**
     * Sets the SelectedLanguage field
     * 
     * @param value the value to be set for SelectedLanguage
     */
    public void setSelectedLanguage(int value)
    {
        selectedLanguage = value;
    }

    /**
     * Sets the default value for the preferred language field
     * 
     * @param value
     */
    public void setPreferredLanguage(Locale value)
    {
        preferredLanguage = value;
    }

    /**
     * Sets the editableIDNumber attribute.
     * 
     * @param value enabled boolean to set editableIDNumber
     */
    public void setEditableIDNumber(boolean value)
    {
        editableIDNumber = value;
    }

    /**
     * Get the editableIDNumber attribute.
     * 
     * @return boolean editableIDNumber returned
     */
    public boolean getEditableIDNumber()
    {
        return (editableIDNumber);
    }

    /**
     * Returns the selectedStatus.
     * 
     * @return int
     */
    public int getSelectedStatus()
    {
        return selectedStatus;
    }

    /**
     * Returns the statusValues.
     * 
     * @return String[]
     */
    public String[] getStatusValues()
    {
        return statusValues;
    }

    /**
     * Sets the selectedStatus.
     * 
     * @param selectedStatus The selectedStatus to set
     */
    public void setSelectedStatus(int value)
    {
        selectedStatus = value;
    }

    /**
     * Sets the statusValues.
     * 
     * @param statusValues The statusValues to set
     */
    public void setStatusValues(String[] values)
    {
        statusValues = values;
    }

    /**
     * Returns the employee type.
     * 
     * @return EmployeeTypeEnum representing the employee type
     * @see EmployeeIfc
     */
    public EmployeeTypeEnum getEmployeeType()
    {
        return this.employeeType;
    }

    /**
     * Sets the employee type.
     * 
     * @param employeeType the employee type to set
     * @see EmployeeIfc
     */
    public void setEmployeeType(EmployeeTypeEnum employeeType)
    {
        this.employeeType = employeeType;
    }
    
    
    /**
     * Sets the days valid for temporary employees.
     * 
     * @param daysValidValue the number of days the employee is to be valid
     */

    public int getDaysValidValue()
    {
        return daysValidValue;
    }

    /**
     * @param sets the daysValidValue
     */
    public void setDaysValidValue(int daysValidValue)
    {
        this.daysValidValue = daysValidValue;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder buff = new StringBuilder();

        buff.append("Class: EmployeeMasterBeanModel Revision: " + revisionNumber + "\n");
        buff.append("FirstName [" + fieldFirstName + "]\n");
        buff.append("MiddleName [" + fieldMiddleName + "]\n");
        buff.append("LastName [" + fieldLastName + "]\n");
        buff.append("IDNumber [" + fieldIDNumber + "]\n");
        buff.append("LoginIDNumber [" + fieldLoginIDNumber + "]\n");
        // buff.append("Roles [" + roles + "]\n");
        buff.append("SelectedRole [" + selectedRole + "]\n");
        buff.append("SelectedLanguage [" + selectedLanguage + "]\n");
        return (buff.toString());
    }

}
