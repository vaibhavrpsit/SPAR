/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/DateTDO.java /main/13 2011/12/05 12:16:23 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
 *    3    360Commerce 1.2         3/31/2005 4:27:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:29 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.3  2004/02/12 16:48:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:23:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Dec 18 2003 19:47:04   blj
 * fixed flow issues and removed debug statements
 * 
 *    Rev 1.1   Nov 21 2003 09:07:44   epd
 * refactored
 * 
 *    Rev 1.0   Nov 20 2003 16:42:08   crain
 * Initial revision.
 * Resolution for 3421: Tender redesign
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.tdo;

import java.util.HashMap;
import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
/**
 *  
 */
public class DateTDO extends TDOAdapter
                     implements TDOUIIfc
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.tdo.TDOIfc#buildBeanModel(java.util.HashMap)
     */
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        POSBaseBeanModel model = new POSBaseBeanModel();
        PromptAndResponseModel parModel = new PromptAndResponseModel();
        parModel.setResponseTypeDate();
        
        String arg = "";
        if (attributeMap.get(TenderConstants.TENDER_TYPE) == TenderTypeEnum.GIFT_CERT)
        {
            arg = setArguments("Gift", "Gift");
        }
        else if (attributeMap.get(TenderConstants.TENDER_TYPE) == TenderTypeEnum.STORE_CREDIT)
        {
            arg = setArguments("StoreCredit", "Store Credit");
        }
        // Set the arg text in the prompt area
        parModel.setArguments(arg);
        
        //parModel.setResponseTypeDateFormat(DateDocument.MONTH_DAY);
        model.setPromptAndResponseModel(parModel);
        return model;
    }
    /* Method to hold common code for argument setup
     * @param labelText
     * @param defaultText
     */
    public String setArguments(String labelText, String defaultText)
    {
        String arg;
        UtilityManagerIfc utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        arg = utility.retrieveCommonText(labelText, defaultText);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        arg = arg.toLowerCase(locale);
        return arg;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine1(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine1(RetailTransactionADOIfc txnADO)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine2(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine2(RetailTransactionADOIfc txnADO)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
