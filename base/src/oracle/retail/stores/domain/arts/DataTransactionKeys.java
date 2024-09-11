/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/DataTransactionKeys.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  06/16/14 - CAE order summary enhancement phase I
 *    abondala  05/14/14 - notifications requirement
 *    jswan     06/19/13 - Modified to perform the status update of an Order in
 *                         the context of a transaction.
 *    blarsen   02/28/13 - Removed JPA Mobile Register Profile feature. Too
 *                         many complicating factors around file/param pushes
 *                         and dynamic register definition.
 *    mkutiana  02/18/13 - Added Currency Rounding Rule Data Transaction key
 *    blarsen   01/28/13 - Added key for Mobile Register Profile Transaction.
 *    sgu       01/22/13 - calling getOrderHistory api for order summary report
 *    yiqzhao   01/16/13 - Add new key STORE_ORDER_READ_DATA_TRANSACTION for
 *                         reading order by status or date range from store
 *                         database.
 *    acadar    08/10/12 - changes to read customer groups
 *    acadar    08/09/12 - changes for XC
 *    abondala  08/08/12 - updted related to discount rules
 *    jkoppolu  03/01/11 - Added scan sheet Transaction
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *
 * ===========================================================================
 * $Log:
 *    11   360Commerce 1.10        11/9/2006 7:28:30 PM   Jack G. Swan
 *         Modifided for XML Data Replication and CTR.
 *    10   360Commerce 1.9         10/26/2006 4:06:13 PM  Gennady Ioffe
 *         Report Removal: removed 6.x Post-Processor
 *    9    360Commerce 1.8         6/19/2006 5:23:59 PM   Brendan W. Farrell
 *         UDM fixes.
 *    8    360Commerce 1.7         6/13/2006 4:12:02 PM   Brett J. Larsen CR
 *         18490 - UDM - removal of TaxAuthorityPostalCode &
 *         TaxAuthorityProvince
 *    7    360Commerce 1.6         6/12/2006 1:56:29 PM   Brett J. Larsen CR
 *         18490 - UDM - CO_RPT & CO_RPT_PARM tables dropped
 *    6    360Commerce 1.5         5/5/2006 10:04:07 AM   Brendan W. Farrell
 *         Fix unit test again.
 *    5    360Commerce 1.4         5/3/2006 3:35:56 PM    Brendan W. Farrell
 *         Remove inventory fix.
 *    4    360Commerce 1.3         5/2/2006 10:17:32 AM   Brendan W. Farrell
 *         Fix problems with inventory removal.
 *    3    360Commerce 1.2         3/31/2005 4:27:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:48 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:28 PM  Robert Pearse
 *
 *   Revision 1.8.4.1  2004/11/09 20:05:04  cdb
 *   @scr 4166 Removed reference to non-existant class added in Trunk but not Branch.
 *
 *   Revision 1.8  2004/06/10 14:21:29  jdeleau
 *   @scr 2775 Use the new tax data for the tax flat files
 *
 *   Revision 1.7  2004/04/20 12:58:32  tmorris
 *   @scr 4332 -Added definition
 *
 *   Revision 1.6  2004/04/19 15:52:00  tmorris
 *   @scr 4332 -Added definition
 *
 *   Revision 1.5  2004/04/17 19:50:28  tmorris
 *   @scr 4332 -Added definition
 *
 *   Revision 1.4  2004/04/16 15:58:12  tmorris
 *   @scr 4332 -Added definition
 *
 *   Revision 1.3  2004/04/15 22:20:39  tmorris
 *   @scr 4332 -Added definition
 *
 *   Revision 1.2  2004/04/15 16:32:15  tmorris
 *   @scr 4332 -Added new definitions
 *
 *   Revision 1.1  2004/04/01 20:07:37  epd
 *   @scr 4243 Updates for new Database Transaction Factory
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

/**
 * This class defines the keys to every DataTransactionIfc implementation to be
 * used by the DataTransactionFactory.
 */
