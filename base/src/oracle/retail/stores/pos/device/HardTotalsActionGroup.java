/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/HardTotalsActionGroup.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:39 mszekely Exp $
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
 *   3    360Commerce 1.2         3/31/2005 4:28:19 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:22:00 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:17 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:51:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Feb 16 2003 10:43:22   mpm
 * Merged 5.1 changes.
 * Resolution for POS SCR-2053: Merge 5.1 changes into 6.0
 *
 *    Rev 1.0   Jan 15 2003 16:32:40   vxs
 * Initial revision.
 * Resolution for POS SCR-1901: Pos Device Action/ActionGroup refactoring
 *Revision: /main/7 $
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;
//java imports
import java.io.Serializable;

import jpos.HardTotals;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.DeviceMode;
import oracle.retail.stores.foundation.manager.device.HardTotalsSession;
import oracle.retail.stores.foundation.manager.device.POSHardTotals;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModeIfc;
import oracle.retail.stores.foundation.manager.ifc.device.HardTotalsIfc;
import oracle.retail.stores.foundation.utility.Util;

//---------------------------------------------------------------------
/**
    The <code>HardTotalsActionGroup</code> defines the HardTotals specific
    device operations available to POS applications.
**/
//---------------------------------------------------------------------
public class HardTotalsActionGroup
extends POSDeviceActionGroup
implements HardTotalsActionGroupIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 9149971192991602167L;

    /**
       posHardTotals is a wrapper around the JPOS Hard Totals implementation
    **/
    protected HardTotalsIfc posHardTotals = null;

    //---------------------------------------------------------------------
    /**
       Writes Hard Totals <P>
       @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#writeHardTotals
    **/
    //---------------------------------------------------------------------
    public void writeHardTotals(Serializable htObj) throws DeviceException
    {

        // obtain HardTotalsSession from DeviceTechnician
        // obtain jpos.HardTotals in busy mode
        // create simplified hard totals wrapper
        // write hard totals
        // release jpos.HardTotals

        DeviceTechnicianIfc dt;
        HardTotalsSession hardTotalsSession = null;

        try
        {
            dt = getDeviceTechnician();

            hardTotalsSession = (HardTotalsSession)
                dt.getDeviceSession(HardTotalsSession.TYPE);
            DeviceModeIfc dm = new DeviceMode();
            dm.setDeviceSessionName(HardTotalsSession.TYPE);
            dm.setDeviceModeName(HardTotalsSession.MODE_BUSY);
            HardTotals hardTotals =
                (HardTotals) hardTotalsSession.getDeviceInMode(dm);

            // Added to support Non Persistent (i.e. non existent) hard totals
            HardTotalsIfc hardTotalsWrapper = getPosHardTotals();
            if (hardTotalsWrapper instanceof POSHardTotals)
            {
                POSHardTotals posHardTotals = (POSHardTotals)hardTotalsWrapper;
                posHardTotals.setHardTotals(hardTotals);

                // get name to use as a file name if this is a simulation
                String fileNameBase = hardTotalsSession.getDeviceName();
                posHardTotals.setFileNameBase(fileNameBase);
            }

            hardTotalsWrapper.write(htObj);
        }
        catch (DeviceException e)
        {
            throw e;
        }
        finally
        {
            if (hardTotalsSession != null)
            {
                hardTotalsSession.releaseDevice();
                DeviceModeIfc dm = new DeviceMode();
                dm.setDeviceSessionName(HardTotalsSession.TYPE);
                dm.setDeviceModeName(HardTotalsSession.MODE_CLOSED);
                hardTotalsSession.setDeviceMode(dm);
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
       This method writes the hard totals to a file based on a name supplied
       as an argument.
       @param hardTotals the SimulatedHardTotals object to be written
       @param nameBase the base of the file name to be written, e.g.,
       "211.defaultHardTotals"
       @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#writeHardTotals
    **/
    //--------------------------------------------------------------------------

    public void writeHardTotals(Serializable hardTotals, String nameBase)
        throws DeviceException
    {
        // Added to support Non Persistent (i.e. non existent) hard totals
        HardTotalsIfc hardTotalsWrapper = getPosHardTotals();
        if (hardTotalsWrapper instanceof POSHardTotals)
        {
            POSHardTotals posHardTotals = (POSHardTotals)hardTotalsWrapper;
            posHardTotals.write(hardTotals, nameBase);
        }
        else
        {
            hardTotalsWrapper.write(hardTotals);
        }
    }


    //---------------------------------------------------------------------
    /**
       Reads Hard Totals <P>
       @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#readHardTotals
    **/
    //---------------------------------------------------------------------
    public Serializable readHardTotals() throws DeviceException
    {

        // obtain HardTotalsSession from DeviceTechnician
        // obtain jpos.HardTotals in busy mode
        // create simplified hard totals wrapper
        // read hard totals
        // release jpos.HardTotals

        DeviceTechnicianIfc dt;
        HardTotalsSession hardTotalsSession = null;

        Serializable htObj = null;

        try
        {
            dt = getDeviceTechnician();

            hardTotalsSession = (HardTotalsSession)
                dt.getDeviceSession(HardTotalsSession.TYPE);
            DeviceModeIfc dm = new DeviceMode();
            dm.setDeviceSessionName(HardTotalsSession.TYPE);
            dm.setDeviceModeName(HardTotalsSession.MODE_BUSY);
            HardTotals hardTotals =
                (HardTotals) hardTotalsSession.getDeviceInMode(dm);

            // Added to support Non Persistent (i.e. non existent) hard totals
            HardTotalsIfc hardTotalsWrapper = getPosHardTotals();
            if (hardTotalsWrapper instanceof POSHardTotals)
            {
                // get name to use as a file name if this is a simulation
                POSHardTotals posHardTotals = (POSHardTotals)hardTotalsWrapper;
                posHardTotals.setHardTotals(hardTotals);
                String fileNameBase = hardTotalsSession.getDeviceName();
                posHardTotals.setFileNameBase(fileNameBase);
            }
            htObj = (Serializable) hardTotalsWrapper.read();
        }
        catch (DeviceException e)
        {
            throw e;
        }
        finally
        {
            if (hardTotalsSession != null)
            {
                hardTotalsSession.releaseDevice();
                DeviceModeIfc dm = new DeviceMode();
                dm.setDeviceSessionName(HardTotalsSession.TYPE);
                dm.setDeviceModeName(HardTotalsSession.MODE_CLOSED);
                hardTotalsSession.setDeviceMode(dm);
            }
        }
        return(htObj);

    }

    //--------------------------------------------------------------------------
    /**
       This method reads the hard totals from a file based on a name supplied
       as an argument.
       @param nameBase the base of the file name to be written, e.g.,
       "211.defaultHardTotals"
       @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#readHardTotals
    **/
    //--------------------------------------------------------------------------

    public Serializable readHardTotals(String nameBase) throws DeviceException
    {
        POSHardTotals posHardTotals = new POSHardTotals();
        Serializable htObj = (Serializable)posHardTotals.read(nameBase);
        return(htObj);
    }


    //---------------------------------------------------------------------
    /**
       Gets hard-totals device name.  This should only be used when
       operating with a file-based device. <P>
       @param none
       @return Serializable hard-totals device name
       @exception DeviceException is thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public Serializable getHardTotalsDeviceName() throws DeviceException
    {                                   // begin getHardTotalsDeviceName()
        String stringValue;             // returned hard totals device name
        DeviceTechnicianIfc dt;
        HardTotalsSession hardTotalsSession;


        dt = getDeviceTechnician();
        hardTotalsSession =
            (HardTotalsSession)dt.getDeviceSession(HardTotalsSession.TYPE);
        stringValue = hardTotalsSession.getDeviceName();


        return stringValue;
    }                                   // end getHardTotalsDeviceName()

    //---------------------------------------------------------------------
    /**
       Sets hard-totals device name.  This should only be used when
       operating with a file-based device. If null or empty value
       is passed, it is ignored. <P>
       @param value new device name
       @exception DeviceException is thrown if error occurs
    **/
    //---------------------------------------------------------------------
    public void setHardTotalsDeviceName(Serializable value) throws DeviceException
    {                                   // begin setHardTotalsDeviceName()
        String stringValue = (String) value;
        if (!(Util.isEmpty(stringValue)))
        {
            DeviceTechnicianIfc dt;
            HardTotalsSession hardTotalsSession = null;
            dt = getDeviceTechnician();

            hardTotalsSession = (HardTotalsSession)
                dt.getDeviceSession(HardTotalsSession.TYPE);
            hardTotalsSession.setDeviceName(stringValue);
        }
    }                                   // end setHardTotalsDeviceName()

    //---------------------------------------------------------------------
    /**
       Sets Pos Hard Totals reference.  This was dded to support Non
       Persistent (i.e. non existent) hard totals.
       @param className name of the Pos Hard totals class
    **/
    //---------------------------------------------------------------------
    public void setPosHardTotals(String className)
    {
        try
        {
            Class phtClass = Class.forName(className);
            posHardTotals = (HardTotalsIfc)phtClass.newInstance();
        }
        catch (ClassNotFoundException ecnf)
        {
            logger.warn( "" + "HardTotalsActionGroup.setPosHardTotals(): Class " + "" + className + "" + " not found." + "");
        }
        catch (InstantiationException einst)
        {
            logger.warn( "" + "HardTotalsActionGroup.setPosHardTotals(): Could not instantiate class " + "" + className + "" + "." + "");
        }
        catch (IllegalAccessException eill)
        {
            logger.warn( "" + "HardTotalsActionGroup.setPosHardTotals(): Could not instantiate class " + "" + className + "" + " due to illegal access." + "");
        }
    }

    //---------------------------------------------------------------------
    /**
       Gets Pos Hard Totals reference.  This was dded to support Non
       Persistent (i.e. non existent) hard totals.
       @return the posHardTotals reference.
       @exception DeviceException thrown if Pos Hard Totals is null
    **/
    //---------------------------------------------------------------------
    public HardTotalsIfc getPosHardTotals() throws DeviceException
    {
        if (posHardTotals == null)
        {
            posHardTotals = new POSHardTotals();
        }
        return(posHardTotals);

    }

}
