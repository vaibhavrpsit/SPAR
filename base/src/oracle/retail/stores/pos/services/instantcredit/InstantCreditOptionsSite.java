/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/InstantCreditOptionsSite.java /main/12 2011/02/16 09:13:24 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    npoola    12/15/09 - disabled the button enrollment for the transaction
 *                         re-entry mode
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:22:08 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:11:25 PM  Robert Pearse   
 *
 *Revision 1.7  2004/07/14 18:47:09  epd
 *@scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 *Revision 1.6  2004/07/01 18:56:49  lwalters
 *@scr 5714
 *
 *Initialize the status bar customer name when arriving to this site
 *
 *Revision 1.5  2004/06/30 15:59:37  aschenk
 *@scr - 5891 disabled House Account Payment button  when parameter is set to No
 *
 *Revision 1.4  2004/06/02 13:03:23  dfierling
 *@scr 5245 - fixed Payment button on transaction
 *
 *Revision 1.3  2004/02/12 16:50:40  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Nov 24 2003 19:23:46   nrao
 * Code Review Changes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * @version $Revision: /main/12 $
 */
public class InstantCreditOptionsSite extends PosSiteActionAdapter
    implements ParameterConstantsIfc
{
    private static final long serialVersionUID = 9155177442788344260L;
    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSBaseBeanModel beanModel = new POSBaseBeanModel(); 
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        
        NavigationButtonBeanModel  localModel = new NavigationButtonBeanModel();

        InstantCreditCargo cargo = (InstantCreditCargo)bus.getCargo();
        
        // Initialize the status bar customer name, because it's possible we
        // get here from a cancel
        beanModel.setLocalButtonBeanModel(localModel);
        StatusBeanModel sbModel = new StatusBeanModel();
        sbModel.setCustomerName("");
        beanModel.setStatusBeanModel(sbModel);
        
        //configure buttons according to parameters, transaction re entry
        boolean reEntryOn = cargo.getRegister().getWorkstation().isTransReentryMode();        
        configureButtons(pm, localModel, reEntryOn);

        UtilityIfc util = null;
        try
        {
            util = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }


        String payments = util.getParameterValue(HOUSEACCOUNT_HouseAccountPayment, YES);
        
        if (cargo.getTransaction() != null)
        {
            localModel.setButtonEnabled(CommonActionsIfc.ENROLL, false);    
            localModel.setButtonEnabled(CommonActionsIfc.REFERENCE, false);   
            localModel.setButtonEnabled(CommonActionsIfc.PAYMENT, false);
        }
        else if (payments.equalsIgnoreCase(YES)) 
        {
            localModel.setButtonEnabled(CommonActionsIfc.PAYMENT, true);
        }
        else
        {
            localModel.setButtonEnabled(CommonActionsIfc.PAYMENT, false);
        }    
        
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.INSTANT_CREDIT_OPTIONS,beanModel);
    }

    /**
     * load the model with the params that control the buttons
     * 
     * @param pm ParameterManagerIfc
     * @param model NavigationButtonBeanModel to be sent to the UI
     */
    protected void configureButtons(ParameterManagerIfc pm, NavigationButtonBeanModel model, boolean reEntryOn)
    {
        try
        {
            if(reEntryOn)
            {
                model.setButtonEnabled(CommonActionsIfc.ENROLL, false);
            }
            else
            {
                model.setButtonEnabled(CommonActionsIfc.ENROLL, pm.getBooleanValue(HOUSEACCOUNT_InstantCreditEnrollment));                
            }
            model.setButtonEnabled(CommonActionsIfc.REFERENCE, pm.getBooleanValue(HOUSEACCOUNT_ReferenceNumberInquiry));
            model.setButtonEnabled(CommonActionsIfc.INQUIRY, pm.getBooleanValue(HOUSEACCOUNT_InstantCreditInquiry));
            model.setButtonEnabled(CommonActionsIfc.TEMP_PASS, pm.getBooleanValue(HOUSEACCOUNT_PrintTemporaryShoppingPass));
            model.setButtonEnabled(CommonActionsIfc.PAYMENT, pm.getBooleanValue(HOUSEACCOUNT_HouseAccountPayment));
        }
        
        catch(ParameterException pe)
        {
            logger.warn( pe.getStackTraceAsString());
        }
    }

}
