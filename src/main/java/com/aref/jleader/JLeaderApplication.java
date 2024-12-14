package com.aref.jleader;


import com.aref.jleader.cli.TableData;
import com.aref.jleader.cli.TableFormatter;
import com.aref.jleader.image.Image;
import com.google.protobuf.Timestamp;
import containerd.services.images.v1.ImagesGrpc;
import containerd.services.images.v1.ImagesOuterClass;
import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollDomainSocketChannel;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.unix.DomainSocketAddress;
import io.grpc.stub.StreamObserver;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class JLeaderApplication {

    public static void main(String[] args) {
        List<Image> images = List.of(
                new Image("Nginx", new HashMap<>(),
                        Timestamp.newBuilder().setSeconds(System.currentTimeMillis()).build(),
                        Timestamp.newBuilder().setSeconds(System.currentTimeMillis()).build(),
                        "",
                        "",
                        0L,
                        new HashMap<>()),
                new Image("Java", new HashMap<>(),
                        Timestamp.newBuilder().setSeconds(System.currentTimeMillis()).build(),
                        Timestamp.newBuilder().setSeconds(System.currentTimeMillis()).build(),
                        "",
                        "",
                        0L,
                        new HashMap<>()),
                new Image("Mysql", new HashMap<>(),
                        Timestamp.newBuilder().setSeconds(System.currentTimeMillis()).build(),
                        Timestamp.newBuilder().setSeconds(System.currentTimeMillis()).build(),
                        "",
                        "",
                        0L,
                        new HashMap<>())
        );

        TableData<Image> tableData = new TableData<>(images);
        TableFormatter<Image> tableFormatter = new TableFormatter<>(tableData);
        tableFormatter.printTable();

        EpollEventLoopGroup eventExecutors = new EpollEventLoopGroup();

        ManagedChannel channel = NettyChannelBuilder
                .forAddress(new DomainSocketAddress(new File("/run/k3s/containerd/containerd.sock")))
                .eventLoopGroup(eventExecutors)
                .channelType(EpollDomainSocketChannel.class)
                .usePlaintext()
                .build();

        ClientInterceptor namespaceInterceptor = new ClientInterceptor() {
            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
                    MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
                return new ForwardingClientCall.SimpleForwardingClientCall<>(next.newCall(method, callOptions)) {
                    @Override
                    public void start(Listener<RespT> responseListener, Metadata headers) {
                        // Add the namespace metadata
                        Metadata.Key<String> namespaceKey = Metadata.Key.of("containerd-namespace", Metadata.ASCII_STRING_MARSHALLER);
                        headers.put(namespaceKey, "k8s.io");
                        super.start(responseListener, headers);
                    }
                };
            }
        };

        // Wrap the channel with the interceptor
        Channel interceptedChannel = ClientInterceptors.intercept(channel, namespaceInterceptor);

        ImagesGrpc.ImagesStub stub = ImagesGrpc.newStub(interceptedChannel);

        ImagesOuterClass.ListImagesRequest imagesRequest = ImagesOuterClass.ListImagesRequest
                .newBuilder()
                .build();
        stub.list(imagesRequest, new StreamObserver<>() {
            @Override
            public void onNext(ImagesOuterClass.ListImagesResponse listImagesResponse) {
                List<ImagesOuterClass.Image> imagesList = listImagesResponse.getImagesList();
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onCompleted() {
                System.out.printf("o");
            }
        });

        eventExecutors.shutdownGracefully();

    }


}

