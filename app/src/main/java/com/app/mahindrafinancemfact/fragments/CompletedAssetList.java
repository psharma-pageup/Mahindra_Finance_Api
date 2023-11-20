package com.app.mahindrafinancemfact.fragments;
import static android.R.layout.simple_spinner_item;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.app.mahindrafinancemfact.R;
import com.app.mahindrafinancemfact.adaptors.AssetListAdaptor;
import com.app.mahindrafinancemfact.adaptors.CustomSpinnerAdapter;
import com.app.mahindrafinancemfact.databinding.FragmentCompletedAssetListBinding;
import com.app.mahindrafinancemfact.models.AssetObjectResponseModel;
import com.app.mahindrafinancemfact.utility.ApiClient;
import com.app.mahindrafinancemfact.utility.ApiInterface;
import com.app.mahindrafinancemfact.utility.UtilityMethods;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CompletedAssetList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompletedAssetList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentCompletedAssetListBinding fragmentCompletedAssetListBinding;
    ArrayList assetList = new ArrayList<>();
    ApiInterface apiInterface;
    String s1;
    String s2;
    int pageIndex = 1;
    int pageSize = 10;
    String selectedValue=null;

    int Status;
    AssetListAdaptor adapter;

    boolean chk = false;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    public CompletedAssetList() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CompletedAssetList.
     */
    // TODO: Rename and change types and number of parameters
    public static CompletedAssetList newInstance(String param1, String param2) {
        CompletedAssetList fragment = new CompletedAssetList();
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
        fragmentCompletedAssetListBinding = FragmentCompletedAssetListBinding.inflate(inflater,container,false);
        spinner();
        setupRecyclerView();
        SharedPreferences sh = getContext().getSharedPreferences("SAPCODE", MODE_PRIVATE);
        s1 = sh.getString("empCode", "");
        SharedPreferences ai = getContext().getSharedPreferences("AID",MODE_PRIVATE);
        s2 = ai.getString("aid","");
        init();
        return fragmentCompletedAssetListBinding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new AssetListAdaptor(getContext(), assetList);
        fragmentCompletedAssetListBinding.rvAssetList.setHasFixedSize(true);
        fragmentCompletedAssetListBinding.rvAssetList.setLayoutManager(new LinearLayoutManager(getContext()));
        fragmentCompletedAssetListBinding.rvAssetList.setAdapter(adapter);

        fragmentCompletedAssetListBinding.rvAssetList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                fragmentCompletedAssetListBinding.progressBar.setVisibility(View.VISIBLE);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= pageSize) {
                        pageIndex++;
                        getAssetList(pageIndex, pageSize);
                    }
                }

            }
        });


    }


    private void init() {
        apiInterface = ApiClient.getClient(getContext()).create(ApiInterface.class);
        selectedValue = "All";
        getAssetList(pageIndex, pageSize);

    }
    private void spinner() {

//        List<CharSequence> spinnerItems = Arrays.asList(getResources().getTextArray(R.array.snanned));
//        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), simple_spinner_item, spinnerItems);
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        fragmentCompletedAssetListBinding.spinner3.setAdapter(adapter);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.snanned,
                simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fragmentCompletedAssetListBinding.spinner3.setAdapter(adapter);

        fragmentCompletedAssetListBinding.spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                chk=true;
                selectedValue = (String) parentView.getItemAtPosition(position);
                if(chk && !isLoading){
                    assetList.clear();
                }
                if(selectedValue.equals("Manual")){
                  Status=2;

                } else if (selectedValue.equals("All")) {
                  Status = 4;
                } else{
                  Status=1;
                }
                pageIndex=1;
                getAssetList(pageIndex, pageSize);
                chk=false;
            }



            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

    }

    private void getAssetList(int pageIndex, int pageSize) {

        try {

            isLoading=true;

            if (UtilityMethods.isConnectingToInternet(getContext())) {
                if(pageIndex == 1) {
                    showMsgView(View.GONE, View.GONE);
                }
                HashMap<String, String> params = new HashMap<>();
                params.put("empCode", s1);
                params.put("aid", s2);
                params.put("index", String.valueOf(pageIndex));
                params.put("records", String.valueOf(pageSize));
                params.put("status", String.valueOf(Status));

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
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();

                                }
                            });
                            fragmentCompletedAssetListBinding.progressBar.setVisibility(View.GONE);
                            isLastPage = responseType.data.assetlist.size() < pageSize;
                            isLoading = false;
                            chk=false;
                        } else {
                            showMsgView(View.GONE,View.VISIBLE);
                        }
                    }


                    @Override
                    public void onFailure(@NonNull Call<AssetObjectResponseModel> call, @NonNull Throwable t) {
                        showMsgView(View.GONE,View.VISIBLE);

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
            fragmentCompletedAssetListBinding.llinternet.setVisibility(containerVisibility);
            fragmentCompletedAssetListBinding.servererror.setVisibility(srvr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}