
package max.retail.stores.pos.services.tender.oxigenwallet.debitresponse;

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
    "requestType",
    "requestId",
    "requesterTimestamp",
    "mobileNumber",
    "channel",
    "walletOwner",
    "originalDialogueTraceId"
})
@Generated("jsonschema2pojo")
public class RequestHeader {

    @JsonProperty("requestType")
    private String requestType;
    @JsonProperty("requestId")
    private String requestId;
    @JsonProperty("requesterTimestamp")
    private String requesterTimestamp;
    @JsonProperty("mobileNumber")
    private String mobileNumber;
    @JsonProperty("channel")
    private String channel;
    @JsonProperty("walletOwner")
    private String walletOwner;
    @JsonProperty("originalDialogueTraceId")
    private String originalDialogueTraceId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("requestType")
    public String getRequestType() {
        return requestType;
    }

    @JsonProperty("requestType")
    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    @JsonProperty("requestId")
    public String getRequestId() {
        return requestId;
    }

    @JsonProperty("requestId")
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @JsonProperty("requesterTimestamp")
    public String getRequesterTimestamp() {
        return requesterTimestamp;
    }

    @JsonProperty("requesterTimestamp")
    public void setRequesterTimestamp(String requesterTimestamp) {
        this.requesterTimestamp = requesterTimestamp;
    }

    @JsonProperty("mobileNumber")
    public String getMobileNumber() {
        return mobileNumber;
    }

    @JsonProperty("mobileNumber")
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @JsonProperty("channel")
    public String getChannel() {
        return channel;
    }

    @JsonProperty("channel")
    public void setChannel(String channel) {
        this.channel = channel;
    }

    @JsonProperty("walletOwner")
    public String getWalletOwner() {
        return walletOwner;
    }

    @JsonProperty("walletOwner")
    public void setWalletOwner(String walletOwner) {
        this.walletOwner = walletOwner;
    }

    @JsonProperty("originalDialogueTraceId")
    public String getOriginalDialogueTraceId() {
        return originalDialogueTraceId;
    }

    @JsonProperty("originalDialogueTraceId")
    public void setOriginalDialogueTraceId(String originalDialogueTraceId) {
        this.originalDialogueTraceId = originalDialogueTraceId;
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
