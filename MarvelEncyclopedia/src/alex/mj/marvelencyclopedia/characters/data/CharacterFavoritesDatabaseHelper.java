/**
 * 
 */
package alex.mj.marvelencyclopedia.characters.data;

import java.util.ArrayList;

import alex.mj.marvelencyclopedia.characters.CharacterInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author alejandro.marijuan@googlemail.com
 *
 */
/**
 * @author alejandro.marijuan@googlemail.com
 *
 */
public class CharacterFavoritesDatabaseHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "CharacterFavoritesDatabaseHelper -->";	
	
	//## VERSION 4: esqueleto del provider
    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "favorites";
	private Resources res;
	
	/**
	 * Constructor de la BBDD.
	 * @param context
	 */
	public CharacterFavoritesDatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		res = context.getResources();
	}

	/**
	 * Crea la tabla en la base de datos de favoritos con ID, nombre, descripcion, modificado, url, extension
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate '" + TABLE_NAME +"' on DDBB '"+DATABASE_NAME+"', v"+DATABASE_VERSION);
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
				CharacterFavoritesConstants.ID + " TEXT PRIMARY KEY," +
				CharacterFavoritesConstants.NAME + " TEXT NOT NULL, " +
				CharacterFavoritesConstants.DESCRIPTION + " TEXT NOT NULL, " +
				CharacterFavoritesConstants.MODIFIED + " TEXT NOT NULL, " +
				CharacterFavoritesConstants.THUMBNAIL_URL + " TEXT NOT NULL, " +
				CharacterFavoritesConstants.THUMBNAIL_EXTENSION + " TEXT NOT NULL " +
			");");		
		//## Initial data
		initialData(db);
	}// onCreate()

	/**
	 * Comprueba la versión de la base de datos, si es antigua borra la tabla.
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade '" + TABLE_NAME +"' on DDBB '"+DATABASE_NAME+"', v"+DATABASE_VERSION);
		if (oldVersion < newVersion){
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
			onCreate(db);
		}
	}// onUpgrade()

	/**
	 * Inicializamos los valores de los faoritos en la BBDD.
	 * @param db es la BBDD
	 */
	private void initialData(SQLiteDatabase db){
		Log.d(TAG, "initialData");
		insertFavorite(db, "001", "Wolverine", "X-MEN", "27/11/2014", "www.google.es", "jpg");
		insertFavorite(db, "002", "Spider-Man", "Avenger", "27/11/2014", "www.google.es", "jpg");
		insertFavorite(db, "003", "Magneto", "X-MEN", "27/11/2014", "www.google.es", "jpg");
		insertFavorite(db, "004", "Thor", "Avenger", "27/11/2014", "www.google.es", "jpg");
		insertFavorite(db, "005", "Iron Man", "Avenger", "27/11/2014", "www.google.es", "jpg");			
	}// initialData()
	
	/**
     * Obtiene los datos de la tabla de la BBDD y los almacena en una lista.
     * @return lista de favoritos
     */
    public ArrayList<CharacterInfo> getFavoritesList(){ 
    	Log.d(TAG, "getFavoritesList");
    	ArrayList<CharacterInfo> favorites = new ArrayList<CharacterInfo>();    	
    	SQLiteDatabase db = getReadableDatabase();    	
    	Cursor c = db.query(CharacterFavoritesConstants.FAVORITES_TABLE_NAME, null, null, null, null, null, null);    	
    	if (c.moveToFirst()){
    		int idDBIndex = c.getColumnIndex(CharacterFavoritesConstants.ID);
    		int nameIndex = c.getColumnIndex(CharacterFavoritesConstants.NAME);
    		int descriptionIndex = c.getColumnIndex(CharacterFavoritesConstants.DESCRIPTION);
    		int modifiedIndex = c.getColumnIndex(CharacterFavoritesConstants.MODIFIED);
    		int thumbnailUrlIndex = c.getColumnIndex(CharacterFavoritesConstants.THUMBNAIL_URL);
    		int thumbnailExtensionIndex = c.getColumnIndex(CharacterFavoritesConstants.THUMBNAIL_EXTENSION);
    		    		
    		do {
    			String idDB = c.getString(idDBIndex);
    			String name = c.getString(nameIndex);
    			String description = c.getString(descriptionIndex);
    			String modified = c.getString(modifiedIndex);
    			String thumbnail_url = c.getString(thumbnailUrlIndex);
    			String thumbnail_extension = c.getString(thumbnailExtensionIndex);
    			
//    				Log.i(TAG,"%%%%%%%%%%%%%%%%%%%%% idDB="+idDB);
    			CharacterInfo favorite = new CharacterInfo(idDB, name, description, modified, thumbnail_url, thumbnail_extension);    			
    			favorites.add(favorite);    			
    		} while (c.moveToNext());    		
    		c.close();
    	}
    	Log.i(TAG, "Nro de favoritos en DB: "+ favorites.size());
        return favorites;
    }// getFavoritesList()
        
 
	/**
	 * @param db
	 * @param id
	 * @param name
	 * @param description
	 * @param modified
	 * @param thumbnail_url
	 * @param thumbnail_extension
	 */
	public void insertFavorite(SQLiteDatabase db, String id, String name, String description, String modified, String thumbnail_url, String thumbnail_extension){
		Log.d(TAG, "insertFavorite");
		ContentValues values = new ContentValues();
		values.put(CharacterFavoritesConstants.ID, id);
		values.put(CharacterFavoritesConstants.NAME, name);
		values.put(CharacterFavoritesConstants.DESCRIPTION, description);
		values.put(CharacterFavoritesConstants.MODIFIED, modified);
		values.put(CharacterFavoritesConstants.THUMBNAIL_URL, thumbnail_url);		
		values.put(CharacterFavoritesConstants.THUMBNAIL_EXTENSION, thumbnail_extension);		
		db.insert(TABLE_NAME, null, values);		
	}// insertFavorite()
	
    /**
     * Actualizamos un favorito que ya se encuentra en la BBDD
     * @param myHeroe informacion del favorito a ser modificado
     * @return valor de la fila a ser actualizada
     */
    public int updateTravel(CharacterInfo myHeroe, String positionId) {
    	Log.d(TAG, "updateTravel IN "+DATABASE_NAME+": "+myHeroe.getId()+"("+myHeroe.getName()+") Description: "+myHeroe.getDescription()+" - '"+myHeroe.getThumbnail_url()+"/"+myHeroe.getThumbnail_extension());
        SQLiteDatabase db = this.getWritableDatabase();     
        ContentValues values = new ContentValues();
        values.put(CharacterFavoritesConstants.ID, myHeroe.getId());
		values.put(CharacterFavoritesConstants.NAME, myHeroe.getName());
		values.put(CharacterFavoritesConstants.DESCRIPTION, myHeroe.getDescription());
		values.put(CharacterFavoritesConstants.THUMBNAIL_URL, myHeroe.getThumbnail_url());
		values.put(CharacterFavoritesConstants.THUMBNAIL_EXTENSION, myHeroe.getThumbnail_extension());
        // ##updating row
        return db.update(TABLE_NAME, values, CharacterFavoritesConstants.ID + " = ?",
                new String[] { positionId });        
    }// updateTravel()
    
    /**
     * Borramos un favorito. El id se obtiene del valor que esta almacenado en la base de datos. 
     * @param positionId es la posicion del favorito en la BBDD
     */
    public void deleteFavorite(String positionId) {
    	Log.d(TAG, "deleteFavorite");
    	Log.i(TAG, "positionId: "+ positionId +"--"+"Table ID" );    	
        SQLiteDatabase db = this.getWritableDatabase();        
        int result=db.delete(TABLE_NAME, CharacterFavoritesConstants.ID + " = ?",
                new String[] { positionId });
        Log.i(TAG, "##number of rows afected: "+ result); 
    }// deleteFavorite()
    
	/**
	 * Obtiene el id con mayor valor de la tabla de nuestra BD.
	 * @return el valor del id mas grande dentro de la tabla
	 *
	public int getLastId() {
		Log.d(TAG, "getLastId");
	    //## openDB();
	    SQLiteDatabase sqlDB = this.getReadableDatabase();
	    int id = 0;
	    final String MY_QUERY = "SELECT MAX(_id) AS _id FROM "+TABLE_NAME;
	    Cursor mCursor = sqlDB.rawQuery(MY_QUERY, null);  
	    try {
	          if (mCursor.getCount() > 0) {
	            mCursor.moveToFirst();
	            id = mCursor.getInt(0);//there's only 1 column in cursor since you only get MAX, not dataset
	          }
	        } catch (Exception e) {
	          System.out.println(e.getMessage());
	        } finally {
	            //## closeDB();
	        	sqlDB.close();
	        }
	    return id;
	}// getLastId()*/
	
	/**
	 * Obtiene la informacion de un favorito almacenado en la base de datos.
	 * @param idFavorite para obtener el favorito de la BD
	 * @return la informacion completa del favorito
	 */
	public CharacterInfo getFavoriteInfo(String idFavorite){		
	    //## openDB();
	    SQLiteDatabase sqlDB = this.getReadableDatabase();
	    final String MY_QUERY = "SELECT * FROM "+TABLE_NAME+" WHERE _id = "+idFavorite;
	    Log.d(TAG, "getFavoriteInfo: "+MY_QUERY);
	    Cursor mCursor = sqlDB.rawQuery(MY_QUERY, null);   
	    String idDB = null;
		String name = null;
		String description = null;
		String modified = null;
		String thumbnail_url = null;
		String thumbnail_extension = null;
		
	    if (mCursor.moveToFirst()){
    		int idDBIndex = mCursor.getColumnIndex(CharacterFavoritesConstants.ID);
    		int nameIndex = mCursor.getColumnIndex(CharacterFavoritesConstants.NAME);
    		int descriptionIndex = mCursor.getColumnIndex(CharacterFavoritesConstants.DESCRIPTION);
    		int modifiedIndex = mCursor.getColumnIndex(CharacterFavoritesConstants.MODIFIED);
    		int thumbnailUrlIndex = mCursor.getColumnIndex(CharacterFavoritesConstants.THUMBNAIL_URL);    		
    		int thumbnailExtensionIndex = mCursor.getColumnIndex(CharacterFavoritesConstants.THUMBNAIL_EXTENSION);
    		do {
    			idDB = mCursor.getString(idDBIndex);
    			name = mCursor.getString(nameIndex);
    			description = mCursor.getString(descriptionIndex);
    			modified = mCursor.getString(modifiedIndex);
    			thumbnail_url = mCursor.getString(thumbnailUrlIndex);
    			thumbnail_extension = mCursor.getString(thumbnailExtensionIndex);
    				Log.i(TAG,"%%%%%%%%%%%%%%%%%%%%% idDB="+idDB);
    		} while (mCursor.moveToNext());    		
    		mCursor.close();
    	}
	    //## closeDB();
    	sqlDB.close();
	    CharacterInfo travel = new CharacterInfo(idDB, name, description, modified, thumbnail_url, thumbnail_extension);
	    return travel;
	}// getFavoriteInfo()
}
