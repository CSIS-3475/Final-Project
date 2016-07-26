import java.util.Scanner;

/**
 * Created by eugene_vilder on 2016-05-28.
 */
public class MudDB {

    public MudDB(){
        Constants c = new Constants();
        String cmd;
        Boolean done = false;
        Database db = new Database();

        db.connect();



        db.runQuery("use db apple");
        db.runQuery("show tables");
        //db.runQuery("create table test fields f_name string, l_name string, age int");

        /*db.runQuery("insert into test f_name=Eugene, l_name=Vilder, age=38");
        db.runQuery("insert into test f_name=Eugene");
        db.runQuery("insert into test f_name=Sean, class=CSIS3450");
        db.runQuery("select * from test where age=318");*/
        //db.runQuery("select * from test");
        //db.runQuery("select * from test where age=123");
        //db.runQuery("update test set age=2 where age=2");
        //db.runQuery("delete from test where age=38");


       /* do {

            try{
                cmd = getInput();

                if (cmd.compareTo(c.EXIT_WORD) != 0) {
                    System.out.println(c.PROJECT_NAME + "> " + cmd);

                    db.runQuery(cmd);

                } else {
                    done = true;
                }
            } catch(Exception ex){
                System.out.println("\n==================================================================================");
                System.out.println("The command is invalid or spelled incorrectly. Please follow the DB documentation.");
                System.out.println("==================================================================================\n");
            }

        } while (!done);*/

        db.disconnect();
    }

    public static String getInput() {
        Scanner keyboard = new Scanner(System.in);
        String cmd = keyboard.nextLine();
        cmd = cmd.length() == 0 ? "" : cmd;
        return cmd;
    }

}
