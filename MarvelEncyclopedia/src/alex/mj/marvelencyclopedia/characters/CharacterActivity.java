/**
 * 
 */
package alex.mj.marvelencyclopedia.characters;

import alex.mj.marvelencyclopedia.Option;
import alex.mj.marvelencyclopedia.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
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

/**
 * @author alejandro.marijuan@googlemail.com
 * 
 */
public class CharacterActivity extends Activity {

	private static final String TAG = "CharacterActivity: --->";
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
		setContentView(R.layout.listview_character_main);
		// CAMBIAMOS EL COLOR DE LA BARRA DE TITULO
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#c20d1c")));
		// RELLENAMOS LAS OPCIONES DEL MENÚ
		Option Option_data[] = new Option[] {
				new Option(R.drawable.icon_spidey, (String) getResources().getText(
						R.string.character_searcher), R.drawable.icon_spidey),
				new Option(R.drawable.icon_spidey, (String) getResources().getText(
						R.string.character_favorites), R.drawable.icon_spidey) };
		OptionAdapter adapter = new OptionAdapter(this,
				R.layout.listview_common_item_row, Option_data);
		listView1 = (ListView) findViewById(R.id.listView1_CHARACTER);
		View header = (View) getLayoutInflater().inflate(
				R.layout.listview_character_header_row, null);
		listView1.addHeaderView(header);
		listView1.setAdapter(adapter);

		listView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				

				Toast.makeText(getBaseContext(), "id:"+id, Toast.LENGTH_LONG)
						.show();
				// Creamos el Intent para lanzar la Activity CharacterActivity
//				Intent intent = null;
//				switch(position){
//				case 1:
//					intent = new Intent(getBaseContext(),CharacterActivity.class);  
//					break;
//				case 2:
//					intent = new Intent(getBaseContext(),UnderConstructionActivity.class);  
//					break;				
//				}
				
				// ##Lanzamos la Activity con el Intent creado a TravelActivity
//				startActivity(intent);//				
			}

		});
	}

}
