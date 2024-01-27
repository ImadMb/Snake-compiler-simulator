import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Snake extends JFrame implements ActionListener {

    JButton submitFile, lexiqueButton, syntaxeButton, semantiqueButton;
    JTextField fileTextField;
    static JTextArea resultTextField;
    JScrollPane scrollPane;

    boolean fileOpened = false;
    File file;
    FileReader reader;
    FileReader r;
    String path = "";

    ImageIcon abc = new ImageIcon("media/alphabet.png");
    ImageIcon fleche = new ImageIcon("media/fast-forward.png");
    ImageIcon snake = new ImageIcon("media/snake.png");
    ImageIcon pense = new ImageIcon("media/modern.png");
    ImageIcon dossier = new ImageIcon("media/folder.png");

    Snake() {
        //gui
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setTitle("Analyse lexical - syntaxique - sémantique");
        this.setIconImage(snake.getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(680, 680);
        this.setLayout(null);
        this.setResizable(false);
        this.setLocationRelativeTo(null);

        JPanel filePanel = new JPanel();
        filePanel.setBounds(20, 20, 630, 100);
        filePanel.setLayout(null);

        JPanel resultPanel = new JPanel();
        resultPanel.setBounds(20, 135, 630, 480);
        resultPanel.setLayout(null);

        submitFile = new JButton("Charger Un Ficher");
        submitFile.setIcon(dossier);
        submitFile.addActionListener(this);
        submitFile.setBounds(35, 30, 200, 40);

        lexiqueButton = new JButton("Lexique");
        lexiqueButton.setBounds(55, 30, 150, 40);
        lexiqueButton.addActionListener(this);

        syntaxeButton = new JButton("Syntaxe");
        syntaxeButton.setBounds(240, 30, 150, 40);
        syntaxeButton.addActionListener(this);

        semantiqueButton = new JButton("Semantique");
        semantiqueButton.setBounds(425, 30, 150, 40);
        semantiqueButton.addActionListener(this);

        lexiqueButton.setIcon(abc);
        syntaxeButton.setIcon(fleche);
        semantiqueButton.setIcon(pense);

        fileTextField = new JTextField();
        fileTextField.setBounds(250, 35, 350, 30);
        fileTextField.setEditable(false);

        resultTextField = new JTextArea();
        resultTextField.setEditable(false);
        resultTextField.setLineWrap(true);
        resultTextField.setWrapStyleWord(true);

        scrollPane = new JScrollPane(resultTextField);
        scrollPane.setBounds(35, 80, 560, 360);

        filePanel.add(submitFile);
        filePanel.add(fileTextField);

        resultPanel.add(lexiqueButton);
        resultPanel.add(syntaxeButton);
        resultPanel.add(semantiqueButton);
        resultPanel.add(scrollPane);

        this.add(filePanel);
        this.add(resultPanel);
        this.setVisible(true);
    }

    //actions performed for each button
    @Override
    public void actionPerformed(ActionEvent e) {

        //submit button
        if (e.getSource() == submitFile) {
            JFileChooser fileChooser = new JFileChooser(".");

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                path = file.getAbsolutePath();
                fileTextField.setText(file.getAbsolutePath());
                fileOpened = true;
            }
        }


        //lexical button

        if (e.getSource() == lexiqueButton && fileOpened) {
            try {
                file = new File(path);
                reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line;
                resultTextField.setText(""); 

                // Lexical analysis
                while ((line = bufferedReader.readLine()) != null) {

                    int l = line.length();
                    int v = 0;
                    int i, j;

                    for( i = 0; i < l; i++) {
                        if(line.charAt(i) == ','){
                            v++;
                        }
                    }

                    Character[] lineT = new Character[l+v];

                    for(i = 0, j=0; i<l+v && j< l; i++, j++) {

                        if(line.charAt(j) != ',') {
                            lineT[i] = line.charAt(j);
                        }
                        else{
                            lineT[i] = ' ';
                            lineT[i+1] = ',';
                            i++;
                        }

                    }

                    StringBuilder stringBuilder = new StringBuilder(lineT.length);

                    for (char c : lineT) {
                        stringBuilder.append(c);
                    }

                    line = stringBuilder.toString();
                    lexicalAnalyser(line);
                }

                reader.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


        //syntax button

        if (e.getSource() == syntaxeButton && fileOpened) {

            int syntaxErrors = 0;

            try {
                file = new File(path);
                reader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line="";
                resultTextField.setText(""); 
                int lineNumber = 0;

                int beginEnd = 0;
                int begins = 0;
                int ends = 0;

                while ((line = bufferedReader.readLine()) != null) {
                    
                    lineNumber++;
                    line = line.replace("\t", "");
                    line = line.trim();
                
                    if (line.length() >= 9 && line.substring(0, 9).equals("Snk_Begin")) {
                        begins++;
                        resultTextField.append(lineNumber + ". " + line + ":\tDebut du programme\n");
                        if(line.endsWith("$")){
                            resultTextField.append("Erreur syntaxique: Debut du programme ne doit pas finis avec $\n");
                            syntaxErrors++;
                        } 
                    }

                    else if (line.length() >= 7 && line.substring(0, 7).equals("Snk_Int")) {
                        resultTextField.append(lineNumber + ". " + line + ":\tDeclaration du type entier\n");
                        if(!line.endsWith("$")){
                            resultTextField.append("Erreur syntaxique: Symbole $ manquant a la fin de l'instruction\n");
                            syntaxErrors++;
                        } 
 
                        line = line.substring(8);
                        if(countIdentifiersAndCommas(line) != 1){
                            resultTextField.append("Erreur syntaxique: Mal déclaration des variables entières\n");
                            syntaxErrors++;
                        }

                    }

                    else if (line.length() >= 8 && line.substring(0, 8).equals("Snk_Real")) {
                        resultTextField.append(lineNumber + ". " + line + ":\tDeclaration du type réel\n");
                        if(!line.endsWith("$")){
                            resultTextField.append("Erreur syntaxique: Symbole $ manquant a la fin de l'instruction\n");
                            syntaxErrors++;

                        } 

                        line = line.substring(9);
                        if(countIdentifiersAndCommas(line) != 1){
                            resultTextField.append("Erreur syntaxique: Mal declaration des variables réel\n");
                            syntaxErrors++;
                        } 
                    }

                    else if (line.length() >= 3 && line.substring(0, 3).equals("Set")) {
                        resultTextField.append(lineNumber + ". " + line + ":\tAffectation entre valeur et variable\n");
                        if(!line.endsWith("$")){
                            resultTextField.append("Erreur syntaxique: Symbole $ manquant a la fin de l'instruction\n");
                        } 

                        line = line.substring(4);
                        if(countIdentifiersAndCommas(line) != 1){
                            resultTextField.append("Erreur syntaxique: Mal affectation d'une valeur a un variable\n");
                            syntaxErrors++;
                        } 
                    }

                    else if (line.length() >= 2 && line.substring(0, 2).equals("If")) {
                        resultTextField.append(lineNumber + ". " + line + ":\tSi du Condition\n");
                        if(line.endsWith("$")){
                            resultTextField.append("Erreur syntaxique: Une condition ne doit pas finis avec $\n");
                            syntaxErrors++;
                        } 
                    }

                    else if (line.length() >= 4 && line.substring(0, 4).equals("Else")) {
                        resultTextField.append(lineNumber + ". " + line + ":\tSinon du condition\n");
                        if(line.endsWith("$")){
                            resultTextField.append("Erreur syntaxique: Une condition ne doit pas finis avec $\n");
                            syntaxErrors++;
                        } 
                    }
                    
                    else if (line.length() >= 5 && line.substring(0, 5).equals("Begin")) {
                        resultTextField.append(lineNumber + ". " + line + ":\tDebut des instructions\n");
                        if(line.endsWith("$")){
                            resultTextField.append("Erreur syntaxique: Debut d'instructions ne doit pas finis avec $\n");
                            syntaxErrors++;
                        } 
                        beginEnd++;

                    }

                    else if (line.length() >= 3 && line.substring(0, 3).equals("End")) {
                        resultTextField.append(lineNumber + ". " + line + ":\tFin des instructions\n");
                        if(line.endsWith("$")){
                            resultTextField.append("Erreur syntaxique: Fin d'instructions ne doit pas finis avec\n");
                            syntaxErrors++;
                        } 
                        beginEnd--;
                    }
                        
                    else if (line.length() >= 3 && line.substring(0, 3).equals("Get")) {
                        resultTextField.append(lineNumber + ". " + line + ":\tAffectation entre variable\n");
                        if(!line.endsWith("$")){
                            resultTextField.append("Erreur syntaxique: Symbole $ manquant a la fin de l'instruction\n");
                            syntaxErrors++;
                        }

                        if(!line.contains("from")) {
                            resultTextField.append("Errreur syntaxique: mot clé from manquant\n");
                        }

                        line = line.substring(6);
                        if(countIdentifiersAndCommas(line) != 2){
                            resultTextField.append("Erreur syntaxique: Mal affectation entre les variables\n");
                            syntaxErrors++;
                        } 
                    }

                    else if (line.length() >= 9 && line.substring(0, 9).equals("Snk_Print")) {
                        
                        if(line.contains("\"")){
                            resultTextField.append(lineNumber + ". " + line + ":\tAffichage d'une chaine de character\n");
                            line = line.substring(10);

                            if(!line.endsWith("$")){
                                resultTextField.append("Erreur syntaxique: Symbole $ manquant a la fin de l'instruction\n");
                                syntaxErrors++;
                            } 
                            int count = 0;
                            for (char c : line.toCharArray()) {
                                if (c == '"') {
                                    count++;
                                }
                            }
                            if(count!=2){
                                resultTextField.append("Erreur syntaxique: Mal affichage du chaine de charactères\n");
                                syntaxErrors++;
                            } 
                        }
                        else {
                            resultTextField.append(lineNumber + ". " + line + ":\tAffichage des variables\n");
                            line = line.substring(10);

                            if(!line.endsWith("$")){
                                resultTextField.append("Erreur syntaxique: Symbole $ manquant a la fin de l'instruction\n");
                                syntaxErrors++;
                            } 
                            if(countIdentifiersAndCommas(line) != 1){
                                resultTextField.append("Erreur syntaxique: Mal affichages des variables\n");
                                syntaxErrors++;
                            } 
                        }

                        
                        
                    }

                    else if (line.length() >= 8 && line.substring(0, 8).equals("Snk_Strg")) {

                        resultTextField.append(lineNumber + ". " + line + ":\t Declaration d'une chaine de character\n");
                            line = line.substring(9);

                            if(!line.endsWith("$")){
                                resultTextField.append("Erreur syntaxique: Symbole $ manquant a la fin de l'instruction\n");
                                syntaxErrors++;
                            } 
                            int counter = 0;
                            for (char c : line.toCharArray()) {
                                if (c == '"') {
                                    counter++;
                                }
                            }
                            if(counter!=2){
                                resultTextField.append("Erreur syntaxique: Mal declaration du chaine de charactères\n");
                                syntaxErrors++;
                            }

                    }

                    else if (line.length() >= 2 && line.substring(0, 2).equals("$$")) {
                        resultTextField.append(lineNumber + ". " + line + ":\tCommentaire\n");

                    }

                    else if (line.length() >= 7 && line.substring(0, 7).equals("Snk_End")) {
                        ends++;
                        resultTextField.append(lineNumber + ". " + line + ":\tFin du program\n");
                        if(line.endsWith("$")){
                            resultTextField.append("Erreur syntaxique: Fin du programme ne doit pas finis avec $\n");
                            syntaxErrors++;
                        } 
                    }
                    
                }

                    if(beginEnd > 0){
                        resultTextField.append("Instruction(s) End manquant\n");
                    }else if (beginEnd < 0){
                        resultTextField.append("Erreur syntaxique: Instruction(s) Begin manquant\n");
                    } 
                

                reader.close();

                if(begins > 1) {
                    resultTextField.append("Erreur syntaxique: Snk_Debut supplémentaires trouvés\n");
                    syntaxErrors++;
                }
                if(ends > 1) {
                    resultTextField.append("Erreur syntaxique: Snk_End supplémentaires trouvés\n");
                    syntaxErrors++;
                }
                if(begins == 0) {
                    resultTextField.append("Erreur syntaxique: Snk_Begin manquant\n");
                    syntaxErrors++;
                }
                if(ends == 0) {
                    resultTextField.append("Erreur syntaxique: Snk_End manquant\n");
                    syntaxErrors++;
                }



            } catch (IOException ex) {
                ex.printStackTrace();
            }
            resultTextField.append("-----------------------------------------------------------------------------------------------------------------------------------\n");

            if(syntaxErrors==0){
                resultTextField.append("0 Erreurs syntaxique trouvé\n");
            }
            else {
                resultTextField.append(syntaxErrors+" Erreurs syntaxique trouvé\n");

            }
        }

        //senatic button
        
        if (e.getSource() == semantiqueButton && fileOpened) {
            int semanticErrors = 0;

            try {

                resultTextField.setText("");
                file = new File(path);
                reader = new FileReader(file);
                r = new FileReader(file);
                String sampleCode="";

                BufferedReader bufferedReader = new BufferedReader(reader);

                String line;   
                int lineNumber = 0;

                while ((line = bufferedReader.readLine()) != null) {
                    Set<String> integerVariables = new HashSet<>();
                    Set<String> realVariables = new HashSet<>();
                    Set<String> stringVariables = new HashSet<>();

                    lineNumber++;
                    line = line.replace("\t", "");
                    line = line.trim();

                    if (line.length() >= 3 && line.substring(0, 3).equals("Set")) {
                        String code = "";
                        int data = r.read();
                        while (data != -1) {
                            code += (char) data;
                            data = r.read();
                        }
                    
                        String[] lines = code.split("\n");
                    
                        for (String li : lines) {
                            findDeclaredVariables(li, "Snk_Int", integerVariables);
                            findDeclaredVariables(li, "Snk_Real", realVariables);
                            findDeclaredVariables(li, "Snk_Strg", stringVariables);
                        }
                    
                        String[] integerVars = formatVariableList(integerVariables).split(",");
                        String[] realVars = formatVariableList(realVariables).split(",");
                        String[] stringVars = formatVariableList(stringVariables).split("\"");
                    
                        for (String var : integerVars) {
                            var = var.trim();
                        }
                        for (String var : realVars) {
                            var = var.trim();
                        }
                        stringVars[0].trim();
                    
                        Set<String> identifiersInLine = getIdentifiersInLine(line);
                        identifiersInLine.remove("Set");
                    
                        String id = identifiersInLine.toString();
                        boolean idIsInt = false;
                        int ll = id.length();
                        id = id.substring(1, ll - 1);
                    
                        if (idIsInt && line.contains(".")) {
                            resultTextField.append(lineNumber + ". " + line + " affectation d'une valeur réel au variable " + id + "\n");
                        } else if (!idIsInt && line.contains(".")) {
                            resultTextField.append(lineNumber + ". " + line + " affectation d'une valeur réel au variable " + id + "\n");
                        } else if (!idIsInt && !line.contains(".")) {
                            resultTextField.append(lineNumber + ". " + line + " affectation d'une valeur entière au variable " + id + "\n");
                        } else if (idIsInt && !line.contains(".")) {
                            resultTextField.append(lineNumber + ". " + line + " affectation d'une valeur entière au variable " + id + "\n");
                        }
                    } else if (line.length() >= 3 && line.substring(0, 3).equals("Get")) {
                        Set<String> identifiersInLine = getIdentifiersInLine(line);
                        identifiersInLine.remove("Get");
                        identifiersInLine.remove("from");
                    
                        String id_1 = ""; 
                        String id_2 = "";
                    

                        for (String id : identifiersInLine) {
                            if (id_1.isEmpty()) {
                                id_1 = id;
                            } else {
                                id_2 = id;
                                break; 
                            }
                        }
                    
                    
                        resultTextField.append(lineNumber + ". " + line + " Affectation de variable " + id_2 + " au variable " + id_1 + "\n");
                    }

                }

                reader = new FileReader(file);
                int data = reader.read();
                while(data != -1){
                    sampleCode += (char)data;
                    data = reader.read();
                }

                semanticErrors += performSemanticAnalysis(sampleCode);
                
                reader.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            resultTextField.append("--------------------------------------------------------------------------------------------------------------------------------------\n");
            if(semanticErrors == 0) resultTextField.append("0 Erreurs semantique trouvé\n"); 
            else resultTextField.append(semanticErrors+"  Erreurs semantique trouvé\n"); 
        }
        
        
    }
    

    //methods used in analysers


    

    private void lexicalAnalyser(String line) {
    
        String regexString = "\"[^\"]*\\s*\"";
        String regexInteger = "\\b\\d+\\b";
        String regexReal = "\\b\\d+\\.\\d+\\b";
        String regexComment = "\\${2}";
        String regexEndOfLine = "[\\$]{1}";
        String regexIdentifier = "[a-zA-Z][a-zA-Z0-9_]*";
        String regexOperator = "<>|<=|>=|<|>|=";
        String regexKeywordSnk_Begin = "\\bSnk_Begin\\b";
        String regexKeywordSnk_Strg = "\\bSnk_Strg\\b";
        String regexKeywordSnk_End = "\\bSnk_End\\b";
        String regexKeywordSnk_Int = "\\bSnk_Int\\b";
        String regexKeywordSnk_Real = "\\bSnk_Real\\b";
        String regexKeywordSet = "\\bSet\\b";
        String regexKeywordGet = "\\bGet\\b";
        String regexKeywordIf = "\\bIf\\b";
        String regexKeywordElse = "\\bElse\\b";
        String regexKeywordFrom = "\\bfrom\\b";
        String regexKeywordBegin = "\\bBegin\\b";
        String regexKeywordEnd = "\\bEnd\\b";
        String regexKeywordSnk_Print = "\\bSnk_Print\\b";
        String regexSymbolComma = ",";
        String regexSymbolDot = "\\.";
        String regexSymbolLeftBracket = "\\[";
        String regexSymbolRightBracket = "\\]";
    
        String regex = String.format("(%s)|(%s)|(%s)|(%s)|(%s)|(%s)|(%s)|(%s)|(%s)|(%s)|(%s)|(%s)|(%s)|(%s)|(%s)|(%s)|(%s)|(%s)|(%s)|(%s)",
            regexKeywordGet, regexSymbolComma, regexSymbolDot, regexString, regexInteger, regexReal, regexComment, regexEndOfLine, regexKeywordSnk_Strg ,
            regexIdentifier, regexOperator, regexSymbolRightBracket, regexSymbolLeftBracket, 
            regexKeywordSnk_Begin, regexKeywordSnk_Int, regexKeywordSnk_Real,
            regexKeywordSet, regexKeywordIf, regexKeywordElse, regexKeywordFrom,
            regexKeywordBegin, regexKeywordEnd, regexKeywordSnk_Print);
    
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        boolean skipLine = false;

        while (matcher.find()) {
            String token = matcher.group().trim();
            if (!token.isEmpty()) {
                if (token.matches(regexComment)) {
                    if (matcher.group(1) != null) {
                        String commentText = matcher.group(1).trim();
                        resultTextField.append("$$ : Mot clé commentaire\n" + commentText + " : commentaire\n");
                    } else {
                        resultTextField.append("$$ : Mot clé commentaire\n");
                        return;
                    }
                } else if (token.matches(regexEndOfLine)) {
                    if (skipLine) {
                        skipLine = false;
                        continue; 
                    }
                    resultTextField.append(token + " : Fin d'instruction\n");
                }
                else if (token.equals("$$")) {
                    skipLine = true; 
                }
                else if (token.matches(regexKeywordSnk_Begin)) {
                    resultTextField.append(token + " : Mot clé de début de programme\n");
                }
                else if (token.matches(regexKeywordGet)) {
                    resultTextField.append(token + " : Mot clé Get de programme\n");
                }
                else if (token.matches(regexKeywordSnk_End)) {
                    resultTextField.append(token + " : Mot clé de fin du programme\n");
                }
                else if (token.matches(regexKeywordSnk_Int)) {
                    resultTextField.append(token + " : Mot clé de déclaration du type entier\n");
                }
                else if (token.matches(regexKeywordSnk_Real)) {
                    resultTextField.append(token + " : Mot clé de déclaration du type réel\n");
                }
                else if (token.matches(regexKeywordSet)) {
                    resultTextField.append(token + " : Mot clé pour affectation d'une valeur\n");
                }
                else if (token.matches(regexKeywordIf)) {
                    resultTextField.append(token + " : Mot clé pour conditionnel\n");
                }
                else if (token.matches(regexKeywordElse)) {
                    resultTextField.append(token + " : Mot clé sinon pour condition\n");
                }
                else if (token.matches(regexKeywordFrom)) {
                    resultTextField.append(token + " : Mot clé from pour affectation entre variables\n");
                }
                else if (token.matches(regexKeywordBegin)) {
                    resultTextField.append(token + " : Mot clé pour Debut d'instructions\n");
                }
                else if (token.matches(regexKeywordEnd)) {
                    resultTextField.append(token + " : Mot clé pour Fin d'instructions\n");
                }
                else if (token.matches(regexKeywordSnk_Strg)) {
                    resultTextField.append(token + " : Mot clé pour Chaine de charactères\n");
                }
                else if (token.matches(regexKeywordSnk_Print)) {
                    resultTextField.append(token + " : Mot clé affichage\n");
                }
                else if (token.equals(regexSymbolComma)) {
                    resultTextField.append(token + " : Séparateur\n");
                }
                else if (token.matches(regexInteger)) {
                    resultTextField.append(token + " : Nombre entier\n");
                }
                else if (token.matches(regexIdentifier)) {
                    resultTextField.append(token + " : Identificateur\n");
                }
                else if (token.matches(regexOperator)) {
                    resultTextField.append(token + " : Opérateur de comparaisonn\n");
                }

                else if (token.matches(regexString)) {
                    resultTextField.append(token + " : Chaine de charactere\n");
                }

                else if (token.matches(regexSymbolDot)) {
                    resultTextField.append(token + " : Point des nombre réel\n");
                }
                else if (token.matches(regexSymbolLeftBracket)) {
                    resultTextField.append(token + " : Debut du condition\n");
                }
                else if (token.matches(regexSymbolRightBracket)) {
                    resultTextField.append(token + " : Fin du condition\n");
                }

                else {
                    resultTextField.append(token+" : Token non recconue: "  + "\n");
                }

            }
        }
    }


    private static int countIdentifiersAndCommas(String line) {

        String regexIdentifier = "[a-zA-Z][a-zA-Z0-9_]*";
        String regexSymbolComma = ",";

        String regex = String.format("(%s)|(%s)", regexSymbolComma, regexIdentifier);

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        int identifierCount = 0;
        int commaCount = 0;

        while (matcher.find()) {
            String token = matcher.group().trim();
            if (!token.isEmpty()) {
                if (token.matches(regexSymbolComma)) {
                    commaCount++;
                } else if (token.matches(regexIdentifier)) {
                    identifierCount++;
                }
            }
        }
        return identifierCount - commaCount;
    }



    private static int performSemanticAnalysis(String code) {

        int semanticErrors = 0;

        String[] lines = code.split("\n");

        Set<String> integerVariables = new HashSet<>();
        Set<String> realVariables = new HashSet<>();
        Set<String> stringVariables = new HashSet<>();
        Set<String> identifiers = new HashSet<>();

        for (String line : lines) {
            findDeclaredVariables(line, "Snk_Int", integerVariables);
            findDeclaredVariables(line, "Snk_Real", realVariables);
            findDeclaredVariables(line, "Snk_Strg", stringVariables);

            if (line.trim().startsWith("$$" ) || line.contains("\""))  {
                continue;
            }

            String[] words = line.split("\\s+"); 

            for (String word : words) {
                if (isKeyword(word)) {
                    continue;
                }

                if (isIdentifier(word)) {
                    identifiers.add(word);
                }
            }

            semanticErrors += typeMismatch(line, integerVariables, realVariables, identifiers);
        }

        // System.out.println("Integer Variables: " + formatVariableList(integerVariables));
        // System.out.println("Real Variables: " + formatVariableList(realVariables));
        // System.out.println("Identifiers: " + formatVariableList(identifiers));

        String[] integerVars = formatVariableList(integerVariables).split(",");
        String[] realVars = formatVariableList(realVariables).split(",");
        String[] identifiersvars = formatVariableList(identifiers).split(",");
        String[] stringVars = formatVariableList(stringVariables).split("\"");
        

        for(String var : integerVars) {
            var = var.trim();
        }
        for(String var : realVars) {
            var = var.trim();
        }
        for(String var : identifiersvars) {
            var = var.trim();
        }
        
        for(String var : identifiersvars) {
            boolean declared = false;
            for(String v : realVars) {
                if(var.trim().equals(v.trim())) declared = true;
            }
            for(String v : integerVars) {
                if(var.trim().equals(v.trim())) declared = true;
            }

            if(var.trim().equals(stringVars[0].trim())) declared = true;

            if (!declared) {
                resultTextField.append("Erreur sémantique: "+var +  " N'est pas declaré\n");
                semanticErrors++;
            }

        }
        return semanticErrors;
    }



    private static int typeMismatch(String line, Set<String> integerVariables, Set<String> realVariables, Set<String> identifiers) {
        int semanticErrors = 0;
        line = line.trim();
        line = line.replace("\t","");
        if (line.length() >= 3 && line.substring(0, 3).equals("Set")) {
            Set<String> identifiersInLine = getIdentifiersInLine(line);

            identifiersInLine.remove("Set");
            // System.out.println("Identifiers in Set line: " + identifiersInLine);
            String id = identifiersInLine.toString();
            boolean idIsInt = false;
            String[] integerVars = formatVariableList(integerVariables).split(",");
            int ll = id.length();
            id = id.substring(1, ll-1);
            for(String var : integerVars) {
                if(var.trim().equals(id)) idIsInt = true;
            }

            if(idIsInt && line.contains(".")) {
                resultTextField.append("Erreur sémantique: Incompatibilité de type : affectation d'une réel à un entier "+id+"\n");
                semanticErrors++;
            }

            
        } else if (line.length() >= 3 && line.substring(0, 3).equals("Get")) {
            Set<String> identifiersInLine = getIdentifiersInLine(line);
            identifiersInLine.remove("Get");
            identifiersInLine.remove("from");
            // System.out.println("Identifiers in Get line: " + identifiersInLine);

            String id = identifiersInLine.toString();
            int ll = id.length();
            id = id.substring(1, ll-1);
            String[]ids = id.split(",");
            String id_1 = ids[0];
            String id_2 = ids[1];
            id_2 = id_2.trim();
            // System.out.println(id_1);
            // System.out.println(id_2);

            boolean id_1IsInt = false;
            boolean id_2IsInt = false;

            String[] integerVars = formatVariableList(integerVariables).split(",");

            for(String var : integerVars) {
                if(var.trim().equals(id_1)) id_1IsInt = true;
            }
            for(String var : integerVars) {
                if(var.trim().equals(id_2)) id_2IsInt = true;
            }
            
            if((id_1IsInt && !id_2IsInt)) {
                resultTextField.append("Erreur sémantique : Incompatibilité de type : affectation d'un réel "+id_2+" à un entier "+id_1+"\n");
                semanticErrors++;
            }

        }
        return semanticErrors;
    }

    private static Set<String> getIdentifiersInLine(String line) {
        Set<String> identifiersInLine = new HashSet<>();
        String[] words = line.split("\\s+");

        for (String word : words) {
            if (isIdentifier(word)) {
                identifiersInLine.add(word);
            }
        }

        return identifiersInLine;
    }

    private static boolean isKeyword(String word) {
        String[] keywords = {"Snk_Begin", "Set", "Snk_End", "Snk_Int", "Snk_Real", "Get", "Else", "Begin", "from", "End", "Snk_Print", "If", "Snk_Strg"};
        for (String keyword : keywords) {
            if (word.equals(keyword)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isIdentifier(String word) {
        String regexIdentifier = "[a-zA-Z][a-zA-Z0-9_]*";
        return word.matches(regexIdentifier);
    }

    private static void findDeclaredVariables(String line, String keyword, Set<String> variables) {
        String regex = String.format("%s\\s+([^$]+)\\s*\\$", keyword);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            String variablesDeclaration = matcher.group(1).trim();
            String[] variableNames = variablesDeclaration.split("\\s*,\\s*");

            for (String variable : variableNames) {
                variables.add(variable);
            }
        }
    }

    private static String formatVariableList(Set<String> variables) {
        StringBuilder formattedList = new StringBuilder();
        for (String variable : variables) {
            formattedList.append(variable).append(", ");
        }
        if (formattedList.length() > 0) {
            formattedList.delete(formattedList.length() - 2, formattedList.length());
        }
        return formattedList.toString();
    }

    
}