package org.kantega.documenter.api;

import fj.data.Either;
import fj.data.List;
import fj.data.Validation;

public interface DocumentationLocator {

     Validation<String, List<Either<FailedHandlerDoc,HandlerDoc>>> getDocumentationFor(String mavenCoordinates);

}
