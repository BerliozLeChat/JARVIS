package plugins.simpleBase;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import client.Event;
import client.Frequence;
import client.IEvent;

public class CreateListener implements ActionListener {

	AgendaFrame frame;
	
	public CreateListener(AgendaFrame frame) {
		super();
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		List<String> fieldsContent = frame.getFieldsContent();
		
		if (fieldsContent.size() > 0){
			
			Class<?> cl;
			Method method;
			Field fields[] = Event.class.getDeclaredFields();
			Object contents[] = new Object[fields.length];
			Class<?> paramTypes[] = new Class<?>[fields.length];
			
			for (int i = 0 ; i < fields.length ; ++i) {
				paramTypes[i] = fields[i].getType();
				System.out.println("Content: " + fieldsContent.get(i) + " ; Expected: " + paramTypes[i] + " ; Class: " + fieldsContent.get(i).getClass()); 
				
				if (paramTypes[i].equals(Date.class)){
					contents[i] = new Date(fieldsContent.get(i));
				} else {
					contents[i] = (String) fieldsContent.get(i);
				}
			}
			
			Event event;
			try {
				
				Constructor<Event> m = Event.class.getConstructor(paramTypes);
				event = m.newInstance(contents);
				
				frame.getAgenda().addEvent(event);
				frame.refreshPrinter();
				
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else {
			//TODO: Replace it with a JDialog.
			System.out.println("Fill fields please.");
			//JDialog dial = new JDialog(frame, "Fill fields please.", true);
			//dial.setVisible(true);
		}
	}
	
	private String upFirstChar(String toUp){
		return toUp.substring(0, 1).toUpperCase() + toUp.substring(1);
	}

}
