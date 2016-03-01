package chau.voipapp;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint({ "SimpleDateFormat", "InflateParams" })
public class HistoryActivity extends Fragment
{
	HistoryInfo contact;
	public static ArrayList<HistoryInfo> results;
	public static ListviewHistoryAdapter adapter;
	String name;
	int lvClickPos;
	
	FileInputStream fis;
	ObjectInputStream ois;
	
	EditText edSearchHistory;
	
	View rootView;
	
	ListView lvHis;	
	AdapterView.OnItemClickListener listenerlvHis;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{	
		setHasOptionsMenu(true);
//		getActivity().supportInvalidateOptionsMenu();
		rootView = inflater.inflate(R.layout.activity_history, container, false);
		
		initWiget();
		initListener();
		InputMethodManager ipm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		ipm.hideSoftInputFromWindow(edSearchHistory.getWindowToken(), 0);
		
		registerForContextMenu(lvHis);
		results = new ArrayList<HistoryInfo>();
//		results = GetlistContact();
//		results = getHistoryfromDevice();
		adapter = new ListviewHistoryAdapter(getActivity(), results);
		edSearchHistory.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
//				if(edSearchHistory.getText() == null || edSearchHistory.getText().length() == 0)
//				{
//					adapter.notifyDataSetChanged();
//				}
//				else
					adapter.getFilter().filter(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		lvHis.setAdapter(adapter);
		lvHis.setOnItemClickListener(listenerlvHis);
		
		getHistoryContact();
		
		return rootView;
	}	
	
	@Override
	public void onResume() 
	{
		edSearchHistory.setText(null);
		super.onResume();
	}

	private ArrayList<HistoryInfo> GetlistContact(){
	    ArrayList<HistoryInfo> contactlist = new ArrayList<HistoryInfo>();

	    contact = new HistoryInfo("0123", "Chau", "21:22", "30",true,false);
	    contactlist.add(contact);
	    contact = new HistoryInfo("4567", "Sven", "21:22", "30",true,false);
	    contactlist.add(contact);
	    contact = new HistoryInfo("8901", "Com", "21:22", "30",true,false);
	    contactlist.add(contact);

	    return contactlist; 	
	}
	
	public void getHistoryContact()
	{
		try
		{
			fis = getContext().openFileInput(SipInit.FILENAME);
			ois = new ObjectInputStream(fis);
			HistoryInfo info;
			while((info = (HistoryInfo)ois.readObject()) != null)
			{
				results.add(info);
			}
		} catch(Exception e)
		{
			try
			{
				ois.close();
			} catch(Exception ex)
			{}
		}
		Collections.reverse(results);
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
        
		inflater.inflate(R.menu.history_bottom_bar, menu);
		
        super.onCreateOptionsMenu(menu,inflater);
        
    }
	
	//-----Tạo Menu cho itemClick listview
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.history_click_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		switch(item.getItemId())
		{
		case R.id.itemHistorycall:
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	};
	
	//----------------------------------------
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int id = item.getItemId();
		if(id == R.id.bottomHistorySearch)
		{
			if(edSearchHistory.isShown())
				edSearchHistory.setVisibility(View.GONE);
			else edSearchHistory.setVisibility(View.VISIBLE);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) 
	{
		
		super.onPrepareOptionsMenu(menu);
//		getActivity().invalidateOptionsMenu();
//		MenuItem filter = menu.findItem(R.id.bottomSearch).setVisible(false);
//		filter = menu.findItem(R.id.bottomStatus).setVisible(false);
//		filter.setVisible(false);
		
	}
	
	private ArrayList<HistoryInfo> getHistoryfromDevice()
	{
		ArrayList<HistoryInfo> contactlist = new ArrayList<HistoryInfo>();
		Cursor mCursor = getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, 
				null, null, null, null);
		
		int name = mCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
		int num = mCursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = mCursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = mCursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = mCursor.getColumnIndex(CallLog.Calls.DURATION);
		boolean outgoingCall = false;
		boolean missedCall = false;
		
		mCursor.moveToLast();
		SimpleDateFormat sdf = new SimpleDateFormat("H:mmaa   EEEE, dd MM, yyyy");
		if(mCursor.getCount() > 0)
		{
			while(mCursor.moveToPrevious())
			{
				String phoneName = mCursor.getString(name);
				String phoneNum = mCursor.getString(num).replace(" ", "");
				String callType = mCursor.getString(type);
				String callDate = mCursor.getString(date);
				Date callDateTime = new Date(Long.valueOf(callDate));
				String dura = mCursor.getString(duration);
	//			String dir = null;
				int dirCode = Integer.parseInt(callType);
				switch(dirCode)
				{
				case CallLog.Calls.OUTGOING_TYPE:
					outgoingCall = true;
					break;
				case CallLog.Calls.INCOMING_TYPE:
					break;
				case CallLog.Calls.MISSED_TYPE:
					missedCall = true;
					break;
				}
				contactlist.add(new HistoryInfo(phoneNum, 
						phoneName, sdf.format(callDateTime), 
						convertTime(dura), outgoingCall, missedCall));
				outgoingCall = false;
				missedCall = false;
			}
		}
		return contactlist;
	}
	
