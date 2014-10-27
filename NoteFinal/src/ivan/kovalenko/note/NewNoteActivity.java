package ivan.kovalenko.note;

import ivan.kovalenko.MyApplication;
import ivan.kovalenko.api.APIException;
import ivan.kovalenko.api.APIUsers;
import ivan.kovalenko.api.APIUsers.CreateNoteResponse;
import ivan.kovalenko.api.APIUtils;
import ivan.kovalenko.database.Provider;
import ivan.kovalenko.database.NoteDatabaseColumns.TableNote;
import ivan.kovalenko.login.MainActivity;
import ivan.kovalenko.notenote.R;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class NewNoteActivity extends Activity implements LoaderCallbacks<CreateNoteResponse>{
	protected APIUsers API;
	protected EditText textNote;
	protected EditText titleNote;
	protected Bundle   bundle;
	protected String[] value;

	private static final String KYE_NOTE = "value";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_note_activity);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		API = new APIUsers();
		
		textNote  = (EditText) findViewById(R.id.textNote);
		titleNote = (EditText) findViewById(R.id.titleNote); 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.new_note_menu, menu);
		return super.onCreateOptionsMenu(menu);
		
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		 
		value = new String [3];
		value[0] = titleNote.getText().toString();
		value[1] = textNote.getText().toString();
		value[2] = ((MyApplication)getApplication()).getLocalData().getSessionId();
		
		bundle = new Bundle();
		bundle.putStringArray(KYE_NOTE, value);
		
		switch (item.getItemId()) {
		case R.id.action_save_new_note:
			getLoaderManager().initLoader(1, bundle, this).forceLoad();
			return true;
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
		
	protected static APIException apiexception;
	
	public static class NewNoteAsyncTask extends AsyncTaskLoader<CreateNoteResponse>{
		String [] values;
		public NewNoteAsyncTask(Context context, String [] values) {
			super(context);
			this.values = values;
		}

		@Override
		public CreateNoteResponse loadInBackground() {
			try {
				CreateNoteResponse data = new APIUsers().putNote(values[2],values[1], values[0]);
				return data;
			} catch (APIException e) {
				apiexception = e;
				e.printStackTrace();
			}
			return null;
		}
	}

	protected ContentValues  cv;
	
	@Override
	public Loader<CreateNoteResponse> onCreateLoader(int id, Bundle args) {
		return new NewNoteAsyncTask(this, args.getStringArray(KYE_NOTE));
	}

	@Override
	public void onLoadFinished(Loader<CreateNoteResponse> loader, CreateNoteResponse data) {
		cv = new ContentValues();

		if (data == null) {
			APIUtils.ToastException(NewNoteActivity.this, apiexception);
		} else {
			switch (data.getNoteCreate()) {
			case 0:
				cv.put(TableNote._ID, data.getNoteID());
				cv.put(TableNote.TITLE, value[0]);
				cv.put(TableNote.CONTENT, value[1]);
				getContentResolver().insert(Provider.URI_NOTE, cv);
				finish();
				break;
			case 1:
				MainActivity.showToast(NewNoteActivity.this,  getResources().getString(R.string.edit_note_problem));
				break;
			default:
				MainActivity.showToast(NewNoteActivity.this, getResources().getString(R.string.exception));
				break;
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<CreateNoteResponse> loader) {	
	}
}