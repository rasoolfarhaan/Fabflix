import java.io.*;
import java.util.Scanner;

public class LogWriter {
    public static void main(String[] args) {
        String inputFile = "/Users/alexhuang/mycs122b-projects/message.txt"; // Replace with your input file name
        String outputFile = "/Users/alexhuang/mycs122b-projects/messageOutput.txt";

        try {
            File inFile = new File(inputFile);
            Scanner scanner = new Scanner(inFile);
            FileWriter writer = new FileWriter(outputFile);

            int count = 0;
            double sum1 = 0, sum2 = 0, sum3 = 0;
            String l = scanner.nextLine();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    // Get the last three numbers from the line
                    int num1 = Integer.parseInt(parts[parts.length - 3]);
                    int num2 = Integer.parseInt(parts[parts.length - 2]);
                    int num3 = Integer.parseInt(parts[parts.length - 1]);

                    sum1 += num1;
                    sum2 += num2;
                    sum3 += num3;
                    count++;

                    String outputLine = num1 + "," + num2 + "," + num3 + "\n";
                    writer.write(outputLine);
                }
            }

            if (count > 0) {
                double avg1 = sum1 / count;
                double avg2 = sum2 / count;
                double avg3 = sum3 / count;
                System.out.println("Average: " + avg1 + ", " + avg2 + ", " + avg3 + "\n");
            }

            scanner.close();
            writer.close();
            System.out.println("Log file created successfully with averages.");
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
        } catch (NumberFormatException e) {
            System.out.println("Error parsing numbers from the file.");
        }
    }
}