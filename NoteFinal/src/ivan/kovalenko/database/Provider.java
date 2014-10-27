package ivan.kovalenko.database;

import ivan.kovalenko.database.DBHelper.NoteTable;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class Provider extends ContentProvider {
	private DBHelper dbHelper;
	
	public static final String AUTHORITY = "ivan.kovalenko.database";
	public static final String SCHEME = "content://";
	
	public static final Uri URI_NOTE = Uri.parse(SCHEME + AUTHORITY + "/" + NoteTable.TABLE_NOTE);
		
	private enum QueryId{
		NONE,
		QUERY_NOTE
	};
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static {
		addURI(NoteTable.TABLE_NOTE, QueryId.QUERY_NOTE);
	}
	
	private static void addURI(String uri, QueryId query) {
		sURIMatcher.addURI(AUTHORITY, uri, query.ordinal());
	}
	
	private static QueryId matchQuery(Uri uri){
		int id = sURIMatcher.match(uri);
		return id == -1 ? QueryId.NONE : QueryId.values()[id];
	}
	
	@Override
	public boolean onCreate() {
		dbHelper = new DBHelper(getContext());
		return true;
	}

	private SelectionBuilder buildSimpleSelection(Uri uri){
		final SelectionBuilder builder = new SelectionBuilder();
		switch (matchQuery(uri)) {
		case QUERY_NOTE:
			return builder.table(NoteTable.TABLE_NOTE);
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor result;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		switch (matchQuery(uri)) {
			default:
				final SelectionBuilder builder = buildSimpleSelection(uri);
				result = builder.where(selection, selectionArgs).query(db, projection, sortOrder);

		}
		
		result.setNotificationUri(getContext().getContentResolver(), uri);
		return result;
	}
	
	@Override
	public String getType(Uri uri) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri result = null;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long id = 0;
		switch (matchQuery(uri)) {
		case QUERY_NOTE:
			id = db.insertOrThrow(NoteTable.TABLE_NOTE, null, values);
			result = Uri.parse(URI_NOTE + "/" + id);
			break;

		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	@Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int rowsDeleted = builder.where(selection, selectionArgs).delete(db);

        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int rowsUpdated = builder.where(selection, selectionArgs).update(db, values);

        if (rowsUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private String simpleGetTable(Uri uri) {
        switch (matchQuery(uri)) {
            case QUERY_NOTE:
                return NoteTable.TABLE_NOTE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
    }

    @Override
    public final int bulkInsert(Uri url, ContentValues[] values) {
        int result = 0;
        String table = simpleGetTable(url);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            for (ContentValues data : values) {
                try {
                    db.insertWithOnConflict(table, null, data, SQLiteDatabase.CONFLICT_REPLACE);
                    result++;
                } catch (SQLiteConstraintException e) {
                    throw e;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(url, null);
        return result;
    }
}
