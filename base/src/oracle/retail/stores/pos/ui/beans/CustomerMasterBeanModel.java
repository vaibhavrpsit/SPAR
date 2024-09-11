/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerMasterBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:41 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:23 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:10:00   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 20 2003 18:19:00   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.1   Aug 07 2002 19:34:16   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
//java imports
import java.util.Date;
import java.util.GregorianCalendar;

import oracle.retail.stores.domain.DomainGateway;
                                                     
//----------------------------------------------------------------------------
/**
   This class is used with the CustomerMasterBean class. <p>
   @version $KW=@(#); $Ver; $EKW;
   @deprecated as of release 6.0 replaced by CustomerInfoBeanModel
*/
//----------------------------------------------------------------------------
public class CustomerMasterBeanModel extends CustomerInfoBeanModel
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
     
    //@deprecated as of release 5.5 replaced by birthDate
    protected Date fieldBirthdate = null;   


     
    //------------------------------------------------------------------------
    /**
       CustomerMasterBeanModel constructor comment.
    */
    //------------------------------------------------------------------------
    public CustomerMasterBeanModel()
    {
        super();
    }
    //------------------------------------------------------------------------
    /**
       Gets the birthdate property (Date) value.
       NOTE!!! this only contains a valid Month and Day
       Use getBirthYear to get year!!!!
       @return The birthdate property value.
       @deprecated as of release 5.5 replace by EYSDate getBirthMonthAndDay()
       @see #setBirthdate
    */
    //------------------------------------------------------------------------
    public Date getBirthdate()
    {
       Date dob = null;
       if (birthdate != null)
       {
         dob = birthdate.dateValue();
       }
       return dob;
    }

  
    //------------------------------------------------------------------------
    /**
     * Gets the middleName property (java.lang.String) value.
     * @return The middleName property value.
     * @see #setMiddleName
     * @deprecated as of release 6.0 no longer used
     */
    //------------------------------------------------------------------------
    public String getMiddleName()
    {
        return fieldMiddleName;
    }

 
    //------------------------------------------------------------------------
    /**
     * Gets the suffix property (java.lang.String) value.
     * @return The suffix property value.
     * @see #setSuffix
     * @deprecated as of release 6.0 no longer used
     */
    //------------------------------------------------------------------------
    public String getSuffix()
    {
        return fieldSuffix;
    }
     //------------------------------------------------------------------------
    /**
     * Sets the suffix property (java.lang.String) value.
     * @param suffix The new value for the property.
     * @see #getSuffix
     * @deprecated as of release 6.0 no longer used
     */
    //------------------------------------------------------------------------
    public void setSuffix(String suffix) 
    {
        fieldSuffix = suffix;
    }
    //------------------------------------------------------------------------
    /**
     * Sets the birthdate property (Date) value.
     * @param birthdate The new value for the property.
     * @see #getBirthdate
     * @deprecated as of release 6.0 replaced by setBirthdate(EYSDate value)
     */
    //------------------------------------------------------------------------
    public void setBirthdate(Date value) 
    {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(value);  // change date to Calendar date

        // initialize EYSDate
       if (birthdate == null)
       {
         birthdate= DomainGateway.getFactory().getEYSDateInstance();
       }
        birthdate.fromGregorianCalendar(cal);
    }

 
    //------------------------------------------------------------------------
    /**
       Not implemented
       @deprecated Use #getBirthdate
     */
    //------------------------------------------------------------------------
    public String getBirthday()
    {
        return "";
    }
    //------------------------------------------------------------------------
    /**
       Not implemented.
       @deprecated Use #getBirthdate
     */
    //------------------------------------------------------------------------
    public String getBirthyear()
    {
        return "";
    }
    //------------------------------------------------------------------------
    /**
       Not implemented.
       @deprecated Use #setBirthdate
     */
    //------------------------------------------------------------------------
    public void setBirthday(String day)
    {
    }
    //------------------------------------------------------------------------
    /**
       Not implemented.
       @deprecated Use #setBirthdate
     */
    //------------------------------------------------------------------------
    public void setBirthyear(String year)
    {
    }

}
