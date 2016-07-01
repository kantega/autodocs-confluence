package org.kantega.documenter.html;

import fj.data.LazyString;

import java.util.function.Supplier;

public class Tags {

    public static final HtmlTag HR = HtmlTag.tag("hr");
    public static final HtmlTag BR = HtmlTag.tag("br");
    public static final HtmlTag TR = HtmlTag.tag("tr");
    public static final HtmlTag TD = HtmlTag.tag("td");
    public static final HtmlTag TABLE = HtmlTag.tag("table");
    public static final HtmlTag TH = HtmlTag.tag("th");
    public static final HtmlTag P = HtmlTag.tag("p");
    public static final HtmlTag EM = HtmlTag.tag("em");
    public static final HtmlTag DIV = HtmlTag.tag("div");
    public static final HtmlTag PRE = HtmlTag.tag("pre");
    public static final HtmlTag SPAN = HtmlTag.tag("span");
    public static final HtmlTag A = HtmlTag.tag("a");
    public static final HtmlTag H1 = HtmlTag.tag("h1");
    public static final HtmlTag H2 = HtmlTag.tag("h2");
    public static final HtmlTag H3 = HtmlTag.tag("h3");
    public static final HtmlTag H4 = HtmlTag.tag("h4");
    public static final HtmlTag H5 = HtmlTag.tag("h5");
    public static final HtmlTag SCRIPT = HtmlTag.tag("script");

    public static HtmlElem renderIf(boolean flag, HtmlElem elem) {
        return flag ? elem : () -> LazyString.empty;
    }

    public static HtmlElem renderIf(boolean flag, Supplier<HtmlElem> elem) {
        return flag ? elem.get() : () -> LazyString.empty;
    }
}
