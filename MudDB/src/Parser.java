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
        Pattern showTablesPattern = Pattern.compile("(show)\\s*(tables)");
        Pattern createDbPattern = Pattern.compile("(create)\\s*(db)\\s*(\\w.*)");
        Pattern useDbPattern = Pattern.compile("(use)\\s*(db)\\s*(\\w.*)");
        Pattern dropDbPattern = Pattern.compile("(drop)\\s*(db)\\s*(\\w.*)");

        Pattern dropTablePattern = Pattern.compile("(drop)\\s*(table)\\s*(\\w.*)");
        Pattern createTablePattern = Pattern.compile("(create)\\s*(table)\\s*(\\w*)\\s*(fields)\\s*(\\w.*)");
        Pattern insertIntoTablePattern = Pattern.compile("(insert)\\s*(into)\\s*(\\w*)\\s*(\\w.*)");
        Pattern selectFromTablePattern = Pattern.compile("(select)\\s*(.*)\\s*(from)\\s*(\\w*)\\s*(?=(where)\\s*(.*)|).*");
        Pattern updateTablePattern = Pattern.compile("(update)\\s+(\\w+)\\s+(set)\\s+(.*?)(?:\\s+(?:where(.*))?)?$");
        Pattern deleteFromTablePattern = Pattern.compile("(delete)\\s+(from)\\s+(\\w+)\\s+(where)\\s+(.*?)?$");

        // 2) Matchers
        Matcher showTablesMatcher = showTablesPattern.matcher(query);
        Matcher showDbsMatcher = showDbsPattern.matcher(query);
        Matcher createDbMatcher = createDbPattern.matcher(query);
        Matcher useDbMatcher = useDbPattern.matcher(query);
        Matcher dropDbMatcher = dropDbPattern.matcher(query);

        Matcher dropTableMatcher = dropTablePattern.matcher(query);
        Matcher createTableMatcher = createTablePattern.matcher(query);
        Matcher insertIntoTableMatcher = insertIntoTablePattern.matcher(query);
        Matcher selectFromTableMatcher = selectFromTablePattern.matcher(query);
        Matcher updateTableMatcher = updateTablePattern.matcher(query);
        Matcher deleteFromTableMatcher = deleteFromTablePattern.matcher(query);

        // 3) Create object for response
        if (showDbsMatcher.matches()) {
            parsedObj.put("show_dbs" , showDbsMatcher);
        } else if (showTablesMatcher.matches()) {
            parsedObj.put("show_tables" , showTablesMatcher);
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
        } else if (deleteFromTableMatcher.matches()){
            parsedObj.put("delete_from_table", deleteFromTableMatcher);
        }
        else if(dropTableMatcher.matches()){
            parsedObj.put("drop_table" , dropTableMatcher);
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

    public String parseInsertingData(String activeDbName, String tblName, String fields){

        List<String> splittedFields = Arrays.asList(fields.split(","));

        JSONParser parser = new JSONParser();

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
        }
        if (resDataObj.size() == 0) {
            System.out.println("There are no records for insertion has been found.");
        } else {
            System.out.println("Inserted 1 record with " + resDataObj.size() + " fields");
        }
        return resDataObj.toString();

    }

}