package in.kriscent.demostore;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.viewpagerindicator.CirclePageIndicator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import in.kriscent.demostore.Adapters.HomeSliderAdapter;
import in.kriscent.demostore.Adapters.ProductAdapter;
import in.kriscent.demostore.Adapters.SlideAdapterDefault;
import in.kriscent.demostore.Model.BannerData;
import in.kriscent.demostore.Model.ProductData;
import in.kriscent.demostore.Model.ProductType;
import in.kriscent.demostore.util.BaseActivity;
import in.kriscent.demostore.util.Constants;
import in.kriscent.demostore.util.PrefernceSettings;


public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    Context context;
    View rootview;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private static final Integer[] IMAGES1 = {R.drawable.ic_home};
    private ArrayList<String> ImagesArray;
    private ArrayList<Integer> DefaultImgArray ;
    RecyclerView rvhm;
    ArrayList<ProductType> ptype;
    String Default_Catid="";
    RelativeLayout rll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        PrefernceSettings.openDataBase(context);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        int dens=dm.densityDpi;
        Log.e("wihi",""+width+" "+height);
        Log.e("dens",""+dens);
        PrefernceSettings.setWidth(String.valueOf(width));
        PrefernceSettings.setHeight(String.valueOf(height));

      //  swipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.swipeRefreshLayout);
        rvhm =  rootview.findViewById(R.id.rv);
        rll=rootview.findViewById(R.id.hmrl);
        int hh = Integer.parseInt(PrefernceSettings.getHeight())/3;
        // rll.setMinimumHeight(hh);

        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,hh);
        RelativeLayout.LayoutParams layout_description = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, hh);
        rll.setLayoutParams(lp);
        if(BaseActivity.isNetworkConnected(context)) {
            GetCatData();
            GetBanners();
        }
        else
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();


       /* swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    if(BaseActivity.isNetworkConnected(getContext())) {
                        new GetData().execute();
                    }
            }
        });*/
        /*grid.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    return true;
                }
                return false;
            }
        });*/

        return rootview;
    }

    private void slider(ArrayList<BannerData> IMAGES) {
        ImagesArray = new ArrayList<String>();
        DefaultImgArray = new ArrayList<>();
        ArrayList<String> imgpid = new ArrayList<>();
        Log.e("imgslidrr",""+IMAGES.size());
        if(IMAGES.size()>0) {
            for (int i = 0; i < IMAGES.size(); i++) {
                ImagesArray.add(String.valueOf(IMAGES.get(i).getBanner_img()));
                imgpid.add(IMAGES.get(i).getBanner_P_id());
            }
            mPager =  rootview.findViewById(R.id.pager);
            mPager.setAdapter(new HomeSliderAdapter(getActivity(), ImagesArray, imgpid));

        }else{
            DefaultImgArray.add(IMAGES1[0]);
            mPager =  rootview.findViewById(R.id.pager);

            mPager.setAdapter(new SlideAdapterDefault(getActivity(), DefaultImgArray));
        }

        CirclePageIndicator indicator =  rootview.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;

        //Set circle indicator radius
        indicator.setRadius(5 * density);
        NUM_PAGES = IMAGES.size();

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 10000, 10000);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });
    }

    private void GetProducts() {
        String url;
      BaseActivity.loader(context);
            url = Constants.GetProduct;

        Map<String, String> params = new HashMap();
        params.put("catid", Default_Catid);
        JSONObject parameters = new JSONObject(params);
        Log.d("parampdd",""+url+"   "+parameters);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG,"resproduct" + response.toString());
                ArrayList<ProductData> parr=new ArrayList<>();
                ptype=new ArrayList<>();
                try {
                    if(response.getString("success").equals("true")){
                      JSONArray jarr = response.getJSONArray("data");
                      for(int i=0;i<jarr.length();i++){
                          JSONObject job = jarr.getJSONObject(i);
                          ProductData pd = new ProductData();
                          pd.setPid(job.getString("pro_id"));
                          pd.setPname(job.getString("pro_name"));
                          pd.setPimg(Constants.IMG_URL+job.getString("pro_img"));
                          ArrayList<String> weight = new ArrayList<>();
                          ArrayList<String> price = new ArrayList<>();
                          ArrayList<String> mrp = new ArrayList<>();
                          ArrayList<String> vid = new ArrayList<>();
                          JSONArray typearr=job.getJSONArray("data");
                          for(int k=0;k<typearr.length();k++){
                              JSONObject jobj=typearr.getJSONObject(k);
                              ProductType pt=new ProductType();
                              weight.add(jobj.getString("weight"));
                              price.add(jobj.getString("price"));
                              mrp.add(jobj.getString("mrp"));
                              vid.add(jobj.getString("vid"));
                              //pd.setItems(ptype);
                              //parr11.add(pd);
                          }
                          pd.setPrice(price);
                          pd.setMrp(mrp);
                          pd.setWeight(weight);
                          pd.setPvid(vid);
                          parr.add(pd);
                      }

                        ProductAdapter padap=new ProductAdapter(parr, "0");
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);
                        rvhm.setLayoutManager(mLayoutManager);
                        rvhm.setAdapter(padap);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                BaseActivity.unloader(context);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("erroree", "Error: " + error.getMessage());
               BaseActivity.unloader(context);
            }
        });
        Volley.newRequestQueue(getActivity()).add(jsonObjReq);
    }

    private void GetBanners() {
        final ArrayList<BannerData> bannerimg= new ArrayList<>();
        String url = Constants.BannerUrl;
        JSONObject parameters = new JSONObject();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, parameters, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e("resbanner", response.toString());
                JSONObject jObject,job ;
                JSONArray jarr;
                try {

                    if(response.getString("success").equals("true")) {
                        try {
                            jarr = response.getJSONArray("data");
                            for (int i = 0; i < jarr.length(); i++) {
                                BannerData bd = new BannerData();
                                job = jarr.getJSONObject(i);
                                if(!job.getString("banner_image").equals("")) {
                                   // bd.setBanner_P_id(job.getString("pro_id"));
                                    bd.setBanner_img(Constants.Banner_URL + job.getString("banner_image"));
                                    bannerimg.add(bd);
                                }
                            }
                            slider(bannerimg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(getActivity() != null)
                        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("erroree", "Error: " + error.getMessage());
                if(getActivity() != null)
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(getActivity()).add(jsonObjReq);
    }

    private void GetCatData() {
        String url = Constants.GetCategory;
        JSONObject parameters = new JSONObject();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v(TAG, " Card data response " + response.toString());
                JSONObject jjob = null,jobsub;
                JSONArray jarr;

                try {
                    if(response.getString("success").equals("true")){
                        jarr = response.getJSONArray("data");
                        for(int i = 0; i < jarr.length(); i++) {
                            jjob = jarr.getJSONObject(i);
                            if(i==0)
                                Default_Catid=jjob.getString("id");
                        }
                        GetProducts();
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
}
