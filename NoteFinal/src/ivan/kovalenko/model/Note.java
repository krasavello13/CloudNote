package ivan.kovalenko.model;

public class Note {
		
	long id;
	String title;
	String description;
	
	public Note(){
	}
	
	public long getId(){
		return id;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getDescription(){
		return description;
	}
		
	public void setTitle(String _title){
		title = _title;
	}
	
	public void setDescription(String _description){
		description = _description;
	}

	public void setID(long _id){
		id = _id;
	}
	
	public Note(String _title, String _description, long _id){
		title = _title;
		description = _description;
		id = _id;
	}
}
