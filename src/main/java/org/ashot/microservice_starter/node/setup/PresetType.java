package org.ashot.microservice_starter.node.setup;


public enum PresetType {
    COMMAND, PATH;

    public String typeToHeader(PresetType type){
        if(type.name().equals(COMMAND.name())){
            return "Command";
        }
        else if (type.name().equals(PATH.name())){
            return "Path";
        }
        return null;
    }
}
