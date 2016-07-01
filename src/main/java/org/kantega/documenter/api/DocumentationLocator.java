package org.kantega.documenter.api;

import fj.data.Either;
import fj.data.List;
import fj.data.Validation;

public interface DocumentationLocator {

     Validation<String, List<Either<FailedPluginDoc,PluginDoc>>> getDocumentationFor(String mavenCoordinates);

}
