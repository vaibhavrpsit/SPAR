/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/EmployeeEnteredRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:02 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   05/11/10 - convert Base64 from axis
 *    cgreene   05/11/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/18/2008 3:03:27 PM   Alan N. Sinton  CR
 *         31133: Undid fix for 30247 and fixed the code for employee card
 *         swipe.  Code was reviewed by Tony Zgarba.
 *    5    360Commerce 1.4         4/16/2008 3:16:03 AM   Anil Kandru     The
 *         unencrypted account number has been taken when the employee swipe a
 *          card.
 *    4    360Commerce 1.3         4/15/2008 6:00:32 AM   Anil Kandru     The
 *         encryption has been removed for the employee ID when a card is
 *         swiped for login.
 *    3    360Commerce 1.2         3/31/2005 4:27:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:18 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:49 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/07/20 14:05:40  kll
 *   @scr 4046: changed ManualEntryID and AutomaticEntryID parameters
 *
 *   Revision 1.2  2004/02/12 16:51:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Feb 03 2004 17:03:22   bwf
 * Use new utlity method, getEmployeeFromModel.
 * 
 *    Rev 1.0   Aug 29 2003 16:03:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Jul 01 2003 17:10:36   bwf
 * Make sure surname is not null or empty before charAt().
 * Resolution for 2895: Crash- Operator ID= Auto- Login ID- Password = Yes or No- Swipe card with no Access
 * 
 *    Rev 1.2   Jun 20 2003 12:58:16   bwf
 * Remove space if one exists in login id.
 * Resolution for 2605: Operator ID - Automatic Entry Not Working
 * 
 *    Rev 1.1   May 08 2003 11:31:28   bwf
 * Get data from MSRModel when swiped.
 * Resolution for 1933: Employee Login enhancements
 * 
 *    Rev 1.0   Apr 29 2002 15:13:32   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:32:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:16   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;

// foundation imports
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;

import org.apache.commons.codec.binary.Base64;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
    This road is traveled when the employee ID has been entered.
    It stores the employee ID in the cargo.
    <p>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class EmployeeEnteredRoad extends LaneActionAdapter
{
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -1182425878243627175L;
    /**
       revision number supplied by Team Connection
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Stores the employee ID in the cargo.
       <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        /*
         * Get the input value from the UI Manager
         */
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel(POSUIManagerIfc.OPERATOR_IDENTIFICATION);
        PromptAndResponseModel pAndRModel = model.getPromptAndResponseModel();
        
        OperatorIdCargo cargo = (OperatorIdCargo)bus.getCargo();
        
        // the following is here only so that testing can be done on 
        // swiping of credit cards as employee login cards.  this 
        // can be removed once we have real employee login cards.
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        try
        {
        
            if(pAndRModel.isSwiped()) 
            {
                if(pm.getStringValue("AutomaticEntryID").equals("User"))
                {
                    String loginID = null;
                    UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);  
                    loginID = util.getEmployeeFromModel(pAndRModel);
                    cargo.setEmployeeID(loginID);
                }
                else
                {
                    MSRModel msrModel = pAndRModel.getMSRModel();
                    String employeeID = null;
                    try
                    {
                        KeyStoreEncryptionManagerIfc encryptionManager =
                            (KeyStoreEncryptionManagerIfc)Gateway.getDispatcher().getManager(KeyStoreEncryptionManagerIfc.TYPE);
                        byte[] eID = encryptionManager.decrypt(Base64.decodeBase64(msrModel.getEncipheredCardData().getEncryptedAcctNumber().getBytes()));
                        // What if operator mistakenly swipes a credit card on this screen.  We can only
                        // assume that what was swiped is an employee card. Since employee ID are 10 chars
                        // or less we can chop off and throw away the excess.
                        if(eID.length > 10)
                        {
                            // not using System.arraycopy() since this is potentially sensitive data.
                            byte[] tmpID = new byte[10];
                            for(int i = 0; i < tmpID.length; i++)
                            {
                                tmpID[i] = eID[i];
                            }
                            employeeID = new String(tmpID);
                            // clear eID
                            Util.flushByteArray(eID);
                            eID = null;
                            // clear tmpID
                            Util.flushByteArray(tmpID);
                            tmpID = null;
                        }
                        else
                        {
                            employeeID = new String(eID);
                        }
                        cargo.setEmployeeID(employeeID);
                    }
                    catch(EncryptionServiceException ese)
                    {
                        logger.error("Could not decrypt employee ID", ese);
                    }
                }
            }
            else
            {
                // the following line will probably be only line needed when using real employee login cards
                cargo.setEmployeeID(ui.getInput());
            }
            
        }
        catch (ParameterException pe)
        {
            System.out.println("*** error getting parameter AutomaticEntryID");
        }
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  EmployeeEnteredRoad (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

    //----------------------------------------------------------------------
    /**
       Main to run a test..
       <P>
       @param  args    Command line parameters
    **/
    //----------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        EmployeeEnteredRoad obj = new EmployeeEnteredRoad();

        // output toString()
        System.out.println(obj.toString());
    }                                   // end main()
}
