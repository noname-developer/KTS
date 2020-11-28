package com.example.kts.ui.group.users;

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

import org.jetbrains.annotations.NotNull;

public class GroupUsersFragment extends Fragment {

    private static final String GROUP_UUID = "GROUP_UUID";
    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private GroupUsersViewModel viewModel;

    @NotNull
    public static GroupUsersFragment newInstance(String groupUuid) {
        GroupUsersFragment fragment = new GroupUsersFragment();
        Bundle args = new Bundle();
        args.putString(GROUP_UUID, groupUuid);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(GroupUsersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_group_users, container, false);
        recyclerView = root.findViewById(R.id.recyclerview_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new UserAdapter();
        recyclerView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.onGroupUuidReceived(getArguments().getString(GROUP_UUID));
        viewModel.userList.observe(getViewLifecycleOwner(), users -> {
            adapter.setData(users);
            adapter.notifyDataSetChanged();
        });
    }
}