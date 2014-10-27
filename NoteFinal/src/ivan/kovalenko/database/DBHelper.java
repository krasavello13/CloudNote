package ivan.kovalenko.database;


import ivan.kovalenko.database.NoteDatabaseColumns.TableNote;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME  = "noteDatabase";
	
	private static final int VERSION_INITIAL  = 1;
	private static final int DATABASE_VERSION = VERSION_INITIAL;

	public interface NoteTable{
		String TABLE_NOTE = "tableNote";
	}
	
	private static final String CREATE_TABLE_NOTE = "CREATE TABLE "
			+ NoteTable.TABLE_NOTE + " ("
			+ TableNote._ID + " INTEGER PRIMARY KEY , "
			+ TableNote.TITLE + " TEXT, "
			+ TableNote.CONTENT + " TEXT)";
			
	private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";

	public DBHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db){
		db.execSQL(CREATE_TABLE_NOTE);
	}
	
	public void dropTable(SQLiteDatabase db) {
		db.execSQL(DROP_TABLE + NoteTable.TABLE_NOTE);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { 		
		dropTable(db);
		onCreate(db);
	}
	
	// Удалить базу
	/*@Override
	public void onOpen(SQLiteDatabase db) {			
		super.onOpen(db);
		onUpgrade(db, 0, VERSION_INITIAL);
	}*/
}