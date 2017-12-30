package xapiclojure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import clojure.java.api.Clojure;
import clojure.lang.IFn;

public class Test {

    static private final IFn _require;
    static private final IFn _validate;
    static private final IFn _check;
    static private final IFn _error_to_data;
    static private final IFn _assoc;
    static private final IFn _equals;
    static private final IFn _parse;
    static {
        _require = Clojure.var("clojure.core", "require");
        _require.invoke(Clojure.read("xapi-schema.core"));
        _validate = Clojure.var("xapi-schema.core", "validate-statement-data");
        _check = Clojure.var("xapi-schema.core", "statement-checker");
        _error_to_data = Clojure.var("xapi-schema.core", "errors->data");
        _assoc = Clojure.var("clojure.core", "assoc");
        _equals = Clojure.var("clojure.core", "=");
        _parse = Clojure.var("cheshire.core", "parse-string");
    }

    // Helper function to easily create Clojure maps to define
    // expected values in tests.
    private static Map<?, ?> m(Object... args) {
        assert (args.length % 2 == 0);
        Object m = clojure.lang.PersistentArrayMap.EMPTY;
        for(int i = 0; i < args.length; i+= 2) {
            m = _assoc.invoke(m, args[i], args[i+1]);
        }
        return (Map<?, ?>)m;
    }

    // Thin Java wrapper for xapi-schema.validate-statement-data
    static private Object validate(Object arg) {
        return _validate.invoke(arg);
    }

    // Thin Java wrapper for xapi-schema.statement-checker
    // Note: returns the error data, not an exception
    static private Object check(Object arg) {
        return _error_to_data.invoke(_check.invoke(arg));
    }

    // Parse JSON to Clojure (a.k.a. "edn") data structures
    // Note: throws on unparseable (i.e. non-JSON) input
    static private Object parseJSON(String s) {
        return _parse.invoke(s);
    }

    // Call out to Clojure's notion of equality
    static private boolean clojureEquals(Object o1, Object o2) {
        Object ret = _equals.invoke(o1, o2);
        if (ret == null) return false;
        return (Boolean) ret;
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

    // Invalid: missing the actor entry
    static private String statement_str_inv =
            "{\"object\":{\"id\":\"http://example.com/xapi/activity/simplestatement\"," +
                          "\"definition\":{\"name\":{\"en-US\":\"simple statement\"}," +
                                          "\"description\":{\"en-US\":\"A simple Experience API statement. Note that the LRS\ndoes not need to have any prior information about the Actor (learner), the\nverb, or the Activity/object.\"}}}," +
             "\"verb\":{\"id\":\"http://example.com/xapi/verbs#sent-a-statement\"," +
                       "\"display\":{\"en-US\":\"sent\"}}," +
             "\"id\":\"fd41c918-b88b-4b20-a0a5-a4c32391aaa0\"}";

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

    // Invalid: missing the actor entry
    static private Map<?, ?> statement_data_inv =
            m("object", m("id", "http://example.com/xapi/activity/simplestatement",
                          "definition", m("name", m("en-US", "simple statement"),
                                          "description", m("en-US", "A simple Experience API statement. Note that the LRS\ndoes not need to have any prior information about the Actor (learner), the\nverb, or the Activity/object."))),
              "verb", m("id", "http://example.com/xapi/verbs#sent-a-statement",
                        "display", m("en-US", "sent")),
              "id", "fd41c918-b88b-4b20-a0a5-a4c32391aaa0");

    @org.junit.Test
    public void validate_valid_string() {
        Object result = validate(statement_str);
        assertNotNull(result);
        assertTrue(result instanceof Map);
        Map<?, ?> result_as_map = (Map<?, ?>) result;
        assertTrue(clojureEquals(statement_data, result_as_map));
    }

    @org.junit.Test
    public void validate_valid_data() {
        Object result = validate(statement_data);
        assertNotNull(result);
        assertTrue(result instanceof Map);
        Map<?, ?> result_as_map = (Map<?, ?>) result;
        assertTrue(clojureEquals(statement_data, result_as_map));
    }

    @org.junit.Test(expected = Exception.class)
    public void validate_invalid_data() {
        validate(statement_data_inv);
        fail("should throw before this");
    }

    @org.junit.Test(expected = Exception.class)
    public void validate_invalid_string() {
        validate(statement_str_inv);
        fail("should throw before this");
    }

    @org.junit.Test(expected = Exception.class)
    public void validate_unparseable_string() {
        validate(statement_str.substring(10));
        fail("should throw before this");
    }

    @org.junit.Test
    public void check_does_not_support_string_input() {
        Object result = check(statement_str);
        assertNotNull(result);
        assertEquals("Not map: a-java.lang.String", result);
    }

    @org.junit.Test
    public void check_parsed_string() {
        Object result = check(parseJSON(statement_str));
        assertNull(result);
    }

    @org.junit.Test
    public void check_valid_data() {
        Object result = check(statement_data);
        assertNull(result);
    }

    @org.junit.Test
    public void check_invalid_data() {
        Object result = check(statement_data_inv);
        assertNotNull(result);
        assertTrue(result instanceof Map);
        Map<?, ?> result_as_map = (Map<?, ?>) result;
        assertEquals("Missing required key", result_as_map.get("actor"));
        assertEquals(1, result_as_map.size());
    }

}
