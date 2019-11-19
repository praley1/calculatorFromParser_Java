/**
 * Created by Patrick on 9/17/2016.
 */
import java.lang.Exception;

public class ParserException extends Exception {
    String parserError = "";

    public ParserException(Tokenizer tokenizer, String token){
        parserError = "Error at " + tokenizer.getPosition() + " token " + token;
//        super(token);
    }

    public String getParserError() {
        return parserError;
    }

}
