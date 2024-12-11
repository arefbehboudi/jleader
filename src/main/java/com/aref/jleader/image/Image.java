package com.aref.jleader.image;


import com.google.protobuf.Timestamp;
import containerd.services.images.v1.ImagesOuterClass;

import java.util.HashMap;
import java.util.Map;

public class Image {

    // Name provides a unique name for the image.
    private String name;

    // Labels provides free form labels for the image.
    private Map<String, String> labels;

    // Target describes the content entry point of the image.
    private Descriptor target;

    // CreatedAt is the time the image was first created.
    private Timestamp createdAt; // ISO 8601 formatted string for Timestamp

    // UpdatedAt is the last time the image was mutated.
    private Timestamp updatedAt; // ISO 8601 formatted string for Timestamp

    // Constructor
    public Image(String name, Map<String, String> labels,
                 Descriptor target,
                 Timestamp createdAt, Timestamp updatedAt) {
        this.name = name;
        this.labels = labels;
        this.target = target;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }



    public static Image fromProtobuf(ImagesOuterClass.Image protoImage) {
        Descriptor target = Descriptor.fromProtobuf(protoImage.getTarget());

        Map<String, String> labels = new HashMap<>(protoImage.getLabelsMap());

        return new Image(
                protoImage.getName(),
                labels,
                target,
                protoImage.getCreatedAt(),
                protoImage.getUpdatedAt()
        );
    }
}