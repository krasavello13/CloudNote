package ivan.kovalenko.api;

import ivan.kovalenko.api.APIException.TypeOfException;
import ivan.kovalenko.model.Note;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

public class APIUsers {

	public String GET(String url) throws APIException {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		InputStream inputStream = null;
		String result = "";
		// создаем HttpClient
		HttpClient httpclient = new DefaultHttpClient();
		// делаем запрос GET к данному URL
		HttpResponse httpResponse;
		try {
			httpResponse = httpclient.execute(new HttpGet(url));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new APIException(TypeOfException.ERROR_SERVER, e);
		}
		// получаем ответ в виде inputStream
		try {
			inputStream = httpResponse.getEntity().getContent();
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			throw new APIException(TypeOfException.ERROR_SERVER, e);
		}

		if (inputStream != null)
			try {
				// преобразуем inputstream в string
				result = convertInputStreamToString(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
				throw new APIException(TypeOfException.ERROR_SERVER, e);
			}
		else{
			result = "Did not work!";
		}
		Log.d("URL", url);
		Log.d("RESULT", result);
		return result ;
	}

	private String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		String line = "";
		String result = "";

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		while ((line = bufferedReader.readLine()) != null) {
			result += line;
		}

		inputStream.close();

		return result;
	}

	public static Uri.Builder builder() {

		return new Uri.Builder()
				.scheme("http")
				.encodedAuthority("notes-androidcoursesdp.rhcloud.com")
				.appendPath("REST");
	}

	public static class LoginResponse {
		int userCreate;
		String sessionID;
		
		public LoginResponse(JSONObject  obj) throws APIException {
			try{
			userCreate = obj.getInt("result");
			sessionID = obj.getString("sessionID");
			}
			catch(JSONException e){
				throw new APIException(APIException.TypeOfException.ERROR_JSON, e);
			}
		}

		public int getUserCreate() {
			return userCreate;
		}

		public String getSessionID() {
			return sessionID;
		}
	}

	public LoginResponse login(String login, String password) throws APIException {
		String rowResponse = GET(builder().appendPath("login")
				.appendQueryParameter("login", login)
				.appendQueryParameter("pass", password)
				.toString());
		
		LoginResponse response = null;
		
		try {
			response = new LoginResponse(new JSONObject(rowResponse));
		} catch (JSONException e) {
			throw new APIException(APIException.TypeOfException.ERROR_JSON, e);
		}
		return response;
	}

	public class RegistrResponse {
		int userCreate;

		public RegistrResponse(JSONObject odj) throws APIException{
			try {
				userCreate = odj.getInt("result");
			} catch (Exception e) {
				throw new APIException(APIException.TypeOfException.ERROR_JSON, e);
			}
		}

		public int getUserCreate() {
			return userCreate;
		}
	}

	public RegistrResponse regist(String login, String password) throws APIException {
		String rowResponse = GET(builder().appendPath("register")
				.appendQueryParameter("login", login)
				.appendQueryParameter("pass", password).toString());
		RegistrResponse response = null;
		try {
			response = new RegistrResponse(new JSONObject(rowResponse));
		} catch (JSONException e) {
			throw new APIException(TypeOfException.ERROR_JSON, e);
		}
		return response;
	}

	public static class ChengPasswordResponse {
		int userChengPassword;

		public ChengPasswordResponse(JSONObject object) throws APIException {
			try {
				userChengPassword = object.getInt("result");
			} catch (Exception e) {
				throw new APIException(APIException.TypeOfException.ERROR_JSON, e);
			}
		}

		public int getChengPasswordResponse() {
			return userChengPassword;
		}
	}

	public ChengPasswordResponse chengPassword(String sessionID, String newPassword, String oldPassword) throws APIException {
		String rowResponse = GET(builder().appendPath("changePassword")
				.appendQueryParameter("sessionID", sessionID)
				.appendQueryParameter("oldPass", oldPassword)
				.appendQueryParameter("newPass", newPassword)
				.toString());
		ChengPasswordResponse response = null;
		try {
			response = new ChengPasswordResponse(new JSONObject(rowResponse));
		} catch (JSONException e) {
			throw new APIException(TypeOfException.ERROR_JSON, e);
		}
		return response;
	}

	public class LogOutResponse {
		int userLogOut;

		public LogOutResponse(JSONObject object) throws APIException{
			try {
				userLogOut = object.getInt("result");	
			} catch (JSONException e) {
				throw new APIException(APIException.TypeOfException.ERROR_JSON, e);
			}
			
		}

		public int getLogOutResponse() {
			return userLogOut;
		}
	}

	public LogOutResponse logOut(String ID) throws APIException {
		String rowResponse = GET(builder().appendPath("logout")
				.appendQueryParameter("sessionID", ID)
				.toString());
		LogOutResponse response = null;
		try {
			response = new LogOutResponse(new JSONObject(rowResponse));
		} catch (JSONException e) {
			throw new APIException(TypeOfException.ERROR_JSON, e);
		}
		return response;
	}
	
	public class CreateNoteResponse {

		int NoteCreate;
		long NoteID;

		public CreateNoteResponse(JSONObject object) throws APIException{
			try{
			NoteCreate = object.getInt("result");
			NoteID     = object.getLong("noteID");
		}
			catch (JSONException e){
				throw new APIException(APIException.TypeOfException.ERROR_JSON, e);
			}
		}

