/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/DataManagerOnlineStatus.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:52 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:27 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 03 2003 11:36:46   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jul 15 2002 10:16:22   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:36:04   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:08:54   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:22:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:14:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:06:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

//java imports
import java.lang.reflect.Field;
import java.util.Hashtable;

import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;

//----------------------------------------------------------------------------
/**
     This class is a temporary stop gap; its purpose is to assemble the
     Online/Offline status of the DataManager.  It will be replaced by
     the Status Manager.
     <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class DataManagerOnlineStatus
{
    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        offline tag and default text
    **/
    public static final String OFFLINE = "Offline";
    /**
        online tag and default text
    **/
    public static final String ONLINE = "Online";
        
    //----------------------------------------------------------------------
    /**
        Uses the given data manager to determine the status of the 
        transactions, the online/offline status.
        <P>
        @param  dataManager a reference to the current data manager
        @return Hashtable hashtable of the transactions and their status's
    **/
    //----------------------------------------------------------------------
    public static Hashtable getTransactionStatus(DataManagerIfc dataManager)
    {
        UtilityManagerIfc utility = 
          (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        String offLine = utility.retrieveCommonText(OFFLINE,
                                                    OFFLINE); 
        String onLine = utility.retrieveCommonText(ONLINE,
                                                   ONLINE);
        // initial return value
        Hashtable transactionStatus = null;

        // get the array of transaction names and check each one to
        // see if any are offline.
        try
        {
            String names[] = dataManager.getTransactionNames();
            if (names != null)
            {
                transactionStatus = new Hashtable();
                for(int i = 0; i < names.length; i++)
                {
                    if (!(dataManager.getTransactionOnline(names[i])))
                    {
                        transactionStatus.put(names[i], offLine);
                    }
                    else
                    {
                        transactionStatus.put(names[i], onLine);
                   }
                }
            }
            else
            {
                transactionStatus = new Hashtable();
                boolean online = dataManager.getOnlineState();
                if (online)
                {
                    transactionStatus.put("DATABASE", onLine);
                }
                else
                {
                    transactionStatus.put("DATABASE", offLine);
                }
            }
        }
        catch (DataException e)
        {
            // if there are no transactions to test, return true
        }
        return transactionStatus;
    }
    
    //----------------------------------------------------------------------
    /**
        Uses the given data manager to determine the 
        online/offline status of the technician.
        <P>
        @param dataManager a reference to the current data manager
        @return boolean true if all the transactions are online.
    **/
    //----------------------------------------------------------------------
    public static boolean getStatus(DataManagerIfc dataManager)
    {
        return dataManager.getOnlineState();
    }

        //---------------------------------------------------------------------
        /**
                Method to default display string function. <P>
            @return String representation of object
        **/
        //---------------------------------------------------------------------
        public String toString()
        {                                   // begin toString()

                // verbose flag
                boolean blnVerbose = false;
                // result string
                String strResult = new String("Class:  DataManagerOnlineStatus (Revision " +
                                                          getRevisionNumber() +
                                                          ")" +
                                                          hashCode());
                // if verbose mode, do inspection gig
                if (blnVerbose)
                {                                                               // begin verbose mode

                    // theClass will ascend through the inheritance hierarchy
                    Class theClass = getClass();
                    // fieldType contains the type of the field currently being examined
                    Class fieldType = null;
                    // fieldName contains the name of the field currently being examined
                    String fieldName = "";
                    // fieldValue contains the value of the field currently being examined
                    Object fieldValue = null;

                    // Ascend through the class hierarchy, capturing field information
                    while (theClass != null)
                    {                                                   // begin loop through fields
                        // fields contains all noninherited field information
                        Field[] fields = theClass.getDeclaredFields();

                        // Go through each field, capturing information
                        for (int i = 0; i < fields.length; i++)
                        {
                            fieldType = fields[i].getType();
                            fieldName = fields[i].getName();

                            // get the field's value, if possible
                            try
                            {
                                fieldValue = fields[i].get(this);
                            }
                            // if the value can't be gotten, say so
                            catch (IllegalAccessException ex)
                            {
                                fieldValue = "*no access*";
                            }
                    // If it is a "simple" field, use the value
                    if (Util.isSimpleClass(fieldType))
                    {
                        strResult += "\n\t" + 
                                     fieldName + 
                                     ":\t" + 
                                     fieldValue;
                    }       // if simple
                    // If it is a null value, say so
                    else if (fieldValue == null)
                    {
                        strResult += "\n\t" + 
                                     fieldName + 
                                     ":\t(null)";
                    }
                    // Otherwise, use <type<hashCode>
                    else
                    {
                        strResult += "\n\t" + 
                                     fieldName + 
                                     ":\t" +
                                     fieldType.getName() + 
                                     "@" + 
                                     fieldValue.hashCode();
                    }
                        }   // for each field
                        theClass = theClass.getSuperclass();
                        }                                                       // end loop through fields
                }                                                               // end verbose mode
                // pass back result
                return(strResult);
        }                                  // end toString()

    //---------------------------------------------------------------------
    /**
        Retrieves the Team Connection revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                  // end getRevisionNumber()
}
