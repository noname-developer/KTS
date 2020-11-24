package com.example.kts.ui.login.choiceOfGroup;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.SingleLiveData;
import com.example.kts.RxBusChoiceOfGroup;
import com.example.kts.data.model.entity.Group;
import com.example.kts.data.model.entity.Specialty;
import com.example.kts.data.repository.GroupRepository;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoiceOfGroupSharedViewModel extends AndroidViewModel {

    private final Map<String, Boolean> expandableSpecialties = new HashMap<>();
    private final GroupRepository groupRepository;
    public LiveData<List<Specialty>> allSpecialties;
    public LiveData<List<Group>> groupsBySpecialty;
    public MutableLiveData<String> selectedSpecialtyUuid = new MutableLiveData<>();
    public SingleLiveData<String> selectedGroupUuid = new SingleLiveData<>();
    public MediatorLiveData<List<Object>> groupAndSpecialtyList = new MediatorLiveData<>();

    public ChoiceOfGroupSharedViewModel(@NonNull Application application) {
        super(application);
        groupRepository = new GroupRepository(application);
        groupsBySpecialty = Transformations.switchMap(selectedSpecialtyUuid, groupRepository::getGroupsBySpecialtyUuid);
        allSpecialties = groupRepository.getAllSpecialties();

        groupAndSpecialtyList.addSource(allSpecialties, specialtyList -> {
            for (Specialty specialty : specialtyList) {
                expandableSpecialties.put(specialty.getName(), false);
            }
            groupAndSpecialtyList.setValue(sortedList(new ArrayList<>(specialtyList)));
        });
        groupAndSpecialtyList.addSource(groupsBySpecialty, groups -> {
            groupAndSpecialtyList.getValue().addAll(groups);
            groupAndSpecialtyList.setValue(sortedList(groupAndSpecialtyList.getValue()));
        });
    }

    @NotNull
    @Contract("_ -> param1")
    private List<Object> sortedList(@NotNull List<Object> list) {
        list.sort((o1, o2) ->
                getSpecialtyUuid(o1).compareTo(getSpecialtyUuid(o2)));
        return list;
    }

    @Nullable
    private String getSpecialtyUuid(Object o) {
        if (o instanceof Specialty) {
            return ((Specialty) o).getUuid();
        } else if (o instanceof Group) {
            return ((Group) o).getSpecialtyUuid();
        }
        return null;
    }

    public void onSpecialtyItemClick(int position) {
        Specialty specialty = (Specialty) groupAndSpecialtyList.getValue().get(position);
        String specialtyName = specialty.getName();
        String specialityUuid = specialty.getUuid();
        if (expandableSpecialties.get(specialtyName)) {
            groupAndSpecialtyList.getValue().removeAll(getGroupListBySpecialtyUuid(specialty.getUuid()));
            groupAndSpecialtyList.setValue(groupAndSpecialtyList.getValue());
        } else {
            selectedSpecialtyUuid.setValue(specialityUuid);
        }
        Log.d("lol", "onSpecialtyItemClick: " + specialtyName + " " + expandableSpecialties.get(specialtyName));
        expandableSpecialties.replace(specialtyName, !expandableSpecialties.get(specialtyName));
    }

    @NotNull
    private List<Group> getGroupListBySpecialtyUuid(String specialtyUuid) {
        List<Group> groupListBySpecialty = new ArrayList<>();
        for (Object o : groupAndSpecialtyList.getValue()) {
            if (o instanceof Group) {
                if (((Group) o).getSpecialtyUuid().equals(specialtyUuid)) {
                    groupListBySpecialty.add((Group) o);
                }
            }
        }
        return groupListBySpecialty;
    }

    public void onGroupItemClick(@NotNull Group group) {
//        selectedGroupUuid.setValue(group.getUuid());
        RxBusChoiceOfGroup.getInstance().postSelectGroupEvent(group.getUuid());
    }

    @Override
    public void onCleared() {
        super.onCleared();
        RxBusChoiceOfGroup.getInstance().clearEvent();
    }
}
