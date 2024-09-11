/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerIDBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/12/10 - Fixed defect in which data from a UI model was
 *                         corrupting subsequent tender objects.
 *    dwfung    09/20/10 - fixed idTypes type
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:36 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:22 PM  Robert Pearse   
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
 *    Rev 1.2   29 Jan 2004 15:37:20   Tim Fritz
 * Changed selectedIDType = -1 to selectedIDType = 0
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

// java imports
import java.util.Vector;


//----------------------------------------------------------------------------
/**
    Data transport between the bean and the application for credit card data
**/
//----------------------------------------------------------------------------
public class CustomerIDBeanModel extends CountryModel
{
    private static final long serialVersionUID = -7185628470295235828L;

    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        The ID expiration date
     **/
    protected String fieldExpirationDate = "";
    
    /**
        the list of ID types
    **/
    private Vector<String> idTypes = null;
    /**
        the selected ID type
    **/
    private int selectedIDType = 0;
    
    //---------------------------------------------------------------------------
    /**
        Get the value of the ExpirationDate field
        @return the value of ExpirationDate
    **/
    //---------------------------------------------------------------------------
    public String getExpirationDate()
    {
        return fieldExpirationDate;
    }

    //----------------------------------------------------------------------------
    /**
        Get the value of the IDTypes field
        @return the value of IDTypes
    **/
    //----------------------------------------------------------------------------
    public Vector<String> getIDTypes()
    {
        return idTypes;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the IDTypes field
        @param the value to be set for IDTypes
    **/
    //----------------------------------------------------------------------------
    public void setIDTypes(Vector<String> value)
    {
        idTypes = value;
    }
    
    //----------------------------------------------------------------------------
    /**
        Get the value of the SelectedIDType field
        @return the value of SelectedIDType
    **/
    //----------------------------------------------------------------------------
    public int getSelectedIDType()
    {
        return selectedIDType;
    }

    //----------------------------------------------------------------------------
    /**
        Sets the SelectedIDType field
        @param the value to be set for SelectedIDType
    **/
    //----------------------------------------------------------------------------
    public void setSelectedIDType(int selected)
    {
        selectedIDType = selected;
    }
    
    //---------------------------------------------------------------------------
    /**
        Sets the ExpirationDate field
        @param date value to be set for ExpirationDate
    **/
    //---------------------------------------------------------------------------
    public void setExpirationDate(String value)
    {
        fieldExpirationDate = value;
    }

    //---------------------------------------------------------------------------
    /**
        Converts to a string representing the data in this Object
        @returns string representing the data in this Object
    **/
    //---------------------------------------------------------------------------
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        buff.append("Class: GapCustomerIDBeanModel Revision: "
                    + revisionNumber
                    + "\n");
        buff.append("idTypes        [" + idTypes + "]\n");
        buff.append("fieldExpirationDate [" + fieldExpirationDate + "]\n");

        return(buff.toString());
    }

    /**
     * Resets the model to original values.
     */
    public void reset()
    {
        super.reset();
        fieldExpirationDate = "";
        idTypes = null;
        selectedIDType = 0;
    }
}
