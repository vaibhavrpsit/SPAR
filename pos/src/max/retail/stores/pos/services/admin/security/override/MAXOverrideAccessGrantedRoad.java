package max.retail.stores.pos.services.admin.security.override;

import java.util.Locale;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.employee.Role;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.admin.security.override.SecurityOverrideCargo;
import oracle.retail.stores.utility.I18NHelper;

public class MAXOverrideAccessGrantedRoad extends LaneActionAdapter {
  public static final String LANENAME = "OverrideAccessGrantedRoad";
  
  public void traverse(BusIfc bus) {
    SecurityOverrideCargo cargo = (SecurityOverrideCargo)bus.getCargo();
    int functionID = cargo.getAccessFunctionID();
    JournalManagerIfc journal = (JournalManagerIfc)bus.getManager("JournalManager");
    if (journal != null && cargo.getOperator() != null && cargo.getLastOperator() != null) {
      Locale journalLocale = LocaleMap.getLocale("locale_Journaling");
      StringBuffer entry = new StringBuffer();
      Object[] dataArgs = new Object[2];
      entry.append(Util.EOL);
      entry.append(Role.getFunctionTitle(journalLocale, functionID));
      entry.append(I18NHelper.getString("EJournal", "JournalEntry.SecurityOverrideGrantedLabel", null));
      entry.append(Util.EOL);
      dataArgs[0] = cargo.getLastOperator().getEmployeeID();
      entry.append(I18NHelper.getString("EJournal", "JournalEntry.ToCashierLabel", dataArgs));
      dataArgs[0] = cargo.getOperator().getEmployeeID();
      entry.append(Util.EOL);
      entry.append(I18NHelper.getString("EJournal", "JournalEntry.ByEmployeeLabel", dataArgs));
      journal.journal(entry.toString());
    } 
  }
}
