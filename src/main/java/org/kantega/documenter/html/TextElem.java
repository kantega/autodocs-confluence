package org.kantega.documenter.html;

import fj.data.LazyString;

public class TextElem implements HtmlElem {

    public final String content;

    public TextElem(String content) {this.content = content;}

    public static TextElem text(String string){
        return new TextElem(string);
    }

    @Override
    public LazyString render() {
        return LazyString.str(content);
    }
}
