/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/TenderLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:50 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    kkhirema  03/18/09 - Updated the tenderCargo to have the Amount in the
 *                         tender attributes to display in CPIO device
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         12/13/2005 4:42:34 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:00 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:54 PM  Robert Pearse
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:48:15  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:28:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Nov 04 2003 11:16:08   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 23 2003 17:28:36   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 13:03:26   epd
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:08:14   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:44:14   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:22:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.postvoid;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;

//--------------------------------------------------------------------------
/**
    Copies the information needed by the Tender service
    from the cargo in the Void service.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TenderLaunchShuttle extends FinancialCargoShuttle
{
    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.postvoid.TenderLaunchShuttle.class);
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       transaction
    **/
    protected RetailTransactionADOIfc transactionADO;
    /**
       void cargo
    **/
    protected VoidCargo voidCargo;

    /** previous letter **/
    protected String letter = null;

    protected TenderADOIfc nextTender = null;


    //----------------------------------------------------------------------
    /**
       Get data from void cargo to shuttle
       <P>
       @param  bus     Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // load the financial cargo
        super.load(bus);
        // load the void cargo
        voidCargo = (VoidCargo) bus.getCargo();
        transactionADO = (RetailTransactionADOIfc) voidCargo.getCurrentTransactionADO();
        nextTender = voidCargo.getNextTender();
        LetterIfc ltr = bus.getCurrentLetter();

        if (ltr != null)
        {
            letter = ltr.getName();
        }

    }

    //----------------------------------------------------------------------
    /**
       Get data from shuttle to tender cargo.
       <P>
       @param  bus     Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // unload the financial cargo
        super.unload(bus);
        // unload the tender cargo
        TenderCargo tenderCargo = (TenderCargo) bus.getCargo();
        tenderCargo.setTransaction((TenderableTransactionIfc)((ADO)transactionADO).toLegacy());
        tenderCargo.setCurrentTransactionADO(transactionADO);
        tenderCargo.setNextTender(null);
        tenderCargo.setSubTourLetter(letter);
        tenderCargo.getTenderAttributes().put(TenderConstants.TENDER_TYPE, TenderTypeEnum.DEBIT);
        tenderCargo.getTenderAttributes().put(TenderConstants.AMOUNT, transactionADO.getTenderTotal(TenderTypeEnum.DEBIT).getStringValue());
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  TenderLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}