public interface DataTransactionKeys
{
    ////////////////////////////////////////////////////////////////////////////
    // Put all defintions here
    // PLEASE KEEP THIS LIST IN ALPHABETICAL ORDER.  THANK YOU
    public static final String ADVANCED_INQUIRY_DATA_TRANSACTION            = "persistence_AdvancedInquiryDataTransaction";
    public static final String ADVANCED_PRICING_RULE_KEY_DATA_TRANSACTION   = "persistence_AdvancedPricingRuleKeyDataTransaction";
    public static final String ALERT_DATA_TRANSACTION                       = "persistence_AlertDataTransaction";
    public static final String AUDIT_LOG_TRANSACTION                        = "persistence_AuditLogTransaction";
    public static final String BUILD_FLAT_FILE_TRANSACTION                  = "persistence_BuildFlatFileTransaction";
    public static final String BUSINESS_READ_DATA_TRANSACTION               = "persistence_BusinessReadDataTransaction";
    public static final String CALENDAR_DATA_TRANSACTION                    = "persistence_CalendarDataTransaction";
    public static final String CERTIFICATE_TRANSACTION                      = "persistence_CertificateTransaction";
    public static final String CODE_LIST_DATA_TRANSACTION                   = "persistence_CodeListDataTransaction";
    public static final String CODE_LIST_SAVE_DATA_TRANSACTION              = "persistence_CodeListSaveDataTransaction";
    public static final String CONFIRMED_CLOCK_ENTRY_TRANSACTION            = "persistence_ConfirmedClockEntryTransaction";
    public static final String CURRENCY_DATA_TRANSACTION                    = "persistence_CurrencyDataTransaction";
    public static final String CURRENCY_ROUNDING_RULE_DATA_TRANSACTION      = "persistence_CurrencyRoundingRuleDataTransaction";
    public static final String CUSTOMER_READ_DATA_TRANSACTION               = "persistence_CustomerReadDataTransaction";
    public static final String CUSTOMER_READ_PRICING_GROUP_TRANSACTION      = "persistence_CustomerReadPricingGroupTransaction";
    public static final String CUSTOMER_WRITE_DATA_TRANSACTION              = "persistence_CustomerWriteDataTransaction";

