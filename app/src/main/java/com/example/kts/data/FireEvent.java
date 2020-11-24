package com.example.kts.data;

import androidx.annotation.NonNull;

public class FireEvent<T> {

    private DataStatus dataStatus;
    private T data;
    private Exception exception;

    public FireEvent<T> complete(@NonNull T data) {
        this.dataStatus = DataStatus.SUCCESSFUL;
        this.data = data;
        return this;
    }

    public FireEvent<T> loading(@NonNull T data) {
        this.dataStatus = DataStatus.LOADING;
        this.data = data;
        return this;
    }

    public FireEvent<T> error(Exception exception) {
        this.dataStatus = DataStatus.ERROR;
        this.exception = exception;
        return this;
    }

    public DataStatus getStatus() {
        return dataStatus;
    }

    public T getData() {
        return data;
    }

    public Exception getException() {
        return exception;
    }

    public enum DataStatus {
        SUCCESSFUL, ERROR, LOADING
    }
}
