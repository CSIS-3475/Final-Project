import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Created by eugene_vilder on 2016-05-29.
 */
public class Database {

    private static String activeDbName;
    private static Constants CONSTANT = new Constants();
    private static DatabaseService dbService = new DatabaseService();


    public static void runQuery(String query){
        String whereCondition;
        Parser qp = new Parser();
        Map<String, Matcher> parsedObj = new HashMap<>();

        parsedObj = qp.getQueryCommandIndex(query);

        // Get action and matches from parsed query
        Map.Entry<String,Matcher> entry = parsedObj.entrySet().iterator().next();
        String action = entry.getKey();
        Matcher matches = entry.getValue();

        switch (action){
            case "create_db" :
                createDb(matches.group(3));
                break;
            case "show_dbs" :
                showDbs();
                break;
            case "use_db" :
                useDb(matches.group(3));
                break;
            case "drop_db" :
                dropDb(matches.group(3));
                break;
            case "create_table" :
                createTable(matches.group(3), matches.group(5));
                break;
            case "insert_into_table" :
                insertIntoTable(matches.group(3), matches.group(4));
                break;
            case "delete_from_table" :
                deleteFromTable(matches.group(3), matches.group(5));
                break;
            case "select_from_table" :
                whereCondition = null;
                try {
                    whereCondition = matches.group(6);
                } catch (Exception e){

                }
                selectFromTable(matches.group(2), matches.group(4), whereCondition);
                break;
            case "update_table" :
                whereCondition = null;
                try {
                    whereCondition = matches.group(5);
                } catch (Exception e){

                }
                updateTable(matches.group(4), matches.group(2), whereCondition);
                break;
            default: System.out.println("Unknown query: [ " + query + " ]");
        }

        System.out.println(CONSTANT.LINE_DELIMETER);
    }

    public static void connect(){
        System.out.println("Starting " + CONSTANT.PROJECT_NAME + "...");
        dbService.createDataFolderIfNotExist();
        dbService.displayIntro();
    }

    public static void disconnect(){
        dbService.displayExitMessage();
    }

    private static void createDb(String dbName) {

        File theDir = new File(CONSTANT.DEFAULT_PATH + "/" + dbName);

        if (!theDir.exists()) {
            try{
                theDir.mkdir();
                System.out.println("Database <" + dbName + "> has been created.");
            }
            catch(SecurityException se){
                System.out.println("ERROR: " + se.toString());
            }
        } else {
            System.out.println("Database <" + dbName + "> is already exists.");
        }

    }

    private static void showDbs() {
        String[] directories = dbService.getFoldersList(CONSTANT.DEFAULT_PATH);
        for (String dir : directories) {
            System.out.println(dir);
        }
        if (directories.length == 0){
            System.out.println("No databases found");
        }
    }

    private static void useDb(String dbName){
        String[] directories = dbService.getFoldersList(CONSTANT.DEFAULT_PATH);

        for (String dir : directories){
            if (dir.equals(dbName)){
                System.out.println("Database <" + dbName + "> is in use");
                activeDbName = dbName;
            }
            else{
                System.out.println("Database <" + dbName + "> was not found");
            }
        }
    }

    public static void dropDb(String dbName) {

        String[] directories = dbService.getFoldersList(CONSTANT.DEFAULT_PATH);

        if (directories.length != 0) {
            for (String dir : directories) {
                if (dir.equals(dbName)) {
                    File curFile = new File(CONSTANT.DEFAULT_PATH, dbName);
                    File[] contents = curFile.listFiles();
                    if (contents != null) {
                        for (File f : contents) {
                            f.delete();
                        }
                    }
                    curFile.delete();
                    System.out.println("Database <" + dbName + "> was successfully deleted");

                }
                else if (dir.lastIndexOf(dbName) == -1) {
                    System.out.println("Database with name <" + dbName + "> was not found!");
                }
            }
        }
        else {
            System.out.println("No databases were found!"); // not working
        }
    }

