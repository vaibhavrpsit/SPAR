/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/ManagerStationLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:00 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:33 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:14  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:48:47  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:15  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:52:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jan 12 2003 16:03:58   pjf
 * Remove deprecated calls to AbstractFinancialCargo.getCodeListMap(), setCodeListMap().
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 * 
 *    Rev 1.0   Apr 29 2002 15:36:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:03:54   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:18:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   06 Mar 2002 16:29:56   baa
 * Replace get/setAccessEmployee with get/setOperator
 * Resolution for POS SCR-802: Security Access override for Reprint Receipt does not journal to requirements
 *
 *    Rev 1.0   Sep 21 2001 11:10:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:05:16   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.manager.ManagerCargo;

//------------------------------------------------------------------------------
/**

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class ManagerStationLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7479689012256110371L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.admin.ManagerStationLaunchShuttle.class);

    public static final String SHUTTLENAME = "ManagerStationLaunchShuttle";
    /**
       source cargo.
    **/

    protected AdminCargo aCargo = null;

    //--------------------------------------------------------------------------
    /**
       ##COMMENT-LOAD##

       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------

    public void load(BusIfc bus)
    {
       super.load(bus);
       aCargo = (AdminCargo) bus.getCargo();

    }

    //--------------------------------------------------------------------------
    /**
       ##COMMENT-UNLOAD##

       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------

    public void unload(BusIfc bus)
    {
        super.unload(bus);
        ManagerCargo cargo = (ManagerCargo) bus.getCargo();

        cargo.setRegister(aCargo.getRegister());
        cargo.setStoreStatus(aCargo.getStoreStatus());
        cargo.setOperator(aCargo.getOperator());

    }
}
