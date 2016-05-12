package com.android.shelter;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toolbar;

import com.android.shelter.helper.MyPostingAdapter;
import com.android.shelter.util.ShelterPropertyTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPropertyFragment extends Fragment {
    private static final String TAG = "SearchPropertyFragment";
    private static final String DIALOG_FILTER = "DialogFilter";
    private static final String DIALOG_SAVE_SEARCH = "DialogSaveSearch";
    private static final int REQUEST_FILTER_OPTION = 0;
    private static final int REQUEST_SAVE_SEARCH_OPTION = 1;
    private SearchPropertyFilterCriteria criteria;
    private SavedSearch searchToBeSaved;
    private RecyclerView mPostingRecyclerView;
    private MyPostingAdapter mPostingAdapter;
    private SearchView mSearchView;


    Button btnFilter;
    Button btnSaveSearch;

    public SearchPropertyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_property, container, false);

        btnFilter = (Button)rootView.findViewById(R.id.filter_btn);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                SearchPropertyFilterFragment dialog = SearchPropertyFilterFragment
                        .newInstance(criteria);
                dialog.setTargetFragment(SearchPropertyFragment.this, REQUEST_FILTER_OPTION);
                dialog.show(fragmentManager, DIALOG_FILTER);

            }
        });

        btnSaveSearch = (Button)rootView.findViewById(R.id.save_search_btn);
        btnSaveSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                SearchPropertySaveSearchFragment dialog = SearchPropertySaveSearchFragment.newInstance(searchToBeSaved);
                dialog.setTargetFragment(SearchPropertyFragment.this, REQUEST_SAVE_SEARCH_OPTION);
                dialog.show(fragmentManager, DIALOG_SAVE_SEARCH);
            }
        });


        criteria =new SearchPropertyFilterCriteria();
        searchToBeSaved = new SavedSearch();

        mPostingRecyclerView = (RecyclerView) rootView.findViewById(R.id.property_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mPostingRecyclerView.setLayoutManager(layoutManager);
        mPostingAdapter = new MyPostingAdapter(PropertyLab.get(getContext()).getProperties(),
                getActivity(), getActivity().getSupportFragmentManager());
        mPostingRecyclerView.setAdapter(mPostingAdapter);
        return rootView;

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mSearchView = (SearchView)menu.findItem(R.id.search_box);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new ShelterPropertyTask(getActivity().getApplicationContext(), "postings", true,
                        null, null, criteria.getKeyword(), criteria.getCity(), criteria.getZipcode(),
                        criteria.getMinRent(), criteria.getMaxRent(), criteria.getApartmentType(),
                        new FragmentCallback() {
                            @Override
                            public void onTaskDone() {
                                mPostingAdapter = new MyPostingAdapter(
                                        PropertyLab.get(getContext()).getProperties(),
                                        getActivity(), getActivity().getSupportFragmentManager()
                                );
                                mPostingRecyclerView.setAdapter(mPostingAdapter);
                            }
                        }).execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FILTER_OPTION){
            if(resultCode == Activity.RESULT_OK){
                criteria = (SearchPropertyFilterCriteria)
                        data.getSerializableExtra(SearchPropertyFilterFragment.EXTRA_OPTION);
                new ShelterPropertyTask(getActivity().getApplicationContext(), "postings", true,
                        null, null, criteria.getKeyword(), criteria.getCity(), criteria.getZipcode(),
                        criteria.getMinRent(), criteria.getMaxRent(), criteria.getApartmentType(),
                        new FragmentCallback() {
                    @Override
                    public void onTaskDone() {
                        mPostingAdapter = new MyPostingAdapter(PropertyLab.get(getContext()).getProperties(),
                        getActivity(), getActivity().getSupportFragmentManager());
                        mPostingRecyclerView.setAdapter(mPostingAdapter);
                    }
                }).execute();
            }
        }else if (requestCode == REQUEST_SAVE_SEARCH_OPTION){
            if(resultCode == Activity.RESULT_OK){
                searchToBeSaved = (SavedSearch)
                        data.getSerializableExtra(SavedSearch.EXTRA_OPTION);
//                new ShelterPropertyTask(getActivity().getApplicationContext(), "postings", true,
//                        null, null, criteria.getKeyword(), criteria.getCity(), criteria.getZipcode(),
//                        criteria.getMinRent(), criteria.getMaxRent(), criteria.getApartmentType(),
//                        new FragmentCallback() {
//                            @Override
//                            public void onTaskDone() {
//                                mPostingAdapter = new MyPostingAdapter(PropertyLab.get(getContext()).getProperties(),
//                                        getActivity(), getActivity().getSupportFragmentManager());
//                                mPostingRecyclerView.setAdapter(mPostingAdapter);
//                            }
//                        }).execute();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);


    }

    public interface FragmentCallback {
        public void onTaskDone();
    }
}
