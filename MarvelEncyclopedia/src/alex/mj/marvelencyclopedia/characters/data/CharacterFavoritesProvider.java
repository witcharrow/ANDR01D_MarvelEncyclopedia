/**
 * 
 */
package alex.mj.marvelencyclopedia.characters.data;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author alejandro.marijuan@googlemail.com
 *
 */
public class CharacterFavoritesProvider extends ContentProvider {
	
	private static final String TAG = "CharacterFavoritesProvider -->";
	private CharacterFavoritesDatabaseHelper mDbHelper;
	private static final String AUTHORITY = "es.alexmj.travellist";	
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites");
	private static final int URI_FAVORITES = 1;
	private static final int URI_FAVORITES_ITEM = 2;	
	private static final UriMatcher mUriMatcher;
	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(AUTHORITY, "favorites", URI_FAVORITES);
		mUriMatcher.addURI(AUTHORITY, "favorites/#", URI_FAVORITES_ITEM);
	}
	

	/**
	 * Genera la base de datos que contiene los favoritos.
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		Log.d(TAG, "onCreate");
		mDbHelper = new CharacterFavoritesDatabaseHelper(getContext());
		return true;
	}// onCreate()

	/**
	 * Se encarga de introducir una nueva información en la BBDD. Si tiene exito,
	 *  debe devolver la URI que representa al recurso que acaba de ser aniadido.
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.d(TAG, "insert");
		SQLiteDatabase db = mDbHelper.getWritableDatabase();		
		long id = db.insert(CharacterFavoritesDatabaseHelper.TABLE_NAME, null, values);
		Log.w(TAG,"id = "+id);
		Uri result = null;		
		if (id >= 0){
			result = ContentUris.withAppendedId(CONTENT_URI, id);
			notifyChange(result);
		}		
		return result;
	}// insert()

	/**
	 * Consulta la base de datos para obtener o todos los favoritos o un item favorito. 
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {		
		Log.d(TAG, "query");
		SQLiteDatabase db = mDbHelper.getReadableDatabase();		
		int match = mUriMatcher.match(uri);		
		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
		
		//## Comprueba si la columna que llamamos no existe.
	    checkColumns(projection);
		
		qBuilder.setTables(CharacterFavoritesDatabaseHelper.TABLE_NAME);		
		switch (match){
		case URI_FAVORITES:
			//nada
			break;
		case URI_FAVORITES_ITEM:
			String id = uri.getPathSegments().get(1);
			qBuilder.appendWhere(CharacterFavoritesConstants.ID + "=" + id);
			break;
		default:
			Log.w(TAG, "Uri didn't match: " + uri);
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}		
		Cursor c = qBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		//## Actualizamos listeners por notificacion.
	    c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}// query()
	
	/**
	 * Actualiza un elemento favorito, a partir de su URI.
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		Log.d(TAG, "update");
		int uriType = mUriMatcher.match(uri);
		SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
		case URI_FAVORITES:
			rowsUpdated = sqlDB.update(CharacterFavoritesDatabaseHelper.TABLE_NAME, values, selection,
					selectionArgs);
			break;
		case URI_FAVORITES_ITEM:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(CharacterFavoritesDatabaseHelper.TABLE_NAME, values,
						CharacterFavoritesConstants.ID + "=" + id, null);
			} else {
				rowsUpdated = sqlDB.update(CharacterFavoritesDatabaseHelper.TABLE_NAME, values,
						CharacterFavoritesConstants.ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		notifyChange(uri);
		return rowsUpdated;
	}// update()

	/**
	 * Borra un elemento viaje, a partir de su URI.
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.d(TAG, "delete");
		int uriType = mUriMatcher.match(uri);
		SQLiteDatabase sqlDB = mDbHelper.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case URI_FAVORITES:
			rowsDeleted = sqlDB.delete(CharacterFavoritesDatabaseHelper.TABLE_NAME,
					selection, selectionArgs);
			break;
		case URI_FAVORITES_ITEM:
			String id = uri.getLastPathSegment();
			Log.i(TAG,"Id to delete in Provider: "+id);
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(CharacterFavoritesDatabaseHelper.TABLE_NAME,
						CharacterFavoritesConstants.ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(CharacterFavoritesDatabaseHelper.TABLE_NAME,
						CharacterFavoritesConstants.ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		notifyChange(uri);
		return rowsDeleted;
	}

	/**
	 * Obtiene el tipo del URI, en funcion si es todos los viajes o un item viaje solo.
	 *  Para conocer el MIME Type de los datos devueltos por el provider. 
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		Log.d(TAG, "getType");
		int match = mUriMatcher.match(uri);		
		switch (match){
		case URI_FAVORITES:
			return "vnd.android.cursor.dir/vnd.example.favorites";
		case URI_FAVORITES_ITEM:
			return "vnd.android.cursor.item/vnd.example.favorites";
		default:
			Log.w(TAG, "Uri didn't match: " + uri);
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}// getType()
	
	/**
	 * Notifica el cambio de un item.
	 * @param uri del elemento que cambia
	 */
	protected void notifyChange(Uri uri) {
		Log.d(TAG, "notifyChange");
        getContext().getContentResolver().notifyChange(uri, null);
	}// notifyChange()
	
	/**
	 * Comprueba si la columna que llamamos no existe.
	 * @param projection para obtener las columnas
	 */
	private void checkColumns(String[] projection) {
		Log.d(TAG, "checkColumns");
		String[] available = { CharacterFavoritesConstants.ID, CharacterFavoritesConstants.NAME,
				CharacterFavoritesConstants.DESCRIPTION, CharacterFavoritesConstants.MODIFIED,
				CharacterFavoritesConstants.THUMBNAIL_URL, CharacterFavoritesConstants.THUMBNAIL_EXTENSION };
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(available));
			//## Comprueba si todas las columnas solicitadas estan disponibles
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}// checkColumns()	
}
