/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/SaveFinancialsActionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:12 mszekely Exp $
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
 */
package oracle.retail.stores.pos.services.main;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;


//--------------------------------------------------------------------------
/**
    Writes register number, pos version, domain version
    and foundation version to the log. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class SaveFinancialsActionSite extends PosSiteActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /**
       The logger to which log messages will be sent.
    **/
      protected Logger logger = Logger.getLogger(getClass());

    //----------------------------------------------------------------------
    /**
       Writes the register number, pos version, domain version
       and foundation version to the log. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {   
        // begin arrive()
        // letter to mail to next destination
        Letter letter = null;
        MainCargo cargo = (MainCargo)bus.getCargo();
        
        try
        {
            RegisterADO register = cargo.getRegisterADO();
            register.writeHardTotals();
            letter = new Letter(CommonLetterIfc.SUCCESS);
        }
        catch (Exception e)
        {
            logger.error(Util.throwableToString(e));
            letter = new Letter(CommonLetterIfc.FAILURE);
        }

        // mail letter

        bus.mail(letter, BusIfc.CURRENT);
    }                                   // end arrive

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
    }                                   // end getRevisionNumber()

}
