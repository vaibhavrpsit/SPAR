/********************************************************************************
 *   
 *	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
 *	
 *	Rev	1.0 	11-Aug-2020		Mohan.Yadav		<Comments>	
 *
 ********************************************************************************/
package max.retail.stores.domain.gstin;

import java.util.ArrayList;

public class GSTInvoice {

	private String locationGstin = null;
	private String locationName = null;
	private String returnPeriod = null;
	private String liabilityDischargeReturnPeriod = null;
	private String itcClaimReturnPeriod = null;
	private String purpose = null;
	private String autoPushOrGenerate = null;
	private String supplyType = null;
	private String irn = null;
	private String documentType = null;
	private String transactionType = null;
	private String transactionNature = null;
	private String transactionTypeDescription = null;
	private String taxpayerType = null;
	private String documentNumber = null;
	private String documentSeriesCode = null;
	private String documentDate = null;
	private String billFromGstin = null;
	private String billFromLegalName = null;
	private String billFromTradeName = null;
	private String billFromVendorCode = null;
	private String billFromAddress1 = null;
	private String billFromAddress2 = null;
	private String billFromCity = null;
	private String billFromStateCode = null;
	private String billFromPincode = null;
	private String billFromPhone = null;
	private String billFromEmail = null;
	private String dispatchFromGstin = null;
	private String dispatchFromTradeName = null;
	private String dispatchFromVendorCode = null;
	private String dispatchFromAddress1 = null;
	private String dispatchFromAddress2 = null;
	private String dispatchFromCity = null;
	private String dispatchFromStateCode = null;
	private String dispatchFromPincode = null;
	private String billToGstin = null;
	private String billToLegalName = null;
	private String billToTradeName = null;
	private String billToVendorCode = null;
	private String billToAddress1 = null;
	private String billToAddress2 = null;
	private String billToCity = null;
	private String billToStateCode = null;
	private String billToPincode = null;
	private String billToPhone = null;
	private String billToEmail = null;
	private String shipToGstin = null;
	private String shipToLegalName = null;
	private String shipToTradeName = null;
	private String shipToVendorCode = null;
	private String shipToAddress1 = null;
	private String shipToAddress2 = null;
	private String shipToCity = null;
	private String shipToStateCode = null;
	private String shipToPincode = null;
	private String paymentType = null;
	private String paymentMode = null;
	private String paymentAmount = null;
	private String advancePaidAmount = null;
	private String paymentDate = null;
	private String paymentRemarks = null;
	private String paymentTerms = null;
	private String paymentInstruction = null;
	private String payeeName = null;
	private String payeeAccountNumber = null;
	private String paymentAmountDue = null;
	private String ifsc = null;
	private String creditTransfer = null;
	private String directDebit = null;
	private String creditDays = null;
	private String creditAvailedDate = null;
	private String creditReversalDate = null;
	private String refDocumentRemarks = null;

	private String refDocumentPeriodStartDate = null;
	private String refDocumentPeriodEndDate = null;
	private String refPrecedingDocumentDetails = null;
	private String refContractDetails = null;
	private String additionalSupportingDocumentDetails = null;
	private String billNumber = null;
	private String billDate = null;
	private String portCode = null;
	private String documentCurrencyCode = null;
	private String destinationCountry = null;
	private String pos = null;
	private String documentValue = null;
	private String documentValueInForeignCurrency = null;
	private String documentValueInRoundedOffAmount = null;
	private String differentialPercentage = null;
	private String reverseCharge = null;	
	private String claimRefund = null;
	private String underIgstAct = null;
	private String refundEligibility = null;
	private String ecommerceGstin = null;
	private String tdsGstin = null;
	private String pnrOrUniqueNumber = null;
	private String availProvisionalItc = null;
	private String originalGstin = null;
	private String originalStateCode = null;
	private String originalTradeName = null;
	private String originalDocumentType = null;
	private String originalDocumentNumber = null;
	private String originalDocumentDate = null;

