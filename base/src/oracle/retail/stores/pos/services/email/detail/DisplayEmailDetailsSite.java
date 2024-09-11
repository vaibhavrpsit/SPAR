/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/email/detail/DisplayEmailDetailsSite.java /main/14 2012/04/25 10:25:37 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  04/24/12 - Fixes for Fortify redundant null check
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:47 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:02 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
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
 *    Rev 1.0   Aug 29 2003 15:58:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Aug 02 2002 13:35:10   jriggins
 * Replaced incorrect spec reference in a call to UtilityManagerIfc.retrieveText()
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 01 2002 15:13:38   jriggins
 * Externalized hardcoded strings and placed them in the emailText_en_US.properties bundle.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:24:56   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:31:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 12 2002 18:40:00   dfh
 * updates to disable the Reply key when email had already been replied (status replied)
 * Resolution for POS SCR-1551: CrossReach and E-Mail have a few small problems.....
 *
 *
 *    Rev 1.0   Sep 24 2001 11:17:26   MPM
 *
 * Initial revision.
 *
 *
 *    Rev 1.1   Sep 17 2001 13:07:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.email.detail;

// Java imports
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.email.EmailCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EmailDetailBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
//------------------------------------------------------------------------------
/**
    This site is used to present the user with a list email details
    to Reply to or to View.
    @version $Revision: /main/14 $
**/
//------------------------------------------------------------------------------
public class DisplayEmailDetailsSite extends PosSiteActionAdapter
{
    /**
       class name constant
    **/
    public static final String SITENAME = "DisplayEmailDetailsSite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/14 $";
    
    /**
        Email details bundle tag.
    **/
    protected static final String EMAIL_DETAILS_TEXT_TAG = "EmailDetails";
    /**
        Email details default text.
    **/
    protected static final String EMAIL_DETAILS_TEXT = "Order Number: {0}\nCustomer: {1}\nDate: {2}\nCustomer Email: {3}\n\nTo: {4}\n\nSubject: {5}\n\n{6}";
    /**
        Recipient separator bundle tag.
    **/
    protected static final String RECIPIENT_SEPARATOR_TEXT_TAG = "RecipientSeparator";
    /**
        Recipient separator default text.
    **/
    protected static final String RECIPIENT_SEPARATOR_TEXT = ", ";

    //--------------------------------------------------------------------------
    /**
       Display the email details
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // This will hold the text for the email details.
        String emailDetails = "";

        // Need to change Cargo type to Email Cargo for this service
        EmailCargo cargo = (EmailCargo)bus.getCargo();

        // get the entry which the user has selected from the email list
        EMessageIfc message = cargo.getSelectedMessage();
        NavigationButtonBeanModel  localModel = new NavigationButtonBeanModel();

        // Get the necessary objects for I18N stuff.

        UtilityManagerIfc utility =
          (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        if (message != null)
        {
            // Place the email details into a parameter array for message
            // formatting.
            Object parms[] = new Object[7];
            parms[0] = message.getOrderID();
            parms[1] = message.getCustomerName();

            EYSDate sentDate = message.getTimestampSent();
            if (sentDate != null)
              parms[2] = sentDate.toFormattedString();
            else
              parms[2] = utility.retrieveCommonText("NotApplicable", "N/A");

            parms[3] = message.getSender();

            if (message.getRecipients().length == 1) // one recipient
            {
                parms[4] = message.getRecipients()[0];
            }
            else
            {
                StringBuffer sb = new StringBuffer();
                sb.append(message.getRecipients()[0]);  // get first

                String separator =
                  utility.retrieveText("EmailDetailSpec",
                                       BundleConstantsIfc.EMAIL_BUNDLE_NAME,
                                       RECIPIENT_SEPARATOR_TEXT_TAG,
                                       RECIPIENT_SEPARATOR_TEXT);

                for (int i=1;i < message.getRecipients().length;i++ )
                {
                    sb.append(separator).append(message.getRecipients()[i]); // add rest
                }

                parms[4] = sb.toString();
            }

            parms[5] = message.getSubject();
            parms[6] = message.getMessageText();

            // Create the message using a message format pattern
            String pattern =
              utility.retrieveText("EmailDetailSpec",
                                   BundleConstantsIfc.EMAIL_BUNDLE_NAME,
                                   EMAIL_DETAILS_TEXT_TAG,
                                   EMAIL_DETAILS_TEXT);
            emailDetails = LocaleUtilities.formatComplexMessage(pattern, parms);

            // need to set the order id in the cargo for
            // the next order lookup site
            cargo.setOrderID(message.getOrderID());
        }
        else
        {
            logger.warn( "No emails available");
            // send Cancel letter, bypass ui
            Letter letter = new Letter(CommonLetterIfc.CANCEL);
            bus.mail(letter, BusIfc.CURRENT);
        }

        // set the bean model to the EmailDetailBeanModel
        EmailDetailBeanModel beanModel = new EmailDetailBeanModel();

        beanModel.setEmailDetail(emailDetails);
        cargo.setMessageDetail(emailDetails);

        // disable Reply button if this email has already been replied
        if (message != null && message.getMessageStatus() == EMessageIfc.MESSAGE_STATUS_REPLIED)
        {
            // disable Reply button
            localModel.setButtonEnabled(CommonActionsIfc.REPLY, false);
        }
        else
        {
            // enable Reply button
            localModel.setButtonEnabled(CommonActionsIfc.REPLY, true);
        }

        /*
         * Setup model information for the UI to display
         */
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        beanModel.setLocalButtonBeanModel(localModel);
        ui.showScreen(POSUIManagerIfc.EMAIL_DETAIL, beanModel);

    }
}
