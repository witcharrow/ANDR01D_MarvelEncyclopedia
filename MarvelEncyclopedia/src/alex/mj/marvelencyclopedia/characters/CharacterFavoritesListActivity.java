/**
 * 
 */
package alex.mj.marvelencyclopedia.characters;

import java.util.ArrayList;

import alex.mj.marvelencyclopedia.R;
import alex.mj.marvelencyclopedia.characters.data.CharacterFavoritesConstants;
import alex.mj.marvelencyclopedia.characters.data.CharacterFavoritesDatabaseHelper;
import alex.mj.marvelencyclopedia.characters.data.CharacterFavoritesProvider;
import alex.mj.marvelencyclopedia.comics.UnderConstructionActivity;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author alejandro.marijuan@googlemail.com
 *
 */
public class CharacterFavoritesListActivity extends ListActivity{
	
	private static final String TAG = "CharacterFavoritesListActivity: --->";
	
	private static final int REQUEST_CODE_NEW_FAVORITE = 1;
	private static final int REQUEST_CODE_EDIT_FAVORITE = 2;	
	// ##Para comunicar con la EditTravelActivity
	private String result;
	
	// ##VERSION 4: FavoritesCursorAdapter
	private static CharacterFavoritesDatabaseHelper dbHelper;
	private Uri newFavoriteHeroeUri;
	private FavoritesCursorAdapter mAdapter;	
	private static final String[] PROJECTION = {CharacterFavoritesConstants.ID,
												CharacterFavoritesConstants.NAME, 
												CharacterFavoritesConstants.DESCRIPTION,
												CharacterFavoritesConstants.MODIFIED,
												CharacterFavoritesConstants.THUMBNAIL_URL,
												CharacterFavoritesConstants.THUMBNAIL_EXTENSION};

	/**
	 * Adapter que contiene la lista con los heroes favoritos.
	 * 	Implementamos CursorAdapter para poder ejecutar el metodo changeCursor y actualizar el adapter,
	 *  ya que desde la versión SDK11 no se actualiza automaticamente con el metodo notify().
	 * @author Alejandro.Marijuan
	 *
	 */
	final class FavoritesCursorAdapter extends CursorAdapter {

		private static final String TAG = "FavoritesCursorAdapter: --->";
		private LayoutInflater mInflater;
		
		public FavoritesCursorAdapter(Context context, Cursor c) {
			super(context, c, 0);
			mInflater=LayoutInflater.from(context);
		}

		/**
		 * Genera la nueva vista para una lista con dos elementos.
		 * @see android.widget.CursorAdapter#newView(android.content.Context, android.database.Cursor, android.view.ViewGroup)
		 */
		public View newView (Context context, Cursor cursor, ViewGroup parent){
			Log.d(TAG, "newView");
			//## En el metodo newView creamos la vista y el holder y lo almacenamos en el tag
			View view = mInflater.inflate(android.R.layout.simple_list_item_2, parent,false);
			ViewHolder holder = new ViewHolder();
			TextView textView1 = (TextView) view.findViewById(android.R.id.text1);
			TextView textView2 = (TextView) view.findViewById(android.R.id.text2);
			holder.text1 = textView1;
			holder.text2 = textView2;
			view.setTag(holder);
			view.setBackgroundColor(Color.BLACK);
			
			return view;
			
		}// newView()
		
		/**
		 * Introduce los datos de cada elemento tipo Favorito en una posicion de la vista generada.
		 * @see android.widget.CursorAdapter#bindView(android.view.View, android.content.Context, android.database.Cursor)
		 */
		@Override
		public void bindView(View v, Context context, Cursor c) {
			Log.d(TAG, "bindView");
			//## En el metodo bindView recuperamos el Holder y asignamos los datos a las vistas
			ViewHolder holder = (ViewHolder) v.getTag();
			String id = c.getString(c.getColumnIndex(CharacterFavoritesConstants.ID));
			String name = c.getString(c.getColumnIndex(CharacterFavoritesConstants.NAME));
			String description = c.getString(c.getColumnIndex(CharacterFavoritesConstants.DESCRIPTION));
			String modified = c.getString(c.getColumnIndex(CharacterFavoritesConstants.MODIFIED));
			String thumbnail_url = c.getString(c.getColumnIndex(CharacterFavoritesConstants.THUMBNAIL_URL));
			String thumbnail_extension = c.getString(c.getColumnIndex(CharacterFavoritesConstants.THUMBNAIL_EXTENSION));
			Log.d(TAG,"("+id+")"+name+","+description+","+modified+","+thumbnail_url+","+thumbnail_extension);
			holder.text1.setText(name);
			holder.text2.setText("("+id+")--"+modified);		
		}// bindView()
	}
	
