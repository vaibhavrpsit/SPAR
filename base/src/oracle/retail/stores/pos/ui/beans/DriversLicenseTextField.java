/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DriversLicenseTextField.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
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
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         10/10/2007 1:02:00 PM  Anda D. Cadar   Changes
 *      to not allow double byte chars
 * 3    360Commerce 1.2         3/31/2005 4:27:51 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:21:11 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:10:43 PM  Robert Pearse   
 *
 *Revision 1.4  2004/03/16 17:15:22  build
 *Forcing head revision
 *
 *Revision 1.3  2004/03/16 17:15:17  build
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 20:56:27  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:10:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 24 2003 14:42:54   bwf
 * Initial revision.
 * Resolution for 2208: Space and Asterisk chars are not allowed in a driver's license ID number
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.text.Document;

import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------
/**
 * This document allows input to be valid if it meets max and min length
 * requirements and is alpha numeric or contains '*' or ' '.  The alpha characters 
 * will be converted to uppercase.
 */
//------------------------------------------------------------------------------
public class DriversLicenseTextField extends ConstrainedTextField
{
    /** Revision number */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
//  ---------------------------------------------------------------------
      /**
         Constructor.
      */
      //---------------------------------------------------------------------
      public DriversLicenseTextField()
      {
          this("");
      }

      //---------------------------------------------------------------------
      /**
         Constructor.
         @param value the default text for the field
      */
      //---------------------------------------------------------------------
      public DriversLicenseTextField(String value)
      {
          this(value, 0, Integer.MAX_VALUE);
      }

      //---------------------------------------------------------------------
      /**
         Constructor.
         @param value the default text for the field
         @param minLength the minimum length for a valid field
         @param maxLength the maximum length for a valid field
      */
      //---------------------------------------------------------------------
      public DriversLicenseTextField(String value, int minLength, int maxLength)
      {
          super(value, minLength, maxLength);
      }
      
    //---------------------------------------------------------------------
      /**
         Constructor.
         @param value the default text for the field
         @param minLength the minimum length for a valid field
         @param maxLength the maximum length for a valid field
      */
      //---------------------------------------------------------------------
      public DriversLicenseTextField(String value, int minLength, int maxLength, boolean doubleByteCharsAllowed)
      {
          super(value, minLength, maxLength, doubleByteCharsAllowed);
      }

      //---------------------------------------------------------------------
      /**
         Gets the default model for the Constrained field
         @return the model for length constrained fields
      */
      //---------------------------------------------------------------------
      protected Document createDefaultModel()
      {
          return new DriversLicenseDocument(Integer.MAX_VALUE);
      }

      //---------------------------------------------------------------------
      /**
         Returns default display string. <P>
         @return String representation of object
      */
      //---------------------------------------------------------------------
      public String toString()
      {
          String strResult = new String("Class: DriversLicenseTextField (Revision " +
                                        getRevisionNumber() + ") @" +
                                        hashCode());
          return(strResult);
      }

      //---------------------------------------------------------------------
      /**
         Retrieves the Team Connection revision number. <P>
         @return String representation of revision number
      */
      //---------------------------------------------------------------------
      public String getRevisionNumber()
      {
          return(Util.parseRevisionNumber(revisionNumber));
      }

}
