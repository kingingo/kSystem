package eu.epicpvp.kSystem.Server.Creative;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.intellectualcrafters.plot.config.C;

import eu.epicpvp.kcore.Translation.TranslationHandler;

public class PlotSquarePrepare {

	public PlotSquarePrepare(){
		setC(C.PREFIX, TranslationHandler.getText("PREFIX"));
	}
	
	public void setC(C m, String def){
		try {
			Field f = C.class.getDeclaredField("s");
			f.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
		    modifiersField.setAccessible(true);
//		    modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
		    f.set(m, def);
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
}
