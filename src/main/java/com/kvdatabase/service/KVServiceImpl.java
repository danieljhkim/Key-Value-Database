package com.kvdatabase.service;

import com.kvcommon.proto.*;
import com.kvdatabase.repository.BaseRepository;
import io.grpc.stub.StreamObserver;


public class KVServiceImpl extends KVServiceGrpc.KVServiceImplBase {

    BaseRepository store;

    public KVServiceImpl (BaseRepository store) {
        this.store = store;
    }

    @Override
    public void get(KeyRequest request, StreamObserver<ValueResponse> responseObserver) {
        String key = request.getKey();
        String value = store.get(key);

        ValueResponse response = ValueResponse.newBuilder()
                .setValue(value != null ? value : "")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void set(KeyValueRequest request, StreamObserver<SetResponse> responseObserver) {
        store.update(request.getKey(), request.getValue());

        SetResponse response = SetResponse.newBuilder()
                .setSuccess(true)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}