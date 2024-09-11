/* =============================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/RMRefundOptionsTDO.java /main/4 2014/06/10 18:41:51 abananan Exp $
 * =============================================================================
 * NOTES
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  02/14/14 - fix the RM refund tender options
 *    abondala  10/07/13 - added missing RM tender types
 *    jswan     11/15/12 - Added to support parameter controlled return
 *                         tenders.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.tdo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.domain.manager.rm.RPIItemResponse;
import oracle.retail.stores.domain.manager.rm.RPIResponseIfc;
import oracle.retail.stores.domain.manager.rm.RPITenderType;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderTypeMap;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.TenderBeanModel;

import org.apache.log4j.Logger;

/**
 *
 */
public class RMRefundOptionsTDO extends RefundOptionsTDO implements TDOUIIfc
{
    /**
    The logger to which log messages will be sent.
     **/
    protected static Logger logger = Logger.getLogger(RMRefundOptionsTDO.class);


    /* (non-Javadoc)
     * @see oracle.retail.stores.tdo.TDOIfc#buildBeanModel(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        // get new tender bean model
        RetailTransactionADOIfc txnADO = (RetailTransactionADOIfc)attributeMap.get(TRANSACTION);
        BusIfc bus = (BusIfc)attributeMap.get(BUS);
        TenderBeanModel model = initializeTenderBeanModel(attributeMap, txnADO, bus);

        // set the local navigation button bean model
        RPIResponseIfc returnResponse = (RPIResponseIfc)attributeMap.get(RETURN_RESPONSE);
        TenderTypeEnum[] tenderTypes = getEnabledRmRefundOptions(returnResponse);
        model.setLocalButtonBeanModel(getRefundNavigationBeanModel(tenderTypes));
        model = setCorrectPromptAndButtonsForRmRefund(model, tenderTypes, bus);

        // This is a return
        model.setReturn(true);

        return model;
    }

    //----------------------------------------------------------------------
    /**
     * Given the internal state of this transaction return an array of tenders which are valid for accepting as a
     * refund tenders.
     *
     * @return @see oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc#getEnabledRefundOptions()
     */
    //----------------------------------------------------------------------
    public TenderTypeEnum[] getEnabledRmRefundOptions(RPIResponseIfc rpiReturnResponse)
    {
        List<RPIItemResponse> itemResponses = rpiReturnResponse.getItemRefundResponse();
        List<SelectedTenderType> selectedTenderTypes = new ArrayList<SelectedTenderType>();

        Iterator<RPIItemResponse> iter = itemResponses.iterator();
        while ( iter.hasNext() )
        {
            RPIItemResponse rpiItemResponse = iter.next();
            List<RPITenderType> returnTenderTypes = rpiItemResponse.getRefundTenderTypes();
            //use the original transaction's tender types
            Iterator<RPITenderType> tenderTypeIter = returnTenderTypes.iterator();
            while ( tenderTypeIter.hasNext() )
            {
                RPITenderType rpiTenderType = tenderTypeIter.next();
                String tenderTypeCode = rpiTenderType.getType();
                TenderTypeEnum tenderTypeEnum = mapTOTenderTypeEnum(tenderTypeCode);
                SelectedTenderType selectedTenderType = getSelectedTenderType(selectedTenderTypes, tenderTypeEnum);
                if ( selectedTenderType == null  )
                {
                    //first time find the tender type as a refund tender
                    selectedTenderType = new SelectedTenderType();
                    selectedTenderType.setTenderType(tenderTypeEnum);
                    selectedTenderType.setSelected(1);
                }
                else
                {
                    //find one more time.
                    selectedTenderType.setSelected(selectedTenderType.getSelected()+1);
                }
                selectedTenderTypes.add(selectedTenderType);
             }
        }
        List<TenderTypeEnum> tenderList = new ArrayList<TenderTypeEnum>(6);
        Iterator<SelectedTenderType> selectedTenderIter = selectedTenderTypes.iterator();
        while ( selectedTenderIter.hasNext() )
        {
          SelectedTenderType selectedTenderType = selectedTenderIter.next();
          tenderList.add(selectedTenderType.getTenderType());
        }

        // convert list to array
        TenderTypeEnum[] tenderTypeArray = new TenderTypeEnum[tenderList.size()];
        tenderTypeArray = tenderList.toArray(tenderTypeArray);
        return tenderTypeArray;
    }

    /**
     * This method sets the correct buttons and the prompt.
     *
     * @param model
     * @param txnADO
     * @param bus
     * @return
     */
    protected TenderBeanModel setCorrectPromptAndButtonsForRmRefund(TenderBeanModel model, TenderTypeEnum[] tenderTypes, BusIfc bus)
    {
        PromptAndResponseModel prModel = model.getPromptAndResponseModel();
        // if we havent created one yet, then make a new one
        if (prModel == null)
        {
            prModel = new PromptAndResponseModel();
        }

        UtilityManagerIfc util = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // get the correct prompt args based on the refund options row
        // this row corresponds to the refund options requirements for button availability and prompt text
        if (tenderTypes != null)
        {
            prModel.setArguments(util.retrieveText("RefundOptionsSpec", "tenderText", "EnterAmountAndChoose", "EnterAmountAndChoose"));
        }

        // set prompt and response
        model.setPromptAndResponseModel(prModel);
        return model;
    }

