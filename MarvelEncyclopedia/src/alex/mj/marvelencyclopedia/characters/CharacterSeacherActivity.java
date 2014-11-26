/**
 * 
 */
package alex.mj.marvelencyclopedia.characters;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import alex.mj.marvelencyclopedia.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * @author alejandro.marijuan@googlemail.com
 *
 */
public class CharacterSeacherActivity extends Activity{
	
	private static final String TAG = "CharacterSeacherActivity: --->";
	private AutoCompleteTextView urlText;
	private ImageButton button_searchCharacter;
	
	// ****NETWORK ACTIVITY*****//
	public static final String WIFI = "Wi-Fi";
	public static final String ANY = "Any";

	// *****CHARACTER DATA*****//
	private String CHARACTER_id = "";
	private String CHARACTER_name = "";
	private String CHARACTER_description = "";
	private String CHARACTER_modified = "";
	private String CHARACTER_thumbnail_url = "";
	private String CHARACTER_thumbnail_extension = "";

	// ****CONNECTION INFO*****//
	private static final String MARVEL_URL = "http://gateway.marvel.com:80";
	private static final String CHARACTERS = "/v1/public/characters";
	private static final String TS = "?ts=1";
	private static final String PUBLIC_API_KEY = "&apikey=b4b9062ae0e13b8a95d9b657cbc3ea4e";
	private static final String HASH_MD5 = "&hash=30faf5304f290b5b5457bd86647b485c";
	private static final String GET_STRING_DATA = MARVEL_URL + CHARACTERS + TS
			+ PUBLIC_API_KEY + HASH_MD5;
	private static String THUMBNAIL_URL = "";
	private ImageView mTHUMBNAIL;
	private String mNameToSearch;

	// ****JSON NODES*****//
	private static final String TAG_ATTRIBUTIONHTML = "attributionhtml";
	private static final String TAG_ATTRIBUTIONTEXT = "attributiontext";
	private static final String TAG_AVAILABLE = "available";
	private static final String TAG_CODE = "code";
	private static final String TAG_COLLECTIONURI = "collectionuri";
	private static final String TAG_COMICS = "comics";
	private static final String TAG_COPYRIGHT = "copyright";
	private static final String TAG_COUNT = "count";
	private static final String TAG_DATA = "data";
	private static final String TAG_DESCRIPTION = "description";
	private static final String TAG_ETAG = "etag";
	private static final String TAG_EVENTS = "events";
	private static final String TAG_EXTENSION = "extension";
	private static final String TAG_ID = "id";
	private static final String TAG_LIMIT = "limit";
	private static final String TAG_MODIFIED = "modified";
	private static final String TAG_NAME = "name";
	private static final String TAG_OFFSET = "offset";
	private static final String TAG_PATH = "path";
	private static final String TAG_RESOURCEURI = "resourceuri";
	private static final String TAG_RETURNED = "returned";
	private static final String TAG_SERIES = "series";
	private static final String TAG_STATUS = "status";
	private static final String TAG_STORIES = "stories";
	private static final String TAG_THUMBNAIL = "thumbnail";
	private static final String TAG_TOTAL = "total";
	private static final String TAG_TYPE = "type";
	private static final String TAG_URL = "url";

