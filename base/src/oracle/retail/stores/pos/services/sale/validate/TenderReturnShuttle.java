/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/TenderReturnShuttle.java /main/15 2014/07/01 13:33:27 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   06/05/14 - XbranchMerge
 *                         blarsen_bug18854403-ajb-call-ref-trans-cancel-bad-invoiceid
 *                         from rgbustores_14.0x_generic_branch
 *    blarsen   06/03/14 - Refactor: Moving call referral fields into their new
 *                         class.
 *    asinton   02/20/14 - added flag to suppress gift card activations in the
 *                         sale complete tour when a call referral was
 *                         performed
 *    blarsen   02/04/14 - AJB requires original auth response for call
 *                         referrals. Adding this to appropriate
 *                         shuttles/cargos.
 *    asinton   08/02/12 - Call referral refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:26 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:03 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:56 PM  Robert Pearse
 *
 *   Revision 1.8  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.7  2004/08/19 21:55:41  blj
 *   @scr 6855 - Removed old code and fixed some flow issues with gift card credit.
 *
 *   Revision 1.6  2004/08/06 18:24:17  dcobb
 *   @scr 6655 Remove letter checks from shuttles.
 *   Removed commented out code.
 *
 *   Revision 1.5  2004/07/23 22:17:26  epd
 *   @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
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
 *    Rev 1.5   Feb 02 2004 08:38:30   blj
 * fixed a nullpointer exception
 *
 *    Rev 1.4   Jan 30 2004 15:13:26   blj
 * gift card refund issue implementation
 *
 *    Rev 1.3   Nov 12 2003 06:44:06   baa
 * build break, references to old ado package
 *
 *    Rev 1.2   08 Nov 2003 01:27:20   baa
 * cleanup -sale refactoring
 *
 *    Rev 1.1   Nov 07 2003 07:47:28   baa
 * integration with subservices
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.validate;

// foundation imports
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.authorization.CallReferralData;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the cargo used
    in the Tender service to the cargo used in the Sale service. <p>
    @version $Revision: /main/15 $
**/
//--------------------------------------------------------------------------
public class TenderReturnShuttle implements ShuttleIfc
{                                       // begin class TenderReturnShuttle()
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8288983768835929378L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.sale.validate.TenderReturnShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/15 $";
    /**
       transaction
    **/
    protected RetailTransactionIfc transaction;

    /** call referral data */
    protected CallReferralData callReferralData = new CallReferralData();

    /** flag to indicate that gift card activation should be suppressed */
    protected boolean suppressGiftCardActivation;

    //----------------------------------------------------------------------
    /**
       Loads cargo from tender service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the selected item
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
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        transaction = (RetailTransactionIfc)((ADO)cargo.getCurrentTransactionADO()).toLegacy();
        callReferralData = cargo.getCallReferralData();
        suppressGiftCardActivation = cargo.isSuppressGiftCardActivation();
    }                                   // end load()

    //----------------------------------------------------------------------
    /**
       Loads cargo for POS service. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>Cargo will contain the selected item
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
        SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
        cargo.setTransaction((SaleReturnTransactionIfc) transaction);
        cargo.setCallReferralData(callReferralData);
        cargo.setSuppressGiftCardActivation(suppressGiftCardActivation);
    }                                   // end unload()

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
        String strResult = new String("Class:  TenderReturnShuttle (Revision " +
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

}                                       // end class TenderReturnShuttle