		public int getNoteCreate() {
			return NoteCreate;
		}

		public long getNoteID() {
			return NoteID;
		}
	}
	
	public CreateNoteResponse putNote(String ID, String NOTE, String NOTE_TITLE_NOTE) throws APIException {
		CreateNoteResponse response = null;
		
		String rowResponse = GET(builder().appendPath("createNote")
				.appendQueryParameter("sessionID", ID)
				.appendQueryParameter("title", NOTE_TITLE_NOTE)
				.appendQueryParameter("content", NOTE).toString());
		try {
			response = new CreateNoteResponse(new JSONObject(rowResponse));
		} catch (JSONException e) {
			throw new APIException(TypeOfException.ERROR_JSON, e);
		}

		return response;
	}
	
	public class NoteListResponse {
		int noteListResponse;
		ArrayList<Note> notes;
		Note[] note;

		public NoteListResponse(JSONObject object) throws APIException {
			try {
				noteListResponse = object.getInt("result");
				notes = new ArrayList<Note>();

				JSONArray jsonArray = object.getJSONArray("notes");
				note = new Note[jsonArray.length()];
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject item = jsonArray.getJSONObject(i);
					note[i] = new Note(item.getString("title"), item.getString("shortContent"),	item.getLong("noteID"));
				}
				
				notes = new ArrayList<Note>(Arrays.asList(note));
			} catch (JSONException e) {
				throw new APIException(TypeOfException.ERROR_JSON, e);
			}
		}

		public int getNoteCreate() {
			return noteListResponse;
		}

		public ArrayList<Note> getNoteArray() {
			return notes;
		}
	}
	
	public NoteListResponse getNotesList(String ID) throws APIException {
		String rawResponse = GET(builder().appendPath("getNotesList")
				.appendQueryParameter("sessionID", ID).toString());

		NoteListResponse response = null;
		try {
			response = new NoteListResponse(new JSONObject(rawResponse));

		} catch (JSONException e) {
			throw new APIException(TypeOfException.ERROR_JSON, e);
		}
		return response;
	}
	
	public class GetNoteResponse{
		int getNoteResponse;
		String title;
		String content;
		
		public GetNoteResponse(JSONObject object) throws APIException{
			try {
				getNoteResponse = object.getInt("result");
				title 			= object.getString("title");
				content 		= object.getString("content"); 
			} catch (JSONException e) {
				throw new APIException(TypeOfException.ERROR_JSON, e);
			}
			
		}
		
		public int getGetNote(){
			return getNoteResponse;
		}
		
		public String getTitle(){
			return title;
		}
		
		public String getContent(){
			return content;
		}
	}
	
	public GetNoteResponse getNote(String sessionID, long noteID) throws APIException{
		String rowResponse = GET(builder().appendPath("getNote")
				.appendQueryParameter("sessionID", sessionID)
				.appendQueryParameter("noteID", Long.toString(noteID))
				.toString());
		GetNoteResponse response = null;
		try {
			response = new GetNoteResponse(new JSONObject(rowResponse));
		} catch (JSONException e) {
			throw new APIException(TypeOfException.ERROR_JSON, e);
		}
		
		return response;
		
	}

	public class EditNoteResponse{
		int editNoteResponse;
		
		public EditNoteResponse(JSONObject object) throws APIException{
			try {
				editNoteResponse = object.getInt("result");
			} catch (JSONException e) {
				throw new APIException(TypeOfException.ERROR_JSON, e);
			}
		}
				
		public int getEditNoteResponse(){
			return editNoteResponse;
		}
	}
	
	public EditNoteResponse getEditNote(String _sessionID, long _NoteID, String _text) throws APIException{
		String rowResponse = GET(builder().appendPath("editNote")
				.appendQueryParameter("sessionID", _sessionID)
				.appendQueryParameter("noteID", String.valueOf(_NoteID))
				.appendQueryParameter("text", _text)
				.toString());
		EditNoteResponse response = null;
		try {
			response = new EditNoteResponse(new JSONObject(rowResponse));
		} catch (Exception e) {
			throw new APIException(TypeOfException.ERROR_JSON, e);
		}
		
		return response;
	}

	public class DeleteNoteResponse{
		int deleteNoteResponse;
		
		public DeleteNoteResponse(JSONObject object) throws APIException{
			try{
			deleteNoteResponse = object.getInt("result");
			}catch(JSONException e){
				throw new APIException(TypeOfException.ERROR_JSON, e);
			}
		}
		
		public int GetNoteResponse(){
			return deleteNoteResponse;
		}
	}
	
	public DeleteNoteResponse getDeleteNote(String _sessionID, long _noteID) throws APIException {
		String rowResponse = GET(builder().appendPath("deleteNote")
				.appendQueryParameter("sessionID", _sessionID)
				.appendQueryParameter("noteID", String.valueOf(_noteID))
				.toString());
		DeleteNoteResponse response = null;
		
		try {
		response = new DeleteNoteResponse(new JSONObject(rowResponse));
		} catch (Exception e) {
			throw new APIException(TypeOfException.ERROR_JSON, e);
		}

		return response;
	}
}