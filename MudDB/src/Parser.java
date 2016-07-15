import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eugene_vilder on 2016-05-29.
 */
public class Parser {

    private static Constants CONSTANT = new Constants();

    public Map<String, Matcher> getQueryCommandIndex(String query){

        Map<String, Matcher> parsedObj = new HashMap<>();

        // 1) Regex patterns
        Pattern showDbsPattern = Pattern.compile("(show)\\s*(dbs)");
        Pattern createDbPattern = Pattern.compile("(create)\\s*(db)\\s*(\\w.*)");
        Pattern useDbPattern = Pattern.compile("(use)\\s*(db)\\s*(\\w.*)");
        Pattern dropDbPattern = Pattern.compile("(drop)\\s*(db)\\s*(\\w.*)");

        Pattern createTablePattern = Pattern.compile("(create)\\s*(table)\\s*(\\w*)\\s*(fields)\\s*(\\w.*)");
        Pattern insertIntoTablePattern = Pattern.compile("(insert)\\s*(into)\\s*(\\w*)\\s*(\\w.*)");
        Pattern selectFromTablePattern = Pattern.compile("(select)\\s*(.*)\\s*(from)\\s*(\\w*)\\s*(?=(where)\\s*(.*)|).*");
        Pattern updateTablePattern = Pattern.compile("(update)\\s+(\\w+)\\s+(set)\\s+(.*?)(?:\\s+(?:where(.*))?)?$");
        //Pattern updateTablePattern = Pattern.compile("(update)\\s*(.*)\\s*(set)\\s*(\\w*=\\w*)\\s*(?=(where)\\s*(.*)|).*");

        // 2) Matchers
        Matcher showDbsMatcher = showDbsPattern.matcher(query);
        Matcher createDbMatcher = createDbPattern.matcher(query);
        Matcher useDbMatcher = useDbPattern.matcher(query);
        Matcher dropDbMatcher = dropDbPattern.matcher(query);

        Matcher createTableMatcher = createTablePattern.matcher(query);
        Matcher insertIntoTableMatcher = insertIntoTablePattern.matcher(query);
        Matcher selectFromTableMatcher = selectFromTablePattern.matcher(query);
        Matcher updateTableMatcher = updateTablePattern.matcher(query);

        // 3) Create object for response
        if (showDbsMatcher.matches()) {
            parsedObj.put("show_dbs" , showDbsMatcher);
        } else if (createDbMatcher.matches()) {
            parsedObj.put("create_db" , createDbMatcher);
        } else if (useDbMatcher.matches()){
            parsedObj.put("use_db", useDbMatcher);
        } else if (dropDbMatcher.matches()){
            parsedObj.put("drop_db", dropDbMatcher);
        } else if (createTableMatcher.matches()){
            parsedObj.put("create_table", createTableMatcher);
        } else if (insertIntoTableMatcher.matches()){
            parsedObj.put("insert_into_table", insertIntoTableMatcher);
        } else if (selectFromTableMatcher.matches()){
            parsedObj.put("select_from_table", selectFromTableMatcher);
        } else if (updateTableMatcher.matches()){
            parsedObj.put("update_table", updateTableMatcher);
        }

        return parsedObj;
    }

    public String parseFields(String fields){

        List<String> splittedFields = Arrays.asList(fields.split(","));

        JSONObject obj = new JSONObject();

        for(String field : splittedFields){
            field = field.trim();
            List<String> fieldItem = Arrays.asList(field.split(" "));
            String fieldName = fieldItem.get(0).trim();
            String fieldType = fieldItem.get(1).trim();

            obj.put(fieldName, fieldType);
        }

        StringWriter out = new StringWriter();
        try {
            obj.writeJSONString(out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String jsonText = out.toString();

        return jsonText;
    }

    /*private String readMetaFile(String activeDbName, String tblName){

        String line="";
        String res="";
        String dirName = Constants.DEFAULT_PATH + "/" + activeDbName;
        String fileName = dirName + "/" + tblName + Constants.TABLE_META_SUFFIX + Constants.JSON_SUFFIX;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                res += line;
            }
            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        return res;
    }
*/

    public String parseInsertingData(String activeDbName, String tblName, String fields){

       // String metaData = readMetaFile(activeDbName, tblName);
        List<String> splittedFields = Arrays.asList(fields.split(","));

        JSONParser parser = new JSONParser();

       /* Object metaObj = null;
        try {
            metaObj = parser.parse(metaData);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONObject jMetaObject = (JSONObject) metaObj;
        */

        JSONObject resDataObj = new JSONObject();

        // Create field ID with unique value
        resDataObj.put(CONSTANT.$_ID, UUID.randomUUID().toString());

        for(String field : splittedFields){
            field = field.trim();
            List<String> splittedSingleField = Arrays.asList(field.split("="));
            String fieldName = splittedSingleField.get(0).trim();
            String fieldValue = splittedSingleField.get(1).trim();

            fieldValue = fieldValue.replaceAll("\'","");
            resDataObj.put(fieldName, fieldValue);

            /*String fieldType = (String) jMetaObject.get(fieldName);
            if (fieldType == null) {
                fieldType = "string";
            }
            System.out.println("--- " + fieldType + " = " + fieldName);

            switch (fieldType){
                case "int":
                    resDataObj.put(fieldName, new Integer(fieldValue));
                    break;
                default:
                    // Added as String by default
                    fieldValue = fieldValue.replaceAll("\'","");
                    resDataObj.put(fieldName, fieldValue);
            }*/
        }

        return resDataObj.toString();

    }

}