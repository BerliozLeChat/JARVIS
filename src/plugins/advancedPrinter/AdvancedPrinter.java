package plugins.advancedPrinter;

import client.IAgenda;
import client.IEvent;
import platform.plugins.IPrinter;
import plugins.simpleBase.AgendaFrame;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;

/**
 * Created by francois on 06/04/17.
 */

public class AdvancedPrinter implements IPrinter {

    @Override
    public JPanel display(IAgenda a, AgendaFrame frame) {

        JPanel panel = new JPanel();
        JLabel label;
        GridLayout grid = new GridLayout(a.getEvents().size(), 1);
        String event_label;

        panel.setLayout(grid);

        Calendar today = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();

        for (IEvent event : a.getEvents()) {
            start.setTime(event.getStartDate());
            end.setTime(event.getEndDate());

            event_label = event.getName()+" From "+start.get(Calendar
                    .DAY_OF_MONTH)+"/"+String.valueOf(start.get(Calendar.MONTH)
                    +1)+"/"+start
                    .get(Calendar.YEAR)+"" +
                    " " +
                    " To "+end.get(Calendar
                    .DAY_OF_MONTH)+"/"+String.valueOf(end.get(Calendar.MONTH)+1)
                    +"/"+end
                    .get(Calendar.YEAR)+" "+event.getType();


            label = new JLabel(event_label);

            if(today.after(end))
                label.setForeground(Color.RED);

            if(today.before(start))
                label.setForeground(Color.GREEN);

            panel.add(label);
        }

        return panel;
    }
}
