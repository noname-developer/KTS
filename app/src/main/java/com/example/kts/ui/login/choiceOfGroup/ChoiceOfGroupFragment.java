package com.example.kts.ui.login.choiceOfGroup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kts.R;
import com.example.kts.ui.adapters.GroupAdapter;

import java.util.ArrayList;

public class ChoiceOfGroupFragment extends Fragment {

    private GroupAdapter adapter;
    private ChoiceOfGroupSharedViewModel viewModel;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_choice_of_group, container, false);
        adapter = new GroupAdapter();
        recyclerView = root.findViewById(R.id.recyclerview_groups);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        adapter.setSpecialtyItemClickListener(position -> viewModel.onSpecialtyItemClick(position));

        adapter.setGroupItemClickListener(position -> viewModel.onGroupItemClick(position));

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChoiceOfGroupSharedViewModel.class);
        viewModel.groupAndSpecialtyList.observe(getViewLifecycleOwner(), list -> adapter.submitList(new ArrayList<>(list)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
        recyclerView = null;
    }
}