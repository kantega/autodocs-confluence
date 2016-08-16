package org.kantega.documenter.api;

public class FailedHandlerDoc {

    public final String name;
    public final String version;
    public final String failMsg;

    public FailedHandlerDoc(String name, String version, String failMsg) {
        this.name = name;
        this.version = version;
        this.failMsg = failMsg;
    }
}
