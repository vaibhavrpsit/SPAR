/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/email/detail/StatusAndJournalUpdatesSite.java /main/15 2012/09/12 11:57:18 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/30/12 - get journalmanager from bus
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:28 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:30:09 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:28 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:23 PM  Robert Pearse
 *
 *   Revision 1.9  2004/06/03 14:47:45  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.8  2004/05/13 14:06:52  awilliam
 *   @scr 3282 email replies do not show order number
 *
 *   Revision 1.7  2004/04/20 13:13:09  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.6  2004/04/12 18:49:35  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.5  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.4  2004/02/16 14:47:18  blj
 *   @scr 3838 - cleanup code
 *
 *   Revision 1.3  2004/02/12 16:50:11  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:48:38  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:58:48   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:25:02   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:31:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 24 2001 11:17:28   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.email.detail;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EMessageWriteDataTransaction;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.email.EmailCargo;

/**
 * This site is used to update the email status and the journal entry.
 * 
 */
@SuppressWarnings("serial")
public class StatusAndJournalUpdatesSite extends PosSiteActionAdapter
{
    /**
     * class name constant
     */
    public static final String SITENAME = "StatusAndJournalUpdatesSite";

    /**
     * Update the email status in the system and journal this status change.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {

        // Need to change Cargo type to Email Cargo for this service
        EmailCargo cargo = (EmailCargo)bus.getCargo();
        EMessageIfc message = cargo.getSelectedMessage(); // original

        EMessageIfc replyMessage =
            DomainGateway.getFactory().getEMessageInstance();
        String replyID = new String("Reply: ");
        replyID += message.getMessageID();

        String[] recipients = new String[1];
        recipients[0] = message.getSender(); // single recipient
        replyMessage.setRecipients(recipients);

        String[] sender = message.getRecipients();
        replyMessage.setSender(sender[0]);
        replyMessage.setSubject("Re: " + message.getSubject());
        replyMessage.setMessageStatus(EMessageIfc.MESSAGE_STATUS_NEW); // new reply message ?
        replyMessage.setTimestampBegin(DomainGateway.getFactory().getEYSDateInstance());
        replyMessage.setTimestampUpdate(replyMessage.getTimestampBegin());
        replyMessage.setTimestampSent(replyMessage.getTimestampBegin());
        replyMessage.setMessageID(replyID);
        replyMessage.setCustomerID(message.getCustomerID());
        replyMessage.setCustomerName(message.getCustomerName());
        replyMessage.setOrderID(cargo.getOrderID());

        // build reply message text
        StringBuilder reply = new StringBuilder();

        // build original emessage pieces
        reply.append("\n\nOriginal Message:\n");
        reply.append("Order Number: ").append(message.getOrderID())
            .append("\nCustomer: ").append(message.getCustomerName())
            .append("\nDate: ").append(message.getTimestampSent())
            .append("\nCustomer Email: ").append(message.getSender())
            .append("\n\nTo: ").append(message.getRecipients()[0])
            .append("\nSubject: ").append(message.getSubject())
            .append("\n").append(message.getMessageText());

        // add reply message text
        String replyText = new String(cargo.getReplyMessage());
        replyText += reply.toString();
        replyMessage.setMessageText(replyText); // set long reply message

        // send the newly created reply email
        EMessageWriteDataTransaction messageTransaction = null;
        messageTransaction = (EMessageWriteDataTransaction) DataTransactionFactory.create(DataTransactionKeys.EMESSAGE_WRITE_DATA_TRANSACTION);

        try
        {
            messageTransaction.sendEMessage(replyMessage);
        }
        catch(DataException e)
        {
            // failed to send EMessage
            logger.error( "Unable to send emessage " + e.getMessage());
        }

        String oldStatus = EMessageIfc.MESSAGE_STATUS_DESCRIPTORS[message.getMessageStatus()];
        // update the status of the original emessage in the system
        message.setMessageStatus(EMessageIfc.MESSAGE_STATUS_REPLIED);
        // udpate the time status changed on the original emessage
        message.setTimestampUpdate();   // now

        // save emessage in the database
        try
        {
            messageTransaction.updateEMessage(message);
        }
        catch(DataException e)
        {
            // failed to update EMessage
            logger.error( "Unable to update emessage " + e.getMessage());
        }

        /*
         * Log a journal entry
         */
        JournalManagerIfc journalManager = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        if (journalManager != null)
        {
            StringBuilder sb = new StringBuilder();
            Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
            DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
            Date now = new Date();
            String dateTimeString = dateTimeService.formatDate(now, journalLocale, DateFormat.SHORT) +
                                   dateTimeService.formatTime(now, journalLocale, DateFormat.SHORT);

            Object[] dataArgs = new Object[2];
            sb.append(Util.EOL).append(dateTimeString).append(Util.EOL);
            dataArgs[0] = cargo.getOperator().getEmployeeID();
            sb
                .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
        				JournalConstantsIfc.CASHIER_LABEL, dataArgs));
            sb.append(Util.EOL);
            sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.EMAIL_REPLY_LABEL, dataArgs));
                sb.append(Util.EOL);
                dataArgs[0] = message.getOrderID();
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORDER_NUMBER_LABEL, dataArgs));
                dataArgs[0] = message.getCustomerName();
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CUSTOMER_NAME_LABEL, dataArgs));
                dataArgs[0] = oldStatus;
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OLD_EMAIL_STATUS_LABEL, dataArgs));
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NEW_EMAIL_STATUS_LABEL, dataArgs));
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,"JournalEntry."+EMessageIfc.MESSAGE_STATUS_DESCRIPTORS[
                                                               message.getMessageStatus()]+ "Label", null));
                sb.append(Util.EOL);

            //actually write the journal
            journalManager.journal(cargo.getOperator().getEmployeeID(),
                                   null, sb.toString());
        }
        else
        {
            logger.warn( "No journal manager found!");
        }

        Letter letter = new Letter(CommonLetterIfc.SUCCESS);
        bus.mail(letter, BusIfc.CURRENT);
    }

    /**
     * Method to default display string function.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class: " + SITENAME + " (Revision " + getRevisionNumber() + ")" + hashCode());

        // pass back result
        return (strResult);
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}
