
package max.retail.stores.pos.services.tender.creditdebit;


public interface MAXPineLabTransactionConstantsIfc
{
	public static final String COMMA = ",";
	
	public static final int PLUTUS_SALE_TRANSACTION_TYPE = 4001;
	
	public static final int PLUTUS_VOID_TRANSACTION_TYPE = 4006;
	
	public static final int PLUTUS_LOYALTY_TRANSACTION_TYPE = 4301;
	
	public static final int PLUTUS_PHONEPE_TRANSACTION_TYPE = 5102;
	
	public static final int PLUTUS_PHONEPE_GETSTATUS = 5112;
	
	public static final int PLUTUS_UPI_TRANSACTION_TYPE = 5120;
	
	public static final int PLUTUS_UPI_GETSTATUS = 5122;
	
	public static final int PLUTUS_POINTS_REDEMPTION_TRANSACTION_TYPE = 4101;
	
	public static final int PLUTUS_POINTS_REDEMPTION_VOID = 4102;
	
	public static final int PLUTUS_POINTS_INQUIRY_TRANSACTION_TYPE = 4208;
	
	public static final int PLUTUS_EMI_TRANSACTION_TYPE = 5101;
	
	
	public static final String PLUTUS_TIMEOUT = "51000";
	
	public static final String SOCKET_IP = "127.0.0.1";
	
	public static final int SOCKET_PORT = 8082;
	
	public static final String PLUTUS_AUTH_REMARK_FIELD_RESPONSE_PROCESSED = "PROCESSED";
	
	public static final String PLUTUS_HOST_RESPONSE_FIELD_APPROVED = "APPROVED";	
	
	public static final String CHARGE_SLIP_PRINTING_PARAMETER = "PrintCreditChargeSlip";
	
	public static final String PLUTUS_OFFLINE_SALE_TXN_DIALOG_MESSAGE_KEY = "PlutusOfflineSale";
	
	public static final String PLUTUS_AUTH_FAILED_DIALOG_MESSAGE_KEY = "CreditAuthorizationDeclined";
	
	public static final String MANUAL_CREDIT_DEBIT_LINE_VOID_DIALOG_MESSAGE_KEY = "ManualCreditLineVoid";
	
	public static final String MANUAL_CREDIT_DEBIT_VOID_DIALOG_MESSAGE_KEY = "ManualCreditVoid";
	
	public static final String PLUTUS_OFFLINE_VOID_TXN_DIALOG_MESSAGE_KEY = "PlutusOfflineLineVoid";
	
	public static final String PLUTUS_OFFLINE_LOYALTY_TXN_DIALOG_MESSAGE_KEY = "PlutusOfflineLoyalty";
	
	public static final String PLUTUS_TIMEOUT_DIALOG_MESSAGE_KEY = "PlutusTimeout";
	
	public static final String PLUTUS_TIMEOUT_DURING_VOID_DIALOG_MESSAGE_KEY = "PlutusTimeoutDuringVoid";
	
	public static final String PLUTUS_LOYALTY_AUTH_FAILED_DIALOG_MESSAGE_KEY = "PlutusLoyaltyAuthorizationDeclined";
	
	public static final String PLUTUS_LOYALTY_TRANS_FAILED_DIALOG_MESSAGE_KEY = "PlutusLoyaltyTxnFailed";
	
	public static final String BANK_LIST_NOT_AVAILABLE_DIALOG_MESSAGE_KEY = "OfflineBankListNotAvailable";
	
	public static final String PLUTUS_ERROR_DIALOG_MESSAGE_KEY = "PlutusError";
	
	public static final String PLUTUS_VOID_AUTH_FAILED_DIALOG_MESSAGE_KEY = "CreditVoidAuthorizationDeclined";
	
	public static final String PLUTUS_TIMEOUT_PARAMETER = "PlutusTimeOut";
	
	public static final String ONLINE = "ONLINE";
	
	public static final String OFFLINE = "OFFLINE";
	
	public static final String CREDIT_TENDER_TYPE = "CRDT";
	
	public static final int CARD_LENGTH = 16;
	public static final String INVALID_CREDIT_AMOUNT_DIALOG_MESSAGE_KEY = "InvalidCreditDebit";	
	public static final String PLUTUS_RETRY_TIMEOUT_DIALOG_MESSAGE_KEY = "PlutusTimeoutRetry";
	
}
