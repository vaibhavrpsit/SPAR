/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerNameAndIDBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Dec 12 2003 13:52:26   blj
 * Initial revision.
 * 
 *    Rev 1.1   Nov 03 2003 11:47:22   epd
 * Updated for internationalization
 * 
 *    Rev 1.0   Oct 31 2003 16:52:00   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;


//----------------------------------------------------------------------------
/**
    Data transport between the bean and the application for credit card data
**/
//----------------------------------------------------------------------------
public class CustomerNameAndIDBeanModel extends CustomerIDBeanModel
{
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected String fieldFirstName = "";
    protected String fieldLastName = "";
    

    //---------------------------------------------------------------------
    /**
     * Gets the firstName property (java.lang.String) value.
     * @return The firstName property value.
     * @see #setFirstName
     */
    //---------------------------------------------------------------------
    public String getFirstName()
    {
        return fieldFirstName;
    }

   //---------------------------------------------------------------------
    /**
     * Sets the firstName property (java.lang.String) value.
     * @param firstName The new value for the property.
     * @see #getFirstName
     */
    //---------------------------------------------------------------------
    public void setFirstName(String firstName)
    {
        fieldFirstName = firstName;
    }
    
    //---------------------------------------------------------------------
    /**
     * Gets the lastName property (java.lang.String) value.
     * @return The lastName property value.
     * @see #setLastName
     */
    //---------------------------------------------------------------------
    public String getLastName()
    {
        return fieldLastName;
    }
    //---------------------------------------------------------------------
    /**
     * Sets the lastName property (java.lang.String) value.
     * @param lastName The new value for the property.
     * @see #getLastName
     */
    //---------------------------------------------------------------------
    public void setLastName(String lastName)
    {
        fieldLastName = lastName;
    }
}
