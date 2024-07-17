package main.studynote;

import java.io.File;
import java.io.IOException;

public class FileFunction {
    public static boolean createFolder( String des, String name) {
        return new File(des + '/' + name).mkdir();
    }

    public static boolean createNewFile(String des, String name) throws IOException {
        return new File(des + '/' + name).createNewFile();
    }
}
