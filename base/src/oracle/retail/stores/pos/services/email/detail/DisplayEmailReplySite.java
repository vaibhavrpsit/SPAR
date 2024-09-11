/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/email/detail/DisplayEmailReplySite.java /main/14 2011/12/05 12:16:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mkochumm  02/12/09 - use default locale for dates
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:47 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:38 PM  Robert Pearse   
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
 *    Rev 1.2   Aug 01 2002 15:30:42   jriggins
 * Added call to EYSDate.toFormattedString() for the sentDate variable.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:24:58   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:31:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 24 2001 11:17:28   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.email.detail;
// Java imports
import java.util.Locale;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.email.EmailCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EmailReplyBeanModel;

//------------------------------------------------------------------------------
/**
    This site is used to present the user with email to
    read and reply to.
    @version $Revision: /main/14 $
**/
//------------------------------------------------------------------------------
public class DisplayEmailReplySite extends PosSiteActionAdapter
{

    /**
       class name constant
    **/
    public static final String SITENAME = "DisplayEmailReplySite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
       email cargo
    **/
    private EmailCargo cargo = null;

    /**
       email reply
    **/
    private EMessageIfc reply = null;

    /**
       email reply bean model
    **/
    private EmailReplyBeanModel beanModel = null;

    /**
        Email reply header bundle tag
    **/        
    protected static final String EMAIL_REPLY_HEADER_TEXT_TAG = "EmailHeader";
    /**
        Email reply header default text
    **/        
    protected static final String EMAIL_REPLY_HEADER_TEXT = "To: {0}\nFrom: {1}\nDate: {2}\n\nRe: {3}";    
    /**
        Email reply history bundle tag
    **/        
    protected static final String EMAIL_REPLY_HISTORY_TEXT_TAG = "EmailHistory";
    /**
        Email reply history default text
    **/        
    protected static final String EMAIL_REPLY_HISTORY_TEXT = "Original Message:\n{0}";    

    //--------------------------------------------------------------------------
    /**
       Display a list of emails.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // Need to change Cargo type to Email Cargo for this service
        cargo = (EmailCargo)bus.getCargo();

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // set the bean model to the EmailReplyBeanModel
        beanModel = new EmailReplyBeanModel();

        EMessageIfc message = cargo.getSelectedMessage();
        String[] recipients = message.getRecipients();

        // Place the variable data into parameter arrays for message 
        // formatting.
        Object replyHeaderParms[] = new Object[4];
        replyHeaderParms[0] = message.getSender();
        replyHeaderParms[1] = recipients[0];

        UtilityManagerIfc utility = 
          (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        EYSDate dateSent = DomainGateway.getFactory().getEYSDateInstance();
        if (dateSent != null)
          replyHeaderParms[2] = dateSent.toFormattedString(locale);
        else
          replyHeaderParms[2] = utility.retrieveCommonText("NotApplicable", "N/A");

        replyHeaderParms[3] = message.getSubject();
        
        Object replyHistoryParms[] = { cargo.getMessageDetail() };

        // Create the messages using a message format patterns        
        String replyHeaderPattern = 
          utility.retrieveText("EmailReplySpec",
                               BundleConstantsIfc.EMAIL_BUNDLE_NAME,
                               EMAIL_REPLY_HEADER_TEXT_TAG,
                               EMAIL_REPLY_HEADER_TEXT);
        String replyHistoryPattern =                                
          utility.retrieveText("EmailReplySpec",
                               BundleConstantsIfc.EMAIL_BUNDLE_NAME,
                               EMAIL_REPLY_HISTORY_TEXT_TAG,
                               EMAIL_REPLY_HISTORY_TEXT);
        String replyHeader = 
          LocaleUtilities.formatComplexMessage(replyHeaderPattern, 
                                               replyHeaderParms);                               
        String replyHistory = 
          LocaleUtilities.formatComplexMessage(replyHistoryPattern, 
                                               replyHistoryParms);                               
                                               
        
        beanModel.setEmailReplyHeader(replyHeader);
        beanModel.setEmailDetail(replyHistory);

        //Display Bean Model
        ui.showScreen(POSUIManagerIfc.EMAIL_REPLY, beanModel);

    }

    //--------------------------------------------------------------------------
    /**
       Send the reply email as you leave the site.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        POSUIManagerIfc     ui        = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        EmailReplyBeanModel beanModel = (EmailReplyBeanModel)ui.getModel(POSUIManagerIfc.EMAIL_REPLY);

        cargo.setReplyMessage(beanModel.getEmailReply());
    }

    //---------------------------------------------------------------------
    /**
       Method to default display string function. <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class: " + SITENAME + " (Revision "
                                      + getRevisionNumber() + ")" + hashCode());

        // pass back result
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }
}
