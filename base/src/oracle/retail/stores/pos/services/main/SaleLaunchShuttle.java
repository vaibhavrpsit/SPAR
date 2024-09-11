/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/SaleLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:12 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:58 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:00 PM  Robert Pearse   
 *
 *   Revision 1.9  2004/07/23 22:17:25  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.8  2004/04/07 17:50:56  tfritz
 *   @scr 3884 - Training Mode rework
 *
 *   Revision 1.7  2004/04/01 16:04:10  bjosserand
 *   @scr 4093 Transaction Reentry
 *
 *   Revision 1.6  2004/03/16 16:11:29  tfritz
 *   @scr 3884 - Removed system print lines.
 *
 *   Revision 1.5  2004/03/14 21:12:40  tfritz
 *   @scr 3884 - New Training Mode Functionality
 *
 *   Revision 1.4  2004/03/11 20:03:25  blj
 *   @scr 3871 - added/updated shuttles to/from redeem, to/from tender, to/from completesale.
 *   also updated sites cargo for new redeem transaction.
 *
 *   Revision 1.3  2004/02/12 16:48:05  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:24:06  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.5   Jan 13 2004 13:22:00   bjosserand
 * code review
 * 
 *    Rev 1.4   Dec 18 2003 09:27:24   bjosserand
 * Remove debugging output.
 * 
 *    Rev 1.3   Dec 16 2003 13:30:16   bjosserand
 * Main Refactor. Rewrite initialization using ADO and TDO objects.
 * 
 *    Rev 1.2   Nov 17 2003 14:56:36   cdb
 * Clear operator before going through Sale service.
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.1   08 Nov 2003 01:12:54   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 06 2003 00:21:54   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    Shuttle used to transfer POS related data.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class SaleLaunchShuttle extends FinancialCargoShuttle
{
    /** 
        The logger to which log messages will be sent.
    **/
    protected Logger logger = Logger.getLogger(getClass());
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Unloads the shuttle data into the cargo.
       <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);

        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        cargo.setPasswordRequired(true);
        cargo.setOperator(null);
        cargo.setAccessFunctionID(RoleFunctionIfc.POS);
        
        RegisterADO registerADO = ((MainCargo)callingCargo).getRegisterADO();
        RegisterIfc register = (RegisterIfc)registerADO.toLegacy();        
        if (((MainCargo)callingCargo).isTrainingMode())
        {
            register.getWorkstation().setTrainingMode(true);
        }
        else
        {
            register.getWorkstation().setTrainingMode(false);
        }
        cargo.setRegister(register);
        cargo.setStoreStatus((StoreStatusIfc)registerADO.getStoreADO().toLegacy());
    }
}
