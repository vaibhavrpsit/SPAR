/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/ARTSCurrencyType.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 10:01:08 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:27:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:31 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:50  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:24  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:29:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Jun 03 2002 16:34:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:44:42   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:04:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 20 2001 15:55:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:35:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;
// java imports
import java.io.Serializable;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

//----------------------------------------------------------------------------
/**
    This class is used for retrieving currency data from an ARTS database.
    This class is required since the domain currency type objects are largely
    unconcerned with the concept of business dates, which are 
    required to select the effective exchange rates from an ARTS
    database.
    @see oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc
    @see oracle.retail.stores.domain.utility.CountryCodesIfc
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class ARTSCurrencyType implements Serializable
{                                       // begin class ARTSCurrencyType
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -6607134680480500055L;

    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        country of issue (by ISO-3166 standard)
    **/
    protected String countryCode = null;
    /**
        business date
    **/
    protected EYSDate businessDate = 
      DomainGateway.getFactory().getEYSDateInstance();

        //---------------------------------------------------------------------
        /**
                Constructs ARTSCurrencyType object. <P>
        **/
        //---------------------------------------------------------------------
        public ARTSCurrencyType()
        {                                   // begin ARTSCurrencyType()
        }                                   // end ARTSCurrencyType()
        
        //---------------------------------------------------------------------
        /**
                Constructs ARTSCurrencyType object, specifying business date. <P>
                @param bDate business date
        **/
        //---------------------------------------------------------------------
        public ARTSCurrencyType(EYSDate bDate)
        {                                   // begin ARTSCurrencyType()
            businessDate = bDate;
        }                                   // end ARTSCurrencyType()

        //---------------------------------------------------------------------
        /**
                Constructs ARTSCurrencyType object, specifying country code and
                business date. <P>
                @param k country code
                @param bDate business date
        **/
        //---------------------------------------------------------------------
        public ARTSCurrencyType(String k,
                                EYSDate bDate)
        {                                   // begin ARTSCurrencyType()
            this(bDate);
            countryCode = k;
        }                                   // end ARTSCurrencyType()

    //---------------------------------------------------------------------
    /**
        Creates clone of this object. <P>
        @return Object clone of this object
    **/
    //--------------------------------------------------------------------- 
    public Object clone()
    {                                   // begin clone()
        // instantiate new object
                ARTSCurrencyType c = new ARTSCurrencyType();
                
                // set clone attributes
                setCloneAttributes(c);

        // pass back Object
        return((Object) c);
    }                                   // end clone()

    //---------------------------------------------------------------------
    /**
        Set attributes for clone method. <P>
    **/
    //---------------------------------------------------------------------
    public void setCloneAttributes(ARTSCurrencyType newClass)
    {                                   // begin setCloneAttributes()
        // set values
        if (countryCode != null)
        {
            newClass.setCountryCode(countryCode);
        }
        if (businessDate != null)
        {
            newClass.setBusinessDate((EYSDate) getBusinessDate().clone());
        }
    }                                   // end setCloneAttributes()

    //---------------------------------------------------------------------
    /**
        Determine if two objects are identical. <P>
        @param obj object to compare with
        @return true if the objects are identical, false otherwise
    **/
    //--------------------------------------------------------------------- 
    public boolean equals(Object obj)
    {                                   // begin equals()
        boolean isEqual = false;
        // confirm object is instance of ARTSCurrencyType    
        if (obj instanceof ARTSCurrencyType)
        {                               // begin compare objects
            // downcast the input object
            ARTSCurrencyType c = (ARTSCurrencyType) obj; 
            // compare all the attributes of ARTSCurrencyType
            if (Util.isObjectEqual(getCountryCode(), c.getCountryCode()) &&
                Util.isObjectEqual(getBusinessDate(), c.getBusinessDate()))
            {
                // set the return code to true
                isEqual = true;             
            }
            else
            {
                // set the return code to false
                isEqual = false;            
            }
        }                               // end compare objects

        return(isEqual);
    }                                   // end equals()
    
    //----------------------------------------------------------------------------
    /**
        Retrieves country code of issuing country. <P>
        @return country code of issuing country
    **/
    //----------------------------------------------------------------------------
    public String getCountryCode()
    {                                   // begin getCountryCode()
        return(countryCode);
    }                                   // end getCountryCode()

    //----------------------------------------------------------------------------
    /**
        Sets country code of issuing country. <P>
        @param value  country code of issuing country
    **/
    //----------------------------------------------------------------------------
    public void setCountryCode(String value)
    {                                   // begin setCountryCode()
        countryCode = value;
    }                                   // end setCountryCode()

    //----------------------------------------------------------------------------
    /**
        Retrieves business date for currency type. <P>
        @return business date for currency type
    **/
    //----------------------------------------------------------------------------
    public EYSDate getBusinessDate()
    {                                   // begin getBusinessDate()
        return(businessDate);
    }                                   // end getBusinessDate()

    //----------------------------------------------------------------------------
    /**
        Sets business date for currency type. <P>
        @param value  business date for currency type
    **/
    //----------------------------------------------------------------------------
    public void setBusinessDate(EYSDate value)
    {                                   // begin setBusinessDate()
        businessDate = value;
    }                                   // end setBusinessDate()

    //---------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // build result string
        StringBuffer strResult = 
          new StringBuffer("Class:  ARTSCurrencyType (Revision ");
        strResult.append(getRevisionNumber())
                 .append(") @").append(hashCode())
                 .append(Util.EOL)
        // add attributes to string
                 .append("countryCode:                        [")
                 .append(getCountryCode()).append("]").append(Util.EOL)
                 .append("businessDate:                       [")
                 .append(businessDate).append("]").append(Util.EOL);
        // pass back result
        return(strResult.toString());
    }                                   // end toString()

    //---------------------------------------------------------------------
    /**
        Retrieves the source-code-control system revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

    //---------------------------------------------------------------------
    /**
        ARTSCurrencyType main method. <P>
        @param String args[]  command-line parameters
    **/
    //---------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        ARTSCurrencyType c = new ARTSCurrencyType();
        // output toString()
        System.out.println(c.toString());
    }                                   // end main()
}                                       // end class ARTSCurrencyType
