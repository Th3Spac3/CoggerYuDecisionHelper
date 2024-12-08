package MCD;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CSVReader {
    public static Object[][] ReadCSV(String path){

        List<List<Object>> records = new ArrayList<>();
        try(Scanner scanner = new Scanner(new File(path))){
            while (scanner.hasNextLine()) records.add(getRecordFromLine(scanner.nextLine()));
        } catch (FileNotFoundException ex){
            ex.printStackTrace();
        }
        Object[][] result = new Object[records.size()][records.get(0).size()];
        for(int i = 0; i < result.length; i++) result[i] = records.get(i).toArray();
        return result;
    }
    private static List<Object> getRecordFromLine(String line){
        List<Object> values = new ArrayList<Object>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }
}
