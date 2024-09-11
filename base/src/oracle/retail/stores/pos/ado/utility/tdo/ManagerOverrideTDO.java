/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/utility/tdo/ManagerOverrideTDO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:41 mszekely Exp $
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
 *    1    360Commerce 1.0         12/13/2005 4:47:03 PM  Barry A. Pape   
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.utility.tdo;



import org.apache.log4j.Logger;


import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;


//------------------------------------------------------------------------------
/**
    This TDO checks for ManagerOverrideForSecurityAccess parameter role functions
    from the application properties file.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ManagerOverrideTDO extends TDOAdapter implements ManagerOverrideTDOIfc
{

    /**
     * revision number
     */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * logger being used
     */
    static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ado.utility.tdo.ManagerOverrideTDO.class);;
    
    /**
     * Default Constructor for ManagerOverrideTDO
     */
    public ManagerOverrideTDO()
    {
        super();
    }

       
    /**
     * Checks for the function id if it is overridable
     * @param bus reference for bus
     * @param functionId function id to check
     * @return boolean true implies overridable
     */
    public boolean isOverridable(BusIfc bus, int functionId)
    {
        ParameterManagerIfc parmManager = 
                    (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        
        String overrideFunctions[] = null;
        
        try             // retrieve the list of override-able functions
        {
            overrideFunctions = parmManager.getStringValues("ManagerOverrideForSecurityAccess");
        }
        catch (ParameterException pe)
        {
            logger.warn( pe.getMessage());
        }
        boolean overridable = containsID(overrideFunctions, functionId);
        return overridable;
    }
    
    //----------------------------------------------------------------------
    /**
        Check to see if the functionID being accessed is in the list of 
        functions contained by the ManagerOverrideForSecurityAccess parameter.
        @param rcValues The list of override-able functions.
        @param functionID The ID of the function being accessed.
        @return boolean true indicate id is there
    **/
    //----------------------------------------------------------------------
    public boolean containsID(String[] rcValues, int functionID)
    {
        int retreivedId;
        for (int k=0; k< rcValues.length; k++)
        {
            String retrievedFunctionId = (Gateway.getProperty("application", 
                                                             "ManagerOverrideForSecurityAccess."+rcValues[k], 
                                                             "-1")).trim();
            retreivedId = Integer.parseInt(retrievedFunctionId);
            if (retreivedId == functionID)
            {
                return true;
            }
        }
        return false;         
    }
}
