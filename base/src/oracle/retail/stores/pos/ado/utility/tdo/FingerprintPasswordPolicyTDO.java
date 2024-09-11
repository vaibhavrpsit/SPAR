/* ===========================================================================
* Copyright (c) 2006, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/utility/tdo/FingerprintPasswordPolicyTDO.java /main/1 2011/02/23 10:45:34 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * At this time we have the std/original password policy (PasswordPolicyTDO)
 * and this new one FingerprintPasswordPolicyTDO. 
 *     IMPORTANT : Note that Fingerprint parameters allow for "NoFingerprint".
         In this scenario even though 'Fingerprinting' is turned "on", the std password policy will be in play.
         Typically clients will use this to prime the system with Fingerprints and may have one reg setup 
         in the back room with this setup.
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  02/22/11 - Password policy for Fingerprint based logins
 *    mkutiana  02/22/11 - Fingerprint login password policies initial version
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.ado.utility.tdo;


import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.services.operatorid.OperatorIdCargo;

import org.apache.log4j.Logger;

/**
    This TDO is used for Fingerprint Password Policy various evaluations and
    its affects in terms of flow in the application.
**/
public class FingerprintPasswordPolicyTDO extends AbstractPasswordPolicyTDO
{
    
    /**
    The logger to which log messages will be sent
    **/
   protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ado.utility.tdo.FingerprintPasswordPolicyTDO.class);
   
   /** 
    * Spring Key used to load this bean
    */ 
   public static final String FINGERPRINT_PASSWORD_POLICY_TDO_BEAN_KEY = "application_FingerprintPasswordPolicyTDO";
  

    //----------------------------------------------------------------------
    /**
       Checks if Employee Compliance is Allowed. 
       @param bus reference to bus
       @return true if employee compliance is allowed, otherwise false
    **/
    //----------------------------------------------------------------------
    public boolean checkEmployeeComplianceEvaluationAllowed(BusIfc bus) 
    {
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        if (cargo.getSecurityOverrideFlag())
        {
            return false;
        }
        boolean trainingModeOn = checkTrainingMode(bus);
        if(trainingModeOn)
        {
            return false;
        }
        if (isFingerprintAllowed())
        {
            return false;
        }       
        return true;
    }
    
    /*
     * Are fingerprints for logging in turned on (based on parameter-FingerprintLoginOptions)
     * @return boolean 
     */
     private boolean isFingerprintAllowed()
     {       
         return Utility.getUtil().isFingerprintAllowed();     
     }
 
}
