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

        // Regex patterns
        Pattern showDbsPattern = Pattern.compile("(show)\\s*(dbs)");
        Pattern createDbPattern = Pattern.compile("(create)\\s*(db)\\s*(\\w.*)");

        // Matchers
        Matcher showDbsMatcher = showDbsPattern.matcher(query);
        Matcher createDbMatcher = createDbPattern.matcher(query);

        if (showDbsMatcher.matches()) {
            parsedObj.put("show_dbs" , showDbsMatcher);
        } else if (createDbMatcher.matches()) {
            parsedObj.put("create_db" , createDbMatcher);
        }

        return parsedObj;
    }

}