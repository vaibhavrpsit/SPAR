/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/FingerprintEnrollAisle.java /main/3 2011/02/23 18:52:23 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   02/23/11 - cleaned up traverse. Moved the dialog handling from
 *                         its own site into traverse. Moved the audit log from
 *                         this aisle to FingerprintVerifyAisle (log only
 *                         occurs after fingerprint passes all the checks).
 *    hyin      01/27/11 - change to use fingerprint audit log event
 *    blarsen   05/19/10 - fingerprint enroll aisle
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeefind;

import oracle.retail.stores.foundation.manager.device.FingerprintReaderModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;


/*
 * FingerprintEnrollAisle handles the data event from the fingerprint reader.
 * 
 * This class checks the event, displays erorr dialog if needed and saves the fingerprint 
 * data on the cargo.
 * 
 */
public class FingerprintEnrollAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 2156143119679059961L;
    public static final String revisionNumber = "$Revision: /main/3 $";

    //--------------------------------------------------------------------------
    /** 
        The traverse method is called when a fingerprint enrollment event occurs.
        This Aisle gets the fingerprint data from the model and assigns it to the
        cargo's employee.
        <P>
        @version $Revision: /main/3 $
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        EmployeeCargo cargo = (EmployeeCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel(POSUIManagerIfc.EMPLOYEE_ENROLL_FINGERPRINT);
        PromptAndResponseModel pAndRModel = model.getPromptAndResponseModel();
        FingerprintReaderModel fingerprintModel = pAndRModel.getFingerprintModel();
        
        if (fingerprintModel.getEventType() == FingerprintReaderModel.EVENT_TYPE.ENROLL)
        {
            cargo.setFingerprintEnrollmentTemplate(fingerprintModel.getFingerprintData());
    
            Letter result = new Letter(CommonLetterIfc.CONTINUE);
            bus.mail(result, BusIfc.CURRENT);
        }
        else
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("FingerprintEnrollRetry");
            dialogModel.setType(DialogScreensIfc.CONFIRMATION);
    
            // set and display the model
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            logger.warn("FingerprintEnrollAisle received unsupported event type: " + fingerprintModel.getEventType());
        }

    }

}

