import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eugene_vilder on 2016-05-29.
 */
public class Parser {

    public Map<String, Matcher> getQueryCommandIndex(String query){

        Map<String, Matcher> parsedObj = new HashMap<>();

        // 1) Regex patterns
        Pattern showDbsPattern = Pattern.compile("(show)\\s*(dbs)");
        Pattern createDbPattern = Pattern.compile("(create)\\s*(db)\\s*(\\w.*)");
        Pattern useDbPattern = Pattern.compile("(use)\\s*(db)\\s*(\\w.*)");
        Pattern dropDbPattern = Pattern.compile("(drop)\\s*(db)\\s*(\\w.*)");

        // 2) Matchers
        Matcher showDbsMatcher = showDbsPattern.matcher(query);
        Matcher createDbMatcher = createDbPattern.matcher(query);
        Matcher useDbMatcher = useDbPattern.matcher(query);
        Matcher dropDbMatcher = dropDbPattern.matcher(query);

        // 3) Create object for response
        if (showDbsMatcher.matches()) {
            parsedObj.put("show_dbs" , showDbsMatcher);
        } else if (createDbMatcher.matches()) {
            parsedObj.put("create_db" , createDbMatcher);
        } else if(useDbMatcher.matches()){
            parsedObj.put("use_db", useDbMatcher);
        } else if(dropDbMatcher.matches()){
            parsedObj.put("drop_db", dropDbMatcher);
        }

        return parsedObj;
    }

}