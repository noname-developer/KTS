package com.example.kts.ui.finder;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kts.R;
import com.example.kts.utils.ViewUtils;
import com.example.widgets.searchBar.SearchBar;
import com.example.widgets.searchBar.SearchBar2;

public class FinderFragment extends Fragment {

    private FinderViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_finder, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FinderViewModel.class);
//        ViewUtils.replaceView(getActivity().findViewById(R.id.app_bar), new SearchBar2(getContext()));
        // TODO: Use the ViewModel


        SearchView searchView = new SearchView(getContext());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

}