    public static final String CUSTOMER_READ_CUSTOMER_GROUPS_DATA_TRANSACTION
                                                                            = "persistence_CustomerReadCustomerGroupsDataTransaction";
    public static final String DATABASE_PURGE_TRANSACTION                   = "persistence_DatabasePurgeTransaction";
    public static final String DATAREPLICATION_DATA_TRANSACTION             = "persistence_DataReplicationDataTransaction";
    public static final String EMESSAGE_READ_DATA_TRANSACTION               = "persistence_EMessageReadDataTransaction";
    public static final String EMESSAGE_WRITE_DATA_TRANSACTION              = "persistence_EMessageWriteDataTransaction";
    public static final String EMPLOYEE_TRANSACTION                         = "persistence_EmployeeTransaction";
    public static final String EMPLOYEE_FIND_FOR_UPDATE_TRANSACTION         = "persistence_EmployeeFindForUpdateTransaction";
    public static final String EMPLOYEE_WRITE_TRANSACTION                   = "persistence_EmployeeWriteTransaction";
    public static final String EMPLOYEE_WRITE_TIME_CLOCK_TRANSACTION        = "persistence_EmployeeWriteTimeClockTransaction";
    public static final String EXECUTE_SQL_DATA_TRANSACTION                 = "persistence_ExecuteSQLDataTransaction";
    public static final String FINANCIAL_TOTALS_DATA_TRANSACTION            = "persistence_FinancialTotalsDataTransaction";
    public static final String ITEM_SIZES_TRANSACTION                       = "persistence_ItemSizesTransaction";
    public static final String LAYAWAY_DATA_TRANSACTION                     = "persistence_LayawayDataTransaction";
    public static final String MERCHANDISE_HIERARCHY_DATA_TRANSACTION       = "persistence_MerchandiseHierarchyDataTransaction";
    public static final String ORDER_CREATE_DATA_TRANSACTION                = "persistence_OrderCreateDataTransaction";
    public static final String ORDER_READ_DATA_TRANSACTION                  = "persistence_OrderReadDataTransaction";
    public static final String ORDER_SUMMARY_READ_DATA_TRANSACTION          = "persistence_OrderSummaryReadDataTransaction";
    public static final String STORE_ORDER_READ_DATA_TRANSACTION            = "persistence_StoreOrderReadDataTransaction";
    public static final String ORDER_HISTORY_READ_DATA_TRANSACTION          = "persistence_OrderHistoryReadDataTransaction";
    public static final String PARAMETER_TRANSACTION                        = "persistence_ParameterTransaction";
    public static final String PLU_TRANSACTION                              = "persistence_PLUTransaction";
    public static final String READ_SHIPPING_METHOD_TRANSACTION             = "persistence_ReadShippingMethodTransaction";
    public static final String READ_NEW_TAX_RULE_TRANSACTION                = "persistence_ReadNewTaxRuleTransaction";
    public static final String READ_TRANSACTIONS_BY_ID_DATA_TRANSACTION     = "persistence_ReadTransactionsByIDDataTransaction";
    public static final String READ_TRANSACTIONS_FOR_RETURN                 = "persistence_ReadTransactionsForReturn";
    public static final String REGISTRY_DATA_TRANSACTION                    = "persistence_RegistryDataTransaction";
    public static final String ROLE_TRANSACTION                             = "persistence_RoleTransaction";
    public static final String SCAN_SHEET_TRANSACTION                       = "persistence_ScanSheetTransaction";
    public static final String STORE_DATA_TRANSACTION                       = "persistence_StoreDataTransaction";
    public static final String STORE_DIRECTORY_DATA_TRANSACTION             = "persistence_StoreDirectoryDataTransaction";
    public static final String STORE_SAFE_READ_DATA_TRANSACTION             = "persistence_StoreSafeReadDataTransaction";
    public static final String STORE_SAFE_WRITE_DATA_TRANSACTION            = "persistence_StoreSafeWriteDataTransaction";
    public static final String SUPPLIER_DATA_TRANSACTION                    = "persistence_SupplierDataTransaction";
    public static final String SUPPLY_CATEGORY_TRANSACTION                  = "persistence_SupplyCategoryTransaction";
    public static final String SUPPLY_ITEM_TRANSACTION                      = "persistence_SupplyItemTransaction";
    public static final String SUPPLY_ORDER_TRANSACTION                     = "persistence_SupplyOrderTransaction";
    public static final String TENDER_DATA_TRANSACTION                      = "persistence_TenderDataTransaction";
    public static final String TILL_CHECKS_DATA_TRANSACTION                 = "persistence_TillChecksDataTransaction";
    public static final String TRANSACTION_HISTORY_DATA_TRANSACTION         = "persistence_TransactionHistoryDataTransaction";
    public static final String TRANSACTION_LIST_PRICE_MAINTENANCE_EVENTS    = "persistence_TransactionListPriceMaintenanceEvents";
    public static final String TRANSACTION_READ_DATA_TRANSACTION            = "persistence_TransactionReadDataTransaction";
    public static final String TRANSACTION_READ_ITEM_COST                   = "persistence_TransactionReadItemCost";
    public static final String TRANSACTION_VERIFICATION_DATA_TRANSACTION    = "persistence_TransactionVerificationDataTransaction";
    public static final String TRANSACTION_WRITE_DATA_TRANSACTION           = "persistence_TransactionWriteDataTransaction";
    public static final String TRANSACTION_WRITE_NOT_QUEUED_DATA_TRANSACTION = "persistence_TransactionWriteNotQueuedDataTransaction";
    public static final String UPDATE_RETURNED_ITEMS_COMMAND_DATA_TRANSACTION = "persistence_UpdateReturnedItemsCommandDataTransaction";
    public static final String UPDATE_RETURNED_ITEMS_DATA_TRANSACTION       = "persistence_UpdateReturnedItemsDataTransaction";
    public static final String WORK_WEEK_TRANSACTION                        = "persistence_WorkWeekTransaction";
    // PLEASE KEEP THIS LIST IN ALPHABETICAL ORDER.  THANK YOU
    ////////////////////////////////////////////////////////////////////////////
}
