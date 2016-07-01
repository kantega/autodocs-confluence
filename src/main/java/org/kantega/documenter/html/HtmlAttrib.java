package org.kantega.documenter.html;

public class HtmlAttrib {

    public final String name;
    public final String value;

    public HtmlAttrib(String name, String value) {
        this.name = name;
        this.value = value;
    }


    public static HtmlAttrib attrib(String name, String value){
        return new HtmlAttrib(name,value);
    }

    public static HtmlAttrib a(String name, String value){
        return attrib(name,value);
    }


}
