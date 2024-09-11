/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.dailyoperations.till.tillreconcile;

import java.util.Locale;

import max.retail.stores.domain.financial.MAXFinancialTotals;
import max.retail.stores.pos.manager.ifc.MAXUtilityManagerIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.TillOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile.TillReconcileCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Updates the till status, totals, operator, and closing time. Updates
    the database with this till information.
    @version $Revision: 11$
**/
//--------------------------------------------------------------------------
public class MAXUpdateStatusSite extends PosSiteActionAdapter
{

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 11$";

    //----------------------------------------------------------------------
    /**
       Updates the till status by setting the status to reconciled,
       setting the closing time, setting the operator, and updating
       the database with this tills totals.
       <P>
       @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        MAXUtilityManagerIfc utility =
          (MAXUtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        TillReconcileCargo cargo = (TillReconcileCargo) bus.getCargo();
		//Changes done for HDD Full Patch From Oracle Starts
        boolean saveTransSuccess = true;
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
//		Changes done for HDD Full Patch From Oracle Ends

        // Local references to register and till.
        RegisterIfc register = cargo.getRegister();
        TillIfc till = register.getTillByID(cargo.getTillID());

        // create close till transaction
        TillOpenCloseTransactionIfc transaction =
          DomainGateway.getFactory().getTillOpenCloseTransactionInstance();
        transaction.setTransactionType(TransactionIfc.TYPE_CLOSE_TILL);
        utility.initializeTransaction(transaction, bus, -1);
        transaction.setTimestampEnd();
        
        //save current register accountability of the register
        till.setRegisterAccountability(register.getAccountability());

        // Update current till to indicate an empty drawer
        TillIfc currentTill = register.getCurrentTill();
        if (currentTill != null) {
            String currentTillID = currentTill.getTillID();
            if (currentTillID != null)
            {
                if (currentTillID.equalsIgnoreCase(till.getTillID()))
                {
                    register.setCurrentTillID("");
                }
            }
        }

        // Mark the status of the till 
        till.setStatus(AbstractFinancialEntityIfc.STATUS_RECONCILED);
        transaction.setEndingFloatCount
          (cargo.getFloatTotals().getEndingFloatCount());
        transaction.setEndingCombinedEnteredCount
          (cargo.getTillTotals().getCombinedCount().getEntered());
        transaction.setTenderDescriptorArrayList
          (cargo.getStoreStatus().getSafeTenderTypeDescList());

        till.setCloseTime();
        till.setSignOffOperator(cargo.getOperator());

        //Change by prateek to add coupon denomination values from cargo to till totals
        if(till.getTotals() instanceof MAXFinancialTotals)
        {
        	((MAXFinancialTotals)till.getTotals()).setCouponDenominationCount(((MAXFinancialTotals)cargo.getTillTotals()).getCouponDenominationCount());
        	((MAXFinancialTotals)till.getTotals()).setAcquirerBankDetails(((MAXFinancialTotals)cargo.getTillTotals()).getAcquirerBankDetails());
        	((MAXFinancialTotals)till.getTotals()).setGiftCertificateDenomination(((MAXFinancialTotals)cargo.getTillTotals()).getGiftCertificateDenomination());
        	((MAXFinancialTotals)till.getTotals()).setCashDenomination((((MAXFinancialTotals)cargo.getTillTotals()).getCashDenomination()));
        }
        
        
        // get the Journal manager
        JournalManagerIfc jmi = (JournalManagerIfc)
          Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);

        journalTillReconcile(till, register, transaction, cargo, jmi);


        // save all common data.
        // Note: These are saved after till totals so that the totals go first into the transaction queue
        try
        {
            transaction.setRegister(register);
            transaction.setTill(till);
            transaction.setTillID(till.getTillID());
            // save the till reconcile transaction
            utility.saveTransaction(transaction);

        }
		//Changes done for HDD Full Patch From Oracle Starts
        catch (DataException e)
        {
            logger.error(e.toString());
            DialogBeanModel dialogModel = utility.createErrorDialogBeanModel(e);
            //display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
            saveTransSuccess = false;
        }

        if (saveTransSuccess)
        {
        	bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
		//Changes done for HDD Full Patch From Oracle Ends

    }

    //---------------------------------------------------------------------
    /**
       Journals the till reconcile. <P>
       @param till till object
       @param register register object
       @param transaction TillOpenCloseTransaction object
       @param cargo cargo object
       @param jmi JournalManagerIfc object
    **/
    //---------------------------------------------------------------------
    protected void journalTillReconcile(TillIfc till,
                                        RegisterIfc register,
                                        TillOpenCloseTransactionIfc transaction,
                                        TillReconcileCargo cargo,
                                        JournalManagerIfc jmi)
    {                                   // begin journalTillReconcile()
        // journal the till status
        if (jmi != null)
        {
            StringBuffer sb = new StringBuffer();
            Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
            // get pattern for baseCurrencyType
            CurrencyIfc baseCurrency =  DomainGateway.getBaseCurrencyInstance();
            
            String baseCountry =  baseCurrency.getCountryCode();

            // Default journal Title, TillID for Parameter
            // TillCloseCountFloat = 'No', 'Summary' or 'Detail'.
            sb.append("               Reconcile Till")
                .append(Util.EOL)
                .append(Util.EOL)
                .append("Till ID: ")
                .append(cargo.getTillID())
                .append(Util.EOL)
                .append("Status: ")
                .append(AbstractStatusEntityIfc.STATUS_DESCRIPTORS[till.getStatus()])
                .append(Util.EOL);

            // Additional journal output for Parameter TillCloseCountFloat
            // set to 'Summary' or 'Detail.
            if (cargo.getFloatCountType() ==
                FinancialCountIfc.COUNT_TYPE_DETAIL ||
                cargo.getFloatCountType() ==
                FinancialCountIfc.COUNT_TYPE_SUMMARY)
            {
                FinancialTotalsIfc floatFti = cargo.getFloatTotals();
                ReconcilableCountIfc floatRci =
                    floatFti.getEndingFloatCount();
                FinancialCountIfc floatFci = floatRci.getEntered();
                FinancialCountTenderItemIfc[] floatFcti =
                    floatFci.getTenderItems();
                CurrencyIfc floatCi = floatFci.getAmount().abs();

                // Journal output for
                // 'Unexpected Float Amount Accepted' condition.
                CurrencyIfc floatAmount = till.getTotals().getStartingFloatCount().getEntered().getAmount();
                if (floatCi.compareTo(floatAmount) != CurrencyIfc.EQUALS)
                {
                    sb.append(Util.EOL)
                        .append("Expected Float Amount: ")
                        .append(Util.SPACES.substring(floatAmount.getStringValue().length(), 17))
                        .append(floatAmount.toFormattedString(defaultLocale));
                }

                // journal output for Parameter
                // TillCloseFloatCount = 'Detail'.
                if (cargo.getFloatCountType() ==
                    FinancialCountIfc.COUNT_TYPE_DETAIL)
                {

                    for (int i = 0; i < floatFcti.length; i++)
                    {
                        if (floatFcti[i].isSummary() == false)
                        {
                            String desc = floatFcti[i].getDescription();
                            int num = floatFcti[i].getNumberItemsOut();
                            sb.append(Util.EOL)
                                .append(desc)
                                .append(":  ")
                                .append(num);
                        }
                    }
                }

                // journal Total Float for Parameter 'Detail', 'Summary'
                String formattedFloat = floatCi.toFormattedString(defaultLocale);
                sb.append(Util.EOL)
                    .append(Util.EOL)
                    .append("Total Float: ")
                    .append(Util.SPACES.substring(formattedFloat.length(), 27));
                
                sb.append(formattedFloat);
                

            }

            // Additional journal output for Parameter TillCount
            // set to 'Summary' or 'Detail.
            if (cargo.getTillCountType() ==
                FinancialCountIfc.COUNT_TYPE_DETAIL ||
                cargo.getTillCountType() ==
                FinancialCountIfc.COUNT_TYPE_SUMMARY)
            {
                FinancialTotalsIfc tillFti = cargo.getTillTotals();
                ReconcilableCountIfc tillRci = tillFti.getCombinedCount();
                FinancialCountIfc entered = tillRci.getEntered();
                FinancialCountIfc expected = tillRci.getExpected();
                FinancialCountTenderItemIfc[] enteredFcti = entered.getTenderItems();
                CurrencyIfc tillCi = entered.getAmount();
                
                CurrencyTypeIfc[] alternateCurrencies = DomainGateway.getAlternateCurrencyTypes();
                int currLen = 1;
                if (alternateCurrencies != null)
                {
                    currLen += alternateCurrencies.length;
                }
                String[] currNat = new String[currLen];
                currNat[0] = DomainGateway.getBaseCurrencyType().getCountryCode();
                for (int i = 1; i < currLen; i++)
                {
                    currNat[i] = alternateCurrencies[i-1].getCountryCode();
                }

                // journal output for Parameter
                // TillCloseCountTill = 'Summary'.
                if (cargo.getTillCountType() ==
                    FinancialCountIfc.COUNT_TYPE_SUMMARY)
                {
                    CurrencyIfc subTotal;

                    for (int cnt = 0; cnt < currNat.length ; cnt++)
                    {
                        subTotal = DomainGateway.getBaseCurrencyInstance();
                        String currDesc = "";
                        if (cnt > 0)
                        {
                            subTotal = DomainGateway.getAlternateCurrencyInstance(currNat[cnt]);
                            currDesc = subTotal.getDescription() + " ";
                        }
                        String currTotalDesc = subTotal.getDescription();
                        
                        sb.append(Util.EOL);
                        String totalsFlag = "no";

                        for (int i = 0; i < enteredFcti.length; i++)
                        {
                            String nat = enteredFcti[i].getAmountTotal().getCountryCode();

                            if (enteredFcti[i].isSummary()
                                && nat.equals(currNat[cnt]))
                            {
                                String desc = enteredFcti[i].getDescription();
                                String amtTotal =
                                    enteredFcti[i].getAmountTotal()
                                    .toGroupFormattedString(defaultLocale);
                              
                                
                                int spc = 28 - currDesc.length() - desc.length() - 3 - amtTotal.length();
                                sb.append(Util.EOL)
                                    .append(currDesc)
                                    .append(desc)
                                    .append(":  ")
                                    .append(Util.SPACES.substring(0, spc))
                                    .append(amtTotal);

                                subTotal = subTotal.add(enteredFcti[i]
                                                        .getAmountTotal());
                                totalsFlag = "yes";
                            }
                        }

                        if (totalsFlag == "yes")
                        {
                            String subTotalString = "0.00";
                            if(!baseCountry.equals(subTotal.getCountryCode()))
                            {
                               subTotalString = subTotal.toISOFormattedString(defaultLocale);
 
                            }
                            else
                            {
                                subTotalString = subTotal.toFormattedString(defaultLocale);
 
                            }
                            
                          
                            sb.append(Util.EOL)
                                .append(Util.EOL)
                                .append("Total Count (")
                                .append(currTotalDesc)
                                .append(")")
                                .append(Util.SPACES.substring(subTotalString.length(), 23))
                                .append(subTotalString);
                        }
                    }
                }

                // journal output for Parameter
                // TillCloseCountTill = 'Detail'.
                else if (cargo.getTillCountType() ==
                           FinancialCountIfc.COUNT_TYPE_DETAIL)
                {
                    CurrencyIfc subTotal;

                    for (int cnt = 0; cnt < currNat.length ; cnt++)
                    {
                        subTotal = DomainGateway.getBaseCurrencyInstance();
                        String currDesc = "";
                        if (cnt > 0)
                        {
                            subTotal = DomainGateway.getAlternateCurrencyInstance(currNat[cnt]);
                            currDesc = subTotal.getDescription() + " ";
                        }
                        String currTotalDesc = subTotal.getDescription();
                        
                        boolean addBlankLines = true;
                        String totalsFlag = "no";

                        boolean alternatesFound = false;
                        for (int i = 0; i < enteredFcti.length; i++)
                        {
                            String nat = enteredFcti[i].getAmountTotal().getCountryCode();

                            if (enteredFcti[i].isSummary() == false &&
                                nat.equals(currNat[cnt]) &&
                                enteredFcti[i].getSummaryDescription()
                                .endsWith("Cash"))
                            {
                                if (addBlankLines)
                                {
                                    sb.append(Util.EOL)
                                      .append(Util.EOL);
                                    addBlankLines = false;
                                }
                                // if we've found alternate currency entry,
                                // put heading
                                if (cnt > 0 &&
                                    alternatesFound == false)
                                {
                                    sb.append("Foreign Currency")
                                      .append(Util.EOL);
                                    alternatesFound = true;
                                }

                                String desc = enteredFcti[i].getDescription();
                                int amt = enteredFcti[i].getNumberItemsTotal();
                                sb.append(Util.EOL);
                               /* if (cnt > 0)
                                {
                                    sb.append(currDesc);
                                }*/
                                sb.append(desc)
                                    .append(":  ")
                                    .append(amt);
                                subTotal = subTotal.add(enteredFcti[i]
                                                        .getAmountTotal());
                                totalsFlag = "yes";
                            }
                        }

                        if (totalsFlag == "yes")
                        {
                            String subTotalString = "0.00";
                            if(!baseCountry.equals(subTotal.getCountryCode()))
                            {
                               subTotalString = subTotal.toISOFormattedString(defaultLocale);
 
                            }
                            else
                            {
                                subTotalString = subTotal.toFormattedString(defaultLocale);
 
                            }
                           

                            sb.append(Util.EOL)
                                .append(Util.EOL)
                                .append("Total ");
                            int spc = 28;
                            if (cnt > 0)
                            {
                                sb.append(currDesc);
                                spc = spc - currDesc.length();
                            }
                            sb.append("Cash: ")
                                .append(Util.SPACES.substring(subTotalString.length(), spc))
                                .append(subTotalString);
                        }
                        CurrencyIfc cashTotal = (CurrencyIfc)subTotal.clone();

                        // journal the rest of the detail.
                        addBlankLines = true;
                        totalsFlag = "no";
                        subTotal = DomainGateway.getBaseCurrencyInstance();
                        if (cnt > 0)
                        {
                            subTotal = DomainGateway.getAlternateCurrencyInstance(currNat[cnt]);
                        }

                        for (int i = 0; i < enteredFcti.length; i++)
                        {
                            String nat = enteredFcti[i].getAmountTotal().getCountryCode();


                            if (enteredFcti[i].isSummary() &&
                                nat.equals(currNat[cnt]) &&
                                !enteredFcti[i].getDescription().endsWith("Cash"))
                            {
                                if (addBlankLines)
                                {
                                    sb.append(Util.EOL);
                                    addBlankLines = false;
                                }
                                String desc = enteredFcti[i].getDescription();
                                CurrencyIfc amt = enteredFcti[i].getAmountTotal();
                                String totalDesc = desc + ": " + enteredFcti[i].getNumberItemsTotal();
                                String amtString = "0.00";
                                if(!baseCountry.equals(amt.getCountryCode()))
                                {
                                   amtString = amt.toISOFormattedString(defaultLocale);
     
                                }
                                else
                                {
                                    amtString = amt.toFormattedString(defaultLocale);
     
                                }
                                
                                
                                sb.append(Util.EOL)
                                    .append("Total ")
                                    .append(totalDesc)
                                    .append(Util.SPACES.substring(totalDesc.length() + amtString.length(), 40))
                                    .append(amtString);

                                subTotal = subTotal.add(enteredFcti[i].getAmountTotal());
                                totalsFlag = "yes";
                            }
                        }
                        if(cashTotal != null)
                        {
                            subTotal = subTotal.add(cashTotal);
                        }
                        if (totalsFlag == "yes")
                        {
                            String subTotalString = "0.00";
                            if(!baseCountry.equals(subTotal.getCountryCode()))
                            {
                               subTotalString = subTotal.toISOFormattedString(defaultLocale);
 
                            }
                            else
                            {
                                subTotalString = subTotal.toFormattedString(defaultLocale);
 
                            }
                            
                           
                            sb.append(Util.EOL)
                                .append(Util.EOL)
                                .append("Total Count (")
                                .append(currTotalDesc)
                                .append(")")
                                .append(Util.SPACES.substring(subTotalString.length(), 23))
                                .append(subTotalString);
                        }
                    }
                }
            }

            jmi.journal(cargo.getOperator().getEmployeeID(),
                        transaction.getTransactionID(),
                        sb.toString());
        }
    }                                   // end journalTillReconcile()
    
}