	private String originalReturnPeriod = null;
	private String originalTaxableValue = null;
	private String originalPortCode = null;
	private String transportDateTime = null;
	private String transporterId = null;
	private String transporterName = null;
	private String transportMode = null;
	private String distance = null;
	private String transportDocumentNumber = null;
	private String transportDocumentDate = null;
	private String vehicleNumber = null;
	private String vehicleType = null;
	private String toEmailAddresses = null;

	private String toMobileNumbers = null;
	private String jwOriginalDocumentNumber = null;
	private String jwOriginalDocumentDate = null;
	private String jwDocumentNumber = null;
	private String jwDocumentDate = null;
	private String custom1 = null;
	private String custom2 = null;
	private String custom3 = null;
	private String custom4 = null;
	private String custom5 = null;
	private String custom6 = null;
	private String custom7 = null;
	private String custom8 = null;
	private String custom9 = null;
	private String custom10 = null;
	private ArrayList<GSTInvoiceItem> items = new ArrayList<GSTInvoiceItem>();
	public String getLocationGstin() {
		return locationGstin;
	}
	public void setLocationGstin(String locationGstin) {
		this.locationGstin = locationGstin;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public String getReturnPeriod() {
		return returnPeriod;
	}
	public void setReturnPeriod(String returnPeriod) {
		this.returnPeriod = returnPeriod;
	}
	public String getLiabilityDischargeReturnPeriod() {
		return liabilityDischargeReturnPeriod;
	}
	public void setLiabilityDischargeReturnPeriod(String liabilityDischargeReturnPeriod) {
		this.liabilityDischargeReturnPeriod = liabilityDischargeReturnPeriod;
	}
	public String getItcClaimReturnPeriod() {
		return itcClaimReturnPeriod;
	}
	public void setItcClaimReturnPeriod(String itcClaimReturnPeriod) {
		this.itcClaimReturnPeriod = itcClaimReturnPeriod;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getAutoPushOrGenerate() {
		return autoPushOrGenerate;
	}
	public void setAutoPushOrGenerate(String autoPushOrGenerate) {
		this.autoPushOrGenerate = autoPushOrGenerate;
	}
	public String getSupplyType() {
		return supplyType;
	}
	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}
	public String getIrn() {
		return irn;
	}
	public void setIrn(String irn) {
		this.irn = irn;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public String getTransactionNature() {
		return transactionNature;
	}
	public void setTransactionNature(String transactionNature) {
		this.transactionNature = transactionNature;
	}
	public String getTransactionTypeDescription() {
		return transactionTypeDescription;
	}
	public void setTransactionTypeDescription(String transactionTypeDescription) {
		this.transactionTypeDescription = transactionTypeDescription;
	}
	public String getTaxpayerType() {
		return taxpayerType;
	}
	public void setTaxpayerType(String taxpayerType) {
		this.taxpayerType = taxpayerType;
	}
	public String getDocumentNumber() {
		return documentNumber;
	}
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	public String getDocumentSeriesCode() {
		return documentSeriesCode;
	}
	public void setDocumentSeriesCode(String documentSeriesCode) {
		this.documentSeriesCode = documentSeriesCode;
	}
	public String getDocumentDate() {
		return documentDate;
	}
	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}
	public String getBillFromGstin() {
		return billFromGstin;
	}
	public void setBillFromGstin(String billFromGstin) {
		this.billFromGstin = billFromGstin;
	}
	public String getBillFromLegalName() {
		return billFromLegalName;
	}
	public void setBillFromLegalName(String billFromLegalName) {
		this.billFromLegalName = billFromLegalName;
	}
	public String getBillFromTradeName() {
		return billFromTradeName;
	}
	public void setBillFromTradeName(String billFromTradeName) {
		this.billFromTradeName = billFromTradeName;
	}
	public String getBillFromVendorCode() {
		return billFromVendorCode;
	}
	public void setBillFromVendorCode(String billFromVendorCode) {
		this.billFromVendorCode = billFromVendorCode;
	}
	public String getBillFromAddress1() {
		return billFromAddress1;
	}
	public void setBillFromAddress1(String billFromAddress1) {
		this.billFromAddress1 = billFromAddress1;
	}
	public String getBillFromAddress2() {
		return billFromAddress2;
	}
	public void setBillFromAddress2(String billFromAddress2) {
		this.billFromAddress2 = billFromAddress2;
	}
	public String getBillFromCity() {
		return billFromCity;
	}
	public void setBillFromCity(String billFromCity) {
		this.billFromCity = billFromCity;
	}
	public String getBillFromStateCode() {
		return billFromStateCode;
	}
	public void setBillFromStateCode(String billFromStateCode) {
		this.billFromStateCode = billFromStateCode;
	}
	public String getBillFromPincode() {
		return billFromPincode;
	}
	public void setBillFromPincode(String billFromPincode) {
		this.billFromPincode = billFromPincode;
	}
	public String getBillFromPhone() {
		return billFromPhone;
	}
	public void setBillFromPhone(String billFromPhone) {
		this.billFromPhone = billFromPhone;
	}
	public String getBillFromEmail() {
		return billFromEmail;
	}
	public void setBillFromEmail(String billFromEmail) {
		this.billFromEmail = billFromEmail;
	}
	public String getDispatchFromGstin() {
		return dispatchFromGstin;
	}
	public void setDispatchFromGstin(String dispatchFromGstin) {
		this.dispatchFromGstin = dispatchFromGstin;
	}
	public String getDispatchFromTradeName() {
		return dispatchFromTradeName;
	}
	public void setDispatchFromTradeName(String dispatchFromTradeName) {
		this.dispatchFromTradeName = dispatchFromTradeName;
	}
	public String getDispatchFromVendorCode() {
		return dispatchFromVendorCode;
	}
	public void setDispatchFromVendorCode(String dispatchFromVendorCode) {
		this.dispatchFromVendorCode = dispatchFromVendorCode;
	}
	public String getDispatchFromAddress1() {
		return dispatchFromAddress1;
	}
	public void setDispatchFromAddress1(String dispatchFromAddress1) {
		this.dispatchFromAddress1 = dispatchFromAddress1;
	}
	public String getDispatchFromAddress2() {
		return dispatchFromAddress2;
	}
	public void setDispatchFromAddress2(String dispatchFromAddress2) {
		this.dispatchFromAddress2 = dispatchFromAddress2;
	}
	public String getDispatchFromCity() {
		return dispatchFromCity;
	}
	public void setDispatchFromCity(String dispatchFromCity) {
		this.dispatchFromCity = dispatchFromCity;
	}
	public String getDispatchFromStateCode() {
		return dispatchFromStateCode;
	}
	public void setDispatchFromStateCode(String dispatchFromStateCode) {
		this.dispatchFromStateCode = dispatchFromStateCode;
	}
	public String getDispatchFromPincode() {
		return dispatchFromPincode;
	}
	public void setDispatchFromPincode(String dispatchFromPincode) {
		this.dispatchFromPincode = dispatchFromPincode;
	}
	public String getBillToGstin() {
		return billToGstin;
	}
	public void setBillToGstin(String billToGstin) {
		this.billToGstin = billToGstin;
	}
	public String getBillToLegalName() {
		return billToLegalName;
	}
	public void setBillToLegalName(String billToLegalName) {
		this.billToLegalName = billToLegalName;
	}
	public String getBillToTradeName() {
		return billToTradeName;
	}
	public void setBillToTradeName(String billToTradeName) {
		this.billToTradeName = billToTradeName;
	}
	public String getBillToVendorCode() {
		return billToVendorCode;
	}
	public void setBillToVendorCode(String billToVendorCode) {
		this.billToVendorCode = billToVendorCode;
	}
	public String getBillToAddress1() {
		return billToAddress1;
	}
	public void setBillToAddress1(String billToAddress1) {
		this.billToAddress1 = billToAddress1;
	}
	public String getBillToAddress2() {
		return billToAddress2;
	}
	public void setBillToAddress2(String billToAddress2) {
		this.billToAddress2 = billToAddress2;
	}
	public String getBillToCity() {
		return billToCity;
	}
	public void setBillToCity(String billToCity) {
		this.billToCity = billToCity;
	}
	public String getBillToStateCode() {
		return billToStateCode;
	}
	public void setBillToStateCode(String billToStateCode) {
		this.billToStateCode = billToStateCode;
	}
	public String getBillToPincode() {
		return billToPincode;
	}
	public void setBillToPincode(String billToPincode) {
		this.billToPincode = billToPincode;
	}
	public String getBillToPhone() {
		return billToPhone;
	}
	public void setBillToPhone(String billToPhone) {
		this.billToPhone = billToPhone;
	}
	public String getBillToEmail() {
		return billToEmail;
	}
	public void setBillToEmail(String billToEmail) {
		this.billToEmail = billToEmail;
	}
	public String getShipToGstin() {
		return shipToGstin;
	}
	public void setShipToGstin(String shipToGstin) {
		this.shipToGstin = shipToGstin;
	}
	public String getShipToLegalName() {
		return shipToLegalName;
	}
	public void setShipToLegalName(String shipToLegalName) {
		this.shipToLegalName = shipToLegalName;
	}
	public String getShipToTradeName() {
		return shipToTradeName;
	}
	public void setShipToTradeName(String shipToTradeName) {
		this.shipToTradeName = shipToTradeName;
	}
	public String getShipToVendorCode() {
		return shipToVendorCode;
	}
	public void setShipToVendorCode(String shipToVendorCode) {
		this.shipToVendorCode = shipToVendorCode;
	}
	public String getShipToAddress1() {
		return shipToAddress1;
	}
	public void setShipToAddress1(String shipToAddress1) {
		this.shipToAddress1 = shipToAddress1;
	}
	public String getShipToAddress2() {
		return shipToAddress2;
	}
	public void setShipToAddress2(String shipToAddress2) {
		this.shipToAddress2 = shipToAddress2;
	}
	public String getShipToCity() {
		return shipToCity;
	}
	public void setShipToCity(String shipToCity) {
		this.shipToCity = shipToCity;
	}
	public String getShipToStateCode() {
		return shipToStateCode;
	}
	public void setShipToStateCode(String shipToStateCode) {
		this.shipToStateCode = shipToStateCode;
	}
	public String getShipToPincode() {
		return shipToPincode;
	}
	public void setShipToPincode(String shipToPincode) {
		this.shipToPincode = shipToPincode;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getPaymentAmount() {
		return paymentAmount;
	}
	public void setPaymentAmount(String paymentAmount) {
		this.paymentAmount = paymentAmount;
	}
	public String getAdvancePaidAmount() {
		return advancePaidAmount;
	}
	public void setAdvancePaidAmount(String advancePaidAmount) {
		this.advancePaidAmount = advancePaidAmount;
	}
	public String getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getPaymentRemarks() {
		return paymentRemarks;
	}
	public void setPaymentRemarks(String paymentRemarks) {
		this.paymentRemarks = paymentRemarks;
	}
	public String getPaymentTerms() {
		return paymentTerms;
	}
	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}
	public String getPaymentInstruction() {
		return paymentInstruction;
	}
	public void setPaymentInstruction(String paymentInstruction) {
		this.paymentInstruction = paymentInstruction;
	}
	public String getPayeeName() {
		return payeeName;
	}
	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}
	public String getPayeeAccountNumber() {
		return payeeAccountNumber;
	}
	public void setPayeeAccountNumber(String payeeAccountNumber) {
		this.payeeAccountNumber = payeeAccountNumber;
	}
	public String getPaymentAmountDue() {
		return paymentAmountDue;
	}
	public void setPaymentAmountDue(String paymentAmountDue) {
		this.paymentAmountDue = paymentAmountDue;
	}
	public String getIfsc() {
		return ifsc;
	}
	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}
	public String getCreditTransfer() {
		return creditTransfer;
	}
	public void setCreditTransfer(String creditTransfer) {
		this.creditTransfer = creditTransfer;
	}
	public String getDirectDebit() {
		return directDebit;
	}
	public void setDirectDebit(String directDebit) {
		this.directDebit = directDebit;
	}
	public String getCreditDays() {
		return creditDays;
	}
	public void setCreditDays(String creditDays) {
		this.creditDays = creditDays;
	}
	public String getCreditAvailedDate() {
		return creditAvailedDate;
	}
	public void setCreditAvailedDate(String creditAvailedDate) {
		this.creditAvailedDate = creditAvailedDate;
	}
	public String getCreditReversalDate() {
		return creditReversalDate;
	}
	public void setCreditReversalDate(String creditReversalDate) {
		this.creditReversalDate = creditReversalDate;
	}
	public String getRefDocumentRemarks() {
		return refDocumentRemarks;
	}
	public void setRefDocumentRemarks(String refDocumentRemarks) {
		this.refDocumentRemarks = refDocumentRemarks;
	}
	public String getRefDocumentPeriodStartDate() {
		return refDocumentPeriodStartDate;
	}
	public void setRefDocumentPeriodStartDate(String refDocumentPeriodStartDate) {
		this.refDocumentPeriodStartDate = refDocumentPeriodStartDate;
	}
	public String getRefDocumentPeriodEndDate() {
		return refDocumentPeriodEndDate;
	}
	public void setRefDocumentPeriodEndDate(String refDocumentPeriodEndDate) {
		this.refDocumentPeriodEndDate = refDocumentPeriodEndDate;
	}
	public String getRefPrecedingDocumentDetails() {
		return refPrecedingDocumentDetails;
	}
	public void setRefPrecedingDocumentDetails(String refPrecedingDocumentDetails) {
		this.refPrecedingDocumentDetails = refPrecedingDocumentDetails;
	}
	public String getRefContractDetails() {
		return refContractDetails;
	}
	public void setRefContractDetails(String refContractDetails) {
		this.refContractDetails = refContractDetails;
	}
	public String getAdditionalSupportingDocumentDetails() {
		return additionalSupportingDocumentDetails;
	}
	public void setAdditionalSupportingDocumentDetails(String additionalSupportingDocumentDetails) {
		this.additionalSupportingDocumentDetails = additionalSupportingDocumentDetails;
	}
	public String getBillNumber() {
		return billNumber;
	}
	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}
	public String getBillDate() {
		return billDate;
	}
	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}
	public String getPortCode() {
		return portCode;
	}
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}
	public String getDocumentCurrencyCode() {
		return documentCurrencyCode;
	}
	public void setDocumentCurrencyCode(String documentCurrencyCode) {
		this.documentCurrencyCode = documentCurrencyCode;
	}
	public String getDestinationCountry() {
		return destinationCountry;
	}
	public void setDestinationCountry(String destinationCountry) {
		this.destinationCountry = destinationCountry;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public String getDocumentValue() {
		return documentValue;
	}
	public void setDocumentValue(String documentValue) {
		this.documentValue = documentValue;
	}
	public String getDocumentValueInForeignCurrency() {
		return documentValueInForeignCurrency;
	}
	public void setDocumentValueInForeignCurrency(String documentValueInForeignCurrency) {
		this.documentValueInForeignCurrency = documentValueInForeignCurrency;
	}
	public String getDocumentValueInRoundedOffAmount() {
		return documentValueInRoundedOffAmount;
	}
	public void setDocumentValueInRoundedOffAmount(String documentValueInRoundedOffAmount) {
		this.documentValueInRoundedOffAmount = documentValueInRoundedOffAmount;
	}
	public String getDifferentialPercentage() {
		return differentialPercentage;
	}
	public void setDifferentialPercentage(String differentialPercentage) {
		this.differentialPercentage = differentialPercentage;
	}
	public String getReverseCharge() {
		return reverseCharge;
	}
	public void setReverseCharge(String reverseCharge) {
		this.reverseCharge = reverseCharge;
	}
	public String getClaimRefund() {
		return claimRefund;
	}
	public void setClaimRefund(String claimRefund) {
		this.claimRefund = claimRefund;
	}
	public String getUnderIgstAct() {
		return underIgstAct;
	}
	public void setUnderIgstAct(String underIgstAct) {
		this.underIgstAct = underIgstAct;
	}
	public String getRefundEligibility() {
		return refundEligibility;
	}
	public void setRefundEligibility(String refundEligibility) {
		this.refundEligibility = refundEligibility;
	}
	public String getEcommerceGstin() {
		return ecommerceGstin;
	}
	public void setEcommerceGstin(String ecommerceGstin) {
		this.ecommerceGstin = ecommerceGstin;
	}
	public String getTdsGstin() {
		return tdsGstin;
	}
	public void setTdsGstin(String tdsGstin) {
		this.tdsGstin = tdsGstin;
	}
	public String getPnrOrUniqueNumber() {
		return pnrOrUniqueNumber;
	}
	public void setPnrOrUniqueNumber(String pnrOrUniqueNumber) {
		this.pnrOrUniqueNumber = pnrOrUniqueNumber;
	}
	public String getAvailProvisionalItc() {
		return availProvisionalItc;
	}
	public void setAvailProvisionalItc(String availProvisionalItc) {
		this.availProvisionalItc = availProvisionalItc;
	}
	public String getOriginalGstin() {
		return originalGstin;
	}
	public void setOriginalGstin(String originalGstin) {
		this.originalGstin = originalGstin;
	}
	public String getOriginalStateCode() {
		return originalStateCode;
	}
	public void setOriginalStateCode(String originalStateCode) {
		this.originalStateCode = originalStateCode;
	}
	public String getOriginalTradeName() {
		return originalTradeName;
	}
	public void setOriginalTradeName(String originalTradeName) {
		this.originalTradeName = originalTradeName;
	}
	public String getOriginalDocumentType() {
		return originalDocumentType;
	}
	public void setOriginalDocumentType(String originalDocumentType) {
		this.originalDocumentType = originalDocumentType;
	}
	public String getOriginalDocumentNumber() {
		return originalDocumentNumber;
	}
	public void setOriginalDocumentNumber(String originalDocumentNumber) {
		this.originalDocumentNumber = originalDocumentNumber;
	}
	public String getOriginalDocumentDate() {
		return originalDocumentDate;
	}
	public void setOriginalDocumentDate(String originalDocumentDate) {
		this.originalDocumentDate = originalDocumentDate;
	}
	public String getOriginalReturnPeriod() {
		return originalReturnPeriod;
	}
	public void setOriginalReturnPeriod(String originalReturnPeriod) {
		this.originalReturnPeriod = originalReturnPeriod;
	}
	public String getOriginalTaxableValue() {
		return originalTaxableValue;
	}
	public void setOriginalTaxableValue(String originalTaxableValue) {
		this.originalTaxableValue = originalTaxableValue;
	}
	public String getOriginalPortCode() {
		return originalPortCode;
	}
	public void setOriginalPortCode(String originalPortCode) {
		this.originalPortCode = originalPortCode;
	}
	public String getTransportDateTime() {
		return transportDateTime;
	}
	public void setTransportDateTime(String transportDateTime) {
		this.transportDateTime = transportDateTime;
	}
	public String getTransporterId() {
		return transporterId;
	}
	public void setTransporterId(String transporterId) {
		this.transporterId = transporterId;
	}
	public String getTransporterName() {
		return transporterName;
	}
	public void setTransporterName(String transporterName) {
		this.transporterName = transporterName;
	}
	public String getTransportMode() {
		return transportMode;
	}
	public void setTransportMode(String transportMode) {
		this.transportMode = transportMode;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getTransportDocumentNumber() {
		return transportDocumentNumber;
	}
	public void setTransportDocumentNumber(String transportDocumentNumber) {
		this.transportDocumentNumber = transportDocumentNumber;
	}
	public String getTransportDocumentDate() {
		return transportDocumentDate;
	}
	public void setTransportDocumentDate(String transportDocumentDate) {
		this.transportDocumentDate = transportDocumentDate;
	}
	public String getVehicleNumber() {
		return vehicleNumber;
	}
	public void setVehicleNumber(String vehicleNumber) {
		this.vehicleNumber = vehicleNumber;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public String getToEmailAddresses() {
		return toEmailAddresses;
	}
	public void setToEmailAddresses(String toEmailAddresses) {
		this.toEmailAddresses = toEmailAddresses;
	}
	public String getToMobileNumbers() {
		return toMobileNumbers;
	}
	public void setToMobileNumbers(String toMobileNumbers) {
		this.toMobileNumbers = toMobileNumbers;
	}
	public String getJwOriginalDocumentNumber() {
		return jwOriginalDocumentNumber;
	}
	public void setJwOriginalDocumentNumber(String jwOriginalDocumentNumber) {
		this.jwOriginalDocumentNumber = jwOriginalDocumentNumber;
	}
	public String getJwOriginalDocumentDate() {
		return jwOriginalDocumentDate;
	}
	public void setJwOriginalDocumentDate(String jwOriginalDocumentDate) {
		this.jwOriginalDocumentDate = jwOriginalDocumentDate;
	}
	public String getJwDocumentNumber() {
		return jwDocumentNumber;
	}
	public void setJwDocumentNumber(String jwDocumentNumber) {
		this.jwDocumentNumber = jwDocumentNumber;
	}
	public String getJwDocumentDate() {
		return jwDocumentDate;
	}
	public void setJwDocumentDate(String jwDocumentDate) {
		this.jwDocumentDate = jwDocumentDate;
	}
	public String getCustom1() {
		return custom1;
	}
	public void setCustom1(String custom1) {
		this.custom1 = custom1;
	}
	public String getCustom2() {
		return custom2;
	}
	public void setCustom2(String custom2) {
		this.custom2 = custom2;
	}
	public String getCustom3() {
		return custom3;
	}
	public void setCustom3(String custom3) {
		this.custom3 = custom3;
	}
	public String getCustom4() {
		return custom4;
	}
	public void setCustom4(String custom4) {
		this.custom4 = custom4;
	}
	public String getCustom5() {
		return custom5;
	}
	public void setCustom5(String custom5) {
		this.custom5 = custom5;
	}
	public String getCustom6() {
		return custom6;
	}
	public void setCustom6(String custom6) {
		this.custom6 = custom6;
	}
	public String getCustom7() {
		return custom7;
	}
	public void setCustom7(String custom7) {
		this.custom7 = custom7;
	}
	public String getCustom8() {
		return custom8;
	}
	public void setCustom8(String custom8) {
		this.custom8 = custom8;
	}
	public String getCustom9() {
		return custom9;
	}
	public void setCustom9(String custom9) {
		this.custom9 = custom9;
	}
	public String getCustom10() {
		return custom10;
	}
	public void setCustom10(String custom10) {
		this.custom10 = custom10;
	}
	public ArrayList<GSTInvoiceItem> getItems() {
		return items;
	}
	public void setItems(ArrayList<GSTInvoiceItem> items) {
		this.items = items;
	}
}
