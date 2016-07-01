package it.org.kantega;

import fj.data.Either;
import fj.data.List;
import fj.data.Validation;
import org.junit.Test;
import org.kantega.documenter.MavenDocumentationLocator;
import org.kantega.documenter.api.DocumentationLocator;
import org.kantega.documenter.api.FailedPluginDoc;
import org.kantega.documenter.api.PluginDoc;

import javax.xml.parsers.ParserConfigurationException;

public class MavenTest {

    @Test
    public void testCoords() throws ParserConfigurationException {
        DocumentationLocator locator = new MavenDocumentationLocator();


        final Validation<String, List<Either<FailedPluginDoc, PluginDoc>>> documentationFor =
          locator.getDocumentationFor("no.nte.distributions.erp:erp-handler:1.8-SNAPSHOT");
        documentationFor.f().forEach(System.out::println);
        documentationFor.forEach(eithers -> eithers.forEach(either->System.out.println(either.<String>either(Object::toString,s->s.documentRoot.toString()))));
    }
}
