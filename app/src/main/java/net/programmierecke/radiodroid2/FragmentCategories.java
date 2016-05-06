package net.programmierecke.radiodroid2;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.programmierecke.radiodroid2.adapters.ItemAdapterCategory;
import net.programmierecke.radiodroid2.data.DataCategory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FragmentCategories extends FragmentBase {
    private ListView lv;
    private DataCategory[] data = new DataCategory[0];
    private String baseSearchAdress = "";
    private SwipeRefreshLayout mySwipeRefreshLayout;

    public FragmentCategories() {
    }

    public void SetBaseSearchLink(String url){
        this.baseSearchAdress = url;
    }

    void ClickOnItem(DataCategory theData) {
        ActivityMain m = (ActivityMain)getActivity();

        try {
            String queryEncoded = URLEncoder.encode(theData.Name, "utf-8");
            m.Search(baseSearchAdress+"/"+queryEncoded);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void RefreshListGui(){
        if (lv != null) {
            data = DataCategory.DecodeJson(getUrlResult());
            ItemAdapterCategory adapterCategory = (ItemAdapterCategory) lv.getAdapter();
            adapterCategory.clear();
            for (DataCategory aData : data) {
                adapterCategory.add(aData);
            }

            lv.invalidate();
            if (mySwipeRefreshLayout != null) {
                mySwipeRefreshLayout.setRefreshing(false);
            }
        }else{
            Log.e("ABC", "LV == NULL FragmentCategories");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ItemAdapterCategory adapterCategory = new ItemAdapterCategory(getActivity(), R.layout.list_item_category);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stations_remote, container, false);

        lv = (ListView) view.findViewById(R.id.listViewStations);
        lv.setAdapter(adapterCategory);
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object anObject = parent.getItemAtPosition(position);
                if (anObject instanceof DataCategory) {
                    ClickOnItem((DataCategory) anObject);
                }
            }
        });

        mySwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        if (mySwipeRefreshLayout != null) {
            mySwipeRefreshLayout.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            Log.i("ABC", "onRefresh called from SwipeRefreshLayout");
                            //RefreshListGui();
                            DownloadUrl(true);
                        }
                    }
            );
        }

        RefreshListGui();

        return view;
    }
}