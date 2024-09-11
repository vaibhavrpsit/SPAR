/* ===========================================================================
* Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/AuthorizationPartiallyApprovedAisle.java /main/2 2012/12/10 19:16:41 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    ohorne    08/04/11 - Use DomainUtil to lookup Masking property values
 *    tksharma  07/29/11 - Moved EncryptionUtily from foundation project to
 *                         EncryptionClient
 *    blarsen   07/26/11 - Last digits not showing up in dialog. Was using
 *                         getAccountNumber(). Changed to use
 *                         getMaskedAccountNumber.
 *    cgreene   07/14/11 - fix tendering and reload gift cards
 *    cgreene   05/27/11 - move auth response objects into domain
 *    asinton   03/25/11 - Moved APF request and response objects to common
 *                         module.
 *    asinton   03/22/11 - new tender authorization service
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.utility.DomainUtil;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

import org.apache.commons.lang3.StringUtils;

/**
 * This aisle shows the authorization partially approved dialog.
 * 
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class AuthorizationPartiallyApprovedAisle extends PosLaneActionAdapter
{
  /** constant for declined dialog name */
  public static final String AUTHORIZATION_PARTIALLY_APPROVED_DIALOG = "AuthPartial";
  
  /*
   * (non-Javadoc)
   * @see
   * oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse
   * (oracle.retail.stores.foundation.tour.ifc.BusIfc)
   */
  @Override
  public void traverse(BusIfc bus)
  {
    AuthorizationCargo cargo = (AuthorizationCargo) bus.getCargo();
    AuthorizeTransferResponseIfc response = cargo.getCurrentResponse();
    String maskedCardNumber = response.getMaskedAccountNumber();

    //lookup the number of trailing unmasked digits for a card from domain.properties  
    int numberOfCardLastDigits = DomainUtil.getNumberOfCardLastDigits();

    if (maskedCardNumber != null && maskedCardNumber.length() > numberOfCardLastDigits)
    {
      maskedCardNumber = maskedCardNumber.substring(maskedCardNumber.length() - numberOfCardLastDigits);
    }
    else
    {
        // masked card number is not available, display mask char to avoid "null" or confusing message to operator
        char maskChar = DomainUtil.getMaskChar();
        maskedCardNumber = StringUtils.repeat(String.valueOf(maskChar), numberOfCardLastDigits);
    }

    String[] dialogArgs = { maskedCardNumber };
    DialogBeanModel dialogModel = new DialogBeanModel();
    dialogModel.setResourceID(AUTHORIZATION_PARTIALLY_APPROVED_DIALOG);
    dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
    dialogModel.setArgs(dialogArgs);
    dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.APPROVED);
    // display dialog
    POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
    ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
  }
  
}
