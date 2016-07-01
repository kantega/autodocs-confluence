package org.kantega.documenter.api;

public class FailedPluginDoc {

    public final String name;
    public final String version;
    public final String failMsg;


    public FailedPluginDoc(String name, String version, String failMsg) {
        this.name = name;
        this.version = version;
        this.failMsg = failMsg;
    }
}