	/**
	 * Vista para cada elemento de la lista.
	 * @author Alejandro.Marijuan
	 *
	 */
	static class ViewHolder {
		TextView text1;
		TextView text2;
	}// ViewHolder()

    /**
     * Genera la lista de favoritos.
     * Genera un Intent para almacenar un favorito nuevo.
     * Asocia los menus contextuales a los controles.
     * Introducimos el FavoritesCursorAdapter, ordenando la lista de favoritos por nombre.
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
		// CAMBIAMOS EL COLOR DE LA BARRA DE TITULO
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c20d1c")));
		// AÑADIMOS LA OPCIÓN DE NAVIGATION UP
		getActionBar().setDisplayHomeAsUpEnabled(true);
        
        dbHelper = new CharacterFavoritesDatabaseHelper(this);
        
        /**VERSION 4: FavoritesCursorAdapter*/ 		        
        ContentResolver cr = getContentResolver();        
        //## Hacemos la consulta
        Cursor c = cr.query(CharacterFavoritesProvider.CONTENT_URI,PROJECTION, null, null, CharacterFavoritesConstants.NAME+" ASC");
        //## y Construimos el Adapter con el cursor                
        mAdapter = new FavoritesCursorAdapter(this,c);
        setListAdapter(mAdapter);
        
        //##TODO:BORRAR?? Para devolver el resultado a la EditTravelActivity
		Intent returnIntent = new Intent();
		returnIntent.putExtra("result", result);
		setResult(RESULT_OK, returnIntent);
		
