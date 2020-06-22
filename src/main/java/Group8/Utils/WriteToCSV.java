package Group8.Utils;

import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;


/**
 * @author Sidney
 * This class writes values to csv files.
 */
public abstract class WriteToCSV {

    public static void writeOut(double[] numbers, String fName) throws IOException {
        try (
                Writer writer = Files.newBufferedWriter(Paths.get(String.format("./%s.csv",fName)));

                CSVWriter csvWriter = new CSVWriter(writer,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);
        ) {
            for(double number: numbers){
                csvWriter.writeNext(new String[]{Double.toString(number)});
            }
        }
    }
    public static void writeOut(int numberIntruders, int numberGuards, int runs, int guardWins, String mapName, String fName) {
        try (
                Writer writer = Files.newBufferedWriter(Paths.get(String.format("./%s.csv",fName)));

                CSVWriter csvWriter = new CSVWriter(writer,
                        CSVWriter.DEFAULT_SEPARATOR,
                        CSVWriter.NO_QUOTE_CHARACTER,
                        CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        CSVWriter.DEFAULT_LINE_END);
        ) {
            csvWriter.writeNext(new String[]{Integer.toString(numberIntruders), Integer.toString(numberGuards),
                    Integer.toString(runs), Integer.toString(guardWins), mapName});

        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }




}