    protected NavigationButtonBeanModel getRefundNavigationBeanModel(TenderTypeEnum[] enabledTypes)
    {
        // convert to list
        List<TenderTypeEnum> typeList = new ArrayList<TenderTypeEnum>(enabledTypes.length);
        boolean isManagerOverrideEnabled = false;

        for (int i = 0; i < enabledTypes.length; i++)
        {
            typeList.add(enabledTypes[i]);
        }

        try
        {
          UtilityIfc utility = Utility.createInstance();

          String[] paramValues = utility.getParameterValueList("ManagerOverrideForSecurityAccess");

          for(int ctr = 0 ; ctr < paramValues.length; ctr++)
          {
            if(paramValues[ctr].equals("RefundTenderOverride"))
            {
              isManagerOverrideEnabled = true;
              break;
            }

          }

        }
        catch (ADOException e)
        {
          isManagerOverrideEnabled = true;
        }

        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        navModel.setButtonEnabled(CommonActionsIfc.CASH, typeList.contains(TenderTypeEnum.CASH));
        navModel.setButtonEnabled(CommonActionsIfc.MAIL_CHECK, typeList.contains(TenderTypeEnum.MAIL_CHECK));
        navModel.setButtonEnabled(CommonActionsIfc.CREDIT, typeList.contains(TenderTypeEnum.CREDIT));
        navModel.setButtonEnabled(CommonActionsIfc.DEBIT, typeList.contains(TenderTypeEnum.DEBIT));
        navModel.setButtonEnabled(CommonActionsIfc.GIFT_CARD, typeList.contains(TenderTypeEnum.GIFT_CARD));
        navModel.setButtonEnabled(CommonActionsIfc.STORE_CREDIT, typeList.contains(TenderTypeEnum.STORE_CREDIT));
        navModel.setButtonEnabled(CommonActionsIfc.MANAGER_OVERRIDE, isManagerOverrideEnabled);
        return navModel;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine1(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine1(RetailTransactionADOIfc txnADO)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine2(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     */
    public String formatPoleDisplayLine2(RetailTransactionADOIfc txnADO)
    {
        return null;
    }
    
    protected TenderTypeEnum mapTOTenderTypeEnum(String tenderTypeCode)
    {
        int type = TenderTypeMap.getTenderTypeMap().getTypeFromCode(tenderTypeCode);

        TenderTypeEnum result = null;

        switch (type)
        {
            case TenderLineItemIfc.TENDER_TYPE_CASH:
            {
                result = TenderTypeEnum.CASH;
                break;
            }
            case TenderLineItemIfc.TENDER_TYPE_CHARGE:
            {
                result = TenderTypeEnum.CREDIT;
                break;
            }
            case TenderLineItemIfc.TENDER_TYPE_MAIL_BANK_CHECK:
            case TenderLineItemIfc.TENDER_TYPE_CHECK:
            {
                result =  TenderTypeEnum.MAIL_CHECK;
                break;
            }
            case TenderLineItemIfc.TENDER_TYPE_DEBIT:
            {
                result =  TenderTypeEnum.DEBIT;
                break;
            } 
            case TenderLineItemIfc.TENDER_TYPE_GIFT_CARD:
            {
                result =  TenderTypeEnum.GIFT_CARD;
                break;
            } 
            case TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT:
            {
                result =  TenderTypeEnum.STORE_CREDIT;
                break;
            }
            default:
            {
                result = TenderTypeEnum.STORE_CREDIT;
            }
        }

        return result;

    }    

    /**
     *
     * @param tenderTypes
     * @param tenderTypeEnum
     * @return
     */
    protected SelectedTenderType getSelectedTenderType(List<SelectedTenderType> tenderTypes, TenderTypeEnum tenderTypeEnum)
    {
        SelectedTenderType selectedTenderType = null;
        
        if(tenderTypeEnum != null)
        {
            Iterator<SelectedTenderType> iter = tenderTypes.iterator();
            while ( iter.hasNext() )
            {
                SelectedTenderType tenderType = iter.next();
                if (tenderType.getTenderType().toString().equals(tenderTypeEnum.toString()))
                {
                    selectedTenderType = tenderType;
                    break;
                }
            }
        }
        
        return selectedTenderType;
    }

    /**
     * The inner class is for finding out the return refund tender.
     * A tender type will be selected as refund tender only when all the return items has
     * specified the tender type as a refund tender.
     *
     */
    class SelectedTenderType
    {
        protected TenderTypeEnum tenderType;

        /** number of times selected for all the refund items. */
        protected int selected;

        public SelectedTenderType() {}

        public TenderTypeEnum getTenderType() {
            return tenderType;
        }

        public void setTenderType(TenderTypeEnum tenderType) {
            this.tenderType = tenderType;
        }

        public int getSelected() {
            return selected;
        }

        public void setSelected(int selected) {
            this.selected = selected;
        }

    }
}
