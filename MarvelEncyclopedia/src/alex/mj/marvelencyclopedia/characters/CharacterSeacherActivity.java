/**
 * 
 */
package alex.mj.marvelencyclopedia.characters;

import alex.mj.marvelencyclopedia.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * @author alejandro.marijuan@googlemail.com
 *
 */
public class CharacterSeacherActivity extends Activity{
	
	private static final String TAG = "CharacterSeacherActivity: --->";
	private AutoCompleteTextView urlText;
	private ImageButton button_searchCharacter;
	
	
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_marvel_encyclopedia, menu);
		return true;
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
		String stringUrl = escapeUrlText(urlText.getText().toString());
		Log.d(TAG, "---stringUrl is: " + stringUrl);
		if (stringUrl != "") {
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {
				// fetch data
				Log.d(TAG,"stringUrl="+stringUrl);
				Intent launchMySearch = new Intent(getBaseContext(),CharacterSearchResultActivity.class);;				
				launchMySearch.putExtra("nameToSearch", stringUrl);				
				// ##Lanzamos la Activity con el Intent creado.
				startActivity(launchMySearch);
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
	
}
