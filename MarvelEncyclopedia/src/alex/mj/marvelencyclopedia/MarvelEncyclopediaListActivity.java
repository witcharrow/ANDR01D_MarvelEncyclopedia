package alex.mj.marvelencyclopedia;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class MarvelEncyclopediaListActivity extends Activity {
	
	private static final String TAG = "MarvelEncyclopediaListActivity: --->";
//	private EncyclopediaAdapter adapter;
	private ListView listView1;
	
	/**
	 * @author Alejandro.Marijuan
	 * 
	 */
	public class OptionAdapter extends ArrayAdapter<Option>{

	    Context context; 
	    int layoutResourceId;    
	    Option data[] = null;
	    
	    public OptionAdapter(Context context, int layoutResourceId, Option[] data) {
	        super(context, layoutResourceId, data);
	        this.layoutResourceId = layoutResourceId;
	        this.context = context;
	        this.data = data;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        OptionHolder holder = null;
	        
	        if(row == null){
	        	LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            row = inflater.inflate(layoutResourceId, parent, false);
	            
	            holder = new OptionHolder();
	            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
	            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
	            
	            row.setTag(holder);
	        }
	        else{
	            holder = (OptionHolder)row.getTag();
	        }
	        
	        Option Option = data[position];
	        holder.txtTitle.setText(Option.title);
	        holder.imgIcon.setImageResource(Option.icon);
	        
	        return row;
	    }
	    
	    class OptionHolder {
	        ImageView imgIcon;
	        TextView txtTitle;
	    }
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG, "onCreate()") ;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_main);
        
        Option Option_data[] = new Option[]{
            new Option(R.drawable.spidey, (String) getResources().getText(R.string.charactersOption)),
            new Option(R.drawable.xmen, (String) getResources().getText(R.string.comicsOption)),
            new Option(R.drawable.capi_shield, (String) getResources().getText(R.string.creatorsOption)),
            new Option(R.drawable.shield, (String) getResources().getText(R.string.eventsOption)),
            new Option(R.drawable.f4, (String) getResources().getText(R.string.seriesOption)),
            new Option(R.drawable.avengers, (String) getResources().getText(R.string.storiesOption))
        };
        
        OptionAdapter adapter = new OptionAdapter(this, 
                R.layout.listview_item_row_marvel_encyclopedia, Option_data);
        
        
        listView1 = (ListView)findViewById(R.id.listView1);
         
        View header = (View)getLayoutInflater().inflate(R.layout.listview_header_row_marvel_encyclopedia, null);
        listView1.addHeaderView(header);
        
        listView1.setAdapter(adapter);
    }


}
