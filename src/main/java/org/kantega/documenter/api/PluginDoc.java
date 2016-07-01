package org.kantega.documenter.api;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;

public class PluginDoc {

    private final String version;
    private final String label;
    public final JsonNode documentRoot;

    public PluginDoc(String version, String label, JsonNode documentRoot) {
        this.version = version;
        this.label = label;
        this.documentRoot = documentRoot;
    }

    public String getVersion() {
        return version;
    }

    public String getLabel() {
        return label;
    }

    public String getId() {
        return label +"-"+ version;
    }

    public String getSelectorId(){
        return StringUtils.replace(getId(),".","_");
    }
}
