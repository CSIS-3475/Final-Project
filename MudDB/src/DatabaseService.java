import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by eugene_vilder on 2016-05-31.
 */
public class DatabaseService {

    private static Constants CONSTANT = new Constants();

    public static void createDataFolderIfNotExist(){
        File theDir = new File(CONSTANT.DEFAULT_PATH);

        if (!theDir.exists()) {
            System.out.print("Creating working directory....");

            try{
                theDir.mkdir();
                System.out.println("OK");
            }
            catch(SecurityException se){
                System.out.println("ERROR: " + se.toString());
            }
        } else {
            System.out.println("All set. Ready to go.");
        }
    }

    public static void displayIntro() {
        System.out.println("==========================");
        System.out.println("Welcome to " + CONSTANT.PROJECT_NAME + ".");;
        System.out.println("==========================");
    }


    public static void displayExitMessage() {
        System.out.println("==========================");
        System.out.println("Thank you for using " + CONSTANT.PROJECT_NAME + ".");
        System.out.println("Goodbye!");
    }

    public static String[] getFoldersList(String path){
        File file = new File(path);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                System.out.println("==========================");
                return new File(current, name).isDirectory();
            }
        });
        //System.out.println(Arrays.toString(directories));
        return directories;

    }

    public static String[] getFilesList(String path){
        File file = new File(path);
        String[] directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isFile();
            }
        });
        System.out.println(directories.toString());
        return directories;

    }

    public static boolean isDatabaseExists(String dbName){
        String[] directories = getFoldersList(CONSTANT.DEFAULT_PATH + "/" + dbName);
        return !(directories.length != 0);
    }
}
