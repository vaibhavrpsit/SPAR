/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/SaleReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:12 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    08/10/10 - removed the training register object
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:59 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:01 PM  Robert Pearse   
 *
 *   Revision 1.11  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.10  2004/07/23 22:17:25  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.9  2004/07/09 04:01:18  crain
 *   @scr 5822 Training mode is turning off when logging into POS
 *
 *   Revision 1.8  2004/07/06 19:35:22  dcobb
 *   @scr 5503 Training Mode button should be disabled when store / register / till are not open.
 *   Return store status to main service from sale service.
 *
 *   Revision 1.7  2004/06/22 13:48:54  khassen
 *   @scr 5761 - Updated the ADO object in the main cargo with the register from the sale cargo.
 *
 *   Revision 1.6  2004/04/07 17:50:56  tfritz
 *   @scr 3884 - Training Mode rework
 *
 *   Revision 1.5  2004/03/23 19:16:02  tfritz
 *   @scr 3884 - Make sure to set the correct RegisterADO when in training mode.
 *
 *   Revision 1.4  2004/03/19 15:37:04  bjosserand
 *   @scr 4093 Transaction Reentry
 *
 *   Revision 1.3  2004/03/14 21:12:40  tfritz
 *   @scr 3884 - New Training Mode Functionality
 *
 *   Revision 1.2  2004/02/12 16:48:05  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Jan 13 2004 13:22:00   bjosserand
 * code review
 * 
 *    Rev 1.1   Dec 30 2003 14:22:28   rwh
 * Refactored classes in Main to use RegisterADO in place of VirtualRegisterADO. Added methods to RegisterADO, tender limits and operator. Moved read financials method from VirtualRegisterADO to MainTDO
 * Resolution for POS SCR-3653: RegisterADO Refactor
 * 
 *    Rev 1.0   Dec 16 2003 17:01:34   bjosserand
 * Initial revision.
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
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//--------------------------------------------------------------------------
/**
    Shuttle used to transfer POS related data.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class SaleReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 6136454329568666770L;


   /**
      revision number
   **/
   public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

   /**
    The logger to which log messages will be sent.
   **/
   protected Logger logger = Logger.getLogger(getClass());

   protected SaleCargoIfc saleCargo;

   public SaleReturnShuttle()
   {
      super();
   }

   //----------------------------------------------------------------------
   /**
      Copies information from the cargo used in the service.  <P>
      @param bus Service Bus
   **/
   //----------------------------------------------------------------------
   public void load(BusIfc bus)
   {      
      saleCargo = (SaleCargoIfc) bus.getCargo();
   }
   
   //----------------------------------------------------------------------
   /**
      Unloads the shuttle data into the cargo.
      <p>
      @param  bus     Service Bus
   **/
   //----------------------------------------------------------------------
   public void unload(BusIfc bus)
   {
      MainCargo cargo = (MainCargo) bus.getCargo();
      
      RegisterADO registerADO = cargo.getRegisterADO();
      RegisterIfc register = saleCargo.getRegister();        
      if (cargo.isTrainingMode())
      {
          register.getWorkstation().setTrainingMode(true);
      }
      else
      {
          register.getWorkstation().setTrainingMode(false);
      }      
      registerADO.fromLegacy(register);
      registerADO.getStoreADO().fromLegacy(saleCargo.getStoreStatus());
   }
}
