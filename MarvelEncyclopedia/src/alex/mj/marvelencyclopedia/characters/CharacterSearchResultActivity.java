/**
 * 
 */
package alex.mj.marvelencyclopedia.characters;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import alex.mj.marvelencyclopedia.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;



/**
 * @author alejandro.marijuan@googlemail.com
 * @param
 * 
 */
public class CharacterSearchResultActivity extends Activity {
   
	
	private static final String TAG = "***CharacterSearchResultActivity";
	private String[] mDataHeroe;
	
	// ****Navigation Drawer variables*****//
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mOptionCharacterResultTitles;
	private static String THUMBNAIL_URL_SMALL;
	private static String THUMBNAIL_URL_UNCANNY;

	// *****CHARACTER DATA*****//
	private static String CHARACTER_id = "";
	private static String CHARACTER_name = "";
	private static String CHARACTER_description = "";
	private static String CHARACTER_modified = "";
	private static String CHARACTER_thumbnail_url = "";
	private static String CHARACTER_thumbnail_extension = "";
	private static TextView mNAME;
	private static TextView mID;
	private static TextView mMODIFIED;
	private static TextView mDESCRIPTION;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_characterresult_main);
		// CAMBIAMOS EL COLOR DE LA BARRA DE TITULO
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c20d1c")));
		

		Intent intent = getIntent();
		mDataHeroe = intent.getExtras().getStringArray("CharacterValues");

		mTitle = mDrawerTitle = getTitle();
        mOptionCharacterResultTitles = getResources().getStringArray(R.array.character_menu_options);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.right_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.view_charactersearchresult_drawer_list_item, mOptionCharacterResultTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret*/
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
		setData(mDataHeroe);
        
        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    /**
     * @param mDataHeroe2 
	 * 
	 */
	private void setData(String[] mDataHeroe2) {
		Log.d(TAG, "setData()");
		for (String data:mDataHeroe2){
			Log.d(TAG,data);
		}		
		CHARACTER_id = mDataHeroe2[0];
		CHARACTER_name = mDataHeroe2[1];
		CHARACTER_description = mDataHeroe2[2];
		CHARACTER_modified = mDataHeroe2[3];
		CHARACTER_thumbnail_url = mDataHeroe2[4];
		CHARACTER_thumbnail_extension = mDataHeroe2[5];	
		THUMBNAIL_URL_UNCANNY = CHARACTER_thumbnail_url + "/portrait_uncanny."+ CHARACTER_thumbnail_extension;
		THUMBNAIL_URL_SMALL = CHARACTER_thumbnail_url + "/portrait_small."+ CHARACTER_thumbnail_extension;

	}

	/*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_marvel_encyclopedia, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    /* Called whenever we call invalidateOptionsMenu() 
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
//        switch(item.getItemId()) {
//        case R.id.action_websearch:
//            // create intent to perform web search for this option
//            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
//            // catch event that there's no activity to handle intent
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                startActivity(intent);
//            } else {
//                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//            }
//            return true;
//        default:
            return super.onOptionsItemSelected(item);
//        }
    }

    /* The click listener for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        private static final String TAG = "CharacterActivity--DrawerItemClickListener";

		@Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
            Log.d(TAG,"Hola position:"+position);            
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment = new CharacterFragment();
        Bundle args = new Bundle();
        args.putInt(CharacterFragment.ARG_OPTION_CHARACTER_RESULT, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mOptionCharacterResultTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Fragment that appears in the "content_frame", shows a option
     */
    public static class CharacterFragment extends Fragment {
        public static final String ARG_OPTION_CHARACTER_RESULT = "option_number";
		private LayoutInflater mInflater;
		private ViewGroup mContainer;
		public View rootView;

        public CharacterFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
//            View rootView = null;
            int i = getArguments().getInt(ARG_OPTION_CHARACTER_RESULT);
//            String option = getResources().getStringArray(R.array.character_menu_options)[i];
            String option = getResources().getStringArray(R.array.character_menu_options)[i];
            Log.d("OptionCharacterResultFragment","i("+i+"option("+option+")");
            //int imageId = getResources().getIdentifier(option.toLowerCase(Locale.getDefault()),"drawable", getActivity().getPackageName());
            switch (i) {
            case 0:              	
            	rootView = inflater.inflate(R.layout.view_charactersearchresult_fragment_character_picture, container, false);
            	showResult(rootView);
            	
                break;
            case 1:
            	rootView = inflater.inflate(R.layout.view_charactersearchresult_fragment_character_description, container, false);
            	mID=(TextView) rootView.findViewById(R.id.textViewID);
            	mMODIFIED=(TextView) rootView.findViewById(R.id.textViewMODIFIED);
            	mNAME = (TextView) rootView.findViewById(R.id.textViewNAME);
        		mDESCRIPTION = (TextView) rootView.findViewById(R.id.textViewDESCRIPTION);
            	mID.setText(CHARACTER_id);
        		mMODIFIED.setText(CHARACTER_modified);
        		mNAME.setText(CHARACTER_name);
        		mDESCRIPTION.setText(CHARACTER_description);
            	break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            	rootView = inflater.inflate(R.layout.view_charactersearchresult_fragment_character_picture, container, false);
				((ImageView) rootView.findViewById(R.id.image)).setImageResource(R.drawable.cover_underconstruction);
            	break;
         
            }
            getActivity().setTitle(CHARACTER_name+ "' s " + option);
            return rootView;
        }

		/**
		 * @param rootView 
		 * 
		 */
		private void showResult(View rootView) {
			// Create an object for subclass of AsyncTask
			Log.d(TAG, "**showResult()--THUMBNAIL_URL:" + THUMBNAIL_URL_UNCANNY);
			String[] url_images={THUMBNAIL_URL_UNCANNY};
			new GetXMLTask().execute(url_images);			
		}
		

		private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {

			@Override
			protected Bitmap doInBackground(String... urls) {
				Log.d(TAG, "GetXMLTask--doInBackground()");
				Bitmap map = null;
				for (String url : urls) {
					map = downloadImage(url);
					Log.d(TAG, "url: " + url);
				}
				return map;
			}

			// Sets the Bitmap returned by doInBackground
			@Override
			protected void onPostExecute(Bitmap result) {
				Log.d(TAG, "GetXMLTask--onPostExecute()");				
				((ImageView) rootView.findViewById(R.id.image)).setImageBitmap(result);
			}

			// Creates Bitmap from InputStream and returns it
			private Bitmap downloadImage(String url) {
				Log.d(TAG, "GetXMLTask--downloadImage()");
				Bitmap bitmap = null;
				InputStream stream = null;
				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				bmOptions.inSampleSize = 1;

				try {
					stream = getHttpConnection(url);
					bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
					stream.close();
				} catch (IOException e1) {
					Log.e(TAG, "Error al descargar la imagen: " + url + "--"
							+ e1);
					e1.printStackTrace();
				}
				return bitmap;
			}

			// Makes HttpURLConnection and returns InputStream
			private InputStream getHttpConnection(String urlString)
					throws IOException {
				Log.d(TAG, "GetXMLTask--getHttpConnection()");
				InputStream stream = null;
				Log.d(TAG, "urlString: " + urlString);
				URL url = new URL(urlString);
				URLConnection connection = url.openConnection();

				try {
					HttpURLConnection httpConnection = (HttpURLConnection) connection;
					httpConnection.setRequestMethod("GET");
					httpConnection.connect();

					if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						stream = httpConnection.getInputStream();
					}
				} catch (Exception ex) {
					Log.e(TAG, "Error al conectar con la imagen: "
							+ urlString + "--" + ex);
					ex.printStackTrace();
				}
				return stream;
			}
		}

    }
}
