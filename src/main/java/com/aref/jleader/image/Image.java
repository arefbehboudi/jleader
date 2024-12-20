package com.aref.jleader.image;


import com.google.protobuf.Timestamp;
import containerd.services.images.v1.ImagesOuterClass;
import containerd.types.DescriptorOuterClass;

import java.util.HashMap;
import java.util.Map;

public class Image {

    private String name;

    private Map<String, String> labels;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private String mediaType;

    private String digest;

    private long size;

    private Map<String, String> annotations;

    public Image(String name,
                 Map<String, String> labels,
                 Timestamp createdAt,
                 Timestamp updatedAt,
                 String mediaType,
                 String digest,
                 long size,
                 Map<String, String> annotations) {
        this.name = name;
        this.labels = labels;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.mediaType = mediaType;
        this.digest = digest;
        this.size = size;
        this.annotations = annotations;
    }

    public static Image fromProtobuf(ImagesOuterClass.Image protoImage) {
        DescriptorOuterClass.Descriptor descriptor = protoImage.getTarget();

        Map<String, String> labels = new HashMap<>(protoImage.getLabelsMap());

        return new Image(
                protoImage.getName(),
                labels,
                protoImage.getCreatedAt(),
                protoImage.getUpdatedAt(),
                descriptor.getMediaType(),
                descriptor.getDigest(),
                descriptor.getSize(),
                descriptor.getAnnotationsMap()
        );
    }
}