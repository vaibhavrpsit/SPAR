/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/browserfoundation/BrowserFoundationAppSite.java /main/13 2012/12/13 10:24:19 vbongu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED (MM/DD/YY)
*    vbongu 12/13/12 - change jfx log error
*    cgreen 10/30/12 - Logo click browser cleanup
*    cgreen 10/30/12 - BrowserFoundation cleanup
*    rabhaw 10/05/12 - logging error if browser env. not configured
*    cgreen 09/10/12 - Popup menu implementation
*    rabhaw 08/30/12 - using javafx for embedded browser
*    cgreen 05/26/10 - convert to oracle packaging
*    abonda 01/03/10 - update header date
*    nkgaut 11/14/08 - Changes for removing JDIC Components from POS Installed
*                      Directories
*    nkgaut 10/01/08 - A new site for browser foundation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.browserfoundation;

import javafx.embed.swing.JFXPanel;
import oracle.retail.stores.commerceservices.audit.AuditLoggerConstants;
import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.UserEvent;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

@SuppressWarnings("serial")
public class BrowserFoundationAppSite extends PosSiteActionAdapter implements SiteActionIfc
{
    /**
     * When null, this site will check if JavaFX is installed.
     */
    protected static Boolean javaFXInstalled;

    /**
     * The BrowserFoundationAppSite site allows the user to access a specific
     * configured URL
     * 
     * @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {
        BrowserFoundationDisplayBeanModel model = new BrowserFoundationDisplayBeanModel();

        // get the home site parameter value
        ParameterManagerIfc paramManager = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        try
        {
            String homeURL = paramManager.getStringValue(ParameterConstantsIfc.BASE_BrowserURL);
            if (!homeURL.startsWith("http"))
            {
                logger.error("Invalid Home URL. Only HTTP or HTTPS protocol is supported.");
                showErrorScreen(bus, "BrowserFoundationURLError");
                return;
            }
            
            model.setHomeURL(homeURL);

        }
        catch (ParameterException e1)
        {
            logger.error("Exception while getting BrowserUrl from application.xml", e1);
            showErrorScreen(bus, "BrowserFoundationError");
            return;
        }

        // checking if javafx runtime is configured or not
        if (isJavaFXInstalled())
        {
            AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
            UserEvent ev = (UserEvent)AuditLoggingUtils.createLogEvent(UserEvent.class,
                    AuditLogEventEnum.LAUNCH_BROWSER);

            BrowserFoundationCargo cargo = (BrowserFoundationCargo)bus.getCargo();
            ev.setStoreId(cargo.getOperator().getStoreID());
            ev.setUserId(cargo.getOperator().getLoginID());
            ev.setStatus(AuditLoggerConstants.SUCCESS);
            ev.setEventOriginator("BrowserFoundationAppSite.arrive");

            RegisterIfc ri = cargo.getRegister();
            if (ri != null)
            {
                WorkstationIfc wi = ri.getWorkstation();
                if (wi != null)
                {
                    ev.setRegisterNumber(wi.getWorkstationID());
                }
            }
            auditService.logStatusSuccess(ev);

            // if homeurl not set or proper, display a error screen
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.BROWSER_FOUNDATION, model);
        }
        else
        {
            showErrorScreen(bus, "BrowserFoundationConfigError");
        }
    }

    /**
     * This method logs an event departing from this site
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void depart(BusIfc bus)
    {
        try
        {
            AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
            UserEvent ev = (UserEvent)AuditLoggingUtils.createLogEvent(UserEvent.class, AuditLogEventEnum.LOG_OUT);

            BrowserFoundationCargo cargo = (BrowserFoundationCargo)bus.getCargo();
            LetterIfc letter = bus.getCurrentLetter();
            if (letter instanceof ButtonPressedLetter) // Is ButtonPressedLetter
            {
                String letterName = letter.getName();
                if (letterName != null && letterName.equals(CommonLetterIfc.UNDO))
                {
                    RegisterIfc ri = cargo.getRegister();
                    if (ri != null)
                    {
                        WorkstationIfc wi = ri.getWorkstation();
                        if (wi != null)
                        {
                            ev.setRegisterNumber(wi.getWorkstationID());
                        }
                    }
                    ev.setStoreId(cargo.getOperator().getStoreID());
                    ev.setUserId(cargo.getOperator().getLoginID());
                    ev.setStatus(AuditLoggerConstants.SUCCESS);
                    ev.setEventOriginator("BrowserFoundationAppSite.depart");
                    auditService.logStatusSuccess(ev);
                }
            }
        }
        catch (Exception e)
        {
            logger.warn("Error while logging event ", e);
        }
    }

    /**
     * Returns true if a {@link JFXPanel} can be created.
     *
     * @return
     * @see BrowserFoundationAppSite#javaFXInstalled
     */
    public static boolean isJavaFXInstalled()
    {
        if (javaFXInstalled == null)
        {
            try
            {
                new JFXPanel();
                javaFXInstalled = Boolean.TRUE;
            }
            catch (Throwable th)
            {
                javaFXInstalled = Boolean.FALSE;
                logger.warn("JavaFX runtime is not configured.");
            }
        }
        return (javaFXInstalled != null)? javaFXInstalled : false;
    }

    /**
     * 
     */
    protected void showErrorScreen(BusIfc bus, String resourceId)
    {
        DialogBeanModel dialogBean = new DialogBeanModel();
        dialogBean.setResourceID(resourceId);
        dialogBean.setType(DialogScreensIfc.ERROR);
        dialogBean.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Undo");
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogBean);
    }
}
