
package max.retail.stores.domain;

import java.util.HashMap;
import java.util.List;
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
    "trace",
    "code",
    "description",
    "details",
    "message",
    "target"
})
@Generated("jsonschema2pojo")
public class Exception {

    @JsonProperty("trace")
    private List<String> trace = null;
    @JsonProperty("code")
    private String code;
    @JsonProperty("description")
    private String description;
    @JsonProperty("details")
    private List<Detail> details = null;
    @JsonProperty("message")
    private Object message;
    @JsonProperty("target")
    private String target;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("trace")
    public List<String> getTrace() {
        return trace;
    }

    @JsonProperty("trace")
    public void setTrace(List<String> trace) {
        this.trace = trace;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("details")
    public List<Detail> getDetails() {
        return details;
    }

    @JsonProperty("details")
    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    @JsonProperty("message")
    public Object getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(Object message) {
        this.message = message;
    }

    @JsonProperty("target")
    public String getTarget() {
        return target;
    }

    @JsonProperty("target")
    public void setTarget(String target) {
        this.target = target;
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
