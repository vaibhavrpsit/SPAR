/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/DisplayStatusSite.java /main/16 2013/04/24 09:27:11 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  04/24/13 - Using reflection to get register object from cargo.
 *    blarsen   07/05/11 - Checking for null register when checking for auth
 *                         service status to avoid NPE.
 *    blarsen   06/30/11 - Updated to use 'new and real' payment manager is
 *                         online interface.
 *    rsnayak   05/12/11 - APF Device status changes
 *    blarsen   02/24/11 - Added support for fingerprint reader device
 *                         online/offline status.
 *    cgreene   07/02/10 - refactor building of model into buildStatusModel
 *                         method
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    abondala  02/20/09 - fixed scanner offline issue
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:21:06 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse
 *
 *Revision 1.3  2004/02/12 16:50:58  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:37  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Oct 22 2003 14:50:06   rsachdeva
 * Device and Database Status
 * Resolution for POS SCR-3411: Feature Enhancement:  Device and Database Status
 *
 *    Rev 1.1   Oct 21 2003 13:40:18   rsachdeva
 * Device and Database Status
 * Resolution for POS SCR-3411: Feature Enhancement:  Device and Database Status
 *
 *    Rev 1.0   Aug 29 2003 16:01:10   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Jul 29 2003 12:14:42   baa
 * fix online/offline status
 * Resolution for 2967: Device/Database status giving incorrect information
 *
 *    Rev 1.1   25 Jun 2003 23:35:10   baa
 * udpate to I18n dba status
 *
 *    Rev 1.0   Apr 29 2002 15:18:34   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:36:12   msg
 * Initial revision.
 *
 *    Rev 1.2   Jan 20 2002 11:17:04   mpm
 * Cleanup pass on PLAF UI integration.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.1   14 Nov 2001 16:33:40   pdd
 * Cleanup and changes to display only one line for DB status.
 * Resolution for POS SCR-291: Device/DB Status updates
 *
 *    Rev 1.0   Sep 21 2001 11:23:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.DataManagerOnlineStatus;
import oracle.retail.stores.pos.services.common.WriteHardTotalsCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DetailStatusBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusDisplayObject;

/**
 * This site displays the status of configured devices and database
 * 
 * @version $Revision: /main/16 $
 */
