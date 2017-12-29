package xapiclojure;

import clojure.java.api.*;
import clojure.lang.IFn;

public class StatementValidatorXAPI {

     public static void main(String[] args){
        String statement_str = "{\"object\":{\"id\":\"http://example.com/xapi/activity/simplestatement\",\"definition\":{\"name\":{\"en-US\":\"simple statement\"},\"description\":{\"en-US\":\"A simple Experience API statement. Note that the LRS does not need to have any prior information about the Actor (learner), the verb, or the Activity/object.\"}}},\"verb\":{\"id\":\"http://example.com/xapi /verbs#sent-a-statement\",\"display\":{\"en-US\":\"sent\"}},\"id\":\"fd41c918-b88b-4b20-a0a5-a4c32391aaa0\",\"actor\":{\"mbox\":\"mailto:user@example.com\",\"name\":\"Project Tin Can API\",\"objectType\":\"Agent\"}}";
        // (xs/validate-statement-data statement-str) ;; => returns statement edn

        String st2 = "{\"actor\": {\"objectType\": \"Agent\",\"mbox_sha1sum\": \"asdfa\"},\"verb\": {\"id\": \"https://w3id.org/xapi/adl/verbs/logged-in\",\"display\": {\"en-US\": \"logged-in\"}},\"object\": {\"objectType\": \"Agent\",\"openid\": \"https://www.testncoi.com\",\"name\": \"NCOI EC4\"},\"timestamp\": \"2017-12-27T09:54:40.8927406\"}";
        
        IFn require = Clojure.var("clojure.core", "require");

        //require.invoke(Clojure.read("schema.core"));
        //require.invoke(Clojure.read("cheshire.core"));
        //require.invoke(Clojure.read("xapi-schema.schemata.json"));
        //require.invoke(Clojure.read("schema.utils"));
        //require.invoke(Clojure.read("xapi-schema.schemata.util"));

        require.invoke(Clojure.read("xapi-schema.core"));

        IFn validate = Clojure.var("xapi-schema.core", "validate-statement-data");



        try {
            validate.invoke(st2);
        }catch(Exception e){
            System.out.println(e.getMessage());
            System.out.println(e.getCause());

            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }
        //String json = "{\"actor\": {\"objectType\": \"Agent\",\"mbox_sha1sum\": \"A0015I8kR\"}}";
        //Object result = validate.invoke(json);
        //System.out.println(result.getClass());
    }
}


