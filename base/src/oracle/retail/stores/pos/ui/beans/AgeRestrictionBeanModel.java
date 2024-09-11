/* ===========================================================================
* Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AgeRestrictionBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  03/13/09 - included birth date & birth year as two separate
 *                         entities
 *
 * ===========================================================================
 * $Log:
 1    360Commerce 1.0         12/13/2005 4:47:07 PM  Barry A. Pape
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.PersonConstantsIfc;

//--------------------------------------------------------------------------
/**
     This bean model holds the customer date of birth.
     $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//--------------------------------------------------------------------------
public class AgeRestrictionBeanModel extends POSBaseBeanModel
{
    /** DOB Of customer */
    protected EYSDate dateOfBirth = null;

    /**Birth date(MM/DD) of the customer*/
    protected EYSDate birthdate = null;

    /**Birth year of the customer*/
    protected long BirthYear = PersonConstantsIfc.YEAR_OF_BIRTH_UNSPECIFIED;

    /**Is birth date valid*/
    protected boolean fieldValidBirthdate = false;

    /**Is birth year valid*/
    protected boolean fieldValidBirthYear = false;


    /**
    @return Returns the BirthDate.
    */
    public EYSDate getBirthdate()
    {
      return birthdate;
    }

    /**
    @param birthdate : The birthdate to set.
    */
    public void setBirthdate(EYSDate birthdate)
    {
      this.birthdate = birthdate;
    }

    /**
    @return Returns the BirthYear.
    */
    public long getBirthYear()
    {
      return BirthYear;
    }

    /**
    @param birthYear : The birthYear to set.
    */
    public void setBirthYear(long birthYear)
    {
      BirthYear = birthYear;
    }

    /**
        Returns validity of birth year.
        @return boolean
    */
    public boolean isBirthYearValid()
    {
        return fieldValidBirthYear;
    }

    /**
        Returns validity of birth date.
        @return boolean
    */
    public boolean isBirthdateValid()
    {
        return fieldValidBirthdate;
    }

    /**
        Sets the validity of birthyear
        @param boolean b
    */
    public void setBirthYearValid(boolean b)
    {
        fieldValidBirthYear = b;
    }

    /**
        Sets the validity of birthdate
        @param boolean b
    */
    public void setBirthdateValid(boolean b)
    {
        fieldValidBirthdate = b;
    }

    public EYSDate getDateOfBirth()
    {
      return dateOfBirth;
    }

    public void setDateOfBirth(EYSDate dateOfBirth)
    {
      this.dateOfBirth = dateOfBirth;
    }
}
