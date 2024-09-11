
package max.retail.stores.domain;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "storeCode",
    "terminalId",
    "optionalInfo"
})
@Generated("jsonschema2pojo")
public class StoreDetails {

    @JsonProperty("storeCode")
    private String storeCode;
    @JsonProperty("terminalId")
    private String terminalId;
    @JsonProperty("optionalInfo")
    private Object optionalInfo;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("storeCode")
    public String getStoreCode() {
        return storeCode;
    }

    @JsonProperty("storeCode")
    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    @JsonProperty("terminalId")
    public String getTerminalId() {
        return terminalId;
    }

    @JsonProperty("terminalId")
    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    @JsonProperty("optionalInfo")
    public Object getOptionalInfo() {
        return optionalInfo;
    }

    @JsonProperty("optionalInfo")
    public void setOptionalInfo(Object optionalInfo) {
        this.optionalInfo = optionalInfo;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
