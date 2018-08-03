package in.kriscent.demostore;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.kriscent.demostore.Adapters.ExpandableListAdapter;
import in.kriscent.demostore.DB.DatabaseHelper;
import in.kriscent.demostore.Model.CategoryData;
import in.kriscent.demostore.Model.ExpandedMenuModel;
import in.kriscent.demostore.util.BaseActivity;
import in.kriscent.demostore.util.Constants;
import in.kriscent.demostore.util.PrefernceSettings;

public class Navigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG =Navigation.class.getSimpleName();
    String[] menunameNew= {"in.kriscent.demostore.demostore.Navigation","in.kriscent.demostore.demostore.About"};
    Context context;
    ExpandableListAdapter mMenuAdapter;
    List<CategoryData> listDataHeader=new ArrayList<>();
    NavigationView navigationView;
    public static RelativeLayout _lincart;
    public static TextView _tvCart;
    DatabaseHelper db;
    TextView _appname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context=this;
        db=new DatabaseHelper(context);
        //  expandableList = findViewById(R.id.navigationmenu);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        _tvCart=findViewById(R.id.tv_cartitm);
        _appname=findViewById(R.id.tv_appname);
        _lincart = findViewById(R.id.lin_cart);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getBackground().setAlpha(122);

        if(BaseActivity.isNetworkConnected(context))
            GetCatData();

       _appname.setText("Home");
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, new HomeFragment()).commit();


        _lincart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BaseActivity.isNetworkConnected(context)) {
                    Intent i = new Intent(context, MyCart.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }else{
                    Toast toast = Toast.makeText(context, "No internet connection.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP, 0, 10);
                    toast.show();
                }
            }
        });

        Cursor cursor = db.getAllCartData();
        Log.e("tblcunt",""+cursor.getCount());
        if(cursor.getCount()>0)
            _tvCart.setText(String.valueOf(cursor.getCount()));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_cat){
            for (int k=0;k<listDataHeader.size();k++){
                Menu m=navigationView.getMenu();
                if(k==0) {
                    boolean b = !m.findItem(R.id.nav_itm1).isVisible();
                    m.findItem(R.id.nav_itm1).setVisible(b).setTitle(listDataHeader.get(k).getC_name());
                }else  if(k==1) {
                    boolean b = !m.findItem(R.id.nav_itm2).isVisible();
                    m.findItem(R.id.nav_itm2).setVisible(b).setTitle(listDataHeader.get(k).getC_name());
                }else  if(k==2) {
                    boolean b = !m.findItem(R.id.nav_itm3).isVisible();
                    m.findItem(R.id.nav_itm3).setVisible(b).setTitle(listDataHeader.get(k).getC_name());
                }else  if(k==3) {
                    boolean b = !m.findItem(R.id.nav_itm4).isVisible();
                    m.findItem(R.id.nav_itm4).setVisible(b).setTitle(listDataHeader.get(k).getC_name());
                }else  if(k==4) {
                    boolean b = !m.findItem(R.id.nav_itm5).isVisible();
                    m.findItem(R.id.nav_itm5).setVisible(b).setTitle(listDataHeader.get(k).getC_name());
                }
            }
            return true;
        }else if (id == R.id.nav_Home) {
            _appname.setText("Home");
            Intent mainIntent = new Intent(context, Navigation.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        }else if (id == R.id.nav_order) {
           DialogBox();
        }else if (id == R.id.nav_about) {
            _appname.setText("About Us");
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new About()).commit();
        }else if (id == R.id.nav_contact) {
            _appname.setText("Contact Us");
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new ContactUs()).commit();
        }else if (id == R.id.nav_tc) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new TCFG()).commit();
        }else if (id == R.id.nav_faq) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new FAQ()).commit();
        }else if (id == R.id.nav_settings) {
            _appname.setText("Settings");
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new Settings()).commit();
        }
        else if(id== R.id.nav_itm1){
            Log.e("svvll",""+listDataHeader.get(0).getC_id());
           // RemoveBackst();
            Bundle bundle = new Bundle();
            bundle.putString("catid", listDataHeader.get(0).getC_id());
            ProductList pdd = new ProductList();
            pdd.setArguments(bundle);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, pdd).addToBackStack(null).commit();

        }else if(id== R.id.nav_itm2){
          //  RemoveBackst();
            Log.e("svvll",""+listDataHeader.get(1).getC_id());
            Bundle bundle = new Bundle();
            bundle.putString("catid", listDataHeader.get(1).getC_id());
            ProductList pdd = new ProductList();
            pdd.setArguments(bundle);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, pdd).addToBackStack(null).commit();
        }else if(id== R.id.nav_itm3){
           // RemoveBackst();
            Log.e("svvll",""+listDataHeader.get(2).getC_id());
            Bundle bundle = new Bundle();
            bundle.putString("catid", listDataHeader.get(2).getC_id());
            ProductList pdd = new ProductList();
            pdd.setArguments(bundle);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, pdd).addToBackStack(null).commit();
        }else if(id== R.id.nav_itm4){
           // RemoveBackst();
            Log.e("svvll",""+listDataHeader.get(3).getC_id());
            Bundle bundle = new Bundle();
            bundle.putString("catid", listDataHeader.get(3).getC_id());
            ProductList pdd = new ProductList();
            pdd.setArguments(bundle);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, pdd).addToBackStack(null).commit();
        }else if(id== R.id.nav_itm5){
           // RemoveBackst();
            Log.e("svvll",""+listDataHeader.get(4).getC_id());
            Bundle bundle = new Bundle();
            bundle.putString("catid", listDataHeader.get(4).getC_id());
            ProductList pdd = new ProductList();
            pdd.setArguments(bundle);

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, pdd).addToBackStack(null).commit();
        }
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void GetCatData() {
        String url = Constants.GetCategory;
        Log.v(TAG, " Catparam " + url);
        JSONObject parameters = new JSONObject();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v(TAG, " Card data response " + response.toString());
                JSONObject jjob = null,jobsub;
                JSONArray jarr;
                listDataHeader=new ArrayList<>();

                try {
                    if(response.getString("success").equals("true")){
                        jarr = response.getJSONArray("data");
                        for(int i = 0; i < jarr.length(); i++) {

                            jjob = jarr.getJSONObject(i);
                            CategoryData item1 = new CategoryData();
                            item1.setC_id(jjob.getString("id"));
                            item1.setC_name(jjob.getString("cat_name"));
                            listDataHeader.add(item1);
                            if(i==0)
                                Constants.Default_Catid=jjob.getString("id");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // BaseActivity.unloader(getActivity());
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("erroree", "Error: " + error.getMessage());
                Toast.makeText(context, "Network Issue", Toast.LENGTH_SHORT).show();
                // hide the progress dialog
                //  BaseActivity.unloader(getActivity());
            }
        });
        Volley.newRequestQueue(context).add(jsonObjReq);
    }

    public void DialogBox(){
        final Dialog dialog;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            dialog = new Dialog(context,R.style.Theme_Dialog);
        } else {
            dialog = new Dialog(context);
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
       // dialog.setCancelable(false);

        // set the custom dialog components - text, image and button
        final TextView _edmob=(EditText)dialog.findViewById(R.id.ed_mob);
        Button _btapply =  dialog.findViewById(R.id.btn_submit);

        _btapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobn=_edmob.getText().toString().trim();
                if (!mobn.equals("") && mobn.length()==10) {
                    _edmob.setError(null);
                    dialog.dismiss();
                    Bundle bundle = new Bundle();
                    bundle.putString("mobno", ""+mobn);
                    _appname.setText("Orders");
                    OrdersFG odd = new OrdersFG();
                    odd.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, odd).commit();
                }else{
                    _edmob.setError("Fill valid mobile number");
                    _edmob.requestFocus();
                }
            }
        });


        dialog.show();
    }

    public void RemoveBackst(){
        FragmentManager fragmentManager = getFragmentManager();
        while(fragmentManager.getBackStackEntryCount()>0){
            fragmentManager.popBackStackImmediate();
        }
    }

}
