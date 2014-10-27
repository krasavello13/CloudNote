package ivan.kovalenko.database;

public class NoteDatabaseColumns {
	private NoteDatabaseColumns(){
	}
	
	public interface BaseColumns{
		String _ID = "_id";
	}
	
	public interface TableNote extends BaseColumns{
		String TITLE   = "title";
		String CONTENT = "content";
	}
}
