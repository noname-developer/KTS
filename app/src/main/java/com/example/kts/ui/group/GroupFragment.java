package com.example.kts.ui.group;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kts.R;
import com.example.kts.ui.adapters.UserAdapter;

public class GroupFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private GroupViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(GroupViewModel.class);
        View root = inflater.inflate(R.layout.fragment_group, container, false);
        recyclerView = root.findViewById(R.id.recyclerview_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new UserAdapter();
        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.toolbarName.observe(getViewLifecycleOwner(), title ->
                ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(title));
        viewModel.userList.observe(getViewLifecycleOwner(), users -> {
            adapter.setData(users);
            adapter.notifyDataSetChanged();
        });
    }
}