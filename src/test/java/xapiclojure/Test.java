package xapiclojure;

import clojure.java.api.*;
import clojure.lang.IFn;
import static org.junit.Assert.*;

public class Test {

    static private final IFn _require;
    static private final IFn _validate;
    static {
        _require = Clojure.var("clojure.core", "require");
        _require.invoke(Clojure.read("xapi-schema.core"));
        _validate = Clojure.var("xapi-schema.core", "validate-statement-data");
    }

    static private Object validate(Object arg) {
        return _validate.invoke(arg);
    }

    @org.junit.Test
    public void test() {
        String statement_str =
                "{\"object\":{\"id\":\"http://example.com/xapi/activity/simplestatement\"," +
                              "\"definition\":{\"name\":{\"en-US\":\"simple statement\"}," +
                                              "\"description\":{\"en-US\":\"A simple Experience API statement. Note that the LRS\\n does not need to have any prior information about the Actor (learner), the\\n verb, or the Activity/object.\"}}}," +
                 "\"verb\":{\"id\":\"http://example.com/xapi/verbs#sent-a-statement\"," +
                           "\"display\":{\"en-US\":\"sent\"}}," +
                 "\"id\":\"fd41c918-b88b-4b20-a0a5-a4c32391aaa0\"," +
                 "\"actor\":{\"mbox\":\"mailto:user@example.com\"," +
                            "\"name\":\"Project Tin Can API\"," +
                            "\"objectType\":\"Agent\"}}";
        Object result = validate(statement_str);
        assertNotNull(result);
    }
}
