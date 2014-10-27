package ivan.kovalenko.note;

import ivan.kovalenko.MyApplication;
import ivan.kovalenko.api.APIException;
import ivan.kovalenko.api.APIUsers;
import ivan.kovalenko.api.APIUsers.EditNoteResponse;
import ivan.kovalenko.api.APIUsers.GetNoteResponse;
import ivan.kovalenko.api.APIUtils;
import ivan.kovalenko.database.Provider;
import ivan.kovalenko.database.NoteDatabaseColumns.TableNote;
import ivan.kovalenko.login.MainActivity;
import ivan.kovalenko.notenote.R;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class EditNoteActivity extends Activity implements LoaderCallbacks<GetNoteResponse>{
	
	protected EditText 			  editNote;
	protected String 			  title;
	protected DialogFragment      loader;
	protected static APIException apiexception;
	protected Bundle 			  bundle;
	
	private final String LONG_EXTRA    = "ID"; 
	private final String SESSION_EXTRA = "TITLE";
	private final String CONTENT_EXTRA = "CONTENT";
	
	private final int GET_NOTE_LOADER  = 0;
	private final int EDIT_NOTE_LOADER = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.edit_note);
		
		editNote = (EditText) findViewById(R.id.editNote);
		
		bundle = new Bundle();
		bundle.putString(SESSION_EXTRA, ((MyApplication)getApplication()).getLocalData().getSessionId());
		bundle.putLong(LONG_EXTRA, getIntent().getLongExtra(LONG_EXTRA, -1));
		Log.d("EDIT NOTE NOTE_ID = ", String.valueOf(bundle.getLong(LONG_EXTRA)));
		Log.d("EDIT NOTE SESSION_ID = ", bundle.getString(SESSION_EXTRA));
		getLoaderManager().initLoader(GET_NOTE_LOADER, bundle, EditNoteActivity.this).forceLoad();
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.edit_note_menu, menu);
	    return true;
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_edit_note:
			bundle.putString(CONTENT_EXTRA, editNote.getText().toString());
			getLoaderManager().initLoader(EDIT_NOTE_LOADER, bundle, EditNoteResponse).forceLoad();
			break;
		case android.R.id.home:
			onBackPressed();
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	};
	
	public static class EditNote extends AsyncTaskLoader<EditNoteResponse>{
		String sessionID;
		String text;
		long noteID;
		
		public EditNote(Context context, String sessionID, long noteID, String text) {
			super(context);
			this.sessionID = sessionID;
			this.noteID    = noteID;
			this.text	   = text;
		}

		@Override
		public EditNoteResponse loadInBackground() {
			try {
				EditNoteResponse data = new APIUsers().getEditNote(sessionID, noteID, text);
				return data;
			} catch (APIException e) {
				apiexception = e;
				e.printStackTrace();
			}
			return null;
		}
		
	}

	public LoaderCallbacks<EditNoteResponse> EditNoteResponse = new LoaderCallbacks<APIUsers.EditNoteResponse>() {
		String content;
		@Override
		public Loader<EditNoteResponse> onCreateLoader(int id, Bundle args) {
			content = args.getString(CONTENT_EXTRA);
			return new EditNote(EditNoteActivity.this, args.getString(SESSION_EXTRA), args.getLong(LONG_EXTRA), content);
		}
		
		@Override
		public void onLoadFinished(Loader<EditNoteResponse> Loader, EditNoteResponse data) {
			
			
			if (data == null) {
				APIUtils.ToastException(EditNoteActivity.this, apiexception);
			} else {
				switch (data.getEditNoteResponse()) {
				case 0:
					ContentValues cv = new ContentValues();
					cv.put(TableNote.TITLE, getActionBar().getTitle().toString());
					cv.put(TableNote.CONTENT, content);
					getContentResolver().update(Provider.URI_NOTE, cv,TableNote._ID + "=" + getIntent().getLongExtra(LONG_EXTRA, -1), null);
					
					finish();
					break;
				case 2:
					MainActivity.showToast(EditNoteActivity.this, getResources().getString(R.string.edit_note_problem));
					break;
				default:
					MainActivity.showToast(EditNoteActivity.this, getResources().getString(R.string.exception));
					break;
				}
			}
			loader.dismissAllowingStateLoss();
		}
		
		@Override
		public void onLoaderReset(Loader<EditNoteResponse> loader) {
		}
	};
	
	public static class NoteResponse extends AsyncTaskLoader<GetNoteResponse>{
		String sessionID;
		long noteID;
		
		public NoteResponse(Context context, String sessionID, long noteID) {
			super(context);
			this.sessionID = sessionID;
			this.noteID    = noteID;
		}

		@Override
		public GetNoteResponse loadInBackground() {
			try {
				GetNoteResponse data = new APIUsers().getNote(sessionID, noteID);
				return data;
			} catch (APIException e) {
				apiexception = e;
				e.printStackTrace();
			}
			return null;
		}
	}

	@Override
	public Loader<GetNoteResponse> onCreateLoader(int id, Bundle args) {
		return new NoteResponse(EditNoteActivity.this, args.getString(SESSION_EXTRA), args.getLong(LONG_EXTRA));
	}

	@Override
	public void onLoadFinished(Loader<GetNoteResponse> Loader , GetNoteResponse data) {
		
		if(data == null){
			APIUtils.ToastException(EditNoteActivity.this, apiexception);
		}else{
			switch(data.getGetNote()) {
				case 0:	
					getActionBar().setTitle(data.getTitle());
					editNote.setText(data.getContent());			
					break;
				case 2:
					MainActivity.showToast(EditNoteActivity.this, getResources().getString(R.string.edit_note_problem));
					break;
				default:
					MainActivity.showToast(EditNoteActivity.this, getResources().getString(R.string.exception));
					break;
			}
		}
	}
	
	@Override
	public void onLoaderReset(Loader<GetNoteResponse> loader) {	
	}
}