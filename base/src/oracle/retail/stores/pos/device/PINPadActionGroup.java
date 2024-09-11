/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/PINPadActionGroup.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:38 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   05/11/10 - convert Base64 from axis
 *    cgreene   05/11/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   5    360Commerce 1.4         3/12/2008 12:32:53 PM  Deepti Sharma
 *        Decrypt the account number for the device
 *   4    360Commerce 1.3         12/14/2007 8:59:59 AM  Alan N. Sinton  CR
 *        29761: Removed non-PABP compliant methods and modified card RuleIfc
 *        to take an instance of EncipheredCardData.
 *   3    360Commerce 1.2         3/31/2005 4:29:21 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:24:05 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:13:03 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/09/23 00:07:13  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.3  2004/02/12 16:48:34  mcs
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 21:30:29  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:51:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Feb 07 2003 11:55:26   vxs
 * moved pinpad member variables from POSDeviceActionGroup to this class
 * Resolution for POS SCR-1901: Pos Device Action/ActionGroup refactoring
 * 
 *    Rev 1.0   Jan 08 2003 15:09:40   vxs
 * Initial revision.
 * Resolution for POS SCR-1901: Pos Device Action/ActionGroup refactoring
 *Revision: /main/7 $
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;
//java imports
import java.io.Serializable;

import jpos.JposException;
import jpos.PINPad;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;

import org.apache.commons.codec.binary.Base64;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.DeviceMode;
import oracle.retail.stores.foundation.manager.device.PINPadSession;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModeIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceSessionIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;

