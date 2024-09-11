package max.retail.stores.pos.services.tender.loyaltypoints;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

//import max.retail.stores.domain.arts.MAXPaytmDataTransaction;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomerIfc;
import max.retail.stores.domain.factory.MAXDomainObjectFactory;
import max.retail.stores.pos.services.capillary.MAXCapillaryCustomer;
import max.retail.stores.pos.services.capillary.MAXCapillaryHelperUtility;
import max.retail.stores.pos.services.customer.tic.MAXCRMSearchCustomer;
import max.retail.stores.pos.services.customer.tic.MAXWebCRMCustomerSearchUtility;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.ARTSTill;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

import org.apache.log4j.Logger;

public class MAXCustomerBalanceEnquiryAisle
  extends PosLaneActionAdapter
{
  private static final long serialVersionUID = -1033332947166897988L;
  protected static Logger logger = Logger.getLogger(MAXCustomerBalanceEnquiryAisle.class);
  private static Long CUST_ID_MIN_LIMIT = new Long("12500");
  
  public void traverse(BusIfc bus)
  {
    String letterName = "Success";
    boolean mailLetter = true;
    boolean trainingMode = false;
    boolean reentryMode = false;
    int genderCustomer = 0;
    TenderCargo cargo = (TenderCargo)bus.getCargo();
    POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager("UIManager");
    

    Vector tempPhones = cargo.getCustomer().getPhones();
    try
    {
      boolean capApiAllowed = false;
      int maximumMatches = 0;
      CustomerIfc[] customerArray = null;
      ParameterManagerIfc pm = null;
      try
      {
        pm = (ParameterManagerIfc)bus.getManager("ParameterManager");
        
        capApiAllowed = pm.getBooleanValue("IsCapillaryAPIAllowed").booleanValue();
      }
      catch (ParameterException e)
      {
        logger.info("IsCapillaryAPIAllowed parameter is not exist");
        e.printStackTrace();
      }
      trainingMode = cargo.getRegister().getWorkstation().isTrainingMode();
      
      reentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();
      if ((!capApiAllowed) || (trainingMode) || (reentryMode))
      {
      //  MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
        ARTSTill till = new ARTSTill(((TenderCargo)bus.getCargo()).getTillID(), ((TenderCargo)bus.getCargo()).getStoreStatus().getStore().getStoreID());
       // boolean dbStatus = paytmTrans.verifyDatabaseStatus(till);
      //  if (!dbStatus)
       /* {
          DialogBeanModel beanModel = new DialogBeanModel();
          beanModel.setResourceID("CRMBalnceEnquiryServerOffline");
          beanModel.setType(1);
          beanModel.setButtonLetter(0, "failureByPhoneNumber");
          uiManager.showScreen("DIALOG_TEMPLATE", beanModel);
        }*/
        CustomerIfc customer = cargo.getCustomer();
        



        MAXWebCRMCustomerSearchUtility customerSearchUtility = MAXWebCRMCustomerSearchUtility.getInstance();
        MAXCRMSearchCustomer searchCustomer = new MAXCRMSearchCustomer();
        
        logger.info("Customer is  " + customer);
        if ((customer != null) && (customer.getPrimaryPhone() != null) && (customer.getPrimaryPhone().getPhoneNumber() != null) 
        		&& (!customer.getPrimaryPhone().getPhoneNumber().equalsIgnoreCase("NOTPROVIDED")))
        {
          logger.info("Entering condition 1 : " + customer.getPrimaryPhone().getPhoneNumber());
          searchCustomer.setMobileNumber(customer.getPrimaryPhone().getPhoneNumber());
          if (((customer instanceof MAXCustomerIfc)) && (((MAXCustomerIfc)customer).getLoyaltyCardNumber() != null)
        		  && (!((MAXCustomerIfc)customer).getLoyaltyCardNumber().equals("")))
          {
            searchCustomer.setCardNumber(((MAXCustomerIfc)customer).getLoyaltyCardNumber());
          }
          else if ((customer != null) && ((customer instanceof MAXCustomerIfc)) && (((MAXCustomerIfc)customer).getMAXTICCustomer() != null))
          {
            MAXCustomerIfc crmCustomer = ((MAXCustomerIfc)customer).getMAXTICCustomer();
            if (((crmCustomer instanceof MAXTICCustomerIfc)) && (((MAXTICCustomerIfc)crmCustomer).getTICMobileNumber() != null))
            {
              searchCustomer.setMobileNumber(((MAXTICCustomerIfc)crmCustomer).getTICMobileNumber());
              logger.info("Entering condition 2.3.1 : " + ((MAXTICCustomerIfc)crmCustomer).getTICMobileNumber());
            }
            else if (((crmCustomer instanceof MAXTICCustomerIfc)) && (((MAXTICCustomerIfc)crmCustomer).getTICCustomerID() != null))
            {
              searchCustomer.setCardNumber(((MAXTICCustomerIfc)crmCustomer).getTICCustomerID());
              logger.info("Entering condition 2.3.2 : " + ((MAXTICCustomerIfc)crmCustomer).getTICCustomerID());
            }
          }
          else if ((customer != null) && (customer.getCustomerID() != null) && (!customer.getCustomerID().equalsIgnoreCase("")))
          {
            searchCustomer.setCardNumber(customer.getCustomerID());
            logger.info("Entering condition 2.1 : " + customer.getCustomerID());
          }
        }
        else if ((customer != null) && (((customer.getPrimaryPhone() != null) && ((customer.getPrimaryPhone().getPhoneNumber() == null) || (customer.getPrimaryPhone().getPhoneNumber().equals("")) || (customer.getPrimaryPhone().getPhoneNumber().equalsIgnoreCase("NOTPROVIDED")))) || (customer.getPrimaryPhone() == null)))
        {
          if ((customer != null) && ((customer instanceof MAXCustomerIfc)) && (((MAXCustomerIfc)customer).getLoyaltyCardNumber() != null) && (!((MAXCustomerIfc)customer).getLoyaltyCardNumber().equals("")))
          {
            searchCustomer.setCardNumber(((MAXCustomerIfc)customer).getLoyaltyCardNumber());
            logger.info("Entering condition 2.2 : " + ((MAXCustomerIfc)customer).getLoyaltyCardNumber());
          }
          else if ((customer != null) && ((customer instanceof MAXCustomerIfc)) && (((MAXCustomerIfc)customer).getMAXTICCustomer() != null))
          {
            MAXCustomerIfc crmCustomer = ((MAXCustomerIfc)customer).getMAXTICCustomer();
            if (((crmCustomer instanceof MAXTICCustomerIfc)) && (((MAXTICCustomerIfc)crmCustomer).getTICMobileNumber() != null))
            {
              searchCustomer.setMobileNumber(((MAXTICCustomerIfc)crmCustomer).getTICMobileNumber());
              logger.info("Entering condition 2.3.1 : " + ((MAXTICCustomerIfc)crmCustomer).getTICMobileNumber());
            }
            else if (((crmCustomer instanceof MAXTICCustomerIfc)) && (((MAXTICCustomerIfc)crmCustomer).getTICCustomerID() != null))
            {
              searchCustomer.setCardNumber(((MAXTICCustomerIfc)crmCustomer).getTICCustomerID());
              logger.info("Entering condition 2.3.2 : " + ((MAXTICCustomerIfc)crmCustomer).getTICCustomerID());
            }
          }
          else if ((customer != null) && (customer.getCustomerID() != null) && (!customer.getCustomerID().equalsIgnoreCase("")))
          {
            searchCustomer.setCardNumber(customer.getCustomerID());
            logger.info("Entering condition 2.1 : " + customer.getCustomerID());
          }
        }
        else if ((customer != null) && ((customer instanceof MAXCustomerIfc)) && (((MAXCustomerIfc)customer).getMAXTICCustomer() != null))
        {
          MAXCustomerIfc crmCustomer = ((MAXCustomerIfc)customer).getMAXTICCustomer();
          if (((crmCustomer instanceof MAXTICCustomerIfc)) && (((MAXTICCustomerIfc)crmCustomer).getTICMobileNumber() != null))
          {
            logger.info("Entering condition 3.1.1 : " + ((MAXTICCustomerIfc)crmCustomer).getTICMobileNumber());
            
            searchCustomer.setMobileNumber(((MAXTICCustomerIfc)crmCustomer).getTICMobileNumber());
          }
          else if (((crmCustomer instanceof MAXTICCustomerIfc)) && (((MAXTICCustomerIfc)crmCustomer).getTICCustomerID() != null))
          {
            searchCustomer.setCardNumber(((MAXTICCustomerIfc)crmCustomer).getTICCustomerID());
            logger.info("Entering condition 3.1.2 : " + ((MAXTICCustomerIfc)crmCustomer).getTICCustomerID());
          }
        }
        if ((cargo != null) && (cargo.getCurrentTransactionADO().getTransactionID() != null)) {
          searchCustomer.setMessageId(cargo.getCurrentTransactionADO().getTransactionID());
        }
        logger.info(" CRM Customer Request  is : " + searchCustomer);
        //customerSearchUtility.searchCRMCustomer(bus, searchCustomer);
        customerSearchUtility.searchBalanceEnquiryCRMCustomer(bus, searchCustomer);
        
        if ((searchCustomer.getResponse() != null) && (searchCustomer.getResponse().trim().equalsIgnoreCase("S")))
        {
          logger.info(" CRM Customer Response  is : " + searchCustomer);
          
          MAXDomainObjectFactory domainFactory = (MAXDomainObjectFactory)DomainGateway.getFactory();
          
          MAXTICCustomerIfc crmCustomer = domainFactory.getTICCustomerInstance();
          
          crmCustomer.setTICCustomerID(searchCustomer.getResCardNumber());
          crmCustomer.setTICEmail(searchCustomer.getEmail());
          crmCustomer.setTICMobileNumber(searchCustomer.getResMobileNumber());
          crmCustomer.setTICFirstName(searchCustomer.getCustName());
          crmCustomer.setTICLastName("");
          crmCustomer.setTICCustomerTier(searchCustomer.getCustTier());
          crmCustomer.setTICPinNumber(searchCustomer.getPincode());
          crmCustomer.setTICCustomerPoints(searchCustomer.getPointBal());
          displayCRMCustomerInfo("LOYALTY_POINTS_DETAILS_REDEEM", uiManager, "CrmCustomerLinked", crmCustomer);
          mailLetter = false;
        }
        else
        {
          mailLetter = false;
        }
      }
      else
      {
        String mobileno = "";
        PhoneIfc phone = null;
        Vector phones = cargo.getCustomer().getPhones();
        if ((phones != null) && (phones.size() > 0))
        {
          phone = (PhoneIfc)phones.elementAt(0);
          if (phone != null) {
            mobileno = phone.getPhoneNumber();
          }
        }
        MAXCapillaryHelperUtility cust = new MAXCapillaryHelperUtility();
        ArrayList customerList = null;
        HashMap request = new HashMap();
        HashMap responseMap = new HashMap();
        request.put("Customer Mobile", mobileno);
        

        MAXCustomerIfc[] capCustomerArray = null;
        responseMap = cust.lookup(request, responseMap);
          logger.info(">> capRequestForlookupCustomer ::" + request.toString());
          
          logger.info(">> capResponseForlookupCustomer ::" + responseMap.toString());
        String ItemStatusCode = "";
        String ConnResponseCode = responseMap.get("Response Code").toString();
        if (responseMap.get("ItemStatusCode") != null) {
          ItemStatusCode = responseMap.get("ItemStatusCode").toString();
        }
        if (ConnResponseCode.equals("200"))
        {
          if (ItemStatusCode.equals("1000"))
          {
            customerList = (ArrayList)responseMap.get("Customers");
            capCustomerArray = new MAXCustomerIfc[customerList.size()];
            
            MAXCapillaryCustomer capCustomer = new MAXCapillaryCustomer();
            Iterator custItr = customerList.iterator();
            int custCount = 0;
            String balancepoint = "";
            while (custItr.hasNext())
            {
              capCustomer = (MAXCapillaryCustomer)custItr.next();
              
              capCustomerArray[custCount] = ((MAXCustomerIfc)DomainGateway.getFactory().getCustomerInstance());
              
              capCustomerArray[custCount].setFirstName(capCustomer.getCustomerName());
              
              capCustomerArray[custCount].setCustomerName(capCustomer.getCustomerName());

              capCustomerArray[custCount].setCustomerTier(capCustomer.getTier());
              
              capCustomerArray[custCount].setCustomerType("T");
              if (capCustomer.getPointsAvailable() != null)
              {
                balancepoint = capCustomer.getPointsAvailable().toString();
                
                BigDecimal money = new BigDecimal(balancepoint.replaceAll(",", ""));
                
                capCustomerArray[custCount].setBalancePoint(money);
              }
              if (capCustomer.getBirthdate() != null) {
                capCustomerArray[custCount].setBirthdate(capCustomer.getBirthdate());
              }
              boolean cobrand = Boolean.getBoolean(capCustomer.getCardType());
              
              capCustomerArray[custCount].setMailPrivacy(cobrand);
              boolean Tier = Boolean.getBoolean(capCustomer.getTier());
              
              capCustomerArray[custCount].setTelephonePrivacy(Tier);
              if (capCustomer.getLastVisit() != null) {
                capCustomerArray[custCount].setLastVisit(capCustomer.getLastVisit());
              }
              if (capCustomer.getLastVisit12months() != null) {
                capCustomerArray[custCount].setLastVisit12months(capCustomer.getLastVisit12months());
              }
              if (capCustomer.getLastVisit3months() != null) {
                capCustomerArray[custCount].setLastVisit3months(capCustomer.getLastVisit3months());
              }
              if (capCustomer.getOffers() != null) {
                capCustomerArray[custCount].setCustoffers(capCustomer.getOffers());
              }
              capCustomerArray[custCount].setCapillaryCustomerSuccessResponse(true);
              if (capCustomer.getCardNumber() != null) {
                capCustomerArray[custCount].setCustomerID(capCustomer.getCardNumber());
              }
              if ((capCustomer.getGender() != null) && 
                (!capCustomer.getGender().equals(""))) {
                if (capCustomer.getGender().equalsIgnoreCase("Female")) {
                  genderCustomer = 1;
                } else if (capCustomer.getGender().equalsIgnoreCase("Male")) {
                  genderCustomer = 2;
                } else {
                  genderCustomer = 0;
                }
              }
              capCustomerArray[custCount].setGenderCode(genderCustomer);
              
              List capCustomerArraylist = new ArrayList(Arrays.asList(capCustomerArray));
              
              custCount++;
            }
          }
          else
          {
            String resMsg = "";
            if ((responseMap.get("ItemStatusMessage") != null) || (responseMap.get("ItemStatusMessage") != "")) {
              resMsg = responseMap.get("ItemStatusMessage").toString();
            } else {
              resMsg = "No Response Found by Capillary";
            }
            String[] msg = new String[1];
            msg[0] = resMsg;
            String resourceID = "CustomerNotFound";
            displayErrorMessage(resourceID, uiManager, msg);
          }
        }
        else
        {
          String resMsg = "";
          if ((responseMap.get("ItemStatusMessage") != null) || (responseMap.get("ItemStatusMessage") != "")) {
            resMsg = responseMap.get("ItemStatusMessage").toString();
          } else if ((responseMap.get("Response Message") != null) || (responseMap.get("ItemStatusMessage") != "")) {
            resMsg = responseMap.get("Response Message").toString();
          } else {
            resMsg = "No Response Found by Capillary";
          }
          String[] msg = new String[1];
          msg[0] = resMsg;
          String resourceID = "CustomerNotFound";
          displayErrorMessage(resourceID, uiManager, msg);
          return;
        }
      }
    }
    catch (Exception e)
    {
      logger.error("" + e + "");
      letterName = "failureByPhoneNumber";
    }
    cargo.getCustomer().setPhones(tempPhones);
    if (mailLetter) {
      bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }
  }
  
  private void displayErrorMessage(String resourceID, POSUIManagerIfc uiManager, String[] capCRMerrormsg)
  {
    DialogBeanModel dialogModel = new DialogBeanModel();
    dialogModel.setResourceID(resourceID);
    dialogModel.setArgs(capCRMerrormsg);
    dialogModel.setType(1);
    dialogModel.setButtonLetter(0, "failureByPhoneNumber");
    
    uiManager.showScreen("DIALOG_TEMPLATE", dialogModel);
  }
  
  private void displayCRMCustomerInfo(String resourceID, POSUIManagerIfc uiManager,
		  		String letter, MAXTICCustomerIfc crmCustomer)
  {
    DialogBeanModel dialogModel = new DialogBeanModel();
    dialogModel.setResourceID(resourceID);
    String[] msg = new String[10];
    

    UtilityIfc utility = null;
    try
    {
      utility = Utility.createInstance();
    }
    catch (ADOException adoe)
    {
      String message = "Configuration problem: could not instantiate UtilityIfc instance";
      logger.error(message, adoe);
      throw new RuntimeException(message, adoe);
    }
    String ticConvFactor = utility.getParameterValue("LoyaltyPointsConversionFactor", null);
    Double equivalentAmount = null;
    try
    {
      if (ticConvFactor != null)
      {
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        Double ticConvFactor1 = new Double(ticConvFactor);
        if (crmCustomer.getTICCustomerPoints() != null) {
          equivalentAmount = new Double(decimalFormat.format(new Double(crmCustomer.getTICCustomerPoints()).doubleValue() * 0.60D));
        }
      }
    }
    catch (NumberFormatException exception)
    {
      equivalentAmount = null;
    }
    msg[0] = "N/A";
    if (crmCustomer.getTICCustomerPoints() != null) {
      msg[1] = String.valueOf(new BigDecimal(crmCustomer.getTICCustomerPoints()).setScale(2, 4));
    }
    msg[2] = "N/A";
    msg[3] = crmCustomer.getTICCustomerTier();
    msg[4] = "N/A";
    msg[5] = "N/A";
    if (equivalentAmount != null) {
      msg[6] = String.valueOf(equivalentAmount);
    }
    String customerName = "";
    if ((crmCustomer != null) && (crmCustomer.getTICFirstName() != null)) {
      customerName = crmCustomer.getTICFirstName();
    } else {
      customerName = "N/A";
    }
    if ((crmCustomer != null) && (crmCustomer.getTICLastName() != null) && (!crmCustomer.getTICLastName().equalsIgnoreCase(""))) {
      customerName = customerName + " " + crmCustomer.getTICFirstName();
    }
    msg[7] = customerName;
    msg[8] = "";
    msg[9] = "";
    
    dialogModel.setArgs(msg);
    dialogModel.setType(0);
    dialogModel.setButtonLetter(1, "LoyaltyCustomerContinue");
    dialogModel.setButtonLetter(2, "Undo");
    uiManager.showScreen("DIALOG_TEMPLATE", dialogModel);
  }
}
