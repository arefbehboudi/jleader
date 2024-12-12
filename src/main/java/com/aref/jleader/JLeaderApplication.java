package com.aref.jleader;


import com.aref.jleader.cli.TableData;
import com.aref.jleader.cli.TableFormatter;
import com.aref.jleader.image.Image;
import com.google.protobuf.Timestamp;
import containerd.services.images.v1.ImagesGrpc;
import containerd.services.images.v1.ImagesOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollDomainSocketChannel;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.unix.DomainSocketAddress;
import io.grpc.stub.StreamObserver;
import java.util.HashMap;
import java.util.List;

public class JLeaderApplication {

    public static void main(String[] args) throws IllegalAccessException {
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
                .forAddress(new DomainSocketAddress("/run/k3s/containerd/containerd.sock"))
                .eventLoopGroup(eventExecutors)
                .channelType(EpollDomainSocketChannel.class)
                .usePlaintext()
                .build();


        ImagesGrpc.ImagesStub stub = ImagesGrpc.newStub(channel);

        stub.list(ImagesOuterClass.ListImagesRequest.newBuilder().build(), new StreamObserver<ImagesOuterClass.ListImagesResponse>() {
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

            }
        });

        eventExecutors.shutdownGracefully();

    }


}

