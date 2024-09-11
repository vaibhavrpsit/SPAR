/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/MailBankCheckInfoDeliveryBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - Mail bank check info delivery bean model
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.domain.utility.CountryIfc;

// ----------------------------------------------------------------------------
/**
 * This is the model used to pass customer information
 *
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
// ----------------------------------------------------------------------------
public class MailBankCheckInfoDeliveryBeanModel extends DeliveryAddressBeanModel
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    // Flag indicating a customer is linked from and Add or Find
    protected boolean custLinked = false;

    // Flag indicating a customer is linked
    protected boolean transLinked = false;

    // Flag indicating something was changed on the screen
    protected boolean changeState = false;

    // Flag indicating transaction is from layaway
    protected boolean layawayFlag = false;

    // Journal string for updated customer fields
    protected String journalString = "";

    // ---------------------------------------------------------------------
    /**
     * MailBankCheckInfoBeanModel constructor.
     */
    // ---------------------------------------------------------------------
    public MailBankCheckInfoDeliveryBeanModel()
    {
        super();
        this.setContactInfoOnly(true);
    }

    // ----------------------------------------------------------------------------
    /**
     * Get flag that shows whether or not data was updated
     *
     * @return whether or not data was updated
     */
    // ----------------------------------------------------------------------------
    public boolean getChangeState()
    {
        return changeState;
    }

    // ----------------------------------------------------------------------------
    /**
     * Get the value of the transLinked field
     *
     * @return the value of transLinked
     */
    // ----------------------------------------------------------------------------
    public boolean isTransLinked()
    {
        return transLinked;
    }

    // ----------------------------------------------------------------------------
    /**
     * Sets the transLinked field
     *
     * @param the value to be set for transLinked
     */
    // ----------------------------------------------------------------------------
    public void setTransLinked(boolean linked)
    {
        transLinked = linked;
    }

    // ----------------------------------------------------------------------------
    /**
     * Sets the value that shows whether or not data was updated
     *
     * @param the value representing whether or not data was updated
     */

    // ----------------------------------------------------------------------------
    public void setChangeState(boolean changed)
    {
        changeState = changed;
    }

    // ---------------------------------------------------------------------
    /**
     * Gets the Country Names property values.
     *
     * @return The country name list.
     *
     */
    public String[] getCountryNames()
    {

        CountryIfc[] countryList = super.getCountries();
        int length = countryList.length;

        int xxIndex = -1;
        for (int i = 0; i < length; i++)
        {
            if (countryList[i].getCountryCode().equals("XX"))
                xxIndex = i;
        }

        String[] completeCountryNames = super.getCountryNames();
        if (xxIndex == -1)
            return (completeCountryNames);

        String[] countryNames = new String[length - 1];

        // index for new Country name array
        int j = 0;
        for (int i = 0; i < length; i++)
        {
            if (xxIndex != i)
            {
                countryNames[j] = completeCountryNames[i];
                j++;
            }
        }

        return countryNames;
    }

    // ----------------------------------------------------------------------------
    /**
     * Get the value of the layawayFlag field
     *
     * @return the value of layawayFlag
     */
    // ----------------------------------------------------------------------------
    public boolean isLayawayFlag()
    {
        return layawayFlag;
    }

    // ----------------------------------------------------------------------------
    /**
     * Sets the layawayFlag field
     *
     * @param the value to be set for layawayFlag
     */
    // ----------------------------------------------------------------------------
    public void setLayawayFlag(boolean layaway)
    {
        layawayFlag = layaway;
    }

    // -------------------------------------------------------------------------
    /**
     * Get the value of the journalString field
     *
     * @return the value of journalString
     */
    // -------------------------------------------------------------------------
    public String getJournalString()
    {
        return journalString;
    }

    // ----------------------------------------------------------------------------
    /**
     * Sets the journalString field
     *
     * @param the value to be set for journalString
     */
    // ----------------------------------------------------------------------------
    public void setJournalString(String value)
    {
        journalString = value;
    }

    // ----------------------------------------------------------------------------
    /**
     * Converts to a string representing the data in this Object
     *
     * @returns string representing the data in this Object
     */
    // ----------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer(super.toString());

        buff.append("Class: MailBankCheckInfoBeanModel Revision: " + revisionNumber + "\n");
        buff.append("changed state [" + changeState + "]\n");
        buff.append("layaway flag [" + layawayFlag + "]\n");
        return (buff.toString());
    }

    /**
     * @return
     */
    public boolean isCustLinked()
    {
        return custLinked;
    }

    /**
     * @param b
     */
    public void setCustLinked(boolean b)
    {
        custLinked = b;
    }

}
