/**
 * 
 */
package alex.mj.marvelencyclopedia.characters;

import alex.mj.marvelencyclopedia.R;
import alex.mj.marvelencyclopedia.characters.data.CharacterFavoritesConstants;
import alex.mj.marvelencyclopedia.characters.data.CharacterFavoritesProvider;
import alex.mj.marvelencyclopedia.comics.UnderConstructionActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author alejandro.marijuan@googlemail.com
 *
 */
public class CharacterFavoritesActivity extends Activity {

	private static final String TAG = "CharacterFavoritesActivity: --->";
	
	private static String BUNDLE_NAME = "name";
	private static String BUNDLE_DESCRIPTION = "description";
	private static String BUNDLE_MODIFIED = "modified";
	private static String BUNDLE_THUMBNAIL_URL = "thumbnail_url";
	private static String BUNDLE_THUMBNAIL_EXTENSION = "thumbnail_extension";

	private TextView mID;
	private TextView mName;
	private TextView mDescription;
	private TextView mModified;
	private TextView mThumbnail_url;
	private TextView mThumbnail_extension;	
	
	/**VERSION 5: ActionBar**/
	private ShareActionProvider mShareActionProvider;
	private ImageButton locButton;

	
	private static String BUNDLE_ID="0";
	private static final int REQUEST_CODE_EDIT_TRIP = 1;

	
	/**
	 * Recoge y establece los datos de ciudad, pais, anio y nota,
	 * que proceden del Intent.
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		// CAMBIAMOS EL COLOR DE LA BARRA DE TITULO
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c20d1c")));
		// AÑADIMOS LA OPCIÓN DE NAVIGATION UP
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setContentView(R.layout.view_character_favorite);

		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		BUNDLE_ID = intent.getExtras().getString(CharacterInfo.EXTRA_ID);
		BUNDLE_NAME = intent.getExtras().getString(CharacterInfo.EXTRA_NAME);
		BUNDLE_DESCRIPTION = intent.getExtras().getString(CharacterInfo.EXTRA_DESCRIPTION);
		BUNDLE_MODIFIED = intent.getExtras().getString(CharacterInfo.EXTRA_MODIFIED);
		BUNDLE_THUMBNAIL_URL = intent.getExtras().getString(CharacterInfo.EXTRA_THUMBNAIL_URL);
		BUNDLE_THUMBNAIL_EXTENSION = intent.getExtras().getString(CharacterInfo.EXTRA_THUMBNAIL_EXTENSION);
		

		Log.i(TAG,"("+BUNDLE_ID+")"+BUNDLE_NAME+","+BUNDLE_DESCRIPTION+",("+BUNDLE_THUMBNAIL_URL+"/"+BUNDLE_THUMBNAIL_EXTENSION+"),"+ BUNDLE_MODIFIED );

		mID = (TextView) findViewById(R.id.idResult);
		mID.setText(BUNDLE_ID);
		mName = (TextView) findViewById(R.id.nameResult);
		mName.setText(BUNDLE_NAME);
		mDescription = (TextView) findViewById(R.id.descriptionResult);
		mDescription.setText(BUNDLE_DESCRIPTION);
		mModified = (TextView) findViewById(R.id.modifiedResult);
		mModified.setText(BUNDLE_MODIFIED.toString());
		
		
	}// onCreate()

	/**
	 * Guarda el estado de la Activity, con los datos del viaje,
	 *  a saber: Anio, ciudad, pais y nota.
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "onSaveInstanceState");
		//## Guardamos el estado
		String id = mID.getText().toString();
		String name = mName.getText().toString();
		String description = mDescription.getText().toString();
		String modified = mModified.getText().toString();
//		String thumbnail_url = mThumbnail_url.getText().toString();
//		String thumbnail_extension = mThumbnail_extension.getText().toString();
		
		outState.putString(BUNDLE_ID, id);
		outState.putString(BUNDLE_NAME, name);
		outState.putString(BUNDLE_DESCRIPTION, description);
		outState.putString(BUNDLE_MODIFIED, modified);
//		outState.putString(BUNDLE_THUMBNAIL_URL, thumbnail_url);
//		outState.putString(BUNDLE_THUMBNAIL_EXTENSION, thumbnail_extension);

		super.onSaveInstanceState(outState);
	}// onSaveInstanceState()

	/**
	 * Crea el menu de opciones en la Activity que muestra los datos de un viaje guardado,
	 *  muestra unicamente la opcion de compartir dicho viaje.
	 * VERSION 5: Aniadimos opciones desde ActionBar, mostrando una alerta al borrar viaje.
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.favorite_menu, menu);
		
		// Set up ShareActionProvider's default share intent
		/** Getting the actionprovider associated with the menu item whose id is share */
		mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.menu_share_favorite).getActionProvider();
	    /** Setting a share intent */
        mShareActionProvider.setShareIntent(getDefaultIntent());
		
        
        locButton = (ImageButton) menu.findItem(R.id.menu_delete_favorite).getActionView();
        ((ImageButton) locButton).setImageResource(android.R.drawable.ic_menu_delete);        
        locButton.setBackgroundColor(Color.TRANSPARENT);
        locButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				final String idResult4delete = mID.getText().toString();
				final String nameResult4delete = mName.getText().toString();
				final String descriptionResult4delete = mDescription.getText().toString();
				final String modifiedResult4delete = mModified.getText().toString();
				
				AlertDialog.Builder builder = new Builder(CharacterFavoritesActivity.this);
				builder.setTitle("DELETE HEROE");
				builder.setMessage("It will be delete "+nameResult4delete+"("+idResult4delete+")"+", date "+modifiedResult4delete+"...");
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String idDB4delete=BUNDLE_ID;
						Log.i(TAG, "---idDB4delete: "+ idDB4delete);			
						Uri uri = Uri.parse(CharacterFavoritesProvider.CONTENT_URI + "/" + idDB4delete);
						getContentResolver().delete(uri, null, null);
						onMenuItemSelected(2, menu.findItem(R.id.menu_delete_favorite));
					}
				});
				builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(CharacterFavoritesActivity.this, "DELETE CANCELED", Toast.LENGTH_SHORT).show();
					}
				});
				
				AlertDialog dialog = builder.create();
				dialog.show();				
			}
		});
        
        
	    return super.onCreateOptionsMenu(menu);
	}// onCreateOptionsMenu()

	/** Defines a default (dummy) share intent to initialize the action provider.
	  * However, as soon as the actual content to be used in the intent
	  * is known or changes, you must update the share intent by again calling
	  * mShareActionProvider.setShareIntent()
	  */
	private Intent getDefaultIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, BUNDLE_NAME + "("
				+ BUNDLE_DESCRIPTION + ")\n" + "Modified: " + BUNDLE_MODIFIED + "\n"
				+ "URL: " + BUNDLE_THUMBNAIL_URL + BUNDLE_THUMBNAIL_EXTENSION);
        return intent;
	}
	
	
	/**
	 * Genera las acciones necesarias para compartir un viaje con otra app.
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.d(TAG, "onMenuItemSelected");
		switch (item.getItemId()) {
		case R.id.menu_share_favorite:
			//## Creamos el Intent para lanzar la Activity que permita compartir el viaje
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, BUNDLE_NAME + "("
					+ BUNDLE_DESCRIPTION + ")\n" + "Modified: " + BUNDLE_MODIFIED + "\n"
					+ "URL: " + BUNDLE_THUMBNAIL_URL + BUNDLE_THUMBNAIL_EXTENSION);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, getResources()
					.getText(R.string.send_to)));
			break;
		case R.id.menu_edit_favorite:
			Log.i(TAG, "Etiqueta: Opcion EDITAR pulsada!");
			//## Creamos el Intent para lanzar la Activity EditTravelActivity
			Intent intent = new Intent(this, UnderConstructionActivity.class);	
			
			//## Obtenemos los datos para incluirlos en el Intent
			String id4edit=mID.getText().toString();
			String nameResult4edit = mModified.getText().toString();
			String descriptionResult4edit = mName.getText().toString();
			String modifiedResult4edit = mDescription.getText().toString();
//			String thumbnailUrlResult4edit = mThumbnail_url.getText().toString();
//			String thumbnailExtensionResult4edit = mThumbnail_extension.getText().toString();
			
			intent.putExtra("SavedDataTripID", id4edit);
			intent.putExtra("SavedDataTripName", nameResult4edit);
			intent.putExtra("SavedDataTripDescription", descriptionResult4edit);
			intent.putExtra("SavedDataTripModified", modifiedResult4edit);
//			intent.putExtra("SavedDataTripThumbnailUrl", thumbnailUrlResult4edit);
//			intent.putExtra("SavedDataTripThumbnailExtension", thumbnailExtensionResult4edit);
			
			Log.i(TAG, "info del viaje para editar: ("+id4edit+")" + nameResult4edit + ","
					+ descriptionResult4edit + "," + modifiedResult4edit.toString()
					+ ",");// + thumbnailUrlResult4edit+"/"+thumbnailExtensionResult4edit);
			startActivityForResult(intent, REQUEST_CODE_EDIT_TRIP);
			return true;
		case R.id.menu_delete_favorite:
				Log.i(TAG, "Etiqueta: Opcion BORRAR pulsada!");
				final String idResult4delete = mID.getText().toString();
				final String nameResult4delete = mName.getText().toString();				
				Toast.makeText(CharacterFavoritesActivity.this, "DELETED HERO "+nameResult4delete+"("+idResult4delete+")", Toast.LENGTH_LONG).show();
			//## Volvemos a la lista de viajes			
			Intent intentDelete = new Intent(this, CharacterFavoritesListActivity.class);
			startActivity(intentDelete);
			
			return true;			
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}// onMenuItemSelected()


	/**
	 * VERSION 5: Recoge los datos de un viaje editado.
	 *  Diferenciamos el metodo en funcion de si es un viaje nuevo o editado.
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		//**************Seccion para edicion de un viaje***********************//	
		if (requestCode == REQUEST_CODE_EDIT_TRIP && resultCode == RESULT_OK) {
			Log.i(TAG, "REQUEST_CODE_EDIT_TRIP: Viaje Editado!");
			String[] resultsTrip = data.getExtras().getStringArray("myFavoriteCreated");
			String myNewFavoriteID=resultsTrip[0];
			String myNewFavoriteName = resultsTrip[1];
			String myNewFavoriteDescription = resultsTrip[2];
			String myNewFavoriteModified = resultsTrip[3];
			String myNewFavoriteThumbnail_url = resultsTrip[4];
			String myNewFavoriteThumbnail_extension = resultsTrip[5];
			
			Log.w(TAG, "info del Viaje Editado:"+myNewFavoriteID+"-"+myNewFavoriteName+"("+myNewFavoriteDescription+"),"+myNewFavoriteModified+"--"+myNewFavoriteThumbnail_url+"/"+myNewFavoriteThumbnail_extension);				
			ContentValues valuesTripEdited = new ContentValues();
			
			valuesTripEdited.put(CharacterFavoritesConstants.ID, myNewFavoriteID);
			valuesTripEdited.put(CharacterFavoritesConstants.NAME, myNewFavoriteName);
			valuesTripEdited.put(CharacterFavoritesConstants.DESCRIPTION, myNewFavoriteDescription);
			valuesTripEdited.put(CharacterFavoritesConstants.MODIFIED, myNewFavoriteModified);
			valuesTripEdited.put(CharacterFavoritesConstants.THUMBNAIL_URL, myNewFavoriteThumbnail_url);
			valuesTripEdited.put(CharacterFavoritesConstants.THUMBNAIL_EXTENSION, myNewFavoriteThumbnail_extension);
			//## Update viaje en el Provider
			getContentResolver().update(CharacterFavoritesProvider.CONTENT_URI, valuesTripEdited, CharacterFavoritesConstants.ID+"="+myNewFavoriteID, null);
			Toast.makeText(CharacterFavoritesActivity.this, "HEROE "+myNewFavoriteName+"("+myNewFavoriteID+") SAVED", Toast.LENGTH_LONG).show();
			//## Volvemos a la lista de viajes
			Intent intent = new Intent(this, CharacterFavoritesListActivity.class);
			startActivity(intent);
		}
		else {
			Log.i(TAG, "ACTION CANCELED " + resultCode + "-" + requestCode);
		}

	}// onActivityResult()
}
