package com.aref.jleader;


import com.aref.jleader.cli.CliField;
import com.aref.jleader.cli.CliUtils;
import containerd.services.images.v1.ImagesGrpc;
import containerd.services.images.v1.ImagesOuterClass;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollDomainSocketChannel;
import io.grpc.netty.shaded.io.netty.channel.epoll.EpollEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.unix.DomainSocketAddress;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;

public class JLeaderApplication {

    public static void main(String[] args) throws IllegalAccessException {
        List<Container> containers = new ArrayList<>();
        containers.add(new Container("1", "Test", "RUNNING"));
        CliUtils.printList(containers);

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
                System.out.println(listImagesResponse);
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



    public static class Container {

        @CliField(name = "Container ID")
        private String id;

        @CliField(name = "Name")
        private String name;

        @CliField(name = "Status")
        private String status;

        public Container(String id, String name, String status) {
            this.id = id;
            this.name = name;
            this.status = status;
        }
    }

}

