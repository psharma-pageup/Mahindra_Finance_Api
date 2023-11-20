package com.app.mahindrafinancemfact.fragments;
import static android.content.Context.MODE_PRIVATE;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.app.mahindrafinancemfact.adaptors.AssetListAdaptor;
import com.app.mahindrafinancemfact.databinding.FragmentTotalAssetListBinding;
import com.app.mahindrafinancemfact.models.AssetObjectResponseModel;
import com.app.mahindrafinancemfact.utility.ApiClient;
import com.app.mahindrafinancemfact.utility.ApiInterface;
import com.app.mahindrafinancemfact.utility.UtilityMethods;
import java.util.ArrayList;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.Callback;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TotalAssetList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TotalAssetList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentTotalAssetListBinding fragmentTotalAssetListBinding;
    ArrayList assetList = new ArrayList<>();
    ApiInterface apiInterface;
    String s1;
    String s2;
    int pageIndex = 1;
    int pageSize = 10;
    AssetListAdaptor adapter;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    public TotalAssetList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TotalAssetList.
     */
    // TODO: Rename and change types and number of parameters
    public static TotalAssetList newInstance(String param1, String param2) {
        TotalAssetList fragment = new TotalAssetList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentTotalAssetListBinding = FragmentTotalAssetListBinding.inflate(inflater,container,false);
        setupRecyclerView();
        SharedPreferences sh = getContext().getSharedPreferences("SAPCODE", MODE_PRIVATE);
        s1 = sh.getString("empCode", "");
        SharedPreferences ai = getContext().getSharedPreferences("AID",MODE_PRIVATE);
        s2 = ai.getString("aid","");
        init();
        return fragmentTotalAssetListBinding.getRoot();    }

    private void setupRecyclerView() {
        adapter = new AssetListAdaptor(getContext(), assetList);
        fragmentTotalAssetListBinding.rvAssetList.setHasFixedSize(true);
        fragmentTotalAssetListBinding.rvAssetList.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentTotalAssetListBinding.rvAssetList.setAdapter(adapter);

        fragmentTotalAssetListBinding.rvAssetList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= pageSize) {
                        fragmentTotalAssetListBinding.progressBar.setVisibility(View.VISIBLE);
                        pageIndex++;
                        getAssetList(pageIndex, pageSize);
                    }
                }
            }
        });
    }
    private void init() {
        apiInterface = ApiClient.getClient(getContext()).create(ApiInterface.class);
        getAssetList(pageIndex,pageSize);

    }
    private void getAssetList(int pageIn, int pageSize) {
        try {
            if (UtilityMethods.isConnectingToInternet(getContext())) {
                 showMsgView(View.GONE,View.GONE);
                HashMap<String, String> params = new HashMap<>();
                params.put("empCode", s1);
                params.put("aid", s2);
                params.put("index", String.valueOf(pageIn));
                params.put("records", String.valueOf(pageSize));
                params.put("status","0");
                SharedPreferences tok = getContext().getSharedPreferences("Token", MODE_PRIVATE);
                String token = tok.getString("token", "");
                Call<AssetObjectResponseModel> call = apiInterface.Asset_request(params,"Bearer " + token);
                call.enqueue(new Callback<AssetObjectResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<AssetObjectResponseModel> call, @NonNull retrofit2.Response<AssetObjectResponseModel> response) {
                        AssetObjectResponseModel responseType = response.body();
                          showMsgView(View.GONE,View.GONE);

                        if (responseType != null) {
                            assetList.addAll(responseType.data.assetlist);
                            adapter.notifyDataSetChanged();
                            fragmentTotalAssetListBinding.progressBar.setVisibility(View.GONE);
                            isLastPage = responseType.data.assetlist.size() < pageSize;
                            isLoading = false;
                        } else {
                            showMsgView(View.GONE,View.VISIBLE);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<AssetObjectResponseModel> call, @NonNull Throwable t) {
                          showMsgView(View.GONE,View.VISIBLE);
                        isLoading = false;
                    }
                });
            } else {
                   showMsgView(View.VISIBLE, View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void showMsgView(int containerVisibility, int srvr) {
        try {
            fragmentTotalAssetListBinding.llinternet.setVisibility(containerVisibility);
            fragmentTotalAssetListBinding.servererror.setVisibility(srvr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}