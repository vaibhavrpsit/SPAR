/* ===========================================================================
* Copyright (c) 2005, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CaptureIRSCustomerBeanModel.java /main/16 2013/06/13 12:21:20 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  06/13/13 - added null check for DOB in toString method.
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   06/30/11 - Encryption CR
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    mkochumm  11/20/08 - cleanup based on i18n changes
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         12/13/2005 4:47:07 PM  Barry A. Pape   
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;

/**
 * This bean model contains the information related to the IRS Customer Bean
 * 
 * @version $Revision: /main/16 $
 */
public class CaptureIRSCustomerBeanModel extends CountryModel
{
    private static final long serialVersionUID = 2322227791055065936L;

    /**
     * Revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    // Fields that contain Customer Information
    protected String fieldFirstName = "";
    protected String fieldMiddleInitial = "";
    protected String fieldLastName = "";
    protected EYSDate fieldDateOfBirth = null;
    /**
     * @deprecated as of 13.4. Use {@link #taxPayerID} instead.
     */
    protected String fieldTaxpayerID = "";
    protected EncipheredDataIfc taxPayerID;
    protected String fieldOccupation = "";
    protected String fieldAddressLine1 = "";
    protected String fieldAddressLine2 = "";
    protected String fieldCity = "";
    protected String fieldPostalCode = "";
    protected String fieldExtPostalCode = "";

    /**
     * CustomerInfoBeanModel constructor.
     */
    public CaptureIRSCustomerBeanModel()
    {
    }

    /**
     * Gets the addressLine1 property (java.lang.String) value.
     * 
     * @return The addressLine1 property value.
     * @see #setAddressLine1
     */
    public String getAddressLine1()
    {
        return fieldAddressLine1;
    }

    /**
     * Sets the addressLine1 property (java.lang.String) value.
     * 
     * @param addressLine1 The new value for the property.
     * @see #getAddressLine1
     */
    public void setAddressLine1(String addressLine1)
    {
        fieldAddressLine1 = addressLine1;
    }

    /**
     * Gets the addressLine2 property (java.lang.String) value.
     * 
     * @return The addressLine2 property value.
     * @see #setAddressLine2
     */
    public String getAddressLine2()
    {
        return fieldAddressLine2;
    }

    /**
     * Sets the addressLine2 property (java.lang.String) value.
     * 
     * @param addressLine2 The new value for the property.
     * @see #getAddressLine2
     */
    public void setAddressLine2(String addressLine2)
    {
        fieldAddressLine2 = addressLine2;
    }

    /**
     * Gets the city property (java.lang.String) value.
     * 
     * @return The city property value.
     * @see #setCity
     */
    public String getCity()
    {
        return fieldCity;
    }

    /**
     * Sets the city property (java.lang.String) value.
     * 
     * @param city The new value for the property.
     * @see #getCity
     */
    public void setCity(String city)
    {
        fieldCity = city;
    }

    /**
     * Gets the Country Names property values.
     * 
     * @return The country name list.
     */
    public String[] getCountryNames()
    {
        return (super.getCountryNames());
    }

    /**
     * Gets the firstName property (java.lang.String) value.
     * 
     * @return The firstName property value.
     * @see #setFirstName
     */
    public String getFirstName()
    {
        return fieldFirstName;
    }

    /**
     * Sets the firstName property (java.lang.String) value.
     * 
     * @param firstName The new value for the property.
     * @see #getFirstName
     */
    public void setFirstName(String firstName)
    {
        fieldFirstName = firstName;
    }

    /**
     * Gets the middleInitial property (java.lang.String) value.
     * 
     * @return The middleInitial property value.
     */
    public String getMiddleInitial()
    {
        return fieldMiddleInitial;
    }

    /**
     * Sets the middleInitial property (java.lang.String) value.
     * 
     * @param middleInitial The new value for the property.
     */
    public void setMiddleInitial(String middleInitial)
    {
        fieldMiddleInitial = middleInitial;
    }

    /**
     * Gets the lastName property (java.lang.String) value.
     * 
     * @return The lastName property value.
     * @see #setLastName
     */
    public String getLastName()
    {
        return fieldLastName;
    }

    /**
     * Sets the lastName property (java.lang.String) value.
     * 
     * @param lastName The new value for the property.
     * @see #getLastName
     */
    public void setLastName(String lastName)
    {
        fieldLastName = lastName;
    }

