package it.org.kantega;

import fj.data.Either;
import fj.data.List;
import fj.data.Validation;
import org.junit.Test;
import org.kantega.documenter.MavenDocumentationLocator;
import org.kantega.documenter.api.*;

import javax.xml.parsers.ParserConfigurationException;

public class MavenTest {

    @Test
    public void testCoords() throws ParserConfigurationException {
        DocumentationLocator locator = new MavenDocumentationLocator();


        final Validation<String, List<Either<FailedHandlerDoc, HandlerDoc>>> documentationFor =
          locator.getDocumentationFor("no.nte.distributions.erp:erp-handler:1.8-SNAPSHOT");

       System.out.println(documentationFor);

    }
}