		//## Asociamos los menus contextuales a los controles
		registerForContextMenu(getListView());		
    }// onCreate()
    
    /**
     * Crea un menu de opciones, contiene la opcion generar un nuevo viaje.
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.listview_favorite_menu, menu);
		return true;
	}// onCreateOptionsMenu()

	/**
	 * Lanza un Intent para crear un nuevo viaje a EditTravelActivity.
	 * @see android.app.Activity#onMenuItemSelected(int, android.view.MenuItem)
	 */
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.d(TAG, "onMenuItemSelected");
		switch (item.getItemId()) {
		case R.id.menu_new_favorite:
			//## Creamos el Intent para lanzar la Activity EditTravelActivity
			Intent intent = new Intent(this, UnderConstructionActivity.class);		
			startActivityForResult(intent, REQUEST_CODE_NEW_FAVORITE);
			break;
		}		
		return super.onMenuItemSelected(featureId, item);
	}// onMenuItemSelected()

	/**
	 * Crea el Intent con los datos necesarios para mostrar un viaje por pantalla.
	 * @see android.app.ListActivity#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d(TAG, "onListItemClick");
		//## Tomamos la informacion del viaje seleccionado
		long row = mAdapter.getItemId(position);
		CharacterInfo info = dbHelper.getFavoriteInfo(String.valueOf(row));				
		//## Creamos el Intent para lanzar la Activity TravelActivity
		Intent intent = new Intent(this, CharacterFavoritesActivity.class);
		//## Aniadimos como extras los datos que consideremos necesarios para la
		//## Activity a lanzar
		
		intent.putExtra(CharacterInfo.EXTRA_ID, row);
		intent.putExtra(CharacterInfo.EXTRA_NAME, info.getName());
		intent.putExtra(CharacterInfo.EXTRA_DESCRIPTION, info.getDescription());
		intent.putExtra(CharacterInfo.EXTRA_MODIFIED, info.getModified());
		intent.putExtra(CharacterInfo.EXTRA_THUMBNAIL_URL, info.getThumbnail_url());
		intent.putExtra(CharacterInfo.EXTRA_THUMBNAIL_EXTENSION, info.getThumbnail_extension());
			Log.w(TAG,"("+row+")"+info.getName()+","+info.getDescription()+",("+info.getModified()+"),"+  info.getThumbnail_url()+",("+info.getThumbnail_extension()+")," );
			
		//## Lanzamos la Activity con el Intent creado a TravelActivity
		startActivity(intent);
		super.onListItemClick(l, v, position, id);
	}// onListItemClick()

	/**
	 * Recoge los datos de un viaje editado.
	 *  Diferenciamos el metodo en funcion de si es un viaje nuevo o editado.
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
		boolean newTripToAdd=false;
		int myBiggestID=0;
		
		//**************Seccion para un nuevo viaje***********************//		
		if (resultCode == RESULT_OK	&& data.getExtras().containsKey("myHeroeCreated")) {
			Log.i(TAG, "RESULT_OK -- Nuevo viaje!");
			String[] resultsHeroe = data.getExtras().getStringArray("myHeroeCreated");
			
			String myNewHeroeID=resultsHeroe[0];
			String myNewHeroeName = resultsHeroe[1];
			String myNewHeroeDescription = resultsHeroe[2];
			String myNewHeroeModified = resultsHeroe[3];
			String myNewHeroeThumbnail_url = resultsHeroe[4];
			String myNewHeroeThumbnail_extension = resultsHeroe[5];
			
			
			Log.i(TAG, "Añadimos al adapter la info del heroe");
		    ContentValues valuesNewHeroe = new ContentValues();
		    valuesNewHeroe.put(CharacterFavoritesConstants.ID, myNewHeroeID);
		    valuesNewHeroe.put(CharacterFavoritesConstants.NAME, myNewHeroeName);
		    valuesNewHeroe.put(CharacterFavoritesConstants.DESCRIPTION, myNewHeroeDescription);
		    valuesNewHeroe.put(CharacterFavoritesConstants.MODIFIED, myNewHeroeModified);
		    valuesNewHeroe.put(CharacterFavoritesConstants.THUMBNAIL_URL, myNewHeroeThumbnail_url);
		    valuesNewHeroe.put(CharacterFavoritesConstants.THUMBNAIL_EXTENSION, myNewHeroeThumbnail_extension);

		    if (newFavoriteHeroeUri == null) {
		      //## Nuevo viaje
		    	newFavoriteHeroeUri = getContentResolver().insert(CharacterFavoritesProvider.CONTENT_URI, valuesNewHeroe);
		    }
		    else{
		    	getContentResolver().insert(CharacterFavoritesProvider.CONTENT_URI, valuesNewHeroe);			    	
		    }			    
			 
			//**************Seccion para edicion de un viaje***********************//	
			if (requestCode == REQUEST_CODE_EDIT_FAVORITE) {
				Log.i(TAG, "REQUEST_CODE_EDIT_FAVORITE: Heroe Editado!");
				Log.w(TAG, "info del Heroe Editado:"+myNewHeroeID+"-"+myNewHeroeName+"("+myNewHeroeDescription+"),"+myNewHeroeModified+"--"+myNewHeroeThumbnail_url+"/"+myNewHeroeThumbnail_extension);				
				ContentValues valuesTripEdited = new ContentValues();

				valuesTripEdited.put(CharacterFavoritesConstants.ID, myNewHeroeID);
				valuesTripEdited.put(CharacterFavoritesConstants.NAME, myNewHeroeName);
				valuesTripEdited.put(CharacterFavoritesConstants.DESCRIPTION, myNewHeroeDescription);
				valuesTripEdited.put(CharacterFavoritesConstants.MODIFIED, myNewHeroeModified);
				valuesTripEdited.put(CharacterFavoritesConstants.THUMBNAIL_URL, myNewHeroeThumbnail_url);
				valuesTripEdited.put(CharacterFavoritesConstants.THUMBNAIL_EXTENSION, myNewHeroeThumbnail_extension);
				//## Update viaje en el Provider
				getContentResolver().update(CharacterFavoritesProvider.CONTENT_URI, valuesTripEdited, CharacterFavoritesConstants.ID+"="+myNewHeroeID, null);				
			}			
			//## VERSION 4: Cambiamos el ResourceCursorAdapter por CursorAdapter para poder actualizar el adapter.
			ContentResolver cr = getContentResolver();
			Cursor c = cr.query(CharacterFavoritesProvider.CONTENT_URI,PROJECTION, null, null, CharacterFavoritesConstants.NAME+" ASC");
	    	mAdapter.changeCursor(c);
	    	Toast.makeText(CharacterFavoritesListActivity.this, "Guardado Heroe "+myNewHeroeName+"("+myNewHeroeID+")", Toast.LENGTH_LONG).show();
		}
		else {
			Log.i(TAG, "ACCIÓN CANCELADA " + resultCode + "-" + requestCode);
		}

	}// onActivityResult()

	/**
	 * Crea un menu con opciones para pulsacion larga sobre un item viaje.
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
	 * android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		Log.d(TAG, "onCreateContextMenu");
		super.onCreateContextMenu(menu, v, menuInfo);
		Log.i(TAG, "pulsación larga...");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
		menu.setHeaderTitle(R.string.menuContx);
	}// onCreateContextMenu()

	/**
     * Obtiene los datos de la tabla de la BBDD y los almacena en una lista.
     *  Lo utilizamos en la VERSION 4 de la app.
     * @return lista de viajes
     */
	public ArrayList<CharacterInfo> getTravelsList() {
		Log.d(TAG, "getTravelsList");
		ArrayList<CharacterInfo> travels = new ArrayList<CharacterInfo>();
		Cursor c = getContentResolver().query(CharacterFavoritesProvider.CONTENT_URI,null, null, null, CharacterFavoritesConstants.NAME+" ASC");
		if (c != null && c.moveToFirst()) {
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
					Log.i(TAG,"%%%%%%%%%%%%%%%%%%%%% idDB="+idDB);
				CharacterInfo favorite = new CharacterInfo(idDB, name, description, modified, thumbnail_url, thumbnail_extension);
				travels.add(favorite);
			} while (c.moveToNext());
			c.close();
		}
    	Log.i(TAG, "Nro de viajes en DB: "+ travels.size());
		return travels;
	}// getTravelsList()
    
	/**
	 * Genera las opciones de COMPARTIR,EDITAR,BORRAR.
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Log.d(TAG, "onContextItemSelected");
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			return false;
		}
		long row = getListAdapter().getItemId(info.position);
		Log.i(TAG, "ListAdapter id = " + row + "---" + item.getTitle());

		switch (item.getItemId()) {
		case R.id.CtxLblOpc1:
			Log.i(TAG, "Etiqueta: Opcion 1 pulsada!--COMPARTIR");
			//## Creamos el Intent para lanzar la Activity que permita compartir el viaje
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			CharacterInfo myHeroe = dbHelper.getFavoriteInfo(String.valueOf(row));
			//## Obtenemos los datos para incluirlos en el intent.			
			String nameResult = myHeroe.getName();
			String descriptionResult = myHeroe.getDescription();
			String modifiedResult = myHeroe.getModified();
			String thumbnailUrlResult = myHeroe.getThumbnail_url();
			String thumbnailExtensionResult = myHeroe.getThumbnail_extension();
			
			//## Creamos la la forma en que quedará representado el viaje al compartirlo
			sendIntent.putExtra(Intent.EXTRA_TEXT,nameResult + "("
							+ descriptionResult + ")\n" + "Modified: "
							+ modifiedResult + "\n" + "URL: "
							+ thumbnailUrlResult+thumbnailExtensionResult);
			sendIntent.setType("text/plain");
			startActivity(Intent.createChooser(sendIntent, getResources()
					.getText(R.string.send_to)));
			return true;
		case R.id.CtxLblOpc2:
			Log.i(TAG, "Etiqueta: Opcion 2 pulsada!--EDITAR");
			//## Creamos el Intent para lanzar la Activity EditTravelActivity
			Intent intent = new Intent(this, UnderConstructionActivity.class);
//			CharacterInfo myHeroe4Edit = dbHelper.getTravelInfo((int) row);
//			//## Obtenemos los datos para incluirlos en el Intent
//			int idDB4edit = myHeroe4Edit.getIdDB();
//			String cityResult4edit = myHeroe4Edit.getCity();
//			String countryResult4edit = myHeroe4Edit.getCountry();
//			Integer yearResult4edit = myHeroe4Edit.getYear();
//			String noteResult4edit = myHeroe4Edit.getNote();
//			Integer originalTripPosition = idDB4edit;
//			if (noteResult4edit == null)
//				noteResult4edit = getResources().getString(R.string.emptyNote);			
//			intent.putExtra("SavedDataTripCity", cityResult4edit);
//			intent.putExtra("SavedDataTripCountry", countryResult4edit);
//			intent.putExtra("SavedDataTripYear", yearResult4edit);
//			intent.putExtra("SavedDataTripNote", noteResult4edit);
//			intent.putExtra("TripId", originalTripPosition);
//			Log.i(TAG, "info del viaje para editar: " + cityResult4edit + ","
//					+ countryResult4edit + "," + yearResult4edit.toString()
//					+ "," + noteResult4edit + "," + originalTripPosition);
			startActivityForResult(intent, REQUEST_CODE_EDIT_FAVORITE);			
			return true;
		case R.id.CtxLblOpc3:
				Log.i(TAG, "Etiqueta: Opcion 3 pulsada!--BORRAR");
				Log.i(TAG, "---Nro de heroes antes de BORRAR (DB): "+ dbHelper.getFavoritesList().size());
			CharacterInfo myHeroe4Delete = dbHelper.getFavoriteInfo(String.valueOf(row));
			String idDB4delete=myHeroe4Delete.getId();
			String nameResult4delete = myHeroe4Delete.getName();		
			Uri uri = Uri.parse(CharacterFavoritesProvider.CONTENT_URI + "/" + idDB4delete);
			getContentResolver().delete(uri, null, null);
			//## VERSION 4: Cambiamos el ResourceCursorAdapter por CursorAdapter para poder actualizar el adapter.
			ContentResolver cr = getContentResolver();
			Cursor c = cr.query(CharacterFavoritesProvider.CONTENT_URI,PROJECTION, null, null, CharacterFavoritesConstants.NAME+" ASC");
	    	mAdapter.changeCursor(c);				
				Log.i(TAG, "---Numero de heroes despues de BORRAR (DB): "+ dbHelper.getFavoritesList().size());
			Toast.makeText(CharacterFavoritesListActivity.this, "Borrado heroe "+nameResult4delete+"("+idDB4delete+")", Toast.LENGTH_LONG).show();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}// onContextItemSelected()

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

}
