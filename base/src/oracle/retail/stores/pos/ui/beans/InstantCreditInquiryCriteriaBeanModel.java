/*===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/InstantCreditInquiryCriteriaBeanModel.java /main/2 2013/10/15 14:16:21 asinton Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* asinton     10/10/13 - removed references to social security number and
*                        replaced with locale agnostic government id
* sgu         05/20/11 - refactor instant credit inquiry flow
* sgu         05/18/11 - add new class
* sgu         05/18/11 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;

// foundation imports


/**
 * Data transport between the bean and the application for instant credit data
 */
public class InstantCreditInquiryCriteriaBeanModel extends ReasonBeanModel
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8898254671689897076L;

    // customer information fields
    protected String postalCode;
    protected EncipheredDataIfc governmentId;
    protected String homePhone;
    protected String referenceNumber;
    protected boolean referenceNumberSearch = true;
    protected boolean firstRun = true;

    //---------------------------------------------------------------------
    /**
        Gets the postal code attribute. <P>
    **/
    //---------------------------------------------------------------------
    public String getPostalCode()
    {
        return postalCode;
    }

    //---------------------------------------------------------------------
    /**
        Sets the postal code attribute. <P>
        @param zipCode  string to set zip code

    **/
    //---------------------------------------------------------------------
    public void setPostalCode(String zipCode)
    {
        this.postalCode = zipCode;
    }

    /**
     * Returns the <code>governmentId</code> value.
     * @return the governmentId
     */
    public EncipheredDataIfc getGovernmentId()
    {
        return governmentId;
    }

    /**
     * Sets the <code>governmentId</code> value.
     * @param governmentId the governmentId to set
     */
    public void setGovernmentId(EncipheredDataIfc governmentId)
    {
        this.governmentId = governmentId;
    }

    //---------------------------------------------------------------------
    /**
        Gets the home phone attribute. <P>
    **/
    //---------------------------------------------------------------------
    public String getHomePhone()
    {
        return homePhone;
    }

    //---------------------------------------------------------------------
    /**
        Sets the home phone attribute. <P>
        @param homePhone  string to set homePhone

    **/
    //---------------------------------------------------------------------
    public void setHomePhone(String homePhone)
    {
        this.homePhone = homePhone;
    }

    //---------------------------------------------------------------------
    /**
        Sets the application reference attribute. <P>
        @param appReference  string to set application reference

    **/
    //---------------------------------------------------------------------
    public String getReferenceNumber()
    {
        return referenceNumber;
    }

    //---------------------------------------------------------------------
    /**
        Sets the application reference attribute. <P>
        @param appReference  string to set application reference

    **/
    //---------------------------------------------------------------------
    public void setReferenceNumber(String referenceNumber)
    {
        this.referenceNumber = referenceNumber;
    }

    //---------------------------------------------------------------------
    /**
         Reset all fields
    **/
    //---------------------------------------------------------------------
    public void reset()
    {
        postalCode = "";
        governmentId = null;
        homePhone = "";
        referenceNumber = "";
    }

    //---------------------------------------------------------------------
    /**
        Gets the first run attribute. <P>

    **/
    //---------------------------------------------------------------------
    public boolean isFirstRun()
    {
        return firstRun;
    }

    //---------------------------------------------------------------------
    /**
        Sets the first run attribute. <P>
        @param firstRun  string to set first run.

    **/
    //---------------------------------------------------------------------
    public void setFirstRun(boolean firstRun)
    {
        this.firstRun = firstRun;
    }

    public boolean isReferenceNumberSearch()
    {
        return referenceNumberSearch;
    }

    public void setReferenceNumberSearch(boolean referenceNumberSearch)
    {
        this.referenceNumberSearch = referenceNumberSearch;
    }
                                  // end geteditableFields()
}