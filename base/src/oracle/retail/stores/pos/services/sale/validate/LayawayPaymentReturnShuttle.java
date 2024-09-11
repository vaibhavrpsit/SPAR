/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/LayawayPaymentReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    nkgautam  09/20/10 - refractored code to use a single class for checking
 *                         cash in drawer
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    nkgautam  02/01/10 - setting cash drawer under warning in the return
 *                         cargo
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
 *    4    .v700     1.2.1.0     11/17/2005 16:39:21    Jason L. DeLeau 4345:
 *         Replace any uses of Gateway.log() with the log4j.
 *    3    360Commerce1.2         3/31/2005 15:28:50     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:23:03     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:16     Robert Pearse
 *
 *   Revision 1.5  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/08/09 14:03:54  kll
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
 *    Rev 1.1   08 Nov 2003 01:27:04   baa
 * cleanup -sale refactoring
 *
 *    Rev 1.0   Nov 05 2003 14:40:24   sfl
 * Initial revision.
 * Resolution for POS SCR-3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.validate;

// foundation imports
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;

//--------------------------------------------------------------------------
/**
   Shuttle used to transfer layaway payment related data.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LayawayPaymentReturnShuttle
extends FinancialCargoShuttle
implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -1760987225502251146L;

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected LayawayCargo layawayCargo = null;


    //----------------------------------------------------------------------
    /**
        Loads the shuttle data from the parent service's cargo into this shuttle.
        The data elements to transfer are determined by the interfaces that
        the parent cargo implements.  For example, none of the layawaySearchCargo
        data elements will be transferred if the calling service's cargo implements
        layawaySummaryCargoIfc.

        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);

        layawayCargo = (LayawayCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Unloads the shuttle data into the cargo.
       If used as both a launch and return shuttle, this cargo will reset the
       references in the calling service to refer to the same objects they
       originally transferred.

        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
    	if (logger.isDebugEnabled()) logger.debug("******** LayawayShuttle.unload()");

        super.unload(bus);

        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        cargo.setTransaction(
            (SaleReturnTransactionIfc)layawayCargo.getTenderableTransaction());

        cargo.setOperator(layawayCargo.getOperator());
        cargo.setRegister(layawayCargo.getRegister());
        cargo.setCashDrawerUnderWarning(layawayCargo.isCashDrawerUnderWarning());


    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.  <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult =
            new String("Class: "    + getClass().getName() +
                       "(Revision " + getRevisionNumber()  +
                       ") @" + hashCode());

        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class. <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

}
