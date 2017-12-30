package xapiclojure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

public class Test {

    static private final IFn _require;
    static private final IFn _validate;
    static {
        _require = Clojure.var("clojure.core", "require");
        _require.invoke(Clojure.read("xapi-schema.core"));
        _validate = Clojure.var("xapi-schema.core", "validate-statement-data");
    }
    private static Map<Object, Object> m(Object... args) {
        assert (args.length % 2 == 0);
        Map<Object, Object> ret = new HashMap<>();
        for(int i = 0; i < args.length; i+= 2) {
            ret.put(args[i], args[i+1]);
        }
        return ret;
    }
    private static void assertMapEqual(Map<?, ?> expected, Map<?, ?> actual) throws AssertionError {
        if (expected.size() != actual.size())
            throw new AssertionError("sizes differ");
        for(Object k: expected.keySet()) {
            if (!actual.containsKey(k))
                throw new AssertionError(String.format("%s not found in actual", k));
            if (expected.get(k) == null && actual.get(k) != null)
                throw new AssertionError(String.format("%s should be null", k));
            if (expected.get(k) != null && actual.get(k) == null)
                throw new AssertionError(String.format("%s should not be null", k));
            if (expected.get(k) != null) {
                Object v1 = expected.get(k);
                Object v2 = actual.get(k);
                if(v1 instanceof Map && v2 instanceof Map) {
                    assertMapEqual((Map<?,?>)v1, (Map<?,?>)v2);
                } else {
                    assertEquals(v1, v2);
                }
            }
        }
    }

    static private Object validate(Object arg) {
        return _validate.invoke(arg);
    }

    static private String statement_str =
            "{\"object\":{\"id\":\"http://example.com/xapi/activity/simplestatement\"," +
                          "\"definition\":{\"name\":{\"en-US\":\"simple statement\"}," +
                                          "\"description\":{\"en-US\":\"A simple Experience API statement. Note that the LRS\ndoes not need to have any prior information about the Actor (learner), the\nverb, or the Activity/object.\"}}}," +
             "\"verb\":{\"id\":\"http://example.com/xapi/verbs#sent-a-statement\"," +
                       "\"display\":{\"en-US\":\"sent\"}}," +
             "\"id\":\"fd41c918-b88b-4b20-a0a5-a4c32391aaa0\"," +
             "\"actor\":{\"mbox\":\"mailto:user@example.com\"," +
                        "\"name\":\"Project Tin Can API\"," +
                        "\"objectType\":\"Agent\"}}";
    static private Map<?, ?> statement_data =
            m("object", m("id", "http://example.com/xapi/activity/simplestatement",
                          "definition", m("name", m("en-US", "simple statement"),
                                          "description", m("en-US", "A simple Experience API statement. Note that the LRS\ndoes not need to have any prior information about the Actor (learner), the\nverb, or the Activity/object."))),
              "verb", m("id", "http://example.com/xapi/verbs#sent-a-statement",
                        "display", m("en-US", "sent")),
              "id", "fd41c918-b88b-4b20-a0a5-a4c32391aaa0",
              "actor", m("mbox", "mailto:user@example.com",
                         "name", "Project Tin Can API",
                         "objectType", "Agent"));

    @org.junit.Test
    public void test() {
        Object result = validate(statement_str);
        assertNotNull(result);
        assertTrue(result instanceof Map);
        Map<?, ?> result_as_map = (Map<?, ?>) result;
        assertMapEqual(statement_data, result_as_map);
    }
}
