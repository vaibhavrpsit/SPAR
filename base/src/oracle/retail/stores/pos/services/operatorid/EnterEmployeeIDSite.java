/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/EnterEmployeeIDSite.java /main/16 2012/03/12 13:52:06 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     02/28/12 - XbranchMerge icole_bug-13699752 from
 *                         rgbustores_13.4x_generic_branch
 *    icole     02/08/12 - Added clear swipe ahead in order to display IDLE
 *                         screen.
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    mkutiana  02/22/11 - Modified to handle multiple password policies
 *                         (introduction of biometrics)
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   04/27/09 - Refactored method checkChangePasswordAllowed to
 *                         prevent issue with button not getting disabled when
 *                         it should.
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         11/2/2006 7:10:46 AM   Rohit Sachdeva
 *         21237: Activating Password Policy Evaluation and Change Password
 *    7    360Commerce 1.6         10/25/2006 3:14:33 PM  Rohit Sachdeva
 *         21237: Password Policy TDO updates
 *    6    360Commerce 1.5         10/16/2006 3:22:15 PM  Rohit Sachdeva
 *         21237: Password Policy Flow Updates
 *    5    360Commerce 1.4         10/13/2006 2:57:56 PM  Rohit Sachdeva
 *         21237: Change Password Updates
 *    4    360Commerce 1.3         10/9/2006 1:42:47 PM   Rohit Sachdeva
 *         21237: Change Password Updates
 *    3    360Commerce 1.2         3/31/2005 4:28:01 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/04/05 15:47:54  jdeleau
 *   @scr 4090 Code review comments incorporated into the codebase
 *
 *   Revision 1.5  2004/03/25 20:25:15  jdeleau
 *   @scr 4090 Deleted items appearing on Ingenico, I18N, perf improvements.
 *   See the scr for more info.
 *
 *   Revision 1.4  2004/02/13 19:43:06  jriggins
 *   @scr 0 Removed elements causing compiler warnings
 *
 *   Revision 1.3  2004/02/12 16:51:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik    
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 03 2003 16:13:22   RSachdeva
 * Add CIDScreen support
 * Resolution for POS SCR-3355: Add CIDScreen support
 * 
 *    Rev 1.0   Aug 29 2003 16:03:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:13:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:32:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:16   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;

import oracle.retail.stores.commerceservices.security.EmployeeStatusEnum;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.tdo.PasswordPolicyTDOIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

/**
 * This site displays the LOGIN screen.
 * 
 * @version $Revision: /main/16 $
 */
public class EnterEmployeeIDSite extends PosSiteActionAdapter
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4415995541495667236L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /**
     * Displays the LOGIN screen.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        //reset what is required
        resetCargo(bus);
        //check if we should even enable the change password button or not
        NavigationButtonBeanModel nbbModel = checkChangePasswordAllowed(bus);       
        POSBaseBeanModel model = new POSBaseBeanModel();
        model.setLocalButtonBeanModel(nbbModel);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.OPERATOR_IDENTIFICATION, model);
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        WorkstationIfc workstation = cargo.getRegister().getWorkstation();
        PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);
        paymentManager.clearSwipeAheadData(workstation);
        paymentManager.showLogo(workstation);
    }

    /**
     * This resets cargo for settings that will be set again
     * @param bus reference to bus
     */
    private void resetCargo(BusIfc bus) 
    {
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        cargo.setLoginValidationChangePassword(false);
        cargo.setLockOut(false);
        cargo.setEmployeeID("");
        cargo.setSelectedEmployee(null);
        cargo.setEvaluateStatusEnum(EmployeeStatusEnum.ACTIVE);
    }
 
    /**
     * This checks if Change Password needs to be enabled
     * @param bus reference to bus
     * @return  NavigationButtonBeanModel navigation bean model
     */
    private NavigationButtonBeanModel checkChangePasswordAllowed(BusIfc bus)
    {
        NavigationButtonBeanModel nbbModel = new NavigationButtonBeanModel();
        //default button is enabled
        boolean enableChangePasswordButton = true;
        PasswordPolicyTDOIfc tdo = getPasswordPolicyTDO();
        boolean employeeComplianceAllowed = tdo.checkEmployeeComplianceEvaluationAllowed(bus);
        if (!employeeComplianceAllowed)
        {
            enableChangePasswordButton = false;
        }
        boolean passwordRequired = tdo.checkPasswordParameter(bus);
        if(!passwordRequired)
        {
            enableChangePasswordButton = false;
        }
        nbbModel.setButtonEnabled(CommonActionsIfc.CHANGE_PASSWORD, enableChangePasswordButton);                    
        return nbbModel;
    }

    /**
     * Calls <code>arrive</code>
     * 
     * @param bus Service Bus
     */
    @Override
    public void reset(BusIfc bus)
    {
        arrive(bus);
    }

    /**
     * Creates Instance of Password Policy TDO.
     * 
     * @return PasswordPolicyTDOIfc instance of Password Policy TDO
     */
    private PasswordPolicyTDOIfc getPasswordPolicyTDO()
    {
        return Utility.getUtil().getPasswordPolicyTDO();
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
        String strResult = new String("Class:  EnterEmployeeIDSite (Revision " + getRevisionNumber() + ")" + hashCode());

        // pass back result
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