    /**
     * Gets the dateOfBirth property (java.lang.String) value.
     * 
     * @return The dateOfBirth property value.
     */
    public EYSDate getDateOfBirth()
    {
        return fieldDateOfBirth;
    }

    /**
     * Sets the dateOfBirth property (java.lang.String) value.
     * 
     * @param dateOfBirth The new value for the property.
     */
    public void setDateOfBirth(EYSDate dateOfBirth)
    {
        fieldDateOfBirth = dateOfBirth;
    }

    /**
     * Gets the taxpayerID property (java.lang.String) value.
     * 
     * @return The taxpayerID property value.
     * @deprecated as of 13.4. Use {@link #getTaxPayerID()} instead.
     */
    public String getTaxpayerID()
    {
        return fieldTaxpayerID;
    }

    /**
     * Sets the taxpayerID property (java.lang.String) value.
     * 
     * @param taxpayerID The new value for the property.
     * @deprecated as of 13.4. Use {@link #setTaxPayerID(EncipheredDataIfc)} instead.
     */
    public void setTaxpayerID(String taxpayerID)
    {
        fieldTaxpayerID = taxpayerID;
    }

    /**
     * Sets tax payer id.
     *
     * @return
     */
    public EncipheredDataIfc getTaxPayerID()
    {
        return taxPayerID;
    }

    /**
     * Sets the tax payer id.
     * 
     * @param taxPayerID
     */
    public void setTaxPayerID(EncipheredDataIfc taxPayerID)
    {
        this.taxPayerID = taxPayerID;
    }

    /**
     * Gets the occupation property (java.lang.String) value.
     * 
     * @return The occupation property value.
     */
    public String getOccupation()
    {
        return fieldOccupation;
    }

    /**
     * Sets the occupation property (java.lang.String) value.
     * 
     * @param occupation The new value for the property.
     */
    public void setOccupation(String occupation)
    {
        fieldOccupation = occupation;
    }

    /**
     * Gets the postalCode property (java.lang.String) value.
     * 
     * @return The postalCode property value.
     * @see #setPostalCode
     */
    public String getPostalCode()
    {
        return fieldPostalCode;
    }

    /**
     * Sets the postalCode property (java.lang.String) value.
     * 
     * @param postalCode The new value for the property.
     * @see #getPostalCode
     */
    public void setPostalCode(String postalCode)
    {
        fieldPostalCode = postalCode;
    }

    /**
     * Gets the extPostalCode property (java.lang.String) value.
     * 
     * @return The extPostalCode property value.
     * @see #setExtPostalCode
     * @deprecated since v13.1
     */
    public String getExtPostalCode()
    {
        return fieldExtPostalCode;
    }

    /**
     * Sets the extPostalCode property (java.lang.String) value.
     * 
     * @param extPostalCode The new value for the property.
     * @see #getExtPostalCode
     * @deprecated since v13.1
     */
    public void setExtPostalCode(String extPostalCode)
    {
        fieldExtPostalCode = extPostalCode;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder("CustomerInfoBeanModel{");
        buf.append("First Name = ").append(getFirstName()).append(",");
        buf.append("Last Name = ").append(getLastName()).append(",");
        buf.append("Middle Initial = ").append(getMiddleInitial()).append(",");
        if (getDateOfBirth() != null)
        {
            buf.append("Date of Birth = ").append(getDateOfBirth().toFormattedString()).append(",");
        }else
        {
            buf.append("Date of Birth = null,");
        }
        buf.append("Taxpayer ID = ").append(getTaxPayerID()).append(",");
        buf.append("Occupation = ").append(getOccupation()).append(",");
        buf.append("Address Line 1 = ").append(getAddressLine1()).append(",");
        buf.append("Address Line 2 = ").append(getAddressLine2()).append(",");
        buf.append("City = ").append(getCity()).append(",");
        buf.append("Country = ");
        if (getCountryInfo() != null)
        {
            buf.append(getCountryInfo().getCountryName()).append(",");
        }
        else
        {
            buf.append("null,");
        }
        if (getStateInfo() != null)
        {
            buf.append(getStateInfo().getStateName()).append(",");
        }
        else
        {
            buf.append("null,");
        }
        buf.append("PostalCode = ").append(getPostalCode());
        buf.append("}");
        return buf.toString();
    }
}
