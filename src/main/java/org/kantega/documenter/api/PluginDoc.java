package org.kantega.documenter.api;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;

public class PluginDoc {

    public final String version;
    public final String label;
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PluginDoc{");
        sb.append("version='").append(version).append('\'');
        sb.append(", label='").append(label).append('\'');
        sb.append(", documentRoot=").append(documentRoot);
        sb.append('}');
        return sb.toString();
    }
}
