package course.labs.dangerousapp;

import android.app.Activity;
import android.os.Bundle;

public class DangerousActivity extends Activity {

	private static final String DANGEROUS_ACTIVITY_ACTION = "course.labs.permissions.DANGEROUS_ACTIVITY";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

}
