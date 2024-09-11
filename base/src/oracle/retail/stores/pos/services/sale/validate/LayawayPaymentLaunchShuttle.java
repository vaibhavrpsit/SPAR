/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/LayawayPaymentLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:10 mszekely Exp $
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
 *    4    360Commerce 1.3         1/25/2006 4:11:28 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:50 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:03 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:16 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/17/2005 16:39:24    Jason L. DeLeau 4345:
 *         Replace any uses of Gateway.log() with the log4j.
 *    3    360Commerce1.2         3/31/2005 15:28:50     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:23:03     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:16     Robert Pearse
 *
 *   Revision 1.5  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/08/09 14:03:55  kll
 *   @scr 6796: logging clean-up
 *
 *   Revision 1.3  2004/02/12 16:48:21  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   08 Nov 2003 01:27:00   baa
 * cleanup -sale refactoring
 *
 *    Rev 1.0   Nov 05 2003 14:35:14   sfl
 * Initial revision.
 * Resolution for POS SCR-3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.validate;

// foundation imports
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the cargo used
    in the Sale service to the cargo used in the Gift Card Activation service. <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LayawayPaymentLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{                                       // begin class LayawayPaymentLaunchShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 609532289112114920L;


    /** revision number supplied by Team Connection **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** SaleCargoIfc  **/
    protected SaleCargoIfc saleCargo = null;

    //----------------------------------------------------------------------
    /**
        Loads cargo from Sale service. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>Cargo will contain the layaway transaction
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>
        </UL>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()
    	if (logger.isDebugEnabled()) logger.debug(
                    "***** LayawayPaymentLaunchShuttle.load()");

        // Get SaleCargoIfc
        super.load(bus);
        saleCargo = (SaleCargoIfc)bus.getCargo();
        if (logger.isDebugEnabled()) logger.debug(
                    "***end** LayawayPaymentLaunchShuttle.load()");
    }                                   // end load()

    //----------------------------------------------------------------------
    /**
        Loads data into LaywayPayment service. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>Cargo will contain the layayway transaction
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>
        </UL>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()
        super.unload(bus);
        LayawayCargo layawayCargo = (LayawayCargo) bus.getCargo();

        // Sets layawayTransaction in layawayCargo
        layawayCargo.setInitialLayawayTransaction(
            (LayawayTransactionIfc)saleCargo.getTransaction());
    }                                   // end unload()

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.  <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  LayawayPaymentLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class. <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}                                       // end class LayawayPaymentLaunchShuttle
