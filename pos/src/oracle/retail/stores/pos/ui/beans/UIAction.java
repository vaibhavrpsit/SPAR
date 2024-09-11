package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ui.behavior.DefaultMailAction;

public class UIAction extends AbstractAction {
  static final long serialVersionUID = 3350095717398205367L;
  
  public static final String revisionNumber = "$Revision: /main/19 $";
  
  public static final String DEFAULT_BUTTON_NAME = "ButtonPressed";
  
  public static final String KEYSTROKE = "KEYSTROKE";
  
  protected static final String NULL_BUTTON = "NullButton";
  
  protected ActionListener defaultListener = null;
  
  protected ActionListener listener;
  
  protected String actionName = null;
  
  protected String keyName = null;
  
  protected String parameterName = null;
  
  protected String buttonName = null;
  
  protected String buttonNameTag = null;
  
  protected int buttonNumber = 0;
  
  protected boolean menu;
  
  protected transient Object source;
  
  public UIAction(String actionName, String keyName, String buttonName, String buttonTag, Icon icon, boolean isEnabled, int keyEvent, ActionListener actListener) {
    setActionName(actionName);
    this.buttonNameTag = buttonTag;
    setButtonLabel(keyName, buttonName, isEnabled);
    if (icon != null) {
      putValue("Name", buttonName);
      putValue("SmallIcon", icon);
    } 
    putValue("KEYSTROKE", keyName);
    if (actListener != null) {
      this.listener = actListener;
      this.defaultListener = actListener;
    } else {
      this.defaultListener = (ActionListener)new DefaultMailAction();
      this.listener = this.defaultListener;
    } 
  }
  
  public void setActionListener(ActionListener l) {
    this.listener = l;
  }
  
  public void resetActionListener() {
    this.listener = this.defaultListener;
  }
  
  public boolean equals(Object o) {
    if (!(o instanceof UIAction))
      return false; 
    UIAction a = (UIAction)o;
    return this.buttonName.equals(a.getButtonName());
  }
  
  public void actionPerformed(ActionEvent evt) {
    ActionEvent newEvt = new ActionEvent(this, evt.getID(), getActionName(), evt.getModifiers());
    this.source = evt.getSource();
    if (this.source instanceof AbstractButton)
      for (ActionListener l : ((AbstractButton)this.source).getActionListeners()) {
        if (l != this)
          l.actionPerformed(newEvt); 
      }  
    this.listener.actionPerformed(newEvt);
  }
  
  public void setKeyName(String value) {
    setButtonLabel(value, this.buttonName, isEnabled());
  }
  
  public void setButtonName(String value) {
    setButtonLabel(this.keyName, value, isEnabled());
  }
  
  public String getButtonName() {
    return this.buttonName;
  }
  
  public void setButtonLabel(String keyText, String buttonText, boolean enabled) {
    String labelText;
    if (buttonText == null || buttonText.equals("NullButton"))
      buttonText = ""; 
    this.buttonName = buttonText;
    this.keyName = keyText;
    if (!Util.isEmpty(keyText)) {
      labelText = keyText + '\n' + buttonText;
    } else {
      labelText = buttonText;
    } 
    putValue("ShortDescription", labelText);
    setEnabled(enabled);
  }
  
  public void setKeyEvent(int value) {
    KeyStroke stroke;
    if (value == 127) {
      stroke = KeyStroke.getKeyStroke(value, 1);
    } else {
      stroke = KeyStroke.getKeyStroke(value, 0);
    } 
    putValue("KEYSTROKE", stroke);
  }
  
  public int getButtonNumber() {
    return this.buttonNumber;
  }
  
  public void setButtonNumber(int value) {
    this.buttonNumber = value;
  }
  
  public String getButtonNameTag() {
    return this.buttonNameTag;
  }
  
  public void setButtonNameTag(String value) {
    this.buttonNameTag = value;
  }
  
  public void setActionName(String value) {
    this.actionName = value;
  }
  
  public String getActionName() {
    return this.actionName;
  }
  
  public String getParameterName() {
    return this.parameterName;
  }
  
  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
  }
  
  public boolean isMenu() {
    return this.menu;
  }
  
  public void setMenu(boolean menu) {
    this.menu = menu;
  }
  
  public Object getSource() {
    return this.source;
  }
  
  public String toString() {
    StringBuilder strResult = new StringBuilder("UIAction[actionName=");
    strResult.append(getActionName());
    strResult.append(",buttonName=");
    strResult.append(getButtonName());
    strResult.append(",buttonNumber");
    strResult.append(getButtonNumber());
    strResult.append(",keyName=");
    strResult.append(this.keyName);
    strResult.append(",menu=");
    strResult.append(this.menu);
    strResult.append("]");
    return strResult.toString();
  }
  
  public String getRevisionNumber() {
    return Util.parseRevisionNumber("$Revision: /main/19 $");
  }
}
