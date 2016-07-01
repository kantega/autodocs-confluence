package org.kantega.documenter;

import fj.data.List;
import fj.data.Option;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class Utils {

    public static List<JsonNode> asList(JsonNode node) {
        return Option.fromNull(node).map(List::iterableList).orSome(List.nil());
    }

    public static String mkString(List<String> list, String delim) {
        if (list.isEmpty()) { return ""; }
        String head = list.head();
        List<String> tail = list.tail();
        if (tail.isEmpty()) { return head; } else { return head + delim + mkString(tail, delim); }
    }

    public static Option<String> prettyPrint(ObjectMapper mapper, String str) {
        if (StringUtils.isBlank(str) || StringUtils.isBlank(StringUtils.replace(str, "\n", ""))) {
            return Option.none();
        }
        try {
            JsonNode n = mapper.readTree(str);
            return Option.some(mapper.writeValueAsString(n));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
