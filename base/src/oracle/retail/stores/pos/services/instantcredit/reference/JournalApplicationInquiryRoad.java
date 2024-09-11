/* ===========================================================================
* Copyright (c) 2003, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/reference/JournalApplicationInquiryRoad.java /main/15 2013/11/05 16:44:00 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   11/05/13 - fixed sales associate argument in call to
 *                         journalManager.journal
 *    asinton   10/10/13 - removed references to social security number and
 *                         replaced with locale agnostic government id
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    icole     08/12/11 - Corrected journal entry for HPQC 427.
 *    sgu       05/20/11 - add journaling for new inquiry criteria
 *    sgu       05/20/11 - refactor instant credit inquiry flow
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         5/21/2007 9:18:50 PM   Mathews Kochummen use
 *      locale format
 * 3    360Commerce 1.2         3/31/2005 4:28:48 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:22:58 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:12:11 PM  Robert Pearse
 *
 *Revision 1.3  2004/02/12 16:50:45  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:22  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Nov 24 2003 19:56:02   nrao
 * Changed copyright message.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.instantcredit.reference;

// foundation imports
import java.text.DateFormat;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.instantcredit.InstantCreditCargo;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

//------------------------------------------------------------------------------
/**

    @version $Revision: /main/15 $
**/
//------------------------------------------------------------------------------

public class JournalApplicationInquiryRoad extends LaneActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2855379305163852999L;

    //--------------------------------------------------------------------------
    /**


            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        InstantCreditCargo cargo = (InstantCreditCargo)bus.getCargo();

        // create a string buffer for the journal string
        StringBuffer sb = new StringBuffer();
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.INSTANT_CREDIT_APPLICATION_INQUIRY_LABEL, null));
        Object[] dataArgs = new Object[1];

        if (cargo.getReferenceNumber() != null)
        {
            sb.append(Util.EOL);
            dataArgs[0] = cargo.getReferenceNumber();
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.REFERENCE_NUMBER_LABEL, dataArgs));
        }
        
        if(cargo.getSalesAssociate().getAlternateID() != null)
        {
        	sb.append(Util.EOL);
        	dataArgs[0] = cargo.getSalesAssociate().getAlternateID();
        	sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SALES_ASSOCIATION, dataArgs));
        }
        
        if (cargo.getZipCode() != null)
        {
            sb.append(Util.EOL);
            dataArgs[0] = cargo.getZipCode();
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ZIP_LABEL, dataArgs));
        }

        if(cargo.getHomePhone() != null)
        {
            sb.append(Util.EOL);
            dataArgs[0] = cargo.getHomePhone();
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PHONE_NUMBER_LABEL, dataArgs));
        }

        if (cargo.getGovernmentId() != null)
        {
            sb.append(Util.EOL);
            dataArgs[0] = "XXX-XX-XXXX";
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GOVERNMENT_ID_NUMBER_LABEL, dataArgs));
        }

        // get the journal manager and write the journal string
        JournalManagerIfc journal =
            (JournalManagerIfc)Gateway.getDispatcher()
                                      .getManager(JournalManagerIfc.TYPE);
        
        if (journal != null)
        {
            String transactionID = cargo.getTransaction().getTransactionID();
            journal.journal(cargo.getStoreStatus().getStore().getStoreID(),
                        cargo.getRegister().getWorkstation().getWorkstationID(),
                        cargo.getOperator().getLoginID(), cargo.getSalesAssociate().getLoginID(),
                        transactionID, sb.toString());
        }

    }
}
