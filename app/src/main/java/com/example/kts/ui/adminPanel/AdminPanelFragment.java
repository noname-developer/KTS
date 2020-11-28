package com.example.kts.ui.adminPanel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kts.R;
import com.example.kts.RxBusChoiceOfGroup;
import com.example.kts.ui.adapters.ItemPreferenceAdapter;
import com.example.kts.ui.login.choiceOfGroup.ChoiceOfGroupSharedViewModel;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.internal.util.AppendOnlyLinkedArrayList;

import static com.example.kts.data.prefs.GroupPreference.GROUP_UUID;

public class AdminPanelFragment extends Fragment {

    private ItemPreferenceAdapter adapter;
    private AdminPanelViewModel viewModel;
    private ChoiceOfGroupSharedViewModel choiceOfGroupSharedViewModel;
    private NavController navController;
    private RecyclerView recyclerView;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private RxBusChoiceOfGroup rxBusChoiceOfGroup;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_panel_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerview_preference);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AdminPanelViewModel.class);
        navController = Navigation.findNavController(view);
        adapter = new ItemPreferenceAdapter();
        adapter.setData(viewModel.getItemList());
        adapter.setItemClickListener(position -> viewModel.onItemClick(position));
        recyclerView.setAdapter(adapter);
        choiceOfGroupSharedViewModel = new ViewModelProvider(this).get(ChoiceOfGroupSharedViewModel.class);
        viewModel.openChoiceOfGroupFragment.observe(getViewLifecycleOwner(), o -> {
            rxBusChoiceOfGroup = RxBusChoiceOfGroup.getInstance();
            compositeDisposable.add(rxBusChoiceOfGroup.getSelectGroupEvent()
                    .filter((AppendOnlyLinkedArrayList.NonThrowingPredicate<Object>) o12 -> o12 != RxBusChoiceOfGroup.EMPTY)
                    .map(String::valueOf)
                    .subscribe(groupUuid -> {
                        Bundle bundle = new Bundle();
                        bundle.putString(GROUP_UUID, groupUuid);
                        navController.navigate(R.id.action_choiceOfGroupFragment_to_group_editor, bundle);
                    }));
            navController.navigate(R.id.action_menu_admin_panel_to_choiceOfGroupFragment);
        });
        viewModel.openTimetableEditorFragment.observe(getViewLifecycleOwner(), (Observer<Object>) o ->
                navController.navigate(R.id.action_menu_admin_panel_to_timetableEditorFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
        recyclerView = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

}