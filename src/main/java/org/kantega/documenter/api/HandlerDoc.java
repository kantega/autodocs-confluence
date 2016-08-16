package org.kantega.documenter.api;

import fj.data.Either;
import fj.data.List;

public class HandlerDoc {

    public final String version;
    public final String label;
    public final List<Either<FailedPluginDoc,PluginDoc>> pluginDocs;

    public HandlerDoc(String version, String label, List<Either<FailedPluginDoc,PluginDoc>> pluginDocs) {
        this.version = version;
        this.label = label;
        this.pluginDocs = pluginDocs;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HandlerDoc{");
        sb.append("version='").append(version).append('\'');
        sb.append(", label='").append(label).append('\'');
        sb.append(", pluginDocs=").append(pluginDocs);
        sb.append('}');
        return sb.toString();
    }
}
