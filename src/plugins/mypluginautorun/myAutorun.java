package plugins.mypluginautorun;

import client.Agenda;
import client.Event;
import client.IAgenda;
import platform.IPluginDescriptor;
import platform.Platform;
import platform.plugins.IAutorun;
import platform.plugins.IPrinter;

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

/**
 * Created by francois on 06/04/17.
 */
public class myAutorun implements IAutorun {

    private IPrinter printer;

    public void loadPrinter() {
        List<IPluginDescriptor> listPluginDescriptor;
        try {
            listPluginDescriptor = Platform.getExtensions(IPrinter.class);
            for(int index = 0;index<listPluginDescriptor.size();index++) {
                System.out.println(index+" : "+listPluginDescriptor.get(index)
                        .getProperties().get("name"));
            }
            Scanner scan = new Scanner(System.in);
            int choix = scan.nextInt();
            scan.close();
            this.printer = (IPrinter) Platform.loadPlugin(
                    listPluginDescriptor.get(choix), IPrinter.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        System.out.println("HelloWorld !");
        loadPrinter();
        IAgenda a = new Agenda();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        try {
            a.addEvent(new Event("Event1", formatter.parse("06/01/2017"),
                    formatter.parse("07/01/2017"), "Anniversaire", "Anniversaire de Keltoum", "Chez moi"));
            a.addEvent(new Event("Event2", formatter.parse("06/02/2017"),
                    formatter.parse("07/02/2017"), "Anniversaire", "Anniversaire de Yasmine", "Chez moi"));
            a.addEvent(new Event("Event3", formatter.parse("06/03/2017"),
                    formatter.parse("07/03/2017"), "Anniversaire", "Anniversaire de Margaux", "Chez moi"));
            a.addEvent(new Event("Event4", formatter.parse("06/04/2017"), formatter.parse("07/04/2017"), "Anniversaire", "Anniversaire de Marwan", "Chez moi"));
            a.addEvent(new Event("Event5", formatter.parse("06/05/2017"), formatter.parse("07/05/2017"), "Anniversaire", "Anniversaire de Pper", "Chez moi"));
            a.addEvent(new Event("Event6", formatter.parse("06/06/2017"), formatter.parse("07/06/2017"), "Anniversaire", "Anniversaire de Khemi", "Chez moi"));
            a.addEvent(new Event("Event7", formatter.parse("06/07/2017"), formatter.parse("07/07/2017"), "Anniversaire", "Anniversaire de Samy", "Chez moi"));
            a.addEvent(new Event("Event8", formatter.parse("06/08/2017"), formatter.parse("07/08/2017"), "Anniversaire", "Anniversaire de Quentin", "Chez moi"));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        JPanel pan = this.printer.display(a,null);
        JFrame frame = new JFrame();
        frame.setTitle("Mon ptit Autorun");
        frame.setSize(500, 500);
        frame.setVisible(true);
        frame.add(pan);
    }
}
