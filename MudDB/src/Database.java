import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * Created by eugene_vilder on 2016-05-29.
 */
public class Database {

    private static String activeDbName;
    private static Constants CONSTANT = new Constants();
    private static DatabaseService dbService = new DatabaseService();

    private static void createDB(String dbName){
        File theDir = new File(CONSTANT.DEFAULT_PATH + "/" + dbName);

        if (!theDir.exists()) {
            try{
                theDir.mkdir();
                System.out.println("Database <" + dbName + "> has been created");
            }
            catch(SecurityException se){
                System.out.println("ERROR: " + se.toString());
            }
        } else {
            System.out.println("Warning: Database <" + dbName + "> is already exists.");
        }
    }


    public static void runQuery(String query){
        Parser qp = new Parser();
        Map<String, Matcher> parsedObj = new HashMap<>();

        parsedObj = qp.getQueryCommandIndex(query);

        // Get action and matches from parsed query
        Map.Entry<String,Matcher> entry=parsedObj.entrySet().iterator().next();
        String action = entry.getKey();
        Matcher matches = entry.getValue();

        switch (action){
            case "create_db" :
                createDb(matches.group(3));
                break;
            case "show_dbs" :
                showDbs();
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


}
