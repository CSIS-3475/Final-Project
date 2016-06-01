import java.util.Scanner;

/**
 * Created by eugene_vilder on 2016-05-28.
 */
public class MudDB {

    public MudDB(){
        Constants c = new Constants();

        String cmd;
        Boolean done = false;
        //Map<String, String> queryData;

        Database db = new Database();

        db.connect();

        do {

            cmd = getInput();

            if (cmd.compareTo(c.EXIT_WORD) != 0) {
                System.out.println(c.PROJECT_NAME + "> " + cmd);

                db.runQuery(cmd);



            } else {
                done = true;
            }
        } while (!done);

        db.disconnect();
    }

    public static String getInput() {
        Scanner keyboard = new Scanner(System.in);
        String cmd = keyboard.nextLine();
        cmd = cmd.length() == 0 ? "" : cmd;
        return cmd;
    }

}