	private String convertTime(String dura)
	{
		String time = null;
		int secs = Integer.valueOf(dura)%60;
		int mins = (Integer.valueOf(dura) / (60))%60;
		int hrs = (Integer.valueOf(dura) / (60*60));
		StringBuilder builder = new StringBuilder(64);
		if(hrs > 0)
		{
		    builder.append(hrs);
		    if(hrs > 1)
		        builder.append(" hrs ");
		    else builder.append("hr");                              
		}
		builder.append(mins);
		if(mins > 1) builder.append(" mins ");
		else builder.append(" min ");
		builder.append(secs);
		if(secs > 1) builder.append(" secs ");
		else builder.append(" sec ");
		time = builder.toString();
		return time;
	}
	
	/**
	 * Khởi tạo các thành phần
	 */
	public void initWiget()
	{
		lvHis = (ListView)rootView.findViewById(R.id.lvHistory);
		edSearchHistory = (EditText)rootView.findViewById(R.id.edSearchHistory);
	}
	
	/**
	 * Tạo sự kiện click
	 */
	public void initListener()
	{
		listenerlvHis = new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, 
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				lvClickPos = arg2;
				getActivity().openContextMenu(arg1);
			}
		};
	}
	public class ListviewHistoryAdapter extends BaseAdapter implements Filterable
	{
		private ArrayList<HistoryInfo> listOriginContact = null;
		private ArrayList<HistoryInfo> listDisplayValue = null;
		private LayoutInflater mInflater;

		public ListviewHistoryAdapter(Context photosFragment, ArrayList<HistoryInfo> results)
		{
		    listOriginContact = results;
		    listDisplayValue = results;
		    mInflater = LayoutInflater.from(photosFragment);
		}

		@Override
		public int getCount() 
		{
		    // TODO Auto-generated method stub
		    return listDisplayValue.size();
		}

		@Override
		public Object getItem(int arg0)
		{
		    // TODO Auto-generated method stub
		    return listDisplayValue.get(arg0);
		}

		@Override
		public long getItemId(int arg0) 
		{
		    // TODO Auto-generated method stub
		    return arg0;
		}

		public View getView(int position, View convertView, ViewGroup parent) 
		{
		    // TODO Auto-generated method stub
		    ViewHolder holder;
		    if(convertView == null){
		        convertView = mInflater.inflate(R.layout.activity_history_customview, null);
		        holder = new ViewHolder();
		        holder.tvNum = (TextView) convertView.findViewById(R.id.sipaddr);   
		        convertView.setTag(holder);
		    } else {
		        holder = (ViewHolder) convertView.getTag();
		    }

		    holder.tvNum.setText(listDisplayValue.get(position).getSipAddr());       
		    
		    holder.tvName = (TextView)convertView.findViewById(R.id.sipName);
		    holder.tvName.setText("[" +listDisplayValue.get(position).getName() + "]");
		    
		    holder.calldate = (TextView)convertView.findViewById(R.id.calldate);
		    holder.calldate.setText(listDisplayValue.get(position).getCallDate());
		    
		    holder.callduration = (TextView)convertView.findViewById(R.id.callduration);
		    holder.callduration.setText(listDisplayValue.get(position).getCallDuration());
		    
	        holder.imgCallStatus = (ImageView)convertView.findViewById(R.id.icon);
	        if(listDisplayValue.get(position).isOutgoingCall())
			{
				holder.imgCallStatus.setImageResource(R.drawable.out_call);
			}
			else if(listDisplayValue.get(position).isMissedCall())
				{
					holder.imgCallStatus.setImageResource(R.drawable.miss_call);
				}
			else holder.imgCallStatus.setImageResource(R.drawable.in_call);
		    
		    return convertView;
		}

		class ViewHolder
		{
		    TextView tvNum, calldate, callduration, tvName;
		    ImageView imgCallStatus;
		}

		@Override
		public Filter getFilter() {
			// TODO Auto-generated method stub
			Filter filter = new Filter() {
				
				@SuppressWarnings("unchecked")
				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					// TODO Auto-generated method stub
					listDisplayValue = (ArrayList<HistoryInfo>)results.values;
					notifyDataSetChanged();
				}
				
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					// TODO Auto-generated method stub
					FilterResults filterResult = new FilterResults();
					ArrayList<HistoryInfo>filterList = new ArrayList<HistoryInfo>();
					if(listOriginContact == null)
					{
						listOriginContact = new ArrayList<HistoryInfo>(listDisplayValue);
					}
					if(constraint == null || constraint.length() == 0)
					{
						filterResult.count = listOriginContact.size();
						filterResult.values = listOriginContact;
					}
					else
					{
						constraint = constraint.toString().toLowerCase();
						for(int i = 0; i < listOriginContact.size(); i++)
						{
							String data = listOriginContact.get(i).getName();
							String num = listOriginContact.get(i).getSipAddr();
							if(data.toLowerCase().contains(constraint.toString()) 
									|| num.contains(constraint.toString()))
							{
								filterList.add(new HistoryInfo(
										listOriginContact.get(i).getSipAddr(),
										listOriginContact.get(i).getName(),
										listOriginContact.get(i).getCallDate(),
										listOriginContact.get(i).getCallDuration(),
										listOriginContact.get(i).isOutgoingCall(),
										listOriginContact.get(i).isMissedCall()));
							}
						}
						filterResult.count = filterList.size();
						filterResult.values = filterList;
					}
					return filterResult;
				}
			};
			return filter;
		}
	}	
}
