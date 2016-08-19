package org.kantega.documenter.api;

public class ResolvedResource<A> {
    public final String artifactId;
    public final String version;
    public final A resource;


    public ResolvedResource(String artifactId, String version, A resource) {
        this.artifactId = artifactId;
        this.version = version;
        this.resource = resource;
    }

    public <B> ResolvedResource<B> withResource(B b){
        return new ResolvedResource<>(artifactId,version,b);
    }
}
