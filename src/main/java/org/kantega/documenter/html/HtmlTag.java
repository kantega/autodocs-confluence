package org.kantega.documenter.html;

import fj.data.LazyString;
import fj.data.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlTag implements HtmlElem {

    public final String tag;
    public final List<HtmlAttrib> attribs;
    public final List<HtmlElem> body;


    public HtmlTag(String tag, List<HtmlAttrib> attribs, List<HtmlElem> body) {
        this.tag = tag;
        this.attribs = attribs;
        this.body = body;
    }


    public static HtmlTag tag(String tag) {
        return new HtmlTag(tag, List.nil(), List.nil());
    }

    public HtmlTag attribs(HtmlAttrib... attribList) {
        return new HtmlTag(tag, attribs.append(List.arrayList(attribList)), body);
    }

    public HtmlTag a(HtmlAttrib... attribList) {
        return attribs(attribList);
    }

    public HtmlTag a(String name, String value) {
        return attrib(name, value);
    }

    public HtmlTag attrib(String name, String value) {
        return attribs(HtmlAttrib.attrib(name, value));
    }

    public HtmlTag body(HtmlElem... children) {
        return new HtmlTag(tag, attribs, List.arrayList(children));
    }

    public HtmlTag b(HtmlElem... children) {
        return body(children);
    }

    public HtmlTag b(String txt) {
        return body(TextElem.text(txt));
    }

    public HtmlTag body(String txt) {
        return body(TextElem.text(txt));
    }

    @Override
    public LazyString render() {
        LazyString beforeBody = LazyString.empty.append("<").append(tag).append(renderAttribs(attribs));
        if (body.isEmpty()) { return beforeBody.append("/>"); } else {
            return beforeBody.append(">").append(renderBody(body)).append("</").append(tag).append(">");
        }
    }




    private static LazyString renderAttribs(List<HtmlAttrib> attribs) {
        if (attribs.isEmpty()) { return LazyString.empty; }
        HtmlAttrib head = attribs.head();
        List<HtmlAttrib> tail = attribs.tail();

        if (tail.isEmpty()) {
            return LazyString.empty.append(" ").append(head.name).append("=").append("\"").append(head.value).append("\"");
        } else { return renderAttribs(tail).append(" ").append(head.name).append("=").append("\"").append(head.value).append("\""); }
    }

    private static LazyString renderBody(List<HtmlElem> body) {
        if (body.isEmpty()) { return LazyString.empty; } else {
            return body.head().render().append(renderBody(body.tail()));
        }
    }
}