    private static void createTable(String tblName, String fields){

        if (activeDbName != null) {
            String dirName = CONSTANT.DEFAULT_PATH + "/" + activeDbName;
            File theDir = new File(dirName);

            if (theDir.exists()) {
                try {

                    // Create table file
                    PrintWriter writer = null;
                    try {
                        writer = new PrintWriter(dirName + "/" + tblName + Constants.TABLE_SUFFIX + Constants.JSON_SUFFIX, "UTF-8");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    writer.close();

                    // Create table meta file
                    PrintWriter writer_meta = null;
                    try {
                        writer_meta = new PrintWriter(dirName + "/" + tblName + Constants.TABLE_META_SUFFIX + Constants.JSON_SUFFIX, "UTF-8");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Parser p = new Parser();
                    writer_meta.println(p.parseFields(fields));
                    writer_meta.close();

                    System.out.println("Table <" + tblName + "> has been created");
                } catch (SecurityException se) {
                    System.out.println("ERROR: " + se.toString());
                }
            } else {
                System.out.println("Warning: Table <" + tblName + "> is already exists.");
            }
        } else {
            System.out.println("Warning: No database selected.");
        }
    }

    private static void insertIntoTable(String tblName, String fieldsData){

        if (activeDbName != null) {
            String dirName = CONSTANT.DEFAULT_PATH + "/" + activeDbName;
            File theDir = new File(dirName);
            String fileName = dirName + "/" + tblName + Constants.TABLE_SUFFIX + Constants.JSON_SUFFIX;

            if (!theDir.exists()) {
                createTable(tblName, fieldsData);
            }

            try(FileWriter fw = new FileWriter(fileName, true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                Parser p = new Parser();
                out.println(p.parseInsertingData(activeDbName, tblName, fieldsData));
                out.close();
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
        } else {
            System.out.println("Warning: No database selected.");
        }
    }

    private static void selectFromTable(String fieldsData, String tblName, String whereCondition){

        fieldsData = fieldsData.trim();

        ArrayList<String> result = new ArrayList<>();
        List<String> fields = null;

        if (fieldsData.equals("*")) {
        } else {
            fields = Arrays.asList(fieldsData.split(","));
        }

        result = readFromTableFile(tblName, fields, whereCondition);

        outputResultOfSelection(result);

    }

    private static void outputResultOfSelection(ArrayList<String> result){
        if (result.size() == 0) {
            System.out.println("No records has been found.");
        } else {
            System.out.println(result.toString());
        }
    }

    private static void updateTable(String fieldsData, String tblName, String whereCondition){
        fieldsData = fieldsData.trim();
        tblName = tblName.trim();
        whereCondition = whereCondition != null ? whereCondition.trim() : null;

        List<String> fields = Arrays.asList(fieldsData.split(","));

        String[] whereConditionArray;
        String whereKey = "";
        String whereValue = "";
        Boolean skipRow;
        int updatedRows = 0;

        // The name of the file to open.
        String fileName = CONSTANT.DEFAULT_PATH + "/" + activeDbName + "/" + tblName + CONSTANT.TABLE_SUFFIX + CONSTANT.JSON_SUFFIX;
        String lockFileName = CONSTANT.DEFAULT_PATH + "/" + activeDbName + "/" + tblName + CONSTANT.TABLE_SUFFIX + CONSTANT.TEMP_TABLE_FILE_SUFFIX;

        // This will reference one line at a time
        String line = null;
        ArrayList<String> result = new ArrayList<>();

        if (whereCondition != null && whereCondition.length() > 0) {
            whereConditionArray = whereCondition.split("=");
            whereKey = whereConditionArray[0];
            whereValue = whereConditionArray[1];
        }

        try {
            // FileReader reads text files in the default encoding.
            FileWriter lockedFileReader = new FileWriter(lockFileName);
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                skipRow = false;

                //row = new ArrayList<>();
                JSONParser parser = new JSONParser();

                //convert from JSON string to JSONObject
                JSONObject newJObject = null;
                try {
                    newJObject = (JSONObject) parser.parse(line);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (whereCondition != null && whereCondition.length()>0 && whereKey.length()>0) {
                    if (newJObject.get(whereKey) == null || ( newJObject.get(whereKey) != null && !whereValue.equals(newJObject.get(whereKey).toString()) )) {
                        skipRow = true;
                    }
                }

                if (!skipRow) {

                    if (fields != null) {
                        JSONObject tmpJSON = new JSONObject();
                        for (String fieldsKeyValue : fields) {
                            fieldsKeyValue = fieldsKeyValue.trim();
                            List<String> fieldKeyValue = Arrays.asList(fieldsKeyValue.split(","));

                            for (String field : fieldKeyValue) {
                                List<String> f = Arrays.asList(field.split("="));
                                String f_key = f.get(0).trim();
                                String f_val = f.get(1).trim();

                                if (!f_key.equals(CONSTANT.$_ID)) {
                                    if (!f_val.equals("null")) {
                                        newJObject.put(f_key, f_val);
                                    } else {
                                        newJObject.remove(f_key);
                                    }
                                }
                            }
                        }
                    }
                    updatedRows++;
                    lockedFileReader.write(newJObject.toJSONString());
                } else {
                    lockedFileReader.write(line);
                }

                lockedFileReader.write(System.lineSeparator());

                renameFilename(lockFileName, fileName);

            }

            bufferedReader.close();
            lockedFileReader.close();

            if (updatedRows==0) {
                System.out.println("No records have been updated.");
            } else {
                System.out.println(updatedRows + " records have been updated.");
            }
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open table '" + tblName + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading table '" + tblName + "'");
        }

       // return result;
    }

    private static void deleteFromTable(String tblName, String whereCondition){

        tblName = tblName.trim();
        whereCondition = whereCondition != null ? whereCondition.trim() : null;

        if (whereCondition == null) {
            System.out.println("Error: No WHERE clause has been provided.");
            return;
        }

        List<String> whereConditionKeyValue = Arrays.asList(whereCondition.split("="));

        String whereKey = whereConditionKeyValue.get(0);
        String whereValue = whereConditionKeyValue.get(1);
        Boolean skipRow;

        String fileName = CONSTANT.DEFAULT_PATH + "/" + activeDbName + "/" + tblName + CONSTANT.TABLE_SUFFIX + CONSTANT.JSON_SUFFIX;
        String lockFileName = CONSTANT.DEFAULT_PATH + "/" + activeDbName + "/" + tblName + CONSTANT.TABLE_SUFFIX + CONSTANT.TEMP_TABLE_FILE_SUFFIX;

        String line = null;

        int deletedRowsCntr = 0;

        try {
            // FileReader reads text files in the default encoding.
            FileWriter lockedFileReader = new FileWriter(lockFileName);
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                skipRow = false;

                //row = new ArrayList<>();
                JSONParser parser = new JSONParser();

                //convert from JSON string to JSONObject
                JSONObject newJObject = null;
                try {
                    newJObject = (JSONObject) parser.parse(line);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (newJObject.get(whereKey) != null && whereValue.equals(newJObject.get(whereKey).toString())) {
                    skipRow = true;
                    deletedRowsCntr++;
                }

                if (!skipRow) {
                    lockedFileReader.write(line);
                    lockedFileReader.write(System.lineSeparator());
                }

                renameFilename(lockFileName, fileName);

            }

            bufferedReader.close();
            lockedFileReader.close();

            if (deletedRowsCntr==0) {
                System.out.println("No records has been found.");
            } else {
                System.out.println("Removed " + deletedRowsCntr + " records.");
            }
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open table '" + tblName + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading table '" + tblName + "'");
        }

    }

    private static void renameFilename (String lockFileName, String fileName){
        File oldName = new File(lockFileName);
        File newName = new File(fileName);
        oldName.renameTo(newName);
    }

    private static ArrayList<String> readFromTableFile(String tblName, List<String> fieldsData, String whereCondition) {
        String[] whereConditionArray;
        String whereKey = "";
        String whereValue = "";
        Boolean skipRow;

        // The name of the file to open.
        String fileName = CONSTANT.DEFAULT_PATH + "/" + activeDbName + "/" + tblName + CONSTANT.TABLE_SUFFIX + CONSTANT.JSON_SUFFIX;

        // This will reference one line at a time
        String line = null;
        ArrayList<String> result = new ArrayList<>();
       // HashMap<String, ArrayList<String>> result = new HashMap<>();

        if (whereCondition != null && whereCondition.length() > 0) {
            whereConditionArray = whereCondition.split("=");
            whereKey = whereConditionArray[0];
            whereValue = whereConditionArray[1];
        }


        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                skipRow = false;

                //row = new ArrayList<>();
                JSONParser parser = new JSONParser();

                //convert from JSON string to JSONObject
                JSONObject newJObject = null;
                try {
                    newJObject = (JSONObject) parser.parse(line);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

               // System.out.println("+++" + whereValue + " - -" + (newJObject.get(whereKey).toString() != whereValue));

                if (whereCondition != null && whereCondition.length()>0 && whereKey.length()>0) {
                    if (newJObject.get(whereKey) == null || ( newJObject.get(whereKey) != null && !whereValue.equals(newJObject.get(whereKey).toString()) )) {
                        skipRow = true;
                    }
                }

                if (!skipRow) {
                    if (fieldsData != null) {
                        JSONObject tmpJSON = new JSONObject();
                        for (String f : fieldsData) {
                            f = f.trim();
                            if (newJObject.get(f) != null) {
                                tmpJSON.put(f, newJObject.get(f));
                            }
                        }
                        newJObject = tmpJSON;
                    }

                    result.add(newJObject.toJSONString());
                }

            }

            // Always close files.
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open table '" + tblName + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading table '" + tblName + "'");
        }

        return result;
    }

}

