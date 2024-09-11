/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/buildfftour/BuildFFTourCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:17 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:40 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:06  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:41  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:36:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:08:06   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:21:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:13:04   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.buildfftour;


// foundation imports
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;

//--------------------------------------------------------------------------
/**
    This is the cargo object for the build flat file service.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class BuildFFTourCargo implements CargoIfc
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class:  BuildFFTourCargo (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
