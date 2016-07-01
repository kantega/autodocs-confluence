package org.kantega.documenter.html;

import fj.F;
import fj.data.LazyString;
import fj.data.List;

public class FragmentElem implements HtmlElem {

    public final List<? extends HtmlElem> elems;

    public FragmentElem(List<? extends HtmlElem> elems) {this.elems = elems;}

    public static <T> HtmlElem repeat(List<T> ts, F<T,HtmlElem> f){
        return new FragmentElem(ts.map(f));
    }


    public static HtmlElem fragment(HtmlElem ... tags){
        return new FragmentElem(List.arrayList(tags));
    }

    @Override
    public LazyString render() {
        return elems.foldLeft((ls, e) -> ls.append(e.render()), LazyString.empty);
    }
}
