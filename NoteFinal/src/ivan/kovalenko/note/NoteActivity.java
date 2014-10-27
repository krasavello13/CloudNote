package ivan.kovalenko.note;

import ivan.kovalenko.MyApplication;
import ivan.kovalenko.api.APIException;
import ivan.kovalenko.api.APIUsers;
import ivan.kovalenko.api.APIUtils;
import ivan.kovalenko.api.APIUsers.LogOutResponse;
import ivan.kovalenko.api.APIUsers.NoteListResponse;
import ivan.kovalenko.database.Provider;
import ivan.kovalenko.database.NoteDatabaseColumns.TableNote;
import ivan.kovalenko.login.MainActivity;
import ivan.kovalenko.note.NoteAdapter.OnItemDeleteListener;
import ivan.kovalenko.notenote.R;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class NoteActivity extends Activity implements LoaderCallbacks<Cursor>{
			
	public  static final int CURSOR_LOADER	       	   = 0;
	private static final int LOG_OUT_ASYNTASK_LOADER   = 1;
	private static final int NOTE_LIST_ASYNTASK_LOADER = 2;
	
	private static final String LOADER_KEY        	   = "LOADER";
	private static final String SESSION_ID_KEY    	   = "sessionID";
	private static final String LONG_EXTRA        	   = "ID";
	public  static final String NOTE_ID            	   = "NOTE_ID";
	
	protected final String [] ALL_COLUMNS         	   = new String []{TableNote._ID, TableNote.TITLE, TableNote.CONTENT};
	
	protected Button buttonDelete;
	protected ListView lv;
	protected NoteAdapter adapter;	
	protected Bundle bundle;
    
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_activity);
		bundle = new Bundle();
		bundle.putString(SESSION_ID_KEY, ((MyApplication)getApplication()).getLocalData().getSessionId());
		
		getLoaderManager().initLoader(NOTE_LIST_ASYNTASK_LOADER, bundle, NoteListResponseCallbacks).forceLoad();		
	
	    if(getLoaderManager().getLoader(LOG_OUT_ASYNTASK_LOADER) != null){
	    	getLoaderManager().initLoader(LOG_OUT_ASYNTASK_LOADER, null, LogOutLoaderCallbacks);
	    }
		getActionBar().setDisplayHomeAsUpEnabled(true);
			
		lv = (ListView) findViewById(R.id.list);
	
		adapter = new NoteAdapter(NoteActivity.this, null, false);
		adapter.setOnItemDeleteListener(new OnItemDeleteListener() {

			@Override
			public void onItemDelete(long id) {
				DialogFragment Loader = new NoteDeleteDialogFragment();
				Bundle b = new Bundle();
				b.putLong(NOTE_ID, id);
				Loader.setArguments(b);
				Loader.show(getFragmentManager(), LOADER_KEY);
			}
		});
		
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent  = new Intent(NoteActivity.this, EditNoteActivity.class);
				intent.putExtra(LONG_EXTRA, id);
				startActivity(intent);				
			}
		});
		//Создаем Loader для чтения
	    getLoaderManager().initLoader(CURSOR_LOADER, null, this);
	    
	}
			
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		switch (item.getItemId()) {
		case R.id.action_changePassword:
			Intent intentChangePassword = new Intent(this, ChengPasswordActivity.class);
			startActivity(intentChangePassword);
			return true;
		case R.id.action_logOut:
			getLoaderManager().initLoader(LOG_OUT_ASYNTASK_LOADER, bundle, LogOutLoaderCallbacks).forceLoad();
			return true;
		case R.id.action_add:
			Intent intentAdd = new Intent(this, NewNoteActivity.class);
			startActivity(intentAdd);
			return true;
		case android.R.id.home:
			getLoaderManager().initLoader(LOG_OUT_ASYNTASK_LOADER, bundle, LogOutLoaderCallbacks).forceLoad();;
			onBackPressed();
			return true;
		case R.id.action_reload:
			getLoaderManager().initLoader(NOTE_LIST_ASYNTASK_LOADER, bundle, NoteListResponseCallbacks).forceLoad();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
			
	public static class LogOutAsyncTask extends AsyncTaskLoader<LogOutResponse> {
		APIException apiexception;
		LogOutResponse mData;		
		String sessionID;
		public LogOutAsyncTask(Context context) {	
			super(context);
		}

		public LogOutAsyncTask(Context context, String sessionID) {
			super(context);
			this.sessionID = sessionID; 
		}
		
		@Override
		public LogOutResponse loadInBackground() {
			try{
				LogOutResponse data = new APIUsers().logOut(sessionID);
				return data;
			}catch (APIException e) {
				apiexception = e;
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		public void deliverResult(LogOutResponse data) {
			if(isReset()){
				releaseResources(data);
			}
			LogOutResponse oldLOR = mData;
			mData = data; 
			
			if(isStarted()){
				super.deliverResult(data);
			}
			
			if(oldLOR != null && oldLOR != data){
				releaseResources(oldLOR);
			}
		}
		
		@Override
		protected void onStartLoading() {
			if(mData != null){
				deliverResult(mData);
			}
			
			if(takeContentChanged() || mData == null){
				forceLoad();
			}
		}
		
		@Override
		protected void onStopLoading() {
			cancelLoad();
		}
		
		@Override
		protected void onReset() {
			onStopLoading();
			if(mData != null){
				releaseResources(mData);
				mData = null;
			}
		}
		
		@Override
		public void onCanceled(LogOutResponse data) {	
			super.onCanceled(data);
			releaseResources(data);
		}
		
		private void releaseResources(LogOutResponse data){	
		}
	}
		
	public static class NotesListArrayAsyncTask extends AsyncTaskLoader<NoteListResponse> {
		String sessionID;
		APIException apiexception;

		public NotesListArrayAsyncTask(Context context, String sessionID) {
			super(context);
			this.sessionID = sessionID;
			
		}
		
		@Override
		public NoteListResponse loadInBackground() {
			try {
				NoteListResponse data = new APIUsers().getNotesList(sessionID);
				return data;
			} catch (APIException e) {
				apiexception = e;
				e.printStackTrace();
			}
			return null;
		}
	}
	
	LoaderCallbacks<NoteListResponse> NoteListResponseCallbacks = new LoaderCallbacks<APIUsers.NoteListResponse>(){
		
		APIException apiexception;
		@Override
		public Loader<NoteListResponse> onCreateLoader(int id, Bundle args) {
			Log.d("DEBUG", "onCreateLoader");
			
			return new NotesListArrayAsyncTask(NoteActivity.this, args.getString(SESSION_ID_KEY));
		}

		@Override
		public void onLoadFinished(Loader<NoteListResponse> loader, NoteListResponse data) {
			Log.d("DEBUG", "onLoadFinished " + (data == null ? "null" : String.valueOf(data.getNoteArray().size())));
			if(data == null){
				APIUtils.ToastException(NoteActivity.this , apiexception);
			} else {
				switch(data.getNoteCreate()) {
					case 0:		
						if(data.getNoteArray() != null){
	
							ContentValues [] cv = new ContentValues [data.getNoteArray().size()];
							
							for(int i = 0; i < data.getNoteArray().size(); i++){
								cv[i] = new ContentValues();
								cv[i].put(TableNote._ID, data.getNoteArray().get(i).getId());
								cv[i].put(TableNote.TITLE, data.getNoteArray().get(i).getTitle());
								cv[i].put(TableNote.CONTENT, data.getNoteArray().get(i).getDescription());
							}
							NoteActivity.this.getContentResolver().bulkInsert(Provider.URI_NOTE, cv);
						}			
						break;
					case 1:
						break;
					default:
						MainActivity.showToast(NoteActivity.this, getResources().getString(R.string.exception));
						break;
				}				
			}
			getLoaderManager().destroyLoader(NOTE_LIST_ASYNTASK_LOADER);
		}

		@Override
		public void onLoaderReset(Loader<NoteListResponse> loader) {
			Log.d("DEBUG", "onLoaderReset");			
		}
		
	};
	
	LoaderCallbacks<LogOutResponse> LogOutLoaderCallbacks = new LoaderCallbacks<APIUsers.LogOutResponse>() {
		APIException apiexception;
		
		@Override
		public Loader<LogOutResponse> onCreateLoader(int id, Bundle args) {		
			return new LogOutAsyncTask(NoteActivity.this, args.getString(SESSION_ID_KEY));
		}

		@Override
		public void onLoadFinished(Loader<LogOutResponse> loader, LogOutResponse data) {	
			if(data == null){
				APIUtils.ToastException(NoteActivity.this, apiexception);
			}else{
				switch(data.getLogOutResponse()) {
					case 0:							
						getContentResolver().delete(Provider.URI_NOTE, null, null);	
						Intent intent = new Intent(NoteActivity.this, MainActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						break;			
					case 1:			
						MainActivity.showToast(NoteActivity.this, getResources().getString(R.string.note_activity_log_out_not));						
						break;			
					default:			
						MainActivity.showToast(NoteActivity.this, getResources().getString(R.string.exception));
						break;
			}
				/*LoadingDialogFragment Loader = (LoadingDialogFragment) getFragmentManager().findFragmentByTag(LOADER_KEY);
				if (Loader != null) {
					Loader.dismissAllowingStateLoss();	
				}*/
		}
		getLoaderManager().destroyLoader(LOG_OUT_ASYNTASK_LOADER);	
	}

	@Override
	public void onLoaderReset(Loader<LogOutResponse> loader) {		
		/*LoadingDialogFragment Loader = (LoadingDialogFragment)getFragmentManager().findFragmentByTag(LOADER_KEY);
		if(Loader != null){
			Loader.dismissAllowingStateLoss();	
		}*/
		getLoaderManager().destroyLoader(LOG_OUT_ASYNTASK_LOADER);
	}
	};

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
		case CURSOR_LOADER:
			return new  CursorLoader(NoteActivity.this,Provider.URI_NOTE, ALL_COLUMNS, null, null, TableNote._ID);
		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		adapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);	
	}	
}