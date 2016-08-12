package org.kantega.documenter;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import fj.data.Either;
import fj.data.List;
import fj.data.Option;
import fj.data.Validation;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.kantega.documenter.api.DocumentationLocator;
import org.kantega.documenter.api.FailedPluginDoc;
import org.kantega.documenter.api.PluginDoc;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static fj.data.List.iterableList;
import static fj.data.Option.*;
import static fj.data.Option.fromNull;
import static org.kantega.documenter.Utils.*;
import static org.kantega.documenter.html.FragmentElem.fragment;
import static org.kantega.documenter.html.FragmentElem.repeat;
import static org.kantega.documenter.html.Tags.*;

public class PluginDocumenterMacro implements Macro {

    private final XhtmlContent xhtmlUtils;
    private final DocumentationLocator docLocator;
    private final ObjectMapper mapper;
    private final PageBuilderService pageBuilderService;

    public PluginDocumenterMacro(
      XhtmlContent xhtmlUtils,
      DocumentationLocator docLocator,
      PageBuilderService pageBuilderService) {
        this.xhtmlUtils = xhtmlUtils;
        this.docLocator = docLocator;
        this.pageBuilderService = pageBuilderService;
        mapper = new ObjectMapper();
        mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
    }

    public String execute(
      Map<String, String> map,
      String s,
      ConversionContext conversionContext)
      throws MacroExecutionException {

        pageBuilderService.assembler().resources().requireWebResource("org.kantega.documenter:documenter-resources");

        Optional<String> maybeCoordinates =
          ofNullOrEmpty(map.get("mavenCoordinates"));

        String mode =
          Option.fromNull(map.get("display")).map(String::toLowerCase).orSome("unknown");

        boolean isFullMode =
          mode.equals("full");

        return
          maybeCoordinates
            .map(coordinates -> {
                Validation<String, List<Either<FailedPluginDoc, PluginDoc>>> maybeDoc =
                  docLocator.getDocumentationFor(coordinates);

                return
                  maybeDoc.validation(
                    fail ->
                      P.body(EM.b(String.format("Documentation for the handler %s could not be loaded: %s", coordinates, fail))),

                    docs ->
                      fragment(
                        H1.b(coordinates),
                        DIV.a("class", "table-wrap").b(
                          TABLE.a("class", "confluenceTable").b(
                            TR.b(
                              TH.a("class", "confluenceTh").b("Navn"),
                              TH.a("class", "confluenceTh").b("Versjon"),
                              TH.a("class", "confluenceTh").b("Beskrivelse")),

                            repeat(docs, failOrDoc ->
                              failOrDoc.either(
                                f -> TR.b(
                                  TD.a("class", "confluenceTd").b(f.name),
                                  TD.a("class", "confluenceTd").b(f.version),
                                  TD.a("class", "confluenceTd").b(EM.b(f.failMsg))),
                                d ->
                                  TR.b(
                                    TD.a("class", "confluenceTd").b(isFullMode ? A.a("href", "#" + d.getId()).b(d.getLabel()) : SPAN.b(d.getLabel())),
                                    TD.a("class", "confluenceTd").b(d.getVersion()),
                                    TD.a("class", "confluenceTd").b(fromNull(d.documentRoot.get("pluginDescription")).map(JsonNode::asText).orSome("No desc"))))))),

                        renderIf(isFullMode,
                          () -> repeat(somes(docs.map(e -> e.right().toOption())), d ->
                            fragment(

                              H1.a("id", d.getId()).b(d.getLabel()),
                              HR,
                              EM.b("Versjon: " + d.getVersion()),
                              P.b(fromNull(d.documentRoot.get("pluginDescription")).map(JsonNode::asText).orSome("No desc")),

                              H3.b("Avhengigheter"),
                              repeat(asList(d.documentRoot.get("dependencies")), node ->
                                P.b(node.get("type").asText() + " " + node.get("url").asText())),

                              renderIf(fromNull(d.documentRoot.get("model")).bind(n -> fromNull(n.get("nodes"))).isSome(),
                                fragment(
                                  H3.b("Flyt"),
                                  DIV.a("id", "flyt-" + d.getSelectorId()).a("class", "model").a("style", "height:600px;width:100%;"),
                                  SCRIPT.b(
                                    "var model =JSON.parse(\"" + getEscaped(d.documentRoot.get("model").toString()) + "\");\n" +
                                      "window.cyto('#flyt-" + d.getSelectorId() + "',model);\n"))),

                              renderIf(asList(d.documentRoot.get("resources")).isNotEmpty(), H3.b("Rest API")),

                              repeat(asList(d.documentRoot.get("resources")), node ->
                                P.b(
                                  H4.b("Resource: " + node.get("path").asText()),
                                  EM.b("RolesAllowed: " + mkString(iterableList(node.get("rolesAllowed")).map(JsonNode::asText), ", ")),
                                  P.b(StringUtils.replace(node.get("documentation").asText(), "\n", "<br/>")),
                                  P.b(repeat(asList(node.get("methodDocs")), mdoc ->
                                    fragment(
                                      H5.b(mdoc.get("method").asText() + " " + mdoc.get("path").asText()),
                                      EM.b("Parameters: " + mkString(iterableList(mdoc.get("parameters")).map(JsonNode::asText), ", ")),
                                      EM.b("RolesAllowed: " + mkString(iterableList(node.get("rolesAllowed")).map(JsonNode::asText), ", ")),
                                      P.b(StringUtils.replace(mdoc.get("documentation").asText(), "\n", "")),
                                      repeat(asList(mdoc.get("exchangeDocumentations")), exDoc ->
                                        fragment(
                                          EM.b("Exchange " + fromNull(exDoc.get("requestDocumentation").get("url")).map(JsonNode::asText).orSome("N/A")),
                                          BR,
                                          DIV.b(render(
                                            xhtmlUtils,
                                            "{code:title=Request|linenumbers=false|language=none|firstline=0001|collapse=false}\n" +
                                              prettyPrint(mapper, exDoc.get("requestDocumentation").get("body").asText()).orSome("empty") +
                                              "{code}", conversionContext)),
                                          DIV.b(render(
                                            xhtmlUtils,
                                            "{code:title=Response " +
                                              fromNull(exDoc.get("responseDocumentation").get("status")).map(JsonNode::asText).orSome("N/A") +
                                              "|linenumbers=false|language=none|firstline=0001|collapse=false}\n" +
                                              prettyPrint(mapper, exDoc.get("responseDocumentation").get("body").asText()).orSome("empty") +
                                              "{code}", conversionContext))))))))))))
                      )).pretty();

            })
            .orElse(EM.b("No coordinates provided").pretty());

    }

    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }

    private Optional<String> ofNullOrEmpty(String str) {
        return org.apache.commons.lang3.StringUtils.isEmpty(str) ? Optional.empty() : Optional.of(str);
    }

    private static String render(XhtmlContent xhtmlUtils, String s, ConversionContext ctx) {
        try {
            return xhtmlUtils.convertWikiToView(s, ctx, new ArrayList<>());
        }
        catch (XMLStreamException | XhtmlException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getEscaped(String json) {
        return StringEscapeUtils.escapeEcmaScript(json);
    }
}
