package com.aref.jleader;


import com.aref.jleader.cli.CliField;
import com.aref.jleader.cli.CliUtils;

import java.util.ArrayList;
import java.util.List;

public class JLeaderApplication {

    public static void main(String[] args) throws IllegalAccessException {
        List<Container> containers = new ArrayList<>();
        containers.add(new Container("1", "Test"));
        CliUtils.printList(containers);
    }


    public static class Container {

        @CliField(name = "Container ID")
        private String id;

        @CliField(name = "Name 1234")
        private String name;

        public Container(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

}

