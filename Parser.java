/** Patrick Raley
 *   CMSC 330
 *   Project 1
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileReader;
import java.io.IOException;

public class Parser extends JFrame{
    Tokenizer tokenizer = null;
    String inputFile = "E:\\School\\UMUC\\CMSC330_AdvProgLanguages\\CMSC330_week8\\inputFile2.txt";
    JFrame window;
    JTextField jTextField;
    int firstValue;

    public enum OperationType {
        ADD, SUBTRACT, DIVIDE, MULTIPLY
    }
    OperationType lastSelectedOperator;
    OperationType selectedOperator;

    public static void main(String args[]){
        Parser p = new Parser();
    }

    public Parser(){
        try {
            FileReader fr = new FileReader(inputFile);
            System.out.println(fr.toString());
            tokenizer = new Tokenizer(fr);

            GUI();

        } catch (IOException e){
            e.printStackTrace();
        }
        catch (ParserException pe) {
            System.out.println(pe.getParserError());
            System.out.println(pe.getMessage());
            pe.printStackTrace();
        }
        System.out.println(inputFile);
    }

    public void GUI() throws ParserException {
        String windowTitle = "";
        int leftParameter = 0;
        int rightParameter = 0;

        // CREATE WINDOW
        String currentToken = tokenizer.nextToken();
        if(currentToken.compareTo("Window") == 0){
            // GET WINDOW TITLE
            currentToken = tokenizer.nextToken();
            windowTitle = stripQuotes(currentToken);
            // GET WINDOW SIZE
            currentToken = tokenizer.nextToken();
            if(currentToken.compareTo("(") == 0){
                currentToken = tokenizer.nextToken();
                leftParameter = Integer.parseInt(currentToken);

                tokenizer.nextToken();

                currentToken = tokenizer.nextToken();
                rightParameter = Integer.parseInt(currentToken);

                tokenizer.nextToken();
            }
            else; //ERROR MESSAGE

            window = new JFrame(windowTitle);
            window.setVisible(true);
            window.setSize(leftParameter, rightParameter);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        else  throw new ParserException(tokenizer, currentToken) ; //ERROR MESSAGE

        Layout(window);

        // ISNT ENFORCED BY GRAMMAR BUT WE EXPECT FIRST WIDGET TO BE UPDATABLE TEXTFIELD
        currentToken = tokenizer.nextToken();
        // CREATE TEXTFIELD
        if (currentToken.compareTo("Textfield") == 0){
            // GET TEXTFIELD WIDTH
            currentToken = tokenizer.nextToken();
            int txtFieldWidth = Integer.parseInt(currentToken);
            jTextField = new JTextField(txtFieldWidth);
            window.add(jTextField);
            tokenizer.nextToken();  // CHECK THIS IS ;
        }

        // ASSUME THERE ARE MORE WIDGETS
        Widgets(window);

        currentToken = tokenizer.nextToken();
        if (currentToken.compareTo("End") == 0){
            System.out.println(currentToken);
        }
        else ; //ERROR MESSAGE

        currentToken = tokenizer.nextToken();
        if (currentToken.compareTo(".") == 0){
            System.out.print(currentToken);
        }
        else throw new ParserException(tokenizer, currentToken); //ERROR MESSAGE

    }

    public void Layout (Container container) throws ParserException {
        String currentToken = tokenizer.nextToken();
        if (currentToken.compareTo("Layout") == 0){
            System.out.println("Layout initiated");
        }
        else throw new ParserException(tokenizer, currentToken); //ERROR MESSAGE

        Layout_Type(container);

        currentToken = tokenizer.nextToken();
        if (currentToken.compareTo(":") == 0){
            System.out.print(currentToken);
        }
        else throw new ParserException(tokenizer, currentToken); //ERROR MESSAGE

    }

    public void Layout_Type (Container container) throws ParserException{
        String currentToken = tokenizer.nextToken();

        if (currentToken.compareTo("Flow") == 0) {
            container.setLayout(new FlowLayout());
        }
        else if (currentToken.compareTo("Grid") == 0) {
            // READ THE GRID
            container.setLayout(new GridLayout());

            currentToken = tokenizer.nextToken();
            if (currentToken.compareTo("(") == 0) {
                // ROWS FOR GRID
                currentToken = tokenizer.nextToken();
                int rows = Integer.parseInt(currentToken);

                tokenizer.nextToken();

                // COLUMNS FOR GRID
                currentToken = tokenizer.nextToken();
                int columns = Integer.parseInt(currentToken);

                currentToken = tokenizer.nextToken();
                if (currentToken.compareTo(",") == 0) {
                    // HORIZONTAL GAP
                    currentToken = tokenizer.nextToken();
                    int horizontalGap = Integer.parseInt(currentToken);

                    tokenizer.nextToken();

                    // VERTICAL GAP
                    currentToken = tokenizer.nextToken();
                    int verticalGap = Integer.parseInt(currentToken);

                    container.setLayout(new GridLayout(rows, columns, verticalGap, horizontalGap));

                    tokenizer.nextToken();  // )

                } else {
                    tokenizer.nextToken();
                    container.setLayout(new GridLayout(rows, columns));
                    // LOOK FOR RIGHT PARENTHESES TO END GRID DEFINITIONS
                }

            }
        }
        else throw new ParserException(tokenizer, currentToken); //ERROR MESSAGE

    }

    public void Widgets (Container container) throws ParserException {
        Widget(container);
        //CHECK TO SEE IF MORE WIDGET
        String nextToken = tokenizer.peekToken();
        boolean isWidget = false;
        if (nextToken.compareTo("Button") == 0)
            isWidget = true;
        if (nextToken.compareTo("Group") == 0)
            isWidget = true;
        if (nextToken.compareTo("Label") == 0)
            isWidget = true;
        if (nextToken.compareTo("Panel") == 0)
            isWidget = true;
        if (nextToken.compareTo("Textfield") == 0)
            isWidget = true;
        if (isWidget)
            Widgets(container);
    }

    public void Widget(Container container) throws ParserException {
        String buttonName = "";
        String labelName = "";
        int txtFieldWidth = 0;


        String currentToken = tokenizer.nextToken();

        // CREATE TEXTFIELD
         if (currentToken.compareTo("Textfield") == 0){
            // GET TEXTFIELD WIDTH
            currentToken = tokenizer.nextToken();
            txtFieldWidth = Integer.parseInt(currentToken);
             container.add(new JTextField(txtFieldWidth));
            tokenizer.nextToken();  // check this is a ;
        }

        // CREATE PANEL
        if (currentToken.compareTo("Panel") == 0){
            JPanel jp = new JPanel();
            container.add(jp);
            Layout(jp);
            Widgets(jp);
            tokenizer.nextToken();
            tokenizer.nextToken();
        }

        //CREATE BUTTONS
        if (currentToken.compareTo("Button") == 0){
            // GET BUTTON NAME
            currentToken = tokenizer.nextToken();
            buttonName = stripQuotes(currentToken);

            //BUTTON ACTIONS
            AbstractAction aa = null;
            if (buttonName.compareTo("Operator") == 0){
                aa = new OperatorButtonAction(buttonName);
            }
            else if (buttonName.compareTo("Equals") == 0){
                aa = new EqualsButtonAction(buttonName);
            }
            else if (buttonName.compareTo("Clear") == 0){
                aa = new ClearButtonAction(buttonName);
            }
            else
                aa = new NumberButtonAction(buttonName);


            aa.putValue("buttonName", buttonName);
            //PLACE BUTTON
            JButton jbt = new JButton(aa);
            container.add(jbt);
            tokenizer.nextToken();
        }

        //CREATE LABEL
        if (currentToken.compareTo("Label") == 0){
            //GET LABEL NAME
            currentToken = tokenizer.nextToken();
            labelName = stripQuotes(currentToken);
            //PLACE LABEL
            JLabel jlbl = new JLabel(labelName);
            container.add(jlbl);
            tokenizer.nextToken();
        }

        //CREATE GROUP
        if (currentToken.compareTo("Group") == 0){
            ButtonGroup bg = new ButtonGroup();
            //container.add(bg);
            Radio_Buttons(container, bg);
            tokenizer.nextToken();
            tokenizer.nextToken();
        }

    }

    public void Radio_Buttons(Container container, ButtonGroup bg) throws ParserException{
        Radio_Button(container, bg);
        //CHECK TO SEE IF MORE RADIO BUTTON
        String nextToken = tokenizer.peekToken();
        boolean isRadioButton = false;
        if (nextToken.compareTo("Radio") == 0)
            isRadioButton = true;
        if (isRadioButton)
            Radio_Buttons(container, bg);
//
    }

    public void Radio_Button(Container container, ButtonGroup bg)throws ParserException {
        String rButtonName = "";
        String currentToken = tokenizer.nextToken();

        if (currentToken.compareTo("Radio") == 0){
            // GET RADIO BUTTON NAME
            currentToken = tokenizer.nextToken();
            rButtonName = stripQuotes(currentToken);
            //PLACE RADIO BUTTON
            AbstractAction aa = null;
            if(rButtonName.compareTo("+") == 0) {
                aa = new OperatorRadioButtonAction(rButtonName, OperationType.ADD);
            }
            else if(rButtonName.compareTo("-") == 0) {
                aa = new OperatorRadioButtonAction(rButtonName, OperationType.SUBTRACT);
            }
            else if(rButtonName.compareTo("/") == 0) {
                aa = new OperatorRadioButtonAction(rButtonName, OperationType.DIVIDE);
            }
            else if (rButtonName.compareTo("*") == 0){
                aa = new OperatorRadioButtonAction(rButtonName, OperationType.MULTIPLY);
            }

            JRadioButton jRbt = new JRadioButton(aa);
            bg.add(jRbt);
            container.add(jRbt);
            tokenizer.nextToken();
        }
        else throw new ParserException(tokenizer, currentToken);
    }

    public String stripQuotes(String source){
        return source.substring(1, source.length()-1);
    }

    // SUB CLASSES FOR BUTTON ACTION LISTENERS
    private class NumberButtonAction extends AbstractAction {
        private NumberButtonAction(String buttonName){
            super (buttonName);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            String textFieldValue = jTextField.getText();
            int currentValue = 0;
            if (!textFieldValue.isEmpty()) {
                currentValue = Integer.parseInt(jTextField.getText());
            }
            currentValue *= 10;
            int buttonValue = Integer.parseInt((String)getValue("buttonName"));
            currentValue += buttonValue;
            String newValue = "" + currentValue;
            jTextField.setText(newValue);
        }
    };

    private class OperatorRadioButtonAction extends AbstractAction{
        OperationType myOperation;
        private OperatorRadioButtonAction(String buttonName, OperationType operator){
            super (buttonName);
            myOperation = operator;
        }
        @Override
        public void actionPerformed(ActionEvent e){
            lastSelectedOperator = myOperation;
        }
    }

    private class OperatorButtonAction extends AbstractAction{
        private OperatorButtonAction(String buttonName){
            super (buttonName);
        }
        @Override
        public void actionPerformed(ActionEvent e){
            String firstValueString = jTextField.getText();
            firstValue = Integer.parseInt(firstValueString);
            selectedOperator = lastSelectedOperator;
            jTextField.setText("");
        }
    }

    private class ClearButtonAction extends AbstractAction{
        private ClearButtonAction(String buttonName){
            super (buttonName);
        }
        @Override
        public void actionPerformed(ActionEvent e){
            jTextField.setText("");

            firstValue = 0;
            selectedOperator = null;
        }
    }

    private class EqualsButtonAction extends AbstractAction{

        private EqualsButtonAction(String buttonName){
            super (buttonName);
        }
        @Override
        public void actionPerformed(ActionEvent e){
            String secondValueString = jTextField.getText();
            int secondValue = Integer.parseInt(secondValueString);
            int sum;

            switch (selectedOperator) {
                case ADD:
                    sum = secondValue + firstValue;
                    jTextField.setText("" + sum);
                    System.out.println("add");
                    break;
                case SUBTRACT:
                    sum = firstValue - secondValue;
                    jTextField.setText("" + sum);
                    System.out.println("SUBTRACT");
                    break;
                case DIVIDE:
                    if(secondValue == 0){
                        jTextField.setText("You cannot divide by 0");
                    }
                    else {
                        sum = firstValue / secondValue;
                        jTextField.setText("" + sum);
                    }
                    System.out.println("DIVIDE");
                    break;
                case MULTIPLY:
                    sum = firstValue*secondValue;
                    jTextField.setText("" + sum);
                    System.out.println("MULTIPLY");
                    break;
            }

        }
    }


}
