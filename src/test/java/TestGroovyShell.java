import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.junit.Test;

/**
 * User: tiziano
 * Date: 12/11/15
 * Time: 10:34
 */
public class TestGroovyShell {
    class Email {
        public String sender;
        public String subject;
        public String body;

        public Email(String sender, String subject, String body) {
            this.sender = sender;
            this.subject = subject;
            this.body = body;
        }
    }

    /*
     *  { email -> email.subject.contains("Shell") && "axiastudio.it".equals(email.sender.split("@")[1]) }
     *
     *  Note:
     *
     *   - { e -> ... } è una closure che prende un parametro, in groovy la posso eseguire con { e -> ... }(par)
     *   - il punto e virgola come fine riga è opzionale in groovy
     *   - un metodo groovy restituisce l'ultima elaborazione (in questo caso l'and), quindi return è opzionale
     *
     */

    String GROOVY_RULE = "{ email ->" +
            "email.subject.contains(\"Shell\") &&" +
            "\"axiastudio.it\".equals(email.sender.split(\"@\")[1])" +
            "}";

    @Test
    public void test() throws Exception {

        Email email = new Email("tiziano@axiastudio.it", "Groovy Shell test", "Fake email wrapper");

        String groovyCode = GROOVY_RULE.trim() + "(email)";

        Binding binding = new Binding();
        binding.setVariable("email", email);
        GroovyShell shell = new GroovyShell(binding);
        Object res = shell.evaluate(groovyCode);

        if( res instanceof Boolean ){
            System.out.println(res);
        }

    }
}
