package alex.mj.marvelencyclopedia;

import alex.mj.marvelencyclopedia.characters.CharacterActivity;
import alex.mj.marvelencyclopedia.comics.UnderConstructionActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MarvelEncyclopediaListActivity extends Activity {

	private static final String TAG = "MarvelEncyclopediaListActivity: --->";
	private ListView listView1;

	/**
	 * @author Alejandro.Marijuan
	 * 
	 */
	public class OptionAdapter extends ArrayAdapter<Option> {

		Context context;
		int layoutResourceId;
		Option data[] = null;

		public OptionAdapter(Context context, int layoutResourceId,
				Option[] data) {
			super(context, layoutResourceId, data);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.data = data;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			OptionHolder holder = null;

			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(layoutResourceId, parent, false);

				holder = new OptionHolder();
				holder.imgIcon = (ImageView) row.findViewById(R.id.imgIcon);
				holder.txtTitle = (TextView) row.findViewById(R.id.txtTitle);
				holder.imgIconBis = (ImageView) row
						.findViewById(R.id.imgIconBis);

				row.setTag(holder);
			} else {
				holder = (OptionHolder) row.getTag();
			}

			Option Option = data[position];
			holder.imgIcon.setImageResource(Option.icon);
			holder.txtTitle.setText(Option.title);
			holder.imgIconBis.setImageResource(Option.iconBis);

			return row;
		}

		class OptionHolder {
			ImageView imgIcon;
			TextView txtTitle;
			ImageView imgIconBis;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_main_marvel_encyclopedia);
		// CAMBIAMOS EL COLOR DE LA BARRA DE TITULO
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c20d1c")));
		// RELLENAMOS LAS OPCIONES DEL MENÚ
		Option Option_data[] = new Option[] {
				new Option(R.drawable.spidey, (String) getResources().getText(
						R.string.charactersOption), R.drawable.spidey),
				new Option(R.drawable.xmen, (String) getResources().getText(
						R.string.comicsOption), R.drawable.xmen),
				new Option(R.drawable.capi_shield, (String) getResources()
						.getText(R.string.creatorsOption),
						R.drawable.capi_shield),
				new Option(R.drawable.shield, (String) getResources().getText(
						R.string.eventsOption), R.drawable.shield),
				new Option(R.drawable.f4, (String) getResources().getText(
						R.string.seriesOption), R.drawable.f4),
				new Option(R.drawable.avengers, (String) getResources()
						.getText(R.string.storiesOption), R.drawable.avengers) };
		OptionAdapter adapter = new OptionAdapter(this,
				R.layout.listview_item_row_marvel_encyclopedia, Option_data);
		listView1 = (ListView) findViewById(R.id.listView1);
		View header = (View) getLayoutInflater().inflate(
				R.layout.listview_header_row_marvel_encyclopedia, null);
		listView1.addHeaderView(header);
		listView1.setAdapter(adapter);

		listView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				

				Toast.makeText(getBaseContext(), "id:"+id, Toast.LENGTH_LONG)
						.show();
				// Creamos el Intent para lanzar la Activity CharacterActivity
				Intent intent = null;
				switch(position){
				case 1:
					intent = new Intent(getBaseContext(),CharacterActivity.class);  
					break;
				case 2:
					intent = new Intent(getBaseContext(),UnderConstructionActivity.class);  
					break;
				case 3:
					intent = new Intent(getBaseContext(),UnderConstructionActivity.class);  
					break;
				case 4:
					intent = new Intent(getBaseContext(),UnderConstructionActivity.class);  
					break;
				case 5:
					intent = new Intent(getBaseContext(),UnderConstructionActivity.class);  
					break;
				case 6:
					intent = new Intent(getBaseContext(),UnderConstructionActivity.class);  
					break;
				}
				
				// ##Lanzamos la Activity con el Intent creado a TravelActivity
				startActivity(intent);//				
			}

		});
	}

}
