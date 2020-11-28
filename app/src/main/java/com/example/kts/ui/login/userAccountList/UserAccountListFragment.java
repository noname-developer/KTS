package com.example.kts.ui.login.userAccountList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kts.R;
import com.example.kts.ui.adapters.UserAdapter;
import com.example.kts.ui.login.LoginViewModel;

public class UserAccountListFragment extends Fragment {
    private LoginViewModel loginViewModel;
    private UserAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_group_users, container, false);
        recyclerView = root.findViewById(R.id.recyclerview_users);
        adapter = new UserAdapter();
        adapter.setUserItemClickListener(position -> loginViewModel.onUserItemClick(adapter.getDate().get(position)));
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(adapter);
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
        loginViewModel.userAccountList.observe(getViewLifecycleOwner(), userList -> {
            adapter.setData(userList);
            adapter.notifyItemRangeChanged(0, userList.size());
            Toast.makeText(getActivity(), "" + userList.size(), Toast.LENGTH_SHORT).show();
        });
    }
}