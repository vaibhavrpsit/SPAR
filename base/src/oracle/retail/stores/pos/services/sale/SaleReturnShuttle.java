/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/SaleReturnShuttle.java /main/18 2014/02/26 14:19:58 vbongu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED (MM/DD/YY)
*    vbongu 02/25/14 - moving call referral data into mpos specific shuttle
*    asinto 02/20/14 - added flag to suppress gift card activations in the sale
*                      complete tour when a call referral was performed
*    blarse 02/04/14 - AJB requires original auth response for call referrals.
*                      Adding this to appropriate shuttles/cargos.
*    asinto 08/02/12 - Call referral refactor
*    cgreen 02/15/11 - move constants into interfaces and refactor
*    nkgaut 09/20/10 - refractored code to use a single class for checking cash
*                      in drawer
*    cgreen 05/26/10 - convert to oracle packaging
*    asinto 03/04/10 - setRefreshNeeded(true) on the return to the sale
*                      service.
*    abonda 01/03/10 - update header date
*    nkgaut 09/18/08 - Loading cash drawer warning boolean variable in
*                      saleCargo

* ===========================================================================

     $Log:
      4    360Commerce 1.3         1/22/2006 11:45:02 AM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         3/31/2005 4:29:48 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:59 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:14:01 PM  Robert Pearse
     $
     Revision 1.10  2004/09/23 00:07:11  kmcbride
     @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents

     Revision 1.9  2004/07/28 15:34:15  rsachdeva
     @scr 4865 Transaction Sales Associate

     Revision 1.8  2004/07/14 15:40:19  jdeleau
     @scr 5025 Persist the item selection on the sale screen across services, such that
     when it returns to the sale screen the same items are selected, if possible.

     Revision 1.7  2004/06/10 23:06:35  jriggins
     @scr 5018 Added logic to support replacing PriceAdjustmentLineItemIfc instances in the transaction which happens when shuttling to and from the pricing service

     Revision 1.6  2004/03/17 16:00:15  epd
     @scr 3561 Bug fixing and refactoring

     Revision 1.5  2004/03/15 21:55:15  jdeleau
     @scr 4040 Automatic logoff after timeout

     Revision 1.4  2004/02/16 13:38:31  baa
     @scr  3561 returns enhancements

     Revision 1.3  2004/02/12 16:48:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:22:50  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.6   Jan 07 2004 10:11:54   nrao
 * Returning lastReprintableTransactionId to fix defects SCR 3593 and SCR 3573.
 * Resolution for 3573: Unable to Reprint a Credit Transaction by selecting Last Transaction.
 * Resolution for 3593: Unable to Reprint Receipt from a Gift Card Tender Transaction
 *
 *    Rev 1.5   Nov 26 2003 09:12:28   lzhao
 * remove tendering.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.4   Nov 14 2003 12:49:56   cdb
 * Corrected problem of lost store status on return from open till (and register and store).
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.3   Nov 10 2003 16:44:36   cdb
 * Updated to preserve till created during initialization of Sale service.
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.2   Nov 07 2003 12:38:02   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.1   Nov 07 2003 07:34:46   baa
 * integration with subservices
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.0   Nov 03 2003 14:49:18   baa
 * Initial revision.
 *
 *    Rev 1.0   Aug 29 2003 16:04:10   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 21 2002 11:21:28   DCobb
 * Added Alterations service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
* ===================================================
*/
package oracle.retail.stores.pos.services.sale;

import java.math.BigDecimal;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.TimedCargoIfc;

import org.apache.log4j.Logger;

/**
 * This shuttle copies information from the cargo used in the modifyItem service
 * to the cargo used in the Alterations service.
 *
 * @version $Revision: /main/18 $
 */
public class SaleReturnShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -1545911469313547532L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(SaleReturnShuttle.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/18 $";

    /**
     * sale cargo
     */
    protected SaleCargoIfc saleCargo;

    /**
     * Loads cargo from alterations service.
     *
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        saleCargo = (SaleCargoIfc) bus.getCargo();
    }

    /**
     * Loads cargo for modifyItem service.
     *
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
        cargo.setItemQuantity(BigDecimal.valueOf(1));
        cargo.setAccessFunctionID(saleCargo.getAccessFunctionID());
        cargo.setPasswordRequired(saleCargo.isPasswordRequired());
        cargo.setCustomerInfo(saleCargo.getCustomerInfo());
        cargo.setEmployee(saleCargo.getEmployee());
        cargo.setLineItem(saleCargo.getLineItem());
        cargo.setOperator(saleCargo.getOperator());
        cargo.setPLUItem(saleCargo.getPLUItem());
        cargo.setIndices(saleCargo.getIndices());
        cargo.setTransaction(saleCargo.getTransaction());
        cargo.setRegister(saleCargo.getRegister());
        cargo.setStoreStatus(saleCargo.getStoreStatus());
        cargo.setLastReprintableTransactionID(saleCargo.getLastReprintableTransactionID());
        cargo.setOriginalReturnTransactions(saleCargo.getOriginalReturnTransactions());
        cargo.setOriginalPriceAdjustmentTransactions(saleCargo.getOriginalPriceAdjustmentTransactions());
        cargo.setAlreadySetTransactionSalesAssociate(false);

        /*
         * Set refresh needed to true so that the items get updated.  This can
         * happen when a gift card activation fails and we return to the sale service
         * and a transaction is still in progress.
         */
        cargo.setRefreshNeeded(true);

        if( !cargo.isCashDrawerUnderWarning())
        {
          cargo.setCashDrawerUnderWarning(saleCargo.isCashDrawerUnderWarning());
        }

        if(cargo instanceof TimedCargoIfc && saleCargo instanceof TimedCargoIfc)
        {
            ((TimedCargoIfc)cargo).setTimeout(((TimedCargoIfc)saleCargo).isTimeout());
        }
        // set flag to indicate that gift card activation should be suppressed
        cargo.setSuppressGiftCardActivation(saleCargo.isSuppressGiftCardActivation());
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
        String strResult = new String("Class:  SaleReturnShuttle (Revision " + getRevisionNumber() + ") @" + hashCode());
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