	// Listas de items JSONArray
	private static final String TAG_ITEMS = "items";
	private static final String TAG_RESULTS = "results";
	private static final String TAG_URLS = "urls";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_charactersearcher_main);
		// CAMBIAMOS EL COLOR DE LA BARRA DE TITULO
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c20d1c")));
		// AÑADIMOS LA OPCIÓN DE NAVIGATION UP
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// OPCIONES PARA EL AUTOCOMPLETADO EN LA BÚSQUEDA
		String[] heroes = getResources().getStringArray(R.array.list_of_heroes);
		ArrayAdapter<?> adapter = new ArrayAdapter<Object>(this,
				android.R.layout.simple_list_item_1, heroes);
		urlText = (AutoCompleteTextView) findViewById(R.id.selectHeroe);
		urlText.setAdapter(adapter);
		// LANZAMOS LA BÚSQUEDA
		button_searchCharacter = (ImageButton) findViewById(R.id.BT_Search_Character);
		button_searchCharacter.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				myClickHandler(v);
			}
		});
	}

	/**
	 * When user clicks button, calls AsyncTask. Before attempting to fetch the
	 * URL, makes sure that there is a network connection.
	 * 
	 * @param view
	 */
	public void myClickHandler(View view) {
		Log.d(TAG, "myClickHandler()");
		// Gets the URL from the UI's text field.
		mNameToSearch = escapeUrlText(urlText.getText().toString());
		Log.d(TAG, "---mNameToSearch is: " + mNameToSearch);
		if (mNameToSearch != "") {
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				// fetch data
				Toast.makeText(CharacterSeacherActivity.this, getResources().getText(R.string.conection_available),
						Toast.LENGTH_SHORT).show();
				Log.d(TAG, "URL is: " + GET_STRING_DATA + "&name="
						+ mNameToSearch);
				new DownloadWebpageTask().execute(GET_STRING_DATA + "&name="
						+ mNameToSearch);
			} else {
				// display error
				Toast.makeText(
						CharacterSeacherActivity.this,
						getResources().getText(R.string.no_conection_available),
						Toast.LENGTH_SHORT).show();
			}
		} else
			Toast.makeText(CharacterSeacherActivity.this,
					getResources().getText(R.string.no_input_name),
					Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * @param string
	 * @return
	 */
	private String escapeUrlText(String url) {
		Log.d(TAG, "escapeUrlText()");
		if (url != null) {
			url = TextUtils.htmlEncode(url);
			url = url.replace(" ", "+");
		} else
			url = "";

		Log.d(TAG, "---url is: " + url);
		return url;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        Intent upIntent = NavUtils.getParentActivityIntent(this);
	        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
	            // This activity is NOT part of this app's task, so create a new task
	            // when navigating up, with a synthesized back stack.
	            TaskStackBuilder.create(this)
	                    // Add all of this activity's parents to the back stack
	                    .addNextIntentWithParentStack(upIntent)
	                    // Navigate up to the closest parent
	                    .startActivities();
	        } else {
	            // This activity is part of this app's task, so simply
	            // navigate up to the logical parent activity.
	            NavUtils.navigateUpTo(this, upIntent);
	        }
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	/********************************************************************************/
	/******************** CONNECTION TO MARVEL ****************************************/
	/********************************************************************************/

	/**
	 * Uses AsyncTask to create a task away from the main UI thread. - This task
	 * takes a URL string and uses it to create an HttpUrlConnection. - Once the
	 * connection has been established, the AsyncTask downloads the contents of
	 * the webpage as an InputStream. - Finally, the InputStream is converted
	 * into a string, which is displayed in the UI by the AsyncTask's
	 * onPostExecute method.
	 * 
	 * @author alejandro.marijuan@googlemail.com
	 * 
	 */
	private class DownloadWebpageTask extends
			AsyncTask<String, Void, CharacterInfo> {
		private CharacterInfo mInfoCharacter;

		/**
		 *  
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			Log.d(TAG, "onPreExecute()");
			super.onPreExecute();
		}

		@Override
		protected CharacterInfo doInBackground(String... urls) {
			Log.d(TAG, "doInBackground()");
			// params comes from the execute() call: params[0] is the url.
			String characterName = urls[0];
			// primero comprobamos que el nombre de entrada no esté vacío.
			if (TextUtils.isEmpty(characterName))
				throw new IllegalArgumentException(getResources().getText(
						R.string.name_empty_error).toString());

			URL url = null;
			HttpURLConnection conn = null;

			try {
				// return downloadUrl(urls[0]);

				// Construimos la URL y realizamos la llamada
				url = new URL(urls[0]);
				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				// Starts the query
				conn.connect();
				int response = conn.getResponseCode();
				Log.d(TAG, "The response is: " + response);
				InputStream is = conn.getInputStream();

				// Parseamos la respuesta en formato JSON
				mInfoCharacter = readCharacterInfo(is);
				return mInfoCharacter;

			} catch (IOException e) {
				Log.e(TAG, "URL is: " + GET_STRING_DATA + "&name="
						+ mNameToSearch);
				// return getResources().getString(R.string.invalid_url);
				Toast.makeText(CharacterSeacherActivity.this,
						getResources().getString(R.string.invalid_url),
						Toast.LENGTH_SHORT).show();
			} finally {
				if (conn != null)
					conn.disconnect();
			}
			return null;
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(CharacterInfo result) {
			Log.d(TAG, "onPostExecute()");
			super.onPostExecute(result);
			Intent showResults = new Intent(getBaseContext(),CharacterSearchResultActivity.class);
			String[] characterValues = {mInfoCharacter.getId(),
										mInfoCharacter.getName(),
										mInfoCharacter.getDescription(),
										mInfoCharacter.getModified(),
										mInfoCharacter.getThumbnail_url(),
										mInfoCharacter.getThumbnail_extension()};
			for (String data:characterValues){
				Log.d(TAG,data);
			}
			showResults.putExtra("CharacterValues", characterValues);			
			// ##Lanzamos la Activity con el Intent creado.
			startActivity(showResults);
		}

		/**
		 * Parsea la respuesta en JSon a partir de la mInfoCharacterrmacion del servicio
		 * 
		 * @param is
		 * @return
		 */
		private CharacterInfo readCharacterInfo(InputStream is) {
			Log.d(TAG, "readCharacterInfo()");
			if (is == null)
				return null;
			mInfoCharacter = new CharacterInfo(CHARACTER_id,
					CHARACTER_name, CHARACTER_description, CHARACTER_modified,
					CHARACTER_thumbnail_url, CHARACTER_thumbnail_extension);
			JsonReader reader = null;
			try {
				reader = new JsonReader(new InputStreamReader(is));
				reader.beginObject();
				while (reader.hasNext()) {
					Log.d(TAG, "---while (reader.hasNext())");
					if (isCancelled()) {
						Log.d(TAG, "---if (isCancelled())");
						break; // Comprobacion de si ha sido cancelada
					}
					String nameNode = reader.nextName();
					Log.d(TAG, "---nameNode: " + nameNode);

					if (nameNode.equals(TAG_CODE)
							&& reader.peek() != JsonToken.NULL) {
						int code = reader.nextInt();
						switch (code) {
						case 200:
							mInfoCharacter.setDescription(getResources().getText(
									R.string.connectionOK_noResultFound)
									.toString());
							mInfoCharacter.setThumbnail_url("http://i.annihil.us/u/prod/marvel/i/mg/b/40/image_not_available");
							mInfoCharacter.setThumbnail_extension("jpg");
						}
					} else if (nameNode.equals(TAG_DATA)
							&& reader.peek() != JsonToken.NULL) { // Nodo
																	// resultados,
						// con la
						// mInfoCharacterrmación del
						// personaje
						Log.d(TAG, "---nameNode.equals(TAG_DATA)");

						reader.beginObject();
						while (reader.hasNext()) {
							String nameNode2 = reader.nextName();
							Log.d(TAG, "---nameNode2: " + nameNode2);
							if (nameNode2.equals(TAG_RESULTS)
									&& reader.peek() != JsonToken.NULL) { // Nodo
																			// resultados,
																			// con
																			// la
																			// mInfoCharacterrmación
																			// del
																			// personaje
								Log.d(TAG,
										"---nameNode2.equals(TAG_RESULTS)");
								reader.beginArray();
								while (reader.hasNext()) {
									Log.d(TAG,
											"---while (reader.hasNext())---(TAG_RESULTS)");

									String id = "";
									String name = "";
									String description = "";
									String modified = "";
									// **Iniciamos el valor de la imagen con una
									// propia de la BD de marvel tipo NOT
									// FOUND**//
									String thumbnail_url = "http://i.annihil.us/u/prod/marvel/i/mg/b/40/image_not_available";
									String thumbnail_extension = "jpg";

									reader.beginObject();
									while (reader.hasNext()) {
										String nameNode3 = reader.nextName();
										Log.d(TAG, "---nameNode2: "
												+ nameNode3);
										if (nameNode3.equals(TAG_ID)
												&& reader.peek() != JsonToken.NULL) {
											Log.d(TAG,
													"---nameNode3.equals(TAG_ID)");
											id = reader.nextString();
											if (id.compareTo("") == 0)
												id = getResources().getText(
														R.string.without_id)
														.toString();
										} else if (nameNode3.equals(TAG_NAME)
												&& reader.peek() != JsonToken.NULL) {
											Log.d(TAG,
													"---nameNode3.equals(TAG_NAME)");
											name = reader.nextString();
											if (name.compareTo("") == 0)
												name = getResources().getText(
														R.string.without_name)
														.toString();
										} else if (nameNode3
												.equals(TAG_DESCRIPTION)
												&& reader.peek() != JsonToken.NULL) {
											Log.d(TAG,
													"---nameNode3.equals(TAG_DESCRIPTION)");
											description = reader.nextString();
											if (description.compareTo("") == 0)
												description = getResources()
														.getText(
																R.string.without_description)
														.toString();
										} else if (nameNode3
												.equals(TAG_MODIFIED)
												&& reader.peek() != JsonToken.NULL) {
											Log.d(TAG,
													"---nameNode3.equals(TAG_MODIFIED)");
											modified = reader.nextString();
											if (modified.compareTo("") == 0)
												modified = getResources()
														.getText(
																R.string.without_modified_date)
														.toString();
										} else if (nameNode3
												.equals(TAG_THUMBNAIL)
												&& reader.peek() != JsonToken.NULL) { // Condition
											Log.d(TAG,
													"---nameNode3.equals(TAG_THUMBNAIL)");
											reader.beginObject();
											while (reader.hasNext()) {
												String nameNode4 = reader
														.nextName();
												if (nameNode4.equals(TAG_PATH)
														&& reader.peek() != JsonToken.NULL) {
													Log.d(TAG,
															"---nameNode4.equals(TAG_PATH)");
													thumbnail_url = reader
															.nextString();
												} else if (nameNode4
														.equals(TAG_EXTENSION)
														&& reader.peek() != JsonToken.NULL) {
													Log.d(TAG,
															"---nameNode4.equals(TAG_PATH)");
													thumbnail_extension = reader
															.nextString();
												} else {
													reader.skipValue();
												}
											}
											reader.endObject();

										} else
											reader.skipValue();
									}
									reader.endObject();
									mInfoCharacter.setId(id);
									mInfoCharacter.setName(name);
									mInfoCharacter.setDescription(description);
									mInfoCharacter.setModified(modified);
									mInfoCharacter.setThumbnail_url(thumbnail_url);
									mInfoCharacter.setThumbnail_extension(thumbnail_extension);
								}
								reader.endArray();
								Log.d(TAG, "---reader.endArray()");

							} else {
								reader.skipValue();
								Log.d(TAG, "---reader.skipValue()");
							}
						}
						reader.endObject();

					} else {
						reader.skipValue();
						Log.d(TAG, "---reader.skipValue()");
					}
				}
				reader.endObject();
				Log.d(TAG, "---reader.endObject()");

			} catch (RuntimeException e1) {
				Log.e(TAG, "---IOException: " + e1);
				e1.printStackTrace();
			} catch (IOException e2) {
				Log.e(TAG, "---IOException: " + e2);
				e2.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			Log.d(TAG, "id es: " + mInfoCharacter.getId());
			Log.d(TAG, "name es: " + mInfoCharacter.getName());
			Log.d(TAG, "description es: (" + mInfoCharacter.getDescription() + ")");
			Log.d(TAG, "modified es: " + mInfoCharacter.getModified());
			Log.d(TAG, "thumbnail_url es: " + mInfoCharacter.getThumbnail_url());
			Log.d(TAG,
					"thumbnail_extension es: " + mInfoCharacter.getThumbnail_extension());
			Log.d(TAG, "THUMBNAIL_URL es: " + mInfoCharacter.getThumbnail_url()
					+ "/standard_fantastic." + mInfoCharacter.getThumbnail_extension());

			THUMBNAIL_URL = mInfoCharacter.getThumbnail_url() + "/standard_fantastic."
					+ mInfoCharacter.getThumbnail_extension();

			return mInfoCharacter;
		}
	}
	
}