//--------------------------------------------------------------------------
/**
The <code>PINPadActionGroup</code> defines specific device operations
available to POS applications.
@version $Revision: /rgbustores_13.4x_generic_branch/1 $
@see oracle.retail.stores.pos.device.PosDeviceActionGroupIfc
**/
//--------------------------------------------------------------------------
public class PINPadActionGroup extends POSDeviceActionGroup implements PINPadActionGroupIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2310508855937485929L;

    /**
        pinpad System
    **/
    protected String pinpadSystemName = "";
    /**
        pinpad System
    **/
    protected int pinpadTransactionHost = 0;
    
    //---------------------------------------------------------------------
    /**
        Sets the name of the pinpad System
        @param value name of the pinpad System
    **/
    //---------------------------------------------------------------------
    public void setPinpadSystemName(String value)
    {
         pinpadSystemName = value;
    }

    //---------------------------------------------------------------------
    /**
        Gets the name of the pinpad System
        @return name of the pinpad System
    **/
    //---------------------------------------------------------------------
    public String getPinpadSystemName()
    {
        return pinpadSystemName;
    }

    //---------------------------------------------------------------------
    /**
        Sets the value of the transaction host
        @param value of the transaction host
    **/
    //---------------------------------------------------------------------
    public void setPinpadTransactionHost(int value)
    {
         pinpadTransactionHost = value;
    }

    //---------------------------------------------------------------------
    /**
        Gets the value of the transaction host
        @return value of the transaction host
    **/
    //---------------------------------------------------------------------
    public int getPinpadTransactionHost()
    {
        return pinpadTransactionHost;
    }
    
    //---------------------------------------------------------------------
    /**
       Get the PINPad from the DeviceSession <P>
       @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#getPINPad
    **/
    //---------------------------------------------------------------------
    public PINPad getPINPad() throws DeviceException
    {
        DeviceTechnicianIfc dt;
        DeviceSessionIfc pinpadSession = null;
        PINPad pinpad;
        try
        {
            dt = getDeviceTechnician();

            pinpadSession = dt.getDeviceSession(PINPadSession.TYPE);
            DeviceModeIfc dm = new DeviceMode();
            dm.setDeviceSessionName(PINPadSession.TYPE);
            dm.setDeviceModeName(PINPadSession.MODE_OPENED);
            pinpad = (PINPad) pinpadSession.getDeviceInMode(dm);
            
        }
        catch (DeviceException e)
        {
            throw e;
        }
        finally
        {
            if (pinpadSession != null)
            {
                pinpadSession.releaseDevice();
            }
        }
        return pinpad;
    }
    //---------------------------------------------------------------------
    /**
       Initiates an EFT transaction  <P>
       @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#beginEFTTransaction
    **/
    //---------------------------------------------------------------------
    public void beginEFTTransaction(String accountNumber,long amount,
                                    String merchantID, String terminalID,
                                    byte[] track2data,
                                    int transactionType, String pinpadSystem, int transactionHost)
        throws DeviceException
    {
    	try
        {
            PINPad pinpad = getPINPad();
            //Hypercom device requires the session be started first
            pinpad.setTransactionType(transactionType);

            //These parameters are now specified in devices.xml as properties
            //to device action group instead of hardcoded in POS app
            //(so, yes, parameters passed in are ignored)
            pinpad.beginEFTTransaction(getPinpadSystemName(), getPinpadTransactionHost());
            
            //set the pinpad attributes
            pinpad.setAccountNumber(new String(accountNumber));
            pinpad.setAmount(amount);
            pinpad.setMerchantID(merchantID);
            pinpad.setTerminalID(terminalID);
            pinpad.setTrack2Data(track2data);

        } catch (JposException e)
        {
            logger.error(
                         "PINPad Exception: Error while initializing transaction. " + e.getMessage() + "  Error Code:  " + Integer.toString(e.getErrorCode()) + "  Extended Error Code:  " + Integer.toString(e.getErrorCodeExtended()) + "");

            throw new DeviceException(DeviceException.JPOS_ERROR,"PINPad error", e);
        }
    }
    //---------------------------------------------------------------------
    /**
       Enables PINEntry on the PINPad.  <P>
       @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#enablePINEntry
    **/
    //---------------------------------------------------------------------
    public void enablePINEntry() throws DeviceException
    {
    	
    	byte[] cardNumber = null;
        try
        {
        	PINPad pinpad = getPINPad();
           	// decrypt the account number
        	KeyStoreEncryptionManagerIfc encryptionManager =
				(KeyStoreEncryptionManagerIfc)Gateway.getDispatcher().getManager(KeyStoreEncryptionManagerIfc.TYPE);
        	cardNumber = (encryptionManager.decrypt(Base64.decodeBase64(pinpad.getAccountNumber().getBytes())));
        	//set the account number
        	pinpad.setAccountNumber(new String(cardNumber));
            // enable pinpad device
        	pinpad.enablePINEntry();
        } 
        catch (JposException e)
        {
            e.printStackTrace();
            logger.error(
                         "PINPad Exception: Error while enabling PIN Entry. " + e.getMessage() + "  Error Code:  " + Integer.toString(e.getErrorCode()) + "  Extended Error Code:  " + Integer.toString(e.getErrorCodeExtended()) + "");
            throw new DeviceException(DeviceException.JPOS_ERROR,"PINPad error", e);
        }
        catch(EncryptionServiceException ese)
        {
            logger.error("Couldn't decrypt card number", ese);
        }
        finally
        {
        	if(cardNumber != null)
            {
            	// clear the original byte array.
                Util.flushByteArray(cardNumber);
                cardNumber = null;
            }
        }
    }

    //---------------------------------------------------------------------
    /**
       Finalizes an EFT transaction.  <P>
       @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#endEFTTransaction
    **/
    //---------------------------------------------------------------------
    public void endEFTTransaction(int completionCode) throws DeviceException
    {
        try
        {
            getPINPad() .endEFTTransaction(completionCode);
        } catch (JposException e)
        {
            logger.error(
                         "PINPad Exception: Error while finalizing transaction. " + e.getMessage() + "  Error Code:  " + Integer.toString(e.getErrorCode()) + "  Extended Error Code:  " + Integer.toString(e.getErrorCodeExtended()) + "");
            throw new DeviceException(DeviceException.JPOS_ERROR,"PINPad error", e);
        }
    }

    //---------------------------------------------------------------------
    /**
       Returns true if PINPad is simulated.  <P>
       @return Serializable Boolean true if PINPad is simulated, false otherwise
       @exception DeviceException is thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Serializable isPinPadSimulated() throws DeviceException
    {                                   // begin isPinPadSimulated()
        String stringValue;             // returned hard totals device name
        DeviceTechnicianIfc dt;
        PINPadSession pinPadSession;

        dt = getDeviceTechnician();
        pinPadSession =
            (PINPadSession) dt.getDeviceSession(PINPadSession.TYPE);
        Boolean returnValue = new Boolean(pinPadSession.isSessionSimulated());

        return returnValue;
    }                                   // end isPinPadSimulated()
}
