package ivan.kovalenko;

import ivan.kovalenko.model.LocalData;
import android.app.Application;

public class MyApplication extends Application {
	LocalData ld = new LocalData();

	public LocalData getLocalData() {
		return ld;
	}
}
