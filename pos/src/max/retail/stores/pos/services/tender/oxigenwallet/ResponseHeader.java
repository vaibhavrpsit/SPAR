
package max.retail.stores.pos.services.tender.oxigenwallet;

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
    "traceId",
    "responseCode",
    "responseMsg",
    "responseTimestamp",
    "exception"
})
@Generated("jsonschema")
public class ResponseHeader {

    @JsonProperty("traceId")
    private String traceId;
    @JsonProperty("responseCode")
    private String responseCode;
    @JsonProperty("responseMsg")
    private String responseMsg;
    @JsonProperty("responseTimestamp")
    private String responseTimestamp;
    @JsonProperty("exception")
    private Object exception;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("traceId")
    public String getTraceId() {
        return traceId;
    }

    @JsonProperty("traceId")
    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @JsonProperty("responseCode")
    public String getResponseCode() {
        return responseCode;
    }

    @JsonProperty("responseCode")
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @JsonProperty("responseMsg")
    public String getResponseMsg() {
        return responseMsg;
    }

    @JsonProperty("responseMsg")
    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    @JsonProperty("responseTimestamp")
    public String getResponseTimestamp() {
        return responseTimestamp;
    }

    @JsonProperty("responseTimestamp")
    public void setResponseTimestamp(String responseTimestamp) {
        this.responseTimestamp = responseTimestamp;
    }

    @JsonProperty("exception")
    public Object getException() {
        return exception;
    }

    @JsonProperty("exception")
    public void setException(Object exception) {
        this.exception = exception;
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
