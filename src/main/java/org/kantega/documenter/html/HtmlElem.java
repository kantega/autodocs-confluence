package org.kantega.documenter.html;

import fj.data.LazyString;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public interface HtmlElem {
    LazyString render();

    default String pretty() {
        String content = render().eval();
        Document doc = Jsoup.parseBodyFragment(content);
        if (content.startsWith("<html")) {
            return doc.outerHtml();
        } else { return doc.body().html(); }
    }
}
