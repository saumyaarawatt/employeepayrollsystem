package payroll.util;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TransactionLogger {
    private static final String LOG = "transactions.log";

    public static synchronized void log(String message) {
        try (FileWriter fw = new FileWriter(LOG, true);
             PrintWriter pw = new PrintWriter(fw)) {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pw.println(time + " - " + message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