public class DisplayStatusSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 1766731738346382422L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * database tag
     */
    public static final String DATABASE_TAG = "Database";

    /**
     * scanner tag
     */
    public static final String SCANNER_TAG = "Scanner";

    /**
     * msr tag
     */
    public static final String MSR_TAG = "MSR";

    /**
     * micr tag
     */
    public static final String MICR_TAG = "MICR";

    /**
     * fingerprint reader tag
     */
    public static final String FINGERPRINT_READER_TAG = "FingerprintReader";
    
    /**
     * Authorization Service tag
     */
    public static final String AUTHORIZATION_SERIVCE = "AuthorizationService"; 

    /**
     * status display list parameter
     */
    public static final String STATUS_DISPLAY_LIST = "StatusDisplayList";

    /**
     * Display the status of configured devices and database
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Setup bean model information for the UI to display
        DetailStatusBeanModel beanModel = buildStatusModel(bus);

        // get the POSUIManager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DEVICE_STATUS, beanModel);
    }

    /**
     * Build the model for showing the screen.
     * 
     * @return
     */
    public DetailStatusBeanModel buildStatusModel(BusIfc bus)
    {
        // get the POSUIManager
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DetailStatusBeanModel beanModel = (DetailStatusBeanModel) ui.getModel(POSUIManagerIfc.DEVICE_STATUS);

        String devicesToShow[] = null;
        ArrayList<String> devices = new ArrayList<String>(12);
        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            devicesToShow = pm.getStringValues(STATUS_DISPLAY_LIST);
            devices.addAll(Arrays.asList(devicesToShow));
            beanModel.setDevicesToShow(devices);
        }
        catch (ParameterException pe)
        {
            logger.error("The DevicesToShowOnDeviceStatus could not be retrieved from the ParameterManager.", pe);
        }

        int status = StatusDisplayObject.STATUS_OFFLINE;
        if (devices.contains(DATABASE_TAG))
        {
            DataManagerIfc dataManager = (DataManagerIfc) bus.getManager(DataManagerIfc.TYPE);
            // This Status is as per the Last Database Transaction Done
            boolean databaseStatus = DataManagerOnlineStatus.getStatus(dataManager);
            if (databaseStatus)
            {
                status = StatusDisplayObject.STATUS_ONLINE;
            }
            beanModel.setDeviceStatus(DATABASE_TAG, status);
        }

        // Checking Status for these devices now
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);

        // MSR Check
        if (devices.contains(MSR_TAG))
        {
            status = StatusDisplayObject.STATUS_OFFLINE;
            boolean msrStatus = checkForMSRStatus(pda);
            if (msrStatus)
            {
                status = StatusDisplayObject.STATUS_ONLINE;
            }
            beanModel.setDeviceStatus(MSR_TAG, status);
        }

        // Scanner Check
        if (devices.contains(SCANNER_TAG))
        {
            status = StatusDisplayObject.STATUS_OFFLINE;
            boolean scannerStatus = checkForScannerStatus(pda);
            if (scannerStatus)
            {
                status = StatusDisplayObject.STATUS_ONLINE;
            }
            beanModel.setDeviceStatus(SCANNER_TAG, status);
        }

        // MICRCheck
        if (devices.contains(MICR_TAG))
        {
            status = StatusDisplayObject.STATUS_OFFLINE;
            boolean micrStatus = checkForMICRStatus(pda);
            if (micrStatus)
            {
                status = StatusDisplayObject.STATUS_ONLINE;
            }
            beanModel.setDeviceStatus(MICR_TAG, status);
        }

        // FingerprintReader Check
        if (devices.contains(FINGERPRINT_READER_TAG))
        {
            status = StatusDisplayObject.STATUS_OFFLINE;
            boolean fingerprintReaderStatus = checkForFingerprintReaderStatus(pda);
            if (fingerprintReaderStatus)
            {
                status = StatusDisplayObject.STATUS_ONLINE;
            }
            beanModel.setDeviceStatus(FINGERPRINT_READER_TAG, status);
        }
        
        //AUTHORIZATION SERIVCE Check
        if (devices.contains(AUTHORIZATION_SERIVCE))
        {
            status = StatusDisplayObject.STATUS_OFFLINE;
            boolean authorizationServiceOnline = isAuthorizationServiceOnline(bus);
            if (authorizationServiceOnline)
            {
                status = StatusDisplayObject.STATUS_ONLINE;
            }
            beanModel.setDeviceStatus(AUTHORIZATION_SERIVCE, status);
        }

        return beanModel; 
    }

    /**
     * Checks whether Authorization Service is online
     * @return true if service is online
     */

    private boolean isAuthorizationServiceOnline(BusIfc bus)
    {
        boolean isOnline = false;

        try
        {
            // Not all cargos implement WriteHardTotalsCargoIfc, use
            // reflection to get the register object from cargo.
            Method getRegisterMethod = bus.getCargo().getClass().getMethod("getRegister");
            RegisterIfc register = (RegisterIfc)getRegisterMethod.invoke(bus.getCargo(), null);
            PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);
            WorkstationIfc workstation = null;
            if (register != null)
            {
                workstation = register.getWorkstation();
            }
            if (workstation != null)
            {
                PaymentManagerIfc.Status status = paymentManager.getStatus(workstation);
                if (PaymentManagerIfc.Status.ONLINE.equals(status))
                {
                    isOnline = true;
                }
            }
            else
            {
                logger.warn("Workstation not available on cargo. Assuming authorization service is offline");
            }

        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e)
        {
            logger.warn("Workstation not available on cargo. Assuming authorization service is offline");
        }

        return isOnline;
    }

    /**
     * Checks whether MSR device is online.
     * 
     * @param pda POSDeviceActions reference
     * @return true if device is online, false otherwise.
     */
    private boolean checkForMSRStatus(POSDeviceActions pda)
    {
        boolean msrStatus = POSUIManagerIfc.OFFLINE;
        try
        {
            msrStatus = pda.isMSROnline();
        }
        catch (DeviceException de)
        {
            logger.error("MSR Status Lookup Error");
        }
        return msrStatus;
    }

    /**
     * Checks whether Scanner device is online.
     * 
     * @param pda POSDeviceActions reference
     * @return true if device is online, false otherwise.
     */
    private boolean checkForScannerStatus(POSDeviceActions pda)
    {
        boolean scannerStatus = POSUIManagerIfc.OFFLINE;
        try
        {
            scannerStatus = pda.isScannerOnline();
        }
        catch (DeviceException de)
        {
            logger.error("Scanner Status Lookup Error");
        }
        return scannerStatus;
    }

    /**
     * Checks whether MICR device is online.
     * 
     * @param pda POSDeviceActions reference
     * @return true if device is online, false otherwise.
     */
    private boolean checkForMICRStatus(POSDeviceActions pda)
    {
        boolean micrStatus = POSUIManagerIfc.OFFLINE;
        try
        {
            micrStatus = pda.isMICROnline();
        }
        catch (DeviceException de)
        {
            logger.error("MICR Status Lookup Error");
        }
        return micrStatus;
    }

    /**
     * Checks whether FingerprintReader device is online.
     * 
     * @param pda POSDeviceActions reference
     * @return true if device is online, false otherwise.
     */
    private boolean checkForFingerprintReaderStatus(POSDeviceActions pda)
    {
        boolean fingerprintStatus = POSUIManagerIfc.OFFLINE;
        try
        {
            fingerprintStatus = pda.isFingerprintReaderOnline();
        }
        catch (DeviceException de)
        {
            logger.error("FingerprintReader Status Lookup Error");
        }
        return fingerprintStatus;
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}