/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/StoreStatusCargo.java /main/15 2014/07/09 13:10:48 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     06/26/14 - Forward port fix for handling the condition of two
 *                         registers opened with same till with one or both
 *                         offline at time of open.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:32 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:48:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:19:59  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:00:26   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 06 2003 00:20:58   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.domain.financial.StoreStatusIfc;

/**
 * This class represents the daily ops service cargo. <>P>
 * 
 * @version $Revision: /main/15 $
 */
public class StoreStatusCargo extends AbstractFinancialCargo implements StoreStatusCargoIfc
{
    private static final long serialVersionUID = -2842964215992459488L;

    /**
     * revision number supplied by PVCS
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * invalid workstation id
     * @since 14.1
     */
    protected String invalidWorkstationID = null;
    
    /**
     * store status list
     */
    protected StoreStatusIfc[] storeStatusList = null;

    /**
     * @return Returns the invalidWorkstationID.
     * 
     * @since 14.1
     */
    public String getInvalidWorkstationID()
    {
        return invalidWorkstationID;
    }

    /**
     * @param invalidWorkstationID The invalidWorkstationID to set.
     * 
     * @since 14.1
     */
    public void setInvalidWorkstationID(String invalidWorkstationID)
    {
        this.invalidWorkstationID = invalidWorkstationID;
    }    

    /**
     * Returns the list of store statuses.
     * 
     * @return The list of store statuses.
     */
    public StoreStatusIfc[] getStoreStatusList()
    {
        return storeStatusList;
    }

    /**
     * Sets the list of store statuses.
     * 
     * @param value The list of store statuses.
     */
    public void setStoreStatusList(StoreStatusIfc[] value)
    {
        storeStatusList = value;
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  StoreStatusCargo (Revision " + getRevisionNumber() + ") @" + hashCode());
        strResult += "\n";
        // add attributes to string
        strResult += abstractToString() + "\n";
        if (storeStatusList == null)
        {
            strResult += "Store status list:                      [null]\n";
        }
        else
        {
            strResult += "Store status list length:               [" + storeStatusList.length + "]\n";
            for (int i = 0; i < storeStatusList.length; i++)
            {
                strResult += "Store status " + (i + 1) + "\nSub" + storeStatusList[i].toString() + "\n";
            }
        }

        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}