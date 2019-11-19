/**
 * Created by Patrick on 9/14/2016.
 */
import java.io.IOException;
import java.io.InputStreamReader;

public class Tokenizer {
    InputStreamReader streamReader = null;
    char readAhead =0;
    String peekedToken = null;
    int lineNumber = 1;
    int tokenNumber = 0;

    public char nextChar(){
        char nextChar = ' ';
        try {
            int i = streamReader.read();
            if ( i >= 0)
                nextChar = (char)i;
            else
                return 0;
        } catch (IOException e){
            e.printStackTrace();
        }
        return nextChar;
    }

    public boolean isSpecialCharacter(char c){
        switch (c) {
            case '(':
            case ')':
            case ',':
            case '"':
            case ';':
            case '.':
            case ':':
                return true;
            default:
                return false;
        }
    }

    public String nextToken() {
        String token = "";

        if (peekedToken != null) {
            token = peekedToken;
            peekedToken = null;
            System.out.println("   nextToken (peeked) " + token);
            return token;
        }

        char c;
        if (readAhead != 0) {
            c = readAhead;
            readAhead = 0;
        }
        else {
            c = nextChar();
        }

        while(Character.isWhitespace(c)) {
            if(c == '\n') {
                lineNumber++;
                tokenNumber = 0;
                System.out.println(lineNumber);
            }
            c = nextChar();
        }


        if (isSpecialCharacter(c)) {
            if (c == '"') {
                token += c;
                c = nextChar();
                while (c != '"') {
                    token += c;
                    c = nextChar();
                }
                token += c;
            }
            else {
                token += c;
            }
        }
        else {
            while (!isSpecialCharacter(c) && !Character.isWhitespace(c)) {
                token += c;
                c = nextChar();
                readAhead = c;
            }
        }
        System.out.println("   nextToken " + token);
        tokenNumber ++;
        System.out.println("token number " + tokenNumber);
        return token;

    }

    String peekToken() {
        if (peekedToken == null)
            peekedToken = nextToken();
        System.out.println("   peekToken " + peekedToken);
        return peekedToken;
    }

    public Tokenizer(InputStreamReader streamReader){
        this.streamReader = streamReader;
    }

    public String getPosition(){
        String position = "line " + lineNumber + ", token number " + tokenNumber;
        return position;
    }

}
