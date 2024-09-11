/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/OperatorIdLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:12 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    08/10/10 - removed the training register object
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:29:12 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:23:49 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:12:50 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/09/23 00:07:13  kmcbride
 *  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *  Revision 1.3  2004/07/28 22:53:23  epd
 *  @scr 6593 fixed training mode issue
 *
 *  Revision 1.2  2004/04/09 16:56:02  cdb
 *  @scr 4302 Removed double semicolon warnings.
 *
 *  Revision 1.1  2004/03/14 21:12:41  tfritz
 *  @scr 3884 - New Training Mode Functionality
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;
// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.operatorid.OperatorIdCargo;

//------------------------------------------------------------------------------
/**
    This shuttle carries the required contents from
    the calling service to the OperatorId service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class OperatorIdLaunchShuttle implements ShuttleIfc
{                                       // begin class OperatorIdLaunchShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 6696547859977136693L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.common.OperatorIdLaunchShuttle.class);

    /**
       revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       class name constant
    **/
    public static final String SHUTTLENAME = "OperatorIdLaunchShuttle";

    /**
       The screen prompt text.
    **/
    protected String operatorIdPromptText = null;

    /**
       The screen name.
    **/
    protected String operatorIdScreenName = null;

    protected RegisterIfc register;
    //--------------------------------------------------------------------------
    /**
       Copies information from the cargo used in the calling service. <P>
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc) bus.getCargo();
        operatorIdPromptText = cargo.getOperatorIdPromptText();
        operatorIdScreenName = cargo.getOperatorIdScreenName();
        RegisterADO registerADO = ((MainCargo)cargo).getRegisterADO();
        register = (RegisterIfc)registerADO.toLegacy();
        if (((MainCargo)cargo).isTrainingMode())
        {
            register.getWorkstation().setTrainingMode(true);
        }
        else
        {
            register.getWorkstation().setTrainingMode(false);
        }        
    }

    //--------------------------------------------------------------------------
    /**
       Copies information to the cargo used in the calling service. <P>
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // set defaults
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        cargo.setMaximumAttempts(1);
        cargo.setHandleError(true);
        cargo.setOperatorIdPromptText(operatorIdPromptText);
        cargo.setOperatorIdScreenName(operatorIdScreenName);
        cargo.setRegister(register);

    }

    //---------------------------------------------------------------------
    /**
       Retrieves the source-code-control system revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                  // end getRevisionNumber()
}                                       // end class OperatorIdLaunchShuttle
