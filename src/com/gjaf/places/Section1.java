package com.gjaf.places;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.gjaf.places.R;

public class Section1 extends Fragment {
	
	private Button btn1 = null;
	private TextView lbl1 = null;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    inflater.inflate(R.menu.section1, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

	    switch (item.getItemId()) {
	        case R.id.menuSection1Add:                
	              Toast.makeText(getActivity().getApplicationContext(), "Add", Toast.LENGTH_SHORT).show();
	            return true;
	    }
	    return super.onOptionsItemSelected(item);
	} 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.activity_section1, container,
				false);

		return v;
	}

	protected void trocaTexto() {
		lbl1.setText("Butão clicado!!!");		
	}

	

}
