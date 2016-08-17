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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FailedHandlerDoc{");
        sb.append("name='").append(name).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", failMsg='").append(failMsg).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
